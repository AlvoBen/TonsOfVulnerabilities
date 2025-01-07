/******************************************************************************************************************
Apex class Name    : PharmacyRXClaimsSummary_C_HUM
Version            : 1.0
Function           : This Class contains methods for services to fetch Pharmacy Rx Claims Summary information.
Created On         : July 14 2020
Test Class         : PharmacyRXClaimsSummary_T_HUM
*******************************************************************************************************************
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                      07/14/2020                 Original Version - REQ - 891651 -- PR00094920- MF 1- Rx Claims- Pharmacy (Rx) Claims Summary page (RxXP) 
* Pinky Vijur                                  05/11/2021                  REQ 2230065: T1PRJ0002606 -2021 MF1- SF - Humana Constants Cleanup Humana Pharmacy
*******************************************************************************************************************/
public with sharing class PharmacyRXClaimsSummary_C_HUM {

    public String sRecId {get;set;}
    public String sMemName{get;set;} 
    public String spolicyStartDate{get;set;} 
    public String spolicyEndDate{get;set;} 
    public string sMemberGenKey{get;set;}
    public string sAttachKey{get;set;}
    public Boolean bdisableButton {Get; Set;}
    Public boolean claimDetailSwitchOnOff{Get;Set;}
    List<Policy_Member__c> lstPolicyMem;
    Public Static final String ID_HUM = 'id';
    
    /**
     *In Constructor fetching value for MemberGenKey,MemberName,Policy start and End Date
    */ 
    public PharmacyRXClaimsSummary_C_HUM(){
        sRecId = apexpages.currentpage().getparameters().get(ID_HUM).escapeHTML4();
        lstPolicyMem = getMemberNameAndGenKey(sRecId);
        if(lstPolicyMem != Null && !lstPolicyMem.isEmpty())    
        {
            if(lstPolicyMem[0].Member__r !=null){
                sMemName = lstPolicyMem[0].Member__r.name;
            	sMemberGenKey = lstPolicyMem[0].Member__r.Mbr_Gen_Key__c ; 
                sAttachKey = lstPolicyMem[0].Member__r.Enterprise_ID__c;
            }			
            spolicyStartDate = lstPolicyMem[0].Effective_Date__c;
			spolicyEndDate = lstPolicyMem[0].End_Date__c;
            bdisableButton = false;
            bdisableButton = lstPolicyMem[0].ETL_Record_Deleted__c;
        }
        //Claim Detail Functionality Switch -- Start
        claimDetailSwitchOnOff = HUMUtilityHelper.isCRMFunctionalityON('1151554');
        
        //Claim Detail Functionality Switch -- End
    }
    
    @AuraEnabled(cacheable=true)
    public static boolean accordionSwitchValue(){
        boolean AccordionSwitchOnOff = false;
        AccordionSwitchOnOff = HUMUtilityHelper.isCRMFunctionalityON('1285346');
        System.debug('AccordionSwitchOnOff@@'+AccordionSwitchOnOff);
        return AccordionSwitchOnOff;
    }
    /**
     * Returns list of policymember to set value 
     * @param    PolicyMemberId
     * @return  List<[olicy_Member__c >
     */ 
    @testvisible
    private List<Policy_Member__c> getMemberNameAndGenKey(ID sRecId )
    {
        List<Policy_Member__c> lstPolicyMem = new List<Policy_Member__c>();
        try
        { 
            lstPolicyMem = [SELECT Id, Member__r.name, Member__r.Enterprise_ID__c, Member__r.Mbr_Gen_Key__c, Effective_Date__c, End_Date__c,createdDate,ETL_Record_Deleted__c FROM POLICY_Member__C WHERE ID=:sRecId order by createdDate desc];
            if(lstPolicyMem != Null && !lstPolicyMem.isEmpty())    
            {
                return lstPolicyMem;
            }
            return NULL;
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'PharmacyRXClaimsSummary_C_HUM', 'getMemberNameAndGenKey');
            return NULL;
        }
    }
        
	/**
     * Returns request object of continuation object     
     * Method to invoke the GetClaims to show claim summary data
     * @parameter memberGenKey, policyStartDate, policyEndDate     
     * @return continuation object
     */
    @AuraEnabled(continuation=true)
    Public static object GetClaims(string memberGenKey,string policyStartDate, string policyEndDate){
        return PharmacyRXClaimsSummary_S_HUM.invokePharmacyClaimSummary(memberGenKey,policyStartDate,policyEndDate);    
    }
    
    /**
     * Returns responses of continuation object    
     * Callback Method to parse the getClaims service response
     * @parameter labels, object   
     * @return object
     */
    @AuraEnabled
    Public Static Object processPharmacyRXClaimSummaryResponse(List <String> labels, Object State)
    {
        return PharmacyRXClaimsSummary_S_HUM.processPharmacyRXClaimSummaryResponse(labels, State);
    }    
  
}