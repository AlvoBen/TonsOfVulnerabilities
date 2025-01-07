/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.runtime;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.scheduler.runtime.JobExecutionRuntime;
import com.sap.engine.services.scheduler.runtime.cluster.ClusterCommunication;
import com.sap.engine.services.scheduler.runtime.event.EventManager;
import com.sap.engine.services.scheduler.runtime.executor.JobExecutorImpl;
import com.sap.engine.services.scheduler.runtime.jxbp.SchedulerManager;
import com.sap.engine.services.scheduler.runtime.logging.JobLoggingManager;
import com.sap.engine.services.scheduleradapter.SchedulerAdapterResourceAccessor;
import com.sap.engine.services.scheduleradapter.jobdeploy.ConfigurationParser;
import com.sap.engine.services.scheduleradapter.repository.JobRepository;
import com.sap.engine.services.scheduler.runtime.db.DBHandler;
import com.sap.engine.services.scheduler.runtime.db.EventPersistor;
import com.sap.engine.services.scheduler.runtime.db.JobDefinitionHandler;
import com.sap.engine.services.scheduler.runtime.db.JobQueryHandler;
import com.sap.engine.services.scheduler.runtime.db.LogHandler;
import com.sap.engine.services.scheduler.runtime.db.SchedulerCache;
import com.sap.engine.services.scheduler.runtime.db.SchedulerManagementHandler;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.scheduler.api.Scheduler;
import com.sap.scheduler.runtime.JobExecutor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Environment holding all important information for the job scheduler
 * adapter.
 * 
 * @author Dirk Marwinski 
 */
public class Environment {
    public static final String LINE_WRAP = System.getProperty("line.separator");
    /**
     * Initialization of the location for SAP logging.
     */
    private final static Location location = Location.getLocation(Environment.class);

    /**
     * Initialization of the category for SAP logging.
     */
    private final static Category category = LoggingHelper.SYS_SERVER;

    public static final String DEFAULT_APPLICATION_ROOT_PATH = "apps";

    public static final String SCHEDULER_CONTEXT_JNDI_NAME = "java_scheduler";
    public static final String SCHEDULER_BINDING_JNDI_NAME = "scheduler";
    public static final String SCHEDULER_ADMINISTRATOR_BINDING_JNDI_NAME = "SchedulerAdministrator";
    public static final String SCHEDULER_CONTROL_JNDI_NAME = "SchedulerControl";
    public static final String SCHEDULER_BINDING_JXBP_NAME = "jxbp";
    public static final String SCHEDULER_BINDING_JOB_EXECUTION_RUNTIME = "jert";
    public static final String SCHEDULER_BINDING_JAVA_SCHEDULER_NAME = "JavaScheduler";
    
    public static final String JOB_LOGGING_ROOT_PATH = "log/jobs";
    
    private JobLoggingManager          mJobLoggingManager;
    private ClusterCommunication       mClusterCommunication;
    private ConfigurationParser        mConfigurationParser;
    private ApplicationServiceContext  mServiceContext;
    private SchedulerManagementHandler mSchedulerManagementHandler;
    private JobDefinitionHandler       mJobDefinitionHandler;
    private SchedulerManager           mSchedulerManager;
    private JobQueryHandler            mJobQueryHandler;
    private LogHandler                 mLogHandler;
    private EventPersistor             mEventPersistor;
    
    private int mClusterId;
    private String mApplicationConfigurationRootPath;
    private String mNodeName;
    private String mClusterName;
    
    private Properties[] mServiceProviderProperties;    
    private JobRepository mJobRepository;
    private DBHandler    mDBHandler;
    private EventManager mEventManager;    
    private JobExecutorImpl mJobExecutorImpl;    
    private JobExecutionRuntime mJobExecutionRuntime;
    
    // cache
    private SchedulerCache m_cache = null;
    
    // data source name
    //
    public static final String DATASOURCE_LOOKUP_NAME = "jdbc/notx/SAP/BC_SCHEDULER";
    public static final String DATASOURCE_LOOKUP_NAME_TX = "jdbc/SAP/BC_SCHEDULER";
    private DataSource mDataSource;
    private DataSource mDataSourceTx;
    
    /**
     * This is a (possibly) remote reference to the SAPJ2EEScheduler.
     * It will be updated automatically on failover.
     */
    private Scheduler          mSAPJ2EEScheduler;
    
    private boolean m_isSafeModeEnabled = false;
    
    /**
     * Constructor which is called while start of scheduler-runtime. The lookup 
     * for the DataSource is also handled here.
     * 
     * @throws ServiceException if the lookup of the DataSource fails
     */
    public Environment() throws ServiceException {
        mJobExecutorImpl = new JobExecutorImpl(this);
        lookupDataSource();
    }
    

    /**
     * Lookups the DataSource
     * 
     * @throws ServiceException if the lookup for the DataSource fails
     */
    private void lookupDataSource() throws ServiceException {
        try {
            Context jCtx = new InitialContext();
            mDataSource = (DataSource)jCtx.lookup(DATASOURCE_LOOKUP_NAME);
            mDataSourceTx = (DataSource)jCtx.lookup(DATASOURCE_LOOKUP_NAME_TX);
            
        } catch (NamingException ne) {
            throw new ServiceException(location, new LocalizableTextFormatter(SchedulerAdapterResourceAccessor.getResourceAccessor(),
                                                                    SchedulerAdapterResourceAccessor.DATASOURCE_LOG_NOT_INIT, 
                                                                    new Object[] {DATASOURCE_LOOKUP_NAME}), ne);
        }
    }
    
    
    /**
     * Returns the DataSource
     * 
     * @return DataSource the DataSource
     */
    public DataSource getDataSource() {
        return mDataSource;
    }
    
