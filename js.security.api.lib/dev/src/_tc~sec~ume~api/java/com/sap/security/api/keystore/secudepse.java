package com.sap.security.api.keystore;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import iaik.asn1.SEQUENCE;
import iaik.asn1.ASN1Object;
import iaik.asn1.DerCoder;
import iaik.asn1.SET;
import iaik.asn1.INTEGER;
import iaik.asn1.OCTET_STRING;
import iaik.asn1.ObjectID;
import iaik.asn1.PrintableString;
import iaik.asn1.UTCTime;
import iaik.x509.X509Certificate;
import iaik.asn1.structures.Name;

/** This class is a Java wrapper around a SECUDE PSE keystore type.
 *  The author has reengineered the encoding of secude PSEs and written
 *  this class.
 *  The following is currently not supported:
 *  <ul>
 *    <li> Encryption of PSEs. The class can only read and create
 *         unencrypted PSEs.
 *    <li> Private keys, own certificates. Currently, only PKList is
 *         supported as PSE object. Please note that the PSE type
 *         only stores the ToBeSigned part of a certificate, therefore
 *         no export of formerly imported certificates is possible.
 *  </ul>
 */
public class secudePSE
{
    public final static String VERSIONSTRING="$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/keystore/secudePSE.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

    private char [] password;
    private Hashtable objects ;

    static final INTEGER  integer = new INTEGER (2);
    static final ObjectID oid     = new ObjectID ("1.2.840.113549.3.7", "", "");
    static final OCTET_STRING os  = new OCTET_STRING (new byte [] { 0x00, 0x00,
                                                                    0x00, 0x00,
                                                                    0x00, 0x00,
                                                                    0x00, 0x00 });
    static final PrintableString user = new PrintableString ("SYSTEM");
    static final INTEGER  integer2    = new INTEGER (1);
    static final SEQUENCE oidseq      = new SEQUENCE ();

    static {
        try {
            oidseq.addComponent (oid, 0);
            oidseq.addComponent (os, 1);
        }
        catch (Exception e) {
//          $JL-EXC$
        }
    }


    /** Encodes the current secudePSE object into a stream.
     *  The encoding is as follows:
     *  <PRE>
     *  verify_pse ::= SEQUENCE {
     *      INTEGER = 2
     *      SEQUENCE {
     *          SEQUENCE {
     *              OBJECT ID = 1.2.840.113549.3.7
     *              OCTET_STRING = 00:00:00:00:00:00:00:00
     *          }
     *          PrintableString = "SYSTEM"
     *          UTCTIME = 011122093252Z
     *          INTEGER = 1
     *          // Here comes the list of the objects
     *          SET {
     *              [PKList,Cert,SKnew,...]
     *          }
     *      }
     *  }
     *  </PRE>
     *  @param os OutputStream that is written into.
     */
    public synchronized void encode (OutputStream os)
        throws IOException
    {
        SET      set= new SET ();
        UTCTime  utc= new UTCTime ("020101000000Z");
        SEQUENCE outerSequence = new SEQUENCE ();
        SEQUENCE otherSequence = new SEQUENCE ();
        
        otherSequence.addComponent (oidseq, 0);
        otherSequence.addComponent (user, 1);
        otherSequence.addComponent (integer2, 2);        

        Enumeration e = objects.keys ();

        while (e.hasMoreElements ()) {
            secudePSEObject obj = (secudePSEObject) objects.get(e.nextElement ());

            set.addComponent (obj.getObject ());
        }

        otherSequence.addComponent (utc, 2);
        otherSequence.addComponent (set, 4);

        outerSequence.addComponent (integer, 0);
        outerSequence.addComponent (otherSequence, 1);

        DerCoder.encodeTo (outerSequence, os);
    }

    /** Contructor. Creates an empty secudePSE object.
     *
     */
    public secudePSE ()
    {
        password = null;
        objects  = new Hashtable (10);
    }

    /** Removes a formerly added element to the PSE.
     *
     */
    public secudePSEObject removeObject (String name)
    {
        return null;
    }

    /** Adds an object to the PSE. Currently, the type
     *  of the object *must* be <i>PKList</i>.
     *  @param object PSE object to be added.
     */
    public void addObject (secudePSEObject object)
    {
        if ("PKList".equals (object.getName())==false) {
            throw new RuntimeException ("Operation not supported");
        }

        objects.put (object.getName(), object);
    }

    /** Returns an implementation of the {@link PKList}PKList interface.
     *  @return an implementation of PKList.
     */
    public static PKList getPKList ()
    {
        return new PKListImpl ();
    }

    /** Test program. Type <PRE>$JAVA_HOME/bin/java -cp <jarfiles> com.sap.security.api.keystore.secudePSE</PRE> for details.
     */
    public static void main (String args[])
    {
        if (args.length==0) {
            System.out.println ("Usage: secudePSE type<n>");
            return ;
        }

        if (args[0].equals ("type1")) {
            if (args.length<3) {
                printType1Usage ();
                return ;
            }

            secudePSE pse = new secudePSE ();

            PKList pklist = secudePSE.getPKList ();
            try {
                pklist.addCertificate (new X509Certificate (new FileInputStream (args[1])));
                pse.addObject (pklist);
                FileOutputStream fos = new FileOutputStream (args[2]);
                pse.encode (fos);
                fos.close ();
                System.out.println("PSE successfully created.");
            }
            catch (Exception e) {
//              $JL-EXC$
                e.printStackTrace ();
                return ;
            }
        }
        else {
            System.out.println("Unknown type.");
        }
    }

    public static void printType1Usage ()
    {
        System.out.println("Usage: secudePSE type1 <DER encoded certificate> <pse file name>");
    }
}

class PKListImpl implements PKList
{
    private static final String   name    = "PKList";
    private static final ObjectID pkloid  = new ObjectID ("1.3.36.2.6.1", "", "");

    private Hashtable   certs = null;
    private ASN1Object  asn   = null;

    PKListImpl ()
    {
        certs = new Hashtable (10);
    }

    public String getName ()
    {
        return name;
    }

    public void     addCertificate (X509Certificate cert)
    {
        certs.put (cert.getSubjectDN().toString(), cert);
    }

    public void     removeCertificate (String subject)
    {
        certs.remove (subject);
    }

    public synchronized ASN1Object getObject   ()
    {
        if (asn!=null)
            return asn;

        int i=0;

        SEQUENCE s = new SEQUENCE ();
        PrintableString str = new PrintableString (name);
        SET     ss = new SET ();

        Enumeration keys = certs.keys ();

        s.addComponent (str, i++);
        s.addComponent (pkloid, i++);

        while (keys.hasMoreElements()) {
            SEQUENCE cert = (SEQUENCE)
                              ((X509Certificate)certs.get (keys.nextElement())).toASN1Object();
            ss.addComponent (cert.getComponentAt(0), 0);
        }

        s.addComponent (ss, i++);

        this.asn = s;

        return s;
    }

    public byte []   getEncoded  ()
    {
        if (asn==null) {
            getObject ();
        }

        return DerCoder.encode (asn);
    }

    public void removeCertificate (Name subject)
    {
    }
}
