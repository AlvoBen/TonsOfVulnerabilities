/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderSummary_DTO_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : DTO Class for getOrder summary. 

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani              58175                  12/22/2016                  Original Version
* Rajesh Keswani              74554  	             04/27/2017                  5/11 merge offline mode
* Rajesh Chandgothia          114002  	             07/24/2017                  Req 323485
*********************************************************************************************************************************/

public with sharing class PharmacyOrderSummary_DTO_HUM
{
    public List<PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM> dtoList = new List<PharmacyOrderSummary_DTO_HUM.PharmacyOrderSummary_wrapper_HUM>();
    public string staleData ='';
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
        Public String OCP = '';
        public String orderType ='';
        Public String OrderStatusLiteral = '';
        public String OrderStatus = '';
        Public string OrderShipDate = '';
    }
}