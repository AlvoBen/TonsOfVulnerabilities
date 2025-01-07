/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSync_C_HUM 
Version             : 1.0
Created Date        : August 10 2019
Function            : This servers as controller class for SoftPhoneSync_VF_HUM.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Joel George                                          08/10/2019                 
* Giri Yeddula                                         06/07/2021         User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
*******************************************************************************************************************************/
public with sharing class SoftPhoneSync_C_HUM 
{   
    public List<Softphone_Agent__c> lstAgents;
    private SoftPhoneSync_S_HUM oServiceBenefits = new SoftPhoneSync_S_HUM();
    public String sConRequest = '';
    public SoftPhoneLinkDB2Request_DTO_HUM softphoneAddAgent; 
    public Integer totalSize{ get; set; }
    public Integer batchSize{ get; set; }   
    private boolean bESAPILibSecurityfixSwitch {get;set;}
    
    public SoftPhoneSync_C_HUM(ApexPages.StandardSetController controller)
    {
        bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
        lstAgents = new List<Softphone_Agent__c>();
        batchSize = (Integer)HUMConstants__c.getInstance('SOFTPHONE_SYNC_BATCH_SIZE').IntegerValue__c;//300
        totalSize = [Select COUNT() from Softphone_Agent__c where DB2_Status__c IN : GLOBAL_CONSTANT_HUM.SOFTPHONE_STATUSES LIMIT 50000];
    }
    public SoftPhoneSync_C_HUM(){}
    
    public void createWrapper()
    {        
        softphoneAddAgent = new SoftPhoneLinkDB2Request_DTO_HUM();
        softphoneAddAgent.insertlist = new List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2>();
    	softphoneAddAgent.updatelist = new List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2>();
    	softphoneAddAgent.deletelist = new List<SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2>();
        SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2 softPhone;
        lstAgents = [SELECT id,Name, Sites__r.Name , User_Id__c, Default_SoftphoneLinks__c ,Alternate_SoftphoneLinks__r.Name ,  
                     Agent_Extension__c, DB2_Status__c, Integration_Update__c, DB2_Sync__c,Default_SoftphoneLinks__r.Name FROM Softphone_Agent__c 
                     WHERE DB2_Status__c IN : GLOBAL_CONSTANT_HUM.SOFTPHONE_STATUSES ORDER BY DB2_Status__c DESC Limit :batchSize];
        for(Softphone_Agent__c agent : lstAgents)
        {
            softPhone = new SoftPhoneLinkDB2Request_DTO_HUM.softPhoneLinkDB2();            
            softPhone.SiteName = agent.Sites__r.Name ; 
            softPhone.DefaultSoftphoneLinkName = agent.Default_SoftphoneLinks__r.Name ; 
            softPhone.AlternateSoftphoneLinkName = agent.Alternate_SoftphoneLinks__r.Name ; 
            softPhone.AgentId = agent.Name; 
            softPhone.UserId = agent.User_Id__c ; 
            
            if(agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_INSERT)
            {
                softphoneAddAgent.insertlist.add(softPhone);
            }
            else if (agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_UPDATE)
            {
                softphoneAddAgent.updatelist.add(softPhone);
            }
            else if (agent.DB2_Status__c == GLOBAL_CONSTANT_HUM.SOFTPHONE_READY_FOR_DELETE)
            {
                softphoneAddAgent.deletelist.add(softPhone);
            } 
        }   
    }
    
    /**
	* Returns request object of continuation object
	* <p>
	* Method to invoke Softphone service
	*
	* @return continuation object
	*/
    public Continuation invokeSoftPhoneService()
    {
        createWrapper();
        return oServiceBenefits.invokeSoftPhoneService(this);
    }   
    
    /**
	* Returns responses of continuation object
	* <p>
	* Callback Method to parse the service response
	*
	* @return Object
	*/
    public Object parseSoftPhoneService()
    {
        return oServiceBenefits.parseSoftPhoneService(this);
    }
    
    public object pageref(){  
          string  returl = ApexPages.currentPage().getParameters().get('retURL');
          returl = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(returl) : returl;
          return new PageReference(returl);
    }
    
}