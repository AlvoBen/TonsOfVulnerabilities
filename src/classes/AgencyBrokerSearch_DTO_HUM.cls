/*******************************************************************************************************************************
Apex Class Name : AgencyBrokerSearch_DTO_HUM
Version         : 1.0
Created On      : 10/31/2020
Function        : This class is used to hold the agent search input values
Test Class      : 

Modification Log: 
 * Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                      Rahul Krishan                                       10/31/2020                      initial version                                                                                                09/28/2020                                                                        Original Version
******************************************************************************************************************************************************************************/
public class AgencyBrokerSearch_DTO_HUM 
{
    @AuraEnabled public String sFirstName{get; set;}
    @AuraEnabled public String sLastName{get; set;}
    @AuraEnabled public String sAgencyName {get; set;}
    @AuraEnabled public String sAgentId{get; set;}
    @AuraEnabled public String sTaxID {get; set;}
    @AuraEnabled public String sAgentType{get; set;}
    @AuraEnabled public String sState{get; set;}
    @AuraEnabled public Boolean isUnknownAgencySearch{get; set;}
    
    public AgencyBrokerSearch_DTO_HUM() 
    {
        this.sFirstName= sFirstName;
        this.sLastName= sLastName;
        this.sAgencyName= sAgencyName;
        this.sAgentId= sAgentId;
        this.sTaxID= sTaxID;
        this.sAgentType= sAgentType;
        this.sState= sState;
        this.isUnknownAgencySearch = isUnknownAgencySearch;
    }
}