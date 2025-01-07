/*******************************************************************************************************************************
Apex Class Name : CaseAttachmentTrackService_C_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Class contains method for services to post Milestone information for Attachments
test Class      : CaseAttachmentTrackService_T_HUM

Modification Log:
* Developer Name            Code Review             Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                257266               07/10/2018                 Original Version: REQ - 362124.
*
********************************************************************************************************************************/
public with sharing class CaseAttachmentTrackService_C_HUM 
{
	//Milestone for Attachments
	public static string sAttachmentMS = '903';
	
	/**
    * This is an invocable method for creating 903 milestone entries for attachments
    * 
    * @param    attachmentIDs   List of new attachment IDs
    * @return   none
    */
	@InvocableMethod 
	public static void invokeTrackServiceForAttachments(List<Id> attachmentIDs)
	{
		try
		{			
			//Get all Medicare Enrollment Users with CRMS_630_MedicareElectionTracking_EnrollmentEdit permission set
			list<PermissionSetAssignment> lstPSAssignment = [SELECT Id, assigneeId, PermissionSet.Name FROM PermissionSetAssignment 
															 WHERE PermissionSet.Name =: GLOBAL_CONSTANT_HUM.METENROLLMENTMEDICAREEDIT_PERMISSIONSET_HUM];
			set<ID> medicareUsers = new set<ID>();			
			for(PermissionSetAssignment psAssignment : lstPSAssignment)
			{
				medicareUsers.add(psAssignment.assigneeID);
			}
			
			map<Id, set<MET_Milestone__c>> mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();
			
			List<Attachment__c> lstAttachments = [SELECT Id, Related_To_Case__c, createdById FROM Attachment__c WHERE ID IN :attachmentIDs];
						
			if(lstAttachments != null && !lstAttachments.isEmpty())
			{
				for(Attachment__c attachmentRec : lstAttachments)
				{
					if(medicareUsers!=null && !medicareUsers.isEmpty() && medicareUsers.contains(attachmentRec.createdById))  //Check if attachment was created by Medicare Enrollment User
					{
						set<MET_Milestone__c> setMilestones = new set<MET_Milestone__c>();
						if(mapCaseMilestones.containsKey(attachmentRec.Related_To_Case__c))	setMilestones = mapCaseMilestones.get(attachmentRec.Related_To_Case__c);	                   
                		setMilestones.add(new MET_Milestone__c(Name = sAttachmentMS));
                		mapCaseMilestones.put(attachmentRec.Related_To_Case__c, setMilestones);
					}				
				}
			}
			if(!mapCaseMilestones.isEmpty())
			{
				System.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));
			}
		}
		catch(Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'CaseAttachmentTrackService_C_HUM', 'invokeTrackServiceForAttachments');
		}
	}    
}