/******************************************************************************************************************************
Apex Class Name  : AttestationVerification_C_HUM
Version          : 1.0 
Created Date     : March 13 2018
Function         : Controller to handle display attestation verification form.                   
Test Class       : AttestationVerification_T_HUM
Modification Log :
   Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
  Bharath Rajappan       204270           03/13/2018           Original version. - REQ - 357304 - Launch Creditable Coverage attestation form 
  Moshitha Gunasekaran   218172           04/22/2018           REQ - 359158 Populate Creditable Coverage subtype on completion of ATTVF template 
 *********************************************************************************************************************************/
public with sharing class AttestationVerification_C_HUM extends HUMTemplateBaseHelper 
{
    public Id SubmissionId {get;set;}     
    public Integer iEditSequenceNumber{get ;set;}      
    public Boolean bSubmissionPres {get;set;}   
    Template_Submission__c oTemplateSubmssion = new Template_Submission__c();
    Integer iConstantOne = Integer.ValueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c);
    String  sCase = HUMConstants__c.getInstance('HUMCase').StringValue__c;
   
    public AttestationVerification_C_HUM(ApexPages.StandardController controller) 
    {
        bSubmissionPres  = false; 
        getSubmissionId();
    } 
    
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
            oTemplateSubmssion = [SELECT ID,Version__c FROM Template_Submission__c where Submission_Owner__c in (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: sCase) ];
            SubmissionId = oTemplateSubmssion.Id;
            iEditSequenceNumber = Integer.Valueof(oTemplateSubmssion.Version__c) + iConstantOne;
        }
        catch(Exception ex) 
        {
            bSubmissionPres  = false; 
            SubmissionId = null;            
            iEditSequenceNumber = iConstantOne;
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
         PageReference prRef = New PageReference('/apex/HUMCloseFlow');
         prRef.getParameters().put('ATTVFCaseId',sSelectedCaseId); 
         prRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,GLOBAL_CONSTANT_HUM.CREDITABLECOVERAGE_ATTESTATION_PROCESS_NAME);
         prRef.setRedirect(true);
         return prRef;
   }

   /*
    * Method Name   :    CleanUpEmptyData
    * Description   :    Clean Up unSaved Data from Flow 
    * Return Type   :    List<List<String>>
    * Parameters    :    List<String>
    */
    @InvocableMethod(label='CleanUpEmptyData' description='CleanUp Empty Data.')
    public static List<string> CleanUpEmptyData(List<List<String>> lstcaseId) 
    {        
        List<string> lstStr = new List<string>();
        if(!lstcaseId.isempty())
        {          
            HUMValidSubmissionHelper.deleteInValidSubmission(lstcaseId[0][0]);
        }
        return lstStr;
    }

}