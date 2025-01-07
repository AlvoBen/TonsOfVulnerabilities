/********************************************************************************************* 
Apex Class Name  : HealthResourcesRequest_DTO_HUM
Version          : 1.0  
Created Date     : Apr 02, 2020
Function         : Request helper for retrieving Group Clincal programs details
***********************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Imran                                       04/07/2020                  REQ-884296 Program code parameter added for Health Resource
***************************************************************************************************/
public class HealthResourcesRequest_DTO_HUM
{
   public GroupClinicalProgramsRequest GroupClinicalProgramsRequest;
   public class GroupClinicalProgramsRequest
   {
        public String GroupId='';        
        public String PlatformCode='';   
   }
 }