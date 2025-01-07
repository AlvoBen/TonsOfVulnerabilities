/*************************************************************************************************
Apex Class Name  : DisplayAttachmentLog_C_HUM
Version          : 1.0
Created Date     : 03/21/2016
Function         : This is controller class for visual force page 'DisplayAttachmentLog_VF_HUM'.
                    This is used to display attachment log details on UI.
Test Class       : DisplayAttachmentLog_T_HUM
***************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date               Description
---------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                    03/21/2016         Original Version
* Santhi Mandava            24308                    04/05/2016         Fixed defect 221734.0001
* Santhi Mandava            27512                    05/12/2016         Added sorting feature as per REQ - 263794 
* Santhi Mandava            32175                    07/26/2016         Implemented attachment body decryption logic.
* Santhi Mandava                                     08/24/2016         Worked on REQ#275929 for DPaaS error handling.
* Santhi Mandava                                     10/17/2018         Implemented platform migration changes
* Santhi Mandava                                     03/12/2019         Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Santhi Mandava                                     03/12/2019         Removed dpaas call
* Santhi Mandava            348723                   04/02/2019         Cleanup cipher cloud references
* Mithra Bharadwaj                                                     US#1522825 SonarQube Changes
****************************************************************************************************/

public with sharing class DisplayAttachmentLog_C_HUM
{
    @testVisible private String sAttachId = '';
    /*
    * Method Name : DisplayAttachmentLog_C_HUM
    * Description : Constructor to initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    public DisplayAttachmentLog_C_HUM()
    {
        sAppName = HUMCipherCloudSettings__c.getInstance('APPLICATION_NAME').Value__c;
        sAttachId = ApexPages.currentPage().getParameters().get('attachId');
        sCreatedByQueue = ApexPages.currentPage().getParameters().get('createdbyqueue');
        
        List<Attachment_Log__c> lstAttachmentLogs = LoggingDataLogic_D_HUM.getAttachmentRecord(sAttachId);
        if(lstAttachmentLogs != Null & !lstAttachmentLogs.isEmpty()) sType = lstAttachmentLogs[0].Attachment_Sub_type__c ;
        
        lstAttachments = new List<LoggingWrapper_DTO_HUM>();
        List<Attachment> lstAttachmentList = LoggingProcessLogic_S_HUM.getAttachments(string.escapeSingleQuotes(sAttachId));
        
        For(Attachment obj : lstAttachmentList)
        {
            LoggingWrapper_DTO_HUM objDTO = new LoggingWrapper_DTO_HUM();
            objDTO.sCreatedBy = obj.CreatedBy.Name ;
            objDTO.sAttachCreatedDate = obj.CreatedDate.Format('MM/dd/yyyy HH:mm:ss a');
            objDTO.sCreatedByQueue =  string.escapeSingleQuotes(sCreatedByQueue);
            objDTO.sAttachId= obj.Id;
            lstAttachments.Add(objDTO);
        }
    }
    
    public String sType{get;set;}
    public String sCreatedByQueue{get;set;}   
    public List<LoggingWrapper_DTO_HUM> lstAttachments{get;set;} 
    public static String sAppName{get;set;}
    public static String sJSONString{get;set;}
    public String dpaasErrorLogName {get; set;}
    public Boolean bCipherDataPresent{get;set;}
    
    /*
    * Method Name : getCurrentPageImages
    * Description : This method is used to retrieve attachment details for decryption.
    * Return type : NA
    * Parameter   : NA
    */
    public void getCurrentPageImages()
    {
        HUMConstants__c objMigrationDate = HUMConstants__c.getInstance('ATTACHMENT_MIGRATION_DATE');
        String sCurrentPageData= ApexPages.currentPage().getParameters().get('pageIds');
        bCipherDataPresent = false;
        if(String.IsNotBlank(sCurrentPageData))
        {
            List<String> lstIds = sCurrentPageData.split('#');
            lstIds.remove(0);
            
            List<Attachment>  lstobj  = LoggingDataLogic_D_HUM.getAttachmentDetails(string.escapeSingleQuotes(sAttachId) );
            List<String> lstsEncryptedImages = new List<String>();
            For(integer i = 0; i <= lstIds.size()-1 ; i++)
            {
                For(integer j = 0; j <= lstobj.size()-1 ; j++)
                {  
                    If(lstIds[i] == lstobj[j].Id)
                    {
                        if(lstobj[j].CreatedDate <= Date.parse(objMigrationDate.StringValue__c))
                        {
                            lstsEncryptedImages.Add(EncodingUtil.base64Encode(lstobj[j].Body));
                        }
                        if(lstobj[j].CreatedDate > Date.parse(objMigrationDate.StringValue__c))
                        {
                            lstsEncryptedImages.Add(lstobj[j].Body.toString());
                        }     
                        break;
                    }
                }
            }
            sJSONString = JSON.Serialize(lstsEncryptedImages);
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
        dpaasErrorLogName = LoggingProcessLogic_S_HUM.createErrorLog(string.escapeSingleQuotes(sErrorMessage),'DisplayAttachmentLog_C_HUM' );
    }
}