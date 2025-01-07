/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsSummary_D_HUM
Version            : 1.0
Function           : This Class contains methods for services to fetch Pharmacy Rx Claims Summary information.
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
*******************************************************************************************************************/
public with sharing class PharmacyRXClaimsSummary_D_HUM {
    
    /**
     * Returns request object of continuation object     
     * Method to invoke the requestStructurePharmacyRXClaimSummary to show claim summary data
     * @parameter PharmacyRXClaimsSummary_Request_DTO_HUM     
     * @return continuation object
     */
    @AuraEnabled
    public static PharmacyRXClaimsSummary_Request_DTO_HUM requestStructurePharmacyRXClaimSummary(string memberGenKey,string policyStartDate, string policyEndDate)
    {
        try{
            PharmacyRXClaimsSummary_Request_DTO_HUM requestBody = new PharmacyRXClaimsSummary_Request_DTO_HUM();
            PharmacyRXClaimsSummary_Request_DTO_HUM.MemberIdentifier memberIdentifier = new PharmacyRXClaimsSummary_Request_DTO_HUM.MemberIdentifier();
            memberIdentifier.Key = 'MemberGenKey';
            memberIdentifier.Value = memberGenKey;
            List<PharmacyRXClaimsSummary_Request_DTO_HUM.MemberIdentifier> memberIdentifierList = new List<PharmacyRXClaimsSummary_Request_DTO_HUM.MemberIdentifier>();
            memberIdentifierList.add(memberIdentifier);
            requestBody.MemberIdentifier = memberIdentifierList;
            requestBody.Requestor='Salesforce-CRM';
            requestBody.FromDate= policyStartDate;
            requestBody.ToDate= '';
            requestBody.CoverageType=0;
            requestBody.ClaimTypes= new List<Integer>{1};        
            requestBody.ActivityDetails=true;
            requestBody.ArgusDetails= true;
            requestBody.DrugDetails= true;
            requestBody.PharmacyDetails= true;
            requestBody.ErrorDetails=true;
            requestBody.PrescriberDetails= true;
            requestBody.AmountDetails=true;
            requestBody.OtherDetails= true;
            requestBody.PatientDetails=true;
            requestBody.BSQDetails= true;
            requestBody.PageSize=1;
            requestBody.PageNumber=0;
            
            return requestBody; 
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXClaimsSummary_D_HUM', 'requestStructurePharmacyRXClaimSummary');
            return null;
        }
        
    }   
    
    /**
     * Returns response object of continuation object     
     * Method to invoke the responseStructurePharmacyRXClaimSummary to show claim summary data
     * @parameter List<PharmacyRXClaimsSummary_wrapper_HUM>     
     * @return continuation object
     */
    @AuraEnabled
    public static List<PharmacyRXClaimsSummary_wrapper_HUM> responseStructurePharmacyRXClaimSummary(string claimSummaryResponse){
        try
        {
            List<PharmacyRXClaimsSummary_Response_DTO_HUM>  pharmacyRXClaimSummaryDTOList;
            PharmacyRXClaimsSummary_Response_DTO_HUM  pharmacyRXClaimSummaryDTO; 
            List<PharmacyRXClaimsSummary_wrapper_HUM> wrapperObjList = new List<PharmacyRXClaimsSummary_wrapper_HUM>() ;
            PharmacyRXClaimsSummary_wrapper_HUM  wrapperObj;
            pharmacyRXClaimSummaryDTOList = (List<PharmacyRXClaimsSummary_Response_DTO_HUM>) System.JSON.deserialize(claimSummaryResponse,List<PharmacyRXClaimsSummary_Response_DTO_HUM>.class);
            
            if (pharmacyRXClaimSummaryDTOList != null )
            {
                for(PharmacyRXClaimsSummary_Response_DTO_HUM obj : pharmacyRXClaimSummaryDTOList ){
                    
                    wrapperObj = new PharmacyRXClaimsSummary_wrapper_HUM();
					//for FillTime and FillDate --  need to change mapping value from ActualProcessDate to DateOfService
                    if(string.isNotBlank(obj.ActualProcessDate)){
                        wrapperObj.FillTime = fetchTime(obj.ActualProcessDate);
                        wrapperObj.FillDate = fetchDate(obj.ActualProcessDate);
                    }
                    if (string.isNotBlank(obj.ClaimId)) wrapperObj.ClaimID  = obj.ClaimId;
                    if (string.isNotBlank(obj.RxNumber)) wrapperObj.RxNumber  = obj.RxNumber; 
                    if(obj.RxClaimDrug !=null){
                        if (string.isNotBlank(obj.RxClaimDrug.DrugName)) wrapperObj.DrugName  = obj.RxClaimDrug.DrugName;
                        if (string.isNotBlank(obj.RxClaimDrug.GCN)) wrapperObj.GCN  = obj.RxClaimDrug.GCN;
                    }
                    if (obj.RxQuantity!=null) wrapperObj.Quantity  = string.valueOf(obj.RxQuantity);
                    if (string.isNotBlank(obj.RxDaySupply)) wrapperObj.DaysSupply = obj.RxDaySupply;
                    if (string.isNotBlank(obj.ClaimStatus)) wrapperObj.ClaimStatus  = obj.ClaimStatus;  
                    if(obj.RxClaimPatient !=null){
                        if (string.isNotBlank(obj.RxClaimPatient.ClientId)) wrapperObj.ClientID  = obj.RxClaimPatient.ClientId; 
                        //for Client --  need mapping value
                        if (string.isNotBlank(obj.RxClaimPatient.ClientId)) wrapperObj.ClientValue  = obj.RxClaimPatient.ClientId; 
                        if (string.isNotBlank(obj.RxClaimPatient.CustomerId)) wrapperObj.CustomerValue  = obj.RxClaimPatient.CustomerId; 
                    }
                    if(obj.RxClaimPharmacy !=null){
                        if (string.isNotBlank(obj.RxClaimPharmacy.PharmacyName)) wrapperObj.PharmacyName  = obj.RxClaimPharmacy.PharmacyName; 
                    }
                    
                    if (string.isNotBlank(obj.AuthorizationNumber)) wrapperObj.AuthorizationNumber  = obj.AuthorizationNumber;                       
                    if (string.isNotBlank(obj.ClaimAdjudicationDate)) wrapperObj.DateUpdated  = obj.ClaimAdjudicationDate;
                    if (obj.MemberResponsibleAmount !=null) wrapperObj.PatientPaid  = obj.MemberResponsibleAmount;
                    if (string.isNotBlank(obj.NDC)) {
                        String NDC = '';
                        List<String> NDCArray = obj.NDC.split('');
                        if(NDCArray.size() == 11){
                            NDCArray.add(5,'-');
                            NDCArray.add(10,'-');
                            for(String s : NDCArray){
                                NDC+=s;
                            }
                        }
                        if(NDC != ''){
                            wrapperObj.NDC  = NDC;
                        }else{
                            wrapperObj.NDC  = obj.NDC;
                        }
                    }              					
                    if (string.isNotBlank(obj.ClaimSource)) wrapperObj.Source  = obj.ClaimSource;
                    if (string.isNotBlank(obj.PriorAuthorizationNumberSubmitted)) wrapperObj.PreAuthorizationNumber  = obj.PriorAuthorizationNumberSubmitted;
                    
                    wrapperObjList.add(wrapperObj);	
                }	
            }
            return wrapperObjList;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXClaimsSummary_D_HUM', 'responseStructurePharmacyRXClaimSummary');
            return null;
        }
    }
    
    
    public static string fetchTime(string ActualProcessDate){
        string fillTime = ActualProcessDate.substring(11); 
        return fillTime;
    }    
    
    public static string fetchDate(string ActualProcessDate){
        string fillDate = ActualProcessDate.substring(0,10);  
        return fillDate;            
    }
}