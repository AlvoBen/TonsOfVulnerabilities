/*******************************************************************************************************************************
Apex Class Name : Go365Controller_T_HUM.cls
Version         : 1.0
Created On      : 2015
Function        : Class contains test methods for Go365.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Prachi Bhasin                                        08/10/2016                 Original Version
 ********************************************************************************************************************************/
@isTest
private class Go365Controller_T_HUM
{
    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup static void setup() {                    
        Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        objAcc.Enterprise_ID__c = '213654';
        insert objAcc; 
        system.assert(objAcc.Name == 'testacc');
        
        Account objAccNegative = HUMTestDataHelperTest.getAccount();
        objAccNegative.Name = 'testacc1';
        objAccNegative.Enterprise_ID__c = '213654';
        insert objAccNegative;
        system.assert(objAccNegative.Name == 'testacc1');
        
        Account objAccTest = HUMTestDataHelperTest.getAccount();
        objAccTest.Name = 'testaccNocache';
        objAccTest.Enterprise_ID__c = '213654';
        insert objAccTest;
        system.assert(objAccTest.Name == 'testaccNocache');
        
        Policy__c policy = HUMTestDataHelperTest.getPolicy();
        policy.Group_Name__c = objAcc.id;
        insert policy;
        system.assert(policy.Group_Name__c == objAcc.id);
        
        Policy_Member__c oPolicymem = HUMTestDataHelperTest.getPolicyMember();
        oPolicymem.Effective_Date__c = Date.today().addDays(-1).format();
        oPolicymem.End_Date__c = Date.today().addDays(1).format();
        oPolicymem.Name='testpolmem';
        oPolicymem.Policy__c = policy.id;
        oPolicymem.Member__c = objAcc.id;
        insert oPolicymem;
        system.assert(oPolicymem.Name == 'testpolmem');
        
        insert new HUM_Webservice_Callout__c( Name = 'HUMVitalityService' ,Request_Type__c='POST', Content_Type__c='application/json',Certificate_Name__c = 'Humana_GuidanceCare_Mercury', Active__c = true, End_Point_URL__c = 'https://int-crmservicews.humana.com/VitalityRestService/GetVitalityInformation', Timeout__c = '12000');
        insert new HUMConstants__c( Name = 'HUMWebEmulate' ,StringValue__c='test.humana.com');        
        
        Member_Cache__c memCacheObject = new Member_Cache__c();
        memCacheObject.AccountID__c = objAcc.id;
        memCacheObject.Vitality_Entity_ID__c = '2134567';
        memCacheObject.Member_Gen_Key__c = '2134567';
        memCacheObject.Is_Web_Registered__c = 'YES'; 
        memCacheObject.Web_Last_Login__c  = 'Feb';    
        memCacheObject.Cache_Expiration_Date_Web_Last_Login__c  = System.Now(); 
        insert memCacheObject;        
        system.assert(memCacheObject.Vitality_Entity_ID__c == '2134567');
        
        Member_Cache__c memCacheObject1 = new Member_Cache__c();
        memCacheObject1.AccountID__c = objAccNegative.id;
        memCacheObject1.Member_Gen_Key__c = '2134567';
        memCacheObject1.Is_Web_Registered__c = 'YES'; 
        memCacheObject1.Web_Last_Login__c  = 'Feb';    
        memCacheObject1.Cache_Expiration_Date_Web_Last_Login__c  = System.Now(); 
        insert memCacheObject1;
        system.assert(memCacheObject1.Member_Gen_Key__c == '2134567');
        
        List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();
    
        lstConstanst.Add(new HUMConstants__c(Name='GO365_WEBEMULATE',IntegerValue__c=0,StringValue__c='test.humana.com'));                   
       insert lstConstanst;
       System.assert(lstConstanst.size() > 0 );
    }

    /*
    * This method is used to test the Go365 Controller
    * @param        None
    * @return       None
    */
    static testmethod void testVitalityNegative()
    {
        Account oAcc = [Select ID from Account where Name=: 'testacc1'];
        Go365Controller_C_HUM vController = new Go365Controller_C_HUM();
        Test.startTest();
        Object o1= Go365Controller_C_HUM.getVitalityInformation(oAcc.ID);     
        Test.stopTest(); 
        
        
    }
    
