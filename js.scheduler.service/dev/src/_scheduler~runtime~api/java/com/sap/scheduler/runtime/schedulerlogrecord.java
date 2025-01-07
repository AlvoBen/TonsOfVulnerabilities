/*
 * Created on 17.08.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.scheduler.runtime;

import java.io.Serializable;
import java.util.Date;

public class SchedulerLogRecord implements Serializable {
    
    static final long serialVersionUID = -1166711865709322043L;
    
    private String m_message = null;
    private Date m_date = null;
    private int m_severity = 0;
    
    
    public SchedulerLogRecord(String message, Date date, int severity) {
        m_message = message;
        m_date = date;
        m_severity = severity;
    }
    
    
    /**
     * @return Returns the m_date.
     */
    public Date getDate() {
        return m_date;
    }
    
    /**
     * @return Returns the m_message.
     */
    public String getMessage() {
        return m_message;
    }
    
    /**
     * @return Returns the m_severity.
     */
    public int getSeverity() {
        return m_severity;
    }
    
    
    

}
