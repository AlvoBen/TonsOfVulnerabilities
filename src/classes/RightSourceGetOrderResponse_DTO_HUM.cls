/******************************************************************************************************************
Apex class Name    : RightSourceGetOrderResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for RightSource GetOrder
Created On         : December 9 2016
Test Class         : RightSource_T_HUM

Modification Log:
*   Version				Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0  				Vishal Verma			                     	 12/09/2016             Original Version -> REQ - 291073
*    1.1                Rajesh Keswani           	66068	           	 02/17/2017             Added OrderType -> REQ - 301418
*                       Rajesh Chandgothia			114002 			     07/25/2017				REQ - 323485
*    1.2                Melkisan Selvaraj							     03/04/2019             REQ - 387544 : Splunk error logging Issues
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceGetOrderResponse_DTO_HUM 
{
	public OrdersReponse GetOrdersReponse;
	
	public class OrdersReponse 
	{
		public String EnterprisePersonID = '';	
		public String OCP = '';	
		public OrdersDetail Orders;
		public String StaleData = '';
		public string ErrorDescription = '';
	}
	
	public class OrdersDetail 
	{
		public List<OrderDetail> Order;
	}
	
	public class OrderDetail 
	{
		public String OrderNumber = '';
		public String OrderType = '';	
		public String ExternalOrderKey = '';
		public String OrderCreationDate = '';	
		public String OrderReleaseDate = '';	
		public String OrderSource = '';
		public String QueueName = '';	
		public String OrderTotalAmount = '';	
		Public String OrderpayAmount = '';
		Public String OrderTaxAmount = '';
        Public String OrderStatusLiteral = '';
        Public String OrderStatus = '';
        Public String OrderShipDate = '';
	}
}