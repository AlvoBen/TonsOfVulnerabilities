/************************************************************************************************
Apex Class Name  : HUMFeedCommentTriggerHelper
Version          : 1.0
Created Date     : Oct 21,2020
Function         : Holds the logic for trigger events on FeedComment Object
Test Class       : 
**************************************************************************************************
Modification Log:
* Developer Name              Code Review                  Date                       Description
*------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                                            10/21/2020              Added a method to call the ehub service call
* Moshitha Gunasekaran           				            06/18/2021              User Story 2377268:  T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365
***************************************************************************************************/
public class HUMFeedCommentTriggerHelper {

/*
* Method Name : onDirectMessageAttchment
* Description : Method to initiate the webservice request when Attachment record is inserted
* Return type : void
* Paramater   : FeedComment
*/
    public static void onDirectMessageAttchment(FeedComment msgWithAttachment,string communityId)
    {        
        Network myCommunity ;
        string urlPath;
        if(communityId != null){
        myCommunity = [SELECT Id, Name, UrlPathPrefix FROM Network where id =:communityId limit 1];
            urlPath =  myCommunity.UrlPathPrefix;
        }
        //string urlPath =  myCommunity.UrlPathPrefix;
        String sUser = [Select Network_User_Id__c from User where id = : UserInfo.getUserId()].Network_User_Id__c;
        ContentVersion Cv = [SELECT Id, ContentDocumentId, VersionData FROM ContentVersion where id =: msgWithAttachment.RelatedRecordId];
        ContentDocument Cd = [SELECT Id, CreatedById, Title, FileExtension FROM ContentDocument where id =: Cv.ContentDocumentId];
        String sBody = EncodingUtil.base64Encode(Cv.VersionData);
        id recTypeId = [Select Id,name From RecordType where sobjecttype =: CommunityConstants_C_HUM.COMMUNITY_ATTACHMENT_OBJECT and DeveloperName =: CommunityConstants_C_HUM.COMMUNITY_ATTCHMENT_DIRECTMESSAGE_RECORDTYPE limit 1].id;
        ComDirectMsg_Attachment__c DirectMsgatt = new ComDirectMsg_Attachment__c ();
        DirectMsgatt.Name =  Cd.Title + '.' + Cd.FileExtension ;
        DirectMsgatt.Document_Source__c = CommunityConstants_C_HUM.DOCUMENT_SOURCE_DIRECTMESSAGE;
        DirectMsgatt.RecordTypeId = recTypeId;
        DirectMsgatt.Related_DirectMessage__c = msgWithAttachment.id;
        insert DirectMsgatt;
        String BaseUrl= System.URL.getSalesforceBaseURL().toExternalForm();
        if(!Test.isRunningTest())
        {
            sendRequestToEhub(Cd.Id,DirectMsgatt.Id ,sBody, DirectMsgatt.Name ,sUser,msgWithAttachment.Id,urlPath );
        }
        
    }

