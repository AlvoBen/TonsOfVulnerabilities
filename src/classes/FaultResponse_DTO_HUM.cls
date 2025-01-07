/************************************************************************************************************************************************
  Apex Class Name  : FaultResponse_DTO_HUM
  Version          : 1.0
  Created Date     : JULY 27 2017
  Function         : Fault Response class to serialize Pharmacy Faults. 
  Test Class       : 
  *************************************************************************************************************************************************

  Modification Log:
  * Modification Id           Developer Name             Review Number               Date                       Description
  *------------------------------------------------------------------------------------------------------------------------------------------------------------------
  * 1.0                       Vishal Verma 					114200				       07/27/2017				   Original Version : REQ - 323542 : Exception handling   
 ***************************************************************************************************************************************************/
public with sharing class FaultResponse_DTO_HUM 
{
	public Fault Fault;
	public class Fault 
	{
		public String faultcode;	
		public String faultstring;	
		public detail detail;
	}
	public class detail 
	{
		public transactionInfo transactionInfo;
		public errorInfo errorInfo;
	}
	public class transactionInfo 
	{
		public String transaction_id;	
		public String transaction_client;	
		public String URL_in;	
		public String processor_name;	
		public String processor_type;	
	}
	public class errorInfo 
	{
		public String error_code;	
		public String error_headers;	
		public String error_message;	
		public String error_protocol_reason_phrase;
		public String error_protocol_response;
		public String error_subcode;
	}	    
}