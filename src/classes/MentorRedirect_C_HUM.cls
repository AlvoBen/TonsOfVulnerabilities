/*******************************************************************************************************************************
Apex Class Name : MentorRedirect_C_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Class contains methods for redirecting to the Mentor URL and passing parameters.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani          27205                    05/06/2016                   Original Version
* Apoorv Jain                                         08/25/2016                   REQ - 269936 : VOB - Mentor Link for All segments
* Amar Gadewar                                        04/19/2017                   REQ - 311385 Benefits Grid Fix for   Medicare Medicaid Policy
* Amar Gadewar                                        06/14/2017                   REQ - 320530 Remove the Rapid Force Permission
* Raja Sekhar Reddy MV        127479                  09/11/2017                   REQ - 328993 - Break fix - CA ticket 7017124
* Amar Gadewar               126019                   08/23/2017                   REQ - 327584 - Benedifts Grid with Segment Id
* Raja Sekhar Reddy M V                               03/20/2018                   REQ - 355953 - Removed deCryptGroupNumber method.
* Vishal Verma                                        05/29/2018                   REQ - 362119 - Group Benefit Tab – Access Benefit Link
* Amar Gadewar                                        07/03/2018                   REQ - 367117 - AHT Clean up
* Rakesh Patni                                        09/16/2019                   REQ - 405932 - Compass to  Mentor Changes
* Trina Ghosh										  01/12/2021				   REQ - 999625 - KY MMC - Access Benefit Grid
* Amar Gadewar                                        02/03/2021                   User Story 1874625: PR00091574 - MF 1 - AHT - Ability to access "Access Benefit Grid" link from Policy Member page for MES plans (CRM)
* Atia Uzma                                           02/19/2021                   User Story 1956960: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 11/20/2020 , 02/12/2021, 03/05/2021 and 3/19/2021 releases (CRM)
*Sayali Nemade                                        02/08/2021                   User Story 1948483 Mentor Redirect _ URL Redirection Attack Checkmarx Fix
*****************************************************************************************************************************************/
public with sharing class MentorRedirect_C_HUM 
{
    private String sPlatform;
    private String sBenCoverage;
    private String sContractNum;
    private String sPBPCode;
    private String sMedicaresegId;  
    private String sGrpNumber;
    private String sProduct;
    private String sProductType;
    private String sStateCode;
    private Boolean bIsMCDProduct ;
    private Boolean isRapidForceApp {get;set;}
    private String sProductDesc;
    public MentorRedirect_C_HUM() 
    {
        isRapidForceApp = HUMUtilityHelper.IsRapidForceAPP();
        bIsMCDProduct = false;
    }
    
    /**
    * Returns the base URL to redirect to Mentor Search page
    *
    * @return  pageURl      
    */    
    public pageReference queryPolicyMemberRec()
    {
        String sBaseURL  ='';
        PageReference pageRefObj = null;
        try
        {
            getRecord();
            sBaseURL  = setBaseURL();
            List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
                                                                                              Where ProductCode__c = :sProduct And  Major_LOB__c = : sProductType And Business_Description__c = :GLOBAL_CONSTANT_HUM.VOB_MEDICARE];
            
            if(sProduct  == GLOBAL_CONSTANT_HUM.MED && !lstRefBusinessLineProdGroups.isEmpty())
            {
                sBaseURL = sBaseURL + isMedicareMedicaidPolicy();
            }
            else 
            {
                sBaseURL = sBaseURL + isNonMedicareMedicaidPolicy();
            }

            if(HUMMentorToSFDCController.IsRsoUser() && isRapidForceApp){
                 sBaseURL = sBaseURL + '&filterset=' + HUMConstants.HUMCompassRSOParam;
            }
            sBaseURL = hssBaseEncode(sBaseURL);
            pageRefObj = new PageReference(sBaseURL);
            pageRefObj.setRedirect(true);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MentorRedirect_C_HUM', 'queryPolicyMemberRec');
        }
        return pageRefObj;
    }
    
    /**
    * Returns the base Base Encoded endpoint
    *
    * @param   String 
    * @return  String      
    */  
    private string hssBaseEncode(string sEndpoint)
    {
        if(sEndpoint.contains(HUMConstants.TARGET_MENTORREDIRECT_HUM))
        {
            sEndpoint= sEndpoint.subStringBefore(HUMConstants.TARGET_MENTORREDIRECT_HUM) + HUMConstants.TARGET_MENTORREDIRECT_HUM + EncodingUtil.base64Encode(blob.valueof(sEndpoint.subStringAfter(HUMConstants.TARGET_MENTORREDIRECT_HUM)));
        }
        return sEndpoint;
    }
    
    /**
    * Method to fetch the record on the baisis of URL Parmater
    *
    * @return  NA      
    */  
    private void getRecord()
    {
        if(ApexPages.currentpage().getParameters().containsKey('Id'))
        {
			String sPolMemId = ApexPages.currentpage().getParameters().get('Id').escapeHtml4();
            if(NULL!= sPolMemId)
            {
                Policy_Member__c objPolMem = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Benefit_coverage__c,Issue_State__c,
                                             Policy__r.Group_Number__c,Policy__r.Medicare_Segment_ID__c,Product_Description__c From Policy_Member__c Where Id =: sPolMemId];
                if(objPolMem != null)
                {   
                    sPlatform = objPolMem.Policy_Platform__c;
                    sBenCoverage = objPolMem.Policy__r.Benefit_coverage__c;
                    sContractNum = objPolMem.Policy__r.Contract_Number__c;
                    sPBPCode = objPolMem.Policy__r.PBP_Code__c;
                    sMedicaresegId = objPolMem.Policy__r.Medicare_Segment_ID__c;  
                    sGrpNumber = objPolMem.Policy__r.Group_Number__c;
                    sProduct = objPolMem.Product__c;
                    sProductType = objPolMem.Product_Type__c;
                    sProductDesc = objPolMem.Product_Description__c;
                    sStateCode = objPolMem.Issue_State__c ;                    
                    if(sProductType != Null && sProductType == 'MCD'){
                        bIsMCDProduct = true;
                    }
                }
            }
        } 
        else if (ApexPages.currentpage().getParameters().containsKey('PolId'))
        {
			String sPolId = ApexPages.currentpage().getParameters().get('PolId').escapeHtml4();
            if(NULL!= sPolId)
            {
                Policy__c objPol = [Select Id ,Issue_State__c,Product__r.Name ,Platform__c,Major_LOB__c,Contract_Number__c,PBP_Code__c,
                                    Benefit_coverage__c, Group_Number__c,Medicare_Segment_ID__c,Product_Description__c From Policy__c Where Id =: sPolId];
                if(objPol != null)
                {
                    sPlatform = objPol.Platform__c;
                    sBenCoverage = objPol.Benefit_coverage__c;
                    sContractNum = objPol.Contract_Number__c;
                    sPBPCode = objPol.PBP_Code__c;
                    sMedicaresegId = objPol.Medicare_Segment_ID__c;  
                    sGrpNumber = objPol.Group_Number__c;
                    sProduct = objPol.Product__r.Name;
                    sProductType = objPol.Major_LOB__c;
                    sProductDesc = objPol.Product_Description__c;
                    sStateCode = objPol.Issue_State__c ;                    
                     if(sProductType != Null && sProductType == 'MCD'){
                        bIsMCDProduct = true;
                    }
                }   
            }
        }
    }
    
    /**
    * Method to set theBase URL
    *
    * @return  String      
    */  
    private string setBaseURL()
    {
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
    
    /**
    * Method to find if the policy is Medicare /Medicaid policy or not  
    *
    * @return  String      
    */
    private string isMedicareMedicaidPolicy()
    {
        String sMentorURL = '';
        if ( sPlatform == HUMConstants.sLV_Value && bIsMCDProduct ){          
                    String stateCode = '%22'+sStateCode + ' Medicaid Comprehensive Benefit Grid%22';

                    sMentorURL = HUMConstants.PARAMTITLE_MENTORREDIRECT_HUM + stateCode+ '&filterType=Benefit Grids&filterset=FilterSet&filters={%22Function%22:%22none%22,%22SubFunction%22:%22none%22,%22Platform%22:%22none%22,%22BusinessSegment%22:%22none%22,%22LineOfCoverage%22:%22none%22,%22Market%22:%22none%22}';                
                    
        }else{
             String sQueryText = sCOntractNum+  HUMConstants.sHiphan + sPBPCode;
            if(String.isNotBlank(sMedicaresegId)) sQueryText += HUMConstants.sHiphan +  sMedicaresegId;
                if(String.isNotBlank(sProductDesc) && String.isNotBlank(sProductType)
                        && GLOBAL_CONSTANT_HUM.MAJOR_LOB_MES == sProductType) {
                            sQueryText = sProductDesc;
                }
        
            sMentorURL = HUMConstants.PARAMTITLE_MENTORREDIRECT_HUM + sQueryText + HUMConstants.CONSTANTPARAMEND_MENTORREDIRECT_HUM;
        }
        return sMentorURL;
    }   
    
    /**
    * Method to find if the policy is non Medicare /Medicaid policy or not  
    *
    * @return  String      
    */
    private string isNonMedicareMedicaidPolicy()
    {
        String sMentorURL = '';           
        
        if( sPlatform == HUMConstants.sEM_Value || sPlatform == HUMConstants.sMTV_Value )
        {
            sMentorURL = HUMConstants.PARAMSUBGROUP_MENTORREDIRECT_HUM + sBenCoverage + HUMConstants.CONSTANTSUBGROUP_MENTORREDIRECT_HUM ;
        }        
        else if( sPlatform == HUMConstants.sLV_Value || sPlatform == HUMConstants.sCI_Value || sPlatform == HUMConstants.sCAS_Value )
        {
            if(String.isNotBlank(sGrpNumber))
            {
                sMentorURL = HUMConstants.PARAMSUBGROUP_MENTORREDIRECT_HUM + sGrpNumber + HUMConstants.CONSTANTSUBGROUP_MENTORREDIRECT_HUM ;
            }
        } 
        else 
        {
            sMentorURL= HUMConstants.PARAMFILTER_MENTORREDIRECT_HUM ;
        }
        return sMentorURL;                                                                                  
    }
}