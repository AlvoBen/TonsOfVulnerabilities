/*--
File Name        : LoggingCaseDataTable_C_HUM.cls
Version          : 1.0 
Created Date     : 01/04/2022   
Function         : Lightning Web Component used to display existing cases and logging 
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kiran Bhuvanagiri                  01/04/2022            User Story 2759501: Original Version
* Kiran Bhuvanagiri					 05/23/2022			   User Story 2935436: Address Component in Lightning - (CRMS) 
**************************************************************************************************/

public with sharing class LoggingCaseDataTable_C_HUM {
    @AuraEnabled(cacheable=true)
    public static CasesWrapper queryCases(String policyid, String caseNumber, String startDate, String endDate){
        try {
            Date startDateTemp, endDateTemp;
            String query = 'SELECT Id, CaseNumber, Classification_Type__c, Classification_Id__r.Name, Intent_Id__r.Name, Status, CreatedBy.Name, Created_By_Queue__c, CreatedDate FROM Case';
            String whereClause = ' WHERE Policy_Member__c = \''+policyid+'\' ';
            if(caseNumber != null && caseNumber != ''){
                whereClause = whereClause + ' AND CaseNumber = \''+caseNumber+'\'';
            }
            if(startDate != null && startDate != ''){
                if(startDate.contains('T')){
                    startDate = startDate.substringBefore('T');
                }
                String[] startDateString = startDate.split('-');
                startDateTemp = Date.newInstance(Integer.valueOf(startDateString[0]), Integer.valueOf(startDateString[1]), Integer.valueOf(startDateString[2]));
                whereClause = whereClause + ' AND CreatedDate >=: startDateTemp';
            }
            if(endDate != null && endDate != ''){
                if(endDate.contains('T')){
                    endDate = endDate.substringBefore('T');
                }
                String[] endDateString = endDate.split('-');
                endDateTemp = Date.newInstance(Integer.valueOf(endDateString[0]), Integer.valueOf(endDateString[1]), Integer.valueOf(endDateString[2])+1);
                whereClause = whereClause + ' AND CreatedDate <: endDateTemp';
            }
            String query1  = ' SELECT Count(Id) FROM Case ';
            query1 = query1+ whereClause;
            List<AggregateResult> aggList = Database.query(query1);
            whereClause = whereClause + ' ORDER BY CreatedDate DESC LIMIT 200 ';
            query = query + whereClause;
            List<Case> lstCases = Database.query(query);
            
            CasesWrapper wrapper = new CasesWrapper();
            wrapper.caseList = lstCases;
            wrapper.totalCases = (Integer)aggList[0].get('expr0');
            return wrapper;
        } catch (Exception e) {
            throw new AuraHandledException(e.getMessage());
        }
    } 
    
    public class CasesWrapper{
        @AuraEnabled
        public List<Case> caseList;
        @AuraEnabled
        public Integer totalCases;
    }
}