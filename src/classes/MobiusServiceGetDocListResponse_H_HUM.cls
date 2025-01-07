/**************************************************************************** 
Apex Class Name  : MobiusServiceGetDocListResponse_H_HUM 
Version          : 1.0  
Created Date     : January 06, 2016
Function         : response helper for GetDocList Method in Mobius Service
Test Class       : MobiusServiceGetDocListResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma            29147              01/06/2016                   Original Version
* Gargey Sharma            30154              02/24/2016                   REQ - 252568: Updated request structure as per updates in service
* Gargey Sharma            30253              03/02/2016                   Made changes as method name changed to GetDocumentList
* Muralidhar Kollu                            10/25/2018                   REQ - 357260 - added RemitID to the response
* Muralidhar Kollu         355971             05/17/2019                   REQ - 397216
* Anjani Vaddadi                              02/25/2020                   REQ - 764250
***************************************************************************************************/
public class MobiusServiceGetDocListResponse_H_HUM 
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