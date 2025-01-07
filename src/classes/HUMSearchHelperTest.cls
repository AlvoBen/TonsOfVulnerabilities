/*******************************************************************************************************************************
Apex Class Name : HUMSearchHelperTest 
Version         : 1.0
Created On      : June 8 2104
Function        : Test Class for HUMSearchHelper

Modification Log: 
 * Developer Name           Code Review              Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar          17176                    08/08/2014                 Original Version
 * Shruthi Karanth          18840                    10/29/2014                 Commented out 14.11 code
 * Pranali Chaudhary        18890                    10/31/2014                 Changes for 14.11 release : CBIS, H1 exchange and Track Enrollment
 * Asha Deepika Koyyana     20572                    01/28/2015                 Fixed test class failure for Defect#164139  
 * Santhi Mandava                                    03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage.
 *********************************************************************************************************************************************************************************/
@isTest
private class HUMSearchHelperTest {


    /*
     * Method Name   :   getStandardSetObjectsByQuery
     * Description   :   Return the list of sObjects based on the arguments sent to class method
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getStandardSetObjectsByQuery() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
         List<Case> lstcases = new List<Case>();
        Case objCase = HUMTestDataHelperTest.getCase();
        objCase.Interacting_With_Name__c = 'Test';
        lstcases.add(objCase);
        //Create Case
        Case objCaseDup = HUMTestDataHelperTest.getCase();
        lstcases.add(objCaseDup);
        objCaseDup.Interacting_With_Name__c = 'Test';
        insert lstCases;
        
        ApexPages.StandardSetController stdSetController;
        Test.startTest();
            stdSetController = HUMSearchHelper.getStandardSetObjects('Select id,Case_TAT__c from Case');
        Test.Stoptest();
        
        System.assertEquals(stdSetController.getRecords().size(),2);
              
    }

    /*
     * Method Name   :   getStandardSetObjects
     * Description   :   Return the list of sObjects based on the SoqlQuery sent to class method
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getStandardSetObjects() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
              
        List<Case> lstcases = new List<Case>();
        Case objCase = HUMTestDataHelperTest.getCase();
        objCase.Interacting_With_Name__c = 'Test';
        lstcases.add(objCase);
        //Create Case
        Case objCaseDup = HUMTestDataHelperTest.getCase();
        objCaseDup.Interacting_With_Name__c = 'Test';
        lstcases.add(objCaseDup);
        insert lstCases;
        
        ApexPages.StandardSetController stdSetController;
        Test.startTest();
            stdSetController = HUMSearchHelper.getStandardSetObjects('Select id,Case_TAT__c ', 'Case', null, 'ID ', 'ASC', 10, null, null);
        Test.Stoptest();
        
        System.assertEquals(stdSetController.getRecords().size(),2);
    }

    /*
     * Method Name   :   getListOfRecordsByQuery
     * Description   :   Return the list of sObjects based on the SoqlQuery sent to class method
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getListOfRecordsByIDParam() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
         List<Case> lstcases = new List<Case>();
        Case objCase = HUMTestDataHelperTest.getCase();
        objCase.Interacting_With_Name__c = 'Test';
        lstcases.add(objCase);
        //Create Case
        Case objCaseDup = HUMTestDataHelperTest.getCase();
        objCaseDup.Interacting_With_Name__c = 'Test';
        lstcases.add(objCaseDup);
        insert lstCases;
        
        ApexPages.StandardSetController stdSetController;
        Test.startTest();
            stdSetController = HUMSearchHelper.getStandardSetObjects('Select id,Case_TAT__c from Case');
        Test.Stoptest();
        
        System.assertEquals(stdSetController.getRecords().size(),2);
    }

    /*
     * Method Name   :   getListOfRecords
     * Description   :   Return the list of sObjects based on the Parameters sent to class method
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getListOfRecords() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
         List<Case> lstcases = new List<Case>();
        Case objCase = HUMTestDataHelperTest.getCase();
        objCase.Interacting_With_Name__c = 'Test';
        lstcases.add(objCase);
        //Create Case
        Case objCaseDup = HUMTestDataHelperTest.getCase();
        objCaseDup.Interacting_With_Name__c = 'Test';
        lstcases.add(objCaseDup);
        insert lstCases;
        
        List<Sobject> lstSObjects;
        Test.startTest();
            lstSObjects= HUMSearchHelper.getListOfRecords('Select id,Case_TAT__c ', 'Case', null, 'ID ', 'ASC', 10, null, null);
        Test.Stoptest();
        
        System.assertEquals(lstSObjects.size(),2);
    }

    /*
     * Method Name   :   getListOfRecordsByQuery
     * Description   :   Return the list of sObjects based on the Parameters sent to class method
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getListOfRecordsByQuery() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
         List<Case> lstcases = new List<Case>();
        Case objCase = HUMTestDataHelperTest.getCase();
        objCase.Interacting_With_Name__c = 'Test';
        lstcases.add(objCase);
        //Create Case
        Case objCaseDup = HUMTestDataHelperTest.getCase();
        objCaseDup.Interacting_With_Name__c = 'Test';
        lstcases.add(objCaseDup);
        insert lstCases;
        
        List<Sobject> lstSObjects;
        Test.startTest();
            lstSObjects= HUMSearchHelper.getListOfRecords('Select id,Case_TAT__c from Case');
        Test.Stoptest();
        
        System.assertEquals(lstSObjects.size(),2);
    }

    /*
     * Method Name   :   getStateCodes
     * Description   :   Get the state codes from Custom setting
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getStateCodes() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Create States in Custom setting
        HUMMember_Search_State_Mapping__c stateAlbania= new HUMMember_Search_State_Mapping__c(Name='Albania',State_Code__c='Albania',State__c='Albania');
        insert stateAlbania;
        HUMMember_Search_State_Mapping__c stateIllinois= new HUMMember_Search_State_Mapping__c(Name='Illinios',State_Code__c='Illinios',State__c='Illinios');
        insert stateIllinois;
                
        String stateValue;
        Test.StartTest();
            StateValue = HUMsearchHelper.getStateCodes('Albania');
        Test.StopTest();
        
        System.assertEquals(stateValue,'Albania');
    }

    /*
     * Method Name   :   getState
     * Description   :   Get state code based on State Name
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getState() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Create States in Custom setting
        HUMMember_Search_State_Mapping__c stateAlbania= new HUMMember_Search_State_Mapping__c(Name='Albania',State_Code__c='Albania',State__c='Albania');
        insert stateAlbania;
        HUMMember_Search_State_Mapping__c stateIllinois= new HUMMember_Search_State_Mapping__c(Name='Illinios',State_Code__c='Illinios',State__c='Illinios');
        insert stateIllinois;
                
        String stateValue;
        Test.StartTest();
            StateValue = HUMsearchHelper.getState('Albania');
        Test.StopTest();
        
        System.assertEquals(stateValue,'Albania');
    }

    /*
     * Method Name   :   getServiceRequestGroup
     * Description   :   Get the Request header for Group Service -Parameters like Certificate name,Timeout etc
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getServiceRequestGroup() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        
        //Create Custom setting
        HUM_Webservice_Callout__c callOutSetting = new HUM_Webservice_Callout__c(Name =label.HUMGrpSearchSvc, End_Point_URL__c='www.test.com',Certificate_Name__c='test Certificate',Timeout__c='10',Active__c=true);
        insert callOutSetting;
        
        HUMIntGroupSearchService.soap requestMsg;
        Test.StartTest();
            requestMsg = (HUMIntGroupSearchService.soap)HUMSearchHelper.getServiceRequestInstance(system.label.HUMGrpSearchSvc,system.label.HUMGrpSearchReq);   
        Test.StopTest();
      
        System.assertEquals(requestMsg.endpoint_x, callOutSetting.End_Point_URL__c);
        System.assertEquals(requestMsg.clientCertName_x, callOutSetting.Certificate_Name__c);
        System.assertEquals(requestMsg.timeout_x, Integer.valueOf(callOutSetting.Timeout__c));
        
    }

    /*
     * Method Name   :   getServiceRequestEESLegacy
     * Description   :   Get the Request header for EES Legacy Service -Parameters like Certificate name,Timeout etc
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getServiceRequestEESLegacy() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
        //Create Custom setting
        HUM_Webservice_Callout__c callOutSetting = new HUM_Webservice_Callout__c(Name =label.HUMEESSearchSvc, End_Point_URL__c='www.test.com',Certificate_Name__c='test Certificate',Timeout__c='10',Active__c=true);
        insert callOutSetting;
        
        HUMIntEESLegacySearchService.PlatformMemberWebServiceSoap requestMsg;
        Test.StartTest();
            requestMsg  = (HUMIntEESLegacySearchService.PlatformMemberWebServiceSoap)HUMSearchHelper.getServiceRequestInstance(system.label.HUMEESSearchSvc,system.label.HUMEESSearchReq);                       
        Test.StopTest();
        
        System.assertEquals(requestMsg.endpoint_x, callOutSetting.End_Point_URL__c);
        System.assertEquals(requestMsg.clientCertName_x, callOutSetting.Certificate_Name__c);
        System.assertEquals(requestMsg.timeout_x, Integer.valueOf(callOutSetting.Timeout__c));
    }

    /*
     * Method Name   :   getServiceRequestH1Exchange
     * Description   :   Get the Request header for H1 Exchange Service -Parameters like Certificate name,Timeout etc
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getServiceRequestH1Exchange() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
         //Create Custom setting
        HUM_Webservice_Callout__c callOutSetting = new HUM_Webservice_Callout__c(Name =label.HUMH1ExchSvc, End_Point_URL__c='www.test.com',Certificate_Name__c='test Certificate',Timeout__c='10',Active__c=true);
        insert callOutSetting;
        
        HumH1ExchangeService.AM65AM65Port requestMsg ;
        Test.StartTest();
            requestMsg = (HumH1ExchangeService.AM65AM65Port)HUMSearchHelper.getServiceRequestInstance(system.label.HUMH1ExchSvc,system.label.HUMH1ExchReq);                     
        Test.StopTest();
        
        System.assertEquals(requestMsg.endpoint_x, callOutSetting.End_Point_URL__c);
        System.assertEquals(requestMsg.clientCertName_x, callOutSetting.Certificate_Name__c);
        System.assertEquals(requestMsg.timeout_x, Integer.valueOf(callOutSetting.Timeout__c));
    }

    /*
     * Method Name   :   getServiceRequestTrackEnrollment
     * Description   :   Get the Request header for Track Enrollment Service -Parameters like Certificate name,Timeout etc
     * Return Type   :   void
     * Parameters    :   NA
     */
     
