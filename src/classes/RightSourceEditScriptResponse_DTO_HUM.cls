/******************************************************************************************************************
Apex class Name    : RightSourceEditScriptResponse_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource EditScriptResponse
Created On         : March 15 2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0         Sumit Pharande                                03/15/2017             Original Version -> REQ - 303405
------------------------------------------------------------------------------------------------------------------*/

public with sharing class  RightSourceEditScriptResponse_DTO_HUM 
{
    public EditScriptResponse EditScriptResponse;
    public class EditScriptResponse
    {
        public String transactionKey ='';   
        Public String transactionTimeStamp = '';
        Public String UID =''; 
    }
}