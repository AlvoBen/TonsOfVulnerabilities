/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListRequest_LH_HUM 
Version          : 1.0  
Created Date     : 06/01/2022
Function         : request helper for GetDocumentList method in Mobius Service
Test Class       : MobiusServiceGetDocListRequest_LT_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
public class MobiusServiceGetDocListRequest_LH_HUM 
{
    public getDocumentList getDocumentList;
    public class getDocumentList
    {
        public getDocument getDocument; 
    }
    public class getDocument
    {
        public list<DocumentSearch> DocumentSearch;
    }
    public class DocumentSearch
    {
        public string DocumentServer = '';
        public string ReportName = '';
        public string Creator = '';
        public string MaxRows = '';
        public TopicSearchList TopicSearchList;
    }
    public class TopicSearchList
    {
        public string JoinType = '';
        public list<TopicSearch> TopicSearch;
    }
    public class TopicSearch
    {
            public string TopicName = '';
            public string Comparator = '';
            public string SortOrder = ''; 
            public string MemberId = '';
            public string PlatformCode = '';
            public string ClaimNumber = '';
            public string RemitId = '';
            public string DocumentType = '';
            public string PersonId = '';
			public string MemberGenKey = '';
    }
}