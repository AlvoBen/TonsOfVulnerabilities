/*******************************************************************************************************************************
Apex Class Name : BenefitsVerificationRedirect_C_HUM.cls
Version         : 1.1
Created On      : 2021
Function        : Class contains methods for redirecting to the Benefits Verification (Pilot) and passing parameters.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sayali Gavane                            				10/16/2020                   Original Version
* Nirmal Garg                                           04/14/2021                  Created new version
* Nirmal Garg											04/14/2021					US-1966394 - T1PRJ0064724 - MF 1 - Adding data elements to Benefits Verification (Pilot) link to pass over to DEBUT tool 
* Samruddhi Rathi 										05/21/2021                 US - 2225964 - T1PRJ0064724 - DEBUT Plan ID for corresponding policy member detail 
*****************************************************************************************************************************************/
public with sharing class BenefitsVerificationRedirectV1_C_HUM
{
    
    public String memberID {get;set;}
    public String sCaseId{get;set;}
    public string sInteractionId{get;set;}
    public String sBaseURL{get;set;}
    public String benVerifUrl = null;
    public string sTabURL{get;set;}
    public string sDecodedTabUrl{get;set;}
    public boolean bPlanIdSwitch {get;set;}
    static final string maxdate = '12/31/9999';

    
    
    public BenefitsVerificationRedirectV1_C_HUM() 
    {
        memberID = Apexpages.currentPage().getParameters().get('Id'); 	
        bPlanIdSwitch = HUMUtilityHelper.isCRMFunctionalityON('2225964');
    }   
    public void decodeTabLink()
    {
        if(sTabURL != '' && null != sTabURL)
            sDecodedTabUrl = EncodingUtil.urlDecode(sTabURL, 'UTF-8');
    }
    public PageReference generateURL(){
        benVerifUrl = Endpoint_Url_HUM__c.getInstance('Benefits_Verification_URL').Remote_Url__c; 
        sBaseURL= benVerifUrl;      
        if(memberID !=null)
        {
            
            
            List<Policy_member__c> lstPolMem= new List<Policy_member__c>();          
            lstPolMem = [SELECT  Member__r.Enterprise_ID__c ,  Policy__r.Medicare_Segment_ID__c, Effective_Date__c, End_Date__c, Policy__r.Contract_Number__c,Policy__r.PBP_Code__c, Policy__r.Coverage_Plan_Effective_Date__c , Policy__r.Coverage_Plan_end_date__c FROM Policy_Member__c where Id=:memberID ];
            if(lstPolMem != null && lstPolMem[0].Member__r.Enterprise_ID__c !=null)
            {
                
                String enterpriseID = lstPolMem[0].Member__r.Enterprise_ID__c;
                Blob blobEnterpriseId = Blob.valueOf(enterpriseID);
                enterpriseID = EncodingUtil.base64Encode(blobEnterpriseId);
                sBaseURL += 'enterpriseId='+enterpriseID;
                if (bPlanIdSwitch == true)
                {  
                    if(lstPolMem[0].Policy__r.Contract_Number__c!= null && lstPolMem[0].Policy__r.PBP_Code__c !=null && lstPolMem[0].Policy__r.Medicare_Segment_ID__c!=null){
                       sBaseURL += '&planId=' + (lstPolMem[0].Policy__r.Contract_Number__c) + '-' + (lstPolMem[0].Policy__r.PBP_Code__c) + '-' + (lstPolMem[0].Policy__r.Medicare_Segment_ID__c);
                    }
                    

                    sBaseURL += (lstPolMem[0].Effective_Date__c!=null) ? '&coverageEffectiveDate=' + DateChangeFormat(lstPolMem[0].Effective_Date__c) :
                    '&coverageEffectiveDate=';

                    sBaseURL += (lstPolMem[0].End_Date__c!=null) ? '&coverageEndDate=' + DateChangeFormat(lstPolMem[0].End_Date__c):
                    '&coverageEndDate=';
                }
            }   
        }
        if(sCaseId != '' && null != sCaseId)
        {
            List<Case> lstCases = [select id, casenumber from case where id =: sCaseId limit 1];
            if(!lstCases.isEmpty() && lstCases.size() > 0 && null != lstCases)
            {
                sBaseURL = sBaseURL + '&case=' + lstCases[0].CaseNumber;
            }
        }
        else
        {
            if(sInteractionId != '' && null != sInteractionId)
            {
                List<Case_Interaction__c> lstCaseInteractions = [SELECT Id, Case__r.CaseNumber FROM Case_Interaction__c where Interaction__c = :sInteractionId limit 1 ];
                if(!lstCaseInteractions.isEmpty() && lstCaseInteractions.size() > 0 && null != lstCaseInteractions)
                {
                    sBaseURL = sBaseURL + '&case=' + lstCaseInteractions[0].Case__r.CaseNumber;
                }   
            }
        }
        if(sInteractionId != '' && null != sInteractionId)
        {
            List<Interaction__c> lstInteractions = [select id, Name from interaction__c where id =: sInteractionId limit 1];
            if(!lstInteractions.isEmpty() && lstInteractions.size() > 0)
            {
                sBaseURL = sBaseURL + '&interactionId=' + lstInteractions[0].Name;
            }
        }
        return null;
    }
    public PageReference redirectBenefitsVerification()
    {      
        PageReference returnPage = null;
        benVerifUrl = Endpoint_Url_HUM__c.getInstance('Benefits_Verification_URL').Remote_Url__c;		
        if(memberID !=null)
        {
            List<Policy_member__c> lstPolMem= new List<Policy_member__c>();          
            lstPolMem = [SELECT  Member__r.Enterprise_ID__c FROM Policy_Member__c where Id=:memberID ];
            if(lstPolMem != null && lstPolMem[0].Member__r.Enterprise_ID__c !=null)
            {
                String enterpriseID = lstPolMem[0].Member__r.Enterprise_ID__c;		
                Blob blobEnterpriseId = Blob.valueOf(enterpriseID);
                enterpriseID = EncodingUtil.base64Encode(blobEnterpriseId);
                sBaseURL= benVerifUrl+'enterpriseId='+enterpriseID ;    
            }
            else
            {
                sBaseURL= benVerifUrl;    
            }		
            
        }
        if(sCaseId != '' && null != sCaseId)
        {
            List<Case> lstCases = [select id, casenumber from case where id =: sCaseId limit 1];
            if(!lstCases.isEmpty() && lstCases.size() > 0 && null != lstCases)
            {
                sBaseURL = sBaseURL + '&Case=' + lstCases[0].CaseNumber;
            }
        }
        else
        {
            if(sInteractionId != '' && null != sInteractionId)
            {
                List<Case_Interaction__c> lstCaseInteractions = [SELECT Id, Case__r.CaseNumber FROM Case_Interaction__c where Interaction__c = :sInteractionId limit 1 ];
                if(!lstCaseInteractions.isEmpty() && lstCaseInteractions.size() > 0 && null != lstCaseInteractions)
                {
                    sBaseURL = sBaseURL + '&Case=' + lstCaseInteractions[0].Case__r.CaseNumber;
                }   
            }
        }
        if(sInteractionId != '' && null != sInteractionId)
        {
            List<Interaction__c> lstInteractions = [select id, Name from interaction__c where id =: sInteractionId limit 1];
            if(!lstInteractions.isEmpty() && lstInteractions.size() > 0)
            {
                sBaseURL = sBaseURL + '&interactionId=' + lstInteractions[0].Name;
            }
        }
        returnPage = new PageReference(sBaseURL);
        returnPage.setRedirect(true); 
        return returnPage;
    }
    
    public string DateChangeFormat (string CoverageDate)
    {
        if(!string.isBlank(CoverageDate))
        {
            if(CoverageDate.contains('/'))
            {
                try{
                    if(CoverageDate.trim().equals(maxdate))
                    {
                        string[] covdate = CoverageDate.trim().split('/');
                        return (string.isNotBlank(covdate[2]) ? covdate[2] : '') + '-' +
                            (string.isNotBlank(covdate[0]) ? covdate[0] : '')+ '-' + (string.isNotBlank(covdate[1]) ? covdate[1] : '') ;
                    }
                    else
                    {
                        Date d = Date.parse(CoverageDate);
                    	return Datetime.newInstance(d.year(), d.month(), d.day()).format('yyyy-MM-dd');
                    }
                    
                }
                catch(Exception ex){
                    return '';
                }
            }
            return '';
        }
        return '';  
    }

}