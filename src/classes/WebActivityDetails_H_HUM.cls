/****************************************************************************
Apex Class Name  : WebActivityDetails_H_HUM
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Helper Class to get the enterprise ID.
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
* Saurabh Kaple           156039            01/10/2018                    Req# 344481 Added genkey to the service request
***************************************************************************************************/


public with sharing class WebActivityDetails_H_HUM{

/*
* Method Name   :    getEnterpriseId() 
* Description   :    Method to get the Enterprise ID   
* return Type   :    String
* Parameters    :    String
*/      
    public string getEnterpriseId(string sMemberId){ 
    
    Account[] oAccountSearch;
    string sEnterpriseId;
    try
    {
    if(sMemberId != NULL && string.IsNotBlank(sMemberId.trim()) )       
        oAccountSearch = [SELECT id, Enterprise_ID__c
                                  FROM Account 
                                  WHERE id = :sMemberId]; 
                                  
        
      if(oAccountSearch.size() > 0)  
        sEnterpriseId = oAccountSearch[0].Enterprise_ID__c;  
      }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','getEnterpriseId');
            return NULL;
        }
       
        if(sEnterpriseId != NULL && string.IsNotBlank(sEnterpriseId.trim()) )
            return sEnterpriseId; 
        else 
            return NULL;                                                                                   
    } 
	
	/*
* Method Name   :    getMemberGenKey() 
* Description   :    Method to get the Member GenKey  
* return Type   :    String
* Parameters    :    String
*/      
    public string getMemberGenKey(string sMemberId){ 
    
    Account[] oAccountSearch;
    string sMemGenKey;
    try
    {
    if(sMemberId != NULL && string.IsNotBlank(sMemberId.trim()) )       
        oAccountSearch = [SELECT id, Mbr_Gen_Key__c
                                  FROM Account 
                                  WHERE id = :sMemberId]; 
                                  
        
      if(oAccountSearch.size() > 0)  
        sMemGenKey = oAccountSearch[0].Mbr_Gen_Key__c;  
      }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberMbeService_S_HUM','getMemberGenKey');
            return NULL;
        }
       
        if(sMemGenKey != NULL && string.IsNotBlank(sMemGenKey.trim()) )
            return sMemGenKey; 
        else 
            return NULL;                                                                                   
    }
}