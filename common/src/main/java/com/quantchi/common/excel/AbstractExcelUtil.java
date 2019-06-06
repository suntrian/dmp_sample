package com.quantchi.common.excel;

import lombok.*;
import org.apache.poi.ss.usermodel.DateUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public abstract class AbstractExcelUtil<P extends AbstractExcelUtil> {

  protected List<ExcelSheetConfig> sheetConfigs = new LinkedList<>();
  protected Map<String, Integer> sheetPatternToIndexMap;
  protected Map<Integer, Map<String, Integer>> columnPatternToIndexMap;

  //此时间日期判断的正则表达式比较简单，没有判断更复杂的情况如2019-14-29 等也能匹配
  protected Pattern datetimePattern = Pattern.compile("(?:(?<year>(?:[1-9]\\d)?\\d{2})[/-])?(?<mon>[0-1]?\\d)[/-](?<day>[0-3]?\\d)\\s+(?<hour>[0-2]?\\d):(?<min>[0-5]?\\d)(?::(?<sec>[0-5]?\\d))?");
  protected Pattern datePattern = Pattern.compile("((?<year>(?:[1-9]\\d)?\\d{2})[/-])?(?<mon>[0-1]?\\d)[/-](?<day>[0-3]?\\d)");
  protected Pattern timePattern = Pattern.compile("(?<hour>[0-2]?\\d):(?<min>[0-5]?\\d)(?::(?<sec>[0-5]?\\d))?");

  @SuppressWarnings("unchecked")
  protected <T> T castToType(Object obj,  Class<T> type){
    if (obj == null) return null;
    if (obj instanceof String){
      if (String.class.isAssignableFrom(type)){
        return (T) ((String) obj).trim();
      } else if (Integer.class.isAssignableFrom(type)){
        return (T) Integer.valueOf((String) obj);
      } else if (Double.class.isAssignableFrom(type)) {
        return (T) Double.valueOf((String) obj);
      } else if (Boolean.class.isAssignableFrom(type)){
        boolean t = "true".equalsIgnoreCase(((String) obj).trim())
                || "1".equals(((String) obj).trim())
                || "yes".equalsIgnoreCase(((String) obj).trim())
                || "T".equalsIgnoreCase(((String) obj).trim())
                || "是".equals(((String) obj).trim());
        if (t){return (T) Boolean.valueOf(true);}
        boolean f = "false".equalsIgnoreCase(((String) obj).trim())
                || "0".equals(((String) obj).trim())
                || "no".equalsIgnoreCase(((String) obj).trim())
                || "F".equalsIgnoreCase(((String) obj).trim())
                || "否".equals(((String) obj).trim());
        if (f){ return (T) Boolean.valueOf(false);}
        return null;
      } else if (Date.class.isAssignableFrom(type)){
        return (T) parseDate((String) obj);
      }
    } else if (obj instanceof Double){
      if (Double.class.isAssignableFrom(type)){
        return (T) obj;
      }
      if (String.class.isAssignableFrom(type)){
        if ((Double) obj -((Double) obj).longValue()==0){
          return (T) String.valueOf(((Double) obj).longValue());
        }
        return (T) obj.toString();
      } else if (Integer.class.isAssignableFrom(type)){
        return (T) (Integer)((Double) obj).intValue();
      } else if (Date.class.isAssignableFrom(type)){
        return (T) DateUtil.getJavaDate((Double) obj);
      } else if (Boolean.class.isAssignableFrom(type)){
        return (T) Boolean.valueOf(!obj.equals(0));
      }
    } else if (obj instanceof Boolean){
      if (String.class.isAssignableFrom(type)){
        return (T) ((Boolean)obj?"true":"false");
      } else if (Integer.class.isAssignableFrom(type)){
        return (T) Integer.valueOf ((Boolean)obj?1:0);
      } else if (Double.class.isAssignableFrom(type)){
        return (T) Double.valueOf ((Boolean)obj?1.0:0.0);
      } else if (Date.class.isAssignableFrom(type)){
        throw new ClassCastException("Cannot cast Boolean to Date");
      }
    } else if (obj instanceof Date){
      if (String.class.isAssignableFrom(type)){
        return (T) String.format("%tc", obj);
      } else if (Integer.class.isAssignableFrom(type)){
        return (T) Integer.valueOf ((int)((Date)obj).getTime());
      } else if (Double.class.isAssignableFrom(type)){
        return (T) (Double.valueOf((double)((Date)obj).getTime()));
      } else if (Date.class.isAssignableFrom(type)){
        return (T) obj;
      }
    }
    return type.cast(obj);
  }

  protected Date parseDate(String date){
    Calendar.Builder builder = new Calendar.Builder();
    Matcher matcher = datetimePattern.matcher(date);
    if (matcher.matches()){
      return builder
              .setDate(Integer.valueOf(matcher.group("year")), Integer.valueOf( matcher.group("mon")),Integer.valueOf(matcher.group("day")))
              .setTimeOfDay(Integer.valueOf(matcher.group("hour")), Integer.valueOf(matcher.group("min")), Integer.valueOf(matcher.group("sec")))
              .build().getTime();
    }
    matcher = datePattern.matcher(date);
    if (matcher.matches()){
      return builder.setDate(Integer.valueOf(matcher.group("year")), Integer.valueOf( matcher.group("mon")),Integer.valueOf(matcher.group("day"))).build().getTime();
    }
    matcher = timePattern.matcher(date);
    if (matcher.matches()){
      return builder.setTimeOfDay(Integer.valueOf(matcher.group("hour")), Integer.valueOf(matcher.group("min")), Integer.valueOf(matcher.group("sec"))).build().getTime();
    }
    throw new ClassCastException(date + "cannot be cast to Date");
  }

  protected <E> List<E> setListValue(List<E> list, int index, E data, E defaultValue){
    if (list == null){
      list = new LinkedList<>();
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

  protected <E> E getListValue(List<E> list, int index, E defaultValue){
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

  protected enum ExcelType {
    XLS,
    XLSX
  }

  private static abstract class BeanUtil {

    public static List<Method> getSetters(Class<?> clazz) {
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
          } else if (m.getName().startsWith("is") && m.getReturnType().equals(Boolean.class)){
            methodList.add(m);
          }
        }
        cl = cl.getSuperclass();
      }
      return methodList;
    }

    public static Method getSetter(Class<?> clazz, String fieldName, Class<?>... parameterTypes) throws NoSuchMethodException {
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

    public static List<Method> getGetters(Class<?> clazz) {
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
          } else if (m.getName().startsWith("is") && m.getReturnType().equals(Boolean.class)){
            methodList.add(m);
          }
        }
        cl = cl.getSuperclass();
      }
      return methodList;
    }

    private static Method getGetter(final Class<?> clazz,final String fieldName) throws NoSuchMethodException {
      Class cl = clazz;
      while (cl != null && !Object.class.equals(cl)) {
        try {
          return cl.getDeclaredMethod("get" + fieldName);
        } catch (NoSuchMethodException var5) {
          cl = cl.getSuperclass();
        }
      }
      throw new NoSuchMethodException("No Such Field:" + fieldName);
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

  public static class ExcelSheetConfig<T> {

    @Getter(AccessLevel.PROTECTED) @Setter
    private String sheetNamePattern;
    @Getter @Setter
    protected String realSheetName;
    @Getter @Setter
    protected int sheetIndex;
    @Getter(value = AccessLevel.PROTECTED) @Setter
    private int titleRows;
    @Getter(AccessLevel.PROTECTED) @Setter
    private Class<T> rowType;
    @Getter @Setter
    private List<ExcelColumnConfig<?>> columnConfigs;

    public static class ExcelColumnConfig<X> {


      @Getter @Setter
      private String columnNamePattern;
      @Getter @Setter
      private String realSheetName;
      @Getter @Setter
      protected String realColumnName;
      @Getter @Setter
      protected int columnIndex;

      @Getter @Setter
      private String fieldName;
      @Getter @Setter
      private Class<X> columnType;
      @Getter @Setter
      private Consumer<X> setter;
      @Getter @Setter
      private Function<Object, X> getter;

      @Getter @Setter
      private X defaultValue;
      @Getter @Setter
      private boolean canBeEmpty;
      @Getter @Setter
      private boolean unique;

      @Getter @Setter
      private List<Map.Entry<String, String>> rules;

    }
  }


  interface Validator<T> {
    void validate(T data, int row) throws ValidateException;
  }

  @SuppressWarnings("unchecked")
  protected class DataValidator<T> implements Validator<T> {

    private List<ExcelSheetConfig.ExcelColumnConfig<Object>> columnConfigs;
    private Map<Object, Map<Object, Integer>> uniqueColumn ;

    public DataValidator(List<ExcelSheetConfig.ExcelColumnConfig<Object>> columnConfigs) {
      this.columnConfigs = columnConfigs;
      this.uniqueColumn = this.columnConfigs.stream()
              .filter(ExcelSheetConfig.ExcelColumnConfig::isUnique)
              .collect(Collectors.toMap(ExcelSheetConfig.ExcelColumnConfig::getColumnNamePattern, i->new LinkedHashMap<>()));
    }

    @Override
    public void validate(T data, int row) throws ValidateException{
      List<ValidateMessage> messages = new LinkedList<>();
      for (ExcelSheetConfig.ExcelColumnConfig<Object> conf : columnConfigs) {
        Object val = conf.getGetter().apply(data);
        if (val == null || "".equals(val)){
          //check empty or set default
          if (conf.isCanBeEmpty()){
            continue;
          }
          if (conf.getDefaultValue() !=null) {
            conf.getSetter().accept(conf.getDefaultValue());
          } else {
            // add to exception
            messages.add(ValidateMessage.builder()
                    .sheet(conf.getRealSheetName())
                    .column(conf.getRealColumnName())
                    .col(conf.getColumnIndex())
                    .row(row)
                    .rule("非空字段")
                    .value(val)
                    .build());
          }
        } else {
          //check rules
          for (Map.Entry<String, String> rule : conf.getRules()) {
            if (!val.toString().matches(rule.getValue())){
              messages.add(ValidateMessage.builder()
                      .sheet(conf.getRealSheetName())
                      .column(conf.getRealColumnName())
                      .row(row)
                      .col(conf.getColumnIndex())
                      .rule(rule.getKey())
                      .value(val)
                      .build());
            }
          }
        }
        // check unique
        if (this.uniqueColumn.containsKey(conf.getColumnNamePattern())){
          Integer count = this.uniqueColumn.get(conf.getColumnNamePattern()).compute(val, (k,v)->v==null?1:v+1);
          if (count>1){
            // add to exception
            messages.add(ValidateMessage.builder()
                    .sheet(conf.getRealSheetName())
                    .column(conf.getRealColumnName())
                    .row(row)
                    .col(conf.getColumnIndex())
                    .rule("唯一字段")
                    .value(val)
                    .build());
          }
        }
      }
      if (messages.size()>0) {
        throw new ValidateException(messages);
      }
    }
  }

  @Data
  @Builder
  public static class ValidateMessage {
    private String sheet;
    private int row;
    private int col = -1;
    private String rule;
    private String column;
    private Object value;

    @Override
    public String toString() {
      if (col<0){
        return (sheet==null?"":sheet) + ": [" + column + "]列不存在";
      }
      return (sheet==null?"":sheet)  + coordinate() + ": [" + column + "]列的[" + value + "]不满足规则[" + rule + "]";
    }

    private String coordinate(){
      StringBuilder builder = new StringBuilder("$");
      int tmp = col;
      while (tmp>=0){
        builder.insert(1,(char)(tmp%26+65));
        tmp = tmp/26-1;
      }
      builder.append("$").append(row+1);
      return builder.toString();
    }
  }

  public static class ValidateException extends Exception {

    @Getter
    private List<ValidateMessage> validateMessages;

    public ValidateException(List<ValidateMessage> validateMessages) {
      this.validateMessages = validateMessages;
    }
  }

}
