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
package com.sap.sdo.api.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.sap.sdo.api.helper.Validator;

import commonj.sdo.helper.HelperContext;

/**
 * @author D042774
 *
 */
public abstract class SapHelperProvider {

    public static final String DEFAULT_CONTEXT_ID = "com.sap.sdo.api.types.ctx.default";
    public static final String CORE_CONTEXT_ID = "com.sap.sdo.api.types.ctx.core";

    private static final String IMPLEMENTATION_CLASS =
        SapHelperProvider.class.getName();
    private static final String IMPLEMENTATION_CLASS_DEFAULT =
        "com.sap.sdo.impl.context.SapHelperProviderImpl";

    static SapHelperProvider INSTANCE = getHelperProviderImpl();
    
    static SapHelperProvider getHelperProviderImpl() {
      try {
          String className =
              System.getProperty(IMPLEMENTATION_CLASS, IMPLEMENTATION_CLASS_DEFAULT);
          return (SapHelperProvider)Class.forName(className).newInstance();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }
    
    protected SapHelperProvider() {
    }

    /**
     * Creates a new HelperContext.
     * The id of the HelperContext will be a {@link UUID}.
     * @return helper context.
     * @see #getId()
     * @see UUID#randomUUID()
     */
    public static HelperContext getNewContext() {
        return INSTANCE.newContext();
    }

    /**
     * Get or create instance of HelperContext by the id.
     * Note that the HelperContexts are maintained as {@link java.lang.ref.WeakReference}.
     * That means if the client looses its last reference to the HelperContext,
     * the look-up by its id will not return the old HelperContext, but create
     * a new one.
     * The default HelperContext is no WeakReference.
     * @param pId The id of the HelperContext.
     * @return helper context.
     */
    public static HelperContext getContext(String pId) {
        return INSTANCE.context(pId);
    }

    /**
     * Removes the HelperContext identified by its id. 
     * @param pId The id of the HelperContext to remove.
     * @return true if the HelperContext was found.
     */
    public static boolean removeContext(String pId) {
        return INSTANCE.removeCxt(pId);
    }

    /**
     * Creates a new HelperContext.
     * The id of the HelperContext will be a {@link UUID}.
     * @return helper context.
     * @see #getId()
     * @see UUID#randomUUID()
     */
    public static HelperContext getNewContext(ClassLoader pClassLoader) {
        return INSTANCE.newContext(pClassLoader);
    }

    /**
     * Creates a new HelperContext.
     * The id of the HelperContext will be a {@link UUID}.
     * @return helper context.
     * @see #getId()
     * @see UUID#randomUUID()
     */
    public static HelperContext getNewContext(HelperContext pHelperContext) {
        return INSTANCE.newContext(pHelperContext);
    }

    /**
     * Get or create instance of HelperContext by the id.
     * Note that the HelperContexts are maintained as {@link java.lang.ref.WeakReference}.
     * That means if the client looses its last reference to the HelperContext,
     * the look-up by its id will not return the old HelperContext, but create
     * a new one.
     * The default HelperContext is no WeakReference.
     * @param pId The id of the HelperContext.
     * @return helper context.
     */
    public static HelperContext getContext(String pId, ClassLoader pClassLoader) {
        return INSTANCE.context(pId, pClassLoader);
    }

    /**
     * Get or create instance of HelperContext by the id.
     * Note that the HelperContexts are maintained as {@link java.lang.ref.WeakReference}.
     * That means if the client looses its last reference to the HelperContext,
     * the look-up by its id will not return the old HelperContext, but create
     * a new one.
     * The default HelperContext is no WeakReference.
     * @param pId The id of the HelperContext.
     * @return helper context.
     */
    public static HelperContext getContext(String pId, HelperContext pHelperContext) {
        return INSTANCE.context(pId, pHelperContext);
    }

    /**
     * Removes the HelperContext identified by its id. 
     * @param pId The id of the HelperContext to remove.
     * @return true if the HelperContext was found.
     */
    public static boolean removeContext(String pId, ClassLoader pClassLoader) {
        return INSTANCE.removeCxt(pId, pClassLoader);
    }

    /**
     * Removes the HelperContext. 
     * @param pHelperContext The HelperContext to remove.
     * @return true if the HelperContext was found.
     */
    public static boolean removeContext(HelperContext pHelperContext) {
        return INSTANCE.removeCxt(pHelperContext);
    }
    
    public static String getContextId(HelperContext pHelperContext) {
        return INSTANCE.contextId(pHelperContext);
    }

    public  static void serializeContexts(List<HelperContext> pHelperContexts, Writer pWriter) {
        INSTANCE.serializeCxt(pHelperContexts, pWriter);
    }
    public  static void serializeContexts(List<HelperContext> pHelperContexts, OutputStream pOutputStream){
        INSTANCE.serializeCxt(pHelperContexts, pOutputStream);
    }
    public  static List<HelperContext> deserializeContexts(Reader pReader){
        return INSTANCE.deserializeCxt(pReader);
    }
    public  static List<HelperContext> deserializeContexts(InputStream pInputStream){
        return INSTANCE.deserializeCxt(pInputStream);
    }

    public static Validator getValidator() {
        return INSTANCE.validator();
    }
    
    public static HelperContext getDefaultContext() {
        return INSTANCE.defaultContext();
    }

    public static HelperContext getDefaultContext(ClassLoader cl) {
        return INSTANCE.defaultContext(cl);
    }

    protected abstract HelperContext defaultContext();
    protected abstract HelperContext newContext();
    protected abstract HelperContext context(String pId);
    protected abstract boolean removeCxt(String pId);
    protected abstract HelperContext defaultContext(ClassLoader cl);
    protected abstract HelperContext newContext(ClassLoader cl);
    protected abstract HelperContext context(String pId, ClassLoader cl);
    protected abstract HelperContext newContext(HelperContext pParent);
    protected abstract HelperContext context(String pId, HelperContext pParent);
    protected abstract boolean removeCxt(String pId, ClassLoader cl);
    protected abstract boolean removeCxt(HelperContext pHelperContext);
    protected abstract String contextId(HelperContext pHelperContext);
    protected abstract void serializeCxt(List<HelperContext> pHelperContexts, Writer pWriter);
    protected abstract void serializeCxt(List<HelperContext> pHelperContexts, OutputStream pOutputStream);
    protected abstract List<HelperContext> deserializeCxt(Reader pReader);
    protected abstract List<HelperContext> deserializeCxt(InputStream pInputStream);
    protected abstract Validator validator();
    @Deprecated
    protected abstract void deserializeCxtInto(Reader reader, HelperContext ctx);
    
    /**
     * @deprecated Use {@link #getDefaultContext(ClassLoader)}
     */
    public static HelperContext getContext(ClassLoader cl) {
        return INSTANCE.defaultContext(cl);
    }

    @Deprecated
    public static void deserializeContextInto(Reader reader, HelperContext ctx) {
        INSTANCE.deserializeCxtInto(reader,ctx);
    }

}
