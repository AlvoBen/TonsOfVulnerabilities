/******************************************************************************************************************
Apex class Name    : ChatTranscriptRestGetResponse_DTO_HUM
Version            : 1.0
Function           : This class is used as response for create transcript Rest Service
Created On         : June 08 ,2020
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Joel George                                    6/08/2020          Original Version

------------------------------------------------------------------------------------------------------------------- */
public with sharing class CreateChatTranscriptResponse_DTO_HUM
{
    public CreateChatTranscriptResponse CreateChatTranscriptResponse ;
    
    public class CreateChatTranscriptResponse  
    {
        public String Success = '';
        public String TranscriptNumber  = '';  
        public String TranscriptId  = '';  
        public String ErrorMessage = ''; 
    }
}