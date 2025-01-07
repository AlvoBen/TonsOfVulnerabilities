package com.sap.ni;

import java.util.Vector;
import java.io.*;

public class NiRouteErr
{

    public static byte [] eyecatcher = NI.NI_RTERR;
    private       byte    version;
    private       byte    opcode;
    private       byte    alignment;
    private       int     nirc;
    private       String  message;
    private       Vector  errMsgs;
    
    public NiRouteErr( NiMsg nimsg ) throws NiException
        {
            byte [] stream = nimsg.msg;
            int     space_used;
	    DataInputStream dis =
		new DataInputStream(new ByteArrayInputStream(stream)) ;

            /*
             * read in NI_RTERR structure
             */
	    try {
		for ( int i = 0; i < eyecatcher.length; i++) {
		    if (dis.readByte() != eyecatcher[i]) {
			throw new NiException( " invalid err message " );
		    }
		}

                version =    dis.readByte ();
                opcode  =    dis.readByte ();
                alignment = dis.readByte ();
                nirc       = dis.readInt ();
                space_used = dis.readInt ();

                /*
                 * build up (fixed format) ErrMsg
                 */
                errMsgs = getErr(stream, space_used);

                /*
                 * and convert to human readable form
                 */
                message = "\n";
                for (int i = 0; i < errMsgs.size(); i++)
                    message = message + getErrField(i);
                
                
            }
            catch (IOException e) {
		System.out.println("IOException caught: " + e);
	    }
        }
    

    public String getMessage ()
        {
            return message;
        }

    public boolean isError()
        {
            if (opcode == NI.NIOP_NOOP)
            {
                return true;
            }
            return false;
        }

    public void dump()
        {
            System.out.println("version = " + version);
            System.out.println("opcode = " + opcode);
            System.out.println("nirc = " + nirc);
            System.out.println(message);
        }
    
    private String getNextString (byte [] s, int len)
        {
            int off = 20;
            return new String(s, off, len);
        }

    private String getErrField(int i)
        {
            String pref = null;
            boolean ret = false;
            
            switch (i)
            {
                case 2:
                    pref = "Description: ";
                    break;
                case 3:
                    pref = "Returncode: ";
                    break;
                case 4:
                    pref = "Component: ";
                    break;
                case 5:
                    pref = "Release: ";
                    break;
                case 6:
                    pref = "Version";
                    break;
                case 7:
                    pref = "Module: ";
                    break;
                case 8:
                    pref = "Line: ";
                    break;
                case 9:
                    pref = "Detail: ";
                    break;
                case 10:
                    pref = "Time: ";
                    break;
                case 11:
                    pref = "Syscall: ";
                    break;
                case 12:
                    pref = "Errno: ";
                    break;
                case 13:
                    pref = "ErrTxt: ";
                    break;
                case 14:
                    pref = "ErrCount: ";
                    break;
                case 15:
                    pref = "Location: ";
                    break;
                case 16:
                    pref = "Detail: ";
                    break;
                default:
                    pref = "";
                    ret = true;
                    break;
            }

            if (ret == true)
                return "";

            return new String(pref + errMsgs.elementAt(i) + "\n");
        }
    
    private Vector getErr (byte [] s, int len)
        {
            int off = 20;
            int l = len;
            Vector m = new Vector();
            String str;
            
            while(off < len)
            {
                int i;

                for (i = off; s[i] != 0 && i < len; i++)
                    ;
                str = new String(s, off, i - off);
                m.addElement(str);
                off = i + 1;
            }

            return m;
        }
    
                
            
            
            
    
    
}
                
