/*****************************************************************************************************************************************************************
Apex Class Name : HUMPowerOfAttorneyExtension_R1
Version         : 1.0
Created On      : 04/05/2015 (MM/DD/YYYY)
Function        : Extension class for retrieving Power of Attorney from backend systems.
Test Class      : HUMPowerOfAttorneyExtensionTest                
                 
Modification Log: 
* Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar               22269                    04/05/2015                 Original Version
                                                                                 The callouts being made are a short term solution using the CI Medicare Member web service. Future state will call from the Designee Hub in batch
* Manish Kumar               22546                    04/22/2015                 Modified the code for defect#180358.0001 to resolve service issue. 
                                                                                 Lines modified : 107. Calling generic metod to decrypt fields
* Manish Kumar               22646                    04/27/2015                 Modified the code for defect#180358.0001 to resolve service issue. 
                                                                                 Lines modified : 71 & 109.
* Kritika Popat              22777                    04/30/2015                 Modified logic as part of defect-181130.0001
* Manish Kumar               23159                    05/14/2015                 Modified the code for defect#182188.0001 to resolve service issue to fetch POA alerts. 
                                                                                 Lines modified : 73 - 82.
* Manish Kumar               23831                    06/08/2015                 Modified the code for defect#184140 & 182167 to resolve service issue to fetch POA alerts for CAS policies and dual alerts.
                                                                                 Lines modified : 80-81 & 73-74.
* Manish Kumar               23968                    06/16/2015                 Modified the code for defect#184140 to resolve service issue to fetch POA alerts.
                                                                                 Lines modified :  188
* Apoorv Jain                                         6/16/2016                  REQ - 237004 : Removed the DPASS service call from the controller
* Harshith Mandya																			08/12/2016								 Defect Fix #237394.0001
*******************************************************************************************************************************************************************/

