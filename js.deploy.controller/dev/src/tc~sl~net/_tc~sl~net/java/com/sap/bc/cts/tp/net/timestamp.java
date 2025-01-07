package com.sap.bc.cts.tp.net;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Timestamp
{
  private static final SimpleDateFormat formatter;
  private static long nextTime =0;
  private static long currentTime =0;
  private static String currentTimestamp =null;
  static {
    formatter = new SimpleDateFormat ("yyyyMMddHHmmss SSSS");
    // Use UTC time as timestamp
    // formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    formatter.setTimeZone(TimeZone.getDefault());
    get();
  }
  
  public static String get() {
    // Format the current time.
    currentTime = System.currentTimeMillis();
    if(nextTime<currentTime){
    nextTime=currentTime;
    currentTimestamp = formatter.format(new Date(currentTime));
    }
    return currentTimestamp;
  }
  
  public static String calculate(long time){
    return formatter.format(new Date(time));
  }
  
  public static void main(String[] args) {
    String tstp_S = (new Timestamp()).get();
    System.out.println(tstp_S);
  }
}
