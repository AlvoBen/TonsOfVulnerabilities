@isTest
private class HSU_UserAccessDetailsControllerTest {
    static testMethod void validateUserAccessDetailsController(){
    User runAsUser = createSFUser('System Administrator', true);
        System.assert(runAsUser.id != null);
        
        System.runAs(runAsUser) {
            
            User u = createSFUser('Standard User', true);
                 
            ApexPages.currentPage().getParameters().put('uid', u.Id);
            HSU_UserAccessDetailsController controller = new HSU_UserAccessDetailsController(); 
            controller.loadFls();
            //controller.getFieldLabels();
            controller.getUserPermLabels();
            controller.getClassNames();
            controller.getPageNames();
            controller.getObjectLabels();
            controller.getFlsPermFieldNames();
            controller.getObjectPermFieldNames();
            controller.getPages();
            controller.getApexClasses();
            controller.getClassStatus();
            //controller.getFieldStatus();
            controller.getObjectPerms();
            controller.getObjectPermStatus();
            controller.getPageStatus();       
            controller.getUserPermStatus();
            //controller.getFls();
            
            controller.flsObjectType = 'User';
            //controller.getObjectPermFieldNames();
            //controller.getFlsPermFieldNames();
            //controller.getFls();
        }
    }
    
    public static User createSFUser(String profileName, Boolean addPermSet){
    
        Profile profileUsed;
        String currentUser = UserInfo.getUserName();
        String ret = 'word' + math.rint(math.random() * 100000);
        // AXW3742 2015/03/03: Added additional variable to account for validation rules in the nophi environment
        String tmpUserName = ret + '@' + currentUser.substringAfter('@');
     
        Profile p = [select id from profile where name= :profileName];
        //WHERE Name = 'AM_User_Provisioning'
        PermissionSet pset = [SELECT Id FROM PermissionSet WHERE IsOwnedByProfile = false LIMIT 1];       
        
        String namePrefix = ret;
    
         User testUser = new User();
         testUser.Email = 'test=test.com@example.com';
         testUser.Username = tmpUserName;
         testUser.LastName = 'test';
         testUser.Alias = 'test';
         testUser.ProfileId = p.Id;
         testUser.LanguageLocaleKey = 'en_US';
         testUser.LocaleSidKey = 'en_US';
         testUser.TimeZoneSidKey = 'America/Chicago';
         testUser.EmailEncodingKey = 'UTF-8';     
         
         insert testUser; 
        
         if (addPermSet = true) {
             PermissionSetAssignment psa = new PermissionSetAssignment(AssigneeId = testUser.id, PermissionSetId = pset.id);
             insert psa;
         }
         
         return testUser;
  } 
}