package cn.churen.biz.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;

@Slf4j
public class ABasicUtil {

  public static boolean getBoolean(Object bool, boolean defaultValue) {
    boolean b = defaultValue;
    try {
      b = (null == bool ? defaultValue : Boolean.parseBoolean(bool.toString()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return b;
  }

  public static int getInt(Object value, int defaultValue) {
    int r = defaultValue;
    try {
      r = (null == value ? defaultValue : Integer.parseInt(value.toString()));
    } catch (Exception ex) {
      return r;
    }
    return r;
  }

  public static String getStr(Object value, String defaultValue) {
    return null == value ? defaultValue : value.toString();
  }

  public static boolean isAllValuesBlank(Map<String, String> rowData) {
    if (null == rowData || rowData.isEmpty()) { return true; }
    for (String k : rowData.keySet()) {
      if (StringUtils.isNotBlank(rowData.get(k))) {
        return false;
      }
    }
    return true;
  }

  public static Date getDay() {
    Date date = new Date();
    DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
    DateUtils.truncate(date, Calendar.MINUTE);
    DateUtils.truncate(date, Calendar.MILLISECOND);
    return date;
  }

  public static Date getDay(Date date) {
    if (null == date) { date = new Date(); }
    DateUtils.truncate(date, Calendar.HOUR_OF_DAY);
    DateUtils.truncate(date, Calendar.MINUTE);
    DateUtils.truncate(date, Calendar.MILLISECOND);
    return date;
  }

  public static String getDayStr() {
    return DateFormatUtils.format(new Date(), "yyyy-MM-dd");
  }
  
  public static String getDayStr(Date date) {
    date = (null == date) ? new Date() : date;
    return DateFormatUtils.format(date, "yyyy-MM-dd");
  }

  public static String getDateTimeStr() {
    return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
  }

  public static String getDateTimeStr(Date date) {
    date = (null == date) ? new Date() : date;
    return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
  }

  public static List<String> getDateTimeStr4OAuth() {
    Date a = new Date();
    Date b = DateUtils.addHours(a, -1);
    return Arrays.asList(
        DateFormatUtils.format(a, "yyyyMMddHH"),
        DateFormatUtils.format(b, "yyyyMMddHH")
    );
  }

  public static String uuid() {
    return UUID.randomUUID().toString().trim().replaceAll("-", "");
  }
}
