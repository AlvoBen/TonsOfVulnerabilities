/**********************************************************************************************************************************************************
Apex Class Name :  CaseClosedEdit_C_HUM
Version: 1.0
Created Date     : 01/11/2018
Function         : This class Is the extension for CaseClosed_Edit_VF_HUM Page
***********************************************************************************************************************************************************

Modification Log: 

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------------
* Prasad Jandhyala                                  01/11/2018                   Original Version
* Satyam Malviya                                    12/12/2018                  NBA MF4- Account Check to Display Alert Panel
* Vijaya Koriprolu                                     11/18/2019                 REQ-383958 added functionality for adding claim numbers on case edit page
* Mohammed Noor									    03/26/2020                  US#975731 - Add DEAA OnOff Switch changes
* Mohammed Noor                                     04/24/2020                  US#1020879 - Remove DEAA OnOff Switch
* Gourab Tewary                                     10/2/2021                    User Story - 1733612 - PR00091574- IR 5050 - Add new response status for Cases with Pending Response (CRM)
* Mohammed Noor                              		04/26/2021            		US2163321 - Case Management Checkmarx Security violations - Case History & Case Misc
* Atia Uzma                                         07/29/2021                   User Story 2498842: T1PRJ0001827 - MF 1 - AHT - Ability to Reopen and Edit closed cases (RF)
* Kiran Kumar B										06/07/2022					User Story 2310106: Adding Escalation Indicator to CRM Service
************************************************************************************************************************************************************/
Public with sharing Class CaseClosedEdit_C_HUM {
    Public Case caseRec{Get;Set;}
    Public caseComment caseCom{Get;Set;}
    /*Salesforce ticket #17890966 . Variables for Page redirection */
    // Return URL If pagereference Is Not used to redirect to different page 
    Public transient String returnURL{ Get; Set; }
    //To check if console url Is detail Or Edit URL . This Is set from page on any Pagereference action call for redirection .
    // If this Is true, Pagereference return value Is used for redirection ,else console tool kit methods are used for redirection
    Public transient Boolean isDetailURL{ Get; Set; }
    /* End Salesforce ticket #17890966  variables */
    public Boolean isDisplay {get;set;}
    
    public Boolean bShowEsclIndSwitch{Get;Set;}
    Public Boolean bShowEsclIndicator{Get;Set;}  
    Public Boolean bClaimNumberBtn {get; set;}
    public List<AddClaimNum_CaseEdit_DTO_HUM> lstClaims{get; set;}
    public List<String> lstTotalClaimNumbers{get;set;}
    Public Boolean bClosedMemberRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedMemberRType;
        }
    }
    Public Boolean bClosedAgentRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedAgentRType;
        }
    }
    Public Boolean bClosedHPAgentRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedHPAgentRType;
        }
    }
    Public Boolean bClosedGroupRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedGroupRType;
        }
    }
    Public Boolean bClosedHPGroupRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedHPGroupRType;
        }
    }
    Public Boolean bClosedProviderRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedProviderRType;
        }
    }
    Public Boolean bClosedUnknownRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedUnknownRType;
        }
    }
    Public Boolean bClosedHPProviderRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedHPProviderRType;
        }
    }
    Public Boolean bClosedHPUnknownRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedHPUnknownRType;
        }
    }
    Public Boolean bClosedHPMemberRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedHPMemberRType;
        }
    }
    Public Boolean bClosedMedicareRType{
        get {
            Return caseRec.recordtype.name == CaseDetailConstants_HUM.sClosedMedicareRType;
        }
    }
	
    /*   
    * Constructor
    * @param  NA
    * @return NA 
    */
    Public CaseClosedEdit_C_HUM(ApexPages.standardController std)
    {
        caseRec = (Case)std.getRecord();
        caseRec = [Select id,CTCI_List__r.Classification__r.Name,CTCI_List__r.Classification__c, CTCI_List__r.classification_type__c, CTCI_List__r.classification_type__r.Name,Interacting_About_Type__c,  Interacting_With__r.name, Origin, Escalation_Indicator__c,Classification_Type__c,recordtype.name,Status,Follow_up_Due_Date__c,OpenEnrollment__c,OpenEnrollmentType__c,AccountId,Response_Status__c from case where id=:caseRec.id];
        caseCom = New CaseComment();
        isDisplay = true;
         lstClaims = new  List<AddClaimNum_CaseEdit_DTO_HUM>();
            for(Integer i=0; i<5; i++)
            {
                AddClaimNum_CaseEdit_DTO_HUM o = new AddClaimNum_CaseEdit_DTO_HUM();
                o.sClaimText = GLOBAL_CONSTANT_HUM.CLAIM_NUMBER;
                lstClaims.add(o);
            }
        lstTotalClaimNumbers = new List<String>();
        bClaimNumberBtn = false;
        if(CaseRec.Status != GLOBAL_CONSTANT_HUM.HUM_CANCELLED)
        {
         checkClassification();
        }
       
        bShowEsclIndicator = false;
        bShowEsclIndSwitch = HUMUtilityHelper.isCRMFunctionalityON('2310106');
        if(bShowEsclIndSwitch){
            showEsclIndicator();
        }
    }
    
    /**
    *** @discription: This mothod is used to reopen the closed case
    *** @param: NA
    *** @return PageReference
    **/
   public PageReference caseReopen(){
        
       String buttonNames = ApexPages.CurrentPage().getparameters().get('buttonName') != null?ApexPages.CurrentPage().getparameters().get('buttonName'):'';
       try{
               if( (String.isNotBlank(buttonNames) && buttonNames == 'ReopenAndEdit') && caseRec.Status == CaseDetailConstants_HUM.sClosed && !((caseRec.recordtype.name).startsWithIgnoreCase('Closed HP')) ) {
                      case updateCase = new case(Id=caseRec.Id);
                      updateCase.Status = CaseDetailConstants_HUM.sInProgress;
                      update updateCase;
                      
                      PageReference curtPage = Page.CaseEdit_VF_HUM;      
                      curtPage.getParameters().put('id',updateCase.id);      
                      curtPage.setRedirect(true);
                      return curtPage;        
                  }
       }
       catch(Exception ex)
       {
           if (ex.getMessage().contains(GLOBAL_CONSTANT_HUM.CASEEDIT_VALIDATION_EXP_HUM))
            {
                ApexPages.addMessages(ex);
            }
       }
        return null;
    }
    
    /*
    * This method Is used to save the closed case
    * @param  NA
    * @return PageReference
    */
    Public Pagereference save()
    {
      Pagereference pgref = null;
      SavePoint dataSavePoint = Database.setSavepoint();
      try
        {
            update caseRec;
            //Process the save result for any exceptions
            pgref = new PageReference('/' + caseRec.Id);
            pgref.setRedirect(true);
            returnURL = '/' + caseRec.Id;
            if(lstTotalClaimNumbers !=null && lstTotalClaimNumbers.size() >0 && bClaimNumberBtn)
               {
                    Attachment_Log__c  AttachmentObj = new Attachment_Log__c();
                    if(Schema.sObjectType.Attachment_Log__c.fields.case__c.isCreateable())	AttachmentObj.case__c = caseRec.id;
                    if(Schema.sObjectType.Attachment_Log__c.fields.Source_System__c.isCreateable())	AttachmentObj.Source_System__c = GLOBAL_CONSTANT_HUM.CRM_CON;
                    if(Schema.sObjectType.Attachment_Log__c.fields.Attachment_Type__c.isCreateable())	AttachmentObj.Attachment_Type__c = GLOBAL_CONSTANT_HUM.CLAIMS_MANUAL_ENTRY;
                    if(Schema.sObjectType.Attachment_Log__c.fields.Created_By__c.isCreateable())	AttachmentObj.Created_By__c = UserInfo.getName();
                    Database.SaveResult sResult = Database.insert(AttachmentObj, true); 
                    if(sResult.isSuccess())
                    {
                       List<Attachment_Log_Item__c> lstAttachLogItem = new List<Attachment_Log_Item__c>();
                       for(Integer I=0;I<lstTotalClaimNumbers.size();I++)
                            {
                                if(String.isNotBlank(lstTotalClaimNumbers[I]))
                                {
                                    Attachment_Log_Item__c AttachLogItem = new Attachment_Log_Item__c(name=GLOBAL_CONSTANT_HUM.CLAIM_NUMBER, value__c =lstTotalClaimNumbers[I],Attachment_Log__c = AttachmentObj.id);
                                    lstAttachLogItem.add(AttachLogItem);
                                }
                            }
                            Database.SaveResult[] srlstAttachLogItem = Database.insert(lstAttachLogItem, false);
                            List<Error_Log__c> listErrorLogs = new List<Error_Log__c>();
                            for(Database.SaveResult sr : srlstAttachLogItem) 
                            {
                                if(!sr.isSuccess()) 
                                {
                                      for(Database.Error err : sr.getErrors()) 
                                      {
                                        listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(err.getStatusCode()),
                                        Error_Message__c = err.getMessage(),
                                        class_name__c = 'CaseClosedEdit_C_HUM',
                                        Method_Name__c = 'Save',
                                        Object_Type__c = 'Attachment_Log_Item__c'));
                                        HUMExceptionHelper.saveLog(listErrorLogs, false); 
                                      }
                                }
                            }
       
                    }
                    else
                    { 
                       throw new HUMCustomException('Error in Creating attachment records to store claim numbers: '+sResult.getErrors()[0]);                     
                    }                       
                   
            }
            if(string.isNotBlank(caseCom.CommentBody)) {
                CaseComment caseComObj = new CaseComment();
                if(Schema.sObjectType.CaseComment.fields.parentId.isCreateable())	caseComObj.parentId = caseRec.id;
                if(Schema.sObjectType.CaseComment.fields.CommentBody.isCreateable())	caseComObj.CommentBody = caseCom.CommentBody;
                Database.insert(caseComObj);
            }
        }
        catch(Exception ex)
        {
            Database.rollback(dataSavePoint);
            if (ex.getMessage().contains(GLOBAL_CONSTANT_HUM.CASEEDIT_VALIDATION_EXP_HUM))
            {
                ApexPages.addMessages(ex);
            }
            else
            {
                HUMExceptionHelper.logErrors(ex, 'CaseEdit_CE_HUM', 'save');
            }
            pgref = null;
            returnURL = '';
        }
        if(isDetailURL==false && !Test.isRunningTest())
            return null;
        return pgref;
    }
    
    
    
    /*
     * Method Name   :    getVerifyAccountType
     * Description   :    Send the boolean value to display the Alert Section on vf page 
     * Return Type   :    boolean
     * Parameters    :    NA
     */
     public Boolean getVerifyAccountType()
     {
         try
            {
                 List<Account> accnts = [Select id,Name,RecordType.Name from Account where id = :caserec.AccountId  limit 1 ];
                 if(!(accnts.isEmpty()))
                 {
                    for(Account accnt : accnts)
                        {
                            if(accnt.RecordType.Name==GLOBAL_CONSTANT_HUM.INTERACTINGWITHTYPE_MEMBER ||accnt.RecordType.Name==GLOBAL_CONSTANT_HUM.CASEEDIT_UNKNOWN_MEMBER_HUM ||accnt.RecordType.Name==GLOBAL_CONSTANT_HUM.MEMBERCAREGIVER)
                                {
                                 isDisplay = true;
                                }
                                else
                                {
                                isDisplay = false;
                                }
                         }
                 }
           }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'AccountDetailR1_C_HUM', 'getVerifyAccountType');
        }
         return  isDisplay;
     }
    
    /*
    * This method is to check Classification pre-condition to add claims numbers manually
    * @param  N/A
    * @return N/A
    */ 
    
    public void checkClassification()
    {
      try
      {
         Boolean bProviderSwitch = ([select id,active__c from Provider_functionality_switch__c where active__c=true limit 1]).size()>0?true:false;
         if(bProviderSwitch)
         {
             List<CaseEdit_Add_Claim_Number_Btn__mdt> lstClassificationCombo;
             if(CaseRec.CTCI_List__c != null && CaseRec.CTCI_List__r.classification_type__r.Name != null)
             { 
                 lstClassificationCombo = [SELECT id FROM CaseEdit_Add_Claim_Number_Btn__mdt WHERE Classification_Type__c=:CaseRec.Classification_Type__c AND Classification__c=:CaseRec.CTCI_List__r.Classification__r.Name];
             }
             if(lstClassificationCombo!=null && !lstClassificationCombo.isEmpty())
             {
                 bClaimNumberBtn = true;
             }
             else
              {
                bClaimNumberBtn = false;
              }
          }
       }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'checkClassification');
        }
    }
      /*
    * This method is to add all claim numbers to single list
    * @param  N/A
    * @return N/A
    */ 
    
    public void claimSaveClick()
    {
    try
    {
        for(Integer I=0;I<lstClaims.size();I++)
        {
            if(string.isnotblank(lstClaims[I].sClaimNumber))
            {
            lstTotalClaimNumbers.add(lstClaims[I].sClaimNumber);
            }
            if(string.isnotblank(lstClaims[I].sSecondCol))
            {
            lstTotalClaimNumbers.add(lstClaims[I].sSecondCol);
            }
            if(string.isnotblank(lstClaims[I].sThirdCol))
            {
            lstTotalClaimNumbers.add(lstClaims[I].sThirdCol);
            }
            if(string.isnotblank(lstClaims[I].sFourthCol))
            {
            lstTotalClaimNumbers.add(lstClaims[I].sFourthCol);
            }
            if(string.isnotblank(lstClaims[I].sFifthCol))
            {
             lstTotalClaimNumbers.add(lstClaims[I].sFifthCol);
            }
        }
        if(lstTotalClaimNumbers != null && lstTotalClaimNumbers.size()>0)
          ClaimValues(lstTotalClaimNumbers);
    }
    catch(Exception e)
    {
    HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'claimSaveClick');
    }
    }
     /*
    * This method is to return the claim values
    * @param  N/A
    * @return N/A
    */ 
    
     public List<String> ClaimValues(List<String> lstClaimsVal)
     { 
        lstTotalClaimNumbers = lstClaimsVal;
        return lstTotalClaimNumbers;
     }
    /*
    * This method is to remove the claim values upon cancel
    * @param  N/A
    * @return N/A
    */  
    public void claimCancelClick()
    {
        try
        {       
            if(lstTotalClaimNumbers!= null && lstTotalClaimNumbers.size()>0)
            {
                lstTotalClaimNumbers.clear();
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseEdit_CE_HUM', 'claimSaveClick');
        } 
    }
    
     /*
    * This method Is to Check to display Escalation indicator 
    * @param  NA
    * @return void
    */
    public void showEsclIndicator()
    {
        List<String> lstInteractingAboutType = new List<String>{'Agent', 'Group', 'Member', 'Provider', 'Unknown-Agent', 'Unknown-Group', 'Unknown-Member', 'Unknown-Provider'};
        List<String> lstInteractionOrigin = new List<String>{'Service Inquiry', 'NINA Web Chat', 'Internal Process', 'Watson Voice', 'Watson Web Chat', 'Vantage', 'Cancelled'};
        if( lstInteractingAboutType.contains(caseRec.Interacting_About_Type__c) && !lstInteractionOrigin.contains(caseRec.Origin)){
            bShowEsclIndicator = true;
            if(caseRec.Origin == 'IVR' && caseRec.Classification_Type__c != 'Calls (RSO)'){
                bShowEsclIndicator = false;
            }  
        }
    } 
}