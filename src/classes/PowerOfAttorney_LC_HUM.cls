/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorney_LC_HUM
Version         : 1.0
Created On      : 10/15/2020
Function        : This class is used for getting POA related to an Account
Test Class      : PowerOfAttorney_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     10/15/2020                 Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class
*    3.0           Ashish Kumar                                     13/05/2021                 Changed Policy_Member__c object references to MemberPlan Object references
*    4.0           Ashish Kumar                                     09/27/2021                 Changes related to US-2616064
*******************************************************************************************************************************************************************/
public with sharing class PowerOfAttorney_LC_HUM {
    @AuraEnabled(cacheable=true) 
    public static Object processPOA(String lstPOAData)
    {
        try
        {	
            
            POA_DTO_HUM lstPOA_DTO = new POA_DTO_HUM();
            lstPOA_DTO = (POA_DTO_HUM)JSON.deserialize(lstPOAData, POA_DTO_HUM.class);
            
            Map<string,List<String>> mapServiceResponse = new Map<string,List<string>>();
            Integer iCount = 0;
            list<POA_DTO_HUM.POAData> lstPOADatainside = lstPOA_DTO.POAData;
            List<String> lstKeyTOCIMember;
            List<poaWrapper> listpoaWrapper= new List<poaWrapper>();
            
            if(lstPOADatainside.size() > 0)
            { 
                for(POA_DTO_HUM.POAData objPOAInfo: lstPOADatainside)
                {	
                    poaWrapper poaval= new poaWrapper();
                    poaval.FirstName=objPOAInfo.FirstName;
                    poaval.LastName=objPOAInfo.LastName;
                    poaval.PhoneNumber=objPOAInfo.PhoneNumber;
                    poaval.Type=objPOAInfo.Type;
                    listpoaWrapper.add(poaval);
                    
                    mapServiceResponse.put(GLOBAL_CONSTANT_LH_HUM.POA_CONSTRECORD + iCount, new list<string>{objPOAInfo.FirstName,objPOAInfo.LastName,objPOAInfo.PhoneNumber,objPOAInfo.Type});
                    iCount = iCount + 1;
                }
            }
            if(!mapServiceResponse.isEmpty())
            {
                lstKeyTOCIMember = new list<string>();
                lstKeyTOCIMember.addAll(mapServiceResponse.keySet());
                
                return listpoaWrapper;
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'PowerOfAttorney_C_HUM', 'processPOA');
        }
        return null;
    }
    
    public class poaWrapper
    {
        @AuraEnabled public String FirstName{get;set;}
        @AuraEnabled public String LastName{get;set;}
        @AuraEnabled public String PhoneNumber{get;set;}
        @AuraEnabled public String Type{get;set;}
        @AuraEnabled public String TypeHelpText{get;set;}
        
    }
}