package com.sap.persistence.monitors.sql.connection;

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.sap.jmx.modelhelper.ChangeableCompositeData;

/**
 * Copyright (c) 2005, SAP-AG
 * 
 * @author d019789
 * @version 7.10
 */
public class DataSourceImpl extends ChangeableCompositeData implements
        DataSource {

    static final long serialVersionUID = 8952455624770766615L;

    /**
     * The CompositeType for DataSource
     * 
     * @see CompositeType
     */
    public static CompositeType COMPOSITE_TYPE;

    /**
     * The exception that may occur during initialization in the static
     * initializator
     */
    public static OpenDataException exception;

    private static String[] names;
    // initializing the composite type before any call to the constructor
    static {
        try {
            names = new String[] {
                    KEY_DATASOURCENAME, KEY_CLEANUPINTERVAL,
                    KEY_INITCONNECTIONS, KEY_CONNECTIONWAITTIME,
                    KEY_DATABASENAME, KEY_DATABASESERVERNAME, KEY_USERNAME,
                    KEY_VENDORNAME, KEY_CONNECTIONCOUNT, KEY_SQLTYPE,
                    KEY_MAXCONNECTIONS, KEY_CONNECTIONTIMEOUT,
                    KEY_USEDCONNECTIONCOUNT, KEY_IDLECONNECTIONCOUNT, 
                    KEY_WAITINGCONNECTIONREQUESTCOUNT, 
                    KEY_SUMSUCCESSCONNECTIONREQUESTCOUNT,
                    KEY_SUMSUCCESSWAITCONNECTIONREQUESTCOUNT,
                    KEY_SUMTIMEOUTCONNECTIONREQUESTCOUNT,
                    KEY_SUMERRORCONNECTIONREQUESTCOUNT,
                    KEY_USEDCONNECTIONRATE, KEY_VALIDCONNECTIONRATE,
                    KEY_WAITINGCONNECTIONREQUESTRATE,
                    KEY_SUMSUCCESSCONNECTIONREQUESTRATE, 
                    KEY_SUMSUCCESSWAITCONNECTIONREQUESTRATE,
                    KEY_SUMTIMEOUTCONNECTIONREQUESTRATE,
                    KEY_SUMERRORCONNECTIONREQUESTRATE                 
                    };

            String[] descriptions = {
                    "DataSourceName", "CleanUpInterval",
                    "InitConnections", "ConnectionWaitTime",
                    "DatabaseName", "DatabaseServerName", "UserName",
                    "VendorName", "ConnectionCount", "SqlType", 
                    "MaxConnections", "ConnectionTimeout",
                    "UsedConnectionCount", "IdleConnectionCount",
                    "WaitingConnectionRequestCount",                     
                    "SumSuccessConnectionRequestCount",
                    "SumSuccessWaitConnectionRequestCount", 
                    "SumTimeoutConnectionRequestCount",
                    "SumErrorConnectionRequestCount", 
                    "UsedConnectionRate", "ValidConnectionRate",
                    "WaitingConnectionRequestRate",
                    "SumSuccessConnectionRequestRate",
                    "SumSuccessWaitConnectionRequestRate",
                    "SumTimeoutConnectionRequestRate",
                    "SumErrorConnectionRequestRate"
                    };

            String compositeTypeName = "DataSourceCompositeType";
            String compositeTypeDescription = "CompositeType that represents a data source.";

            ArrayType stringArray = new ArrayType(1, SimpleType.STRING);
            OpenType[] types = {
                    SimpleType.STRING, SimpleType.INTEGER,
                    SimpleType.INTEGER, SimpleType.INTEGER,
                    SimpleType.STRING, SimpleType.STRING, SimpleType.STRING,
                    SimpleType.STRING, SimpleType.INTEGER, SimpleType.STRING,
                    SimpleType.INTEGER, SimpleType.INTEGER, 
                    SimpleType.INTEGER, SimpleType.INTEGER,
                    SimpleType.INTEGER,                    
                    SimpleType.LONG,
                    SimpleType.LONG,
                    SimpleType.LONG,
                    SimpleType.LONG, 
                    SimpleType.FLOAT, SimpleType.FLOAT,
                    SimpleType.FLOAT,
                    SimpleType.FLOAT,
                    SimpleType.FLOAT,
                    SimpleType.FLOAT,
                    SimpleType.FLOAT
                    };

            COMPOSITE_TYPE = new CompositeType(compositeTypeName,
                    compositeTypeDescription, names, descriptions, types);
        } catch (OpenDataException e) {
            exception = e; // keep it to be able to traced on upper level
        }
    }

    //------------------------------------------------------
    // Constructor
    //------------------------------------------------------
    public DataSourceImpl() {
        super(COMPOSITE_TYPE);
    }

    //------------------------------------------------------
    // Getters/Setters
    //------------------------------------------------------

    // DataSourceName
    public String getDataSourceName() {
        return (String) get(KEY_DATASOURCENAME);
    }

    public void setDataSourceName(String dataSourceName) {
        set(KEY_DATASOURCENAME, dataSourceName);
    }

    // CleanUpInterval
    public int getCleanUpInterval() {
        return ((Integer) get(KEY_CLEANUPINTERVAL)).intValue();
    }

    public void setCleanUpInterval(int cleanUpInterval) {
        set(KEY_CLEANUPINTERVAL, new Integer(cleanUpInterval));
    }

    // InitConnections
    public int getInitConnections() {
        return ((Integer) get(KEY_INITCONNECTIONS)).intValue();
    }

    public void setInitConnections(int initConnections) {
        set(KEY_INITCONNECTIONS, new Integer(initConnections));
    }

    // ConnectionWaitTime
    public int getConnectionWaitTime() {
        return ((Integer) get(KEY_CONNECTIONWAITTIME)).intValue();
    }

    public void setConnectionWaitTime(int connectionWaitTime) {
        set(KEY_CONNECTIONWAITTIME, new Integer(connectionWaitTime));
    }

    // DatabaseName
    public String getDatabaseName() {
        return (String) get(KEY_DATABASENAME);
    }

    public void setDatabaseName(String databaseName) {
        set(KEY_DATABASENAME, databaseName);
    }

    // DatabaseServerName
    public String getDatabaseServerName() {
        return (String) get(KEY_DATABASESERVERNAME);
    }

    public void setDatabaseServerName(String databaseServerName) {
        set(KEY_DATABASESERVERNAME, databaseServerName);
    }

    // UserName
    public String getUserName() {
        return (String) get(KEY_USERNAME);
    }

    public void setUserName(String userName) {
        set(KEY_USERNAME, userName);
    }

    // VendorName
    public String getVendorName() {
        return (String) get(KEY_VENDORNAME);
    }

    public void setVendorName(String vendorName) {
        set(KEY_VENDORNAME, vendorName);
    }

    // ConnectionCount
    public int getConnectionCount() {
        return ((Integer) get(KEY_CONNECTIONCOUNT)).intValue();
    }

    public void setConnectionCount(int connectionCount) {
        set(KEY_CONNECTIONCOUNT, new Integer(connectionCount));
    }

    // SqlType
    public String getSqlType() {
        return (String) get(KEY_SQLTYPE);
    }

    public void setSqlType(String sqlType) {
        set(KEY_SQLTYPE, sqlType);
    }

    // MaxConnections
    public int getMaxConnections() {
        return ((Integer) get(KEY_MAXCONNECTIONS)).intValue();
    }

    public void setMaxConnections(int maxConnections) {
        set(KEY_MAXCONNECTIONS, new Integer(maxConnections));
    }

    // ConnectionTimeout
    public int getConnectionTimeout() {
        return ((Integer) get(KEY_CONNECTIONTIMEOUT)).intValue();
    }

    public void setConnectionTimeout(int connectionTimeout) {
        set(KEY_CONNECTIONTIMEOUT, new Integer(connectionTimeout));
    }
    
    // UsedConnectionCount
    public int getUsedConnectionCount() {
        return ((Integer) get(KEY_USEDCONNECTIONCOUNT)).intValue();
    }
    public void setUsedConnectionCount(int usedConnectionCount) {
        set(KEY_USEDCONNECTIONCOUNT, new Integer(usedConnectionCount));
    }
    
    // IdleConnectionCount
    public int getIdleConnectionCount() {
        return ((Integer)  get(KEY_IDLECONNECTIONCOUNT)).intValue();
    }
    public void setIdleConnectionCount(int idleConnectionCount) {
        set(KEY_IDLECONNECTIONCOUNT, new Integer(idleConnectionCount));
    }
    
    // WaitingConnectionRequestCount
    public int getWaitingConnectionRequestCount() {
        return ((Integer) get(KEY_WAITINGCONNECTIONREQUESTCOUNT)).intValue();
    }
    public void setWaitingConnectionRequestCount(int waitingConnectionRequestCount) {
        set(KEY_WAITINGCONNECTIONREQUESTCOUNT, new Integer(waitingConnectionRequestCount));
    }
    
    // SumSuccessConnectionRequestCount
    public long getSumSuccessConnectionRequestCount() {
        return ((Long) get(KEY_SUMSUCCESSCONNECTIONREQUESTCOUNT)).longValue();
    }
    public void setSumSuccessConnectionRequestCount(long sumSuccessConnectionRequestCount) {
        set(KEY_SUMSUCCESSCONNECTIONREQUESTCOUNT, new Long(sumSuccessConnectionRequestCount));
    }
    
    // SumSuccessWaitConnectionRequestCount
    public long getSumSuccessWaitConnectionRequestCount() {
        return ((Long) get(KEY_SUMSUCCESSWAITCONNECTIONREQUESTCOUNT)).longValue();
    }
    public void setSumSuccessWaitConnectionRequestCount(long sumSuccessWaitConnectionRequestCount) {
        set(KEY_SUMSUCCESSWAITCONNECTIONREQUESTCOUNT, new Long(sumSuccessWaitConnectionRequestCount));
    }
    
    // SumTimeoutConnectionRequestCount
    public long getSumTimeoutConnectionRequestCount() {
        return ((Long) get(KEY_SUMTIMEOUTCONNECTIONREQUESTCOUNT)).longValue();
    }
    public void setSumTimeoutConnectionRequestCount(long sumTimeoutConnectionRequestCount) {
        set(KEY_SUMTIMEOUTCONNECTIONREQUESTCOUNT, new Long(sumTimeoutConnectionRequestCount));
    }

    // SumErrorConnectionRequestCount
    public long getSumErrorConnectionRequestCount() {
        return ((Long) get(KEY_SUMERRORCONNECTIONREQUESTCOUNT)).longValue();
    }
    public void setSumErrorConnectionRequestCount(long sumErrorConnectionRequestCount) {
        set(KEY_SUMERRORCONNECTIONREQUESTCOUNT, new Long(sumErrorConnectionRequestCount));
    }
    
    // UsedConnectionRate
    public float getUsedConnectionRate() {
        return ((Float) get(KEY_USEDCONNECTIONRATE)).floatValue();
    }
    public void setUsedConnectionRate(float usedConnectionRate) {
        set(KEY_USEDCONNECTIONRATE, new Float(usedConnectionRate));
    }

    // ValidConnectionRate
    public float getValidConnectionRate() {
        return ((Float) get(KEY_VALIDCONNECTIONRATE)).floatValue();
    }
    public void setValidConnectionRate(float validConnectionRate) {
        set(KEY_VALIDCONNECTIONRATE, new Float(validConnectionRate));
    }

    // WaitingConnectionRequestRate
    public float getWaitingConnectionRequestRate() {
        return ((Float) get(KEY_WAITINGCONNECTIONREQUESTRATE)).floatValue();
    }
    public void setWaitingConnectionRequestRate(float waitingConnectionRequestRate) {
        set(KEY_WAITINGCONNECTIONREQUESTRATE, new Float(waitingConnectionRequestRate));
    }
    
    // SumSuccessConnectionRequestRate
    public float getSumSuccessConnectionRequestRate() {
        return ((Float) get(KEY_SUMSUCCESSCONNECTIONREQUESTRATE)).floatValue();
    }
    public void setSumSuccessConnectionRequestRate(float sumSuccessConnectionRequestRate) {
        set(KEY_SUMSUCCESSCONNECTIONREQUESTRATE, new Float(sumSuccessConnectionRequestRate));
    }
    
    // SumSuccessWaitConnectionRequestRate
    public float getSumSuccessWaitConnectionRequestRate() {
        return ((Float) get(KEY_SUMSUCCESSWAITCONNECTIONREQUESTRATE)).floatValue();
    }
    public void setSumSuccessWaitConnectionRequestRate(float sumSuccessWaitConnectionRequestRate) {
        set(KEY_SUMSUCCESSWAITCONNECTIONREQUESTRATE, new Float(sumSuccessWaitConnectionRequestRate));
    }
    
    
    // SumTimeoutConnectionRequestRate
    public float getSumTimeoutConnectionRequestRate() {
        return ((Float) get(KEY_SUMTIMEOUTCONNECTIONREQUESTRATE)).floatValue();
    }
    public void setSumTimeoutConnectionRequestRate(float sumTimeoutConnectionRequestRate) {
        set(KEY_SUMTIMEOUTCONNECTIONREQUESTRATE, new Float(sumTimeoutConnectionRequestRate));
    }

    // SumErrorConnectionRequestRate
    public float getSumErrorConnectionRequestRate() {
        return ((Float) get(KEY_SUMERRORCONNECTIONREQUESTRATE)).floatValue();
    }
    public void setSumErrorConnectionRequestRate(float sumErrorConnectionRequestRate) {
        set(KEY_SUMERRORCONNECTIONREQUESTRATE, new Float(sumErrorConnectionRequestRate));
    }
}