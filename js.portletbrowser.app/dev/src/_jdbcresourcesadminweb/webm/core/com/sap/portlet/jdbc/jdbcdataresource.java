package com.sap.portlet.jdbc;

import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeProperty;
import javax.naming.InitialContext;

import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeActionStatus;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCCustomDataSourceSettings;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCManagementServiceWrapper;
import com.sap.engine.admin.model.itsam.jsr77.jdbc.SAP_ITSAMJ2eeJDBCSystemDataSourceSettings;

enum JDBCVersion{
	JDBC_1_0(1){
		public String toString(){
			return "JDBC 1.x (no XA support)";
		}
	}
	,JDBC_2_0(2){
		public String toString(){
			return "JDBC 2.x (with XA support)";
		}
	};
	
	private int version;
        public int getVersion(){
            return version;
        }
	JDBCVersion(int v){
		this.version =v;
	}
	
	
}

enum DSType{
	ConnPool(1){
		public String toString(){
			return "Connection Pooled Data Source";
		}
	}
	,XA(2){
		public String toString(){
			return "XA Data Source";
		}
	};
	
	private int version;
        public int getVersion(){
            return version;
        }
	DSType(int v){
		this.version =v;
	}
	
	
}

public class JDBCDataResource {

		private String name = "";
		private boolean selected = false;
		private JDBCDataResourceAlias[] aliases;
		private boolean system_datasource;
		private String description;
		private SAP_ITSAMJ2eeJDBCCustomDataSourceSettings settings;
		private SAP_ITSAMJ2eeJDBCSystemDataSourceSettings system_settings;
		private String driver;
		private int initialConnections = -1;
		private int maxConnections = -1;
		private int maxTimeToWaitConnection = -1;
		private int connectionLifetime;
		private int runCleanupThread;
		private int isolationLevel;
		private String sqlEngine;
                private JDBCVersion jdbc_version;
                private int jdbcVersion;
                private DSType ds_type;
                private int dsType;
		
		
		//JDBC 1.0
                private String driverClass;
		private String database_URL;
		private String user;
		private String password;
                private SAP_ITSAMJ2eeProperty[] properties1x;
		
		//JDBC 2.0
                private String objectFactory;
		private int datasourceType;
		private String cpdsClassName;
		private String xadsClassName;
                private SAP_ITSAMJ2eeProperty[] properties2x;
		
		
		public JDBCDataResource(SAP_ITSAMJ2eeJDBCCustomDataSourceSettings settings,JDBCDataResourceAlias[] aliases){
			this.settings = settings;
			this.name = settings.getDataSourceName();
			this.aliases = aliases;
                        
		}
		
		public JDBCDataResource(SAP_ITSAMJ2eeJDBCSystemDataSourceSettings settings,JDBCDataResourceAlias[] aliases){
			this.system_settings = settings;
			this.name = settings.getDataSourceName();
			this.aliases = aliases;
		}
		
		
		
		public String getName(){
			return name;
		}



		public boolean isSelected() {
			return selected;
		}



		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
		public JDBCDataResourceAlias[] getAliases(){
			return aliases;
		}



		public boolean isSystem_datasource() {
			return system_datasource;
		}



		public void setSystem_datasource(boolean system_datasource) {
			this.system_datasource = system_datasource;
		}



		public String getDescription() {
			if (description != null){
				return description;
			}
			if (system_datasource){
				description = system_settings.getDescription();
			}else{
			    description =  settings.getDescription();
			}
			
			return description;
		}



		public void setDescription(String description) {
			//this.description = description;
		}

		public String getDriver() {
			if (system_datasource){
				driver = system_settings.getDriverName();
			}else{
				driver = settings.getDriverName();
			}
			return driver;
			
		}

		public void setDriver(String driver) {
			//this.driver = driver;
		}

		public Integer getInitialConnections() {
			if (system_datasource){
				initialConnections = system_settings.getInitConnections();				
			}else{
				initialConnections = settings.getInitConnections();
			}
			return initialConnections;
		}

		public void setInitialConnections(Integer initalConnections) {
			//this.initalConnections = initalConnections;
		}

		public int getMaxConnections() {
			if (system_datasource){
				maxConnections = system_settings.getMaxConnections();				
			}else{
				maxConnections = settings.getMaxConnections();
			}
			return maxConnections;
		}

		public void setMaxConnections(int maxConnections) {
			//this.maxConnections = maxConnections;
		}

		public int getMaxTimeToWaitConnection() {
			if (system_datasource){
				maxTimeToWaitConnection = system_settings.getMaxTimeToWaitConnection();				
			}else{
				maxTimeToWaitConnection = settings.getMaxTimeToWaitConnection();
			}
			return maxTimeToWaitConnection;
		}

		public void setMaxTimeToWaitConnection(int maxTimeToWaitConnection) {
			//this.maxTimeToWaitConnection = maxTimeToWaitConnection;
			
		}

		public int getConnectionLifetime() {
			if (system_datasource){
				connectionLifetime = system_settings.getConnectionLifetime();			
			}else{
				connectionLifetime = settings.getConnectionLifetime();
			}
			return connectionLifetime;
		}

