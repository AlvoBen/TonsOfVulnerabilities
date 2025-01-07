/******************************************************************************************************************
Apex class Name    : RightSourceGetOrderRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource GetOrder
Created On         : December 9 2016
Test Class         : RightSource_T_HUM 

Modification Log:
*   Version				Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0  				Vishal Verma			                     12/09/2016             Original Version -> REQ - 291073
*    1.1                Rajesh Keswani            	93585		     05/12/2017             Offline mode including IncludeStaledata -> REQ - 313437
*    1.2 				Rajesh Keswani              112036	         07/20/2017             Implementation of REQ - 323389 - adding networkId in service request
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceGetOrderRequest_DTO_HUM
{
	public OrdersRequest GetOrdersRequest;
	
	public class OrdersRequest 
	{
		public String EnterprisePersonID = '';	 
		public String OrderStartDate = '';	
		public String OrderEndDate = '';
		public String IncludeStaleData = '';	
		public String CustomerServiceId = '';
	}
}