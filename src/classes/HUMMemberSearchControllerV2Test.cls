/*******************************************************************************************************************************
Apex Class Name : HUMMemberSearchControllerV2Test
Version         : 2.0
Created On      : 08/05/2014
Function        : Test class for HUMMemberSearchControllerV2.
Modification Log: 
* Developer Name           Code Review              Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani         25261                     08/05/2014                  Original version
* Manish Kumar              25261                     08/05/2015                  Modified code to reach high code coverage
* Prasanthi Kandula         46228                     10/14/2016                  Modified code to increase code coverage
* Prafull Verma             84636                     04/05/2017                  REQ - 306322 : SF - TECH - Softphone routing optimization
* Mohammed Noor             96594                     05/24/2017                  REQ - 311432 Softphone / CRM Results for All Family Members by searching using Member Id only and launch optimization.
* Mohammed Noor             99521                     06/05/2017                  Defect fix for 291013.0001
* Harika Devi Kalidindi     112000                    07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Amar Gadewar                                        08/01/2017                  REQ - 323549 - SF - TECH - If single Result adding as a Parameter.
* Santhi Mandava            229797                    05/16/2018                  REQ - 362027 Implemented platform encryption for member_Id__c
* Santhi Mandava            262341                    07/17/2018                  Implemented platform encryption for custom search related account fields
* Moshitha Gunasekaran      262118                    06/28/2018                  REQ - 350472 ID Number field on the Unknown Account tab
* Charan Konakandla         289399                    09/21/2018                  REQ- 367752 :Able to Search BY PID in search page.
* Santhi Mandava                                      11/29/2018                  Implemented platform encryption for Account.Name
* Melkisan Selvaraj           340805                    02/28/2019                  REQ - 367741 - Ability to view middle initial
* Santhi Mandava                                       04/15/2019                 Replaced "Member_Id_Value__c" with Name
* Prudhvi Pamarthi                                	   07/15/2019                 REQ - 396335 Legacy Delete Requirement
* Atia Uzma                                            05/28/2021                 User Story 2325460: T1PRJ0001827 - MF 1 - Quality - Ability to display "Standardized Greeting" message on the Search Page (RF)
*******************************************************************************************************************************/
@isTest
private class HUMMemberSearchControllerV2Test
{ 
    /*
     * Method Name : prepareTestData
     * Description : Create common test Data
     * Return type : void
     * Parameter   : NA
     */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='MembersearchConstantZero',StringValue__c='0',IntegerValue__c=0));
            lstConstants.add(new HUMConstants__c(Name='HUMServiceTimeOut',StringValue__c='12',IntegerValue__c=12));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 2);
            
            HUMSearchLimit__c oSearchLimit = new HUMSearchLimit__c(Name='Member_SearchLimit', Search_Limit__c=101);
            insert oSearchLimit;
            System.assertNotEquals(oSearchLimit, NULL, 'Value not matched');
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '987654321';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.Birthdate__c = '01/01/1984';
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
	    oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1030693160';
            oAcc.Home_Office_Account__c = false;
            insert oAcc;
            System.AssertNotEquals(oAcc, NULL);
            
            Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
            Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
            Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(HUMConstants.HumMemberRecordType);
            
            Policy__c oPolicy1 = new Policy__c();
            oPolicy1.Group_Name__c = oAcc.id;
            oPolicy1.Policy_Group_Number__c='11111';
            insert oPolicy1;
            System.AssertNotEquals(oPolicy1, NULL);
            
            Policy_Member__c oPolicyMember1 = new Policy_Member__c();
            oPolicyMember1.Member__c = oAcc.ID;
            oPolicyMember1.Policy__c = oPolicy1.ID;
            oPolicyMember1.Name = '666';
            insert oPolicyMember1;
            System.AssertNotEquals(oPolicyMember1, NULL);
            
            Member_ID__c oMember = new Member_ID__c();
            oMember.Policy_Member__c = oPolicyMember1.ID;
            oMember.Name = '123456';
            oMember.Member_Dependent_Code__c ='01';
            oMember.Policy__c = oPolicy1.ID;
            oMember.Member_Card_External_ID__c = 'M123';
            insert oMember;
            System.AssertNotEquals(oMember, NULL);
            
            Member_ID__c oMember1 = new Member_ID__c();
            oMember1.Policy_Member__c = oPolicyMember1.ID;
            oMember1.Name = '12@345';
            oMember1.Member_Dependent_Code__c ='06';
            oMember1.Policy__c = oPolicy1.ID;
            oMember1.Member_Card_External_ID__c = 'M1283';
            insert oMember1;
            System.AssertNotEquals(oMember1, NULL);

            ConsumerID__c oConsumerId = new ConsumerID__c();
            oConsumerId.Account__c = oAcc.ID;
            oConsumerId.Consumer_External_ID__c = 'UKM|00|SFDC|HumanaId|a0uL00000058F50IAE';
            oConsumerId.Consumer_ID__c = '12345';
            oConsumerId.ID_Type__c = 'HumanaId';
            insert oConsumerId;
            System.AssertNotEquals(oConsumerId, NULL);
            
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'DOB' , Value__c = 'DOB');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'FirstName' , Value__c = 'TestFirst');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'LastName' , Value__c = 'TestLast');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'MemberId' , Value__c = 'Member_Id1_SOFTPHONE');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'DOB2' , Value__c = 'DOB');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'FirstName2' , Value__c = 'TestFirst');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'LastName2' , Value__c = 'TestLast');
            insert new SOFTPHONE_PRPID_HUM__c( Name = 'MemberId2' , Value__c = 'TestId');
            System.assertNotEquals(SOFTPHONE_PRPID_HUM__c.getInstance('DOB').Value__C , NULL);
            
            HUMMemberSearchControllerV2 oHumMemberSearch = new HUMMemberSearchControllerV2();
            CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
                Name = '2325460',
                IsON__c = true,
                Release_Name__c = '07/09/2021',
                Scrum_Team__c = 'RapidForce',
                Story_Title__c = 'User Story 2325460: T1PRJ0001827 - MF 1 - Quality - Ability to display "Standardized Greeting" message on the Search Page (RF)');
            insert objCS;
            system.assertNotEquals(oHumMemberSearch.bCallGreetings, null);
            
            List<CRMFunctionality_ONOFF_Switch__c> lstswitch = new List<CRMFunctionality_ONOFF_Switch__c>();

            CRMFunctionality_ONOFF_Switch__c objSwitch1 = new CRMFunctionality_ONOFF_Switch__c(Name = '2037674',IsON__c = true);
            lstswitch.add(objSwitch1);
            CRMFunctionality_ONOFF_Switch__c objSwitch2 = new CRMFunctionality_ONOFF_Switch__c(Name = '755829',IsON__c = true);
            lstswitch.add(objSwitch2);
            CRMFunctionality_ONOFF_Switch__c objSwitch3 = new CRMFunctionality_ONOFF_Switch__c(Name = '1426106',IsON__c = true);
            lstswitch.add(objSwitch3);
			if(!lstswitch.isEmpty()){
               insert lstswitch;
            }
           
        }
    }
    
    /*
    * Method Name : prepareTestDataForSearchCriteria
    * Description : Test method to cover HUMMemberSearhController 
    * Return Type : NA
    * Parameters : NA
    */
    static testMethod void prepareTestDataForSearchCriteria()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        Account oAcc = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c from Account where Group_Number__c = '987654321' limit 1];
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        List<HumMemberSearchControllerV2> lstHumMemberSearch = new List<HumMemberSearchControllerV2>();
        HumMemberSearchControllerV2 oHumMemberSearch = NULL;
        for(integer i = 0 ; i < 20 ; i++)
        {
            lstHumMemberSearch.add(new HumMemberSearchControllerV2());
            
            lstHumMemberSearch[i].oSearchWrapper.sMemberId = '';
            lstHumMemberSearch[i].oSearchWrapper.sFirstName = '';
            lstHumMemberSearch[i].oSearchWrapper.sLastName = '';
            lstHumMemberSearch[i].oSearchWrapper.sBirthdate = '';
            lstHumMemberSearch[i].oSearchWrapper.sState = 'None';
            lstHumMemberSearch[i].oSearchWrapper.sPostalCode = '';
            lstHumMemberSearch[i].oSearchWrapper.sPhone = '';
            lstHumMemberSearch[i].oSearchWrapper.sGroupNumber = '';
            lstHumMemberSearch[i].oSearchWrapper.sPID = ''; 
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sMemberId, '');
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sFirstName, '');
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sLastName, '');
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sBirthdate, '');
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sState, 'None');
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sPostalCode, '');
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sPhone, '');
            System.AssertEquals(lstHumMemberSearch[i].oSearchWrapper.sGroupNumber, '');
        }
        
        lstHumMemberSearch[1].oSearchWrapper.sMemberId = '12345';
        System.AssertEquals(lstHumMemberSearch[1].oSearchWrapper.sMemberId, '12345');
        
        lstHumMemberSearch[2].oSearchWrapper.sMemberId = '12@345';
        System.AssertEquals(lstHumMemberSearch[2].oSearchWrapper.sMemberId, '12@345');
        
        lstHumMemberSearch[3].oSearchWrapper.sMemberId = '12345';
        lstHumMemberSearch[3].oSearchWrapper.sState = 'Arizona';
        System.AssertEquals(lstHumMemberSearch[3].oSearchWrapper.sMemberId, '12345');
        System.AssertEquals(lstHumMemberSearch[3].oSearchWrapper.sState, 'Arizona');
        
        lstHumMemberSearch[4].oSearchWrapper.sMemberId = '12345'; 
        lstHumMemberSearch[4].oSearchWrapper.sBirthdate = ' 08/05/1995';
        System.AssertEquals(lstHumMemberSearch[4].oSearchWrapper.sMemberId, '12345');
        System.AssertEquals(lstHumMemberSearch[4].oSearchWrapper.sBirthdate, ' 08/05/1995');
        
        lstHumMemberSearch[5].oSearchWrapper.sMemberId = '12345'; 
        lstHumMemberSearch[5].oSearchWrapper.sPostalCode = '40202';
        System.AssertEquals(lstHumMemberSearch[5].oSearchWrapper.sMemberId, '12345');
        System.AssertEquals(lstHumMemberSearch[5].oSearchWrapper.sPostalCode, '40202');
        
        lstHumMemberSearch[6].oSearchWrapper.sBirthdate = '08/05/1995';
        System.AssertEquals(lstHumMemberSearch[6].oSearchWrapper.sBirthdate, '08/05/1995');
        
        lstHumMemberSearch[7].oSearchWrapper.sBirthdate = '08/05/1995';
        lstHumMemberSearch[7].oSearchWrapper.sFirstName = 'huey';
        System.AssertEquals(lstHumMemberSearch[7].oSearchWrapper.sBirthdate, '08/05/1995');
        System.AssertEquals(lstHumMemberSearch[7].oSearchWrapper.sFirstName, 'huey');
        
        lstHumMemberSearch[8].oSearchWrapper.sPostalCode = '40202';
        System.AssertEquals(lstHumMemberSearch[8].oSearchWrapper.sPostalCode, '40202');
        
        lstHumMemberSearch[9].oSearchWrapper.sPostalCode = '40202';
        lstHumMemberSearch[9].oSearchWrapper.sFirstName = 'huey';
        System.AssertEquals(lstHumMemberSearch[9].oSearchWrapper.sPostalCode, '40202');
        System.AssertEquals(lstHumMemberSearch[9].oSearchWrapper.sFirstName, 'huey');
        
        lstHumMemberSearch[10].oSearchWrapper.sState = 'Arizona';
        System.AssertEquals(lstHumMemberSearch[10].oSearchWrapper.sState, 'Arizona');
        
        lstHumMemberSearch[11].oSearchWrapper.sState = 'Arizona';
        lstHumMemberSearch[11].oSearchWrapper.sFirstName = 'huey';
        System.AssertEquals(lstHumMemberSearch[11].oSearchWrapper.sState, 'Arizona');
        System.AssertEquals(lstHumMemberSearch[11].oSearchWrapper.sFirstName, 'huey');
        
        lstHumMemberSearch[12].oSearchWrapper.sFirstName = 'huey';
        System.AssertEquals(lstHumMemberSearch[12].oSearchWrapper.sFirstName, 'huey');
        
        lstHumMemberSearch[13].oSearchWrapper.sLastName = 'duck';
        System.AssertEquals(lstHumMemberSearch[13].oSearchWrapper.sLastName, 'duck');
        
        lstHumMemberSearch[14].oSearchWrapper.sMemberId = '12345';
        lstHumMemberSearch[14].oSearchWrapper.sFirstName = 'huey';
        lstHumMemberSearch[14].oSearchWrapper.sLastName = 'duck';
        lstHumMemberSearch[14].oSearchWrapper.sPostalCode = '40202';
        lstHumMemberSearch[14].oSearchWrapper.sBirthdate = '08/05/1995';
        lstHumMemberSearch[14].oSearchWrapper.sState = 'Arizona';
        lstHumMemberSearch[14].oSearchWrapper.sPhone = '(123) 456-7890';
        lstHumMemberSearch[14].oSearchWrapper.sGroupNumber = '987654321';
        
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sMemberId, '12345');
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sFirstName, 'huey');
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sLastName, 'duck');
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sPostalCode, '40202');
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sBirthdate, '08/05/1995');
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sState, 'Arizona');
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sPhone, '(123) 456-7890');
        System.AssertEquals(lstHumMemberSearch[14].oSearchWrapper.sGroupNumber, '987654321');
        
        lstHumMemberSearch[15].oSearchWrapper.sGroupNumber = '987654321';
        System.AssertEquals(lstHumMemberSearch[15].oSearchWrapper.sGroupNumber, '987654321');
        
        lstHumMemberSearch[17].oSearchWrapper.sGroupNumber = '1212';
        System.AssertEquals(lstHumMemberSearch[17].oSearchWrapper.sGroupNumber, '1212');
        
        lstHumMemberSearch[18].oSearchWrapper.sPID = '1234'; 
        System.AssertEquals(lstHumMemberSearch[18].oSearchWrapper.sPID , '1234');
        
        lstHumMemberSearch[19].oSearchWrapper.sFirstName='Daphiner';
        lstHumMemberSearch[19].oSearchWrapper.sLastName= 'Superman';
        System.AssertEquals(lstHumMemberSearch[19].oSearchWrapper.sFirstName , 'Daphiner');
        System.AssertEquals(lstHumMemberSearch[19].oSearchWrapper.sLastName , 'Superman');

        List<HumMemberSearchControllerV2> lstHumMemberSearch1 = new List<HumMemberSearchControllerV2>();
        HumMemberSearchControllerV2 oHumMemberSearch0 = new HumMemberSearchControllerV2();
        oHumMemberSearch0.memberID = oAcc.id;
        lstHumMemberSearch1.add(oHumMemberSearch0);
        System.AssertNotEquals(lstHumMemberSearch1.size(), 0);
        
        Map<String, String> mapEncryptedResult = new Map<String, String>
        {
            'FirstName' => '(FirstName Like \'huey%\') ',
            'LastName' => '(LastName Like \'duck%\') ',
            'Birthdate__c' => '(Birthdate__c = \'08/05/1995\') ',
            'PersonHomePhone' => '(PersonHomePhone = \'(123) 425-8569\') ',
            'PersonMailingPostalCode' => '(PersonMailingPostalCode = \'12345\') ',
            'Group_Number__c' => '(Group_Number__c = \'987654321\') ',
            'Name' => '( Name Like \'12345%\') ',
            'Consumer_ID__c' => '(Consumer_ID__c Like \'12345%\' )'
        };
        
        Map<String, String> mapEncryptedResult1 = new Map<String, String>
        {
            'FirstName' => '(FirstName Like \'huey%\') ',
            'LastName' => '(LastName Like \'duck%\') ',
            'Birthdate__c' => '(Birthdate__c = \'08/05/1995\') ',
            'PersonHomePhone' => '(PersonHomePhone = \'(123) 425-8569\') ',
            'PersonMailingPostalCode' => '(PersonMailingPostalCode = \'12345\') ',
            'Group_Number__c' => '(Group_Number__c = \'987654321\') ',
            'Name' => '( Name Like \'12345%\') ',
            'Consumer_ID__c' => '(Consumer_ID__c Like \'54234%\' )'
        };

        HumMemberSearchControllerV2 oHumMemberSearch111 = new HumMemberSearchControllerV2();
        System.AssertNotEquals(oHumMemberSearch111, NULL);
        oHumMemberSearch111.mapEncryptedResult = mapEncryptedResult;
        oHumMemberSearch111.setAccMemberId.add(oAcc.id);
        System.AssertNotEquals(oHumMemberSearch111.setAccMemberId.size(), 0);
        
        Test.startTest();
                
        oHumMemberSearch0.getThis();
        lstHumMemberSearch1[0].searchManual();    
        lstHumMemberSearch[14].sConsumerSearchQuery = 'SELECT ID, Name, FirstName, LastName,Individual_First_Name__c,Individual_Last_Name__c, RecordType.Name, Birthdate__c, PersonEmail, '+
                                   + 'PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                   + 'PersonMailingPostalCode, PersonHomePhone, UserRecordAccess.HasReadAccess, Home_Office_Account__c, ETL_Record_Deleted__c ' +
                                   + 'From Account Where RecordtypeId IN ' + oMemRecType ;
            
        lstHumMemberSearch[14].sConsumerSearchQuery += ' AND ' + mapEncryptedResult1.get('FirstName');
            
        lstHumMemberSearch[14].sConsumerSearchQuery += ' AND ' + mapEncryptedResult1.get('LastName');
        
        lstHumMemberSearch[14].sConsumerSearchQuery += ' AND ' + mapEncryptedResult1.get('Birthdate__c');
        
        lstHumMemberSearch[14].sConsumerSearchQuery += ' AND PersonMailingStateCode = \'' +string.escapeSingleQuotes(lstHumMemberSearch[14].oSearchWrapper.sState) + '\'';
        
        lstHumMemberSearch[14].sConsumerSearchQuery += ' AND ' + mapEncryptedResult1.get('PersonHomePhone');
        
        lstHumMemberSearch[14].sConsumerSearchQuery += ' AND ' + mapEncryptedResult1.get('PersonMailingPostalCode');
        lstHumMemberSearch[14].mapEncryptedResult = mapEncryptedResult1;
        lstHumMemberSearch[14].memberIdSearch = false;
        lstHumMemberSearch[14].unKnownMemberCheck = true;  
        lstHumMemberSearch[14].SearchConsumer();
       System.runAs(oCCSUser)
        {
            Account acctname=[SELECT FirstName,LastName FROM Account WHERE Enterprise_ID__c='1030693160' LIMIT 1];
            List<Id> fixedResultId=new List<Id>();
            fixedResultId.add(acctname.id);
            List<ConsumerID__c> customerId=new List<ConsumerID__c>([SELECT ID ,Consumer_ID__c FROM ConsumerID__c WHERE Consumer_ID__c= '12345' ]);
            fixedResultId.add(customerId[0].id);
            Test.setFixedSearchResults(fixedResultId);
                 }
        lstHumMemberSearch[19].mapEncryptedResult = mapEncryptedResult1;
        lstHumMemberSearch[19].memberIdSearch = false;
        lstHumMemberSearch[19].unKnownMemberCheck = true; 
                
        for(integer i = 0 ; i < lstHumMemberSearch.size() ; i++)
        {
            lstHumMemberSearch[i].mapEncryptedResult = mapEncryptedResult;
            lstHumMemberSearch[i].searchManual();
            lstHumMemberSearch[i].reset();
        }
        Test.stopTest(); 
                
        oHumMemberSearch111.searchManual();         
        
        lstHumMemberSearch[17].oSearchWrapper.sGroupNumber = '1212000';
        System.AssertEquals(lstHumMemberSearch[17].oSearchWrapper.sGroupNumber, '1212000');
        lstHumMemberSearch[17].searchManual();
        
        System.runAs(oCCSUser)
        {
            lstHumMemberSearch[1].oSearchWrapper.sMemberId = '12345';
            lstHumMemberSearch[1].searchManual();  
            lstHumMemberSearch[1].sConsumerSearchQuery =   'SELECT Id, Name, Account__c, Consumer_External_ID__c, Consumer_ID__c, ID_Type__c, Account__r.ID, Account__r.Name, Account__r.FirstName, Account__r.LastName,'
                        + 'Account__r.Individual_First_Name__c, Account__r.Individual_Last_Name__c,Account__r.RecordTypeId, Account__r.RecordType.Name, Account__r.Birthdate__c, Account__r.PersonEmail, Account__r.PersonMailingStreet, '
                        + 'Account__r.PersonMailingCity, Account__r.PersonMailingStateCode, Account__r.PersonMailingState, Account__r.PersonMailingPostalCode, Account__r.PersonHomePhone, Account__r.UserRecordAccess.HasReadAccess, '
                        + 'Account__r.Home_Office_Account__c, Account__r.ETL_Record_Deleted__c FROM ConsumerID__c where Account__r.ETL_Record_Deleted__c = false and ';
            lstHumMemberSearch[1].sConsumerSearchQuery += mapEncryptedResult1.get('Consumer_ID__c');  
            lstHumMemberSearch[1].memberIdSearch = true; 
            lstHumMemberSearch[1].unKnownMemberCheck = true; 
            lstHumMemberSearch[1].SearchConsumer();                      
            lstHumMemberSearch[14].searchManual();
            //search.query(lstHumMemberSearch[1].sConsumerSearchQuery);
           
        }
                 
        Set<ID> setIds = new Set<ID>();
        ID id1 = UserInfo.getUserId();
        setIds.add(id1);
        
        List<selectOption> lstMemberStateValue = new List<selectOption>();
        lstMemberStateValue = HUMUtilityHelper.getstateValue();
        System.assertNotEquals(lstMemberStateValue.size(), 0);
        System.runAs(oCCSUser)
        {
            lstHumMemberSearch[1].oSearchWrapper.sGroupNumber = '12345';
            lstHumMemberSearch[1].searchManual();
        } 
        HumMemberSearchControllerV2 oHUMMemSearch = new HumMemberSearchControllerV2();
        oHUMMemSearch.setAccount = NULL;
        oHUMMemSearch.checkResultSizeLimit();
        System.assertNotEquals(oHUMMemSearch.lstAccount.size(), 101);
        System.assertEquals(oHUMMemSearch.setAccount, NULL);
        
        oHUMMemSearch = new HumMemberSearchControllerV2();
        oHUMMemSearch.oSearchWrapper.sGroupNumber = '1212';
        oHUMMemSearch.oSearchWrapper.sMemberId = '123456';
        oHUMMemSearch.oSearchWrapper.sSuffix='01';
        oHUMMemSearch.searchManual();                      
        oHUMMemSearch = new HumMemberSearchControllerV2();
        
        String sQuery = 'SELECT ID, Name, FirstName, LastName,MiddleName,Individual_First_Name__c,Individual_Last_Name__c, RecordType.Name, Birthdate__c, PersonEmail, '+
                               + 'PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState,Group_Number__c, ' +
                               + 'PersonMailingPostalCode, PersonHomePhone, UserRecordAccess.HasReadAccess, Home_Office_Account__c ' +
                               + 'From Account Where RecordtypeId = \'' + oMemRecType.Id+ '\''+
                               +  'AND FirstName =\'' + oHUMMemSearch.oSearchWrapper.sFirstName +'\'' 
                               +  'AND LastName=\'' + oHUMMemSearch.oSearchWrapper.sLastName + '\'' + ' ';
        oHUMMemSearch.sSearchQuery = sQuery ;
        oHUMMemSearch.memberIdSearch = false;
        oHUMMemSearch.isSoftPhoneSearch =false;                        
        oHUMMemSearch.getEligibleAccountList('ASC');
        oHUMMemSearch.sSearchQuery = sQuery ;          
        oHUMMemSearch.reset();         
        HUMMemberSearchWrapperHelper oSearchWrapperwithCipher = new HUMMemberSearchWrapperHelper();
        Map<String, String> mapFieldCiper = new Map<String, String>();
        mapFieldCiper.put('DOB','Testdob');
        mapFieldCiper.put('FirstName','TestFirst');
        mapFieldCiper.put('LastName','TestLast');
	mapFieldCiper.put('MiddleName', 'TestMiddle');
        mapFieldCiper.put('TestId','TestId');
        oSearchWrapperwithCipher.sIntAboutType = 'Member';   
        oSearchWrapperwithCipher.mapfieldCipherMapping = mapFieldCiper;
        oHumMemberSearch0.bAccountLookup = true;
        oHumMemberSearch0.isSoftPhoneSearch = true;
        List<selectOption>  testOptions = oHumMemberSearch0.lstMemberStateValue;
        Map<String, String> mapFieldCiperOutput = oHumMemberSearch0.FieldCipherMaping(oSearchWrapperwithCipher);
        System.assertNotEquals(mapFieldCiperOutput, NULL);
        
        oSearchWrapperwithCipher.sIntAboutType = ' ';
        mapFieldCiperOutput = oHumMemberSearch0.FieldCipherMaping(oSearchWrapperwithCipher);
        System.assertNotEquals(mapFieldCiperOutput, NULL);
        
        HumMemberSearchControllerV2 oHUMMemSearchV2 = new HumMemberSearchControllerV2();
        oHUMMemSearchV2.oSearchWrapper.sGroupNumber = '1212';
        oHUMMemSearchV2.oSearchWrapper.sMemberId = '123456';
        oHUMMemSearchV2.oSearchWrapper.sSuffix='01';       
        oHUMMemSearchV2.memberIdErr = true;
        oHUMMemSearchV2.memberIdLengthErr = true;
        oHUMMemSearchV2.suffixErr = true;
        oHUMMemSearchV2.suffixLengthErr = true;
        oHUMMemSearchV2.fNameError = true;
        oHUMMemSearchV2.lNameError = true;
        oHUMMemSearchV2.lNameLengthErr = true;
        oHUMMemSearchV2.birthDateErr = true;
        oHUMMemSearchV2.postalCodeErr = true;
        oHUMMemSearchV2.groupNumberErr = true;
        oHUMMemSearchV2.phoneErr = true;
        oHUMMemSearchV2.phoneLengthErr = true;
        oHUMMemSearchV2.birthDateGreaterErr = true;     
        oHUMMemSearchV2.dataErrors();
        List<Apexpages.Message> msgs = ApexPages.getMessages();
        System.assertNotEquals(msgs, NULL);
        System.assertNotEquals(msgs.size(), 0);                
    }  
    
     /*
    * Method Name : testPlatformChanges
    * Description : Test method to cover HUMMemberSearhController 
    * Return Type : NA
    * Parameters : NA
    */
    static testMethod void testPlatformChanges()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        Account oAcc = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c from Account where Group_Number__c = '987654321' limit 1];
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        List<HumMemberSearchControllerV2> lstHumMemberSearch = new List<HumMemberSearchControllerV2>();
        List<Member_Id__c> lstMembers = [Select Id,Name from Member_Id__c];
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = lstMembers[0].Id;
        Test.setFixedSearchResults(fixedSearchResults);
        System.runAs(oCCSUser)
        {
            HumMemberSearchControllerV2 objMem = new HumMemberSearchControllerV2();
            objMem.searchManual();
            objMem.oSearchWrapper.sMemberId = '123456';
            objMem.oSearchWrapper.sSuffix = '01';
            objMem.searchManual();
            //System.assertEquals(1, objMem.setAccount.getResultSize());
            
            Map<String,String> mapFieldCiper = new Map<String,String>();
            mapFieldCiper.put('Member_Id1_SOFTPHONE','123456');
            objMem.oSearchWrapper.sIntAboutType = 'Member';   
            objMem.oSearchWrapper.mapfieldCipherMapping = mapFieldCiper;
            objMem.oSearchWrapper.sMemberId = '123456';
            objMem.datasearchSoftPhone();
			
			HumMemberSearchControllerV2 objMem1 = new HumMemberSearchControllerV2();
            objMem1.searchManual();
            objMem1.bAccountLookup = true;
            objMem1.clicksource = true;
            objMem1.oSearchWrapper.sMemberId = '123456';
            objMem1.oSearchWrapper.sSuffix = '01';
            objMem1.searchManual();
            //System.assertEquals(1, objMem1.setAccount.getResultSize());
        }
    } 
   /*
    * Method Name : testPlatformChangesNeg
    * Description : Test method to cover HUMMemberSearhControllerV2
    * Return Type : NA
    * Parameters : NA
    */
    static testMethod void testPlatformChangesNeg()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        Account oAcc = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c from Account where Group_Number__c = '987654321' limit 1];
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        List<HumMemberSearchControllerV2> lstHumMemberSearch = new List<HumMemberSearchControllerV2>();
        List<Member_Id__c> lstMembers = [Select Id,Name from Member_Id__c];
        Id [] fixedSearchResults= new Id[1];
        fixedSearchResults[0] = lstMembers[0].Id;
        Test.setFixedSearchResults(fixedSearchResults);
        System.runAs(oCCSUser)
        {
            List<Policy_member__c> policyMemLst =new List<Policy_member__c>([SELECT Id FROM Policy_member__c]);
            System.assertNotEquals(policyMemLst,Null);
            System.assertNotEquals(policyMemLst.size(),0);
            delete policyMemLst;
            List<Member_ID__c> memberIdLst=new List<Member_ID__c>([SELECT ID FROM Member_ID__c ]);
            System.assertNotEquals(memberIdLst,Null);
            System.assertNotEquals(memberIdLst.size(),0);
            delete memberIdLst;
            HumMemberSearchControllerV2 objMem = new HumMemberSearchControllerV2();
            objMem.unKnownMemberCheck=true;
            objMem.searchManual();
            
            objMem.unKnownMemberCheck=true;
            objMem.oSearchWrapper.sMemberId = '123456';
            objMem.oSearchWrapper.sSuffix = '01';
            objMem.oSearchWrapper.sPID='1234';
            objMem.searchManual();
            
            Map<String,String> mapFieldCiper = new Map<String,String>();
            mapFieldCiper.put('Member_Id1_SOFTPHONE','123456');
            objMem.oSearchWrapper.sIntAboutType = 'Member';   
            objMem.oSearchWrapper.mapfieldCipherMapping = mapFieldCiper;
            objMem.oSearchWrapper.sMemberId = '123456';
            objMem.datasearchSoftPhone();
        }
    }                                                           
     /*
    * Method Name : testMBEPlusService
    * Description : Test method to cover invokeMBEPlusService 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testMBEPlusService()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs(oCCSUser)
        {
            Test.startTest();
            HumMemberSearchControllerV2 objMem = new HumMemberSearchControllerV2();
            MemberSearchv2_S_HUM memberVar=new MemberSearchv2_S_HUM();
            objMem.sourcePersonId='0261350310165609';
            HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='MBEPlusService',Timeout__c='12000',Request_Type__c='POST',Active__c=true,Certificate_Name__c = 'Test_Certificate', 
                                                                                      Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
                                                                                      
            insert mbeServiceRecord;
            System.assertEquals(mbeServiceRecord.Name,'MBEPlusService');
            String mockResposne = '{"GetMemberResponse":{"Body":{"Member":{"EnterpriseId":"1030693160","Platform":"EM"}}}}';
            HttpResponse responseobj=new Httpresponse();
            responseobj.setBody(mockResposne);
            responseobj.setStatusCode(200);
            continuation con =(continuation)objMem.invokeMBEPlusService();
            Test.setContinuationResponse(memberVar.sRequestLabelPID,responseobj);
            object result=Test.invokeContinuationMethod(objMem,con);
            System.assertEquals(result,null);
            Test.StopTest();
    
    }                    
  }
   /*
    * Method Name : testMBEPlusService
    * Description : Test method to cover invokeMBEPlusService 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testMBEPlusServiceNeg2()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs(oCCSUser)
        {
            Test.startTest();
            HumMemberSearchControllerV2 objMem = new HumMemberSearchControllerV2();
            objMem.sourcePersonId='0261350310165609';
            MemberSearchv2_S_HUM memberVar=new MemberSearchv2_S_HUM();
            HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='MBEPlusService',Timeout__c='12000',Request_Type__c='POST', Active__c=true,Certificate_Name__c = 'Test_Certificate', 
                                                                                      Content_Type__c = 'application/json',End_Point_URL__c = 'test_End_Point');
                                                                                      
            insert mbeServiceRecord;
            System.assertEquals(mbeServiceRecord.Name,'MBEPlusService');
            continuation con =(continuation)objMem.invokeMBEPlusService();
            String mockResposne = '{"GetMemberResponse":{"Body":{"Member":{"EnterpriseId":null,"Platform":"EM"}}}}';
            HttpResponse responseobj=new Httpresponse();
            responseobj.setBody(mockResposne);
            responseobj.setStatusCode(200);
            Test.setContinuationResponse(memberVar.sRequestLabelPID,responseobj);
            object result=Test.invokeContinuationMethod(objMem,con);
            System.assertEquals(result,null);
            Test.StopTest();
    
    }                    
  } 
  /*
    * Method Name : testMBEPlusService_NegativeTestScenario
    * Description : Test method to cover testMBEPlusService_NegativeTestScenario 
    * Return Type : NA
    * Parameters : NA
    */
    static @isTest void testMBEPlusService_NegativeTestScenario()
    {
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        System.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs(oCCSUser)
        {
            Test.startTest();
            HumMemberSearchControllerV2 objMem = new HumMemberSearchControllerV2();
            MemberSearchv2_S_HUM memberVar=new MemberSearchv2_S_HUM();
            objMem.sourcePersonId='0261350310165609';
            HUM_Webservice_Callout__c mbeServiceRecord =new HUM_Webservice_Callout__c(Name='MBEPlusService',Timeout__c='12000',Request_Type__c='POST',Active__c=false,Certificate_Name__c = 'Test_Certificate', Content_Type__c = 'application/json',
                                                                                      End_Point_URL__c = 'test_End_Point');
                                                                                       
            insert mbeServiceRecord;
            System.assertEquals(mbeServiceRecord.Name,'MBEPlusService');
            continuation con =(continuation)objMem.invokeMBEPlusService();
            String mockResposne = ' ';
            HttpResponse responseobj=new Httpresponse();
            responseobj.setBody(mockResposne);
            responseobj.setStatusCode(404);
            Test.setContinuationResponse(memberVar.sRequestLabelPID,responseobj);
            object result=Test.invokeContinuationMethod(objMem,con);
            System.assertEquals(result,null);
            objMem.sEnterpriseId = 'test'; 
            objMem.buildEnterpriseIDQuery();
            Test.stopTest();
    
    }                    
  } 
}