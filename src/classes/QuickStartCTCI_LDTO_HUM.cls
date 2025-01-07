/************************************************************************************************************************************************************************************       
Apex Class Name  : QuickStart_LDTO_Hum.cls 
Created Date     : March 2, 2022
Function         : DTO for Classification and Intent for logged in User
Test Class       : QuickStart_LT_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Jasmeen Shangari            -----                 03/02/2022                 Original Version
************************************************************************************************************************************************************************************/
public with sharing class QuickStartCTCI_LDTO_HUM 
{
    public Id idClassification{get; set;}
    public String sClassificationLabel{get; set;}
    public Map<Id, Intent_DTO_HUM> mapIntentInfo{Get; Set;}
    
    public QuickStartCTCI_LDTO_HUM(CTCI_Junction__c ctci, Map<Id, Intent_DTO_HUM> mapIntentInfo) 
    {
        this.idClassification = ctci.Classification__c;
        this.sClassificationLabel = ctci.Classification__r.Name;
        this.mapIntentInfo = mapIntentInfo;
    }

    public class Intent_DTO_HUM 
    {
        public Id idCTCI{get; set;}
        public Id idIntent{get; set;}
        public String sIntentLabel{Get; Set;}
        
        public Intent_DTO_HUM(CTCI_Junction__c ctci)
        {
            this.idCTCI = ctci.Id;
            this.idIntent = ctci.Intent__c;
            this.sIntentLabel = ctci.Intent__r.Name;
        } 
    }    
}