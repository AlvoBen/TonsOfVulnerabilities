/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdateTemplateInsertion_T_HUM
    @Version          : 1.0
    @Created Date     : June 27th, 2020
    @Function         : Test class for DemographicUpdateTemplateInsertion_D_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  27/06/2020                    Original Version
* Ajay Chakradhar                                  20/05/2022                    User Story 2568799: (CR 1945 / 1789) - Success / Fail indication in Process Title - Case Edit Page
******************************************************************************************************************************************************************************/
@istest
public class DemographicUpdateTemplateInsertion_T_HUM {
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
     @testSetup 
     Private static void prepareTestData() 
     {
         User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         insert oCCSUser;
         system.assertEquals(oCCSUser.LastName,'test User1');
         
         System.runAs(oCCSUser) 
        {
             Account objAcc = HUMTestDataHelperTest.getAccount(); 
             objAcc.FirstName = 'Test';
             objAcc.LastName = 'Account';
             objAcc.Tenant_Id__c = '00';
             objAcc.Enterprise_ID__c = '1005577525';
             objAcc.General_Account__c = true;
             objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
             insert objAcc;
             System.assertEquals(objAcc.Tenant_Id__c,'00');
             
             Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
             insert oProd;
             System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
             
             Policy__c objpol = HUMTestDataHelperTest.getPolicy();
             objpol.Name='testPolicy';
             objpol.Group_Name__c = objAcc.Id;
             objpol.Major_LOB__c = 'MER';
             objpol.Source_Cust_Cov_Key__c = '123456789';
             objpol.Product__c = oProd.Id;
             objpol.Product__r = oProd;
             objpol.Platform__c = 'LV';
             insert objpol;
             system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
             
             Policy_Member__c objPolMem = new Policy_Member__c();
             objPolMem.Name = 'testpolmem11';
             objPolMem.Relationship__c = 'Self';
             objPolMem.Relationship_Code__c = '00';
             objPolMem.Subscriber__c = objPolMem.Id;
             objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
             objPolMem.End_Date__c=Date.today().addDays(1).format();
             objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
             objPolMem.Member__c = objAcc.Id;
             objPolMem.Policy__c = objpol.Id;
             objPolMem.Policy__r = objpol;
             insert objPolMem;
             system.assertEquals(objPolMem.Relationship__c, 'Self');
             
             Classification__c classificationRec = New Classification__c();
             classificationRec.Name = 'Claims';
             classificationRec.Is_Active__c = true;
             insert classificationRec;
             System.assertEquals(classificationRec.Name,'Claims');
             
             Intent__c intentRec = New Intent__c();
             intentRec.Name = 'Filling';
             intentRec.Is_Active__c = true;
             insert intentRec;
             System.assertEquals(intentRec.Name,'Filling');
             
             Case oCase1 = New Case(AccountId = objAcc.Id, Intent_Id__c = intentRec.Id, Classification_Id__c = classificationRec.Id, 
                                    Policy_Member__c = objPolMem.Id,Classification_Type__c='calls',Interacting_With_Type__c = 'Group',
                                    Interacting_With__c=objAcc.Id,Due_Date__c=Date.Today(),Subtype__c='Incomplete',Origin='Email',
                                    Case_Owner__c = UserInfo.getUserName(), Owner_Queue__c ='Cincinnati CRU',DCN__c='12345678', 
                                    Work_Queue_View_Name__c='Cincinati CRU',Interacting_About_Type__c='Member',Type='PPI',
                                    G_A_Rights_Given__c='Yes',G_A_Reason__c='Other',Service_Center__c ='Cincinnati', Department__c='CRU',
                                    status='In Progress');
             insert oCase1;
             System.assertEquals(oCase1.DCN__c,'12345678');
            
             Template__C Template=new Template__c();
             Template.Description__c='Address Update - Medicare';
             insert template;
              
             Template_Field__c TemField=new Template_Field__c();
             TemField.Name='PersonSpeakingWith';
             TemField.Template__c=Template.id;
             insert TemField;
             System.assertNotEquals(TemField.id,Null);	
             
             Template_Field__c TemField1 = new Template_Field__c();
             TemField1.Name='UserInterfaceData';
             TemField1.Template__c=Template.id;
             insert TemField1;
             System.assertNotEquals(TemField.id,Null);

             Template_Field__c TemField2 = new Template_Field__c();
             TemField2.Name='ServiceResponse';
             TemField2.Template__c=Template.id;
             insert TemField2;
             System.assertNotEquals(TemField2.id,Null);

             CRMFunctionality_ONOFF_Switch__c objCS = new CRMFunctionality_ONOFF_Switch__c(
             Name = '1935446',
             IsON__c = true,
             Release_Name__c ='2/12/2020',
             Scrum_Team__c ='Thunder');
             insert objCS;
        }    
    }
    
