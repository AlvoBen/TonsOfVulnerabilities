/******************************************************************************************************************
Apex class Name    : QueueRoutingConfigUpdateRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for queue routing config service update
Created On         : 12/18/2017
Test Class         : OmniRoutingConfigurationsEdit_T_HUM


Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0             Syed Mubarak                                  12/12/2017            Original version

------------------------------------------------------------------------------------------------------------------- */
public with sharing class QueueRoutingConfigUpdateRequest_DTO_HUM

{
    public UpdateRequestDTO UpdateRequest;
    
    public  class UpdateRequestDTO
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
        public String Type ='';
        public String DeveloperName = '';
        public String CapacityWeight = '';
        public String MasterLabel = '';
        public String PushTimeout = '';
        public String RoutingModel = '';
        public String RoutingPriority = '';
		public String OverflowAssigneeId = '';
		public String CapacityPercentage = '';
    }


}