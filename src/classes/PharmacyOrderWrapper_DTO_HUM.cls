/**************************************************************************** 
Apex Class Name :   PharmacyOrderWrapper_DTO_HUM   
Version: 1.0  
Created Date     : April 28, 2017
Function         : Wrapper class for GetOrder adn OrderDetails  Pharmacy service 
****************************************************************************
Modification Log:  
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash  Choubey         87810                 04/17/2017                 REQ-305689 Original version
* Sharan Shanmugam								 07/27/2017				    REQ - 323542 : Exception handling variable
***************************************************************************************************/
Public Class PharmacyOrderWrapper_DTO_HUM
{
    Public RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objParentOrder {Get;Set;}
    Public RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objChildOrder {Get;Set;}  
    Public PharmacyOrderSummary_wrapper_HUM objOrderSummary {Get;Set;}
    Public String source {Get;Set;}
    Public String sOrderNumber {Get;Set;}
    Public String networkID {Get;Set;}
    Public String sRecordId {Get;Set;}
     
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objParentOrder Parent Order Details from First Call
    * @param  RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objChildOrder Child Order Details from Second Call
    * @param  PharmacyOrderSummary_wrapper_HUM objOrderSummary to hold order summary data
    * @return  None
    */
    Public PharmacyOrderWrapper_DTO_HUM(RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objParentOrder, RightSourceGetOrderDetResponse_DTO_HUM.GetOrderDetailResponse  objChildOrder,  PharmacyOrderSummary_wrapper_HUM objOrderSummary,String source,String sOrderNumber, String networkID, String sRecordId )
    {
      this.objParentOrder =  objParentOrder ;
      this.objChildOrder = objChildOrder;
      this.objOrderSummary = objOrderSummary;
      this.source = source;
      this.sOrderNumber = sOrderNumber ;
      this.networkID = networkID;
      this.sRecordId = sRecordId;
    }
    
    public class PharmacyOrderSummary_wrapper_HUM
	{
		public String OrderNumber = '';	
		public String OrderCreationDate = '';	
		public String OrderReleaseDate = '';	
		public String OrderSource = '';
		public String QueueName = '';	
		public String OrderTotalAmount = '';	
		public String OrderpayAmount = '';
		public String OrderTaxAmount = '';
		public String OCP = '';
		public String orderType ='';
		Public String staleData ='';
		Public String webOrderNumber = '';
        Public String OrderStatusLiteral = '';
        Public String OrderStatus = '';
        Public String OrderShipDate = '';
	}
}