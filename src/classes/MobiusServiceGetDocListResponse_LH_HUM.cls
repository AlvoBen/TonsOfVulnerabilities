/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListResponse_LH_HUM 
Version          : 1.0  
Created Date     : 06/01/2022 
Function         : response helper for GetDocList Method in Mobius Service
Test Class       : MobiusServiceGetDocListResponse_LT_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
public class MobiusServiceGetDocListResponse_LH_HUM 
{
    public getDocumentListResponse getDocumentListResponse;
    
    public class getDocumentListResponse 
    {
        public getDocumentListReturn getDocumentListReturn;
    }
    
    public class getDocumentListReturn 
    {
        public list<DocumentSearch> DocumentSearch;
    }
    
    public class DocumentSearch 
    {
        public TopicResultList TopicResultList;
    }
    
    public class TopicResultList
    {   
        public List<TopicResult> TopicResult;
    }
    
    public class TopicResult
    {
        public TopicKeyList TopicKeyList;
        public string DocumentKey = '';
        public string PrintedDate = ''; 
    }
    
    public class TopicKeyList
    {
        public List<TopicKey> TopicKey;
    }
    
    public class TopicKey
    {
        public string topicName = '';       
        public string statementBeginDate = '';  
        public string statementEndDate = '';    
        public string statementType = '';
        public string statementDescription = '';                               
        public string PersonId = '';
        public string RemitId = '';  
        public string MemberGenKey = '';                                                                         
    }
}