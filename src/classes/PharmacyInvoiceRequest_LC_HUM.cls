/*******************************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Monali Jagtap									     07/17/2023 		         USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy                                                                
************************************************************************************************************************************/
public with sharing class PharmacyInvoiceRequest_LC_HUM {
    public static String SubmissionID ='';
    public static String TN ='';
    public static string SubmissionData ='';
    public static Boolean TicketRequired=false;
    public static Boolean hasProcess=false;
    public static Map<String,String> mapProcessData = new Map<String,String>();
 	
    @AuraEnabled(cacheable=true)
    public static string init(String caseid){
        try {
            List<Template_Submission__c> tempList = [SELECT Id FROM Template_Submission__c WHERE Submission_Owner__r.Object_Owner_ID__c =:CaseId WITH USER_MODE LIMIT 1];
        
            if(TempList != null && TempList.size() > 0){ 
                SubmissionID = TempList[0].Id;
                mapProcessData.put('SubmissionID',SubmissionID);
                List<Template_Submission_Data__c> submission = [SELECT Value__c FROM Template_Submission_Data__c WHERE Template_Submission__c =: SubmissionID WITH USER_MODE LIMIT 1];
                if(submission != null && submission.size() > 1 && (submission[0].Value__c != '' && submission[1].Value__c != ''))
                {
                    SubmissionData = (submission.size() > 0)?submission[0].Value__c:null;
                    mapProcessData.put('SubmissionData',SubmissionData);
                    hasProcess =true;
                    mapProcessData.put('hasProcess',String.valueOf(hasProcess));
                }
            }
            else
            {
                SubmissionID = '';
                mapProcessData.put('SubmissionID',SubmissionID);
                hasProcess = false;
                mapProcessData.put('hasProcess',String.valueOf(hasProcess));
            }
            
            List<Template__c> templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Invoice Request' WITH USER_MODE Limit 1];
           {
                TN = (templateNumber != null && templateNumber.size() > 0)?templateNumber[0].name:null;
                mapProcessData.put('TN',TN);
            }
                return JSON.serialize(mapProcessData);
                
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'PharmacyInvoiceRequest_LC_HUM','init');
            throw new AuraHandledException(e.getMessage());
        }
    }
     
}