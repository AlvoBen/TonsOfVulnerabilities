/***********************************************************************************************************
 * Apex Class Name  : ClaimDetailMTVRemarksDTO_LT_HUM
 * Version          : 1.0
 * Created Date     : 06/01/2022
 * Function         : Test class for ClaimDetailMTVRemarks_LDTO_HUM

Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
@isTest
private class ClaimDetailMTVRemarksDTO_LT_HUM
{

 /*
  * Method Name : dataSetup
  * Description : Create common test Data
  * Return type : void
  * Parameter   : None
  */ 
  
 @testSetup static void dataSetup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        HUM_Webservice_Callout__c oSvcHeaderMTVRemarks = new HUM_Webservice_Callout__c(Request_Type__c = 'GET', Name='TestClaim',
        End_Point_URL__c = 'test',
        Certificate_Name__c = 'Humana_GuidanceCare_Venus',
        Timeout__c = '100',
        Content_Type__c = 'text/application');
        insert oSvcHeaderMTVRemarks;
        System.assertEquals(oSvcHeaderMTVRemarks.Timeout__c, '100');
    }
  
 /*
  * Method Name : createRecords
  * Description : Test Method to cover ClaimDetailMTVRemarks_DTO_HUM controller
  * Return type : void
  * Parameter   : None
  */   
  testMethod static void createRecords()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {
      Test.startTest();
      ClaimDetailMTVRemarks_LDTO_HUM oRemarks=new ClaimDetailMTVRemarks_LDTO_HUM();
      oRemarks.sPagingInd ='';
      System.assertEquals('',oRemarks.sPagingInd);
      oRemarks.sStartRemarkSequenceNumber='001';
      System.assertEquals('001',oRemarks.sStartRemarkSequenceNumber);
      oRemarks.sCreationDate='04/13/2015';
      oRemarks.sCreationTime='12:00';
      oRemarks.sSurrogateId='';
      oRemarks.sRemarkEntityType='';
      oRemarks.sRemarkIdentifier='';
      oRemarks.sRemarkSequenceNumber='';
      oRemarks.sRemarkType='';
      oRemarks.sRemarkCategory='';
      oRemarks.sRemarkCreationTimestamp='';
      oRemarks.sRemarkText='';
      oRemarks.sRemarkExtendedText='';
      oRemarks.sRemarkSurrogateId='123';
      System.assertEquals('123',oRemarks.sRemarkSurrogateId);
      Test.stopTest();
     }
   }
}