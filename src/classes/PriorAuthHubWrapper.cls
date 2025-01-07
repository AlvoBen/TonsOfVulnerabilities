/*******************************************************************************************************************************
Apex Class Name : PriorAuthHubWrapper.cls
Version         : 1.0
Created On      : 20th June 2017
Function        : Wrapper class for PriorHub service.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Joel George                                      06/20/2017                Original Version (REQ 305017)
* Melkisan Selvaraj	         224067		           04/24/2018		         Added EventType, DenialLanguage and DecisionRoute (REQ - 335128)
* Nirmal Garg								      10/14/2021				 US26489489 - Adding new fields
* Tharun Madishetti                                03/31/2023                US-4250871:T1PRJ0891742- CRM IOP-2022-6037203-- Pharmacy Authorization Details: add Department,Coverage Start Date & Coverage End Date(Classic)
*****************************************************************************************************************************************/
public class PriorAuthHubWrapper 
{
    
    public PriorAuthHubWrapper(String AuthDescription, String AddCreateDate, 
                               String DecisionDate, String DrugName, 
                               String HCSEventType, String DenialLanguage, String DecisionRoute)
    {
        sAuthDescription = AuthDescription;
        sAddCreateDate = AddCreateDate;
        sDecisionDate = DecisionDate;
        sDrugName = DrugName;
        sHCSEventType = HCSEventType;
        sDenialLanguage = DenialLanguage;
        sDecisionRoute = DecisionRoute;
        
    } 
    
    public PriorAuthHubWrapper(String AuthDescription, String AddCreateDate, 
                               String DecisionDate, String DrugName, 
                               String HCSEventType, String DenialLanguage, String DecisionRoute
                              ,string PrescriberFirstName, string PrescriberLastName,
                              string PrescriberFaxNumber, string Urgency, string LineOfBusiness, 
                               List<PriorAuthHubResponse_DTO_HUM.DrugDetail> drugdetails,
                               List<PriorAuthHubResponse_DTO_HUM.Attachment> Attachments)
    {
        sAuthDescription = AuthDescription;
        sAddCreateDate = AddCreateDate;
        sDecisionDate = DecisionDate;
        sDrugName = DrugName;
        sHCSEventType = HCSEventType;
        sDenialLanguage = DenialLanguage;
        sDecisionRoute = DecisionRoute;
        sMDO = (string.isNotBlank(PrescriberFirstName) ? PrescriberFirstName : '') + ' ' + (string.isNotBlank(PrescriberLastName) ? PrescriberLastName : '');
        sPhone = PrescriberFaxNumber;
        sUrgency = Urgency;
        sLineOfBusiness = LineOfBusiness;
        lstDrugDetails = drugdetails;
        lstAttachments = Attachments;
    } 
    
    public String sAuthDescription ='';
    public String sAddCreateDate = '';   
    public String sDecisionDate = '';   
    public String sDrugName = '';
    public string sHCSEventType = '';
    public String sDenialLanguage = '';
    public String sDecisionRoute = '';
	public string sMDO  = '';
    public string sPhone = '';
    public string sUrgency = '';
    public string sLineOfBusiness = '';
    public List<PriorAuthHubResponse_DTO_HUM.DrugDetail> lstDrugDetails;
    public List<PriorAuthHubResponse_DTO_HUM.Attachment> lstAttachments;
}