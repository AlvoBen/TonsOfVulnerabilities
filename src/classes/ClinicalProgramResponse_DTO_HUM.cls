/********************************************************************************************* 
Apex Class Name  : ClinicalProgramResponse_H_HUM 
Version          : 1.0  
Created Date     : Oct 29, 2018
Function         : Response helper for Clincal programs details
***********************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Sudhendra                            		10/29/2018                    Original Version
* Sagar MN									10/29/2018
* Asish Behera                              06/20/2019                  REQ-380791 Program code parameter added
* Imran                                     04/14/2020                  REQ - 891352 - Table updated
*  Pinky Vijur                               08/19/2021                 REQ - 2080855 - T1PRJ0052463 - MF 6 -  Display Palliative Care program participation details in CRM - Pallitus Health Partners (CRM)
***************************************************************************************************/
public class ClinicalProgramResponse_DTO_HUM {
    public ClinicalProgramResponse ClinicalProgramResponse;
    
    public class EnrolledProgramDetails {
        public String ProgramName;
        public String InterventionVendor;
        public String CareManagerName;
        public String PhoneNumber;
        public DateTime BeginDate;
        public DateTime EndDate;
    }

    public class EligibleProgramDetails {
        public String ProgramName;
        public String InterventionVendor;        
        public DateTime BeginDate;
        public DateTime EndDate;
        public String Comments;
    }

    public class ClinicalProgramResponse {
        public List<EligibleProgramDetails> EligibleProgramDetails;
        public List<EnrolledProgramDetails> EnrolledProgramDetails;
    }
   
}