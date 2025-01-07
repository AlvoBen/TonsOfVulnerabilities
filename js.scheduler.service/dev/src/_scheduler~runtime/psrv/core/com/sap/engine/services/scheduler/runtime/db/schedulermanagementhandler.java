/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.scheduler.runtime.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduleradapter.SchedulerAdapterResourceAccessor;
import com.sap.guid.GUID;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.SchedulerAlreadyDefinedException;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SubscriberID;
import com.sap.scheduler.runtime.SchedulerDefinition.SchedulerStatus;
import com.sap.sql.DuplicateKeyException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.transaction.TransactionTicket;
import com.sap.transaction.TxException;
import com.sap.transaction.TxManager;
import com.sap.transaction.TxRollbackException;

/**
 * This class implements the database access methods for the management
 * of external schedulers.
 * 
 * @author Dirk Marwinski
 *
 */
public class SchedulerManagementHandler {
    /**
     * Initialization of the location for SAP logging.
     */
    private final static Location location = Location
                             .getLocation(SchedulerManagementHandler.class);

    /**
     * Initialization of the category for SAP logging.
     */
    private final static Category category = LoggingHelper.SYS_SERVER;

    private DataSource  mDataSource;
    private Environment mEnvironment;
    
    // data source name
    //
    public static final String DATASOURCE_NAME = "jdbc/notx/SAP/BC_SCHEDULER";
        
    private static SchedulerManagementHandler sJobQueryHandler;
    
    public static SchedulerManagementHandler Instance() {
        return sJobQueryHandler; 
    }
    
    public SchedulerManagementHandler(Environment env) 
                        throws ServiceException {
        init();
        sJobQueryHandler = this;
        mEnvironment = env;
    }
    
