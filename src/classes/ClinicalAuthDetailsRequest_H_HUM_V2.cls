/**************************************************************************** 
Apex Class Name  : ClinicalAuthDetailsRequest_H_HUM_V2 
Version          : 1.0  
Created Date     : November 11, 2019
Function         : Request helper for GetAuthorizationDetails method in clinical service
Test Class       : ClinicalAuthDetailsRequest_T_HUM_V2
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Imran           28658            11/11/2019                    Original Version
***************************************************************************************************/
public class ClinicalAuthDetailsRequest_H_HUM_V2
{   
    public AuthDetailRequest AuthDetailRequest =null;
    
    public class AuthDetailRequest
    {        
        public String AuthID='';  
        public String MemberId=''; 
        public String MemberIdType =''; 
        public String DateOfBirth ='';        
        public PageMetaData_Communication PageMetaData_Communication;
        public PageMetaData_Letters PageMetaData_Letters;
        public Filter_Communication Filter_Communication;
        public Filter_Letters Filter_Letters;
        public List<SortDetail_Letters> SortDetail_Letters;        
        public List<SortDetail_Communication> SortDetail_Communication;
    }   
    
    public class PageMetaData_Communication
    { 
        public Integer Pagenumber;  
        public Integer Pagesize ; 
    }
     
    public class Filter_Communication 
    {
        public string CreatedFrom;
        public string CreatedTo;
    } 
    
    public class SortDetail_Communication 
    {
        public Integer Sequence;
        public String Order;
        public String Criteria;
    }
    
    public class PageMetaData_Letters 
    { 
        public Integer Pagenumber;  
        public Integer Pagesize ; 
    }
     
    public class Filter_Letters 
    {
        public string CreatedFrom;
        public string CreatedTo;
    } 
    
    public class SortDetail_Letters
    {
        public Integer Sequence;
        public String Order;
        public String Criteria;
    }
 }