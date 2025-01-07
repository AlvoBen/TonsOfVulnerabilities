/*******************************************************************************************************************************
Apex Class Name : PharmacyOrderDetail_C_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Class contains methods for services to fetch Pharmacy Order  information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey            87810                    04/14/2017                 REQ - 305689  Original ersion
* Rajesh Keswani                                      07/17/2017                 Adding network ID in service Request
* Rajesh Chandgothia		 114002 			      07/25/2017				 REQ - 323485, CMO CR 5227 (CSI 587)-- 1) remove MTM hover; 2) add 'order status' column; 3) stop translating to OPEN; 4) add 'finished date' to summary
* Sharan Shanmugam									   07/27/2017				 REQ - 323542 : Exception handling
* AshokKumar Nutalapati      319140                   01/10/2019                 REQ - 377476: Adding 'Order Total' and 'Order Tax' Fields to the Order Details Page (RxRF) 
* AshokKumar Nutalapati      338598                   02/21/2019                 Defect Fix 402874.0001
* Ravi Prakash kantamneni    345989                   03/21/2019                 REQ-383718 - Automatically Record Order Details to Case: 'Save & Log' to New Case & Existing Case(RXRF)
* Ravi Prakash kantamneni                             03/27/2019                 REQ-383718 - Error Handling in Orderdetail Page
* Suresh Sunkari             359157                   06/14/2019                 REQ - 396354 - Styling the Link
* Riya Agarwal								          08/18/2020			     REQ - 760189-PR00091606 - MF 4 - Finance Code Order Detail Page
* AshokKumar Nutalapati                     		  09/01/2020                 REQ - 1327436 - Display OMS Originated Notes & Case Comments on the Order Details Page(RxRF)
* AshokKumar Nutalapati                               01/19/2021                 REQ - 1826594 Display OMS Originated Notes & Case Comments on the Order Details Page - Query Null Check (CRMS)
* Himalay Patel                                       01/27/2021                 REQ - 1735008 Display Quantity, Day Supply and Refills Remaining on Order Details Page (RxRF) (ID# 126)
* Pallavi Shewale				      				  02/10/2021	    		 User Story 1669828 - PR00094226 - CR AEP readiness -Research - Critical Security Issues
* AshokKumar Nutalapati                         	  03/05/2021                 REQ - 1985174 T1PRJ0002517 - MF 1 - TECH - Display OMS Originated Notes & Case Comments on the Order Details Page - Clean Up (CRMS) (ID# 120) 
* Pallavi Shewale				      06/08/2021		 US-1491784 Changes - Finance Queue
* Abhishek Mangutkar								  06/01/2021				 REQ - 2315809 - Omni-Carrier HP Solutions - HP CRM FedEx Shipment Method Changes
* Prudhvi Pamarthi                                    06/04/2021                 US#2140380 Introducing Null check
* Apurva Urkude                                       06/17/2021                 Req:2361295 T1PRJ0002736 - 2021 MF6 - SF - TECH - CheckMarx Vulnerabilities Fix - Pharmacy Order
* Nirmal Garg										  07/22/2021			     Defect 3438 Fix
* Satyam Malviya									  08/30/2021			     CheckMarx Issse Fix
* Nirmal Garg										09/16/2021					 Added browser logic for coveo
* AshokKumar Nutalapati								  10/19/2021				 User Story - 660769: T1PRJ0002517 - MF 4 - Update Order Payment Method Fields (RxRF) (ID# 74)
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
* Bhavani Tati								 		   05/20/2022				 User Story - 3175324: T1PRJ0200895 - MF 5 - TECH - Switch Clean Up - Update Order Payment Method Fields (CRMS) (ID# 74)
* Pinky Vijur                                       10/17/2022                   US - 3683150: T1PRJ0272555- MF 2 SF - TECH - SonarQube 2022 - CRM_Pharmacy : SOQL and DML inside loops.
*****************************************************************************************************************************************/
public with sharing class PharmacyOrderDetail_C_HUM 
{  
   public string capType {get;set;}
   public string orderNumber {get;set;}
   public string webOrderNumber {get;set;}
   public string OTC {get;set;}
   public string OcreationDate {get;set;}
   public string oReleaseDate {get;set;}
   public string CurrentQueue {get;set;}
   public String orderSource {Get;Set;}
   public String orderTotal {Get;Set;}
   public String orderCopay {Get;Set;}
   public String orderTax {Get;Set;}
   public String ostartDate {Get;Set;}
   public string oEndDate {get;set;}
   public string oShipDate {get;set;}
   public string orderStatus {get;set;}
   public string enterPriseID {get;set;}
   public string memId {get;set;}
   public string source {get;set;}
   public Boolean renderDetail {get;set;}
   public String sPayer {get;set;}   
   public string recordId{get;set;}
   public string pmId{get;set;}
   public Boolean bSwitch{ get; set;}
   public list<CaseCommentsOMSNotes_DTO_HUM>  lstCaseComments{get;set;}
   @TestVisible private transient String sCaseCommentsJSONreturn = '';
   public Boolean caseCommentsFailed {get;set;}
   public Boolean caseCommentsExists {get;set;}
   public Boolean OMSExists {get;set;}
   public Boolean bSwitchERC {get;set;}
   public Boolean bSwitchShippmentMethod {get;set;}
   private boolean bESAPILibSecurityfixSwitch {get;set;}
   public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
	public string sCaseCommentsJSON
	{
		get 
		{
			if(lstCaseComments != NULL)
			{
				sCaseCommentsJSONreturn = JSON.serialize(lstCaseComments); 
				return sCaseCommentsJSONreturn;
			}
			else
			{
				return null;
			}
		}
		public set;
	}

   public PharmacyOrderDetail_C_HUM()
   {
		//REQ - 760189 Switch for new Finance Code Column 
         bSwitch =  HUMUtilityHelper.isCRMFunctionalityON('760189');
		bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('2522719');
        bSwitchERC  = HUMUtilityHelper.isCRMFunctionalityON('1491784');
		bSwitchShippmentMethod = HUMUtilityHelper.isCRMFunctionalityON('2315809');
        string sIsDetail =  ApexPages.currentPage().getParameters().get('isDetail');       
       if(!String.IsBlank(sIsDetail)) 
       {
                recordId =  ApexPages.currentPage().getParameters().get('recordId');
             	if(!String.IsBlank(recordId)) recordId = EncodingUtil.urlDecode(recordId, 'UTF-8');
                
                sIsDetail = EncodingUtil.urlDecode(sIsDetail, 'UTF-8');
                if(sIsDetail == 'true') renderDetail = true;
                
                capType =  ApexPages.currentPage().getParameters().get('capType');
                if(!String.IsBlank(capType)) capType = EncodingUtil.urlDecode(capType, 'UTF-8');
                  
                sPayer =  ApexPages.currentPage().getParameters().get('payer');
                if(!String.IsBlank(capType)) sPayer = EncodingUtil.urlDecode(sPayer, 'UTF-8');
                  
                orderNumber =  ApexPages.currentPage().getParameters().get('orderNumber');
                if(!String.IsBlank(orderNumber)) orderNumber = EncodingUtil.urlDecode(orderNumber, 'UTF-8');
                
                orderTotal =  ApexPages.currentPage().getParameters().get('orderTotal');       
                If (!String.IsBlank(orderTotal)) orderTotal = EncodingUtil.urlDecode(orderTotal, 'UTF-8');

                orderCopay =  ApexPages.currentPage().getParameters().get('orderCopay');       
                If (!String.IsBlank(orderCopay)) orderCopay = EncodingUtil.urlDecode(orderCopay, 'UTF-8');

                orderTax =  ApexPages.currentPage().getParameters().get('orderTax');       
                If (!String.IsBlank(orderTax)) orderTax = EncodingUtil.urlDecode(orderTax, 'UTF-8');
                       
                ostartDate =  ApexPages.currentPage().getParameters().get('ostartDate');       
                if(!String.IsBlank(ostartDate)) ostartDate = EncodingUtil.urlDecode(ostartDate, 'UTF-8');
                       
                oEndDate =  ApexPages.currentPage().getParameters().get('oEndDate');       
                if(!String.IsBlank(oEndDate)) oEndDate = EncodingUtil.urlDecode(oEndDate, 'UTF-8');
                
                enterPriseID =  ApexPages.currentPage().getParameters().get('enterPriseID');       
                if(!String.IsBlank(enterPriseID)) enterPriseID = EncodingUtil.urlDecode(enterPriseID, 'UTF-8');
				
				memId =  ApexPages.currentPage().getParameters().get('memId');   	   
				if(!String.IsBlank(memId)) memId = EncodingUtil.urlDecode(memId, 'UTF-8');
                       
                orderStatus=  ApexPages.currentPage().getParameters().get('orderStatus');         
                if(!String.IsBlank(orderStatus)) orderStatus = EncodingUtil.urlDecode(orderStatus, 'UTF-8');
                
                oShipDate =  ApexPages.currentPage().getParameters().get('FinishedDate');         
                If (!String.IsBlank(oShipDate)) oShipDate = EncodingUtil.urlDecode(oShipDate , 'UTF-8');

                pmId =  ApexPages.currentPage().getParameters().get('pmId');
                if(!String.IsBlank(pmId)) pmId = EncodingUtil.urlDecode(pmId, 'UTF-8');

                if (!String.isBlank(pmId) && !String.isBlank(orderNumber)){
                    List<String> listCasesLink = New List<String>();
                    Map<Id, String> mapCaseLinkByCaseId = New Map<Id, String>();
                    
                    List<CaseComment> listCaseComment = [SELECT Id, CommentBody, ParentId, Parent.CaseNumber FROM CaseComment WHERE Parent.Policy_Member__c = :pmId And Parent.Status != 'Cancelled' ORDER BY Parent.LastModifiedDate DESC];
                    For (CaseComment objCaseComment : listCaseComment){                      
                        If (objCaseComment.CommentBody!= null && objCaseComment.CommentBody.contains(orderNumber.escapeHtml4()) && !mapCaseLinkByCaseId.containsKey(objCaseComment.ParentId)){
                            String strLink = '<a class="caselink" href="#" onclick="openCaseNewSubTabOrderDetails(\'' + objCaseComment.ParentId + '\', \'' + objCaseComment.Parent.CaseNumber + '\')">' + objCaseComment.Parent.CaseNumber + '</a>';
                            mapCaseLinkByCaseId.put(objCaseComment.ParentId, strLink);
                        }                        
                    }
                    
                    If (mapCaseLinkByCaseId.size() > 0){
                        string strMessage = 'This order was previously logged to the following case(s):</br>';
                        strMessage += String.join(mapCaseLinkByCaseId.values(), ',');
                        ApexPages.addMessage(New ApexPages.message(ApexPages.severity.INFO, strMessage));
                    }
                }
                
				
                source = ApexPages.currentPage().getParameters().get('source');
				source = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(source) : source;				
               if(!String.IsBlank(source)) 
               {			        
                    if(!String.IsBlank(source) && source == 'Summary' )
                    {                          
					   webOrderNumber = ApexPages.currentPage().getParameters().get('webOrderNumber');
					   webOrderNumber =  bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(webOrderNumber) : webOrderNumber;
                        
					   OcreationDate = ApexPages.currentPage().getParameters().get('OcreationDate');
					   OcreationDate = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(OcreationDate) : OcreationDate;
                       
                       OTC = ApexPages.currentPage().getParameters().get('OTC');
					   OTC = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(OTC) : OTC;
                       
                       oReleaseDate = ApexPages.currentPage().getParameters().get('oReleaseDate');
					   oReleaseDate = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(oReleaseDate) : oReleaseDate;
                       
                       CurrentQueue = ApexPages.currentPage().getParameters().get('CurrentQueue');
					   CurrentQueue = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(CurrentQueue) : CurrentQueue;
                       
                       orderSource = ApexPages.currentPage().getParameters().get('orderSource');
					   orderSource	= bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(orderSource) : orderSource;				   
                      
                    }
             }
       }
       string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
	   sBrowserContext = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sBrowserContext) : sBrowserContext;	
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
   }
     
   
   /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation getOrderData(String orderKeyValue,String source,String startDate,String endDate,String enterpriseId, string networkID, String sRecordId)
    {
        return PharmacyOrderDetail_S_HUM.invokeOrderDetail( orderKeyValue, source, startDate, endDate, enterpriseId, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object ProcessOrderResponse(List < string > labels, object State)
    {
        return PharmacyOrderDetail_S_HUM.ProcessOrderResponse(labels, State);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    Public Static Object processChildOrderDetails(List < String > labels, Object State)
    {
        return PharmacyOrderDetail_S_HUM.processChildOrder(labels, State);
    }
    
   /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Order details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation callGetOrder(String orderKeyValue,String startDate,String endDate,String enterpriseId, String CurrentQueueName, string networkID, String sRecordId)
    {
        return PharmacyOrderDetail_S_HUM.callGetOrder( orderKeyValue, startDate, endDate, enterpriseId,  CurrentQueueName, networkID, sRecordId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object processGetOrderResponse(List < string > labels, Object State)
    {
        return PharmacyOrderDetail_S_HUM.processGetOrderResponse(labels, State);
    }
    
     /*
     * Method Name : toggleCusPopFlag
     * Description : flag to show pop up for upating the section 
     * Return type : Void
     * Parameter   : NA
     */
    public void refreshDetail()
    {
       
    }
    
      /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost createEditOrder details continuation requests
     *
     * @return continuation object
     */
     @RemoteAction
    public static Continuation sendRequestReleaseAndCancelOrder(String OrderKey, String EnterpriseId, boolean isCancel, string networkID, String sRecordId,boolean overrideconsent,boolean copayconsent,string lstScriptKey)
    {

        return PharmacyOrderDetail_S_HUM.sendRequestReleaseAndCancelOrder( OrderKey,  EnterpriseId,isCancel,networkID,sRecordId,overrideconsent,copayconsent,lstScriptKey);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object  processRelOrderResponse(List < string > labels, Object State)
    {
        return PharmacyOrderDetail_S_HUM.processRelOrderResponse( labels,  State);
    }
	
	/**
	 * Returns request object of continuation object
	 * <p>
	 * Method to invoke the Get Member details continuation requests for Log Notes
	 *
	 * @return continuation object
	 */
	@RemoteAction
	public static Continuation invokeOMSLogNotes(String memID, String startDate, String endDate, string networkID, String sRecordId)
	{
		return Pharmacy_S_HUM.sendRequestGetMemberResponse(memID, startDate, endDate, networkID, sRecordId);
	}

	/**
	 * Returns responses of continuation object
	 * <p>
	 * Callback Method to parse the service response
	 *
	 * @return Object
	 */
	public static object parseRSGetMemberResponse(List < string > labels, object State)
	{
		return Pharmacy_D_HUM.parseRSGetMemberResponse(labels, State);
	}

	/**
	*  Retrieves Case Comments and Combines Both Case Comments and OMS data into Single Wrapper 
	*  @name mergeCaseCommentsOMSDataDTO
	*  @param NA
	*  @return void
	*  @throws NA
	*/
	public void mergeCaseCommentsOMSDataDTO()
	{  
		list<PharmacyOMSLogNotes_DTO_HUM> oMSNotesDTO = ( List<PharmacyOMSLogNotes_DTO_HUM> )JSON.deserialize( Apexpages.currentPage().getParameters().get('oMSNotesDTO'), List<PharmacyOMSLogNotes_DTO_HUM>.class ) ;        
		String sPolicyId = ApexPages.currentPage().getParameters().get('policyId');        
		String orderNumber = ApexPages.currentPage().getParameters().get('orderNumber');
		lstCaseComments = new list<CaseCommentsOMSNotes_DTO_HUM>();
		try
		{    
			CaseCommentsOMSNotes_DTO_HUM oCaseCommentDTO;
            If (String.isNotBlank(sPolicyId)) {
                List<CaseComment> caseCommentList = [SELECT Parent.Id, Parent.CaseNumber, CommentBody, CreatedDate, CreatedBy.Name FROM CaseComment WHERE Parent.Policy_Member__c = :sPolicyId ORDER BY CreatedDate DESC]; 
                If (caseCommentList == null || caseCommentList.size() <= 0){
                    caseCommentsFailed = true;
                }
                If(caseCommentList != null && caseCommentList.size() > 0)
                {
                    for (CaseComment cm : caseCommentList)
                    {  
                        If (cm.CommentBody != null && cm.CommentBody.contains(orderNumber) ){
                            oCaseCommentDTO = new CaseCommentsOMSNotes_DTO_HUM();
                            oCaseCommentDTO.CaseId = cm.Parent.Id;
                            oCaseCommentDTO.CaseNumber = cm.Parent.CaseNumber;
                            oCaseCommentDTO.CreatedDate = cm.CreatedDate.format('MM/dd/yyyy');
                            oCaseCommentDTO.CreatedBy = cm.CreatedBy.Name;
                            oCaseCommentDTO.Note = cm.CommentBody;
                            oCaseCommentDTO.SourceType = 'Case Comments';
                            lstCaseComments.add(oCaseCommentDTO);
                            caseCommentsExists = true;
                       }
                    }
                }
            }
			Set<string> userAlias = new Set<string>();
            for (PharmacyOMSLogNotes_DTO_HUM cm : oMSNotesDTO)
			{
				If (cm.LogNoteMessage != null && cm.LogNoteMessage.contains(orderNumber) ){
                   userAlias.add(cm.LogNoteUser); 
                }
            }
            User[] createdUser = [Select Alias,Name from User where Alias IN : userAlias];
            Map<string,string> userMap = new  Map<string,string>();
			if(createdUser.size()>0)            
            {
				for(User c : createdUser)
                {
                    userMap.put(c.Alias,c.Name);
                }
            }

			for (PharmacyOMSLogNotes_DTO_HUM cm : oMSNotesDTO)
			{
				If (cm.LogNoteMessage != null && cm.LogNoteMessage.contains(orderNumber) ){
					oCaseCommentDTO = new CaseCommentsOMSNotes_DTO_HUM();
					oCaseCommentDTO.CaseId = cm.LogNoteKey;
					oCaseCommentDTO.LogCode = cm.LogNoteCode;
					oCaseCommentDTO.CreatedDate = cm.LogNoteDate;
					If (userMap.size() > 0 && userMap.get(cm.LogNoteUser) == cm.LogNoteUser){
							oCaseCommentDTO.CreatedBy = userMap.get(cm.LogNoteUser);
					}else {
						oCaseCommentDTO.CreatedBy = cm.LogNoteUser;
					}
					oCaseCommentDTO.Note = cm.LogNoteMessage;
					oCaseCommentDTO.SourceType = 'OMS';
					lstCaseComments.add(oCaseCommentDTO);
					OMSExists = true;
				}
			}       
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'PharmacyOrderDetail_C_HUM', 'mergeCaseCommentsOMSDataDTO');
		}
    }
    /**
     * Returns request object of continuation object
     * <p>
     * Method to invoke the Epost Get Member Prescription details continuation requests
     *
     * @return continuation object
     */
    @RemoteAction
    public static Continuation invokeEpostMemberPrescription(String memID,String scriptKey, String startDate, String endDate, string networkId, String sRecordId)
    {
        return Pharmacy_S_HUM.sendRequestGetScript(memID,scriptKey, startDate, endDate, networkId, sRecordId);
    }

     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the service response
     *
     * @return Object
     */
    public static object parseRSGetScriptResponse(List < string > labels, object State)
    {
        return Pharmacy_S_HUM.parseRSGetScriptResponse(labels, State);
    }
    
}