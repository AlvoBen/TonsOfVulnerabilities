/**
   @Author      : Pradeepkumar Dani
   @name        : CaseAttachments_C_HUM
   @CreateDate  : 10/30/2017
   @Description : This class serves as controller for CaseAttachments_CMP_HUM component
   @Version     : 1.0
   @reference   : CaseAttachments_CMP_HUM
   
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pradeepkumar Dani         142531                  10/30/2017                     Original version  
 * Pradeepkumar Dani         157391                  12/14/2017                     Datatable updates
 * Srivinas Damera           215199                  04/18/2018                     REQ - 360903- CA Ticket 7458982
 * Srivinas Damera           293796                  09/30/2018                     REQ - 369721 - eHub DSS 2.0 upgrade
 * Prudhvi Pamarthi                                  02/06/2019                     REQ - 361155 - Not allowing case creation on Legacy Deleted policies
 * Santhosh Ganji                                    04/19/2019                     REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
 * Santhosh Ganji                                    05/24/2019                     REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
 * Mohammed Noor									 09/18/2020						USER STORY 1334690 - Changes to open MRM documents in HumRecViewer
 * Santhi Mandava                                  01/29/2021                     User Story 1791226: PR00094254 - MF 4- AEP Performance - SF Limits - Sync CPU Time Limit Analysis - Case Details
 * Gowthami Thota							        08/06/2021				        US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page
 * Vijaya Lakshmi Tummala                 	 	10/17/2022     			Original Version-Case Attachment -Feature-3033033
 *														UserStory -3775271 Creating Case Attachments in Lightning		
 ********************************************************************************************************************************/
