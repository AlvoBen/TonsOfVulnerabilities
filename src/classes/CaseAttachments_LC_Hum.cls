/***********************************************************************************************************************
   @Author      : Vijaya Lakshmi Tummala
   @name        : CaseAttachments_LC_Hum
   @CreateDate  : Oct/17/2022
   @Description : This class serves as Controller for LWC component FileUploadLWCHum
   @Version     : 1.0
************************************************************************************************************************
Modification Log:
 * Developer Name            	Code Review   Date           Description
 *------------------------------------------------------------------------------------------------------------
 * Vijaya Lakshmi Tummala                 	 10/17/2022     Original Version-Case Attachment -Feature-3033033
 * 															UserStory -3775271 Creating Case Attachments in Lightning		
************************************************************************************************************************/
public with sharing class CaseAttachments_LC_Hum {   

	
    /**
    * Function: method fetches attachments related to case 
    * @param  String
    * @return list<AttachmentWrapper> 
    */
    @AuraEnabled
    public static list<AttachmentWrapper> getRelatedFiles(String recordId) {
		
            Map<Id,String> mapAttachIdwithURL = new  Map<Id,String>();
			Map<String,String> mapAttachIdConId = new  Map<String,String>();
			Set<Id> attIds = new  Set<Id>();			
            list<AttachmentWrapper> attWrapMain = new list<AttachmentWrapper>();
			
			// classic attachment 
            List<Attachment__c> attLstClassic = [SELECT Id,Name,Attachment_URL_Text__c,DocId__c,Version_Id__c,RecordtypeID, Related_To_Case__c,Document_Source__c,Created_By_Queue__c,CreatedDate,CreatedById,CreatedBy.Name,
            (SELECT Id FROM Attachments LIMIT 1) FROM Attachment__c WHERE Related_To_Case__c =: recordId and Document_Source__c !='LWC' ORDER BY CreatedDate DESC];
            // lightning files
			List<Attachment__c> attLstLight = [SELECT Id,Name,Attachment_URL_Text__c,DocId__c,Version_Id__c,RecordtypeID, Related_To_Case__c,Document_Source__c,Created_By_Queue__c,CreatedDate,CreatedById,CreatedBy.Name
             FROM Attachment__c WHERE Related_To_Case__c =: recordId and Document_Source__c=:'LWC' ORDER BY CreatedDate DESC ];
			 
			 if (attLstLight != null && !attLstLight.isEmpty()) {
				for(Attachment__c attach: attLstLight){
					if(attach.Document_Source__c=='LWC' && (attach.DocId__c== null) && (attach.Version_Id__c==null)){			
					attIds.add(attach.id);
					}
				}
			 }
						 
			List<ContentVersion> converLst = [select Id,ContentDocumentId,Attachment__c,Attachment__r.DocId__c,Attachment__r.Version_Id__c,Attachment__r.Attachment_URL_Text__c from ContentVersion where Attachment__c in : attIds ORDER BY CreatedDate DESC];
			 
			  if (converLst != null && !converLst.isEmpty())
			  {
					for(ContentVersion con: converLst){
						mapAttachIdConId.put(con.Attachment__c,con.id);
					 }
			  }
			 
			// classic attachment with and without standard attachemnts
		if (attLstClassic != null && !attLstClassic.isEmpty())
			{
            for(Attachment__c attach: attLstClassic){
				string sURL = CONSTANTS_CASE_HUM.sEmpty;            
                String attStd ='';
               
                if(!attach.Attachments.isEmpty())
                { 
                    attStd=attach.Attachments.get(0).Id;
                }else{
                    attStd=CONSTANTS_CASE_HUM.sEmpty;
                }
        	                	
				if (string.isNotBlank(attach.Attachment_URL_Text__c))
					{
						mapAttachIdwithURL.put(attach.Id,attach.Attachment_URL_Text__c);
                        
						
					} else if(string.isBlank(attach.Attachment_URL_Text__c) && string.isNotBlank(attach.DocId__c))
								{ // when doc id and version are not balnk and response from ehub is recived
									String sfdcBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
									sURL = sfdcBaseURL + CONSTANTS_CASE_HUM.EHUB_URL_DOCID+attach.DocId__c +CONSTANTS_CASE_HUM.EHUB_URL_VID+attach.Version_Id__c;
                                    mapAttachIdwithURL.put(attach.Id,sURL);									
								}
								else
								{ // when docid and version id are blank no response from enhub
                                    if(string.isNotBlank(attStd))
                                    {sURL = CONSTANTS_CASE_HUM.sAttURL +  attStd ;}
                                    else{sURL = CONSTANTS_CASE_HUM.sAttURL +   CONSTANTS_CASE_HUM.sEmpty ;}
                                   	mapAttachIdwithURL.put(attach.Id,sURL);
								}					
            }
                 
        }
			
		if (attLstLight != null && !attLstLight.isEmpty()) {
			for(Attachment__c attach: attLstLight){
					string sURL = CONSTANTS_CASE_HUM.sEmpty;
					if (string.isNotBlank(attach.Attachment_URL_Text__c))
						{
							mapAttachIdwithURL.put(attach.Id,attach.Attachment_URL_Text__c);
							
						} else if(string.isBlank(attach.Attachment_URL_Text__c) && string.isNotBlank(attach.DocId__c))
									{ // when doc id and version are not balnk and response from ehub is recived
										String sfdcVFURL = URL.getSalesforceBaseUrl().toExternalForm();
										sURL = sfdcVFURL + CONSTANTS_CASE_HUM.EHUB_URL_DOCID+attach.DocId__c +CONSTANTS_CASE_HUM.EHUB_URL_VID+attach.Version_Id__c;
										mapAttachIdwithURL.put(attach.Id,sURL);
                                       								
									}
									else if(string.isBlank(attach.Attachment_URL_Text__c) && string.isBlank(attach.DocId__c) && string.isBlank(attach.Version_Id__c) && (attach.Document_Source__c == 'LWC'))
									{ // when docid and version id are blank no response from enhub lighting download url
										String converURL =mapAttachIdConId.get(attach.id);
										String sfdcBaseURL = URL.getOrgDomainUrl().toExternalForm();
                                        sURL = sfdcBaseURL+CONSTANTS_CASE_HUM.sAttLightURL+converURL;
                                       	mapAttachIdwithURL.put(attach.Id,sURL);
										
									}	
					
               
				}
			}
			if (attLstClassic != null && !attLstClassic.isEmpty()){
             	attWrapMain.addall(frameAttach(attLstClassic,mapAttachIdwithURL));				
			}
			if (attLstLight != null && !attLstLight.isEmpty()) {
                attWrapMain.addall(frameAttach(attLstLight,mapAttachIdwithURL));
				
			}
            
            return attWrapMain;
        }
	/**
    * Wrapper Class: sets and gets all the strings
    * */	
    public class AttachmentWrapper{
        @AuraEnabled
		public string sAttId {get;set;}
     	@AuraEnabled
		public string sName {get;set;}
		@AuraEnabled
		public string sSource {get;set;}
		@AuraEnabled
		public string sCreatedDate {get;set;}
		@AuraEnabled
		public string sCreatedByName {get;set;}
		@AuraEnabled
		public string sCreatedByQueue {get;set;}
		@AuraEnabled
		public string sUrl {get;set;}
        @AuraEnabled
		public string sUrlCreated {get;set;}
        
    }

	/**
    * Function: method deletes attachment related to case 
    * @param  String
    * @return boolean 
    */
    @AuraEnabled
        public static boolean deleteSelectedRow(String sAttIdDel) {
           
            boolean res=false;
            List<Attachment__c> delAtt = new List<Attachment__c>();
             String att = sAttIdDel.substring(1,sAttIdDel.length()-1);
             if(att!=NUll && Attachment__c.sobjecttype.getdescribe().isdeletable())
            { 
                Database.delete([Select Id from Attachment__c where Id= :att],false);
                res=true;
               
            }
            return res;
           
       }

    /**
    * Function: when status of the case is closed attachment upload will not be shown
    * @param  String recordId
    * @return boolean 
    */
     @AuraEnabled(cacheable=true)
     public static boolean verifyLegacyDelete(String recordId) {
		Id caseId =recordId;
		boolean bdisableButton = false;
		boolean blegacyDeleteCheck = false;
		String sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
		boolean hPharmacyUser = false;
		Set<String> wqueues = new Set<String>{CONSTANTS_CASE_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CONSTANTS_CASE_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CONSTANTS_CASE_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CONSTANTS_CASE_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
        Set<String> chatQueues = new Set<String>(CONSTANTS_CASE_HUM.WEB_CHAT_WQUEUES);	
		
			if(caseId!=null){
				Case caseRec = [SELECT Id,CaseNumber, Account.ETL_Record_Deleted__c,RecordType.name, Policy_Member__c,Policy_Member__r.ETL_Record_Deleted__c, Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin, AccountId,  Status,Type  FROM Case where id= :caseId];
				if(caseRec.Account.ETL_Record_Deleted__c == true) { blegacyDeleteCheck = true;}
				else if (caseRec.Policy_Member__r.ETL_Record_Deleted__c == true){ blegacyDeleteCheck = true;}
				if(blegacyDeleteCheck && caseRec.Status=='Closed') {bdisableButton = true;}
			
			if(sPharmacyUser != CONSTANTS_CASE_HUM.NONPHARMACY_USER_HUM) {hPharmacyUser = true;}
			if(!Test.isRunningTest()){
			   if(caseRec != null && (caseRec.Recordtype.name.containsIgnoreCase(CONSTANTS_CASE_HUM.HumHPRecType) ||  (chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CONSTANTS_CASE_HUM.WEB_CHAT_ORIGIN) ||(caseRec.Policy_Member__c != null && caseRec.Policy_Member__r.Product__c == CONSTANTS_CASE_HUM.MEDICARE_POLICY)))
			   {
				 if(caseRec.Recordtype.name.containsIgnoreCase(CONSTANTS_CASE_HUM.HumHPRecType) || wqueues.contains(caseRec.Owner_Queue__c) || wqueues.contains(caseRec.Created_By_Queue__c) || (chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CONSTANTS_CASE_HUM.WEB_CHAT_ORIGIN))
				 {
				   bdisableButton = true;
				 }
			   }
		}       
            }	
			
			
        return bdisableButton;
    }
    
    /**
    * Function: function used to frame attachments
    * @param  List<Attachment__c> lstAttachments
    * @param  Map<Id,String> mapAttachIdwithURL
    * @return list<AttachmentWrapper> 
    */
    public static list<AttachmentWrapper> frameAttach(List<Attachment__c> lstAttachments,Map<Id,String> mapAttachIdwithURL) {
        list<AttachmentWrapper> attWrap = new list<AttachmentWrapper>();
        for(Attachment__c att: lstAttachments){
					String sfdcBaseURL = URL.getOrgDomainUrl().toExternalForm();
					AttachmentWrapper wrap = new AttachmentWrapper();
					wrap.sName = att.Name;
					wrap.sSource = att.Document_Source__c;
					wrap.sCreatedDate = att.CreatedDate.format(CONSTANTS_CASE_HUM.HUMDateTimeFormat);
					wrap.sCreatedByName = att.CreatedBy.Name;
					wrap.sCreatedByQueue = att.Created_By_Queue__c;
					wrap.sUrlCreated=sfdcBaseURL+'/'+att.CreatedById;
					wrap.sUrl = mapAttachIdwithURL.get(att.Id);
					wrap.sAttId=att.Id;
					attWrap.add(wrap);
				}
          return attWrap;
    }
	
}