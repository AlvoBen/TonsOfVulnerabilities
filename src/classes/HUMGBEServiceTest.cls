/**************************************************************************************************************************************
Apex Class Name : HUMGBEServiceTest
Version         : 1.0
Created On      : August 27 2015
Function        : This class is used as test class for HUMGBEService

* Modification Log:
* Developer Name          Code Review                   Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma            25870                     08/27/2015                 Original Version 
* Vishal Verma             25870                     08/27/2015                 Fixing code review  comments for review 25870
* Gargey Sharma            26691                     09/24/2015                 REQ - 222748 : Add Paging to Vitality Info                                 
****************************************************************************************************************************************/

@isTest
global class HUMGBEServiceTest 
{
 
    /*
    * Method Name : createCustomData
    * Description : Create Test Dat 
    * Return type : void
    * Paramater   : NA
    */
    @testSetup 
    static void createCustomData() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        system.assert(user.Id != null);
        insert new HUM_Webservice_Callout__c(Name='GBEService', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/EnrollmentServices/Group_V1.0', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='text/xml',Methods_Available__c='A;B;C');
    }
    
     /*
    * Method Name : testHUMGBEService
    * Description : Test method 
    * Return type : void
    * Paramater   : NA
    */    
    static testMethod void testHUMGBEService()
    {
        
        User oUser = [Select Id , Name from User where Username= 'user1user1@humcrmstest.com'  order by LastModifiedDate desc Limit 1];
        System.runas(oUser) 
        {
            Test.setMock(WebServiceMock.class, new HUMGBEServiceMockTest());
            HUMTestDataHelperTest.getHUMConstantsData();
            Test.StartTest();
                HUMGBEService.RequestGroupDTO reqGrp = new HUMGBEService.RequestGroupDTO();
                HUMGBEService.GroupDetailDTO grpDetailDTO = new HUMGBEService.GroupDetailDTO();
                HUMGBEService.ResponseGroupDetail_element resgrpele = new HUMGBEService.ResponseGroupDetail_element();
                
                grpDetailDTO.Platform = 'EM';
                grpDetailDTO.GroupId = '708599' ;                
                grpDetailDTO.SubGroupId = '70859924';                  
                grpDetailDTO.BenefitPackageId = 'SFINHDHP'; 
                grpDetailDTO.StartDate = '2013-01-01';
                grpDetailDTO.EndDate = '2013-12-31';
                grpDetailDTO.BenefitSequenceNumber = 'HFHGC';
                grpDetailDTO.VitalityStartPosition = 'Start';
                grpDetailDTO.VitalityRecordsToReturn = 'Rec';
                
                reqGrp.GroupDetailDTO = grpDetailDTO;
                HUM_Webservice_Callout__c oSvcHeader = HUM_Webservice_Callout__c.getValues('GBEService');
                HUMGBEService.soap sop = new HUMGBEService.soap(oSvcHeader.End_Point_URL__c, oSvcHeader.Certificate_Name__c, Integer.valueof(oSvcHeader.Timeout__c));
                resgrpele = sop.GetGroupInfo(reqGrp);
                system.assert(resgrpele.GroupId != null);
            Test.stopTest(); 
        }
    }
}