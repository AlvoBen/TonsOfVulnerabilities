package com.sap.portlet.jdbc;


import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeActionStatus;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCManagementServiceWrapper;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCCustomDataSourceSettings;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCCustomDataSourceWrapper;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCDataSourceAliasSettings;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCDataSourceAliasWrapper;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCSystemDataSourceSettings;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCSystemDataSourceWrapper;

import static com.sap.portlet.jdbc.LogContext.log;



public class JMXBeansAdapter {
	
	private static String JDBC_RESOURCES_QUERY = "*:j2eeType=JDBCDataSource,*";	
	private static String JDBC_RESOURCE_ALIAS_QUERY = "*:j2eeType=JDBCDataSourceAlias,*";	
	private static String JDBC_RESOURCE_MANAGER = "*:j2eeType=JDBCResource,*";
	private static String SYSTEM_DATA_SOURCE = "*:cimclass=SAP_ITSAMJ2eeJDBCSystemDataSource,*";
        private static final String RESOURCE_MANAGER_NAME   = "JDBCResourceManager";
	
	private MBeanServerConnection mbs;
	private Set<ObjectName> _system;
	
	public JMXBeansAdapter(){
		init();
		_system = querySystemDataSource();
	}
	
	
	private void init() {
		try {
		      InitialContext ctx = new InitialContext();
		      mbs = (MBeanServerConnection) ctx.lookup("jmx");
		    } catch (NamingException ex) {
		      log.errorT(LogContext.getExceptionStackTrace(ex));
		    }
		
	}
	
	
	
	private Set<ObjectName> queryAllJDBCResources(){
		return queryResources(JDBC_RESOURCES_QUERY);
	}
	
