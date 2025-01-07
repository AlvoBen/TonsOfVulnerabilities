/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailUnregisterParticipants_T_HUM 
* Version          : 1.0
* Created Date     : 24th June, 2021
* Function         : This class is used as a test class for Unregister Participant rest service
* Test Class       : 
***********************************************************************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                                          06/24/2021            Original Version 2389080
**********************************************************************************************************************************/

@isTest
public class CRMRetailUnregisterParticipants_T_HUM {
    /**
    * This method is used to insert the data only once and called automatically in all methods 
    *
    * @param  None      
    * @return  Void
    */
    
    @testSetup static void setup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','ETL API Access');
        insert oUser;
        System.assertNotEquals(oUser, null);
        
        System.runAs(oUser) 
        {
            list<Event> lstEvent = new list<Event>();
            list<Storefront_I_Reason__c> lstIntReason = new list<Storefront_I_Reason__c>();
            list<Storefront_I_Type__c> lstIntType = new list<Storefront_I_Type__c>();
            list<Storefront_Location__C> lstLocation = new List<Storefront_Location__C>();
            List<Storefront_Interaction__c> lstInt = new List<Storefront_Interaction__c>();
            List<Account> lstAcc = new List<Account>();
            
            Id recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            
            Account objAcc = CRMRetail_TestDataFactory_H_HUM.createObjAccount(recordTypeId,'04/06/1985','Roger','Walter','12209','','','',false);
            lstAcc.add(objAcc);
            insert lstAcc;
			System.assertEquals(lstAcc.size(), 1);

            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Education');
            lstIntType.add(objIntType);
            insert lstIntType;
            System.assertEquals(lstIntType.size(), 1);

            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('Consumer Education',null);
            objIntReason.Interaction_Type__c = lstIntType[0].Id;
            lstIntReason.add(objIntReason);
            insert lstIntReason;
            System.assertEquals(lstIntReason.size(), 1);

            Storefront_Location__C objLocation = CRMRetail_TestDataFactory_H_HUM.createLocation('Atlanta','Street no. 2','Texas','67890','12345','LA',25);
            lstLocation.add(objLocation);
			insert lstLocation;
            System.assertEquals(lstLocation.size(), 1);
			
			Storefront_Interaction__c objInteraction = CRMRetail_TestDataFactory_H_HUM.createInteraction(lstAcc[0].Id,lstLocation[0].id,lstIntReason[0].id,false,'Member',Date.today());
            lstInt.add(objInteraction);
            insert lstInt;
            System.assertEquals(lstInt.size(), 1);
        }
    }
    
    /*
    * Method Name   :   testInteractionDelete
    * Description   :   Creates an instance of Interaction object and returns it
    * Return Type   :   NA
    */
    
    @isTest static void testInteractionDelete()
	{
		User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {   
            
            list<Storefront_Interaction__c> lstInt = [SELECT Id, Reason__r.Name FROM Storefront_Interaction__c WHERE Reason__r.Name = 'Consumer Education' LIMIT 1];
            System.assertEquals(lstInt.size(), 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.interactionId = lstInt[0].Id;
           
            RestRequest req = new RestRequest();
        
            String JSONMsg = System.JSON.serialize(request);
	
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UnregisterParticipant';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
      
            CRMRetailUnregisterParticipants_I_HUM.unregisterParticpants();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM unregisterPart = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM(); 
            unregisterPart = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM.class);
            system.assertEquals(unregisterPart.isSuccess, true); 
            
            Test.StopTest();
        }
	}
    
	/*
    * Method Name   :   testInteractionDontMatch
    * Description   :   Send incorrect Interaction Id which does not exists in database
    * Return Type   :   NA
    */
    
    @isTest static void testInteractionDontMatch()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailUnregisterParticipantsReq_DTO_HUM();
            request.sourceSystem = 'VNC';
            request.interactionId = '00Uc00000091ABCDEF';
           
            RestRequest req = new RestRequest();
        
            String JSONMsg = System.JSON.serialize(request);
        
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UnregisterParticipant';  
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
		
            Test.StartTest();               
      
            CRMRetailUnregisterParticipants_I_HUM.unregisterParticpants();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM unregisterPart = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM(); 
            unregisterPart = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM.class);
            system.assertEquals(unregisterPart.isSuccess, false); 
            
            Test.StopTest();
        }
    }
    
    /*
    * Method Name   :   testDeleteInteractionException
    * Description   :   Cover the exception part 
    * Return Type   :   NA
    */
    
    @isTest static void testDeleteInteractionException()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            RestRequest req = new RestRequest();
            
            String JSONMsg = '';
            
            RestResponse res = new RestResponse();
            req.requestURI = '/CRMRetailServices_V1.0/UnregisterParticipant';  
            req.httpMethod = 'POST';
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.StartTest();               
            
            CRMRetailUnregisterParticipants_I_HUM.unregisterParticpants();
            
            RestResponse response = RestContext.response;           
            CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM unregisterPart = new CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM(); 
            unregisterPart = (CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailUnregisterParticipantsRes_DTO_HUM.class);
            system.assertEquals(unregisterPart.isSuccess, false); 
            
            Test.StopTest();
        }
    }
}