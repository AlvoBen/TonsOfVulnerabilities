/*******************************************************************************************************************************
Apex Class Name :  UnifiedSearch_C_HUM.cls
Version: 1.0
Created On      : 01/12/2021 
Function        : Class is used As base Class For Unified Search Component

Modification Log : 
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Yogesh Gupta                                    01/12/2021                  Original Version
* Sayali Gavane                                   01/29/2021                  REQ -1801186 PR00094010 - MVP MF 6 - Humana Pharmacy Tab page-Strides
* Nirmal Garg									  07/27/2021				
* Nirmal Garg									  08/12/2021				  DF-3554 fix
* Nirmal Garg									  09/16/2021					Added browser logic for coveo
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
*****************************************************************************************************************************************/

Public With sharing Class UnifiedSearch_C_HUM{

    Public String sPolicyMemberId{get;set;}
    Public String sPageNameToGet{get;set;}
    Public String sPageName{get;set;}
   
  
    Public String sUnifiedSearchJSON{get;set;}    
 
    Public Boolean bCoveoSearchUser{get;set;}
    Public String sMemberLastName{get;set;}
    Public String sMemberFirstName{get;set;}
    Public String sTabURL{get;set;}
    Public String sDecodedTabUrl{get;set;}
    Public String sCaseId{get;set;}
    Public String sInteractionId{get;set;}
    Public String sCoveoMessage{get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
    public List<string> lstMajorLOB = New List<string>{'MEF','MEP','MER','MES','MGF','MGO','MGP','MGR','MGS','MPD','MRO','MRP','MSL','MSP','RSK','SUP'};
    public Boolean bCustomMargin{get;set;}
    public String sCustomMarginVal{get;set;}
    public string sMargin{get;set;}

   /**
    * @Description : Constructor for controller
    * @ Param      :
    * @ Return     
    */ 
    Public UnifiedSearch_C_HUM(){
        sPolicyMemberId =  '';
        sPageName = '';
     	//bCoveoSearchUser = UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
     	string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoSearchUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    } 
    public string getmargin(){
       return  bCustomMargin != null && bCustomMargin ? 'margin-top:' + sCustomMarginVal+'; !Important' : 'margin-top:10px; !Important';
       
    }

    /**
     * @Descriptim : Method for creating coveop message
     * @Param : 
     * @Return  
     */
    Public PageReference createCoveoMessage()
    {
        UnifiedSearchDTO_HUM.InteractionDetails_DTO oInt = New UnifiedSearchDTO_HUM.InteractionDetails_DTO();
        UnifiedSearchDTO_HUM.Context_DTO oContext  = New UnifiedSearchDTO_HUM.Context_DTO();
        UnifiedSearchDTO_HUM oWrap = New UnifiedSearchDTO_HUM();
		List<policy_Member__c> lstMemId = new List<policy_Member__c>();
        if(sPageName.equalsIgnoreCase('Dual Eligible Details')){
            if(sPolicyMemberId.contains('ent')){
                lstMemId = [SELECT Id, Name,  Policy__r.Major_LOB__c, Product__c ,
                                           Policy__r.Product__r.ProductCode,
                                             member__r.name , Policy__r.Source_Market__c
                                            FROM policy_Member__c
                                            where member__r.enterprise_id__c =: sPolicyMemberId.split('-')[1]  AND
                                                policy__r.major_lob__c in :lstMajorLOB limit 1];
            }
            else{
                lstMemId =[SELECT Id, Name,  Policy__r.Major_LOB__c, Product__c ,
                                           Policy__r.Product__r.ProductCode,
                                             member__r.name , Policy__r.Source_Market__c
                                            FROM policy_Member__c
                                             WHERE Id =: sPolicyMemberId LIMIT 1];
            }
           
        }else{
             lstMemId =[SELECT Id, Name,  Policy__r.Major_LOB__c, Product__c ,
                                           Policy__r.Product__r.ProductCode,
                                             member__r.name , Policy__r.Source_Market__c
                                            FROM policy_Member__c
                                             WHERE Id =: sPolicyMemberId LIMIT 1];
        }
        if (lstMemId!= null && lstMemId.size() > 0)
        {
            if (String.isNotBlank(sInteractionId))
            {
                /*List<Interaction__c> lstInteractions = [select id, Name,Interacting_With_Type__c, Interacting_With__r.Name
                                                        from interaction__c where id =: sInteractionId limit 1];
                If (lstInteractions!= null && lstInteractions.size() > 0)
                {
                    oInt.intWithType = string.isNotBlank(lstInteractions[0].Interacting_With_Type__c)?lstInteractions[0].Interacting_With_Type__c:'' ;
                    oInt.intWith = string.isNotBlank(lstInteractions[0].Interacting_With__r.Name)?lstInteractions[0].Interacting_With__r.Name'' ;
                    oContext.IntrtWithType = string.isNotBlank(lstInteractions[0].Interacting_With_Type__c)?lstInteractions[0].Interacting_With_Type__c'' ;
                    oContext.IntrNumber = string.isNotBlank(lstInteractions[0].Name)?lstInteractions[0].Name'' ;
                }*/
                
                List<Interaction_Member__c> lstIntMembers = [SELECT Id, Interaction__r.Name, Interacting_About__r.Name, Interaction__r.Interacting_With_Type__C, 
                                                             Interacting_About_Type__c, 
                                                             interaction__r.interacting_with__c FROM Interaction_Member__c where 
                                                             interaction__c =:sInteractionId order by createddate desc
                                                            limit 1];
                if (lstIntMembers!= null && lstIntMembers.size() > 0)
                {
                    oInt.intWithType = string.isNotBlank(lstIntMembers[0].Interaction__r.Interacting_With_Type__C)?lstIntMembers[0].Interaction__r.Interacting_With_Type__C:'' ;
                    oInt.intWith = string.isNotBlank(lstIntMembers[0].interaction__r.interacting_with__c)?getaccountname(lstIntMembers[0].interaction__r.interacting_with__c):'' ;
                    oContext.IntrtWithType = string.isNotBlank(lstIntMembers[0].Interaction__r.Interacting_With_Type__C)?lstIntMembers[0].Interaction__r.Interacting_With_Type__C:'' ;
                    oContext.IntrNumber = string.isNotBlank(lstIntMembers[0].Interaction__r.Name)?lstIntMembers[0].Interaction__r.Name:'' ;
                    oContext.IntrtAbtType = string.isNotBlank(lstIntMembers[0].Interacting_About_Type__c) ? lstIntMembers[0].Interacting_About_Type__c : '';
                    oInt.CaseNumber = '';
                }
                else{
                    oInt.intWithType = '';
                    oInt.intWith = '';
                    oContext.IntrtWithType = '';
                    oContext.IntrNumber = '';
                	oInt.CaseNumber = '';
                	oContext.IntrtAbtType = '';
                }
            }
            else{
                	oInt.intWithType = '';
                    oInt.intWith = '';
                    oContext.IntrtWithType = '';
                    oContext.IntrNumber = '';
                	oInt.CaseNumber = '';
                	oContext.IntrtAbtType = '';
            }
            /*if(string.isNotBlank(sCaseId))
            {
                List<Case> lstCases = [select id, casenumber,Interacting_About_Type__c, Account.Name from case where id =: sCaseId limit 1];
                If (!lstCases.isEmpty() && lstCases.size() > 0 && null!= lstCases)
                {
                    oInt.CaseNumber = string.isNotBlank(lstCases[0].casenumber)?lstCases[0].casenumber:'';
                    oContext.CaseNumber = string.isNotBlank(lstCases[0].casenumber)?lstCases[0].casenumber'';
                    oContext.IntrtAbtType = string.isNotBlank(lstCases[0].Interacting_About_Type__c)?lstCases[0].Interacting_About_Type__c'';
                    oWrap.coveoTabName = String.isNotBlank(lstCases[0].Account.Name) ? lstCases[0].Account.Name  '';
                }
            }
            Else{
                If (String.isNotBlank(sInteractionId))
                {
                    List<Case_Interaction__c> lstCaseInteractions = [SELECT Id, Case__r.CaseNumber, Case__r.Interacting_About_Type__c, Case__r.Account.Name
                                                                     FROM Case_Interaction__c where Interaction__c = :sInteractionId limit 1 ];
                    If (!lstCaseInteractions.isEmpty() && lstCaseInteractions.size() > 0 && null!= lstCaseInteractions)
                    {
                        oInt.CaseNumber = string.isNotBlank(lstCaseInteractions[0].Case__r.CaseNumber)?
                            lstCaseInteractions[0].Case__r.CaseNumber:'';
                        oContext.CaseNumber = string.isNotBlank(lstCaseInteractions[0].Case__r.CaseNumber)?
                            lstCaseInteractions[0].Case__r.CaseNumber:'';
                        oContext.IntrtAbtType = string.isNotBlank(lstCaseInteractions[0].Case__r.Interacting_About_Type__c)?
                            lstCaseInteractions[0].Case__r.Interacting_About_Type__c:'';
                        oWrap.coveoTabName = String.isNotBlank(lstCaseInteractions[0].Case__r.Account.Name) ? 
                            lstCaseInteractions[0].Case__r.Account.Name : '';
                    } 
                }
            }*/
            oContext.CRMPageName = sPageName;
            oContext.MajorLOB = string.isNotBlank(lstMemId[0].Policy__r.Major_LOB__c) ? lstMemId[0].Policy__r.Major_LOB__c : '';
            oContext.MinorLOB = string.isNotBlank(lstMemId[0].Policy__r.Product__r.ProductCode) ? lstMemId[0].Policy__r.Product__r.ProductCode:  '';
            oContext.Market = string.isNotBlank(lstMemId[0].Policy__r.Source_Market__c) ? lstMemId[0].Policy__r.Source_Market__c  :'';
            oWrap.coveoTabName = string.isNotBlank(oWrap.coveoTabName) ? oWrap.coveoTabName : lstMemId[0].Member__r.Name;
            oContext.CaseNumber = '';
        }
        oWrap.searchParams = '';
        oWrap.InteractionDetails = oInt;
        oWrap.context = oContext;
        sCoveoMessage = JSON.serialize(oWrap);
        Return null;
    }

    /**
     * @Description : Method for decoding tab link
     * @Param : 
     * @Return  
     */
    Public void decodeTabLink()
    {
        if (sTabURL!= '' && null != sTabURL)
        sDecodedTabUrl = EncodingUtil.urlDecode(sTabURL, 'UTF-8');
    }
    
    Private String getaccountname(String accId)
    {
        List<Account> lstAccounts = [select Id, Name from Account where Id=: accId limit 1];
        if (lstAccounts!= null && lstAccounts.size() > 0)
        {
            Return lstAccounts[0].Name;
        }
        else{
            Return '';
        }
    }
    
}