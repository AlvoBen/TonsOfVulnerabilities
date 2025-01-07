/*******************************************************************************************************************************
Apex Class Name : EmailTaskCallBackRemainder_H_HUM
Version         : 1.0
Created On      : 10/25/2021

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vinay Lingegowda                                                  10/25/2021                 USER STORY 2769842 T1PRJ0001827 - IR 5050 - Send Task Call Back reminder emails to supervisors (RF)
*******************************************************************************************************************************/
public with sharing class EmailTaskCallBackRemainder_H_HUM {

    private Date dToday = System.today();
    private map<String,map<String,integer>> mapSupervisior;
    private Boolean bBatchON = false;
    private Boolean bDefaultEmailSwitch = false;
    private Integer iQueryLimit = 0;
    private Integer iRecepientsCount=0;
    private String sOrgWideEmailAddressId = '';
    private String arTaskStatus = '';
    private List<String> sProfiles = new List<String>();
    private String sPermissionSet = '';
    private String sDefaultPOCName='';
    private String sDefaultPOCEmail='';

    /**
     * constructor for this helper class
     */
    public EmailTaskCallBackRemainder_H_HUM() {
        loadSetUp();
    }

    /**
     * Load initial data
     */
    private void loadSetUp() {
        String sProfileID = EmailTaskCallBackRemainder_D_HUM.getProfileID();
        EmailBatchCS_HUM__c csEmailBatch = EmailBatchCS_HUM__c.getInstance(sProfileID);
        if(csEmailBatch != null) {
            bBatchON = csEmailBatch.isBatchON__c;
            bDefaultEmailSwitch = csEmailBatch.isDefaultMailON__c;
            iQueryLimit = (Integer)csEmailBatch.Query_Limit__c;
            iRecepientsCount = (Integer)csEmailBatch.RecipentsCount__c;
            sOrgWideEmailAddressId = EmailTaskCallBackRemainder_D_HUM.getOrgWideEmailAddress(csEmailBatch.No_Reply_Address__c);

            if(String.isNotBlank(csEmailBatch.Query_Task_Statuses__c)) {
                this.arTaskStatus='(';
                for (String sName : csEmailBatch.Query_Task_Statuses__c.split(',')) {
                    this.arTaskStatus += '\''+ sName.trim() +'\',';
                }
                this.arTaskStatus = this.arTaskStatus.removeEnd(',') + ') '; 
            }
            if(String.isNotBlank(csEmailBatch.Profiles__c)) {
                string str = String.ValueOf(csEmailBatch.Profiles__c);
                this.sProfiles = str.split(',');
            }
            if(String.isNotBlank(csEmailBatch.Permission_Sets__c)) {
                this.sPermissionSet = '(';
                for (String sName : csEmailBatch.Permission_Sets__c.split(',')) {
                    sPermissionSet += '\''+ sName.trim() +'\',';
                }
                this.sPermissionSet = this.sPermissionSet.removeEnd(',') + ') ';
            }
            if(String.isNotBlank(csEmailBatch.DefaultContact__c)){
                this.sDefaultPOCName= String.ValueOf(csEmailBatch.DefaultContact__c);
            }
            if(String.isNotBlank(csEmailBatch.DefaultEmailAddress__c)){
                this.sDefaultPOCEmail=String.ValueOf(csEmailBatch.DefaultEmailAddress__c);
            }
        }
    }

    /**
     * get query for Task list
     * @param Set<Id>
     * @return String
     */

    public String getTaskRecords(Set<Id> setId) {
        String sSOQL = 'Select ID FROM Task limit 0';
        if(bBatchON){
            String sPermitedUserIds = '(SELECT AssigneeId FROM PermissionSetAssignment WHERE PermissionSet.Name IN '+sPermissionSet+') ';
            String sTaskIds = null;
            sSOQL ='SELECT Id, Task_Age__c, Task_Number__c, What.name, Owner.Name, Created_By_Queue__c, Owner_Queue__c, ActivityDate, Work_Queue_View_Name__c, RecordType.Name, CreatedBy.Name FROM Task'; 
            sSOQL +=' WHERE Status IN '+arTaskStatus+' AND RecordType.Name =\'Service CRM\' AND OwnerId IN '+sPermitedUserIds+' AND Owner_Queue__c != null AND (ActivityDate <= TOMORROW OR ActivityDate =null)';
            
            if(setId != null && !setId.isEmpty()){
                sTaskIds = '(';
                for(String sId : setId) {
                    sTaskIds += '\''+ sId +'\',';
                }
                sTaskIds = sTaskIds.removeEnd(',') + ') ';
                sSOQL +=' AND ID IN '+sTaskIds;
            }
            sSOQL +=' ORDER BY Owner_Queue__c, ActivityDate Limit '+iQueryLimit;
        }

        return sSOQL;    
    }

	/**
     * get query for User list
     * @param List<Task>
     * @return Map<Id,User>
     */
    public Map<Id,User> getSupervisiorDetails(List<Task> lstTsk){ 
        Map<Id,User> mapSupervisiorDetails = new Map<Id,User>();
        if(bBatchON){
            Set<Id> setSupId = new Set<Id>();
            for(Task oTask :lstTsk){
                setSupId.add(oTask.Ownerid);
            }
            mapSupervisiorDetails = EmailTaskCallBackRemainder_D_HUM.getSupervisiorDetails(setSupId, sProfiles);
        }
        return mapSupervisiorDetails;
    }
    

    /**
     * Combine task and user values in string formate to create CSV data.
     * @param Task
     * @param User
     * @return String
     */
    public String processTaskDataforCSV(Task oTask, User oUser){
        String sManager = oUser.Manager_Name__c == null ? '' : String.ValueOf(oUser.Manager_Name__c);
        String sSupervisor = oUser.Supervisor_Name__c == null ? '' : oUser.Supervisor_Name__c;
        String sOwnerQueue = oTask.Owner_Queue__c == null ? '' : oTask.Owner_Queue__c;
        String sActivityDate = oTask.ActivityDate == null? '': String.valueOf(oTask.ActivityDate);
        String sFormateRow = oTask.Task_Age__c +','+ sManager +','+ sSupervisor +','+ oUser.Network_User_Id__c +','+ oTask.Task_Number__c+','+oTask.What.name +','+ oTask.Owner.Name +','+ oTask.Created_By_Queue__c +','+ sOwnerQueue +','+ sActivityDate+','+ oTask.Work_Queue_View_Name__c+','+oTask.CreatedBy.Name;    
        
        return  sFormateRow; 
    }

    /**
     * Method to construct email body with csv attachments and send the email to supervisiors.
     * @param String 
     * @param List<Messaging.EmailFileAttachment>
     * @param Set<String>
     * @return void
     */
    public void sendSummaryDetails(String htmlTable, List<Messaging.EmailFileAttachment> emailFileAttachment, Set<String> setEmailAddresses){
            List<String> toAddress = new List<String>();
            String sMessageBody = 'Greetings! </br></br> We have changed to an automated email showcasing call back tasks. Attached are 2 reports.<ul><li>Callback Tasks Report.</li><li>Callback Tasks due on '+(dToday+1).format()+'</li></ul>';
            sMessageBody +='<p style="Color:red";>Action needed:</p><ol><li>Open document.</li><li>Add a filter to the top row by:<ul><li>Highlighting the 1st row by clicking on the number 1</li><li>On the far right click on "Sort & Filter"</li><li>Click "Filter"</li><ul><li>As a leader you can filter on anything you\'d like: Manager, Supervisor, Full Name(owner), queue or due date depending on how you want to view the information.</li></ul></ul></li></ol>';
            sMessageBody +='Information: <ol><li>As a leader you have the option to review your teams open cases and task by clicking the "Team Members" on the cases/task tab within CRM.</li><li>The email is ONLY sent to leaders NOT to the team leads</li></ol>';
            sMessageBody +='</br> Questions/Concerns: Reach out to <a href="mailto:'+sDefaultPOCEmail+'">'+sDefaultPOCName+'.</a></br></br>';
            String sSubject = 'INFO: CRM Service - Callback tasks - Action Needed';

            String htmlBody ='<html><body>'+ sMessageBody;
            htmlBody += '<table border="1" style="border-collapse:collapse;">';
            htmlBody += htmlTable;
            htmlBody += '</table></br></br>';
            htmlBody += 'This is a system generated message. Do not reply to this email.</br></br>Regards,</br>CRM Service.</body></html>'; 
            List<Messaging.SingleEmailMessage> lstEmail = new List<Messaging.SingleEmailMessage>();
            Messaging.SingleEmailMessage email = new Messaging.SingleEmailMessage();
            email.setOrgWideEmailAddressId(sOrgWideEmailAddressId);
            email.setSubject(sSubject);
            email.setHtmlBody(htmlBody);
            email.setFileAttachments(emailFileAttachment);
            System.debug('Vny--bDefaultEmailSwitch->'+bDefaultEmailSwitch);
            toAddress.add(sDefaultPOCEmail);
            if(setEmailAddresses.Size() > 0 && !bDefaultEmailSwitch){
                Integer iCount = 1;
                for(String sEmail :setEmailAddresses){
                    toAddress.add(sEmail);
                    iCount= iCount+1;
                    if(iCount == iRecepientsCount){
                        email.setToAddresses(toAddress);
                        lstEmail.add(email);
                        iCount =0;
                        toAddress.clear();
                    }
                }
            }else {
                email.setToAddresses(toAddress);
                lstEmail.add(email);    
            }
            if(!lstEmail.isEmpty()){
                EmailTaskCallBackRemainder_D_HUM.manageSendEmail(lstEmail);
            }
    }

    /**
     * Create CSV file with data.
     * @param String
     * @param List<String>
     * @return Messaging.EmailFileAttachment
     */
    public Messaging.EmailFileAttachment createAttachmentFile(String sAttachmentName, List<String> lstOfTaskForCSV){
        String csvColHeadder = 'Task Age, Manager Name, Supervisor Name, Network User Id, Task Number, CaseNumber, Full Name, Created By Queue, Owner Queue, DueDate, Work Queue View Name, Created By';
        String attachmentName = sAttachmentName+'.csv';
        String csvTaskCallBackData = csvColHeadder +'\n'+ String.join(lstOfTaskForCSV,'\n');

        Messaging.EmailFileAttachment emailFileAttachment = new Messaging.EmailFileAttachment();
        Blob taskCallbackLog = blob.valueOf(csvTaskCallBackData);
        emailFileAttachment.setFileName(attachmentName);
        emailFileAttachment.setBody(taskCallbackLog);

        return emailFileAttachment;
    }

    /**
     * Create content summary table for email body.
     * @param Map<String
     * @param List<EmailTaskCallBackWrapper>>
     * @return String
     */
    public String createSummaryTable(Map<String, List<EmailTaskCallBackWrapper>> mapSummaryTaskCallBack){
        String sHtmlBody = '';
        if(!mapSummaryTaskCallBack.isEmpty()) {
        sHtmlBody += '<tr style="text-align:left; padding: 5px;" bgcolor="#cccccc"><th>Work Queue</th><th>Supervisor</th><th> > 7+ Days Past Due</th><th> < 7 Days Past Due</th><th>Due on '+(dToday+1).format()+'</th><th>No Due Date</th><th>Total Task Count</th></tr>';         
            for(String sOwnerQueueKey : mapSummaryTaskCallBack.keyset()){
                sHtmlBody +='<tr style="padding: 5px;"><td rowspan="'+mapSummaryTaskCallBack.get(sOwnerQueueKey).size()+'" scope="rowgroup">'+sOwnerQueueKey+'</td>';
                for(EmailTaskCallBackWrapper sSupNameKey :mapSummaryTaskCallBack.get(sOwnerQueueKey)){ 
                    String sSevenDaysOverDueCount = (sSupNameKey.iSevenDaysOverDue>0) ? String.valueOf(sSupNameKey.iSevenDaysOverDue) : '';
                    String sOverDue = (sSupNameKey.iOverDue>0) ? String.valueOf(sSupNameKey.iOverDue) : '';
                    String sNoDueDate = (sSupNameKey.iNoDueDate>0) ? String.valueOf(sSupNameKey.iNoDueDate) : '';
                    String sWithinSevenDaysOverDue = (sSupNameKey.iWithinSevenDaysOverDue>0) ? String.valueOf(sSupNameKey.iWithinSevenDaysOverDue) : '';
                    Integer iTotal = (sSupNameKey.iSevenDaysOverDue + sSupNameKey.iOverDue + sSupNameKey.iNoDueDate + sSupNameKey.iWithinSevenDaysOverDue);
                    
                    sHtmlBody +='<td style="padding: 5px;">'+sSupNameKey.sSupervisior+'</td>';
                    sHtmlBody +='<td style="padding: 5px;">'+sSevenDaysOverDueCount+'</td>';
                    sHtmlBody +='<td style="padding: 5px;">'+sWithinSevenDaysOverDue+'</td>';
                    sHtmlBody +='<td style="padding: 5px;">'+sOverDue+'</td>';
                    sHtmlBody +='<td style="padding: 5px;">'+sNoDueDate+'</td>';
                    sHtmlBody +='<td style="padding: 5px;">'+iTotal+'</td></tr>';
                }
            }           
        }
        return sHtmlBody;
    }

    /**
     * method to construct email content and attachment CSV.
     * @param Task
     * @param User 
     * @param Map<String, List<EmailTaskCallBackWrapper>>
     * @return Map<String, List<EmailTaskCallBackWrapper>>
     */
    public Map<String, List<EmailTaskCallBackWrapper>> processSummaryTaskCallBack(Task oTask, User oUser, Map<String, List<EmailTaskCallBackWrapper>> mapSummaryTaskCallBack){
        List<EmailTaskCallBackWrapper> lstTaskDatail = new List<EmailTaskCallBackWrapper>();  

        if(!mapSummaryTaskCallBack.containsKey(oTask.Owner_Queue__c) ){
            EmailTaskCallBackWrapper oWrapper = new EmailTaskCallBackWrapper(oTask, oUser);
            lstTaskDatail.add(oWrapper);
            mapSummaryTaskCallBack.put(oTask.Owner_Queue__c,lstTaskDatail); 
        }else if(mapSummaryTaskCallBack.containsKey(oTask.Owner_Queue__c)){
            List<EmailTaskCallBackWrapper> mapSupervisior = mapSummaryTaskCallBack.get(oTask.Owner_Queue__c);
            boolean bSupervisiorFound= false;
            for(EmailTaskCallBackWrapper sd:mapSupervisior){
                if(sd.sSupervisior == oUser.Supervisor_Name__c || (sd.sSupervisior == 'None' && oUser.Supervisor_Name__c ==null)){
                    if(oTask.ActivityDate == null){
                        sd.iNoDueDate = sd.iNoDueDate + 1;
                    }else if(oTask.ActivityDate == (dToday + 1)){
                        sd.iOverDue = sd.iOverDue + 1;
                    }else if(oTask.ActivityDate < (dToday - 7)){
                        sd.iSevenDaysOverDue = sd.iSevenDaysOverDue + 1;
                    }else if(oTask.ActivityDate >= (dToday - 7) && oTask.ActivityDate <= dToday){
                        sd.iWithinSevenDaysOverDue= sd.iWithinSevenDaysOverDue + 1;
                    }
                    bSupervisiorFound = true;
                }
            }
            
            if(!bSupervisiorFound){
                EmailTaskCallBackWrapper oWrapper= new EmailTaskCallBackWrapper(oTask, oUser);
                mapSupervisior.add(oWrapper);
            }
           
            mapSummaryTaskCallBack.put(oTask.Owner_Queue__c,mapSupervisior);
        }
        return mapSummaryTaskCallBack;
    }

    /**
     * Wrapper class to store summary table data for each workqueue (Supervisior, Over due, no due date, seven days over due and within seven days over view tasks).
     * 
     */
    public class EmailTaskCallBackWrapper {
        public String sSupervisior{get; set;}
        public Integer iSevenDaysOverDue{get; set;}
        public Integer iWithinSevenDaysOverDue{get; set;}
        public Integer iOverDue{get; set;}
        public Integer iNoDueDate{get; set;}

        public EmailTaskCallBackWrapper(Task oTask, User oUser) {
            Date dToday = System.today();
            this.sSupervisior = oUser.Supervisor_Name__c == null ? 'None' : oUser.Supervisor_Name__c;
            this.iNoDueDate = (oTask.ActivityDate == null) ? 1 : 0;
            this.iOverDue= (oTask.ActivityDate ==(dToday+1)) ? 1 : 0;
            this.iSevenDaysOverDue = (oTask.ActivityDate < (dToday - 7)) ? 1 : 0;
            this.iWithinSevenDaysOverDue =(oTask.ActivityDate >= (dToday - 7) && oTask.ActivityDate <= dToday) ? 1 : 0;
        }
    }
}