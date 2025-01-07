/**********************************************************************************************************
Apex Class Name  : AttachLogDetailsToCase_C_HUM
Version          : 1.0
Created Date     : 02/18/2016
Function         : This is controller class for visual force component 'AttachLogDetailsToCase_CMP_HUM'.
                    This is used to attach logging details to either new case or to an existing case.
Test Class       : AttachLogDetailsToCase_T_HUM
***********************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                    02/18/2016                 Original Version
* Santhi Mandava            24069                    04/01/2016                 Fixed defect 221350.0001. 
* Manish Kumar              24880                    04/11/2016                 Added queue selection logic
* Manish Kumar              25386                    04/20/2016                 Fixed defect#223482.0001. Sorting logic update
* Santhi Mandava            26846                    05/05/2016                 Modified 'createAttachmentLogRecords' method to consider subtype.
* Santhi Mandava            27512                    05/16/2016                 Corrected LoggingWrapper_DTO_HUM class variable names. 
* Manish Kumar              32175                    07/18/2016                 Worked on REQ-275930. Enabled encryption for logged records 
* Manish Kumar              35877                    08/15/2016                 Worked on REQ#279125 for Attachment_Key encryption
* Santhi Mandava            48114                    08/24/2016                 Worked on REQ#275929 for DPaaS error handling.
* Manish Kumar              59048                    01/27/2017                 Worked on Defect#243750, REQ#296700. Incorporated changes for Datatable
                                                                                in "Existing case" scenario.
* Prasanthi Kandula                                  02/16/2017                 Fixed defect 259573.0001    
* Asish Behera                                       03/20/2018                 REQ - 357690-Ability to Attach an Interaction to an Existing Case When Logging CR5581 (RF)           
* Prudhvi Pamarthi                                   02/06/2019                 REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Asish Behera                                       02/26/2019                 REQ - 383941 User - Ability to set the Classification on the Case Edit Page Based on the Page a User Logged from (RF)
* Ravi Prakash kantamneni  345989                    03/21/2019                 REQ-383718 - Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)
* Amar Gadewar										 04/11/2019					REQ - 395541 - AHT - All Users - Ability to set the Classification on the Case Edit Page Based on the Page a User Logged from (CRM)
* Prathyu Tushar		   355095					 05/02/2019				    REQ - 397913: CA Incident # 8244067, Removed select order attribute.
* Prathyu Tushar		   356946					 05/22/2019				    Defect# 417759.0001, Added Method to get Pharmacy RecardType cases on account.
* Prathyu Tushar		        					 07/17/2019				    REQ - 39602, new methods to fetch the cases in ID-27 logging component. 
* Prathyu Tushar		        					 08/07/2019				    Defect# 432291, New method to create New case from Create Order Logging Component ID - 27.
* Prasuna Pattabhi                                   11/11/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant - Account And Policy Pages
* Prasuna Pattabhi                                                      11/12/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant CaseManagement_Constants_HUM reference added
*************************************************************************************************************/

