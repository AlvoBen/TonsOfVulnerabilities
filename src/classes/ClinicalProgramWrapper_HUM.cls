/************************************************************************************************** 
Apex Class Name  : ClinicalProgramWrapper_HUM 
Version          : 1.0  
Created Date     : Oct 30, 2018
Function         : Data Transfer Object class to populate ClinicalPrograms/CareManager Data.
***************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Buvaneswaran M                              10/30/2018                    Original Version
* Sagar MN									  06/04/2019                                     
* Asish Behera                                06/20/2019                  REQ-380791 Program code parameter added 
* Imran                                     04/14/2020                  REQ - 891352 - Table updated
* Ranjeeth N                                05/18/2020                  DF-746 remove null
* Pinky Vijur                                   08/19/2021                 REQ - 2080855 - T1PRJ0052463 - MF 6 -  Display Palliative Care program participation details in CRM - Pallitus Health Partners (CRM)
****************************************************************************************************/
public with sharing class ClinicalProgramWrapper_HUM {
   public ClinicalProgramDetails ClinicalProgramDetails{get;set;} 
   public string internalError{get;set;}
  
   public class ClinicalProgramDetails{       
    
  	 	public List<EnrolledProgramDetail> EnrolledProgramDetail{get;set;}       
   		public List<EligibleProgramDetail> EligibleProgramDetail{get;set;}
        
   }
  
  public class EnrolledProgramDetail{
        public String programName  {get;set;}
        public String InterventionVendor {get;set;} 
        public String CareManagerName  {get;set;}
        public String phoneNumber  {get;set;} 
        public String BeginDate {get;set;}
        public String EndDate {get;set;}
 }
   public class EligibleProgramDetail{
        public String sprogramName  {get;set;}        
        public String InterventionVendor {get;set;}        
        public String BeginDate {get;set;}
        public String EndDate {get;set;}
        public String Comments {get;set;}
        public string ProgramCode{get;set;}
   } 
    
}