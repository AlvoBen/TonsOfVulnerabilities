/*******************************************************************************************************************************
Component Name : CreateUnknowGroup_LT_HUM
Version        : 1.0
Created On     : 6/23/2021
Function       : This component is for creating Unknow Group account functionality on Group Search tab
Test Class     : CreateUnknowGroup_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
	1.0			   Surendra Vemulapalli								06/23/2021					US1464387- Creating Unknown Group Account (Health Cloud Lightning App)
**************************************************************************************************************************************/
@isTest
public class CreateUnknowGroup_LT_HUM {
	
   
        static testmethod void prepareTestSearchResults()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        
        String unKnowGp = '{"unAccountName":"unAccountName","unTaxId":"unTaxId","unBillingCity":"unBillingCity","unWorkEmail":"unWorkEmail@h.com","unGroupNumber": "unGroupNumber","unBillingStreet":"unBillingStreet","unZipCode":"unZipCode","unPhone":"unPhone","unState": "Alaska"}';
     	
        UnknowGroupCreateWrapper_LH_HUM usewrapper = new UnknowGroupCreateWrapper_LH_HUM();
		    usewrapper.unAccountName='unAccountName';
		usewrapper.unTaxId ='unTaxId';
		usewrapper.unBillingCity ='unBillingCity';
		usewrapper.unWorkEmail ='unWorkEmail@h.com';
		usewrapper.unGroupNumber= 'unGroupNumber';
		usewrapper.unBillingStreet='unBillingStreet';
		usewrapper.unZipCode='unZipCode';
		usewrapper.unPhone='unPhone';
		usewrapper.unState= 'Alaska';
        
          
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1', 'Customer Care Specialist');
        insert oCCSUser;
        system.assert(oCCSUser.Id != NULL);
        system.runAs(oCCSUser) 
        {
        Test.startTest();
        
        
        CreateUnknowGroup_LC_HUM.createUnknowGroup(unKnowGp);
        
            Test.stopTest();
        }
    }
    
 }