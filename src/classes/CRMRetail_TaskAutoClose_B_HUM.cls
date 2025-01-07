/******************************************************************************************************************************
Apex Class Name  : CRMRetail_TaskAutoClose_B_HUM
Version          : 1.0 
Created Date     : 03/21/2023
Function         : Batch to auto-close the tasks for deceased member   
Test Class       : CRMRetail_TaskAutoClose_T_HUM
Modification Log :
* Developer              Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Mohamed Thameem      	                	    03/21/2023           US - 4407867 Storefront: Auto-close tasks for deceased member
* Mohamed Thameem      	                	    05/12/2023           US - 4594514 Storefront: Auto-close tasks for members with missing contact info
*********************************************************************************************************************************/

public class CRMRetail_TaskAutoClose_B_HUM implements Database.Batchable<sObject>,Schedulable,Database.stateful
{
    public Integer numofFailedUpdates =0;
    public String errorMesg = '\n\n';
    
    public Database.QueryLocator start(Database.BatchableContext bc)
    {    
        try
        {
            String query;
            if(HUMUtilityHelper.isCRMFunctionalityON('4594514'))
            {
                query = 'select id,status,Completed_Reason__c,Description,Account.PersonOtherPhone,Account.PersonHomePhone,Account.Deceased_Date__c from task where recordtype.developername = \'' +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKRECORDTYPENAME+ '\' AND what.type= \'' +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT+ '\' AND Account.recordtype.developername = \'' +GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+ '\' AND status IN ' +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_AUTO_TASK_CLOSE_STATUS_LIST+ ' AND((Account.Deceased_Date__c  != Null) OR (Account.PersonHomePhone = Null AND Account.PersonOtherPhone = Null))';
            }
            else
            {
                query = 'select id,status,Completed_Reason__c,Account.Deceased_Date__c,Description from task where recordtype.developername = \'' +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASKRECORDTYPENAME+ '\' and status IN ' +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_AUTO_TASK_CLOSE_STATUS_LIST+ ' and what.type= \'' +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT+ '\' and Account.recordtype.developername = \'' +GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+ '\' and Account.Deceased_Date__c  != Null';
            }
            return Database.getQueryLocator(query);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_TASKAUTOCLOSE_CLASS, 'start');
            return null;
        }
    }   
    
    public void execute(Database.BatchableContext bc, List<task> taskRecords)
    {    
        try
        {
            if(HUMUtilityHelper.isCRMFunctionalityON('4407867'))
            {
                list<task> taskListToUpdate = new List<task>();
                CRM_GenericHelper_H_HUM.accessibilityOutput accessbilityObj;
                Database.SaveResult[] srList;
                
                for(task tsk : taskRecords)
                {
                    tsk.status = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_STATUS;
                    tsk.Completed_Reason__c  = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_UNABLE_TO_CONTACT;

                    if(tsk.Account.Deceased_Date__c  != Null)
                    {
                        tsk.Description = tsk.Description!=null ? tsk.Description+ GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NEWLINE +GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED : GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_DECEASED;
                    }
                    else if(HUMUtilityHelper.isCRMFunctionalityON('4594514') && tsk.Account.PersonHomePhone == null && tsk.Account.PersonOtherPhone == null)
                    {
                        tsk.Description = tsk.Description!=null ? tsk.Description+ GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NEWLINE +GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_PHONENOT_LISTED : GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_PHONENOT_LISTED;
                    }
                    
                    taskListToUpdate.add(tsk);
                }
                
                if(taskListToUpdate.size()>0)
                {	
                    accessbilityObj  = CRM_GenericHelper_H_HUM.securityCheckFLS(AccessType.UPDATABLE, taskListToUpdate);             
                    
                    if(accessbilityObj.isObjectAccessible && (accessbilityObj.areFieldsAccessible || ( accessbilityObj.fieldsNotAccessible.size() == 1 && accessbilityObj.fieldsNotAccessible.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_ACCOUNTID))))
                    {
                        srList = Database.update(taskListToUpdate, false);
                        for (Database.SaveResult sr : srList) 
                        {	
                            if (!sr.isSuccess())
                            {	
                                errorMesg += GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_BATCHERROR_TASKID+sr.getId()+ GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_BTACHERROR;
                                for(Database.Error err : sr.getErrors()) 
                                {
                                    errorMesg+= err.getMessage()+',';
                                }
                                errorMesg = errorMesg.substringBeforeLast(',')+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NEWLINE;
                                numofFailedUpdates++;
                            }
                        }
                    }            
                    else
                    {
                        String error = (!accessbilityObj.isObjectAccessible) ? GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK : GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + accessbilityObj.fieldsNotAccessible;
                        HUMExceptionHelper.logError(error,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_TASKAUTOCLOSE_CLASS, 'execute','','');
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_TASKAUTOCLOSE_CLASS, 'execute');
        }
    }    
    
    public void finish(Database.BatchableContext bc)
    {
        try
        {
            if(String.isNotBlank(errorMesg) && numofFailedUpdates > 0)
            {
                String DML_EXCEPTION = HUMConstants__c.getInstance('DML_EXCEPTION') != null ? HUMConstants__c.getInstance('DML_EXCEPTION').StringValue__c : '';
                Integer lengthOfMsgField = Error_Log__c.Error_Message__c.getDescribe().getLength();
                String finalErrorMesg = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_UPDATING_TASK+numofFailedUpdates+ GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_BATCHERROR_RECORDS+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NEWLINE+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_BATCHERROR_DETAILS+errorMesg;
                
                Error_Log__c logError = new Error_Log__c();
                logError.Error_Code__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_BATCHFAIL_MSG  + DateTime.now().format();
                logError.class_name__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_TASKAUTOCLOSE_CLASS;
                logError.Method_Name__c = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_TASKAUTOCLOSE_CLASS+'.execute';
                logError.Error_Message__c = (finalErrorMesg.Length() >= lengthOfMsgField) ? finalErrorMesg.subString(0,lengthOfMsgField):finalErrorMesg;
                logError.Type__c = DML_EXCEPTION;
                Database.insert(logError);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_STRING_TASKAUTOCLOSE_CLASS, 'finish');
        }
    }    
    
    public void execute(SchedulableContext sc) 
    {        
        Database.executeBatch(new CRMRetail_TaskAutoClose_B_HUM());
    } 
}