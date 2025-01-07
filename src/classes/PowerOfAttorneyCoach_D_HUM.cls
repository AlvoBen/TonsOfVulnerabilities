/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorneyCoach_D_HUM 
Version         : 1.0
Created On      : 10/15/2020
Function        : This class is the data layer class
Test Class      : PowerOfAttorneyCoach_T_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

public with sharing class PowerOfAttorneyCoach_D_HUM {
    
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
    
    /**
* This method checks whether policy member is eligible for POA
* 
* @param    
* @return   Object
*/   
    public static List<Policy_Member__c> checkConditionForCallout(String sAccId)
    {   
        List<Policy_Member__c> lstPM;
        Set<string> setValidMemTypes = new set<string>(GLOBAL_CONSTANT_CH_HUM.POA_VALIDMEMBERTYPES.split(';')); 
        Set<string> setValidPlatforms = new Set<string>(GLOBAL_CONSTANT_CH_HUM.POA_VALIDPLATFORMS.split(';'));
        
        
        lstPM = [Select Id, Name, Policy__r.Source_Cust_Cov_Key__c, 
                 (Select  Name From Member_Ids__r Where Type__c IN : setValidMemTypes) 
                 From Policy_Member__c
                 Where Member__c =: sAccId AND Policy__r.Platform__c IN : setValidPlatforms LIMIT: limitValue];
        
        for(Integer i = 0; i < lstPM.size() ; i++)
        {
            
            if(lstPM.get(i).Member_Ids__r.isEmpty() || string.isBlank(lstPM.get(i).Member_Ids__r[0].Name) )
            {
                lstPM.remove(i);
                i--;
            }
        }
        return lstPM;
    }    
    
    public static Map<String,List<String>> getMapServiceResponse(HttpResponse response)
    {           
        Map<string, list<string>> mapServiceResponse;
        PowerOfAttorney_H_HUM oPOAHelper = new PowerOfAttorney_H_HUM();
        mapServiceResponse = oPOAHelper.parseCIResponse(response.getBody());
        
        return mapServiceResponse;
    }
}