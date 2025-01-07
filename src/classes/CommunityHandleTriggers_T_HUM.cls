/*******************************************************************************************************************************
Apex Class Name : CommunityHandleTriggers_T_HUM 
Version         : 1.0
Created On      : 06/22/2020
Function        : This class is used for testing the class CommunityDataMasking_H_HUM and trigger HUMFeedItemTrigger

Modification Log: 
 * Developer Name           Code Review                     Date                            Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Shreya Agrawal                                       06/22/2020                      Original version
 ****************************************************************************************************************************/

@isTest
public class CommunityHandleTriggers_T_HUM {
    
     /*  
     * Method name : TestSetup
     * Description : Test setup method for the testclass
     * Return Type : void
     * Parameter   : none
     */
     
    @testSetup static void TestSetup(){
        
        List<String> CommunityList= System.Label.CommunityName_HUM.split(',');
        Network CurrentCommunity= [Select id from Network where name IN : CommunityList limit 1];
        User u= [select id from user where id=: userinfo.getUserId()];
        
        system.runas(u){
            //Create a new FeedItem
            List<FeedItem> allFeedItems = CommunityTestDataFactory_H_HUM.createFeedItemPosts(1,CurrentCommunity.id);
            
        }   
    }
    
    /*  
     * Method name : testDataMaskingControllerForFeedItems
     * Description : This method tests the PHI masking on posts on Community
     * Return Type : void
     * Parameter   : none
     */ 
        
    private static testmethod void testDataMaskingControllerForFeedItems()
    {
        List<String> CommunityList= System.Label.CommunityName_HUM.split(',');
        Network CurrentCommunity= [Select id from Network where name IN : CommunityList limit 1];
        profile pAdmin = [select id from profile where Name='System Administrator'];
        User user = new User(alias = 'test1234', email='test1234@noemail.com',
                                  emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                                  localesidkey='en_US', profileid = pAdmin.id, country='United States',IsActive =true,
                                  timezonesidkey='America/Los_Angeles', username='tester1234@noemail.com');
        
        system.runas(user){ 
            FeedItem post = new FeedItem(); 
            post.ParentId = userinfo.getuserid(); 
            post.Body = 'test@test.com '+user.name; 
            post.Title = user.name+' Test data 99999999';
            post.NetworkScope = CurrentCommunity.id;
            test.startTest();
            insert post; 
            FeedItem testresultPost=[select id,ParentId,Body,Title from FeedItem limit 1];
            System.assertEquals('*****', testresultPost.Body);
            test.stopTest();
            
        }
    }
    
    /*  
     * Method name : testDataMaskingControllerForFeedComments
     * Description : This method tests the PHI masking on Comments on Community
     * Return Type : void
     * Parameter   : none
     */ 
    
    private static testmethod void testDataMaskingControllerForFeedComments()
    {
        profile pAdmin = [select id from profile where Name='System Administrator'];
        User adminUser = new User(alias = 'test1234', email='test1234@noemail.com',
                                  emailencodingkey='UTF-8', lastname='Testing', languagelocalekey='en_US',
                                  localesidkey='en_US', profileid = pAdmin.id, country='United States',IsActive =true,
                                  timezonesidkey='America/Los_Angeles', username='tester1234@noemail.com');
        
        system.runas(adminUser)
        { 
            Test.startTest();
            FeedItem post= [select id,body,title from FeedItem limit 1];
            FeedComment Comment = new FeedComment(); 
            Comment.FeedItemId=post.id; 
            Comment.CommentBody = 'test@test.com '+adminUser.LastName; 
            insert Comment; 
            FeedComment testresultComment=[select id,CommentBody from FeedComment limit 1];
            System.assertEquals('***** *****', testresultComment.CommentBody);
            test.stopTest();
            
        }
    }
}