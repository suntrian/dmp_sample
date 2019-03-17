package com.quantchi.common.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

/**
 * 导出Excel
 * 支持多个sheet
 * 支持复杂表头
 * 复杂表头可以是 List<List<String>> title。会自动按行优先对相同内容进行合并
 * 例如
 *        {'a','b','c','d'},
 *        {'a','b','c'},
 *        {'a','c','d','e'},
 *        {'b','c','e','e'}
 * 会自动合并生成
 *        ┏┉┉┉┉┉┉┉┉┉┉┉┯┉┉┉┓
 *        ┃     a     │ b ┃
 *        ┠┈┈┈┈┈┈┈┬┈┈┈┴┈┈┈┨
 *        ┃   b   │   c   ┃
 *        ┠┈┈┈┬┈┈┈┼┈┈┈┬┈┈┈┩
 *        ┃ c │   │ d │ e ┃
 *        ┠┈┈┈┤ c ├┈┈┈┴┈┈┈┨
 *        ┃ d │   │   e   ┃
 *        ┗┉┉┉┷┉┉┉┷┉┉┉┉┉┉┉┛
 * 支持List, Map 和 任意bean数据类型。 bean需实现 Serializable接口
 *
 * @author suntrian
 * @date 2010.1.04
 * @since 1.5.9
 */
@SuppressWarnings("all")
public class ExcelWriter {

  public enum BorderPosition{
    TOP, BOTTOM, LEFT, RIGHT, ALL
  }

  private Workbook workbook;

  private int sheetCount = 1;

  private List<Class> type;

  private CellStyle titleStyle;

  private CellStyle bodyStyle;

  private List<Map<CellRangeAddress, CellStyle>> customStyle;

  //自动设置列宽
  private List<Boolean> autoCellWidth;

  //顺序输出属性
  private List<List<String>> sortedFields;

  private List<Map<String,Method>> beanGetMethods;

  private List<String> sheetNames;

  private List<List<?>> titles;

  //复杂表头的合并策略，true:水平合并优先,false:垂直合并优先
  private List<Boolean> titleMergeHorizonFirst;

  private List<Map<Integer, List<String>>> cellConstraint;

  private List<List<?>> data;

  private Stream<?> dataSteam;

  private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
  private SimpleDateFormat dateFormat = new SimpleDateFormat(datetimeFormat);
  private DataFormat dataFormat;

  public ExcelWriter setSheetCount(int size){
    this.sheetCount = size;
    this.workbook.getCreationHelper().createDataFormat();
    return this;
  }

  public ExcelWriter setDataType(Class clazz){
    this.type = setList(this.type, clazz, sheetCount);
    return this;
  }

  /**
   * 设置每个sheet的数据类型，不设置时将根据传入的数据data自动推断
   * @param clazz
   * @param sheetNum based from 1
   * @return
   */
  public ExcelWriter setDataType(int sheetNum, Class clazz){
    this.type = setList(this.type, sheetNum-1, clazz, null, sheetCount);
    return this;
  }

  public ExcelWriter setAutoWidth(boolean autoWidth){
    this.autoCellWidth = setList(this.autoCellWidth, autoWidth, sheetCount);
    return this;
  }

  public ExcelWriter setAutoWidth(int sheetNum, boolean autoWidth){
    this.autoCellWidth = setList(this.autoCellWidth, sheetNum-1, autoWidth, false, sheetCount);
    return this;
  }

  public ExcelWriter setBodyCellStyle(){
    if (this.workbook==null){
      this.workbook = new SXSSFWorkbook();
    }
    if (this.bodyStyle == null) {
      this.titleStyle = workbook.createCellStyle();
    }
    return this;
  }

  public ExcelWriter setBodyCellStyleFont(@Nullable String fontName, @Nullable  Integer fontSize, @Nullable Integer fontColor){
    setBodyCellStyle();
    this.bodyStyle = setCellStyleFont(this.bodyStyle, fontName, fontSize, fontColor);
    return this;
  }

  public ExcelWriter setBodyCellStyleAlign(@Nullable HorizontalAlignment horizontalAlignment, @Nullable VerticalAlignment verticalAlignment){
    setBodyCellStyle();
    this.bodyStyle = setCellStyleAlign(this.bodyStyle, horizontalAlignment, verticalAlignment);
    return this;
  }

  public ExcelWriter setBodyCellStyleBorder(@Nullable BorderStyle borderStyle, @Nullable Integer color, BorderPosition... positions) {
    setBodyCellStyle();
    this.bodyStyle = setCellStyleBorder(this.bodyStyle, borderStyle, color, positions);
    return this;
  }

  private ExcelWriter setTitleCellStyle(){
    if (this.workbook==null){
      this.workbook = new SXSSFWorkbook();
    }
    if (this.titleStyle == null){
      this.titleStyle = workbook.createCellStyle();
      this.titleStyle.setAlignment(HorizontalAlignment.CENTER);
      this.titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      Font font = this.workbook.createFont();
      font.setFontHeightInPoints((short) 12);
      font.setFontName("黑体");
      this.titleStyle.setFont(font);
    }
    return this;
  }

