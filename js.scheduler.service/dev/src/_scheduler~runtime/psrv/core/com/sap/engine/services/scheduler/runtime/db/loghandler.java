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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.guid.GUID;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.LogIterator;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerLogRecord;
import com.sap.scheduler.runtime.SchedulerLogRecordIterator;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.LogRecord;
import com.sap.tc.logging.Severity;

/**
 * This class implements the database access methods for the management of
 * JobDefinitions, JobParameterDefintions and localizations infos.
 * 
 * This class will is an singleton will will instantiated only once while
 * scheduler-runtime service startup.
 * 
 * @author Thomas Mueller (d040939)
 */

public class LogHandler {
    public static final int SHORT_ARGUMENT_LENGTH = 200;
    // logging and tracing
    private final static Location location = Location.getLocation(JobDefinitionHandler.class);
    private final static Category category = LoggingHelper.SYS_SERVER;
    private DataSource mDataSource = null;
    private Environment mEnv = null;
    // date formatter
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
    

    /**
     * Constructor
     *  
     * @param env the Envorinment
     */
    public LogHandler(Environment env) {
        mEnv = env;
        mDataSource = mEnv.getDataSource();
    }
    
    
    
    /**
     * Add log entry to the database.
     */
    public void addEntry(LogRecord rec, long position, JobID jobid) {
    
        // Mail from Robert Boban:
        // -----------------------------------------------
        // This is also one of the Logging API mistery...
        //
        // If you write a message using 
        //
        // - Location then sourceName == Location name
        // - Category then sourceName == Category name
        // -----------------------------------------------
       
        Connection c = null;
        PreparedStatement psLog = null;
        PreparedStatement psLogArgs = null;
        
        byte[] id = new GUID().toBytes();
        Timestamp msg_date = new Timestamp(rec.getTime().getTime());
        
        // Make sure we do not write an emty String as this is 
        // not supported by OpenSQL
        //
        String message = rec.getMsgClear();
        if ("".equals(message)) {
            message = null;
        }

        try {
            c = mDataSource.getConnection();
            
            // turn off auto commit
            c.setAutoCommit(false);
            
            StringBuilder stmt = new StringBuilder();
            stmt.append("INSERT INTO BC_JOB_LOG (ID, POS, APPLICATION, CATEGORY, USERID, MESSAGE, ");
            stmt.append("MSG_TIME, SEVERITY, JOBID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            
            psLog = c.prepareStatement(stmt.toString());
            psLog.setBytes(1, id);
            psLog.setLong(2, position);
            psLog.setString(3, rec.getApplication());
            psLog.setString(4, rec.getSourceName());
            psLog.setString(5, rec.getUser());
            psLog.setString(6, message);
            psLog.setTimestamp(7, msg_date);
            psLog.setInt(8, rec.getSeverity());
            psLog.setBytes(9, jobid.getBytes());
            // flush
            psLog.executeUpdate();
            
            List args = rec.getArgs();
            
            if (args.size() != 0) {
                String stmtStr = "INSERT INTO BC_JOB_LOG_ARGS (LOG_ID, ARG_POS, ARG_VALUE, ARG_LONG_VALUE) VALUES (?, ?, ?, ?)";
                psLogArgs = c.prepareStatement(stmtStr);
                
                for (short i = 0; i < args.size(); i++) {
                    String arg = (String)args.get(i);
                    psLogArgs.setBytes(1, id);
                    psLogArgs.setShort(2, i);
                    
                    if (arg.length() <= SHORT_ARGUMENT_LENGTH ) {
                        psLogArgs.setString(3, arg);
                        psLogArgs.setString(4, null);
                    } else { 
                        psLogArgs.setString(3, null);
                        psLogArgs.setString(4, arg);
                    }
                    psLogArgs.addBatch();
                } // for
                // flush
                psLogArgs.executeBatch();
            } // if
            
            c.commit();
            
        } catch (SQLException se) {
            // this is an error because we loose the log entry
            category.logThrowableT(Severity.ERROR, location, "Unable to log error message from job due to database exception.", se);
        } finally {
            try {
                if (psLog != null) {
                    psLog.close();
                }
                if (psLogArgs != null) {
                    psLogArgs.close();
                }                
                if (c != null) {
                    c.close();
                }
            } catch (SQLException sqle) {
                location.traceThrowableT(Severity.ERROR, "Non critical error while closing PreparedStatements or DBConnection.", sqle);
            }
            
        }
    }
    
    
    
    /**
     * Delete a log from the log database
     * 
     *  @param jobId job id for which to delete the job
     *  
     *  @excpetion NoSuchJobException if a log was requested for a job that
     *  does not exist
     *  @excpetion SQLException in case of a database exception
     */
    public void deleteLog(JobID jobId) throws NoSuchJobException, SQLException {
        Job j = mEnv.getJobQueryHandler().getJob(jobId);
        if (j == null) {
            throw new NoSuchJobException("There is no job with id \"" + jobId.toString() + "\".");
        }
        
        Connection c = null;
        PreparedStatement psLog = null;
        PreparedStatement psLogArgs = null;
        
        try {
            c = mDataSource.getConnection();
            // turn off auto commit
            c.setAutoCommit(false);
            
            String stmt = "DELETE FROM BC_JOB_LOG_ARGS WHERE LOG_ID IN (SELECT ID FROM BC_JOB_LOG WHERE JOBID = ?)";
            psLogArgs = c.prepareStatement(stmt);
            psLogArgs.setBytes(1, jobId.getBytes()); 
            // flush
            psLogArgs.executeUpdate();
            
            // delete records
            //
            psLog = c.prepareStatement("DELETE FROM BC_JOB_LOG WHERE JOBID = ?");
            psLog.setBytes(1, jobId.getBytes()); 
            // flush
            psLog.executeUpdate();

            c.commit();
            
        } finally {
            try {
                if (psLog != null) {
                    psLog.close();
                }
                if (psLogArgs != null) {
                    psLogArgs.close();
                }                
                if (c != null) {
                    c.close();
                }
            } catch (SQLException sqle) {
                location.traceThrowableT(Severity.ERROR, "Non critical error while closing PreparedStatements or DBConnection.", sqle);
            }
        }
    }

    
    /**
     * Get log file for the named category from the database. These method returns
     * a LogIterator with returns as chunk (nextChunk()) a String.
     * 
     * @param jobid Job id of the log
     * @return the log or null if there is no such job
     * @exception SQLException if there was a technical problem
     */ 
    public LogIterator getLog(JobID jobid, LogIterator lit, int fetchSize) throws NoSuchJobException, SQLException {
        if (lit == null) {
            lit = new LogIterator(); // initial call
        }
        
        return (LogIterator)getLogInternal(jobid, lit, null, fetchSize);
    }
    
    /**
     * Get log file for the named category from the database. These method returns
     * a SchedulerLogRecordIterator with returns as chunk (nextChunk()) a SchedulerLogRecordIterator[].
     * 
     * @param jobid Job id of the log
     * @return the log or null if there is no such job
     * @exception SQLException if there was a technical problem
     */  
    public SchedulerLogRecordIterator getLogRecords(JobID jobid, SchedulerLogRecordIterator lrit, int fetchSize) throws NoSuchJobException, SQLException {
        if (lrit == null) {
            lrit = new SchedulerLogRecordIterator(); // initial call
        }
        
        return (SchedulerLogRecordIterator)getLogInternal(jobid, null, lrit, fetchSize);
    }
    
    

    private Object getLogInternal(JobID jobid, LogIterator lit, SchedulerLogRecordIterator lrit, int fetchSize) throws NoSuchJobException, SQLException {        
        Job j = mEnv.getJobQueryHandler().getJob(jobid);
        if (j == null) {
            throw new NoSuchJobException("There is no job with id \"" + jobid.toString() + "\".");
        }

        // user error
        if (lit != null) {
            if (!lit.hasMoreChunks()) {
                return lit;
            }
        } else {
            if (!lrit.hasMoreChunks()) {
                return lrit;
            }
        }

        Connection conn = mDataSource.getConnection();
        PreparedStatement ps = null;
        StringBuilder selectStr = new StringBuilder();
        selectStr.append("SELECT ID, APPLICATION, CATEGORY, LOCATION, USERID, MESSAGE, MSG_TIME, SEVERITY, POS ");
        selectStr.append("FROM BC_JOB_LOG WHERE JOBID = ? AND POS >= ? ORDER BY POS");
        ps = conn.prepareStatement(selectStr.toString());

        if (fetchSize != 0) {
            // retrieve one line more in order to set hasMoreChunks() on the
            // iterator correctly
            ps.setMaxRows(fetchSize + 1);
        }
        ps.setBytes(1, jobid.getBytes());
        if (lit != null) {
            ps.setLong(2, lit.getPos());
        } else {
            ps.setLong(2, lrit.getPos());
        }
        ResultSet rs = ps.executeQuery();

        // Read log entries
        //
        int records = 0;
        
        StringBuffer log = new StringBuffer();
        ArrayList<SchedulerLogRecord> logRecords = null;
        
        if (lit != null) {
            log = new StringBuffer();
        } else {
            logRecords = new ArrayList<SchedulerLogRecord>();
        }
        

        // set to position below if there are more log entries after this
        // call
        if (lit != null) {
            lit.setPos(-1);
        } else {
            lrit.setPos(-1);
        }

        /*
         * byte[] id, String application, String category, String location, 
         * String userid, String message, Timestamp msg_time, Integer severity, Long pos
         */
        try {
            while (rs.next()) {
                byte[] id = rs.getBytes("ID");
                String msg = rs.getString("MESSAGE");
                Timestamp time = rs.getTimestamp("MSG_TIME");
                String user = rs.getString("USERID");
                Integer severity = new Integer(rs.getInt("SEVERITY"));
                long pos = rs.getLong("POS");

                if (records >= fetchSize) {
                    // this is the first record that has to be read on the next
                    // call to getLog(), so we just keep the position of the
                    // record and don't add it to the text
                    if (lit != null) {
                        lit.setPos(pos);
                    } else {
                        lrit.setPos(pos);
                    }
                    break;
                }

                // get arguments for log record
                //

                String[] args = getLogArguments(conn, id);
                if (msg == null) {
                    msg = "";
                }
                
                if (lit != null) {
                    formatEntry(log, time, msg, user, severity, args);
                } else {
                    formatEntryToLogRecord(logRecords, time, msg, user, severity, args);
                }
                records++;
            } // while

            if (records > 0) {
                // found logfile
                if (lit != null) {
                    lit.setLog(log.toString());
                    return lit;
                } else {
                    lrit.setLogRecords((SchedulerLogRecord[]) logRecords.toArray(new SchedulerLogRecord[logRecords.size()]));
                    return lrit;
                }

            } else {
            	if (lit != null) {
	                // no record found --> we return the empty String
	                lit.setLog("");
	                return lit;
            	} else {
            		lrit.setLogRecords(new SchedulerLogRecord[0]);
            		return lrit;
            	}
            }
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
    
    
    private String[] getLogArguments(Connection conn, byte[] recordId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> args = null;

        try {
            StringBuilder stmt = new StringBuilder();
            stmt.append("SELECT ARG_POS, ARG_VALUE, ARG_LONG_VALUE FROM BC_JOB_LOG_ARGS ");
            stmt.append("WHERE LOG_ID = ? ORDER BY ARG_POS");
            ps = conn.prepareStatement(stmt.toString());
            
            ps.setBytes(1, recordId);
            rs = ps.executeQuery();

            while (rs.next()) {
                if (args == null) {
                    args = new ArrayList<String>();
                }

                int pos = rs.getInt("ARG_POS");
                String arg_value = rs.getString("ARG_VALUE");
                if (arg_value == null) {
                    arg_value = rs.getString("ARG_LONG_VALUE");
                }

                args.add(arg_value);
            }
            if (args == null) {
                return new String[0];
            } else {
                return (String[]) args.toArray(new String[args.size()]);
            }

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
    // ----------- Private helper methods --------------------------------------
    // -------------------------------------------------------------------------
    private void formatEntry(StringBuffer buf, Timestamp ts, String msg, String userid, Integer severity, String[] av) {
        
        // {<num>} will be replaced by argument
        //        
        // timestamp
        //
        buf.append("[").append(dateFormatter.format(ts)).append("] ");
        
        // severity
        //
        buf.append(severityIdToString(severity));
        
        // the rest of the string
        //
        if (av.length > 0) {
        
            int pos=0;
            while (pos < msg.length()) {
                if (msg.charAt(pos) != '{') {
                    // add to result and continue
                    buf.append(msg.charAt(pos));
                    pos++;
                    continue;
                }
                pos++;
                String numStr = parseNumber(msg, pos);
                if (numStr == null) {
                    // no number here, just ignore the '{'
                    //
                    buf.append("{");
                    continue;
                }
                pos += numStr.length();
                int num = Integer.parseInt(numStr);
                
                // found number, cursor must be on '}'
                //
                if (!(pos < msg.length())) {
                    // enf of message before }
                    buf.append("{" + numStr);
                    continue;
                } 
                if (msg.charAt(pos) == '}')
                {
                    // substitude {<num>} 
                    if (num < av.length) {
                        buf.append(av[num]);
                    } else {
                        // there was an error while creating this message
                        //
                        buf.append("(null)");
                    }                    
                } else {
                    buf.append("{" + numStr + msg.charAt(pos));
                }
                
                pos++;
                
            }
        
        } else {
            // this is a plain message without arguments
            //
            buf.append(msg);
        }
        buf.append("\n");
    }
    
    
    private void formatEntryToLogRecord(ArrayList<SchedulerLogRecord> logRecordsList, Timestamp ts, String msg, String userid, Integer sev, String[] av) {
        StringBuffer message = new StringBuffer();
        Date date = null;
        int severity = 0;

        // {<num>} will be replaced by argument
        //

        // timestamp
        //
        date = new Date(ts.getTime());

        // severity
        //
        severity = sev.intValue();

        // the rest of the string
        //
        if (av.length > 0) {

            int pos = 0;
            while (pos < msg.length()) {
                if (msg.charAt(pos) != '{') {
                    // add to result and continue
                    message.append(msg.charAt(pos));
                    pos++;
                    continue;
                }
                pos++;
                String numStr = parseNumber(msg, pos);
                if (numStr == null) {
                    // no number here, just ignore the '{'
                    //
                    message.append("{");
                    continue;
                }
                pos += numStr.length();
                int num = Integer.parseInt(numStr);

                // found number, cursor must be on '}'
                //
                if (!(pos < msg.length())) {
                    // enf of message before }
                    message.append("{" + numStr);
                    continue;
                }
                if (msg.charAt(pos) == '}') {
                    // substitude {<num>} 
                    if (num < av.length) {
                        message.append(av[num]);
                    } else {
                        // there was an error while creating this message
                        //
                        message.append("(null)");
                    }
                } else {
                    message.append("{" + numStr + msg.charAt(pos));
                }

                pos++;

            }

        } else {
            // this is a plain message without arguments
            //
            message.append(msg);
        }

        SchedulerLogRecord lr = new SchedulerLogRecord(message.toString(), date, severity);
        logRecordsList.add(lr);
    }
    
    
    private String parseNumber(String msg, int pos) {
        String number = "";
        int i = pos;

        while (i < msg.length() && Character.isDigit(msg.charAt(i))) {
            number += msg.charAt(i);
            i++;
        }

        if ("".equals(number)) {
            // no digit
            return null;
        }
        return number;
    }
    
    
    private String severityIdToString(Integer id) {        
        if (id== null) {
            return "<NULL> ";
        }
        switch(id.intValue()) {
            case 100:
                return "DEBUG  ";
            case 300:
                return "INFO   ";
            case 400:
                return "WARNING";
            case 500:
                return "ERROR  ";
            case 600:
                return "FATAL  ";
            default:
                return "UNKNOWN";
        }
    }

}
