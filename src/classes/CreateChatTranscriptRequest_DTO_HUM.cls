/******************************************************************************************************************
Apex class Name    : ChatTranscriptRestCreateRequest_DTO_HUM
Version            : 1.0
Function           : This class is used as request for create transcript Rest Service
Created On         : June 08 ,2020
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Joel George                                    6/08/2020          Original Version

------------------------------------------------------------------------------------------------------------------- */
public with sharing class CreateChatTranscriptRequest_DTO_HUM
{
    public CreateChatTranscriptRequest CreateChatTranscriptRequest;
    
    public class CreateChatTranscriptRequest 
    {
        public String InteractionNumber  ='';
        public String CaseNumber  =''; 
        public String TranscriptBody  ='';
        public String ChatType  ='';
        public String Issue  ='';
        public String PlanName  ='';
        public String URL  ='';
        public Integer AverageResponseTimeVisitor;
        public Integer MaxResponseTimeVisitor;
        public String RequestTime  ='';
        public String EndTime  ='';
        public String StartTime  ='';       
        public String EndedBy  ='';
        public Integer AverageResponseTimeOperator;
        public Integer MaxResponseTimeOperator;
        public Integer VisitorMessageCount;
        public Integer OperatorMessageCount;
        public String SourceApp  ='';
        public String SourceKey  ='';
        public String SuccessfulResolution  ='';
    }
}