  public ExcelWriter setTitleCellStyleFont(@Nullable String fontName, @Nullable Integer fontSize, @Nullable Integer fontColor){
    setTitleCellStyle();
    this.titleStyle = setCellStyleFont(this.titleStyle, fontName, fontSize, fontColor);
    return this;
  }

  public ExcelWriter setTitleCellStyleAlign(@Nullable HorizontalAlignment horizontalAlignment, @Nullable VerticalAlignment verticalAlignment){
    setTitleCellStyle();
    this.titleStyle = setCellStyleAlign(this.titleStyle, horizontalAlignment, verticalAlignment);
    return this;
  }

  public ExcelWriter setTitleCellStyleBorder(@Nullable BorderStyle borderStyle, @Nullable Integer color, BorderPosition... positions) {
    setTitleCellStyle();
    this.titleStyle = setCellStyleBorder(this.titleStyle, borderStyle, color, positions);
    return this;
  }

  private CellStyle setCellStyleFont(@NotNull CellStyle style,
                                     @Nullable String fontName,
                                     @Nullable Integer fontSize,
                                     @Nullable Integer fontColor,
                                     @Nullable Boolean italic,
                                     @Nullable Boolean bold,
                                     @Nullable Boolean strikeOut,
                                     @Nullable Byte underLine){
    if (fontName==null&&fontSize==null){
      return style;
    }
    Font font = this.workbook.createFont();
    if (fontSize!=null) {font.setFontHeightInPoints(fontSize.shortValue());}
    if (fontName!=null) {font.setFontName(fontName);}
    if (fontColor!=null) {font.setColor(fontColor.shortValue());}
    if ( italic != null) {font.setItalic(italic);}
    if ( bold != null) {font.setBold(bold);}
    if ( strikeOut != null) {font.setStrikeout(strikeOut);}
    if ( underLine != null) {font.setUnderline(underLine);}
    style.setFont(font);
    return style;
  }

  private CellStyle setCellStyleFont(@NotNull CellStyle style,
                                     @Nullable String fontName,
                                     @Nullable Integer fontSize,
                                     @Nullable Integer fontColor){
    return setCellStyleFont(style, fontName, fontSize, fontColor, null, null, null, null);
  }

  private CellStyle setCellStyleAlign(CellStyle style, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment){
    if (horizontalAlignment!=null){ style.setAlignment(horizontalAlignment); }
    if (verticalAlignment!=null){ style.setVerticalAlignment(verticalAlignment); }
    return style;
  }

  private CellStyle setCellStyleBorder(CellStyle style, BorderStyle borderStyle, Integer color, BorderPosition... positions){
    boolean all = false;
    if (positions.length==0 || Arrays.asList(positions).contains(BorderPosition.ALL)){
      all = true;
      positions = new BorderPosition[]{BorderPosition.ALL};
    }
    for (BorderPosition position: positions){
      switch (position){
        case ALL:
          all = true;
        case TOP:
          if (borderStyle!=null) {style.setBorderTop(borderStyle);};
          if (color!=null)       {style.setTopBorderColor(color.shortValue()) ;};
          if (!all) break;
        case BOTTOM:
          if (borderStyle!=null) {style.setBorderBottom(borderStyle);}
          if (color!=null)       {style.setBottomBorderColor(color.shortValue());}
          if (!all) break;
        case LEFT:
          if (borderStyle!=null) {style.setBorderLeft(borderStyle);};
          if (color!=null)       {style.setLeftBorderColor(color.shortValue());};
          if (!all) break;
        case RIGHT:
          if (borderStyle!=null) {style.setBorderRight(borderStyle);};
          if (color!=null)       {style.setRightBorderColor(color.shortValue());};
          if (!all) break;
      }
    }
    return style;
  }

  private CellStyle setCellStyleColor(CellStyle style, Integer backgroundColor, Integer foregroundColor) {
    if (backgroundColor!=null){style.setFillBackgroundColor(backgroundColor.shortValue());}
    if (foregroundColor!=null){
      style.setFillForegroundColor(foregroundColor.shortValue());
      style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }
    return style;
  }
  /**
   * 默认设置第一个sheet的style
   * @param fontName
   * @param fontSize
   * @param startRow
   * @param endRow
   * @param startCol
   * @param endCol
   * @return
   */
  public ExcelWriter setCustomCellStyleFont(String fontName, Integer fontSize, Integer fontColor, int startRow, int endRow, int startCol, int endCol){
    setCustomCellStyleFont(1, fontName, fontSize, fontColor, startRow, endRow, startCol, endCol);
    return this;
  }

