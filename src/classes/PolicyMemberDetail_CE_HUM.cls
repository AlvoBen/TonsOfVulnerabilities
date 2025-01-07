/*******************************************************************************************************************************
Apex Class Name : PolicyMemberDetail_CE_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Extension controller for PolicyMemberDetail_VF_HUM Page
Test Class      : PolicyMemberDetail_T_HUM
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------

* Avinash Choubey            90004                  04/05/2017                 REQ - 303396  Original version
* Rajesh Keswani             118189                 02/08/2017                 REQ - 305662  PCU- RX Carved Out Information
* Darshan Sharma					                05/24/2018			       REQ - 363629 AHT - Add Jump Links to the Policy Member Page(RF)
* Anurag Shah               267634             		07/20/2018             	   REQ - 371243 - Adding the ‘Queue Selection’ fields to the Person Account/ Policy Member pages
* Vijay Gurusamy            275634            		08/23/2018             	   REQ - 367015 - Open up access to Memberid and Policy member section for HP + insururance data PS users.
* Anjali Kumar                                      08/28/2018                 REQ - 373406 Replace PBE1.2 with PBE1.3 in Policy Member Detail Page
* Santhosh Ganji                                    01/31/2019                 REQ - 379004 Ability to view and select the Member ID and Policy member sections via the Member Policy Page
* Shailesh Mali                                     01/24/2019                 REQ - 383577 Integration Story: Changes For EDI Group
* Shailesh Mali                                     03/14/2019                 REQ - 383577 Added logic to send GroupId based on LV platform code.
* Suraj patil		                                  04/05/2019               REQ - 389730 - MTV Member Remarks - Policy Member Page
* Manikanta                 356372                  05/31/2019                 REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
* Suraj patil		                                06/1/2019                  REQ - 366241 - MTV Member Remarks - Policy Member Page.
* Praveen Kumar Parimi                              10/31/2019                 REQ - 415243 Fetching IAB plan name from Policy Member or Policy.
* Yogesh Gupta                                      11/04/2019                 REQ - 403709 Ancillary service for Medicaid Id for V2 Version
* Amar Gadewar                                      05/25/2020                 User Story 1114854: PR00091574 - MF 1 - AHT - Display Contract ID/PBP Code/Segment Code within the header of Policy Member page (CRM)
* Suresh Kokkeragadda                               06/24/2020                 User Story 1203671: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 07/07 And 07/17 releases (CRM)
* Riddhi Mittal                                      07/27/2020               755539-PR00093155 - MF 21 - Other Insurance (OI) Status in CRM 
* Sayali Nemade				                         08/04/2020			      Added switch for REQ -1137828 MF 3 - CRM User Access to CCP GCP Data in Repository (Legacy Contact History Section - Policy Member)
* Raja Sekhar Reddy M V                              08/20/2020                PR00095673_US_1276005_DualstatusIndicator
* Sayali Nemade 				                     08/27/2020               REQ - 1170241 MF 3 - Archived CCP Reference Summary Page - High Level  (Overall look)
* Sayali Nemade 								     09/03/2020               REQ - 1170241 DF -1509 Not able to see data on UI for Group, Provider And Agent/Broker
* Keerti Durbha                                      08/20/2020               US-1114331- PR00094035 - MF 02 – Adding three New fields for Medicaid Policy Details section for “SC” 
* Firoja Begam                                       10/28/2020               US-1279281- PR00095366 - MF 1 - New fields in policy details section (member policy page)
* Sai Kumar Boga                                     11/10/2020                 REQ - 1567028  CALL_MF03_FR02: KY MMC 2021(Added per CR17)_CRM service must create a NATIVE AMERICAN indicator
* Firoja Begam                                       11/4/2020                US-1661723 -- PR00095366 Accolade - SF - TECH - Vendor details display only for GBO products
* Ajay Chakradhar                                    11/20/2020	              US 1641087 - PR00093155 - MF 21 - Release 12/18
* Firoja Begam                                       12/12/2020	              Error Log Fix
* Subrahmanyam N                                     27/01/2021               REQ - 1896703  CALL_MF05_FR01: KY MMC 2021_The CRM system must display PriorMedicaidID & PriorMedicaidBeginDate
* Ajay Chakradhar                                    02/15/2021	              User Story 1707499: PR00093155 - MF 21 - Release 03/19/21
* Firoja Begam                                       02/10/2021               US-1760349 - PR00095180 - CRM 6.01 Add PEO Indicator for MTV and CAS products  to Member Policy Page (copy)
* Ajay Chakradhar         							 03/10/2021	          	  User Story 1481853: T1PRJ0002606/PR00094268 - INC0972187 - memberancillary API (INC0379703) - Release 04/16/21
* Firoja Begam                                       04/07/2021               US-2150784 - T1PRJ0002736/PR00094232 - 2021 - MF6 - Security - Checkmarx High Defects
* Satyanarayana chenna                               04/09/2021               US-1918663 PR00094226 - CR AEP readiness - Low Security Issues 
* Amar Gadewar                                       05/03/2021               User Story 2215191: T1PRJ0001827 - MF 1 - AHT - Ability to automatically launch Dual Eligible tab if the policy is Dual Eligible (RF)
* Ajay Chakradhar                           		 05/05/2021	              REQ - 2184294 - Replacing constants with Consumer Management Constants
* Atia Uzma                                          05/25/2021               User Story 2325450: T1PRJ0001827 - MF 1 - AHT - Ability to automatically launch Dual Eligible tab if the policy is Dual Eligible (CRM)
*Giri Yeddula                                          06/21/2021                User Story 2049344:T1PRJ0001793 - MF WR-06307 - Accolade Vendor Fields Visible for PROVIDER- MEMBER POLICY PAGE WA-22144
* Samantha Tennyson                                  06/24/2021               User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM)
*Giri Yeddula                                        07/14/2021                Defect DF-3371
* Firoja Begam                                       07/14/2021               User Story 1992199: Accolade 2021 - US 29.01 CRM to display the members effective and term date for the Accolade Program
* Vardhman Jain                                      07/29/2021               User story 1801229 PR00094010 - MVP MF 6 - Policy Member page - Strides
* Vardhman Jain                                      08/12/2021               User story 1801229 PR00094010 - Added Browser Check and Defect fix 3553
* Sahil Verma                                        10/14/2021               Show popup on Case/Plan/Detail page on click of Save button on Interaction Log Panel as part of US - 2564484
* Vardhman Jain                                      11/11/2021               Added switch on Account and Policy page for Coveo
* Ajay Chakradhar                                    11/22/2021               DEF 4182 FIX - Global Constant replace for performance improvement
* Akash Surwase                                      04/27/2022               User Story 2444097: T1PRJ0054938 MF09 - PLT - CRM Must Display New Fields in Medicaid Details Section  for OH MCD
* Ajay C / Subhamay                                      04/28/2022               User Story 3226796: T1PRJ0054938 - FR20.01:TECH- Display Redetermination Date in MCD Policy details from CRM platform instead of from MAS Service - OH State
* Akash Surwase                                       04/29/2022                 User Story 3134909: T1PRJ0054938 MF09 - Medicaid Policy Detail - Native Amercian to Indigenous Indicator
* Akash Surwase                                      05/23/2022                 User Story 3085055: T1PRJ0054938 - FR09.07 CRM Service must display the following fields on the Medicaid policy detail page for OH Medicaid
* Ajay Chakradhar                                    05/23/2022               DEF 4925 FIX - Redetermination Date Format
* Akash Surwase                                      07/18/2022                 T1PRJ0163283 MCD-NS-LA 3.5.1.1: CRM Service must be able to display the Redetermination Date for LA
* Anil Parvathaneni                                 07/18/2022                  User Story 2968018: T1PRJ0163283 MCD-NS-LA 3.5.1.2: CRM Indigenous Indicator for LA Medicaid(CRM)
* Ajay Chakradhar                                    08/17/2022               USER STORY 3027335 : T1PRJ0100059- MF 3 - Disable Legacy Contact History and Demographic Updates - Policy Member
*****************************************************************************************************************************************/

