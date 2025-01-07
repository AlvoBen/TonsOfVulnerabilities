/******************************************************************************************************************
Apex class Name    : QueueRoutingConfigDeleteRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for queue routing config service delete
Created On         : 12/18/2017
Test Class         : OmniRoutingConfigurationsEdit_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Syed Mubarak                                  12/12/2017            Original version

------------------------------------------------------------------------------------------------------------------- */
public with sharing class QueueRoutingConfigDeleteRequest_DTO_HUM

{
    public DeleteRequestDTO DeleteRequest;
    
    public  class DeleteRequestDTO
    {
    public RecordData Records;
    }
    
    public class RecordData
    {
        public RecordDataList[] Record;
        
    }
    
    public class RecordDataList
    {
        public String Id = '';
    }


}