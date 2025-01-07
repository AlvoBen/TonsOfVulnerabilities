package com.sap.jms.util.compat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class PrintWriter extends java.io.PrintWriter {
	
	public PrintWriter(OutputStream out) {
		super(out);
	}
 
	public PrintWriter(OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
	}
 
	public PrintWriter(Writer out) {
		super(out);		
	}
 
	public PrintWriter(Writer out, boolean autoFlush) {
		super(out, autoFlush);		
	}
	
	public void printf(String format, Object data0) throws IOException {
		printf(format, new Object[]{data0});
	}
	
	public void printf(String format, Object data0, Object data1) throws IOException {
		printf(format, new Object[]{data0, data1});
	}
	
	public void printf(String format, Object data0, Object data1, Object data2) throws IOException {
		printf(format, new Object[]{data0, data1, data2});
	}
	
	public void printf(String format, Object data0, Object data1, Object data2, Object data3) throws IOException {
		printf(format, new Object[]{data0, data1, data2, data3});
	}

	public void printf(String format, Object data0, Object data1, Object data2, Object data3, Object data4) throws IOException {
		printf(format, new Object[]{data0, data1, data2, data3, data4});
	}

	public void printf(String format, Object data0, Object data1, Object data2, Object data3, Object data4, Object data5) throws IOException {
		printf(format, new Object[]{data0, data1, data2, data3, data4, data5});
	}
	
		
	public java.io.PrintWriter printf(String format, Object data[])/* throws IOException */{
		// TODO
		if (data != null) {
			for (int i = 0, j = data.length; i < j; i++) {
				Object value  = data[i]; 
				String text = (value != null ? value.toString() : "null") + " "; 
				try {
					out.write(text);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return (java.io.PrintWriter)this;
	}	
}
