/******************************************************************************************************************
Apex class Name    : RightSourceGetOrderDetRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource GetOrderDetails Service
Created On         : January 10 2017
Test Class         :  

Modification Log:
*   Version				Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0  				Harshith Mandya			                     10/01/2017             Original Version -> REQ - 291162
*    1.1  				Rajesh Keswani			    112036           10/01/2017             Adding NetworkID in orderDetail Service Request
------------------------------------------------------------------------------------------------------------------*/

public with sharing class RightSourceGetOrderDetRequest_DTO_HUM 
{
    public GetOrderDetailsRequest GetOrderDetailsRequest;

    public class GetOrderDetailsRequest 
    {
        public String OrderKey = '';
        public String IncludeStaleData = '';
        public String CustomerServiceId = '';
    }
}