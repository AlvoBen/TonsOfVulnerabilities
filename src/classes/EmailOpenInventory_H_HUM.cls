/*******************************************************************************************************************************
Apex Class Name : EmailOpenInventory_H_HUM
Version         : 1.0
Created On      : 04/08/2021
Function        : Helper Class for batch EmailOpenInventory_B_HUM
Test Class      : EmailOpenInventory_T_HUM 

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Subhamay Mandal                                                   04/08/2021                 Initial version
*******************************************************************************************************************************/
public with sharing class EmailOpenInventory_H_HUM {
    private Map<String, OpenCasesTasks> mapCasesTasks = null;
    private Boolean bBatchON = false;
    private Boolean bShowClickHere = false;
    private Integer iBatchQueryLimit = 0;
    private Integer iQueryLimit = 0;
    private Integer iQueryAgeLimit = 0;
    private Integer iEmailDisplayLimit = 0;
    private String sEmailDateFormat = 'MM/dd/yyyy';
    private String sOrgWideEmailAddressId = '';
    private String[] arCaseStatus = null;
    private String[] arTaskStatus = null;
    private String sProfiles = '(\'Customer Care Specialist\')';
    private String sPermissionSet = '(\'CRM_RF_LouisvilleUser_Access\')';
    private String sTimeZone = 'America/New_York';

    /**
     * constructor for this helper class
     */
    public EmailOpenInventory_H_HUM() {
        loadSetUp();
    }

    /**
     * Load initial load
     */
    private void loadSetUp() {
        EmailBatchCS_HUM__c csEmailBatch = EmailBatchCS_HUM__c.getOrgDefaults();
        if(csEmailBatch != null) {
            bBatchON = csEmailBatch.isBatchON__c;
            bShowClickHere = csEmailBatch.isShowClickHere__c;
            iBatchQueryLimit = (Integer)csEmailBatch.BatchQueryLimit__c;
            iQueryLimit = (Integer)csEmailBatch.Query_Limit__c;
            iQueryAgeLimit = (Integer)csEmailBatch.Query_Age_Limit__c;
            iEmailDisplayLimit = (Integer)csEmailBatch.Email_Display_Count__c;
            sOrgWideEmailAddressId = EmailOpenInventory_D_HUM.getOrgWideEmailAddress(csEmailBatch.No_Reply_Address__c);
            if(String.isNotBlank(csEmailBatch.Email_Date_Format__c)) {
                sEmailDateFormat = csEmailBatch.Email_Date_Format__c;
            }
            if(String.isNotBlank(csEmailBatch.Query_Case_Statuses__c)) {
                arCaseStatus = csEmailBatch.Query_Case_Statuses__c.split(','); 
            }
            if(String.isNotBlank(csEmailBatch.Query_Task_Statuses__c)) {
                arTaskStatus = csEmailBatch.Query_Task_Statuses__c.split(','); 
            }
            if(String.isNotBlank(csEmailBatch.Profiles__c)) {
                this.sProfiles = '(';
                for (String sName : csEmailBatch.Profiles__c.split(',')) {
                    this.sProfiles += '\''+ sName.trim() +'\',';
                }
                this.sProfiles = this.sProfiles.removeEnd(',') + ') ';
            }
            if(String.isNotBlank(csEmailBatch.Permission_Sets__c)) {
                this.sPermissionSet = '(';
                for (String sName : csEmailBatch.Permission_Sets__c.split(',')) {
                    sPermissionSet += '\''+ sName.trim() +'\',';
                }
                this.sPermissionSet = this.sPermissionSet.removeEnd(',') + ') ';
            }
        }
    }
    /**
     * get query for user list
     * @param NA
     * @return String
     */
    public String getQueryCCA(Set<Id> setId) {
        String sSOQL = 'Select ID FROM User limit 0';
        if(bBatchON) {
            String sUserIds = '(SELECT AssigneeId FROM PermissionSetAssignment WHERE PermissionSet.Name IN '+sPermissionSet+') ';
            if(setId != null && !setId.isEmpty()) {
                sUserIds = '(';
                for(String sId : setId) {
                    sUserIds += '\''+ sId +'\',';
                }
                sUserIds = sUserIds.removeEnd(',') + ') ';
            } 

            sSOQL = 'Select ID from User where isActive = true AND PROFILE.Name IN '+sProfiles+' AND ID in ' + sUserIds;
            sSOQL += 'Order by ID limit '+iBatchQueryLimit;
        }
        return sSOQL;
    }

    /**
     * retrieve open inventor from Case and Task
     * @param List<User>
     * @return void
     */
    public void processOpenInventory(List<User> lstUser) {
        try {
            if(bBatchON) {
                Map<Id, User> mapUser = EmailOpenInventory_D_HUM.queryUserDetails(lstUser);
                mapCasesTasks = new Map<String, OpenCasesTasks>();
                
                processOpenInventoryCase(mapUser.keySet());
                processOpenInventoryTask(mapUser.keySet());
                sendNotificationCCA(mapUser);
            }
        } catch(Exception e) {
            HUMExceptionHelper.logErrors(e,'EmailOpenInventory_H_HUM', 'processOpenInventory');
        }
    }

    /**
     * process open inventory Case
     * @param Set<Id> IdUser
     * @return void 
     */
    private void processOpenInventoryCase(Set<Id> setId) {
        String sRows = '';
        Integer iTotalCount = 0;
        String sOwnerIdTemp = '';

        for(Case oCaseRec: EmailOpenInventory_D_HUM.queryCaseRecords(setId, arCaseStatus, iQueryLimit, iQueryAgeLimit)) {
            String sOwnerId = oCaseRec.OwnerId;
            OpenInventory_DTO_HUM oOpenInventoryWrapper = new OpenInventory_DTO_HUM(oCaseRec);
            if(sOwnerIdTemp == '') {
                sOwnerIdTemp = sOwnerId;
                sRows += oOpenInventoryWrapper.getRow();
            } else if(sOwnerIdTemp.equalsIgnoreCase(sOwnerId)) {
                if(iTotalCount < iEmailDisplayLimit) {
                    sRows += oOpenInventoryWrapper.getRow();
                }                
            } else if(!sOwnerIdTemp.equalsIgnoreCase(sOwnerId)) {                
                mapCasesTasks.put(sOwnerIdTemp, new OpenCasesTasks(iTotalCount, sRows, 0, ''));
                sOwnerIdTemp = sOwnerId; 
                sRows = '';
                iTotalCount = 0;
                sRows += oOpenInventoryWrapper.getRow();
            }
            iTotalCount++;
        }
        if(String.isNotBlank(sRows)) {
            mapCasesTasks.put(sOwnerIdTemp, new OpenCasesTasks(iTotalCount, sRows, 0, ''));
            sOwnerIdTemp = null;
            sRows = '';
            iTotalCount = 0;
        }
    }

    /**
     * process open inventory Task
     * @param Set<Id> IdUser
     * @return void
     */
    private void processOpenInventoryTask(Set<Id> setId) {
        String sRows = '';
        Integer iTotalCount = 0;
        String sOwnerIdTemp = '';

        for(Task oTaskRec: EmailOpenInventory_D_HUM.queryTaskRecords(setId, arTaskStatus, iQueryLimit, iQueryAgeLimit)) {            
            String sOwnerId = oTaskRec.ownerId;
            OpenInventory_DTO_HUM oOpenInventoryWrapper = new OpenInventory_DTO_HUM(oTaskRec);
            if(sOwnerIdTemp == '') {
                sOwnerIdTemp = sOwnerId;
                sRows += oOpenInventoryWrapper.getRow();
            } else if(sOwnerIdTemp.equalsIgnoreCase(sOwnerId)) {
                if(iTotalCount < iEmailDisplayLimit) {
                    sRows += oOpenInventoryWrapper.getRow();
                }
            } else if(!sOwnerIdTemp.equalsIgnoreCase(sOwnerId)) {
                if(mapCasesTasks.containsKey(sOwnerIdTemp)) {
                    OpenCasesTasks oData = mapCasesTasks.get(sOwnerIdTemp);
                    oData.iTotalTask = iTotalCount;
                    oData.sTaskRows = sRows;
                } else {
                    mapCasesTasks.put(sOwnerIdTemp, new OpenCasesTasks(0, '', iTotalCount, sRows));
                }
                sOwnerIdTemp = sOwnerId; 
                sRows = '';
                iTotalCount = 0;
                sRows += oOpenInventoryWrapper.getRow();
            }
            iTotalCount++;
        }

        if(String.isNotBlank(sRows)) {
            if(mapCasesTasks.containsKey(sOwnerIdTemp)) {
                OpenCasesTasks oData = mapCasesTasks.get(sOwnerIdTemp);
                oData.iTotalTask = iTotalCount;
                oData.sTaskRows = sRows;
            } else {
                mapCasesTasks.put(sOwnerIdTemp, new OpenCasesTasks(0, '', iTotalCount, sRows));
            }
            sOwnerIdTemp = null;
            sRows = '';
            iTotalCount = 0;
        }
    }

    /**
     * send email notfction to CCA user with open inventories
     * @param Map<Id, User>
     * @return void
     */
    private void sendNotificationCCA(Map<Id, User> mapUser) {
        List<Messaging.singleEmailMessage> lstSendMails = new List<Messaging.singleEmailMessage>();
        String dToday = Datetime.now().format(sEmailDateFormat, sTimeZone); 
        String sMessageBody = 'You have <b><1></b> open cases and <b><2></b> open tasks assigned for resolution as of <b><3></b>. Here are the top <b><4></b> aged open inventory. To view the complete list of inventory in CRM Service to take action, ';
        
        for(String sOwnerId : mapCasesTasks.keySet()) {
            User oUserTo = mapUser.get(sOwnerid);
            OpenCasesTasks oRecord = mapCasesTasks.get(sOwnerId);
            String sMessageTemp = sMessageBody;

            sMessageTemp = sMessageTemp.replaceFirst('<1>', ''+oRecord.iTotalCase);
            sMessageTemp = sMessageTemp.replaceFirst('<2>', ''+oRecord.iTotalTask);
            sMessageTemp = sMessageTemp.replaceFirst('<3>', ''+dToday);
            sMessageTemp = sMessageTemp.replaceFirst('<4>', ''+iEmailDisplayLimit);
            
            String sAppenedMessage = 'please visit CRM Service.';
            if(bShowClickHere) {
                sAppenedMessage = 'please click <b>[<a href="'+System.Url.getOrgDomainUrl().toExternalForm()+'/console">here</a>]</b>.';
            }
            sMessageTemp += sAppenedMessage;

            Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();
            mail.setOrgWideEmailAddressId(sOrgWideEmailAddressId);
            mail.setTargetObjectId(oUserTo.Id);
            mail.toAddresses = new String[] {oUserTo.email};
            mail.setSubject('INFO: CRM Service - '+oUserTo.Name+'\'s Open Inventory - '+dToday);
            
            String htmlBody = '<html><body>Hi ' +oUserTo.Name+', </br></br>'+ sMessageTemp +'</br></br>';
            htmlBody += '<table border="1" style="border-collapse:collapse;">';
            htmlBody += createNewTable(oRecord.sCaseRows);
            htmlBody += createNewTable(oRecord.sTaskRows);
            htmlBody += '</table></br></br>';
            htmlBody += 'This is a system generated message. Do not reply to this email.</br></br>Regards,</br>CRM Service.</body></html>'; 
            mail.setHtmlBody(htmlBody);
            mail.setSaveAsActivity(false);
            
            lstSendMails.add(mail);            
        }

        if(!lstSendMails.isEmpty()) {
            EmailOpenInventory_D_HUM.manageSendEmail(lstSendMails);
            mapCasesTasks = null;
        } 
    }

    /**
     * create a new tale based row data
     * @param String rows
     * @return String
     */
    private String createNewTable(String sRows) {
        String sHtmlBody = '';
        if(String.isNotBlank(sRows)) {
            sHtmlBody += '<tr style="padding: 5px;" bgcolor="#cccccc"><th>Case/Task ID</th><th>Item Age</th><th>Work Queue</th><th>Queue View</th><th>Last Modified Date</th><th>Followup Due Date</th><th>Pending With</th></tr>';
            sHtmlBody += sRows;            
        }
        return sHtmlBody;
    }

    /**
     * wrapper class to store open cases and tasks
     */
    class OpenInventory_DTO_HUM {
        public String sCaseTaskNumber { get; set; }
        public Decimal dCaseTaskAge { get; set; }
        public String sWorkQueue {get;set;}
        public String sQueueView {get; set;}
        public String sLastModifiedDate {get;set;}
        public String sFollowUpDueDate {get;set;}
        public String sCasePendingWith {get;set;}

        private String sCreateRow = '';
        
        public OpenInventory_DTO_HUM(sObject oRecord) {
            this.sCasePendingWith =  '';
            this.sWorkQueue = formatString((String)oRecord.get('Owner_Queue__c'));
            this.sQueueView = formatString((String)oRecord.get('Work_Queue_View_Name__c'));
            this.sLastModifiedDate = formatDate((Datetime)oRecord.get('LastModifiedDate'));
            if(Schema.Case.getSObjectType() == oRecord.getSObjectType()) {
                this.sCaseTaskNumber = (String)oRecord.get('CaseNumber');
                this.dCaseTaskAge = (Decimal)oRecord.get('Case_Age__c');
                this.sFollowUpDueDate = formatDate((Date)oRecord.get('Follow_up_Due_Date__c'));
                this.sCasePendingWith = formatString((String)oRecord.get('Response_Status__c'));
            } else {
                this.sCaseTaskNumber = (String)oRecord.get('Task_Number__c');
                this.dCaseTaskAge = (Decimal)oRecord.get('task_Age__c');
                this.sFollowUpDueDate = formatDate((Date)oRecord.get('ActivityDate'));
            }
            
            this.sCreateRow ='<tr style="padding: 5px;"><td>'+  this.sCaseTaskNumber +'</td><td>'+ this.dCaseTaskAge +'</td><td>'+ this.sWorkQueue +'</td><td>'+ this.sQueueView +'</td><td>'+ this.sLastModifiedDate +'</td><td>'+ this.sFollowUpDueDate +'</td><td>'+ this.sCasePendingWith +'</td></tr>';
        }

        /**
         * get row data for a record
         * @param NA
         * @return String 
         */
        public String getRow() {
            return sCreateRow;
        }

        /**
         * format date value else empty for null value
         * @param Datetime
         * @return String
         */
        private String formatDate(Date dtValue) {
            String sDate = '';
            if(dtValue != null) {
                sDate = dtValue.format();
            }
            return sDate;
        }
        /**
         * format date value else empty for null value
         * @param Datetime
         * @return String
         */
        private String formatDate(Datetime dtValue) {
            String sDate = '';
            if(dtValue != null) {
                sDate = dtValue.format('MM/dd/yyyy');
            }
            return sDate;
        }

        /**
         * format String, relace null to empty value
         * @param String
         * @return String
         */
        private String formatString(String sValue) {
            String sSource = '';
            if(String.isNotBlank(sValue)) {
                sSource = sValue;
            }
            return sSource;
        }
    }

    /**
     * Class to store all data
     */
    class OpenCasesTasks {
        public Integer iTotalCase{get; set;}
        public String sCaseRows{get; set;} 

        public Integer iTotalTask{get; set;}
        public String sTaskRows{get; set;}
        
        OpenCasesTasks(Integer iTotalCase, String sCaseRows, Integer iTotalTask, String sTaskRows) {
            this.iTotalCase = iTotalCase;
            this.sCaseRows = sCaseRows;
            this.iTotalTask = iTotalTask;
            this.sTaskRows = sTaskRows;
        }
    }

    /**
     * custom exception for EmailOpenInventory
     */
    public class EmailOICustomException extends Exception  { }
}