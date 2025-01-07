/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumanaFeedbackReq_DTO_HUM.cls 
Created Date     : September 25, 2023
Function         : This class contain request nodes of feedback service.

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Kiran Kotni               09/25/2023                Initial Version
************************************************************************************************************************************************************************************/
public with sharing class MyAssistantAtHumanaFeedbackReq_DTO_HUM {
    
    public string chatSessionId;
    public string sequenceId;
    public string timestamp;
    public Feedback feedback;
    
    public class Feedback{
        public Boolean isHelpful;
        public string userComment;
    }

}