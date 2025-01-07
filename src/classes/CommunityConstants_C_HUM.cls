/*******************************************************************************************************************************
Apex Class Name : CommunityConstants_C_HUM 
Version         : 1.0
Created On      : 07/27/2020
Function        : This is a Constants class for Communities.
Test Class      : 

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Veena Aphale                                        07/27/2020                    Original version
 **************************************************************************************************************************************/


public with sharing class CommunityConstants_C_HUM {
    
    public static String FEED_DETAIL = 'FeedDetail';
    public static String QUESTION_DETAIL = 'QuestionDetail';
    public static String ARTICLE_DETAIL = 'ArticleDetail';
    public static String HOME_PAGE = 'HomePage';
    public static String ARTICLE = 'article';
    public static String VIDEO = 'video';
    public static Integer QUERY_LIMIT1 = 5;
    public static Integer QUERY_LIMIT2 = 6;
    public static Integer QUERY_LIMIT3 = 3;
    Public Static final String ETL_API_ACCESS = 'ETL API Access';
    
    // Community Set up object - Record types
    public static String ANNOUNCEMENT_RECORDTYPE = 'Announcement';
    public static String DIRECT_MESSAGE_RECORDTYPE = 'Direct_Message_Setup';
    public static String TERMS_AND_CONDITIONS_RECORDTYPE = 'Terms_and_Conditions';
    public static String BANNER_IMAGE_RECORDTYPE = 'Community_Banner_Image';

    //Deactivate Community User Days
    Public Static final Integer NUMBER_LASTLOGINDAYS_HUM = -365;
    Public Static final Integer NUMBER_CREATEDDATEDAYS_HUM = -365;
    
    // Discussion Feed Constants
    public static String DISCUSSION_FEEDITEMTYPE_1 = 'TextPost';
    public static String DISCUSSION_FEEDITEMTYPE_2 = 'QuestionPost';
    public static String DISCUSSION_FEEDITEMTYPE_3 = 'LinkPost';
    public static String DISCUSSION_FEEDCOMMENTTYPE = 'TextComment'; 
    public static String DISCUSSION_FEEDPOSTTYPE_1 = 'posted a feed';
    public static String DISCUSSION_FEEDPOSTTYPE_2 = 'asked a question';
    public static String DISCUSSION_FEEDOPTION_1 = 'Latest Posts';
    public static String DISCUSSION_FEEDOPTION_2 = 'Latest Questions';
    public static String DISCUSSION_FEEDOPTION_3 = 'Posts';
    public static String DISCUSSION_FEEDOPTION_4 = 'Questions';
    public static String SORTITEM_TYPE = 'Most Recent Activity';
    public static String BUTTONNAME_LIKE = 'Like';
    public static String BUTTONNAME_LIKED = 'Liked';
    public static String DISCUSSION_FEEDUSERTYPE = 'Guest';
    public static Integer SEARCHFEED_LIMIT = 1000;
    public static String  INITAL_LOADED = '2958323';

    // Gamification constants
    public static String GAMIFICATION_LEVEL_1 = 'Novice';
    public static String GAMIFICATION_LEVEL_2 = 'Apprentice';
    public static String GAMIFICATION_LEVEL_3 = 'Contributor';
    public static String GAMIFICATION_LEVEL_4 = 'Active Contributor';
    public static String GAMIFICATION_LEVEL_5 = 'Valued Contributor';
    public static String GAMIFICATION_LEVEL_6 = 'Premiere Contributor';
    public static String GAMIFICATION_LEVEL_7 = 'Senior Contributor';
    public static String GAMIFICATION_LEVEL_8 = 'Master Contributor';
    public static String GAMIFICATION_LEVEL_9 = 'Rock Star Contributor';
    public static String GAMIFICATION_LEVEL_10 = 'Contributor Wizard';
    public static String GAMIFICATION_LEVEL_11 = 'Rookie';
    public static String GAMIFICATION_LEVEL_12 = 'Rising Star';
    public static String GAMIFICATION_LEVEL_13 = 'Pro';
    public static String GAMIFICATION_LEVEL_14 = 'Champion';
    public static String GAMIFICATION_LEVEL_15 = 'All Star';
    public static String GAMIFICATION_LEVEL_16 = 'Quite Awesome';
    public static String GAMIFICATION_LEVEL_17 = 'Legendary';
    public static String GAMIFICATION_LEVEL_18 = 'Hall of Famer';
    public static String GAMIFICATION_LEVEL_19 = 'Expert';
    public static String GAMIFICATION_LEVEL_20 = 'Mythical';
    
    public static String GAMIFICATION_NEXT_LEVEL= 'nextLevelName';
    public static String GAMIFICATION_USER_CHATTER = 'UserChatterActivity';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_LIKE_SOMETHING = 'FeedItemLikeSomething';
    public static String GAMIFICATION_LIKE_SOMETHING_COUNT = 'LikeSomethingCountRequired';
    public static String GAMIFICATION_LIKE_SOMETHING_POINTS = 'LikeSomethingPoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_ANSWER = 'FeedItemAnswerAQuestion';
    public static String GAMIFICATION_ANSWER_QUESTION_COUNT = 'AnswerAQuestionCountRequired';
    public static String GAMIFICATION_ANSWER_QUESTION_POINTS = 'AnswerAQuestionPoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_QUESTION = 'FeedItemPostQuestion';
    public static String GAMIFICATION_POST_QUESTION_COUNT = 'PostQuestionCountRequired';
    public static String GAMIFICATION_POST_QUESTION_POINTS = 'PostQuestionPoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_WRITE_POST = 'FeedItemWriteAPost';
    public static String GAMIFICATION_WRITE_POST_COUNT = 'WriteAPostCountRequired';
    public static String GAMIFICATION_WRITE_POST_POINTS = 'WriteAPostpoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_RECEIVE_LIKE = 'FeedItemReceiveALike';
    public static String GAMIFICATION_RECEIVE_LIKE_COUNT = 'ReceiveALikeCountRequired';
    public static String GAMIFICATION_RECEIVE_LIKE_POINTS = 'ReceiveALikePoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_RECEIVE_COMMENT = 'FeedItemReceiveAComment';
    public static String GAMIFICATION_RECEIVE_COMMENT_COUNT = 'ReceiveACommentCountRequired';
    public static String GAMIFICATION_RECEIVE_COMMENT_POINTS = 'ReceiveACommentPoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_WRITE_COMMENT = 'FeedItemWriteAComment';
    public static String GAMIFICATION_WRITE_COMMENT_COUNT = 'WriteACommentCountRequired';
    public static String GAMIFICATION_WRITE_COMMENT_POINTS = 'WriteACommentPoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_RECEIVE_ANSWER = 'FeedItemReceiveAnAnswer';
    public static String GAMIFICATION_RECEIVE_ANSWER_COUNT = 'ReceiveAnAnswerCountRequired';
    public static String GAMIFICATION_RECEIVE_ANSWER_POINTS = 'ReceiveAnAnswerPoints';
    
    public static String GAMIFICATION_RULE_ACTION_FEED_ITEM_BEST_ANSWER = 'FeedItemYourAnswerMarkedBest';
    public static String GAMIFICATION_BEST_ANSWER_COUNT = 'YourAnswerMarkedBestCountRequired';
    public static String GAMIFICATION_BEST_ANSWER_POINTS = 'YourAnswerMarkedBestPoints';
    
    
    public static String COMMUNITY_GO365_MEDICARE = 'Go365Medicare';
    public static String COMMUNITY_GO365_COMMERCIAL = 'Go365Commercial';
    public static String COMMUNITY_SUPPORT = 'humanasupport';
    
    public static String COMMUNITY_MASKING_WEBSITE_URL = 'Website_URL_Pattern';
    public static String COMMUNITY_ADMIN_PROFILE = 'Humana Community Admin';
    public static String COMMUNITY_CUSTOMER_CARE_SPECIALIST_PROFILE = 'Customer Care Specialist';
    
    public static String COMMUNITY_MODERATOR_PERMISSIONSET = 'Communities_Moderator';
    public static String COMMUNITY_ADMIN_PERMISSIONSET = 'Communities_Admin';
    public static String COMMUNITY_DELEGATED_ADMIN_PERMISSIONSET = 'Communities_Delegated_Admin';
    
    public static String COMMUNITY_ATTACHMENT_OBJECT = 'ComDirectMsg_Attachment__c';
    public static String COMMUNITY_ATTCHMENT_DIRECTMESSAGE_RECORDTYPE = 'Direct_Message';
    public static String DOCUMENT_SOURCE_DIRECTMESSAGE = 'Communities Direct Message';
    public static String DIRECTMESSAGE_SYSCODE = 'SC';
    
    
    
    
    
    

}