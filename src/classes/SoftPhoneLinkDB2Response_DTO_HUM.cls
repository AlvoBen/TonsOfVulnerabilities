/******************************************************************************************************************
Apex class Name    : SoftPhoneLinkDB2Response_DTO_HUM
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
public with sharing class SoftPhoneLinkDB2Response_DTO_HUM 
{
    public List<softPhoneLinkDB2> insertList;
    public List<softPhoneLinkDB2> updateList;
    public List<softPhoneLinkDB2> deleteList;
    public List<softPhoneLinkDB2> syncList;

    public class softPhoneLinkDB2 
    {
        public String AgentId='';
        public String UserId='';
        public String ErrorDesc='';
    }
}