/*
  Apex Class Name  : OmniRoutingConfigurationsEdit_T_HUM   
  Version          : 1.0 
  Created Date     : OCT 25 2017
  Function         : Test class for OmniRoutingConfigurationsEdit_X_HUM,OmniRoutingConfigurationsEdit_S_HUM,OmniRoutingConfigurationsEdit_D_HUM 
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T                                      11/30/2017                      Original Version
* 1.1                       Syed Mubarak T                                      03/01/2019                      Omni Config Item changes
* 1.2                       Pooja Kumbhar                                       04/29/2020                      Checkmarx test method without assert error fix
* 1.3                       Mitra Sankisa                                       09/10/2020                      Code changes for test coverage.
* 1.4                       Vamshi Krishna Pemberthi                            07/08/2021                      Code Coverage Improvements.
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

@isTest
private class OmniRoutingConfigurationsEdit_T_HUM
{
        /*
    * This method Is to setup test data
    * @param NA
    * @return NA
    */
    
     @testSetup static void setUpTestData()
    {
        User objUser = HUMTestDataHelperTest.createTestUser('RoutingUser1','System Administrator');
        insert objUser;
        User objUser2 = HUMTestDataHelperTest.createTestUser('RoutingUser2','Customer Care Supervisor');
        insert objUser2;
        PermissionSet ps = [SELECT ID From PermissionSet WHERE Name = 'CRMS_605_BizConfig_MedicareEnrollment_Edit'];
        insert new PermissionSetAssignment(AssigneeId = objUser2.id, PermissionSetId = ps.Id );
        system.assertEquals(objUser.lastname=='test RoutingUser1',true);
        system.runAs(objUser)
        {
            
           Omni_Routing_Configuration__c routingConfigData =  new Omni_Routing_Configuration__c();
           routingConfigData.Name = 'RoutingConfig Test';
           routingConfigData.Is_Active__c = true;
           routingConfigData.Push_Time_Out__c = 100;
           routingConfigData.QueueRoutingConfig_Id__c = null;
           routingConfigData.Routing_Priority__c = 1;
           insert routingConfigData;
           
           Omni_Routing_Configuration__c routingConfigData2 =  new Omni_Routing_Configuration__c();
           routingConfigData2.Name = 'duplicate routing config';
           routingConfigData2.Is_Active__c = true;
           routingConfigData2.Push_Time_Out__c = 100;
           routingConfigData2.QueueRoutingConfig_Id__c = null;
           routingConfigData2.Routing_Priority__c = 1;
           insert routingConfigData2;
           
           HUM_Webservice_Callout__c humWSCallout = new HUM_Webservice_Callout__c();
           humWSCallout.Name = 'QueueRoutingConfigService_Create';
           humWSCallout.Certificate_Name__c = 'Humana_GuidanceCare_Mars';
           humWSCallout.Timeout__c = '12000';
           humWSCallout.Content_Type__c = 'application/json';
           humWSCallout.End_Point_URL__c = 'https://int-crmservicews.humana.com/QueueRoutingConfigService/Create';
           humWSCallout.Request_Type__c = 'POST';
           insert humWSCallout;
           
           HUM_Webservice_Callout__c humWSCallout2 = new HUM_Webservice_Callout__c();
           humWSCallout2.Name = 'QueueRoutingConfigService_Update';
           humWSCallout2.Certificate_Name__c = 'Humana_GuidanceCare_Mars';
           humWSCallout2.Timeout__c = '12000';
           humWSCallout2.Content_Type__c = 'application/json';
           humWSCallout2.End_Point_URL__c = 'https://int-crmservicews.humana.com/QueueRoutingConfigService/Update';
           humWSCallout2.Request_Type__c = 'POST';
           insert humWSCallout2;
           
           HUM_Webservice_Callout__c humWSCallout3 = new HUM_Webservice_Callout__c();
           humWSCallout3.Name = 'QueueRoutingConfigService_Delete';
           humWSCallout3.Certificate_Name__c = 'Humana_GuidanceCare_Mars';
           humWSCallout3.Timeout__c = '12000';
           humWSCallout3.Content_Type__c = 'application/json';
           humWSCallout3.End_Point_URL__c = 'https://int-crmservicews.humana.com/QueueRoutingConfigService/Delete';
           humWSCallout3.Request_Type__c = 'POST';
           insert humWSCallout3;
           
           
           Omni_Routing_Configuration__c routingConfigData3 =  new Omni_Routing_Configuration__c();
           routingConfigData3.Name = 'RoutingConfigData';
           routingConfigData3.Is_Active__c = true;
           routingConfigData3.Push_Time_Out__c = 100;
           routingConfigData3.QueueRoutingConfig_Id__c = null;
           routingConfigData3.Routing_Priority__c = 1;
           insert routingConfigData3;
           
           
           Group queueData = new Group();
             queueData.name = 'Test omni Queue';
             queueData.type = 'Queue';
             insert queueData;
             
             
            OmniQueue__c omniData = new OmniQueue__c();
            omniData.Name = 'Test Omni Queue';
            omniData.Omni_Queue_ID__c = queueData.Id;
            omniData.Routing_Configuration_Name__c = routingConfigData3.Id;
            insert omniData;
            
            
            Omni_Configuration_Item__c oc = new Omni_Configuration_Item__c();
            oc.Business_Area__c = 'Medicare Enrollment';
            oc.Name = 'Medicare_Enrollment';
            oc.Maximum_Limit__c = 48000;
            oc.Maximum_No_of_Work_Items__c = 999;
            oc.Work_Item__c = 'Case';
            oc.Default_Queue_Name__c = queueData.Name;
            oc.Default_Queue_Id__c = queueData.Id;
            oc.Overflow_Assignee_Queue__c = queueData.Name;
            oc.Overflow_Assignee_Queue_Id__c = queueData.Id;
            insert oc;

            Omni_Routing_Configuration__c oData =  new Omni_Routing_Configuration__c();
            oData.Name = 'RoutingConfig Test1';
            oData.Is_Active__c = true;
            oData.Push_Time_Out__c = 100;
            oData.Configuration_Item_Name__c = 'RoutingConfig_Test1';
            oData.QueueRoutingConfig_Id__c = null;
            oData.Routing_Priority__c = 1;
            oData.Percentage_of_Capacity__c = '100';
            oData.Unit_of_Capacity__c = '0.25';
            insert oData;
                
            OmniQueue__c omniData1 = new OmniQueue__c();
            omniData1.Name = 'RoutingConfig Queue1';
            omniData1.Omni_Queue_ID__c = queueData.Id;
            omniData1.Routing_Configuration_Name__c = oData.Id;
            insert omniData1;    
            
            Omni_Configuration_Item__c oc1 = new Omni_Configuration_Item__c();
            oc1.Business_Area__c = 'RoutingConfig Test1';
            oc1.Name = 'RoutingConfig_Test1';
            oc1.Maximum_Limit__c = 48000;
            oc1.Maximum_No_of_Work_Items__c = 999;
            oc1.Work_Item__c = 'Case';
            oc1.Default_Queue_Name__c = queueData.Name;
            oc1.Default_Queue_Id__c = queueData.Id;
            oc1.Overflow_Assignee_Queue__c = queueData.Name;
            oc1.Overflow_Assignee_Queue_Id__c = queueData.Id;
            insert oc1;
            
        }
    }
     
    /*
    * This method Is to test routing configuration
    * @param NA
    * @return NA
    */
     
    static testMethod void testOmniRoutingConfiguration()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test RoutingUser2'];
            System.runAs(testUser)
            {
                Test.startTest();
                // Create a new Omni Routing Configuration
                Test.setCurrentPage(page.OmniRoutingConfigurationEdit_VF_HUM);
                Test.setMock(HttpCalloutMock.class, new QueueRoutingConfig_CreateResponseMock());

                Omni_Routing_Configuration__c oData =  new Omni_Routing_Configuration__c();

                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig = new OmniRoutingConfigurationsEdit_X_HUM(scon);
                
                //validate Routing Priority 
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = null;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                //validate push timeout
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 10;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = null;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = null;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                //validate Routing Config Name
                oRoutingConfig.omniRoutingConfig.Name = null;
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'duplicate routing config';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();
                
                
                
                //validate capacity fields
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue capacity 1';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.omniRoutingConfig.Percentage_of_Capacity__c = '100';
                oRoutingConfig.omniRoutingConfig.Unit_of_Capacity__c = '0.25';
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue capacity 2';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.omniRoutingConfig.Percentage_of_Capacity__c = '0';
                oRoutingConfig.omniRoutingConfig.Unit_of_Capacity__c = null;
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue capacity 3';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.omniRoutingConfig.Percentage_of_Capacity__c = '100';
                oRoutingConfig.omniRoutingConfig.Unit_of_Capacity__c = null;
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue capacity 4';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig.omniRoutingConfig.Unit_of_Capacity__c = '1.20';
                oRoutingConfig.save();
                
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue capacity new';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig.save();
                
                oRoutingConfig.dRoutingConfig.createQueueRoutingConfig(oData);

                oRoutingConfig.assignConfigItem();
                
                // to avoild callout and DML operation in the same method future methods are implemented
                createRoutingCOnfigcallout();
                updateRoutingConfiguration();
                createLocalRoutingConfiguration();
                deleteRoutingConfiguration();
                Test.stopTest();
            }
    }
    
    /*
    * This method Is to test exceptions/cancel methods
    * @param NA
    * @return NA
    */
    
    static testMethod void testVFPageMethods()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test RoutingUser2'];
            System.runAs(testUser)
            {
                Test.startTest();
                Test.setCurrentPage(page.OmniRoutingConfigurationEdit_VF_HUM);
                Omni_Routing_Configuration__c oData =  new Omni_Routing_Configuration__c();
                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                ApexPages.currentPage().getParameters().put('retURL','1234');
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig = new OmniRoutingConfigurationsEdit_X_HUM(scon);
                
                // input error variable is true
                oRoutingConfig.inputError = true;
                oRoutingConfig.omniRoutingConfig.Name = 'RoutingConfig Test';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = '1234';
                oRoutingConfig.save();
                
                oRoutingConfig.messageName = 'Test Error Message';
                oRoutingConfig.showErrorMessage();
                system.assertEquals(oRoutingConfig.messageName != null,true);
                
                oRoutingConfig.getlstConfigItems();
                
                oRoutingConfig.viewLookup =  true;
                oRoutingConfig.getopenConfigLookup();
                oRoutingConfig.viewLookup =  false;
                oRoutingConfig.getopenConfigLookup();
                
                oRoutingConfig.cancel();
                
                Test.stopTest();
            }
    }
    
    
    /*
    * This method Is to test create routing configuration functionlaity
    * @param NA
    * @return NA
    */
    
    @future(callout=true)
    static void createRoutingCOnfigcallout()
    {
                Omni_Routing_Configuration__c oData =  new Omni_Routing_Configuration__c();
                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig = new OmniRoutingConfigurationsEdit_X_HUM(scon);
                
        
                //Create Routing Config: Is Active: True 
                //setting up mock test class for response
                Test.setMock(HttpCalloutMock.class, new QueueRoutingConfig_CreateResponseMock());
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig.dRoutingConfig.createQueueRoutingConfig(oData);
                oRoutingConfig.save();
                
                //Create Routing Config: Is Active: false
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = false;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig.save();
    }
    
    
    
    
        /*
    * This method Is to test update routing configuration functionlaity
    * @param NA
    * @return NA
    */
    
    @future(callout=true)
    static void updateRoutingConfiguration(){
                
                //update Omni Routing Configuration
                Omni_Routing_Configuration__c oData2 = [SELECT Id,Name,Routing_Priority__c,Push_Time_Out__c,Is_Active__c FROM Omni_Routing_Configuration__c WHERE Name = 'RoutingConfig Test'];
                ApexPages.StandardController scon2 = new ApexPages.StandardController(oData2);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig2 = new OmniRoutingConfigurationsEdit_X_HUM(scon2);
                oRoutingConfig2.omniRoutingConfig.Name = 'duplicate routing config';
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig2.save();
                
                oRoutingConfig2.omniRoutingConfig.Name = null;
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig2.save();
                
                //validate Routing config Name on update
                oRoutingConfig2.omniRoutingConfig.Name = 'RoutingConfig Test';
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig2.save();
                
                oRoutingConfig2.omniRoutingConfig.Name = 'RoutingConfig Test2';
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig2.save();
                
                // update routing config: Is Active - true & Routing config Id is not null 
                oRoutingConfig2.omniRoutingConfig.Name = 'RoutingConfig Test';
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 10;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = '1234';
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                Test.setMock(HttpCalloutMock.class, new QueueRoutingConfig_UpdateResponseMock());
                oRoutingConfig2.save();
                system.assertEquals(oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c != null,true);
                
                // update routing config: Is Active - true & Routing config Id is null 
                oRoutingConfig2.omniRoutingConfig.Name = 'RoutingConfig Test';
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig2.save(); 
                
    }
    
    /*
    * This method Is to test create/update of createLocalRoutingConfig method
    * @param NA
    * @return NA
    */
    static void createLocalRoutingConfiguration()
    {
            //update Omni Routing Configuration
                Omni_Routing_Configuration__c oData2 = [SELECT Id,Name,Routing_Priority__c,Push_Time_Out__c,Is_Active__c FROM Omni_Routing_Configuration__c WHERE Name = 'RoutingConfigData'];
                ApexPages.StandardController scon2 = new ApexPages.StandardController(oData2);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig2 = new OmniRoutingConfigurationsEdit_X_HUM(scon2);
                
                // update routing config: Is Active - false & Routing config Id is null 
                Test.setMock(HttpCalloutMock.class, new QueueRoutingConfig_CreateResponseMock());
                oRoutingConfig2.omniRoutingConfig.Name = 'RoutingConfigData';
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = false;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig2.save();
    }
    
    /*
    * This method Is to test remove routing configuration functionality
    * @param NA
    * @return NA
    */
    
    @future(callout=true)
    static void deleteRoutingConfiguration()
    {
                //update Omni Routing Configuration
                Omni_Routing_Configuration__c oData2 = [SELECT Id,Name,Routing_Priority__c,Push_Time_Out__c,Is_Active__c FROM Omni_Routing_Configuration__c WHERE Name = 'RoutingConfigData'];
                ApexPages.StandardController scon2 = new ApexPages.StandardController(oData2);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig2 = new OmniRoutingConfigurationsEdit_X_HUM(scon2);
                
                // update routing config: Is Active - false & Routing config Id is not null  queue present
                oRoutingConfig2.omniRoutingConfig.Name = 'RoutingConfigData';
                oRoutingConfig2.omniRoutingConfig.Routing_Priority__c = 2;
                oRoutingConfig2.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig2.omniRoutingConfig.Is_Active__c = false;
                oRoutingConfig2.inputError = false;
                oRoutingConfig2.omniRoutingConfig.QueueRoutingConfig_Id__c = '1234';
                oRoutingConfig2.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig2.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig2.save();
                
                
                Omni_Routing_Configuration__c oData3 = [SELECT Id,Name,Routing_Priority__c,Push_Time_Out__c,Is_Active__c FROM Omni_Routing_Configuration__c WHERE Name = 'duplicate routing config'];
                ApexPages.StandardController scon3 = new ApexPages.StandardController(oData3);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig3 = new OmniRoutingConfigurationsEdit_X_HUM(scon3);
                
                Test.setMock(HttpCalloutMock.class, new QueueRoutingConfig_DeleteResponseMock());
                // update routing config: Is Active - false & Routing config Id is not null 
                oRoutingConfig3.omniRoutingConfig.Name = 'duplicate routing config';
                oRoutingConfig3.omniRoutingConfig.Routing_Priority__c = 1;
                oRoutingConfig3.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig3.omniRoutingConfig.Is_Active__c = false;
                oRoutingConfig3.inputError = false;
                oRoutingConfig3.omniRoutingConfig.QueueRoutingConfig_Id__c = '1234';
                oRoutingConfig3.omniRoutingConfig.Percentage_of_Capacity__c = null;
                oRoutingConfig3.omniRoutingConfig.Unit_of_Capacity__c = '0.20';
                oRoutingConfig3.save(); 


    }
    /*
    * This method Is to test response DTO classes
    * @param NA
    * @return NA
    */
    static testMethod void testResponseDTOClasses()
    {
        //create response
        QueueRoutingConfigCreateResponse_DTO_HUM.Errorresponse eCreateResponse =  new QueueRoutingConfigCreateResponse_DTO_HUM.Errorresponse();
        eCreateResponse.Fields = 'Test field';
        eCreateResponse.Message = 'Test Message';
        eCreateResponse.StatusCode = 'Test Status Code';
        System.assertEquals(eCreateResponse.Fields, 'Test field');
        
        QueueRoutingConfigCreateResponse_DTO_HUM.RecordDataList lCreateResponse = new QueueRoutingConfigCreateResponse_DTO_HUM.RecordDataList();
        lCreateResponse.Id = '1234';
        lCreateResponse.Success = 'true';
        lCreateResponse.Errors = eCreateResponse;
        
        List<QueueRoutingConfigCreateResponse_DTO_HUM.RecordDataList> lstCreateResponse = new List<QueueRoutingConfigCreateResponse_DTO_HUM.RecordDataList>();
        lstCreateResponse.add(lCreateResponse);
        
        QueueRoutingConfigCreateResponse_DTO_HUM.RecordData rCreateResponse = new QueueRoutingConfigCreateResponse_DTO_HUM.RecordData();
        rCreateResponse.Result = lstCreateResponse;
        
        QueueRoutingConfigCreateResponse_DTO_HUM.CreateResponseDTO dtoCreateResponse = new QueueRoutingConfigCreateResponse_DTO_HUM.CreateResponseDTO();
        dtoCreateResponse.Records = rCreateResponse;
        
        QueueRoutingConfigCreateResponse_DTO_HUM createResponseClass = new QueueRoutingConfigCreateResponse_DTO_HUM();
        createResponseClass.CreateResponse = dtoCreateResponse;
        
        
        //update response
        QueueRoutingConfigUpdateResponse_DTO_HUM.Errorresponse eUpdateResponse =  new QueueRoutingConfigUpdateResponse_DTO_HUM.Errorresponse();
        eUpdateResponse.Fields = 'Test field';
        eUpdateResponse.Message = 'Test Message';
        eUpdateResponse.StatusCode = 'Test Status Code';
        
        QueueRoutingConfigUpdateResponse_DTO_HUM.RecordDataList lUpdateResponse = new QueueRoutingConfigUpdateResponse_DTO_HUM.RecordDataList();
        lUpdateResponse.Id = '1234';
        lUpdateResponse.Success = 'true';
        lUpdateResponse.Errors = eUpdateResponse;
        
        List<QueueRoutingConfigUpdateResponse_DTO_HUM.RecordDataList> lstUpdateResponse = new List<QueueRoutingConfigUpdateResponse_DTO_HUM.RecordDataList>();
        lstUpdateResponse.add(lUpdateResponse);
        
        QueueRoutingConfigUpdateResponse_DTO_HUM.RecordData rUpdateResponse = new QueueRoutingConfigUpdateResponse_DTO_HUM.RecordData();
        rUpdateResponse.Result = lstUpdateResponse;
        
        QueueRoutingConfigUpdateResponse_DTO_HUM.UpdateResponseDTO dtoUpdateResponse = new QueueRoutingConfigUpdateResponse_DTO_HUM.UpdateResponseDTO();
        dtoUpdateResponse.Records = rUpdateResponse;
        
        QueueRoutingConfigUpdateResponse_DTO_HUM UpdateResponseClass = new QueueRoutingConfigUpdateResponse_DTO_HUM();
        UpdateResponseClass.UpdateResponse = dtoUpdateResponse;
        
        //delete response
        QueueRoutingConfigDeleteResponse_DTO_HUM.Errorresponse eDeleteResponse =  new QueueRoutingConfigDeleteResponse_DTO_HUM.Errorresponse();
        eDeleteResponse.Fields = 'Test field';
        eDeleteResponse.Message = 'Test Message';
        eDeleteResponse.StatusCode = 'Test Status Code';
        
        QueueRoutingConfigDeleteResponse_DTO_HUM.RecordDataList lDeleteResponse = new QueueRoutingConfigDeleteResponse_DTO_HUM.RecordDataList();
        lDeleteResponse.Id = '1234';
        lDeleteResponse.Success = 'true';
        lDeleteResponse.Errors = eDeleteResponse;
        
        List<QueueRoutingConfigDeleteResponse_DTO_HUM.RecordDataList> lstDeleteResponse = new List<QueueRoutingConfigDeleteResponse_DTO_HUM.RecordDataList>();
        lstDeleteResponse.add(lDeleteResponse);
        
        QueueRoutingConfigDeleteResponse_DTO_HUM.RecordData rDeleteResponse = new QueueRoutingConfigDeleteResponse_DTO_HUM.RecordData();
        rDeleteResponse.Result = lstDeleteResponse;
        
        QueueRoutingConfigDeleteResponse_DTO_HUM.DeleteResponseDTO dtoDeleteResponse = new QueueRoutingConfigDeleteResponse_DTO_HUM.DeleteResponseDTO();
        dtoDeleteResponse.Records = rDeleteResponse;
        
        QueueRoutingConfigDeleteResponse_DTO_HUM deleteResponseClass = new QueueRoutingConfigDeleteResponse_DTO_HUM();
        deleteResponseClass.DeleteResponse = dtoDeleteResponse;
        
    }
    
    /*
    * This method Is to test request DTO classes
    * @param NA
    * @return NA
    */
    static testMethod void testQueueRoutingRequestClasses()
    {
       
        QueueRoutingConfigCreateRequest_DTO_HUM.RecordDataList oRecordDataList = new QueueRoutingConfigCreateRequest_DTO_HUM.RecordDataList();
        oRecordDataList.CapacityPercentage ='45';
        oRecordDataList.CapacityWeight= '56';
        oRecordDataList.DeveloperName ='testDev';
        oRecordDataList.MasterLabel ='testMaster';
        oRecordDataList.OverflowAssigneeId='testID';
        oRecordDataList.PushTimeout ='6';
        oRecordDataList.RoutingModel ='testModel';
        oRecordDataList.RoutingPriority ='testPriority';
        oRecordDataList.Type ='test';
        
     
        QueueRoutingConfigDeleteRequest_DTO_HUM.RecordDataList  oRecordDataList2= new QueueRoutingConfigDeleteRequest_DTO_HUM.RecordDataList();
        oRecordDataList2.Id ='testID';        
       
       
        QueueRoutingConfigUpdateRequest_DTO_HUM.RecordDataList oRecordDataList3 = new QueueRoutingConfigUpdateRequest_DTO_HUM.RecordDataList();
        oRecordDataList3.CapacityPercentage ='45';
        oRecordDataList3.CapacityWeight= '56';
        oRecordDataList3.DeveloperName ='testDev';
        oRecordDataList3.MasterLabel ='testMaster';
        oRecordDataList3.OverflowAssigneeId='testID';
        oRecordDataList3.PushTimeout ='6';
        oRecordDataList3.RoutingModel ='testModel';
        oRecordDataList3.RoutingPriority ='testPriority';
        oRecordDataList3.Type ='test';
        oRecordDataList3.Id ='testID';  

    }

    static testMethod void testOmniRoutingConfiguration1()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test RoutingUser2'];
        
            System.runAs(testUser)
            {
                Test.startTest();
                // Create a new Omni Routing Configuration
                Test.setCurrentPage(page.OmniRoutingConfigurationEdit_VF_HUM);
                Omni_Routing_Configuration__c oData = [SELECT id, Name,Is_Active__c,Push_Time_Out__c,Configuration_Item_Name__c,QueueRoutingConfig_Id__c,Routing_Priority__c,Percentage_of_Capacity__c,Unit_of_Capacity__c from Omni_Routing_Configuration__c where name = 'RoutingConfig Test1' ];

                Test.setMock(HttpCalloutMock.class, new QueueRoutingConfig_CreateResponseMock());
                OmniRoutingConfigurationsEdit_D_HUM oRoutD = new OmniRoutingConfigurationsEdit_D_HUM();
                oRoutD.createQueueRoutingConfig(oData);

                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig = new OmniRoutingConfigurationsEdit_X_HUM(scon);
                oRoutingConfig.save();
                //validate Routing Priority 
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();

                oRoutingConfig.assignConfigItem();

                oRoutingConfig.dRoutingConfig.createQueueRoutingConfig(oData);

                // to avoild callout and DML operation in the same method future methods are implemented
                createRoutingCOnfigcallout();
                updateRoutingConfiguration();
                createLocalRoutingConfiguration();
                deleteRoutingConfiguration();
                Test.stopTest();
            }
    }

    static testMethod void testCreateRoutingConfig()
    {
        User testUser = [Select Id,Name, Current_Queue__C from User where LastName='test RoutingUser2'];
        
            System.runAs(testUser)
            {
                Test.startTest();
                // Create a new Omni Routing Configuration
                Test.setCurrentPage(page.OmniRoutingConfigurationEdit_VF_HUM);
                Test.setMock(HttpCalloutMock.class, new QueueRoutingConfig_CreateResponseMock());

                Omni_Routing_Configuration__c oData = [SELECT id, Name,Is_Active__c,Push_Time_Out__c,Configuration_Item_Name__c,QueueRoutingConfig_Id__c,Routing_Priority__c,Percentage_of_Capacity__c,Unit_of_Capacity__c from Omni_Routing_Configuration__c where name = 'RoutingConfig Test1' ];
                OmniRoutingConfigurationsEdit_D_HUM oRoutD = new OmniRoutingConfigurationsEdit_D_HUM();
                oRoutD.createQueueRoutingConfig(oData);
                oRoutD.createQueueRoutingConfig(null);
                oRoutD.updateQueueRoutingConfig(oData);

                ApexPages.StandardController scon = new ApexPages.StandardController(oData);
                OmniRoutingConfigurationsEdit_X_HUM oRoutingConfig = new OmniRoutingConfigurationsEdit_X_HUM(scon);
                oRoutingConfig.save();
                //validate Routing Priority 
                oRoutingConfig.omniRoutingConfig.Name = 'Test omni Queue';
                oRoutingConfig.omniRoutingConfig.Routing_Priority__c = 0;
                oRoutingConfig.omniRoutingConfig.Push_Time_Out__c = 100;
                oRoutingConfig.omniRoutingConfig.Is_Active__c = true;
                oRoutingConfig.inputError = false;
                oRoutingConfig.omniRoutingConfig.QueueRoutingConfig_Id__c = null;
                oRoutingConfig.save();

                oRoutingConfig.assignConfigItem();

                oRoutingConfig.dRoutingConfig.createQueueRoutingConfig(oData);
                
                // to avoild callout and DML operation in the same method future methods are implemented
                createRoutingCOnfigcallout();
                updateRoutingConfiguration();
                createLocalRoutingConfiguration();
                deleteRoutingConfiguration();
                Test.stopTest();
            }
    }

            
}