/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailCreateIntTypeReason_T_HUM 
* Version          : 1.0
* Created Date     : 10t June, 2021
* Function         : This class is used as a test class for create interaction type reason rest service
* Test Class       : 
***********************************************************************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                                          06/10/2021            Original Version 2322478
**********************************************************************************************************************************/

@isTest
public class CRMRetailCreateIntTypeReason_T_HUM {
    
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
            List<Storefront_I_Type__c> lstIntType = new List<Storefront_I_Type__c>();
            List<Storefront_I_Reason__c> lstIntReason = new List<Storefront_I_Reason__c>();
            
            Storefront_I_Type__c objIntType = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu');
            lstIntType.add(objIntType);
            
            Storefront_I_Type__c objIntTypeWithoutReason = CRMRetail_TestDataFactory_H_HUM.createInteractionType('Consumer Edu1');
            lstIntType.add(objIntTypeWithoutReason);
            
            insert lstIntType;
           	System.assertEquals(lstIntType.size(),2);
            
            Storefront_I_Reason__c objIntReason = CRMRetail_TestDataFactory_H_HUM.createInteractionReason('consumer test',null);
            objIntReason.Interaction_Type__c = lstIntType[0].Id;
            lstIntReason.add(objIntReason);
            insert lstIntReason;
            System.assertEquals(lstIntReason.size(),1);
            
        }
    }
    
    /**
    * This method is used to test when interaction type and reason record already exists and is linked
    *
    * @param  None      
    * @return  Void
    */
    
    @isTest static void testInttypeReasonExists()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Type__c> lstIntType = [Select Id,Name FROM Storefront_I_Type__c WHERE Name = 'Consumer Edu'];
            System.assert(lstIntType.size() == 1);
            
            List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
            System.assert(lstIntReason.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.intTypeName = lstIntType[0].Name;
            request.intReasonName = lstIntReason[0].Name;
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateInteractionTypeReason'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateIntTypeReason_I_HUM.createIntTypeReason();
            RestResponse response = RestContext.response;
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM createIntTypeReason = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM();
            createIntTypeReason = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM.class);
            System.assertEquals(createIntTypeReason.intTypeId, lstIntType[0].Id);
            
            Test.StopTest();

        }        
    } 
    
    /**
    * This method is used to test when interaction type and reason record does not exist
    *
    * @param  None      
    * @return  Void
    */
    
    @isTest static void testInttypeWithoutReason()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            List<Storefront_I_Type__c> lstIntType = [Select Id,Name FROM Storefront_I_Type__c WHERE Name = 'Consumer Edu1'];
            System.assert(lstIntType.size() == 1);
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.intTypeName = lstIntType[0].Name;
            request.intReasonName = 'Reason1';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateInteractionTypeReason'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateIntTypeReason_I_HUM.createIntTypeReason();
            RestResponse response = RestContext.response;
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM createIntTypeReason = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM();
            createIntTypeReason = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM.class);
            System.assertEquals(createIntTypeReason.intTypeId, lstIntType[0].Id);
            
            Test.StopTest();

        }
    }
    
    /**
    * This method is used to test when interaction type and reason record does not exist
    *
    * @param  None      
    * @return  Void
    */
    
    @isTest static void testNoMatchingIntTypeReason()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM();
            request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
            request.intTypeName = 'IntType1';
            request.intReasonName = 'Reason2';
            
            RestRequest req = new RestRequest();
            String JSONMsg = System.JSON.serialize(request);
            
            RestResponse res = new RestResponse();
            req.requestURI = 'CRMRetailServices_V1.0/CreateInteractionTypeReason'; 
            req.httpMethod = 'POST';
            req.requestBody = Blob.valueof(JSONMsg);
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateIntTypeReason_I_HUM.createIntTypeReason();
            RestResponse response = RestContext.response;
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM createIntTypeReason = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM();
            createIntTypeReason = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM.class);
            System.assertEquals(createIntTypeReason.isSuccess, true);
            
            Test.StopTest();

        }
    }
	
	/**
    * This method is used to test when the response received is incorrect
    *
    * @param  None      
    * @return  Void
    */	
    
    @isTest static void testException()
    {
        User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
        system.assertEquals(oUser.Name,'test User1');
        
        system.runAs(oUser)
        {
            
            RestRequest req = new RestRequest();    
            RestResponse res = new RestResponse();
            String JSONMsg = '';
            req.requestURI = 'CRMRetailServices_V1.0/CreateInteractionTypeReason';  
            req.httpMethod = 'POST';
            
            RestContext.request = req;
            RestContext.response= res;
            
            Test.startTest();
            
            CRMRetailCreateIntTypeReason_I_HUM.createIntTypeReason();
            RestResponse response = RestContext.response;
            
            CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM createIntTypeReason = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM();
            createIntTypeReason = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM.class);
            System.assertEquals(createIntTypeReason.isSuccess, false);
            
            Test.StopTest();

        }
    }
    
    /**
    * This method is used to test when the Interaction type does not exist but reason exists
    *
    * @param  None      
    * @return  Void
    */
    
     @isTest static void testIntReasonExists()
     {  
         User oUser = [select Id,Name from User where name = 'test User1' limit 1]; 
         system.assertEquals(oUser.Name,'test User1');
         
         system.runAs(oUser)
         {
             
             List<Storefront_I_Reason__c> lstIntReason = [Select Id,Name,Interaction_Type__c FROM Storefront_I_Reason__c WHERE Name = 'consumer test'];
             System.assert(lstIntReason.size() == 1);
             
             CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailCreateInteractionReason_DTO_HUM();
             request.sourceSystem = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GCM_VISITOR_BARCODE;
             request.intTypeName = 'IntType';
             request.intReasonName = lstIntReason[0].Name;
             
             RestRequest req = new RestRequest();
             String JSONMsg = System.JSON.serialize(request);
             
             RestResponse res = new RestResponse();
             req.requestURI = 'CRMRetailServices_V1.0/CreateInteractionTypeReason'; 
             req.httpMethod = 'POST';
             req.requestBody = Blob.valueof(JSONMsg);
             
             RestContext.request = req;
             RestContext.response= res;
             
             Test.startTest();
             
             CRMRetailCreateIntTypeReason_I_HUM.createIntTypeReason();
             RestResponse response = RestContext.response;
             
             CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM createIntTypeReason = new CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM();
             createIntTypeReason = (CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CRMRetailVNCResponse_DTO_HUM.CRMRetailCreateIntTypeReason_DTO_HUM.class);
             System.assertEquals(createIntTypeReason.isSuccess, false);
             
             Test.StopTest();
             
         }
    } 
}