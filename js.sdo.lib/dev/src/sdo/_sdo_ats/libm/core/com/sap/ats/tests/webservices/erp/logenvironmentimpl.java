/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.ats.tests.webservices.erp;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sap.ats.env.LogEnvironment;

/**
 * @author D042774
 *
 */
public class LogEnvironmentImpl implements LogEnvironment {

    private static Logger logger = Logger.getLogger(LogEnvironmentImpl.class.getName());
    
    static {
//        try {
//            FileHandler fileHandler = new FileHandler("java%u.log", 10485760, 2000);
//            fileHandler.setFormatter(new SimpleFormatter());
//            logger.addHandler(fileHandler);
//        } catch (SecurityException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#excludeServerLog(java.lang.String, java.lang.String)
     */
    public void excludeServerLog(String pS, String pS1) {
        throw new UnsupportedOperationException();

    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#getAsPrintStream()
     */
    public PrintStream getAsPrintStream() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#getExternalLogFile()
     */
    public File getExternalLogFile() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#getLogDir()
     */
    public String getLogDir() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#log(java.lang.String)
     */
    public void log(String pS) {
        logger.info(pS);
    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#log(java.lang.Throwable)
     */
    public void log(Throwable pThrowable) {
        logger.log(Level.WARNING, pThrowable.getMessage(), pThrowable);

    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#log(byte[], int, int)
     */
    public void log(byte[] pAbyte0, int pI, int pJ) {
        throw new UnsupportedOperationException();

    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#setInfoValue(java.lang.String)
     */
    public void setInfoValue(String pS) {
        throw new UnsupportedOperationException();

    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.LogEnvironment#setResultLinkFile(java.lang.String)
     */
    public void setResultLinkFile(String pS) {
        throw new UnsupportedOperationException();

    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.Environment#close()
     */
    public void close() throws Exception {
        throw new UnsupportedOperationException();

    }

    /* (non-Javadoc)
     * @see com.sap.ats.env.Environment#init(java.util.Properties)
     */
    public void init(Properties properties) throws Exception {
        throw new UnsupportedOperationException();

    }

}