    /*
    * Method Name : chatterFeedCommentNotification
    * Description : Method to initiate the webservice request when FeedComment record is inserted for Coaching
    * Return type : void
    * Paramater   : FeedComment
    */
    public static void chatterFeedCommentNotification(FeedComment ofeedComment,string communityId)
    {
        try
		{
            List<CollaborationGroupMember> collabGrpMembers;
            if(!Test.isRunningTest())
            {
                collabGrpMembers = [SELECT Id, CollaborationGroupId, MemberId, CollaborationGroup.Name FROM CollaborationGroupMember where CollaborationGroup.Name = :GLOBAL_CONSTANT_CH_HUM.CoachingAdministrationGroup and MemberId = :ofeedComment.CreatedById limit 1];
            }
            else
            {
                collabGrpMembers = [SELECT Id, CollaborationGroupId, MemberId, CollaborationGroup.Name FROM CollaborationGroupMember where CollaborationGroup.Name = 'CoachingTesting' and MemberId = :ofeedComment.CreatedById limit 1];
            }
            if(null != collabGrpMembers && !collabGrpMembers.isEmpty())
            {
                String feedElementId = ofeedComment.Id;
                ConnectApi.Comment feedCommentValue = ConnectApi.ChatterFeeds.getComment(communityId, ofeedComment.Id);
                postChatterWithSubject(feedCommentValue, feedElementId);
            }
        }
        catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'HUMFeedCommentTriggerHelper', 'chatterFeedCommentNotification');
		}
    }
    /*
* Method Name : sendRequestToEhub
* Description : Method to invoke the webservice request . This method is a future method as we are invoking a webservice from trigger
* Return type : Void
* Paramater   : String, String , String , String , String 
*/
    @TestVisible
    @future(callout = true) 
    private static void sendRequestToEhub(String attachmentStdId,String sParentId , String sBody , String sName , String sUser,Id FeedCommentId,String urlPath)
    {        
        try
        {           
            Domain domainName = [SELECT Id, HttpsOption, Domain FROM Domain where HttpsOption =: HumConstants.HUMCommunity limit 1];            
            HUM_Webservice_Callout__c attachementServiceConfigObj = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.MRCALLOUT_SERVICE_NAME);
            
            //Ignored calling callout utlity which help to increase heap size also help avoid creating additional objects and bytes creation 
            HttpRequest req = new HttpRequest();
            req.setMethod(attachementServiceConfigObj.Request_Type__c);  
            req.setHeader(Label.HUMServiceTesterController_content_type, attachementServiceConfigObj.Content_Type__c);            
            req.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
            
            //Creating DTO is adding additional heapsize, hence ignored the DTO creation for this add document request.
            req.setBody('{"AddDocument":{"AddDocumentRequest":{"SysCode":"'+CommunityConstants_C_HUM.DIRECTMESSAGE_SYSCODE+'","FileName":"'+sName+'","DocId":"'+GLOBAL_CONSTANT_HUM.MRDOCID+'","File":"'+sBody +'"}}}');
            req.setTimeout(Integer.valueOf(attachementServiceConfigObj.Timeout__c));           
            req.setClientCertificateName(attachementServiceConfigObj.Certificate_Name__c); 
            req.setEndpoint(attachementServiceConfigObj.End_Point_URL__c);  
            Http http = new Http();
            HttpResponse response;
            if(!Test.isRunningTest())
        	response =http.send(req);
            else {
                 response = new HttpResponse();
                response.setHeader('Content-Type', 'application/json');
                response.setBody('{ "AddDocument":{ "AddDocumentResponse":{ "Action":"http://test.com", "AddDocumentResultDTO":{ "Status":"Success", "DocId":"999999", "Version":"1", "StatusDescription":"Document added successfully." } } } }');
                response.setStatusCode(200);
            }
            EhubAttachmentResponse_DTO_HUM documentDTO= (EhubAttachmentResponse_DTO_HUM)JSON.deserialize(response.getbody(),EhubAttachmentResponse_DTO_HUM.class);  
            String DocID = documentDTO.AddDocument.AddDocumentResponse.AddDocumentResultDTO.DocId;
            String VersionId = documentDTO.AddDocument.AddDocumentResponse.AddDocumentResultDTO.Version;           
            String BaseUrl = 'https://'+ domainName.Domain +'/'+urlPath ;           
            String VFPageURL= BaseUrl +'/EhubDocumentViewer_VF_HUM'+ '?DocId=' + DocId + '&VId=' + VersionId;
            
            FeedComment Fc = [SELECT Id, FeedItemId, ParentId, CreatedById,CommentBody,CreatedDate, RelatedRecordId FROM FeedComment where id =: FeedCommentId];
            String AttachmentUrl = '<html><a href="'+VFPageURL+'">Click here to view your document</a></html>';
            if(Fc.CommentBody !=Null){
                Fc.CommentBody = Fc.CommentBody + ' ' + AttachmentUrl;
            }
            else
            {
                Fc.IsRichText = true;
                Fc.CommentBody = AttachmentUrl; 
            }
            Update Fc;
            
            List<ComDirectMsg_Attachment__c> lstatt = [Select Id, Version_Id__c,DocId__c,Attachment_URL__c from ComDirectMsg_Attachment__c where Id =  :sParentId];
            for(ComDirectMsg_Attachment__c attObj : lstatt)
            {
                attObj.Version_Id__c = VersionId ;
                attObj.DocId__c = DocID ; 
                attObj.Attachment_URL__c = VFPageURL;                
            }
            
            Database.SaveResult[] Result=Database.update(lstatt,false);
            if(attachmentStdId!=NUll && ContentDocument.sobjecttype.getdescribe().isdeletable())
            { 
                
                Database.delete([Select Id from ContentDocument where Id= :attachmentStdId],false);
            }
            
            
            
        } catch(exception ex)
        {            
            HUMExceptionHelper.logErrors(ex,'HUMFeedCommentTriggerHelper','sendRequestToEhub');             
        }      
    }

    /*
	* Method name : postChatterWithSubject
	* Description : This method will be used to send notification to Go365 for FeedComment
	* Return Type : void
	* Parameter   : ConnectApi.Comment feedvalue
	*/
	@TestVisible 
	private static void postChatterWithSubject(ConnectApi.Comment feedvalue, String feedCommentId)
	{
		String sPersonAccountId = '';
		//Get the feed element based on the type of message segment added
		List<ConnectApi.MessageSegment> segments = feedvalue.body.messageSegments;
		integer leng = segments.size();
		for(integer i=0; i < leng;i++)
		{
			if(segments.get(i) instanceof ConnectApi.EntityLinkSegment)
			{
				ConnectApi.EntityLinkSegment entiyval = (ConnectApi.EntityLinkSegment) segments.get(i);
				if(null != entiyval.reference)
				{
					sPersonAccountId = entiyval.reference.Id;
				}
			}
		}
		if(String.isNotBlank(sPersonAccountId))
		{
			PushNotification_S_HUM.callPushNotificationService(sPersonAccountId, feedCommentId, 'FeedComment');
		}
	}
}