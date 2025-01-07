package com.sap.engine.services.scheduler.runtime.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;


/**
 * 
 * The mapping is defined as follows:
 *
 * <p>
 * TODO: this is just a HACK, ahem proof of concept
 * 
 * <ul>
 * <li> SAP Logging | JDK Logging
 * <li>  info       |   fine, finer, finest
 * <li>  info       |   config
 * <li>  info       |   info
 * <li>  warning    |   warning
 * <li>  errror     |   severe
 * </ul>
 */
public class SAPLoggingWrapperHandler extends Handler {

    private Category mCategory;
    private Location mLocation;
    
    public SAPLoggingWrapperHandler(Category cat, Location loc) {
        super();
        
        mCategory = cat;
        mLocation = loc;
    }

    
    public void close() {
        // nothing to do
    }
    
    public void flush() {
        // nothing to do
    }
    
    public boolean isLoggable(LogRecord rec) {
        // we log everything
        return true;
    }
    
    public Level getLevel() {
        return Level.FINEST;
    }
    
    public void publish(LogRecord rec) {
    
        Level l = rec.getLevel();
        Object[] params = rec.getParameters();
        String msg = rec.getMessage();
        
        if (l.equals(Level.SEVERE)) {
            mCategory.errorT(mLocation, msg, params);
        } else if (l.equals(Level.WARNING)) {
            mCategory.warningT(mLocation, msg, params);
        } else if (l.equals(Level.INFO)
                   || l.equals(Level.CONFIG) 
                   || l.equals(Level.FINE)
                   || l.equals(Level.FINER)
                   || l.equals(Level.FINEST)) {
            
            mCategory.infoT(mLocation, msg, params);
        }
    }
}
