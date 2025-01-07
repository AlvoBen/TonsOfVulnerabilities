package com.sap.persistence.monitors.sql.connection;

import javax.management.openmbean.CompositeData;

/**
 * Copyright (c) 2005, SAP-AG
 * 
 * @author d019789
 * @version 7.10
 */
public interface DataSource extends CompositeData {
    /**
     * Key in the CompositeData for DataSourceName.
     * 
     * @see CompositeData
     */
    public static final String KEY_DATASOURCENAME = "DataSourceName";

    /**
     * Key in the CompositeData for CleanUpInterval.
     * 
     * @see CompositeData
     */
    public static final String KEY_CLEANUPINTERVAL = "CleanUpInterval";

    /**
     * Key in the CompositeData for InitConnections.
     * 
     * @see CompositeData
     */
    public static final String KEY_INITCONNECTIONS = "InitConnections";

    /**
     * Key in the CompositeData for ConnectionWaitTime.
     * 
     * @see CompositeData
     */
    public static final String KEY_CONNECTIONWAITTIME = "ConnectionWaitTime";

    /**
     * Key in the CompositeData for DatabaseName.
     * 
     * @see CompositeData
     */
    public static final String KEY_DATABASENAME = "DatabaseName";

    /**
     * Key in the CompositeData for DatabaseServerName.
     * 
     * @see CompositeData
     */
    public static final String KEY_DATABASESERVERNAME = "DatabaseServerName";

    /**
     * Key in the CompositeData for UserName.
     * 
     * @see CompositeData
     */
    public static final String KEY_USERNAME = "UserName";

    /**
     * Key in the CompositeData for VendorName.
     * 
     * @see CompositeData
     */
    public static final String KEY_VENDORNAME = "VendorName";

    /**
     * Key in the CompositeData for ConnectionCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_CONNECTIONCOUNT = "ConnectionCount";

    /**
     * Key in the CompositeData for SqlType.
     * 
     * @see CompositeData
     */
    public static final String KEY_SQLTYPE = "SqlType";

    /**
     * Key in the CompositeData for MaxConnections.
     * 
     * @see CompositeData
     */
    public static final String KEY_MAXCONNECTIONS = "MaxConnections";

    /**
     * Key in the CompositeData for ConnectionTimeout.
     * 
     * @see CompositeData
     */
    public static final String KEY_CONNECTIONTIMEOUT = "ConnectionTimeout";
    
    /**
     * Key in the CompositeData for UsedConnectionCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_USEDCONNECTIONCOUNT = "UsedConnectionCount";
    
    /**
     * Key in the CompositeData for IdleConnectionCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_IDLECONNECTIONCOUNT = "IdleConnectionCount";
    
    /**
     * Key in the CompositeData for WaitingConnectionRequestCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_WAITINGCONNECTIONREQUESTCOUNT = "WaitingConnectionRequestCount";
    
    /**
     * Key in the CompositeData for SumSuccessConnectionRequestCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMSUCCESSCONNECTIONREQUESTCOUNT = "SumSuccessConnectionRequestCount";
    
    /**
     * Key in the CompositeData for SumSuccessWaitConnectionRequestCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMSUCCESSWAITCONNECTIONREQUESTCOUNT = "SumSuccessWaitConnectionRequestCount";
    
    /**
     * Key in the CompositeData for SumTimeoutConnectionRequestCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMTIMEOUTCONNECTIONREQUESTCOUNT = "SumTimeoutConnectionRequestCount";
    
    /**
     * Key in the CompositeData for SumErrorConnectionRequestCount.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMERRORCONNECTIONREQUESTCOUNT = "SumErrorConnectionRequestCount";

    /**
     * Key in the CompositeData for UsedConnectionRate.
     * 
     * @see CompositeData
     */
    public static final String KEY_USEDCONNECTIONRATE = "UsedConnectionRate";
    
    /**
     * Key in the CompositeData for ValidConnectionRate.
     * 
     * @see CompositeData
     */
    public static final String KEY_VALIDCONNECTIONRATE = "ValidConnectionRate";

    /**
     * Key in the CompositeData for WaitingConnectionRequestRate.
     * 
     * @see CompositeData
     */
    public static final String KEY_WAITINGCONNECTIONREQUESTRATE = "WaitingConnectionRequestRate";
    
