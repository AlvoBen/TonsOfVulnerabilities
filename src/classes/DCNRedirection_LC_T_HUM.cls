@isTest
public class DCNRedirection_LC_T_HUM {
	@testSetup 
    static void testSetup()
    {        
        User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr;
        
        HUMDCNApplicationUrl__c dcnLink= new HUMDCNApplicationUrl__c (DCNImageURL__c= 'https://dcnlink.com' );
        insert dcnLink;
        
        Endpoint_Url_HUM__c ssoLink= new Endpoint_Url_HUM__c(Name='SSO_LOGINURL' ,Remote_Url__c= 'https://ssolink.com');
        insert ssoLink;
        
        String orgId = UserInfo.getOrganizationId();
        String strEndpointURL = '';
                //TESTFULL
                If(orgId == '00D4C0000000TunUAE')
                {
                    strEndpointURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //QA
                Else If(orgId == '00Dc0000003uIioEAE')
                {
                    strEndpointURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';

                }
                //QAS
                Else If(orgId == '00DL0000005rQrAMAU')
                {
                    strEndpointURL = 'https://QA-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                //PROD
                Else If(orgId == '00DF00000005DjnMAE')
                {
                    strEndpointURL = 'https://associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                else
                {
                    strEndpointURL = 'http://test-associates.humana.com/SPIDAR/SpidarViewer/BasicViewer?ViewFlag=True&DCN=';
                }
                
        Endpoint_Url_HUM__c DCNSpider2Link= new Endpoint_Url_HUM__c(Name='DCNSpider2URL' ,Remote_Url__c= strEndpointURL);
        insert DCNSpider2Link;
        
        RecordType AccountRecordType = [Select Id from RecordType where Name='Member' and sObjectType='Account'];
        
        Account accountRec = HUMTestDataHelperTest.getAccount();
        accountRec.OwnerId = usr.id;
        accountRec.FirstName = 'Test';
        accountRec.LastName = 'Account';
        accountRec.Enterprise_Id__c = '123456';
        accountRec.recordtypeId = AccountRecordType.id;
        insert accountRec;
        
        List<Case> caseList = new List<Case>();
        Case caseRecord = new Case();
        caseRecord.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord.Status = 'In Progress';
        caseRecord.AccountId = accountRec.id;
        caseRecord.ownerId = usr.Id;
        caseRecord.Interacting_With__c = accountRec.id;
        caseRecord.Interacting_With_Type__c = 'Member';
        caseRecord.Interacting_About_Type__c = 'Member';
        caseRecord.Policy_Member__c = null;
        caseRecord.dcn__c= '23432';
        caseList.add(caseRecord);
        
        Case caseRecord1 = new Case();
        caseRecord1.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord1.Status = 'In Progress';
        caseRecord1.AccountId = accountRec.id;
        caseRecord1.ownerId = usr.Id;
        caseRecord1.Interacting_With__c = accountRec.id;
        caseRecord1.Interacting_With_Type__c = 'Member';
        caseRecord1.Interacting_About_Type__c = 'Member';
        caseRecord1.Policy_Member__c = null;
        caseRecord1.dcn__c= 'AC23432';
        caseList.add(caseRecord1);
        
        Case caseRecord2 = new Case();
        caseRecord2.Owner_Queue__c = 'Louisville RSO Calls Support';
        caseRecord2.Status = 'In Progress';
        caseRecord2.AccountId = accountRec.id;
        caseRecord2.ownerId = usr.Id;
        caseRecord2.Interacting_With__c = accountRec.id;
        caseRecord2.Interacting_With_Type__c = 'Member';
        caseRecord2.Interacting_About_Type__c = 'Member';
        caseRecord2.Policy_Member__c = null;
        caseRecord2.dcn__c= 'DC23432';
        caseRecord2.dcn_type__c= 'claim';
        caseList.add(caseRecord2);

        insert caseList;
    }
    static testMethod void testdcnURL()
    {            
        User oUser = [select Id,Name from User where name ='test User1' limit 1];                
        system.runAs(oUser)
        {   
            Test.StartTest();
            List<case> caseRecord = [Select id,DCN__c,DCN_Type__c from case limit 1];
            DCNRedirection_LC_HUM.generateRedirectToDCNURL(caseRecord);
            
            Test.StopTest();
        }
    }
    
    static testMethod void spider2DCNURLTest()
    {            
        User oUser = [select Id,Name from User where name ='test User1' limit 1];                
        system.runAs(oUser)
        {   
            List<case> caseRecord = [SELECT Id,DCN__c,DCN_Type__c FROM Case WHERE dcn__c= 'AC23432'];   
            Test.StartTest();
            DCNRedirection_LC_HUM.generateRedirectToDCNURL(caseRecord);
            Test.StopTest(); 
        }
    }
    Static testMethod void spider2DCNURLTest2()
    {            
        User oUser = [select Id, Name from User where name ='test User1' limit 1];                
        system.runAs(oUser)
        {   
            List<case> caseRecord = [SELECT Id,DCN__c, dcn_type__c,dcn_Url__c FROM Case WHERE dcn__c= 'DC23432'];   
            Test.StartTest();
            DCNRedirection_LC_HUM.generateRedirectToDCNURL(caseRecord);
            Test.StopTest();
        }
	}
}