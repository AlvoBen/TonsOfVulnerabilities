/************************************************************************************************** 
Apex Class Name  : HealthResourcesWrapper_HUM 
Version          : 1.0  
Created Date     : Apr 02, 2020
Function         : Data Transfer Object class to populate GroupClinicalPrograms Data.
***************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------                                   
* Imran                                       04/07/2020                  REQ-884296 Program code parameter added for Health Resource
* Imran                                       06/29/2020                  Req-1129152- Add comments column to the group's Health Resources page   
****************************************************************************************************/
public with sharing class HealthResourcesWrapper_HUM {
   public GroupClinicalProgramDetails GroupClinicalProgramDetails{get;set;} 
   public string internalError{get;set;}
  
   public class GroupClinicalProgramDetails
   {    
        public List<GroupClinicalProgramsRequest> GroupClinicalProgramsRequest{get;set;}        
   }
   
   public class GroupClinicalProgramsRequest {
        public String ProgramName {get;set;}
        public String InterventionVendor {get;set;}
        public String BeginDate {get;set;}
        public String EndDate {get;set;}
        public String Comments {get;set;}
    } 
}