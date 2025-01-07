/*****************************************************************************************
Apex class Name    : GroupDetailSection_C_T_HUM_V2
Version            : 1.0
Function           : Test class for GroupDetailSection_C_HUM_V2
Created Date       : 14 March 2016

Modification Log:
*   Developer                        Code Review            Date                Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Praveen Kumar Parimi                23955              03/14/2016            Original Version
*********************************************************************************************************************************/
@isTest
private  class GroupDetailSection_C_T_HUM_V2
{
    /*
    * Method Name : setup
    * Description : This method is used to insert the data only once and called automicticaly in all methods
    * Return type : Void
    * Paramater   : NA
    */
     @testSetup static void setup() 
     {   
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
            oHUMGBESservice.name='GroupBusinessEntityService';
            oHUMGBESservice.Active__c=true;
            oHUMGBESservice.Certificate_Name__c='Certificate';
            oHUMGBESservice.End_Point_URL__c='https://int-crmservicews.humana.com/GBEServiceREST/GetGroupInfo';
            oHUMGBESservice.Timeout__c='12000';
            oHUMGBESservice.Request_Type__c = 'POST';
            insert oHUMGBESservice ;
            system.assert(oHUMGBESservice.id!=null);
            system.assert(oHUMGBESservice.name == 'GroupBusinessEntityService');
            
            HUMConstants__c oConstant1 = new HUMConstants__c(Name = 'GBES_DivisionSubGroup_Records_HUM',IntegerValue__c= 5,StringValue__c = '5') ;
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
        System.runAS(oUser)
        {
            GBEServiceResponse_H_HUM objResponse = new GBEServiceResponse_H_HUM();
            objResponse.GroupInfo = new GBEServiceResponse_H_HUM.GroupInfo();
            objResponse.GroupInfo.Platform = 'EM';
            objResponse.GroupInfo.EffectiveDate = '03-10-1990'; 
            objResponse.GroupInfo.DentalOriginalEffectiveDate= '03-10-1990';
            objResponse.GroupInfo.NextRenewalDate = '13-20-2013';
            objResponse.GroupInfo.EnrolledSubscriberCountMedical = '23';
            objResponse.GroupInfo.EnrolledSubscriberCountDental = '23';
            objResponse.GroupInfo.DivisionRecordsTotalCount = '5';
            
            objResponse.GroupInfo.DivisionList = new GBEServiceResponse_H_HUM.DivisionList();
            objResponse.GroupInfo.DivisionList.Division = new List<GBEServiceResponse_H_HUM.Division>();
            
            GBEServiceResponse_H_HUM.Division objDiv;
            for(Integer i = 0; i < 2; i++)
            	{   
	                objDiv = new GBEServiceResponse_H_HUM.Division();
	                objDiv.VitalityIndicator = 'SX';
	                objDiv.SubGroupID = '1234';
	                objDiv.SubGroupName = 'Humana Group';
	                objDiv.unitCount = '122'; 
	                objDiv.Unit = 'D';
	                objDiv.EffectiveProvision = 'FM';
	                objResponse.GroupInfo.DivisionList.Division.Add(objDiv);
            }
        
            Account oAccount = [Select id,Source_Platform_Code__c,Source_Customer_Key__c from Account where Source_Customer_Key__c = :'608356'limit 1];
            Test.setMock(WebServiceMock.class, new HUMGBEServiceMockTest());
            
            ApexPages.StandardController oStdController = new ApexPages.StandardController(oAccount);
            GroupDetailSection_C_HUM_V2 oGrpDetailController = new GroupDetailSection_C_HUM_V2(oStdController);
            oGrpDetailController.objGrpAccount =  oAccount ;
            
            Object objRes = oGrpDetailController.getGroupInfo();
            System.assertNotEquals(Null,objRes);
             
            oGrpDetailController.oReturnedresponse = objResponse;
            oGrpDetailController.iMaxRecordsToDisplay = 5;
            oGrpDetailController.groupInfoDivisonData();
            System.assertEquals(true,oGrpDetailController.bShowDivisionDetails );
             
            oGrpDetailController.iTotalCount = 5;
            oGrpDetailController.iMaxRecordsToDisplay =3;
            oGrpDetailController.iCount =1;
            oGrpDetailController.getNext();
            System.assertEquals(2,oGrpDetailController.iTotalRec );
             
            oGrpDetailController.getPrevious();
            System.assertEquals(3,oGrpDetailController.iTotalRec );
             
            oGrpDetailController.processResponse();
            System.assertEquals(false,oGrpDetailController.bShowGroupDetails );
             
            oGrpDetailController.objGrpAccount.Source_Platform_Code__c ='';
            oGrpDetailController.getGroupInfo();
            System.assertEquals(false,oGrpDetailController.bShowDivisionDetails );
             
            oGrpDetailController.iTotalCount = 2;
            oGrpDetailController.iMaxRecordsToDisplay =3;
            objRes = oGrpDetailController.getNext();
            System.assertEquals(Null,objRes);
             
            oGrpDetailController.oReturnedresponse.GroupInfo.DivisionRecordsTotalCount = '6';
            oGrpDetailController.iMaxRecordsToDisplay = 5;
            oGrpDetailController.iTotalRec = 4;
            oGrpDetailController.groupInfoDivisonData();
            System.assertEquals(4,oGrpDetailController.iSize);
        }
    }
 }