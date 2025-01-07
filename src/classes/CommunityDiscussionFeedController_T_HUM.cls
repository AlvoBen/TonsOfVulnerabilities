/*******************************************************************************************************************************
Apex Class Name : CommunityDiscussionFeedController_T_HUM 
Version         : 1.0
Created On      : 08/03/2021
Function        : This class is used for testing the class CommunityDiscussionFeedController_C_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nitaj Titans                                      08/03/2021                    Original version
**************************************************************************************************************************************/
@isTest
private class CommunityDiscussionFeedController_T_HUM {
     /*  
     * Method name : TestSetup
     * Description : Test setup method for the testclass
     * Return Type : void
     * Parameter   : none
     */
    @testSetup static void TestSetup(){
        User u;
        User oUser = HUMTestDataHelperTest.createTestUser('UserForFeed','System Administrator');
        oUser.UserRoleId = [SELECT Id, Name FROM UserRole WHERE name = 'System Account Role' LIMIT 1].Id;
        Insert oUser;
        system.assert(oUser.Id != null);
        profile p= [select id from profile where Name='Humana Community Member'];
        
        Id recType = Schema.SObjectType.Account.getRecordTypeInfosbyDeveloperName().get('Member').getRecordTypeId();
        List<NetworkMember> membership;
        Account objAccount;
        system.runAs(oUser){
            objAccount = new Account();
            objAccount.FirstName='Test Firstname';
            objAccount.LastName='Test LastName';
            objAccount.RecordTypeId =recType;
            insert objAccount;  
            Account oAcc = [Select Id, PersonContactId,FirstName from Account where Id=:objAccount.Id];
            System.assertEquals(oAcc.FirstName, 'Test Firstname');
            
            u = new User(alias = 'test123', email='test123@noemail.com',
                         emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                         localesidkey='en_US', profileid = p.id, country='United States',IsActive =true,
                         ContactId = oAcc.PersonContactId,
                         timezonesidkey='America/Los_Angeles', username='tester@noemail.com');
            
            insert u;            
            System.assertEquals([SELECT Id,alias FROM User WHERE alias = 'test123' LIMIT 1].alias, 'test123');
            membership = [SELECT NetworkID,Network.Name, MemberID FROM NetworkMember WHERE memberID = :u.id limit 1];     
            system.debug('membership' + membership);
            FeedItem fdItm = new FeedItem();
            fdItm.Body = 'This is for Test';
            fdItm.ParentId = oUser.Id;
            insert fdItm;
            system.assert(fdItm.Id != null);
            FeedItem fdItm1 = new FeedItem();
            fdItm1.Body = 'This is for Test';
            fdItm1.Title = 'This is for Test';
            fdItm1.Type = 'QuestionPost';
            fdItm1.ParentId = oUser.Id;
            insert fdItm1;
            system.assert(fdItm1.Id != null);
            FeedComment fdCmnt = new FeedComment();
            fdCmnt.CommentBody = 'This is for text comment';
            fdCmnt.CommentType = 'TextComment';
            fdCmnt.FeedItemId  = fdItm.Id;
            insert fdCmnt;
            system.assert(fdCmnt.Id != null);
            FeedComment fdCmnt1 = new FeedComment();
            fdCmnt1.CommentBody = 'This is for text comment1';
            fdCmnt1.CommentType = 'TextComment';
            fdCmnt1.FeedItemId  = fdItm.Id;
            insert fdCmnt1;
            system.assert(fdCmnt1.Id != null);
            FeedLike fdLk = new FeedLike();
            fdLk.FeedItemId  = fdItm.Id;
            insert fdLk;
            system.assert(fdLk.Id != null);
        }
    }
    /*  
     * Method name : testGetSearchFeedItems
     * Description : This method tests the method getSearchFeedItems from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testGetSearchFeedItems(){
        User u =[select id from User where email='UserForFeed@test.com'];
        Id feedItemId = [SELECT id from FeedItem LIMIT 1].Id;
        system.runAs(u){
            Test.startTest();
            CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap1 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
            feedItemWrap1.feedItemsLength = 10;
            feedItemWrap1.selectItem = 'TextPost';
            feedItemWrap1.searchInput = '';
            feedItemWrap1.sortItem = '';
            feedItemWrap1.isComponentVisible = true;
            String myJSON1 = JSON.serialize(feedItemWrap1);
            CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap4 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
            feedItemWrap4.feedItemsLength = 10;
            feedItemWrap4.selectItem = '';
            feedItemWrap4.searchInput = 'Test';
            feedItemWrap4.sortItem = '';
            feedItemWrap4.isComponentVisible = true;
            String myJSON4 = JSON.serialize(feedItemWrap4);
             CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap2 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
            feedItemWrap2.feedItemsLength = 10;
            feedItemWrap2.selectItem = 'QuestionPost';
            feedItemWrap2.searchInput = 'Test';
            feedItemWrap2.sortItem = '';
            feedItemWrap2.isComponentVisible = true;
            String myJSON2 = JSON.serialize(feedItemWrap2);
            CommunityDiscussionFeedController_C_HUM.getDiscussionCompConfig();
            CommunityDiscussionFeedController_C_HUM.getSearchFeedItems(myJSON1);
            CommunityDiscussionFeedController_C_HUM.getSearchFeedItems(myJSON2);
            CommunityDiscussionFeedController_C_HUM.getLatestFeedOptions();
            CommunityDiscussionFeedController_C_HUM.getFeedOptions();
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.getSearchFeedItems(myJSON4);
            Test.stopTest();
            System.assertNotEquals(null, discWrapList);
        }
    }
    /*  
     * Method name : testGetSortedFeedItems
     * Description : This method tests the method getSortedFeedItems from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testGetSortedFeedItems(){
        User u =[select id from User where email='UserForFeed@test.com'];
        Id feedItemId = [SELECT id from FeedItem LIMIT 1].Id;
        system.runAs(u){
            Test.startTest();
            CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap1 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
            feedItemWrap1.feedItemsLength = 10;
            feedItemWrap1.selectItem = '';
            feedItemWrap1.searchInput = '';
            feedItemWrap1.sortItem = 'TextPost';
            feedItemWrap1.isComponentVisible = true;
            String myJSON1 = JSON.serialize(feedItemWrap1);
            CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap4 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
            feedItemWrap4.feedItemsLength = 10;
            feedItemWrap4.selectItem = '';
            feedItemWrap4.searchInput = '';
            feedItemWrap4.sortItem = 'Most Recent Activity';
            feedItemWrap4.isComponentVisible = true;
            String myJSON4 = JSON.serialize(feedItemWrap4);
            CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap3 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
            feedItemWrap3.feedItemsLength = 10;
            feedItemWrap3.selectItem = '';
            feedItemWrap3.searchInput = '';
            feedItemWrap3.sortItem = 'QuestionPost';
            feedItemWrap3.isComponentVisible = true;
            String myJSON3 = JSON.serialize(feedItemWrap3);
            CommunityDiscussionFeedController_C_HUM.getSortedFeedItems(myJSON3);
            CommunityDiscussionFeedController_C_HUM.getSortedFeedItems(myJSON1);
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.getSortedFeedItems(myJSON4);
            Test.stopTest();
            System.assertNotEquals(null, discWrapList);
        }
    }
    /*  
     * Method name : testGetLoadedOrRefreshedFeedItems
     * Description : This method tests the method getLoadedOrRefreshedFeedItems from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testGetLoadedOrRefreshedFeedItems(){
        User u =[select id from User where email='UserForFeed@test.com'];
        system.runAs(u){
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.getLoadedOrRefreshedFeedItems(10,true,true);
            CommunityDiscussionFeedController_C_HUM.isRendered();
            System.assertNotEquals(null, discWrapList);  
        }
    }
     /*  
     * Method name : testGetLoadedFeedItems
     * Description : This method tests the method getLoadedOrRefreshedFeedItems from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testGetLoadedFeedItems(){
        User u =[select id from User where email='UserForFeed@test.com'];
        system.runAs(u){
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.getLoadedOrRefreshedFeedItems(10,true,false);
            System.assertNotEquals(null, discWrapList);  
        }
    }
    /*  
     * Method name : testSubmitFeedComment
     * Description : This method tests the method submitFeedComment from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testSubmitFeedComment(){
        User u =[select id from User where email='UserForFeed@test.com'];
        List<String> feedItemIds = new List<String>();
        List<FeedItem> feedItemList = [SELECT Id from FeedItem];
        for(FeedItem fItem : feedItemList){
            feedItemIds.add(fItem.Id);
        }
        system.runAs(u){
            Test.startTest();
            CommunityDiscussionFeedController_C_HUM.CommentWrapper cmntWrap = new CommunityDiscussionFeedController_C_HUM.CommentWrapper();
            cmntWrap.commentBody = 'This is test comment';
            cmntWrap.feedItemId = feedItemList[0].Id;
            String JsonVal = JSON.serialize(cmntWrap);
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.submitFeedComment(feedItemIds,JsonVal);
            Test.stopTest();
            System.assertNotEquals(null, discWrapList); 
        }
    }
    /*  
     * Method name : testSubmitOrRemoveFeedLike
     * Description : This method tests the method submitOrRemoveFeedLike from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest
    private static void testSubmitOrRemoveFeedLike(){
        User u =[select id from User where email='UserForFeed@test.com'];
        Id feedItemId = [SELECT id from FeedItem LIMIT 1].Id;
        List<String> feedItemIds = new List<String>();
        List<FeedItem> feedItemList = [SELECT Id from FeedItem];
        for(FeedItem fItem : feedItemList){
            feedItemIds.add(fItem.Id);
        }
        system.runAs(u){
            Test.startTest();
            CommunityDiscussionFeedController_C_HUM.submitOrRemoveFeedLike(feedItemIds,feedItemId,'Like');
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.submitOrRemoveFeedLike(feedItemIds,feedItemId,'Liked');
            Test.stopTest();
            System.assertNotEquals(null, discWrapList);
        }
    }
    /*  
     * Method name : testGetHideOrMoreComments
     * Description : This method tests the method getHideOrMoreComments from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest static void testGetHideOrMoreComments(){
        User u =[select id from User where email='UserForFeed@test.com'];
        Id feedItemId = [SELECT id from FeedItem LIMIT 1].Id;
        List<String> feedItemIds = new List<String>();
        List<FeedItem> feedItemList = [SELECT Id from FeedItem];
        for(FeedItem fItem : feedItemList){
            feedItemIds.add(fItem.Id);
        }
        system.runAs(u){
            Test.startTest();
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.getHideOrMoreComments(feedItemIds,feedItemId);
            Test.stopTest();
            System.assertNotEquals(null, discWrapList);
        }
    }
    /*  
     * Method name : testGetMoreFeedItems
     * Description : This method tests the method getMoreFeedItems from class CommunityDiscussionFeedController_C_HUM
     * Return Type : void
     * Parameter   : none
     */
    @isTest static void testGetMoreFeedItems(){
        User u =[select id from User where email='UserForFeed@test.com'];
        CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap1 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
        feedItemWrap1.feedItemsLength = 10;
        feedItemWrap1.selectItem = '';
        feedItemWrap1.searchInput = '';
        feedItemWrap1.sortItem = 'TextPost';
        feedItemWrap1.isComponentVisible = true;
        String myJSON1 = JSON.serialize(feedItemWrap1);
        CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap2 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
        feedItemWrap2.feedItemsLength = 10;
        feedItemWrap2.selectItem = '';
        feedItemWrap2.searchInput = '';
        feedItemWrap2.sortItem = 'Most Recent Activity';
        feedItemWrap2.isComponentVisible = true;
        String myJSON2 = JSON.serialize(feedItemWrap2);
        CommunityDiscussionFeedController_C_HUM.FeedItemWrapper feedItemWrap3 = new CommunityDiscussionFeedController_C_HUM.FeedItemWrapper();
        feedItemWrap3.feedItemsLength = 10;
        feedItemWrap3.selectItem = '';
        feedItemWrap3.searchInput = 'Test';
        feedItemWrap3.sortItem = '';
        feedItemWrap3.isComponentVisible = true;
        String myJSON3 = JSON.serialize(feedItemWrap3);
        system.runAs(u){
            Test.startTest();
            CommunityDiscussionFeedController_C_HUM.getMoreFeedItems(myJSON1);
            CommunityDiscussionFeedController_C_HUM.getMoreFeedItems(myJSON2);
            List<CommunityDiscussionFeedController_C_HUM.DiscussionFeedWrapper> discWrapList = CommunityDiscussionFeedController_C_HUM.getMoreFeedItems(myJSON3);
            Test.stopTest();
            System.assertNotEquals(null, discWrapList);
        }
    }
}