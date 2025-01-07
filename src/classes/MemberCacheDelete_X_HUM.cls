/******************************************************************************************************************************       
Apex Class Name  : MemberCacheDelete_X_HUM 
Version          : 1.0  
Created Date     : Nov 30, 2015
Function         :"MemberCacheDelete_VF_HUM" page extension to Delete All Member Cache Records.
Test Class       : ChangeHasVitality_T_HUM
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                          Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                                  11/30/2015                    Original Version
* Raja Sekhar Reddy M V                        02/16/2018                    Code changed for resolving prod issue. Added limit 2000 in line # 52
* Prathyu Tushar          210765               04/06/2018                    As per Checkmarx REQ - 328958, Checking Profile user object delete permissions defore deleting records.
*************************************************************************************************************************/

public with sharing class MemberCacheDelete_X_HUM {
    
    public boolean blEnablePoller { get; set;}
    public boolean blEnableButton { get; set;}
    public boolean blEnableMessage { get; set;}
    public boolean blAccessMessage{get;set;}    
    
    public MemberCacheDelete_X_HUM (ApexPages.StandardController stdSetController) {
        blEnablePoller = true;
        blEnableButton = true;
        blAccessMessage = false;
    }
  
 /*
* Method Name   :    CacheDelete() 
* Description   :    Method to Delete All Member Cache Records   
* return Type   :    null
* Parameters    :    null
*/    
    public void CacheDelete()
    {   
        list<Member_Cache__c> lstMemberCache = new list<Member_Cache__c>();
        lstMemberCache = [Select id from Member_Cache__c LIMIT 2000];

        if(Member_Cache__c.sObjectType.getDescribe().isDeletable())
        {
            if(lstMemberCache.size() > 0)
                delete lstMemberCache ;
        }
        else{
            blAccessMessage = true;
        }
        
    }
    
/*
* Method Name   :    getDisplayMessage() 
* Description   :    Method to display message on VF page.   
* return Type   :    string
* Parameters    :    null
*/     
    public string getDisplayMessage()
    {
        Member_Cache__c[] oMemberCache = [Select id from Member_Cache__c limit 2000];

        if(!blAccessMessage){
            if(oMemberCache.size() < 1)
            {   
                blEnableButton = false; 
                blEnablePoller = false;
                return Label.CacheDelete_Success_HUM;
            }
            return Label.CacheDelete_InProgress_HUM;
        }
        else {
            blEnableButton = false;
            return 'Insufficient Access';
        }
    }
 
}