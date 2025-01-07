/*****************************************************************************************
Apex class Name    : EligibilityInformation_LT_HUM
Version            : 1.0
Function           : controller for retrieving eligibility info
Created Date       : 03/12/2021
Test Class         : EligibilityInformation_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Joel George                                            05/03/2021                  initial version
*Joel George                                            05/17/2021                  Added Policy Object change
****************************************************************************************************************/
@isTest
public with sharing class EligibilityInformation_LT_HUM 
{
    
     /*
    * Method Name : prepareTestData
    * Description : Method to set up test data
    * Return type : void
    * Paramater   : NA
    */
   
    @testSetup static void prepareTestData() 
    {

        insert new HUM_Webservice_Callout__c(Name = 'CIMedicareMultipleMemberDetails',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
        
        // Fetching and inserting Account Record
        Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        objAcc.Group_Number__c = '213654';
        objAcc.Enterprise_Id__c = '12321422';
        insert objAcc; 
        system.assertEquals(objAcc.id!=null,true);
        
        Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
        insert oProd;
        System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
        
        
        PurchaserPlan oPurchaserPlan = new PurchaserPlan(name = 'Test Policy', PayerId = objAcc.Id,Product__c = oProd.id,
                                                  Policy_Group_Number__c='11111',Major_LOB__c='MER',Source_Cust_Cov_Key__c='123456789',
                                                  Platform__c='LV',Product__r = oProd);
        insert oPurchaserPlan;  
        system.assert(oPurchaserPlan.id != null);

        oPurchaserPlan = [Select Name, PayerId, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From PurchaserPlan Where Id = : oPurchaserPlan.Id];
        
        oPurchaserPlan.Product__r.Segment_Type__c = oProd.Segment_Type__c;
        update oPurchaserPlan;
        system.assertEquals(oPurchaserPlan.id!=null,true);
        
        oPurchaserPlan = [Select Name, PayerId, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                        From PurchaserPlan Where Id = : oPurchaserPlan.Id];
                        
                        
        MemberPlan objMemberplan = new MemberPlan();
        objMemberplan.Name='MemberPlan1';
        objMemberplan.MemberId=objAcc.id;
        objMemberplan.planId=oPurchaserPlan.id;
        objMemberPlan.Member_Dependent_Code__c = '01';
        objMemberPlan.Member_Id_base__c = 'H62753913';
        insert objMemberplan;

       
    }
     /*
    * Method Name : testMemberEligibility
    * Description : Test method to get response for the indicators
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void testMemberEligibility()
    {

        MemberPlan memberPlanObj = [select id,Name,planId from MemberPlan where Name = 'MemberPlan1' limit 1];
        String memberId = memberPlanObj.Id;
        Test.startTest();
        Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest()); 
        Object returnObj = (Object)EligibilityInformation_LC_HUM.callCIMedMultipleMemberService(memberId);
        
        
         List<String> lstLabels = new List<String>();
        lstLabels = new List<String>{'label1','label2'};
        String mockResposne = '{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[ { "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"Y", "SubsidyEffectDate":"01/01/2021", "SubsidyEndDate":"12/31/2021", "LISSubsidyLevel":"100", "LISCopayCategory":"1", "LTSSIndicator":"N", "ESRDIndicator":"N", "SPAPIndicator":" ", "SNPIndicator":"N", "SNPTypeCode":"  ", "POAIndicator":"", "POALastName":"", "POAFirstName":"", "POAPhoneNumber":"", "POAType":"", "POAApplDate":"", "Errors":{ "Error":{  } } } } ] } }';
        
        HttpResponse responseobj=new Httpresponse();
        responseobj.setBody(mockResposne);
        responseobj.setStatusCode(200);
        EligibilityInformation_LC_HUM.processAllResponses(lstLabels, null);

        Test.stopTest();
    }
    /*
    * Method Name : testMemberEligibility
    * Description : Test method to get response for the indicators
    * Return type : void
    * Paramater   : NA
    */
    public static testMethod void testMemberEligibilityInfo()
    {

        MemberPlan memberPlanObj = [select id,Name,planId from MemberPlan where Name = 'MemberPlan1' limit 1];
        String memberId = memberPlanObj.Id;
        Test.startTest();
        Test.setMock(HttpCalloutMock.class, new HUMCIMedicareMemberMockHttpResponseTest()); 
        Object returnObj = (Object)EligibilityInformation_LC_HUM.callCIMedMultipleMemberService(memberId);
        String resBodyString = '{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[ { "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"Y", "SubsidyEffectDate":"01/01/2021", "SubsidyEndDate":"12/31/2021", "LISSubsidyLevel":"100", "LISCopayCategory":"1", "LTSSIndicator":"N", "ESRDIndicator":"N", "SPAPIndicator":" ", "SNPIndicator":"N", "SNPTypeCode":"  ", "POAIndicator":"", "POALastName":"", "POAFirstName":"", "POAPhoneNumber":"", "POAType":"", "POAApplDate":"", "Errors":{ "Error":{  } } } } ] } }';
        
        HttpResponse httpRes = new Httpresponse();
        httpRes.setStatusCode(200);
        httpRes.setBody(resBodyString);
        system.assert(httpRes.getBody() == resBodyString);         
        string reqType = 'Continuation-1';        
        List<string> labelGetmember = new List<string>();
        labelGetmember.add(reqType);
        Test.setContinuationResponse(reqType,httpRes);
        
         try
        {
            Object result = EligibilityInformation_LC_HUM.processAllResponses(labelGetmember,null);
            system.assert(result!=NULL);  
        }
        catch(Exception e)
        {
            system.assert(e.getMessage()!=NULL);
        }    
                
        Test.stopTest();
    }
    
}