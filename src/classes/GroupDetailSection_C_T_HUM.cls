/*****************************************************************************************
Apex class Name    : GroupDetailSection_C_T_HUM
Version            : 1.0
Function           : Test class for GroupDetailSection_C_HUM
Created Date       : 21 Oct 2015

Modification Log:
*   Developer                        Code Review            Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  SuryaKumari Medicherla            27320               10/21/2015            Original Version
*  SuryaKumari Medicherla            27484               11/03/2015            Code modified for REQ-213234(MTVx link in Division-Subgroup related list)
*********************************************************************************************************************************/
@isTest
private  class GroupDetailSection_C_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to insert the data only once and called automicticaly in all methods
    * Return type : Void
    * Paramater   : NA
    */
    
     @testSetup static void setup() 
     {           
        //Load all Constants data        
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser.id,null,'​Value not matched');
        
        System.runAS(oUser)
        {
            RecordType oGroupAccountRecordType = [Select Id from RecordType where Name = 'Group' and sObjectType='Account'];
            system.assert(oGroupAccountRecordType.id != null);
            
            Account oacc =  HUMTestDataHelperTest.getAccount();
            oacc.Group_Number__c = '987654321';
            oacc.recordtypeid = oGroupAccountRecordType.id;
            oacc.name= 'duck';
            oacc.NPI_ID__c='0123456789';
            oacc.Individual_First_Name__c='testname';
            oacc.Individual_Last_Name__c='firstname';
            oacc.Source_Customer_Key__c ='608356';
            oacc.Source_Platform_Code__c='EM';
            insert oacc;
            system.assert(oacc.id!=null);
            system.assert(oacc.Source_Customer_Key__c == '608356');
            system.assert(oacc.Source_Platform_Code__c == 'EM');
             
            HUM_Webservice_Callout__c oHUMGBESservice = new HUM_Webservice_Callout__c();
            oHUMGBESservice.name='GBEService';
            oHUMGBESservice.Active__c=true;
            oHUMGBESservice.Certificate_Name__c='Certificate';
            oHUMGBESservice.End_Point_URL__c='https://int-crmservicews.humana.com/EnrollmentServices/Group_V1.0';
            oHUMGBESservice.Timeout__c='12000';
            insert oHUMGBESservice ;
            system.assert(oHUMGBESservice.id!=null);
            system.assert(oHUMGBESservice.name == 'GBEService');
            
            HUMConstants__c oConstant1 = new HUMConstants__c(Name = 'GBES_DivisionSubGroup_Records_HUM',IntegerValue__c= 25,StringValue__c = '25') ;
            insert oConstant1;
            system.assert(oConstant1.id!=null);
        }
    }
    
    /*
    * Method Name : testGroupAndDivisionData
    * Description : This method tests Group Detail section details is populating or not.
    * Return type : Void
    * Paramater   : None
    */
    static testMethod void testGroupAndDivisionData()
    {
        User oUser = [Select id from User WHERE Lastname='test User1' LIMIT 1];
        
        HUMGBEService.ResponseGroupDetail_element onewResponse= new  HUMGBEService.ResponseGroupDetail_element();
        System.assertNotEquals(onewResponse, NULL);
        
        HUMGBEService.ResponseGroupExchangeDTO oGExchangeResponse= new  HUMGBEService.ResponseGroupExchangeDTO();
        System.assertNotEquals(oGExchangeResponse, NULL);
        
        HUMGBEService.ResponseGroupBenefitDTO oBenftResponse= new  HUMGBEService.ResponseGroupBenefitDTO();
        System.assertNotEquals(oBenftResponse, NULL);
        
        HUMGBEService.VitalityIndicatorListDTO oVitalityResponse = new  HUMGBEService.VitalityIndicatorListDTO();
        System.assertNotEquals(oVitalityResponse, NULL);
        
        HUMGBEService.VitalityIndicatorDTO oVitalityInd = new HUMGBEService.VitalityIndicatorDTO();
        
        List<HUMGBEService.VitalityIndicatorDTO> lstVitalityIndicators = new List<HUMGBEService.VitalityIndicatorDTO>();
        
        oVitalityInd.VitalityIndicator = true;
        oVitalityInd.SubGroupID = '5402301';
        oVitalityInd.SubGroupName = 'ABCD';
        oVitalityInd.UnitCount = '2';
        oVitalityInd.Unit = 'D';
        oVitalityInd.EffectiveProvision = 'IM';
        
        lstVitalityIndicators.add(oVitalityInd);
        
        oVitalityResponse.VitalityIndicatorDTO = lstVitalityIndicators;
        
        oBenftResponse.GroupName = 'abc';
        oBenftResponse.BenfitSequenceNumber = '123456';
        oBenftResponse.EffectiveDate = '03-10-1990';
        oBenftResponse.EnrolledSubscriberCountDental = '23';
        oBenftResponse.EnrolledSubscriberCountMedical = '23';
        oBenftResponse.NextRenewalDate = '13-20-2013';
        oBenftResponse.Product = '234';
        oBenftResponse.ProductCode = 'LV';
        oBenftResponse.DentalEffectiveDate = '03-10-1990';
        
        onewResponse.GroupId = '12345';
        onewResponse.Platform = 'EM';
        onewResponse.BenefitPackageId ='234' ;
        onewResponse.StartDate = '13-10-1990';
        onewResponse.EndDate = '13-10-1990';
        onewResponse.AsOfDate = '13-10-1990';
        onewResponse.ResponseGroupExchangeDTO = oGExchangeResponse;
        onewResponse.ResponseGroupBenefitDTO = oBenftResponse;
        onewResponse.VitalityIndicatorListDTO = oVitalityResponse;
        onewResponse.BenefitStatus = true;
        onewResponse.ExchangeStatus = false;
        onewResponse.VitalityStatus = true;
        onewResponse.VitalityRecordsTotalCount = '30';
        onewResponse.BenefitStatusNote = 'ok';
        onewResponse.ExchangeStatusNote = null;
        onewResponse.VitalityStatusNote = 'ok';
        
        System.runAS(oUser)
        {
            Account oAccount = [Select id,Source_Platform_Code__c,Source_Customer_Key__c from Account where Source_Customer_Key__c = :'608356'limit 1];
            Test.setMock(WebServiceMock.class, new HUMGBEServiceMockTest());
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oAccount);
            GroupDetailSection_C_HUM oGrpDetailController = new GroupDetailSection_C_HUM(oStdController);
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            
            Continuation objCon2 = (Continuation)oGrpDetailController.startRequest();
            System.assertEquals(objCon2.continuationMethod,objCon.continuationMethod);
            oGrpDetailController.oGBEResponse = onewResponse;
            Continuation objCon3 = (Continuation)oGrpDetailController.startRequest();
            System.assertEquals(objCon3.continuationMethod,'processResponse');

            oGrpDetailController.iTotalCount = 30;
            oGrpDetailController.iMaxRecordsToDisplay = 10;
            oGrpDetailController.iTotalRec  = 10;
            oGrpDetailController.getNext();
            System.assertEquals(oGrpDetailController.iCount, 11);
            
            oGrpDetailController.getPrevious();
            System.assertEquals(oGrpDetailController.iCount, 1);
            
            oGrpDetailController.processResponse();
            System.assertEquals(oGrpDetailController.bShowGroupDetails, false);
            
            oGrpDetailController.objGrpAccount.Source_Platform_Code__c = ''; 
            oGrpDetailController.startRequest();
            System.assertEquals(False, oGrpDetailController.bShowDivisionDetails );
            
            oGrpDetailController.oResponseGroupBenefitDTO = Null;
            oGrpDetailController.oGBEResponse = Null;
            oGrpDetailController.processResponse();
            System.assertEquals(False, oGrpDetailController.bShowGroupDetails);
            
            oGrpDetailController.oGBEResponse = onewResponse;
            oGrpDetailController.oGBEResponse.Platform = Null;
            oGrpDetailController.processResponse();
            System.assertEquals(False, oGrpDetailController.bShowDivisionDetails);
            
            oGrpDetailController.iCount = 0;
            Object oResult = oGrpDetailController.getPrevious();
            System.assertEquals(Null, oResult );
            
            GroupDetailSection_C_HUM oGrpDetailController1 = new GroupDetailSection_C_HUM(Null);
            System.assertEquals(Null, oGrpDetailController1.objGrpAccount);
        }
    }
 }