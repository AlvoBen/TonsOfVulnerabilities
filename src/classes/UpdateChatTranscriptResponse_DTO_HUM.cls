/******************************************************************************************************************
Apex class Name    : UpdateChatTranscriptResponse_DTO_HUM
Version            : 1.0
Function           : This class is used as response for update transcript Rest Service
Created On         : Dec 28, 2021
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Vishnu Pilli                                   12/28/2021          Original Version

------------------------------------------------------------------------------------------------------------------- */
public class UpdateChatTranscriptResponse_DTO_HUM {

    public UpdateChatTranscriptResponse UpdateChatTranscriptResponse ;
    
    public class UpdateChatTranscriptResponse  
    {
        public String Success = '';
        public String TranscriptNumber  = '';  
        public String TranscriptId  = '';  
        public String ErrorMessage = ''; 
    }
}   //  end of UpdateChatTranscriptResponse_DTO_HUM