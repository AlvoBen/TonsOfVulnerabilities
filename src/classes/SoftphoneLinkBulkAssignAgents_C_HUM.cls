/*******************************************************************************************************************************
Apex Class Name     : SoftphoneLinkBulkAssignAgents_C_HUM 
Version             : 1.0
Created Date        : August 10 2019
Function            : This servers as controller class for bulk assign softphone link vf page.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Rakesh Patni                                          08/10/2019                 
*******************************************************************************************************************************/
public with sharing class SoftphoneLinkBulkAssignAgents_C_HUM 
{   
    public string strCurrentSoftphonLinkId ='';
    public String sConRequest = '';
    public String selectedOption = null;
    public SoftphoneLinks__c currentObj = null;    
    public Boolean allRequest = false;
    public SoftPhoneLinkDB2Request_DTO_HUM softphoneAddAgent = null;
    public SoftPhoneLinkDB2UpdateRequest_DTO_HUM sfUpdateRequest = null;
    public List<Softphone_Agent__c> lstSfAgent = null;
    private SoftphoneLinkBulkAssignAgents_S_HUM oServiceTransfer = new SoftphoneLinkBulkAssignAgents_S_HUM();
    
    /**
    * SoftphoneLinkBulkAssignAgents_C_HUM
    * <p>
    * Constructor method
    *
    * @param  StandardController
    * @return Void
    **/ 
    public SoftphoneLinkBulkAssignAgents_C_HUM(ApexPages.StandardController stdController) 
    {
        strCurrentSoftphonLinkId = stdController.getId();
        currentObj = (SoftphoneLinks__c)stdController.getRecord();
        isDefaultToDefault = false;
        isDefaultToAlt = false;
        isAltToAlt = false;
        isServiceCallDone = false;
        isServiceCallFail = false;
        ErrorMsg='';
        batchSize = (Integer)HUMConstants__c.getInstance('SOFTPHONE_SYNC_BATCH_SIZE').IntegerValue__c;
        count=0;
        totalSize=0;
        intCountOfAgentsToTransfer=0;
        isErrorScenario = false;
    }

    public boolean isDefaultToDefault {get;set;}
    public boolean isDefaultToAlt {get;set;}
    public boolean isAltToAlt {get;set;}
    public Integer intCurrentAgentCount {get;set;}
    public Integer validTransferAgentCount {get;set;}
    public Integer intCurrentServerAgentCount {get;set;}
    public Integer intCountOfAgentsToTransfer {get;set;}
    public Integer intNewAgentCount {get;set;}
    public Integer intNewServerCount {get;set;}
    public String strNewServerName {get;set;}
    public String strNewSiteName {get;set;}
    public Boolean isServiceCallDone{get;set;}
    public Boolean isServiceCallFail{get;set;}
    public String ErrorMsg{get;set;}
    public Integer totalSize { get; set; }
    public Integer batchSize { get; set; } 
    public Integer count { get; set; } 
    public string selectedSiteLink{get;set;}
    public Boolean isErrorScenario{ get; set; } 
    
    /**
    * getSelectedOption
    * <p>
    * returns the selected option
    *
    * @param  NA
    * @return Void
    **/ 
    
    public String getSelectedOption() 
    {
        return selectedOption;
    }
    
    /**
    * setSelectedOption
    * <p>
    * sets the selected option
    *
    * @param  NA
    * @return Void
    **/
    
    public void setSelectedOption(String selectedOption) 
    {
        this.selectedOption = selectedOption;
    }
    
    /**
    * getTransferOptions
    * <p>
    * sets the selected option
    *
    * @param  NA
    * @return Void
    **/
    
    public List<SelectOption> getTransferOptions() 
    {
        List<SelectOption> options = new List<SelectOption>();
        options.add(new SelectOption(HUMConstants.HUMSoftphoneLinksTransferD2D,HUMConstants.HUMSoftphoneLinksTransferD2D));
        options.add(new SelectOption(HUMConstants.HUMSoftphoneLinksTransferA2A,HUMConstants.HUMSoftphoneLinksTransferA2A));
        options.add(new SelectOption(HUMConstants.HUMSoftphoneLinksTransferD2A,HUMConstants.HUMSoftphoneLinksTransferD2A));
        return options;
    } 
    
    /**
    * agentsForNewSoftphone
    * <p>
    * sets the selected option
    *
    * @param  NA
    * @return Void
    **/
    
    public void agentsForNewSoftphone() 
    {
        if(string.isNotBlank(selectedSiteLink))
        {
            SoftphoneLinks__c objSFLTemp = [SELECT Id, Sites__c, Sites__r.Name, ServerName__c, Default_Server_Agents__c, Agent__c  FROM SoftphoneLinks__c WHERE Id=:selectedSiteLink];
            
            if(isDefaultToDefault) 
            {
                intNewAgentCount = integer.valueOf(objSFLTemp.Agent__c);
                intNewServerCount = integer.valueOf(objSFLTemp.Default_Server_Agents__c);
                strNewServerName = objSFLTemp.ServerName__c;
                strNewSiteName = objSFLTemp.Sites__r.Name;
            } 
            else  
            {
                intNewAgentCount = [SELECT Count() FROM Softphone_Agent__c WHERE Alternate_SoftphoneLinks__c=:selectedSiteLink];
                intNewServerCount = [SELECT Count() FROM Softphone_Agent__c 
                                     WHERE Alternate_SoftphoneLinks__r.ServerName__c=:objSFLTemp.ServerName__c AND 
                                     Alternate_SoftphoneLinks__r.Sites__c=:objSFLTemp.Sites__c];
                strNewServerName = objSFLTemp.ServerName__c;
                strNewSiteName = objSFLTemp.Sites__r.Name;
                if(isDefaultToAlt) 
                {
                    validTransferAgentCount = [SELECT Count() FROM Softphone_Agent__c WHERE Default_SoftphoneLinks__c=:strCurrentSoftphonLinkId AND Alternate_SoftphoneLinks__c !=:selectedSiteLink];
                }
            }
        }       
    }
    
    /**
    * fetchNextSection
    * <p>
    * sets the selected option
    *
    * @param  NA
    * @return Void
    **/
    public void fetchNextSection() 
    {
        isDefaultToDefault = isAltToAlt = isDefaultToAlt = false;
        intNewAgentCount = intNewServerCount = intCountOfAgentsToTransfer = 0;
        strNewServerName = '';
        strNewSiteName = '';
        selectedSiteLink = '';
        if(selectedOption == HUMConstants.HUMSoftphoneLinksTransferD2D) 
        {
            isDefaultToDefault = true;
        }
        else if (selectedOption == HUMConstants.HUMSoftphoneLinksTransferA2A) 
        {
            isAltToAlt = true;
            intCurrentAgentCount = [SELECT Count() FROM Softphone_Agent__c WHERE Alternate_SoftphoneLinks__c=:strCurrentSoftphonLinkId];
            intCurrentServerAgentCount = [SELECT Count() FROM Softphone_Agent__c WHERE Alternate_SoftphoneLinks__r.ServerName__c=:currentObj.ServerName__c AND Sites__c=:currentObj.Sites__c];
        } 
        else if (selectedOption == HUMConstants.HUMSoftphoneLinksTransferD2A) 
        {
            isDefaultToAlt = true;
        }
        validTransferAgentCount = [SELECT Count() FROM Softphone_Agent__c WHERE Default_SoftphoneLinks__c=:strCurrentSoftphonLinkId];
        
    }
    
    /**
    * getlstAlternateSFLinks
    * <p>
    * sets the selected option
    *
    * @param  NA
    * @return Void
    **/
    
    public List<SelectOption> getlstAlternateSFLinks()
    {
        List<SelectOption> lstAltSFLinks = new List<SelectOption>();
        List<SoftphoneLinks__c> lSFLinks = new List<SoftphoneLinks__c>();
        lSFLinks = [Select id,name from SoftphoneLinks__c where Sites__c=:currentObj.Sites__c];
        for(SoftphoneLinks__c sfLink: lSFLinks)
        {
            if(sfLink.name!=currentObj.name)
            {
                lstAltSFLinks.add(new SelectOption(sfLink.id,sfLink.name));
            }
        }
        return lstAltSFLinks;
    }
    
    /**
    * validateErrorScenario
    * <p>
    * validates UI errors
    *
    * @param  NA
    * @return Void
    **/
    public Boolean validateErrorScenario()
    {
        Integer validCount =0;
        if(isAltToAlt)
            validCount = intCurrentAgentCount;
        else if (isDefaultToAlt || isDefaultToDefault)
            validCount = validTransferAgentCount;

        if(selectedSiteLink == null)
        {
            if(isDefaultToDefault)
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR, GLOBAL_CONSTANT_HUM.SOFTPHONE_BULKTRANSFER_ERRORMESSAGE_1));
            else
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR, GLOBAL_CONSTANT_HUM.SOFTPHONE_BULKTRANSFER_ERRORMESSAGE_2));
            return true;
        }
        else if(intCountOfAgentsToTransfer == null || intCountOfAgentsToTransfer <= 0 || intCountOfAgentsToTransfer > validCount)
        {
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR, GLOBAL_CONSTANT_HUM.SOFTPHONE_BULKTRANSFER_ERRORMESSAGE_4));
                return true;    
        }
        else
        {
            return false;
        }
    }
    
    /**
    * formRequest
    * <p>
    * forms the request
    *
    * @param  NA
    * @return Void
    **/
    
    public void formRequest()
    {
        Integer countofTotalValidAgents = 0;
        SoftphoneLinks__c selectedObj = [SELECT Name, Sites__r.Name FROM SoftphoneLinks__c 
                                    WHERE Id=:selectedSiteLink];
                               
        if(isAltToAlt)
            countofTotalValidAgents = intCurrentAgentCount;
        else
            countofTotalValidAgents = validTransferAgentCount;
        
        if(intCountOfAgentsToTransfer==countofTotalValidAgents)
        {
            allRequest = true;    
            
            if(isDefaultToDefault)  
                sfUpdateRequest = new SoftPhoneLinkDB2UpdateRequest_DTO_HUM(strNewSiteName,currentObj.Name,selectedObj.Name,null,null);
        
            else if(isDefaultToAlt)
                sfUpdateRequest = new SoftPhoneLinkDB2UpdateRequest_DTO_HUM(strNewSiteName,currentObj.Name,null,null,selectedObj.Name);
            
            else if(isAltToAlt)
                sfUpdateRequest = new SoftPhoneLinkDB2UpdateRequest_DTO_HUM(strNewSiteName,null,null,currentObj.Name,selectedObj.Name);

        }
        else
        {
            List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2> lstInsertSoftPhone = new List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2>();
            List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2> lstDeleteSoftPhone = new List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2>();
            List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2> lstUpdateSoftPhone = new List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2>();
        
            SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2 softPhone ;
            softphoneAddAgent = new SoftPhoneLinkDB2Request_DTO_HUM();
           
            lstSfAgent = new List<Softphone_Agent__c>();
            if(count==0)
            {
                totalSize = intCountOfAgentsToTransfer;
                count++;
            }
             
            Integer nQueryCount = (batchSize < totalSize) ? batchSize : totalSize ; 
            if(nQueryCount < totalSize) totalSize = totalSize-nQueryCount;

            if(isAltToAlt)
            lstSfAgent = [SELECT Id,Name, Agent_Extension__c, Default_SoftphoneLinks__c, Default_SoftphoneLinks__r.Name, 
                                 Alternate_SoftphoneLinks__c, Alternate_SoftphoneLinks__r.Name, Sites__c, Sites__r.Name ,User_Id__c , DB2_Status__c
                                 FROM Softphone_Agent__c WHERE 
                                 Alternate_SoftphoneLinks__c=:strCurrentSoftphonLinkId
                                  AND Alternate_SoftphoneLinks__c !=:selectedSiteLink
                                       LIMIT :nQueryCount];
            else if(isDefaultToAlt)
            lstSfAgent = [SELECT Id,Name, Agent_Extension__c, Default_SoftphoneLinks__c, Default_SoftphoneLinks__r.Name, 
                                 Alternate_SoftphoneLinks__c, Alternate_SoftphoneLinks__r.Name, Sites__c, Sites__r.Name ,User_Id__c , DB2_Status__c
                                 FROM Softphone_Agent__c WHERE 
                                 Default_SoftphoneLinks__c=:strCurrentSoftphonLinkId
                                 AND Alternate_SoftphoneLinks__c !=:selectedSiteLink
                                       LIMIT :nQueryCount];
            else
            lstSfAgent = [SELECT Id,Name, Agent_Extension__c, Default_SoftphoneLinks__c, Default_SoftphoneLinks__r.Name, 
                                 Alternate_SoftphoneLinks__c, Alternate_SoftphoneLinks__r.Name, Sites__c, Sites__r.Name ,User_Id__c , DB2_Status__c
                                 FROM Softphone_Agent__c WHERE 
                                 Default_SoftphoneLinks__c=:strCurrentSoftphonLinkId
                                  AND Default_SoftphoneLinks__c !=:selectedSiteLink
                                       LIMIT :nQueryCount];
                                       
            for(Softphone_Agent__c agent : lstSfAgent) 
            {

                softPhone = new SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2();

                softPhone.SiteName = agent.Sites__r.Name ; 
                if(isDefaultToDefault)
                    softPhone.DefaultSoftphoneLinkName = selectedObj.Name ;  
                else
                    softPhone.DefaultSoftphoneLinkName = agent.Default_SoftphoneLinks__r.Name ; 
                
                if(isAltToAlt || isDefaultToAlt)
                    softPhone.AlternateSoftphoneLinkName = selectedObj.Name ;
                else
                    softPhone.AlternateSoftphoneLinkName = agent.Alternate_SoftphoneLinks__r.Name ; 
                
                softPhone.AgentId = agent.Name; 
                softPhone.UserId = agent.User_Id__c; 
                
                if(agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_INSERT || agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_INSERT_FAILED)
                {
                    lstInsertSoftPhone.add(softPhone);
                }
                else if (agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_DELETE || agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_DELETE_FAILED)
                {
                    lstDeleteSoftPhone.add(softPhone);
                }
                else if (agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_UPDATE || agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED || agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_UPDATE_FAILED)
                {
                    lstUpdateSoftPhone.add(softPhone);
                }                                           
            }
            
            if(!lstInsertSoftPhone.IsEmpty())
                softphoneAddAgent.insertlist = lstInsertSoftPhone;
    
            if(!lstUpdateSoftPhone.IsEmpty())
                softphoneAddAgent.updatelist = lstUpdateSoftPhone;
        
            if(!lstDeleteSoftPhone.IsEmpty())
                softphoneAddAgent.deletelist = lstDeleteSoftPhone;
        }   
    }
    
     
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke Softphone service
    *
    * @return continuation object
    */
    
    public Continuation transferDTDAgents2() 
    {
        isErrorScenario = validateErrorScenario();
        if(isErrorScenario)
            return null;
        
        formRequest();
        
        return oServiceTransfer.invokeSoftPhoneService(this);
    }
    
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
    public Object parseSoftphoneTransfer()
    {
         return oServiceTransfer.parseSoftphoneTransfer(this);
    }
}