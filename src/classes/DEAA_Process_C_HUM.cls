/******************************************************************************************************************************
Apex Class Name  : DEAA_Process_C_HUM
Version          : 1.0 
Created Date     : November 14 2019
Function         : Controller to handle display of DEAA process form.                   
Test Class       : DEAA_Process_T_HUM
Modification Log :
   Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
  Mohammed Noor                           11/14/2019           Original version added for US725182.
  
 *********************************************************************************************************************************/
public with sharing class DEAA_Process_C_HUM extends HUMTemplateBaseHelper 
{
    public Id SubmissionId {get;set;}
    public Boolean bSubmissionExist {get;set;}  
    String  sCase = HUMConstants__c.getInstance('HUMCase').StringValue__c;
    
    /**
	* Constructor of the class to Instantiate the variables.
	*
	* @param    None	
	* @return   N/A
	*/
    public DEAA_Process_C_HUM()
    {
        bSubmissionExist  = false;
        fetchSubmissionId();
    } 
       
    /**
	* Method to fetch any existing DEAA template on the case
	*
	* @param    None	
	* @return   void
	*/
    @TestVisible
    private void fetchSubmissionId()
    {
        list<Template_Submission__c> lstTemplateSubmssion = new list<Template_Submission__c>();
        try
        {           
            if(String.isNotBlank(sSelectedCaseId))
            {
                lstTemplateSubmssion = [SELECT ID, Version__c FROM Template_Submission__c WHERE Submission_Owner__c IN (SELECT Id FROM Template_Submission_Owner__c WHERE  Object_Owner_ID__c  =: sSelectedCaseId  AND Object_Owner_Type__c =: sCase)];                
                if(lstTemplateSubmssion!=null && !lstTemplateSubmssion.isEmpty())
                {
                    SubmissionId = lstTemplateSubmssion[0].Id;
                    bSubmissionExist = true;        
                }
            }                       
        }
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'DEAA_Process_C_HUM', 'fetchSubmissionId');   
        }   
    }
    
    /**
	* Method to create the URL for page redirect after template finish.
	*
	* @param    None	
	* @return   PageReference
	*/    
    public PageReference getFinishPage()
    {
         PageReference prRef = New PageReference('/apex/HUMCloseFlow');
         prRef.getParameters().put('DEAACaseId',sSelectedCaseId);
         prRef.getParameters().put(GLOBAL_CONSTANT_HUM.CASEACTIONLAUNCH_HUMFLOWCOBPROCESSTYPE_HUM,GLOBAL_CONSTANT_HUM.DEAA_Origin);
         prRef.setRedirect(true);
         return prRef;
   }
}