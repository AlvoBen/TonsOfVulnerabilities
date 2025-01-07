/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorney_LD_HUM
Version         : 1.0
Created On      : 10/15/2020
Function        : This class is the data layer class
Test Class      : PowerOfAttorney_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     10/15/2020                  Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class and added limit in query
*    3.0           Ashish Kumar                                     13/05/2021                  Changed Policy_Member__c object references to MemberPlan Object references
*    4.0           Ashish Kumar                                     09/27/2021                 Changes related to US-2616064
*******************************************************************************************************************************************************************/

public with sharing class PowerOfAttorney_LD_HUM {
    
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    /**
* This method checks whether policy member is eligible for POA
* 
* @param    
* @return  	Object
*/   
    public static List<MemberPlan> checkConditionForCallout(String sAccId)
    {	
        List<MemberPlan> lstPM;

        Set<string> setValidMemTypes = new set<string>(GLOBAL_CONSTANT_LH_HUM.POA_VALIDMEMBERTYPES.split(';')); 
        Set<string> setValidPlatforms = new Set<string>(GLOBAL_CONSTANT_LH_HUM.POA_VALIDPLATFORMS.split(';'));
        
        lstPM = [Select Id, Name, Plan.Source_Cust_Cov_Key__c, Member_Id_Base__c
                 From MemberPlan Where MemberId =: sAccId AND Plan.Platform__c IN : setValidPlatforms AND Member_Id_Base__c!=null LIMIT: limitValue];
   
        return lstPM;
    }    
}