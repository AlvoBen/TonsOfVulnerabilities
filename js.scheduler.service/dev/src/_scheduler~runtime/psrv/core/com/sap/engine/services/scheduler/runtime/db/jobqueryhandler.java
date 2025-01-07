package com.sap.engine.services.scheduler.runtime.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;

import javax.sql.DataSource;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.guid.GUID;
import com.sap.guid.GUIDFormatException;
import com.sap.scheduler.api.SchedulerTaskID;
import com.sap.scheduler.runtime.Job;
import com.sap.scheduler.runtime.JobDefinitionID;
import com.sap.scheduler.runtime.JobFilter;
import com.sap.scheduler.runtime.JobID;
import com.sap.scheduler.runtime.JobIllegalStateException;
import com.sap.scheduler.runtime.JobIterator;
import com.sap.scheduler.runtime.JobStatus;
import com.sap.scheduler.runtime.NoSuchJobException;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SchedulerRuntimeException;
import com.sap.scheduler.runtime.JobIterator.StateDescriptor;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class provides the functionality for querying the BC_JOB_JOBS and
 * BC_JOB_JOBS_CO tables.
 * 
 * @author Dirk Marwinski
 * 
 */
public class JobQueryHandler {

    /**
     * Initialization of the location for SAP logging.
     */
    private final static Location location = Location
            .getLocation(JobQueryHandler.class);

    /**
     * Initialization of the category for SAP logging.
     */
    private final static Category category = LoggingHelper.SYS_SERVER;

    public static final String TABLE_0_NAME = "BC_JOB_JOBS";

    public static final String TABLE_1_NAME = "BC_JOB_JOBS_CO";

    // we need this for database table sorting as the behavior of null is
    // undefined (for some databases it is the 1st day of creation, for others
    // it is doomsday. We arbitrarily define the end of the world to be on
    // April 1st, 5000, 6:42:00 UTC time (which will also make it to the DB)
    //
    public static final Timestamp MAX_TIME_TIMESTAMP;

    public static final Timestamp SHORTLY_BEFORE_MAX_TIMESTAMP;

    /**
     * Protect the system by not allowing the construction of overly huge data
     * structures.
     * <p>
     * Note: this needs to be reflected and be described and defined as a side
     * effect in the public API.
     */
    public static final int MAX_FETCH_SIZE = 10000;

    static {
        Calendar tmpCal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        tmpCal1.clear();
        tmpCal1.set(5000, 3, 1, 6, 42, 0);
        MAX_TIME_TIMESTAMP = new Timestamp(tmpCal1.getTimeInMillis());

        Calendar tmpCal2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        tmpCal2.clear();
        tmpCal2.set(5000, 2, 1, 6, 42, 0);
        SHORTLY_BEFORE_MAX_TIMESTAMP = new Timestamp(tmpCal2.getTimeInMillis());

    }

    // allow some

    // data source name
    //
    private DataSource mDataSource;

    private static JobQueryHandler sJobQueryHandler;

    public static JobQueryHandler Instance() {
        return sJobQueryHandler;
    }

    public JobQueryHandler(Environment env) throws ServiceException {
        mDataSource = env.getDataSource();
        sJobQueryHandler = this;
    }

    /**
     * This method returns the JobInformation object from the database.
     * 
     * @param jobid
     *            JobID for the job
     * @return Job object or null if there is no such job
     * @SQLException in case there was an error accessing the Database
     */
    public Job getJob(JobID jobid) throws SQLException {
        JobFilter filter = new JobFilter();
        filter.setJobId(jobid);

        JobIterator iter = getJobs(filter, null, 1);

        Job[] jobs = iter.nextChunk();

        if (jobs.length == 0) {
            // no job with given id
            return null;
        } else {
            return jobs[0];
        }
    }

