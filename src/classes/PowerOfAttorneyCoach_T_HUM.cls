/*******************************************************************************************************************************
Apex Class Name : PowerOfAttorneyCoach_T_HUM
Version         : 1.0
Created On      : 10/15/2020
Function        : This is the Test class for PowerOfAttorneyCoach_C_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

@isTest
public class PowerOfAttorneyCoach_T_HUM{
    
    private static Account oAcc;
    private static Account objAcc;
    
    /**
* Method to setup test data
* 
* @param    None
* @return   Void 
*/
    @testSetup
    static void setup() 
    {  
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;
        system.assertEquals(oUser.lastname=='test User1',true);
        
        system.runAs(oUser)
        {   
            
            objAcc = HUMTestDataHelperTest.getAccount();
            objAcc.Name = 'testacc';
            objAcc.Group_Number__c = '213654';
            insert objAcc;
            system.assertEquals(objAcc.Group_Number__c, '213654');
            
            oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'testacc1';
            oAcc.Group_Number__c = '213655';
            insert oAcc;
            system.assertEquals(oAcc.Group_Number__c, '213655');
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR');
            insert oProd;
            System.assertEquals(oProd.Segment_Type__c, 'MCR');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER'; 
            objpol.Platform__c='LV';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            insert objpol; 
            system.assertEquals(objpol.Platform__c,'LV');
            
            objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                      From Policy__c Where Id = : objpol.Id];
            
            objpol.Product__r.Segment_Type__c = oProd.Segment_Type__c;
            update objPol;
            system.assertEquals(objpol.id!=null,true);
            
            objpol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                      From Policy__c Where Id = : objpol.Id];
            
            Policy__c opol = HUMTestDataHelperTest.getPolicy();
            opol.Name='testPolicy';
            opol.Group_Name__c = oAcc.Id;
            opol.Major_LOB__c = 'MER'; 
            opol.Platform__c='LV';
            opol.Source_Cust_Cov_Key__c = '123456789';
            opol.Product__c = oProd.Id;
            opol.Product__r = oProd;
            insert opol; 
            system.assertEquals(opol.Major_LOB__c,'MER');
            
            opol = [Select Name, Group_Name__c, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                    From Policy__c Where Id = : opol.Id];
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;       
            objPolMem.Policy__r = objpol;       
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Policy_Member__c oPolMem = new Policy_Member__c();
            oPolMem.Name = 'testpolmem';
            oPolMem.Relationship__c = 'Self';
            oPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            oPolMem.End_Date__c=Date.today().addDays(1).format();
            oPolMem.Policy_Member_External_ID__c='01|ODS|6925004434817|12345678|34567|2013-07-01';
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = opol.Id;       
            insert oPolMem;
            system.assertEquals(oPolMem.Relationship__c, 'Self');
            
            Member_Id__c memberIdInstance = new Member_Id__c();
            memberIdInstance.Policy__c = objpol.Id;
            memberIdInstance.Policy_Member__c = objPolMem.Id;
            memberIdInstance.Member_Card_External_ID__c = objPolMem.Id + 'ExtId';
            memberIdInstance.Type__c = 'Member-Id-Base';
            memberIdInstance.Name = '123456789012';
            insert memberIdInstance;
            system.assertEquals(memberIdInstance.Type__c, 'Member-Id-Base');
            
            Member_Id__c oMem = new Member_Id__c();
            oMem.Policy__c = opol.Id;
            oMem.Policy_Member__c = oPolMem.Id;
            oMem.Member_Card_External_ID__c = oPolMem.Id + 'ExtId1';
            oMem.Type__c = 'Member';
            oMem.Name = '123456789012';
            insert oMem;
            system.assertEquals(oMem.Type__c, 'Member');
            
            insert new HUM_Webservice_Callout__c(Name='CIMedicareMultipleMemberDetails',SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/CIMedicareMultipleMemberDetails', Timeout__c='120000', Request_Type__c='POST', Content_Type__c='application/json', Certificate_Name__c='Test_Certificate');     
            system.assert(HUM_Webservice_Callout__c.getValues('CIMedicareMultipleMemberDetails').Certificate_Name__c == 'Test_Certificate');
        }
    }
    
    
    /**
* Method to test POA Section - Positive Scenario
* 
* @param    None
* @return   Void 
*/
    static testMethod void testPOA()
    {
        User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        { 
            
            test.startTest();
            
            Account oAccount = [Select Id from Account where Name = 'testacc' limit 1];
            
            PowerOfAttorneyCoach_C_HUM oPOA = new PowerOfAttorneyCoach_C_HUM();
            
            String mockResponse='{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[ { "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"Y", "POALastName":"lastNameTest", "POAFirstName":"firstNameTest", "POAPhoneNumber":"1234567891", "POAType":"H", "POAApplDate":"", "Errors":{ "Error":{ "Code":"", "Description":"" } } } } ] } }';
            Continuation con = (Continuation)PowerOfAttorneyCoach_C_HUM.callCIMedicare(oAccount.id);
            HttpResponse resCIMed = new Httpresponse();
            resCIMed.setBody(mockResponse);
            resCIMed.setStatusCode(200);
            
            Test.setContinuationResponse(PowerOfAttorneyCoach_S_HUM.sCIMedReq,resCIMed);
            
            Object state=(Object)PowerOfAttorneyCoach_S_HUM.sCIMedReq;
            PowerOfAttorneyCoach_C_HUM.processCIResponse(state);
            
            string lstPOAData='{"POAData":[{"DocumentType":"Power of Attorney","EffectiveDate":"10/1/2020","FirstName":"Aditi","GuardianAddress":"1239 KAST AVE, OWATONNA, MN, 55060","IndexNumber":8,"LastName":"Chowrasya","LevelOfConsent":"","PersonofAuthority":"Aditi Chowrasya","PhoneNumber":"5024761654","Relationship":"Parent","SubmissionMehtod":"Paper","TerminationDate":"3/30/2021","Type":"C"}]}';
            
            PowerOfAttorneyCoach_C_HUM.processPOA(lstPOAData);
            
            
            PowerOfAttorneyCoach_C_HUM.poaWrapper poaWrapper= new PowerOfAttorneyCoach_C_HUM.poaWrapper();
            poaWrapper.FirstName='firstName';
            poaWrapper.LastName='lastName';
            poaWrapper.PhoneNumber='123456';
            poaWrapper.Type='XYZ';
            poaWrapper.TypeHelpText='HelpText';
            
            
            test.stopTest();
        }
    }
    
      /**
* Method to test POA Section - Negative Scenario
* 
* @param    None
* @return   Void 
*/
    static testMethod void testPOANegative()
    {  
         User oUser = [SELECT Id FROM User WHERE Lastname='test User1' LIMIT 1];
        
        system.assert(ouser.id!=null);
        System.runAs (ouser)
        { 
            
            test.startTest();
            
            Account oAccount = [Select Id from Account where Name = 'testacc1' limit 1];
            
            PowerOfAttorneyCoach_C_HUM oPOA = new PowerOfAttorneyCoach_C_HUM();
            
            String mockResponse='<><><><>';
            Continuation con = (Continuation)PowerOfAttorneyCoach_C_HUM.callCIMedicare(oAccount.id);
            HttpResponse resCIMed = new Httpresponse();
            resCIMed.setBody(mockResponse);
            resCIMed.setStatusCode(400);
            
            Test.setContinuationResponse(PowerOfAttorneyCoach_S_HUM.sCIMedReq,resCIMed);
            
            Object state=(Object)PowerOfAttorneyCoach_S_HUM.sCIMedReq;
            PowerOfAttorneyCoach_C_HUM.processCIResponse(state);
            
                test.stopTest();
        }
    }
}