		public void setConnectionLifetime(int connectionLifetime) {
			//this.connectionLifetime = connectionLifetime;
		}

		
		public int getIsolationLevel() {
			if (system_datasource){
				isolationLevel = system_settings.getIsolationLevel();			
			}else{
				isolationLevel = settings.getIsolationLevel();
			}
			return isolationLevel;
		}

		public void setIsolationLevel(int isolationLevel) {
			//this.isolationLevel = isolationLevel;
		}

		public int getRunCleanupThread() {
			if (system_datasource){
				runCleanupThread = system_settings.getRunCleanupThread();			
			}else{
				runCleanupThread = settings.getRunCleanupThread();
			}
			
			return runCleanupThread;
		}

		public void setRunCleanupThread(int runCleanupThread) {
			//this.runCleanupThread = runCleanupThread;
		}

		public String getSqlEngine() {
			if (system_datasource){
				sqlEngine = system_settings.getSQLEngine();			
			}else{
				sqlEngine = settings.getSQLEngine();
			}
			return sqlEngine;
		}

		public void setSqlEngine(String sqlEngine) {
			//this.sqlEngine = sqlEngine;
		}

		public int getJdbcVersion() {
                        if (system_datasource){
                            return JDBCVersion.JDBC_1_0.getVersion();
                        }
                        
                        if (jdbc_version == null){
                            if (getDriverClass() != null){
                                jdbc_version = JDBCVersion.JDBC_1_0;
                            }else{
                                jdbc_version = JDBCVersion.JDBC_2_0;
                            }
                        }
                        jdbcVersion = jdbc_version.getVersion();
                        return jdbcVersion;
		}
                
                
                public void setJdbcVersion(int jdbcv){
                    if (system_datasource){
                        return;
                    }
                    if (jdbcv == JDBCVersion.JDBC_1_0.getVersion()){
                        jdbc_version = JDBCVersion.JDBC_1_0;
                    }
                    if (jdbcv == JDBCVersion.JDBC_2_0.getVersion()){
                        jdbc_version = JDBCVersion.JDBC_2_0;
                    }
                    
                    jdbcVersion = jdbc_version.getVersion();
                }
                
                public String getDriverClass(){
                    if (!system_datasource){
                        driverClass = settings.getDriverClassName();
                        return driverClass;
                    }
                    return null;
                }
		
		
		

    public String getDatabase_URL() {
        if (system_datasource){
            return "";
        }
        
        database_URL = settings.getURL();
        return database_URL;
    }

    public void setDatabase_URL(String database_URL) {
        //this.database_URL = database_URL;
    }
    
    

    public String getUser() {
        if (system_datasource){
            return "";
        }
        user = settings.getUsername();
        return user;
    }

    public void setUser(String user) {
       // this.user = user;
    }
    


    public String getPassword() {
        if (system_datasource){
            return "";
        }
        password = settings.getPassword();
        return password;
    }

    public void setPassword(String password) {
        //this.password = password;
    }

    public String getObjectFactory() {
        if (system_datasource){
            return "";
        }
        objectFactory = settings.getObjectFactory();
        return objectFactory;
    }

    public void setObjectFactory(String objectFactory) {
       // this.objectFactory = objectFactory;
    }

    public int getDsType() {
        if (system_datasource){
            return -1;
        }
        if (ds_type == null){
            if (getCpdsClassName()!=null && getXadsClassName()==null){
                ds_type = DSType.ConnPool;
            }else if(getXadsClassName() != null && getCpdsClassName() == null){
                ds_type = DSType.XA;
            }else{
                //default
                ds_type = DSType.ConnPool;
            }
        }     
        
        dsType = ds_type.getVersion();
        return dsType;
    }
    
    public void setDsType(int dst){
        if (dst == DSType.ConnPool.getVersion()){
             ds_type = DSType.ConnPool;
        }
        if (dst == DSType.XA.getVersion()){
            ds_type = DSType.XA;
        }
        dsType = ds_type.getVersion();
    }

    public String getCpdsClassName() {
        if (system_datasource){
            return "";
        }
        cpdsClassName = settings.getCPDSClassName();
        return cpdsClassName;
    }
    
    

    public void setCpdsClassName(String cpdsClassName) {
       // this.cpdsClassName = cpdsClassName;
    }

    public String getXadsClassName() {
        if (system_datasource){
            return "";
        }
        xadsClassName = settings.getXADSClassName();
        return xadsClassName;
    }


    

    public SAP_ITSAMJ2eeProperty[] getProperties1x() {
        if (system_datasource){
            return null;
        }
        if (getJdbcVersion() == 1){
            properties1x = settings.getProperties1x();
        }
        return properties1x;
    }
    
    
    public SAP_ITSAMJ2eeProperty[] getProperties2x() {
        if (system_datasource){
            return null;
        }
        if (getJdbcVersion() == 2){
            properties2x = settings.getProperties20();
        }
        return properties2x;
    }

    public void setAliases(JDBCDataResourceAlias[] aliases) {
        this.aliases = aliases;
    }

    
   
    
}
    