Public with sharing Class PolicyMemberDetail_CE_HUM {
    Public string columnName{Get; Set;}
    Public string columnHeader{Get; Set;}
    Public Boolean bIsPharmacyUser {Get; Set;}
    Public list<String> lstFloatLinkelem{Get; Set;}
    Public Boolean bIsPDPPilotUser{Get; Set;}
    Public policy_Member__c sPloyConcat{Get; Set;}
    Public string sPloyConcatConv{Get; Set;}
    Public String sId{Get; Set;}
    Public String EDIValue{Get; Set;}
    Public string humanaValue {Get; Set;}
    Public string groupUpdateFrequencyValue {Get; Set;}
    Public string sMemIdBase;
    Public string sDependentCode;
    Public Policy_member__c oPolMem;
    Public boolean bProductTypeCheck {Get; Set;}
    Public MemberAncillaryResponse_DTO_HUM_V2 AncillaryInfo {Get; Private Set;}
    Public string sRequest;
    Public MemberAncillary_S_HUM_V2 oAncillaryService = New MemberAncillary_S_HUM_V2();
    Public Boolean ancillaryOnOffswitch {Get; Set;}
    Public Boolean sEnableDualStatus{Get; Set;}
    Public MBEResponseV1_DTO_HUM MbeInfo {Get; Private Set;}
    Public BenefitsV2_S_HUM oMbeService = New BenefitsV2_S_HUM();
    Public Boolean mMbeOnOffswitch {Get; Set;}
    Public String sPolicyMemberId {Get; Set;}
    Public String sPersonID{Get; Set;}
    Public string sRecId {Get; Set;}
    Public string sAccountId {Get; Set;}
    Public string sAccountName {Get; Set;}
    Public String level {Get; Set;}
    Public String message {Get; Set;}
    Public boolean isRendered {Get;Set;}
    Public String sOtherInsuranceResult {Get; Set;}  
    Public boolean bIsOtherInsurance {Get; Set;}
    Public Boolean sOIfieldOnOffswitch {Get; Set;}
    Public Boolean bOIOffDualOnIndicator {Get; Set;}
    Public Boolean sPolicyMemberPlatformcheck {Get; Set;}
	Public Boolean bSwitchlegacyContactHistory {Get;Set;}
	Public String sPageName {Get;Set;}
	Public Boolean bDualIndicatorTwelveMonthOnOffSwitch {Get; Set;}
	Public String sInteractingAboutCCPGCP{Get;Set;}
    Public String sSelectedPolicy{Get;Set;}
    Public String saccName{Get;Set;}
	Public String sProductName{Get; Set;}
	Public String sEnterpriseId {Get; Set;}
    Public Boolean memberancillaryOnOffswitch {Get; Set;}
    //US#2564484	
    public static boolean bPopup{get; set;}
        /*******VendorFields******/
    Public GBEServiceResponse_H_HUM respond = New GBEServiceResponse_H_HUM();
    Public Boolean isCRMSGBOPermissionSetUser {Get; Set;}
    Public Boolean isCRMSPCCPermissionSetUser {Get; Set;}
    //public Policy__c oPolicy {get;set;}
    Public String sProgramName {Get; Set;}
    Public String sProgramDesc {Get; Set;}
   // public String sProgramHoverDesc {get; set;}
    Public String sProgramEff {Get; Set;}
    Public String sProgramEnd {Get; Set;}
   // public String sProgramType {get; set;}
    Public Boolean ccpOnOffVendor {Get; Set;}
    Public Policy_Member__c policyMember = New Policy_Member__c();
    @TestVisible private PolicyMemberDetail_S_HUM oServiceVendorPolicyMem = New PolicyMemberDetail_S_HUM ();
    //@TestVisible public String sRequest = '';
    String comma = ',';
    Private Policy_Plan_Details__c policyPlan = New Policy_Plan_Details__c();
    list<Policy_Plan_Details__c> policyPlanList = new List<Policy_Plan_Details__c>();
    @testVisible private GBEServiceResponse_H_HUM.VendorProgramOfferListGroup vendorProgramGroupDTO = NULL;
    @testVisible private GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = NULL;
    public class ServiceDownException extends Exception {}
    /*******PEO Indicator Fields******/
    Public Boolean switchOnOffPEO {get; set;}
    Public Boolean displayFieldPEO {get; set;}
    @testVisible private GBEServiceResponse_H_HUM.AssociationList associationDTO = NULL;
    @testVisible private GBEServiceResponse_H_HUM.OrganizationTypeList organizationTypeDTO = NULL;
    public String sPeoIndicator {get; set;} 
    /*******Accolade Fields for OnDate******/
    Public Boolean switchOnOffAccOnDt {get; set;}
    @testVisible private List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorActiveProgramList = NULL;
    @testVisible private List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorTerminatedProgramList = NULL;
    @testVisible private List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorFutureProgramList = NULL;
    /*******Unified Search Button***/
	public boolean bShowUnifiedSearch{get;set;}
    Public Boolean sIndigenousIndicatorSwitch {Get; Set;}
    public boolean bRedeterminationDateSwitch{get;set;}
    Public String sRedeterminationDate{get;set;}
    public boolean ohioRiseSwitch{get;set;} //Switch related with US-3085055(OhioRise) 
    Public Boolean IndigenousIndicatorSwitchLA {Get; Set;} 
    Public Boolean bRedeterminationdateswitchLA {Get; Set;}
    public String RecordTypeNm{get;set;}
    public boolean blegacyCntHistry{get;set;}
    public boolean bagnosticswitch{get;set;}
   
   /**
     * Returns void
     * <p>
     * constructor for Other Insurance (OI) data table fields
     * 
     */
     public PolicyMemberDetail_CE_HUM() {
        sRecId = ApexPages.currentpage().getParameters().get('Id').escapeHtml4(); //Checkmarx High Defects Fix for Reflected XSS //26/11 - id to Id
        if(String.isNotBlank(sRecId))
        {
        policy_member__c  Policymember = [select member__c,Policy_Platform__c,Member__r.Name, Member__r.Enterprise_ID__c from policy_member__c where id=:sRecId limit 1];
            if(Policymember!=null){
                 sAccountId=Policymember.member__c;
                 saccName = Policymember.Member__r.Name;
                 sEnterpriseId = Policymember.Member__r.Enterprise_ID__c;

                if(Policymember.Policy_Platform__c=='EM')
                {
                     columnHeader = json.serialize(New List <String> {'Carrier Name','Effective Date','End Date','Primary', 'Last Verified Date'});
                      columnName = json.serialize(New List <String> {'sCarrierName','sEffectiveDate','sEndDate','sPrimary','sOILastVerifiedDate'});
                }else if(Policymember.Policy_Platform__c=='LV'){
                    
                     columnHeader = json.serialize(new List <String> {'Carrier Name','Effective Date','End Date','Primary', 'Last Updated Date'});
                      columnName = json.serialize(new List <String> {'sCarrierName','sEffectiveDate','sEndDate','sPrimary','sLastUpdatedDate'});
                }
            
          }
        } 
    }
    
    
        
   /**
     * Returns void
     * <p>
     * constructor to check pharmacy user And Not CRMS_301_HPInsuranceData PS user 
     * 
     */
    public PolicyMemberDetail_CE_HUM(ApexPages.StandardController controller)
    {
        sOIfieldOnOffswitch = false;
         // On/Off switch functionality for suffix for other insurance
            sPolicyMemberPlatformcheck = false;
         sOIfieldOnOffswitch= HUMUtilityHelper.isCRMFunctionalityON('1707499');
         bDualIndicatorTwelveMonthOnOffSwitch = HUMUtilityHelper.isCRMFunctionalityON('1276005');
         this.bRedeterminationDateSwitch = HUMUtilityHelper.isCRMFunctionalityON('3226796');
         sId = ApexPages.currentPage().getParameters().get('Id');
            if(sId != null )
            {
                sPloyConcat= [select Id, Name, Redetermination_Date__c,Member__r.Mbr_Gen_Key__c, Member__r.Name,Member__r.RecordType.Name, Member__r.Enterprise_ID__c,Policy_Platform__c,Policy__r.Client_Number__c,Group_Number__c,Product__c,Policy__r.Source_Cust_Cov_Key__c,Issue_State__c from Policy_Member__c where id=:sId];
                if (sPloyConcat.Policy_Platform__c != 'CB' && (sPloyConcat.Policy_Platform__c== 'EM'||sPloyConcat.Policy_Platform__c== 'LV' )){
                   sPolicyMemberPlatformcheck = true;
               }
                if(sPloyConcat.Policy__r.Client_Number__c != null)
                    {
                    sPloyConcatConv = sPloyConcat.Policy_Platform__c+' ('+sPloyConcat.Policy__r.Client_Number__c+')';
                    }
				if(sPloyConcat != null){    
                    sInteractingAboutCCPGCP = sPloyConcat.Member__r.Mbr_Gen_Key__c;
                    String sSelectedPolicyName = sPloyConcat.Name;
                    if(sSelectedPolicyName != null){
                        sSelectedPolicy = sSelectedPolicyName.substring(0,sSelectedPolicyName.length()-2);
                    }
                    saccName = sPloyConcat.Member__r.Name;
                    sProductName = sPloyConcat.Product__c;
					sEnterpriseId = sPloyConcat.Member__r.Enterprise_ID__c;
                    sRedeterminationDate = (sPloyConcat.Redetermination_Date__c != Null) ? sPloyConcat.Redetermination_Date__c.format() : '' ;
                    RecordTypeNm = sPloyConcat.Member__r.RecordType.Name;
                    
                }
            }
      blegacyCntHistry = false;      
      sOtherInsuranceresult = '';
      bIsOtherInsurance = false;
      String EnableDualStatus = HUMConstants__c.getValues('Enable Dual Status Indicator').StringValue__c; 
      sEnableDualStatus = Boolean.valueOf(EnableDualStatus);
      bIsPharmacyUser = false;
      bIsPDPPilotUser = false;
      bProductTypeCheck = false;
      mMbeOnOffswitch= HUMUtilityHelper.isCRMFunctionalityON('1567028');
        ancillaryOnOffswitch = HUMUtilityHelper.isCRMFunctionalityON('1896703');
      memberancillaryOnOffswitch =  HUMUtilityHelper.isCRMFunctionalityON('1114331');
      ohioRiseSwitch = HUMUtilityHelper.isCRMFunctionalityON('3085055'); //Changes related with US-3085055(OhioRise)            
      IndigenousIndicatorSwitchLA = HUMUtilityHelper.isCRMFunctionalityON('2968018'); //IndigenousIndicatorSwitchLA.
      bRedeterminationdateswitchLA=false;
      bRedeterminationdateswitchLA = HUMUtilityHelper.isCRMFunctionalityON('3251724');//RedeterminationLA switch.
      bagnosticswitch = HUMUtilityHelper.isCRMFunctionalityON('3027335');
      if(Pharmacy_H_HUM.isPharmacyUser() != ConsumerManagementConstants_HUM.NONPHARMACY_USER_HUM && Pharmacy_H_HUM.isHPInsurancePSUser() == false && Pharmacy_H_HUM.isHPTraditionalInsurancePSUser() == false)
          bIsPharmacyUser  = true;
       
        if((!bIsPharmacyUser && Pharmacy_H_HUM.isHPInsurancePSUser() == false ))
        {
            bIsPDPPilotUser = Pharmacy_H_HUM.isPDPPilotUser();
        }
      infoSet();
      createFloatingList();
	  //REQ - 1137828 Switch
	  bSwitchlegacyContactHistory = HUMUtilityHelper.isCRMFunctionalityON('1137828'); 
	  sPageName = ConsumerManagementConstants_HUM.POLICYMEMBERCCP;
      isCRMSGBOPermissionSetUser = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(ConsumerManagementConstants_HUM.CRMS_240_GBO_Access_HUM);
      isCRMSPCCPermissionSetUser = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(ConsumerManagementConstants_HUM.CRMS_685_PCC_Access_HUM); 
      sIndigenousIndicatorSwitch=false;
      sIndigenousIndicatorSwitch=HUMUtilityHelper.isCRMFunctionalityON('3134909'); //MCD Field Label update
      ccpOnOffVendor = false;
      ccpOnOffVendor= HUMUtilityHelper.isCRMFunctionalityON('1279281');
      switchOnOffPEO = false;
      switchOnOffPEO = HUMUtilityHelper.isCRMFunctionalityON('1760349');
      switchOnOffAccOnDt = false;
      switchOnOffAccOnDt = HUMUtilityHelper.isCRMFunctionalityON('1992199');
      bOIOffDualOnIndicator = false ;
      checkOIOffDualOnIndicator(); // OI-OFF & Dual-ON indicator
	  getUnifiedSearchDeatils();//Unified search method.
    }
    
    /**
    * Returns Object
    * <p>
    * This method is used to invoke the MBE service
    *
    * @pram  String : PersonId
    * @return  Object
    * @see
    */    
    @RemoteAction
    public static continuation getOIData(string sRecordID ,string sPolicymemberID ) 
    {
    try{
        return ValidOIPolicyMemberID_D_HUM.getOtherInsuranceData(sRecordID,sPolicymemberID); 
       }
        catch(Exception ex)
         {
             HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'getOIData');
             return null;
         }     
    } 
    
  /**
    * Returns an object
    * <p>
    * This method is Process the response
    *
    * @pram  List<String> : Request Headers
    * @param  Object : State
    * @return  object
    * @see
    */
    public Static Object ProcessMBEOtherInsuranceResponse(List<String> Labels,Object State)
    {
       try{ 
        return ValidOIPolicyMemberID_D_HUM.processMBEOtherInsuranceResponse(Labels, State);
        }
        catch(Exception ex)
         {
             HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'ProcessMBEOtherInsuranceResponse');
             return null;
         }
    }
    
    /**
     * Method to set variables Other Insurance (OI) yes/no
     */
     public pageReference otherInsuranceYesNO(){
        if(sOtherInsuranceResult == 'Yes'){
            bIsOtherInsurance = True;
        }else{
            sOtherInsuranceResult = 'No';
            bIsOtherInsurance = False;
        }
        return null;
    }

    /*
	 * This method is to set an indicator when OI is Off and Dual is On
	 * @param  NA
	 * @return void
	 */
    public void checkOIOffDualOnIndicator()
    {
        if((!sOIfieldOnOffswitch && bDualIndicatorTwelveMonthOnOffSwitch && sPolicyMemberPlatformcheck) ||
		   (sOIfieldOnOffswitch && bDualIndicatorTwelveMonthOnOffSwitch && bIsPharmacyUser && sPolicyMemberPlatformcheck)){
				bOIOffDualOnIndicator = True ;
		}
    }
     
	/*
     * Method Name   :    GetGroupInfoFromGBE
     * Description   :    This method makes call to GBE when the Member policy page is loaded 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */  
     public Object GetGroupInfoFromGBE() 
    	{  
         try{
            SetPolicyPlanValues();
            if(policyPlan.id != null)
                {  
                    HUM_Webservice_Callout__c ServiceObj = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
                    Integer iTIMEOUT_INT_SECS = Integer.valueof(ServiceObj.Timeout__c)/1000;
                    Continuation cont = new Continuation(iTIMEOUT_INT_SECS );
                    cont.continuationMethod=ConsumerManagementConstants_HUM.PROCESSRESPONSE_HUM;
                    HttpRequest request = new HttpRequest();
                    oServiceVendorPolicyMem.setupRequest(request, ServiceObj,policyPlan,policyMember); 
                    sRequest = cont.addHttpRequest(request);
                    return cont;
                } 
             }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_CE_HUM','GetGroupInfoFromGBE');     
            } 
            return NULL;
        }
    /**
     * Returns NULL
     * <p>
     * get and set the record values of PolicyMember Object.
     * @return      NULL
     * @see         Record Values
     */     
     public void SetPolicyPlanValues()
    {
        try{
            if(sId != NULL)
            {
             policyMember = PolicyMemberDetail_D_HUM.getPolicyMemberVendor(sId);
             String vendorProdType = Label.RSO_Product_Type;
             List<String> lstProdType = vendorProdType.split(comma);
             if(policyMember != NULL)
              {
                if(policyMember.Policy_Platform__c.equals(ConsumerManagementConstants_HUM.VOB_PLATFORM_EM_HUM) && policyMember.Product__c.equals(ConsumerManagementConstants_HUM.MED) && (isCRMSGBOPermissionSetUser ||  isCRMSPCCPermissionSetUser))
                  {
                   GetPolicyPlan(policyMember);
                  }
                  else if(policyMember.Policy_Platform__c.equals(ConsumerManagementConstants_HUM.VOB_PLATFORM_LV_HUM) && policyMember.Product__c.equals(ConsumerManagementConstants_HUM.MED) && (isCRMSGBOPermissionSetUser || isCRMSPCCPermissionSetUser))
                  {
                   if(!lstProdType.contains(policyMember.Product_Type__c))
                   { 
                    GetPolicyPlan(policyMember);
                   }
                  }  
               }
            }
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_CE_HUM','SetPolicyPlanValues');     
        } 
    }
    
    
    /*
     * Method Name   :    GetPolicyPlan
     * Description   :    This method get the PolicyPlanDetails  
     * Return Type   :    void
     * Parameters    :    policyMember
     */  
    
     public void GetPolicyPlan(Policy_Member__c policyMember) 
    	{  
         try{
             policyPlanList = PolicyMemberDetail_D_HUM.getPolicyPlanVendor(policyMember);
             if(policyPlanList != NULL && !policyPlanList.isEmpty())
             for(Policy_Plan_Details__c policyPlanDetail : policyPlanList)
                {
                 if(policyPlanDetail.Group_Number__c <> null)
                  {
                   policyPlan = policyPlanDetail;
                   }
                }
             }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_CE_HUM','GetPolicyPlan');     
            } 
        }
    /*
     * Method Name   :    processResponseVendor
     * Description   :    This method process Http Response from GBE Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    public object processResponseVendor()
    {   
        try 
        { 
           HttpResponse response = Continuation.getResponse(sRequest); 
           boolean isResponseValid = checkResponseValidity(response);
           if(isResponseValid)
                {
                respond = (GBEServiceResponse_H_HUM)System.JSON.deserialize(response.getBody(),GBEServiceResponse_H_HUM.class);
                if(respond<>null && respond.GroupInfo<>null )
                    {
                     if(respond.GroupInfo.Platform != NULL )
                     {
                      if(respond.GroupInfo.VendorProgramOfferListSubGroup<>null )
                      	{
                         if(switchOnOffAccOnDt){
                          setVendorValueOnDate(respond);      
                         }else{
                          vendorInfoValue(respond);
                         }
                      	}
                      if(switchOnOffPEO)
                      	{
                          peoInfoValue(respond);
                      	}  
                      }
                    }
                }
                else
                {
                  HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), 'PolicyMemberDetail_CE_HUM', 'processResponseVendor');
            	}
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_CE_HUM','processResponseVendor');    
        }
 		return null;  
    }

    /*
     * Method Name   :  peoInfoValue()
     * Description   :  This method is used to populate peo Indicator Info 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       public void peoInfoValue(GBEServiceResponse_H_HUM resObj){
           if(resObj != NULL){
               if(resObj.GroupInfo.Platform.equals(ConsumerManagementConstants_HUM.VOB_PLATFORM_EM_HUM) && resObj.GroupInfo.AssociationList != NULL)
               {
                setPeoInfoforEM(resObj);
               }else if(resObj.GroupInfo.Platform.equals(ConsumerManagementConstants_HUM.VOB_PLATFORM_LV_HUM) && resObj.GroupInfo.OrganizationTypeList != NULL)
               {
                setPeoInfoforLV(resObj);
               }
            }
       }
    
    /*
     * Method Name   :  setPeoforEM()
     * Description   :  This method is used to populate peo Indicator Info for EM Platform 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       public void setPeoInfoforEM(GBEServiceResponse_H_HUM resObj){
          if(resObj.GroupInfo.AssociationList != NULL) associationDTO = resObj.GroupInfo.AssociationList;
                if(associationDTO != NULL && associationDTO.Association != NULL)
                 {          
	                for(GBEServiceResponse_H_HUM.Association association: associationDTO.Association)
                    {
                     /*******To verify AssociationType as PEO from AssociationList****/
                     if((!String.ISBLANK(association.AssociationType)) && (association.AssociationType.equalsIgnoreCase(ConsumerManagementConstants_HUM.PEO)))
                       {
                         List<String> associationEndDateSplit = new List<String>();
                         List<String> associationEfffectiveDateSplit = new List<String>();
                         if(!String.isBlank(association.AssociationEndDate) && !String.isBlank(association.AssociationEffectiveDate))  
                         {              
                           associationEndDateSplit = association.AssociationEndDate.split('/');
                           associationEfffectiveDateSplit = association.AssociationEffectiveDate.split('/');
                         }
                         if(!associationEndDateSplit.isEmpty() && !associationEfffectiveDateSplit.isEmpty())
                         {
                           Date assoEndDate = Date.newInstance(Integer.valueOf(associationEndDateSplit[2]),Integer.valueOf(associationEndDateSplit[0]),Integer.valueOf(associationEndDateSplit[1]));
                           Date assoEffDate = Date.newInstance(Integer.valueOf(associationEfffectiveDateSplit[2]),Integer.valueOf(associationEfffectiveDateSplit[0]),Integer.valueOf(associationEfffectiveDateSplit[1]));
                           //Set PEO Indicator Field value to Yes/NO based on AssociationEndDate is Future Date or Past Date
                           if(assoEffDate <= system.today() && assoEndDate >= system.today())
                           {
                            sPeoIndicator =  ConsumerManagementConstants_HUM.PEO_YES; 
                            break;   
                           }else{
                            sPeoIndicator =  ConsumerManagementConstants_HUM.PEO_NO;
                           }
                         } 
                       }else{
                          sPeoIndicator = ConsumerManagementConstants_HUM.PEO_NO; //Set PEO Indicator Field value to NO if no AssociationType is PEO
                       }
                    }	     		
                 }
       }
    
    /*
     * Method Name   :  setPeoInfoforLV()
     * Description   :  This method is used to populate peo Indicator Info for LV Platform 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       public void setPeoInfoforLV(GBEServiceResponse_H_HUM resObj){
          if(resObj.GroupInfo.OrganizationTypeList != NULL) organizationTypeDTO = resObj.GroupInfo.OrganizationTypeList;
             if(organizationTypeDTO != NULL && organizationTypeDTO.OrganizationType != NULL)
               {          
	             for(GBEServiceResponse_H_HUM.OrganizationType organization: organizationTypeDTO.OrganizationType)
                   {
                     if((!String.ISBLANK(organization.Code)) && (organization.Code.equalsIgnoreCase(ConsumerManagementConstants_HUM.PE)))
                     {
                      sPeoIndicator = ConsumerManagementConstants_HUM.PEO_YES;  //Set PEO Indicator Field value to Yes if organization Code is PE
                      break;
                     }else{
                      sPeoIndicator = ConsumerManagementConstants_HUM.PEO_NO;  //Set PEO Indicator Field value to No if no organization Code is PE
                     }
                   }	     		
               } 
        }

    /*
     * Method Name   :  vendorInfoValue()
     * Description   :  This is the used to populate Vendor Info 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
    	public void vendorInfoValue(GBEServiceResponse_H_HUM resObj){
           if(resObj != NULL){
               if(resObj.GroupInfo.VendorProgramOfferListSubGroup != NULL) vendorProgramSubGroupDTO = resObj.GroupInfo.VendorProgramOfferListSubGroup;
                if(vendorProgramSubGroupDTO != NULL && vendorProgramSubGroupDTO.VendorProgramOffer != NULL)
                {          
	                for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorProgramSubGroupDTO.VendorProgramOffer)
                    {
                      if((!String.ISBLANK(programoffer.VendorIdentifier)) && (programoffer.VendorIdentifier.equalsIgnoreCase(ConsumerManagementConstants_HUM.ACCL)))
                       {
                          if(!String.ISBLANK(programoffer.VendorIdentifierDescription)) sProgramName = programoffer.VendorIdentifierDescription; 
                          if(!String.ISBLANK(programoffer.VendorBenefitCodeDescription)) sProgramDesc = programoffer.VendorBenefitCodeDescription;
                          if(!String.ISBLANK(programoffer.ProgramOfferEffectiveDate)) sProgramEff = programoffer.ProgramOfferEffectiveDate; 
                          if(!String.ISBLANK(programoffer.ProgramOfferEndDate)) sProgramEnd = programoffer.ProgramOfferEndDate;
                          break;
                       }
                    }	     		
                } 
            }
    }
    /*
     * Method Name   :    checkResponseValidity
     * Description   :    This method to check HttpResponseStatus 
     * Return Type   :    Boolean
     * Parameters    :    HttpResponse response
     */
    public static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        try {
            if(response != null) {
                integer statusCode = response.getStatusCode();
                Boolean isStatusOk = (statusCode == GlobalCommonConstants_HUM.HTTP_OK_HUM) ? true : (statusCode == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) ? true : false;
                Boolean isNoFault = (response.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE)) ? false : true;
                returnValue = (isStatusOk && isNoFault) ? true : false;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_CE_HUM','checkResponseValidity');
            returnValue = false;
        }
        return returnValue;
    }

    /*
     * Method Name   :  setVendorValueOnDate()
     * Description   :  This is the used to verify accolade programs if available in GBeresponse and call method to set Vendor Info 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
    
     public void setVendorValueOnDate(GBEServiceResponse_H_HUM resObj){
         try{
           if(resObj != NULL){
               if(resObj.GroupInfo.VendorProgramOfferListSubGroup != NULL) vendorProgramSubGroupDTO = resObj.GroupInfo.VendorProgramOfferListSubGroup;
                if(vendorProgramSubGroupDTO != NULL && vendorProgramSubGroupDTO.VendorProgramOffer != NULL)
                {    
                    setAccoladeProgram(vendorProgramSubGroupDTO);     		
                } 
            }
         }catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_CE_HUM','setVendorValueOnDate');
        }
      }

    /*
     * Method Name   :  setAccoladeProgram()
     * Description   :  This is the used to populate Active Or Future Or Terminated Accolade Info 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */

    	public void setAccoladeProgram(GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO){
           vendorActiveProgramList =  new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
           vendorTerminatedProgramList =  new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
           vendorFutureProgramList =  new List<GBEServiceResponse_H_HUM.VendorProgramOffer>();
           List<String> progEndDateSplit = new List<String>();
           List<String> progEffDateSplit = new List<String>();
           List<Date> progEffDateList = new List<Date>();
           List<Date> progEffFutrDateList = new List<Date>();
           List<Date> progEndDateList = new List<Date>();
           try{
           if(vendorProgramSubGroupDTO != NULL && vendorProgramSubGroupDTO.VendorProgramOffer != NULL)
	                for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorProgramSubGroupDTO.VendorProgramOffer)
                    {
                      if((!String.ISBLANK(programoffer.VendorIdentifier)) && (programoffer.VendorIdentifier.equalsIgnoreCase(ConsumerManagementConstants_HUM.ACCL)))
                       {
                         if(!String.ISBLANK(programoffer.ProgramOfferEffectiveDate) && !String.ISBLANK(programoffer.ProgramOfferEndDate)) 
                         {              
                           progEndDateSplit = programoffer.ProgramOfferEndDate.split('/');
                           progEffDateSplit = programoffer.ProgramOfferEffectiveDate.split('/');
                         }
                         if(!progEffDateSplit.isEmpty() && !progEndDateSplit.isEmpty())
                         {
                           Date progEndDate = Date.newInstance(Integer.valueOf(progEndDateSplit[2]),Integer.valueOf(progEndDateSplit[0]),Integer.valueOf(progEndDateSplit[1]));
						   Date progEffDate = Date.newInstance(Integer.valueOf(progEffDateSplit[2]),Integer.valueOf(progEffDateSplit[0]),Integer.valueOf(progEffDateSplit[1]));
                           //create List for Activeprogram or Futureprogram or Termed program based on ProgramEffDt and ProgramEndDt
                           if(progEffDate <= system.today() && progEndDate >= system.today())
                           {
                           //Active Programs added in vendorActiveProgramList and corresponding program Effective dates added in progEffDateList to calc latest Active Program
                            vendorActiveProgramList.add(programoffer); 
                            progEffDateList.add(progEffDate);
                           }else if(progEffDate >= system.today()){
                           //Future Programs added in vendorFutureProgramList and corresponding program Effective dates added in progEffFutrDateList to calc Earliest Active Program
                            vendorFutureProgramList.add(programoffer);
                            progEffFutrDateList.add(progEffDate);
                           }else if(progEndDate <= system.today()){
                            //Terminated Programs added in vendorTerminatedProgramList and corresponding program End dates added in progEndDateList to calc latest Terminated Program
                            vendorTerminatedProgramList.add(programoffer);
                            progEndDateList.add(progEndDate);
                           }
                         } 
                       }
                    }	
            	setActiveTermedOrFutureVendor(vendorActiveProgramList, vendorTerminatedProgramList, vendorFutureProgramList, progEffDateList, progEffFutrDateList, progEndDateList);
            }
            catch(Exception ex)
        	{    
            HUMExceptionHelper.logErrors(ex,'PolicyMemberDetail_CE_HUM','setAccoladeProgram');    
        	}
         }

	/*
     * Method Name   :  setActiveTermedOrFutureVendor()
     * Description   :  This is used to call corresponding method based on available Active or Future Or Termed Programlist 
     * Return Type   :  void
     * Parameters    :  List of Activeprogram, FutureProgram, Termedprogram, EffectiveDt, EndDt
    */

    public void setActiveTermedOrFutureVendor(List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorActiveProgramList, List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorTerminatedProgramList, List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorFutureProgramList, List<Date> progEffDateList, List<Date> progEffFutrDateList, List<Date> progEndDateList)
	{//it will check for availability of Active Program List, else Future Program List or else Terminated Program List
      if(!vendorActiveProgramList.isEmpty()){
        setActiveVendor(vendorActiveProgramList, progEffDateList);  
      }else if(!vendorFutureProgramList.isEmpty()) {
        setFutureVendor(vendorFutureProgramList, progEffFutrDateList); 
      }else if(!vendorTerminatedProgramList.isEmpty()) {
        setTerminatedVendor(vendorTerminatedProgramList, progEndDateList); 
      } 
    }
    

	/*
     * Method Name   :  setActiveVendor()
     * Description   :  This is the used to populate Active Vendor Info based on latest Effective date
     * Return Type   :  void
     * Parameters    :  List of ActiveProgram, effectivedt
    */

    public void setActiveVendor(List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorActiveProgramList, List<Date> progEffDateList)
	{
     List<String> progEffDateSplit = new List<String>();
     date MaxDate;
      if(!vendorActiveProgramList.isEmpty() && !progEffDateList.isEmpty()){
        //calculatest latest program Effective date
         progEffDateList.sort(); 
         MaxDate = progEffDateList[progEffDateList.size()-1]; 
         //Iterate through List of ActiveProgram to compare progEffDt from ActiveProgramList with Maxdate and fetch corresponding Accolade programdetails
         for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorActiveProgramList)
         { 
           progEffDateSplit = programoffer.ProgramOfferEffectiveDate.split('/');
           Date progEffDate = Date.newInstance(Integer.valueOf(progEffDateSplit[2]),Integer.valueOf(progEffDateSplit[0]),Integer.valueOf(progEffDateSplit[1]));
             if(progEffDate == MaxDate){
                 if(!String.ISBLANK(programoffer.VendorIdentifierDescription)) sProgramName = programoffer.VendorIdentifierDescription; 
                 if(!String.ISBLANK(programoffer.VendorBenefitCodeDescription)) sProgramDesc = programoffer.VendorBenefitCodeDescription;
                 if(!String.ISBLANK(programoffer.ProgramOfferEffectiveDate)) sProgramEff = programoffer.ProgramOfferEffectiveDate; 
                 if(!String.ISBLANK(programoffer.ProgramOfferEndDate)) sProgramEnd = programoffer.ProgramOfferEndDate;
                 break;
             }
         }
      } 
    }

    /*
     * Method Name   :  setFutureVendor()
     * Description   :  This is the used to populate Future Vendor Info based on earliest Effective date
     * Return Type   :  void
     * Parameters    :  List of FutureProgram, effectivedt
    */

    public void setFutureVendor(List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorFutureProgramList, List<Date> progEffFutrDateList)
	{
     List<String> progEffFutrDateSplit = new List<String>();
     date MinDate;
      if(!vendorFutureProgramList.isEmpty() && !progEffFutrDateList.isEmpty()){
         //calculatest earliest program Effective date
         progEffFutrDateList.sort(); 
         MinDate = progEffFutrDateList[0]; 
         //Iterate through List of FutureProgram to compare progEffDt from FutureProgramList with Mindate and fetch corresponding Accolade programdetails
         for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorFutureProgramList)
         { 
           progEffFutrDateSplit = programoffer.ProgramOfferEffectiveDate.split('/');
           Date progEffDate = Date.newInstance(Integer.valueOf(progEffFutrDateSplit[2]),Integer.valueOf(progEffFutrDateSplit[0]),Integer.valueOf(progEffFutrDateSplit[1]));
             if(progEffDate == MinDate){
                 if(!String.ISBLANK(programoffer.VendorIdentifierDescription)) sProgramName = programoffer.VendorIdentifierDescription; 
                 if(!String.ISBLANK(programoffer.VendorBenefitCodeDescription)) sProgramDesc = programoffer.VendorBenefitCodeDescription;
                 if(!String.ISBLANK(programoffer.ProgramOfferEffectiveDate)) sProgramEff = programoffer.ProgramOfferEffectiveDate; 
                 if(!String.ISBLANK(programoffer.ProgramOfferEndDate)) sProgramEnd = programoffer.ProgramOfferEndDate;
                 break;
             }
         }
      } 
    }
    
	/*
     * Method Name   :  setTerminatedVendor()
     * Description   :  This is the used to populate Terminated Vendor Info based on latest End date 
     * Return Type   :  void
     * Parameters    :  List of TerminatedProgram, enddt
    */

    public void setTerminatedVendor(List<GBEServiceResponse_H_HUM.VendorProgramOffer> vendorTerminatedProgramList, List<Date> progEndDateList)
	{
     List<String> progEndDateSplit = new List<String>();
     date MaxDate;
      if(!vendorTerminatedProgramList.isEmpty() && !progEndDateList.isEmpty()){
         //calculatest latest program End date
         progEndDateList.sort(); 
         MaxDate = progEndDateList[progEndDateList.size()-1]; 
         //Iterate through List of TerminatedProgram to compare progEndDt from TerminatedProgramList with Maxdate and fetch corresponding Accolade programdetails 
         for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorTerminatedProgramList)
         { 
           progEndDateSplit = programoffer.ProgramOfferEndDate.split('/');
           Date progEndDate = Date.newInstance(Integer.valueOf(progEndDateSplit[2]),Integer.valueOf(progEndDateSplit[0]),Integer.valueOf(progEndDateSplit[1]));
             if(progEndDate == MaxDate){
                 if(!String.ISBLANK(programoffer.VendorIdentifierDescription)) sProgramName = programoffer.VendorIdentifierDescription; 
                 if(!String.ISBLANK(programoffer.VendorBenefitCodeDescription)) sProgramDesc = programoffer.VendorBenefitCodeDescription;
                 if(!String.ISBLANK(programoffer.ProgramOfferEffectiveDate)) sProgramEff = programoffer.ProgramOfferEffectiveDate; 
                 if(!String.ISBLANK(programoffer.ProgramOfferEndDate)) sProgramEnd = programoffer.ProgramOfferEndDate;
                 break;
             }
         }
      }
    }
    

     /**
     * Returns responses of continuation object
     * <p>
     * remote action Method to parse the PBE service response
     *
     * @param PolicyMemberDetail_DTO_HUM object
     * @return Object
     */
     @RemoteAction
    public static object invokePBECall(PolicyMemberDetail_DTO_HUM PBERequestDTO)
    {
        return PolicyMemberDetail_S_HUM.SendRequestPBEService(PBERequestDTO);
    }

    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the PBE service response
     *
     * @param label object
     * @param continuation state object
     * @return Object
     */
    public static object parsePBEResponse(List <string> labels, Object State)
    {
        return PolicyMemberDetail_S_HUM.parsePBEResponse(labels, state);
    }
    
     /*
	 * This method Is to create the list of floating links which are visible at the top of the customized Policy Detail VF pages 
	 * @param  NA
	 * @return void
	 */
    public void createFloatingList()
    {
        lstFloatLinkelem = new list<string>();
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy1);   //Policy Member Detail
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy2);   //Policy Details
		if(sOIfieldOnOffswitch == True && !bIsPharmacyUser) {
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy3);   //Other Insurance
        }

         //Adding issue state check to display link
        if(bProductTypeCheck && (sPloyConcat.Issue_State__c == label.Ancillary_State_Code_KY || sPloyConcat.Issue_State__c == label.Ancillary_State_Code_LA || sPloyConcat.Issue_State__c == label.Ancillary_State_Code_SC || (sPloyConcat.Issue_State__c == label.Ancillary_State_Code_OH && HUMUtilityHelper.isCRMFunctionalityON('2444097')) && memberancillaryOnOffswitch ))    //Medicaid Policy Details
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy10);
        }
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy4);   //Case History
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy5);   //Eligibility Information
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy6);   //MyHumana Details        
        if(RecordTypeNm != ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER && bagnosticswitch)
        {
            blegacyCntHistry = true;
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy7);   //Legacy Contact History
        }

        if(RecordTypeNm == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER && !bagnosticswitch){
            blegacyCntHistry = true;
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy7);   //Legacy Contact History
        }

        if(!bIsPharmacyUser)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy8);   //Member Ids
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy9);   //Policy Members
            
        }
        if(sPloyConcat.Policy_Platform__c == ConsumerManagementConstants_HUM.VOB_PLATFORM_EM_HUM)
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkPolicy11);   //MTV Remarks
        }
    }
	
	/*
     * REQ-383577 - Integration for EDI Group 
     * @method - remote action method to parse the EDI service response
     * @param - groupId
     * @return - Continuation Object
    */
	@RemoteAction
    public static Continuation getEDIDataCntrlMethod(string groupId){
		return PolicyMemberDetail_S_HUM.sendRequestEDI(groupId);
    }
    
	/*
     * REQ-383577 - Integration for EDI Group 
     * Returns responses of continuation object
     * @method - Callback Method to parse the EDI service response 
     * @param - label object and continuation state object
     * @return - Object
    */
    public static object processEDIResponse(List <string> labels, Object State){
    	return PolicyMemberDetail_S_HUM.processEDIResponse(labels, state);    
    }

    /* Method Name : infoSet
    * Description : Method to get all details required for the service
    * Return type : void
    * Parameter   : NA
    */
    public void infoSet()
    {       
        try
        {
            list<Policy_member__c> lstPolMem = MemberAncillaryRequest_D_HUM_V2.getMemberName(sId);
            if(!lstPolMem.isEmpty()) oPolMem = lstpolMem[0];
            if (oPolMem.Product_Type__c.equalsIgnoreCase('MCD')) bProductTypeCheck = true;
            list<Member_ID__c> lstMemIdObj = MemberAncillaryRequest_D_HUM_V2.getMemberIDObjDetails(sId);        
            if(lstMemIdObj!=null && !lstMemIdObj.isEmpty() && lstMemIdObj[0].Member_Dependent_Code__c!=null) sDependentCode = lstMemIdObj[0].Member_Dependent_Code__c;

            if(lstMemIdObj!=null && !lstMemIdObj.isEmpty() && lstMemIdObj[0].Name!=null) sMemIdBase = lstMemIdObj[0].Name;             
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'infoSet');
           
        }   
    }
