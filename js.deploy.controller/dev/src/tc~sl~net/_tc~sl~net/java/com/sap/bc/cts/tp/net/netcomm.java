package com.sap.bc.cts.tp.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.StringTokenizer;

import com.sap.bc.cts.tp.log.Trace;


/* This class provides one part of the functionality that is
 * provided by the com.sap.bc.ge.net.CharLink class. The functionality
 * provided here is the packing of Strings in messages of the format:
 * <header><String> where header contains the length of the String.
 */

public class NetComm
{
  private final static Trace trace = Trace.getTrace(NetComm.class);
  
  public static String eocs = "$&^#";
  final static String EOL = System.getProperty("line.separator");

  private OutputStreamWriter writer;
  private InputStreamReader reader;
  private InputStream inputStream;
  private OutputStream outputStream;
  private SocketTimeoutViewIF socketTimeoutView = null;
  private char writeHdr [] = new char [12];
  private char readHdr []  = new char [12];
  private char sendBuf [] = new char [0];
  private boolean bufferedSend = false;
  private StringBuffer collectorStringBuffer = null;
  private StringTokenizer receivedTokens = null;

  public NetComm (
    InputStream inputStream,
    OutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    InputStreamReader inR = new InputStreamReader(inputStream);
    this.writer = new OutputStreamWriter(outputStream);
    this.reader = new InputStreamReader(inputStream);
  }



