/**
   @Author      : Prasad Jandhyala
   @name        : CCPGCPDataTableExtension_C_HUM
   @CreateDate  : 10/30/2017
   @Description : This test class ensures CCPGCPDataTableExtension_C_HUM coverage
   @Version     : 1.0
   @reference   : CCPGCPDataTableExtension_C_HUM
   
   Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Prasad Jandhyala        149219                      10/30/2017                     Original version 
 * Sumit Pharande                                      27/12/2017                     Defect Fix -334135.0001
 * Sumit pharande                                      28/12/2017                     Rollback changes   
 * Joel George	                                       11/14/2018                     REQ 378818 Changes   
 * Rakesh Patni			  319302					   01/28/2019                     REQ-380402 .Claim Hyperlink	 
 * Suraj Patil			  							   10/02/2019                     REQ-404549 Link in CRM Case to IMS ticket 
 * Vijaya Koriprolu                                     11/18/2019                 REQ-383958 added functionality for adding claim numbers on case edit page
 * Suraj Patil			  							   11/20/2019                     REQ - 414435 Adding IMS Approval Link to Related Information section
 * Jasmeen Shangari                                    02/26/2020                     AzureID- 878026 Case Detail Page - Related Info - RTI CLick Counter 
 * Mithra Bharadwaj                                    07/06/2020                     US#1199202 Excluding  MsgDefCode value in attachmentlog Item values
 * Prasanthi Kandula                                   07/24/2020                     US - 1031530 Added swtich to toggle between claim details V2 and V3 page
 * Gowthami Thota									   07/29/2021					  US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page 
 ********************************************************************************************************************************/
