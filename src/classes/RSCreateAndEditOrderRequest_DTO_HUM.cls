/******************************************************************************************************************
Apex class Name    : RSCreateAndEditOrderRequestt_DTO_HUM 
Version            : 1.0
Function           : Request DTO for RightSource EditOrder
Created On         : January 4 2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Samridh Manucha                                 1/04/2017           Original Version -> REQ - 291078
*    1.1                Sumit Pharande                                  3/01/2017           Update CreateAndEditOrder Method -> REQ - 303403
*    1.2                Sumit Pharande                                  3/11/2017           Changed class moveToRouting  -> REQ - 303403
*    1.3                Sumit Pharande                                  3/01/2017           Added OrderReleaseDate,consentDateTime-> REQ - 304862
*    1.4                Rajesh Keswani               112036             3/01/2017           Added NetworkID in service request
*    1.5                Shailesh Mali				 343018		        3/05/2019           REQ-386790, Added PaymentMethod type
*    1.6		        Pallavi Shewale					                06/08/2021           US-1491784 Changes - Finance Queue
------------------------------------------------------------------------------------------------------------------*/

public with sharing class RSCreateAndEditOrderRequest_DTO_HUM 
{
    public CreateAndEditOrderRequest CreateAndEditOrderRequest;
    
    public class CreateAndEditOrderRequest
    {
        public String EnterprisePersonID ='';   
        public String orderKey = '';
        public String cancelOrder = ''; 
        public String shippingMethodCode = '';  
        public String deliveryInstruction = ''; 
        public MoveToRouting moveToRouting;
        public String orderPlatform = '';
        public String cancelReasonCode = '';    
        public Address address;
        public CreditCard creditCard;
        public ScriptOrders scriptOrders;
        public String orderReleaseDate = '';
        public String CustomerServiceId = '';
        public List<PaymentMethods> paymentMethod;
		public boolean overrideQueueConsent;
		public string userDefined1 = '';

    }
    
    public class MoveToRouting
    {
        public list<String> orderKey;
    }
    
    public class OrderKey 
    {
    
    }
    
    public class Address
    {
        public String addressKey = '';  
        public String addressLine1 = '';    
        public String addressLine2 = '';    
        public String addressType = ''; 
        public String addressName = '';
        public String city = '';    
        public String isActive = '';    
        public String overrideReasonCode = '';  
        public String stateCode = '';   
        public String uspsValidate = '';    
        public String zipCode = ''; 
    }
    
    public class CreditCard 
    {
        public String creditCardKey = '';   
        public String creditCardType = '';  
        public String expirationMonth = ''; 
        public String expirationYear = '';  
        public String firstName = '';   
        public String isActive = '';    
        public String isAutoCharge = '';    
        public String lastName = '';    
        public String middleName = '';  
        public String tokenKey = '';    
    }
    
    public class ScriptOrders 
    {
        public list<Script> script;
    }
    
    public class Script 
    {
	    public String consentDateTime ='';
        public String scriptKey = '';
        public String consentStatus = ''; 
		public boolean coPayConsent;		
    }
    
    public class PaymentMethods
    {
     public string paymentMethodType = '';
    }
    
}