/************************************************************************************************************************************************************************************       
Apex Class Name  : METEnrollmentCaseDetailTable_LC_HUM.cls 
Created Date     : November 2, 2022
Function         : Executes SOQL in support of the METEnrollmentDetailHum component
Test Class       : METEnrollmentCaseDetailTable_LT_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Pooja Kumbhar                                     11/02/2022                  User Story - 3863263 : T1PRJ0170850 - Lightning - RCC/Medicare (MET) Enrollment sections Case Details Page
* Pooja Kumbhar                                     02/02/2022                  US 4224131 Lightning - MET Case Edit - Sorting  & Case Details  Refresh MET Component
************************************************************************************************************************************************************************************/
public with sharing class METEnrollmentCaseDetailTable_LC_HUM
{

    /**
    *  method to fetch MET records
    *  @name getMETRecords
    *  @param   sCaseRecordId
    *  @return  lstscript
    */
      @AuraEnabled(cacheable=false)
    public static object getMETRecords(String sCaseRecordId)
    {
    List<METEnrollmentDataTable_LDTO_HUM> lstMETEnrollments = new list<METEnrollmentDataTable_LDTO_HUM>();          
        try 
        {
            String metTaskType = GLOBAL_CONSTANT_HUM.MET_TASK_RECORDTYPE_HUM;
            List<Task> listMetTask = [Select id, createdby.name, createdby.id, createddate, Lastmodifiedby.id,
                Lastmodifiedby.name, Lastmodifiedby.firstName, Lastmodifiedby.LastName, lastmodifieddate,
                MET_CancelledByUser__r.name, MET_CancelledByUser__r.id, MET_CancelledBydate__c,
                MET_Task__r.Name, MET_Action__r.Name, MET_Source__r.Name, Status, CreatedDate_Sort__c,Created_By_Queue__c,LastModifiedby_Queue__c
                from Task where Whatid = :sCaseRecordId And RecordType.DeveloperName = : metTaskType order by
                CreatedDate_Sort__c];
               Case oCase = [Select id, Case_Owner__c, Owner_Queue__c, createdById from case where id = :sCaseRecordId];
              ID createdID = oCase.CreatedById;
            User caseUser = [SELECT Id, Current_Queue__c FROM User WHERE Id = :createdID];
            for(Task objTask :listMetTask )
            {
               METEnrollmentDataTable_LDTO_HUM objMETEnrollment = new METEnrollmentDataTable_LDTO_HUM();
               objMETEnrollment.sColSort = objTask.CreatedDate_Sort__c;
               objMETEnrollment.sTask = objTask.MET_Task__r.Name;
               objMETEnrollment.sAction = objTask.MET_Action__r.Name;
               objMETEnrollment.sSource = objTask.MET_Source__r.Name;
               objMETEnrollment.sStatus = objTask.Status;
               objMETEnrollment.sCreatedDate = objTask.createdDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
               objMETEnrollment.sCreatedBy = objTask.createdby.name +CaseDetailConstants_HUM.sSrcUp+objTask.createdby.id;
               objMETEnrollment.sLastModDate = objTask.lastmodifieddate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);         
               objMETEnrollment.sLastModifiedBy = objTask.Lastmodifiedby.firstName+' '+objTask.Lastmodifiedby.LastName+CaseDetailConstants_HUM.sSrcUp+objTask.Lastmodifiedby.id;
               if (objTask.MET_CancelledBydate__c != null) {
                   objMETEnrollment.sCancelledByDate = objTask.MET_CancelledBydate__c.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
               }
               if(objTask.MET_CancelledByUser__r.name!= null)
               {
                   objMETEnrollment.sCancelledBy = objTask.MET_CancelledByUser__r.name+CaseDetailConstants_HUM.sSrcUp+objTask.MET_CancelledByUser__r.id;
               }
               else
               {
                   objMETEnrollment.sCancelledBy = objTask.MET_CancelledByUser__r.name;
               }
               lstMETEnrollments.add(objMETEnrollment);
              }
            
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e,'METEnrollmentDataTable_C_HUM','getMETEnrollments');
            return null;
        }
         return JSON.serialize(lstMETEnrollments);
    }
}