public with sharing class CCPGCPDataTableExtension_C_HUM 
{
	public static ClaimsSummary_S_HUM_V2 oServiceClaim = new ClaimsSummary_S_HUM_V2();
    public string columnName{ get; set; }
    public string columnHeader{ get; set; }
    public string paging{ get; set; }
    public string sIMSDetailURL{get;set;}
	public string sIMSApprovalURL {get;set;}
    public string attachmentItemColumnName{ get; set; }
    public string attachmentItemColumnHeader{ get; set; }
    public boolean bClmDetailURLSwitch {get; set;}
    public boolean showAttachmentLogs{get;set;}
    
    public CCPGCPDataTableExtension_C_HUM()
    {
        columnName = json.serialize(new list<String>{ 'sourceSys','attachmentType','attachmentId', 'loggedDateTime','loggedBy' });
        columnHeader = json.serialize(new list<String>{ Label.HUM_CCP_Source, Label.HUM_CCP_Type,Label.HUMAttachmentId, 'Created Date' , 'Created By'});
        paging = json.serialize(new list<Integer>{ 5,10,25,50,75,100 });
        
        attachmentItemColumnHeader = json.serialize(new list<String>{ 'Name','Value'});
        attachmentItemColumnName = json.serialize(new list<String>{ 'Name','Value'});
        showAttachmentLogs = true;        
		
		Endpoint_Url_HUM__c serviceDetails = Endpoint_Url_HUM__c.getValues('IMSTicketDetailURL');  
        sIMSDetailURL = serviceDetails.Remote_Url__c;
		
		Endpoint_Url_HUM__c IMSApprovalLink = Endpoint_Url_HUM__c.getValues('IMSTicketApprovalURL');  
        sIMSApprovalURL = IMSApprovalLink.Remote_Url__c;
        bClmDetailURLSwitch = CRMFunctionalityenabler_H_HUM.Checkflagvalue('ClaimDetailsNewVersion');
    }
    
    // This is the remote action called to populate CCPGCP Attachemnt data.
    @RemoteAction
    public static list <CCPGCPAttachmentDTO_C_HUM> getCCPGCPAttachments(String sCaseId) 
	{
        return getCCPGCPAttachmentsList(sCaseId);
    }
    
    // This method fetches CCPGCP Attachments and Attachment log item records related to the Attachement Logs.
    public static list<CCPGCPAttachmentDTO_C_HUM> getCCPGCPAttachmentsList(String sCaseId)
    {
        String crmServiceSystem = CaseDetailConstants_HUM.CRMSERVICESYSTEMNAME_HUM;
        Integer humLimit = Integer.valueOf(Label.HUM_Limit);
        List<Id> attachmentLogList = new List<Id>();
        List<CCPGCPAttachmentDTO_C_HUM> CCPGCPAttachmentDTOList = new List<CCPGCPAttachmentDTO_C_HUM>();
         Map<Id,Attachment_Log__c> attachmentLogMap = new Map<Id,Attachment_Log__c> ([Select Id,Name,Attachment_Type__c,Created_By__c,CreatedDate,Source_System__c from Attachment_Log__c where Case__c = :sCaseId AND ((Source_System__c = :crmServiceSystem and Attachment_Type__c = :CaseDetailConstants_HUM.CLAIMS_MANUAL_ENTRY) OR (Source_System__c != :crmServiceSystem and Attachment_Type__c !=:CaseDetailConstants_HUM.CLAIMS_MANUAL_ENTRY))  ORDER BY Id DESC limit :humLimit]);
        Map<Id,List<Attachment_Log_Item__c>> attachmentLogItemMap = new Map<Id,List<Attachment_Log_Item__c>>();
        List<Attachment_Log_Item__c> attachLogList ;
        for(Attachment_Log_Item__c attachItemLogItem : [Select id,Name,Value__c,Attachment_Log__c,Attachment_Log__r.Attachment_Type__c from Attachment_Log_Item__c where Attachment_Log__c in :attachmentLogMap.keySet() and Name != :CaseDetailConstants_HUM.sMsgDefCode order by Custom_Order_Number__c])
        {
            attachLogList = attachmentLogItemMap.containsKey(attachItemLogItem.Attachment_Log__c) ? attachmentLogItemMap.get(attachItemLogItem.Attachment_Log__c) : new List<Attachment_Log_Item__c>();
            attachLogList.add(attachItemLogItem);
            attachmentLogItemMap.put(attachItemLogItem.Attachment_Log__c,attachLogList);
        }
        for(Attachment_Log__c attachmentLog :attachmentLogMap.values()) 
		{
            CCPGCPAttachmentDTO_C_HUM ccpgcp = new CCPGCPAttachmentDTO_C_HUM(attachmentLog,attachmentLogItemMap.get(attachmentLog.Id));
            CCPGCPAttachmentDTOList.add(ccpgcp);
        }
        return CCPGCPAttachmentDTOList;
    }
	/*
	*  This method is used to call Claim Summary Service using Contination via JS remoting
	*  @name   fetchClaimsSummary
	*  @param  String
	*  @return Continuation Object
	*  @throws NA
	*/
	@RemoteAction
		public static Object fetchClaimsSummary(String sClaimNumber, String sCaseid) {
		try {
			if (sClaimNumber != '')
			{
				String polMem;
				List<Case> lCase = new list<Case>();
				lCase = [Select Policy_Member__c from Case where id = :sCaseid];
				if (!lCase.isEmpty())
				{
					polMem = lCase[0].Policy_Member__c;
				}
				//Continuation logic
				HUM_Webservice_Callout__c oSvcHeaderClaims = HUM_Webservice_Callout__c.getValues(CaseDetailConstants_HUM.HUMGetClaimSummary_V3);
				Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaims.Timeout__c) / 1000;
				Continuation con = new Continuation(iTIMEOUT_INT_SECS);
				con.continuationMethod = 'processResponse';
				HttpRequest request = new HttpRequest();
				request = oServiceClaim.setupGoButtonRequest(request, oSvcHeaderClaims, sClaimNumber);
				con.state = polMem;
				if (!Test.isRunningTest())
				{
					con.addHttpRequest(request);
				}
				return con;
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex) {
			HUMExceptionHelper.logErrors(ex, 'CCPGCPDataTableExtension_C_HUM', 'fetchClaimsSummary');
			return null;
		}
	}
	/*
	*  This method is used to process and parese Continuation Response
	*  @name   processResponse
	*  @param  Contination label and state
	*  @return ClaimSummaryResponse_CORR_DTO_HUM Object
	*  @throws NA
	*/
	public static Object processResponse(List<string> labels, Object State)
	{
		try {
			HttpResponse responseClaimSummary = Continuation.getResponse(labels[0]);
			List<ClaimsSummary_Wrapper_DTO_HUM> claimSummaryRespObj = new List<ClaimsSummary_Wrapper_DTO_HUM>();
			claimSummaryRespObj = oServiceClaim.processResponse(responseClaimSummary.getBody());
			ClaimSummaryResponse_CORR_DTO_HUM remoteResp = new ClaimSummaryResponse_CORR_DTO_HUM();
			remoteResp.claimsResp = claimSummaryRespObj;
			String memberID = claimSummaryRespObj[0].sMemberId;
			if (memberID.contains('-'))
			{
				memberID = memberID.substringBefore('-')+memberID.substringAfter('-');
			}
			if ((String)state != null)
			{
				remoteResp.polMemId = (String)state;
			}
			else
			{
				List<Policy_Member__c> lPolMem = [Select id from Policy_Member__c where name = :memberID];
				if (!lPolMem.isEmpty())
				{
					remoteResp.polMemId = lPolMem[0].id;
				}
			}
			if(remoteResp.polMemId==null || remoteResp.polMemId=='')
			{
				return null;
			}
			else 
			{
				return remoteResp;
			}			
		}
		catch (Exception ex) {
			HUMExceptionHelper.logErrors(ex, 'CCPGCPDataTableExtension_C_HUM', 'processResponse');
			return null;
		}

	}
}