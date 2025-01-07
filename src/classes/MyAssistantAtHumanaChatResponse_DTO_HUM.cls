/************************************************************************************************************************************************************************************       
Apex Class Name  : MyAssistantAtHumanaChatResponse_DTO_HUM.cls 
Created Date     : September 21, 2023
Function         : Class contains Response Nodes for MyAH Chat API.

Modification Log: 
Developer Name                    Date                    Description
*--------------------------------------------------------------------------------------------------
* Kiran Kotni               09/21/2023                Initial Version
************************************************************************************************************************************************************************************/
public with sharing class MyAssistantAtHumanaChatResponse_DTO_HUM {
   
      @AuraEnabled public string chatSessionId;
      @AuraEnabled public string query;
      @AuraEnabled  public String sequenceId;
      @AuraEnabled  public String timestamp;
      @AuraEnabled  public Result result;
    
        public class Result{
        @AuraEnabled    public string response;
        @AuraEnabled    public List<ChainOfThought> chainOfThought;
        }
        public class ChainOfThought{
        @AuraEnabled   public string tool;
        @AuraEnabled   public List<Proof> proof;
        @AuraEnabled    public string observation;
        }
        public class Proof{
        @AuraEnabled    public string text;
        @AuraEnabled    public string sourceUrl;
        }
   
}