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
package com.sap.engine.services.scheduler.runtime.jxbp;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import sun.security.action.GetBooleanAction;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduler.runtime.db.SchedulerManagementHandler;
import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.scheduler.runtime.SchedulerAlreadyDefinedException;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SchedulerRemoveException;
import com.sap.scheduler.runtime.UserAccountException;
import com.sap.scheduler.runtime.SchedulerDefinition.SchedulerStatus;
import com.sap.scheduler.spi.JXBPException;

import com.sap.security.api.IPrincipal;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserMaint;
import com.sap.security.api.NoSuchUserAccountException;
import com.sap.security.api.NoSuchUserException;
import com.sap.security.api.UMException;
import com.sap.security.api.UMFactory;
import com.sap.security.api.IUserAccountFactory;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.logon.ILoginConstants;
import com.sap.security.core.util.UMEEntityAccessPermission;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.transaction.TransactionTicket;
import com.sap.transaction.TxException;
import com.sap.transaction.TxManager;
import com.sap.transaction.TxRollbackException;

/**
 * This class implements the management of external schedulers.
 * 
 * @author Dirk Marwinski
 *
 */
public class SchedulerManager {

    /**
     * Initialization of the location for SAP logging.
     */
    private final static Location location = Location
                             .getLocation(SchedulerManager.class);

    /**
     * Initialization of the category for SAP logging.
     */
    private final static Category category = LoggingHelper.SYS_SERVER;
    
    private Environment mEnvironment;
    
    private SchedulerManagementHandler mSMH;
    
    public SchedulerManager(Environment env) {
        mEnvironment = env;
        mSMH = env. getSchedulerManagementHandler();
    }

    
    /**
     * Register a new external scheduler with the scheduler runtime.
     * 
     * @param schedulerName
     * @param userName
     * @param userPassword
     * @param description
     * @param inactivityGracePeriod
     * @param events
     * @return
     * @throws UserAccountException
     * @throws SchedulerAlreadyDefinedException
     * @throws SQLException
     */
    public SchedulerDefinition addScheduler(
                         String schedulerName,
                         String userName,
                         String userPassword,
                         String description,
                         long inactivityGracePeriod,
                         String[] events
                         ) throws UserAccountException,
                                  SchedulerAlreadyDefinedException,
                                  SQLException {

        if (userName == null) {
            // fail fast
            throw new NullPointerException("User name must not be null");
        }
        
        // check whether this external scheduler already exists
        //
        if (getSchedulerByName(schedulerName) != null) {
            throw new SchedulerAlreadyDefinedException("An external scheduler with name \"" + schedulerName + "\" already exists.");
        }
        
        // check whether this user is already associated with an external 
        // scheduler (must not)
        //
        SchedulerDefinition defUser = getSchedulerByUser(userName); 
        if ( defUser != null) {
            throw new SchedulerAlreadyDefinedException("An external scheduler with name \"" + defUser.getName() + "\" is already associated with user \"" + userName + "\".");
        }

        TransactionTicket ticket = null;
        
        try {

            boolean userExists = userExists(userName);
            boolean userCreated = false;

            // Open a new JTA transaction, we don't join an existing one
            //
            ticket = TxManager.requiresNew();
            
            // ***************************************************************
            // Step 1: check for existing user and create one if neccessary
            //
            // if the user password is null we don't try to create a new 
            // user as we assume there is one alreay. This is needed because
            // it might not be possible to create a user from here (use case:
            // users are maintained in ABAP)
            
            if (userPassword != null) {
                // add user to UME, user must not exist
                //
                if (userExists) {
                    TxManager.setRollbackOnly();
                    throw new UserAccountException("A user with name " + userName + " exists already.");
                }
                
                addUser(schedulerName, userName, userPassword);
                userCreated = true;
                
            } else {
                
                // user must exist and there must be no scheduler associated
                // with it

                if (!userExists) {
                    TxManager.setRollbackOnly();
                    throw new UserAccountException("A user with name " + userName + " does not exist.");
                }                
            }
            
            // ***************************************************************
            // Step 2: add user/scheduler mapping to scheduler table
            //
            
            SchedulerDefinition def = null;
            
            def = mEnvironment.getSchedulerManagementHandler().addScheduler(
                                SchedulerID.newID(),
                                schedulerName,
                                userName,
                                description,
                                inactivityGracePeriod,
                                events,
                                userCreated);
            
            // if everything is fine, commit the database modifications on
            // this transaction level; note, that this might be a noop here, if
            // the previous call of required() has not started its own JTA
            // transaction but only joined an existing one
            TxManager.commitLevel(ticket);

            return def;
            
        } catch (TxException e) {

            throw new UserAccountException("Scheduler \"" + schedulerName + "\" cannot be added to the system.", e);

        } catch (UMException ume) {

            throw new UserAccountException("Scheduler \"" + schedulerName + "\" cannot be added to the system.", ume);
            
        } finally {
            // Complete and leave the current transaction level; if the
            // commitLevel() operation has not been executed because some
            // application error ocurred, then the virtual transaction will be
            // rolled back implicitly by the leaveLevel() method (either by
            // directly executing a rollback operation, if the transaction was
            // started on this level, or indirectly by marking the current
            // transaction for rollback only).
            try {
                if(ticket != null){
                  TxManager.leaveLevel(ticket);
                }
            } catch (TxException txe) {
                location.traceThrowableT(Severity.ERROR,"Error in TxManager.leaveLevel()", txe);
                // TODO handle and print
            }
        }
    }

