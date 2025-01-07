/*******************************************************************************************************************************
Apex Class Name : PharmacyCaseComment_C_HUM.cls
Version         : 1.0
Created On      : April 25 2017
Function        : Class contains methods to create Case Comments for Humana Pharmacy User
TestClass       : PharmacyCaseComment_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma               87024                  04/25/2017                   Original Version
* Rajesh Keswani             97817                  05/30/2017                   REQ - 320329: ePost call for Case Comment for Humana Pharmacy
* Rajesh Keswani             112036	                07/20/2017                   Implementation of REQ - 323389 - adding networkId in service request
* Vishal Verma                                      08/09/2017                   REQ - 324819: CMO CR 5257 dynamic dropdown for log code
* Sharan Shanmugam									08/09/2017					 REQ - 324814 : Save Case Comment to CRM during ePost failure
* Melkisan Selvaraj			 341652		    		03/04/2019                   REQ - 387544 : Splunk error logging Issues
* Apurva urkude                                     04/26/2021                   REQ - 2164003 T1PRJ0002736 - 2021 MF6- TECH - SF  - CheckMarx Fix Part 2 - Pharmacy pages
* Apurva urkude              2549610                08/13/2021                   REQ - 2549610 MF6- SF -TECH Check Marx vulnerability Fix - Pharmacy Case History Section
*****************************************************************************************************************************************/

