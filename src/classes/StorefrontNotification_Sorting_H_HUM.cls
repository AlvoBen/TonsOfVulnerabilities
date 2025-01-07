/********************************************************************************************************************************************************
Apex Class Name  : StorefrontNotification_Sorting_H_HUM
Version          : 1.0 
Created Date     : April 22 2020
Modification Log :       
* Developer                         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
*  Vinoth L                      	04/22/2021           Original Version : User Story 2144275 -CRM Storefront Notifications: New Member Eligibility for Action Required Prompt
*******************************************************************************************************************************************************/
public with sharing class StorefrontNotification_Sorting_H_HUM implements Comparable  { 
    public Policy_Member__c policyMem;        
    public StorefrontNotification_Sorting_H_HUM(Policy_Member__c plcyMember) {
        policyMem = plcyMember;
    }        
    public Integer compareTo(Object compareTo){              
        StorefrontNotification_Sorting_H_HUM compareToOppy = (StorefrontNotification_Sorting_H_HUM)compareTo;                
        Integer returnValue = 0;
        Date effDateComp;
        Date effDate;
        try{
            effDate =Date.parse(policyMem.Effective_Date__c);
            effDateComp=Date.parse(compareToOppy.policyMem.Effective_Date__c);
        }
        catch(Exception e){
        	 HUMExceptionHelper.logErrors(e, 'StorefrontNotification_Sorting_H_HUM', 'compareTo');   
        }
        if( effDate> effDateComp){            
            returnValue = 1;
        }
        else if(effDate<effDateComp){            
            returnValue = -1;
        } 
        else if((policyMem.Effective_Date__c == null) && (compareToOppy.policyMem.Effective_Date__c != null)){            
            returnValue = -1;
        } 
        else if((policyMem.Effective_Date__c != null) && (compareToOppy.policyMem.Effective_Date__c == null)){            
            returnValue = 1;
        }        
        return returnValue;              
    }
}