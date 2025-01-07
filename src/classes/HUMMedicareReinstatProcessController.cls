/******************************************************************************************************************************
Apex Class Name  : HUMMedicareReinstatProcessController
Version          : 1.0 
Created Date     : October 10th 2017
Function         : Controller to handle display Medicare Good Cause Reinstatement Form               
Test Class       : HUMTemplateBaseHelperTest
Modification Log :
    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
   * Pallavi R          141064            10/10/2017           Original version.
   * Moshitha Gunasekaran                 02/04/2020           US752867 - New TRR Templates Process Section
   * Vinay Lingegowda                     03/19/2020           US-857398 AHT/Quality - Medicare Good Cause Reinstatement Template Auto Routing and AHT Updates (CRM)
   * Jasmeen Shangari                     04/09/2020           942030 - Added 'Send Print Item: Member' process in filter criteria
 *********************************************************************************************************************************/
public with sharing class HUMMedicareReinstatProcessController extends HUMTemplateBaseHelper 
{
 List<Template_Submission__c> oTemplateSubmssion = new List<Template_Submission__c>();
 String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);

 /*
  * Method Name   :    HUMMedicareReinstatProcessController
  * Description   :    Constructor for Class 
  * Return Type   :    NA
  * Parameters    :    Case std controller instance
  */
  public HUMMedicareReinstatProcessController(ApexPages.StandardController controller) 
    {
       bSubmissionPres  = false; 
       getSubmissionId();  
    }
    
    public Id SubmissionId {get;set;}
    public Boolean bSubmissionPres {get;set;}
    public String gcrPersonName {get;set;}
    public String gcrRelationship {get;set;}
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
            bSubmissionPres = true;
			if(String.IsNotBlank(sSelectedCaseId)){
            oTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where template__r.Description__c not in ('Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member','Transaction Reply Report (TRR)') and Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId AND Object_Owner_Type__c =: sCase)];
				if(!oTemplateSubmssion.isEmpty()){
                    SubmissionId = oTemplateSubmssion[0].Id;
                }
                //Get Interaction log details for displaying Name and relationship of/with member
                List<Case_Interaction__c> lstCaseInteractionDetails = [SELECT Id,Interaction__c,Interaction__r.Interacting_With_type__c,Interaction__r.Caller_Name__c,Interaction__r.Interacting_With__r.Name
                                                                        FROM Case_Interaction__c 
                                                                        WHERE Case__c = :sSelectedCaseId ORDER BY createddate DESC LIMIT 1];
                if(!lstCaseInteractionDetails.isEmpty() && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__c) && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With_type__c)){
                    String sRelationshipToMember = lstCaseInteractionDetails[0].Interaction__r.Interacting_With_type__c;
                    if(sRelationshipToMember.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBERCAREGIVER) || sRelationshipToMember.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INTERACTINGWITHTYPE_MEMBER)){
                        gcrRelationship = sRelationshipToMember.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBERCAREGIVER) ? GLOBAL_CONSTANT_HUM.MEMBEREP : GLOBAL_CONSTANT_HUM.INTERACTINGWITHTYPE_MEMBER;
                        if(String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c) && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With__c)){
                            gcrPersonName = '';
                            gcrRelationship = '';
                        }
                        else if(String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c) && String.IsBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With__c))
                            gcrPersonName = lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c;
                        else if(String.IsBlank(lstCaseInteractionDetails[0].Interaction__r.Caller_Name__c) && String.IsNotBlank(lstCaseInteractionDetails[0].Interaction__r.Interacting_With__c))
                            gcrPersonName = lstCaseInteractionDetails[0].Interaction__r.Interacting_With__r.Name;  
                    }
                }    
	        }
        }
        catch(Exception ex) 
        {
            SubmissionId = null;
            bSubmissionPres  = false; 
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
        prRef.getParameters().put('GoodCause',sSelectedCaseId); 
        prRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWMEDICAREREINSTATDESC_HUM);
        prRef.setRedirect(true);
        return prRef;
    }
}