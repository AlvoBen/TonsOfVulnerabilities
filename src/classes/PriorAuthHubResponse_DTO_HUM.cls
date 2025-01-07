/******************************************************************************************************************
Apex class Name    : PriorAuthHubResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for PriorAuthHub
Created On         : May 30 ,2017
Test Class         : PriorAuthHubResponse_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
*-------------------------------------------------------------------------------------------------------------------------------
*   1.0             Sumit Pharande                                30/05/2017            Original Version
*   1.1			    Melkisan Selvaraj	             224067	      04/27/2018	    REQ-335128 Added Event Type and Outcome Verbiage
* 	1.2				Nirmal Garg								      10/14/2021		US26489489 - Adding new fields
* 	1.3				Tharun Madishetti							  31/03/2023		US:4250871 - Adding new 7 fields
------------------------------------------------------------------------------------------------------------------------------*/

public with sharing class PriorAuthHubResponse_DTO_HUM
{
    public GetPriorAuthStatusHistoryResponse GetPriorAuthStatusHistoryResponse;
    public class GetPriorAuthStatusHistoryResponse
    {
        public List<PriorAuthDetail> PriorAuthDetails ;
    }
    
    public class PriorAuthDetail 
    {
        public List<ArgusAuthDetail> ArgusAuthDetails;
        public  List<AgadiaAuthDetail> AgadiaAuthDetails;
    }
    
   public class ArgusAuth_Detail
    {
    List<ArgusAuthDetail> ArgusAuthDetail;
    }
    public class ArgusAuthDetail
    {
       public string AuthDescription ='';
       public string AuthAddDate='';
       public string AuthDecisionDate='';
       public string DrugName='';
    }
    
   public class AgadiaAuth_Detail
    {
    List<AgadiaAuthDetail> AgadiaAuthDetail;
    }
    
    
    public class DrugDetail
    {
        public string Quantity = '';
        public string DrugDaysSupply = '';
        public string DepartmentCode = '';
        public string ProductName = '';
        public string CoverageStartDate = '';
        public string CoverageEndDate = '';
    }

    public class Attachment
    {
        public string Description = '';
    }
    
    public class AgadiaAuthDetail
    {
       public string AuthDescription='';
       public string EOCCreationDate='';
       public string EOCDecisionDate='';
       public string DrugName='';
       public string HCSEventType='';
       public string DenialLanguage='';
       public string DecisionRoute='';
       public string PrescriberFirstName = '';
       public string PrescriberLastName = '';
       public string PrescriberFaxNumber = '';
       public string Urgency = '';
       public string LineOfBusiness = '';
       public List<DrugDetail> DrugDetails;
       public List<Attachment> Attachment;
    }
   
}