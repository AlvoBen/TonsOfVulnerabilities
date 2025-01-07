/*==============================================================================
    File:         EvictionPeriod.java       
    Created:      May 7, 2004

    $Author: d042378 $
    $Revision: #1 $
    $Date: 2004/08/26 $
==============================================================================*/
package com.sap.util.cache;

import java.io.Serializable;

/**
 * The <code>EvictionPeriod</code>  class defines the constants for indicating
 * a specific eviction period. The constants can be used in conjunction with
 * the element configuration and absolute eviction time settings. 
 * 
 * @author Michael Wintergerst
 * @version $Revision: #1 $
 */
public final class EvictionPeriod implements Serializable {
    
  static final long serialVersionUID = 3932906055325348362L;
  
    /**
     * Hourly period.
     */
    public static final EvictionPeriod HOURLY = new EvictionPeriod(1);
    
    /**
     * Daily period.
     */
    public static final EvictionPeriod DAILY  = new EvictionPeriod(2);
    
    /**
     * Weekly period.
     */
    public static final EvictionPeriod WEEKLY = new EvictionPeriod(3);
    
    /**
     * Description for an hourly period.
     */
    public static final String HOURLY_DESCRIPTION = "Hourly period";

    /**
     * Description for a daily period.
     */
    public static final String DAILY_DESCRIPTION  = "Daily period";
    
    /**
     * Description for a weekly description.
     */
    public static final String WEEKLY_DESCRIPTION = "Weekly period";
    
    /**
     * Internal identifier for an hourly period.
     */
    private static final int HOURLY_ID = 1;
    
    /**
     * Internal identifier for a daily period.
     */
    private static final int DAILY_ID  = 2;
    
    /**
     * Internal identifier for a weekly period.
     */
    private static final int WEEKLY_ID = 3;
    
    /**
     * Milliseconds per minute.
     */
    private static final int MILLISECONDS_PER_MINUTE = 
            60000;
    
    /**
     * Milliseconds per hour.
     */
    private static final int MILLISECONDS_PER_HOUR = 
            60 * MILLISECONDS_PER_MINUTE;
    
    /**
     * Milliseconds per day. 
     */
    private static final int MILLISECONDS_PER_DAY  = 
            24 * MILLISECONDS_PER_HOUR;  
    
    /**
     * The internal identifier of the eviction period for this instance. 
     */
    private int period;
    
    /**
     * The number of days.
     */
    private int day;
    
    /**
     * The number of hours.
     */
    private int hour;
    
    /**
     * The number of minutes.
     */
    private int min;
    
    /**
     * Private constructor prevents instantiation from the the outside.
     * Objects are only constructed internally.
     * 
     * @param period the internal identifier for the eviction period
     */
    private EvictionPeriod(int period) {
        this.period = period;
        
        if (period == 1) {
            this.day  = 0;
            this.hour = 1;
            this.min  = 0;
        }
        else if (period == 2) {
            this.day  = 1;
            this.hour = 0;
            this.min  = 0;
        }
        else if (period == 3) {
            this.day  = 7;
            this.hour = 0;
            this.min  = 0;
        }
    }
    
    /**
     * Constructs an <code>EvictionPeriod</code> object with the specified 
     * time parameters.
     * <p> 
     * Note it is not necessary that the <code>hour</code> parameter
     * value is between <code>0</code> and <code>23</code> nor that the
     * <code>min</code> parameter value is between <code>0</code> and 
     * <code>59</code>.  
     * 
     * @param day  the number of days
     * @param hour the number of hours
     * @param min  the number of minutes
     * 
     * @throws IllegalArgumentException if one of parameters is set to a 
     *         negative value
     */
    public EvictionPeriod(int day, int hour, int min) {
        this(0);
        
        // check parameter
        if (day < 0) {
            throw new IllegalArgumentException(
                    "The parameter \"day\" is negative");
        }
        if (hour < 0) {
            throw new IllegalArgumentException(
                    "The parameter \"hour\" is negative");
        }
        if (min < 0) {
            throw new IllegalArgumentException(
                    "The parameter \"min\" is negative");
        }
        
        // check the range of parameter values
        this.min  = min % 60;
        
        hour += (min / 60);
        this.hour = hour % 24;
        
        day += (hour / 24);
        this.day  = day;
    }
    
    /**
     * Returns the number of specified days.
     * 
     * @return the number of specified days
     */
    public int getDays() {
        return day;
    }
    
    /**
     * Returns the number of specified hours.
     * <p>
     * Note the returned value is always between <code>0</code> and
     * <code>23</code>.
     * 
     * @return the number of specified hours
     */
    public int getHours() {
        return hour;
    }
    
    /**
     * Returns the number of specified minutes.
     * <p>
     * Note the returned value is always between <code>0</code> and
     * <code>59</code>.
     * 
     * @return the number of specified minutes
     */
    public int getMinutes() {
        return min;
    }
    
    /**
     * Returns the eviction period in milliseconds.
     * 
     * @return the eviction period in milliseconds
     */
    public long getMilliSeconds() {
        return getDays()    * MILLISECONDS_PER_DAY    + 
               getHours()   * MILLISECONDS_PER_HOUR   + 
               getMinutes() * MILLISECONDS_PER_MINUTE;
    }
    
    /**
     * Overrides the default implementation and checks whether the specified
     * object is of type <code>EvictionPeriod</code> and constitutes 
     * the same eviction period.
     *  
     * @return <code>true</code> if the specified parameter is of type
     *         <code>EvictionPeriod</code> and constitutes the same 
     *         eviction period; otherwise <code>false</code> is returned
     */
    public boolean equals(Object anObject) {      
        if (anObject != null && 
            (anObject.getClass().equals(this.getClass()))) {
               
            EvictionPeriod periodObj = (EvictionPeriod) anObject;

            if (periodObj.day  == day  && 
                periodObj.hour == hour &&
                periodObj.min  == min) {             
                                                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Overwrites the default <code>hashCode</code> implementation in
     * <code>java.lang.Object</code>.
     * 
     * @return hash code
     */
    public int hashCode() {        
        return day ^ hour ^ min;
    }

    
    /**
     * Gets a string describing the eviction period.
     * 
     * @return the description of the eviction period
     */
    public String toString() {
        
        // check the interval
        switch(period) {
            case HOURLY_ID: 
                return HOURLY_DESCRIPTION;
            case DAILY_ID: 
                return DAILY_DESCRIPTION;
            case WEEKLY_ID:
                return WEEKLY_DESCRIPTION;
            default:
                StringBuffer buffer = new StringBuffer();
                buffer.append("Days=<").
                       append(day).
                       append(">, Hours=<").
                       append(hour).
                       append(">, Minutes=<").
                       append(min).
                       append(">");
                
                return buffer.toString();
        }
    }        
}