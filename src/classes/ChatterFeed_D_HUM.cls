/******************************************************************************************************************
Apex class Name    : ChatterFeed_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for chatter feed related activities
Created On         : May 12 2021
Test Class         : ChatterService_T_HUM

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        05/12/2021          Original Version
*   1.1            Moshitha Gunasekaran        06/08/2021          Made changes as per latest ICD to add FeedItemId and CarePlanId functionalities
*   1.2            Moshitha Gunasekaran        07/09/2021          User Story 2464051:  T1PRJ0002081 / PR00092569 **Coaching** SF - TECH � Digital Messaging �Updates
*   1.3			   Moshitha Gunasekaran        07/27/2021          Made changes as per latest ICD to replace CarePlanId with Subject functionalities
*   1.4            Moshitha Gunasekaran        08/04/2021          User Story 2495909:  T1PRJ0002081 / PR00092569 **Coaching** Out Of Office feature in Chatter
*   1.5            Moshitha Gunasekaran        11/11/2021          User Story 2864451 - T1PRJ0002081 / PR00092569 - PLT - TECH- UI - **Coaching ** - PLT - Mobile - Feed item not associated with the Coach
*   1.6            Manjit Singh                04/07/2023          User Story 3922786 - T1PRJ0307696: Interactions for Chatter Feed Comments
------------------------------------------------------------------------------------------------------------------- ------------------------------------------------------------------------------------------*/
global with sharing class ChatterFeed_D_HUM {

    public static string CoachFeedElementType = 'Coach Message';
    public static string GroupFeedElementType = 'Enrollment & Generic';
    public static boolean isCreateInteraction = false;
    public static list<String> lstCaseStatus = New List<String>{'Pending - Unassigned','Pending - Response','In Progress'}; 
    
     /*
     * Method name : createChatterRequest
     * Description : This method is to create chatter in the feed based on inputs from Go365
     * Return Type : ConnectApi.FeedElement
     * Parameter   : ChatterRequestDTO
     */
    public static ChatterResponseDTO createChatterRequest(ChatterRequestDTO chatterRequest)
    {
        
        ChatterResponseDTO chatterResponse = new ChatterResponseDTO();        
                
        if(string.isNotBlank(chatterRequest.FeedElementType) && CoachFeedElementType.equalsIgnoreCase(chatterRequest.FeedElementType) && string.isNotBlank(chatterRequest.Subject))
        {
            isCreateInteraction = true;
        }

        /*FeedElementType should Not be Blank And FeedElementType should be Enrollment & Generic And FeedItemId should Not be blank And PersonID should Not be blank.*/
        if (String.isNotBlank(chatterRequest.FeedElementType) && GroupFeedElementType.equalsIgnoreCase(chatterRequest.FeedElementType) && String.isNotBlank(chatterRequest.FeedItemId) && String.isNotBlank(chatterRequest.PersonId))
        {
            isCreateInteraction = true;
        }
        
        try
        {
            list<Account> lstCoachingMembers = new list<Account>();
            if(string.isNotBlank(chatterRequest.PersonId) && string.isNotBlank(chatterRequest.FeedElementType))
            {
                lstCoachingMembers = [SELECT Id, Enterprise_ID__c,ETL_Record_Deleted__c FROM Account WHERE Enterprise_ID__c = :chatterRequest.PersonId and ETL_Record_Deleted__c = false];
             
                list<CollaborationGroup> coachingGroups = null;
                list<case> coachingUsers = null;
                
                if(null != lstCoachingMembers && !lstCoachingMembers.isEmpty())
                {
                    if (!Test.isRunningTest())
                    {
                        coachingGroups = [SELECT Id, Name FROM CollaborationGroup where Name = :GLOBAL_CONSTANT_CH_HUM.CoachingAdministrationGroup] ;
                    }
                    else
                    {
                        coachingGroups = [SELECT Id, Name FROM CollaborationGroup where Name = 'CoachingTesting'] ;
                    }
                    //If FeedElementType is Passed as Coach Message then retrieve Coach name from Subject
                    if(CoachFeedElementType.equalsIgnoreCase(chatterRequest.FeedElementType) && string.isNotBlank(chatterRequest.Subject))
                    {
                        coachingUsers = [SELECT Id, Area_of_Focus__c, AccountId, OwnerId, Owner.Name, Status FROM Case where Area_of_Focus__c = :chatterRequest.Subject and AccountId = :lstCoachingMembers[0].Id and Status in :lstCaseStatus order by createddate desc limit 1] ;
                    }
                                    
                    //If FeedElementType is Passed as Enrollment
                    if(GroupFeedElementType.equalsIgnoreCase(chatterRequest.FeedElementType))
                    {
                        //If FeedElementType is Passed as Enrollment and FeedItemId is not passed then Post Message as FeedItem by tagging Group Name.
                        if(string.isBlank(chatterRequest.FeedItemId))
                        {
                            if(null!= coachingGroups && !coachingGroups.isEmpty())
                            {
                                chatterResponse = PostFeedItem(coachingGroups[0].Id, lstCoachingMembers[0].Id, chatterRequest.MessageText, false, chatterRequest.Subject);
                            }
                        }
                        //If FeedElementType is Passed as Enrollment and FeedItemId is passed then Post Message as comment to FeedItem by tagging Coach Name retrieved from FeedItem or FeedComments.
                        else
                        {
                            //If FeedItem is created by Member then check is made to verify if there is any FeedComment made by Coach to tag the comment to corresponding Coach.
                            list<FeedItem> feedItemList = [SELECT Id, Body, CreatedById, CreatedBy.Name, (SELECT Id, CreatedDate, CreatedById, CreatedBy.Name FROM FeedComments order by createddate desc) FROM Feeditem where id = :chatterRequest.FeedItemId limit 1];
                            string sCoachId = '';
                            boolean isCoach = false;
                            if(null != feedItemList && !feedItemList.isEmpty())
                            {
                                if(GLOBAL_CONSTANT_CH_HUM.DatapowerUserName.equalsIgnoreCase(feedItemList[0].CreatedBy.Name))
                                {
                                    if(null != feedItemList[0].FeedComments && !feedItemList[0].FeedComments.isEmpty())
                                    {
                                        for(FeedComment ofeedComment :feedItemList[0].FeedComments)
                                        {
                                            if(!GLOBAL_CONSTANT_CH_HUM.DatapowerUserName.equalsIgnoreCase(ofeedComment.CreatedBy.Name))
                                            {
                                                sCoachId = ofeedComment.CreatedById;
                                                break;
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    if (!Test.isRunningTest()) sCoachId = feedItemList[0].CreatedById;
                                }
                                
                                //If no coach is tagged to the message then tag the message to group for any comments
                                if(string.isBlank(sCoachId))
                                {
                                    if(feedItemList[0].Body.indexOf('CarePlanSubject:') != -1)
                                    {
                                        string carePlanSub = feedItemList[0].Body.substringAfter('CarePlanSubject:') ;
                                        if(string.isNotBlank(carePlanSub))
                                        {
                                            coachingUsers = [SELECT Id, Area_of_Focus__c, AccountId, OwnerId, Owner.Name, Status FROM Case where Area_of_Focus__c = :carePlanSub and AccountId = :lstCoachingMembers[0].Id and Status in :lstCaseStatus order by createddate desc limit 1] ;
                                            if(null!= coachingUsers && !coachingUsers.isEmpty())
                                            {       
                                                sCoachId = coachingUsers[0].OwnerId;
                                            }
                                        }
                                    }
                                    if((string.isBlank(sCoachId)) && null!= coachingGroups && !coachingGroups.isEmpty())
                                    {
                                        sCoachId = coachingGroups[0].Id;
                                    }
                                }
                                else
                                    isCoach = true;
                            }
                            chatterResponse = PostFeedComment(lstCoachingMembers[0].Id, sCoachId, chatterRequest.FeedItemId, chatterRequest.MessageText, isCoach);
                        }
                    }
                    //If FeedElementType is Passed as Coach Message and Suject is sent
                    else if(CoachFeedElementType.equalsIgnoreCase(chatterRequest.FeedElementType))
                    {
                        //If FeedItemId is not passed then Post the message as FeedItem and tag the Coach Name who is the owner of the CarePlan.
                        if(string.isBlank(chatterRequest.FeedItemId))
                        {
                            if(null!= coachingUsers && !coachingUsers.isEmpty())
                            {       
                                chatterResponse = PostFeedItem(coachingUsers[0].OwnerId, lstCoachingMembers[0].Id, chatterRequest.MessageText, true, chatterRequest.Subject);
                            }
                            else
                            {
                                chatterResponse = PostFeedItem(coachingGroups[0].Id, lstCoachingMembers[0].Id, chatterRequest.MessageText, false, chatterRequest.Subject);
                            }
                        }
                        //If FeedItemId is passed then validate if CarePlan Owner Name and FeedItem or FeedComment Coach Name is same.
                        //If same Post the message as FeedComment for the FeedItem and tag the Coach Name who is the owner of the CarePlan. 
                        //If Coach Name and CarePlanOwner Name is not same then error is thrown.
                        else
                        {
                            list<FeedItem> feedItemList = [SELECT Id, Body, CreatedById, CreatedBy.Name, (SELECT Id, CreatedDate, CreatedById, CreatedBy.Name FROM FeedComments order by createddate desc) FROM Feeditem where id = :chatterRequest.FeedItemId limit 1];
                            string sCoachId;
                            if(null != feedItemList && !feedItemList.isEmpty())
                            {
                                if(GLOBAL_CONSTANT_CH_HUM.DatapowerUserName.equalsIgnoreCase(feedItemList[0].CreatedBy.Name))
                                {
                                    for(FeedItem oFeedItem :feedItemList)
                                    {
                                        if(null != oFeedItem.FeedComments && !oFeedItem.FeedComments.isEmpty())
                                        {
                                            for(FeedComment ofeedComment :oFeedItem.FeedComments)
                                            {
                                                if(!GLOBAL_CONSTANT_CH_HUM.DatapowerUserName.equalsIgnoreCase(ofeedComment.CreatedBy.Name))
                                                {
                                                    sCoachId = ofeedComment.CreatedById;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    if (!Test.isRunningTest()) sCoachId = feedItemList[0].CreatedById;
                                }

                                //If no coach is tagged to the message then tag the message to owner of CarePlan
                                if(string.isBlank(sCoachId))
                                {
                                    if(null!= coachingUsers && !coachingUsers.isEmpty())
                                    {
                                        sCoachId = coachingUsers[0].OwnerId;
                                    }
                                    else
                                    {
                                        if(feedItemList[0].Body.indexOf('CarePlanSubject:') != -1)
                                        {
                                            string carePlanSub = feedItemList[0].Body.substringAfter('CarePlanSubject:') ;
                                            if(string.isNotBlank(carePlanSub))
                                            {
                                                coachingUsers = [SELECT Id, Area_of_Focus__c, AccountId, OwnerId, Owner.Name, Status FROM Case where Area_of_Focus__c = :carePlanSub and AccountId = :lstCoachingMembers[0].Id and Status in :lstCaseStatus order by createddate desc limit 1] ;
                                                if(null!= coachingUsers && !coachingUsers.isEmpty())
                                                {       
                                                    sCoachId = coachingUsers[0].OwnerId;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(string.isNotBlank(sCoachId))
                            {
                                chatterResponse = PostFeedComment(lstCoachingMembers[0].Id, sCoachId, chatterRequest.FeedItemId, chatterRequest.MessageText, true);
                            }
                            else
                            {
                                chatterResponse.Success = false;
                                chatterResponse.ErrorMessage = 'CarePlanSubject and FeedItem Coach owners are not same. Please validate and send the message again.';
                            }
                        }
                    }
                }
                else
                {
                    chatterResponse.Success = false;
                    chatterResponse.ErrorMessage = GLOBAL_CONSTANT_CH_HUM.PersonIdErrorMessage;
                }
            }
            else
            {
                chatterResponse.Success = false;
                chatterResponse.ErrorMessage = 'Pass mandatory field values';
            }
           
        }
        catch(Exception ex)
        {
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'ChatterFeed_D_HUM','createChatterRequest');
            
            if(error!=null) 
            { 
                HUMExceptionHelper.saveLog(new List<Error_Log__c>{error}, false);
            }
            chatterResponse.Success = false;
            chatterResponse.ErrorMessage = ex.getMessage(); 
        }
        return chatterResponse;
    }
    
    /*
    * Method name : CreateDigitalInteraction
    * Description : This method is to create interaction for the feeditem when Member tags a coach. 
    *               For same member feeditem should be created only once in 24 hours. 
    * Parameter   : String
    */
    @TestVisible 
    private static void CreateDigitalInteraction(string feedItemParentId)
    {
        Id interactionRecordType = Schema.SObjectType.Interaction__c.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_CH_HUM.CoachingInteractionRecordType).getRecordTypeId();
        list<Interaction__c> lstInteractions = [select Id, Interacting_With__c,RecordTypeId from Interaction__c where Interacting_With__c = :feedItemParentId and RecordTypeId = :interactionRecordType and CreatedDate = Today and CreatedBy.Name = :GLOBAL_CONSTANT_CH_HUM.DatapowerUserName];
        if(lstInteractions == null || (lstInteractions != null && lstInteractions.isEmpty()))
        {
            Interaction__c oInteraction = new Interaction__c();
            ointeraction.Interacting_With__c = feedItemParentId;
            ointeraction.Interacting_With_type__c = HUMConstants.HumMemberRecordType;
            ointeraction.Interaction_Origin__c = GLOBAL_CONSTANT_CH_HUM.IncomingDigitalOrigin;
            ointeraction.Created_By_Queue__c  = GLOBAL_CONSTANT_CH_HUM.CoachingCreatedQueue;
            ointeraction.RecordTypeId  = interactionRecordType; 
            insert ointeraction;
        }
    }
    
    /** 
    * Method name : PostFeedItem
    * Description : This method is to post the feeditem when Member doesnt pass FeedElementId. 
    *               For same member interaction should be created only once in 24 hours. 
    * Parameter   : Id, Id, String
    * Return type : ChatterResponseDTO
    */
    private static ChatterResponseDTO PostFeedItem(Id mentionId, Id subjectId, string messageText, boolean isCoach, string carePlanSubject)
    {
        ChatterResponseDTO chatterResponse = new ChatterResponseDTO();
        ConnectApi.MessageBodyInput messageBodyInput = new ConnectApi.MessageBodyInput();
        ConnectApi.FeedItemInput feedItemInput = new ConnectApi.FeedItemInput();
        ConnectApi.TextSegmentInput textSegmentTitle = new ConnectApi.TextSegmentInput();
        messageBodyInput.messageSegments = new List<ConnectApi.MessageSegmentInput>();
        textSegmentTitle.text = GLOBAL_CONSTANT_CH_HUM.CoachingSubject;
        messageBodyInput.messageSegments.add(textSegmentTitle);
        ConnectApi.MarkupBeginSegmentInput markupBeginSegment = new ConnectApi.MarkupBeginSegmentInput();
        ConnectApi.MarkupEndSegmentInput markupEndSegment;
        markupBeginSegment.markupType = ConnectApi.MarkupType.Paragraph;
        messageBodyInput.messageSegments.add(markupBeginSegment);
        ConnectApi.MentionSegmentInput mentionSegmentInput = new ConnectApi.MentionSegmentInput(); 
        ConnectApi.TextSegmentInput textSegmentInput = new ConnectApi.TextSegmentInput();
        mentionSegmentInput.id = mentionId;
        messageBodyInput.messageSegments.add(mentionSegmentInput);
        textSegmentInput.text = ' ';
        messageBodyInput.messageSegments.add(textSegmentInput);
        
        if(string.isNotBlank(messageText))
        {
            textSegmentInput = new ConnectApi.TextSegmentInput();
            textSegmentInput.text = messageText;
            messageBodyInput.messageSegments.add(textSegmentInput);
        }
        
        
        markupEndSegment = new ConnectApi.MarkupEndSegmentInput();
        markupEndSegment.markupType = ConnectApi.MarkupType.Paragraph;
        messageBodyInput.messageSegments.add(markupEndSegment);
        if (string.isNotBlank(carePlanSubject))
        {
            textSegmentInput = new ConnectApi.TextSegmentInput();
            textSegmentInput.text = 'CarePlanSubject:' +  carePlanSubject;
            messageBodyInput.messageSegments.add(textSegmentInput);
        }
        feedItemInput.subjectId = subjectId;
        
        feedItemInput.body = messageBodyInput;
        feedItemInput.feedElementType = ConnectApi.FeedElementType.FeedItem;
        
        ConnectApi.FeedElement feedElementResponse = null;
                
        if (!Test.isRunningTest())
        {
            feedElementResponse = ConnectApi.ChatterFeeds.postFeedElement(Network.getNetworkId(), feedItemInput);
        }
        
        if(feedElementResponse != null && string.isNotBlank(feedElementResponse.Id))
        {
            if(isCreateInteraction && null != feedElementResponse.Parent && string.isNotBlank(feedElementResponse.Parent.Id))
            {
                CreateDigitalInteraction(feedElementResponse.Parent.Id);
            }
            if(isCoach)
            {
                postOOOFeedComment(subjectId, mentionId, feedElementResponse.Id);
            }
            chatterResponse.Success =  true;
            chatterResponse.FeedItemId =  feedElementResponse.Id;
        }

        return chatterResponse;

    }

    /*
    * Method name : PostFeedComment
    * Description : This method is to post the feeditem when Member pass FeedElementId. 
    *               For same member interaction should be created only once in 24 hours. 
    * Parameter   : Id, Id, Id, String
    * Return type : ChatterResponseDTO
    */
    private static ChatterResponseDTO PostFeedComment(Id subjectId, string coachId, Id feedItemId, string messageText, boolean isCoach)
    {
        ChatterResponseDTO chatterResponse = new ChatterResponseDTO();
        ConnectApi.MessageBodyInput messageBodyInput = new ConnectApi.MessageBodyInput();
        messageBodyInput.messageSegments = new List<ConnectApi.MessageSegmentInput>();
        ConnectApi.CommentInput feedCommentInput = new ConnectApi.CommentInput();
        ConnectApi.EntityLinkSegmentInput entityLinkSegmentInputContact = new ConnectApi.EntityLinkSegmentInput();
        entityLinkSegmentInputContact.entityId = subjectId;   
        messageBodyInput.messageSegments.add(entityLinkSegmentInputContact);
        ConnectApi.TextSegmentInput textSegmentInput = new ConnectApi.TextSegmentInput();
        textSegmentInput.text = ':- ';
        messageBodyInput.messageSegments.add(textSegmentInput);
        
        if(string.isNotBlank(coachId))
        {
            ConnectApi.MentionSegmentInput mentionSegmentInput = new ConnectApi.MentionSegmentInput();
            textSegmentInput = new ConnectApi.TextSegmentInput();
            mentionSegmentInput.id = coachId;
            messageBodyInput.messageSegments.add(mentionSegmentInput);
            textSegmentInput.text = ' ';
            messageBodyInput.messageSegments.add(textSegmentInput);
        }
                
        textSegmentInput = new ConnectApi.TextSegmentInput();
        if(string.isNotBlank(messageText))
        {
            textSegmentInput.text = messageText;
        }
        messageBodyInput.messageSegments.add(textSegmentInput);
                                
        feedCommentInput.body = messageBodyInput;
        ConnectApi.Comment feedCommentResponse = null;
        
        feedCommentResponse = ConnectApi.ChatterFeeds.postCommentToFeedElement(Network.getNetworkId(),feedItemId, feedCommentInput,null);
        
        if(feedCommentResponse != null && string.isNotBlank(feedCommentResponse.Id))
        {
            if(isCreateInteraction)
            {
                CreateDigitalInteraction(subjectId);
            }
            if(isCoach && string.isNotBlank(coachId))
            {
                postOOOFeedComment(subjectId, coachId, feedItemId);
            }
            chatterResponse.Success =  true;
            chatterResponse.FeedItemId = feedItemId;
        }

        return chatterResponse;
    }

    /*
    * Method name : postOOOFeedComment
    * Description : This method is to post the Feedcomment when Coach is OOO Comment 
                    will be added to the Feed and notification will be sent to Member. 
    * Parameter   : Id, Id, Id, String
    * Return type : ChatterResponseDTO
    */
    @TestVisible 
    private static void postOOOFeedComment(Id subjectId, string coachId, Id feedItemId)
    {
        if(string.isNotBlank(coachId))
        {
            boolean bSwitch_2377268 =  HUMUtilityHelper.isCRMFunctionalityON('2377268');
            list<OutOfOffice> lstOOOUsers = [SELECT Id, Message, StartDate, EndDate, UserId, IsEnabled FROM OutOfOffice where UserId = :coachId and IsEnabled = true limit 1];
            if(null != lstOOOUsers && !lstOOOUsers.isEmpty())
            {
                FeedComment feedComt = new FeedComment();
                feedComt.FeedItemId = feedItemId;
                feedComt.CreatedById = coachId;
                feedComt.IsRichText = false;
                
                
                if(string.isNotBlank(lstOOOUsers[0].Message))
                {
                    feedComt.CommentBody = lstOOOUsers[0].Message;
                }
                                        
                feedComt.CommentType  = 'TextComment';
                Database.SaveResult result = Database.insert(feedComt, false);
                if(result.isSuccess())
                {
                    if(bSwitch_2377268)
                    {
                        PushNotification_S_HUM.callPushNotificationService(subjectId, result.getId(), 'FeedComment');
                    }
                }
            }
            
        }
    }
    
}