    /**
     * Get job object from single row.
     * 
     * @param res
     * @return
     */
    private Job getNextRow(ResultSet res) throws SQLException {

        if (res == null) {
            return null;
        }

        if (!res.next()) {
            // no more rows here
            return null;
        }

        JobID id = JobID.parseID(res.getBytes(1));
        SchedulerID schedId = SchedulerID.parseID(res.getBytes(2));
        JobStatus status = JobStatus.valueOf(res.getShort(3));
        JobID parentId = JobID.parseID(res.getBytes(4));

        java.util.Date started = null;
        Timestamp ts = res.getTimestamp(5);
        if (ts != null && !ts.after(SHORTLY_BEFORE_MAX_TIMESTAMP)) {
            started = new java.util.Date(ts.getTime());
        }

        java.util.Date ended = null;
        Timestamp tsEnded = res.getTimestamp(6);
        if (tsEnded != null && !tsEnded.after(SHORTLY_BEFORE_MAX_TIMESTAMP)) {
            ended = new java.util.Date(tsEnded.getTime());
        }

        JobDefinitionID jobdefID = JobDefinitionID.parseID(res.getBytes(7));
        String vendorData = res.getString(8);
        String node = res.getString(9);
        short returnCode = res.getShort(10);
        String user = res.getString(11);
        String name = res.getString(12);
        boolean cancel_request = res.getShort(13) == 0 ? false : true;
        int retentionPeriod = res.getInt(14);
        java.util.Date submitted = new java.util.Date(res.getTimestamp(15)
                .getTime()); // must not be null
        SchedulerTaskID schedTaskID = SchedulerTaskID.parseID(res.getBytes(16));
        long cpu = res.getLong(17);
        long memoryAllocation = res.getLong(18);

        return new Job(id, jobdefID, schedId, name, status, started, ended,
                submitted, node, returnCode, user, parentId, vendorData,
                cancel_request, retentionPeriod, schedTaskID, cpu,
                memoryAllocation);

    }    

    // TODO, check whether ordering is according to db ordering    


    private JobIterator readResult(ResultSet resTab1, ResultSet resTab2,
            JobIterator iter, int fetchSize) throws SQLException {

        int i = 0;
        ArrayList<Job> jobs = new ArrayList<Job>();

        Job jTab1 = getNextRow(resTab1);
        Job jTab2 = getNextRow(resTab2);

        // extract one more row from the result sets than we need to return
        // (in case there is a next chunk)
        //
        while (i <= fetchSize) {

            i++;

            if (jTab1 == null && jTab2 == null) {

                // no more jobs, probably last chunk
                //
                break;
            }

            if (jTab1 == null) {

                jobs.add(jTab2);
                jTab2 = getNextRow(resTab2);
            } else if (jTab2 == null) {

                jobs.add(jTab1);
                jTab1 = getNextRow(resTab1);
            } else {
                if (jTab1.compareTo(jTab2) > 0) {//Comparison of jobs moved into a job(and fixed)
                    jobs.add(jTab1);
                    jTab1 = getNextRow(resTab1);
                } else {
                    jobs.add(jTab2);
                    jTab2 = getNextRow(resTab2);
                }
            }
        }

        if (fetchSize > 0 && jobs.size() > fetchSize) {
            // there are more elements to fetch from the database, so take
            // the last element and put it into the iterator (do not return
            // it)
            //
            Job j = (Job) jobs.remove(jobs.size() - 1);
            iter.getStateDescriptor().setJobId(j.getId());
            iter.getStateDescriptor().setEndDate(j.getEndDate());
        } else {
            iter.getStateDescriptor().setJobId(null);
            iter.getStateDescriptor().setEndDate(null);
        }

        iter.setJobs((Job[]) jobs.toArray(new Job[jobs.size()]));
        return iter;
    }