    /**
     * Key in the CompositeData for SumSuccessConnectionRequestRate.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMSUCCESSCONNECTIONREQUESTRATE = "SumSuccessConnectionRequestRate";
    
    /**
     * Key in the CompositeData for SumSuccessWaitConnectionRequestRate.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMSUCCESSWAITCONNECTIONREQUESTRATE = "SumSuccessWaitConnectionRequestRate";
   
    /**
     * Key in the CompositeData for SumTimeoutConnectionRequestRate.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMTIMEOUTCONNECTIONREQUESTRATE = "SumTimeoutConnectionRequestRate";
    
    /**
     * Key in the CompositeData for SumErrorConnectionRequestRate.
     * 
     * @see CompositeData
     */
    public static final String KEY_SUMERRORCONNECTIONREQUESTRATE = "SumErrorConnectionRequestRate";
 

    //------------------------------------------------------
    // Getters and Setters
    //------------------------------------------------------

    // DataSourceName
    public String getDataSourceName();
    public void setDataSourceName(String dataSourceName);

    // CleanUpInterval
    public int getCleanUpInterval();
    public void setCleanUpInterval(int cleanUpInterval);

    // InitConnections
    public int getInitConnections();
    public void setInitConnections(int initConnections);

    // ConnectionWaitTime
    public int getConnectionWaitTime();
    public void setConnectionWaitTime(int connectionWaitTime);

    // DatabaseName
    public String getDatabaseName();
    public void setDatabaseName(String databaseName);

    // DatabaseServerName
    public String getDatabaseServerName();
    public void setDatabaseServerName(String databaseServerName);

    // UserName
    public String getUserName();
    public void setUserName(String userName);

    // VendorName
    public String getVendorName();
    public void setVendorName(String vendorName);

    // ConnectionCount
    public int getConnectionCount();
    public void setConnectionCount(int connectionCount);

    // SqlType
    public String getSqlType();
    public void setSqlType(String sqlType);

    // MaxConnections
    public int getMaxConnections();
    public void setMaxConnections(int maxConnections);

    // ConnectionTimeout
    public int getConnectionTimeout();
    public void setConnectionTimeout(int connectionTimeout);
    
    // UsedConnectionCount
    public int getUsedConnectionCount();     
    public void setUsedConnectionCount(int usedConnectionCount); 
    
    // IdleConnectionCount
    public int getIdleConnectionCount(); 
    public void setIdleConnectionCount(int idleConnectionCount); 
    
    // WaitingConnectionRequestCount
    public int getWaitingConnectionRequestCount();  
    public void setWaitingConnectionRequestCount(int waitingConnectionRequestCount);  
    
    // SumSuccessConnectionRequestCount
    public long getSumSuccessConnectionRequestCount();
    public void setSumSuccessConnectionRequestCount(long sumSuccessConnectionRequestCount);
    
    // SumSuccessWaitConnectionRequestCount
    public long getSumSuccessWaitConnectionRequestCount(); 
    public void setSumSuccessWaitConnectionRequestCount(long sumSuccessWaitConnectionRequestCount); 
    
    // SumTimeoutConnectionRequestCount
    public long getSumTimeoutConnectionRequestCount();
    public void setSumTimeoutConnectionRequestCount(long sumTimeoutConnectionRequestCount);
    
    // SumErrorConnectionRequestCount
    public long getSumErrorConnectionRequestCount();
    public void setSumErrorConnectionRequestCount(long sumErrorConnectionRequestCount);
   
    // UsedConnectionRate
    public float getUsedConnectionRate();
    public void setUsedConnectionRate(float usedConnectionRate);

    // ValidConnectionRate
    public float getValidConnectionRate();
    public void setValidConnectionRate(float validConnectionRate);
    
    // WaitingConnectionRequestRate
    public float getWaitingConnectionRequestRate();  
    public void setWaitingConnectionRequestRate(float waitingConnectionRequestRate);
    
    // SumSuccessConnectionRequestRate
    public float getSumSuccessConnectionRequestRate();
    public void setSumSuccessConnectionRequestRate(float sumSuccessConnectionRequestRate);
    
    // SumSuccessWaitConnectionRequestRate
    public float getSumSuccessWaitConnectionRequestRate();
    public void setSumSuccessWaitConnectionRequestRate(float sumSuccessWaitConnectionRequestRate);
    
    // SumTimeoutConnectionRequestRate
    public float getSumTimeoutConnectionRequestRate();
    public void setSumTimeoutConnectionRequestRate(float sumTimeoutConnectionRequestRate);

    // SumErrorConnectionRequestRate
    public float getSumErrorConnectionRequestRate();
    public void setSumErrorConnectionRequestRate(float sumErrorConnectionRequestRate);
    
}