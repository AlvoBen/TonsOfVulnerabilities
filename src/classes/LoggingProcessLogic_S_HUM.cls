/****************************************************************************************************************************
Apex Class Name  : LoggingProcessLogic_S_HUM
Version          : 1.0
Created Date     : 03/02/2016
Function         : This is service layer class used to execute logging functionality.
Test Class       : LoggingProcessLogic_T_HUM
****************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                    03/02/2016                 Original Version
* Santhi Mandava            24308                    04/05/2016                 Fixed defect 221734.0001
* Santhi Mandava            26846                    05/04/2016                 Removed 'type' field value calculation logic. 
* Santhi Mandava            27512                    05/16/2016                 Changed wrapper variables
* Manish Kumar              29967                    06/13/2016                 Changed implementation of key and description of Attachment object
* Manish Kumar              32175                    07/18/2016                 Fixed defect#D-00463. Enabled encryption for logged records
* Manish Kumar              35877                    08/15/2016                 Worked on REQ#279125 for Attachment_Key encryption
* Manish Kumar                                       08/24/2016                 Worked on REQ#275929 for DPaaS error handling.
* Manish Kumar                                       08/26/2016                 Worked on defect#239472, changed field length from 32700 to 131000 & name of Attachment file
* Anurag Shah               201051                   03/13/2018           		REQ - 354520:: SF - USER - Research/Attachments - MA Post Claims Summary.
* Anurag Shah               235263                   05/23/2018          		Reverting the Resolution changes.
* Prathyu Tushar                                     08/07/2019          		Defect#832291, new method to call ExtensionController to create pharmacy related case.
* Kiran Kumar										 06/26/2020					REQ - 941987: Automatically Transfer Case When 'Invoice Request' Guided Process Attached (RxRF)
* Kiran Kumar Bhuvanagiri	 						 11/03/2020					User Story - 815259: Automatically Record Credit Card Updates to Case: Add Logging Component to 'Edit Credit Card' Pop-up (RxRF)
* Lakshmi Madduri                                    05/05/2021                 2235919 - Separate constant class for logging framework
* Ashok Kumar Nutalapati            				 09/30/2021            		REQ - 2593758 T1PRJ0002517 - MF 2 - New case button, create case, & open new case in a subtab (RXRF) (ID# 35b)
* AshokKumar Nutalapati                         	 01/10/2022                 REQ - 2843422 T1PRJ0002517 - MF 1 - Inactivate Rx: Process Content (RxRF) (ID# 163)
************************************************************************************************************************************/

