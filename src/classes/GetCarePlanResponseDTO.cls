/******************************************************************************************************************
Apex class Name    : GetCarePlanResponseDTO
Version            : 1.0
Function           : This class is used as response for getting CarePlans for CarePlanService.
Created On         : May 28 2021

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran		   1.0				   Original Version		
*   1.2			   Moshitha Gunasekaran        07/27/2021          Made changes as per latest ICD to add CarePlanSubject			
------------------------------------------------------------------------------------------------------------------- */
public with sharing class GetCarePlanResponseDTO {
    public boolean Success = false;
    public boolean Enrolled = false;
    public string ErrorMessage = '';
    public string MemberName = '';
    public string MemberSFID = '';
    public string PhoneNumber = '';
    public list<CarePlanDetailDTO> CarePlans;
    
    public class CarePlanDetailDTO {
    	public string CarePlanId = '';
        public string CaseOwner = '';
        public string CarePlanType = '';
        public string CarePlanSubType = '';
        public string CarePlanSubject = '';
        public string AreaFocus = '';
        public string Status = '';
        public string CreatedDateTime = '';
        public list<GoalDetailDTO> Goals;
        public list<MemberTaskDetailDTO> MemberTasks;
    }
    
    public class GoalDetailDTO {
    	public string GoalId = '';
        public string GoalName = '';
        public string Progress = '';
    	public string Category = '';
        public string Description = '';
        public string Priority = '';
        public string SortOrder = '';
        public string CreatedDateTime = '';
    }
    
    public class MemberTaskDetailDTO {
    	public string TaskId = '';
        public string GoalId = '';
        public string TaskType = '';
    	public string AssignedTo = '';
        public string Subject = '';
        public string DueDate = '';
        public string Status = '';
        public string Priority = '';
        public string Comments = '';
        public string CreatedDateTime = '';
    }
}