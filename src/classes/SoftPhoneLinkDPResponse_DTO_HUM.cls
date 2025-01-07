/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneLinkDPResponse_DTO_HUM 
Version             : 1.0
Created Date        : June 12 2020
Function            : DTO class for agent details sync
Test Class 			: SoftPhoneSyncQueueable_T_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Akshay Pai                                    		06/12/2020                  Initial Version
* Akshay Pai                                            09/04/2020                  REQ-1442568 TEST COVERAGE FIX
*******************************************************************************************************************************/
public with sharing class SoftPhoneLinkDPResponse_DTO_HUM 
{
    public List<unsyncedAgentDP> RecordsInDB = new List<unsyncedAgentDP>();

    public class unsyncedAgentDP 
    {
        public String AgentId;
        public String UserId;
        public String SiteName;
		public String DefaultSoftphoneLinkName;
		public String AlternateSoftphoneLinkName;
        public String AgentACDName;
		
    }
}