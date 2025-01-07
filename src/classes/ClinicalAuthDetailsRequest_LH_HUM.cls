/**************************************************************************** 
Apex Class Name  : ClinicalAuthDetailsRequest_LH_HUM 
Version          : 1.0  
Created Date     : July 12, 2022
Function         : Request helper for GetAuthorizationDetails method in clinical service
Test Class       : ClinicalAuthDetailsRequest_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Anuradha Gajbhe           		            07/12/2022                 Original Version
***************************************************************************************************/
public class ClinicalAuthDetailsRequest_LH_HUM
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