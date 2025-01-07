package com.sap.bc.cts.tp.net;
 
public interface ManagerInfo
{
    /**
     * Whoever wants to be notified about the inner storage of the Manager
     * should implement this interface and pass an instance of that class with
     * the constructor of the manager
     */

    /**
     * The Manager will call this method when a Property was added or has changed 
     */
    public String setProperty(String _name,String _value);
	

    /**
     * The Manager will call this method when a Connection was added or has changed 
     */
    public String setConnection(String _host, 
				String _port, 
				String _localport, 
				String _clientinfo);
	

    /**
     * The Manager will call this method when a Connection was deleted
     */
    public String delConnection(String _host, 
				String _port);
	

    /**
     * The Manager will call this method when a Servant was added or has changed 
     */
     public String setServant(String _servant);
	

    /**
     * The Manager will call this method when a Servant was deleted
     */
    public String delServant(String _servant);
	


		public void check();
}
