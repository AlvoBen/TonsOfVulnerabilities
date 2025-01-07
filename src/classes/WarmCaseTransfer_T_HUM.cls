/*******************************************************************************************************************************
Apex Class Name : WarmCaseTransfer_T_HUM
Version         : 1.0
Created On      : 01/31/2017
Function        : Test class for Batch class WarmCaseTransfer_B_HUM

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*	Vamsi Kondragunta			59634              01/31/2017				Created;To correct data of issue; REQ - 296903
*	Pradeepkumar Dani			99570			   06/08/2017				Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
*	Pradeepkumar Dani			100996 			   06/13/2017				Classification__c.Classification_Type__c & Etl_Idftr__c Clean Up
*   Pradeepkumar Dani			102268			   06/16/2017				Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
*******************************************************************************************************************************/
@isTest
public class WarmCaseTransfer_T_HUM {
    /**
    * Returns user
    * <p>
    * creating user to include changes for the user 'CustomerService Build'
    *     * 
    * @param  String  userName
    * @param  String profileName
    * @param  String fname
    * @param  String lname
    * @return  user
    */  
    
    public static User createTestUser(String userName, String profileName, String fname, String lname )
    {
        Id profileId;
        try{
            profileId = [SELECT Id FROM Profile WHERE  name =: profileName LIMIT 1 ].Id;
        }catch (Exception ex) {
            System.assert( false, profileName + ' profile does not exist in the system');
        }
        User testUser = new User();
        testUser.FirstName = fname;
        testUser.LastName = lname;
        testUser.Alias = (userName.length() > 8 ) ?  userName.substring(0 , 8) : userName;
        testUser.Email = UserName+'@test.com';
        testUser.Username = UserName+UserName+'@humcrmstest.com';
        testUser.ProfileId = profileId;
        testUser.CommunityNickname = 'a'+UserName;
        testUser.TimeZoneSidKey = 'America/New_York';
        testUser.LocaleSidKey = 'en_US';
        testUser.EmailEncodingKey = 'ISO-8859-1';
        testUser.LanguageLocaleKey = 'en_US';
        testUser.IsActive = true;
        return testUser;
    }
    /*
    * Method Name : UnitTest
    * Description : This method creates test data for test class
    * Return type : None
    * Parameter   : None
    */
    Static Testmethod void UnitTest(){
        User oUser = createTestUser('customerservicebuild','Deployment', 'CustomerService', 'Build');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        
        insert new HUMConstants__c( Name = 'HUMClosed' , StringValue__c = 'Closed');
        insert new HUMConstants__c( Name = 'HUMInProgress' , StringValue__c = 'In Progress');
        WarmCaseTransfer_B_HUM job = new WarmCaseTransfer_B_HUM();
        
        List<RecordType> lstRecordTypeCaseIds = [SELECT Id  FROM RecordType WHERE Name = 'Member Case' LIMIT 1 ];
        id idMemberCaseRecordType = lstRecordTypeCaseIds[0].Id;
        
        List<RecordType> lstRecordTypeMemberIds = [SELECT Id  FROM RecordType WHERE Name = 'Member' LIMIT 1 ];
        id idMemberAccountRecordType = lstRecordTypeMemberIds[0].Id;
        
        Group oGroup = new Group(Type='Regular', Name='PG Cincinati CRU1');
        insert oGroup;
        system.assertEquals(oGroup.Type, 'Regular', 'Value not matched');
        
        Account oAccount = new Account(FirstName='Test' , LastName='Account1', recordtypeid=idMemberAccountRecordType);
        insert oAccount;
        system.assertEquals(oAccount.FirstName,'Test','Value not matched');
        
        Classification__c classificationRec = new Classification__c();
        classificationRec.Name = 'Enrollment/Effective Date Correction';
        classificationRec.Is_Active__c = true;
        insert classificationRec;
        system.assertEquals(classificationRec.Name, 'Enrollment/Effective Date Correction', 'Value not matched');
        
        Intent__c intentRec = new Intent__c();
        intentRec.Name = 'Rewards';
        intentRec.Is_Active__c = true;
        insert intentRec;
        system.assertEquals(intentRec.Name , 'Rewards','Value not matched');
        
        List<Classification_Type__c> ctList= new list<Classification_Type__c>();
		Classification_Type__c ctRec = new Classification_Type__c();
		ctRec.name = 'Claims (RSO)';
		ctRec.Is_Active__c=true;
		ctList.add(ctRec);	        
		insert ctList;
		system.assertEquals(ctList.size(),1);
		
		
		List<CTCI_Junction__c> ctciList = new list<CTCI_Junction__c>();
		CTCI_Junction__c ctciRec = new CTCI_Junction__c();
		ctciRec.Classification_Type__c = ctRec.id;
		ctciRec.Classification__c = classificationRec.Id;
		ctciRec.Intent__c = intentRec.id;
		ctciRec.IsActive__c = true;
		ctciList.add(ctciRec);
		insert ctciList;
		system.assertEquals(ctciList.size(),1);
        
        Case oCase1 = new Case(AccountId=oAccount.Id, Interacting_With__c=oAccount.Id, Due_Date__c=Date.Today(),Follow_Up_Due_Date__c = Date.Today(),
                               Case_Owner__c = 'Cincinati CRU1', Work_Queue_View_Name__c='other', Owner_Queue__c='Cincinati CRU1', status='Closed', 
                               Origin = 'Inbound Call', Type = 'Customer Inquiry', Interacting_With_TYpe__c = 'Member',
                               Interacting_With_Name__c = 'TestAccount1', Interacting_About_Type__c = 'Member', 
                               Classification_Type__c = 'Calls', Classification_Id__c = classificationRec.Id,
                               CTCI_List__c = ctciRec.Id, G_A_Rights_Given__c = 'No', Complaint__c = 'Yes',
                               Complaint_Reason__c = 'Customer Service Quality', Behavioral_Health__c = 'None');
        insert oCase1;  
        
        System.runAs(oUser){
 
            oCase1.status='In Progress';
            update oCase1;
            //execute batch
            Database.ExecuteBatch(new WarmCaseTransfer_B_HUM ());
            
            WarmCaseTransfer_B_HUM obj1 = new WarmCaseTransfer_B_HUM();
            List<WarmCaseTransfer_B_HUM.CaseHisoryWrapper> lstCh = new List<WarmCaseTransfer_B_HUM.CaseHisoryWrapper>();
            lstCh.add(new WarmCaseTransfer_B_HUM.CaseHisoryWrapper(oCase1.Id, 'Closed', 'In Progress'));
            obj1.executeWrapper(lstCh);
            obj1.execute(null,new List<CaseHistory>());
        }
    }
       
}