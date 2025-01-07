/************************************************************************************************************************************************************************************       
Apex Class Name  : ExternalLinkLauncher_LC_Hum
Created Date     : 05/27/2022
Function         : 
**Modification Log
* Developer Name                   Date                       Description
************************************************************************************************************************************************************************************
* Kalyani Pachpol                  05/27/2022                 User Story 3150124: Benefits External link
* Kalyani Pachpol                  07/28/2022                 US - 3613352
* Kalyani Pachpol                  08/10/2022                 US- 3613352
* Aishwarya Pawar                  9/27/2022                  US- 3552709
* Aishwarya Pawar                  10/31/2022                 REQ-  3819554
* Anuradha Gajbhe                  10/18/2023                 US-5066619 - Benefit and Accumulator page- ability to pass data elements to Debut from CRM links  
* Raj Paliwal                      10/18/2023                 US-5066625 - Auth/referral page- ability to pass data elements to Debut from CRM links 
* Dimple Sharma                    10/18/2023                 US-4934019 - Plan Member page- ability to pass data elements to Debut from CRM links
************************************************************************************************************************************************************************************/
Public with sharing Class ExternalLinkLauncher_LC_Hum {

    Static final String maxdate = '12/31/4000';

    @AuraEnabled(cacheable=true)
    Public Static string generateCertificateURL(String memplanid){
        String sBaseURL ='';
        try {
            List<MemberPlan> lstMemberPlan = getMemberPlanRecord(memplanid);
            sBaseURL = HUMCertificateURL__c.getValues('CertificateURL') != NULL ? HUMCertificateURL__c.getValues('CertificateURL').CertificateListURL__c : '';
            if(lstMemberPlan != null && lstMemberPlan.size() > 0){
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_PERS_HUM + (String.isNOTBlank(lstMemberPlan[0].Member.Mbr_Gen_Key__c) ? lstMemberPlan[0].Member.Mbr_Gen_Key__c : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_PTFM_HUM + (String.isNOTBlank(lstMemberPlan[0].Plan.Platform__c) ? lstMemberPlan[0].Plan.Platform__c : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_PROD_HUM + (String.isNOTBlank(lstMemberPlan[0].Plan.Product__r.Sold_Product_Key_Value__c) ? lstMemberPlan[0].Plan.Product__r.Sold_Product_Key_Value__c : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_LOB_HUM + (String.isNOTBlank(lstMemberPlan[0].Plan.Source_Major_LOB__c) ? lstMemberPlan[0].Plan.Source_Major_LOB__c : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_SEQ_HUM + (String.isNOTBlank(lstMemberPlan[0].Plan.Product__r.Segment_Type__c) ? lstMemberPlan[0].Plan.Product__r.Segment_Type__c : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_CUST_HUM + (String.isNOTBlank(lstMemberPlan[0].Plan.Payer.enterprise_id__c) ? lstMemberPlan[0].Plan.Payer.enterprise_id__c : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_CCNBR_HUM + (String.isNOTBlank(lstMemberPlan[0].Plan.Purchaser_Plan_External_ID__c) ? lstMemberPlan[0].Plan.Purchaser_Plan_External_ID__c.split('\\|')[3] : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_SUB_HUM + (lstMemberPlan[0].SubscriberPlanId__c != null ? lstMemberPlan[0].SubscriberPlanId__r.Member.Mbr_Gen_Key__c : String.isNOTBlank(lstMemberPlan[0].Member.Mbr_Gen_Key__c) ? lstMemberPlan[0].Member.Mbr_Gen_Key__c : '');
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_ASOF_HUM + (lstMemberPlan[0].EffectiveFrom != null ? String.valueOf(lstMemberPlan[0].EffectiveFrom) : (lstMemberPlan[0].EffectiveTo != null ? String.valueOf(lstMemberPlan[0].EffectiveTo) : system.today().format()));
                sBaseURL += ExternalLinkLauncherConstant_HUM.VOB_CERTIFICATE_PORT_HUM;
            }
			if(sBaseURL.contains('HIDDENTARGET='))
		        {
		            sBaseURL = sBaseURL.subStringBefore('HIDDENTARGET=') + 'HIDDENTARGET=' + EncodingUtil.base64Encode(blob.valueof(sBaseURL.subStringAfter('HIDDENTARGET=')));
		        }
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'ExternalLinkLauncher_LC_Hum', 'generateCertificateURL');
             throw new AuraHandledException(e.getMessage());
        }
        return sBaseURL;
    }
    
	@testvisible
    @AuraEnabled(cacheable=true)
    public static string generateBenefitGridURL(string memplanid){
        String sBaseURL  ='';
        Boolean isRapidForceApp = HUMUtilityHelper.IsRapidForceAPP();
        try {
            sBaseURL = setMentorBenefitGridBaseURL(isRapidForceApp);
            List<MemberPlan> lstMemberPlan = getMemberPlanRecord(memplanid);
            if(lstMemberPlan != null && lstMemberPlan.size() > 0){
                List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
                Where ProductCode__c = :lstMemberPlan[0].Product__c And  Major_LOB__c = :lstMemberPlan[0].Product_Type__c And Business_Description__c = 'RSO- Medicare'];
                if(lstMemberPlan[0].Product__c  == 'MED' && !lstRefBusinessLineProdGroups.isEmpty())
                {
                    sBaseURL = sBaseURL + isMedicareMedicaidPolicy(lstMemberPlan[0].Policy_Platform__c, lstMemberPlan[0].Issue_State__c, lstMemberPlan[0].Plan.Contract_Number__c,
                    lstMemberPlan[0].Plan.PBP_Code__c,lstMemberPlan[0].Product_Type__c,lstMemberPlan[0].Plan.Medicare_Segment_ID__c,
                    lstMemberPlan[0].Product_Description__c);
                }
                else 
                {
                    sBaseURL = sBaseURL + isNonMedicareMedicaidPolicy(lstMemberPlan[0].Policy_Platform__c,lstMemberPlan[0].Plan.Policy_Group_Number__c,lstMemberPlan[0].Plan.Benefit_coverage__c);
                }
                if(HUMMentorToSFDCController.IsRsoUser() && isRapidForceApp){
                    sBaseURL = sBaseURL + '&filterset=' + ExternalLinkLauncherConstant_HUM.HUMCompassRSOParam;
                }
            }
            sBaseURL = hssBaseEncode(sBaseURL);
        } catch (Exception e) {
             HUMExceptionHelper.logErrors(e, 'ExternalLinkLauncher_LC_Hum', 'generateBenefitGridURL');
             throw new AuraHandledException(e.getMessage());
        }
        return sBaseURL;
    }
	
    @AuraEnabled(cacheable=true)
    public static RxDetails generateRXConnectProURL(string memplanid, String source){
        RxDetails objRxDetails = new RxDetails();
        Map<String,String> mapRxConnectPro = new Map<String,String>();
        string setRXConnectFlag;
        try {
            if(string.isNotBlank(source)){
                objRxDetails.sURL = HUMCertificateURL__c.getValues(ExternalLinkLauncherConstant_HUM.RXConnectPro_URL) != NULL ? HUMCertificateURL__c.getValues(ExternalLinkLauncherConstant_HUM.RXConnectPro_URL).CertificateListURL__c: '';
                objRxDetails.setRXConnectFlag = true;
            }else{
                objRxDetails.sURL = HUMCertificateURL__c.getValues(ExternalLinkLauncherConstant_HUM.RXLOOKUP_URL) != NULL ? HUMCertificateURL__c.getValues(ExternalLinkLauncherConstant_HUM.RXLOOKUP_URL).CertificateListURL__c: '';
                objRxDetails.setRXConnectFlag = false;
            }
            objRxDetails.HSS_ONECLICK_TARGET = ExternalLinkLauncherConstant_HUM.RXLOOKUP_TARGET;
            objRxDetails.HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance(ExternalLinkLauncherConstant_HUM.SSO_URL).Remote_Url__c;
            objRxDetails.IdForMember = getMemberIdBase(memplanid);
            return objRxDetails;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'ExternalLinkLauncher_LC_Hum', 'generateRXConnectProURL');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @testvisible
    private static string getMemberIdBase(string memplanid){
        List<MemberPlan> memplan = [SELECT Id,Name,Member_id_base__c FROM MemberPlan where Id =: memplanid];
        if( memplan != null && memplan.size() > 0){
            return memplan[0].Member_id_base__c;
        }
        return '';
    }

    @AuraEnabled(Continuation=true Cacheable=true)
    public static Continuation invokeEncryptService(String MemberId)
    {
        return ExternalLinkLauncher_LS_Hum.SendRequestForEncryption(MemberId);   
    }
    @AuraEnabled(Cacheable=true)  
    public static object ProcessEncryptService(list<String> labels, Object State)
    {
        return RxConnect_S_HUM.ProcessEncryptService(labels, State);        
    }
	
	/**
    * Method to find if the policy is non Medicare /Medicaid policy or not  
    *
    * @return  String      
    */
    @testvisible
    private static string isNonMedicareMedicaidPolicy(string sPlatform, string sGrpNumber,string sBenCoverage)
    {
        String sMentorURL = '';           
        
        if( sPlatform == ExternalLinkLauncherConstant_HUM.sEM_Value || sPlatform == ExternalLinkLauncherConstant_HUM.sMTV_Value )
        {
            sMentorURL = ExternalLinkLauncherConstant_HUM.PARAMSUBGROUP_MENTORREDIRECT_HUM + sBenCoverage + ExternalLinkLauncherConstant_HUM.CONSTANTSUBGROUP_MENTORREDIRECT_HUM ;
        }        
        else if( sPlatform == ExternalLinkLauncherConstant_HUM.sLV_Value || sPlatform == ExternalLinkLauncherConstant_HUM.sCI_Value || sPlatform == ExternalLinkLauncherConstant_HUM.sCAS_Value )
        {
            if(String.isNotBlank(sGrpNumber))
            {
                sMentorURL = ExternalLinkLauncherConstant_HUM.PARAMSUBGROUP_MENTORREDIRECT_HUM + sGrpNumber + ExternalLinkLauncherConstant_HUM.CONSTANTSUBGROUP_MENTORREDIRECT_HUM ;
            }
        } 
        else 
        {
            sMentorURL= ExternalLinkLauncherConstant_HUM.PARAMFILTER_MENTORREDIRECT_HUM ;
        }
        return sMentorURL;                                                                                  
    }
	
	/**
    * Method to find if the policy is Medicare /Medicaid policy or not  
    *
    * @return  String      
    */
    
    @testvisible
    private static string isMedicareMedicaidPolicy(string sPlatform, string sStateCode, string sCOntractNum, string sPBPCode,
    string sProductType, string sMedicaresegId, string sProductDesc)
    {
        String sMentorURL = '';
        if ( sPlatform == ExternalLinkLauncherConstant_HUM.sLV_Value && checkMCDProduct(sProductType)){          
                    String stateCode = '%22'+sStateCode + ' Medicaid Comprehensive Benefit Grid%22';

                    sMentorURL = ExternalLinkLauncherConstant_HUM.PARAMTITLE_MENTORREDIRECT_HUM + stateCode+ '&filterType=Benefit Grids&filterset=FilterSet&filters={%22Function%22:%22none%22,%22SubFunction%22:%22none%22,%22Platform%22:%22none%22,%22BusinessSegment%22:%22none%22,%22LineOfCoverage%22:%22none%22,%22Market%22:%22none%22}';                
                    
        }else{
             String sQueryText = sCOntractNum+  ExternalLinkLauncherConstant_HUM.sHiphan + sPBPCode;
            if(String.isNotBlank(sMedicaresegId)) sQueryText += ExternalLinkLauncherConstant_HUM.sHiphan +  sMedicaresegId;
                if(String.isNotBlank(sProductDesc) && String.isNotBlank(sProductType)
                        && ExternalLinkLauncherConstant_HUM.MAJOR_LOB_MES == sProductType) {
                            sQueryText = sProductDesc;
                }
        
            sMentorURL = ExternalLinkLauncherConstant_HUM.PARAMTITLE_MENTORREDIRECT_HUM + sQueryText + ExternalLinkLauncherConstant_HUM.CONSTANTPARAMEND_MENTORREDIRECT_HUM;
        }
        return sMentorURL;
    } 
	
	@testvisible
    private static boolean checkMCDProduct(String sProductType){
        if(sProductType != Null && sProductType == 'MCD'){
            return true;
        }
        return false;
    }
    
	@AuraEnabled(cacheable=true)
    public static string generateBenefitMatrixURL(string planid){
        try {
             String sBaseURL; 
            if(String.isNotBlank(planid)){
                List<MemberPlan> lstMemberPlan = getMemberPlanRecord(planid);
                boolean bCRMGBORFAccess = false;                                                            
                bCRMGBORFAccess =  UtilityHelper_H_AHT_HUM.ischeckCustomPermissionAccess();
                if(bCRMGBORFAccess == true)
                {
                    sBaseURL  = ExternalLinkLauncherConstant_HUM.VOB_TABLEOFCONENTS_URL ;  
                }
                else  
                {
                    sBaseURL  = setBenefitMatrixBaseURL(); 
                }
                if(lstMemberPlan.size() > 0){
                    if(lstMemberPlan[0].Policy_Platform__c == ExternalLinkLauncherConstant_HUM.VOB_PLATFORM_MTV_HUM || lstMemberPlan[0].Policy_Platform__c == ExternalLinkLauncherConstant_HUM.VOB_PLATFORM_EM_HUM)
                    {
                        if(bCRMGBORFAccess  ==false)
                        {
                            string sMTVPolicy = ExternalLinkLauncherConstant_HUM.PARAMTITLE_MENTORREDIRECT_HUM + ExternalLinkLauncherConstant_HUM.BENEFITMATRIX_MTV_PLATFORM_HUM + ExternalLinkLauncherConstant_HUM.BENEFITMATRIX_FILTER_DOCUMENTS_HUM;
                            sBaseURL = sBaseURL + sMTVPolicy;
                            sBaseURL = hssBaseEncode(sBaseURL);
                        }
                        return sBaseURL;
                     } else if(lstMemberPlan[0].Policy_Platform__c == ExternalLinkLauncherConstant_HUM.VOB_PLATFORM_CAS_HUM  || lstMemberPlan[0].Policy_Platform__c == ExternalLinkLauncherConstant_HUM.VOB_PLATFORM_LV_HUM  || lstMemberPlan[0].Policy_Platform__c == ExternalLinkLauncherConstant_HUM.VOB_PLATFORM_CI_HUM){
                        if(bCRMGBORFAccess  ==false){
                            string sCASPolicy = ExternalLinkLauncherConstant_HUM.PARAMTITLE_MENTORREDIRECT_HUM + ExternalLinkLauncherConstant_HUM.BENEFITMATRIX_CAS_PLATFORM_HUM + ExternalLinkLauncherConstant_HUM.BENEFITMATRIX_FILTER_DOCUMENTS_HUM;
                                sBaseURL = sBaseURL + sCASPolicy;
                                sBaseURL = hssBaseEncode(sBaseURL);  
                        }
                        return sBaseURL;
                    }
                }else{
                    return sBaseURL;
                }
                
            }
            return sBaseURL;
        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'ExternalLinkLauncher_LC_Hum', 'generateBenefitMatrixURL');
            throw new AuraHandledException(ex.getMessage());
        }
    }

  @AuraEnabled
    public static string generateDebutToolURL(){
        return Endpoint_Url_HUM__c.getInstance('Benefits_Verification_URL').Remote_Url__c;
        
    }
    
    @AuraEnabled
    public static object getInteractionAndCaseDetail(string interactionId)
    {
        interactionAndCaseDetail interactionAndCaseDet = new interactionAndCaseDetail();
        interactionAndCaseDet.interactionCaseSwitch = HUMUtilityHelper.isCRMFunctionalityON('5066619');
        
        if(interactionAndCaseDet.interactionCaseSwitch && interactionId != '' && interactionId != null)
        {
            List<Interaction__c> lstInteraction  = [Select Id,Name from Interaction__c where Id =: interactionId WITH SECURITY_ENFORCED limit 1];
            if(!lstInteraction.isEmpty() && lstInteraction != null)
            {
                List<Case_Interaction__c> lstCaseInteraction = [Select case__r.casenumber, Interaction__c, Interaction__r.name FROM Case_Interaction__c where interaction__c =:interactionId WITH SECURITY_ENFORCED order by LastModifiedDate desc limit 1];
                        
                if(lstCaseInteraction != null && !lstCaseInteraction.isEmpty())
                {   
                    interactionAndCaseDet.InteractionId = lstCaseInteraction[0].Interaction__r.name;
                    interactionAndCaseDet.IntCase = lstCaseInteraction[0].case__r.casenumber;
                }
                else{
                    interactionAndCaseDet.InteractionId = lstInteraction[0].Name;
                    interactionAndCaseDet.IntCase = '';
                }
            }
        }
		return interactionAndCaseDet;
    }
    
    public class interactionAndCaseDetail{
        @AuraEnabled  public String InteractionId;
        @AuraEnabled  public String IntCase;
        @AuraEnabled  public Boolean interactionCaseSwitch = false;
    }

    @testvisible
    private static String hssBaseEncode(String sEndpoint)
    {
        if(sEndpoint.contains(ExternalLinkLauncherConstant_HUM.TARGET_MENTORREDIRECT_HUM))
        {
            sEndpoint= sEndpoint.subStringBefore(ExternalLinkLauncherConstant_HUM.TARGET_MENTORREDIRECT_HUM) + ExternalLinkLauncherConstant_HUM.TARGET_MENTORREDIRECT_HUM + EncodingUtil.base64Encode(blob.valueof(sEndpoint.subStringAfter(ExternalLinkLauncherConstant_HUM.TARGET_MENTORREDIRECT_HUM)));
        }
        return sEndpoint;
    }

    @testvisible
    private static List<MemberPlan> getMemberPlanRecord(string planid){
        try {

        List<MemberPlan> lstMemberPlan = [Select Id, Member.Mbr_Gen_Key__c ,Product_Type__c,Product__c ,Policy_Platform__c,Member.Enterprise_ID__c,Plan.Benefit_coverage__c,
        Plan.Medicare_Segment_ID__c, EffectiveFrom, EffectiveTo, Plan.Contract_Number__c,Plan.PBP_Code__c, Issue_State__c,
        Plan.Policy_Group_Number__c,Product_Description__c,Plan.EffectiveFrom ,Plan.EffectiveTo,
        Plan.Platform__c, Plan.Product__r.Sold_Product_Key_Value__c, Plan.Source_Major_LOB__c,Plan.Product__r.Segment_Type__c,Plan.Payer.enterprise_id__c,SubscriberPlanId__c,
		SubscriberPlanId__r.Member.Mbr_Gen_Key__c, Plan.Purchaser_Plan_External_ID__c  From MemberPlan Where Id =: planid];
        return lstMemberPlan;
        }
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'ExternalLinkLauncher_LC_Hum', 'getMemberPlanRecord');
            return null;
		}
    }

    @testvisible
    private static string setBenefitMatrixBaseURL(){
        String sURL;
        HUMCertificateURL__c objAppURL = HUMCertificateURL__c.getValues('MentorRedirectURL');
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
        if(objAppURL!= null && null!= objAppURL.CertificateListURL__c && objHssURL != null && null!= objHssURL .CertificateListURL__c) 
        {
            sURL = objHssURL.CertificateListURL__c + objAppURL.CertificateListURL__c;          
        }
        return sURL;
    }
	
	@testvisible
    private static string setMentorBenefitGridBaseURL(Boolean isRapidForceApp){
        string sURL;
        HUMCertificateURL__c objAppURL;
        if(isRapidForceApp)
            objAppURL = HUMCertificateURL__c.getValues('MentorRedirectURLRapidForce');
        else
            objAppURL = HUMCertificateURL__c.getValues('MentorRedirectURL');
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
        if(objAppURL!= null && null!= objAppURL.CertificateListURL__c && objHssURL != null && null!= objHssURL .CertificateListURL__c) 
        {
            sURL = objHssURL.CertificateListURL__c + objAppURL.CertificateListURL__c;
        }
        return sURL;
    }

    @testvisible
    private static string DateFormat(string strDate){
        if(String.isNotBlank(strDate)){
            if(strDate.contains('-')){
                return strDate.length() > 10 ? strDate.substring(0,10) : strDate;
            }else if(strDate.contains('/')){
                if(strDate.trim().equals(maxdate))
                {
                    string[] covdate = strDate.trim().split('/');
                    return (string.isNotBlank(covdate[2]) ? covdate[2] : '') + '-' +
                        (string.isNotBlank(covdate[0]) ? covdate[0] : '')+ '-' + (string.isNotBlank(covdate[1]) ? covdate[1] : '') ;
                }
                else
                {
                    Date d = Date.parse(strDate);
                    return Datetime.newInstance(d.year(), d.month(), d.day()).format('yyyy-MM-dd');
                }
            }else{
                return strDate;
            }
        }else{
            return '';
        }
    }
	
    public class RxDetails{
     @AuraEnabled public String sURL{get;set;}
     @AuraEnabled public String HSS_ONECLICK_URL {get; set;}
     @AuraEnabled public String HSS_ONECLICK_TARGET {get; set;}
     @AuraEnabled public String IdForMember{get;set;}
     @AuraEnabled public Boolean setRXConnectFlag{get;set;}
    }
}