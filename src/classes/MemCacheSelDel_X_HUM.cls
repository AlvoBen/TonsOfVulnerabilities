/******************************************************************************************************************************       
Apex Class Name  : MemCacheSelDel_X_HUM
Version          : 1.0  
Created Date     : Nov 30, 2015
Function         :"MemCacheSelDel_VF_HUM" page extension to Delete selected Member Cache Records.
                   A detailed analysis was done on date "13/11/2017" by Raja Sekhar Reddy MV on Sharing with option for this class.
                   During the analysis it was found that this class is used to delete member cache records. 
                   So we can use With Sharing option instead of using default sharing option.
                   This was further discussed with Architects Amrish and Arvind. Got approval to change from default to "With Sharing" mode.
Test Class       : ChangeHasVitality_T_HUM
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               11/30/2015                    Original Version
* Raja Sekhar Reddy M V                     13/11/2017                    REQ - 337740 - Checkmarx security remediation - MemCacheSelDel_X_HUM
*************************************************************************************************************************/

public with sharing class MemCacheSelDel_X_HUM{

    public boolean blEnablePoller { get; set;}
    public boolean blEnableButton { get; set;}
    Boolean blSelectedrecord;
    Member_Cache__c[] lstMemberCache = new list<Member_Cache__c>();
    
    public MemCacheSelDel_X_HUM(ApexPages.StandardSetController stdSetController) {
        
        blEnablePoller = true;
        blEnableButton = true;
        lstMemberCache = stdSetController.getSelected();
        if(lstMemberCache.size() > 0)
            blSelectedrecord = true;
        else
            blSelectedrecord = false;
    }

/*
* Method Name   :    CacheDelete() 
* Description   :    Method to Delete Selected Member Cache Records   
* return Type   :    null
* Parameters    :    null
*/    
    public void CacheDelete()
    {    
        list<Member_Cache__c> lstSelMemberCache = new list<Member_Cache__c>();

        if(lstMemberCache != NULL)
            lstSelMemberCache = [Select id from Member_Cache__c where id in : lstMemberCache LIMIT 2000];
                      
        if(lstSelMemberCache != NULL){
            SObjectType myObj = Schema.getGlobalDescribe().get('Member_Cache__c');
        	if(myObj.getDescribe().isDeletable()) 
                delete lstSelMemberCache;
        }
            
       
        if(lstSelMemberCache.size() < 1)
            blSelectedrecord = false;
    }
 
/*
* Method Name   :    getDisplayMessage() 
* Description   :    Method to display message on VF page.   
* return Type   :    string
* Parameters    :    null
*/   
    public string getDisplayMessage()
    {
        if(blSelectedrecord == false)
        {    
            blEnableButton = false;
            blEnablePoller = false;
            return Label.CacheDelete_Success_HUM;
        }
        else
        {
            return Label.CacheDelete_InProgress_HUM;
        }
    }
}