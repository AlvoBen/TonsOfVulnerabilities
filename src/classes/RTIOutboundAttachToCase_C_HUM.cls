/**********************************************************************************************************
Apex Class Name  : RTIOutboundAttachmentToCase_C_HUM
Version          : 1.0
Created Date     : 03/24/2020
Function         : This is controller class for visual force component'RTIOutBoundAttachmentToCase_CMP_HUM '.
                   This is used to attach OutBound Communication Details to either new case or to an existing case.
Test Class       : RTIOutboundAttachmentToCase_T_HUM
***********************************************************************************************************
Modification Log:  
* Developer Name    Review Number    Date             Description
-------------------------------------------------------------------------------------------------------------
* Anjani Vaddadi                     03/24/2020       Original Version
* Anjani Vaddadi                     06/08/2020       Defect Fix-849
* Anjani Vaddadi                     07/03/2020       US-1125628- Pass Message Definition Code (MDC)On Outbound Communications attached to CRM Cases via RTI
* Trina Ghosh                        02/09/2021       US:1669848 - Low Security Issues Fix
* Rahul Chaurasia                    03/22/2021       1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service
* Suraj Patil                        04/09/2021       REQ - 1905877 - PR00094226 - CR AEP readiness - Low Security Issues 
* Prasanthi Kandula                  05/11/2021       US - 2102866 - Inserting Attachment log records for 3 new channels - Email, Web, Text(SMS)
* Rahul Chaurasia					 06/14/2021		  US - 2321114 - T1PRJ0002735 - SF - RTI - CR AEP readiness - Medium Security Issues
* Anuradha Gajbhe                    08/19/2021       US - 2496294 Pass Message Definition Code (MDC) on Outbound Communications attached to CRM Cases via RTI
*************************************************************************************************************/
public with sharing class RTIOutboundAttachToCase_C_HUM extends LogDetails_A_HUM
{
    @testVisible private Id accId = NULL;
    @testVisible private String sObjName ='';
    @testVisible private List<Case> lstFinalCases = null;
    @testVisible private String sSortExp = 'CreatedDate';
    @testVisible private String sSortDirection = 'DESC';
    @testVisible private Case oCaseRec = new Case();
    @testVisible private Id idAttLogId = NULL;   
    @testVisible private List<Attachment_Log__c> lstAttachmentLog = new List<Attachment_Log__c>();    
    
    //MF4
    Public string sCampaignName{Get;Set;}
    Public string sPhoneNumber{Get;Set;}
    Public string sStatusDescription{Get;Set;}
    Public string ssubStatusDesc{Get;Set;}
    Public string sDate{Get;Set;}
    Public string sCategoryDescription{Get;Set;}
    Public string strackingId{Get;Set;}
    Public string sViewurl{Get;Set;}    
    Public string smessageprintParam{Get;Set;}
    Public string sType{Get;Set;}
    Public string sSubArea{Get;Set;}
    Public string sScourceCode{Get;Set;}
    Public string sMsgDefCode{Get;Set;} //REQ-1125628
    public string sChannelCode{get;set;}
    public string sRecipientEmail {get;set;}
    
    /*
    * Method Name : RTIOutboundAttachmentToCase_C_HUM 
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public RTIOutboundAttachToCase_C_HUM()
    {        
        
        bIsExistingCase = false;
        reRenderQueueSelectionPage = true;
        bSucessSave = False;
        lstFinalCases = New List<Case>(); 
    }
    
    public string selectedCaseOption{get;set;} 
    public String sObjectId{get;set;}    
    public String userCurrentQueue {get; set;}    
    public String sCaseNumber{get;set;}
    public String sSelectedCaseNumber{get;set;}
    public String sSelectedCaseId{get;set;}
    public String sRedirectURL {get; set;}
    public String sCaseId {get; set;}          
    public Boolean bIsExistingCase{get;set;}    
    public Boolean bSucessSave {get; set;}
    public Boolean reRenderQueueSelectionPage {get; set;}
    public ApexPages.StandardSetController stdsetcon {get;set;}     
    public Boolean hasNext 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getHasNext();}
            else {return null;}
        }
        set;
    }    
    public Boolean hasPrevious 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getHasPrevious();}
            else {return null;}
        }
        set;
    }       
    public Integer pageNumber 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getPageNumber();}
            else{ return null;}
        }
        set;
    }    
    public List<Case> lstCases
    {
        get
        {
            if (stdsetcon != null)
            {
                return (List < Case > ) stdsetcon.getRecords();
            }
            else
            {
                displayExistingCaseList();
                return (List < Case > ) stdsetcon.getRecords();
            }
        }
        set;
    }    
    public integer iConstantValue
    {
        get
        {
          iConstantValue = 100;
          return iConstantValue;
        }
        set;
    }    
    public String sSortExpression 
    {
        get 
        {
            return sSortExp;
        }
        set 
        {
            if (value == sSortExp)
            {
                sSortDirection = (sSortDirection == 'DESC') ? 'ASC' : 'DESC';
            }
            else{sSortDirection = 'DESC';}
            sSortExp = value;
        }
    }
    
    /*
    * Method Name : displayExistingCaseList
    * Description : This method is used to retrieve cases associated to an account.
    * Return type : Void
    * Parameter   : NA
    */
    public void displayExistingCaseList()
    {  
        bIsExistingCase= false;
        sSelectedCaseId ='';
        sSelectedCaseNumber = '';
        bIsExistingCase = true;      
        sObjName = getSObjName(sObjectId);
        accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );        
        lstFinalCases = LoggingDataLogic_D_HUM.retrieveAccountCases(accId, DateTime.Now().adddays(-90), DateTime.Now(), null, null);
        stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
        if(stdsetcon != NULL) stdsetcon.setPageSize(5);        
    } 
    
    /*
    * Method Name : search
    * Description : This method is used filter cases by CaseNumber. This is an exact match search
    * Return type : Void
    * Parameter   : NA
    */
    public void search()
    {
        if(String.isBlank(sCaseNumber))
        {
            stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
        }
        else
        {
            stdsetcon = new ApexPages.StandardSetController(LoggingProcessLogic_S_HUM.SearchCases(sCaseNumber, lstFinalCases));
        }
        if(stdsetcon != NULL)
        {
            stdsetcon.setPageSize(5);
        }
    }    
    public void getSelectedCase()
    {
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
            if(String.isBlank(accId) || String.isBlank(sObjName))
            {
                sObjName = getSObjName(sObjectId);
                accId = LoggingHelper_H_HUM.getAccountId(sObjectId,sObjName );
            }
            
            sSelectedCaseId  = LoggingProcessLogic_S_HUM.createNewCase(sObjectId, ApexPages.CurrentPage().getParameters().get('standardURL').escapeHtml4());
            sRedirectURL = LoggingProcessLogic_S_HUM.sRedirectURL;
            if(String.IsNotBlank(sSelectedCaseId))
            {
                sSelectedCaseNumber =  LoggingDataLogic_D_HUM.getCaseNumber(sSelectedCaseId);                
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'RTIOutboundAttachmentToCase_C_HUM','createCaseAndRedirect');
        }
    }
    
    /*
     * Method Name : getSortDirection
     * Description : This method is used to get the sortdirection.
     * Return type : String
     * Parameter   : NA
    */    
    public String getSortDirection()
    {
        if (String.isBlank(sSortDirection))
        {
            return 'DESC';
        }
        else
        {
            return sSortDirection;
        }
    }
    
    /*
     * Method Name : setSortDirection
     * Description : This method is used to set the Sort direction.
     * Return type : Void
     * Parameter   : String
    */    
    public void setSortDirection(String value)
    {
        sSortDirection = value;
    }
    
    /*
     * Method Name : sortResultPage
     * Description : This method is used to sort the case records.
     * Return type : Void
     * Parameter   : NA
    */    
    public void sortResultPage()
    {
        try
        {            
            lstFinalCases = LoggingDataLogic_D_HUM.retrieveAccountCases(accId, DateTime.Now().adddays(-90), DateTime.Now(), null, null);
            stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
            if(stdsetcon != Null)    stdsetcon.setPageSize(5);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'RTIOutboundAttachmentToCase_C_HUM','sortResultPage');
        }
    }
    
    /*
     * Method Name : previous
     * Description : This method returns the previous page of records.
     * Return type : Void
     * Parameter   : NA
    */ 
    public void previous()
    {
        stdsetcon.previous();
    }

    /*
     * Method Name : first
     * Description : This method returns the first page of records.
     * Return type : Void
     * Parameter   : NA
    */     
    public void first()
    {
        stdsetcon.first();
    }

    /*
     * Method Name : last
     * Description : This method returns the last page of records.
     * Return type : Void
     * Parameter   : NA
    */ 
    public void last() 
    {
        stdsetcon.last();
    }

    /*
     * Method Name : next
     * Description : This method returns the next page of records.
     * Return type : Void
     * Parameter   : NA
    */      
    public void next() 
    {
        stdsetcon.next();
    } 
    
    /*
     * Method Name : checkUserQueue
     * Description : This method checks if User is part of any queue or not
     * Return type : Void
     * Parameter   : NA
    */  
 public void checkUserQueue() 
  {    
    Logging_C_HUM.updateQueueLogic();
    userCurrentQueue = LoggingDataLogic_D_HUM.getUserQueue(UserInfo.getUserId());    
   }
    
   
      
    /*
    * Method Name : updateQueueLogic
    * Description : This method is used to update user's queue
    * Return type : void
    * Parameter   : NA
    */
    public static void updateQueueLogic()
    {    
        HUMQueueSelectionController queueObj = new HUMQueueSelectionController();
        queueObj.init();        
    }     
    
      
    /*
     * Method Name : updateWhoID
     * Description : This method will launch the auto save flow.
     * Return type : void
     * Parameter   : NA
     */
     public void updateWhoID() 
     {                        
          sCaseId = ApexPages.CurrentPage().getParameters().get('caseId').escapeHtml4();          
          List<case> lstcasenumber = [select id,CaseNumber from case where id =:sCaseId LIMIT 1];
          sCaseNumber = lstcasenumber[0].CaseNumber ;
          sCaseId=  lstcasenumber[0].Id;          
          bSucessSave=true;
          if(bSucessSave==true){
            attachRTItoCaseAttachment();
          }          
      }
    
         
      /*
     * Method Name : attachRTItoCaseAttachment
     * Description : This method will create records on attachment log object which updates process section on case
     * Return type : void
     * Parameter   : NA
     */
     public void attachRTItoCaseAttachment()
     {
        Boolean bMdcSwitch = CRMFunctionalityenabler_H_HUM.Checkflagvalue('RTI_MDC_Switch');
        List<Attachment_Log_Item__c> lstAttachLogItem = new List<Attachment_Log_Item__c>();
        Attachment_Log__c  AttachmentObj = new Attachment_Log__c();
		if(Schema.sObjectType.Attachment_Log__c.fields.case__c.isCreateable()){
            AttachmentObj.case__c = sCaseId;
        }
        if(Schema.sObjectType.Attachment_Log__c.fields.Source_System__c.isCreateable()){
            AttachmentObj.Source_System__c = sScourceCode; 
        }
        if(Schema.sObjectType.Attachment_Log__c.fields.Attachment_Type__c.isCreateable()){
            AttachmentObj.Attachment_Type__c = sChannelCode;
        }
        if(Schema.sObjectType.Attachment_Log__c.fields.Created_By__c.isCreateable()){
            AttachmentObj.Created_By__c =  UserInfo.getName();
        }
        Database.SaveResult sResult = Database.insert(AttachmentObj, true); 
        if(sResult.isSuccess())
        {   
            if(AttachmentObj.Attachment_Type__c.equalsIgnoreCase('VAT'))
            {   
                Attachment_Log_Item__c AttachLogItem_Campaign_name = new Attachment_Log_Item__c(name='Campaign Name', value__c = (sCampaignName==NULL)?'':sCampaignName,Attachment_Log__c = AttachmentObj.id, Custom_Order_Number__c=1);
                lstAttachLogItem.add(AttachLogItem_Campaign_name);
                Attachment_Log_Item__c AttachLogItem_phoneNumber = new Attachment_Log_Item__c(name='Telephone Number', value__c = (sPhoneNumber==NULL)?'':sPhoneNumber,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=2);
                lstAttachLogItem.add(AttachLogItem_phoneNumber);
                Attachment_Log_Item__c AttachLogItem_status = new Attachment_Log_Item__c(name='Status', value__c = (sStatusDescription==NULL)?'':sStatusDescription,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=3);
                lstAttachLogItem.add(AttachLogItem_status);
                Attachment_Log_Item__c AttachLogItem_statusResult = new Attachment_Log_Item__c(name='Status Result', value__c = (ssubStatusDesc==NULL)?'':ssubStatusDesc ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=4);
                lstAttachLogItem.add(AttachLogItem_statusResult);
                Attachment_Log_Item__c AttachLogItem_date = new Attachment_Log_Item__c(name='Date', value__c = (sDate==NULL)?'':sDate,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=5);
                lstAttachLogItem.add(AttachLogItem_date);
                Attachment_Log_Item__c AttachLogItem_TrackingID = new Attachment_Log_Item__c(name='Tracking ID', value__c = (strackingId==NULL)?'':strackingId ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=6);
                lstAttachLogItem.add(AttachLogItem_TrackingID);                        
                Attachment_Log_Item__c AttachLogItem_campaignSummary = new Attachment_Log_Item__c(name='Campaign Summary', value__c =(sviewurl==NULL)?'':sviewurl,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=7);
                lstAttachLogItem.add(AttachLogItem_campaignSummary);                                                                       
                Attachment_Log_Item__c AttachLogItem_MsgDefCode = new Attachment_Log_Item__c(name='MsgDefCode', value__c = (sMsgDefCode==NULL)?' ':sMsgDefCode ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=8);
                lstAttachLogItem.add(AttachLogItem_MsgDefCode);
            }
            if(AttachmentObj.Attachment_Type__c.equalsIgnoreCase('PRINT') || AttachmentObj.Attachment_Type__c.equalsIgnoreCase('WEB'))
            {   
                Attachment_Log_Item__c AttachLogItem_MessageName = new Attachment_Log_Item__c(name='Message Name', value__c = (smessageprintParam==NULL)?'':smessageprintParam ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=1);
                lstAttachLogItem.add(AttachLogItem_MessageName);
                Attachment_Log_Item__c AttachLogItem_Type = new Attachment_Log_Item__c(name='Type', value__c = (sCategoryDescription==NULL)?'':sCategoryDescription ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=2);
                lstAttachLogItem.add(AttachLogItem_type);
                Attachment_Log_Item__c AttachLogItem_Subarea = new Attachment_Log_Item__c(name='Sub Area', value__c = (sSubArea==NULL)?'':sSubArea,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=3);
                lstAttachLogItem.add(AttachLogItem_Subarea);
                Attachment_Log_Item__c AttachLogItem_Date = new Attachment_Log_Item__c(name='Date', value__c = (sDate==NULL)?'':sDate ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=4);
                lstAttachLogItem.add(AttachLogItem_Date);
                Attachment_Log_Item__c AttachLogItem_TrackingID = new Attachment_Log_Item__c(name='Tracking ID', value__c = (sTrackingiD==NULL)?'':sTrackingiD ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=5);
                lstAttachLogItem.add(AttachLogItem_TrackingID);                        
                Attachment_Log_Item__c AttachLogItem_communicationlink = new Attachment_Log_Item__c(name='Communication',value__c =(sviewurl==NULL)?'':sviewurl,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=6);
                lstAttachLogItem.add(AttachLogItem_communicationLink); 
                if(bMdcSwitch){ 
                    Attachment_Log_Item__c AttachLogItem_MsgDefCode = new Attachment_Log_Item__c(name='MsgDefCode', value__c = (sMsgDefCode==NULL)?' ':sMsgDefCode ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=7);
                    lstAttachLogItem.add(AttachLogItem_MsgDefCode); 
                }
                else if(AttachmentObj.Attachment_Type__c.equalsIgnoreCase('PRINT')){                                                                 
                         Attachment_Log_Item__c AttachLogItem_MsgDefCode = new Attachment_Log_Item__c(name='MsgDefCode', value__c = (sMsgDefCode==NULL)?' ':sMsgDefCode ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=7);
                         lstAttachLogItem.add(AttachLogItem_MsgDefCode);                          
                }
            } 
            if(AttachmentObj.Attachment_Type__c.equalsIgnoreCase('Email') || AttachmentObj.Attachment_Type__c.equalsIgnoreCase('Text'))
            {   
                Attachment_Log_Item__c AttachLogItem_MessageName = new Attachment_Log_Item__c(name='Message Name', value__c = (smessageprintParam==NULL)?'':smessageprintParam ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=1);
                lstAttachLogItem.add(AttachLogItem_MessageName);
                if(AttachmentObj.Attachment_Type__c.equalsIgnoreCase('Email')) {
                    Attachment_Log_Item__c AttachLogItem_Type = new Attachment_Log_Item__c(name='Recipient Email', value__c = (sRecipientEmail==NULL)?'':sRecipientEmail ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=2);
                    lstAttachLogItem.add(AttachLogItem_type); 
                }
                if(AttachmentObj.Attachment_Type__c.equalsIgnoreCase('Text')) {
                    Attachment_Log_Item__c AttachLogItem_Type = new Attachment_Log_Item__c(name='Recipient Telephone Number', value__c = (sPhoneNumber==NULL)?'':sPhoneNumber ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=2);
                    lstAttachLogItem.add(AttachLogItem_type);
                }
                Attachment_Log_Item__c AttachLogItem_Subarea = new Attachment_Log_Item__c(name='Status', value__c = (sStatusDescription==NULL)?'':sStatusDescription,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=3);
                lstAttachLogItem.add(AttachLogItem_Subarea);
                Attachment_Log_Item__c AttachLogItem_Date = new Attachment_Log_Item__c(name='Date', value__c = (sDate==NULL)?'':sDate ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=4);
                lstAttachLogItem.add(AttachLogItem_Date);
                Attachment_Log_Item__c AttachLogItem_TrackingID = new Attachment_Log_Item__c(name='Tracking ID', value__c = (sTrackingiD==NULL)?'':sTrackingiD ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=5);
                lstAttachLogItem.add(AttachLogItem_TrackingID);                        
                Attachment_Log_Item__c AttachLogItem_communicationlink = new Attachment_Log_Item__c(name='Communication',value__c =(sviewurl==NULL)?'':sviewurl,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=6);
                lstAttachLogItem.add(AttachLogItem_communicationLink);   
                if(bMdcSwitch){
                   Attachment_Log_Item__c AttachLogItem_MsgDefCode = new Attachment_Log_Item__c(name='MsgDefCode', value__c = (sMsgDefCode==NULL)?' ':sMsgDefCode ,Attachment_Log__c = AttachmentObj.id,Custom_Order_Number__c=7);
                   lstAttachLogItem.add(AttachLogItem_MsgDefCode);
                }				
                } 
            }
            Database.SaveResult[] srlstAttachLogItem = Database.insert(lstAttachLogItem, false);
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
}