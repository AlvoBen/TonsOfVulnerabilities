/*******************************************************************************************************************************
Apex Class Name : HUMLaunchCaseDisenrollmentExtension
Version         : 1.0
Created On      : 5/12/2015
Function        : This class is used to redirect to Case Disenrollment and Cancellation template page
Test Class      : HUMLaunchCaseDisenrollmentExtensionTest
          
Modification Log: 
* Developer Name                       Code Review                       Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
    SuryaKumari Medicherla              23305                          05/12/2015                 Original Version
    Harshith Mandya                     28341                          12/01/2015                 Implemented REQ - 215100
    Pallavi Ravishankar                 49663                          11/30/2016                 Defect fix 250549.0001 - Read encrypted data from custom settings for mdac attachment key    
    Prudhvi Pamarthi                    164129                         01/19/2018                 REQ - 328929 Fix for HUMExceptionHelper: DML 
    Sunil Bukya                         293887                         10/03/2018                 REQ - 369758 Implemented platform changes.
    Jasmeen Shangari                                                   04/21/2021                 Checkmarx fixes Azure ID - 2163309
****************************************************************************************************************************/

public with sharing class HUMLaunchCaseDisenrollmentExtension
{
    private String sGivenURL = '';
    private Id caseId = NULL;
    private List<Attachment_Log__c> lstAttachmentLog = new List<Attachment_Log__c>();
    private List<Attachment_Log_Item__c> lstAttachmentLogItem = new List<Attachment_Log_Item__c>();
    private String caseSubTabId = NULL;
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
   /**
    *  This is the constructor method used to intialize variable 
    *  @name HUMLaunchCaseDisenrollmentExtension
    *  @param ApexPages.StandardController - instance of standard controller for case
    *  @return NA
    *  @throws exception - HUMLaunchCaseDisenrollmentExtension
    */
    public HUMLaunchCaseDisenrollmentExtension(ApexPages.StandardController controller) 
    {
        try
        {
            HUMConstants__c constObj = HUMConstants__c.getInstance(GLOBAL_CONSTANT_HUM.HUMATTACHMENTLOGFORDAC_HUM);
            HUMConstants__c constPlainTextObj = HUMConstants__c.getInstance('PLAIN_ATTACHMENTLOGFORDAC_HUM');
            sTabId = Apexpages.currentPage().getParameters().get(HUMConstants.HUM_CASEACTIONLAUNCH_HUMFLOWTABID) != null ? Apexpages.currentPage().getParameters().get(HUMConstants.HUM_CASEACTIONLAUNCH_HUMFLOWTABID).escapehtml4() : null;
            caseId = (Apexpages.currentPage().getParameters().containsKey(HUMConstants.HUM_MEDICAREDAC_CASEID)  && Apexpages.currentPage().getParameters().get(HUMConstants.HUM_MEDICAREDAC_CASEID) != null) ? Apexpages.currentPage().getParameters().get(HUMConstants.HUM_MEDICAREDAC_CASEID).escapehtml4() : null;
            caseSubTabId = Apexpages.currentPage().getParameters().get(HUMConstants.HUM_CASEACTIONLAUNCH_CASESUBTABID) != null ? Apexpages.currentPage().getParameters().get(HUMConstants.HUM_CASEACTIONLAUNCH_CASESUBTABID).escapehtml4() : null;
            if(caseSubTabId != NULL)
                sTabId = caseSubTabId;
            if(caseId != NULL) 
            {
                oCase = [Select Policy_Member__c, Policy_Member__r.Name,Policy_Member__r.Policy__r.Contract_Number__c,Policy_Member__r.Policy__r.PBP_Code__c, CaseNumber from Case where id = :caseId]; 
                if(constObj != NULL && constPlainTextObj != NULL) lstAttachmentLog = [Select id, Attachment_Key__c,Attachment_Type__c from Attachment_Log__c where Case__c = :caseId and (Attachment_Key__c = : constObj.StringValue__c OR Attachment_Key__c = : constPlainTextObj.StringValue__c) limit 1];    
            }
            createDisenrollmentURL();
        }
        catch(Exception e)
        {            
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(e,'HUMLaunchCaseDisenrollmentExtension','HUMLaunchCaseDisenrollmentExtension');
        }
    }
    
    public String sTabId { get; set; }
    public Case oCase{get;set;}
    public String sParameterURL{get; set;}
    
    /*
     * Method Name : createDisenrollmentURL
     * Description : This method is used to form URL for disenrollment template
     * Return type : Void
     * Paramater   : NA
     */
    public void createDisenrollmentURL()
    {
        String sConstantURL1 = HUMConstants.HUMDisEnrollmentURL1;
        String sConstantURL2 = HUMConstants.HUMDisEnrollmentURL2;
        String sConstantURL3 = HUMConstants.HUMDisEnrollmentURL3;
        String sCaseIdParam = HUMConstants.HUMCaseIDParameter;
        String sPolicyDetailsParameter = HUMConstants.HUMPolicyDetailsParameter;
        String sPlanInformation = '';  
        String sPolicyMemberName = '';   
        try
        {
            if(oCase != NULL && oCase.Policy_Member__c != NULL)
            {
                if(lstAttachmentLog.size() > 0)
                {
                    lstAttachmentLogItem = [Select Value__c from Attachment_Log_Item__c where Attachment_Log__c = :lstAttachmentLog[0].id limit 1];    
                    if(lstAttachmentLogItem.size() > 0)
                    {
                        sParameterURL = lstAttachmentLogItem[0].Value__c;
                    }
                }
                else
                {
                    sGivenURL = HUM_Webservice_Callout__c.getInstance('HUMDisenrollmentCancellationService').End_Point_URL__c;
                    if(!String.isBlank(oCase.Policy_Member__r.Policy__r.Contract_Number__c) && !String.isBlank(oCase.Policy_Member__r.Policy__r.PBP_Code__c))
                    {
                        sPlanInformation = oCase.Policy_Member__r.Policy__r.Contract_Number__c + '/'+  oCase.Policy_Member__r.Policy__r.PBP_Code__c + '/'+'000';
                    }
                    else
                    {
                        sPlanInformation = HUMConstants.HUMDisenrollmentPlanInfo;
                    }
                    
                    sPolicyMemberName = oCase.Policy_Member__r.Name;
                    sParameterURL = sGivenURL+sCaseIdParam+oCase.CaseNumber+sConstantURL1+sConstantURL2+sConstantURL3+sPolicyMemberName + sPolicyDetailsParameter + sPlanInformation;
                }
            }
        }
        catch(Exception e)
        {            
            HUMExceptionHelper.logErrors(e,'HUMLaunchCaseDisenrollmentExtension','createDisenrollmentURL');
        }
    }
    
    /*
     * Method Name : createDisenrollmentURL
     * Description : This method is used to form URL for disenrollment template
     * Return type : Void
     * Paramater   : NA
     */
    public pageReference reDirectPage()
    {
        if(!String.isBlank(sParameterURL))
        {
            PageReference objPageRef = new PageReference(sParameterURL);
            return objPageRef;
        }
        else
        {
            return null;    
        }
    }
}