    /**
     * init method called during service initialization (called during startup
     * of the service)
     */
    public void init()
                  throws ServiceException {
        try {
            Context jCtx = new InitialContext();
            mDataSource = (DataSource)jCtx.lookup(DATASOURCE_NAME);
            
        } catch (NamingException ne) {
            throw new ServiceException(location, new LocalizableTextFormatter(SchedulerAdapterResourceAccessor.getResourceAccessor(),
                                                                    SchedulerAdapterResourceAccessor.DATASOURCE_LOG_NOT_INIT, 
                                                                    new Object[] {DATASOURCE_NAME}), ne);
        }
    }

    
    public SchedulerDefinition getSchedulerByName(String schedulerName)
                                                            throws SQLException {
    
        SchedulerDefinition def = mEnvironment.getSchedulerCache().cache_getSchedulerDefinitionByName(schedulerName);
        if (def != null) {
            return def;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = mDataSource.getConnection();

            ps = conn.prepareStatement(
                    "SELECT ID, SCHEDULER_NAME, SCHEDULER_USER, DESCRIPTION, SUBSCRIBER_ID, LAST_ACCESS, INACTIVITY_GRACE, STATUS " +
                    "FROM BC_JOB_SCHEDULERS " +
                    "WHERE SCHEDULER_NAME = ?");
            
            ps.setString(1, schedulerName);

            rs = ps.executeQuery();
            
            if (!rs.next()) {
                // no such scheduler
                return null;
            }
            
            def = readSchedulerResultSet(rs);
            
            // put it to the cache
            mEnvironment.getSchedulerCache().cache_put(def);            
            
            return def;
                         
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * This method returns true if the user associated with this scheduler
     * should be removed as well.
     *
     * @param id the id of the scheduler
     * @return true if the scheduler exsits and the user should be deleted, false otherwise
     * @throws SQLException
     */
    public boolean deleteUserForScheduler(SchedulerID id) 
                                               throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = mDataSource.getConnection();

            ps = conn
                    .prepareStatement("SELECT USER_EXISTENT FROM BC_JOB_SCHEDULERS "
                            + "WHERE ID = ?");

            ps.setBytes(1, id.getBytes());

            rs = ps.executeQuery();

            if (!rs.next()) {
                // no such scheduler
                return false;
            }

            short result = rs.getShort("USER_EXISTENT");
            
            return result == (short)0;

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    
    public SchedulerDefinition getSchedulerById(SchedulerID id)
                                                 throws SQLException {
      
        SchedulerDefinition def = mEnvironment.getSchedulerCache().cache_getSchedulerDefinitionByID(id);
        if (def != null) {
            return def;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = mDataSource.getConnection();

            ps = conn
                    .prepareStatement("SELECT ID, SCHEDULER_NAME, SCHEDULER_USER, DESCRIPTION, SUBSCRIBER_ID, LAST_ACCESS, INACTIVITY_GRACE, STATUS "
                            + "FROM BC_JOB_SCHEDULERS "
                            + "WHERE ID = ?");

            ps.setBytes(1, id.getBytes());

            rs = ps.executeQuery();

            if (!rs.next()) {
                // no such scheduler
                return null;
            }

            def = readSchedulerResultSet(rs);
            
            // put it to the cache
            mEnvironment.getSchedulerCache().cache_put(def);  

            return def;

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    
    
    public SchedulerDefinition getSchedulerByUser(String user)
                                                   throws SQLException {
      
        SchedulerDefinition def = mEnvironment.getSchedulerCache().cache_getSchedulerDefinitionByUser(user);
        if (def != null) {
            return def;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = mDataSource.getConnection();

            ps = conn.prepareStatement(
                    "SELECT ID, SCHEDULER_NAME, SCHEDULER_USER, DESCRIPTION, SUBSCRIBER_ID, LAST_ACCESS, INACTIVITY_GRACE, STATUS " +
                    "FROM BC_JOB_SCHEDULERS " +
                    "WHERE SCHEDULER_USER = ?");
            
            ps.setString(1, user);

            rs = ps.executeQuery();
            
            if (!rs.next()) {
                // no such scheduler
                return null;
            }
            
            def = readSchedulerResultSet(rs);
            
            // put it to the cache
            mEnvironment.getSchedulerCache().cache_put(def); 
            
            return def;
                         
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


    public SchedulerDefinition[] getAllSchedulers() 
                                    throws SQLException {

        Connection conn = mDataSource.getConnection();
        
        try {
            return getAllSchedulers(conn);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    public SchedulerDefinition[] getAllSchedulers(Connection conn)
                                                    throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {

            ps = conn.prepareStatement(
                    "SELECT ID, SCHEDULER_NAME, SCHEDULER_USER, DESCRIPTION, SUBSCRIBER_ID, LAST_ACCESS, INACTIVITY_GRACE, STATUS " +
                    "FROM BC_JOB_SCHEDULERS");
            
            rs = ps.executeQuery();
            
            ArrayList<SchedulerDefinition> schedulers = new ArrayList<SchedulerDefinition>();

            while (rs.next()) {
                schedulers.add(readSchedulerResultSet(rs));
            }
            
            SchedulerDefinition[] schedDefsArray = schedulers.toArray(new SchedulerDefinition[schedulers.size()]);
            
            // put them to the cache
            mEnvironment.getSchedulerCache().cache_put(schedDefsArray);
            
            return schedDefsArray;
                         
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }
    
    public SchedulerDefinition addScheduler(
                                      SchedulerID id, 
                                      String schedulerName, 
                                      String userName, 
                                      String description,
                                      long inactivityGracePeriod,
                                      String[] events,
                                      boolean userCreated)
                                            throws SchedulerAlreadyDefinedException,
                                                   SQLException,
                                                   TxException {

        Connection conn = null;
        PreparedStatement ps = null;
        TransactionTicket txticket = null;
        
        try {

            // open a TX if one doesn't exist yet. 
            txticket = TxManager.required();

            conn = mEnvironment.getDataSourceTx().getConnection();

            // (0) Check whether this is already a scheduler with the given
            //     name
            
            SchedulerDefinition existing = getSchedulerByUser(userName);
            if (existing != null) {
                throw new SchedulerAlreadyDefinedException("An external scheduler which is associated with the given user name \""
                        + userName + "\" does already exist. The scheduler name is \"" + existing.getName() + "\".");
            }
            
            // (1) Add subscriber for this scheduler
            //

            byte[] subscriberId = new GUID().toBytes();
            
            ps = conn.prepareStatement(
                    "INSERT INTO BC_JOB_SUBSCRIBER (ID, PERSIST_EVENTS) " +
                    "VALUES (? , 1)");

            ps.setBytes(1, subscriberId);
            
            ps.execute();
            ps.close();

            // (2) Add scheduler to scheduler table (may fail if there is
            //     a scheduler with the same name
            //
            ps = conn.prepareStatement( 
                    "INSERT INTO BC_JOB_SCHEDULERS " + 
                    "(ID, SCHEDULER_NAME, SCHEDULER_USER, DESCRIPTION, SUBSCRIBER_ID, LAST_ACCESS, INACTIVITY_GRACE, STATUS, USER_EXISTENT) " +  
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            
            ps.setBytes(1, id.getBytes());
            ps.setString(2, schedulerName);
            ps.setString(3, userName);
            ps.setString(4, description);
            ps.setBytes(5, subscriberId);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setLong(7,inactivityGracePeriod);
            ps.setShort(8, SchedulerStatus.active.getValue());
            ps.setShort(9, (userCreated ? (short)0 : (short)1));
            
            try {
                ps.execute();
            } catch (DuplicateKeyException dke) {
                throw new SchedulerAlreadyDefinedException("A scheduler with name \"" + schedulerName + "\" exists already.", dke);
            }
            ps.close();

            // (3) Add events which this scheduler has subscribed for
            //
            
            if (events.length > 0) {
                
                ps = conn.prepareStatement(
                        "INSERT INTO BC_JOB_FILTER " +
                        "(SUBSCRIBER_ID, FILTER_STRING) " +
                        "VALUES (?, ?)");             

                for (String event : events) {
                    
                    ps.setBytes(1, subscriberId);
                    ps.setString(2, event);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            SchedulerDefinition scheduler = new SchedulerDefinition(id,
                                                                    schedulerName,
                                                                    userName,
                                                                    description,
                                                                    SubscriberID.parseID(subscriberId),
                                                                    SchedulerStatus.inactive,
                                                                    (long)-1,
                                                                    inactivityGracePeriod);
                        
            TxManager.commitLevel(txticket);
            
            // As the persistent subscriber list has changed we need 
            // to invalidate all lists in memory
            //
            mEnvironment.getEventManager().invalidatePersistentEventSubscriberList();
            
            // put the new SchedulerDefinition to the cache
            mEnvironment.getSchedulerCache().cache_put(scheduler);
            
            return scheduler;

        } finally {
        
            if (txticket != null){
                TxManager.leaveLevel(txticket);
            }

            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    public void removeScheduler(SchedulerDefinition scheduler)
                                                         throws TxException,
                                                                SQLException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        TransactionTicket txticket = null;
        
        try {

            // open a TX if one doesn't exist yet. 
            txticket = TxManager.required();

            conn = mEnvironment.getDataSourceTx().getConnection();

            // (1) delete subscriber entry
            //
            ps = conn.prepareStatement(
                    "DELETE FROM BC_JOB_SUBSCRIBER " +
                    "WHERE ID = ?");

            ps.setBytes(1, scheduler.getSubscriberId().getBytes());

            ps.execute();
            ps.close();
            
            // (2) clear all events
            //
            clearAllEvents(conn, scheduler.getSubscriberId());        
            
            // (3) remove scheduler from scheduler table
            //
            ps = conn.prepareStatement( 
                    "DELETE FROM BC_JOB_SCHEDULERS " +
                    "WHERE ID = ?");

            ps.setBytes(1, scheduler.getId().getBytes());

            ps.execute();
            ps.close();

            // (4) remove all subscriber filters
            //
            ps = conn.prepareStatement(
                    "DELETE FROM BC_JOB_FILTER " +
                    "WHERE SUBSCRIBER_ID = ?");
            
            ps.setBytes(1, scheduler.getSubscriberId().getBytes());
            ps.execute();
            ps.close();
                        
            TxManager.commitLevel(txticket);
            
            // As the persistent subscriber list has changed we need 
            // to invalidate all lists in memory
            //
            mEnvironment.getEventManager().invalidatePersistentEventSubscriberList();
            
            // we need also to invalidate the cached SchedulerDefinition
            mEnvironment.getSchedulerCache().cache_invalidate(scheduler);
                                
        } finally {

            if (txticket != null){
                TxManager.leaveLevel(txticket);
            }

            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void activateScheduler(SchedulerDefinition def)
                                            throws SQLException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = mDataSource.getConnection();

            ps = conn.prepareStatement( 
                    "UPDATE BC_JOB_SCHEDULERS SET STATUS " + 
                    "SET STATUS = ? " +  
                    "WHERE ID = ?");
            
            ps.setShort(1, SchedulerDefinition.SchedulerStatus.active.getValue());
            ps.setBytes(2, def.getId().getBytes());
            
            ps.execute();
            ps.close();
            ps = null;

            // As the persistent subscriber list has changed we need 
            // to invalidate all lists in memory
            //
            mEnvironment.getEventManager().invalidatePersistentEventSubscriberList();
            
            // put it to the cache
            mEnvironment.getSchedulerCache().cache_put(def);  
            
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    public void updateTimestamp(SchedulerID id) 
                                            throws SQLException {

        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = mDataSource.getConnection();
            ps = conn.prepareStatement( 
                    "UPDATE BC_JOB_SCHEDULERS SET LAST_ACCESS = ?, STATUS = ?" + 
                    "WHERE ID = ?");
            
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setShort(2, SchedulerStatus.active.getValue());
            ps.setBytes(3, id.getBytes());
            
            ps.execute();
            ps.close();
            ps = null;

        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
        
    public void deactivateScheduler(SchedulerDefinition scheduler) 
                                                   throws SQLException {
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = mDataSource.getConnection();

            // turn off auto commit (everything done in one tx)
            //
            conn.setAutoCommit(false);
            
            ps = conn.prepareStatement( 
                    "UPDATE BC_JOB_SCHEDULERS " + 
                    "SET STATUS = ? " +  
                    "WHERE ID = ?");
            
            ps.setShort(1, SchedulerDefinition.SchedulerStatus.inactive.getValue());
            ps.setBytes(2, scheduler.getId().getBytes());
            
            ps.execute();
            ps.close();
            ps=null;

            clearAllEvents(conn, scheduler.getSubscriberId());

            conn.commit();

            // As the persistent subscriber list has changed we need 
            // to invalidate all lists in memory
            //
            mEnvironment.getEventManager().invalidatePersistentEventSubscriberList();
            
            // we need also to invalidate the cached SchedulerDefinition
            mEnvironment.getSchedulerCache().cache_invalidate(scheduler);            
            
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        
    }
    
    private void clearAllEvents(Connection conn, SubscriberID id)
                                                            throws SQLException {
        
        PreparedStatement ps = null;

        try {

            ps = conn.prepareStatement(
                    "DELETE FROM BC_JOB_EVENTS " +
                    "WHERE SUBSCRIBER_ID = ?");
            
            ps.setBytes(1, id.getBytes());

            ps.execute();
            ps.close();
            ps = null;
            
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    
    private SchedulerDefinition readSchedulerResultSet(ResultSet rs)
                                                          throws SQLException {

        Timestamp lastAccess = rs.getTimestamp("LAST_ACCESS");
        
        return new SchedulerDefinition(
                SchedulerID.parseID(rs.getBytes("ID")),
                rs.getString("SCHEDULER_NAME"),
                rs.getString("SCHEDULER_USER"),
                rs.getString("DESCRIPTION"),
                SubscriberID.parseID(rs.getBytes("SUBSCRIBER_ID")),
                rs.getShort("STATUS") == (short)0 ? SchedulerStatus.inactive : SchedulerStatus.active,
                lastAccess == null ? System.currentTimeMillis() : lastAccess.getTime(),
                rs.getLong("INACTIVITY_GRACE")
                );
    }
    
}
