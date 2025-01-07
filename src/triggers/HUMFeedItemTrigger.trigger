/*******************************************************************************************************************************
Apex Class Name : HUMFeedItemTrigger 
Version         : 1.4
Created On      : 05/14/2014
Function        : This trigger is used to capture user comment from Chatter post on articles
Test Class      : HUMFeedItemTriggerTest,CommunityHandleTriggers_T_HUM

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Amit Sinha                                           05/14/2014                Created 
 * Shruthi Karanth                                      05/16/2014                Moved code to Helper class
 * Pranali Choudhari         17773                      09/03/2014                Updated code to stop bulk feeditems insert on articles
 * Palak Papneja             18737                      10/22/2014                Updated the code to fix defect 157842
 * Shruthi Karanth           18737                      10/27/2014                Removed bulk feed restriction based on review feedback
 * Shreya Agrawal                                       06/22/2020                Updated code to mask the PHI data in FeedItems posts in Humana Community
 * Shreya Agrawal										07/01/2020				  Updated code to mask the PHI data on the comments on the Posts in Humana Community
 * Moshitha Gunasekaran									05/23/2021				  US1801017 Made changes for Digital messaging to add subject line for feeditems
 **************************************************************************************************************************************/


trigger HUMFeedItemTrigger on FeedItem (after insert,before insert, before update) 
{
    public String sFeedParentId = '';
    
    if(Trigger.isAfter && Trigger.isInsert)
    {       
        sFeedParentId = trigger.new[0].ParentId;
        if(sFeedParentId.startsWith(System.Label.HUMArtUserKA) || sFeedParentId.startsWith('005'))
        { 
            HUMFeedItemHelper clsChatterHelper = new HUMFeedItemHelper();
            clsChatterHelper.afterInsertFeedItem(trigger.new);
        }
    }
    
    else if(Trigger.isBefore && (Trigger.isInsert || Trigger.isUpdate))
    {
        CommunityDataMasking_H_HUM dataMaskUtility= new CommunityDataMasking_H_HUM();
       	dataMaskUtility.dataMaskingControllerForFeedItems(trigger.new);
    }
}