    public DataSource getDataSourceTx() {
        return mDataSourceTx;
    }
    
    public int getClusterId() {
        return mClusterId;
    }
    
    public void setClusterId(int id) {
        mClusterId = id;
    }
    
    public String getNodeName() {
    	return mNodeName;
    }
    
    public void setNodeName(String name) {
    	mNodeName = name;
    }
    
    public String getClusterName() {
    	return mClusterName;
    }
    
    public void setClusterName(String name) {
    	mClusterName = name;
    }
    
    public JobLoggingManager getJobLoggingManager() {
        return mJobLoggingManager;
    }

    public void setJobLoggingManager	(JobLoggingManager mgr) {
        mJobLoggingManager = mgr;
    }
    
    public ClusterCommunication getClusterCommuniction() {
        return mClusterCommunication;
    }
    
    public void setClusterCommunication(ClusterCommunication comm) {
        mClusterCommunication = comm;
    }

    public ConfigurationParser getConfigurationParser() {
        return mConfigurationParser;
    }
    
    public void setConfigurationParser(ConfigurationParser configurationParser) {
        mConfigurationParser = configurationParser;
    }

    public ApplicationServiceContext getServiceContext() {
        return mServiceContext;
    }

    public void setServiceContext(ApplicationServiceContext serviceContext) {
        mServiceContext = serviceContext;
    }

    // from configuration
    //
    public void setApplicationConfigurationRootPath(String path) {
        mApplicationConfigurationRootPath = path;
    }

    public String getApplicationConfigurationRootPath() {
        return mApplicationConfigurationRootPath;
    }

    public Properties[] getSchedulerProviderProperties() {
        return mServiceProviderProperties;
    }
    public void setSchedulerProviderProperties(
            Properties[] serviceProviderProperties) {
        mServiceProviderProperties = serviceProviderProperties;
    }
	/**
	 * @return Returns the mJobRepository.
	 */
	public JobRepository getJobRepository() {
		return mJobRepository;
	}
	/**
	 * @param jobRepository The mJobRepository to set.
	 */
	public void setJobRepository(JobRepository jobRepository) {
		mJobRepository = jobRepository;
	}
	
	public DBHandler getDBHandler() {
		return mDBHandler;
	}
    
    public void setLogHandler(LogHandler hdlr) {
        mLogHandler = hdlr;
    }
    
    public LogHandler getLogHandler() {
        return mLogHandler;
    }
	
	public void setDBHandler(DBHandler db) {
		mDBHandler = db;
	}
    
    public void setSchedulerManagementHandler(SchedulerManagementHandler smh) {
        mSchedulerManagementHandler = smh;
    }

    public SchedulerManagementHandler getSchedulerManagementHandler() {
        return mSchedulerManagementHandler;
    }
    
    public void setJobDefinitionHandler(JobDefinitionHandler jdh) {
        mJobDefinitionHandler = jdh;
    }

    public JobDefinitionHandler getJobDefinitionHandler() {
        return mJobDefinitionHandler;
    }

    public void setSchedulerManager(SchedulerManager smh) {
        mSchedulerManager = smh;
    }

    public SchedulerManager getSchedulerManager() {
        return mSchedulerManager;
    }

    /**
	 * @return Returns the mEventManager.
	 */
	public EventManager getEventManager() {
		return mEventManager;
	}
	/**
	 * @param eventManager The mEventManager to set.
	 */
	public void setEventManager(EventManager eventManager) {
		mEventManager = eventManager;
	}
    
    public JobExecutor getJobExecutor() {
        return mJobExecutorImpl;
    }
    
    public void addJobExecutor(JobExecutor exe, int type) {
        mJobExecutorImpl.addJobExecutor(exe, type);
    }
    
    public synchronized void setSAPJ2EEScheduler(Scheduler sched) {
        mSAPJ2EEScheduler = sched;
    }
    
    public synchronized Scheduler getSAPJ2EEScheduler() {

        if (mSAPJ2EEScheduler != null) {
            return mSAPJ2EEScheduler;
        }

        // Job Scheduler is on a remote system
        //
        // TODO cache scheduler data and only update on failover
        //

        try {
            Context rootCtx = new InitialContext();            
            mSAPJ2EEScheduler = (Scheduler)rootCtx.lookup(SCHEDULER_BINDING_JNDI_NAME);
        } catch (NamingException ne) {   
            category.logThrowableT(Severity.ERROR, location, "Unable to lookup Java Scheduler \"" + SCHEDULER_BINDING_JNDI_NAME + "\".",ne);
        }
        return mSAPJ2EEScheduler;

    }

    
    public void setJobExecutionRuntime(JobExecutionRuntime rt) {
        mJobExecutionRuntime = rt;
    }

    public JobExecutionRuntime getJobExecutionRuntime() {
        return mJobExecutionRuntime;
    }


    public JobQueryHandler getJobQueryHandler() {
        return mJobQueryHandler;
    }


    public void setJobQueryHandler(JobQueryHandler jobQueryHandler) {
        mJobQueryHandler = jobQueryHandler;
    }
    
    
    public void setSafeMode(boolean isSafeModeEnabled) {
        m_isSafeModeEnabled = isSafeModeEnabled;
    }
    
    public boolean isSafeModeEnabled() {
        return m_isSafeModeEnabled;
    }

    public void setEventPersistor(EventPersistor ep) {
        mEventPersistor = ep;
    }
    public EventPersistor getEventPersistor() {
        return mEventPersistor;
    }    
    
    public void setSchedulerCache(SchedulerCache cache) {
        m_cache = cache;
    }
    public SchedulerCache getSchedulerCache() {
        return m_cache;
    }

}
