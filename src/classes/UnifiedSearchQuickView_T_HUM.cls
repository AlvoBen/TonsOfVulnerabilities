/**********************************************************************************************************
Apex Class Name  : UnifiedSearchQuickView_T_HUM
Version          : 1.0
Created Date     : 12/21/2020
Function         : used to test methods of UnifiedSearchQuickView_C_HUM
************************************************************************************************************
Modification Log:
* Developer Name                          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
*  T. Prasanna Sai Kumar Reddy                                  12/21/2020                 original Version
**************************************************************************************************************/
@isTest
Private class UnifiedSearchQuickView_T_HUM 
{
    /*
    * Method Name : setupTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
	*/ 
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('UnifiedSearchUser3','Customer Care Specialist');
        insert objUser;
        
        System.assertEquals(objUser.LastName, 'test UnifiedSearchUser3');
        
        system.runAs(objUser)
        {
            HUM_Webservice_Callout__c serviceDetails = New HUM_Webservice_Callout__c(Name = 'CoveoService',User_Name__c = 'Bearer 1234567',Certificate_Name__c = 'Humana_GuidanceCare_test',Active__c = true,
                                                                                     End_Point_URL__c = 'https://dev-crmservicews.com/' ,
                                                                                     Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json',Service_Name__c = 'rest/search/v2/token' ); 
            insert serviceDetails; 
            
            system.assertEquals('CoveoService',serviceDetails.Name);
            
            HUMConstants__c orgId = new HUMConstants__c(Name = 'CoveoOrgID', StringValue__c = 'humanaincnonproduction1if143lrw');
            insert orgId;
            
            system.assertEquals('CoveoOrgID',orgId.Name);
        }
    }
	/*
    * Method Name : testDocumentHtml
    * Description : This method tests positive scenario of quick view class.
    * Return type : None
    * Parameter   : None
    */
   @isTest static void testDocumentHtml()
    {
        User testUser = [Select Id,LastName from User where LastName = 'test UnifiedSearchUser3']; 
        system.assertEquals('test UnifiedSearchUser3', testUser.LastName); 
        
        system.runAs(testUser)
        {
			UnifiedSearchQuickView_C_HUM objCls = new UnifiedSearchQuickView_C_HUM(); 
			Test.setCurrentPage(page.UnifiedSearchQuickView_VF_HUM);
			ApexPages.currentPage().getParameters().put('documentID','42.6https://www.google.com/help/');
			ApexPages.currentPage().getParameters().put('searchToken','e1NiJ9.er');
			Test.startTest();
			continuation con =  (continuation)objCls.fetchDocumentHtml();
            string body = '<html><p>covid</p></html>';
            HttpResponse res = new HttpResponse();
            res.setStatuscode(200);
            res.setBody(body);
            Test.setContinuationResponse(objCls.request, res);
			Test.invokeContinuationMethod(objCls,con);
			System.assertEquals(UnifiedSearchQuickView_C_HUM.DocumentHtml,body);
			Test.stopTest();
		}
	}
	/*
    * Method Name : exceptionTestDocumentHtml
    * Description : This method tests exception scenario of quick view class.
    * Return type : None
    * Parameter   : None
    */
    
     @isTest static void exceptionTestDocumentHtml()
    {
        User testUser = [Select Id,LastName from User where LastName ='test UnifiedSearchUser3']; 
        system.assertEquals('test UnifiedSearchUser3',testUser.LastName);
        
        system.runAs(testUser)
        {
            UnifiedSearchQuickView_C_HUM objCls = new UnifiedSearchQuickView_C_HUM();
            string errorMessage = '<html><body><div style="text-align:center"><h1>	Oops! Something Went Wrong</h1><br/><h3>Please close this window and launch it again using Quick View button</h3></div></body></html>';
            Test.setCurrentPage(page.UnifiedSearchQuickView_VF_HUM);
            ApexPages.currentPage().getParameters().put('searchToken','e1NiJ9.er');
            Test.startTest(); 
            objCls.fetchDocumentHtml();
            System.assertEquals(errorMessage, UnifiedSearchQuickView_C_HUM.DocumentHtml);
            Test.stopTest();
        }
    }
    /*
    * Method Name : negativeTest1DocumentHtml
    * Description : This method tests else part scenario of quick view class.
    * Return type : None
    * Parameter   : None
    */
    
   @isTest static void negativeTest1DocumentHtml()
    {
        User testUser = [Select Id,LastName from User where LastName = 'test UnifiedSearchUser3']; 
        system.assertEquals('test UnifiedSearchUser3', testUser.LastName);
        
        system.runAs(testUser)
        {
            UnifiedSearchQuickView_C_HUM objCls = new UnifiedSearchQuickView_C_HUM();
            string errorMessage = '<html><body><div style="text-align:center"><h1>	Oops! Something Went Wrong</h1><br/><h3>Please close this window and launch it again using Quick View button</h3></div></body></html>';
            test.setCurrentPage(page.UnifiedSearchQuickView_VF_HUM);
            ApexPages.currentPage().getParameters().put('documentID','42.6https://www.google.com/help/');
            ApexPages.currentPage().getParameters().put('searchToken','e1NiJ9.er');
            Test.startTest();
			string body = '{"Fault": {"faultcode":"faultcode","message" : "error"}}'; 
            continuation con =  (continuation)objCls.fetchDocumentHtml();
            HttpResponse res = new HttpResponse();
            res.setStatuscode(401);
            res.setBody(body);
            Test.setContinuationResponse(objCls.request, res);
			Test.invokeContinuationMethod(objCls,con);
            system.assertEquals(errormessage, UnifiedSearchQuickView_C_HUM.DocumentHtml);
            Test.stopTest();
        }
    }
    /*
    * Method Name : negativeTest2DocumentHtml
    * Description : This method tests else part scenario of quick view class.
    * Return type : None
    * Parameter   : None
    */
    @isTest static void negativeTest2DocumentHtml()
    {
        User testUser = [Select Id,LastName from User where LastName = 'test UnifiedSearchUser3']; 
        system.assertEquals('test UnifiedSearchUser3',testUser.LastName);
        
        system.runAs(testUser)
        {
            UnifiedSearchQuickView_C_HUM objCls = new UnifiedSearchQuickView_C_HUM();
            string errorMessage = '<html><body><div style="text-align:center"><h1>	Oops! Something Went Wrong</h1><br/><h3>Please close this window and launch it again using Quick View button</h3></div></body></html>';
            test.setCurrentPage(page.UnifiedSearchQuickView_VF_HUM);
            ApexPages.currentPage().getParameters().put('documentID','42.6https://www.google.com/help/');
            ApexPages.currentPage().getParameters().put('searchToken','e1NiJ9.er');
            Test.startTest();
            string body = '{"message": "error bad request"}';
			continuation con =  (continuation)objCls.fetchDocumentHtml();
            HttpResponse res = new HttpResponse();
            res.setStatuscode(401);
            res.setBody(body);
            Test.setContinuationResponse(objCls.request, res);
			Test.invokeContinuationMethod(objCls,con);
            system.assertEquals(errormessage, UnifiedSearchQuickView_C_HUM.DocumentHtml);
            Test.stopTest();
        }
    }   
}