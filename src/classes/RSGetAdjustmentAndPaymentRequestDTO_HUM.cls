/******************************************************************************************************************
Apex class Name    : RSGetAdjustmentAndPaymentRequestDTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource GetAdjustmentsAndPayments
Created On         : 02/01/2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Rajesh Chandgothia            60688                   02/01/2017            Original Version -> REQ - 296897
*    1.1                Rajesh Keswani            	  93585	                  05/12/2017            Offline mode including IncludeStaledata -> REQ - 313437
*    1.2                Rajesh Keswani            	  112036               	  07/17/2017            Adding networkID in service Request
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RSGetAdjustmentAndPaymentRequestDTO_HUM
{
    public AdjustmentsAndPaymentsRequest GetAdjustmentsAndPaymentsRequest;
    
    public class AdjustmentsAndPaymentsRequest 
    {
        public String EnterprisePersonID = '';
        public String IncludeStaleData = '';  
        public String StartDate = '';    
        public String EndDate = '';  
        public String IncludePayments = '';
        public String IncludeAdjustments = '';  
        public String CustomerServiceId = '';
    }   
}