/*******************************************************************************************************************************************************
Apex Class Name  : Logging_C_HUM
Version          : 1.0
Created Date     : 02/24/2016
Function         : This is controller class for visual force component 'Logging_CMP_HUM'.
                    This is used to display logging details.
Test Class       : Logging_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                    02/24/2016                 Original Version
* Santhi Mandava            25231                    04/19/2016                 Fixed defect 223172.0001
* Santhi Mandava            26846                    05/05/2016                 Replaced type with subtype in retrieveCaseReocords method.
* Santhi Mandava            27764                    05/18/2016                 Implemented requirement REQ - 263122
* Manish Kumar              33921                    07/18/2016                 Fixed defect#D-00463. Enabled encryption for logged records
* Santhi Mandava            35034                    04/19/2016                 Modified test class as per defect 230816.0001 fix.
* Santhi Mandava            40488                    09/19/2016                 Created properties to store attachment key and description.
* Manish Kumar                                       01/27/2017                 Worked on Defect#243750, REQ#296700. Incorporated changes for Datatable
                                                                                in "Existing case" scenario.
* Pradeep Dani              237740                   06/01/2018                 Addressed label error issue.
* Pradeep Dani              268872                   08/09/2017                 Logging Regression Defect 368854.0001
* Prudhvi Pamarthi                                   02/06/2019                 REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Ravi Prakash kantamneni   345989                   03/21/2019                 REQ-383718 - Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)* Santhi Mandava                                     03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Santhi Mandava                                     04/04/2019                 Removed "ccURL" variable
* Santhi Mandava            348723                   04/02/2019                 Cleanup cipher cloud references
* Prathyu Tushar			355095					 05/02/2019				    REQ - 397913: CA Incident # 8244067, Removed select order attribute.
* Santhi Mandava                                     02/18/2020                 Added "bEnablePause" variable for pause logging functionality
* Amar Gadewar                                       03/09/2020                 User Story 860241 PR00091574 - MF 1 - AHT - Add Pause Logging button on Claim Details/Claim Summary/Auth/Referral Summary Page (CRM)
* Amar Gadewar                                       04/03/2020                 US 995046 PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup - 4/10 and 4/24 Release (CRM)
* Lakshmi Madduri                                    05/07/2021                 2235919 - Separate constant class for logging framework
********************************************************************************************************************************/
public with sharing class Logging_C_HUM extends LogDetails_A_HUM
{
    /*
    * Method Name : Logging_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public Logging_C_HUM()
    {
        numberOfQueue = '0';
        displayMsg = true;
        displayCaseMsg = false;
        reRenderQueueSelectionPage = false;
        lstCases = new List<Case>();
        appName = HUMCipherCloudSettings__c.getInstance('APPLICATION_NAME').Value__c;

    }
    
    public Boolean bdisableButton {get; set;}
    public Boolean displayMsg{get; set;}
    public String log_color{get; set;}
    public String logged_color{get; set;}
    public String log_area{get; set;}
    public Id record_Id{get; set;}
    public List<Case> lstCases{get;set;}
    public Boolean displayCaseMsg{get;set;}
    public String numberOfQueue {get; set;}
    public boolean reRenderQueueSelectionPage {get; set;}
    public static Map<String, String> mapStandardValues = new Map<String, String>{};
    public String sInfoMessage{get;set;}
    public Static String appName {get; set;}
    public Static String sAttachmentDesc {get; set;}
    public Static String sAttachmentKey {get; set;}
    public Boolean bEnablePause{get;set;}
  
    
    /*
    * Method Name : updateQueueLogic
    * Description : This method is used toupdate user's queue
    * Return type : void
    * Parameter   : NA
    */
    public static void updateQueueLogic()
    {
        HUMQueueSelectionController queueObj = new HUMQueueSelectionController();
        queueObj.sPageTitle = LOGGING_CONSTANT_HUM.STRING_SELECT_WRKQUEUE_BEFORE_CREATING_OR_EDITING_CASE;
        queueObj.init();
    }
    
    /*
    * Method Name : retrieveCaseReocords
    * Description : This method is used to retrieve cases attaced a particular account. 
    * Return type : void
    * Parameter   : NA
    */
    public void retrieveCaseReocords()
    {
        if (displayMsg)
        {
            String sSubtype = Apexpages.currentpage().getparameters().get('sPageSubType');
            String sUniqueVal = Apexpages.currentpage().getparameters().get('sPageUniqVal');
            lstCases = LoggingDataLogic_D_HUM.retrieveCases(LoggingHelper_H_HUM.getAccountId(record_Id,getSObjName(record_Id)),sSubtype ,sUniqueVal ,'LastModifiedDate',LOGGING_CONSTANT_HUM.STRING_DESC);
            displayCaseMsg = (lstCases != null && !lstCases.isEmpty()) ? true : false;

            //sInfoMessage is used to store URL type value. This will be used by component to access the custom label <type>_LoggingInformation_HUM. 
            sInfoMessage = ApexPages.CurrentPage().getParameters().get('type');
            sInfoMessage = sInfoMessage != null ? sInfoMessage.replaceAll('\\s+', '').trim() + '_LoggingInformation_HUM' : 'Logging_Summary_HUM';
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
        //Placeholder
    }
}