public with sharing class PharmacyCaseComment_C_HUM 
{      
    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */ 
    public PharmacyCaseComment_C_HUM(ApexPages.StandardController controller) 
    {
        objCase =  (Case)controller.getRecord();
        objCase = [select id,subject,description,CaseNumber , Account.Enterprise_Id__c, Classification_Type__c from case where id=:objCase.id];
        if (ApexPages.currentpage().getParameters().get('type') == 'New') 
        {
        	objCaseComment = new CaseComment(ParentId=objCase.id);
        }
        else
        {
        	 objCaseComment = [select id,CommentBody from caseComment where id =: ApexPages.currentpage().getParameters().get('type')];
        	 sCaseCommentBody = objCaseComment.CommentBody;
        }
        lstCaseComment = new List<CaseCommentView_DTO_HUM>();
        bHasComments= false;
        List<String> lstLogNodes = Pharmacy_H_HUM.fetchLogCode(objCase.Classification_Type__c);
        if(lstLogNodes != null && lstLogNodes.size() > 0)      	sCodeHidden =  lstLogNodes[0];
		sHiddenCaseCommentMemberNotFound = GLOBAL_CONSTANT_HUM.CASECOMMENT_MEMBERNOTFOUND_HUM;
    	sHiddenCaseCommentFaultError = GLOBAL_CONSTANT_HUM.CASECOMMENT_FAULTERROR_HUM;
    }
    
    public List<CaseCommentView_DTO_HUM> lstCaseComment {get;set;}
    public boolean bHasComments {get;set;}
    public Case objCase {get;set;}
    public CaseComment objCaseComment {get;set;}
    public String sCode {get;set;}
    public String sCodeHidden {get;set;}
    public String sCaseCommentBody {get;set;}
	public String sHiddenCaseComment {get;set;}
    public String sHiddenCaseCommentMemberNotFound {get;set;}
    public String sHiddenCaseCommentFaultError {get;set;}
    
   /**
    * Method to fetch case comment for the case
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void fetchCaseComments()
    {
        
        for(CaseComment comment : [Select LastModifiedDate, LastModifiedBy.Id, LastModifiedBy.Name,  CreatedDate, CreatedBy.Id, CreatedBy.Name, CommentBody From CaseComment c where ParentId = :objCase.id order by c.LastModifiedDate desc])
        {
            CaseCommentView_DTO_HUM objComment = new CaseCommentView_DTO_HUM();
            objComment.cComment = comment;
            objComment.commentText = '<b>Created By: <a href="#dummy" onclick="openTabforUserInfo(\'' + comment.CreatedBy.Id +'\',\'' + comment.CreatedBy.Name +'\');">' + comment.CreatedBy.Name + '</a> (' + comment.CreatedDate.format() + ')  ';
            if(comment.createddate != comment.lastmodifieddate)
            {
                objComment.commentText += ' | Last Modified By: <a href="#dummy" onclick="openTabforUserInfo(\'' + comment.LastModifiedBy.Id +'\',\'' + comment.LastModifiedBy.Name +'\');">' + comment.LastModifiedBy.Name + '</a> (' + comment.LastModifiedDate.format() + ')</b><br>';
            }
            else
            {
                objComment.commentText += '</b> <br>';
            }
            if(comment.CommentBody != NULL && comment.CommentBody.contains('(') && comment.CommentBody.contains(')'))
			{
				List<String> lstStringComment = comment.CommentBody.split('\\)');
				if(lstStringComment != NULL && !lstStringComment.isEmpty() && lstStringComment.size() > 1)
				{
					objComment.commentText += '(<b>'+ lstStringComment[0].replace('(','')+'</b>)' + lstStringComment[1];
				}
				else
				{
					objComment.commentText += comment.CommentBody;
				}
			}
			else
			{
				objComment.commentText += comment.CommentBody;
			}
            lstCaseComment.add(objComment);
        }
        bHasComments = !lstCaseComment.isEmpty() ? true : false; 	
    }
    
    /**
	* <p>
	* Method to upsert the case comment
	*
	* @param  NA
	* @return Void
	*/    
    public void saveCaseComments()
    {
        try
        {
            if(CaseComment.SObjectType.getDescribe().isCreateable()&&CaseComment.SObjectType.getDescribe().isUpdateable())
            {
        	objCaseComment.CommentBody = string.isNotBlank(sCodeHidden)  ? '('+ sCodeHidden+') '+sCaseCommentBody : sCaseCommentBody ; 
            upsert objCaseComment; 
             } 
        }
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e,'PharmacyCaseComment_C_HUM','saveCaseComments');  
        }
    }
    
    /**
	* <p>
	* Method to populate value in the hidden variable
	*
	* @param  NA
	* @return Pagereference
	*/
    public PageReference populateHiddentVal()
    {
        sCodeHidden = sCode;
        return null;
    }
    
    /**
	* Returns list of log node
	* <p>
	* Method to create list of log notes 
	*
	* @param  NA
	* @return list<SelectOption> 
	*/
    public list<SelectOption> getCaseCommentOptions()
    {
        list<SelectOption> lstSortBy = new list<SelectOption>();
        for(String code: Pharmacy_H_HUM.fetchLogCode(objCase.Classification_Type__c))
        {
            lstSortBy.add(new SelectOption(code, code));
        }
        return lstSortBy;
    }
    
    /**
	* Returns responses of continuation object
	* <p>
	* Callback Method to parse the Get log Note service response
	*
	* @param  List<String> List of Continuation Labels
	* @param  Object State of Continuation
	* @return Object
	*/
    
   public static object parseLogNoteResponse(List < string > labels, object State)
    {
    	RightSourceEditMemberResponse_DTO_HUM dtoRes;
    	boolean successFlag;
		String sNoFaultResponse = '';
    	String sFaultResponse = '';
    	List<String> lstString = new List<String>();
        try
        {
            HttpResponse logNoteResponse = Continuation.getResponse(labels[0]);
            CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitch = CRMFunctionality_ONOFF_Switch__c.getValues('CaseCommentErrorSwitch');
        	boolean switchValue = objFunctionalitySwitch != null ? objFunctionalitySwitch.IsON__c : false;
      		if(switchValue==false){
                if (logNoteResponse != null && !logNoteResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((logNoteResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || logNoteResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
                {   
                    dtoRes = (RightSourceEditMemberResponse_DTO_HUM) System.JSON.deserialize(logNoteResponse.getBody(), RightSourceEditMemberResponse_DTO_HUM.class);
                    if (null != dtoRes && null != dtoRes.EditMemberResponse && dtoRes.EditMemberResponse.ErrorDescription != GLOBAL_CONSTANT_HUM.HUM_MEMBER_NOT_FOUND)
                    {
                         if(dtoRes != null && dtoRes.EditMemberResponse != null && dtoRes.EditMemberResponse.UID =='')
                        {	
                            successFlag = true;
                            sNoFaultResponse = 'true';
                        }
                        else if(dtoRes != null && dtoRes.EditMemberResponse != null && string.isNotBlank(dtoRes.EditMemberResponse.UID))
                        {
                            Error_Log__c errorLog = new Error_Log__c(Error_Code__c = 'Sever Down Exception',
                            Error_Message__c = dtoRes.EditMemberResponse.UID,
                            Request_Sent__c = true,
                            class_name__c = 'PharmacyCaseComment_C_HUM',
                            Method_Name__c ='parseLogNoteResponse',
                            Type__c = 'Integration Error');
                            if(errorLog != null ) insert errorLog;
                            successFlag = false;  
                            sNoFaultResponse = 'false'; 
                        }
                    }
                    else{
                        sFaultResponse = 'true';
                    }
                }
            
                else
                {
                    // Fault Code
                    sFaultResponse = Pharmacy_H_HUM.logHpFaultError(logNoteResponse,'PharmacyCaseComment_C_HUM', 'parseLogNoteResponse', State != null ? String.valueof(State) : '').contains('Member not found') ? 'true' : 'false';
                }
            }
            else{
                sFaultResponse='false';
            }
			lstString.add(sNoFaultResponse);
            lstString.add(sFaultResponse);
        }
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'PharmacyCaseComment_C_HUM', 'parseLogNoteResponse');
        }
        return lstString;
    }
    
    /*
	* This method saves the HP case comment record
	* @param   NA
	* @return  void
	*/
    public pageReference saveHPCaseComment()
    {
    	
    	string bRedirect = '';
    	PageReference pageRef;
    	caseComment caseComObj;
		string bErrorCaseComment = '';
		List<CaseComment> lstCaseComment = new List<CaseComment>();
    	if (ApexPages.currentpage().getParameters().containsKey('redirectFlag'))
    	{
    		bRedirect = ApexPages.currentpage().getParameters().get('redirectFlag');
    	}
		if (ApexPages.currentpage().getParameters().containsKey('errorCaseComment'))
    	{
    		bErrorCaseComment = ApexPages.currentpage().getParameters().get('errorCaseComment');
    	}
		
		if(bErrorCaseComment == 'true')
    	{
    		sHiddenCaseComment = sHiddenCaseCommentMemberNotFound;	
    	}
    	else if(bErrorCaseComment == 'false')
    	{
    		sHiddenCaseComment = sHiddenCaseCommentFaultError;	
    	}
		
		if(string.isNotBlank(sHiddenCaseComment)) 
    	{
	     	caseComObj = new caseComment(parentId = objCase.id, commentBody = sHiddenCaseComment);
	     	if(caseComObj != null)	lstCaseComment.add(caseComObj);
    	}
    	
    	if(string.isNotBlank(sCaseCommentBody) && string.isNotBlank(sCodeHidden)) 
    	{
	     	caseComObj = new caseComment(parentId = objCase.id, commentBody = '('+sCodeHidden+') '+sCaseCommentBody);
			if(caseComObj != null)	lstCaseComment.add(caseComObj);
    	}
	SObjectAccessDecision securityDecision = Security.stripInaccessible(AccessType.CREATABLE,lstCaseComment);
         lstCaseComment= securityDecision.getRecords();      	
	    if(!lstCaseComment.isEmpty())
	    {
	    	insert lstCaseComment; 
	    	if(bRedirect == 'true')
	    	{
		    	pageRef = new PageReference('/'+objCase.id);
			    pageRef.setRedirect(true);
	    	}
    	}
    	return pageRef;
    }
    
    /**
	* Returns continuation object
	* <p>
	* Method to call continuation serive for log note.
	*
	* @param sComment                      String
	* @param sEnterpriseId                 String
	* @param sNetworkUserId                String
	* @param sRecordId                     String
	* @param sCode                         String
	* @return                              continuation
	*/
    @RemoteAction
    public static Continuation sendRequestLogNotes(String sComment, String sEnterpriseId , String sNetworkUserId , String sRecordId, String sCode, String caseId)
    {
         try
        {	
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('RighSourceService_EditMember');
            list<string> stateList = new list<string>();
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'parseLogNoteResponse';
			con.State = sRecordId;
            HttpRequest httpEditGetMemberReqObj = PharmacyCaseComment_C_HUM.builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(PharmacyCaseComment_C_HUM.RequestLogNotes(sComment,sEnterpriseId,sNetworkUserId , sCode)));
            con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyCaseComment_C_HUM', 'sendRequestLogNotes');
            return null;
        }
    }
    
    /**
	* Returns HttpRequest request with request body
	* <p>
	* Method to prepare httpRequest details and certification details
	*
	* @param  HUM_Webservice_Callout__c record .
	* @return HttpRequest
	*/
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, ServiceObj.Content_Type__c);
            request.setHeader(GLOBAL_CONSTANT_HUM.Connection_HUM, GLOBAL_CONSTANT_HUM.Keep_alive_HUM);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(GLOBAL_CONSTANT_HUM.SOAPAction_HUM,'');
        }
        return request;
    } 
    
    /**
	* Returns eedit member log note struture request  object
	* <p>
	* Method to call create structure  for Edit member log note call .
	*
	* @param sComment                      String
	* @param sEnterpriseId                 String
	* @param sNetworkUserId                String
	* @param sCode                         String
	* @return                              request structure for log note
	*/
    public static RightSourceEditMemberRequest_DTO_HUM RequestLogNotes(String sComment, String sEnterpriseId , String sNetworkUserId , String sCode)
    {
        RightSourceEditMemberRequest_DTO_HUM oRequest = new RightSourceEditMemberRequest_DTO_HUM();
        RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest getEditMembReqObj = new RightSourceEditMemberRequest_DTO_HUM.GetEditMemberRequest();
        RightSourceEditMemberRequest_DTO_HUM.memberDetail memDetailObj = new RightSourceEditMemberRequest_DTO_HUM.memberDetail();
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetail logNote = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetail();
        logNote.logNoteCode = sCode;
        logNote.logNoteDate = system.now().format('MM/dd/yyyy ');
        logNote.logNoteKey = String.valueof(GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_MINUSONE_HUM);
        logNote.logNoteMessage = sComment;
        logNote.logNoteUser = sNetworkUserId;
        List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail>  lstLogNote = new List<RightSourceEditMemberRequest_DTO_HUM.logNoteDetail> ();
        lstLogNote.add(logNote);
        RightSourceEditMemberRequest_DTO_HUM.logNoteDetails logNotesObject = new RightSourceEditMemberRequest_DTO_HUM.logNoteDetails ();
        logNotesObject.logNote = lstLogNote;
        memDetailObj.logNotes   = logNotesObject; 
        memDetailObj.EnterprisePersonID = sEnterpriseId;
        getEditMembReqObj.members = new List<RightSourceEditMemberRequest_DTO_HUM.memberDetail>();
        getEditMembReqObj.CustomerServiceId = sNetworkUserId;
        getEditMembReqObj.members.add(memDetailObj);
        oRequest.EditMemberRequest = getEditMembReqObj;
        return oRequest;
    }         
}