    @SuppressWarnings("unchecked")
    private PreparedStatement createStatement(Connection conn,
            JobFilter filter, JobIterator iter) throws SQLException {
        ArrayList values = new ArrayList();

        String tableName;
        StateDescriptor sd = iter.getStateDescriptor();

        if (sd.getTable() == StateDescriptor.TABLE_JOBS) {
            tableName = TABLE_0_NAME;
        } else if (sd.getTable() == StateDescriptor.TABLE_JOBS_CO) {
            tableName = TABLE_1_NAME;
        } else {
            throw new IllegalArgumentException("Illegal table id: "
                    + sd.getTable());
        }

        StringBuffer query = new StringBuffer();
        query.append("SELECT ID, SCHEDULER_ID, JOB_STATUS, PARENT_ID, ");
        query.append("STARTED, ENDED, JOB_DEFINITION_ID, VENDOR_DATA, NODE, ");
        query.append("RETURN_CODE, USER_ID, NAME, CANCEL_REQUEST, RETENTION_PERIOD, ");
        query.append("SUBMITTED, TASK_ID, CPU, ALLOCATION FROM ").append(
                tableName);

        boolean addAnd = false;

        // add start date range
        //
        if (filter.getStartedFrom() != null) {
            addAnd(query, addAnd);
            query.append("STARTED > ?");
            values.add(filter.getStartedFrom());
            addAnd = true;
        }

        if (filter.getStartedTo() != null) {
            addAnd(query, addAnd);
            query.append("STARTED < ?");
            values.add(filter.getStartedTo());
            addAnd = true;
        }

        if (filter.getEndedFrom() != null) {
            addAnd(query, addAnd);
            query.append("ENDED > ?");
            values.add(filter.getEndedFrom());
            addAnd = true;
        }

        if (filter.getEndedTo() != null) {
            addAnd(query, addAnd);
            query.append("ENDED < ?");
            values.add(filter.getEndedTo());
            addAnd = true;
        }

        if (filter.getVendorData() != null) {
            addAnd(query, addAnd);
            query.append("VENDOR_DATA = ?");
            values.add(filter.getVendorData());
            addAnd = true;
        }

        if (filter.getJobStatus() != null) {
            // need to add this as a literal value in order not to confuse the
            // database optimizer.
            // This will create too many prepared statements but we cannot
            // avoid this at the moment.
            //
            addAnd(query, addAnd);
            query.append("JOB_STATUS = " + filter.getJobStatus().value());
            addAnd = true;
        }

        if (filter.getParentId() != null) {
            addAnd(query, addAnd);
            query.append("PARENT_ID = ?");
            values.add(filter.getParentId());
            addAnd = true;
        }

        if (filter.getJobId() != null) {
            addAnd(query, addAnd);
            query.append("ID = ?");
            values.add(filter.getJobId());
            addAnd = true;
        }

        if (filter.getJobDefinitionId() != null) {
            addAnd(query, addAnd);
            query.append("JOB_DEFINITION_ID = ?");
            values.add(filter.getJobDefinitionId());
            addAnd = true;
        }

        if (filter.getNode() != null) {
            addAnd(query, addAnd);
            query.append("NODE = ?");
            values.add(filter.getNode());
            addAnd = true;
        }

        if (filter.getReturnCode() != null) {
            addAnd(query, addAnd);
            query.append("RETURN_CODE = ?");
            values.add(filter.getReturnCode());
            addAnd = true;
        }

        if (filter.getScheduler() != null) {
            addAnd(query, addAnd);
            query.append("SCHEDULER_ID = ?");
            values.add(filter.getScheduler());
            addAnd = true;
        }

        if (filter.getNode() != null) {
            addAnd(query, addAnd);
            query.append("NODE = ?");
            values.add(filter.getNode());
            addAnd = true;
        }

        if (filter.getSchedulerTaskId() != null) {
            addAnd(query, addAnd);
            query.append("TASK_ID = ?");
            values.add(filter.getSchedulerTaskId());
            addAnd = true;
        }

        if (filter.getName() != null) {
            addAnd(query, addAnd);
            query.append("NAME = ?");
            values.add(filter.getName());
            addAnd = true;
        }

        if (filter.getUserId() != null) {
            addAnd(query, addAnd);
            query.append("USER_ID = ?");
            values.add(filter.getUserId());
            addAnd = true;
        }

        // -------------------------------------------------------------------
        //
        // the if/else statement is executed only for the second and
        // following chunks. On the first chunk the job id and the end date
        // of the state descriptor are null.
        //
        // An end date of NULL means that the job has not yet ended. This is 
        // represented as MAX_TIME_TIMESTAMP in the database. 
        //
        
        if (sd.getJobId() != null && sd.getEndDate() != null) {
            // at this point in time we have an end date which is before 
            // MAX_TIME_TIMESTAMP (in other words: the job has ended).
            //

            addAnd(query, addAnd);
            query.append("( (ID <= ? AND ENDED = ?) OR ENDED < ? )");
            
            values.add(sd.getJobId());
            values.add(sd.getEndDate());
            values.add(sd.getEndDate());

        } else if (sd.getJobId() != null) {
            
            // We are still selecting jobs which have not yet ended. We may 
            // now continue selecting jobs which have not ended but also need
            // to select those that have been ended
            //


            addAnd(query, addAnd);
            query.append("( (ID <= ? AND ENDED > ?) OR ENDED < ? )");
            values.add(sd.getJobId());
            values.add(SHORTLY_BEFORE_MAX_TIMESTAMP);
            values.add(SHORTLY_BEFORE_MAX_TIMESTAMP);
        }

        query.append(" ORDER BY ENDED DESC, ID DESC");

        if (location.beDebug()) {
            location.debugT("About to execute query: " + query.toString());
        }

        // Create prepared statement and add parameters to query

        PreparedStatement ps = conn.prepareStatement(query.toString());

        if (sd.getTable() == StateDescriptor.TABLE_JOBS_ALL) {
            // if we have to query both tables we add the values twice
            values.addAll(values);
        }

        int size = values.size();
        for (int i = 1; i <= size; i++) {
            Object value = values.remove(0);

            if (value instanceof java.util.Date) {
                java.util.Date dvalue = (java.util.Date) value;
                ps.setTimestamp(i, new java.sql.Timestamp(dvalue.getTime()));
            } else if (value instanceof Timestamp) {
                ps.setTimestamp(i, (Timestamp)value);
            }else if (value instanceof String) {
                ps.setString(i, (String) value);
            } else if (value instanceof JobID) {
                JobID jvalue = (JobID) value;
                ps.setBytes(i, jvalue.getBytes());
            } else if (value instanceof JobDefinitionID) {
                JobDefinitionID jvalue = (JobDefinitionID) value;
                ps.setBytes(i, jvalue.getBytes());
            } else if (value instanceof Short) {
                ps.setShort(i, ((Short) value).shortValue());
            } else if (value instanceof JobStatus) {
                JobStatus s = (JobStatus) value;
                ps.setShort(i, s.value());
            } else if (value instanceof SchedulerID) {
                SchedulerID jvalue = (SchedulerID) value;
                ps.setBytes(i, jvalue.getBytes());
            } else if (value instanceof SchedulerTaskID) {
                SchedulerTaskID jvalue = (SchedulerTaskID) value;
                ps.setBytes(i, jvalue.getBytes());
            } else {
                throw new IllegalArgumentException(
                        "Illegal type for job filter: "
                                + value.getClass().getName());
            }
        } // inner for

        return ps;
    }
    
