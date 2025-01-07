/*****************************************************************************************
Apex class Name    : Network_Response_DTO_HUM
Version            : 1.0
Function           : controller for displaying PCP update template data.
Created Date       : 04/12/2023
Test Class         : 
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                         				 	04/12/2023                 	 Initial Version
****************************************************************************************************************/
public with sharing class Network_Response_DTO_HUM {
    public class Networksresponse
    {
        @AuraEnabled
        public List<Network> Networks { get; set; }
    }
    @AuraEnabled public Networksresponse NetworksResponse { get; set; }
    
    public class Network
    {
        @AuraEnabled
        public Integer networkId { get; set; }
        @AuraEnabled
        public string networkName { get; set; }
        @AuraEnabled
        public string lobTypeDesc { get; set; }
        @AuraEnabled
        public Integer lobTypeId { get; set; }
        @AuraEnabled
        public Integer coverageTypeId { get; set; }
        @AuraEnabled
        public string coverageTypeDesc { get; set; }
        @AuraEnabled
        public Boolean flagMedicare { get; set; }
        @AuraEnabled
        public Boolean flagAFP { get; set; }
        @AuraEnabled
        public string redirectUrl { get; set; }
        @AuraEnabled
        public Boolean flagShowLanguage { get; set; }
        @AuraEnabled
        public Boolean flagFeedback { get; set; }
        @AuraEnabled
        public object[] directorySegments { get; set; }
    }
}