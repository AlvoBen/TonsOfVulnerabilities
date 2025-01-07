/**************************************************************************** 
Apex Class Name  : CommunicationRecordsRequest_H_HUM
Version          : 1.0  
Created Date     : December 03, 2019
Function         : This class is used to build request Rest Service for Communication record service
Test Class       : 
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula                                             12/03/2019                   Original Version 
***************************************************************************************************/
public with sharing class CommunicationRecordsRequest_H_HUM
{
    public GetCommunicationRecordsRequest GetCommunicationRecordsRequest;
    public class GetCommunicationRecordsRequest
    {
        public string MemberId = '';  
        public string MemberIdType = '';
        public String DateOfBirth;
        public Filter Filter = null;
        public PageMetaData PageMetaData = null;
        public List<SortInfo> SortDetail = null;
   }
   public class PageMetaData
   {
        public integer Pagesize;   
        public integer Pagenumber;
   }
   public class Filter
   {
       public string EntityID = '';
       public DateTime CreatedFrom;
       public DateTime CreatedTo;
   }
   public class SortInfo
   {
        public integer Sequence;
        public string Order;
        public string Criteria;   
   }
   
}