/********************************************************************************************* 
Apex Class Name  : ClinicalProgramRequest_H_HUM 
Version          : 1.0  
Created Date     : Oct 29, 2018
Function         : Request helper for retrieving Clincal programs details
***********************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Buvaneswaran M                             10/29/2018                    Original Version
* Sagar MN									 10/29/2018
***************************************************************************************************/
public class ClinicalProgramRequest_DTO_HUM
{   
   public ClinicalProgramRequest ClinicalProgramRequest;
   public String includes='';
  
 
   public class ClinicalProgramRequest
   {
        public String MemberID='';        
        public String MemberIDType='';   
     }   
 }