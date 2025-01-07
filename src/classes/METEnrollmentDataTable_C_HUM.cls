/*****************************************************************************************************
Apex Class Name : METEnrollmentDataTable_C_HUM
Version   		: 1.0 
Created Date  	: 11/30/2017
Function  		: Controller to fetch MET Task records of the case
Test Class  	: METEnrollmentDataTable_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Rakesh Patni				153362			11/30/2017					  Original Version
* Rakesh Patni				158049			12/18/2017					  Updates as per REQ - 349584
*******************************************************************************************************/
public with sharing class METEnrollmentDataTable_C_HUM
{
    public string columnName{get;set;}
    public string columnHeader{get;set;}
    public integer orderColumnNumber{get;set;}
    /**
	*  Constructor for the class
	*  @name METEnrollmentDataTable_C_HUM
	*  @param NA
	*  @return void
	*  @throws NA
	*/
    public METEnrollmentDataTable_C_HUM()
    {
		List<String> columnNameList = new list<String>{ 'sColSort','sTask','sAction','sSource','sStatus','sCreatedDate','sCreatedBy','sLastModDate','sLastModifiedBy','sCancelledByDate','sCancelledBy' };
		List<String> columnHeaderList = new list<String>{ 'Column Sort','Task','Action','Source','Status','Created Date','Created By','Last Modified Date','Last Modified By','Cancelled By Date','Cancelled By' };
		columnName = JSON.serialize(columnNameList);
        columnHeader = JSON.serialize(columnHeaderList);
        orderColumnNumber = 0;
    }
    /**
	*  Remote method for visualforce remoting to fetch MET records
	*  @name getMETRecords
	*  @param 	sCaseRecordId
	*  @return 	lstscript
	*/
    @RemoteAction
    public static List<METEnrollmentDataTable_DTO_HUM> getMETRecords(String sCaseId)
    {
        return getMETEnrollments(sCaseId);
    } 
    /**
	*  Gets list of MET records for given case 
	*  @name getMETEnrollments
	*  @param sCaseRecordId
	*  @return lstMETEnrollments
	*/
    public static List<METEnrollmentDataTable_DTO_HUM> getMETEnrollments(String sCaseRecordId)
    {
        try 
        {
            List<METEnrollmentDataTable_DTO_HUM> lstMETEnrollments = new list<METEnrollmentDataTable_DTO_HUM>();
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
               METEnrollmentDataTable_DTO_HUM objMETEnrollment = new METEnrollmentDataTable_DTO_HUM();
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
            return lstMETEnrollments;
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e,'METEnrollmentDataTable_C_HUM','getMETEnrollments');
            return null;
        }
    }
}