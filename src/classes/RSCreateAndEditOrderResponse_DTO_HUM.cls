/******************************************************************************************************************
Apex class Name    : RSCreateAndEditOrderResponse_DTO_HUM 
Version            : 1.0
Function           : Response DTO for RightSource CreateAndEditOrder
Created On         : January 4 2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Samridh Manucha                                01/04/2017           Original Version -> REQ - 291078
*    1.1                Rajesh Keswani                                 02/17/2017            added UID -> REQ - 301418 
*    1.1                Sumit Pharande                                 03/01/2017            added orderkey-> REQ - 303403
*    1.1                Sumit Pharande                                 03/13/2017            changed class name-> REQ - 303403
*    1.2                Melkisan Selvaraj						       03/04/2019              REQ - 387544 : Splunk error logging Issues
------------------------------------------------------------------------------------------------------------------*/

public with sharing class RSCreateAndEditOrderResponse_DTO_HUM 
{
    public CreateAndEditOrderResponse CreateAndEditOrderResponse;
    public class CreateAndEditOrderResponse
    {
        public String transactionKey ='';   
        public String transactionTimeStamp = '';
        public String UID = ''; 
        public String orderKey ='';   
		public string ErrorDescription = '';
    }
}