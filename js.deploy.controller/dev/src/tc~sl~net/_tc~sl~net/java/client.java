public class Client
{
	public static void main(String[] args)
	{
    Integer Port = null;
    int port = 0;
    String host = null;
    
    
    if (1 == args.length || 2 == args.length) {
      try {
        Port = new Integer(args[0]);
        port = Port.intValue();
      }
      catch (NumberFormatException nfe) {
        System.err.println("Error: The first argument has to be a port NUMBER!");//$JL-SYS_OUT_ERR$
        System.exit(8);
      }
      
      if (2 == args.length) {
        host = args[1];
      }
      com.sap.bc.cts.tp.Client.Client c = null;
      if (null == host) {
        System.out.println("Starting Client on port " + port); 
        c =new com.sap.bc.cts.tp.Client.Client(port);
        c.setPrompt("Your entry > ");
      } else {
        System.out.println("Starting Client on port " + port + " for server " + host); 
        c = new com.sap.bc.cts.tp.Client.Client(port,host,"Your entry>");
      }
      c.beserved();
      
    } else {
      System.out.println("Usage: Client <port>");
      System.out.println("  or");
      System.out.println("       Client <port> <server>");
      System.exit(8);
    }
	}
}
