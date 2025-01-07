/**************************************************************************************************************************************************
Apex class Name    :  ProcessTemplateSummary_C_HUM
Version            :  1.0
Function           :  This is class to process template summary data for respective processes when user clicks on Process in case related list.
Test Class         :  ProcessTemplateSummary_T_HUM

Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                             07/08/2020                 Original Version
************************************************************************************************************************************************* */
public with sharing class ProcessTemplateSummary_C_HUM {
   
    public string sTemplateId { get; set; }
    public string UserInterfaceDetails { get; set; }
    public List<Template_Submission__c> lstTempSubmission { get; set;}
    public List<Template_Submission_Data__c> lstTempSubmissionData { get; set; }
    
    /*
    *  This method is used to fetch template summary data
    *  @name   fetchUserInterfaceDetails
    *  @param  void
    *  @return void
    */    
    public void fetchUserInterfaceDetails()
    {	
        try
        {
            sTemplateId = ApexPages.currentPage().getParameters().get('vTemplateId');
            if(sTemplateId != null) {
            	lstTempSubmission = [Select ID from Template_Submission__C where Id = :sTemplateId LIMIT 1 ];
            }
            if(lstTempSubmission != null && !lstTempSubmission.isEmpty()) {
            	lstTempSubmissionData = [
                    select ID,Name__c,Value__c from Template_Submission_Data__c 
                    where 
                    Template_Submission__c =: lstTempSubmission[0].id 
                    AND Name__c='UserInterfaceData' 
                    LIMIT 1
                ]; 
                      
            	if(lstTempSubmissionData != Null && !lstTempSubmissionData.isEmpty()) {
                	UserInterfaceDetails = lstTempSubmissionData[0].Value__c;
                }
            }
        }
        catch(Exception e){	 HUMExceptionHelper.logErrors(e, 'ProcessTemplateSummary_C_HUM', 'fetchUserInterfaceDetails');
        }
    }
}