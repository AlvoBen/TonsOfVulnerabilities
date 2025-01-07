package com.sap.engine.lib.xml.util;

import java.io.*;

public class ReaderThread extends Thread {

  private InputStream in;
  private OutputStream out;
  private int timeout;
  private boolean[] isAlive = new boolean[] {false};
  private byte[] buffer = new byte[1000];
  int timeToSleep = 1000;

  public ReaderThread(InputStream in, OutputStream out, int timeout, boolean[] isAlive) {
    this.in = in;
    this.out = out;
    this.timeout = timeout;
    this.isAlive = isAlive;
  }

  public void run() {
    int sleptFor = 0;
    isAlive[0] = false;
    try {
      while (true) {
        int i = in.read(buffer);

        if (sleptFor > timeout) {
          out.close();
          break;
        }

        if (i == -1) {
          isAlive[0] = true;
          out.close();
          break;
        }

        if (i == 0) {
          try {
            sleep(timeToSleep);
          } catch (InterruptedException e) {
            //$JL-EXC$
            e.printStackTrace();
          }
          sleptFor += timeToSleep;
          continue;
        }

        out.write(buffer, 0, i);
      }
    } catch (IOException e) {
      //$JL-EXC$
      e.printStackTrace();
    }
  }

  //  public void run() {
  //    LogWriter.getSystemLogWriter().println("Starting with:" + isAlive[0]);
  //    byte b = 0;
  //    int timePassed = 0;
  //    int global;
  //    int j = 0;
  //    
  //   
  //    try {
  //      while (true) {
  //        int i = in.available();
  //        LogWriter.getSystemLogWriter().println(i + " available!");
  //                       
  //        if ( timePassed > timeout ) {
  //          isAlive[0] = false;
  //          break;
  //        }  
  //        
  //        
  //        if ( i >= buffer.length ) {
  //          j = in.read(buffer);
  //          if (j == -1) {
  //            break;
  //          }
  //        } else if (i > 0){
  //          j = in.read(buffer, 0, i);
  //          if (j == -1) {
  //            break;
  //          }
  //        } else {
  //          if(in.read() == -1) 
  //            break;
  //           
  //          try {
  //            sleep(timeToSleep);
  //          } catch(InterruptedException e) {
  //            e.printStackTrace();
  //          }
  //          timePassed += timeToSleep;
  //          continue;
  //
  //        }
  //        
  //
  //        out.write(buffer, 0, j);
  //       
  //      }
  //      out.close();
  //      
  //
  //    } catch(IOException e) {
  //      e.printStackTrace();
  //    }
  //    
  //  }

}

//        if(timePassed < timeout) {
//          
//          if(in.available() == 0) {
//            try {         
//              sleep(1000);
//              LogWriter.getSystemLogWriter().println("Sleeping 1000");
//            } catch(InterruptedException e) {
//              e.printStackTrace();
//            }
//            timePassed += 1000;
//        
//          } else {
//            b = (byte)in.read();
//            
//            if(b == (byte)-1) {
//              break;
//            } else {
//               out.write(b);
//               timePassed = 0;
//            }
//            
//            
//          }
//         
//        } else {
//          isAlive[0] = false;
//          break;
//        }

