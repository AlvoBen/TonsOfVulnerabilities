/******************************************************************************************************************
Apex class Name    : SoftPhoneLinkDB2Request_DTO_HUM
Version            : 1.0
Function           : DTO Class for Agent Details Sync
Created On         : August 27 2019
Test Class         : SoftPhoneSync_T_HUM

Modification Log:
*   Version             Developer                  Date                     Description
* -----------------------------------------------------------------------------------------------------------------
*    1.0                SaiSree Lakhsmi            08/27/2019             Original Version
*	 2.0				Akshay Pai				   06/12/2020			  REQ-1182756 , Added synclist
------------------------------------------------------------------------------------------------------------------*/
public with sharing class SoftPhoneLinkDB2Request_DTO_HUM
{
    public List<softPhoneLinkDB2> insertlist;
    public List<softPhoneLinkDB2> updatelist;
    public List<softPhoneLinkDB2> deletelist;
    public List<syncedAgentDP> synclist;

    public class softPhoneLinkDB2
    {
        public string SiteName='';
        public string SiteCode='';
        public string DefaultSoftphoneLinkName='';
        public string AlternateSoftphoneLinkName='';
        public string AgentId='';
        public string UserId='';
    }
    
    public class syncedAgentDP 
    {
        public String UserId='';
        public String SyncInd='';				
    }
}