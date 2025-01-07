package com.sap.ni;


public class NI
{
    //
    // NI-Version
    //
    public static int VERSION = 35;

    //
    // NiHandle-Talkmodes
    //
    public static byte NI_TALK_NI = 0;
    public static byte NI_TALK_NATIVE = 1;
    public static byte NI_TALK_NATIVE2 = 2;

    //
    // NI Control messages
    //
    public static byte [] NI_PING = {78,73,95,80,73,78,71,0 };
    public static byte [] NI_PONG = {78,73,95,80,79,78,71,0 };
    public static byte [] NI_RTERR= {78,73,95,82,84,69,82,82,0};

    //
    // NI Opcodes
    //
    protected static byte NIOP_NOOP = 0;
    protected static byte NIOP_VERSION = 1;
    protected static byte NIOP_VERSREP = 2;
    protected static byte NIOP_PING = 3;
    protected static byte NIOP_PASS_HDL = 4;
    protected static byte NIOP_PASSREPLY = 5;
    protected static byte NIOP_PASSOK = 6;
    protected static byte NIOP_PASS_HDL_LISTEN = 7;
    protected static byte NIOP_PASS_HDL_EX = 8;    
    

    public static byte [] netInt(int len)
	{
	    byte [] b = new byte [4];

	    b[3] = (byte)(len & 0xff);
	    b[2] = (byte)((len >> 8) & 0xff);
	    b[1] = (byte)((len >> 16) & 0xff);
	    b[0] = (byte)((len >> 24) & 0xff);

	    return b;
	}
/*    public static int hostInt ( byte [] b )
	{
	    int i;

	    i  = (b[0] << 24)
		+ (b[1] << 16)
		+ (b[2] << 8)
		+ b[3];
	    return i;

	}*/
     public static int hostInt ( byte [] b )
	{
	    int i;

	    i  = (int ) ((b[0] << 24)          |
                         ((b[1] & 0xff) << 16) |
                         ((b[2] & 0xff) << 8)  |
                         (b[3] & 0xff));
	    return i;

	}

}



