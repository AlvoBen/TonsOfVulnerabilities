package com.sap.bc.cts.tp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

public interface Service
{
  public void serve(InputStream in, OutputStream out) throws InterruptedIOException,IOException;

  public void endIt(InputStream in, OutputStream out) ;

  public String toString();

  public void setNumber(int _number);

  public int getNumber();
}
