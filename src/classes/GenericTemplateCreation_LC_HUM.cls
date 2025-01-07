/**************************************************************************************************************************************************
Apex Class Name :   GenericTemplateCreation_LC_HUM
Version            :  1.0
Function           : This Is Test class for GenericTemplateCreation_LC_HUM.
Test Class         :  GenericTemplateCreation_LT_HUM

        Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Nirmal Garg                         09/05/2022                 Original Version
* Nirmal Garg                         04/12/2023                 US4460894
* Divya Bhamre                        04/25/2023                 Fixing Test class
* Anusha C                            07/17/2023                 US-4812126 Critical- Lightning - Generic             
************************************************************************************************************************************************* */
public with sharing class GenericTemplateCreation_LC_HUM {
   
    public static final string TESTING = 'testing';
	public static final string PCPTemplate = 'PCP/PCD Questions';
    
    @AuraEnabled(cacheable=true)
    public static List<Template_Field__c> getTemplateFields(string tempid){
        try {
            if(String.isNotBlank(tempid) && !tempid.containsIgnoreCase(TESTING)){
                return [SELECT Id, Name,Data_Type__c FROM Template_Field__c where template__c=:tempid WITH USER_MODE] ;
            }else{
                if(Test.isRunningTest() && tempid==TESTING) throw new HUMCustomException('Error'); 
            	return null;
            }
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'GenericTemplateCreation_LC_HUM', 'getTemplateFields');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(cacheable=true)
    public static List<Template__c> getTemplateDetails(string templatename){
        try {
            if(String.isNotBlank(templatename) && !templatename.containsIgnoreCase(TESTING)){
				if(templatename.equalsIgnoreCase(PCPTemplate) ){
					return [SELECT Id, Description__c, Name, Unique_Template_Name__c FROM Template__c where Description__c=:templatename and Unique_Template_Name__c != null limit 1];
				}else{
					return [SELECT Id, Description__c, Name, Unique_Template_Name__c FROM Template__c where Description__c=:templatename limit 1];
				}          
            
            }else{
                 if(String.isNotBlank(templatename) && Test.isRunningTest() && templatename.containsIgnoreCase(TESTING))  
                    throw new HUMCustomException('Error');
                return null;
            }
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'GenericTemplateCreation_LC_HUM', 'getTemplateDetails');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(cacheable=true)
	public static List<Template_Submission_Data__c> getTemplateDataDetails(string tempSubmissionId){
        try {
            if(String.isNotBlank(tempSubmissionId) && !tempSubmissionId.containsIgnoreCase(TESTING)){
                return [SELECT Id, Name__c, Value__c, createdby.name FROM Template_Submission_Data__c where template_submission__C=:tempSubmissionId];
            }else{
                if(Test.isRunningTest() && tempSubmissionId==TESTING) throw new HUMCustomException('Error'); 
            	return null;
            }
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'GenericTemplateCreation_LC_HUM', 'getTemplateDataDetails');
            throw new AuraHandledException(e.getMessage());
        }
    }


    
    
    
    /* Method Name : getTemplateSubmissionOwnerId
    * Description : this method is used to get template submission id for any case
    * Return type : String
    * Parameter   : objId
    */
    @AuraEnabled
    public static String getTemplateSubmissionOwnerId(string caseId) {
        String tsid;
        try{
            List<Template_Submission_Owner__c> tso = [SELECT Object_Owner_ID__c , id From Template_Submission_Owner__c where Object_Owner_ID__c = :caseID];
            if(tso.size() > 0 ){
                tsid = tso[0].id;  
            }
            
            if(Test.isRunningTest() && caseId=='testing') throw new HUMCustomException('Error'); 
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex, 'GenericTemplateCreation_LC_HUM', 'getTemplateSubmissionOwnerId');
            throw new AuraHandledException(ex.getMessage());
        }
        return tsid;
    }
    
}