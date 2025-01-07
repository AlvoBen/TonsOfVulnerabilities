/*******************************************************************************************************************************
Apex Class Name : EmailTaskCallBackRemainder_B_HUM
Version         : 1.0
Created On      : 10/25/2021
Test Class      : EmailTaskCallBackRemainder_T_HUM

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vinay Lingegowda                                                 10/25/2021                 USER STORY 2769842 T1PRJ0001827 - IR 5050 - Send Task Call Back reminder emails to supervisors (RF)
*******************************************************************************************************************************/

public with sharing class EmailTaskCallBackRemainder_B_HUM Implements Database.Batchable <sObject>, Database.Stateful{
    // list of user id from console
    private Set<Id> setId;
    public static final string SUPERVISIORPROFILE = 'Customer Care Supervisor';
    public  EmailTaskCallBackRemainder_H_HUM oHelper = new EmailTaskCallBackRemainder_H_HUM();
    public Map<String, List<EmailTaskCallBackRemainder_H_HUM.EmailTaskCallBackWrapper>> mapSummaryTaskCallBackData = new Map<String, List<EmailTaskCallBackRemainder_H_HUM.EmailTaskCallBackWrapper>>();
    public List<String> lstAllTaskData = new List<String>();
    public List<String> lstTodayTaskData = new List<String>();
    public Set<String> setSupervisiorEmail = new Set<String>();
    
    public EmailTaskCallBackRemainder_B_HUM() {
        oHelper = new EmailTaskCallBackRemainder_H_HUM();
    }
    
    public EmailTaskCallBackRemainder_B_HUM(Set<Id> setId) {
        this();
        this.setId = setId;
    }

    /**
     * start batch to get all Task
     */
    public Database.QueryLocator start(Database.BatchableContext bc){
        return Database.getQueryLocator(oHelper.getTaskRecords(setId));
    }
    
    /**
     * process Task and User and create custom data for creating attachment.
     */
    public void execute(Database.BatchableContext bc, List<Task> scopeTask){
        Map<Id,User> mapOfSupDetails = oHelper.getSupervisiorDetails(scopeTask);

        for(task oTask : scopeTask){
            if(mapOfSupDetails.containsKey(oTask.Ownerid)){
                user oUsr = mapOfSupDetails.get(oTask.Ownerid);
                if(oUsr.Email.endsWith('@humana.com')){
                    String str = oUsr.profile.name == SUPERVISIORPROFILE ? oUsr.Email : oUsr.Supervisor_Email_Address__c;
                    setSupervisiorEmail.add(str);
                }
                String sData = oHelper.processTaskDataforCSV(oTask, oUsr);
                if(oTask.ActivityDate == (System.Today() + 1)){
                    lstTodayTaskData.add(sData);
                }
                lstAllTaskData.add(sData);
                mapSummaryTaskCallBackData = oHelper.processSummaryTaskCallBack(oTask, oUsr, mapSummaryTaskCallBackData);
            }
        }
    }
    
    /**
     * Send Email with attachment to respective Supervisiors.
     */
    public void finish(Database.BatchableContext bc){
        List<Messaging.EmailFileAttachment> emailFileAttachment = new List<Messaging.EmailFileAttachment>();
        if(!lstAllTaskData.isEmpty()){
            emailFileAttachment.add(oHelper.createAttachmentFile('Callback_Tasks_Report', lstAllTaskData));
        }
        if(!lstTodayTaskData.isEmpty()){
            emailFileAttachment.add(oHelper.createAttachmentFile('Callback_Tasks_due_on_'+(System.Today() + 1).format() ,lstTodayTaskData));
        }
        if(!emailFileAttachment.isEmpty() && !mapSummaryTaskCallBackData.isEmpty() && !setSupervisiorEmail.isEmpty()){
            String htmlTable = oHelper.createSummaryTable(mapSummaryTaskCallBackData);
            oHelper.sendSummaryDetails(htmlTable, emailFileAttachment, setSupervisiorEmail);
        }
    }
}