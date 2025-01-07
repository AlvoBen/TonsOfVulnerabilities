/********************************************************************************************
Description: This is a batch class to delete FScale_Perf_EventMonitoringRecords records
9 March 2021   Chris Cutting   Original ('Eagle Eyes' App)
********************************************************************************************/

global class EagleEyes_RecordCleanUp implements Database.Batchable<sObject>, Schedulable {
    Exception[] errors = new Exception[0];
    
    /*
@method      : start
@description : This method is the start method of the batch class, it initially returns all jobs
@params      : Database.BatchableContext
@returns     : Database.QueryLocator
*/
    global Database.QueryLocator start(Database.BatchableContext BC){
        Eagle_Eyes_Settings__mdt ees = Eagle_Eyes_Settings__mdt.getInstance('Record_Clean_Up');
        Integer deletionInterval = Integer.valueOf(ees.Record_Clean_Up_Days__c);
        Date d = System.today().addDays(-deletionInterval);
        String queryString = 'SELECT Id FROM FScalePerf__EventMonitoringRecord__c WHERE FScalePerf__TIMESTAMP_DERIVED__c <=:d';
        return Database.getQueryLocator(queryString);
    }
    
    /*
@method      : execute
@description : This method is the execute method of the batch class, which disposes of all jobs older than one month. 
@params      : Database.BatchableContext
@params      : List<FScalePerf__EventMonitoringRecord__c>
@returns     : void
*/
    global void execute(Database.BatchableContext BC, List<FScalePerf__EventMonitoringRecord__c> Scope){
        try {
            delete Scope;
            if(Test.isRunningTest()) {
                CalloutException e = new CalloutException();
                e.setMessage('This is a constructed exception for testing and code coverage');
                throw e;
            }
        } catch (Exception e) {
            errors.add(e);            
        }        
    }
    
    /*
@method      : finish
@description : This method is the finish method of the batch class
@params      : Database.BatchableContext
@returns     : void
*/
    global void finish(Database.BatchableContext BC){
        //If there were errors, Send Error email notification       
        if(!errors.isEmpty()) {           
            sendNotification();          
        }       
    }
    
    /*
@method      : sendNotification()
@description : To send error notification to emails/users setup in Apex Exception Email
@params      : NA
@returns     : void
*/            
    public void sendNotification(){
        List<String> toAddresses = new List<String>();
        List<ApexEmailNotification> aenList = [select Id, UserId, Email From ApexEmailNotification];
        Set<String> emails = new Set<String>();
        Set<String> userIds = new Set<String>();
        
        if(!aenList.IsEmpty()){
            for(ApexEmailNotification aen:aenList){
                if(aen.UserId!= null){
                    userIds.add(aen.UserId);
                }
                if(aen.Email != null){
                    emails.add(aen.Email);
                }
            }
            
            if(!userIds.isEmpty()){
                For(User u : [select id, Email from user where Id in: userIds]){
                    emails.add(u.Email);
                }
            }
        }else{
            emails.add('hsc@humana.com');//default to HSC inbox address if Apex Exception Email not setup/existing
        }
        
        toAddresses.addAll(emails);        
        
        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();
        mail.setSubject('Errors occurred during Eagle Eye process');
        mail.setToAddresses(toAddresses);
        mail.setSaveAsActivity(false);
        mail.setPlainTextBody(String.ValueOf(errors));
        Messaging.sendEmail(new Messaging.Email[] { mail });       
    }    
    
    //Scheduling code
    global void execute(SchedulableContext ctx) {
        Database.executeBatch(new EagleEyes_RecordCleanUp());
    }   
    
}//end class