  public ExcelWriter setCustomCellStyleFont(int sheetNumBased1, String fontName, Integer fontSize, Integer fontColor, int startRow, int endRow, int startCol, int endCol){
    CellRangeAddress addresses = new CellRangeAddress(startRow, endRow, startCol, endCol);
    CellStyle style;
    Map<CellRangeAddress, CellStyle> styleMap ;
    if ((styleMap = getList(this.customStyle, sheetNumBased1-1, new HashMap<>())).containsKey(addresses)){
      style = styleMap.get(addresses);
    } else {
      if (this.workbook == null){ this.workbook = new SXSSFWorkbook(); }
      style = this.workbook.createCellStyle();
    }
    setCellStyleFont(style, fontName, fontSize, fontColor);
    styleMap.put(addresses, style);
    this.customStyle = setList(this.customStyle, sheetNumBased1-1, styleMap, new HashMap<>(1), sheetCount);
    return this;
  }

  public ExcelWriter setCustomCellStyleColor(int sheetNum, Integer backgroundColor, Integer foregroundColor, int startRow, int endRow, int startCol, int endCol){
    CellRangeAddress addresses = new CellRangeAddress(startRow, endRow, startCol, endCol);
    CellStyle style;
    Map<CellRangeAddress, CellStyle> styleMap ;
    if ((styleMap = getList(this.customStyle, sheetNum-1, new HashMap<>())).containsKey(addresses)){
      style = styleMap.get(addresses);
    } else {
      if (this.workbook == null){ this.workbook = new SXSSFWorkbook(); }
      style = this.workbook.createCellStyle();
    }
    setCellStyleColor(style, backgroundColor, foregroundColor);
    styleMap.put(addresses, style);
    this.customStyle = setList(this.customStyle, sheetNum-1, styleMap, new HashMap<>(1),sheetCount);
    return this;
  }

  public ExcelWriter setCustomCellStyleColor(Integer backgroundColor, Integer foregroundColor, int startRow, int endRow, int startCol, int endCol){
    return setCustomCellStyleColor(1, backgroundColor, foregroundColor, startRow, endRow, startCol, endCol);
  }

  public ExcelWriter setSortedFields(List<String> sortedFields){
    this.sortedFields = setList(this.sortedFields, sortedFields, sheetCount);
    return this;
  }

  public ExcelWriter setSortedFields(int sheetNum, List<String> sortedFields){
    this.sortedFields = setList(this.sortedFields,sheetNum-1, sortedFields, null, sheetCount);
    return this;
  }

  public ExcelWriter setSortedFields(String... sortedFields){
    this.sortedFields = setList(this.sortedFields, Arrays.asList(sortedFields), sheetCount);
    return this;
  }

  public ExcelWriter setSheetNames(String sheetNames) {
    this.sheetNames = setList(this.sheetNames, sheetNames, sheetCount);
    return this;
  }

  public ExcelWriter setSheetNames(int sheetNum, String sheetName){
    this.sheetNames = setList(this.sheetNames, sheetNum-1, sheetName, null, sheetCount);
    this.sheetCount = sheetNum>this.sheetCount?sheetNum:sheetCount;
    return this;
  }

  public ExcelWriter setTitles(List<?> titles){
    this.titles = setList(this.titles, titles, sheetCount);
    return this;
  }

  public ExcelWriter setTitles(int sheetNum, List<?> titles){
    this.titles = setList(this.titles, sheetNum-1, titles, Collections.emptyList(), sheetCount);
    this.sheetCount = sheetNum>this.sheetCount?sheetNum:sheetCount;
    return this;
  }

  public ExcelWriter setTitles(String... titles)  {
    this.titles = setList(this.titles, Arrays.asList(titles), sheetCount);
    return this;
  }

  public ExcelWriter setTitleMergeHorizonFirst(Boolean horizonFirst){
    this.titleMergeHorizonFirst = setList(this.titleMergeHorizonFirst, horizonFirst, sheetCount);
    return this;
  }

  public ExcelWriter setTitleMergeHorizonFirst(int sheetNum, Boolean horizonFirst){
    this.titleMergeHorizonFirst = setList(this.titleMergeHorizonFirst, sheetNum-1, horizonFirst, true, sheetCount);
    return this;
  }

  /**
   * 设置单元格下拉选项
   * @param sheetNum   第几个sheet,based 1
   * @param columnNum  第几列 based 1
   * @param constaint
   * @return
   */
  public ExcelWriter setCellConstraint(int sheetNum, int columnNum, List<String> constaint){
    if (this.cellConstraint!=null && this.cellConstraint.size()>=sheetNum){
      Map<Integer, List<String>> constainMap = this.cellConstraint.get(sheetNum-1);
      if (constainMap!=null){
        constainMap.put(columnNum, constaint);
      } else {
        constainMap = new HashMap<>();
        constainMap.put(columnNum, constaint);
        this.cellConstraint.set(sheetNum-1, constainMap);
      }
    } else {
      Map<Integer, List<String>> constaintMap = new HashMap<>();
      constaintMap.put(columnNum, constaint);
      this.cellConstraint = setList(this.cellConstraint, sheetNum-1, constaintMap, new HashMap<>(), sheetCount);
    }
    return this;
  }

