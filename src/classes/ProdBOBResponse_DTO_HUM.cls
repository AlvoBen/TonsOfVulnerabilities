/******************************************************************************************************************
Apex class Name    : ProducerBusinessResultResponse_DTO_HUM 
Version            : 1.0
Function           : Response DTO for producerBookOfBusinessRequest response
Created On         : Sep 19 2019
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Chetan Sindhe                                  09/18/2019         Original Version 
------------------------------------------------------------------------------------------------------------------*/
public with sharing class ProdBOBResponse_DTO_HUM {
    
	public ProducerBusinessResultData ProducerBusinessResultData {get; set;}
	
    public class ProducerBusinessWithGa {
        public String AbcProductCode {get; set;}
        /*public String AgentCoverageEffectiveDate {get; set;}
        public String AgentCoverageTermDate {get; set;}*/
        public String AorName {get; set;}
        public String AorSan {get; set;}
        /*public String AorSplitPercent {get; set;}
        public String ApplicationId {get; set;}
       	public String BenSequenceNumber {get; set;}*/
        public String BlockOfBusiness {get; set;}
        /*public String CiCustomerNumber {get; set;}
        public String ContractCode {get; set;}
        public String CoverageEffectiveDate {get; set;}
        public String CoverageTermDate {get; set;}
        public String ExchangeInd {get; set;}
        public String FmClientId {get; set;}
        public String FmContractNumber {get; set;}
        public String GroupName {get; set;}
        public String GroupNumber {get; set;}
        public String HpsCaseId {get; set;}*/
        public String MemberId {get; set;}
        public String MemberName {get; set;}
        public String PID {get; set;}
        //public String PlanCode {get; set;}
        public String Platform {get; set;}
        /*public String PolicyNumber {get; set;}
        public String PrimaryAorIndicator {get; set;}
        public String ProducerGroupNumber {get; set;}
        public String ProductLine {get; set;}
        public String State {get; set;}
        public String UMID {get; set;}*/
        public String WaName {get; set;}
        public String WaSan {get; set;}
        /*public String WaSplitPercent {get; set;}
        public String GaId {get; set;}
        public String GaName {get; set;}
        public String GaSan {get; set;}*/
    }
    
    public class ProducerBusinessResultData {
        public ProducerBusinessWithGa ProducerBusinessWithGa {get; set;}
    }
}