public with sharing class HUMPowerOfAttorneyExtension_R1
{
    private final Account oAcct = NULL;
    private List<Policy_Member__c> lstPM = NULL;
    @testVisible private List<HUMCIMedicareMemberResponseHelper> lstPOAHelper = new List<HUMCIMedicareMemberResponseHelper>();
    private static final Integer iSix = Integer.valueOf(HUMConstants__C.getInstance('HUMSix').StringValue__c);
    private static final Integer iNine = Integer.valueOf(HUMConstants__C.getInstance('HUMNine').StringValue__c);
    private static final Integer iZero = Integer.valueOf(HUMConstants__C.getInstance('ZERO').IntegerValue__c);
    private static final String sDateFormat = HUMConstants__c.getInstance('HUMDateFormat').Stringvalue__c;
    private static final String sCharY = HUMConstants__c.getInstance('HUMCharY').Stringvalue__c;
    private static final String sCharN = HUMConstants__c.getInstance('HUMCharN').Stringvalue__c;
    private static final String sConstRecord = HUMConstants__c.getInstance('HUMConstRecord').Stringvalue__c;

    
    
    /*
    * Method Name   :    HUMPowerOfAttorneyExtension - Constructor
    * Description   :    This method is used for instantiating local variables.    
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMPowerOfAttorneyExtension_R1(ApexPages.StandardController oStdController)
    {
        try
        {
            lstFieldSet = new List<String>(HUMConstants__c.getInstance('POAFields').StringValue__c.split(
                        HUMConstants__c.getInstance('SEMICOLON').StringValue__c));
            this.oAcct = (Account)oStdController.getRecord();          
                         
            checkConditionForCallout();
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'HUMPowerOfAttorneyExtension', 'HUMPowerOfAttorneyExtension');
        }
    }
    
   
    
    public Map<String, List<String>> mapServiceResponse {get; private set;}
    public final List<String> lstFieldSet {get; private set;}
    public List<String> lstKeyTOCIMember {get; private set;}
    
    /*
    * Method Name   :    checkConditionForCallout
    * Description   :    This method is used to check if to make callout or not
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testVisible
    private void checkConditionForCallout()
    {
        Set<String> setValidMemTypes = new Set<String>(HUMConstants__c.getInstance('MEMBER_ID_TYPE').StringValue__c.split(
                        HUMConstants__c.getInstance('SEMICOLON').StringValue__c));
        Set<String> setValidPlatforms = new Set<String>(HUMConstants__c.getInstance('POLICY_PLATFORM_INFO').StringValue__c.split(
                        HUMConstants__c.getInstance('SEMICOLON').StringValue__c));
        
        lstPM = [Select Id, Name, Policy__r.Source_Cust_Cov_Key__c, 
                                        (Select Name From Member_Ids__r Where Type__c IN : setValidMemTypes) 
                                        From Policy_Member__c
                                        Where Member__c =: this.oAcct.Id AND Policy__r.Platform__c IN : setValidPlatforms];
        
        for(Integer i = 0; i < lstPM.size() ; i++)
        {
            if(lstPM.get(i).Member_Ids__r.isEmpty() || String.isBlank(lstPM.get(i).Member_Ids__r[iZero].Name))
            {
                lstPM.remove(i);
                i--;
            }
        }
        
        if(!lstPM.isEmpty())    calloutService();
    }
    
    /*
    * Method Name   :    calloutService
    * Description   :    This method is used to make callout to CI Medicare service
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testVisible
    private void calloutService()
    {
        String sMemberCardId = '';
        String sBenefitSequenceNumber = '';
        String sGroupId = '';

                        HumCIMedicareMemberService objCIMedicare = new HumCIMedicareMemberService();
        
        for(Integer i = 0; i < lstPM.size() && Limits.getCallouts() < Limits.getLimitCallouts() ; i++)
        {
            sMemberCardId = lstPM.get(i).Member_Ids__r[iZero].Name;
            
            If(!String.isBlank(lstPM.get(i).Policy__r.Source_Cust_Cov_Key__c) && lstPM.get(i).Policy__r.Source_Cust_Cov_Key__c.length() >= iNine)
            {
                sBenefitSequenceNumber = lstPM.get(i).Policy__r.Source_Cust_Cov_Key__c.subString(iSix, iNine);
                sGroupId = lstPM.get(i).Policy__r.Source_Cust_Cov_Key__c.subString(iZero, iSix);
            }
            
            lstPOAHelper.add(objCIMedicare.callRestCIMedicareMemberService
            ('', sMemberCardId, sGroupId, sBenefitSequenceNumber, Datetime.now().format(sDateFormat), sCharN, sCharN, sCharN, sCharN, sCharN, sCharY));
        }
        
        removeUnwantedHelpers();
        populateMapForHelpers();
    }
    
    /*
    * Method Name   :    removeUnwantedHelpers
    * Description   :    This method is used for removing null records from the list
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testVisible
    private void removeUnwantedHelpers()
    {
        for(Integer i = 0; i < lstPOAHelper.size() ; i++)
        {
            if(NULL == lstPOAHelper.get(i) || lstPOAHelper.get(i).Errors.size() > 0)
            {
                lstPOAHelper.remove(i);
                i--;
            }
        }
    }
    
    /*
    * Method Name   :    populateMapForHelpers
    * Description   :    This method is used populate map.
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testVisible
    private void populateMapForHelpers()
    {
        lstKeyTOCIMember = new List<String>();
        mapServiceResponse = new Map<String, List<String>>();
        
        List<HUMCIMedicareMemberResponseHelper> lstHelper = new List<HUMCIMedicareMemberResponseHelper>();
        Set<HUMCIMedicareMemberResponseHelper> setHelper = new Set<HUMCIMedicareMemberResponseHelper>();
        HUMCIMedicareMemberResponseHelper oHelper;
        
        for(Integer i = 0 ; i < lstPOAHelper.size() ; i++)
        {
            if((!String.isBlank(lstPOAHelper.get(i).sPOAIndicator) && lstPOAHelper.get(i).sPOAIndicator == sCharY) &&
            !(String.isBlank(lstPOAHelper.get(i).sPOAFirstName) && String.isBlank(lstPOAHelper.get(i).sPOALastName) && 
            String.isBlank(lstPOAHelper.get(i).sPOAPhoneNumber) && String.isBlank(lstPOAHelper.get(i).sPOAType)))
            {
                oHelper = new HUMCIMedicareMemberResponseHelper();
                oHelper.sPOAFirstName = lstPOAHelper.get(i).sPOAFirstName;
                oHelper.sPOALastName = lstPOAHelper.get(i).sPOALastName;
                oHelper.sPOAPhoneNumber = lstPOAHelper.get(i).sPOAPhoneNumber;
                oHelper.sPOAType = lstPOAHelper.get(i).sPOAType;
                setHelper.add(oHelper);
            }
        }
        
        lstHelper.addAll(setHelper);
        
        for(Integer i = 0 ; i < lstHelper.size() ; i++)
        {
            lstKeyTOCIMember.add(sConstRecord + i);
            populateMapWithData(sConstRecord + i, lstHelper.get(i));
        }
    }
    
    /*
    * Method Name   :    populateMapWithData
    * Description   :    This method is used for instantiating map to display on VF page
    * Return Type   :    void
    * Parameters    :    String, HUMCIMedicareMemberResponseHelper, String
    */
    @testVisible
    private void populateMapWithData(String sKeyToCIMember, HUMCIMedicareMemberResponseHelper oPM)
    {
        mapServiceResponse.put(sKeyToCIMember, new List<String>{oPM.sPOAFirstName,oPM.sPOALastName,oPM.sPOAPhoneNumber,oPM.sPOAType});
    }
}