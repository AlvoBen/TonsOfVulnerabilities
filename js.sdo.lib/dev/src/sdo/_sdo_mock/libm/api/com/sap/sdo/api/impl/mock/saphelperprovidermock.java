/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.api.impl.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.sap.sdo.api.helper.Validator;
import com.sap.sdo.api.impl.SapHelperProvider;

import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public class SapHelperProviderMock extends SapHelperProvider {

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#context(java.lang.String)
     */
    @Override
    protected HelperContext context(String pId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#context(java.lang.String, java.lang.ClassLoader)
     */
    @Override
    protected HelperContext context(String pId, ClassLoader pCl) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#context(java.lang.String, commonj.sdo.helper.HelperContext)
     */
    @Override
    protected HelperContext context(String pId, HelperContext pParent) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#contextId(commonj.sdo.helper.HelperContext)
     */
    @Override
    protected String contextId(HelperContext pHelperContext) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#defaultContext()
     */
    @Override
    protected HelperContext defaultContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#defaultContext(java.lang.ClassLoader)
     */
    @Override
    protected HelperContext defaultContext(ClassLoader pCl) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#deserializeCxt(java.io.Reader)
     */
    @Override
    protected List<HelperContext> deserializeCxt(Reader pReader) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#deserializeCxt(java.io.InputStream)
     */
    @Override
    protected List<HelperContext> deserializeCxt(InputStream pInputStream) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#newContext()
     */
    @Override
    protected HelperContext newContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#newContext(java.lang.ClassLoader)
     */
    @Override
    protected HelperContext newContext(ClassLoader pCl) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#newContext(commonj.sdo.helper.HelperContext)
     */
    @Override
    protected HelperContext newContext(HelperContext pParent) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#removeCxt(java.lang.String)
     */
    @Override
    protected boolean removeCxt(String pId) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#removeCxt(java.lang.String, java.lang.ClassLoader)
     */
    @Override
    protected boolean removeCxt(String pId, ClassLoader pCl) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#removeCxt(commonj.sdo.helper.HelperContext)
     */
    @Override
    protected boolean removeCxt(HelperContext pHelperContext) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#serializeCxt(java.util.List, java.io.Writer)
     */
    @Override
    protected void serializeCxt(List<HelperContext> pHelperContexts,
        Writer pWriter) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#serializeCxt(java.util.List, java.io.OutputStream)
     */
    @Override
    protected void serializeCxt(List<HelperContext> pHelperContexts,
        OutputStream pOutputStream) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#validator()
     */
    @Override
    protected Validator validator() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.api.impl.SapHelperProvider#deserializeCxtInto(java.io.Reader, commonj.sdo.helper.HelperContext)
     */
    @Override
    protected void deserializeCxtInto(Reader pReader, HelperContext pCtx) {
        // TODO Auto-generated method stub
        
    }

}
