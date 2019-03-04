package com.quantchi.common.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * double, date, boolean 等等类型未测试
 */
@SuppressWarnings("all")
public class ExcelReader {

  private int sheetCount = 1;

  private Workbook workbook;
  private POIFSFileSystem poifsFileSystem;
  private OPCPackage opcPackage;

  private List<Class> columnClass;
  private List<List<String>> excelColumns;
  private List<Map<String, Method>> classSetters;
  private List<Integer> titleRows;
  //输出的sheet
  private Set<Integer> sheetRead;

  public ExcelReader setExcelClass(Class<?> clazz){
    this.columnClass = setListValue(this.columnClass, -1, clazz, null, sheetCount);
    return this;
  }

  public ExcelReader setExcelClasses( int sheetNum, Class<?> clazz){
    this.columnClass = setListValue(this.columnClass, sheetNum-1, clazz, null, sheetCount);
    return this;
  }

  public ExcelReader setTitleRows(int titleRows){
    this.titleRows = setListValue(this.titleRows, -1, titleRows, 0, sheetCount);
    return this;
  }

  public ExcelReader setTitleRows(int sheetNum, int titleRows) {
    this.titleRows = setListValue(this.titleRows, sheetNum-1, titleRows, 0, sheetCount);
    return this;
  }

  public ExcelReader setSkipRows(Integer skipRows){
    return setTitleRows(skipRows);
  }

  public ExcelReader setSkipRows(int sheetNum, int skipRows){
    return setTitleRows(sheetNum, skipRows);
  }

  public ExcelReader setSheetToRead(Integer sheetNum){
    if (this.sheetRead == null){
      this.sheetRead = new HashSet<>(sheetCount);
    }
    this.sheetRead.add(sheetNum);
    return this;
  }

  public ExcelReader setSheetToRead(Integer... sheetNums){
    if (this.sheetRead == null){
      this.sheetRead = new HashSet<>(sheetCount);
    }
    this.sheetRead.addAll(Arrays.asList(sheetNums));
    return this;
  }

  public ExcelReader setSortedFields(String... columns){
    return setSortedFields(Arrays.asList(columns));
  }

  public ExcelReader setSortedFields(List<String> columns){
    return setSortedFields(-1, columns);
  }
  /**
   *
   * @param columns  excel输出的列属性名称
   * @param sheetNum  base 1
   * @return this
   */
  public ExcelReader setSortedFields(int sheetNum, List<String> columns) {
    this.excelColumns = setListValue(this.excelColumns, sheetNum-1, columns, Collections.emptyList(), sheetCount);
    return this;
  }

  private int countSheet() {
    return this.workbook.getNumberOfSheets();
  }

