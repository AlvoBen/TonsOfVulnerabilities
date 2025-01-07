/**********************************************************************************************************************
Apex Class   : LoggingTestDataSetup_T_HUM
Description  : Utility class for creating test data that can be used in Test Classes across the Application.
               This class is made public since it is referenced by other classes.
Created By   : Santhi Mandava
Created On   : 03/14/2016

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer             Code Review            Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Santhi Mandava        24069                  03/14/2016              Original version
* Santhi Mandava        24308                  04/05/2016              Added line number 115
* Santhi Mandava        25231                  04/19/2016              Modified line number 114
* Avinash Choubey       33332 			       07/20/2016              Chnages made as part of REQ- 270351* Avinash Choubey 
* Pradeepkumar Dani		99570				   06/08/2017			   Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Harika Devi Kalidindi 112000                 07/20/2017              REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
***********************************************************************************************************************/
@isTest
public class LoggingTestDataSetup_T_HUM
{
   /**
    * Returns void
    * <p>
    * Method to setup Testdata 
    *
    * @return  Void
    * @see
    */
    @testSetup public static void createTestData()
    {
        List<HUMConstants__c> lstConstanst = new List<HUMConstants__c>();

        lstConstanst.Add(new HUMConstants__c(Name='HUMLISTSIZEONE',IntegerValue__c=1,StringValue__c='0'));
        lstConstanst.Add(new HUMConstants__c(Name='HUMConstantHundered',IntegerValue__c=100,StringValue__c='0'));
        
        insert lstConstanst;
        System.assert(lstConstanst[0].Name == 'HUMLISTSIZEONE' );
        
        Id grpRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.name = 'test';
        acc.Source_Platform_Code__c ='EM';
        acc.Source_Customer_Key__c = 'XY';
        acc.Enterprise_ID__c = '123456';
        acc.Recordtypeid = grpRecordTypeId ;
        insert acc;
        system.assertNotEquals(Null,acc.id);
        system.assertequals(grpRecordTypeId ,acc.Recordtypeid);
        
        Policy__c policy = new Policy__c();
        policy.Platform__c = 'LV';
        policy.Policy_Group_Number__c='11111';
        policy.Group_Name__c = acc.Id;
        insert policy ;
        system.assertequals('LV',policy.Platform__c );
        
        Policy_Member__c objPolMemeber = new Policy_Member__c();
        
        Classification__c ClassificationValue = new Classification__c();
        ClassificationValue.name = 'Claims';
        insert ClassificationValue;
        system.assertequals('Claims',ClassificationValue.name);
        
        list <intent__c> listintentrec = new list<intent__c>();
        listintentrec.add(new Intent__c(name = 'Activities'));
        listintentrec.add(new Intent__c(name = 'Billing'));
        listintentrec.add(new Intent__c(name = 'Filling'));
        insert listintentrec;
        system.assertequals('Activities',listintentrec[0].name);
        
        objPolMemeber.Policy__c = policy.Id;
        objPolMemeber.Source_Coverage_ID__c = '12345';
        objPolMemeber.Member__c = acc.Id; 
        insert objPolMemeber;
        system.assertequals('12345',objPolMemeber.Source_Coverage_ID__c );
        
        List<Case> lstCases = new List<Case>();
        lstCases.Add(new Case(AccountId=acc.Id, Intent_Id__c = listintentrec[0].Id , Classification_Id__c =ClassificationValue.Id, Policy_Member__c= objPolMemeber.Id ,
                         Interacting_With_Type__c = 'Group', Interacting_With__c=acc.Id, Due_Date__c=Date.Today(),
                         Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU',
                         Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
        
        lstCases.Add(new Case(AccountId=acc.Id, Intent_Id__c = listintentrec[1].Id, Classification_Id__c =ClassificationValue.Id, Policy_Member__c= objPolMemeber.Id,
                         Interacting_With_Type__c = 'Group', Interacting_With__c=acc.Id, Due_Date__c=Date.Today(),
                         Case_Owner__c=UserInfo.getUserName(), Owner_Queue__c='Cincinnati CRU', 
                         Service_Center__c ='Cincinnati', Department__c='CRU', status='In Progress'));
        
        lstCases.Add(new Case(AccountId=acc.Id, Intent_Id__c = listintentrec[2].Id, Classification_Id__c =ClassificationValue.Id, Policy_Member__c= objPolMemeber.Id ,
                          Interacting_With_Type__c = 'Group',Complaint__c='No',G_A_Rights_Given__c='No', Due_Date__c=Date.Today(), 
                          Case_Owner__c=UserInfo.getUserName(),
                          Owner_Queue__c='Cincinnati CRU', Service_Center__c ='Cincinnati',Department__c='CRU', status='Closed'));
        insert lstCases;
        
        Interaction__c oInteraction = new Interaction__c(Caller_Name__c = 'Test', Interaction_Origin__c = 'Inbound Call'); 
        oInteraction.Interacting_With__c = acc.id;
        oInteraction.Interacting_With_type__c = 'Group' ;
        oInteraction.Caller_Name__c = '111';
        insert oInteraction;
        System.assertEquals(oInteraction.Caller_Name__c, '111');
    
        Interaction_Member__c oIntMem = new Interaction_Member__c();
        oIntMem.Interaction__c = oInteraction.id;
        oIntMem.Interacting_About__c = acc.id;
        oIntMem.Interacting_About_Type__c = 'Group';
        insert oIntMem; 
        System.assertEquals(oIntMem.Interacting_About_Type__c, 'Group');
        
        HUMCaseInteractionAboutType__c oCaseInterAbotType = new HUMCaseInteractionAboutType__c();
        oCaseInterAbotType.Name = 'Group';
        oCaseInterAbotType.CaseRecordType__c = 'Group';
        oCaseInterAbotType.CaseInteraction_About_Type__c = 'Group';
        insert oCaseInterAbotType;
        System.assertEquals(oCaseInterAbotType.CaseInteraction_About_Type__c, 'Group');
    }
    
    /**
    * Returns attachment log record
    * <p>
    * This method is used to create attachment log records from test class
    * @param sCaseID ID
    * @return  Attachment_log__c 
    * @see
    */ 
    public static Attachment_log__c createAttachmentLog(Id sCaseId)
    {
        Attachment_log__c attachLog = New Attachment_Log__c(
                Attachment_Description__c ='Sample Description',
                Attachment_Key__c ='Sample Key',
                Attachment_Type__c ='Auth Summary',
                Attachment_Sub_type__c = 'Auth:093028246',
                Case__c = sCaseId,
                Source_System__c = 'CRM',
                Created_By__c = UserInfo.getName());
            
            insert attachLog;
            System.assertEquals(attachLog.Attachment_Key__c, 'Sample Key');
            
            string screenshotData = 'Sample Screenshot data Sample Screenshot data Sample Screenshot data';
            Blob objblob = Blob.valueOf(screenshotData);
            Attachment attachRec = new Attachment(
                Body = objblob ,
                ContentType = 'data:image/png;base64',
                Description = '',
                Name = 'TestAttachment',
                OwnerId = UserInfo.getUserId(),
                parentId = attachLog.Id);
            
            insert attachRec;
            System.assertEquals(attachRec.Name, 'TestAttachment');
            return attachLog ;
    }
    
    /**
    * Returns void
    * <p>
    * This method is used to create attachment records from test class.
    * @param sAttachLogId ID
    * @return  void 
    * @see
    */ 
    public static Void createAttachment(Id sAttachLogId)
    {
            string screenshotData = 'Sample Screenshot data Sample Screenshot data Sample Screenshot data';
            Blob objblob = Blob.valueOf(screenshotData);
            Attachment attachRec = new Attachment(
                Body = objblob ,
                ContentType = 'data:image/png;base64',
                Description = '',
                Name = 'TestAttachment',
                OwnerId = UserInfo.getUserId(),
                parentId = sAttachLogId);
            
            insert attachRec;
           System.assertEquals(attachRec.Name, 'TestAttachment');
    }
}