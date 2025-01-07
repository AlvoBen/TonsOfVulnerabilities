/*****************************************************************************************************************************************
Apex Class Name : HUMCaseNewEditButtonOverrideExtension
Version         : 1.0
Created On      : 06/13/2014
Function        : This class is used to redirect to queue selection page, if logged user doesn't selected any workqueue and tries
to create a case/ edit a case
Test Class      : HUMCaseNewEditButtonOverrideExtenTest

Modification Log:
*   Developer Name                Code Review                        Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------
*   SuryaKumari M                    17093                         06/13/2014                   Original Version
*   SuryaKumari M                    17093                         06/17/2014                   Case Edit Functionality Added
*   Pradeep Raju                                                   09/22/2015                   Updated the code as part of REQ-160856
*   Avinash Choubey                                                01/20/2016                   modified line no 147 to set page refrence to CaseEdit_VF_HUM
*   Avinash Choubey                  29427                         01/25/2016                   fixes for review no 29427
*   Prasad Jandhyala                 180277                        02/07/2018                   REQ - 335173 : Added logic for redirection to CaseClosed_Edit_VF_HUM
*   Suraj Patil                      187433                        10/16/2017                   Changes made for Req 333803
*   Bharath Rajappan                                               05/05/2018                   REQ - 360169 - Redirect to case Edit
*   Prathyu Tushar	                 324032                        09/27/2018                   REQ - 380419: Auto route cases for Expedited and Part D Redetermination Process
*   Vinay Lingegowda                             		           08/24/2020                	User Story 1314983: PR00091574- IR 5050-OGO -Open case in Edit mode to set complaint fields (Scrubber Action-Accept on open cases) -(CRM)
*   Vinay Lingegowda                             		           02/19/2021                	User Story 1941154: PR00091574 - IR5050 - INC0855442 - Medicare Good Cause Reinstatement  - Case auto routing to Other queue View (CRM)
*   Prudhvi Pamarthi                                               04/08/2021                   US#2076136 Checkmarx scan issue fix 
*   Kavya Bondada                                                  04/14/2021                   User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
*   Mithra Bharadwaj 										                                    US#2089563 Sonar Qube fix
*   Amar Gadewar                                                   05/21/2021                   User Story 1874589: T1PRJ0001827 - MF 1 - AHT - "Medicaid PAR Provider Not Accepting" template auto routing (CRM)
*   Samantha Tennyson                                              10/28/2021                   User Story 2786833: T1PRJ0001827 - MF 1 - AHT - Ability to automatically create a case when an ID Card Request is successfully submitted (RF)
******************************************************************************************************************************************/
public with sharing class HUMCaseNewEditButtonOverrideExtension
{
    private String sCaseId ='';
    private String sPageFrom = '';
    private String sCaseRecType = '';
    private String sAccountId = '';
    private Id accRecordTypeId = null;
    private String sAccRecordTypeName = '';
    private String sCaseRecordType = '';   
   
    @TestVisible private boolean bForTesting = false; // For Code Coverage    
    
    /*
    * Method Name : HUMCaseNewEditButtonOverrideExtension
    * Description : Constructor for the class , intialize variables , read page parameter Values
    * Return type : N/A
    * Paramater   : StandardController
    */
    public HUMCaseNewEditButtonOverrideExtension(ApexPages.StandardController controller)
    {
    }
    
    public String sPageTitle {get; set;}
      
    /*
    * Method Name : reDirectPage
    * Description : This method is used to set page parameters for next page based on user workqueue value and redirects to
    next page when user clicks on New Case button.
    * Return type : PageReference
    * Paramater   : N/A
    */
    public PageReference reDirectPage()
    {
        try
        {
            sCaseRecordType = ApexPages.Currentpage().getParameters().get(HUMConstants.HUMRecordType).escapeHtml4();
            User oCurrentUser = [select Id,Current_Queue__c from User where id =: Userinfo.getUserId()];
            
            if(String.isEmpty(oCurrentUser.Current_Queue__c))
            {
                PageReference pageRef = new PageReference(HUMConstants.HUMApexinURL + HUMConstants.HUMQueueSelectionPage);
                pageRef.getParameters().put(HUMConstants.HUMNextPage, HUMConstants.HUMNewCaseRedirectPage);
                pageRef.getParameters().put(HUMConstants.HUMObjectType, HUMConstants.OBJECT_CASE);
                
                if(!String.isEmpty(sCaseRecordType))    pageRef.getParameters().put(HUMConstants.HUMRecordType, sCaseRecordType );
                
                return pageRef;
            }
            else
            {
                PageReference pageRef = new PageReference(HUMConstants.HUMApexinURL + HUMConstants.HUMNewCaseRedirectPage);
                
                if(!String.isEmpty(sCaseRecordType))    pageRef.getParameters().put(HUMConstants.HUMRecordType, sCaseRecordType );
                
                return pageRef;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HUMCaseNewEditButtonOverrideExtension','reDirectPage');
            return null;
        }
    }
    
    /*
    * Method Name : reDirectPageForEdit
    * Description : This method is used to set page parameters for next page based on user workqueue value and redirects to
    next page when user clicks on edit case button.
    * Return type : PageReference
    * Paramater   : N/A
    */
    public PageReference reDirectPageForEdit()
    {
        String sUrl = 'e?';
        String sCaseName;
        String sNxtPageforEdit;
        try
        {
            User oCurrentUser = [select Id, Current_Queue__c from User where id =: Userinfo.getUserId()];
            
            sCaseId = ApexPages.Currentpage().getParameters().get(HUMConstants.HUMId);
            sPageFrom = ApexPages.Currentpage().getParameters().get(HUMConstants.HUM_CASEEDIT_PAGEFROM);
            sCaseRecType =  ApexPages.Currentpage().getParameters().get(HUMConstants.HUMcaseRecordType);
            if(!String.isEmpty(sCaseId))    sUrl += 'retURL=' + sCaseId.escapeHtml4();
            if(!String.isEmpty(sCaseRecType))    sUrl += '&RecordType=' + sCaseRecType.escapeHtml4();
            sUrl += '&nooverride=1';
            sNxtPageforEdit = '/' + sCaseId.escapeHtml4() + '/' + sUrl;
                       
            if(String.isEmpty(oCurrentUser.Current_Queue__c))
            {
                PageReference pageRef = new PageReference(HUMConstants.HUMApexinURL + HUMConstants.HUMQueueSelectionPage);
                
                sCaseName = [select CaseNumber from Case where Id =: sCaseId].CaseNumber;
                pageRef.getParameters().put(HUMConstants.HUMNextPage, sNxtPageforEdit);
                pageRef.getParameters().put(HUMConstants.HUMObjectType, HUMConstants.OBJECT_CASE);
                pageRef.getParameters().put(HUMConstants.HUMobjectNumber, sCaseName);
                pageRef.getParameters().put(HUMConstants.HUMObjectId, sCaseId.escapeHtml4());
                
                if(sPageFrom == HUMConstants.HUM_CASEEDIT_CASEHISTORY)
                {
                    pageRef.getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesFromCaseHistory);
                    sPageTitle = HUMConstants.HUM_CASEEDIT_NEWCASE;
                }
                else if(sPageFrom == HUMConstants.HUM_CASEEDIT_ACCOUNTRELLIST)
                {
                    pageRef.getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesFromAccountRelatedList);
                    sPageTitle = HUMConstants.HUM_CASEEDIT_NEWCASE;
                }
                else
                {
                    pageRef.getParameters().put(HUMConstants.HUMActionType, HUMConstants.HUMQueueYesForEdit);
                    sPageTitle = HUMConstants.HUM_CASEEDIT_CASENAME + sCaseName;
                }
                return pageRef;
            }
            else
            {
                       
                List<Case> lstCaseRecord = [Select id, caseNumber, RecordType.Name from Case where id=:scaseId];  
                Boolean bCaseRecordType = HUMUtilityHelper.validateCaseRecordType(lstCaseRecord[0].RecordType.Name);
                if(bCaseRecordType)
                {
                   PageReference pg = new PageReference(HUMConstants.HUMApexinURL + HUMConstants.HUM_CASEEDIT_EDITPAGENAME);
                   pg.setRedirect(true);
                   pg.getParameters().put(HUMConstants.HUMId, scaseId.escapeHtml4());
                   pg.getParameters().put('isATTVF',Apexpages.currentPage().getParameters().get('isATTVF'));
                   pg.getParameters().put('isExp',Apexpages.currentPage().getParameters().get('isExp'));
                   pg.getParameters().put('isPart',Apexpages.currentPage().getParameters().get('isPart'));
                   if(Apexpages.currentPage().getParameters().containskey('isHP'))
                   {
                        pg.getParameters().put('isHP', Apexpages.currentPage().getParameters().get('isHP'));
                   }
                   if(Apexpages.currentPage().getParameters().containskey('isOGO')){
                       pg.getParameters().put('isOGO',Apexpages.currentPage().getParameters().get('isOGO'));
                   }
                   if(Apexpages.currentPage().getParameters().containskey('isGoodCause'))
                    {
                        pg.getParameters().put('isGoodCause',Apexpages.currentPage().getParameters().get('isGoodCause'));
                    }
                    if(Apexpages.currentPage().getParameters().containskey('isPCP'))
                    {
                        pg.getParameters().put('isPCP',Apexpages.currentPage().getParameters().get('isPCP'));
                    }
		            if(Apexpages.currentPage().getParameters().containskey('isQAA')){
                       pg.getParameters().put('isQAA',Apexpages.currentPage().getParameters().get('isQAA'));
                    }
		    if(Apexpages.currentPage().getParameters().containskey('isMedicaidPARProvider')){
                       pg.getParameters().put('isMedicaidPARProvider',Apexpages.currentPage().getParameters().get('isMedicaidPARProvider'));
                    }
                    if(Apexpages.currentPage().getParameters().containskey('PageRequestIDFrom')){
                        pg.getParameters().put('PageRequestIDFrom',Apexpages.currentPage().getParameters().get('PageRequestIDFrom'));
                    }
					if(Apexpages.currentPage().getParameters().containskey('isEnrollmentWelcomeCall')){
                        pg.getParameters().put('isEnrollmentWelcomeCall',Apexpages.currentPage().getParameters().get('isEnrollmentWelcomeCall'));
                    }
                   return pg;
                }               
                else 
                {
                     Boolean bIsClosedCase = HUMUtilityHelper.validateClosedCaseRecordType(lstCaseRecord[0].RecordType.Name);
                    if(bIsClosedCase) {
                        PageReference pg = new PageReference(HUMConstants.HUMApexinURL + GLOBAL_CONSTANT_HUM.CASECLOSEDEDITPAGE  );
                        pg.setRedirect(true);
                        pg.getParameters().put(HUMConstants.HUMId, scaseId.escapeHtml4());
                        return pg;
                    }
                    else
                    {
                        return new PageReference(sNxtPageforEdit);
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMCaseNewEditButtonOverrideExtension', 'reDirectPageForEdit');
            return null;
        }
    }
}