public with sharing class CaseAttachments_C_HUM
{
    /**
    * This constructor method for CaseAttachments_C_HUM.
    * 
    * @param    NA
    * @return   NA
    */
    public CaseAttachments_C_HUM()
    {
        columnName = json.serialize(new list<String>{'fileName', 'createdDate', 'createdBy', 'createdByQueue'});
        columnHeader = json.serialize(new list<String>{ 'File Name', 'Created Date', 'Created By', 'Created By Queue' });
		sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
        toGetCaseId =  ApexPages.currentPage().getParameters().get('Id');
        bdisableButton = verifyLegacyDelete();
		hideTheButton = verifyMedicareCase();        
        if(HUMUtilityHelper.isCRMFunctionalityON(CaseDetailConstants_HUM.sUS1334690))
        {
            HUM_Webservice_Callout__c oMRMViewerLink = HUM_Webservice_Callout__c.getValues(CaseDetailConstants_HUM.sMRMViewerURL);
            if(oMRMViewerLink!=null && String.isNotBlank(oMRMViewerLink.End_Point_URL__c))  sMRMViewerURL = oMRMViewerLink.End_Point_URL__c;
        }        
	}
    
    public string columnName{ get; set; }
    public string columnHeader{ get; set; }
    public string sPharmacyUser{ get; private set; }
    public Boolean bdisableButton {get; set;}
    public Boolean blegacyDeleteCheck;
    public Id toGetCaseId;
	public boolean hideTheButton{ get; set;}
	public boolean hPharmacyUser{ get; set;}
    public Set<String> wqueues = new Set<String>{CaseDetailConstants_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CaseDetailConstants_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CaseDetailConstants_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public Set<String> chatQueues = new Set<String>(CaseDetailConstants_HUM.WEB_CHAT_WQUEUES);
    public string sMRMViewerURL{get; set;}    
    private Case caseRec;
    /**
    * This method generates list of attachments associated to the case.
    * 
    * @param    sCaseId         Case record ID
    * @return   lstCaseAtts     List of attachment records
    */
    public static list<CaseAttach_DTO_HUM> getWrapperClass(Id sCaseId)
    {
        string userName = system.UserInfo.getName();
        list<CaseAttach_DTO_HUM>  lstCaseAtts = new list<CaseAttach_DTO_HUM>();
        Case oCase = [SELECT Id, Case_Owner__c, Owner_Queue__c FROM Case WHERE id = :sCaseId];
        Boolean bUS1334690_Flag = HUMUtilityHelper.isCRMFunctionalityON(CaseDetailConstants_HUM.sUS1334690);
        for (Attachment__c oAtt : [SELECT Id, Name, Related_To_Case__c, CreatedDate, CreatedById, CreatedBy.Name, Attachment_URL_Text__c, Created_By_Queue__c, AlternateDocId__c, Document_Source__c, DocId__c FROM Attachment__c WHERE Related_To_Case__c = :sCaseId ORDER BY Id DESC])
        {
			CaseAttach_DTO_HUM oAttWrap = new CaseAttach_DTO_HUM();
			string deleteLink = CaseDetailConstants_HUM.sDel + CaseDetailConstants_HUM.sAttDel + oAtt.Id;
			oAttWrap.Id = CaseDetailConstants_HUM.sActionSeparator + (Schema.getGlobalDescribe().get(CaseDetailConstants_HUM.sAttObject).getDescribe().isDeletable() ? deleteLink : CaseDetailConstants_HUM.sEmpty);
			oAttWrap.fileName = oAtt.Name + CaseDetailConstants_HUM.sOpenAtt + (String.isNotBlank(oAtt.Attachment_URL_Text__c) ? (oAtt.Attachment_URL_Text__c).replace('\'', CaseDetailConstants_HUM.ATTACHMENT_FILENAME_APOSTOPHE) : oAtt.Attachment_URL_Text__c)+ CaseDetailConstants_HUM.sComma + oAtt.Id + CaseDetailConstants_HUM.sComma + oAtt.Document_Source__c + CaseDetailConstants_HUM.sComma + oAtt.AlternateDocId__c + CaseDetailConstants_HUM.sComma + oAtt.DocId__c;
			oAttWrap.createdDate = oAtt.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
			oAttWrap.createdBy = oAtt.CreatedBy.Name + CaseDetailConstants_HUM.sSrcUp + oAtt.CreatedById;
			oAttWrap.createdByQueue = oAtt.Created_By_Queue__c;
            if(String.isNotBlank(oAtt.Document_Source__c) && oAtt.Document_Source__c.equalsIgnoreCase(CaseDetailConstants_HUM.sDocSourceMRM) && !bUS1334690_Flag)
            {
                continue;   //if the Document Source is MRM and the MRM Viewer Switch is Off, then skip dispalying the MRM Attachments on UI.
            }
			lstCaseAtts.add(oAttWrap);
        }
        return lstCaseAtts;
    }
    
    /**
    * This remoting inokeed method generates list of attachments associated to the case.
    * 
    * @param    sCaseId         Case record ID
    * @return   List of attachment records
    */
    @RemoteAction
    public static list <CaseAttach_DTO_HUM> getJSONData(String sCaseId) 
    {
        return getWrapperClass(sCaseId);
    }

	/**
	* This remoting inokeed method generates list of attachments associated to the case.
	*
	* @param    sCaseId         Case record ID
	* @return   List of attachment records
	*/
	@RemoteAction
	public static string getURL(String sAttId)
	{
		string sURL = CaseDetailConstants_HUM.sEmpty;
        Map<Id,Id> attIdConVerId = new Map<Id,Id>();
		list<Attachment__c> lstAttachments = [SELECT Id, Attachment_URL_Text__c,Document_Source__c,DocId__c,Version_Id__c,(SELECT Id FROM Attachments LIMIT 1) FROM Attachment__c WHERE Id = :sAttId];

        if (lstAttachments != null && !lstAttachments.isEmpty())
		{
			Attachment__c attLight = lstAttachments.get(0);
                        
            if(string.isBlank(attLight.Attachment_URL_Text__c) && string.isBlank(attLight.DocId__c) && string.isBlank(attLight.Version_Id__c)&& string.isNotBlank(attLight.Document_Source__c) && (attLight.Document_Source__c =='LWC') ){
                List<ContentVersion> converLst = [select Id,ContentDocumentId,Attachment__c,Attachment__r.DocId__c,Attachment__r.Version_Id__c,Attachment__r.Attachment_URL_Text__c from ContentVersion where Attachment__c  = :sAttId];
                if(converLst != null && !converLst.isEmpty()){
                	ContentVersion conVer = converLst.get(0);
                 	attIdConVerId.put(attLight.id,conVer.id) ;
                }
                              
            }
        }
        
		if (lstAttachments != null && !lstAttachments.isEmpty())
		{
			Attachment__c att = lstAttachments.get(0);
			if (string.isNotBlank(att.Attachment_URL_Text__c))
			{
				sURL = att.Attachment_URL_Text__c;
			}
			  else if(string.isBlank(att.Attachment_URL_Text__c) && string.isNotBlank(att.DocId__c))
            {
                String sfdcBaseURL = URL.getSalesforceBaseUrl().toExternalForm();
				sURL = sfdcBaseURL + CaseDetailConstants_HUM.EHUB_URL_DOCID+lstAttachments[0].DocId__c +CaseDetailConstants_HUM.EHUB_URL_VID+lstAttachments[0].Version_Id__c;
            	
            }else if(string.isBlank(att.Attachment_URL_Text__c) && string.isBlank(att.DocId__c) && string.isBlank(att.Version_Id__c)&& string.isNotBlank(att.Document_Source__c) && (att.Document_Source__c =='LWC') ){
          			if(!attIdConVerId.isEmpty()){
                         String converURL =attIdConVerId.get(att.id);
                         String sfdcBaseURL1 = URL.getOrgDomainUrl().toExternalForm();
                         sURL =sfdcBaseURL1 +CaseDetailConstants_HUM.sAttLightURL+converURL;
                                                          
          			}
            } else
			{
				sURL = CaseDetailConstants_HUM.sAttURL + (!att.Attachments.isEmpty() ? att.Attachments.get(0).Id : CaseDetailConstants_HUM.sEmpty);
			}
		}
		return sURL;
	}
		

    /**  
    * This method is to check whether new attachment button should be disabled.
    * 
    * @return   Boolean
    */
    public boolean verifyLegacyDelete()
    {
       bdisableButton = false;
       blegacyDeleteCheck = false;
        if(toGetCaseId!=null)caseRec = [SELECT Id,CaseNumber, Account.ETL_Record_Deleted__c,RecordType.name, Policy_Member__c,Policy_Member__r.ETL_Record_Deleted__c, Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin, AccountId,  Status,Type  FROM Case where id= :toGetCaseId];
       
        if(caseRec.Account.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
        else if (caseRec.Policy_Member__r.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
        if(blegacyDeleteCheck && caseRec.Status=='Closed') bdisableButton = true;
        return bdisableButton;
     }
	 
	 public boolean verifyMedicareCase()
     {
       hPharmacyUser = false;
       hideTheButton = false;
       if(sPharmacyUser != CaseDetailConstants_HUM.NONPHARMACY_USER_HUM) hPharmacyUser = true;
        if(!Test.isRunningTest()){
           if(caseRec != null && (caseRec.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) ||  (chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN) ||(caseRec.Policy_Member__c != null && caseRec.Policy_Member__r.Product__c == CaseDetailConstants_HUM.MEDICARE_POLICY)))
           {
             if(caseRec.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || wqueues.contains(caseRec.Owner_Queue__c) || wqueues.contains(caseRec.Created_By_Queue__c) || (chatQueues.contains(caseRec.Owner_Queue__c) && caseRec.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN))
             {
               hideTheButton = true;
             }
           }
		}
       
       return hideTheButton;
     }
}