public with sharing class AttachLogDetailsToCase_C_HUM extends LogDetails_A_HUM
{
    @testVisible private Id accId = NULL;
    @testVisible private String sObjName = '';
    @testVisible private String sSortExp = CaseManagement_Constants_HUM.SORTDATE_HUM;
    @testVisible private String sSortDirection = CaseManagement_Constants_HUM.Sort_Expression_DESC_HUM;
    @testVisible private Case oCaseRec = new Case();
    @testVisible private Id idAttLogId = NULL;
    @TestVisible private transient String sCaseDetailsJSONreturn = '';
    Public Static final Integer Minus90 = -90;
    /*
    * Method Name : AttachLogDetailsToCase_C_HUM 
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public AttachLogDetailsToCase_C_HUM ()
    {
        displayInfoMeaasge = false;
        bIsExistingCase = false;
        lstFinalCases = new List<Case>();
        createNewAttachmentLog = 'false';
    }
    
    Public Boolean blegacyDeleteCheck {get; set;}
    public String enterpriseId {get; private set;}
    public String sRedirectURL {get; set;}
    public Id sObjectId{get;set;}
    public String selectedCaseOption{get;set;}
    public ApexPages.StandardSetController stdsetcon {get;set;}
    public String sCaseNumber{get;set;}
    public String sSelectedCaseNumber{get;set;}
    public String sSelectedCaseId{get;set;}
    public String sCompMessage{get;set;}
    public Boolean bIsExistingCase{get;set;}
    public Boolean displayInfoMeaasge {get; set;}
    public String sCaseId {get; set;}
    public String createNewAttachmentLog {get; set;}
    public String userCurrentQueue {get; set;}
    public String dpaasErrorLogName {get; set;}
    public transient List<Case> lstFinalCases {get; private set;}
    public Integer caseLstSize {get; private set;}
    public String sInteractionId {get;set;}    
    public  Static String sClassfication {get; set;} 
    public string sCaseDetailsJSON
    {
        get 
        {
            if(lstFinalCases != NULL)
            {
                sCaseDetailsJSONreturn = JSON.serialize(lstFinalCases); 
                sCaseDetailsJSONreturn = '{"data":'+sCaseDetailsJSONreturn+'}';
                return sCaseDetailsJSONreturn;
            }
            else
            {
                return null;
            }
        }
        private set;
    }
    
    /*
    * Method Name : displayCaseList
    * Description : This method is used to retrieve cases associated to an account.
    * Return type : Void
    * Parameter   : NA
    */
    public void displayCaseList()
    {
        try
        {
            bIsExistingCase= false;
            if(selectedCaseOption == 'Existing Case')
            {
                sSelectedCaseId ='';
                sSelectedCaseNumber = '';
                bIsExistingCase = true;
                sCaseNumber = '' ;
                if(String.isBlank(accId) || String.isBlank(sObjName))
                {
                    sObjName = getSObjName(sObjectId);
                    accId = LoggingHelper_H_HUM.getAccountId(sObjectId, sObjName);
                }
                
                returnListOfCase();
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachLogDetailsToCase_C_HUM','displayCaseList');
        }
    } 
    
    /*
    * Method Name : returnListOfCase
    * Description : This method is used to retrieve cases associated to an account.
    * Return type : Void
    * Parameter   : NA
    */
    public void returnListOfCase()
    {
        try
        {
            String startDate = ApexPages.currentPage().getParameters().get('searchStartDate');
            String endDate = ApexPages.currentPage().getParameters().get('searchEndDate');
            String caseNumberToSearch = ApexPages.currentPage().getParameters().get('searchCaseNumber');
            Pattern TimePttrn = Pattern.compile('(19|20)\\d\\d([- /.])(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])');
            DateTime todayDate = DateTime.now();
            DateTime lastDate = (DateTime)Date.today().addDays(Minus90);
            Integer limitVal = (Integer)HUMConstants__c.getInstance('Size_TwoHundred').IntegerValue__c;
            
            startDate = String.isNotBlank(startDate) ? String.escapeSingleQuotes(startDate) : null;
            endDate = String.isNotBlank(endDate) ? String.escapeSingleQuotes(endDate) : null;
            caseNumberToSearch = String.isNotBlank(caseNumberToSearch) ? String.escapeSingleQuotes(caseNumberToSearch) : null;
            
          if(String.isNotBlank(startDate) && String.isNotBlank(endDate) && TimePttrn.matcher(startDate).matches() && TimePttrn.matcher(endDate).matches())
            {
                lastDate = System.DateTime.newInstance(Integer.valueOf(startDate.subString(0,4)), Integer.valueOf(startDate.subString(5,7)), Integer.valueOf(startDate.subString(8,10)), 00, 00, 00);
                todayDate = System.DateTime.newInstance(Integer.valueOf(endDate.subString(0,4)), Integer.valueOf(endDate.subString(5,7)), Integer.valueOf(endDate.subString(8,10)), 23, 59, 59);
            }
            
            lstFinalCases = LoggingDataLogic_D_HUM.retrieveAccountCases(accId, lastDate, todayDate, caseNumberToSearch, limitVal);
            if(String.isNotBlank(caseNumberToSearch))
            {
                caseLstSize = lstFinalCases.size();
            }
            else
            {
                caseLstSize = LoggingDataLogic_D_HUM.getCaseCount(accId, lastDate, todayDate);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachLogDetailsToCase_C_HUM','returnListOfCase');
        }
    }
    
    /*
    * Method Name : retrievePharmacyOrderCases - RXRF - ID-27
    * Description : This method is used to retrieve cases associated to an Policy member.
    * Return type : Void
    * Parameter   : startDate, endDate, caseNumberToSearch, limitVal
    */
    public void retrievePharmacyOrderCases()
    {
        try
        {
            bIsExistingCase= false;
            if(selectedCaseOption == 'Existing Case')
            {
                sSelectedCaseId ='';
                sSelectedCaseNumber = '';
                bIsExistingCase = true;
                sCaseNumber = '' ;
                if(String.isBlank(accId) || String.isBlank(sObjName))
                {
                    sObjName = getSObjName(sObjectId);
                    accId = LoggingHelper_H_HUM.getAccountId(sObjectId, sObjName);
                }
                returnListOfPharmacyCase();
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachLogDetailsToCase_C_HUM','retrievePharmacyOrderCases');
        }
    }
    
    /*
    * Method Name : returnListOfCase
    * Description : This method is used to retrieve cases associated to an account.
    * Return type : Void
    * Parameter   : NA
    */
    public void returnListOfPharmacyCase()
    {
        try
        {
            String startDate = ApexPages.currentPage().getParameters().get('searchStartDate');
            String endDate = ApexPages.currentPage().getParameters().get('searchEndDate');
            String caseNumberToSearch = ApexPages.currentPage().getParameters().get('searchCaseNumber');
            Pattern TimePttrn = Pattern.compile('(19|20)\\d\\d([- /.])(0[1-9]|1[012])\\2(0[1-9]|[12][0-9]|3[01])');
            DateTime todayDate = DateTime.now();
            DateTime lastDate = (DateTime)Date.today().addDays(Minus90);
            Integer limitVal = (Integer)HUMConstants__c.getInstance('Size_TwoHundred').IntegerValue__c;
            
            startDate = String.isNotBlank(startDate) ? String.escapeSingleQuotes(startDate) : null;
            endDate = String.isNotBlank(endDate) ? String.escapeSingleQuotes(endDate) : null;
            caseNumberToSearch = String.isNotBlank(caseNumberToSearch) ? String.escapeSingleQuotes(caseNumberToSearch) : null;
            
            if(String.isNotBlank(startDate) && String.isNotBlank(endDate) && TimePttrn.matcher(startDate).matches() && TimePttrn.matcher(endDate).matches())
            {
                lastDate = System.DateTime.newInstance(Integer.valueOf(startDate.subString(0,4)), Integer.valueOf(startDate.subString(5,7)), Integer.valueOf(startDate.subString(8,10)), 00, 00, 00);
                todayDate = System.DateTime.newInstance(Integer.valueOf(endDate.subString(0,4)), Integer.valueOf(endDate.subString(5,7)), Integer.valueOf(endDate.subString(8,10)), 23, 59, 59);
            }
            
            lstFinalCases = LoggingDataLogic_D_HUM.retrievePharmacyOrderCases(accId, lastDate, todayDate, caseNumberToSearch, limitVal);
            if(String.isNotBlank(caseNumberToSearch))
            {
                caseLstSize = lstFinalCases.size();
            }
            else
            {
                caseLstSize = LoggingDataLogic_D_HUM.getCaseCount(accId, lastDate, todayDate);
            }  
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e,'AttachLogDetailsToCase_C_HUM','returnListOfPharmacyCase');
        }
    }
    /*
    * Method Name : createCaseAndRedirect
    * Description : This method is used to create new case
    * Return type : void
    * Parameter   : NA
    */
    public void createCaseAndRedirect()
    {
        try
        {
            String sStandardUrlForTab = ApexPages.CurrentPage().getParameters().get('standardURL');
            if(String.isBlank(accId) || String.isBlank(sObjName))
            {
                sObjName = getSObjName(sObjectId);
                accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            }
            sSelectedCaseId  = LoggingProcessLogic_S_HUM.createNewCase(sObjectId,sStandardUrlForTab);
            sRedirectURL = LoggingProcessLogic_S_HUM.sRedirectURL;
            
            if(String.IsNotBlank(sSelectedCaseId))
            {
                sSelectedCaseNumber =  LoggingDataLogic_D_HUM.getCaseNumber(sSelectedCaseId);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AttachLogDetailsToCase_C_HUM','createCaseAndRedirect');
        }
    }

    /*
    * Method Name : createPharmacyCaseAndRedirect
    * Description : This method is used to create new case from Logging component in Create order page
    * Return type : void
    * Parameter   : NA
    */
    public void createPharmacyCaseAndRedirect()
    {
        try
        {
            String sStandardUrlForTab = ApexPages.CurrentPage().getParameters().get('standardURL');
            if(String.isBlank(accId) || String.isBlank(sObjName))
            {
                sObjName = getSObjName(sObjectId);
                accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            }
            sSelectedCaseId  = LoggingProcessLogic_S_HUM.createNewPharmacyLoggingCase(sObjectId,sStandardUrlForTab);
            sRedirectURL = LoggingProcessLogic_S_HUM.sRedirectURL;
            
            if(String.IsNotBlank(sSelectedCaseId))
            {
                sSelectedCaseNumber =  LoggingDataLogic_D_HUM.getCaseNumber(sSelectedCaseId);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AttachLogDetailsToCase_C_HUM','createPharmacyCaseAndRedirect');
        }
    }
    
    /*
     * Method Name : createAttachmentLogRecords
     * Description : This method is used to create attachment log and attachment records.
     * Return type : void
     * Parameter   : NA
     */
    public void createAttachmentLogRecords()
    {
        try
        {
            String screenshotData = Apexpages.currentpage().getparameters().get('screenshotData');
            
            LoggingWrapper_DTO_HUM objWrapper = new LoggingWrapper_DTO_HUM();
            objWrapper.sSelectedCaseId = Apexpages.currentpage().getparameters().get('caseId');
            objWrapper.sAccountId = accId;
            objWrapper.sAuthId = Apexpages.currentpage().getparameters().get('authId');
            objWrapper.sType = Apexpages.currentpage().getparameters().get('type');
            objWrapper.sSubType = Apexpages.currentpage().getparameters().get('subtypeCipherValue');
            objWrapper.keyValue = Apexpages.currentPage().getParameters().get('attachmentKeyValue');
            objWrapper.keyDescValue = Apexpages.currentPage().getParameters().get('keyDescValue');
            objWrapper.sJsonMap = Apexpages.currentpage().getparameters().get('jsonMap');
            objWrapper.sJsonStandardMap = Apexpages.currentpage().getparameters().get('jsonStandardMap');
            objWrapper.sAttachmentSubType = Apexpages.currentpage().getparameters().get('attachmentSubType');            
            
            if(objWrapper.keyDescValue.contains('&'))
            {
               objWrapper.sType = objWrapper.keyDescValue.split('&')[1];
               objWrapper.sSubType = objWrapper.keyDescValue.split('&')[2];  
               objWrapper.keyDescValue = objWrapper.keyDescValue.split('&')[0];           
            }
            
            if(createNewAttachmentLog == 'true' || idAttLogId == NULL)
            {
                createNewAttachmentLog = 'false';
                idAttLogId = LoggingProcessLogic_S_HUM.createAttachmentLog(objWrapper);
            }
            else
            {
                LoggingProcessLogic_S_HUM.updateAttachmentLog(idAttLogId, objWrapper);
            }
            LoggingProcessLogic_S_HUM.createAttachment(idAttLogId,screenshotData);
            if(UtilityHelper_H_AHT_HUM.isUserHavingMedicareCustomerServiceAccess()){
            	sClassfication = UtilityHelper_H_AHT_HUM.getClassficationId(objWrapper.sType,objWrapper.sSelectedCaseId);
            }
                        
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'AttachLogDetailsToCase_C_HUM','createRecords');
        }
    }
        
    /*
     * Method Name : getCaseId
     * Description : Gets the caseId for the case on which mouse is hover
     * Return type : void
     * Parameter   : NA
     */
    public void getCaseId()
    {
        sCaseId = ApexPages.CurrentPage().getParameters().get('caseId');
    }
    
    /*
     * Method Name : checkUserQueue
     * Description : This method checks if USer is part of any queue or not
     * Return type : Void
     * Parameter   : NA
     */  
    public void checkUserQueue() 
    {
        try
        {
            Logging_C_HUM.updateQueueLogic();
            userCurrentQueue = LoggingDataLogic_D_HUM.getUserQueue(UserInfo.getUserId());
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachLogDetailsToCase_C_HUM','checkUserQueue');
        }
    }
    
    /*
    * Method Name : getKeyValue
    * Description : This method is used to retrieve Key to create Attachment_log record
    * Return type : void
    * Parameter   : NA
    */
    public void getKeyValue()
    {
        try
        {
            sObjName = getSObjName(sObjectId);
            accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            enterpriseId = LoggingProcessLogic_S_HUM.getKeyDesc(accId);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'AttachLogDetailsToCase_C_HUM','getKeyValue');
        }
    }
    
    /*
    * Method Name : createErrorLog
    * Description : This method is used to create ErrorLog
    * Return type : void
    * Parameter   : NA
    */
    public void createErrorLog()
    {
        String sErrorMessage = Apexpages.currentpage().getparameters().get('errorMessage');
        dpaasErrorLogName = LoggingProcessLogic_S_HUM.createErrorLog(sErrorMessage,'AttachLogDetailsToCase_C_HUM' );
    }
    
     /*
     * Method Name : associateInteractiontToCase
     * Description : 
     * Return type : void
     * Parameter   : NA
     */
    public void associateInteractiontToCase(){
		
		try{
			
			if(String.isNotBlank(sInteractionId) && String.isNotBlank(sSelectedCaseId)) {
				
			
				List<Case_interaction__c> lstCaseInt = [SELECT Id FROM Case_interaction__c WHERE interaction__c = :sInteractionId AND Case__c = :sSelectedCaseId Limit 1];
				if(lstCaseInt.isEmpty()){			
					Case_Interaction__c oCaseInt = new Case_Interaction__c(Case__c = sSelectedCaseId, Interaction__c = sInteractionId);				
					insert oCaseInt;
				
				}
			}
		}catch(Exception e){
			HUMExceptionHelper.logErrors(e, 'AttachLogDetailsToCase_C_HUM', 'AssociateInteractiontoCase');
		}
        
    }
}