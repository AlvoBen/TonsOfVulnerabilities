/***************************************************************************************************************** 
Apex Class Name  : ClaimDetailMTVRemarksService_T_HUM
Version          : 1.0  
Created Date     : April 21, 2016
Function         : Test Class for ClaimDetailMTVRemarks_S_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Pallavi Ravishankar             25441              04/21/2016         Original Version
 ******************************************************************************************************************************/
@isTest
private Class ClaimDetailMTVRemarksService_T_HUM
{
    /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
     * Return type : void
     * Paramater   : NA
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
  * Method Name : TestAssignValuesToHelper
  * Description : Used to test assignValuesToHelper method
  * Return type : void
  * Paramater   : NA
  */
   testMethod static void TestAssignValuesToHelper()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {
          Test.startTest();
          ClaimDetailMTVRemarks_S_HUM oClaimMTVService = new ClaimDetailMTVRemarks_S_HUM ();
          MTVRemarksServiceGetClaimsRequest_H_HUM oMTVRequest = new MTVRemarksServiceGetClaimsRequest_H_HUM();
          Boolean bSecondCall = true;
          String sClaimNumber = '123456';
          String sCreationDate = '04/22/2016';
          String sCreationTime = '12:15:22';
          String sSurrogate = '1234567';
          String sSeqNum = '1';
          oMTVRequest = oClaimMTVService.assignValuesToHelper(bSecondCall,sClaimNumber,sCreationDate,sCreationTime,sSurrogate,sSeqNum);
          system.assertEquals(oMTVRequest.SurrogateId,'1234567');
          system.assertEquals(oMTVRequest.StartRemarkSequenceNumber,sSeqNum );
          bSecondCall = false;
          oMTVRequest = oClaimMTVService.assignValuesToHelper(bSecondCall,sClaimNumber,sCreationDate,sCreationTime,sSurrogate,sSeqNum);
          system.assertEquals(oMTVRequest.SurrogateId,'');
          system.assertEquals(oMTVRequest.StartRemarkSequenceNumber,'');
          Test.stopTest();
     }
    }
   
   /*
    * Method Name : TestProcessResponse
    * Description : Used to test ProcessResponse method
    * Return type : void
    * Paramater   : NA
    */ 
    
   testMethod static void TestProcessResponse()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {
      Test.startTest();
      ClaimDetailMTVRemarks_S_HUM oClaimMTVService = new ClaimDetailMTVRemarks_S_HUM ();
      List<ClaimDetailMTVRemarks_DTO_HUM> lstClaimRemarks = new List<ClaimDetailMTVRemarks_DTO_HUM>();
      string sjson = '{"MTVRemarksResponse": {"PagingInd": "Y","StartRemarkSequenceNumber": "01","CreationDate": "20081213","CreationTime": "073632419584","SurrogateId": "821307363241","RemarksList": {"Remarks":[{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01",'+
                    '"RemarkType": "TMID","RemarkCategory": " ","CreationTimestamp": "03/18/2015 10:14:13.283735","RemarkText": "0046121855141","SurrogateId": "731810141328"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "TMID","RemarkCategory": "",'+
                    '"CreationTimestamp": "01/07/2015 16:56:39.171615","RemarkText": "0045600105558",SurrogateId": "510716563917"}]}}}';
      lstClaimRemarks = oClaimMTVService.processResponse(sjson);
      system.assertEquals(lstClaimRemarks,null);
      sjson = '{"MTVRemarksResponse": {"PagingInd": "Y","StartRemarkSequenceNumber": "01","CreationDate": "20081213","CreationTime": "073632419584","SurrogateId": "821307363241","RemarksList": {"Remarks":[{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01",'+
                    '"RemarkType": "TMID","RemarkCategory": " ","CreationTimestamp": "03/18/2015 10:14:13.283735","RemarkText": "0046121855141","SurrogateId": "731810141328"},{"EntityType": "Claim","Identifier": "201506246744894","RemarkSequenceNumber": "01","RemarkType": "TMID","RemarkCategory": "",'+
                    '"CreationTimestamp": "01/07/2015 16:56:39.171615","RemarkText": "0045600105558","SurrogateId": "510716563917"}]}}}';
      lstClaimRemarks = oClaimMTVService.processResponse(sjson);
      system.assertEquals(lstClaimRemarks[0].sPagingInd ,'Y');
      system.assertEquals(lstClaimRemarks[0].sStartRemarkSequenceNumber,'01');
      Test.stopTest();
    
     }
    }
   
   /*
    * Method Name : TestSetupRequest
    * Description : Used to test SetupRequest method
    * Return type : void
    * Paramater   : NA
    */  
   testMethod static void TestSetupRequest()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {
          ClaimDetailMTVRemarks_S_HUM oClaimMTVService = new ClaimDetailMTVRemarks_S_HUM ();
          HttpRequest oRequest = new HttpRequest();
          HUM_Webservice_Callout__c oSvcHeaderMTVRemarks = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c Limit 1];
          String sRequestBody = 'Body';
          oRequest = oClaimMTVService.setupRequest(oRequest, oSvcHeaderMTVRemarks, sRequestBody);
          system.assert(oRequest!=null);
     }
    }
}