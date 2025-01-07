/******************************************************************************************************************
Apex class Name    : RightSourceGetOrderDetResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for RightSource GetOrderDetails Service
Created On         : January 10 2017
Test Class         :  

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* ---------------------------------------------------------------------------------------------------------------------------------                
*    1.0                Harshith Mandya                              10/01/2017             Original Version -> REQ - 291162
*    1.1                Sharan Shanmugam                             02/09/2017             Modified as part of REQ - 299420 
*    1.2                Sumit Pharande                               03/15/2017             Modified as part of REQ - 304862-Added AddressKey
*    1.3                Sumit Pharande                               03/22/2017             Modified as part of REQ - 304862-Added Changedorder
*    1.4                Sumit Pharande                               30/05/2017             Added OrderReleaseDate as part of Defect Fix 287189.0001
*	 1.5				Riya Agarwal								 08/18/2020				REQ - 760189-PR00091606 - MF 4 - Finance Code Order Detail Page 
* 	 1.6				Pallavi Shewale							         06/08/2021				 US-1491784 Changes - Finance Queue
*	 1.7				Abhishek Mangutkar							 01/06/2021				REQ - 2315809 - Omni-Carrier HP Solutions - HP CRM FedEx Shipment Method Changes
---------------------------------------------------------------------------------------------------------------------------------*/

public with Sharing class RightSourceGetOrderDetResponse_DTO_HUM 
{
    public class Addresses 
    {
	    public String AddressKey ='';
        public String AddressLine1 ='';
        public String AddressLine2 = '';
        public String AddressName = '';
        public String City = '';
        public String StateCode = '';
        public String ZipCode = '';
    }

    public class OrderItems 
    {
        public CoPayCost CoPayCost;
        public String ScriptKey = '';
        public String ExternalOrderKey = '';
        public String OrderItemStatus = '';
        public String OrderItemStatusLiteral = '';
        public List<Items> Items;
        public String Strength = '';
        public PlanCost PlanCost; 
		public ChangedOrder ChangedOrder;
    }

    public class CoPayCost 
    {
        public String ItemAmount = '';
        public String TaxAmount = '';
    }

    public GetOrderDetailResponse GetOrderDetailResponse;

    public class ShippingInformation 
    {
        public String ShippingMethod = '';
        public String ShippingTrackingNumber = '';
        public String DeliveryInstructions = '';
		public String ShipperType = '';
    }
    
    public class Queues 
    {
        public  List<Queue> Queue;
    }
    public class Queue 
    {
        public String QueueName = '';
        public String QueueUser = '';
        public String QueueDate = '';
		public QueueDetails QueueDetails;
    }
	public class CurrentQueueDetails 
    {
        Public List<QueueDetail> QueueDetail;
    }
	
	public class QueueDetails
    {
        Public List<QueueDetail> QueueDetail;
    }
    public class QueueDetail
    {
        public String scriptKey = '';
        public String exceptionIdentifier = '';
        public String Description = '';
    }
	public class ChangedOrder 
    {
        public String ChangedOrderKey = '';
        public String ChangedOrderType = '';
        public String ChangedOrderTypeLiteral = '';
        public String ChangedReasonCode = '';
    }


    public class GetOrderDetailResponse 
    {
        public String OrderKey = ''; 
        public String ExternalOrderKey = ''; 
        public String OrderCreationDate = ''; 
        public String OrderShipDate = '';
		public String OrderReleaseDate = '';
        public String CurrentQueue = '';
        public String CurrentQueueDate = '';
		public CurrentQueueDetails CurrentQueueDetails;
        public String OrderSource = '';
        public String CancelReasonCode = '';
        public String CreditCardKey = '';
        public Queues Queues;       
        public ShippingInformation ShippingInformation;
        public Addresses Addresses;
        public String FirstName = '';
        public String LastName = '';
        public List<OrderItems> OrderItems;
        public CreditCardsDetail CreditCards;
        public String StaleData = '';
		public String exceptionIdentifier = '';
        
    }
   

    public class Items 
    {
        public String ItemType = '';
        public String ItemLabel = '';
    }
    
    public class CreditCardsDetail 
    {
        public List<CreditCardDetail> CreditCard;
    }
    
    public class CreditCardDetail
    {
        public String CreditCardKey = '';
        public String CreditCardType = '';
        public String CreditCardTypeLiteral = '';
        public String FirstName = '';
        public String LastName = '';
        public String MiddleName = '';
        public String ExpirationMonth = ''; 
        public String ExpirationYear = '';
        public String IsActive = '';    
        public String IsAutoCharge = '';    
        public String CreditCardLast4Digits = '';  
        public String IsSpendingAccount = '';
    }
    
     public class PlanCost
    {
        public String ItemAmount = '';
        public String TaxAmount = '';
    }
   
    
}