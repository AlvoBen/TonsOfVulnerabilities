/******************************************************************************************************************************
Apex Class Name  : HUMAddressVerificationProcessController
Version          : 1.0 
Created Date     : May 19 2015
Function         : Controller to handle display address verification form.                   
Test Class       : HUMTemplateBaseHelperTest
Modification Log :
    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
    Santhi Mandava         23304            05/19/2015           Original version.
    Asha Deepika Koyyana   23342            06/08/2015           Implemented as part of REQ 123926
    Asish Behera                            06/25/2018           REQ - 367102 have the Person Speaking With and the Relationship to Member fields prefilled if the Interacting With is equal to Member or Member Representative or CareGiver
    Asish Behera                            07/12/2018           Defect 369783.0001 fix
    Asish Behera                            07/23/2018           Defect 371090.0001 fix
 * Vijaya Koriprolu                         11/10/2018            REQ - 371427- Logging for Provide claims
 * Jasmeen Shangari                         04/09/2020           942030 - Added 'Send Print Item: Member' process in filter criteria  
 *********************************************************************************************************************************/
public with sharing class HUMAddressVerificationProcessController extends HUMTemplateBaseHelper 
{
    list<Template_Submission__c> oTemplateSubmssion = new list<Template_Submission__c>();
    Integer iConstantOne = Integer.ValueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c);
    String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);

    public HUMAddressVerificationProcessController(ApexPages.StandardController controller) 
    {
        bSubmissionPres  = false; 
        iEditSequenceNumber = 1;
        getSubmissionId();
    } 


    public Id SubmissionId {get;set;}
    public String personSpeakingWith {get;set;}
    public String relationshipTomember {get;set;}
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

            bSubmissionPres = true;
            if(String.IsNotBlank(sSelectedCaseId)){
                oTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: sCase) order by createddate desc limit 1];              
                if(!oTemplateSubmssion.isempty()){
                    SubmissionId = oTemplateSubmssion[0].Id;
                    iEditSequenceNumber = Integer.Valueof(oTemplateSubmssion[0].Version__c) + iConstantOne;
                }

                //Getting Interaction details 
                List<Case_Interaction__c> lstCaseInteractionDetails = [SELECT Id,Interaction__c,Interaction__r.Interacting_With_type__c,Interaction__r.Caller_Name__c,Interaction__r.Interacting_With__r.name
                                                                    FROM Case_Interaction__c 
                                                                    WHERE Case__c = :sSelectedCaseId ORDER BY createddate DESC LIMIT 1];
                if(!lstCaseInteractionDetails.isEmpty() && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__c) && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With_type__c)){
                    
                    String sRelationshipToMember = lstCaseInteractionDetails[0].Interaction__r.Interacting_With_type__c;
                    if(sRelationshipToMember.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBERCAREGIVER) || sRelationshipToMember.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INTERACTINGWITHTYPE_MEMBER)){
                                                
                        relationshipTomember = sRelationshipToMember.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBERCAREGIVER) ? GLOBAL_CONSTANT_HUM.MEMBEREP : GLOBAL_CONSTANT_HUM.RELATIONTOMEMBERSELF;
                        
                        if(String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c) && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With__c)){
                            personSpeakingWith = '';
                            relationshipTomember = '';
                        }
                        else if(String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c) && String.IsBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With__c))
                            personSpeakingWith = lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c;
                        else if(String.IsBlank(lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c) && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With__c))
                            personSpeakingWith = lstCaseInteractionDetails[0].Interaction__r.Interacting_With__r.Name;  
                        }
                }

            }
        } catch(Exception ex) {

            SubmissionId = null;
            bSubmissionPres  = false; 
            iEditSequenceNumber = iConstantOne;
            HUMExceptionHelper.logErrors(ex, 'HUMAddressVerificationProcessController', 'AVF form Lunch Error');
        } 
    }
}