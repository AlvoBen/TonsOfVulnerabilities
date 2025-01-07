package com.sap.engine.services.scheduler.runtime.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.logging.LoggingHelper;
import com.sap.engine.services.scheduler.runtime.Environment;
import com.sap.guid.GUID;
import com.sap.scheduler.runtime.AbstractIdentifier;
import com.sap.scheduler.runtime.Event;
import com.sap.scheduler.runtime.EventSubscriber;
import com.sap.scheduler.runtime.SchedulerDefinition;
import com.sap.scheduler.runtime.SchedulerID;
import com.sap.scheduler.runtime.SubscriberID;
import com.sap.scheduler.runtime.SchedulerDefinition.SchedulerStatus;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * This class implements the functionality around persisting of events.
 * 
 * @author Dirk Marwinski
 */
public class EventPersistor {

    // logging and tracing
    private final static Location location = Location.getLocation(EventPersistor.class);
    private final static Category category = LoggingHelper.SYS_SERVER;

    private DataSource mDataSource = null;
    private Environment mEnv = null;

    /**
     * Constructor
     *  
     * @param env the Envorinment
     * 
     * @throws ServiceException the DataSource failed to lookup
     */
    public EventPersistor(Environment env) {
        mEnv = env;
        mDataSource = mEnv.getDataSource();
    }