    /**
     * Remove external scheduler information (and user)
     * 
     * @param def
     * @throws NullPointerException if def is null
     */
    public void removeScheduler(SchedulerDefinition def) 
                                              throws SchedulerRemoveException,
                                                     SQLException {

        if (def == null) {
            throw new NullPointerException("removeScheduler invoked will null argument");
        }
        
        SchedulerDefinition builtin = getBuiltinScheduler();
        if (builtin.getId().equals(def.getId())) {
            category.errorT(location, "Builtin scheduler cannot be deleted.");
            throw new SchedulerRemoveException("Builtin scheduler cannot be deleted.");
        }

        // read it again so no user can pass garbage here (e.g. remove a 
        // random user from the system
        //
        SchedulerDefinition toRemove = getSchedulerById(def.getId());
        if (toRemove == null) {
            throw new SchedulerRemoveException("Inalid scheduler definition passed to removeScheduler() method.");
        }
        
        // check whether the user needs to be removed as well. It will only 
        // be removed if it was created when this scheduler was defined. 
        //
        boolean deleteUser = mSMH.deleteUserForScheduler(def.getId());
        
        TransactionTicket ticket = null;
        
        try {

            // Open a new JTA transaction, we don't join an existing one
            //
            ticket = TxManager.requiresNew();
            
            if (deleteUser) {
                removeUser(toRemove.getUser());
            }
            
            mEnvironment.getSchedulerManagementHandler().removeScheduler(toRemove);

            TxManager.commitLevel(ticket);

        } catch (TxException tx) {

            throw new SchedulerRemoveException("The scheduler \"" + toRemove.getName() + 
                    "\" could not be removed.", tx);

        } catch (UMException ume) {
            
            throw new SchedulerRemoveException("The scheduler \"" + toRemove.getName() + 
                    "\" could not be removed because there was a problem removing " +
                    " the associated user \"" + toRemove.getUser() + "\".", ume);

        } catch (UserAccountException uae) {

            throw new SchedulerRemoveException("The scheduler \"" + toRemove.getName() + 
                    "\" could not be removed because there was a problem removing " +
                    " the associated user \"" + toRemove.getUser() + "\".", uae);
            
        } finally {
            try {
                if(ticket != null){
                  TxManager.leaveLevel(ticket);
                }
            } catch (TxException txe) {
                location.traceThrowableT(Severity.ERROR,"Error in TxManager.leaveLevel()", txe);
            }
        }
    }
    
    /**
     * Activate external scheduler
     * 
     * @param def
     * @throws SQLException
     */
    public void activateScheduler(SchedulerDefinition def) 
                                                 throws SQLException {

        mEnvironment.getSchedulerManagementHandler().activateScheduler(def);
    }
    
    /**
     * Activate external scheduler (also removes all events)
     * 
     * @param def
     * @throws SQLException
     */
    public void deactivateScheduler(SchedulerID id) 
                                              throws SQLException {
        
        SchedulerDefinition def = mEnvironment.getSchedulerManagementHandler().getSchedulerById(id);
        
        if (def != null) {
            mEnvironment.getSchedulerManagementHandler().deactivateScheduler(def);
       } else {
           // nothing to deactivate but issue warning
           //
           category.warningT(location, "Deactivation request for scheduler with id \"" + id.toString() +
                                       " received but there is no such scheduler.");
       }
        
    }
    
    /**
     * Updates the timestamp for a given external scheduler
     */
    public void updateTimestamp(SchedulerID id) {
        
        try {
            mSMH.updateTimestamp(id);
        } catch (SQLException sql) {
            category.logThrowableT(Severity.ERROR, location, "Update timestamp for scheduler with id \"" + 
                    id.toString() + "\" failed.", sql);
        }
    }
    
