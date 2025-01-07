package com.sap.persistence.monitors.jpa;

import javax.management.openmbean.CompositeData;
/**
 * Composite data interface for the monitored persistence units in JPA runtime.
 * <p>
 * Copyright (c) 2008, SAP-AG
 * 
 * @author d019789
 * @version 7.11
 */
public interface PersistenceUnitData extends CompositeData {
    public static final String KEY_NAME = "Name";
    public static final String KEY_APPLICATION_NAME = "ApplicationName";
    public static final String KEY_DATA_SOURCE_NAME = "DataSourceName";
    public static final String KEY_SUM_PERSISTENCE_CONTEXT_COUNT = "SumPersistenceContextCount";
    public static final String KEY_SUM_PERSISTENCE_CONTEXT_LIFE_TIME = "SumPersistenceContextLifeTime";
    public static final String KEY_SUM_MANAGED_ENTITY_COUNT = "SumManagedEntityCount";
    public static final String KEY_EXTENDED_PERSISTENCE_CONTEXT_COUNT = "ExtendedPersistenceContextCount";
    public static final String KEY_ENTITY_COUNT = "EntityCount";
    public static final String KEY_MAX_QUERIES = "MaxQueries";
    public static final String KEY_DEFAULT_MAX_QUERIES = "DefaultMaxQueries";
    public static final String KEY_QUERY_OVERFLOWS = "QueryOverflows";

    
    public String getName();
    public void setName(String name);
    
    public String getApplicationName();
    public void setApplicationName(String appName);
    
    public String getDataSourceName();
    public void setDataSourceName(String dsName);
    
    public long getSumPersistenceContextCount();

    public void setSumPersistenceContextCount(long sumPcCount);

    public long getSumPersistenceContextLifeTime();

    public void setSumPersistenceContextLifeTime(long sumPcLifetime);

    public long getSumManagedEntityCount();

    public void setSumManagedEntityCount(long sumManagedEntityCount);

    public long getExtendedPersistenceContextCount();

    public void setExtendedPersistenceContextCount(long extPcCount);

    public long getEntityCount();

    public void setEntityCount(long entityCount);
    
    public int getMaxQueries();
    public void setMaxQueries(int maxQueries);
    
    public int getDefaultMaxQueries();
    public void setDefaultMaxQueries(int maxQueries);
    
    public long getQueryOverflows();
    public void setQueryOverflows(long overflows);
}
