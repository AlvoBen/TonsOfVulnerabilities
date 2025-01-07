/**************************************************************************** 
Apex Class Name  : ClinicalAuthSummaryRequest_H_HUM_V2
Version          : 1.0  
Created Date     : Nov 11, 2019
Function         : Request helper for GetAuthSummary method in clinical service
Test Class       : ClinicalAuthSummaryRequest_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty         28658             11/04/2019                   Original Version 
***************************************************************************************************/


public with sharing class ClinicalAuthSummaryRequest_H_HUM_V2
{   
    public ClinicalAuthSummaryRequest AuthSummaryRequest =null;    
    public class ClinicalAuthSummaryRequest 
    {
        public String MemberId=''; 
        public String MemberIdType =''; 
        public String DateOfBirth ='';    
        public String IncludeCDRAuth =''; 
        public ClincalAuthcategory AuthFilter = null;       
        public ClinicalPageMetaData PageMetaData =null;
    }

     Public class ClincalAuthcategory
    {
        public String AuthCategory ='';  
    }

    public class ClinicalPageMetaData 
    { 
        public Integer Pagenumber;  
        public Integer Pagesize ; 
    }
}