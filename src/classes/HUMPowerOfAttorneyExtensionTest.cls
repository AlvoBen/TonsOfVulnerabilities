/**************************************************************************** 
Apex Class Name  : HUMPowerOfAttorneyExtensionTest
Version          : 1.0  
Created Date     : April 9, 2015
Description      : This class is used as test class for HUMPowerOfAttorneyExtension
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar            22269                      09/04/2015                  Original Version
* Manish Kumar            22546                      04/22/2015                  Modified the code for defect#180358.0001 to resolve service issue. 
                                                                                 Lines modified : 71 & 80.
* Tanushree Hazari        22777                      04/30/2015                  Modified for increasing code coverage as part of defect-181130.0001
* Manish Kumar            23159                      05/14/2015                  Modified the code for defect#182188.0001 to resolve service issue to fetch POA alerts. 
                                                                                 Lines modified : 45 - 47. 
* Manish Kumar            23968                      06/08/2015                  Modified the code for defect#184140 & 182167 to resolve service issue to fetch POA alerts for CAS policies and dual alerts.
* Santhi Mandava                                     03/12/2019                  Removed "DpaaSWebServiceMockImpl" usage.
*******************************************************************************************************************************************************/
@isTest
private Class HUMPowerOfAttorneyExtensionTest
{    
    private static Account oAcc;
    /*
     * Method Name : testcheckConditionForCallout
     * Description : Test method for HUMPowerOfAttorneyExtension
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testcheckConditionForCallout()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertNotEquals(oUser.Id, NULL);
        
        System.runAs(oUser)
        {
            Test.StartTest();
            
            Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());
            
            List<HUMConstants__c> lstHUMConstants = new List<HUMConstants__c>();
            lstHUMConstants.add(new HUMConstants__c(Name='Type_LV', StringValue__c='LV'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMCalloutLimit', StringValue__c='50'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMSix', StringValue__c='6'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMNine', StringValue__c='9'));
            lstHUMConstants.add(new HUMConstants__c(Name='POAFields', StringValue__c='First Name;Last Name;Phone;POA Type;'));
            lstHUMConstants.add(new HUMConstants__c(Name='ELIGIBILITY_INFO', StringValue__c='MER;MGR;MEP;MGP;MEF;MRO;PDP;MPD;MES;MCD'));
            lstHUMConstants.add(new HUMConstants__c(Name='MEMBER_ID_TYPE', StringValue__c='Member-Id-Base;'));
            lstHUMConstants.add(new HUMConstants__c(Name='POLICY_PLATFORM_INFO', StringValue__c='LV;'));
            lstHUMConstants.add(new HUMConstants__c(Name='SEMICOLON', StringValue__c=';'));
            lstHUMConstants.add(new HUMConstants__c(Name='ZERO', IntegerValue__c=0));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMDateFormat', StringValue__c='DD/MM/YYYY'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMCharY', StringValue__c='Y'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMCharN', StringValue__c='N'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMConstRecord', StringValue__c='Record'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMMedicare', StringValue__c='MedicareID'));
            lstHUMConstants.add(new HUMConstants__c(Name='HUMTypeRSO',StringValue__c='RSO;'));
            insert lstHUMConstants;
            System.assertEquals(lstHUMConstants.size(), 16);
            
            Account objAcc = HUMTestDataHelperTest.getAccount();
            objAcc.Name = 'testacc';
            objAcc.Group_Number__c = '213654';
            insert objAcc;
            System.assertNotEquals(objAcc.Id, NULL);
            
            oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Name = 'testacc1';
            oAcc.Group_Number__c = '213655';
            insert oAcc;
            System.assertNotEquals(oAcc.Id, NULL);
            
            Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER'; 
            objpol.Platform__c='LV';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            insert objpol; 
            System.assertNotEquals(objpol.Id,NULL);
            
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
            System.assertNotEquals(opol.Id,NULL);
            
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
            System.assertNotEquals(objPolMem.Id, NULL);
            
            Policy_Member__c oPolMem = new Policy_Member__c();
            oPolMem.Name = 'testpolmem';
            oPolMem.Relationship__c = 'Self';
            oPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            oPolMem.End_Date__c=Date.today().addDays(1).format();
            oPolMem.Policy_Member_External_ID__c='01|ODS|6925004434817|12345678|34567|2013-07-01';
            oPolMem.Member__c = oAcc.Id;
            oPolMem.Policy__c = opol.Id;       
            insert oPolMem;
            System.assertNotEquals(oPolMem.Id, NULL);
            
            Member_Id__c memberIdInstance = new Member_Id__c();
            memberIdInstance.Policy__c = objpol.Id;
            memberIdInstance.Policy_Member__c = objPolMem.Id;
            memberIdInstance.Member_Card_External_ID__c = objPolMem.Id + 'ExtId';
            memberIdInstance.Type__c = 'Member-Id-Base';
            memberIdInstance.Name = '123456789012';
            insert memberIdInstance;
            System.assertNotEquals(memberIdInstance.Id, NULL);
            
            Member_Id__c oMem = new Member_Id__c();
            oMem.Policy__c = opol.Id;
            oMem.Policy_Member__c = oPolMem.Id;
            oMem.Member_Card_External_ID__c = oPolMem.Id + 'ExtId1';
            oMem.Type__c = 'Member-Id-Base';
            oMem.Name = '123456789012';
            insert oMem;
            System.assertNotEquals(oMem.Id, NULL);
            
            insert new HUM_Webservice_Callout__c(Name='HumCIMedicareMemberService', Certificate_Name__c='Humana_GuidanceCare_R1', Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/CIMedicareMember', Timeout__c='12000');
            
            Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest());  
            ApexPages.StandardController oSC = new ApexPages.StandardController(objAcc);
            HUMPowerOfAttorneyExtension objHelper = new HUMPowerOfAttorneyExtension(oSC);
            HUMCIMedicareMemberResponseHelper oPM = new HUMCIMedicareMemberResponseHelper();
            HUMCIMedicareMemberResponseHelper oCIM = new HUMCIMedicareMemberResponseHelper();
            oCIM.sPOAIndicator = 'Y';
            oCIM.sPOAFirstName = 'Test';
            oCIM.sPOALastName = 'Attorney';
            oCIM.sPOAPhoneNumber = '123456789';
            oCIM.sPOAType = 'Y';
            objHelper.lstPOAHelper = new List<HUMCIMedicareMemberResponseHelper>();
            objHelper.lstPOAHelper.add(oCIM);
            objHelper.populateMapForHelpers();
            Test.StopTest();
        }
    }
}