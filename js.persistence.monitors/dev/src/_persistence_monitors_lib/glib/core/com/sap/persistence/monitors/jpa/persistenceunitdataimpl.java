package com.sap.persistence.monitors.jpa;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.sap.jmx.modelhelper.ChangeableCompositeData;

public class PersistenceUnitDataImpl extends ChangeableCompositeData implements
		PersistenceUnitData {
	// TODO
	private static final long serialVersionUID = 1L;

	public final static CompositeType COMPOSITE_TYPE;

	private static String[] names;

	static {
		try {
			names = new String[] { KEY_NAME, KEY_APPLICATION_NAME,
					KEY_DATA_SOURCE_NAME, KEY_ENTITY_COUNT,
					KEY_EXTENDED_PERSISTENCE_CONTEXT_COUNT,
					KEY_SUM_MANAGED_ENTITY_COUNT,
					KEY_SUM_PERSISTENCE_CONTEXT_COUNT,
					KEY_SUM_PERSISTENCE_CONTEXT_LIFE_TIME, KEY_MAX_QUERIES,
					KEY_DEFAULT_MAX_QUERIES,
					KEY_QUERY_OVERFLOWS };

			String[] descriptions = { "Name", "ApplicationName",
					"DataSourceName", "EntityCount",
					"ExtendedPersistenceContextCount", "SumManagedEntityCount",
					"SumPersistenceContextCount",
					"SumPersistenceContextLifeTime", "MaxQueries",
					"DefaultMaxQueries",
					"QueryOverflows" };

			String compositeTypeName = "PersistenceUnitDataCompositeType";
			String compositeTypeDescription = "CompositeType that represents a persistence unit.";

			OpenType[] types = { SimpleType.STRING, SimpleType.STRING,
					SimpleType.STRING, SimpleType.LONG, SimpleType.LONG,
					SimpleType.LONG, SimpleType.LONG, SimpleType.LONG,
					SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.LONG };

			COMPOSITE_TYPE = new CompositeType(compositeTypeName,
					compositeTypeDescription, names, descriptions, types);
		} catch (OpenDataException e) {
			throw new RuntimeException(e);
		}
	}

	public PersistenceUnitDataImpl() {
		super(COMPOSITE_TYPE);
	}

	public PersistenceUnitDataImpl(String name, String appName,
			String dsName, long entityCount, long extendedPcCount,
			long sumManagedEntityCount, long sumPcCount, long sumPCLifeTime,
			int maxQueries, int defaultMaxQueries, long queryOverflows) {
		super(COMPOSITE_TYPE);
		setName(name);
		setApplicationName(appName);
		setDataSourceName(dsName);
		setEntityCount(entityCount);
		setExtendedPersistenceContextCount(extendedPcCount);
		setSumManagedEntityCount(sumManagedEntityCount);
		setSumPersistenceContextCount(sumPcCount);
		setSumPersistenceContextLifeTime(sumPCLifeTime);
		setMaxQueries(maxQueries);
		setDefaultMaxQueries(defaultMaxQueries);
		setQueryOverflows(queryOverflows);
	}

	public String getName() {
		return (String) get(KEY_NAME);
	}

	public void setName(String name) {
		set(KEY_NAME, name);
	}

	public String getApplicationName() {
		return (String) get(KEY_APPLICATION_NAME);
	}

	public void setApplicationName(String appName) {
		set(KEY_APPLICATION_NAME, appName);
	}

	public String getDataSourceName() {
		return (String) get(KEY_DATA_SOURCE_NAME);
	}

	public void setDataSourceName(String dsName) {
		set(KEY_DATA_SOURCE_NAME, dsName);
	}

	public long getEntityCount() {
		return ((Long) get(KEY_ENTITY_COUNT)).longValue();
	}

	public void setEntityCount(long entityCount) {
		set(KEY_ENTITY_COUNT, new Long(entityCount));
	}

	public long getExtendedPersistenceContextCount() {
		return ((Long) get(KEY_EXTENDED_PERSISTENCE_CONTEXT_COUNT)).longValue();
	}

	public void setExtendedPersistenceContextCount(long extPcCount) {
		set(KEY_EXTENDED_PERSISTENCE_CONTEXT_COUNT, new Long(extPcCount));
	}

	public long getSumManagedEntityCount() {
		return ((Long) get(KEY_SUM_MANAGED_ENTITY_COUNT)).longValue();
	}

	public void setSumManagedEntityCount(long sumManagedEntityCount) {
		set(KEY_SUM_MANAGED_ENTITY_COUNT, new Long(sumManagedEntityCount));
	}

	public long getSumPersistenceContextCount() {
		return ((Long) get(KEY_SUM_PERSISTENCE_CONTEXT_COUNT)).longValue();
	}

	public void setSumPersistenceContextCount(long sumPcCount) {
		set(KEY_SUM_PERSISTENCE_CONTEXT_COUNT, new Long(sumPcCount));
	}

	public long getSumPersistenceContextLifeTime() {
		return ((Long) get(KEY_SUM_PERSISTENCE_CONTEXT_LIFE_TIME)).longValue();
	}

	public void setSumPersistenceContextLifeTime(long sumPcLifetime) {
		set(KEY_SUM_PERSISTENCE_CONTEXT_LIFE_TIME, new Long(sumPcLifetime));
	}

	public int getMaxQueries() {
		return ((Integer) get(KEY_MAX_QUERIES)).intValue();
	}

	public void setMaxQueries(int maxQueries) {
		set(KEY_MAX_QUERIES, new Integer(maxQueries));
	}
	
	public int getDefaultMaxQueries() {
		return ((Integer) get(KEY_DEFAULT_MAX_QUERIES)).intValue();
	}

	public void setDefaultMaxQueries(int maxQueries) {
		set(KEY_DEFAULT_MAX_QUERIES, new Integer(maxQueries));
	}

	public long getQueryOverflows() {
		return ((Long) get(KEY_QUERY_OVERFLOWS)).longValue();
	}

	public void setQueryOverflows(long queryOverflows) {
		set(KEY_QUERY_OVERFLOWS, new Long(queryOverflows));
	}

}
