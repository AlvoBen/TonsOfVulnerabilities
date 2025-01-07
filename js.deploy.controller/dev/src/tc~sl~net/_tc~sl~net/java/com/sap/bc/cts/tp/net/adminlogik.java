package com.sap.bc.cts.tp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sap.bc.cts.tp.log.Logger;
import com.sap.bc.cts.tp.log.Trace;


final class AdminLogik implements ExtendedServiceIF
{
  private final static Logger log = Logger.getLogger();
  private final static Trace trace = Trace.getTrace(AdminLogik.class);
  
  private Manager manager = null;
  private int number = 0;

  private static final String[] requests = {
    "get number of connections",           // 00
    "#connections",                        // 01
    "get maximal number of connections",   // 02
    "get number of servants",              // 03
    "#servants",                           // 04
    "get maximal number of servants",      // 05
    "connections++",                       // 06
    "connections--",                       // 07
    "servants++",                          // 08
    "servants--",                          // 09
    "managerlog",                          // 10
    "connectionlog",                       // 11
    "servantlog",                          // 12
    "workerTimeout",                       // 13
    "check",                               // 14
    "connections",                         // 15
    "delcon",                              // 16
    "shutdown",                            // 17
    null
  };
  private Vector v = null;


  public AdminLogik (Manager _manager) {
    this.manager = _manager;

  } // AdminLogik

  public void serve(InputStream in, OutputStream out) throws InterruptedIOException,IOException {
    NetComm nc = new NetComm(in,out);
    serve(nc);
  }

  public void serve(NetComm nc) throws InterruptedIOException,IOException {
    String from_s = null;
    String to_s = null;

    from_s = nc.receive();
    trace.debug("Request from admin client: " + from_s);

    try {
      to_s = this.process(from_s);
      if (null != to_s)
        nc.send(to_s);

      nc.send(NetComm.eocs);
    }
    catch (Exception e) {
      log.fatal("Unexpected Error in AdminLogik", e);
      log.fatal("Exiting.");
      //Commented for JLin reasons
      //System.exit(12);
      //--
    }

  } // serve

  public void endIt(InputStream _in, OutputStream _out) {
     try {
      if (null != _in)
        _in.close();
      if (null != _out)
        _out.close();
    }
    catch (Exception e) {//$JL-EXC$
      //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
    }
  } // endIt

  public String toString() {
     return new String("AdminLogik");
  } // toString

  public void setNumber(int _number) {
    this.number = _number;
  }

  public int getNumber() {
    return this.number;
  }
  /**
   * @deprecated
   */
  public void setLogging(PrintWriter _out) {}

