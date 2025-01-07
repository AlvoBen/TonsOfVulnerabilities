/*
 * Created on 29.12.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.services.scheduleradapter.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;


public class SchedulerJMSCommand extends AbstractCommand {
    private static final String NAME = "scheduler_jms"; 
              
    private static StringBuilder m_usageBuffer = new StringBuilder(); 
    static {
      m_usageBuffer.append("Lists info for the jms-messages in the jms-queue for the scheduler.").append(LINE_WRAP);
      m_usageBuffer.append(LINE_WRAP);
      m_usageBuffer.append("Usage: scheduler_jms <-[l]istMessages>").append(LINE_WRAP);
      m_usageBuffer.append("-[l]istMessages  - Lists the jms-messages in the jms-queue for the scheduler").append(LINE_WRAP);
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see com.sap.engine.interfaces.shell.Command#exec(com.sap.engine.interfaces.shell.Environment,
     *      java.io.InputStream, java.io.OutputStream, java.lang.String[])
     */
    public void exec(com.sap.engine.interfaces.shell.Environment env, InputStream input, OutputStream output, String[] params) {
        PrintStream out = new PrintStream(output, true); // true for auto-flush
        int executeValue = ARGS_INVALID;
        
        if ( (executeValue = parseArgs(params)) == ARGS_INVALID ) {
            out.println(getHelpMessage());
            return;
        }

        if (executeValue == 1) {
            
            QueueConnection queueConn = null;
            QueueSession queueSession = null;
            QueueBrowser queueBrowser = null;
            
            try {                
                queueConn = lookupSchedulerJMS();
                queueSession = getQueueSession(queueConn);
                queueBrowser = getQueueBrowser(queueSession);
                
                Enumeration msgs = queueBrowser.getEnumeration();
                
                if ( !msgs.hasMoreElements() ) {
                    out.println("No messages in scheduler jms-queue");
                  } else {
                    while (msgs.hasMoreElements()) {
                      Message msg = (Message)msgs.nextElement();
                      out.println("Class:            "+msg.getClass().getName());
                      out.println("JMSMessageId:     "+msg.getJMSMessageID());
                      out.println("JMSTimestamp:     "+msg.getJMSTimestamp()+" ("+new Date(msg.getJMSTimestamp())+")");
                      out.println("JMSCorrelationId: "+msg.getJMSCorrelationID());
                      out.println("JMSReplyTo:       "+msg.getJMSReplyTo());
                      out.println("JMSDeliveryMode:  "+msg.getJMSDeliveryMode());
                      out.println("JMSRedelivered:   "+msg.getJMSRedelivered());
                      out.println("JMSType:          "+msg.getJMSType());
                      out.println("JMSExpiration:    "+msg.getJMSExpiration());
                      out.println("JMSPriority:      "+msg.getJMSPriority());
                      Enumeration enumeration = msg.getPropertyNames();
                      if (enumeration != null) {
                          while (enumeration.hasMoreElements()) {
                              String prop = (String)enumeration.nextElement();
                              Object val = msg.getObjectProperty(prop);
                              out.println("Property:         "+prop+" = "+val);
                          }
                      } else {
                          out.println("Properties:       null");    
                      }
                      out.println("JMSDestination:");
                      // write it into a new line, because it is already formatted over several lines
                      out.println(msg.getJMSDestination());  
                      out.println("---------------------------------------------");
                    }
                  } // else
            } catch (Exception e) {
                e.printStackTrace(out);
            } finally {
                try {
                    closeSchedulerJMS(queueConn, queueBrowser, queueSession);
                } catch (JMSException e) {
                    e.printStackTrace(out);
                }
            }
        } 
    } // exec
    
    
    private int parseArgs(String[] args) {    
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("-listMessages") || args[0].equalsIgnoreCase("-l")) {
                return 1;
            }
        }
        return ARGS_INVALID;
    } // parseArgs
    
    
    // ---------------------------------------------------------------------------

    public String getHelpMessage() {
      return m_usageBuffer.toString();
    }

    public String getName() {
      return NAME;
    }
    
  }
