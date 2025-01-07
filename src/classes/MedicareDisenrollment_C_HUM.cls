/******************************************************************************************************************************
Apex Class Name  : MedicareDisenrollment_C_HUM
Version          : 1.0 
Created Date     : Aug 20 2020
Function         : Controller to handle display Disenrollment And Cancellation Template                   
Test Class       : 
Modification Log :
    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
    Jayesh Solanki                        08/20/2020           Original version. 
 *********************************************************************************************************************************/
public with sharing class MedicareDisenrollment_C_HUM  extends HUMTemplateBaseHelper {
    list<Template_Submission__c> oTemplateSubmssion = new list<Template_Submission__c>();
    Integer iConstantOne = Integer.ValueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c);
    String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);

    public MedicareDisenrollment_C_HUM(ApexPages.StandardController controller) 
    {
        bSubmissionPres  = false; 
        iEditSequenceNumber = 1;
        getSubmissionId();
    } 


    public Id SubmissionId {get;set;}
    public Boolean bSubmissionPres {get;set;}
    public Integer iEditSequenceNumber{get ;set;} 

    /*
     * Method Name   :    getSubmissionId
     * Description   :    gets the submission Id of the Case 
     * Return Type   :    NA
     * Parameters    :    NA
     */
    public void getSubmissionId() {

        try {
            if(String.IsNotBlank(sSelectedCaseId)){
                oTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: sCase) order by createddate desc limit 1];              
                if(!oTemplateSubmssion.isempty()){
                    SubmissionId = oTemplateSubmssion[0].Id;
                    iEditSequenceNumber = Integer.Valueof(oTemplateSubmssion[0].Version__c) + iConstantOne;
                    bSubmissionPres = true;
                }
            }
        } catch(Exception ex) {

            HUMExceptionHelper.logErrors(ex, 'MedicareDisenrollment_C_HUM', 'getSubmissionId()');
        } 
    }
}