/******************************************************************************************************************************
Apex Class Name  : MedicaidGoodCauseRequestProcess_C_HUM
Version          : 1.0 
Created Date     : 2nd February 2019
Function         : Controller to handle display Medicaid Good Cause Request Process           
Test Class       : HUMTemplateBaseHelperTest
Modification Log :
    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
   * Pallavi R           338657           02/01/2019         Original version.
 *********************************************************************************************************************************/
public with sharing class MedicaidGoodCauseRequestProcess_C_HUM extends HUMTemplateBaseHelper 
{
 Template_Submission__c oTemplateSubmssion = new Template_Submission__c();
 String  sCase = String.ValueOf(HUMConstants__c.getInstance('HUMCase').StringValue__c);

 /*
  * Method Name   :    MedicaidGoodCauseRequestProcess_C_HUM
  * Description   :    Constructor for Class 
  * Return Type   :    NA
  * Parameters    :    Case std controller instance
  */
  public MedicaidGoodCauseRequestProcess_C_HUM(ApexPages.StandardController controller) 
    {
       bSubmissionPres  = false; 
       getSubmissionId();  
    }
    
    public Id SubmissionId {get;set;}
    public Boolean bSubmissionPres {get;set;}

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
        }
        catch(Exception ex) 
        {
            SubmissionId = null;
            bSubmissionPres  = false; 
        } 
    }
}