   /**
    * This method is used to Process Template Data
    *
    * @param     NULL
    * @return    void 
    */
    @istest
    Private Static void  testprocessTemplateData()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testuser)
        {
            Case caseObj = [Select id from Case where DCN__c='12345678' limit 1];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
            CRMFunctionality_ONOFF_Switch__c onOffObj = [select Name,IsON__c from CRMFunctionality_ONOFF_Switch__c where Name = '1935446' limit 1];
            Test.startTest();
            	String templateData = '{"templateBase":[{"data":{"rsoCOD":{"HomeEmail":"HOME@HUMANATEST.COM","WorkEmail":"NONE","HomePhone":"(985) 758-7716","Mobile":"(343) 433-4234","WorkPhone":"","WorkPhoneExt":""}},"templateName":"Contact Demographic Update - Medicare","key":"cod"},{"data":{"rsoCRD":{"FirstName":"RICHILEE","MiddleInitial":"E","LastName":"EDNILAO","Gender":"M","DateOfBirth":"10/15/1947","SSN":""}},"templateName":"Critical Demographic Update - Medicare","key":"crd"},{"data":{"rsoMAU":{"PersonSpeakingWith":"4232323243234","RelationToMember":"34342344234","PermanentResidentialPhoneNumber":"(342) 432-4323","WhatAddressMemberCallingToUpdate":"residential","PermanentResidentialAddress":"9022 Shelbyville Road Apt 66","PermanentResidentialCityName":"Louisville","PermanentResidentialStateCode":"KY","PermanentResidentialZipCode":"40222","PermanentResidentialCountyName":"Jefferson","PermanentResidentialStartDate":"2021-03-11","DoesMemberHaveMailAddressDiffFromPermAddress":"No","MailingAddress":"9022 Shelbyville Road Apt 66","MailingCityName":"Louisville","MailingStateCode":"KY","MailingZipCode":"40222","MailingCounty":"Jefferson","DoesMailMemberHaveTemporaryAddress":"No","TemporaryAddress":"","TemporaryCityName":"","TemporaryStateCode":"","TemporaryZipCode":"","TemporaryCountyName":"","TemporaryOSAStartDate":"","TemporaryOSAEndDate":""}},"templateName":"Address Update - Medicare","key":"mau"}]}';
                //String templateData = '{"templateBase":[{"data":{"rsoMAU":{"PersonSpeakingWith":"MARIE RODGERS","RelationToMember":"MEMBER","PermanentResidentialPhoneNumber":"(876) 756-9769","WhatAddressMemberCallingToUpdate":"temporary","PermanentResidentialAddress":"1723 KEENELAND COURT","PermanentResidentialCityName":"MURFREESBORO","PermanentResidentialStateCode":"TN","PermanentResidentialZipCode":"37217","PermanentResidentialCountyName":"RUTHERFORD","PermanentResidentialStartDate":"2020-06-11","DoesMemberWantMailSentToTemporaryAddress":"No","DoesMemberWantMailAddressDiffFromResAddress":"No","MailingAddress":"1723 KEENELAND COURT","MailingCityName":"MURFREESBORO","MailingStateCode":"TN","MailingZipCode":"37217","MailingCounty":"RUTHERFORD","TemporaryAddress":"1201 Christ RD","TemporaryCityName":"MURFREESBORO","TemporaryStateCode":"TN","TemporaryZipCode":"37217","TemporaryCountyName":"RUTHERFORD","TemporaryOSAStartDate":"2020-06-01","TemporaryOSAEndDate":"2020-06-26"}},"templateName":"Address Update - Medicare","key":"mau"}]}';
            	String caseNumber = caseObj.Id;
            	String policyId = pmObj.id;
            	String templateName = 'RSO';
            	DemographicUpdateTemplateInsertion_D_HUM.processTemplateData(caseNumber, templateData, templateName, policyId, true);
                onOffObj.IsON__c = false;
            	update onOffObj;
            	DemographicUpdateTemplateInsertion_D_HUM.processTemplateData(caseNumber, templateData, templateName, policyId, true);
            Test.stopTest();
        }
    }
    

    @istest
    Private Static void testinsertTemplateSubmissionData(){
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testuser){
            Case caseObj = [Select id from Case where DCN__c='12345678' limit 1];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
            Test.startTest();
            	String templateData = '{"templateBase":[{"data":{"rsoCOD":{"HomeEmail":"HOME@HUMANATEST.COM","WorkEmail":"NONE","HomePhone":"(985) 758-7716","Mobile":"(343) 433-4234","WorkPhone":"","WorkPhoneExt":""}},"templateName":"Contact Demographic Update - Medicare","key":"cod"},{"data":{"rsoCRD":{"FirstName":"RICHILEE","MiddleInitial":"E","LastName":"EDNILAO","Gender":"M","DateOfBirth":"10/15/1947","SSN":""}},"templateName":"Critical Demographic Update - Medicare","key":"crd"},{"data":{"rsoMAU":{"PersonSpeakingWith":"4232323243234","RelationToMember":"34342344234","PermanentResidentialPhoneNumber":"(342) 432-4323","WhatAddressMemberCallingToUpdate":"residential","PermanentResidentialAddress":"9022 Shelbyville Road Apt 66","PermanentResidentialCityName":"Louisville","PermanentResidentialStateCode":"KY","PermanentResidentialZipCode":"40222","PermanentResidentialCountyName":"Jefferson","PermanentResidentialStartDate":"2021-03-11","DoesMemberHaveMailAddressDiffFromPermAddress":"No","MailingAddress":"9022 Shelbyville Road Apt 66","MailingCityName":"Louisville","MailingStateCode":"KY","MailingZipCode":"40222","MailingCounty":"Jefferson","DoesMailMemberHaveTemporaryAddress":"No","TemporaryAddress":"","TemporaryCityName":"","TemporaryStateCode":"","TemporaryZipCode":"","TemporaryCountyName":"","TemporaryOSAStartDate":"","TemporaryOSAEndDate":""}},"templateName":"Address Update - Medicare","key":"mau"}]}';
            	//String templateData = '{"templateBase":[{"data":{"rsoMAU":{"UserInterfaceData":"MARIE RODGERS","RelationToMember":"MEMBER","PermanentResidentialPhoneNumber":"(876) 756-9769","WhatAddressMemberCallingToUpdate":"temporary","PermanentResidentialAddress":"1723 KEENELAND COURT","PermanentResidentialCityName":"MURFREESBORO","PermanentResidentialStateCode":"TN","PermanentResidentialZipCode":"37217","PermanentResidentialCountyName":"RUTHERFORD","PermanentResidentialStartDate":"2020-06-11","DoesMemberWantMailSentToTemporaryAddress":"No","DoesMemberWantMailAddressDiffFromResAddress":"No","MailingAddress":"1723 KEENELAND COURT","MailingCityName":"MURFREESBORO","MailingStateCode":"TN","MailingZipCode":"37217","MailingCounty":"RUTHERFORD","TemporaryAddress":"1201 Christ RD","TemporaryCityName":"MURFREESBORO","TemporaryStateCode":"TN","TemporaryZipCode":"37217","TemporaryCountyName":"RUTHERFORD","TemporaryOSAStartDate":"2020-06-01","TemporaryOSAEndDate":"2020-06-26"}},"templateName":"Address Update - Medicare","key":"mau"}]}';
            	String caseNumber = caseObj.Id;
            	String policyId = pmObj.id;
            	String templateName = 'RSO';
            	DemographicUpdateTemplateInsertion_D_HUM.processTemplateData(caseNumber, templateData, templateName, policyId, true);
            Test.stopTest();
        }
    }

     @istest
    Private Static void  testprocessTemplateDataFalse()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testuser)
        {
            Case caseObj = [Select id from Case where DCN__c='12345678' limit 1];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
            CRMFunctionality_ONOFF_Switch__c onOffObj = [select Name,IsON__c from CRMFunctionality_ONOFF_Switch__c where Name = '1935446' limit 1];
            Test.startTest();
            	String templateData = '{"templateBase":[{"data":{"rsoCOD":{"HomeEmail":"HOME@HUMANATEST.COM","WorkEmail":"NONE","HomePhone":"(985) 758-7716","Mobile":"(343) 433-4234","WorkPhone":"","WorkPhoneExt":""}},"templateName":"Contact Demographic Update - Medicare","key":"cod"},{"data":{"rsoCRD":{"FirstName":"RICHILEE","MiddleInitial":"E","LastName":"EDNILAO","Gender":"M","DateOfBirth":"10/15/1947","SSN":""}},"templateName":"Critical Demographic Update - Medicare","key":"crd"},{"data":{"rsoMAU":{"PersonSpeakingWith":"4232323243234","RelationToMember":"34342344234","PermanentResidentialPhoneNumber":"(342) 432-4323","WhatAddressMemberCallingToUpdate":"residential","PermanentResidentialAddress":"9022 Shelbyville Road Apt 66","PermanentResidentialCityName":"Louisville","PermanentResidentialStateCode":"KY","PermanentResidentialZipCode":"40222","PermanentResidentialCountyName":"Jefferson","PermanentResidentialStartDate":"2021-03-11","DoesMemberHaveMailAddressDiffFromPermAddress":"No","MailingAddress":"9022 Shelbyville Road Apt 66","MailingCityName":"Louisville","MailingStateCode":"KY","MailingZipCode":"40222","MailingCounty":"Jefferson","DoesMailMemberHaveTemporaryAddress":"No","TemporaryAddress":"","TemporaryCityName":"","TemporaryStateCode":"","TemporaryZipCode":"","TemporaryCountyName":"","TemporaryOSAStartDate":"","TemporaryOSAEndDate":""}},"templateName":"Address Update - Medicare","key":"mau"}]}';
                //String templateData = '{"templateBase":[{"data":{"rsoMAU":{"PersonSpeakingWith":"MARIE RODGERS","RelationToMember":"MEMBER","PermanentResidentialPhoneNumber":"(876) 756-9769","WhatAddressMemberCallingToUpdate":"temporary","PermanentResidentialAddress":"1723 KEENELAND COURT","PermanentResidentialCityName":"MURFREESBORO","PermanentResidentialStateCode":"TN","PermanentResidentialZipCode":"37217","PermanentResidentialCountyName":"RUTHERFORD","PermanentResidentialStartDate":"2020-06-11","DoesMemberWantMailSentToTemporaryAddress":"No","DoesMemberWantMailAddressDiffFromResAddress":"No","MailingAddress":"1723 KEENELAND COURT","MailingCityName":"MURFREESBORO","MailingStateCode":"TN","MailingZipCode":"37217","MailingCounty":"RUTHERFORD","TemporaryAddress":"1201 Christ RD","TemporaryCityName":"MURFREESBORO","TemporaryStateCode":"TN","TemporaryZipCode":"37217","TemporaryCountyName":"RUTHERFORD","TemporaryOSAStartDate":"2020-06-01","TemporaryOSAEndDate":"2020-06-26"}},"templateName":"Address Update - Medicare","key":"mau"}]}';
            	String caseNumber = caseObj.Id;
            	String policyId = pmObj.id;
            	String templateName = 'RSO';
            	DemographicUpdateTemplateInsertion_D_HUM.processTemplateData(caseNumber, templateData, templateName, policyId, false);
                onOffObj.IsON__c = false;
            	update onOffObj;
            	DemographicUpdateTemplateInsertion_D_HUM.processTemplateData(caseNumber, templateData, templateName, policyId, false);
            Test.stopTest();
        }
    }

    /**
    * This method is used to Process Template Data after Response
    *
    * @param     NULL
    * @return    void 
    */
    @istest
    Private Static void  testprocessTemplateDataOnResponse()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testuser)
        {
            Case caseObj = [Select id from Case where DCN__c='12345678' limit 1];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
            //CRMFunctionality_ONOFF_Switch__c onOffObj = [select Name,IsON__c from CRMFunctionality_ONOFF_Switch__c where Name = '1935446' limit 1];
            Test.startTest();
            	String templateData = '{"templateBase":[{"data":{"rsoCOD":{"HomeEmail":"HOME@HUMANATEST.COM","WorkEmail":"NONE","HomePhone":"(985) 758-7716","Mobile":"(343) 433-4234","WorkPhone":"","WorkPhoneExt":"","ServiceResponse":""}},"templateName":"Contact Demographic Update - Medicare","key":"cod"},{"data":{"rsoCRD":{"FirstName":"RICHILEE","MiddleInitial":"E","LastName":"EDNILAO","Gender":"M","DateOfBirth":"10/15/1947","SSN":""}},"templateName":"Critical Demographic Update - Medicare","key":"crd"},{"data":{"rsoMAU":{"PersonSpeakingWith":"4232323243234","RelationToMember":"34342344234","PermanentResidentialPhoneNumber":"(342) 432-4323","WhatAddressMemberCallingToUpdate":"residential","PermanentResidentialAddress":"9022 Shelbyville Road Apt 66","PermanentResidentialCityName":"Louisville","PermanentResidentialStateCode":"KY","PermanentResidentialZipCode":"40222","PermanentResidentialCountyName":"Jefferson","PermanentResidentialStartDate":"2021-03-11","DoesMemberHaveMailAddressDiffFromPermAddress":"No","MailingAddress":"9022 Shelbyville Road Apt 66","MailingCityName":"Louisville","MailingStateCode":"KY","MailingZipCode":"40222","MailingCounty":"Jefferson","DoesMailMemberHaveTemporaryAddress":"No","TemporaryAddress":"","TemporaryCityName":"","TemporaryStateCode":"","TemporaryZipCode":"","TemporaryCountyName":"","TemporaryOSAStartDate":"","TemporaryOSAEndDate":""}},"templateName":"Address Update - Medicare","key":"mau"}]}';
                //String templateData = '{"templateBase":[{"data":{"rsoMAU":{"PersonSpeakingWith":"MARIE RODGERS","RelationToMember":"MEMBER","PermanentResidentialPhoneNumber":"(876) 756-9769","WhatAddressMemberCallingToUpdate":"temporary","PermanentResidentialAddress":"1723 KEENELAND COURT","PermanentResidentialCityName":"MURFREESBORO","PermanentResidentialStateCode":"TN","PermanentResidentialZipCode":"37217","PermanentResidentialCountyName":"RUTHERFORD","PermanentResidentialStartDate":"2020-06-11","DoesMemberWantMailSentToTemporaryAddress":"No","DoesMemberWantMailAddressDiffFromResAddress":"No","MailingAddress":"1723 KEENELAND COURT","MailingCityName":"MURFREESBORO","MailingStateCode":"TN","MailingZipCode":"37217","MailingCounty":"RUTHERFORD","TemporaryAddress":"1201 Christ RD","TemporaryCityName":"MURFREESBORO","TemporaryStateCode":"TN","TemporaryZipCode":"37217","TemporaryCountyName":"RUTHERFORD","TemporaryOSAStartDate":"2020-06-01","TemporaryOSAEndDate":"2020-06-26"}},"templateName":"Address Update - Medicare","key":"mau"}]}';
            	String responseStatusMap = '{"UpdateMemberResponse":{"timestamp":"2021-06-05 08:38:23.171100","result":"Success","responseCode":"S","requestid":"654D5134-88E3-400D-B380-BA8D197C03E9","members":[{"systemsupdated":[],"membercriterion":[{"updatestatus":[{"status":"FAILED","requesttype":"BIOGRAPHICS","errormessage":"GENDER ALREADY EXISTS IN CI PLATFORM","errorcode":"04RJ42"},{"status":"SUCCESS","requesttype":"Email","errormessage":"","errorcode":""},{"status":"SUCCESS","requesttype":"Address","errormessage":"","errorcode":""}],"subscribersourcepersonid":"194007161","platform":"CUSTOMERINTERFACE","membersourcepersonid":"194007161"},{"updatestatus":[{"status":"Success","requesttype":"BIOGRAPHICS","errormessage":"GENDER ALREADY EXISTS IN CI PLATFORM","errorcode":"04RJ42"},{"status":"FAILED","requesttype":"Phone","errormessage":"","errorcode":""}],"subscribersourcepersonid":"194007161","platform":"CUSTOMERINTERFACE","membersourcepersonid":"194007161"}],"cdmmsgack":"Message was never transmitted to the broker"}],"errorsource":["KAFKA","CUSTOMERINTERFACE"],"errormessage":null}}';
                string responseStatus = 'SUCCESS';
                String caseNumber = caseObj.Id;
            	String policyId = pmObj.id;
            	String templateName = 'RSO';
            	DemographicUpdateTemplateInsertion_D_HUM.processTemplateDataAfterResponse(caseNumber, templateData, templateName, policyId, true, responseStatusMap, responseStatus);
            Test.stopTest();
        }
    }
    
    /**
    * This method is used to Process Template Data after Response
    *
    * @param     NULL
    * @return    void 
    */
    @istest
    Private Static void  testprocessTemplateDataOnResponseFalse()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testuser)
        {
            Case caseObj = [Select id from Case where DCN__c='12345678' limit 1];
            Policy_Member__c pmObj = [Select id from Policy_Member__c where Name = 'testpolmem11' limit 1];
            //CRMFunctionality_ONOFF_Switch__c onOffObj = [select Name,IsON__c from CRMFunctionality_ONOFF_Switch__c where Name = '1935446' limit 1];
            Test.startTest();
            	String templateData = '{"templateBase":[{"data":{"rsoCOD":{"HomeEmail":"HOME@HUMANATEST.COM","WorkEmail":"NONE","HomePhone":"(985) 758-7716","Mobile":"(343) 433-4234","WorkPhone":"","WorkPhoneExt":"","ServiceResponse":""}},"templateName":"Contact Demographic Update - Medicare","key":"cod"},{"data":{"rsoCRD":{"FirstName":"RICHILEE","MiddleInitial":"E","LastName":"EDNILAO","Gender":"M","DateOfBirth":"10/15/1947","SSN":""}},"templateName":"Critical Demographic Update - Medicare","key":"crd"},{"data":{"rsoMAU":{"PersonSpeakingWith":"4232323243234","RelationToMember":"34342344234","PermanentResidentialPhoneNumber":"(342) 432-4323","WhatAddressMemberCallingToUpdate":"residential","PermanentResidentialAddress":"9022 Shelbyville Road Apt 66","PermanentResidentialCityName":"Louisville","PermanentResidentialStateCode":"KY","PermanentResidentialZipCode":"40222","PermanentResidentialCountyName":"Jefferson","PermanentResidentialStartDate":"2021-03-11","DoesMemberHaveMailAddressDiffFromPermAddress":"No","MailingAddress":"9022 Shelbyville Road Apt 66","MailingCityName":"Louisville","MailingStateCode":"KY","MailingZipCode":"40222","MailingCounty":"Jefferson","DoesMailMemberHaveTemporaryAddress":"No","TemporaryAddress":"","TemporaryCityName":"","TemporaryStateCode":"","TemporaryZipCode":"","TemporaryCountyName":"","TemporaryOSAStartDate":"","TemporaryOSAEndDate":""}},"templateName":"Address Update - Medicare","key":"mau"}]}';
            	String responseStatusMap = '{"UpdateMemberResponse":{"timestamp":"2021-06-05 08:38:23.171100","result":"Success","responseCode":"S","requestid":"654D5134-88E3-400D-B380-BA8D197C03E9","members":[{"systemsupdated":[],"membercriterion":[{"updatestatus":[{"status":"FAILED","requesttype":"BIOGRAPHICS","errormessage":"GENDER ALREADY EXISTS IN CI PLATFORM","errorcode":"04RJ42"},{"status":"SUCCESS","requesttype":"Email","errormessage":"","errorcode":""}],"subscribersourcepersonid":"194007161","platform":"CUSTOMERINTERFACE","membersourcepersonid":"194007161"},{"updatestatus":[{"status":"Success","requesttype":"BIOGRAPHICS","errormessage":"GENDER ALREADY EXISTS IN CI PLATFORM","errorcode":"04RJ42"},{"status":"FAILED","requesttype":"Phone","errormessage":"","errorcode":""}],"subscribersourcepersonid":"194007161","platform":"CUSTOMERINTERFACE","membersourcepersonid":"194007161"}],"cdmmsgack":"Message was never transmitted to the broker"}],"errorsource":["KAFKA","CUSTOMERINTERFACE"],"errormessage":null}}';
                string responseStatus = 'SUCCESS';
                String caseNumber = caseObj.Id;
            	String policyId = pmObj.id;
            	String templateName = 'RSO';
            	DemographicUpdateTemplateInsertion_D_HUM.processTemplateDataAfterResponse(caseNumber, templateData, templateName, policyId, false, responseStatusMap, responseStatus);
            Test.stopTest();
        }
    } 
}