    /*
    * This method is used to test the Go365 Controller getVitalityInformation method
    * @param        None
    * @return       None
    */
    static testmethod void testVitality()
    {
        Account oAcc = [Select ID from Account where Name=: 'testacc'];        
        Test.startTest();
        Object o1= Go365Controller_C_HUM.getVitalityInformation(oAcc.ID);     
        Test.stopTest(); 
        System.assertNotEquals(o1 , NULL); 
        
    }      
    
    /*
    * This method is used to test the Go365 Controller getVitalityInformation method
    * @param        None
    * @return       None
    */
    static testmethod void testWebService() 
    {
        Account oAcc = [Select ID from Account where Name=: 'testacc'];    
        
        // Invoke the continuation by calling the action method
        Continuation conti = (Continuation)Go365Controller_C_HUM.getVitalityInformation(oAcc.ID);             
        // Verify that the continuation has the proper requests
        Map<String, HttpRequest> requests = conti.getRequests();
        system.assert(requests.size() == 1);
                      
    }
    
    /*
    * This method is used to test the Go365 Controller getVitalityInformation method
    * @param        None
    * @return       None
    */
    static testmethod void testrespVitality()
    {
        
        Test.startTest();      
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String json=        '{"Response": {"MemberVitalityData": {'+
        '   "MemberEntityNumber": "2503765337",'+
        '   "VitalityAge": "45",'+
        '   "VitalityBucksBalances":    {'+
        '      "SpendableBucksBalance": "1485",'+
        '      "StatusBucksBalance": "293",'+
        '      "BucksToNextStatus": "0"'+
        '   },'+
        '   "VitalityStatuses":    {'+
        '      "CarryoverStatus": "Blue",'+
        '      "EarnedStatus": "Blue"'+
        '   },'+
        '   "IsHraCompleted": "false",'+
        '   "MemberGoals": {"MemberGoal":    ['+
        '            {'+
        '         "GoalID": "QTS1",'+
        '         "GoalName": "Quit Smoking",'+
        '         "GoalDescription": "Stop Cigarette Smoking - HRA  Intentions",'+
        '         "GoalPointValue": "3140",'+
        '         "Status": "Recommended",'+
        '         "EffectiveFromDateTime": "2012-08-29T08:06:59.690-04:00",'+
        '         "ExternalEndDate": "10-10-2012",'+
        '         "DaysUntillExpiration": "",'+
        '         "internalEndDate": "",'+
        '         "MeasurementStartDate": "",'+
        '         "MonitorEndDate": ""'+
        '      },'+
        '            {'+
        '         "GoalID": "DBP",'+
        '         "GoalName": "Lower your diastolic blood pressure",'+
        '         "GoalDescription": "Diastolic blood pressure - reduce",'+
        '         "GoalPointValue": "400",'+
        '         "Status": "Recommended",'+
        '         "EffectiveFromDateTime": "2013-08-30T01:25:20.265-04:00",'+
        '         "ExternalEndDate": "",'+
        '         "DaysUntillExpiration": "",'+
        '         "internalEndDate": "",'+
        '         "MeasurementStartDate": "",'+
        '         "MonitorEndDate": ""'+
        '      },'+
        '            {'+
        '         "GoalID": "PAH2",'+
        '         "GoalName": "Get Started on your Physical Activity With HumanaVitality",'+
        '         "GoalDescription": "Physical Activity - START goal exercising at >= 30 and < 60 ",'+
        '         "GoalPointValue": "125",'+
        '         "Status": "Recommended",'+
        '         "EffectiveFromDateTime": "2015-02-13T03:02:15.989-05:00",'+
        '         "ExternalEndDate": "",'+
        '         "DaysUntillExpiration": "",'+
        '         "internalEndDate": "",'+
        '         "MeasurementStartDate": "",'+
        '         "MonitorEndDate": ""'+
        '      }'+
        '   ]}'+
        '}}}';

         
        res.setBody(json);
        res.setStatusCode(200);       
        VitalityAppState_DTO_HUM  v = new VitalityAppState_DTO_HUM();
        v.lastLoginDate ='10-10-2013';
        v.MemGenKey ='213';
        v.webRegistered ='YES';
        Go365Controller_C_HUM.checkResponse(res,v);
        
        Test.stopTest();      
    }
     
}