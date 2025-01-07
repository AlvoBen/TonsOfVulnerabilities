/******************************************************************************************************************************
Apex Class Name  : PCPTemplate_X_HUM
Version          : 1.0 
Created Date     : Jun 26 2018
Function         : Controller to handle PCP Question Template                   
Test Class       : PCPTemplate_T_HUM
Modification Log :
    Developer          Code Review         Date                 Description
  ------------------------------------------------------------------------------------------------------------------------------                
    Ashish Behera      		               06/26/2018           Original version.
    Ashish Behera      		               07/05/2018           Fixed Error Is in expression '{!interviewDisplay}' in component.
   * Vijaya Koriprolu                    11/10/2018            REQ - 371427- Logging for Provide claims
   * Vinay lingegowda                      03/04/2020           USER STORY 856241 PR00091574 - MF 1 - Quality - PCP Template Updates (CRM)
   * Jasmeen Shangari                      04/09/2020           942030 - Added 'Send Print Item: Member' process in filter criteria
  ********************************************************************************************************************************/
public with sharing class PCPTemplate_X_HUM extends HUMTemplateBaseHelper {
    
    /*
    * Constructor for the class
    */
    public PCPTemplate_X_HUM(ApexPages.StandardController controller) 
    {        
        getSubmissionId();
    } 
    
    public Id submissionId {get;set;}
        
    
    /*
    * Method Name   :    getSubmissionId
    * Description   :    gets the submission Id of the Case 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public void getSubmissionId()
    {
        try
        {
			String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);
            if(String.IsNotBlank(sSelectedCaseId)){
                List<Template_Submission__c> oTemplateSubmssionList = [SELECT Id FROM Template_Submission__c WHERE template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member') and Submission_Owner__c IN (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: sCase) ORDER BY createddate DESC LIMIT 1];
                if(!oTemplateSubmssionList.isempty()){
                    submissionId = oTemplateSubmssionList[0].Id;
                }
            }
            
        }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'PCPTemplate_X_HUM', 'getSubmissionId');
            submissionId = null;
        } 
    }
     /*
    * Method Name   :    getFinishPage
    * Description   :    sets the finish page and pass parameter 
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public PageReference getFinishPage()
    {
        PageReference prRef = new PageReference('/apex/HUMCloseFlow');
        prRef.getParameters().put('PCP',sSelectedCaseId); 
        prRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWPCPPCDPROCESS_HUM);
        prRef.setRedirect(true);
        return prRef;
    }
}