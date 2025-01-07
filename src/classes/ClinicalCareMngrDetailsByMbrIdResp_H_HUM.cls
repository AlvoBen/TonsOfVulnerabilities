/**************************************************************************** 
Apex Class Name  : ClinicalCareMngrDetailsByMbrIdResp_H_HUM 
Version          : 1.0  
Created Date     : July 26, 2018
Function         : Response helper for retrieveCareManagerDetailById method in clinical service
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Amol Patil		                         7/26/2018                    Original Version

***************************************************************************************************/

public with sharing class ClinicalCareMngrDetailsByMbrIdResp_H_HUM{
    public CareManagerbyMemberIdResponse CareManagerbyMemberIdResponse;
       public class CareManagerbyMemberIdResponse 
       {
         public CareManagerDetails CareManagerDetails;
         public String Message = '';
    }
    public class CareManagerDetails {
        public list<CareManager> CareManager;
    }
    public class CareManager {
        public String EndDate;
        public String ID;   //1
        public String Name{get;set;} //Abby Murphy
        public String PhoneNumber{get;set;}   //901-566-1002
        public String StartDate;
        public String Type{get;set;} //Behavioral Health Care Manager         
    }
       
}