    static testMethod void getServiceRequestTrackEnrollment() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
         //Create Custom setting
        HUM_Webservice_Callout__c callOutSetting = new HUM_Webservice_Callout__c(Name =label.HUMTrackEnrollSvc, End_Point_URL__c='www.test.com',Certificate_Name__c='test Certificate',Timeout__c='10',Active__c=true);
        insert callOutSetting;
        
        HUMIntTrackEnrollmentService.BasicHttpBinding_ITwoWayAsync requestMsg ;
        Test.StartTest();
            requestMsg  = (HUMIntTrackEnrollmentService.BasicHttpBinding_ITwoWayAsync)HUMSearchHelper.getServiceRequestInstance(system.Label.HUMTrackEnrollSvc,system.label.HUMTrackEnrollReq);                     
        Test.StopTest();
         
        System.assertEquals(requestMsg.endpoint_x, callOutSetting.End_Point_URL__c);
        System.assertEquals(requestMsg.clientCertName_x, callOutSetting.Certificate_Name__c);
        System.assertEquals(requestMsg.timeout_x, Integer.valueOf(callOutSetting.Timeout__c));
    }

    /*
     * Method Name   :   getServiceRequestAEHistory
     * Description   :   Get the Request header for AE History Service -Parameters like Certificate name,Timeout etc
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getServiceRequestAEHistory() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
         //Create Custom setting
        HUM_Webservice_Callout__c callOutSetting = new HUM_Webservice_Callout__c(Name =label.HUMAEHistSvc, End_Point_URL__c='www.test.com',Certificate_Name__c='test Certificate',Timeout__c='10',Active__c=true);
        insert callOutSetting;
        
        HUMIntAEHistoryService.BasicHttpBinding_ITwoWayAsync requestMsg ;
        Test.StartTest();
            requestMsg = (HUMIntAEHistoryService.BasicHttpBinding_ITwoWayAsync)HUMSearchHelper.getServiceRequestInstance(system.label.HUMAEHistSvc,system.label.HUMAEHisReq);                    
        Test.StopTest();
        
        System.assertEquals(requestMsg.endpoint_x, callOutSetting.End_Point_URL__c);
        System.assertEquals(requestMsg.clientCertName_x, callOutSetting.Certificate_Name__c);
        System.assertEquals(requestMsg.timeout_x, Integer.valueOf(callOutSetting.Timeout__c));
    }

    /*
     * Method Name   :   getServiceRequestOHBP
     * Description   :   Get the Request header for OHBP Service -Parameters like Certificate name,Timeout etc
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getServiceRequestOHBP() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
         //Create Custom setting
        HUM_Webservice_Callout__c callOutSetting = new HUM_Webservice_Callout__c(Name =label.HUMOHBPSvc, End_Point_URL__c='www.test.com',Certificate_Name__c='test Certificate',Timeout__c='10',Active__c=true);
        insert callOutSetting;
                
        HUMOHBPService.soap requestMsg ;
        Test.StartTest();
            requestMsg = (HUMOHBPService.soap)HUMSearchHelper.getServiceRequestInstance(system.label.HUMOHBPSvc,system.label.HUMOHBPReq);                   
        Test.StopTest();
        
        System.assertEquals(requestMsg.endpoint_x, callOutSetting.End_Point_URL__c);
        System.assertEquals(requestMsg.clientCertName_x, callOutSetting.Certificate_Name__c);
        System.assertEquals(requestMsg.timeout_x, Integer.valueOf(callOutSetting.Timeout__c));
    }
    
    /*
     * Method Name   :   getServiceRequestCBIS
     * Description   :   Get the Request header for CBIS Service -Parameters like Certificate name,Timeout etc
     * Return Type   :   void
     * Parameters    :   NA
     */
    static testMethod void getServiceRequestCBIS() 
    {
        //Load Constants from Custom Setting
        HUMTestDataHelperTest.getHUMConstantsData();
         //Create Custom setting
        HUM_Webservice_Callout__c callOutSetting = new HUM_Webservice_Callout__c(Name ='HUMCbisSearchEnrollment', End_Point_URL__c='www.test.com',Certificate_Name__c='test Certificate',Timeout__c='10',Active__c=true);
        insert callOutSetting;
                
        HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails requestMsg ;
        Test.StartTest();
            requestMsg = (HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails)HUMSearchHelper.getServiceRequestInstance('HUMCbisSearchEnrollment','HUMIntCBISService.EndPoint_GetAEHistoryServices');                   
        Test.StopTest();
        
        System.assertEquals(requestMsg.endpoint_x, callOutSetting.End_Point_URL__c);
        System.assertEquals(requestMsg.clientCertName_x, callOutSetting.Certificate_Name__c);
        System.assertEquals(requestMsg.timeout_x, Integer.valueOf(callOutSetting.Timeout__c));
    }


}