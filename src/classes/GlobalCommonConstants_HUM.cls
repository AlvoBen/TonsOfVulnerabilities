/********************************************************************************************************************************************************
Apex Class Name  : GlobalCommonConstants_HUM
Version          : 1.0 
Created Date     : MAY 04 2021
Test Class       : GlobalCommonConstants_HUM
Function         : Constants declaration here that are going to be used across all Products.
Modification Log :       
* Developer                       Code review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Ajay Chakradhar                           		      05/05/2021	         Original Version REQ- 2184294
* Prudhvi Pamarthi                                        06/04/2021             Added common constants
* Pooja Kumbhar							                  06/07/2021			 User Story 2270747: T1PRJ0002606/PR00094268 - MF1 - Architectural Remediation - Case Management Global Constant Class Changes - Case History
*******************************************************************************************************************************************************/
public class GlobalCommonConstants_HUM {
    
    Public Static final String STRING_TRUE = 'true';
    Public Static final String STRING_FALSE = 'false';
    Public Static final String CONTENT_TYPE_HUM = 'content-type';
    Public Static final String CONNECTION_HUM = 'Connection';
    Public Static final String KEEP_ALIVE_HUM = 'keep-alive';
    Public Static final String SOAPACTION_HUM = 'SOAPAction';
    Public Static final Integer Service_Timeout_HUM  = 120000;
    Public Static final Integer SERVICETIMEOUT = 120;    
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String STRING_YES = 'yes';
    Public Static final String STRING_NO = 'no';
    Public Static final String AUTHDETAIL_PROVIDER_HUM = 'Provider';
    Public Static final String BACKSLASH_HUM = '/';
}