    public int updateEndedNullValues() throws SQLException {
        
        Connection conn = null;

        PreparedStatement updateStmt = null;
        String stmt = "UPDATE BC_JOB_JOBS SET ENDED = ? WHERE ENDED IS NULL";

        try {
            conn = obtainConnection();
            updateStmt = conn.prepareStatement(stmt);
            updateStmt.setTimestamp(1, MAX_TIME_TIMESTAMP);
            
            return updateStmt.executeUpdate();
        } finally {
            if (updateStmt != null) {
                updateStmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    private void addAnd(StringBuffer buf, boolean addAnd) {
        if (addAnd) {
            buf.append(" AND ");
        } else {
            buf.append(" WHERE ");
        }
    }

    public JobIterator getJobs(JobFilter filter, JobIterator iter, int fetchSize)
            throws SQLException {
        Connection conn = null;
        try {
            conn = obtainConnection();
            return getJobs(conn, filter, iter, fetchSize);
        } finally {
            closeConnection(conn);
        }
    }

    public JobIterator getAllJobs(JobIterator jit, int fetchSize)
            throws SQLException {
        JobFilter filter = new JobFilter();
        return getJobs(filter, jit, fetchSize);
    }

    private JobIterator getJobs(Connection conn, JobFilter filter,
            JobIterator iter, int fetchSize) throws SQLException {

        if (location.beDebug()) {
            StringBuilder answer = new StringBuilder();
            answer.append("Received the following query:\n");
            if (filter != null) {
                answer.append("Filter: " + filter.toString() + "\n");
            } else {
                answer.append("query without filter\n");
            }
            if (iter != null) {
                answer.append("Iterator: " + iter.getStateDescriptor().toString());
            } else {
                answer.append("No iterator for query");
            }
            location.debugT(answer.toString());
        }

        // check for the fetch size and make sure it is not too big
        //

        if (fetchSize == 0 || fetchSize > MAX_FETCH_SIZE) {
            fetchSize = MAX_FETCH_SIZE;
        }

        // -----------------------------------------------------------------

        // check for client error (e.g. getJobs was invokes although
        // hasMoreChunks() has returned false
        //
        if (iter != null && !iter.hasMoreChunks()) {
            return iter;
        }

        // -----------------------------------------------------------------

        // this is the first call to getJobs
        if (iter == null) {
            iter = new JobIterator();
        }

        StateDescriptor sd = iter.getStateDescriptor();
        JobStatus queryStatus = filter.getJobStatus();

        // -----------------------------------------------------------------
        // Simple case, we just need to query one single table, either
        // BC_JOB_JOBS or BC_JOB_JOBS_CO
        //
        if (queryStatus != null) {

            String tableName;
            if (queryStatus.equals(JobStatus.COMPLETED)) {

                if (location.beDebug()) {
                    location.debugT("Creating query for table " + TABLE_1_NAME
                            + ". Selecting COMPLETED jobs.");
                }
                tableName = TABLE_1_NAME;
                sd.setTable(StateDescriptor.TABLE_JOBS_CO);
            } else {

                if (location.beDebug()) {
                    location.debugT("Creating query for table " + TABLE_0_NAME
                            + ". Selecting jobs which are in status "
                            + queryStatus.toString());
                }
                tableName = TABLE_0_NAME;
                sd.setTable(StateDescriptor.TABLE_JOBS);
            }

            PreparedStatement ps = null;

            try {
                ps = createStatement(conn, filter, iter);

                if (fetchSize > 0) {
                    ps.setMaxRows(fetchSize + 1);
                }

                ResultSet res = ps.executeQuery();
                iter = readResult(res, null, iter, fetchSize);

            } finally {

                // from java.sql.Statement javadoc: "Note: A Statement object
                // is automatically closed when it is garbage collected. When a
                // Statement object is closed, its current ResultSet object, if
                // one exists, is also closed.
                //
                if (ps != null) {
                    ps.close();
                }
            }

            if (location.beDebug()) {
                location
                        .debugT("Query on table "
                                + tableName
                                + " returned "
                                + iter.getJobs().length
                                + " records. Fetch Size was "
                                + fetchSize
                                + ". There are "
                                + (iter.getStateDescriptor().getJobId() == null ? "no more"
                                        : "more")
                                + " records in that table to fetch.");
            }

        } else {

            // -------------------------------------------------------------
            // need to query and merge two tables
            //
            if (location.beDebug()) {
                location.debugT("Creating query for both tables: "
                        + TABLE_0_NAME + " and " + TABLE_1_NAME);
            }

            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;

            try {
                // query BC_JOB_JOBS first
                //
                sd.setTable(StateDescriptor.TABLE_JOBS);
                ps1 = createStatement(conn, filter, iter);

                if (fetchSize > 0) {
                    ps1.setMaxRows(fetchSize + 1);
                }

                ResultSet res1 = ps1.executeQuery();

                // then query BC_JOB_JOBS_CO table
                //
                sd.setTable(StateDescriptor.TABLE_JOBS_CO);
                ps2 = createStatement(conn, filter, iter);

                if (fetchSize > 0) {
                    ps2.setMaxRows(fetchSize + 1);
                }

                ResultSet res2 = ps2.executeQuery();

                iter = readResult(res1, res2, iter, fetchSize);
            } finally {
                if (ps1 != null) {
                    ps1.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
            }

            if (location.beDebug()) {
                location
                        .debugT("Query on "
                                + TABLE_0_NAME
                                + " and "
                                + TABLE_1_NAME
                                + " returned "
                                + iter.getJobs().length
                                + " records. Fetch Size was "
                                + fetchSize
                                + ". There are "
                                + (iter.getStateDescriptor().getJobId() == null ? "no more"
                                        : "more")
                                + " records in that table to fetch.");
            }

        }

        if (iter.getStateDescriptor().getJobId() == null) {
            iter.getStateDescriptor().setMoreChunks(false);
        }
        return iter;
    }

    // -------------------------------------------------------------------------
    // ----------- Remove-methods to remove a single job from DB ---------------
    // -------------------------------------------------------------------------

    /**
     * This method removes a job object from the database
     * 
     * @param jobid
     *            the job id
     */
    public void removeJob(JobID jobid) throws NoSuchJobException,
            JobIllegalStateException, SQLException {
        removeJob(jobid, false);
    }

    /**
     * This method removes a job object from the database
     * 
     * @param jobid
     *            the job id
     * @param force
     *            if true removes the entry in any case
     */
    public void removeJob(JobID jobId, boolean force)
            throws NoSuchJobException, JobIllegalStateException, SQLException {
        Connection c = null;

        PreparedStatement psLogArgs = null;
        PreparedStatement psLog = null;
        PreparedStatement psJobArgs = null;

        try {
            c = obtainConnection();

            // turn off auto commit (everything done in one tx)
            c.setAutoCommit(false);

            // delete from job job-tables
            if (force) {
                removeJobForce(c, jobId);
            } else {
                removeJob(c, jobId);
            }

            // delete job log arguments
            String stmtLogArgs = "DELETE FROM BC_JOB_LOG_ARGS WHERE LOG_ID IN (SELECT ID FROM BC_JOB_LOG WHERE JOBID = ?)";
            // delete job log entries
            String stmtLog = "DELETE FROM BC_JOB_LOG WHERE JOBID = ?";
            // delete job arguments
            String stmtJobArgs = "DELETE FROM BC_JOB_JOB_ARGS WHERE JOB_ID = ?";

            psLogArgs = c.prepareStatement(stmtLogArgs);
            psLog = c.prepareStatement(stmtLog);
            psJobArgs = c.prepareStatement(stmtJobArgs);

            psLogArgs.setBytes(1, jobId.getBytes());
            psLog.setBytes(1, jobId.getBytes());
            psJobArgs.setBytes(1, jobId.getBytes());

            psLogArgs.executeUpdate();
            psLog.executeUpdate();
            psJobArgs.executeUpdate();

            c.commit();

        } finally {
            try {
                if (psLogArgs != null) {
                    psLogArgs.close();
                }
                if (psLog != null) {
                    psLog.close();
                }
                if (psJobArgs != null) {
                    psJobArgs.close();
                }
            } catch (SQLException e) {
                location
                        .traceThrowableT(
                                Severity.ERROR,
                                "Non critical error while closing statements (but should not happen)",
                                e);
            }

            closeConnection(c);
        }
    }

    protected void removeJobForce(Connection c, JobID jobId)
            throws NoSuchJobException, SQLException {
        PreparedStatement psJob = null;
        PreparedStatement psJobCo = null;

        String stmtJob = "DELETE FROM BC_JOB_JOBS WHERE ID = ?";
        String stmtJobCo = "DELETE FROM BC_JOB_JOBS_CO WHERE ID = ?";

        try {
            psJob = c.prepareStatement(stmtJob);
            psJob.setBytes(1, jobId.getBytes());
            int updateCount = psJob.executeUpdate();
            if (updateCount == 1) {
                return;
            }

            // ----
            psJobCo = c.prepareStatement(stmtJobCo);
            psJobCo.setBytes(1, jobId.getBytes());
            updateCount = psJobCo.executeUpdate();
            if (updateCount == 1) {
                return;
            }

            // job not deleted (because not found)
            throw new NoSuchJobException("Job with id '" + jobId.toString()
                    + "' could not be found.");

        } finally {
            if (psJob != null) {
                psJob.close();
            }
            if (psJobCo != null) {
                psJobCo.close();
            }
        }
    }

    private void removeJob(Connection c, JobID jobId)
            throws NoSuchJobException, JobIllegalStateException, SQLException {
        PreparedStatement psJob = null;
        PreparedStatement psJobCo = null;

        StringBuilder stmtJob = new StringBuilder();
        stmtJob.append("DELETE FROM BC_JOB_JOBS WHERE ID = ? AND ");
        stmtJob
                .append("(JOB_STATUS = ? OR JOB_STATUS = ? OR JOB_STATUS = ? OR JOB_STATUS = ?)");

        StringBuilder stmtJobCo = new StringBuilder();
        stmtJobCo.append("DELETE FROM BC_JOB_JOBS_CO WHERE ID = ? AND ");
        stmtJobCo
                .append("(JOB_STATUS = ? OR JOB_STATUS = ? OR JOB_STATUS = ? OR JOB_STATUS = ?)");

        try {
            psJob = c.prepareStatement(stmtJob.toString());
            psJob.setBytes(1, jobId.getBytes());
            psJob.setShort(2, JobStatus.UNKNOWN.value());
            psJob.setShort(3, JobStatus.COMPLETED.value());
            psJob.setShort(4, JobStatus.ERROR.value());
            psJob.setShort(5, JobStatus.CANCELLED.value());

            int updateCount = psJob.executeUpdate();
            if (updateCount == 1) {
                // job deleted
                return;
            }

            // ----
            psJobCo = c.prepareStatement(stmtJobCo.toString());
            psJobCo.setBytes(1, jobId.getBytes());
            psJobCo.setShort(2, JobStatus.UNKNOWN.value());
            psJobCo.setShort(3, JobStatus.COMPLETED.value());
            psJobCo.setShort(4, JobStatus.ERROR.value());
            psJobCo.setShort(5, JobStatus.CANCELLED.value());

            updateCount = psJobCo.executeUpdate();
            if (updateCount == 1) {
                // job deleted
                return;
            }

            // if we are here this means that
            // 1.) the job does not exist --> throw a NoSuchJobException
            // 2.) the job exists, but it is NOT in state UNKNOWN, COMPLETED,
            // ERROR or CANCELLED --> throw a JobIllegalStateException

            JobFilter filter = new JobFilter();
            filter.setJobId(jobId);
            Job[] jobs = getJobs(c, filter, null, 10).getJobs();

            if (jobs.length == 0) {
                throw new NoSuchJobException("Job with id '" + jobId.toString()
                        + "' couldn't be found.");
            } else {
                throw new JobIllegalStateException(
                        "Job with id '"
                                + jobId.toString()
                                + "' could not be removed, because it has an illegal state which is '"
                                + jobs[0].getJobStatus().toString() + "'.");
            }

        } finally {
            if (psJob != null) {
                psJob.close();
            }
            if (psJobCo != null) {
                psJobCo.close();
            }
        }
    }

    /**
     * Checks if the Job with jobId exists in one of the both job tables.
     * 
     * @param jobId
     *            the JobID to check for
     * @return true if the Job exists, false otherwise
     * @throws SQLException
     *             if any error occurs, like db-error
     */
    public boolean existsJob(JobID jobId) throws NoSuchJobException,
            SQLException {
        if (getJob(jobId) == null) {
            return false;
        }

        return true;
    }

    // -------------------------------------------------------------------------
    // ----------------- Methods to check for child jobs -----------------------
    // -------------------------------------------------------------------------

    public boolean hasChildJobs(JobID jobid) throws SQLException {
        Connection conn = null;

        PreparedStatement psJobs = null;
        ResultSet rsJobs = null;
        PreparedStatement psJobsCo = null;
        ResultSet rsJobsCo = null;

        // in cause of selecting with COUNT(*) is expensive on some platforms
        // (e.g. Oracle) we select a column which exists always and is also
        // indexed
        // to reach the same goal
        String stmtJobs = "SELECT ID FROM BC_JOB_JOBS WHERE PARENT_ID = ?";
        String stmtJobsCo = "SELECT ID FROM BC_JOB_JOBS_CO WHERE PARENT_ID = ?";

        try {
            conn = obtainConnection();
            psJobs = conn.prepareStatement(stmtJobs);
            psJobs.setBytes(1, jobid.getBytes());
            rsJobs = psJobs.executeQuery();

            if (rsJobs.next()) {
                return true;
            }

            psJobsCo = conn.prepareStatement(stmtJobsCo);
            psJobsCo.setBytes(1, jobid.getBytes());
            rsJobsCo = psJobsCo.executeQuery();

            if (rsJobsCo.next()) {
                return true;
            }

            return false;

        } finally {
            if (rsJobs != null) {
                rsJobs.close();
            }
            if (psJobs != null) {
                psJobs.close();
            }
            if (rsJobsCo != null) {
                rsJobsCo.close();
            }
            if (psJobsCo != null) {
                psJobsCo.close();
            }

            closeConnection(conn);
        }
    }

    /**
     * Returns all child jobs of a a given JobID
     * 
     * @param parentJobId
     *            the JobID to look for
     * @return a Job[] with all child jobs belonging to the given JobID
     * @throws NoSuchJobException
     *             if the job with JobID parentJobID does not exist
     * @throws SQLException
     *             if any error occurs, e.g. DB-error
     */
    public Job[] getChildJobs(JobID parentJobId) throws NoSuchJobException,
            SQLException {

        if (!existsJob(parentJobId)) {
            throw new NoSuchJobException("Job with id '"
                    + parentJobId.toString() + "' does not exist.");
        }

        JobFilter filter = new JobFilter();
        filter.setParentId(parentJobId);

        // Note: 0 for fetch size means that all jobs will be returned in
        // one chunk
        //
        JobIterator it = getJobs(filter, null, 0);
        return it.nextChunk();
    }

    // -------------------------------------------------------------------------
    // ----------------- private helper methods --------------------------------
    // -------------------------------------------------------------------------

    private void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (SQLException sqle) {
            category
                    .logThrowableT(
                            Severity.ERROR,
                            location,
                            "Unable to close jdbc connection. Due to this error the connection might"
                                    + " not has been returned to the connection pool and thus less jdbc connections might"
                                    + " be available for this data-source.",
                            sqle);
        }
    }

    private Connection obtainConnection() {
        try {
            return mDataSource.getConnection();
        } catch (SQLException sqle) {
            final String errMsg = "Unable to obtain connection from data-source.";
            category.logThrowableT(Severity.ERROR, location, errMsg, sqle);
            throw new SchedulerRuntimeException(errMsg, sqle);
        }
    }

}
