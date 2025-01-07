/*
Class Name       : HUMTaskDetailExtension
Version          : 1.0 
Created On       : JUNE 9th 2014
Function         : Class used for querying task for given task Id in url.
Test Class       : HUMTaskDetailExtensionTest

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Surya kumari                 17093                         6/9/2014                  Original Version
* Avinash Choubey              40794                         09/15/2016                Code added to get parent task from Task heirarchy Object REQ 252450
* Sharan Shanmugam             50226                         11/30/2106                Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                        Case/Task/Interaction/Attachment to Work Queue Setup And Queue View
* Shreya Choodamani            90487                         04/28/2017                REQ#: 311397 Updated the code to mimic the behavior of CCSupervisor for HP Supervisor
* Pallavi Ravishankar          268909                        06/08/2018                REQ 361926 - adding Extension Request to Task Type
* Amar Gadewar                                               04/18/2019                REQ - 396424 - Quality - RCC - Medicare ID field on Task Edit and Task Detail page (RF)
* Santhosh Ganji                                             04/19/2019                REQ - 386935 - Hide Task Edit Button of Non-MED Cases for HP Users 
* Santhi Mandava                                             04/15/2019                Replaced "Member_Id_Value__c" with Name
* Santhosh Ganji                                             05/24/2019                REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
* Amar Gadewar                  358979                       06/12/2019                REQ - 399947 - Quality - RCC - Medicare ID field on Task Edit and Task Detail page (CRM)
* Vinay Lingegowda                                           06/28/2019                REQ 402793 AHT - All Users - Add Close Task button on Task Detail Page (RF)
* Samantha Tennyson                                          02/05/2021                User Story 1875448: PR00091574 - MF 1 - Quality - Ability to capture additional information related to Notification Task (RF)
* Samantha Tennyson       						 			 06/17/2021                User Story 2369230: T1PRJ0001827 - MF 1 - Quality - Ability to capture additional information related to Notification Task (CRM)
* Samantha Tennyson       						 			 06/23/2021           	   User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM)
* Jasmeen Shanagri											 01/04/2022				   User Story 2701238 : Handle exceptions
* Anil Parvathaneni				                             10/18/2022				   User Story 3176163 : Create New Task Type and fields for Market Credentialing
*****************************************************************************************************************************/

public with sharing class HUMTaskDetailExtension 
{
    private Task task = new Task();
    private String sExtTask = HUMConstants.HUMExtensionTask;
    public String sMedicareID {get; set;}
    public Boolean bShowTaskEndDate{get;set;}
    public boolean bCredentialingswitch{get;set;}
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> lstErrors
    {
        get
        {
            if(lstErrors==null){return new List<Error_Log__c>();}
            else{return lstErrors;}
        }
        set;
    }
    /*
     * Method Name : HUMTaskDetailExtension - Controller
     * Description : Query task object for given task Id in url
     * Return type : NA
     * Paramater   : ApexPages.StandardController
     */
     
    public HUMTaskDetailExtension(ApexPages.StandardController controller)
     {
        String sOwnerId = '';
        
        try
        {
            this.task = (Task)controller.getRecord();  
            bCredentialingswitch = HUMUtilityHelper.isCRMFunctionalityON('3176163');        
            sOwnerId = ApexPages.currentPage().getParameters().get('ownerId');            
            if(String.isNotBlank(sOwnerId)) task.OwnerId = sOwnerId;            
            list<Case> lstCase = [select Id,Recordtype.Name,Policy_Member__c,Policy_Member__r.Product__c, Owner_Queue__c,Created_By_Queue__c,Origin,Classification_Id__r.name , Intent_Id__r.name from Case where id=:task.Whatid];
            bReadOnly = false;
            if(lstCase!=null && !lstCase.isEmpty())
            {                
                if(lstCase[0].Recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || (lstCase[0].Policy_Member__c != null && lstCase[0].Policy_Member__r.Product__c == GLOBAL_CONSTANT_HUM.MEDICARE_POLICY) || (chatQueues.contains(lstCase[0].Owner_Queue__c) && lstCase[0].Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
                {
                    if(lstCase[0].Recordtype.name.containsIgnoreCase(GLOBAL_CONSTANT_HUM.HumHPRecType) || wqueues.contains(lstCase[0].Owner_Queue__c) || wqueues.contains(lstCase[0].Created_By_Queue__c) || (chatQueues.contains(lstCase[0].Owner_Queue__c) && lstCase[0].Origin == GLOBAL_CONSTANT_HUM.WEB_CHAT_ORIGIN))
                    {
                        bReadOnly = True;
                    }
                }
                if(lstCase[0].Policy_Member__c!=null)
                {
                    List<Member_ID__c> listMemberIds = [select id,Name from Member_ID__c where Policy_Member__c =:lstCase[0].Policy_Member__c and Type__c =: HUMConstants.HUMMedicare limit 1];    
                    if(listMemberIds!=null && !listMemberIds.isempty())
                    {
                        sMedicareID = listMemberIds[0].Name;
                    }
                }  
               bShowTaskEndDate = (lstCase[0].Classification_Id__r.name == 'Provider Requests' && 
                        lstCase[0].Intent_Id__r.name == 'LA Medicaid Cred Workflow' &&
                        lstCase[0].Owner_Queue__c == 'Market National Network Ops' )? true:false;                
            }
            
            List<HUM_Task_Heirarchy__c> lstparentTaskID  = [select HUM_Parent_TaskID__c FROM HUM_Task_Heirarchy__c where HUM_Dependent_TaskID__c = :Task.id AND HUM_Parent_TaskID__c != null limit 1];
            if(lstparentTaskID != null && !lstparentTaskID.isEmpty())
            {
                list<Task> lstParentTask = [select ID, Task_Number__c from Task where Id = :lstparentTaskID[0].HUM_Parent_TaskID__c limit 1 ];
                if(lstParentTask!=null && !lstParentTask.isEmpty())
                {
                    parentTask = lstParentTask[0];
                }
            }
            sPharmacyUser =  Pharmacy_H_HUM.isPharmacyUser();
            if(String.isNotBlank(task.Type) && Task.Type.EqualsIgnoreCase(sExtTask)) bIsExtRequest= true;
            else  bIsExtRequest=false;                                                    
                      
        } 
        catch(Exception e)
        {
            HUMExceptionHelper.bRestrictLog = true;  // Exception not inserted when calling from constructor.
            lstErrors = HUMExceptionHelper.logErrors(e,'HUMTaskDetailExtension', 'HUMTaskDetailExtension');
        }
    }
    Public Task parentTask {Get;Set;}
    Public String sPharmacyUser{Get;Set;}
    public boolean bIsExtRequest{get;set;}
    public boolean bReadOnly {get;set;}
    public Set<String> wqueues = new Set<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public Set<String> chatQueues = new Set<String>(GLOBAL_CONSTANT_HUM.WEB_CHAT_WQUEUES); 

    /* To close the task from detail page
    * @return NA
    */ 
    public void closeTask() {

        try{
            if((null == task.Task_Completion_Date__c || null == task.Credentialing_Task__c) && bCredentialingswitch  && bShowTaskEndDate){
				ApexPages.Message msg = new Apexpages.Message
				(ApexPages.Severity.Error,'You must select Credentialing Task and Completion date before closing the Task');
				 Apexpages.addMessage(msg);
				return;
			}else{
				task.status = Label.HUMTaskStatusClosed;
                task.ownerId = UserInfo.getUserId();
                update(task);
			}
        }catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'HUMTaskDetailExtension', 'closeTask');  
        }
    }          
}