@isTest
private class EagleEyes_RecordCleanUp_TEST{
    
    @testSetup
    static void createTestRecords() {
        
        //Generate 100 Test Records
        
        List<FScalePerf__EventMonitoringRecord__c> dummyList = new List<FScalePerf__EventMonitoringRecord__c>();
        
        // include current deletion variable
        
        Eagle_Eyes_Settings__mdt ees = Eagle_Eyes_Settings__mdt.getInstance('Record_Clean_Up');
        Integer deletionInterval = Integer.valueOf(ees.Record_Clean_Up_Days__c);
        
        // insert 100 records
        
        for (Integer i=0;i<100;i++) {
            dummyList.add(new FScalePerf__EventMonitoringRecord__c(name='dummyPerfRecord '+i, FscalePerf__user_ID__c=('0000000' + i), FScalePerf__ORGANIZATION_ID__c=UserInfo.getOrganizationId(), FScalePerf__EVENT_TYPE__c='apexExecution', FScalePerf__TIMESTAMP_DERIVED__c=system.today().addDays(-deletionInterval -1)));
        }
        insert dummyList;
        
        //insert one entire record that falls outside of the interval.
        
        dummyList.add(new FScalePerf__EventMonitoringRecord__c(name='dummyPerfRecord101', FscalePerf__user_ID__c=('0000000101'), FScalePerf__ORGANIZATION_ID__c=UserInfo.getOrganizationId(), FScalePerf__EVENT_TYPE__c='apexExecution', FScalePerf__TIMESTAMP_DERIVED__c=system.today().addDays(-deletionInterval +1)));
        upsert dummyList;
        
    }
    
    
    //Run method deleting all records older than 30 days
    
    @isTest
    Static void confirmRecordDeletion(){
        Test.startTest();
        
        //declare mdt/vars within test scope
        
        Eagle_Eyes_Settings__mdt ees = Eagle_Eyes_Settings__mdt.getInstance('Record_Clean_Up');
        Integer deletionInterval = Integer.valueOf(ees.Record_Clean_Up_Days__c);
        Date deletionIntervalActual = System.today().addDays(-deletionInterval);
        
        //Declare and run class
        
        EagleEyes_RecordCleanUp obj = new EagleEyes_RecordCleanUp();
        DataBase.executeBatch(obj);
        
        Test.stopTest();
        
        //Assert list is correct
        //Assert no records exist older than deletion interval days
        System.assertEquals(0, [SELECT COUNT() FROM FScalePerf__EventMonitoringRecord__c WHERE FScalePerf__TIMESTAMP_DERIVED__c>:deletionIntervalActual AND name!='dummyPerfRecord101']);
        System.assertEquals(1, [SELECT COUNT() FROM FScalePerf__EventMonitoringRecord__c WHERE Name='dummyPerfRecord101']);  
    }
    
    
    static testMethod void testSendNotification() {
        EagleEyes_RecordCleanUp obj = new EagleEyes_RecordCleanUp();
        //assert before
        Test.startTest();
        Integer expectedLimits = 0;
        Integer actualLimits = Limits.getEmailInvocations();
        System.assertEquals(expectedLimits, actualLimits, 'wrong limits before');
        
        //act
        obj.sendNotification();
        
        //assert after
        expectedLimits = 1;
        actualLimits = Limits.getEmailInvocations();
        System.assertEquals(expectedLimits, actualLimits, 'wrong limits after');
        
    }
}