/* Method Name : initiateMbeRequest
    * Description : Method to call the service on page load and display the data in Medicaid Policy Details section
    * Return type : Continuation
    * Parameter   : NA
    */
    public Object initiateMbeRequest() 
    {
      try
      {
          MBEPlusRequest_DTO_HUM oMbeReq= new MBEPlusRequest_DTO_HUM();
		  //As a part of REQ- 1918663 To fix error - Bulkify Apex Methods Using Collections In Methods - instead of passing Policy_Member__C object, now passing list of Policy_Member__C to the raceStructureMBEService method.
          List<Policy_Member__C> lstPolMem = new List<Policy_Member__C>();
          lstPolMem.add(oPolMem);
          oMbeReq = oMbeService.raceStructureMBEService(lstPolMem); 
          
        if((bProductTypeCheck!=null && bProductTypeCheck) && oMbeReq!=null)
        {     
			  
              HUM_Webservice_Callout__c oSvcMbe = HUM_Webservice_Callout__c.getValues('MBEPlusService'); 
              integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcMbe.Timeout__c)/1000;
              HttpRequest oRequest = BenefitsV2_S_HUM.builtHttpRequest(oSvcMbe);
              Continuation con = new Continuation(iTIMEOUT_INT_SECS);
              oRequest.setBody(JSON.serialize(oMbeReq));
              sRequest = con.addHttpRequest(oRequest); 
              con.continuationMethod='processMbeResponse';
              return con;
        }
        else
        {
            return null;
        }   
      } 
      catch(Exception ex)
       {
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'initiateMbeRequest');
            return null;
       }   
    }
    
    /*
    * Method Name   :  processMbeResponse
    * Description   :  Method is used to get response from Mbe service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    
    public void processMbeResponse()
    {   
        try 
        {
            HttpResponse oResponse = Continuation.getResponse(sRequest);
            
            if(oResponse != null)
            {
                mbeResponseparse(oResponse);
            }
        }
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'processMbeResponse');
        }
        
    }
    
    /*
    * Method Name   :  mbeResponseparse
    * Description   :  Method is used to parse response from Mbe Service.
    * Return Type   :  void
    * Parameters    :  HttpResponse oResponse
    */
   public void mbeResponseparse(HttpResponse oResponse)
    {
        try
        {
            MbeInfo = new MBEResponseV1_DTO_HUM();     
            integer iStatusCode = oResponse.getStatusCode();
            if (iStatusCode == GlobalCommonConstants_HUM.HTTP_OK_HUM || iStatusCode == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)
            {    
               MbeInfo = oMbeService.parseMbeRaceresponse(oResponse.getbody());  
              
            }
        }       
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'mbeResponseparse');
        }    
    }

    
    /* Method Name : initiateAncillaryRequest
    * Description : Method to call the service on page load and display the data in Medicaid Policy Details section
    * Return type : Continuation
    * Parameter   : NA
    */
    public Object initiateAncillaryRequest() 
    {
      try
      {
        //US 1481853 Member Ancillary Service call will get triggerred only for StateCode 'KY','LA','SC'
        Boolean bAncillary = True;
        //Returns 'KY','LA','SC' value into ancillaryStateCode with respective Policy Member Issue State or else 'Null'
        Ancillary_State_Code__c ancillaryStateCode= Ancillary_State_Code__c.getValues(oPolMem.Issue_State__c);
        if(HUMUtilityHelper.isCRMFunctionalityON('1481853')){
           //bAncillary sets to 'True' when StateCode 'KY','LA','SC' or Else it sets to 'False'
           bAncillary = (ancillaryStateCode == null) ? False : True;    
       	}
        //Member Ancillary service call will get hold whenever 1481853 switch is ON and Issue State is not 'KY','LA','SC'   
        if (bAncillary)
        {  
            MemberAncillaryRequest_H_HUM_V2 oAncillaryReq= new MemberAncillaryRequest_H_HUM_V2();
            //REQ - 403709 iEntityTypeList variable has been removed as it is no more used  
            oAncillaryReq =  oAncillaryService.assignValuesToAncillaryReq(oPolMem , sMemIdBase, sDependentCode);        
            if((bProductTypeCheck!=null && bProductTypeCheck) && oAncillaryReq!=null)
            {     
                // REQ-403709 Pointing Ancillary Service V2 Version
                HUM_Webservice_Callout__c oSvcAncillary = HUM_Webservice_Callout__c.getValues('GetMemberAncillary_V2');  
                integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcAncillary.Timeout__c)/1000;
                HttpRequest oRequest = new HttpRequest();
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                string sJsonRequest = JSON.Serialize(oAncillaryReq);              
                oRequest = oAncillaryService.setupAncillaryRequest(oRequest, oSvcAncillary, sJsonRequest);              
                sRequest = con.addHttpRequest(oRequest);              
                con.continuationMethod='processAncillaryResponse';
                return con;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }   
      } 
      catch(Exception ex)
       {
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'initiateAncillaryRequest');
            return null;
       }   
    }
    
    /*
    * Method Name   :  processAncillaryResponse
    * Description   :  Method is used to get response from Ancillary service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    
    public void processAncillaryResponse()
    {   
        try 
        {
            HttpResponse oResponse = Continuation.getResponse(sRequest);            
            String result = oResponse.getBody();
            if(oResponse != null)
            {
                ancillaryResponseparse(oResponse);
            }
        }
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'processAncillaryResponse');
        }
        
    }
    
    /*
	* Method Name : bShowEligibilityInfoInHeader
	* Description : This property is used to get/set OnOFFSwitch Status to display Eligibility Info in Header .
	* Return type : Boolean
	* Parameter   : NA
	*/
	public Boolean bShowEligibilityInfoInHeader
    {
        get
        {
         return HUMUtilityHelper.isCRMFunctionalityON('1114854');	
        }
        set;
    }        

    /*
    * Method Name   :  ancillaryResponseParse
    * Description   :  Method is used to parse response from Ancillary Service.
    * Return Type   :  void
    * Parameters    :  HttpResponse oResponse
    */
    @testvisible public void ancillaryResponseParse(HttpResponse oResponse)
    {
        try
        {
            AncillaryInfo = new MemberAncillaryResponse_DTO_HUM_V2();     
            integer iStatusCode = oResponse.getStatusCode();
            if (iStatusCode == GlobalCommonConstants_HUM.HTTP_OK_HUM || iStatusCode == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM)
            {
                AncillaryInfo = oAncillaryService.processResponse(oResponse.getbody());                
            }
        }       
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'PolicyMemberDetail_CE_HUM', 'ancillaryResponseParse');
        }    
    }
	
	 /*
    * Method Name   :  getUnifiedSearchDeatils
    * Description   :  Method is used to render the unified search button.
    * Return Type   :  void
    */
	    @testvisible private void getUnifiedSearchDeatils() {   
        string sBrowserContext=ApexPages.currentPage().getHeaders().get('USER-AGENT');
        
        if(sBrowserContext.contains(ConsumerManagementConstants_HUM.sChromeBrowser) || sBrowserContext.contains(ConsumerManagementConstants_HUM.sEdgeBrowser)){ 
        bShowUnifiedSearch = HUMUtilityHelper.isCRMFunctionalityON('ConsumerMgmtUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible(); 
        }
    }
    
    /* Method Name   :    closePopUp
     * Description   :    Close the pop up on interaction
     * Return Type   :    NA
     * Parameters    :    NA
    */
    public void closePopup() 
    {        
        try
        {  
            bPopup = false;  
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PolicyMemberDetail_CE_HUM','closePopup');
        }
    }

    /* Method Name  :    saveAndContPopup
    * Description   :    Open the pop up on interaction
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public PageReference saveAndContPopup() { 
        bPopUp = true;    
        return null;
   }
}