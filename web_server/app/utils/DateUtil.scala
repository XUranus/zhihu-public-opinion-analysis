package utils

import java.text.SimpleDateFormat

object DateUtil {

  def timestampToDate(timestamp:Long): java.util.Date = {
    val date = new java.util.Date(timestamp)
    date
  }

  //pattern "yyyy-MM-dd HH:mm:ss"
  def timestampToDateString(timestamp:Long, pattern:String): String = {
    val date = new java.util.Date(timestamp)
    new SimpleDateFormat(pattern).format(date)
  }

  //second level timestamp
  def dateToTimestampString(date:java.util.Date): String = {
    val timestamp = date.getTime
    (timestamp/1000).toString
  }

  //return timestamp in second level
  def dateStringToTimestamp(formatString:String, dateString:String): Long = {
    //"yyyy-MM-dd
    val df = new SimpleDateFormat(formatString);
    val date = df.parse(dateString);
    date.getTime/1000
  }

  def dateStringToDate(formatString:String, dateString:String): java.util.Date = {
    //"yyyy-MM-dd
    val df = new SimpleDateFormat(formatString);
    val date = df.parse(dateString);
    date
  }

}
