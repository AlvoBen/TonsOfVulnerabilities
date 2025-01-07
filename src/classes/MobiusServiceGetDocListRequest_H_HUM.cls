/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListRequest_H_HUM 
Version          : 1.0  
Created Date     : January 05, 2016
Function         : request helper for GetDocumentList method in Mobius Service
Test Class       : MobiusServiceGetDocListRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             29147             01/05/2016                   Original Version
* Gargey Sharma             30154             02/23/2016                   REQ - 252568: Updated request structure as per updates in service
* Gargey Sharma             30253             03/02/2016                   Made changes as method name changed to GetDocumentList
* Saurabh Kaple             157136            01/10/2018           REQ - 348181 Mobius Service- Use Genkey from platform instead of MBE genkey conversion  
***************************************************************************************************/
public class MobiusServiceGetDocListRequest_H_HUM 
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