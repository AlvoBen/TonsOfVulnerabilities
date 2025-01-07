/******************************************************************************************************************
Apex class Name    : RightSourceEditMemberResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for RightSource Edit member
Created On         : 02/02/2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Rajesh Chandgothia           60688           02/01/2017            Original Version -> REQ - 291077
*    1.1                Rajesh Keswani           	 66068	         02/17/2017            added UID -> REQ - 301418 
*    1.2                Melkisan Selvaraj							 03/04/2019            REQ - 387544 : Splunk error logging Issues
*    1.3                Sumit Pharande               345615          03/27/2019            REQ - 362063 - Payment Card Project: do not allow users to inactivate a cc that�s attached to an order in progress
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceEditMemberResponse_DTO_HUM
{
    public EditMemberResponse EditMemberResponse;
    
    public class EditMemberResponse
    {
        public String transactionKey = '';  
        public String transactionTimeStamp = '';
        public String UID = '';    
		public String ErrorDescription = '';          
        public Orders Orders ;
        public String Error ='';
    }
    
     public class Orders 
    {
        public  List<Order> Order;
    }
    public class Order
    {
        public String orderKey= '';
        public String orderStatus= '';
        public String orderPlatform= '';
    }
  
}