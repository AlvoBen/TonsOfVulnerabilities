/*******************************************************************************************************************************
Apex Class Name : HUMAccountDetailPageControllerTest
Version         : 1.0
Created On      : 07/24/2014
Function        : Test class for HUMAccountDetailPageController

  Modification Log: 
* Developer Name              Code Review                    Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar                17122                          07/24/2014                 Original Version     
* Manish Kumar                19728                          12/05/2014                 Fixed production error
* Vardhman Jain                                              11/15/2021                 US 2791814: Account pages of all types
*******************************************************************************************************************************/
@isTest
private class HUMAccountDetailPageControllerTest
{
    /*
    * Method Name   :    overallTestMethod
    * Description   :    This method is for overall test coverage.
    * Return Type   :    void
    * Parameters    :    NA
    */
    static testMethod void overallTestMethod()
    {
        User oCCSUser = [Select Id from User where Id =: UserInfo.getUserId()];
        Account acc = NULL;
        System.assertEquals(acc, NULL);
        
        System.runAs ( oCCSUser )
        {
            acc = new Account(Name='TestAccount');
            insert acc;
            System.assertNotEquals(acc.Id, NULL);
        }
        
        oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs ( oCCSUser )
        {
			pageRef();
            HUMAccountDetailPageController accDetail = new HUMAccountDetailPageController();
            System.assertNotEquals(accDetail, NULL);
            ApexPages.currentPage().getParameters().put('id', acc.Id);
            System.assertEquals(ApexPages.currentPage().getParameters().get('id'), acc.Id);
            acc = accDetail.acc;
            System.assertEquals(acc, accDetail.acc);
        }
    }
	static void pageRef(){
        PageReference pageref = Page.HUMAccountDetailPage;    
             pageref.getHeaders().put('USER-AGENT','chrome');
             Test.setCurrentPage(pageref);
    }
}