    public SchedulerDefinition getSchedulerByName(String name) 
                                         throws SQLException {
        
        if (name == null) {
            throw new NullPointerException("Scheduler name must not be null.");
        }
        return mSMH.getSchedulerByName(name);
    }
    
    
    public SchedulerDefinition getSchedulerById(SchedulerID id) throws SQLException {

        if (id == null) {
            throw new NullPointerException("SchedulerID must not be null.");
        }
        return mSMH.getSchedulerById(id);
    }
    
    
    public SchedulerDefinition getSchedulerByUser(String user) 
                                                        throws SQLException {
        
        if (user == null) {
            throw new NullPointerException("User name must not be null.");
        }
        return mSMH.getSchedulerByUser(user);
    }
    
    public SchedulerDefinition[] getAllSchedulers()
                                        throws SQLException {

        return mSMH.getAllSchedulers();
    }

    public SchedulerDefinition getBuiltinScheduler()
                                          throws SQLException {

        return mSMH.getSchedulerByName(SchedulerDefinition.SAP_SCHEDULER_NAME);
    }

    
    // special method for jxbp (get cached scheduler)
    //
    public SchedulerDefinition getSchedulerForUser(String user) 
                                                       throws SQLException {
        
        SchedulerDefinition def = null;
        
        def = getSchedulerByUser(user);
        
        return def;
    }
    
    // ----------------------------------------------------------------------
    // class private methods below
    // ----------------------------------------------------------------------

    private void addUser(String schedulerName, String userName, String password) 
                                                                throws UMException, UserAccountException {

        if (location.beDebug()) {
            location.debugT("Creating user \"" + userName + "\" for scheduler \"" + schedulerName + "\".");
        }
        IUserFactory userfact = null;
        IUserAccountFactory accountFact = null;

        // get user factory which participates in our transaction
        //
        try {
            Properties initCtxProps = new Properties();
            initCtxProps.put("domain","true");
            InitialContext ctx = new InitialContext(initCtxProps);
            userfact = (IUserFactory)ctx.lookup("UME/sharable/com.sap.security.api.IUserFactory");
            accountFact = (IUserAccountFactory)ctx.lookup("UME/sharable/com.sap.security.api.IUserAccountFactory");
        } catch (NamingException ne) {
            throw new UserAccountException("Unable to look up user " + userName + ".",ne);
        }
        
        IUserMaint userMaint = userfact.newUser(userName);

        // make sure the string does not exceed 255 characters
        //
        String displayName = "JXBP User for external scheduler \"" + schedulerName + "\".";
        if (displayName.length() > 255) {
            displayName = displayName.substring(0,254);
        }
        userMaint.setDisplayName(displayName);
        
        // add IUserMaint.setLastName(userName), as user creation in ABAP 
        // works only if the last name of the user is available. 
        //
        userMaint.setLastName(userName);

        IUserAccount account = accountFact.newUserAccount(userName);
        account.setPassword(password);
        account.setPasswordChangeRequired(false);

        userfact.commitUser(userMaint,account);

        if (UMFactory.getPrincipalFactory().isPrincipalAttributeModifiable(account.getUniqueID(), IPrincipal.DEFAULT_NAMESPACE, ILoginConstants.LOGON_ALIAS))
        {
        	account = UMFactory.getUserAccountFactory().getMutableUserAccount(account.getUniqueID());
        	account.setAttribute(IPrincipal.DEFAULT_NAMESPACE, ILoginConstants.LOGON_ALIAS, new String[]{ userName });
            	account.save();
            account.commit();
        }
        if (location.beDebug()) {
            location.debugT("User \"" + userName + "\" for scheduler \"" + schedulerName + "\" successfully created.");
        }
    }
    
    boolean userExists(String userName) 
                                 throws UMException {

        boolean userExists; 
        try 
        { 
                IUser userToCheck  = UMFactory.getUserFactory().getUserByLogonID(userName); 
                userExists = true; 
        } 
        catch (NoSuchUserAccountException nsuex) 
        { 
                //user doesn't exist 
                userExists = false;
        } catch (NoSuchUserException ns) {
            // required due to an incompatible change in the UME
            userExists = false;
        }
        return userExists;
    }

    
    private void removeUser(String userName) 
                                  throws UMException,
                                         UserAccountException {

        if (location.beDebug()) {
            location.debugT("Removing user \"" + userName + "\".");
        }

        IUserFactory userfact = null;
        IUserAccountFactory accountFact = null;

        // get user factory which participates in our transaction
        //
        try {
            InitialContext ctx = new InitialContext();
            userfact = (IUserFactory)ctx.lookup("UME/sharable/com.sap.security.api.IUserFactory");
            accountFact = (IUserAccountFactory)ctx.lookup("UME/sharable/com.sap.security.api.IUserAccountFactory");
        } catch (NamingException ne) {
            throw new UserAccountException("Unable to look up UME factories.",ne);
        }
                
        IUserAccount acc = accountFact.getUserAccountByLogonId(userName);
        userfact.deleteUser(acc.getAssignedUserID());    

        if (location.beDebug()) {
            location.debugT("User \"" + userName + "\" removed.");
        }
    }
    
}
