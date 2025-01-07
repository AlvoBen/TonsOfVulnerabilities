/*******************************************************************************************************************************
Apex Class Name : HUMFeedCommentTrigger 
Version         : 1.0
Created On      : 07/01/2020
Function        : This trigger is used to mask the PHI information commented by the user on the Community Posts
Test Class      : CommunityHandleTriggers_T_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                                       07/01/2020                Created to mask the PHI data in FeedComment in Humana Community
* Shreya Agrawal                                        11/04/2020                Updated to call the eHub service when an attachment is uploaded on Direct message
* Moshitha Gunasekaran           				       06/18/2021                User Story 2377268:  T1PRJ0002081 / PR00092569 - MF #1799660  - SF - **Coaching ** - Push Notification to Go365
**************************************************************************************************************************************/


trigger HUMFeedCommentTrigger on FeedComment (after insert,before insert, before update) 
{
    public String sFeedParentId = '';    
    
    if(Trigger.isBefore && (Trigger.isInsert || Trigger.isUpdate))
    {
        CommunityDataMasking_H_HUM dataMaskUtility= new CommunityDataMasking_H_HUM();
        dataMaskUtility.dataMaskingControllerForFeedComments(trigger.new);
    }
    
    
    else if(Trigger.isAfter && Trigger.isInsert)
    {   
        
        boolean bSwitch_2377268 =  HUMUtilityHelper.isCRMFunctionalityON('2377268');
        HUMTriggerSwitch__c objTriggerSwitch = HUMTriggerSwitch__c.getValues('FeedComment_DirectMessage');
        if(objTriggerSwitch != NULL && objTriggerSwitch.Exeute_Trigger__c == true )
        {
            sFeedParentId = trigger.new[0].ParentId;
            if(sFeedParentId.startsWith('05y')) 
            {
                id RelatedRecordId= trigger.new[0].RelatedRecordId;
                if(trigger.new[0].CommentType=='ContentComment' && RelatedRecordId !=NULL)
                {
                    String communityId=Network.getNetworkId();
                    HUMFeedCommentTriggerHelper.onDirectMessageAttchment(trigger.new[0], communityId);
                }
            }
        }

        if(bSwitch_2377268)
        {
            string sFeedCreatedById = trigger.new[0].CreatedById;
            //When feedcomment is inserted for coaching notification call done to go365
            string profileName = HUMUtilityHelper.getCurrentUserProfileName();
            if(!profileName.equalsIgnoreCase('ETL API Access') && sFeedCreatedById.startsWith('005'))
            {
                String communityId=Network.getNetworkId();
                HUMFeedCommentTriggerHelper.chatterFeedCommentNotification(trigger.new[0], communityId);
            }
        }
        
    }
}