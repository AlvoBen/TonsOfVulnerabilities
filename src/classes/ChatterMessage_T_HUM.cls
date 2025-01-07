@isTest
private class ChatterMessage_T_HUM 
{ 
    @isTest(SeeAllData=true) 
        static void testConversationCreation() {
            
            Boolean expectedExceptionThrown = false;            
            List<User> lstChatterUser = new List<User>(); 
			User sender = HUMTestDataHelperTest.createTestUser('chatterUSer','Deployment');
			User recipient = HUMTestDataHelperTest.createTestUser('chatterUSer2','Deployment');
			lstChatterUser.add(sender);
			lstChatterUser.add(recipient);
			insert lstChatterUser;
			  
            
            System.runAs(sender) {
                try
                {
                    ConnectApi.ChatterMessage message = ConnectApi.ChatterMessages.sendMessage('chatterUSer2', recipient.Id);
                    System.assertNotEquals(null, message);
                }catch(Exception e){
                    
                     expectedExceptionThrown =  (e.getMessage().contains('Humana Security')) ? true : false; 
                     System.AssertEquals(expectedExceptionThrown, true);
                    
                }
            }
        }
}