  public NetComm (
    InputStream inputStream,
    OutputStream outputStream,
    SocketTimeoutViewIF socketTimeoutView) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.writer = new OutputStreamWriter(outputStream);
    this.reader = new InputStreamReader(inputStream);
    this.socketTimeoutView = socketTimeoutView;
  }
  
  public OutputStream getOutputStream() {
    return this.outputStream;
  }
  public InputStream getInputStream() {
    return this.inputStream;
  }

  /**
   * Send and Receive using the given Reader and Writer
   * This class is implementing the simple protocol used to make clear
   * the length of the messages to be expected
   * if (_bufferedSend) the send method actually does not write anything to the
   * Writer before a eocs string is meant to be send
   * The send(string) method rather collects all incoming strings up to the eocs string
   * _bufferedSend should only be used by the Semaphore Server so far.
   */
  public NetComm (InputStream inputStream,OutputStream outputStream,boolean _bufferedSend) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
    this.writer = new OutputStreamWriter(outputStream);
    this.reader = new InputStreamReader(inputStream);
    bufferedSend = _bufferedSend;
  }


  public void setbufferedSend (boolean _bufferedSend) {
    bufferedSend = _bufferedSend;
  }

  public void flush() throws IOException {
    writer.flush();
  }


  public String receive () throws IOException
  {
    trace.entering("receive()");
    try {
      String returnString = null;
      if (null != receivedTokens) {
        if (receivedTokens.hasMoreTokens()) {
          returnString = receivedTokens.nextToken();
          trace.debug("NetComm.receive() from it's buffer returns ['" + returnString + "'].");
          return returnString;
        } else {
           receivedTokens = null;
        }
      }
  
      {
        int receivedHeaderBytes = -1;
        try {
          receivedHeaderBytes = receiveHdr (readHdr);
        } catch (IOException ioe) {
          trace.debug("Caught IOException during read of header bytes ("+ 
            receivedHeaderBytes+","+new String (readHdr)+"):"+ioe.getMessage());
          if (ioe instanceof InterruptedIOException) {
            trace.debug("  throwing InterruptedIOException(net.id_000001)");
            throw new InterruptedIOException(ioe.getMessage());
          } else {
            trace.debug("  throwing IOException(net.id_000001)");
            throw new IOException(
              "net.id_000001: Caught IOException during read of header bytes ("+ 
              receivedHeaderBytes+","+new String (readHdr)+"):"+ioe.getMessage());
          }
        }
        if (receivedHeaderBytes < 0) {
          // If we get EOF while reading the length
          // we assume that the server was terminated without
          // errors.
  	      trace.debug("EOF while reading length");
          throw new IOException ("EOF while reading length");
        } else if (receivedHeaderBytes < readHdr.length) {
          trace.debug("Did not receive complete set of header bytes ("+ 
            receivedHeaderBytes+","+new String (readHdr)+")"+
            EOL + "  throwing IOException(net.id_000002)");
          throw new IOException("net.id_000002: Did not receive complete set of header bytes ("+ 
            receivedHeaderBytes+","+new String (readHdr)+")");
        }
  
        String str    = (new String (readHdr)).trim ();
        trace.debug("received length String: '" + str + "'.");
        int msglen = 0;
        try {
          msglen = Integer.parseInt (str);
          trace.debug("length String translates to the following number: '" + msglen + "'.");
        }
        catch (NumberFormatException nfe) {
          trace.debug("the length string does not translate to a number"+
            EOL+ "  throwing IOException(net.id_000003)");
          throw new IOException("net.id_000003: Length information in Header ("+
          str+") does not translate to a number");
        }
        char   msg [] = new char [msglen];
  
        // Now, we can read the actual message.

        int receivedMessageBytes = 0;
        try {
          receivedMessageBytes = receiveMsg (msg);
        } catch (IOException ioe) {
          trace.debug("Caught IOException during read of message bytes ("+ 
            receivedMessageBytes+","+new String (msg)+"):"+ioe.getMessage());
          if (ioe instanceof InterruptedIOException) {
            trace.debug("  throwing InterruptedIOException(net.id_000005)");
            throw new InterruptedIOException(ioe.getMessage());
          } else {
            trace.debug("  throwing IOException(net.id_000005)");
            throw new IOException(
              "net.id_000005: Caught IOException during read of message bytes ("+ 
              receivedMessageBytes+","+new String (msg)+"):"+ioe.getMessage());
          }
          
        }
        if (receivedMessageBytes < 0) {
  	      trace.debug("EOF while reading message ("+(new String(msg)).trim()+")"+
            EOL + " throwing IOException(net.id_000004)");
          throw new IOException(
            "net.id_000004: EOF while reading message("+(new String(msg)).trim()+")");
        } else if (receivedMessageBytes < msglen) {
          trace.debug("Did not receive complete set of message bytes ("+ 
            receivedMessageBytes+","+(new String (msg)).trim()+")"+
            EOL + "  throwing IOException(net.id_000006)");
          throw new IOException("net.id_000006: Did not receive complete set of message bytes ("+ 
            receivedMessageBytes+","+(new String (msg)).trim()+")");
          
        } else {
          returnString = new String(msg);
        }
        receivedTokens = new StringTokenizer(returnString,"\n",false);
  
        /* (TB) make sure this method does not return an 
         * empty String out of the memory (receivedTokens)
         * instead of reading from the network again.
         */
        int tokenCount = receivedTokens.countTokens(); //(TB)
        if (tokenCount <= 1) {                         //(TB)
          receivedTokens = null;                     //(TB)
        } else {                                       //(TB)
        if (receivedTokens.hasMoreTokens()) {
  	      returnString = receivedTokens.nextToken();
                if (!receivedTokens.hasMoreTokens()) { //(TB)
  		    receivedTokens = null;             //(TB)
  	      }
  	  } else {
  	      receivedTokens = null;
  	  }
        }
      }
      trace.debug("NetComm.receive() is returning ['" + returnString + "'].");
      return returnString;
    } finally {
      trace.exiting("receive()");
    }
  } // receive

  synchronized public void send (String  str)
  {
    trace.entering("send("+str+").");
    if (bufferedSend) {
      if (null == collectorStringBuffer) {
        collectorStringBuffer = new StringBuffer(str);
      } else {
        collectorStringBuffer.append('\n');
        collectorStringBuffer.append(str);
      }
    }
    if (str.equals(eocs)|| !bufferedSend) {
      String collectedString = null;
      if (bufferedSend)
        collectedString = collectorStringBuffer.toString();
      else
        collectedString = str;
      collectorStringBuffer = null;
      int strLen = collectedString.length ();

      // If the write buffer is to small
      // we have to allocate a larger write buffer.

      //(tb)if (strLen > sendBuf.length)
        sendBuf = new char [strLen];

      collectedString.getChars (0, strLen, sendBuf, 0);

      send (sendBuf, 0 , strLen);
    }
    trace.exiting("send(...)");
  } //send

  public void setHeaderLen (int headerLen)
  {
    readHdr  = new char [headerLen];
    writeHdr = new char [headerLen];
  } // setHeaderLen

  public int getHeaderLen ()
  {
    return readHdr.length;
  } // getHeaderLen

  private void fillWriteHdr (int length)
  {
    String s  = String.valueOf (length);
    int    n  = s.length ();
    int    m  = writeHdr.length - n;
    int    i;

    // Fill in starting blanks

    for (i = 0; i < m; i++)
      writeHdr [i] = ' ';

    // Fill in the length string

    for (i = 0; i < n; i++)
      writeHdr [m++] = s.charAt (i);
  } // fillWriteHdr

  private int receiveHdr(char[] msg) throws IOException {
    return receive(msg,true,msg.length);
  }
  
  private int receiveMsg(char[] msg) throws IOException {
    return receive(msg,false,msg.length); 
  }

  private int receiveMsg(char[] msg,int msglen) throws IOException {
    return receive(msg,false,msglen); 
  }
  
  private int receive (
    char [] msg,
    boolean isHeader,
    int msglen) 
    throws IOException
  {
    int offset;      // offset in msg
    int read;        // number of bytes actually read
    int remaining;   // number of remaining bytes to be read
    boolean firstByteReceived = !isHeader;

    initSoTimeout();        
    if (firstByteReceived) 
      setSoTimeoutBlocking();

    offset     = 0;
    read       = -1;
    remaining  = msglen;
    try {
  
      while (remaining > 0)
      {
        
        try {
          read = 0;
          read = reader.read (msg, offset, remaining);
        } catch (InterruptedIOException iioe) {
          if (!firstByteReceived) {
            trace.debug(
              "Caught InterruptedIOException before the first Byte was read."+
              " Throwing InterruptedIOException(net.id_000100: "+iioe.getMessage()+").");
            throw new  InterruptedIOException("net.id_000100: " + iioe.getMessage());
          } else {
            trace.debug(
              "Caught InterruptedIOException after the first Byte was read."+
              " That is OK. Start reading again.");
            
          }
        }
        if ((read) < 0)
        {
          return -1;
        } else {
          
          /* read at least something from the client
           * make sure the rest is read too.
           */
          firstByteReceived = true;
          setSoTimeoutBlocking();
        }
        
        offset    += read;
        remaining -= read;
      }
      if (reader.markSupported()) {
        try {
          reader.mark(65536);
          reader.reset();
        } catch (IOException ioeReader) {
          trace.debug("Caught IOException during mark/reset operation ("+ioeReader.getMessage()+")");
        }
        
      } else {
        trace.debug("Reader does not support marks.");
      }
  /*
      try {
        FileWriter fw = new FileWriter("netty",true);
        fw.write("NetComm.receive(): ");
        fw.write(msg);
        fw.write("\n");
        fw.close();
      } catch (Exception e) {}
      */
      return msglen - remaining;
    } finally {
      resetSoTimeout();
      if (remaining > 0) {
        trace.debug(
          "Method \"receive(char[])\" could not read all requested bytes. "+
          "There are still "+remaining+" bytes to read"); 
      } 
      
    }
  } // receive

  private int originalSocketTimeout = -1;
  private boolean blocking = false;
  private void initSoTimeout() {
    this.blocking = false;
    if (null != this.socketTimeoutView) {
       try {
        this.originalSocketTimeout = this.socketTimeoutView.getSoTimeout();
      } catch (SocketTimeoutViewException e) {
        trace.debug(
          "SocketTimeoutViewException occurred during getSoTimeout(): " + 
          e.getMessage());
        this.originalSocketTimeout = -1;
      }
    }  
  } // initSoTimeout
  
  private void setSoTimeoutBlocking() {
    if (
      !this.blocking && 
      -1 != originalSocketTimeout && 
      null != this.socketTimeoutView) {
      try {
        this.socketTimeoutView.setSoTimeout(0);
      } catch (SocketTimeoutViewException e) {
        trace.debug(
          "SocketTimeoutViewException occurred during setSoTimeout(0): "+
          e.getMessage()
          );
      } 
    }
    this.blocking = true;
  } // setSoTimeoutBlocking
  
  private void resetSoTimeout() {
    if (
      this.blocking && 
      -1 != originalSocketTimeout && 
      null != this.socketTimeoutView) {
      try {
        this.socketTimeoutView.setSoTimeout(
          this.originalSocketTimeout);
      } catch (SocketTimeoutViewException e) {
        trace.debug(
          "SocketTimeoutViewException occurred during setSoTimeout("+
          this.originalSocketTimeout+"): "+
          e.getMessage()
          );
      } 
    }
    this.blocking = false;
     
  }
  
  synchronized private void send (char [] msg)
  {
    send (msg, 0, msg.length);
  }

  synchronized private void send (char [] msg, int off, int len)
  {
    try
    {
      // Generate the information about the message length

      fillWriteHdr (len);

      // Write information about the message length

      writer.write (writeHdr, 0, writeHdr.length);

      // Write the message itself

      writer.write (msg, off, len);

      // Flush the stream

      writer.flush ();
      /*
      try {
        FileWriter fw = new FileWriter("netty",true);
        fw.write("NetComm.send(): ");
        fw.write(writeHdr);
        fw.write(msg);
        fw.write("\n");
        fw.close();
      } catch (Exception e) {}
      */
    }
    catch (IOException e)
    {//$JL-EXC$
      //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
      
      // getObserver ().closed ("IOException " +  e.getMessage ());
    }
  }


}