    /**
     * This method returns all event subscribers
     * 
     * @return array of event subscribers
     */
    public ArrayList<EventSubscriber> getPersistentEventSubscribers() 
                                                     throws SQLException {
            
        // Note: Only external schedulers are persistentevent subscribers
        //       We only consider them when they are "active"
        
        Connection conn = mDataSource.getConnection();
        try {
            
            // read all schedulers
            SchedulerDefinition[] schedulers = mEnv.getSchedulerManagementHandler().getAllSchedulers(conn);
            
            // determine all "active" subscribers
            HashSet<SubscriberID> activeSubscribers = new HashSet<SubscriberID>();
            for (SchedulerDefinition s: schedulers) {
                if (s.getSchedulerStatus().equals(SchedulerStatus.active)) {
                    activeSubscribers.add(s.getSubscriberId());
                }
            }
            
            // get all subscibers
            ArrayList<EventSubscriber> subscribers = getEventSubscribers(conn);
            
            // filter all which are not related to an active external scheduler
            Iterator<EventSubscriber> it = subscribers.iterator();
            while (it.hasNext()) {
                EventSubscriber c = it.next();
                if (!activeSubscribers.contains(c.getSubscriberId())) {
                    it.remove();
                }
            }
            return subscribers;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    private ArrayList<EventSubscriber> getEventSubscribers(Connection conn) 
                                                        throws SQLException {

        PreparedStatement ps = null;
        try {
            String queryString = "SELECT ID, PERSIST_EVENTS FROM BC_JOB_SUBSCRIBER";
            ps = conn.prepareStatement(queryString);

            ResultSet result = ps.executeQuery();

            ArrayList<EventSubscriber> subscribers = new ArrayList<EventSubscriber>();

            while(result.next()) {
                EventSubscriber sub = new EventSubscriber();
                sub.setSubscriberId(SubscriberID.parseID(result.getBytes(1)));
                sub.setPersistEvents(result.getShort(2) == 0 ? false : true);
                subscribers.add(sub);
            }
            result.close();
            ps.close();

            // read filters
            //
            queryString = "SELECT FILTER_STRING FROM BC_JOB_FILTER WHERE SUBSCRIBER_ID = ?";
            ps = conn.prepareStatement(queryString);
            ArrayList<String> filters = new ArrayList<String>();
            
            for (EventSubscriber sub: subscribers) {

                ps.setBytes(1, sub.getSubscriberId().getBytes());
                
                result = ps.executeQuery();
                
                while(result.next()) {
                    filters.add(result.getString(1));
                }
                result.close();

                String[] filterArray = filters.toArray(new String[filters.size()]);
                sub.setFilters(filterArray);
            }

            return subscribers;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * This method adds an event to all persistent event subscribers
     * 
     */
    public void persistEvent(HashMap<SubscriberID,EventSubscriber> subscribers, Event event) 
                                                                           throws SQLException {

        Connection conn = mDataSource.getConnection();
        PreparedStatement ps = null;
        
        try {
            // turn off auto commit (everything done in one tx)
            //
            conn.setAutoCommit(false);
            
            String insertString = "INSERT INTO BC_JOB_EVENTS (ID, SUBSCRIBER_ID, EVENT_TYPE, EVENT_PARAMETER, EVENT_ADD_PARAM, EVENT_DATE, EVENT_READ ) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(insertString);
                
            Timestamp eventDate = new Timestamp(event.getRaisedDate().getTime());
            
            boolean emptyBatch = true;
            Set<Map.Entry<SubscriberID,EventSubscriber>> entries = subscribers.entrySet();
            for (Map.Entry<SubscriberID,EventSubscriber> entry : entries) {
                                
                EventSubscriber sub = entry.getValue();
                if (!sub.persistEvents()) {
                    // do not persist events for this subscriber
                    //
                    continue;
                }
                for (int j=0; j < sub.getFilters().length; j++) {
                    if (event.getType().equals(sub.getFilters()[j])) {
                        
                        emptyBatch = false;
                        byte[] eventId = new GUID().toBytes();
                        
                        ps.setBytes(1, eventId);
                        ps.setBytes(2, sub.getSubscriberId().getBytes());
                        ps.setString(3, event.getType());
                        ps.setString(4, event.getParameter());
                        ps.setString(5, event.getAdditionalParameter());
                        ps.setTimestamp(6, eventDate);
                        ps.setShort(7, (short)0);

                        ps.addBatch();
                        break;
                    }
                }
            }
            
            if (!emptyBatch) {
                ps.executeBatch();
    
                // commit transaction
                //
                conn.commit();
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

    /**
     * This method clears all events for a given event subscriber which have
     * already been read.
     */
    public void clearEvents(EventSubscriber subscriber)
                                                  throws SQLException
    {
        Connection conn = mDataSource.getConnection();
        PreparedStatement ps = null;
        
        try {
            
            String deleteString = "DELETE FROM BC_JOB_EVENTS WHERE EVENT_READ = 1 AND SUBSCRIBER_ID = ?";
            ps = conn.prepareStatement(deleteString);
            
            ps.setBytes(1, subscriber.getSubscriberId().getBytes());
            
            ps.executeUpdate();
            
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
    }
    
    /**
     * Gets all events for the specified event subscriber which have not been
     * read so far.
     */
    public Event[] getUnreadEvents(AbstractIdentifier subscriberId, int fetchSize) 
                                                                      throws SQLException {

        Connection conn = mDataSource.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
                        
            // turn off auto commit (everything done in one tx)
            //
            conn.setAutoCommit(false);
            
            // read unread events
            //
            String selectString = "SELECT ID, EVENT_TYPE, EVENT_PARAMETER, EVENT_ADD_PARAM, EVENT_DATE FROM BC_JOB_EVENTS WHERE EVENT_READ = 0 AND SUBSCRIBER_ID = ? ORDER BY EVENT_DATE";
            ps = conn.prepareStatement(selectString);

            if (fetchSize != 0) {
                ps.setMaxRows(fetchSize);
            }

            ps.setBytes(1, subscriberId.getBytes());
            rs = ps.executeQuery();

            ArrayList<Event> events = new ArrayList<Event>();
            Event event;
            while (rs.next()) {           
                event = new Event(rs.getString(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5));
                event.setId(rs.getBytes(1));
                events.add(event);
            }
            rs.close();
            ps.close();
            
            if (events.size() == 0) {
            	return new Event[0];
            }
            
            // now update all records and mark them as being read()
            //
            String updateString = "UPDATE BC_JOB_EVENTS SET EVENT_READ = 1 WHERE ID = ? AND SUBSCRIBER_ID = ?";
            ps = conn.prepareStatement(updateString);
            
            for (Event ev : events) {

                ps.setBytes(1, ev.getId());
                ps.setBytes(2, subscriberId.getBytes());
                
                ps.addBatch();
            }
            
            ps.executeBatch();
            conn.commit();

            return events.toArray(new Event[events.size()]);
       
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
     * Sets the filter for the given persistent event subscriber.
     * 
     * @param subid
     *            subscriber id
     * @param filters
     */
    public void setFilter(AbstractIdentifier persistentEventSubscriber, String[] filters)
                                                                             throws SQLException {
        Connection conn = mDataSource.getConnection();
        PreparedStatement ps = null;

        byte[] subscriberID = persistentEventSubscriber.getBytes();

        try {

            // turn off auto commit (everything done in one tx)
            //
            conn.setAutoCommit(false);

            String deleteString = "DELETE FROM BC_JOB_FILTER WHERE SUBSCRIBER_ID = ?";
            ps = conn.prepareStatement(deleteString);
            ps.setBytes(1, subscriberID);

            ps.executeUpdate();
            ps.close();
            
            String insertString = "INSERT INTO BC_JOB_FILTER (SUBSCRIBER_ID, FILTER_STRING) VALUES (?, ?)";
            ps = conn.prepareStatement(insertString);
            
            for (String filter : filters) {
                ps.setBytes(1, subscriberID);
                ps.setString(2, filter);
                ps.addBatch();
            }
            
            ps.executeBatch();
            ps.close();
           
            conn.commit();
            
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    

    /**
     * This method returns a persistent event subscriber object for a given event
     * subscriber
     * 
     * @param subid
     *            subscriber id for the event subscriber
     * @return an EventSubscriber object or null if there is no event subscriber
     *         for the given id
     */
    public EventSubscriber getPersistentEventSubscriberById(AbstractIdentifier persistentSubscriberId)
                                                                                       throws SQLException {

        Connection conn = mDataSource.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        byte[] subscriberID = persistentSubscriberId.getBytes();

        try {
            
            String query1 = "SELECT PERSIST_EVENTS FROM BC_JOB_SUBSCRIBER WHERE ID = ?";
            ps = conn.prepareStatement(query1);
            ps.setBytes(1, subscriberID);
            
            rs = ps.executeQuery();
            
            if (!rs.next()) {
                // result set is empty -> there is no such subscriber
                //
               return null;
            }
            
            rs.close();
            ps.close();
            
            String query2 = "SELECT FILTER_STRING FROM BC_JOB_FILTER WHERE SUBSCRIBER_ID = ?";
            ps = conn.prepareStatement(query2);
            ps.setBytes(1, subscriberID);
            
            rs = ps.executeQuery();
            
            ArrayList<String> filters = new ArrayList<String>();
            while (rs.next()) {
                filters.add(rs.getString(1));
            }
            
            String[] filterArray = filters.toArray(new String[filters.size()]);
            return new EventSubscriber(SubscriberID.parseID(persistentSubscriberId.getBytes()), filterArray, true);
            
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
}
