package com.sap.security.api.vault;

import java.security.AccessControlException;
import java.util.Hashtable;
import java.io.Serializable;
import java.io.IOException;

/**
 *  Secure Storage
 *  Facility to store passwords and stuff. Only for usage within SAP software.
 *  Usage is protected and not possible for non-SAP java implementations.
 *  <p>
 *  In order to use the class in your software, first get an instance. There
 *  is (hopefully) always the default implementation which you can get by
 *  a call to the {@link #getInstance()} method. Currently the default
 *  implementation stores the data into a file. There might be other engines
 *  registered, but this depends on the context.
 */
public abstract class SecureStorage
{
    private static  String                  default_key = "DEFAULT";
    private static  Hashtable               engines     = new Hashtable (5);

    public static final String STORE_FILE_NAME_KEY = "ume.secstore.filename";
    public static final String PORTAL_STORAGE_KEYTYPE_ALIAS = "ume.secstore.keytype";
    public static final String PORTAL_STORAGE_ALGO_ALIAS = "ume.secstore.algorithm";

    protected SecureStorage ()
    {
    }

    public static void registerStorage (String key, Object storage)
        throws IllegalArgumentException
    {
        engines.put (key, storage);
    }

    public static void unregisterStorage (String key)
    {
        engines.remove (key);
    }

    public static SecureStorage getInstance (String key)
    {
        return (SecureStorage) engines.get (key);
    }

    public static SecureStorage getInstance ()
    {
        return (SecureStorage) engines.get (default_key);
    }

    public abstract void addEntry (String id, Serializable s, Class [] classes)
        throws IOException;

    public abstract Serializable readEntry (String id)
        throws AccessControlException, IOException, InvalidFormatException;

    public abstract void changeEntry (String id, Serializable toChange)
        throws AccessControlException, IllegalArgumentException, IOException;

    public abstract void deleteEntry (String id)
        throws AccessControlException, IOException;
}
//
//final class SecureStorageImplWrapper
//{
//    private Object m_obj;
//    private Method m_addEntry;
//    private Method m_readEntry;
//    private Method m_changeEntry;
//    private Method m_deleteEntry;
//
//    byte [] b  = new byte [0];
//    Class[] cs = new Class [0];
//    Class   str= "java.lang.String".getClass();
//    Class   bA  = null;
//
//
//
//
//    SecureStorageImplWrapper (Object storageObj)
//        throws IllegalArgumentException
//    {
//        m_obj = storageObj;
//
//        try {
//            bA = Class.forName ("java.io.Serializable");
//
//            m_addEntry = m_obj.getClass().getMethod ("addEntry",
//                                                     new Class [] { str,
//                                                                    bA,
//                                                                    cs.getClass() });
//            m_readEntry= m_obj.getClass().getMethod ("readEntry",
//                                                     new Class [] { str });
//
//            m_changeEntry = m_obj.getClass().getMethod ("changeEntry",
//                                                        new Class [] { str, bA });
//
//            m_deleteEntry = m_obj.getClass().getMethod ("deleteEntry",
//                                                        new Class [] { str });
//        }
//        catch (Exception e) {
//            e.printStackTrace ();
//            throw new IllegalArgumentException ("provided argument was not of the correct" +
//                                                " implementation class.");
//        }
//    }
//
//    public void addEntry (String id, byte [] content, Class [] classes)
//        throws AccessControlException, IllegalArgumentException, IOException
//    {
//        try {
//            m_addEntry.invoke (m_obj, new Object [] {id,content,classes});
//        }
//        catch (InvocationTargetException e) {
//            Throwable exc = e.getTargetException ();
//            if (exc instanceof AccessControlException)
//                throw (AccessControlException)exc;
//            if (exc instanceof IllegalArgumentException)
//                throw (IllegalArgumentException)exc;
//            if (exc instanceof IOException)
//                throw (IOException)exc;
//
//            exc.printStackTrace ();
//            throw new RuntimeException ("Unexpected exception");
//        }
//        catch (IllegalArgumentException ee) {
//            throw ee;
//        }
//        catch (IllegalAccessException eee) {
//            eee.printStackTrace ();
//            throw new RuntimeException ("Method can't be accessed.");
//        }
//    }
//
//    public Serializable readEntry (String id)
//        throws AccessControlException, IOException, InvalidFormatException
//    {
//        try {
//            return (Serializable) m_readEntry.invoke (m_obj, new Object [] {id});
//        }
//        catch (InvocationTargetException e) {
//            Throwable exc = e.getTargetException ();
//            if (exc instanceof AccessControlException)
//                throw (AccessControlException)exc;
//            if (exc instanceof IOException)
//                throw (IOException)exc;
//            if (exc instanceof InvalidFormatException)
//                throw (InvalidFormatException)exc;
//
//            exc.printStackTrace ();
//            throw new RuntimeException ("Unexpected exception");
//        }
//        catch (IllegalArgumentException ee) {
//            throw ee;
//        }
//        catch (IllegalAccessException eee) {
//            eee.printStackTrace ();
//            throw new RuntimeException ("Method can't be accessed.");
//        }
//    }
//
//    public void changeEntry (String id, Serializable toChange)
//        throws AccessControlException, IllegalArgumentException, IOException
//    {
//        try {
//            m_changeEntry.invoke (m_obj, new Object [] {id,toChange});
//        }
//        catch (InvocationTargetException e) {
//            Throwable exc = e.getTargetException ();
//            if (exc instanceof AccessControlException)
//                throw (AccessControlException)exc;
//            if (exc instanceof IllegalArgumentException)
//                throw (IllegalArgumentException)exc;
//            if (exc instanceof IOException)
//                throw (IOException)exc;
//
//            exc.printStackTrace ();
//            throw new RuntimeException ("Unexpected exception");
//        }
//        catch (IllegalArgumentException ee) {
//            throw ee;
//        }
//        catch (IllegalAccessException eee) {
//            eee.printStackTrace ();
//            throw new RuntimeException ("Method can't be accessed.");
//        }
//    }
//
//    public void deleteEntry (String id)
//        throws AccessControlException, IOException
//    {
//        try {
//            m_deleteEntry.invoke (m_obj, new Object [] {id});
//        }
//        catch (InvocationTargetException e) {
//            Throwable exc = e.getTargetException ();
//            if (exc instanceof AccessControlException)
//                throw (AccessControlException)exc;
//            if (exc instanceof IOException)
//                throw (IOException)exc;
//
//            exc.printStackTrace ();
//            throw new RuntimeException ("Unexpected exception");
//        }
//        catch (IllegalArgumentException ee) {
//            throw ee;
//        }
//        catch (IllegalAccessException eee) {
//            eee.printStackTrace ();
//            throw new RuntimeException ("Method can't be accessed.");
//        }
//    }
//}
