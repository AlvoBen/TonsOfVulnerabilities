package com.sap.security.api.session;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.security.api.ticket.InfoUnit;

/**
 * @deprecated Must not be used any longer.
 */
public abstract class SessionFactory
{
    public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/session/SessionFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";
//    private static IUMTrace trace = InternalUMFactory.getTrace(VERSIONSTRING);

    public static final String SAP_USER_ID     = "com.sap.security.user_id";
    public static final int    SAP_AUTHSCHEME_INFOUNITID = InfoUnit.ID_AUTHSCHEME;

    public static          SessionFactory   getInstance (Properties conf)
        throws ClassNotFoundException
    {
        String sess_fac_class_str = conf.getProperty("login.session.factory",
                "com.sap.security.core.session.imp.DefaultSAPSessionFactory");

        Class sess_fac_class = null;
        SessionFactory fac = null;
        ClassLoader cl = null;

        try {

            // First get correct class loader
            // We might be in an API package but we need to load
            // classes from the core package
            cl = Thread.currentThread().getContextClassLoader();

            // Then instantiate class
            sess_fac_class = Class.forName(sess_fac_class_str, true, cl);

            Class[] args = new Class[] { conf.getClass() };
            fac = (SessionFactory)(sess_fac_class.getMethod("getInstance", args).invoke(null, new Object[] {
                conf
            }));
        } catch (ClassNotFoundException cnfe) {

            // Throw an error in order to really stop further processing.
            throw new NoClassDefFoundError(cnfe.toString());
        }
         catch (InvocationTargetException ite) {
            ite.printStackTrace ();
            throw new NoClassDefFoundError(ite.toString());
        }
         catch (NoSuchMethodException nsme) {
            throw new NoClassDefFoundError(nsme.toString());
        }
         catch (SecurityException se) {
            throw new NoClassDefFoundError(se.toString());
        }
         catch (ClassCastException cce) {
            throw new NoClassDefFoundError(cce.toString());
        }
         catch (IllegalAccessException cce) {
            throw new NoClassDefFoundError(cce.toString());
        }

        return fac;
    }

    public        abstract ISSOSession      getSSOSession (HttpServletRequest req,
                                                            HttpServletResponse resp);
}
