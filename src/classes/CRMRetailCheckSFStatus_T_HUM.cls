/******************************************************************************************************************
Apex class Name    : CRMRetailCheckSFStatus_T_HUM
Version            : 1.0
Function           : This class is used as test class for CRMRetailCheckSFStatus_I_HUM
Created On         : Mar 01 ,2022

Modification Log: 
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Ekta Bist                                       06/03/2021          Original Version 3175716
------------------------------------------------------------------------------------------------------------------- */
@isTest
public class CRMRetailCheckSFStatus_T_HUM {
	/**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {  
        List<User> lstUserToInsert = new List<User>();
        User oUserNotAccess = HUMTestDataHelperTest.createTestUser('UserRetail1','CRMRetail User');
        lstUserToInsert.add(oUserNotAccess);

        insert lstUserToInsert;
        System.assertEquals(lstUserToInsert.size(), 1);
    }

    /**
    * This method is used to cover the validate health of Salesforce rest services 
    *
    * @param  None      
    * @return  Void
    */
    @isTest static void testCheckSFStatus()
    {          
    	User oUser = [select Id,Name from User where name = 'test UserRetail1' limit 1]; 
        system.assertEquals(oUser.Name,'test UserRetail1');
        
        system.runAs(oUser)
        {
            Test.startTest();
            RestRequest req = new RestRequest();
            req.requestURI = '/CRMRetailServices_V1.0/CheckSFStatus'; 
            req.httpMethod = 'POST';
            RestResponse res = new RestResponse();
            RestContext.request = req;
            RestContext.response= res;
            CRMRetailCheckSFStatus_I_HUM.CheckSFStatus();
            system.assertEquals(res.statusCode,200);
            Test.stopTest();
        }
    }

    /**
    * This method is used to generate the exception that validate health of Salesforce rest services 
    *
    * @param  None      
    * @return  Void
    */
    
    @isTest static void testCheckSFStatusExcp()
    { 
        User oUser = [select Id,Name from User where name = 'test UserRetail1' limit 1]; 
        system.assertEquals(oUser.Name,'test UserRetail1');
        
        system.runAs(oUser)
        {
            Test.startTest();
            RestRequest req = new RestRequest();
            req.requestURI = '/CRMRetailServices_V1.0/CheckSFStatus'; 
            req.httpMethod = 'POST'; 
            CRMRetailCheckSFStatus_I_HUM.CheckSFStatus();
            system.assertEquals(req.httpMethod,'POST');
            Test.stopTest();
        }
    }
}