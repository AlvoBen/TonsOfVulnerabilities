/********************************************************************************************* 
Apex Class Name  : HealthResourcesResponse_DTO_HUM
Version          : 1.0  
Created Date     : Apr 02, 2020
Function         : Response helper for HealthResourcesResponse
***********************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Imran                                       04/07/2020                  REQ-884296 Program code parameter added for Health Resource
* Imran                                       06/29/2020                  Req-1129152- Add comments column to the group's Health Resources page   
***************************************************************************************************/
public class HealthResourcesResponse_DTO_HUM { 

   public GroupClinicalProgramsResponse GroupClinicalProgramsResponse;
     
    public class GroupClinicalProgramsResponse 
    {    
        public List<GroupClinicalProgramsDetails> GroupClinicalProgramsDetails;
    }   
    
    public class GroupClinicalProgramsDetails
     {
        public String ProgramName ='';
        public String InterventionVendor='' ;
        public DateTime BeginDate;
        public DateTime EndDate;
        public String Comments='' ;
     } 
}