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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.engine.services.scheduleradapter.jobdeploy.UpdateJobDefinition;
import com.sap.engine.services.scheduleradapter.jobdeploy.UpdateJobDefinition.UpdateType;
import com.sap.scheduler.runtime.util.LocalizationHelper;
import com.sap.guid.GUID;
import com.sap.scheduler.runtime.JobDefinition;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobDefinitionName;
import com.sap.scheduler.runtime.JobParameterDefinition;
import com.sap.scheduler.runtime.JobParameterType;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.util.cache.CacheFacade;
import com.sap.util.cache.CacheRegion;
import com.sap.util.cache.CacheRegionFactory;
import com.sap.util.cache.RegionConfigurationInfo;
import com.sap.util.cache.exception.CacheException;
import com.sap.util.cache.exception.WriteException;

/**
 * This class implements the database access methods for the management of
 * JobDefinitions, JobParameterDefintions and localizations infos.
 * 
 * This class will is an singleton will will instantiated only once while
 * scheduler-runtime service startup.
 * 
 * Note: Currently not all methods are moved from DBHandler (SQLJ) to this class
 * 
 * @author Thomas Mueller (d040939)
 */

public class JobDefinitionHandler {
    // logging and tracing
    private final static Location location = Location.getLocation(JobDefinitionHandler.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

    private DataSource mDataSource = null;
    private Environment mEnv = null;
    
    
    /**
     * Constructor
     *  
     * @param env the Environment
     * 
     * @throws ServiceException the DataSource failed to lookup
     */
    public JobDefinitionHandler(Environment env) {
        mEnv = env;
        mDataSource = mEnv.getDataSource();
    }

    // -------------------------------------------------------------------------
    // ------------------ Methods for adding JobDefinitions --------------------
    // -------------------------------------------------------------------------

    
    /**
     * Adds JobDefinitions to the DB. The commit will be executed inside this 
     * method.
     * 
     * The ArrayList parameter is an ArrayList of JobDefinition java arrays. 
     * The semantics is the following:
     * - Length of the array is one: This is a new job which does not exist
     * - Length of the array is two:
     *      - the second field is null: The job did exist before and is equal,
     *                                   no change required
     *      - the second field is not null: the job did exist before but has 
     *                                   changed with this deployment. Remove
     *                                   the old one and add the new one
     *                                   Field 0 contains the new job, field 1
     *                                   contains the old one
     *                                   <p>
     * Note: We need to handle the situation when with this deployment less job-
     *       definitions are included in comparison with the previous deployment, 
     *       that we have to remove (deactivate) the ones which are obsolete.  
     * <p>
     * Note 2: The method will append removed job definitions to the jobDefinitions
     * array.
     * 
     * @param jobDefinitions ArrayList of JobDefinitions
     * 
     * @throws SQLException if any db-problem occurs
     */
    public void updateJobDefinitionsForApplication(ArrayList<UpdateJobDefinition> jobDefinitions) throws SQLException {
        Connection conn = mDataSource.getConnection();
        // turn off auto commit (everything done in one tx)
        conn.setAutoCommit(false);
        PreparedStatement ps = null;

        StringBuilder stmt = new StringBuilder();
        stmt.append("INSERT INTO BC_JOB_DEFINITION (ID, NAME, DESCRIPTION, JOB_TYPE, "); 
        stmt.append("RETENTION_PERIOD, APPLICATION_NAME) VALUES(?, ?, ?, ?, ?, ?)");        
        
        try {
            ps = conn.prepareStatement(stmt.toString());

            for (int i = 0; i < jobDefinitions.size(); i++) {
                
                UpdateJobDefinition udef = jobDefinitions.get(i);
                
                switch (udef.getUpdateType()) {
                
                case NEW_JOB_DEFINITION:
                    addJobDefinition(conn, ps, udef.getJobDefinition(), false, null);
                    break;
                case CHANGED_JOB_DEFINITION:
                    addJobDefinition(conn, ps, udef.getJobDefinition(), true, udef.getOldJobDefinition());
                    break;
                case REMOVED_JOB_DEFINITION:
                    deactivateJobDefinition(conn, udef.getRemovedJobDefinition().getJobDefinitionId());
                    break;
                case UNCHANGED_JOB_DEFINITION:
                    // nothing to do, comment for clarity
                    break;
                default:
                    // just for making is shatter if new update types are added
                    throw new IllegalArgumentException("Update type \"" + udef.getUpdateType().toString() + "\" not known.");
                }
            }
            
            // flush the inserts to db
            ps.executeBatch();

            // commit transaction
            conn.commit();

        } catch (SQLException sql) {
            // something went wrong, do a rollback, error will be
            // dealt with later
            //
            conn.rollback();
            throw sql;
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqle) {
                    location.traceThrowableT(Severity.ERROR, "Non critical error while closing connection (but should not happen)", sqle);
                }
            }
        }
    } // addJobDefinitions

    
    /**
     * Add the given job defintion in the database. If the boolean value is set
     * to true, the second job definition parameter must not be null. It will be
     * deactivated.
     * 
     * The commit to DB of this JobDefinition will be handled outside this method 
     * 
     * @param conn the connection to which this JobDefintion will be added
     * @param ps the PreparedStatement to which this JobDefintion will be batched
     * @param job the new JobDefinition
     * @param deactivate if the old JobDefinition should be deactivated (marked for 
     *                   cleaning up)
     * @param oldJob the new JobDefinition
     * 
     * @throws SQLException if any DB error occurs
     */
    protected void addJobDefinition(Connection conn, PreparedStatement ps, JobDefinition job, boolean deactivate, JobDefinition oldJob) throws SQLException {
        String jobName = job.getJobDefinitionName().getName();
        String appName = job.getJobDefinitionName().getApplicationName();

        // deactivate "old" job definition (if required)
        //
        if (deactivate) {
            deactivateJobDefinition(conn, oldJob.getJobDefinitionId());
        }

        // add new job definition
        //
        byte[] id = job.getJobDefinitionId().getBytes();
        String description = job.getDescription();
        short jobType = (short) job.getJobType();

        ps.setBytes(1, id);
        ps.setString(2, jobName);
        ps.setString(3, description);
        ps.setShort(4, jobType);
        ps.setInt(5, job.getRetentionPeriod());
        ps.setString(6, appName);

        ps.addBatch();

        // add properties
        //
        addJobDefinitionProperties(conn, job.getJobDefinitionId(), job.getProperties());

        // add parameters
        //
        addJobParameterDefinitions(conn, job.getJobDefinitionId(), job.getParameters());

        // add localization info
        //
        writeLocalizationInfo(conn, job);
    }

    
    /**
     * Writes the localization information to the given JobDefinition. The commit 
     * of this action will be handled by the caller.
     * 
     * @param conn the Connection
     * @param jobDef the JobDefinition
     * 
     * @throws SQLException if any DB-error occurs
     */
    private void writeLocalizationInfo(Connection conn, JobDefinition jobDef) throws SQLException {
        // commit must be handled by calling method
        PreparedStatement ps = null;
        try {
            StringBuilder insertIntoBC_JOB_JOB_I18N = new StringBuilder();
            insertIntoBC_JOB_JOB_I18N.append("INSERT INTO BC_JOB_JOB_I18N ");
            insertIntoBC_JOB_JOB_I18N.append("(JOB_DEFINITION_ID, JOB_NAME, PARAM_NAME, NAME_VALUE, DESC_VALUE, LANG, COUNTRY) ");
            insertIntoBC_JOB_JOB_I18N.append("VALUES(?, ?, ?, ?, ?, ?, ?)");
            ps = conn.prepareStatement(insertIntoBC_JOB_JOB_I18N.toString());

            byte[] jobDefId = jobDef.getJobDefinitionId().getBytes();
            String jobName = jobDef.getJobDefinitionName().getName();
            String paramName = null;

            boolean valuesExist = false;

            // add first the localization info of the JobDefintion
            HashMap<String, HashMap<String, String>> localeMap = jobDef.getLocalizationInfoMap();
            if (localeMap != null) {
                if (localeMap.size() > 0) {
                    addLocalizationInfoMap(ps, localeMap, jobDefId, jobName, paramName);
                    valuesExist = true;
                }
            }

            JobParameterDefinition[] jobParamDefs = jobDef.getParameters();
            // add the localization info of the JobParameterDefintion
            for (int i = 0; i < jobParamDefs.length; i++) {
                HashMap<String, HashMap<String, String>> localeMapParam = jobParamDefs[i].getLocalizationInfoMap();
                if (localeMapParam != null) {
                    paramName = jobParamDefs[i].getName();
                    if (localeMapParam.size() > 0) {
                        addLocalizationInfoMap(ps, localeMapParam, jobDefId, jobName, paramName);
                        valuesExist = true;
                    }
                }
            }

            if (valuesExist) {
                ps.executeBatch();
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    

    /**
     * Adds the Map of localization info to a JobDefinition. The commit of this
     * action will be handledby the caller.
     * 
     * @param ps the PreparedStatement 
     * @param localeMap the Map of Locales and Properties
     * @param jobDefId the belonging JobDefinition
     * @param jobName JobDefinition name
     * @param paramName JobParameterDefinition if the localization info belongs 
     *                  to a parameter
     *                   
     * @throws SQLException if any DB-error occurs
     */
    private void addLocalizationInfoMap(PreparedStatement ps, HashMap<String, HashMap<String, String>> localeMap, byte[] jobDefId, String jobName, String paramName) throws SQLException {
        // commit & flush must be handled by calling method
        for (Map.Entry<String, HashMap<String, String>> entry : localeMap.entrySet()) {
            String localeStr = entry.getKey();
            HashMap<String, String> props = entry.getValue();

            Locale l = LocalizationHelper.getLocaleFromString(localeStr);
            // language may never be null
            String language = l.getLanguage();

            String country = l.getCountry();
            if ("".equals(country)) {
                country = null;
            }

            for (Iterator<Map.Entry<String, String>> iter = props.entrySet().iterator(); iter.hasNext();) {
                Map.Entry<String, String> propEntry = iter.next();
                String propKey = propEntry.getKey();
                String propValue = propEntry.getValue();

                // add row to the table
                ps.setBytes(1, jobDefId);
                ps.setString(2, jobName);
                ps.setString(3, paramName);
                ps.setString(4, propKey);
                ps.setString(5, propValue);
                ps.setString(6, language);
                ps.setString(7, country);

                ps.addBatch();
            }
        }
    }

    
    /**
     * Add the JobDefinition properties to a Connection. The commit will be 
     * handled by the caller.
     * 
     * @param conn the Connectio
     * @param id the JobDefinition
     * @param properties the properties to add
     * 
     * @throws SQLException if any DB-error occurs
     */
    private void addJobDefinitionProperties(Connection conn, JobDefinitionID id, String[][] properties) throws SQLException {
        // commit must be handled by calling method
        PreparedStatement ps = null;
        try {
            String insertIntoBC_JOB_DEF_PROPS = "INSERT INTO BC_JOB_DEF_PROPS " + "(JOB_DEFINITION_ID, NAME, VAL) " + "VALUES(?, ?, ?)";
            ps = conn.prepareStatement(insertIntoBC_JOB_DEF_PROPS);

            boolean valuesExist = false;

            for (int i = 0; i < properties.length; i++) {
                String name = properties[i][0];

                if (name == null) {
                    // properties can have empty slots
                    continue;
                }
                String value = properties[i][1];
                if ("".equals(value)) {
                    value = null;
                }
                valuesExist = true;

                ps.setBytes(1, id.getBytes());
                ps.setString(2, name);
                ps.setString(3, value);

                ps.addBatch();
            }

            if (valuesExist) {
                ps.executeBatch();
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    

    /**
     * Adds JobParameterDefinitions to a given Connection. The commit of this
     * action wil be handled by the caller.
     * 
     * @param conn the Connection
     * @param id The JobDefinition
     * @param params the parameter to add
     * 
     * @throws SQLException if any DB-error occurs
     */
    private void addJobParameterDefinitions(Connection conn, JobDefinitionID id, JobParameterDefinition[] params) throws SQLException {
        // commit must be handled by calling method
        PreparedStatement ps = null;
        try {
            String insertIntoBC_JOB_DEF_ARGS = "INSERT INTO BC_JOB_DEF_ARGS " + "(ID, JOB_DEF_ID, ARG_NAME, ARG_TYPE, ARG_DIRECTION, ARG_DEFAULT, ARG_GROUP, NULLABLE, DESCRIPTION, DISPLAY) " + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(insertIntoBC_JOB_DEF_ARGS);

            for (int i = 0; i < params.length; i++) {
                JobParameterDefinition p = params[i];
                byte[] paramGuid = new GUID().toBytes();
                String name = p.getName();
                String type = p.getType().toString();
                String direction = p.getDirection();
                String defaultValue = p.getDefaultData();
                
                // Internal CSN 3113673 2008
                // people may decide to specify empty strings for default values
                // which will blow up OpenSQL. In this case we assume that no
                // default value has been specified.
                //
                if ("".equals(defaultValue)) {
                    defaultValue = null;
                }

                String groupName = p.getGroup();
                short nullable = p.isNullable() ? (short) 1 : (short) 0;
                short display = p.isDisplay() ? (short) 1 : (short) 0;

                ps.setBytes(1, paramGuid);
                ps.setBytes(2, id.getBytes());
                ps.setString(3, name);
                ps.setString(4, type);
                ps.setString(5, direction);
                ps.setString(6, defaultValue);
                ps.setString(7, groupName);
                ps.setShort(8, nullable);
                ps.setString(9, p.getDescription());
                ps.setShort(10, display);

                ps.addBatch();
            }

            ps.executeBatch();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // ---------------- Methods for deactivating JobDefinitions ----------------
    // -------------------------------------------------------------------------

    /**
     * Deactivates (marked for deletion) a given JobDefinition. The commit of this
     * action will be handled by the caller 
     * 
     * @param conn the Connection
     * @param id The JobDefinitionId
     * 
     * @throws SQLException in any DB-error occurs
     */
    private void deactivateJobDefinition(Connection conn, JobDefinitionID id) throws SQLException {
        
        mEnv.getSchedulerCache().cache_invalidate(id);
        
        // commit must be handled by calling method
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE BC_JOB_DEFINITION SET REMOVE_DATE = ? WHERE REMOVE_DATE IS NULL AND ID = ?");

            Timestamp removeDate = new Timestamp(new Date().getTime());

            // Mark old job definition as outdated (there may)
            //
            ps.setTimestamp(1, removeDate);
            ps.setBytes(2, id.getBytes());

            ps.execute();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // ------------------ Methods for reading JobDefinitions, ------------------
    // ------------------ respectively Localization info -------------------
    // -------------------------------------------------------------------------

    /**
     * Reads the localization info for all parameter of a given JobDefinition 
     * with one DB-call.
     * 
     * @param conn the Connection
     * @param jobDefId The JobDefinitionId
     * 
     * @return the Map with the key JobParameterDefinition name and the value
     *         Map which contains the Locales and Properties 
     * 
     * @throws SQLException if any DB-error occurs
     */
    protected HashMap<String, HashMap<String, HashMap<String, String>>> getLocalizationInfoForAllParams(Connection conn, JobDefinitionID jobDefId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<String, HashMap<String, HashMap<String, String>>> paramMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();

        try {
            ps = conn.prepareStatement("SELECT LANG, COUNTRY, NAME_VALUE, DESC_VALUE, PARAM_NAME " + "FROM BC_JOB_JOB_I18N " + "WHERE JOB_DEFINITION_ID = ? AND PARAM_NAME IS NOT NULL");

            ps.setBytes(1, jobDefId.getBytes());
            rs = ps.executeQuery();

            String lang = null;
            String country = null;
            String key = null;
            String value = null;
            String paramName = null;

            while (rs.next()) {
                // lang can not be null
                lang = rs.getString("LANG");
                country = rs.getString("COUNTRY");
                if (country == null) {
                    country = "";
                }
                key = rs.getString("NAME_VALUE");
                value = rs.getString("DESC_VALUE");
                paramName = rs.getString("PARAM_NAME");

                HashMap<String, HashMap<String, String>> locTextMap = paramMap.get(paramName);
                if (locTextMap == null) {
                    locTextMap = new HashMap<String, HashMap<String, String>>();
                    LocalizationHelper.addPropertyToLocale(locTextMap, new Locale(lang, country), key, value);
                    paramMap.put(paramName, locTextMap);
                } else {
                    LocalizationHelper.addPropertyToLocale(locTextMap, new Locale(lang, country), key, value);
                }
            } // while

            return paramMap;

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }
    

    /**
     * Reads the localization info for a given JobDefinition and Connection.
     * This method returns only the localization info for the JobDefinition
     * itself. Connection-close will be handled outside by the caller.
     * 
     * @return returns the Map-instance which contains the Locales and Properties 
     *                 or null if there's no localization for this JobDefinition
     *                 
     * @param conn the Connection
     * @param jobDefId The JobDefinitionId
     * 
     * @throws SQLException if any DB-error occurs
     */
    protected HashMap<String, HashMap<String, String>> getLocalizationInfoForJobDefinition(Connection conn, JobDefinitionID jobDefId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap<String, HashMap<String, String>> localeTextMap = null;

        try {
            ps = conn.prepareStatement("SELECT LANG, COUNTRY, NAME_VALUE, DESC_VALUE " + "FROM BC_JOB_JOB_I18N " + "WHERE JOB_DEFINITION_ID = ? AND PARAM_NAME IS NULL");

            ps.setBytes(1, jobDefId.getBytes());
            rs = ps.executeQuery();

            String lang = null;
            String country = null;
            String key = null;
            String value = null;

            while (rs.next()) {
                // lang can not be null
                lang = rs.getString("LANG");
                country = rs.getString("COUNTRY");
                if (country == null) {
                    country = "";
                }
                key = rs.getString("NAME_VALUE");
                value = rs.getString("DESC_VALUE");

                if (localeTextMap == null) {
                    localeTextMap = new HashMap<String, HashMap<String, String>>();
                }

                LocalizationHelper.addPropertyToLocale(localeTextMap, new Locale(lang, country), key, value);
            } // while

            return localeTextMap;

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }
    

    /**
     * Reads the localization info for a given JobDefinition and parameter
     * name. The cleanup of the Connection will be handled outside by the caller.
     * 
     * @param conn the Connection
     * @param id The JobDefinition
     * @param paramName the parameter 
     * 
     * @return returns the Map-instance which contains the Locales and Properties 
     *                 or null if there's no localization for this 
     *                 JobParameterDefinition
     * 
     * @throws SQLException if any DB-error occurs
     */
    protected Map getLocalizationInfoForParam(Connection conn, JobDefinitionID jobDefId, String paramName) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashMap localeTextMap = null;

        try {
            ps = conn.prepareStatement("SELECT LANG, COUNTRY, NAME_VALUE, DESC_VALUE " + "FROM BC_JOB_JOB_I18N " + "WHERE JOB_DEFINITION_ID = ? AND PARAM_NAME = ?");

            ps.setBytes(1, jobDefId.getBytes());
            ps.setString(2, paramName);
            rs = ps.executeQuery();

            String lang = null;
            String country = null;
            String key = null;
            String value = null;

            while (rs.next()) {
                // lang can not be null
                lang = rs.getString("LANG");
                country = rs.getString("COUNTRY");
                if (country == null) {
                    country = "";
                }
                key = rs.getString("NAME_VALUE");
                value = rs.getString("DESC_VALUE");

                if (localeTextMap == null) {
                    localeTextMap = new HashMap();
                }

                LocalizationHelper.addPropertyToLocale(localeTextMap, new Locale(lang, country), key, value);
            } // while

            return localeTextMap;

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    // -------------------------------------------------------------------------
    // ------------------ Methods for removing JobDefinitions ------------------
    // -------------------------------------------------------------------------

    /**
     * Removes specified JobDefinition (and JobParameterDefinition, Localization 
     * information) from database. There is no check on whether it is allowed to 
     * remove the job definition or not. This must be done on a higher layer.
     * 
     * @param def the JobDefinition to remove
     * 
     * @throws SQLException if any DB-error occurs
     */
    public void removeJobDefinition(JobDefinition def) throws SQLException {
        
        mEnv.getSchedulerCache().cache_invalidate(def);
        
        Connection conn = null;
        PreparedStatement psJobDefParams = null;
        PreparedStatement psJobDefs = null;
        PreparedStatement psJobLocalization = null;

        try {
            conn = mDataSource.getConnection();
            // turn off auto commit (everything done in one tx)
            //
            conn.setAutoCommit(false);

            psJobDefParams = conn.prepareStatement("DELETE FROM BC_JOB_DEF_ARGS " + 
                                                   "WHERE JOB_DEF_ID = ?");

            psJobDefs = conn.prepareStatement("DELETE FROM BC_JOB_DEFINITION " + 
                                              "WHERE ID = ?");

            psJobLocalization = conn.prepareStatement("DELETE FROM BC_JOB_JOB_I18N " + 
                                                      "WHERE JOB_DEFINITION_ID = ?");

            byte[] jobDefId = def.getJobDefinitionId().getBytes();

            // Remove job parameter definitions
            psJobDefParams.setBytes(1, jobDefId);
            // Remove job definition
            psJobDefs.setBytes(1, jobDefId);
            // Remove job localization
            psJobLocalization.setBytes(1, jobDefId);

            // flush the changes
            psJobDefParams.execute();
            psJobDefs.execute();
            psJobLocalization.execute();

            conn.commit();

        } finally {
            if (psJobDefParams != null) {
                psJobDefParams.close();
            }
            if (psJobDefs != null) {
                psJobDefs.close();
            }
            if (psJobLocalization != null) {
                psJobLocalization.close();
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqle) {
                    location.traceThrowableT(Severity.ERROR, "Non critical error while closing connection (but should not happen)", sqle);
                }
            }
        } // finally
    }

    
    public JobDefinition getJobDefinitionById(JobDefinitionID defid)  
                                                          throws SQLException {

        JobDefinition def = mEnv.getSchedulerCache().cache_get(defid);
        if (def != null) {
            return def;
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = mDataSource.getConnection();

            ps = conn.prepareStatement(
                    "SELECT ID, NAME, DESCRIPTION, JOB_TYPE, REMOVE_DATE, RETENTION_PERIOD, APPLICATION_NAME " +
                    "FROM BC_JOB_DEFINITION WHERE ID = ?");

            ps.setBytes(1, defid.getBytes());
                        
            ResultSet res = ps.executeQuery();
            
            ArrayList<JobDefinition> defs = readJobDefinitionsFromResultSet(conn, res);
            mEnv.getSchedulerCache().cache_put(defs);
            
            if (defs.size() == 0) {
                return null;
            } else {
                return defs.get(0);
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    
    public JobDefinition[] getJobDefinitions() 
                                   throws SQLException {

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = mDataSource.getConnection();

            ps = conn.prepareStatement(
                    "SELECT ID, NAME, DESCRIPTION, JOB_TYPE, REMOVE_DATE, RETENTION_PERIOD, APPLICATION_NAME " +
                    "FROM BC_JOB_DEFINITION");

            ResultSet res = ps.executeQuery();
            
            ArrayList<JobDefinition> defs = readJobDefinitionsFromResultSet(conn, res);
            
            // Most of the entries will be in the cache already
            // cache_put(defs);
            
            return defs.toArray(new JobDefinition[defs.size()]);

        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
     
    
    public JobDefinition getJobDefinitionByName(String name) 
                                               throws SQLException {

        return getJobDefinitionByName(new JobDefinitionName(name));
    }

    public JobDefinition getJobDefinitionByName(JobDefinitionName name) 
                                                           throws SQLException {

        ArrayList<JobDefinition> defs = getJobDefinitionsByName(name);

        if (defs.size() == 1) {
            return defs.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * This method returns all job definitions with the given job name,
     * regardless of the application they live in
     */
    public ArrayList<JobDefinition> getJobDefinitionsByName(JobDefinitionName name) 
                                                                      throws SQLException {
        Connection conn = null;
        PreparedStatement psJobDef = null;
        
        String selectWithoutAppName =
            "SELECT ID, NAME, DESCRIPTION, JOB_TYPE, REMOVE_DATE, RETENTION_PERIOD, APPLICATION_NAME " +
            "FROM BC_JOB_DEFINITION WHERE NAME = ? AND REMOVE_DATE IS NULL";

        String selectWithAppName =
            "SELECT ID, NAME, DESCRIPTION, JOB_TYPE, REMOVE_DATE, RETENTION_PERIOD, APPLICATION_NAME " +
            "FROM BC_JOB_DEFINITION WHERE NAME = ? AND APPLICATION_NAME = ? AND REMOVE_DATE IS NULL";
        
        try {
            conn = mDataSource.getConnection();

            if (name.getApplicationName() == null) {

                psJobDef = conn.prepareStatement(selectWithoutAppName);
                psJobDef.setString(1, name.getName());
            } else {
                
                psJobDef = conn.prepareStatement(selectWithAppName);
                psJobDef.setString(1, name.getName());
                psJobDef.setString(2, name.getApplicationName());
            }
            
            ResultSet res = psJobDef.executeQuery();
            
            ArrayList<JobDefinition> defs = readJobDefinitionsFromResultSet(conn, res);
            // Most of the entries will be in the cache already
            // cache_put(defs);
            return defs;
            
        } finally {
            if (psJobDef != null) {
                psJobDef.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
        
 
    /**
     * This methods marks all job definitions which belong to the specified
     * application as deactivated.
     */
    public ArrayList<JobDefinition>  deactivateJobDefinitions(String applicationName) 
                                                    throws SQLException {

        Connection conn = null;
        
        try {
            conn = mDataSource.getConnection();

            // turn off auto commit (everything done in one tx)
            //
            conn.setAutoCommit(false);
    
            // figure out which job definitions to deactivate
            //
            ArrayList<JobDefinition> jobDefinitions = getJobDefinitionsByApplication(conn, applicationName);
            
            // deactivate job definitions
            //
            for (int i=0; i < jobDefinitions.size(); i++) {
                JobDefinition jdef = jobDefinitions.get(i);
                mEnv.getSchedulerCache().cache_invalidate(jdef);
                
                if (location.beDebug()) {
                    location.debugT("Job definition \"" + jdef.getJobDefinitionName() + "\" " +
                            "of application \"" + applicationName + "\" is being deactivated.");
                }
                deactivateJobDefinition(conn, jdef.getJobDefinitionId());
            }

            conn.commit();
            return jobDefinitions;

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    
    JobParameterDefinition[] getJobParameterDefinitions(Connection conn,
            JobDefinitionID id) throws SQLException {

    	
    	// Customer CSN 0120025231 0000909781 2008 
    	// 
    	// Due to a missing index on table BC_JOB_DEF_ARGS (columns ARG_NAME
    	// and JOB_DEF_ID) it did happen that there are double entries in that
    	// table. This supposedly happened due to a double import. 
    	//
    	// An index may be added much later. This time we just need to make 
    	// sure we filter the double entries
    	
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn
                    .prepareStatement("SELECT ARG_NAME, ARG_TYPE, ARG_DIRECTION, ARG_DEFAULT, NULLABLE, ARG_GROUP, DESCRIPTION, DISPLAY "
                            + "FROM BC_JOB_DEF_ARGS WHERE JOB_DEF_ID = ?");

            ps.setBytes(1, id.getBytes());

            rs = ps.executeQuery();

            JobParameterDefinition param;
            ArrayList<JobParameterDefinition> params = new ArrayList<JobParameterDefinition>();
            HashSet<String> paramNamesMap = new HashSet<String>();

            // access the localization info for all parameters
            HashMap<String, HashMap<String, HashMap<String, String>>> localizationInfoMap = getLocalizationInfoForAllParams(conn, id);

            while (rs.next()) {
                String paramName = rs.getString("ARG_NAME");
                
                if (paramNamesMap.contains(paramName)) {
                	
                	// double entry in table, skip (see description above)
                	//
                	continue;
                }
                paramNamesMap.add(paramName);
                
                HashMap<String, HashMap<String, String>> localizedTextMap = localizationInfoMap.get(paramName);

                boolean nullable = rs.getShort("NULLABLE") == 1 ? true : false;
                boolean display = rs.getShort("DISPLAY") == 1 ? true : false;

                param = new JobParameterDefinition(paramName, JobParameterType
                        .valueOf(rs.getString("ARG_TYPE")), nullable, rs
                        .getString("DESCRIPTION"), rs.getString("ARG_DEFAULT"),
                        display, rs.getString("ARG_DIRECTION"), rs
                                .getString("ARG_GROUP"), localizedTextMap);

                params.add(param);
            }
            return params.toArray(new JobParameterDefinition[params.size()]);

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    private String[][] readJobDefinitionProperties(Connection conn, JobDefinitionID id) 
                                                                           throws SQLException {
    
        PreparedStatement ps = null; 
        ResultSet rs = null;
        
        try {
            ps = conn.prepareStatement("SELECT NAME, VAL FROM BC_JOB_DEF_PROPS WHERE JOB_DEFINITION_ID = ?");
            ps.setBytes(1, id.getBytes()); 

            rs = ps.executeQuery();
        
            ArrayList<String[]> properties = new ArrayList<String[]>();
            while (rs.next()) {
                String[] property = new String[2];
                property[0] = rs.getString("NAME");
                property[1] = rs.getString("VAL");
                properties.add(property);
            }
            return properties.toArray(new String[properties.size()][]);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }
        
    private ArrayList<JobDefinition> readJobDefinitionsFromResultSet(Connection conn, ResultSet rs)
                                                                                    throws SQLException {

        ArrayList<JobDefinition> jobDefs = new ArrayList<JobDefinition>();
        JobDefinition def;

        while(rs.next()) {
            JobDefinitionID foundId = JobDefinitionID.parseID(rs.getBytes("ID"));
            String[][] properties = readJobDefinitionProperties(conn, foundId);            
            HashMap<String, HashMap<String, String>> localeTextMap = getLocalizationInfoForJobDefinition(conn, foundId);
            JobParameterDefinition[] parameterDefs = getJobParameterDefinitions(conn, foundId);

            Timestamp ts = rs.getTimestamp("REMOVE_DATE");
            Date remDate = null;
            if (ts != null) {
                remDate = new java.util.Date(ts.getTime());
            }
            
            def = new JobDefinition(foundId ,
                                    new JobDefinitionName(rs.getString("APPLICATION_NAME"),rs.getString("NAME")),
                                    rs.getString("DESCRIPTION"),
                                    parameterDefs,
                                    rs.getInt("RETENTION_PERIOD"),
                                    0, 
                                    remDate,
                                    properties,
                                    localeTextMap);
            jobDefs.add(def);
        }
        rs.close();
        return jobDefs;   
    }

    public ArrayList<JobDefinition> getJobDefinitionsByApplication(String application) 
                                                                      throws SQLException {

        Connection conn = null;
        
        try {
            conn = mDataSource.getConnection();
            
            return getJobDefinitionsByApplication(conn, application);

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        
    }
    
    private ArrayList<JobDefinition> getJobDefinitionsByApplication(Connection conn, String application) 
                                                                                         throws SQLException {

        PreparedStatement ps = null;
        ResultSet res = null;
        
        try {

            ps = conn.prepareStatement(
                    "SELECT ID, NAME, DESCRIPTION, JOB_TYPE, REMOVE_DATE, RETENTION_PERIOD, APPLICATION_NAME " +
                    "FROM BC_JOB_DEFINITION WHERE APPLICATION_NAME = ? AND REMOVE_DATE IS NULL");

            ps.setString(1, application);

            res = ps.executeQuery();

            return readJobDefinitionsFromResultSet(conn, res);

        } finally {
            if (res != null) {
                res.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

}