  public ExcelWriter setCellConstraint(int columnNum, List<String> constaint) {
    return setCellConstraint(this.cellConstraint==null?1:this.cellConstraint.size()+1, columnNum, constaint);
  }

  public ExcelWriter setData(List<?> data){
    this.data = setList(this.data, data, sheetCount);
    this.dataSteam = null;
    return this;
  }

  public ExcelWriter setData(int sheetNum, List<?> data){
    this.data = setList(this.data, sheetNum-1, data, Collections.emptyList(), sheetCount);
    this.dataSteam = null;
    this.sheetCount = sheetNum>this.sheetCount?sheetNum:sheetCount;
    return this;
  }



  public ExcelWriter setData(Stream<?> dataSteam){
    this.dataSteam = dataSteam;
    this.data = null;
    return this;
  }

  @SuppressWarnings("unchecked")
  private void preHandler() throws NoSuchMethodException {
    //检测是否存在数据，以数据或者标题的数量来判定sheet数量
    if (this.data==null || this.data.size()==0){
      if (this.titles==null || this.titles .size() == 0){
        throw new IllegalArgumentException("未设置数据");
      } else {
        this.sheetCount = this.titles.size()>this.sheetCount?this.titles.size():this.sheetCount;
      }
      this.data = new ArrayList<>(sheetCount);
    } else {
      this.sheetCount = this.data.size()>this.sheetCount?this.data.size():this.sheetCount;
    }

    //判断输入的数据类型, 并根据数据类型生成fields, 如果是bean类型则缓存getter方法
    for (int i = 0 ; i < sheetCount; i ++){
      if (getList(this.data,i, Collections.EMPTY_LIST).size()>0){
        //判断数据类型
        Class clazz = null;
        if ((clazz = getList(this.type,i,null)) == null){
          if (getList(this.data.get(i), 0, null) == null){
            continue;
          }
          clazz = this.data.get(i).get(0).getClass();
        }
        if (List.class.isAssignableFrom(clazz)){

        } else if (Map.class.isAssignableFrom(clazz)){
          if (getList(this.sortedFields, i, Collections.EMPTY_LIST).size() == 0){
            if (getList(this.data,i,Collections.EMPTY_LIST).size()>0){
              List<String> keys = new ArrayList<> (((Map)this.data.get(i).get(0)).keySet());
              this.sortedFields = setList(this.sortedFields, i, keys, Collections.EMPTY_LIST, sheetCount);
            } else {

            }
          }
        } else if (Serializable.class.isAssignableFrom(clazz)){
          if (getList(this.sortedFields,i, Collections.EMPTY_LIST).size() > 0){
            //指定了输出属性及顺序，则按指定的顺序输出
            Map<String, Method> methodMap = new HashMap<>(this.sortedFields.get(i).size());
            for (String field: this.sortedFields.get(i)){
              if (field == null || "".equals(field.trim())){
                continue;
              }
              methodMap.put(field, BeanUtil.getBeanGetter(clazz, field));
            }
            this.beanGetMethods = setList(this.beanGetMethods, i, methodMap, Collections.EMPTY_MAP, sheetCount);
          } else {
            //没有指定输出属性，则获取所有属性输出
            LinkedList<Method> getters = (LinkedList<Method>) BeanUtil.getBeanGetters(clazz);
            List<String> fields = new ArrayList<>(getters.size());
            Map<String, Method> methodMap = new HashMap<>(getters.size());
            Method getter;
            while ((getter = getters.pollLast())!=null){
              //从尾开始遍历，子类同名方法覆盖父类方法
              String fieldName = BeanUtil.unCapture(getter.getName().substring(3));
              fields.add(fieldName);
              methodMap.put(fieldName, getter);
            }
            this.sortedFields = setList(this.sortedFields, i, fields, Collections.EMPTY_LIST, sheetCount);
            this.beanGetMethods = setList(this.beanGetMethods, i, methodMap, Collections.EMPTY_MAP, sheetCount);
          }
        } else {
          throw new IllegalArgumentException("不支持的数据类型");
        }
        this.type = setList(this.type, i, clazz, null, sheetCount);
      }

    }
    setTitleCellStyle();
  }

