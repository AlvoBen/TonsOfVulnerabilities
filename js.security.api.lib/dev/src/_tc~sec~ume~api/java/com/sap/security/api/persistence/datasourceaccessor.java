package com.sap.security.api.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.sap.security.api.UMException;

public class DataSourceAccessor {

	private static Collection mDataSourceMetaData;
	private static Map        mClassNamesMap;
	private static Map        mDataSourceIDMap;
	
	static
	{
		mClassNamesMap   = new HashMap();
		mDataSourceIDMap = new HashMap();
	}
	
	/**
	 * Returns all available IDataSourceMetaData objects registered by the persistence manager.
	 * @return All available IDataSourceMetaData objects or <code>null</code> if nothing is registered.
	 * @throws UMException If an error occurs.
	 */
	public static Collection getDataSourceMetaData() throws UMException
	{
		return mDataSourceMetaData;
	}
	
	/**
	 * Returns all datasources that match the given implementation class name.
	 * @param className The class name to match.
	 * @return The datasources that match the given class name or <code>null</code>.
	 * @throws UMException If an error occurs.
	 */
	public static Collection getDataSourceMetaDataByClassName(String className) throws UMException
	{
		return (Collection)mClassNamesMap.get(className);
	}
	
	/**
	 * Returns the datasources that matches the given datasource ID.
	 * @param dataSourceID The datasource ID to match.
	 * @return The datasource that matches the given datasource ID or <code>null</code>. 
	 * @throws UMException If an error occurs.
	 */
	public static IDataSourceMetaData getDataSourceMetaDataByDataSourceID(String dataSourceID) throws UMException
	{
		return (IDataSourceMetaData)mDataSourceIDMap.get(dataSourceID);
	}
	
	public static synchronized void setDataSourceMetaData(Collection metadata) throws UMException
	{
		Map newClassNameMap = new HashMap();
		Map newDatasourceIDMap = new HashMap();
		for (Iterator iter=metadata.iterator();iter.hasNext();)
		{
			IDataSourceMetaData md = (IDataSourceMetaData)iter.next();
			String id = md.getDataSourceID();
			newDatasourceIDMap.put(id,md);

			String className = md.getClassName();
			Collection container = (Collection)newClassNameMap.get(className);
			if (container == null)
			{
				container = new HashSet();
				newClassNameMap.put(className,container);
			}
			container.add(md);
		}
		mDataSourceMetaData = metadata;
		mClassNamesMap = newClassNameMap;
		mDataSourceIDMap = newDatasourceIDMap;
	}
	
}
