/*******************************************************************************************************************************
Class Name    : NBADynamicStrategy_T_HUM 
Version         : 1.0
Created On      : 12/03/2020
Function        : Test Class for NBADynamicStrategy_S_HUM
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade                                  12/03/2020              original version - 95% Coverage
* Aishwarya Pawar								20/08/2021		build Fix for 9/24 release
* Aishwarya Pawar								27/4/2022				  REQ 2948386
*Aishwarya Pawar                				05/12/2022                REQ - 3285223 Alert Configuration on Plan Member Page
* Nilesh Gadkar					                05/04/2023			      User Story 3421330: T1PRJ0881713 - FR09.09: CRM Service must create and display a command alert notification on the Person Account Page when the HRA flag/indicator = N
* Kinal Mangukiya                               08/18/2023                  US 4812228 - T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – NBA
********************************************************************************************************************************/


@IsTest(SeeAllData=true) 
public class NBADynamicStrategy_T_HUM {
    
    /*
    * Method Name : getListOfRecommendationsToDisplayTest
    * Description : This method is used to test the constructor GetNBARecommendations_T_HUM class.
    * Return type : None
    * Parameter   : None
    */
    @IsTest
    static void getListOfRecommendationsToDisplayTest()
    {
        List<Interaction__c> interac = [Select id, name from Interaction__c limit 1];
        List<Recommendation_Detail__c> recommDet = [SELECT Id, Name, Provider_NPIId__c, Account__c,Account__r.Birthdate__c , Provider_TaxId__c, Recommendation__c,Recommendation__r.Location__c,Enterprise_ID__c FROM Recommendation_Detail__c WHERE Active__c =true AND(Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') AND Account__c != null AND Recommendation__r.Show_To__c = 'Member' AND Recommendation__r.Location__c ='All Pages' WITH USER_MODE LIMIT 1];
        Test.StartTest();
        if(recommDet.size() > 0 && interac.size() >0) {
            NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
            String contextRecordId = recommDet[0].Account__c;
            String interactionId = interac[0].id; 
            String sPageName = 'Person Account'; 
            String enterpriseId = ''; 
            String accountId = recommDet[0].Account__c;
			String MCDProductEligible = 'true';
            String HRAEligible='false';
	    Integer showAlertCount = 0;
            Boolean bInteractionId = true;
            NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false' , MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
        }
        else {
            List<Recommendation_Detail__c> recommDetPer = [SELECT Id, Name, Provider_NPIId__c, Account__c,Account__r.Birthdate__c , Provider_TaxId__c, Recommendation__c,Recommendation__r.Location__c,Enterprise_ID__c FROM Recommendation_Detail__c WHERE Active__c =true AND(Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') AND Account__c != null AND Recommendation__r.Show_To__c = 'Member' AND Recommendation__r.Location__c ='Person Account' WITH USER_MODE LIMIT 1];
            if(recommDetPer.size() > 0 && interac.size() >0) {
                NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
                String contextRecordId = recommDet[0].Account__c;
                String interactionId = interac[0].id; 
                String sPageName = recommDet[0].Recommendation__r.Location__c;
                //String enterpriseId = '';
				String enterpriseId = recommDet[0].Enterprise_ID__c;
                String accountId = recommDet[0].Account__c;
				String MCDProductEligible = 'true';
                String HRAEligible='false';
		Integer showAlertCount = 1;
                Boolean bInteractionId = true;
                NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false',MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
            }
            else {
                List<Recommendation_Detail__c> recommDetNoLcation = [SELECT Id, Name, Provider_NPIId__c, Account__c,Account__r.Birthdate__c ,Provider_TaxId__c, Recommendation__c,Recommendation__r.Location__c,Enterprise_ID__c FROM Recommendation_Detail__c WHERE Active__c =true AND(Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') AND Account__c != null AND Recommendation__r.Show_To__c = 'Member' WITH USER_MODE LIMIT 1];
                if(recommDetPer.size() > 0 && interac.size() >0) {
                NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
                String contextRecordId = recommDet[0].Account__c;
                String interactionId = interac[0].id; 
                String sPageName = 'Person Account';
                String enterpriseId = '';
                String accountId = recommDet[0].Account__c;
				String MCDProductEligible = 'true';
                String HRAEligible='false';
		Integer showAlertCount = 0;
                Boolean bInteractionId = true;
                NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false',MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
                }
             }
        }
        Test.StopTest();
    }
    
    /*
    * Method Name : getListOfRecommendationsToDisplayNegTest
    * Description : This method is used to test Negative scenario of the constructor GetNBARecommendations_T_HUM class.
    * Return type : None
    * Parameter   : None
    */
    @IsTest
    static void getListOfRecommendationsToDisplayNegTest()
    {
        List<Interaction__c> interac = [Select id, name from Interaction__c limit 1];
        List<Recommendation_Detail__c> recommDet = [SELECT Id, Name, Provider_NPIId__c, Account__c, Provider_TaxId__c, Recommendation__c,Recommendation__r.Location__c,Enterprise_ID__c FROM Recommendation_Detail__c WHERE Active__c =true AND(Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') AND Account__c != null AND Recommendation__r.Show_To__c = 'Member' AND Recommendation__r.Location__c ='All Pages' AND Recommendation__r.Alert_Type__c = 'Contact Handling' WITH USER_MODE LIMIT 1];
        Test.StartTest();
        if(recommDet.size() > 0 &&  interac.size() >0) {
            NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
            String contextRecordId = recommDet[0].Account__c;
            String interactionId = null; 
            String sPageName = 'Claims Summary';
            String enterpriseId = '';
            String accountId = recommDet[0].Account__c;
			String MCDProductEligible = 'false';
            String HRAEligible='false';
	    Integer showAlertCount = 0;
            Boolean bInteractionId = false;
            NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false',MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
        }
        else {
            List<Recommendation_Detail__c> recommDetPer = [SELECT Id, Name, Provider_NPIId__c, Account__c, Provider_TaxId__c, Recommendation__c,Recommendation__r.Location__c,Enterprise_ID__c FROM Recommendation_Detail__c WHERE Active__c =true AND(Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') AND Account__c != null AND Recommendation__r.Show_To__c = 'Member' AND Recommendation__r.Location__c ='Person Account' AND Recommendation__r.Alert_Type__c = 'Contact Handling' WITH USER_MODE LIMIT 1];
            if(recommDetPer.size() > 0) {
                NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
                String contextRecordId = recommDet[0].Account__c;
                String interactionId = null; 
                String sPageName = recommDet[0].Recommendation__r.Location__c;
                String enterpriseId = '';
                String accountId = recommDet[0].Account__c;
				String MCDProductEligible = 'false';
                String HRAEligible='false';
		Integer showAlertCount = 0;
                Boolean bInteractionId = false;
                NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false',MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
            }
            else {
                List<Recommendation_Detail__c> recommDetNoLcation = [SELECT Id, Name, Provider_NPIId__c, Account__c, Provider_TaxId__c, Recommendation__c,Recommendation__r.Location__c,Enterprise_ID__c FROM Recommendation_Detail__c WHERE Active__c =true AND(Action__c = '' OR Action__c = 'Acknowledged' OR Action__c = 'Not Delivered' OR Action__c = 'None') AND Account__c != null AND Recommendation__r.Show_To__c = 'Member' AND Recommendation__r.Alert_Type__c = 'Contact Handling' WITH USER_MODE LIMIT 1];
                NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
                if(recommDetNoLcation.size() > 0) {
                    
                
                String contextRecordId = recommDet[0].Account__c;//
                String interactionId = null; 
                String sPageName = 'Person Account';
                String enterpriseId = '';
                String accountId = recommDet[0].Account__c;
				String MCDProductEligible = 'false';
                String HRAEligible='false';
		Integer showAlertCount = 0;
                Boolean bInteractionId = false;
                NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false',MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
            }
          }
        
        }
        Test.StopTest();
    }
     /*
    * Method Name : getMCDPendedTest
    * Description : This method is used to test MCD-Pended alert scenario.
    * Return type : None
    * Parameter   : None
    */
        @IsTest
    static void getMCDPendedTest()
    {
        List<Account> lstAcc = new List<Account>();
        lstAcc.add(new Account(Name='Test0',Home_Office_Account__c=false,Group_Number__c='325240',Tenant_Id__c='00'));
        insert lstAcc;
        
        Policy__c objPol = HUMTestDataHelperTest.getPolicy();
        objPol.Group_Name__c = lstAcc[0].Id;
        objPol.Policy_Group_Number__c = '325240';
        objPol.Name = 'Test Policy';
        insert objPol;

		Policy_Member__c objPolMem = new Policy_Member__c();
        objPolMem.Name = 'testpolmem11';
        objPolMem.Relationship__c = 'Self';
        objPolMem.Subscriber__c = objPolMem.Id;
        objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
        objPolMem.End_Date__c=Date.today().addDays(1).format();
        objPolMem.Member__c = lstAcc[0].Id;
        objPolMem.Policy__c = objPol.Id;
        objPolMem.Policy__r = objPol;
        objPolMem.Relationship_Code__c='00';
        insert objPolMem;
        
        MemberPlan objMemPlan = new MemberPlan();
        objMemPlan.RelationshipToSubscriber = 'Self';
        objMemPlan.EffectiveFrom=Date.today().addDays(-1);
        objMemPlan.EffectiveTo=Date.today().addDays(1);
        objMemPlan.Name='Test MCD Pended';
        objMemPlan.Policy__c=objPol.Id;
        objMemPlan.Policy_Member__c = objPolMem.Id;
        objMemPlan.Memberid = lstAcc[0].Id;
        objMemPlan.GroupNumber = '325240';
        insert objMemPlan;
        
        List<Policy_Member__c> polMem = [SELECT ID, Member__C FROM Policy_Member__c WHERE ID =: objPolMem.Id WITH USER_MODE limit 1];
         
        List<MemberPlan> memPlan = [SELECT ID FROM MemberPlan WHERE Id =: objMemPlan.Id WITH USER_MODE];
        
        Test.startTest();
        if(polMem.size()> 0)
        {
         NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
                String contextRecordId = polMem[0].Member__C;
                String interactionId = null; 
                String sPageName = 'Account';
                String enterpriseId = '';
                String accountId = polMem[0].Member__C;
				String MCDProductEligible = 'false';
            	String HRAEligible='false';
		Integer showAlertCount = 0;
                Boolean bInteractionId = false;
                NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false',MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
             //NBADynamicStrategy_S_HUM nbaDynamic = new NBADynamicStrategy_S_HUM();
                 contextRecordId = polMem[0].Id;
                 interactionId = null; 
                 sPageName = 'Account';
                 enterpriseId = '';
                 accountId = polMem[0].Member__C;
				 MCDProductEligible = 'false';
                 bInteractionId = false;
                NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId, interactionId, sPageName, enterpriseId, accountId,20 , 'false',MCDProductEligible,HRAEligible,showAlertCount,false,bInteractionId);
        }
        if(memPlan.size() > 0)
        {
            NBADynamicStrategy_S_HUM nbaPolMemDynamic = new NBADynamicStrategy_S_HUM();
                String contextRecordId1 = memPlan[0].Id;
                String interactionId1 = null; 
                String sPageName1 = 'Account';
                String enterpriseId1 = '';
                String accountId1 = polMem[0].Member__C;
				String MCDProductEligible1 = 'false';
            	String HRAEligible='false';
		Integer showAlertCount =1;
                Boolean bInteractionId1 = false;
                NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId1, interactionId1, sPageName1, enterpriseId1, accountId1,20 , 'false',MCDProductEligible1,HRAEligible,showAlertCount,false,bInteractionId1);
        }
         Test.StopTest();
    }
    @IsTest
    static void getListOfRecommendationsToCoverException()
    {
        RecordType oMemRecType = [SELECT Id,Name FROM RecordType WHERE Name = 'Member' WITH USER_MODE Limit 1];
        List<Account> acclist =[select id,name from account WITH USER_MODE limit 1] ;
                    
        Account oAcc = new Account();
            oAcc.Enterprise_ID__c='1030693160';
            oAcc.Mbr_Gen_Key__c = '1234567890';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
	        oAcc.Birthdate__c = '02/10/1974';
            oAcc.HRA_HNA_Completed__c ='N';
            insert oAcc;
    
    Test.StartTest();
            NBADynamicStrategy_S_HUM nbaDynamic2 = new NBADynamicStrategy_S_HUM();
            String contextRecordId1 = oAcc.Id;
            String interactionId1 =oAcc.Id ; 
            String sPageName1 = 'Person Account'; 
            String enterpriseId1 = '1113427548'; 
            String accountId1 = oAcc.Id;
	        String MCDProductEligible1 = 'false';
            String HRAEligible1='true';
	    Integer showAlertCount =3;
            Boolean bInteractionId1 = true;
            NBADynamicStrategy_S_HUM.ExecuteNBAStrategy(contextRecordId1, interactionId1, sPageName1, enterpriseId1, accountId1,20 , 'false' , MCDProductEligible1,HRAEligible1,showAlertCount,false,bInteractionId1);
        	Integer productDayLimit = NBADynamicStrategy_S_HUM.getDayLimit('OH');
            String EffectiveDate ='03/01/2023';
            NBADynamicStrategy_S_HUM.getAlertCount(1, EffectiveDate, productDayLimit, contextRecordId1);
            Integer alertCount = NBADynamicStrategy_S_HUM.getAlertCount(showAlertCount, EffectiveDate, productDayLimit, contextRecordId1);
            NBADynamicStrategy_S_HUM.getHRAStatus('{"HRACompletedDate":"2023-01-01","HRAStatus":"Y","HRAExpiryDate":"2024-01-01"}');
            NBADynamicStrategy_S_HUM.getHRAStatus('"{"HRACompletedDate":"2023-01-01","HRAStatus":"Y","HRAExpiryDate":"2024-01-01"}"');
            NBADynamicStrategy_S_HUM.checkHRAAlert(alertCount,showAlertCount,'N',10);
            Test.StopTest(); 
    }

}