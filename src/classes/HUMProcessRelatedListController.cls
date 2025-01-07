/******************************************************************************************************************************
Apex Class Name  : HUMProcessRelatedListController 
Version          : 1.0 
Created Date     : MAY 14 2015
Function         : Controller to handle display Related section on Case detail page.                   
Test Class       : HUMProcessRelatedListControllerTest

Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *    Manish Kumar       23321               05/14/2015           Original version. Code to show Existing/New process flow related to particular Case
                                                                  I have used 15 digit CaseID explicitly for comparision because in object the ID is stroed as 15 digit. 
                                                                  So, I need to compare it with 15 digit IDs only but not with 18 digit.
 * Shreya Choodamani                         06/20/2015           Added code for handling incorrect submssions 
 * Asish Behera                              02/15/2018           Added RF permission to make PCP PCD process visible in process realted list only for RF users 
 * Amar Gadewar								 06/22/2018			  REQ - 367117 - Remove the RapidForce Permission If Condition added for PCP/PCD       
 *********************************************************************************************************************************/
public with sharing class HUMProcessRelatedListController
{
    private static final String sExistingProcess = HUMConstants__c.getInstance('HUMExistingProcess').StringValue__c;
    private static final String sNewProcess = HUMConstants__c.getInstance('HUMNewProcess').StringValue__c;
    private static final Integer iZero = Integer.valueOf(HUMConstants__c.getInstance('ZERO').IntegerValue__c);
    private static final Integer iLastIndex = Integer.valueOf(HUMConstants__c.getInstance('HUMLastIndex').IntegerValue__c);
    private static final Integer iOne = Integer.valueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c);
    private static final String sStatus = HUMConstants__c.getInstance('STATUS').StringValue__c;
    
    /*
     * Method Name : HUMProcessRelatedListController
     * Description : constructor for the class. Initialized variables and fetched query results.
     * Return type : Void
     * Paramater   : ApexPages.StandardController
     */
    public HUMProcessRelatedListController(ApexPages.StandardController controller) 
    {
        try
        {
            lstSObj = new List<SObject>();
            
            if(controller != NULL) 
            {
                if(!Test.isRunningTest())   controller.addFields(new List<String>{sStatus});
                casObj = (Case)controller.getRecord();
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMProcessRelatedListController', 'HUMProcessRelatedListController');
        } 
    }
    
    public List<SObject> lstSObj {get; private set;}
    public Integer iOldProcessCount {get; private set;}
    public Case casObj {get; private set;}
    
    /*
     * Method Name : deleteInvalidSubmission
     * Description : Method for deleting invalid submission
     * Return type : null
     * Paramater   : Page Reference
     */
    public PageReference deleteInvalidSubmission()
    {
        PageReference pgref = NULL; 
        HUMValidSubmissionHelper.deleteInValidSubmission(casObj.Id); 
        populateProcessDetail();
		return null;
    }
    
    
    /*
     * Method Name : populateProcessDetail
     * Description : Populate process detail
     * Return type : Void
     * Paramater   : NA
     */
    @testVisible
    private void populateProcessDetail()
    {
        if(casObj.id != null)
        {
            lstSObj = (List<Case_Action_Association__c>)[Select LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name, 
                           CreatedById, Case__c, Case_Action__r.Name, Case_Action__c, Action__c 
                           From Case_Action_Association__c WHERE Case__c =: casObj.Id];
            iOldProcessCount = lstSObj.size();
            
            if(iOldProcessCount <= iZero)
            {
                for(Template_Submission_Owner__c oTSO : (List<Template_Submission_Owner__c>)[Select 
                            (Select Template__r.Description__c, LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name, CreatedById 
                            From Template_Submissions__r Limit : iOne)  
                           From Template_Submission_Owner__c WHERE Object_Owner_ID__c =: casObj.Id Limit : iOne])
                {
                    if(oTSO.Template_Submissions__r != NULL && oTSO.Template_Submissions__r.size() > iZero)
                    		lstSObj = (List<Template_Submission__c>)oTSO.Template_Submissions__r;
                }
            }
            
        }
    }
}