  public<T> List<T> read(File file) throws IOException, InvalidFormatException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    String suffix = fileExtension(file.getName());
    switch (suffix.toLowerCase()){
      case "xls":
        return readXls(file, true);
      case "xlsx":
      case "xlsm":
        return readXlsx(file, true);
      case "":
      default:
        throw new IllegalArgumentException("not excel extension");
    }
  }

  protected <T> List<T> readXlsx(File file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    return readXls(file, true);
  }

  protected<T> List<T> readXlsx(File file, boolean readOnly) throws IOException, InvalidFormatException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    this.opcPackage = OPCPackage.open(file, readOnly?PackageAccess.READ:PackageAccess.READ_WRITE);
    this.workbook = new XSSFWorkbook(this.opcPackage);
    return parse();
  }

  protected<T> List<T> readXlsx(InputStream inputStream) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    this.workbook = new XSSFWorkbook(inputStream);
    return parse();
  }

  protected<T> List<T> readXls(InputStream inputStream) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    this.workbook = new HSSFWorkbook(inputStream);
    return parse();
  }

  protected<T> List<T> readXls(File file) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    return readXls(file, true);
  }

  protected<T> List<T> readXls(File file, boolean readOnly) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    this.poifsFileSystem = new POIFSFileSystem(file, readOnly);
    this.workbook = new HSSFWorkbook(poifsFileSystem);
    return parse();
  }

  private void preHandler() throws NoSuchMethodException {
    int actualSheetCount = countSheet();
    if (actualSheetCount < this.sheetCount){
      this.sheetCount = actualSheetCount;
    }
    for (int i = 0; i < this.sheetCount; i++){
      Class clazz;
      if ( (clazz = getListValue(this.columnClass, i, null)) == null){
        this.columnClass = setListValue(this.columnClass, i, ArrayList.class, ArrayList.class, this.sheetCount);
      } else {
        if (Map.class.isAssignableFrom(clazz)){
          if (getListValue(this.excelColumns, i, Collections.emptyList()).size() == 0){
            int titleRows;
            if ((titleRows = getListValue(this.titleRows, i , 0)) == 0){
              throw new IllegalStateException("请检查参数");
            } else if (titleRows == 1){
              //需检查是否存在数据
              Row titleRow = this.workbook.getSheetAt(i).getRow(0);
              List<String> titles = new ArrayList<>(titleRow.getLastCellNum());
              for (int l = 0; l < titleRow.getLastCellNum(); l++){
                titles.add(titleRow.getCell(l).getStringCellValue());
              }
              this.excelColumns = setListValue(this.excelColumns, i, titles, Collections.emptyList(), sheetCount);
            }
          }
        } else if (Serializable.class.isAssignableFrom(clazz)){
          List<String> columns;
          if ( (columns = getListValue(this.excelColumns, i, Collections.emptyList())).size() == 0){
            throw new IllegalStateException("未指定输出列的属性");
          }
          List<Method> methods = BeanUtil.getBeanSetters(clazz);
          Map<String, Method> methodMap = new HashMap<>(columns.size());
          OUT: for (String col: columns){
            if (col == null || "".equals(col.trim())){
              continue;
            }
            Iterator<Method> methodIter = methods.iterator();
            while (methodIter.hasNext()){
              Method method = methodIter.next();
              if (method.getName().substring(3).equals(BeanUtil.capture(col))){
                methodMap.put(col, method);
                methodIter.remove();
                continue OUT;
              }
            }
            throw new NoSuchMethodException("未找到方法:" + col);
          }
          this.classSetters = setListValue(this.classSetters, i, methodMap, Collections.emptyMap(),sheetCount);
        } else {

        }
      }
    }
  }

  private <T>  List<T> parse(final int sheetNum) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, IOException {
    if (sheetNum > this.sheetCount){
      throw new IllegalArgumentException("sheet num overflow");
    }
    preHandler();
    int sheetIndex = sheetNum -1;
    Class clazz;
    if ((clazz = getListValue(this.columnClass, sheetIndex, null))== null){
      throw new IllegalStateException("un given data type");
    }
    if (!List.class.isAssignableFrom(clazz)){
      if (getListValue(this.excelColumns, sheetIndex, null) == null){
        throw new IllegalArgumentException("un given properties");
      }
    }
    Sheet sheet = this.workbook.getSheetAt(sheetIndex);
    List<T> sheetData = new ArrayList<>(sheet.getLastRowNum());
    Integer titleRows = getListValue(this.titleRows, sheetIndex, 0);
    for (int rowIndex = titleRows; rowIndex < sheet.getLastRowNum(); rowIndex ++){
      Row row = sheet.getRow(rowIndex);
      T rowData = (T)readRow(row, getListValue(this.columnClass, sheetIndex, null),
              getListValue(this.excelColumns, sheetIndex, Collections.emptyList()),
              getListValue(this.classSetters, sheetIndex, Collections.emptyMap()));
      sheetData.add(rowData);
    }
    postHandler();
    return sheetData;
  }

  private List parse() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
    if (sheetCount == 1 || (this.sheetRead !=null && this.sheetRead.size() == 1)){
      return parse(1);
    }
    if (this.sheetRead!=null && this.sheetRead.size()>0){
      List<List> result = new ArrayList<>(this.sheetRead.size());
      for (Integer sheetNum: this.sheetRead){
        result.add(parse(sheetNum-1));
      }
      return result;
    }
    if (sheetCount>1){
      List<List> result = new ArrayList<>(this.sheetCount);
      for (int i = 0; i < this.sheetCount; i++){
        result.add(parse(i));
      }
      return result;
    }
    return Collections.emptyList();
  }

  private<T> T readRow(Row row, Class<T> clazz, List<String> properties, Map<String, Method> methodMap) throws IllegalAccessException, InstantiationException, InvocationTargetException {
    T rowData = clazz.newInstance();
    for (int i = 0; i < properties.size(); i++){
      if (properties.get(i) == null || "".equals(properties.get(i))) {
        continue;
      }
      Cell cell = row.getCell(i);
      Object val;
      switch (cell.getCellType()){
        case STRING:
          val = cell.getStringCellValue();
          break;
        case NUMERIC:
          val = cell.getNumericCellValue();
          break;
        case BOOLEAN:
          val = cell.getBooleanCellValue();
          break;
        case _NONE:
        case BLANK:
        case ERROR:
          val = null;
          break;
        case FORMULA:
          val = cell.getStringCellValue();
          break;
        default:
          val = null;
          break;
      }
      if (rowData instanceof List){
        ((List) rowData).add(val);
      } else if (rowData instanceof Map){
        ((Map) rowData).put(properties.get(i), val);
      } else {
        Method method = methodMap.get(properties.get(i));
        Class paramType = method.getParameterTypes()[0];
        method.invoke(rowData, paramType.cast(val));
      }
    }
    return rowData;
  }

  private void postHandler() throws IOException {
    if (this.opcPackage!=null){
      this.opcPackage.close();
    }
    if (this.poifsFileSystem!=null){
      this.poifsFileSystem.close();
    }
  }

  private <E> List<E> setListValue(List<E> list, int index,E data, E defaultValue, int initialCapacity){
    if (list == null){
      list = new ArrayList<>(initialCapacity);
    }
    if (index<0){
      list.add(data);
    } else if (list.size()>index){
      list.set(index, data);
    } else {
      for (int i = list.size(); i<index; i++){
        list.add(defaultValue);
      }
      list.add(data);
    }
    return list;
  }

  private <E> E getListValue(List<E> list, int index, E defaultValue){
    if (list == null){
      return defaultValue;
    }
    if (index<0 || index>=list.size()){
      return defaultValue;
    }
    E t = list.get(index);
    if (t == null){
      return defaultValue;
    }
    return t;
  }

  private String fileExtension(String fileName){
    if (fileName==null){
      return null;
    }
    int intExtension = fileName.lastIndexOf('.');
    if (intExtension<0){
      return "";
    }
    return fileName.substring(intExtension+1);
  }

  private static abstract class BeanUtil {

    public static List<Method> getBeanSetters(Class<?> clazz) {
      Class cl = clazz;
      List<Method> methodList = new LinkedList<>();
      while (cl != null && !cl.equals(Object.class)) {
        Method[] methods = cl.getDeclaredMethods();
        for (Method m : methods) {
          if (m.getParameterCount() == 0 || m.getParameterCount() > 1){
            continue;
          }
          if (Modifier.isProtected(m.getModifiers())
                  || Modifier.isAbstract(m.getModifiers())
                  ||Modifier.isPrivate(m.getModifiers())){
            continue;
          }
          if (m.getName().startsWith("set")) {
            methodList.add(m);
          } /*else if (m.getName().startsWith("is") && m.getReturnType().equals(Boolean.class)){
            methodList.add(m);
          }*/
        }
        cl = cl.getSuperclass();
      }
      return methodList;
    }

    public static Method getBeanSetter(Class<?> clazz, String fieldName, Class<?>... parameterTypes) throws NoSuchMethodException {
      Class<?> cl = clazz;
      String field = capture(fieldName);
      while (cl != null) {
        try {
          return cl.getDeclaredMethod("set" + field, parameterTypes);
        } catch (NoSuchMethodException var5) {
          cl = cl.getSuperclass();
        }
      }
      throw new NoSuchMethodException(fieldName);
    }

    public static String capture(String str) {
      char[] chars = str.toCharArray();
      if (chars.length == 0) {
        return str;   //同一对象
      } else if (chars[0] >= 'a' && chars[0] <= 'z') {
        chars[0] -= 32;
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
        chars[0] += 32;
        return new String(chars);   //新对象
      } else {
        return str;   //同一对象
      }
    }
  }

}
