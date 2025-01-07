/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumanaChatRequest_DTO_HUM.cls 
Created Date     : September 22, 2023
Function         : Class contains Request Nodes for MyAH Chat API.

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Kiran Kotni               09/22/2023                Initial Version
************************************************************************************************************************************************************************************/
public with sharing class MyAssistantAtHumanaChatRequest_DTO_HUM {
    
        public Client client;
        public string query;
        public string chatSessionId;
        public Context context;
   
    public class Client{
        public string name;
        public string id;
        public Metadata1 metadata;    
    }
    
    public class Metadata1{
        public string advocateId;
        public string interactionId;
    }
    
    public class Context{
        public Member member;
        public Metadata2 metadata;
    }
    
    public class Member{
        public string id;
        public boolean isProspect;
        public SelectedCoverage selectedCoverage;
    }
    
    public class SelectedCoverage{
        public string idBase;
        public string dependentCode;
        public string groupId;
        public string platformCode;
        public string effectiveDate;
    }
    
    public class Metadata2{
        public string oldPlanName;
    }
   
}