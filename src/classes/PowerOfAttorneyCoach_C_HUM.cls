/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorneyCoach_C_HUM
Version         : 1.0
Created On      : 10/15/2020
Function        : This class is used for getting POA related to an Account
Test Class      : PowerOfAttorneyCoach_T_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

public with sharing class PowerOfAttorneyCoach_C_HUM{
    
    /**
* This is a service callout method for CI Medicare
* 
* @param    
* @return   Object
*/  
    @AuraEnabled(continuation=true cacheable=true)
    public static Continuation callCIMedicare(String sAccId)
    {   
        try{    
            List<Policy_Member__c> lstPM = PowerOfAttorneyCoach_D_HUM.checkConditionForCallout(sAccId);
            if(lstPM != null && !(lstPM.isEmpty()))
            {   
                Continuation oCon= PowerOfAttorneyCoach_S_HUM.requestContinuationCIMedicare(sAccId, lstPM);
                return oCon;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PowerOfAttorneyCoach_C_HUM', 'callCIMedicare');
            return null;
            
        }
        return null;
    }
    
    /**
* This is a a callback method CI medicare service call
* 
* @param    
* @return   Object
*/ 
    @AuraEnabled(cacheable=true)
    public static object processCIResponse(Object state)
    {
        try
        {   String sCIMedReq=(String)state;
         HttpResponse response = Continuation.getResponse(sCIMedReq);

         if(response != null && response.getStatusCode() == GLOBAL_CONSTANT_CH_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_CH_HUM.HTTP_ACCEPTED_HUM)
         {  
             list<string> lstKeyTOCIMember;
             Map<String,List<String>> mapServiceResponse=PowerOfAttorneyCoach_D_HUM.getMapServiceResponse(response);
             List<poaWrapper> listpoaWrapper= new List<poaWrapper>();
             
             if(!mapServiceResponse.isEmpty())
             {  
                 for(List<String> mapValues: mapServiceResponse.values())
                 {
                     poaWrapper poaval= new poaWrapper();
                     poaval.FirstName=mapValues[0];
                     poaval.LastName=mapValues[1];
                     poaval.PhoneNumber=mapValues[2];
                     poaval.Type=mapValues[3];
                     listpoaWrapper.add(poaval);
                 }
             }
             return listpoaWrapper;
         }
         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PowerOfAttorneyCoach_C_HUM', 'processCIResponse');
        }
        return null;
    }
    
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
                    
                    mapServiceResponse.put(GLOBAL_CONSTANT_CH_HUM.POA_CONSTRECORD + iCount, new list<string>{objPOAInfo.FirstName,objPOAInfo.LastName,objPOAInfo.PhoneNumber,objPOAInfo.Type});
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
            HUMExceptionHelper.logErrors(ex,'PowerOfAttorneyCoach_C_HUM', 'processPOA');
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