  public Workbook generate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    if (this.dataSteam == null){
      return generate(sheetNames, titles, sortedFields, data);
    }else if (this.dataSteam != null){
      return generate(getList(this.sheetNames,0,null), getList(this.titles, 0, Collections.EMPTY_LIST), getList(this.sortedFields, 0, Collections.EMPTY_LIST), dataSteam);
    } else {
      throw new IllegalArgumentException("未提供数据");
    }
  }

  public Workbook generate(final String sheetName, final List<?> titles, final List<String> sortedFields, final List<?> datas) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    return generate(
            Collections.singletonList(sheetName),
            Collections.singletonList(titles),
            Collections.singletonList(sortedFields),
            Collections.singletonList(datas)
            );
  }

  /**
   *
   * @param titles
   * @param data
   * @return
   */
  public Workbook generate(final List<String> sheetNames, final List<List<?>> titles, final List<List<String>> sortedFields, final List<List<?>> data) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    this.sheetNames = sheetNames;
    this.titles = titles;
    this.sortedFields = sortedFields;
    this.data = data;

    if (this.workbook == null){
      this.workbook = new SXSSFWorkbook();
    }
    preHandler();

    for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++){
      String sheetName = getList(this.sheetNames, sheetIndex, null);
      Sheet sheet;
      if (sheetName == null){
        sheet = workbook.createSheet("Sheet"+(sheetIndex+1));
      } else {
        sheet = workbook.createSheet(sheetName);
      }
      int titleRows = 0;
      List<?> title ;
      if ((title = getList(this.titles, sheetIndex, Collections.emptyList())).size()>0){
        titleRows = exportTitle(sheet, title, getList(this.titleMergeHorizonFirst, sheetIndex, true));
      }
      if (this.data.size()==0){
        continue;
      }
      List<?> sheetData = getList(this.data, sheetIndex, Collections.EMPTY_LIST);
      for (int rowIndex = titleRows; rowIndex < sheetData.size() + titleRows; rowIndex++){
        Row row = sheet.createRow(rowIndex);
        Object rowData = sheetData.get(rowIndex-titleRows);
        List<Object> rowDataList = flatData(rowData, getList(this.type, sheetIndex, List.class), getList(this.sortedFields, sheetIndex, Collections.EMPTY_LIST), getList(this.beanGetMethods, sheetIndex, Collections.EMPTY_MAP));
        for (int cellIndex = 0 ; cellIndex < rowDataList.size(); cellIndex++){
          Cell cell = row.createCell(cellIndex);
          setCell(cell, rowDataList.get(cellIndex), bodyStyle);
        }
      }

      postHandler(sheetIndex, sheet, titleRows, sheetData.size());

    }
    return workbook;
  }

  /**
   * 只能支持单个sheet的导出
   * 经测试直接输出到输出流失败，所以还是改成先生成workbook, 但是可以节省点内存
   * @param sheetNames
   * @param titles
   * @param sortedFields
   * @param data
   * @param <T>
   * @throws NoSuchMethodException
   */
  private<T> Workbook generate(final String sheetNames, final List<?> titles, final List<String> sortedFields, Stream<T> data) throws NoSuchMethodException {
    if (this.workbook == null){
      this.workbook = new SXSSFWorkbook();
    }
    preHandler();
    Sheet sheet;
    if (sheetNames == null){
      sheet = workbook.createSheet("Sheet1");
    } else {
      sheet = workbook.createSheet(sheetNames);
    }
    int titleRows = 0;
    if (titles.size()>0){
      titleRows = exportTitle(sheet, titles, true);
    }
    int[] dataRows = new int[1];
    final Map<String, Method> methodMap = getList(this.beanGetMethods, 0, Collections.EMPTY_MAP);
    data.map(i-> {
      try {
        return flatData(i,i.getClass(), sortedFields, methodMap);
      } catch (Exception e) {
        return null;
      }
    }).forEach(l->{
      dataRows[0] += 1;
      Row row = sheet.createRow(sheet.getLastRowNum()+1);
      for (int cellIndex = 0 ; cellIndex < l.size(); cellIndex++){
        Cell cell = row.createCell(cellIndex);
        setCell(cell, l.get(cellIndex), bodyStyle);
      }
    });
    postHandler(0, sheet, titleRows, dataRows[0]);
    return this.workbook;
  }

  private void postHandler(int sheetIndex, Sheet sheet , int titleRows, int dataRows){
    if (true == getList(this.autoCellWidth, sheetIndex, false)){
      Row row = sheet.getRow(sheet.getFirstRowNum());
      for (int i = 0; i < row.getLastCellNum(); i++){
        sheet.autoSizeColumn(i);
      }
    }
    if (getList(this.cellConstraint, sheetIndex, Collections.EMPTY_MAP).size()>0){
      Map<Integer, List<String>> constraints = this.cellConstraint.get(sheetIndex);
      DataValidationHelper helper = new XSSFDataValidationHelper((XSSFSheet) sheet);
      for (Map.Entry<Integer, List<String>> entry: constraints.entrySet()){
        // 加载下拉列表内容
        String[] constraintValues = new String[entry.getValue().size()];
        entry.getValue().toArray(constraintValues);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(constraintValues);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(titleRows, titleRows + dataRows, entry.getKey()-1, entry.getKey()-1);
        // 数据有效性对象
        DataValidation dataValidation = helper.createValidation(constraint, regions);
        dataValidation.setShowPromptBox(true);
        dataValidation.setEmptyCellAllowed(true);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.WARNING);
        sheet.addValidationData(dataValidation);
      }
    }

    if (getList(this.customStyle, sheetIndex, Collections.EMPTY_MAP).size()>0){
      for (Map.Entry<CellRangeAddress, CellStyle> map: this.customStyle.get(sheetIndex).entrySet()){
        CellRangeAddress addresses = map.getKey();
        CellStyle style = map.getValue();
        for (int r = addresses.getFirstRow()>=0?addresses.getFirstRow():0; r <= (addresses.getLastRow()<=sheet.getLastRowNum()?addresses.getLastRow():sheet.getLastRowNum()); r++){
          for (int l = addresses.getFirstColumn()>=0?addresses.getFirstColumn():0;
               l <= (addresses.getLastColumn()<=sheet.getRow(r).getLastCellNum()?addresses.getLastColumn():sheet.getRow(r).getLastCellNum());
               l++){
            Cell cell = sheet.getRow(r).getCell(l);
            //使用同一个style, 与不同的cellstyle合并可能会产生意想不到的结果
            mergeCellStyle(style, cell.getCellStyle());
            cell.setCellStyle(style);
          }

        }
      }
    }
  }

  public void export(File file) throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException {
    export(new FileOutputStream(file));
  }

  public void export(OutputStream out) throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException {
    generate();
    this.workbook.write(out);
    this.workbook.close();
  }

  /**
   *
   * @param sheet
   * @param title
   * @return 表头占用的行数
   */
  private int exportTitle(Sheet sheet, List<?> title, Boolean mergeHorizonFirst){
    if (title==null || title.size() == 0) {
      return 0;
    }
    int maxRowCount = 1;
    int length = title.size();
    List<Row> rows = new ArrayList<>();
    Row row = sheet.createRow(0);
    rows.add(row);
    int[] rowCount = new int[length];
    for (int i = 0; i < length; i++){
      Object titleCol = title.get(i);
      if (titleCol == null){
        rowCount[i] = 0;
        continue;
      }
      if (titleCol instanceof String){
        setCell(row.createCell(i), titleCol, titleStyle);
        rowCount[i] = 1;
      }if (titleCol instanceof List){
         int curRowCount = ((List)titleCol).size();
         for (int j = rows.size(); j< curRowCount; j++){
           rows.add(sheet.createRow(j));
         }
         for (int j = 0; j < curRowCount; j++){
           setCell(rows.get(j).createCell(i), ((List)titleCol).get(j), titleStyle);
         }
         rowCount[i] = curRowCount;
         maxRowCount = curRowCount>maxRowCount?curRowCount:maxRowCount;
      } else {
        setCell(row.createCell(i), titleCol, titleStyle);
        rowCount[i] = 1;
      }
    }
    if (maxRowCount == 1){
      return maxRowCount;
    } else {
      //复杂表头，需要检查是否需要合并单元格
      //判断是否需要纵向合并
      boolean verticalMerge = false;
      if (mergeHorizonFirst==null){
        mergeHorizonFirst = true;
      }
      for (int i = 1; i<length;i++){
        if (rowCount[i] != rowCount[i-1]){
          verticalMerge = true;
        }
      }
      boolean[][] isCellMerged = new boolean[maxRowCount][length];
      if (verticalMerge){
        for (int i = 0; i < length; i++){
          if (rowCount[i]<maxRowCount){
            //合并多余出来的单元格
            mergeCell(sheet, rowCount[i], i, maxRowCount-1, i);
            for (int k = rowCount[i]; k<=maxRowCount-1;k++){
              isCellMerged[k][i] = true;
            }
          }
        }
      }
      //找到相同的内容块做横向或者>=2×2的合并， 默认优先做水平合并。
      //优先垂直合并暂不做吧。真TM复杂
      int mergeStartRow = 0;
      int mergeStartCol = 0;
      int mergeEndRow = 0;
      int mergeEndCol = 0;
      int r = 0, c = 0;
      for (; r< maxRowCount; r++){
        mergeStartRow = r;
        mergeEndRow = r;
        while (true){
          while (c<length-1 && !isCellMerged[r][c] && !isCellMerged[r][c+1] && isCellEqual(title,r,c, r, c+1)){
            mergeEndCol = ++c;
          }
          if (mergeEndCol>mergeStartCol){
            //判断纵向是否需要合并
            for (int i = mergeStartRow; i< maxRowCount-1; i++){
              boolean needVerticalMerge = true;
              for (int j = mergeStartCol; j<=mergeEndCol; j++){
                needVerticalMerge = isCellEqual(title, i, j, i + 1, j);
                if (!needVerticalMerge){
                  break;
                }
              }
              if (needVerticalMerge){
                mergeEndRow = i+1;
              }else {
                break;
              }
            }
            mergeCell(sheet, mergeStartRow, mergeStartCol, mergeEndRow, mergeEndCol);
            for (int m=mergeStartRow; m <= mergeEndRow; m++){
              for (int n = mergeStartCol; n <= mergeEndCol; n++){
                isCellMerged[m][n] = true;
              }
            }
          } else if (r < maxRowCount-1){
            //横向不需合并，判断纵向是否需要合并
            int tmpR = r;
            while (tmpR<maxRowCount-1 &&!isCellMerged[tmpR][c] && !isCellMerged[tmpR+1][c] && isCellEqual(title, tmpR, c, tmpR+1, c)){
              mergeEndRow = ++tmpR;
            }
            if (mergeEndRow>mergeStartRow){
              mergeCell(sheet, mergeStartRow, mergeStartCol, mergeEndRow, mergeEndCol);
              for (int m=mergeStartRow; m <= mergeEndRow; m++){
                for (int n = mergeStartCol; n <= mergeEndCol; n++){
                  isCellMerged[m][n] = true;
                }
              }
            }
          }
          c++;
          while (c<length && isCellMerged[r][c]){
            c++;
          }
          if (c>=length){
            mergeStartCol = 0;
            mergeEndCol = 0;
            c=0;
            break;
          } else {
            mergeStartCol = c;
            mergeEndCol = c;
          }
          mergeEndRow = r;
        }
      }
    }
    return maxRowCount;
  }

  private<E> List<E> setList(List<E> list, int index, E data, E fillEmpty, int listCapacity){
    if (list == null){
      list = new ArrayList<E>(listCapacity);
    }
    if (index<0){
      list.add(data);
      return list;
    }
    if (index<list.size()){
      list.set(index, data);
      return list;
    }
    for (int i = list.size(); i < index; i++){
      list.add(fillEmpty);
    }
    list.add(data);
    return list;
  }

  private<E> List<E> setList(List<E> list, E data, int initialCapacity){
    return setList(list, -1, data, null, initialCapacity);
  }

  private<E> List<E> setList(List<E> list, int index, E data, E fillEmpty){
    return setList(list, index, data, fillEmpty, index);
  }

  private <E> E getList(List<E> list, int index, E defaultValue){
    if (list == null || index>=list.size() || index< 0){
      return defaultValue;
    }
    E r = list.get(index);
    if (r == null) {
      return defaultValue;
    }
    return r;
  }

  private <E> E getList(List<E> list, int index) {
    return getList(list, index, null);
  }

  private boolean isCellEqual(List<?> title, int firstCellRow, int firstCellCol, int secondCellRow, int secondCellCol){
    if (((List)title.get(firstCellCol)).get(firstCellRow)==null){
      if (((List)title.get(secondCellCol)).get(secondCellRow)==null){
        return true;
      }
      return false;
    }
    return ((List)title.get(firstCellCol)).get(firstCellRow).equals(((List)title.get(secondCellCol)).get(secondCellRow));
  }

  private int mergeCell(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol){
    if (lastRow > firstRow || lastCol > firstCol){
      sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
      return (lastRow-firstRow+1)*(lastCol-firstCol+1);
    }
    return 0;
  }

  private List<Object> flatData(Object data, Class type, List<String> sortedFields, Map<String, Method> beanGetters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    if (List.class.isAssignableFrom(type)){
      return (List<Object>) data;
    } else if (Map.class.isAssignableFrom(type)){
      List<Object> result = new ArrayList<>(this.sortedFields.size());
      for (String key: sortedFields){
        if (key==null||"".equals(key.trim())){
          result.add(null);
        } else {
          result.add(((Map)data).get(key));
        }
      }
      return result;
    } else if (Serializable.class.isAssignableFrom(type)){
      List<Object> result = new ArrayList<>(this.sortedFields.size());
      for (String key: sortedFields){
        if (key==null||"".equals(key.trim())){
          result.add(null);
        } else {
          result.add(beanGetters.getOrDefault(key, BeanUtil.getBeanGetter(type, key)).invoke(data));
        }
      }
      return result;
    }
    return new ArrayList<>();
  }

  private void setCell(Cell cell, Object data){
    setCell(cell, data, null);
  }

  private int setCell(Cell cell, Object data, CellStyle style){
    if (data == null){
      return 0;
    } else if (data instanceof Number){
      cell.setCellValue(((Number)data).doubleValue());
    } else if (data instanceof String){
      cell.setCellValue((String)data);
    } else if (data instanceof Boolean){
      cell.setCellValue((boolean)data);
    } else if (data instanceof Date){
      //cell.setCellValue(dateFormat.format(data));
      cell.setCellValue((Date) data);
      if (dataFormat==null){
        dataFormat = this.workbook.getCreationHelper().createDataFormat();
      }
      if (style == null){
        style = this.workbook.createCellStyle();
      }
      style.setDataFormat(dataFormat.getFormat(datetimeFormat));
    } else if (data instanceof Calendar){
      cell.setCellValue(((Calendar)data));
    } else if (data instanceof Serializable){
      cell.setCellValue(data.toString());
    } else {
      return 0;
    }
    if (style!=null){
      cell.setCellStyle(style);
    }
    return 1;
  }

  /**
   * style合并可能会产生意想不到的结果
   * @param styleTo
   * @param styleFrom
   */
  private void mergeCellStyle(CellStyle styleTo, CellStyle styleFrom){
    if (styleTo.getBorderBottom().equals(BorderStyle.NONE) && !styleFrom.getBorderBottom().equals(BorderStyle.NONE)){
      styleTo.setBorderBottom(styleFrom.getBorderBottom());
    }
    if (styleTo.getBorderTop().equals(BorderStyle.NONE) && !styleFrom.getBorderTop().equals(BorderStyle.NONE)){
      styleTo.setBorderTop(styleFrom.getBorderTop());
    }
    if (styleTo.getBorderLeft().equals(BorderStyle.NONE) && !styleFrom.getBorderLeft().equals(BorderStyle.NONE)){
      styleTo.setBorderLeft(styleFrom.getBorderLeft());
    }
    if (styleTo.getBorderRight().equals(BorderStyle.NONE) && !styleFrom.getBorderRight().equals(BorderStyle.NONE)){
      styleTo.setBorderRight(styleFrom.getBorderRight());
    }
    if (styleTo.getAlignment().equals(HorizontalAlignment.GENERAL) && !styleFrom.getAlignment().equals(HorizontalAlignment.GENERAL)){
      styleTo.setAlignment(styleFrom.getAlignment());
    }
    if (styleTo.getVerticalAlignment().equals(VerticalAlignment.BOTTOM) && !styleFrom.getVerticalAlignment().equals(VerticalAlignment.BOTTOM)) {
      styleTo.setVerticalAlignment(styleFrom.getVerticalAlignment());
    }
    if (styleTo.getFillBackgroundColor() == IndexedColors.AUTOMATIC.index && styleFrom.getFillBackgroundColor() != IndexedColors.AUTOMATIC.index){
      styleTo.setFillBackgroundColor(styleFrom.getFillBackgroundColor());
    }
    if (styleTo.getFillForegroundColor() == IndexedColors.AUTOMATIC.index && styleFrom.getFillForegroundColor() != IndexedColors.AUTOMATIC.index) {
      styleTo.setFillForegroundColor(styleFrom.getFillForegroundColor());
    }
  }

  private static abstract class BeanUtil {

    public static List<Method> getBeanGetters(Class<?> clazz) {
      Class cl = clazz;
      List<Method> methodList = new LinkedList<>();
      while (cl != null && !cl.equals(Object.class)) {
        Method[] methods = cl.getDeclaredMethods();
        for (Method m : methods) {
          if (m.getParameterCount() > 0){
            continue;
          }
          if (Modifier.isProtected(m.getModifiers())
                  || Modifier.isAbstract(m.getModifiers())
                  ||Modifier.isPrivate(m.getModifiers())){
            continue;
          }
          if (m.getName().startsWith("get")) {
            methodList.add(m);
          } /*else if (m.getName().startsWith("is") && m.getReturnType().equals(Boolean.class)){
            methodList.add(m);
          }*/
        }
        cl = cl.getSuperclass();
      }
      return methodList;
    }

    public static Method getBeanGetter(Class<?> clazz, String fieldName) throws NoSuchMethodException {
      return getBeanGetter(clazz, fieldName, true);
    }

    private static Method getBeanGetter(final Class<?> clazz,final String fieldName,final boolean captured) throws NoSuchMethodException {
      Class cl = clazz;
      String field = fieldName;
      if (captured) {field = capture(fieldName);}
      while (cl != null && !Object.class.equals(cl)) {
        try {
          return cl.getDeclaredMethod("get" + field);
        } catch (NoSuchMethodException var5) {
          cl = cl.getSuperclass();
        }
      }
      if (captured){
        return getBeanGetter(clazz, fieldName, false);
      } else {
        throw new NoSuchMethodException("No Such Field:" + fieldName);
      }
    }

    public static String capture(String str) {
      char[] chars = str.toCharArray();
      if (chars.length == 0) {
        return str;   //同一对象
      } else if (chars[0] >= 'a' && chars[0] <= 'z') {
        chars[0] -= 'a' - 'A';
        return new String(chars);   //新对象
      } else {
        return str;   //同一对象
      }
    }

    public static String unCapture(String str) {
      char[] chars = str.toCharArray();
      if (chars.length == 0) {
        return str;   //同一对象
      } else if (chars[0] >= 'A' && chars[0] <= 'Z') {
        chars[0] += 'a' - 'A';
        return new String(chars);   //新对象
      } else {
        return str;   //同一对象
      }
    }
  }

}
