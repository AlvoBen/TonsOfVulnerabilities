/******************************************************************************************************************
Apex class Name    : QueueRoutingConfigDeleteResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for queue routing config service delete
Created On         : 12/18/2017
Test Class         : OmniRoutingConfigurationsEdit_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Syed Mubarak                                  12/12/2017            Original version

------------------------------------------------------------------------------------------------------------------- */
public with sharing class QueueRoutingConfigDeleteResponse_DTO_HUM

{
    public DeleteResponseDTO DeleteResponse;
    
    public  class DeleteResponseDTO
    {
    public RecordData Records;
    }
    
    public class RecordData
    {
        public RecordDataList[] Result;
        
    }
    
    public class RecordDataList
    {
        public String Id ='';
        public String Success = '';
        public ErrorResponse Errors;
    }
    
    public class ErrorResponse
    {
        public String Fields = '';
        public String Message = '';
        public String StatusCode = '';
    }


}