/*******************************************************************************************************************************
Apex Class Name     : SoftphoneLinkBulkAssignAgents_T_HUM 
Version             : 1.0
Created Date        : August 23 2019
Function            : Test class to test scenarios in  SoftphoneLinkBulkAssignAgents_C_HUM class.

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj                                      08/22/2019                  Initial Version
* Akshay Pai                                             08/26/2020                  To fix code coverage issue 
* Joel George                                            09/08/2020                  Code coverage
*******************************************************************************************************************************/
@isTest
public class SoftphoneLinkBulkAssignAgents_T_HUM 
{

    /*
    * Method Name : setUpTestData
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    @testSetup
    public static void setupTestData()
    {
        Softphone_Sites__c softSite = new Softphone_Sites__c();
        softSite.Name = 'SK_3';
        
        insert softSite;
        
        SoftphoneLinks__c softLink = new SoftphoneLinks__c();
        softLink.ServerName__c = 'Server 3';
        softLink.Sites__c = softSite.Id;
        softLink.Name='SK_LINK3';
        softLink.Agent__c = 3;
        
        SoftphoneLinks__c softLink2 = new SoftphoneLinks__c();
        softLink2.ServerName__c = 'Server 3';
        softLink2.Sites__c = softSite.Id;
        softLink2.Name='ZZ_LINK';
        
        List<SoftphoneLinks__c> lstSoftPhoneLink = new List<SoftphoneLinks__c>();
    
        lstSoftPhoneLink.add(softLink);
        lstSoftPhoneLink.add(softLink2);
        
        if(!lstSoftPhoneLink.IsEmpty())
            insert lstSoftPhoneLink;
        
        system.assertEquals(lstSoftPhoneLink[0].Sites__c, softSite.Id);  
        
        Softphone_Agent__c softAgent = new Softphone_Agent__c();
        softAgent.Name = 'SK_A1';
        softAgent.User_Id__c = 'UD_3';
        softAgent.Agent_Extension__c= 'SK_A1';
        softAgent.DB2_Status__c = 'Ready For Insert';
        softAgent.Default_SoftphoneLinks__c = softLink.Id;
        softAgent.Alternate_SoftphoneLinks__c = softLink.Id;
        softAgent.Sites__c = softSite.Id;
        
        Softphone_Agent__c softAgent2 = new Softphone_Agent__c();
        softAgent2.Name = 'SK_A2';
        softAgent2.User_Id__c = 'UD_4';
        softAgent2.Agent_Extension__c= 'SK_A2';
        softAgent2.DB2_Status__c = 'Ready For Insert';
        softAgent2.Default_SoftphoneLinks__c = softLink.Id;
        softAgent2.Alternate_SoftphoneLinks__c = softLink.Id;
        softAgent2.Sites__c = softSite.Id;
        
        
        Softphone_Agent__c softAgent3 = new Softphone_Agent__c();
        softAgent3.Name = 'SK_A3';
        softAgent3.User_Id__c = 'UD_5';
        softAgent3.Agent_Extension__c= 'SK_A3';
        softAgent3.DB2_Status__c = 'Ready For Insert';
        softAgent3.Default_SoftphoneLinks__c = softLink.Id;
        softAgent3.Alternate_SoftphoneLinks__c = softLink.Id;
        softAgent3.Sites__c = softSite.Id;
        
        List<Softphone_Agent__c> lstSoftphoneAgent = new List<Softphone_Agent__c>();
        lstSoftphoneAgent.add(softAgent);
        lstSoftphoneAgent.add(softAgent2);
        lstSoftphoneAgent.add(softAgent3);
        if(!lstSoftphoneAgent.IsEmpty())
            insert lstSoftphoneAgent;
        
        system.assertEquals(lstSoftphoneAgent[0].DB2_Status__c, 'Ready For Insert');  
        
        List<HUM_Webservice_Callout__c> lstwebCallout = new List<HUM_Webservice_Callout__c>();
         HUMConstants__c constantsObj = new HUMConstants__c();
        constantsObj.Name = 'SOFTPHONE_SYNC_BATCH_SIZE';
        constantsObj.IntegerValue__c = 300;
        insert constantsObj;
        system.assertEquals(constantsObj.IntegerValue__c, 300);  
        
        HUM_Webservice_Callout__c softphonebulkCallout = new HUM_Webservice_Callout__c();
        softphonebulkCallout.Name = 'AgentSoftPhoneBulkDBSync';
        softphonebulkCallout.Timeout__c = '12000';
        softphonebulkCallout.Active__c = true;
        softphonebulkCallout.Certificate_Name__c = 'testCertificate';
        softphonebulkCallout.End_Point_URL__c = 'https://testst-crmservicews.humana.com/';
        softphonebulkCallout.Content_Type__c = 'application/json';
        softphonebulkCallout.Request_Type__c='POST';
        lstwebCallout.add(softphonebulkCallout);
        system.assertEquals(softphonebulkCallout.Name,'AgentSoftPhoneBulkDBSync');  
        
        HUM_Webservice_Callout__c softphoneCallout = new HUM_Webservice_Callout__c();
        softphoneCallout.Name = 'AgentSoftPhoneDBSync';
        softphoneCallout.Timeout__c = '12000';
        softphoneCallout.Active__c = true;
        softphoneCallout.Certificate_Name__c = 'testCertificate';
        softphoneCallout.End_Point_URL__c = 'https://testst-crmservicews.humana.com/';
        softphoneCallout.Content_Type__c = 'application/json';
        softphoneCallout.Request_Type__c='POST';
        lstwebCallout.add(softphoneCallout);
        
        if(!lstwebCallout.IsEmpty())
            insert lstwebCallout;
        system.assertEquals(softphoneCallout.Name,'AgentSoftPhoneDBSync');
    }
    
    /*
    * Method Name : testDefaultToDefault
    * Description : This method creates test constructor method
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void testDefaultToDefault()
    {
        
        Test.startTest();   
        initVariables(HUMConstants.HUMSoftphoneLinksTransferD2D);
        Test.stopTest();   
        

    }
    
    /*
    * Method Name : testDefaultToAlternate
    * Description : This method creates test constructor method
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void testDefaultToAlternate()
    {
        
        Test.startTest();   
        initVariables(HUMConstants.HUMSoftphoneLinksTransferD2A);
        Test.stopTest();   
        

    }
    
    /*
    * Method Name : testAlternateToAlternate
    * Description : This method creates test constructor method
    * Return type : None
    * Parameter   : None
    */
    @isTest
    public static void testAlternateToAlternate()
    {
        
        Test.startTest();   
        initVariables(HUMConstants.HUMSoftphoneLinksTransferA2A);
        Test.stopTest();   
        

    }
    
     
     public static void initVariables(String typeOfRequest)
     {
                
    
        PageReference pageRef = Page.SoftphoneLinkBulkAssignment_VF_HUM;
        Test.setCurrentPage(pageRef);

        List<SoftphoneLinks__c> softLinks = new List<SoftphoneLinks__c>();
        softLinks = [Select id,name, ServerName__c,Sites__c,Agent__c from  SoftphoneLinks__c where Name IN ('SK_LINK3','ZZ_LINK') ORDER BY Name];
        system.assertEquals(softLinks.size(), 2);

        
        pageRef.getParameters().put('Id', String.valueOf(softLinks[0].Id));
        ApexPages.StandardController sc = new ApexPages.StandardController(softLinks[0]);
        SoftphoneLinkBulkAssignAgents_C_HUM sLBAA = new SoftphoneLinkBulkAssignAgents_C_HUM(sc);
        SoftphoneLinkBulkAssignAgents_S_HUM sSLBAA = new SoftphoneLinkBulkAssignAgents_S_HUM();
        
        sLBAA.setSelectedOption(typeOfRequest);
        
        List<SelectOption> options = new List<SelectOption>();
        options =  sLBAA.getTransferOptions();
        
        String selectedOption = sLBAA.getSelectedOption();
        sLBAA.fetchNextSection();
        sLBAA.selectedSiteLink = softLinks[1].Id;

        sLBAA.agentsForNewSoftphone();
        
        options  = new List<SelectOption>();
        options =  sLBAA.getlstAlternateSFLinks();
        
        Boolean returnValue = sLBAA.validateErrorScenario();
        sLBAA.intCountOfAgentsToTransfer = 1;
        returnValue = sLBAA.validateErrorScenario();
        sLBAA.formRequest();
        
        
        sLBAA.lstSfAgent = [Select id,Name,User_Id__c,Agent_Extension__c,DB2_Status__c ,Default_SoftphoneLinks__c , Alternate_SoftphoneLinks__c ,Sites__c 
                            from Softphone_Agent__c ];
        system.assertEquals(sLBAA.lstSfAgent.size(), 3);
        
        Continuation con = sLBAA.transferDTDAgents2();
        
        sLBAA.allRequest = true;
        Object ob = (object)sSLBAA.parseSoftphoneTransfer(sLBAA);
        sLBAA.allRequest = false;
        ob = (object)sSLBAA.parseSoftphoneTransfer(sLBAA);
        
        con = (continuation)sSLBAA.invokeSoftPhoneService(sLBAA);       //Creating continuation instance
        HttpResponse res = new HttpResponse();
        res.setBody('{ "insertList":[  ], "updateList":[ { "AgentId":"JXA6843", "UserId":"UD_3" }, { "AgentId":"JXA7486", "UserId":"UD_5" } ], "deleteList":[  ], "syncList":[  ] }');
         
        Test.setContinuationResponse(sLBAA.sConRequest, res);       //Sets mock response
        Test.invokeContinuationMethod(sLBAA, con);      //Invoke continuation method
        
        res.setBody('{ "updateList":[  ], "insertList":[ { "AgentId":"JXA6843", "UserId":"UD_3" }, { "AgentId":"JXA7486", "UserId":"UD_4" } ], "deleteList":[  ], "syncList":[  ] }');
         
        Test.setContinuationResponse(sLBAA.sConRequest, res);       //Sets mock response
        Test.invokeContinuationMethod(sLBAA, con);      //Invoke continuation method
        
        res.setBody('{ "updateList":[  ], "deleteList":[ { "AgentId":"JXA6843", "UserId":"UD_3" }, { "AgentId":"JXA7486", "UserId":"UD_4" } ], "insertList":[  ], "syncList":[  ] }');
         
        Test.setContinuationResponse(sLBAA.sConRequest, res);       //Sets mock response
        Test.invokeContinuationMethod(sLBAA, con);      //Invoke continuation method
        
        res.setBody('error');
         
        Test.setContinuationResponse(sLBAA.sConRequest, res);       //Sets mock response
        Test.invokeContinuationMethod(sLBAA, con);      //Invoke continuation method
        
        res.setBody('{ "updateList":[ { "AgentId":"JXA6843", "UserId":"UD_3" , "ErrorDesc":"JXA6843" }, { "AgentId":"JXA7486", "UserId":"UD_4" , "ErrorDesc":"JXA6843" } ], "deleteList":[ { "AgentId":"JXA6843", "UserId":"UD_3" , "ErrorDesc":"JXA6843" }, { "AgentId":"JXA7486", "UserId":"UD_4" , "ErrorDesc":"JXA6843" } ], "insertList":[ { "AgentId":"JXA6843", "UserId":"UD_3" , "ErrorDesc":"JXA6843" }, { "AgentId":"JXA7486", "UserId":"UD_5" , "ErrorDesc":"JXA6843" } ], "syncList":[  ] }');
         
        Test.setContinuationResponse(sLBAA.sConRequest, res);       //Sets mock response
        Test.invokeContinuationMethod(sLBAA, con);      //Invoke continuation method
        
        sLBAA.allRequest = true;
        
        con = (continuation)sSLBAA.invokeSoftPhoneService(sLBAA);     
        
        res.setBody('{ "updateList":[ { "AgentId":"JXA6843", "UserId":"UD_3" , "ErrorDesc":"JXA6843" }, { "AgentId":"JXA7486", "UD_4":"JXA7486" , "ErrorDesc":"JXA6843" } ], "deleteList":[ { "AgentId":"JXA6843", "UserId":"UD_5" , "ErrorDesc":"JXA6843" }, { "AgentId":"JXA7486", "UD_3":"JXA7486" , "ErrorDesc":"JXA6843" } ], "insertList":[ { "AgentId":"JXA6843", "UserId":"UD_4" , "ErrorDesc":"JXA6843" }, { "AgentId":"JXA7486", "UD_3":"JXA7486" , "ErrorDesc":"JXA6843" } ], "syncList":[  ] }');
         
        Test.setContinuationResponse(sLBAA.sConRequest, res);       //Sets mock response
        Test.invokeContinuationMethod(sLBAA, con);      //Invoke continuation method
        
        res.setBody('{ "ErrorDesc":"err" }');  
        Test.setContinuationResponse(sLBAA.sConRequest, res);       //Sets mock response
        Test.invokeContinuationMethod(sLBAA, con);      //Invoke continuation method
         
     }
}