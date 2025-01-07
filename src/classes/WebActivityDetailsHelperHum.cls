/****************************************************************************
Apex Class Name  : WebActivityDetailsHelperHum 
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Helper Class to get the enterprise ID.
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
***************************************************************************************************/


public with sharing class WebActivityDetailsHelperHum {

/*
* Method Name   :    getEnterpriseId() 
* Description   :    Method to get theEnterprise ID   
* return Type   :    String
* Parameters    :    String
*/      
    public string getEnterpriseId(string sMemberId){        
        Account oAccountSearch = [SELECT id, Enterprise_ID__c
                                  FROM Account 
                                  WHERE id = :sMemberId]; 
        string sEnterpriseId = oAccountSearch.Enterprise_ID__c;  
        return sEnterpriseId;                                                                                    
    } 
}