  private String process (String _input) {
    Manager m = this.manager;
    String rs = null;

    if (_input.equalsIgnoreCase(requests[0]) ||
        _input.equalsIgnoreCase(requests[1])                  ) {
      rs = new String("Current number of connections is: " + m.getNumberOfConnections());
    } else if (_input.equalsIgnoreCase(requests[2])) {
      rs = new String("Maximal allowed number of connections is: "+m.getMaxConnections());
    } else if (_input.equalsIgnoreCase(requests[3]) ||
               _input.equalsIgnoreCase(requests[4])                ) {
      rs = new String("Current number of servants is: "+m.getNumberOfServants());
    } else if (_input.equalsIgnoreCase(requests[5])) {
      rs = new String("Maximal allowed number of servants is: "+m.getMaxServants());
    } else if (_input.equalsIgnoreCase(requests[6])) {
      int con_i = m.getMaxConnections();
      con_i++;
      m.setMaxConnections(con_i);
      rs = new String("Maximal number of Connections increased to " + con_i);
    } else if (_input.equalsIgnoreCase(requests[7])) {
      int con_i = m.getMaxConnections();
      if (1 == con_i) {
        rs = new String("Cannot decrease maximal number of Connections below 1");
      } else {
        con_i--;
        m.setMaxConnections(con_i);
        rs = new String("Maximal number of Connections decreased to " + con_i);
      }
    } else if (_input.equalsIgnoreCase(requests[8])) {
      int sv_i = m.getMaxServants();
      sv_i++;
      m.setMaxServants(sv_i);
      rs = new String("Maximal number of Servants increased to " + sv_i);
    } else if (_input.equalsIgnoreCase(requests[9])) {
      int sv_i = m.getMaxServants();
      if (1 == sv_i) {
        rs = new String("Cannot decrease maximal number of Servants below 1");
      } else {
        sv_i--;
        m.setMaxServants(sv_i);
        rs = new String("Maximal number of Servants decreased to " + sv_i);
      }
    } else if (_input.startsWith(requests[10]) ) {
      rs = requests[10] + " not supported anymore";
      log.warning(rs);
    } else if (_input.startsWith(requests[11]) ) {
      rs = requests[11] + " not supported anymore";
      log.warning(rs);
    } else if (_input.startsWith(requests[12]) ) {
      rs = requests[12] + " not supported anymore";
      log.warning(rs);
    } else if (_input.startsWith(requests[13]) ) {
      StringTokenizer strtok = new StringTokenizer(_input," ",false);
      String timeout = strtok.nextToken();
      timeout = strtok.nextToken();
      boolean isint = true;
      int timeout_i = 1000;
      try {
        timeout_i = Integer.parseInt(timeout);
      }
      catch (NumberFormatException nfe) {
        isint = false;
        rs = new String("Error: "+ requests[13] + " has to be followed by a number");
      }
      if (isint) {
        m.setWorkerTimeout(timeout_i);
        rs = new String("Set Workertimeout to " + timeout_i);
      }
    } else if (_input.equalsIgnoreCase(requests[14])) {
      // =======
      // check
      // =======
      m.check();
      rs = new String("Notified Manager to check his administrative structures.");
    } else if (_input.equalsIgnoreCase(requests[15])) {
      // =============
      // connections
      // =============
      int num_c = m.getNumberOfConnections();
      StringBuffer sb = new StringBuffer();

      this.v = new Vector(num_c);
      Connection c = null;
      for (int i=0;i<num_c;i++) {
        c = m.getConnection(i);
        if (null == c)
          break;
        this.v.addElement(c);
        sb.append(i + ": " + c.toString()+"\n");
      }
      rs = sb.toString();
    } else if (_input.startsWith(requests[16]) ) {
      // ============
      // delcon
      // ============
      StringTokenizer strtok = new StringTokenizer(_input," ",false);
      String num = strtok.nextToken();
      num = strtok.nextToken();
      boolean isint = true;
      int num_i = -1;
      try {
        num_i = Integer.parseInt(num);
      }
      catch (NumberFormatException nfe) {
        isint = false;
        rs = new String("Error: "+ requests[16] + " has to be followed by a number");
      }
      if (isint) {
        if (null != this.v) {
          if (0 <= num_i && num_i < this.v.size()) {
            Connection c = (Connection)this.v.elementAt(num_i);
            c.setClosing();
            m.check();
            rs = new String("Set connection \"" + c.toString() + "\" to closing");
          } else {
            rs = new String("Connection number '" + num_i + "' is not defined.\n" +
                            "It might help to refresh the information storage by using \"" +
                            requests[15] + "\" again.");
          }
        } else {
          rs = new String("Error: no connections stored. Use \"" + requests[15]
                          + "\" first.");
        }

      }
    } else if (_input.startsWith(requests[17]) ) {
      // ============
      // shutdown
      // ============
      rs = new String("Shutting down Semaphore Server");
      this.manager.stopRunning();

    } else {
      StringBuffer sb = new StringBuffer("Sorry I do not understand!\n Please choose one of the following:");
      for (int rii = 0; null != requests[rii]; rii++) {
        sb.append("\n" + requests[rii]);
      }
      rs = sb.toString();
    }
    return rs;
  } // process
} // class AdminLogik
