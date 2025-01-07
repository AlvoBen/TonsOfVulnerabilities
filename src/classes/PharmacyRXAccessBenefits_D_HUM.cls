/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_D_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This data Class contains methods for KMDocumentServiceREST services to buiild request to service and response from service
******************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
* Suraj Patil     				               05/21/2021                 DF 3050 The name of the documents not showing as expected in the Benefit grid search results in CRM
***************************************************************************************************/
public with sharing class PharmacyRXAccessBenefits_D_HUM {
    /**
	* Returns request object of continuation object     
	* Method to invoke the requestStructurePharmacyRXAccessBenefits to show claim summary data
	* @parameter policyMemberId     
	* @return PharmacyRXAccessBenefits_Request_HUM object
	*/
    @AuraEnabled
    public static PharmacyRXAccessBenefits_Request_HUM requestStructurePharmacyRXAccessBenefits(string policyMemberId)
    {
        try{
            Policy_Member__c objPolMem = getPolicyMemberData(policyMemberId);
            string sQueryText;
            if(objPolMem != null)
            {        
                List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c Where ProductCode__c = :objPolMem.Product__c And  Major_LOB__c = : objPolMem.Product_Type__c And Business_Description__c = :GLOBAL_CONSTANT_HUM.VOB_MEDICARE];
                if(objPolMem.Product__c  == GLOBAL_CONSTANT_HUM.MED && !lstRefBusinessLineProdGroups.isEmpty())
                {
                    sQueryText = objPolMem.Policy__r.Contract_Number__c + '-' + objPolMem.Policy__r.PBP_Code__c;
                    if(String.isNotBlank(objPolMem.Policy__r.Medicare_Segment_ID__c)) 
                        sQueryText += '-' + objPolMem.Policy__r.Medicare_Segment_ID__c;
                }
                else 
                {
                    if( objPolMem.Policy_Platform__c == 'EM' || objPolMem.Policy_Platform__c == 'MTV')
                    {
                        sQueryText = 'subgroup:' + objPolMem.Policy__r.Benefit_coverage__c;
                    } 
                    else if( objPolMem.Policy_Platform__c == 'LV' || objPolMem.Policy_Platform__c == 'CI' || objPolMem.Policy_Platform__c == 'CAS')
                    {
                        if(String.isNotBlank(objPolMem.Policy__r.Group_Number__c))
                        {
                            sQueryText = 'subgroup:' + objPolMem.Policy__r.Group_Number__c;
                        }
                    }
                }
            }
            PharmacyRXAccessBenefits_Request_HUM requestBody = new PharmacyRXAccessBenefits_Request_HUM();
				User oUser = [SELECT Id, Network_User_Id__c FROM User where id =: UserInfo.getUserId() limit 1];
				
                PharmacyRXAccessBenefits_Request_HUM.DoSearchRequest doSearchRequestBody = new PharmacyRXAccessBenefits_Request_HUM.DoSearchRequest();
                doSearchRequestBody.BenefitGridsOnly = true;
                doSearchRequestBody.ConsumingApp = 'CRM';
                doSearchRequestBody.DoPaging = true;
                doSearchRequestBody.MaxNumberResults = 100;
                doSearchRequestBody.Query = sQueryText;
                doSearchRequestBody.ResultsPerPage = 100;
                doSearchRequestBody.SLSessionID = String.IsNotBlank(oUser.Network_User_Id__c) ? oUser.Network_User_Id__c : '';
                doSearchRequestBody.SortOrder ='ZThruA';
                doSearchRequestBody.UserAKAName = 'aka'+oUser.Network_User_Id__c;
                
                requestBody.DoSearchRequest = doSearchRequestBody;      
            
            return requestBody; 
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXClaimsSummary_D_HUM', 'requestStructurePharmacyRXClaimSummary');
            return null;
        }
        
    } 
    
    
    public static Policy_Member__c getPolicyMemberData(string policyMemberId )
	{  
        Policy_Member__c objPolMem;
        if(String.IsNotBlank(policyMemberId))
        {
            objPolMem = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Benefit_coverage__c,
                         Policy__r.Group_Number__c,Policy__r.Medicare_Segment_ID__c 
                         From Policy_Member__c Where Id =: policyMemberId limit 1];
        }
        return objPolMem;
    }
    
    
    @AuraEnabled
    public static PharmacyRXAccessBenefits_Wrapper_HUM responseStructurePharmacyRXAccessBenefits(string accessBenefitMentorResponse){
        try{
            PharmacyRXAccessBenefits_Wrapper_HUM wrapperObj = new PharmacyRXAccessBenefits_Wrapper_HUM();
            List<PharmacyRXAccessBenefits_Wrapper_HUM.Documents> wrapperDocList = new List<PharmacyRXAccessBenefits_Wrapper_HUM.Documents>();
            PharmacyRXAccessBenefits_Wrapper_HUM.KMDSearchResultsWrapper kmdResultWrapper = new PharmacyRXAccessBenefits_Wrapper_HUM.KMDSearchResultsWrapper();
            PharmacyRXAccessBenefits_Response_HUM serviceResponse = (PharmacyRXAccessBenefits_Response_HUM) System.JSON.deserialize(accessBenefitMentorResponse, PharmacyRXAccessBenefits_Response_HUM.class);
            PharmacyRXAccessBenefits_Response_HUM.KMDSearchResults kmdResults = new PharmacyRXAccessBenefits_Response_HUM.KMDSearchResults();
            if(serviceResponse !=null){
                if(serviceResponse.KMDSearchResults !=null){
                    kmdResults = serviceResponse.KMDSearchResults;
                    if(!kmdResults.Documents.isEmpty() && kmdResults.Documents.size() > 0 ){
                        for(PharmacyRXAccessBenefits_Response_HUM.Documents doc : kmdResults.Documents){
                            PharmacyRXAccessBenefits_Wrapper_HUM.Documents wrapperDoc = new PharmacyRXAccessBenefits_Wrapper_HUM.Documents();
                            if(doc.Title!= null && doc.Title!= ''){
							doc.Title = doc.Title.replace('<span class="BoldSearchTerm0">', '');
                            doc.Title = doc.Title.replace('</span>', '');
							wrapperDoc.FileName = doc.Title;
							}
                            else wrapperDoc.FileName = 'No File Name';                            
                            
                            if(doc.FileType !=null && doc.FileType != '') wrapperDoc.FileType = doc.FileType;
                            else wrapperDoc.FileType = 'No File Type';
                            
                            if(doc.Href !=null && doc.Href !='') wrapperDoc.FileUrl = doc.Href;
                            else wrapperDoc.FileUrl = 'https://hi.humana.com';
                            wrapperDocList.add(wrapperDoc);
                        }
                        kmdResultWrapper.Documents = wrapperDocList;
                    }
                    
                    if(kmdResults.Error !=null && kmdResults.Error.ErrorCode !=null && kmdResults.Error.ErrorCode !='' && kmdResults.Error.ErrorMessage !=null && kmdResults.Error.ErrorMessage !='' )
					{
                        insertFaultExceptionToErrorLog(kmdResults.Error.ErrorCode,kmdResults.Error.ErrorMessage);
                        return null;
                    }
                } 
                 wrapperObj.KMDSearchResultsWrapper = kmdResultWrapper;
            }       
            return wrapperObj;        
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PharmacyRXAccessBenefits_D_HUM', 'responseStructurePharmacyRXAccessBenefits');
            return null;
        }
    }

    public static void insertFaultExceptionToErrorLog( String errorCode,string errorMessage){            
        Error_Log__c objErrorLog = new Error_Log__c();   
        objErrorLog.Class_Name__c = 'PharmacyRXAccessBenefits_D_HUM';
        objErrorLog.Method_Name__c = 'responseStructurePharmacyRXAccessBenefits';
        objErrorLog.Type__c ='Integration Error';
        objErrorLog.Status__c = errorCode;          
        objErrorLog.Error_Code__c = errorCode;
        objErrorLog.Error_Message__c = errorMessage;
        database.insert(objErrorLog, false);
    }  
}