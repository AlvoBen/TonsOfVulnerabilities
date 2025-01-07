package com.sap.bc.cts.tp.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.sap.bc.cts.tp.net.NetComm;

public class Client
{
  private String host = null;
  private int port = 0;
  private int number = 0;
  private boolean first = true;
  private Socket server = null;
  private String prompt = "Client > ";
  
  
  public Client ( int _port, String _host,String _prompt) {
		this.host = _host;
		this.port = _port;
		this.number = 0;
		this.first = true;
    if (null != _prompt)
      this.prompt = _prompt;
	}
	
  public Client ( int _port, String _host) {
		this(_port,_host,null);
	}
	
	public Client (int _port) {
	  this(_port,null,null);
	}
  
  public void setPrompt(String _prompt) {
    if (null != _prompt)
      this.prompt = _prompt;
  }
  
  public void beserved () {
    OutputStream out = null;
    InputStream in = null;
    InetAddress inetAddr = null;
    try {
      if (host != null)
        inetAddr = InetAddress.getByName (host);
      else
        inetAddr = InetAddress.getLocalHost();
      
      this.server = new Socket(inetAddr, port);
      this.server.setTcpNoDelay (true);
      
      
    }
    catch (IOException ioe) {
      System.err.println("Error: Could not establish connection to server " + this.host + " at port " + this.port + ": " + ioe.getMessage());//$JL-SYS_OUT_ERR$
      return;
    }
    try {
      out = this.server.getOutputStream(); 
      in = this.server.getInputStream(); 
    }
    catch (IOException ioe) {
      System.err.println("Error: Could not establish communication Streams to server " + this.host + " at port " + this.port + ": " + ioe.getMessage());//$JL-SYS_OUT_ERR$ 
      return;
    }
    NetComm nc = new NetComm(in,out);
    String input = null;
    String output = null;
    boolean getmore = true;
    
    while (true) {
      getmore = true;
      System.out.print(prompt); 
      input = this.receiveInput();
			if (input.equals(".")) 
				break;
      nc.send(input);
      do {
        try {
          output = nc.receive(); 
          if (output.equals(NetComm.eocs))
            getmore = false;
          else
            System.out.println(output);
        }
        catch (IOException ioe) {
          System.err.println("An IOException occurred while reading from the Server: " + ioe.getMessage());//$JL-SYS_OUT_ERR$
          output = null;
        }
      } while (null != output && getmore);
      
      
    } // while
		System.out.println("Bye, bye."); 
    
  } // beserved
  
  private String receiveInput() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String inputline = null;
		
		
		try{
			inputline = in.readLine();
			return inputline;
		}
		catch (IOException e1){
			System.out.println("An IO Exception occurred during read from stdin. (" +
												 e1.getMessage() + ")");
			e1.printStackTrace();
		}
		catch (ArrayIndexOutOfBoundsException e2)
		{
			System.out.println("An 'Array Index out of Bound Exception' occurred (" + 
												 e2.getMessage() + ")");
			e2.printStackTrace();
		}
		catch (Exception e_global) 
		{
			System.out.println("An unexpected Exception occurred (" +
												 e_global.getMessage() + ")");
			e_global.printStackTrace();
		}
		
		return inputline;
				
	} // receiveInput
	
}
