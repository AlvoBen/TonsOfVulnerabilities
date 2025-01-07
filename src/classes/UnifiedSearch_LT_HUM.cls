/*******************************************************************************************************************************
Apex Class Name     :UnifiedSearch_LT_HUM 
Version             : 1.0
Function            : Test class to test scenarios in  CoveoSearch classes.

Modification Log: 
* Developer Name                     Code Reveiw                      Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* T.Prasanna Sai kumar Redddy                                      12/08/2020                       Original Version
*******************************************************************************************************************************/

@isTest
private class UnifiedSearch_LT_HUM {  
   /*
    * Method Name : setupTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */ 
    @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('UnifiedSearchUser4','Customer Care Specialist');
        objUser.FirstName = 'UnifiedSearchUser20';
        objUser.Network_User_Id__c = 'ABC1234';
        insert objUser;
        
        System.assertEquals('test UnifiedSearchUser4', objUser.LastName);
        system.runAs(objUser)
        {
            HUM_Webservice_Callout__c serviceDetails = New HUM_Webservice_Callout__c(Name = 'CoveoService',User_Name__c = 'Bearer 1234567',Certificate_Name__c = 'Humana_GuidanceCare_test' ,Active__c = true,
                                                                                     End_Point_URL__c = 'https://dev-crmservicews.humana.com/CoveoService/' ,
                                                                                     Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c = 'application/json',Service_Name__c= 'rest/search/v2/token' );
            insert serviceDetails; 
            
            system.assertEquals('CoveoService',serviceDetails.Name);
            
            HUMConstants__c orgId =new HUMConstants__c(Name = 'CoveoOrgID', StringValue__c = 'humana124');
            insert orgId; 
            
            system.assertEquals('CoveoOrgID', orgid.Name);              
            
            CRMFunctionality_ONOFF_Switch__c decryptSwitch = new CRMFunctionality_ONOFF_Switch__c(Name='DecryptToken',IsON__c=true);
            insert decryptSwitch;
            system.assertEquals('DecryptToken', decryptSwitch.Name);    
        }
    }
    /*
    * Method Name : testGenerateToken
    * Description : This method tests positive scenario of generateToken method where coveotoken is present in cache
    * Return type : None
    * Parameter   : None
    */
    @isTest static void testGenerateToken()
    {
        User testUser = [Select Id, FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName); 
        
        object result;
        System.runAs(testUser)
        {  
            map<string, string> results = new map<string,string>();
            string coveotoken = 'fzKjcHdjPJKJVaJ2OjK0fz';
            Cache.Session.Put('coveoToken', coveotoken);
            Test.startTest();
            result = UnifiedSearch_LC_HUM.generateToken();
            Map < String, Object > response = (Map < String, Object > ) JSON.deserializeUntyped( json.serialize(result));
            system.assertEquals(coveotoken, (string)response.get('token'));
            Test.stopTest();
        }
    }    
    /*
    * Method Name : positiveTestGenerateToken
    * Description : This method tests scenario where token is not present in cache
    * Return type : None
    * Parameter   : None
    */  
   @isTest static void positiveTestGenerateToken()
    {
       	User testUser = [Select Id, FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName);  
       
        object res;
        System.runAs(testUser)
        {
            list<string> labels = new list<string>();
            string labelValue = 'Continuation-1';
            labels.add(labelValue);
            string mockResponse = '{"token": "fzKjcHdjPJKJVaJ2OjK0fz"}';
            string coveoToken = 'fzKjcHdjPJKJVaJ2OjK0fz';
            string cacheValue = '';
            Test.startTest();
            continuation con =  (continuation) UnifiedSearch_LC_HUM.generateToken();
            system.assertEquals('processGetTokenResponse', con.ContinuationMethod);
	    
            HttpResponse resp = new HttpResponse();
            resp.setStatusCode(200);
            resp.setHeader('Content-Type', 'application/json');
            resp.setBody(mockResponse);
            Test.setContinuationResponse(labelValue, resp);
            res = UnifiedSearch_LC_HUM.processGetTokenResponse(labels);
            cacheValue = string.valueOf(cache.session.get('coveoToken'));
            Map < String, Object > mapResponse = (Map < String, Object > ) JSON.deserializeUntyped(json.serialize(res));
            system.assertEquals(coveoToken, mapResponse.get('token'));
            system.assertEquals(coveoToken, cachevalue);            
            Test.stopTest();
        }    
    }
    /*  
    * Method Name : faultErrorTestGenerateToken
    * Description : This method tests else scenario  of processGetTokenResponse method
    * Return type : None
    * Parameter   : None
    */
    @isTest static void faultErrorTestGenerateToken()
    {
        User testUser = [Select Id, FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName); 
      
        object res;
        System.runAs(testUser)
        {
            list<string> labels = new list<string>();
            string labelValue = 'Continuation-1';
            labels.add(labelValue);
            string cacheValue;
            Test.startTest();
            continuation con =  (continuation) UnifiedSearch_LC_HUM.generateToken();
            string mockResponse='{"Fault": {"faultcode":"faultcode","message" : "error"}}';
            HttpResponse responseNeg = new HttpResponse();
            responseNeg.setStatuscode(401);
            responseNeg.setBody(mockResponse);
            Test.setContinuationResponse(labelValue, responseNeg);
            res = UnifiedSearch_LC_HUM.processGetTokenResponse(labels);
            Map < String, Object > mapResponse = (Map < String, Object > ) JSON.deserializeUntyped(json.serialize(res));
 			cacheValue = string.valueOf(cache.session.get('coveoToken'));
            system.assertEquals('Error',cacheValue); 
            system.assertEquals('Error',mapResponse.get('token')); 
            Test.stopTest();
        }
    } 
    /*  
    * Method Name : negativePartTestGenerateToken
    * Description : This method tests else scenario  of processGetTokenResponse method
    * Return type : None
    * Parameter   : None
    */
   @isTest static void negativePartTestGenerateToken()
    {
  		User testUser = [Select Id, FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName); 

        object res;
        System.runAs(testUser)
        {
            list<string> labels = new list<string>();
            string labelValue = 'Continuation-1';
            labels.add(labelValue);
            Test.startTest();
            continuation con =  (continuation) UnifiedSearch_LC_HUM.generateToken();
            string mockResponse = '{"message": "No Request header found"}';
            HttpResponse responseNeg = new HttpResponse();
            responseNeg.setStatuscode(401);
            responseNeg.setBody(mockResponse);   
            Test.setContinuationResponse(labelValue, responseNeg);
            res = UnifiedSearch_LC_HUM.processGetTokenResponse(labels);
            Map < String, Object > mapResponse = (Map < String, Object > ) JSON.deserializeUntyped(json.serialize(res));
            system.assertEquals('Error', mapResponse.get('token'));           
            Test.stopTest();
        }
    } 
        /*  
    * Method Name : errorscenarioTestGenerateToken
    * Description : This method tests else scenario  of processGetTokenResponse method
    * Return type : None
    * Parameter   : None
    */
    @isTest static void errorscenarioTestGenerateToken()
    {
        User testUser = [Select Id, FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName); 

        object res;
        System.runAs(testUser)
        {
            list<string> labels = new list<string>();
            string labelValue = 'Continuation-1';
            labels.add(labelValue);
            Test.startTest();
            continuation con =  (continuation) UnifiedSearch_LC_HUM.generateToken();
			string mockResponse = 'No Request header found';
            HttpResponse responseNeg = new HttpResponse();
            responseNeg.setStatuscode(200);
            responseNeg.setBody(mockResponse);   
            Test.setContinuationResponse(labelValue, responseNeg);
            res = UnifiedSearch_LC_HUM.processGetTokenResponse(labels);
            Map < String, Object > mapResponse = (Map < String, Object > ) JSON.deserializeUntyped(json.serialize(res));
           	system.assertEquals('Error', mapResponse.get('token')); 
           	Test.stopTest();
        }
    }
    /*
    * Method Name : exceptiontestGenerateToken
    * Description : This method tests exception scenario 
    * Return type : None
    * Parameter   : None
    */   
      @isTest static void exceptiontestGenerateToken(){
 		User testUser = [Select Id, FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName);
        
        object res;
        System.runAs(testUser)
        {
            list<string> labels = new list<string>();
            string labelValue = 'Continuation-1';
            labels.add(labelValue);
            string cacheValue;
            Test.startTest();
            continuation con =  (continuation) UnifiedSearch_LC_HUM.generateToken();
            HttpResponse resp = new HttpResponse();
            string mockresponse = '';
            resp.setStatusCode(400);
            resp.setHeader('Content-Type', 'application/json');
            resp.setBody(mockresponse);
            Test.setContinuationResponse(labelValue, resp);
            res = UnifiedSearch_LC_HUM.processGetTokenResponse(labels);
          	Map < String, Object > mapresponse = (Map < String, Object > ) JSON.deserializeUntyped(json.serialize(res));
            cacheValue = string.valueOf(cache.session.get('coveoToken'));
            system.assertEquals('Error',cacheValue); 
            system.assertEquals('Error', mapresponse.get('token'));
            Test.stopTest();
        } 
    }  
 
    /*
    * Method Name : testsearchQuery
    * Description : This method tests positive scenario of searchQuery method
    * Return type : None
    * Parameter   : None
    */    
    @isTest static void testsearchQuery()
    {
        User testUser = [Select Id,FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName);
        
        object obj;
        System.runAs(testUser)
        {
            string mockResponse = '{"token": "fzKjcHdjPJKJVaJ2OjK0fz"}';
            string params ='{"errorsAsSuccess":false,"method":"POST","uniqueId":"oA23123","queryString":["","visitor=7e6erte"],"requestData":[{"actionCause":"interfaceLoad","actionType":"interface","anonymous":false,"device":"Chrome","mobile":false,"language":"en","responseTime":1000,"originLevel1":"default","originLevel2":"All","originLevel3":"https://humanaservice--coveo--c.visualforce.com/apex/GenericHiddenWidgetComp_VF_HUM?isdtp=vw&sfdcIFrameOrigin=https%3A%2F%2Fhumanaservice--coveo.my.salesforce.com&nonce=e2087fce7059ffc6e28e3fe48beb50b1e6fb6e2da52d453200749a374f161781","originContext":"Search","customData":{"JSUIVersion":"2.8864.5;2.8864.5","context_classification":"","context_intent":""},"searchQueryUid":"2308f482-27f2-4760","queryText":"","advancedQuery":"","resultsPerPage":"","pageNumber":0,"contextual":false,"numberOfResults":2000,"facetState":[]}],"url":"fakeuri/rest/search/v2/html","responseType":"text","requestDataType":"application/json","accessToken":"fzKjcHdjPJKJVaJ2OjK0fz"}';
            string labelValue = 'Continuation-1';
            list< string> labels = new list<string>();
            labels.add(labelValue);
            Test.startTest();
            continuation con12= (continuation) UnifiedSearch_LC_HUM.searchQuery(params);
            system.assertEquals('processSearchQueryResponse',con12.ContinuationMethod);
            
            HttpResponse response12 = new HttpResponse();
            response12.setStatuscode(200);
            response12.setHeader('Content-Type', 'application/json');
            response12.setBody(mockResponse);
            Test.setContinuationResponse(labelValue, response12);
            obj = UnifiedSearch_LC_HUM.processSearchQueryResponse(labels);
            Map < String, Object > mapResponse = (Map < String, Object > ) JSON.deserializeUntyped(string.valueOf(obj));
            system.assertEquals(10000, mapResponse.get('duration'));
            Test.stopTest();     
        }       
    } 
   /*
    * Method Name : exceptiontestsearchQuery
    * Description : This method tests exception scenario of searchQuery method
    * Return type : None
    * Parameter   : None
    */  
     @isTest static void exceptiontestsearchQuery()
    {
        User testUser = [Select Id,FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName);
        
        object res;
        System.runAs(testUser)
        {
            string paras ='{"errorsAsSuccess":false,"method":"POST","uniqueId":"oA23123","queryString":["","visitor=7e6erte"],"requestData":[{"actionCause":"interfaceLoad","actionType":"interface","anonymous":false,"device":"Chrome","mobile":false,"language":"en","responseTime":1000,"originLevel1":"default","originLevel2":"All","originLevel3":"https://humanaservice--coveo--c.visualforce.com/apex/GenericHiddenWidgetComp_VF_HUM?isdtp=vw&sfdcIFrameOrigin=https%3A%2F%2Fhumanaservice--coveo.my.salesforce.com&nonce=e2087fce7059ffc6e28e3fe48beb50b1e6fb6e2da52d453200749a374f161781","originContext":"Search","customData":{"JSUIVersion":"2.8864.5;2.8864.5","context_classification":"","context_intent":""},"searchQueryUid":"2308f482-27f2-4760","queryText":"","advancedQuery":"","resultsPerPage":"","pageNumber":0,"contextual":false,"numberOfResults":2000,"facetState":[]}],"url":"fakeuri/rest/search/v2/html","responseType":"text","requestDataType":"application/json","accessToken":"fzKjcHdjPJKJVaJ2OjK0fz"}';
            string labelValue= 'Continuation-1';
            list< string> labels = new list<string>();
            labels.add(labelValue);
            string mockResponse = '';
            Test.startTest();
            continuation negCon= (continuation) UnifiedSearch_LC_HUM.searchQuery(paras);           
            HttpResponse negResponse = new HttpResponse();
            negResponse.setStatuscode(400);
            negResponse.setBody(mockResponse);
            Test.setContinuationResponse(labelValue, negResponse);
            res = UnifiedSearch_LC_HUM.processSearchQueryResponse(labels);
			system.assertEquals(null,res);            
            Test.stopTest();       
        }   
    }
      /*
    * Method Name : negativePart1TestsearchQuery
    * Description : This method tests negative scenario of searchQuery method
    * Return type : None
    * Parameter   : None
    */
     @isTest static void negativePart1TestsearchQuery()
    {
        User testUser = [Select Id,FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName);

        object res;
        System.runAs(testUser)
        {
    		string paras ='{"errorsAsSuccess":false,"method":"POST","uniqueId":"oA23123","queryString":["","visitor=7e6erte"],"requestData":[{"actionCause":"interfaceLoad","actionType":"interface","anonymous":false,"device":"Chrome","mobile":false,"language":"en","responseTime":1000,"originLevel1":"default","originLevel2":"All","originLevel3":"https://humanaservice--coveo--c.visualforce.com/apex/GenericHiddenWidgetComp_VF_HUM?isdtp=vw&sfdcIFrameOrigin=https%3A%2F%2Fhumanaservice--coveo.my.salesforce.com&nonce=e2087fce7059ffc6e28e3fe48beb50b1e6fb6e2da52d453200749a374f161781","originContext":"Search","customData":{"JSUIVersion":"2.8864.5;2.8864.5","context_classification":"","context_intent":""},"searchQueryUid":"2308f482-27f2-4760","queryText":"","advancedQuery":"","resultsPerPage":"","pageNumber":0,"contextual":false,"numberOfResults":2000,"facetState":[]}],"url":"fakeuri/rest/search/v2/html","responseType":"text","requestDataType":"application/json","accessToken":"fzKjcHdjPJKJVaJ2OjK0fz"}';
            string labelValue= 'Continuation-1';
            list< string> labels = new list<string>();
            labels.add(labelValue);
            string mockResponse = '{"Fault": {"faultcode":"faultcode","message" : "error"}}';           
            Test.startTest();
            continuation negCon= (continuation) UnifiedSearch_LC_HUM.searchQuery(paras);
            HttpResponse negResponse = new HttpResponse();
            negResponse.setStatuscode(401);
            negResponse.setHeader('Content-Type', 'application/json');
            negResponse.setBody(mockResponse);
            Test.setContinuationResponse(labelValue, negResponse);
            res =  UnifiedSearch_LC_HUM.processSearchQueryResponse(labels); 
            Map < String, Object > mapResponse = (Map < String, Object > ) JSON.deserializeUntyped(string.valueOf(res));           
	    system.assertEquals(401, mapResponse.get('statusCode'));
            Test.stopTest();           
        }   
    }
   /*
    * Method Name : negativePart2TestsearchQuery
    * Description : This method tests negative scenario of searchQuery method
    * Return type : None
    * Parameter   : None
    */
    @isTest static void negativePart2TestsearchQuery()
    {
        User testUser = [Select Id,FirstName from User where LastName = 'test UnifiedSearchUser4']; 
        system.assertEquals('UnifiedSearchUser20', testUser.FirstName);

        object res;
        System.runAs(testUser)
        {
    		string paras ='{"errorsAsSuccess":false,"method":"POST","uniqueId":"oA23123","queryString":["","visitor=7e6erte"],"requestData":[{"actionCause":"interfaceLoad","actionType":"interface","anonymous":false,"device":"Chrome","mobile":false,"language":"en","responseTime":1000,"originLevel1":"default","originLevel2":"All","originLevel3":"https://humanaservice--coveo--c.visualforce.com/apex/GenericHiddenWidgetComp_VF_HUM?isdtp=vw&sfdcIFrameOrigin=https%3A%2F%2Fhumanaservice--coveo.my.salesforce.com&nonce=e2087fce7059ffc6e28e3fe48beb50b1e6fb6e2da52d453200749a374f161781","originContext":"Search","customData":{"JSUIVersion":"2.8864.5;2.8864.5","context_classification":"","context_intent":""},"searchQueryUid":"2308f482-27f2-4760","queryText":"","advancedQuery":"","resultsPerPage":"","pageNumber":0,"contextual":false,"numberOfResults":2000,"facetState":[]}],"url":"fakeuri/rest/search/v2/html","responseType":"text","requestDataType":"application/json","accessToken":"fzKjcHdjPJKJVaJ2OjK0fz"}';
            string labelValue= 'Continuation-1';
            list< string> labels = new list<string>();
            labels.add(labelValue);
            string mockResponse = '{"message": "error bad request"}';
            Test.startTest();
            continuation negCon= (continuation) UnifiedSearch_LC_HUM.searchQuery(paras);
            HttpResponse negResponse = new HttpResponse();
            negResponse.setStatuscode(401);
            negResponse.setHeader('Content-Type', 'application/json');
            negResponse.setBody(mockResponse);
            Test.setContinuationResponse(labelValue, negResponse);
            res =  UnifiedSearch_LC_HUM.processSearchQueryResponse(labels); 
            Map < String, Object > mapResponse = (Map < String, Object > ) JSON.deserializeUntyped(string.valueOf(res));
            system.assertEquals(401, mapResponse.get('statusCode'));
            Test.stopTest();           
        }   
    }

}