public with sharing class LoggingProcessLogic_S_HUM
{
    @testVisible private static final String attachId = 'Authorization Id';
    
    public static String sRedirectURL = '';
	public static Boolean bInvoiseRequest = false;
	public static Boolean bOneTimeCC = false;
	public static Boolean bAddressLWC = false;
    
     public static String sPolicyMemberId='';
     
     public static Interaction_Member__c interactionRecord;
	public static Boolean bInactivateRx = false;
    public static String sClinicalTransfer = '';
    /*
    * Method Name : SearchCases
    * Description : This method is used to filter cases based on case number.
    * Return type : List<Case>
    * Parameter   : String,List<Case>
    */
    public static List<Case> SearchCases(String sCaseNumber,List<Case> lstFinalCases)
    {
        List<Case> lstCases = new List<Case>();
        if(String.isNotBlank(sCaseNumber))
        {
            for(Case obj : lstFinalCases )
            {
                if(obj.CaseNumber == sCaseNumber)
                {
                    lstCases.add(obj);
                }
            }
        }
        else
        {
            lstCases = lstFinalCases;
        }
        return lstCases;
    }
    
    /*
    * Method Name : createNewCase
    * Description : This method is used to create a new case.
    * Return type : String
    * Parameter   : Id, String
    */
    public static String createNewCase(Id sObjectId,String sStandardUrl)
    {
        String sSelectedCaseId='';
        try
        {
            HUMNewCaseRedirectExtension caseRedExt = new HUMNewCaseRedirectExtension(NULL);
            caseRedExt.objectID = sObjectId;
            caseRedExt.sObjectName = sObjectId.getSobjectType().getDescribe().getName();
            caseRedExt.sStandardUrlForTab = sStandardUrl ;
            caseRedExt.funcOnPageLoad();
            caseRedExt.saveCase();
            sSelectedCaseId = caseRedExt.caseID;
            sRedirectURL = caseRedExt.sRedirectURL;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'LoggingProcessLogic_S_HUM','createNewCase');
        }
        return sSelectedCaseId;
    }
    
    /*
    * Method Name : createNewPharmacyLoggingCase
    * Description : This method is used to create a new case from create order logging component.
    * Return type : String
    * Parameter   : Id, String
    */
    public static String createNewPharmacyLoggingCase(Id sObjectId,String sStandardUrl)
    {
        String sSelectedCaseId='';
        try
        {
            HUMNewCaseRedirectExtension caseRedExt = new HUMNewCaseRedirectExtension(NULL,true);
            caseRedExt.objectID = sObjectId;
            caseRedExt.sObjectName = sObjectId.getSobjectType().getDescribe().getName();
            caseRedExt.sStandardUrlForTab = sStandardUrl ;
            caseRedExt.funcOnPageLoad();
            caseRedExt.saveCase();
			LoggingProcessLogic_S_HUM.bInvoiseRequest = false;
            sSelectedCaseId = caseRedExt.caseID;
            sRedirectURL = caseRedExt.sRedirectURL;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'LoggingProcessLogic_S_HUM','createNewPharmacyLoggingCase');
        }
        return sSelectedCaseId;
    }
	
	/*
	* Method Name : createNewPharmacyAddressCase
	* Description : This method is used to create a new case from Address LWC.
	* Return type : String
	* Parameter   : Id, String
	*/
	public static String createNewPharmacyAddressCase(Id sObjectId)
	{
		String sSelectedCaseId='';
		try
		{    
			HUMNewCaseRedirectExtension caseRedExt = new HUMNewCaseRedirectExtension(NULL,NULL,TRUE);
			caseRedExt.objectID = sObjectId;
			caseRedExt.sObjectName = sObjectId.getSobjectType().getDescribe().getName();
			caseRedExt.funcOnPageLoad();
			caseRedExt.saveCase();
			sSelectedCaseId = caseRedExt.caseID;
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex,'LoggingProcessLogic_S_HUM','createNewPharmacyAddressCase');
		}
		return sSelectedCaseId;
	}

    /*
    * Method Name : createAttachmentLog
    * Description : This method is used to create attachment log records.
    * Return type : Id
    * Parameter   : LoggingWrapper_DTO_HUM
    */
    public static Id createAttachmentLog(LoggingWrapper_DTO_HUM objWrapper)
    {        
        Id oAttchId = Null;
        String userHighlightedValue = objWrapper.sJsonMap;
        
        try
        {
            Attachment_log__c attachLog = new Attachment_Log__c (
                Case__c = objWrapper.sSelectedCaseId,
                Attachment_Key__c = objWrapper.keyValue,
                Attachment_Description__c = objWrapper.keyDescValue,
                Attachment_Type__c = objWrapper.sType,
                Attachment_Sub_type__c = objWrapper.sSubType,
                Source_System__c = LOGGING_CONSTANT_HUM.STRING_CRM,
                Logging_URL__c = '',
                Standard_Value__c = String.isNotBlank(objWrapper.sJsonStandardMap) ? objWrapper.sJsonStandardMap : '',
                User_Selected_Value__c = userHighlightedValue.length() > 131000 ? userHighlightedValue.subString(0, 131000) : userHighlightedValue,
                User_Selected_Value_EXT__c = userHighlightedValue.length() > 131000 ? userHighlightedValue.subString(131000, userHighlightedValue.length()) : '',
                Created_By__c = LoggingDataLogic_D_HUM.getUser(UserInfo.getUserId()).Network_User_Id__c,
                CreatedByQueue__c = getCreatedByQueue(objWrapper.sSelectedCaseId)
            );
            oAttchId = LoggingDataLogic_D_HUM.insertAttachementLogRecord(attachLog);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'LoggingProcessLogic_S_HUM','createAttachmentLog');
        }
        return oAttchId;
    }
    
    /*
    * Method Name : getKeyDesc
    * Description : This method is used to get key and description for Attachment object
    * Return type : String
    * Parameter   : Id
    */
    public static String getKeyDesc(Id accId)
    {
        return LoggingDataLogic_D_HUM.getAccountEnterpriseID(accId);
    }
    
    /*
    * Method Name : getCreatedByQueue
    * Description : This method is used to calculate created by queue value based on case owner.
    * Return type : Id
    * Parameter   : String
    */
    @testVisible private static String getCreatedByQueue(String sSelecCaseId)
    {
        String sCreatedByQueue = '';
        Case objCase = LoggingDataLogic_D_HUM.getCaseDetails(sSelecCaseId);
        If(objCase != null)
        {
            If(objCase.Case_Owner__c == UserInfo.getName())
            {
                sCreatedByQueue = objCase.Owner_Queue__c;
            }
            else
            {
                sCreatedByQueue = LoggingDataLogic_D_HUM.getUserCurrentQueue();
            }
        }
        return sCreatedByQueue;
    }
    
    /*
    * Method Name : createAttachment
    * Description : This method is used to create attachment records.
    * Return type : Void
    * Parameter   : Id
    */
    public static void createAttachment(Id attchlogId, String screenshotData)
    {
        Blob sBody = Null;
        if(String.isNotBlank(screenshotData)) sBody = Blob.valueOf(screenshotData);
        Attachment attachRec = new Attachment(
                Body = sBody,
                ContentType = 'data:image/png;base64',
                Description = '',
                Name = 'Logging_Screenshot_Encrypted.png',
                OwnerId = UserInfo.getUserId(),
                parentId = attchlogId);
            
            LoggingDataLogic_D_HUM.insertAttachementRecord(attachRec);
    }
    /*
    * Method Name : updateAttachmentLog
    * Description : This method is used to update attachment records.
    * Return type : void
    * Parameter   : Id, LoggingWrapper_DTO_HUM
    */
    public static void updateAttachmentLog(Id attchlogId, LoggingWrapper_DTO_HUM objWrapper)
    {
        if(String.isNotBlank(attchlogId))
        {
            List<Attachment_Log__c> lstAttachmentLog = LoggingDataLogic_D_HUM.getAttachmentRecord(attchlogId);
            if(lstAttachmentLog != NULL && !lstAttachmentLog.isEmpty())
            {
                String userHighlightedValue = objWrapper.sJsonMap;
                lstAttachmentLog[0].User_Selected_Value__c = String.isNotBlank(userHighlightedValue) ? userHighlightedValue.length() > 131000 ? userHighlightedValue.subString(0, 131000) : userHighlightedValue: '';
                lstAttachmentLog[0].User_Selected_Value_EXT__c = String.isNotBlank(userHighlightedValue) ? userHighlightedValue.length() > 131000 ? userHighlightedValue.subString(131000, userHighlightedValue.length()) : '': '';
                LoggingDataLogic_D_HUM.updateAttachmentRecord(lstAttachmentLog);
            }
        }
    }
    
    /*
    * Method Name : getAttachments
    * Description : This method is used to retrieve attachment records.
    * Return type : List<LoggingWrapper_DTO_HUM>
    * Parameter   : String
    */
    public static List<Attachment> getAttachments(String sAttachId)
    {
        return  LoggingDataLogic_D_HUM.getAttachmentDetails(sAttachId);
    }
    
    /*
    * Method Name : createErrorLog
    * Description : This method is used to create dpaas error log record.
    * Return type : Void 
    * Parameter   : NA
    */
    public static String createErrorLog(String sErrorMessage,String sClassName)
    {
        try {
            throw new HUMCustomException(sErrorMessage);
        } catch (Exception ex) {
            Error_Log__c errorLog = HUMExceptionHelper.logCallOutExceptions(ex,sClassName,'createErrorLog');
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.saveLog(new List<Error_Log__c>{errorLog}, false);
            errorLog = LoggingDataLogic_D_HUM.getErrorLogObject(errorLog.Id);
            return ' Error ' + errorLog.Name;
        }
    }
}