	private Set<ObjectName> queryAllJDBCResourceAliases(){
		return queryResources(JDBC_RESOURCE_ALIAS_QUERY);
    }
	private Set<ObjectName> querySystemDataSource(){
		return queryResources(SYSTEM_DATA_SOURCE);
    }
	
	
        private ObjectName getResourceManager(){
            Set<ObjectName> _res = queryResources(JDBC_RESOURCE_MANAGER);
            if (_res == null){
                return null;
            }
            for (ObjectName on:_res){
                if (on.getKeyProperty("name").equals(RESOURCE_MANAGER_NAME)){
                    return on;
                }
            }
            return null;
        }
	private Set<ObjectName> queryResources(String query){
		if (mbs == null){
			return null;
		}
		ObjectName pattern;
		try {
			pattern = new ObjectName(query);
			Set result = mbs.queryNames(pattern, null); 
			return result;
		} catch (MalformedObjectNameException e) {
			log.errorT(LogContext.getExceptionStackTrace(e));
		} catch (NullPointerException e) {
			log.errorT(LogContext.getExceptionStackTrace(e));
		} catch (IOException e) {
			log.errorT(LogContext.getExceptionStackTrace(e));
		}
	    
		return null;
	}
	
	
	public JDBCDataResource[] getAllJDBCResources(){
		List<JDBCDataResource> _l = new ArrayList<JDBCDataResource>();
		Set<ObjectName> _set = queryAllJDBCResources();
		
		
		//mark system datasource so it is not editable
		
		
		
		
		for(ObjectName on : _set){
			try {
				String name = (String)mbs.getAttribute(on, "Name");
				log.debugT("DataSource name :"+name);
				JDBCDataResourceAlias[] aliases = getJDBCResourceAliases(name);
				CompositeData _cd = getDSCompositeData(on);
				JDBCDataResource ds = null;
				if (checkSystemDS(name)){
					SAP_ITSAMJ2eeJDBCSystemDataSourceSettings settings = SAP_ITSAMJ2eeJDBCSystemDataSourceWrapper.getSAP_ITSAMJ2eeJDBCSystemDataSourceSettingsForCData(_cd);
					ds = new JDBCDataResource(settings,aliases);
					ds.setSystem_datasource(true);
				}else{	
					SAP_ITSAMJ2eeJDBCCustomDataSourceSettings settings = SAP_ITSAMJ2eeJDBCCustomDataSourceWrapper.getSAP_ITSAMJ2eeJDBCCustomDataSourceSettingsForCData(_cd);
					ds = new JDBCDataResource(settings,aliases);
				}
				
				_l.add(ds);
			} catch (AttributeNotFoundException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (InstanceNotFoundException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (MBeanException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (ReflectionException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (IOException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			}
		}
		if (_l.size()>0){
			return (JDBCDataResource[])_l.toArray(new JDBCDataResource[_l.size()]);
		}
		return null;
	}
	
	
	private boolean checkSystemDS(String name) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
		for (ObjectName sys: _system){
			String sys_name = (String)mbs.getAttribute(sys, "Name");
			if (sys_name.equals(name)){
				return true;
			}
		}
		return false;
	}


	private JDBCDataResourceAlias[] getJDBCResourceAliases(String name) {
		JDBCDataResourceAlias[] _all = getAllJDBCResourceAliases();
		List<JDBCDataResourceAlias> _l = new ArrayList<JDBCDataResourceAlias>();
		for (JDBCDataResourceAlias alias : _all){
			 if (alias.getDataSourceName().equals(name)){
				 _l.add(alias);
			 }
		}
		if (_l.size() > 0){
			return (JDBCDataResourceAlias[])_l.toArray(new JDBCDataResourceAlias[_l.size()]);
		}
		return null;
	}


	public JDBCDataResourceAlias[] getAllJDBCResourceAliases(){
		List<JDBCDataResourceAlias> _l = new ArrayList<JDBCDataResourceAlias>();
		Set<ObjectName> _set = queryAllJDBCResourceAliases();
		for(ObjectName on : _set){
			try {
				CompositeData _cd = getDSCompositeData(on);
				_l.add(new JDBCDataResourceAlias(_cd));
			} catch (AttributeNotFoundException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (InstanceNotFoundException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (MBeanException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (ReflectionException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			} catch (IOException e) {
				log.errorT(LogContext.getExceptionStackTrace(e));
			}
		}
		if (_l.size()>0){
			return (JDBCDataResourceAlias[])_l.toArray(new JDBCDataResourceAlias[_l.size()]);
		}
		return null;
	}
	
	
	public CompositeData getDSCompositeData(ObjectName on) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException{
	    return (CompositeData)mbs.getAttribute(on, "Settings");
	  }

	  public CompositeData applyChanges(ObjectName on,CompositeData cd) throws Exception {
	    return (CompositeData) mbs.invoke(on, "ApplyChanges", new Object[]{cd}, new String[]{CompositeData.class.getName()});
	  }
	
	public void populateDS(ObjectName on){
		try {
			SAP_ITSAMJ2eeJDBCCustomDataSourceSettings settings = SAP_ITSAMJ2eeJDBCCustomDataSourceWrapper.getSAP_ITSAMJ2eeJDBCCustomDataSourceSettingsForCData(getDSCompositeData(on));
		
		} catch (Exception e) {
			log.errorT(LogContext.getExceptionStackTrace(e));
		}
	}
	
	public void addAlias(String ds,String alias){
            ObjectName resource =getResourceManager();
            
            CompositeData cd = null;
            try {
                cd = (CompositeData) mbs.invoke(resource, "AddAlias", new Object[] {alias, ds}, new String[] {String.class.getName(), String.class.getName()});
            } catch (InstanceNotFoundException ex) {
                log.errorT(LogContext.getExceptionStackTrace(ex));
            } catch (ReflectionException ex) {
                log.errorT(LogContext.getExceptionStackTrace(ex));
            } catch (MBeanException ex) {
               log.errorT(LogContext.getExceptionStackTrace(ex));
            } catch (IOException ex) {
                log.errorT(LogContext.getExceptionStackTrace(ex));
            }
            
               SAP_ITSAMJ2eeActionStatus status = SAP_ITSAMJ2eeJDBCManagementServiceWrapper.getSAP_ITSAMJ2eeActionStatusForCData(cd);
               if (SAP_ITSAMJ2eeActionStatus.ERROR_CODE.equals(status.getCode())){
                   log.errorT("Failed to create datasource alias: "+status.getStackTrace());
               }
               log.infoT("Result of adding alias: " + status.getMessageId());
          
        }
        
        public void removeAlias(CompositeData alias){
            ObjectName resource = getResourceManager();
            CompositeData cd = null;
                try {
                  cd = (CompositeData) mbs.invoke(resource, "RemoveAlias", new Object[]{alias}, new String[]{CompositeData.class.getName()});
                
                } catch (InstanceNotFoundException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (ReflectionException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (MBeanException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (IOException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                }
            
               SAP_ITSAMJ2eeActionStatus status = SAP_ITSAMJ2eeJDBCManagementServiceWrapper.getSAP_ITSAMJ2eeActionStatusForCData(cd);
               if (SAP_ITSAMJ2eeActionStatus.ERROR_CODE.equals(status.getCode())){
                   log.errorT("Failed to remove datasource alias: "+status.getStackTrace());
               }
               log.infoT("Result of removing alias: " + status.getMessageId());
          
        }
        
        


     public void redirectAlias(JDBCDataResourceAlias alias, String redirectToDataSource) {
        ObjectName resource = getResourceManager();
            CompositeData cd = null;
                try {
                  cd = (CompositeData) mbs.invoke(resource, "RedirectAlias", new Object[]{alias.getCompositeData(), redirectToDataSource}, new String[]{CompositeData.class.getName(), String.class.getName()});
                
                } catch (InstanceNotFoundException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (ReflectionException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (MBeanException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (IOException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                }
            
               SAP_ITSAMJ2eeActionStatus status = SAP_ITSAMJ2eeJDBCManagementServiceWrapper.getSAP_ITSAMJ2eeActionStatusForCData(cd);
               if (SAP_ITSAMJ2eeActionStatus.ERROR_CODE.equals(status.getCode())){
                   log.errorT("Failed to redirect datasource alias: "+alias.getName()+" to datasource: "+redirectToDataSource);
               }
               log.infoT("Result of redirect alias: " + status.getMessageId());
        
    }

    void removeDataSource(String datasource) {
        if (datasource == null || datasource.length() < 1){
            return;
        }
        ObjectName resource = getResourceManager();
            CompositeData cd = null;
                try {
                  cd = (CompositeData) mbs.invoke(resource, "RemoveDataSource", new Object[]{datasource}, new String[]{String.class.getName()});
                
                } catch (InstanceNotFoundException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (ReflectionException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (MBeanException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                } catch (IOException ex) {
                    log.errorT(LogContext.getExceptionStackTrace(ex));
                }
            
               SAP_ITSAMJ2eeActionStatus status = SAP_ITSAMJ2eeJDBCManagementServiceWrapper.getSAP_ITSAMJ2eeActionStatusForCData(cd);
               if (SAP_ITSAMJ2eeActionStatus.ERROR_CODE.equals(status.getCode())){
                   log.errorT("Failed to remove datasource: "+datasource);
               }
               log.infoT("Result of remove datasource: " + status.getMessageId());
        
    }


}
