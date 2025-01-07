/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_LDTO_Hum.cls 
Created Date     : March 2, 2022
Function         : DTO for Classification and Intent for logged in User
Test Class       : QuickStart_LT_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Jasmeen Shangari            -----                 03/02/2022                 Original Version
* Pooja Kumbhar						  			    08/16/2022  		       US:3272640 and US:3272641 - Reference and Mentor document section
* Pooja Kumbhar									    08/22/2022				   US:3272634 - Lightning - Quick Start - RCC Specific - G&A Complaint Section
* Jasmeen Shangari									09/27/2022				   US:3230697 - Check to see if current user is RSOHP user and selected CI belongs to HP
* Pooja Kumbhar									    10/06/2022				   DF-6290: QA Lightning US3705153 The name of the Task tab is not displayed correctly
************************************************************************************************************************************************************************************/

public with sharing class QuickStart_LDTO_Hum {

    public Id userId{get; set;}
    public String sUserName{get; set;}
    public String sProfileName{get; set;}
    public String sQueueServiceCenter{get; set;}
    public String sQueueDepartment{get; set;}
    public String sClassificationType{get; set;}
    public Id idClassificationType{get; set;} 
    public String sQueueName{get; set;}
    public Id idQueueName{get; set;}
    public String sBusinessGroup{get; set;}
    public Map<String,String> lstMedicarePartCPartD{get; set;}    
    public Map<String, QuickStartCTCI_LDTO_HUM> mapClassificationIntent;
    public Map<String, String> mapFrequentlyUsedCIOption;
    Map<String,Map<String,String>> DupCases{get; set;}
    public List<CustomLink__c> lstRFDocumentLink {get;set;}

    public QuickStart_LDTO_HUM(Map<String,Map<String,String>> DupCases){
        this.DupCases = DupCases;
    }
    
    public QuickStart_LDTO_HUM(Id userId, String sUserName,String sProfileName, String sQueueServiceCenter, String sQueueDepartment,
     String sClassType, Id idClassType, String sQueueName, 
            Id idQueueName, String sBusinessGroup, Map<String,String> lstMedicarePartCPartD,
            Map<String, QuickStartCTCI_LDTO_HUM> mapClassIntent, Map<String,String> mapFreqUsedCI, List<CustomLink__c> lstRFDocumentLink ){
        this.userId = userId;
        this.sUserName = sUserName;
        this.sProfileName = sProfileName;
        this.sQueueServiceCenter = sQueueServiceCenter;
        this.sQueueDepartment = sQueueDepartment;

        this.sClassificationType = sClassType;
        this.idClassificationType = idClassType;
        this.sQueueName = sQueueName;
        this.idQueueName = idQueueName;
        this.sBusinessGroup = sBusinessGroup;
        this.lstMedicarePartCPartD = lstMedicarePartCPartD;
        this.mapClassificationIntent = mapClassIntent;   
        this.mapFrequentlyUsedCIOption = mapFreqUsedCI;
        this.lstRFDocumentLink = lstRFDocumentLink;
    }       
    
    public class AdditionalInfo_DTO_HUM 
    {
        public Id idQuickPreText{get; set;}
        public String sInformationalMessage{get; set;}
    	public String sSoftWarningMessage{get; set;}
    	public String sComplaintGnASetup{get; set;}    	
    	public Boolean bAllowMultipleCase{get; set;}
	    public String sHeaderMessageType{get; set;}
        public String sHeaderMessage{get; set;}
        public Boolean bNeverComplaint{get; set;}
        public Boolean bNeverGnA{get; set;}        
        public String sRecommendActions{get; set;}
        public String sComplaintGnA{get; set;}
        public String sComplaintGnAReason{get; set;}
		public Map<String,List<String>> mapRAiconImages{get;set;}

    	public AdditionalInfo_DTO_HUM(Quick_Start_Pretext__c qsp) 
         {
    		If (qsp!= null)
            {
    			this.sInformationalMessage = qsp.Informational_Message__c;
    			this.sSoftWarningMessage = qsp.Soft_Warning_Message__c;
    			this.sComplaintGnASetup = qsp.Complaint_GA_Setup__c;
    			this.bAllowMultipleCase = qsp.Is_Allow_Multiple_Case__c;
    			this.sHeaderMessageType = qsp.Header_Message_Type__c;
                this.sHeaderMessage = qsp.Header_Message__c;
                this.bNeverComplaint = qsp.CTCI_List_lkp__r.Never_a_Complaint__c;
                this.bNeverGnA = qsp.CTCI_List_lkp__r.Never_a_G_A_Rights_Given__c;                
                this.sRecommendActions = qsp.Recommended_Actions__c;
                this.sComplaintGnA = qsp.Complaint_GA__c;
                this.sComplaintGnAReason = qsp.Complaint_GA_Reason__c;
                this.idQuickPreText = qsp.id;
    		}
    	}
    }
	
    public class CommonInfo_DTO_HUM   
    {
        public AdditionalInfo_DTO_HUM additionalData {get;set;}
        public Boolean bHPCase{get;set;}

        public CommonInfo_DTO_HUM (AdditionalInfo_DTO_HUM additionalData, Boolean bHPCase)
        {
            this.additionalData = additionalData;
            this.bHPCase = bHPCase;
        }

    }
	
	public class Task_DTO_HUM 
    {
         public string taskId {get;set;}
         public string taskNumber {get;set;}
        
        public Task_DTO_HUM (id taskid, string taskNumber){
        this.taskId = taskid;
        this.taskNumber = taskNumber;
        } 
    }
}