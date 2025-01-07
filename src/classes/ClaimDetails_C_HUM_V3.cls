/*****************************************************************************************
Apex Class Name :  ClaimDetails_C_HUM_V3 
Version         : 1.0
Function        : Controller to VF Page
                  This class Is used to display Claim Details.
Created Date    : 07/22/2020
Test Class      : ClaimDetails_T_HUM_V3

Modification Log:  
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Prasanthi Kandula             25441              04/12/2016           Original Version
 *   Muralidhar Kollu              25441              04/19/2016           Added code to implement MTV Remarks
 *   Prasanthi Kandula             26076              04/26/2016           Calling the Service method LineItemsFooter to read Total Claim Processing Details And display on Page.
 *   Muralidhar Kollu              26844              05/02/2016           Claim Payments section implementation
 *   Kritika Popat                 26844              05/05/2016           Claim Payments accordion implementation And break down
 *   Gargey Sharma                 27407              05/13/2016           Defect 226030: Pre-Determination And Ptient Account Number have same values
 *   Muralidhar Kollu              27319              05/12/2016           Claim Payments Sanity fixes   
 *   Muralidhar Kollu              27819              05/19/2016           226214.0001 - removing empty list check for both MTV And Payments data
 *   Prasanthi Kandula             27974              05/19/2016           REQ-24426, REQ-268861 To fetch CodeEdit Details from getCodeEditDetails service
 *   Muralidhar Kollu              27974              05/20/2016           Member Claim Statements implementation 
 *   Muralidhar Kollu              28321              05/25/2016           226214.0001 - Show'No records to display' and remove data table display both MTV and Payments sections   
 *   Prasanthi Kandula             28586              05/31/2016           REQ-268861 Encode ClaimInquiryTool url to base64
 *   Prasanthi Kandula             28390              06/07/2016           Defect# 228315.0001 fix Return Sequence Number on Line Items Last Processed Date filter for MTV claims.
 *   Pallavi Ravishankar           29226              06/03/2016           Added Logic for REQ 263142
 *   Santhi Mandava                29879              06/11/2016           Implemeted logging.
 *   Pallavi Ravishankar           30143              06/15/2016           Added logic for Defect Fix 229874.0001
 *   Muralidhar Kollu              30622              06/20/2016           REQ - 270092 - update payment details service input   
 *   Pallavi Ravishankar           30916              06/23/2016           Removed logic to check for zero in client number in setHiddenValues() 
 *   Prasanthi Kandula             31877              07/05/2016           REQ - 270129 Add Sequence Column to Line Items Table And Claim Payment Table
 *   Muralidhar Kollu              32214              07/05/2016           Defect # 232217.0001 - 249360: Payee field Not returning correct values    
 *   Muralidhar Kollu              32469              07/08/2016           Defect # 231477.0001 244472: Statement dates don't make sense for claim listed
 *   Prasanthi Kandula             32709              07/14/2016           Defect #233055.0001 fix -  Updated standardMapForLogging map labels with the correct labels mentioned on defect
 *   Pallavi Ravishankar           33054              07/15/2016           Defect fix #231974.0001 -Added logic to get the Policy Member Id for logging from MemberId          
 *   Prasanthi Kandula             33598              07/22/2016           Prod defect fix  D-00477 - Changing the logic to form post for Claim Inquiry tool.
 *   Praveen Kumar Parimi          34166              08/01/2016           Fixing the critical technical defect during UAT.
 *   Muralidhar Kollu              34245              08/03/2016           defect# 235498- REQ-277220-ICD Change: Pass empty parameters “SRCNPIID” And “ProviderID” for GetPaymentDetails
 *   Harika Devi Kalidindi         34240              08/03/2016           Defect 229652.0001 fix- Added sClaimTypeCd variable to read  ClaimTypeCd value from the url
 *   Prasanthi Kandula             36268              08/22/2016           REQ - 277892 Defect 235497: Logic for Displaying Payment Information
 *   Prasanthi Kandula             38140              09/02/2016           Defect 240391.0001 Fix Assigning Payment Method
 *   Santhi Mandava                47535              11/11/2016           Implemented REQ - 290132.
 *   Saurabh Kaple                 157136             01/10/2018           REQ - 348181 Mobius Service- Use Genkey from platform instead of MBE genkey conversion  
 *   Prasanthi Kandula             212620             03/06/2018           REQ - 355817 - Incorporated changes as part of Claims V3 service change.
 *   Praveen Kumar Parimi          230338             05/17/2018           Added the New CAS WEB UI references as part of REQ - 324821.
 *   Sunil Bukya                   239093             06/01/2018           REQ - 366901 - Implemented Member_Id__c platform encryption changes.
 *   Muralidhar Kollu              288893             09/20/2018           REQ - 357260 - Provider Claim Statements section code
 *   Prudhvi Pamarthi              280616             09/26/2018           REQ - 371439: Fix for CA Ticket 7363284 Error msg. No cases found for the rage you entered.
 *   Raja Sekhar Reddy MV          292092             09/26/2018           REQ - 375749 - Switch Functionality for member And Provider Send link   
 *   Muralidhar Kollu                                 10/11/2018           REQ - 357260 - code to send multiple remitID to Provider Claim Statements service
 *   Muralidhar Kollu                                 10/31/2018           REQ - 357260 - 389467.0001 defect fix
 *   Santhi Mandava                                   03/12/2019           Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
 *   Praveen Kumar Parimi                             07/30/2019           REQ 403782 - Requirement to update PreDeterminationNumber to PreDeterminationIndicator. 
 *   Amar Gadewar                                     08/05/2019           REQ - 406022 AHT - All Users - Cause Code Hyperlink on Claim Detail page (RF)
 *   Yogesh Gupta                                     09/18/2019           REQ - 408299 - Create a CRM Application Launcher - CAS (batch 5 links)
 *   Prasanthi Kandula                                11/20/2019           REQ - 416044 Point Mobius Service methods to 2.3 version
 *   Sumit Pharande                                   12/05/2019           REQ - 415182 - Create a CRM Application Launcher - MTV (batch 6 links)
 *   Anjani Vaddadi                                   02/25/2020           REQ - 764250
 *   Prasanthi Kandula                                03/12/2020           US - 746884 Implemented switch for PrimarySpecialty field      
 *   Sayali Nemade                                    04/07/2020           REQ - 844876  MF 6 - Renaming new links, removing old links 
 *   Amit Agarwal                                     05/04/2020           User Story 1081523: PR00091574 - MF 1 - AHT - All Users - Ability to hover over claim line item column value to see header label (CRM)
 *   Sayali Nemade                                    04/07/2020           REQ - 844876  MF 6 - Removed Code
 *   Sunil Kumar Reddy Y                              05/22/2020           User Story 1114855: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 6/5 and 6/19 releases (CRM)
 *   Sayali Nemade                                    06/22/2020           REQ - 844876  MF 6 - Renaming new links, removing old links (Applauncher Full Launch) 
 *   Trina Ghosh                                      07/17/2020           US# - 1039163 Primary Speciality Fields Logging- SWITCH
 *   Prasanthi Kandula                                07/22/2020           US - 1031530 Refactored code to improve performance 
 *   Ramakrishna Gundeti                              07/01/2020           US #1210909 - PCP Not showing correctly under Claims PCP_PCD
 *   Trina Ghosh                                      09/29/2020           US# - 1527283 - OLD Cas link fix
 *   Priyanka Puranik                                 1/28/2021            REQ - 1759875 PR00094167 - MF 4 - TECH - SF- CRMS – Legacy App Launcher code clean up MTV, CAS
 *   Priyanka Puranik                                 1/29/2021            REQ - 1759875 Removing check for permission set
 *   Prasanthi Kandula                                02/01/2021           US-1669846 SonarQube critical security fixes  
 *   Dan Laratta                                      03/10/2021           US 2016399: Ability to display Platform Number along with Platform Name on the Claim Detail page (RF)
 *   Satyanarayana chenna                             04/09/2021           US 1905124 PR00094226 - CR AEP readiness - Low Security Issues
 *   Dan Laratta                                      04/15/2021           US 2184269:Ability to display Platform Number along with Platform Name on the Claim Detail page (CRM)
 *   Atia Uzma                                        04/28/2021           User Story 2230288: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 04/16/2021, 05/14/2021 and 05/28/2021 releases (CRM)
 *   Prasanthi Kandula                                05/18/2021           MTV Remarks service error handling        
 *   Abhishek Mangutkar                               05/24/2021           US - 2201523 -  Add 'Claims Payment Responsibility' Field in Claim Line Item Accordion (CRM Service)
 *   Prashant Moghe                                   07/02/2021           US - 2417605 -  T1PRJ0002736 (PR00094226) - SF - SOQL, FLS, and Potential - CR AEP readiness - High/Medium Security Issues
 *   Nirmal Garg                                      09/16/2021           Added browser logic for coveo
 *   Apurva Urkude                                    10/22/2021           REQ - 2132792
 *   Nirmal Garg                                      11/11/2021           Added CRMS switch for coveo
 *   keshava Reddy                                    01/19/2022           User Story 2482702: T1PRJ0001827 - MF 1 - RESEARCH - AHT - Ability to hover over on the EX Code column on the Claim Detail Page to see EOB Or EOR details (RF)
 *   Anuradha Gajbhe                                  02/07/2022           US #2732975 - Remove 'Show More' - Provider Details: Primary Specialty
 *   Vamshi Krishna Pemberthi                         02/07/2022           US #2732959 - Remove 'Show More' - Claim Details: Diagnosis Code
 * Vamshi Krishna Pemberthi 						  02/10/2022			US #2732959 - Remove 'Show More' - Claim Details: Diagnosis Code floating links
*Tharun Madishetti                                   06/06/2022        US-3384618:T1PRJ0272555 -TECH - SF - Priority Central High Priority - ClaimDetails VF Page 
*   Pinky Vijur                                       02/22/2023         User Story 4200769: T1PRJ0883127- Author Shutdown- MF 2- Display and Resend Author generated tEOB’s in CRM Service (Classic)
******************************************************************************************************************************/
 
public with sharing class ClaimDetails_C_HUM_V3 extends SObjectDescriptorImpl_H_HUM 
{
    @TestVisible private String sClaimGenKey ='';
    @TestVisible private String sRequest  = '';
    @TestVisible private String sPaymentRequest  = '';     
    @TestVisible private String sClaimStatementsRequest  = '';               
    @TestVisible private String sPrvdrClaimStatementsRequest  = '';                    
    @TestVisible private String sDeductAmt = '';
    @TestVisible private String sCopayAmt = '';
    @TestVisible private String sCoInsAmt = '';
    @TestVisible private ClaimDetailMTVRemarks_S_HUM oclaimMTVRemarks = new ClaimDetailMTVRemarks_S_HUM(); 
    @TestVisible private List<ClaimDetailMTVRemarks_DTO_HUM> lstMTVRemarks = new List<ClaimDetailMTVRemarks_DTO_HUM>();
    @TestVisible private List<ClaimDetailMTVRemarks_DTO_HUM> lstMTV = new List<ClaimDetailMTVRemarks_DTO_HUM>();
    @testVisible private List<ClaimsPaymentDetails_DTO_HUM> lstPayments = new List<ClaimsPaymentDetails_DTO_HUM>();
    @TestVisible private String sDependentCode = '';
    @TestVisible private List<String> lstSplitData = NULL;
    @TestVisible private Set<String> lstRemitNumber = New Set<String>();
    @TestVisible private ClaimMobiusDetails_S_HUM oclaimStatements = new ClaimMobiusDetails_S_HUM();    
    @TestVisible private ClaimMobiusDetails_S_HUM oPrvdrStatements = new ClaimMobiusDetails_S_HUM();         
    @TestVisible private String sMbrRespAmt = '';
    @TestVisible private String sPaidAmt = '';
    @TestVisible private ClaimPaymentDetails_S_HUM oclaimPayments = new ClaimPaymentDetails_S_HUM();     
    @TestVisible private string sStartDate;
    @TestVisible private string sEndDate;
    @TestVisible private string sProviderName;
    @TestVisible private string sProviderId;
    @TestVisible private String sNPI;
    @TestVisible private string sStatus; 
    public string sAccountId{get;set;}   
    public Boolean NBANewAlertEngineSwitch{get;private set;}
    public boolean bCoveoUser {get;set;}
    Public Static final String ChromeBrowser = 'Chrome/';
    Public Static final String EdgeBrowser = 'Edg';
    public Boolean bSwitch_2132792 {get;set;} 
    public Boolean bSwitch_4200713 {get;set;}

    /*
     * Method Name   :  ClaimDetails_C_HUM_V3
     * Description   :  This is the constructor of the class 
     * Return Type   :  NA
     * Parameters    :  NA
    */
    public ClaimDetails_C_HUM_V3() 
    {    
        NBANewAlertEngineSwitch = HUMUtilityHelper.isCRMFunctionalityON('1041229');
        sPolMemID = ApexPages.currentPage().getParameters().get('Id');
        sClaimTypeSummary = ApexPages.currentPage().getParameters().get('ClaimType');
        if(sClaimTypeSummary!=null && String.isNotEmpty(sClaimTypeSummary.escapeHtml4()) && (sClaimTypeSummary.escapeHtml4().equalsIgnoreCase('Medical') || sClaimTypeSummary.escapeHtml4().equalsIgnoreCase('Ambulatory') || sClaimTypeSummary.escapeHtml4().equalsIgnoreCase('Hospital')))  {  bisClaimTypeMedical = true; sClaimType = 'Medical';}
        else sClaimType = 'Dental';
        sClaimNumber = ApexPages.currentPage().getParameters().get('ClaimNbr');
        sClaimGenKey = ApexPages.currentPage().getParameters().get('ClaimGenKey');
        sStartDate = ApexPages.currentPage().getParameters().get('StartDate');
        sEndDate = ApexPages.currentPage().getParameters().get('EndDate');
        sAdjInd = ApexPages.currentPage().getParameters().get('AdjustInd');
        sPlatformCode = ApexPages.currentPage().getParameters().get('PlatformCd');
        sProviderName = ApexPages.currentPage().getParameters().get('ProviderName');
        sProviderId = ApexPages.currentPage().getParameters().get('ProviderId');
        sNPI = ApexPages.currentPage().getParameters().get('Npi');
        sStatus = ApexPages.currentPage().getParameters().get('Status');
        sMemberId = getHTMLEncodedPageParameter('MemberId');
        claimPolMemID =getClaimPolicyMemberID();
        sDeductAmt = ApexPages.currentPage().getParameters().get('Deduct');
        sCopayAmt = ApexPages.currentPage().getParameters().get('Copay');
        sCoInsAmt = ApexPages.currentPage().getParameters().get('CoIns');
        sMbrRespAmt = ApexPages.currentPage().getParameters().get('MbrRespAmt'); 
        sPaidAmt = ApexPages.currentPage().getParameters().get('PaidAmt');
        bShowServiceError = false;
        bShowPaymentsServiceError = false;
        bShowMTVServiceError = false;
        bShowClaimStatementsServiceError = false;               
        bShowPrvdrClaimStatementsServiceError = false;   
        bShowMTVTable = false;
        bMTVSecondCall = false;
        bShowPaymentTable = false;  
        bShowClaimStatementsTable = false; 
        bShowPrvdrClaimStatementsTable = false;         
        bDisplayPaymentsInfoMsg = false;
        bPaymentZeroCheckNum  = false;
        bPaymentNonZeroCheckNum = False;
        bShowNewPSLogic = CRMFunctionalityenabler_H_HUM.Checkflagvalue('Provider_Primary_Speciality_Switch');
        bShowMoreSwitch = CRMFunctionalityenabler_H_HUM.Checkflagvalue('ClaimsShowMoreSwitch');
        oclaimdetails  = New ClaimDetails_DTO_HUM_V2();
        setHiddenValues();     
        getLoggedinUserQueue();
        createFloatingList();
        if (FeatureManagement.checkPermission('Reprint_Claim_Access'))    {
            oClaimStatementsUI.isPermission = true; 
        }
        else {
            oClaimStatementsUI.isPermission = false;
        }
        
        bPrimarySpecSwitch = ([SELECT Id,  Primary_Specialty_Switch__c FROM Provider_functionality_switch__c where Primary_Specialty_Switch__c = true limit 1]).size()>0?true:false;
        bCPRSwitch = HUMUtilityHelper.isCRMFunctionalityON('2201523');
        string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
        if(string.isNotBlank(sBrowserContext)){
            if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
                bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
           }
       }
        bSwitch_2132792 = HUMUtilityHelper.isCRMFunctionalityON('2132792');
        bSwitch_4200713 = HUMUtilityHelper.isCRMFunctionalityON('4200713');
    } 
     
    public List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> oclaimlineItems = new List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine>();
    public Map<String, ClaimDetailMTVRemarks_DTO_HUM> mapMTV = new Map<String, ClaimDetailMTVRemarks_DTO_HUM>();
    public boolean setPagindID = false;
    public boolean bShowNewPSLogic {get;set;}
    public boolean bShowMoreSwitch{get; set;}
    Public Enum EnumPlatform{EM,LV,Undefined,HP}
    public enum EnumDocumentType{SS,EOB,EOR}
    public enum EnumStatementType{Provider, Member}
    public enum EnumYesNo{Y, N, Undefined}
    public string sJSONPageLoad {get; set;}
    public ClaimDetailUIValues_DTO_HUM oClaimDetailUI = new ClaimDetailUIValues_DTO_HUM();
    public ClaimDetailUIValues_DTO_HUM.ClaimDetailsPage oClaimDetailPg = new ClaimDetailUIValues_DTO_HUM.ClaimDetailsPage();
    ClaimDetailUIValues_DTO_HUM.ClaimReusableValues oClaimReUsableVal = new ClaimDetailUIValues_DTO_HUM.ClaimReusableValues();
    ClaimDetailUIValues_DTO_HUM.ClaimLineItems oClaimLineItemsUI = new ClaimDetailUIValues_DTO_HUM.ClaimLineItems();
    ClaimDetailUIValues_DTO_HUM.ClaimPayments oClaimPaymentsUI = new ClaimDetailUIValues_DTO_HUM.ClaimPayments();
    ClaimDetailUIValues_DTO_HUM.ClaimStatements oClaimStatementsUI = new ClaimDetailUIValues_DTO_HUM.ClaimStatements(); 
    public string sClaimTypeSummary {get; set;}
    Public string claimPolMemID {get; set;} 
    Public string sDepartmentData {get; set;}
    Public string sClaimInquiryTool {get; set;} 
    public string sClaimNumber {get; set;}
    public string sPolMemID {get; set;}
    public String sProductType {get;set;}
    public string sClaimType {get; set;} 
    public boolean bisClaimTypeMedical {get; set;}
    public boolean bPrimarySpecSwitch {get; set;}
    public boolean bCPRSwitch{get;set;}
    public String sProduct {get;set;}
    
    Public String sAdjInd {
        get {    
            return sAdjInd.escapeHtml4();
        }
        set{ 
            if (value == null){
                eAdjInd=enumYesNo.Undefined;
                sAdjInd='';
            }
            else {
                sAdjInd= value.Trim().toUpperCase();
                if (sAdjInd == 'Y')
                    eAdjInd = enumYesNo.Y;
                else if (sAdjInd == 'N')
                    eAdjInd = enumYesNo.N;
                else
                    eAdjInd = enumYesNo.Undefined;
            }  
        } 
    }       
    public EnumYesNo eAdjInd{Get;Set;} 
    public string sPlatformCode {
        get{
            return sPlatformCode.escapeHtml4();
        }
        set{
            if(value==null){
                ePlatform = EnumPlatform.Undefined;
                sPlatformCode='';
            }
            else {
                sPlatformCode= value.Trim().toUpperCase();
                if (sPlatformCode== 'LV')
                    ePlatform = EnumPlatform.LV;
                else if (sPlatformCode== 'EM')
                    ePlatform = EnumPlatform.EM;
                else if (sPlatformCode== 'HP')
                    ePlatform = EnumPlatform.HP;
            }    
        } 
    } 
    public EnumPlatform  ePlatform{get;set;}
    public string sMemberId {get; set;}
    public Boolean bShowServiceError{get;set;}
    public Boolean bShowPaymentsServiceError{get;set;}
    public Boolean bShowMTVServiceError{get;set;}
    public Boolean bShowClaimStatementsServiceError{get;set;}          
    public Boolean bShowPrvdrClaimStatementsServiceError{get;set;}               
    public ClaimDetails_DTO_HUM_V2 oclaimdetails {get; set;}
    public List<string> lstDiagnosisCodes {get; set;}
    public string sClaimLineItemsjson {get;set;}
    public String sMTVJsonData { get; set; }
    public Boolean bShowMTVTable {get; set;}
    public Boolean bMTVSecondCall {get; set;}
    public Boolean bShowClaimStatementsTable{get; set;}          
    public Boolean bShowPrvdrClaimStatementsTable{get; set;}               
    private String sCreationDate;
    private String sCreationTime;
    private String sSurrogateId;
    private String sStartRemarkSequenceNumber;
    public String sjsonpaymentsdata { get; set; }      
    public Boolean bShowPaymentTable {get; set;}
    public String sPaymentCheckNumber {get; set;}
    public String sPaymentLastProcessDate {get; set;} 
    public String sRemittenceID {get; set;} 
    public String sPayeeCd {get; set;}           
    public String sjsonClaimStatementsdata { get; set; }               
    Public String sjsonPrvdrClaimStatementsdata { Get; Set; }                    
    Public Static List<ClaimsGetCodeEditDetails_DTO_HUM> oCodeEditDTO{Get;Set;} 
    public String sSequenceNumber {get; set;}       
    public String standardMapvalue {get; set;}
    public Boolean bDisplayPaymentsInfoMsg {get; set;}
    public Boolean bPaymentNonZeroCheckNum {get; set;}
    public Boolean bPaymentZeroCheckNum {get; set;}
    public String sPaymentMethod {get; set;}
    public String sRemittenceIds {Get; Set;}
    public String sPloyPlatCodeConcatNumber{get;set;}
	public list<string> lstFloatLinkelem{get;set;}
	
	public void createFloatingList()
    {
        lstFloatLinkelem = new list<string>();   
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail1); 
        if(sClaimType != 'Dental')
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail2); 
        }
        if(oclaimdetails.sDeficiencyInd == 'Y')
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail3); 
        }
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail4); 
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail5); 
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail6);
		if(sPlatformCode == 'EM')
        {
            lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail7); 
        }
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail8); 
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkClaimDetail9); 
    }
    
    //Capture Any DML Exceptions in errorLog object
    Public List<Error_Log__c> listErrorLogs 
    {
        get
        {
          if(listErrorLogs == Null){
          listErrorLogs = new List<Error_Log__c>();
          if(!listErrorLogs.isempty())
          listErrorLogs.addAll(listErrorLogs);
          }return listErrorLogs;
        }
        set;
    }
    
    
    public void SaveException()
    {
        HUMExceptionHelper.saveLog(listErrorLogs, false);
    }
    public Boolean bEXCodeColumnSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2977611');
        }
        set;
    }
    /*
     * Method Name   :  setHiddenValues()
     * Description   :  This method is used to fetch values for CASPrefill for ClaimPayment section
     * Return Type   :  void
     * Parameters    :  NA
     */
    public void setHiddenValues()
    {
        try
        {  
            //Fetching the URL
            HUM_Webservice_Callout__c cstGCP =  HUM_Webservice_Callout__c.getInstance(System.Label.HUMLaunchGCP);
            HUM_Webservice_Callout__c cstCASWEB =  HUM_Webservice_Callout__c.getInstance(System.Label.HUMLaunchCASWEBUI);
            if(cstGCP!=null)  oClaimPaymentsUI.sGCPUrl = cstGCP.End_Point_URL__c;
            if(cstCASWEB!=null)  oClaimPaymentsUI.urlcasweb = cstCASWEB.End_Point_URL__c;
            //Fetching Policy Member Details
            if(String.isNotBlank(sPolMemID))
            {
                list<Policy_member__c> lstpolMem = ClaimDetails_D_HUM_V2.getPolicyMember(ApexPages.currentPage().getParameters().get('Id').escapeHTML4());
                if(lstpolMem != NULL && lstpolMem.size() > 0) sAccountId = lstpolMem[0].Member__c;
                //Fetching Client Number,FirstName  
                if((lstpolMem!=null) && (!lstpolMem.isEmpty()))
                {
                    if (lstpolMem[0].Product_Type__c!=null)sProductType = lstpolMem[0].Product_Type__c;
                    if(lstpolMem[0].Policy__r.Client_Number__c!=null)  oClaimPaymentsUI.clientNo = lstpolMem[0].Policy__r.Client_Number__c;      
                    if(lstpolMem[0].Member__r.FirstName!=null)oClaimPaymentsUI.FirstName = lstpolMem[0].Member__r.FirstName;
                    //Fetching Relation Details from LVRelationShipCodeToCAS__c     
                    if(lstpolMem[0].Relationship_Code__c!=null)
                    {
                        list<LVRelationShipCodeToCAS__c> lstlvcas = ClaimDetails_D_HUM_V2.getLVCASTranslationDetails(lstpolMem[0].Relationship_Code__c);
                        if(((lstlvcas!=null)&&(!lstlvcas.isEmpty())) && lstlvcas[0].CASTranslation__c!=null)oClaimPaymentsUI.Relation = lstlvcas[0].CASTranslation__c;
                    }
                    if (lstpolMem[0].Product__c!=null)sProduct = lstpolMem[0].Product__c;
                }
                list<Member_ID__c> lstmemIdObj= ClaimDetails_D_HUM_V2.getMemberIDObjDetails(ApexPages.currentPage().getParameters().get('Id').escapeHTML4()); 
                if(((lstmemIdObj!=null) && (!lstmemIdObj.isEmpty())) && lstmemIdObj[0].Name !=null)oClaimPaymentsUI.MemId = lstmemIdObj[0].Name; 
            }
            oClaimPaymentsUI.applname = GLOBAL_CONSTANT_HUM.CASPrefillAppName;
            HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
            HUMCertificateURL__c oCauseCodeMentorLinkURL = HUMCertificateURL__c.getValues('CauseCodeMentorLinkURL');
            if(objHssURL != null && oCauseCodeMentorLinkURL != null) {
                oClaimLineItemsUI.urlCauseCodeMentorLink = objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(oCauseCodeMentorLinkURL.CertificateListURL__c));
            }
            oClaimPaymentsUI.AppEnvironment =  HUMConstants__c.getInstance('APPLAUNCHER_ENVIRONMENT').StringValue__c;
           }
        catch(Exception ex)
        {
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'setHiddenValues');
        }
    }
     
     /*
     * Method Name   :  getThis
     * Description   :  This method is used to return object of ClaimDetails_C_HUM_V3 
     * Return Type   :  ClaimDetails_C_HUM_V3
     * Parameters    :  NA
     */
    public ClaimDetails_C_HUM_V3 getThis()
    {
        return this;
    }
    
    
    /*
     * Method Name   :  getRecord
     * Description   :  This method always returns either NULL or an instance of sObject depending upon implementation.
     * Return Type   :  sObject 
     * Parameters    :  NA
     */    
    public sObject getRecord()
    {
        return NULL;
    }   
    
   /*
     * Method Name   :  getRecords
     * Description   :  This method always returns either NULL Or List Of sObjects depending
     *                  upon implementation. Use this method to get list of multiple records
     * Return Type   :  List of sObjects 
     * Parameters    :  NA
     */
    Public List<sObject> getRecords()
    {
        Return NULL;
    }
  
       
    /*
     * Method Name   :  claimDetailsRequest
     * Description   :  This method is used to call the Claim Details Service while loading the page.
     * Return Type   :  Object
     * Parameters    :  NA
     */
    public Object claimDetailsRequest() 
    {
       try
        {
            ClaimDetails_S_HUM_V2 oClaimDetailsService = new ClaimDetails_S_HUM_V2();
            ClaimsServiceDetailsRequest_H_HUM_V2 oClaimDetailsReq = new ClaimsServiceDetailsRequest_H_HUM_V2();
            oClaimDetailsReq = oClaimDetailsService.assignValuesToHelper(sClaimType,sClaimNumber.escapeHtml4(),sClaimGenKey);
            if(oClaimDetailsReq != null)
            {
                HUM_Webservice_Callout__c oServiceCalloutClaimsDetail = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsDetail_V3');  
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oServiceCalloutClaimsDetail.Timeout__c)/1000;  
                string sJsonRequest = JSON.Serialize(oClaimDetailsReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oClaimDetailsService.setHttpRequest(oRequest,oServiceCalloutClaimsDetail,sJsonRequest);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processResponse';
                sRequest = con.addHttpRequest(oRequest);
                sPolMemID = ApexPages.currentPage().getParameters().get('Id');
                return con;
            }
            else 
            {
               return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'claimDetailsRequest');
            return null;
        } 
    }
    

    /*
     * Method Name   :  processResponse
     * Description   :  This is the Callback method for Continous Object to process the Response data
     * Return Type   :  Object
     * Parameters    :  NA
     */  
     public Object processResponse() 
    {  
        try
        {
          HttpResponse oResponse = Continuation.getResponse(this.sRequest);
          if(oResponse != null)
          responseDeserialize(oResponse);
        }
        catch(Exception ex)
        {
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
            ApexPages.addMessage(myMsg);
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processResponse');
        }
        return null;  
    }
    
    /*
    * Method Name   :  responseDeserialize
    * Description   :  This method is used dederialize the response
    * Return Type   :  ClaimsServiceDetailsResponse_H_HUM_V2 
    * Parameters    :  HttpResponse 
    */
    public ClaimsServiceDetailsResponse_H_HUM_V2 responseDeserialize(HttpResponse sResponse)
    {
       try 
       {   
          Integer statusCode = sResponse.getStatusCode();
          if (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
          {
              ClaimsServiceDetailsResponse_H_HUM_V2 oResponse = new ClaimsServiceDetailsResponse_H_HUM_V2();
              oResponse = (ClaimsServiceDetailsResponse_H_HUM_V2)JSON.deserialize(sResponse.getBody(),ClaimsServiceDetailsResponse_H_HUM_V2.class); 
              if(oResponse.ClaimDetailReponse != null)
              {
                  ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailReponse oClaimDetailsResponse = oResponse.ClaimDetailReponse;
                  ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oClaimDetailsResult = oClaimDetailsResponse.ClaimDetailResult;
                  responseParse(oClaimDetailsResult);
                  
             }
             else
             {
                sClaimLineItemsjson = '{"data":""}'; 
                bShowServiceError = true;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                ApexPages.addMessage(myMsg);  
             }
         }
         else
         {
            sClaimLineItemsjson = '{"data":""}'; 
            bShowServiceError = true;
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
            ApexPages.addMessage(myMsg);
         }
         oClaimReUsableVal.ClaimNumber = sClaimNumber.escapeHtml4();
         if(lstDiagnosisCodes!=null) oClaimReUsableVal.DiagCode = lstDiagnosisCodes;
         oClaimReUsableVal.platformCode = sPlatformCode.escapeHtml4();
         oClaimReUsableVal.sClaimType = sClaimType.escapeHtml4();
         oClaimReUsableVal.Recid = sPolMemID.escapeHtml4();
         oClaimReUsableVal.bPrimaryspecialityShowMore = oclaimdetails.bShowMore;
         oClaimReUsableVal.listAllPrimarySpeciality = oclaimdetails.listPrimarySpeciality;
         oClaimReUsableVal.bPrimarySpecSwitch = bPrimarySpecSwitch;
         if(oclaimdetails.bShowMore != null && oclaimdetails.bShowMore)
             oClaimReUsableVal.sFullPrimarySpeciality= oclaimdetails.sProviderSpecAllRecords;
         if(string.isNotBlank(oclaimdetails.sProviderSpecFirstRecord)) {         
             oClaimReUsableVal.Primaryspeciality = oclaimdetails.sProviderSpecFirstRecord; 
             oClaimReUsableVal.sProviderSpecFirstRecord = oclaimdetails.sProviderSpecFirstRecord;
         }
         oClaimReUsableVal.bSwitchPS = bShowNewPSLogic;
         oClaimReUsableVal.bSwitchShowmorePS = bShowMoreSwitch;
         oClaimLineItemsUI.TIN = sProviderId.escapeHtml4();
         oClaimLineItemsUI.NPI = sNPI.escapeHtml4();
         oClaimPaymentsUI.PaymentNonZeroCheckNum  = bPaymentNonZeroCheckNum;
         oClaimPaymentsUI.PaymentZeroCheckNum = bPaymentZeroCheckNum;
         oClaimPaymentsUI.paymentMethod = oclaimdetails.sPaymentMethod;
         oClaimStatementsUI.memberId = sMemberId.escapeHtml4();
         oClaimStatementsUI.provIDVal = sProviderId.escapeHtml4();
         oClaimDetailPg.ClaimReusableValues = oClaimReUsableVal;
         oClaimDetailPg.ClaimLineItems = oClaimLineItemsUI;
         oClaimDetailPg.ClaimPayments = oClaimPaymentsUI;
         oClaimDetailPg.ClaimStatements = oClaimStatementsUI;
         sJSONPageLoad = JSON.serialize(oClaimDetailPg); 
       }
       catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'responseDeserialize');
           
        }  
		createFloatingList();
        return null;
    } 
    
    /*
    * Method Name   :  responseParse
    * Description   :  This method is used to parse the response
    * Return Type   :  N/A
    * Parameters    :  ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult 
    */
   @TestVisible private void responseParse(ClaimsServiceDetailsResponse_H_HUM_V2.ClaimDetailResult oClaimDetailsResult)
    {
        if(oClaimDetailsResult != null)
        {
            ClaimDetails_S_HUM_V2 oClaimsService = new ClaimDetails_S_HUM_V2();
            oclaimdetails = oClaimsService.ClaimDetails(oClaimDetailsResult,bPrimarySpecSwitch);
            lstDiagnosisCodes = new List<string>();
            if(oclaimdetails.lstDiagnosisCodes!=null)
            for(Integer i = 0; i< oclaimdetails.lstDiagnosisCodes.size() ;i++)
            {
                lstDiagnosisCodes.add(oclaimdetails.lstDiagnosisCodes[i]);   
            } 
            if(sDeductAmt!=null) sDeductAmt = sDeductAmt.escapeHtml4();
            if(sProviderName !=null) sProviderName = sProviderName.escapehtml4();
            
            if (String.isNotBlank(sPlatformCode) && String.isnotBlank(oClaimDetailsResult.SourceLvCASPrefixCode) && sPlatformCode == 'LV') {

                sPloyPlatCodeConcatNumber = sPlatformCode + ' ('+oClaimDetailsResult.SourceLvCASPrefixCode+')';
                sPloyPlatCodeConcatNumber = sPloyPlatCodeConcatNumber.escapeHtml4();
            } else {
                sPloyPlatCodeConcatNumber = sPlatformCode;
                sPloyPlatCodeConcatNumber = sPloyPlatCodeConcatNumber.escapeHtml4();
            }
            
            oClaimLineItemsUI.lineItemsFooter = oClaimsService.LineItemsFooter(oClaimDetailsResult, sDeductAmt, sCopayAmt, sCoInsAmt );
            oclaimlineItems = oClaimsService.ClaimLineItems(oClaimDetailsResult,sProviderName, sProviderId, sNPI);
            sSequenceNumber = oClaimsService.getSequenceNumberMTV(oclaimlineItems, string.valueOf(ePlatform),sProduct);
            sClaimLineItemsjson = JSON.serialize(oclaimlineItems); 
            sClaimLineItemsjson = '{"data":'+sClaimLineItemsjson+'}';            
            validateCheckNumberandRemidID(oclaimlineItems);            
            oClaimsService.standardMapForLogging.put('StatusDesc', new Map<String, Object>{'Status' => sStatus});
            oClaimsService.standardMapForLogging.put('ProviderID', new Map<String, Object>{'TIN' => sProviderId});
            oClaimsService.standardMapForLogging.put('ServiceEndDate', new Map<String, Object>{'End DOS' => sEndDate });
            oClaimsService.standardMapForLogging.put('ServiceStartDate', new Map<String, Object>{'Begin DOS' => sStartDate });
            oClaimsService.standardMapForLogging.put('MbrRespAmt', new Map<String, Object>{'Mbr Resp' => sMbrRespAmt });
            oClaimsService.standardMapForLogging.put('ClmGenKey', new Map<String, Object>{'ClmGenKey' => sClaimGenKey });
            oClaimsService.standardMapForLogging.put('ProviderName', new Map<String, Object>{'Provider Name' => sProviderName });
            oClaimsService.standardMapForLogging.put('PaidAmt', new Map<String, Object>{'Paid Amt' => sPaidAmt });
            standardMapvalue = JSON.serialize(oClaimsService.standardMapForLogging);
        }
    }
    
   /*
    * Method Name   :  validateCheckNumberVal
    * Description   :  This method is used to Call the Service while loading the page
    * Return Type   :  void
    * Parameters    :  List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine>
    */
    @TestVisible private void validateCheckNumberandRemidID(List<ClaimLineItems_DTO_HUM_V2.ClaimDetailLine> oclaimlineItems) 
    {
        if(oclaimlineItems!=null && (!oclaimlineItems.isEmpty()))
        {
            for(ClaimLineItems_DTO_HUM_V2.ClaimDetailLine oDetailLine: oclaimlineItems)
                {
                    if(String.isBlank(oDetailLine.sCheckNbr) || oDetailLine.sCheckNbr=='0' || oDetailLine.sCheckNbr.containsOnly('0'))
                        {
                            bPaymentZeroCheckNum = true; break;
                    
                        }
                    else
                        {
                            bPaymentNonZeroCheckNum = true; break;
                        }                                             
                }
         }
    }
          
   /*
    * Method Name   :  initiateMTVRequest
    * Description   :  This method is used to Call the Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    public object initiateMTVRequest()
    {
        try
        {
            if(bSwitch_2132792 ==true) {  
                           
                MTVRemarksGetClaimsRequest_H_HUM_V1.ClaimNumbersList oClaimnumber= new MTVRemarksGetClaimsRequest_H_HUM_V1.ClaimNumbersList();
                oClaimnumber.ClaimNumber = sClaimNumber;                
                MTVRemarksGetClaimsRequest_H_HUM_V1 oMTVRemarksGetClaimsRequest = new MTVRemarksGetClaimsRequest_H_HUM_V1();
                oMTVRemarksGetClaimsRequest .GetClaimsRemarksRequest = new MTVRemarksGetClaimsRequest_H_HUM_V1.GetClaimsRemarksRequest();
                oMTVRemarksGetClaimsRequest .GetClaimsRemarksRequest.ClaimNumbersList = new List<MTVRemarksGetClaimsRequest_H_HUM_V1.ClaimNumbersList>();
                oMTVRemarksGetClaimsRequest .GetClaimsRemarksRequest.ClaimNumbersList.add(oClaimnumber);
                HUM_Webservice_Callout__c oSvcHeaderClaimMTVRemarks = HUM_Webservice_Callout__c.getValues('MTVRemarkService_GetClaimsMTVRemarkV2'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimMTVRemarks.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processMTVResponse1';
                string sJsonRequest = JSON.Serialize(oMTVRemarksGetClaimsRequest );
                HttpRequest oRequest = new HttpRequest();
                oRequest = oclaimMTVRemarks.setupRequest(oRequest,oSvcHeaderClaimMTVRemarks,sjsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                return con;  
             
            }
            else {
            
            MTVRemarksServiceGetClaimsRequest_H_HUM oMTVRemarksReq = new MTVRemarksServiceGetClaimsRequest_H_HUM();            
            oMTVRemarksReq = oclaimMTVRemarks.assignValuesToHelper(bMTVSecondCall, sClaimNumber, sCreationDate, sCreationTime, sSurrogateId, sStartRemarkSequenceNumber);            
            if(oMTVRemarksReq != null)
            {
                HUM_Webservice_Callout__c oSvcHeaderClaimMTVRemarks = HUM_Webservice_Callout__c.getValues('MTVRemarksService_GetClaimsMTVRemarks'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimMTVRemarks .Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processMTVResponse';
                string sJsonRequest = JSON.Serialize(oMTVRemarksReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oclaimMTVRemarks.setupRequest(oRequest,oSvcHeaderClaimMTVRemarks,sjsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                return con;            
            }
            else
            {
                return null;
            }
                
          }
        }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'initiateMTVRequest');
            return NULL;
        }
    }
    
    
    /*
    * Method Name   :  processMTVResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processMTVResponse()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sRequest);
            if (response.getStatusCode() == 200 || response.getStatusCode() == 202) 
            {
                lstMTVRemarks = oclaimMTVRemarks.processResponse(response.getbody());
                prepareMTVRemarkslist(lstMTVRemarks );
            }
            else
            {
                bShowMTVServiceError = true;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                ApexPages.addMessage(myMsg);
            }

        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processMTVResponse');
        }
    } 
    
     /*
    * Method Name   :  processMTVResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processMTVResponse1()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sRequest);
              
            if (response.getStatusCode() == 200 || response.getStatusCode() == 202) 
            {
                lstMTVRemarks = oclaimMTVRemarks.processResponseNew(response.getbody());
                sMTVJsonData = JSON.serialize(lstMTVRemarks ); 
                    sMTVJsonData = '{"data":'+sMTVJsonData+'}';
                    bShowMTVTable = true;
                
            }
            else
            {
                bShowMTVServiceError = true;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                ApexPages.addMessage(myMsg);
            }           

        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processMTVResponse');
        }
    } 
    
    
        /*
    * Method Name   :  prepareMTVRemarkslist
    * Description   :  This method is used to form the wrapper list that needs to be display on UI. 
    * Return Type   :  void 
    * Parameters    :  List<ClaimDetailMTVRemarks_DTO_HUM>
    */
    @TestVisible  private void prepareMTVRemarkslist(List<ClaimDetailMTVRemarks_DTO_HUM> lstMTVRemarks)
    {
        try
        {
            setPagindID = false;
            if(lstMTVRemarks != null)
            {
                if(!lstMTVRemarks.isEmpty())
                {
                    lstMTV = new List<ClaimDetailMTVRemarks_DTO_HUM>();
                    string sRTxt;
                    mtvConcatenate(setPagindID,lstMTVRemarks);
                    for(string varS : mapMTV.keyset())
                    {
                        ClaimDetailMTVRemarks_DTO_HUM objMTV = mapMTV.get(varS);
                        lstMTV.add(objMTV);
                    }
                    sMTVJsonData = JSON.serialize(lstMTV); 
                    sMTVJsonData = '{"data":'+sMTVJsonData+'}';
                    bShowMTVTable = true;
                }
                else
                {
                    bShowMTVServiceError = true;
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.ClaimDetails_NoRecords_Msg);
                    ApexPages.addMessage(myMsg);                                
                }  
            }
            else
            {
                bShowMTVServiceError = true;
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                ApexPages.addMessage(myMsg);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'prepareMTVRemarkslist');
        }               
    }
    
    /*
    * Method Name   :  mtvConcatenate
    * Description   :  This method is used for Concatenation of MTV Remarks text based on SurrogateId. 
    * Return Type   :  void 
    * Parameters    :  Boolean, List<ClaimDetailMTVRemarks_DTO_HUM>
    */
    public void mtvConcatenate(boolean setPagindID, List<ClaimDetailMTVRemarks_DTO_HUM> lstMTVRemarks)
    {
        for(ClaimDetailMTVRemarks_DTO_HUM obj:lstMTVRemarks)
        {
            if(!setPagindID )
            {
                setPagindID = true;                              
                if(obj.sPagingInd == 'Y')
                {
                    bMTVSecondCall = true;
                    sCreationDate = obj.sCreationDate;
                    sCreationTime = obj.sCreationTime;
                    sSurrogateId = obj.sSurrogateId;
                    sStartRemarkSequenceNumber = obj.sStartRemarkSequenceNumber;
                }
                else
                {
                    bMTVSecondCall = false;                     
                }                
             }
            if(!(mapMTV.containskey(obj.sRemarkSurrogateId)))
            {
                mapMTV.put(obj.sRemarkSurrogateId, obj);
                obj.sRemarkExtendedText = obj.sRemarkText.trim();
            }
            else
            {
                ClaimDetailMTVRemarks_DTO_HUM objRepeat =  mapMTV.get(obj.sRemarkSurrogateId);
                                         
                if(Integer.Valueof(obj.sRemarkSequenceNumber) <= Integer.valueOf(objRepeat.sRemarkSequenceNumber) )
                {
                    objRepeat.sRemarkExtendedText =  obj.sRemarkText.trim() + objRepeat.sRemarkExtendedText.trim();
                } 
                else
                {
                    objRepeat.sRemarkExtendedText = objRepeat.sRemarkExtendedText.trim() + obj.sRemarkText.trim();
                }
                 mapMTV.remove(objRepeat.sRemarkSurrogateId);
                 mapMTV.put(objRepeat.sRemarkSurrogateId, objRepeat);
             }
         }
    }
    
   /*
    * Method Name   :  initiatePaymentRequest
    * Description   :  This method is used to Call Claims Payments Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    public object initiatePaymentRequest()
    {
        try
        {
           if(bPaymentZeroCheckNum == true && bPaymentNonZeroCheckNum == false && eAdjInd == enumyesno.Y)
            {
                bDisplayPaymentsInfoMsg = true;
                setInformationMessage();
            }
            else if(bPaymentNonZeroCheckNum == true && eAdjInd == enumyesno.Y) 
            {
                if(String.isBlank(sPaymentCheckNumber) || sPaymentCheckNumber == '0' || sPaymentCheckNumber.containsOnly('0')) 
                {
                    setDataForZeroChkNumber();
                    return null;
                }
                else
                { 
                    return hitPaymentServiceMethod(); 
                }
            }
            else if(eAdjInd == enumyesno.N)
            {
                return hitPaymentServiceMethod(); 
            }
           return null;
       }
       catch(Exception ex)
       {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'initiatePaymentRequest');
            return NULL;
        }
    }
    
   /*
    * Method Name   :  hitPaymentServiceMethod
    * Description   :  This method is used to hit the Claims Payment service(835).
    * Return Type   :  Object
    * Parameters    :  NA
    */
    public Object hitPaymentServiceMethod() 
    {
        try
        {
            ClaimsServicePaymentDetailsRequest_H_HUM oPaymentsReq = new ClaimsServicePaymentDetailsRequest_H_HUM();
            oPaymentsReq.GetPaymentDetails = new ClaimsServicePaymentDetailsRequest_H_HUM.GetPaymentDetails();
            oPaymentsReq.GetPaymentDetails.SearchType = GLOBAL_CONSTANT_HUM.POLICYMEMBER_CLAIMPAYMENT_SEARCHTYPE_HUM;
            oPaymentsReq.GetPaymentDetails.SRCNPIID = '';
            oPaymentsReq.GetPaymentDetails.ClaimNbr = sClaimNumber.escapeHtml4();
            oPaymentsReq.GetPaymentDetails.PlatformCd = sPlatformCode.escapeHtml4();
            oPaymentsReq.GetPaymentDetails.ProviderID = '';
            oPaymentsReq.GetPaymentDetails.CheckEFTNumber = ''; 
            oPaymentsReq.GetPaymentDetails.PaymentMethod = '';
            if(eAdjInd == enumyesno.Y)
            {
                oPaymentsReq.GetPaymentDetails.CheckEFTNumber = sPaymentCheckNumber; 
                oPaymentsReq.GetPaymentDetails.PaymentMethod = GLOBAL_CONSTANT_HUM.POLICYMEMBER_CLAIMPAYMENT_METHOD_HUM;
            }

            if(oPaymentsReq != null)
            {
                HUM_Webservice_Callout__c oSvcHeaderClaimPayments = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsPayment_V3'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimPayments.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS);
                con.continuationMethod = 'processPaymentsResponse';
                string sJsonRequest = JSON.Serialize(oPaymentsReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oclaimPayments.setupRequest(oRequest,oSvcHeaderClaimPayments,sjsonRequest);
                sPaymentRequest = con.addHttpRequest(oRequest);
                return con;            
            }
            else
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'hitPaymentServiceMethod');
            return Null;
        } 
    }
    
   /*
    * Method Name   :  setInformationMessage
    * Description   :  This method is used to set the informational message based on platformcode for claim payments section.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void setInformationMessage() 
    {
        if(ePlatform == EnumPlatform.EM || (bSwitch_4200713 && ePlatform == EnumPlatform.HP && sProduct == 'DEN')) 
        {
        ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.MTV_Information_Message_HUM_V2_Claim);
            ApexPages.addMessage(myMsg);
            
        }
        if(ePlatform == EnumPlatform.LV || (bSwitch_4200713 && ePlatform == EnumPlatform.HP && sProduct == 'MED'))
        {
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.CAS_Information_Message_HUM_V1_Claim);
            ApexPages.addMessage(myMsg);
            
        }
    }
    
   /*
    * Method Name   :  setDataForZeroChkNumber
    * Description   :  This method is used to assign lastprocessdate and sequencenumber for check number zero.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void setDataForZeroChkNumber()
    {
        ClaimsPaymentDetails_DTO_HUM oClaimPayment = new ClaimsPaymentDetails_DTO_HUM(); 
        oClaimPayment.sLastProcessDate = sPaymentLastProcessDate.substring(0,10);
        if(ePlatform == EnumPlatform.EM)
        oClaimPayment.sSequenceNum = sPaymentLastProcessDate.substring(sPaymentLastProcessDate.lastIndexOf('(')+1,sPaymentLastProcessDate.lastIndexOf(')')); 
        oClaimPayment.bPaymentInfoMsg = true;
        lstPayments.add(oClaimPayment);
        sjsonpaymentsdata = JSON.serialize(lstPayments); 
        sjsonpaymentsdata = '{"data":'+sjsonpaymentsdata+'}';
        bShowPaymentTable = true;
    }
    
    
   /*
    * Method Name   :  processPaymentsResponse
    * Description   :  This method is used to get response from Claims Payments service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processPaymentsResponse()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sPaymentRequest);
            if(response != NULL)    processPay(response); 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processPaymentsResponse');
        }
    }
    
   /*
    * Method Name   :  processPay
    * Description   :  This method is used to process the response from Claims Payments service.
    * Return Type   :  void 
    * Parameters    :  HttpResponse 
    */
    public void processPay(HttpResponse response)
    {
        try
        {
           List<ClaimsPaymentDetails_DTO_HUM> lstPaymentsResp = new List<ClaimsPaymentDetails_DTO_HUM>();
           if (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
           {
            lstPaymentsResp = oclaimPayments.processResponse(response.getBody());
            String sPaymentSequenceNumber = '';
            if(lstPaymentsResp!=null && !lstPaymentsResp.isEmpty())
            {
                for(ClaimsPaymentDetails_DTO_HUM objPayments:lstPaymentsResp )
                {
                     objPayments.sLastProcessDate = sPaymentLastProcessDate.substring(0,10);
                    if(ePlatform == enumPlatform.EM)
                        objPayments.sSequenceNum = sPaymentLastProcessDate.substring(sPaymentLastProcessDate.lastIndexOf('(')+1,sPaymentLastProcessDate.lastIndexOf(')')); 
                    else   
                        objPayments.sSequenceNum = '';
                      
                    objPayments.sRemit = sRemittenceID;
                    objPayments.sPayee = sPayeeCd;
                    lstPayments.add(objPayments);
                }
             }
            if(lstPaymentsResp.isEmpty() )
            {
                if(eAdjInd == enumyesno.Y)
                {
                    ClaimsPaymentDetails_DTO_HUM oClaimPayment = new ClaimsPaymentDetails_DTO_HUM(); 
                    oClaimPayment.sLastProcessDate = sPaymentLastProcessDate.substring(0,10);
                    if(ePlatform == EnumPlatform.EM)
                    oClaimPayment.sSequenceNum = sPaymentLastProcessDate.substring(sPaymentLastProcessDate.lastIndexOf('(')+1,sPaymentLastProcessDate.lastIndexOf(')')); 
                    oClaimPayment.bPaymentInfoMsg = true;
                    lstPayments.add(oClaimPayment); 
                 }
                 else if (eAdjInd == enumyesno.N && sStatus == 'Pended') 
                 {
                     bShowPaymentsServiceError = true;
                     ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.ClaimDetails_NoRecords_Msg);
                     ApexPages.addMessage(myMsg); 
                 }
                 else if (eAdjInd == enumyesno.N && sStatus != 'Pended')
                 {
                      bDisplayPaymentsInfoMsg = true;
                      setInformationMessage();    
                 }
            } 
            if(lstPayments != null)
            {
                if(!lstPayments.isEmpty())
                {
                    sjsonpaymentsdata = JSON.serialize(lstPayments); 
                    sjsonpaymentsdata = '{"data":'+sjsonpaymentsdata+'}';
                    bShowPaymentTable = true;   
                }
             }
             else
             {
                 bShowPaymentsServiceError = true;
                 ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                 ApexPages.addMessage(myMsg);
             }
            }
            else {
                 bShowPaymentsServiceError = true;
                 ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                 ApexPages.addMessage(myMsg);
                 }     
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processPay');
        }
    }
    
     /*
     * Returns an object 
     * <p>
     * This method is used to call the getCodeEditDetails Service on click of accordion image for Lnie Item.
     *
     * @param  List<String>, List<ClaimLineItems_DTO_HUM_V2.PaymentCode>,String, String, String, String, String 
     * @return Object
     */
    @RemoteAction
    public static Object getRationale(List<String> lstDiagnosisCd,List<ClaimLineItems_DTO_HUM_V2.PaymentCode> lstPaymentCode, String sPlatformCode, String sDiagIndicator, String sPaidStatDesc, String sCPTModCode, String sServiceCode, String sClmNbr) {
        ClaimDetails_S_HUM_V2  oClaimDetailsService = new ClaimDetails_S_HUM_V2();
        ClaimsServiceCodeEditDetailRequest_H_HUM oCodeEditReq = new ClaimsServiceCodeEditDetailRequest_H_HUM(); 
        oCodeEditReq  = oClaimDetailsService.CodeEditRationale(lstDiagnosisCd, lstPaymentCode, sPlatformCode, sDiagIndicator, sPaidStatDesc, sCPTModCode, sServiceCode);      
        if(oCodeEditReq != null)
          {
                HUM_Webservice_Callout__c oServiceCalloutClaimsCodeEdit = HUM_Webservice_Callout__c.getValues('ClaimsService_GetClaimsCodeEdit_V3');  
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oServiceCalloutClaimsCodeEdit.Timeout__c)/1000; 
              string sJsonRequestCodeEdit = JSON.Serialize(oCodeEditReq );
                HttpRequest oRequest = new HttpRequest();
                oRequest = oClaimDetailsService.setHttpRequestCodeEdit(oRequest,oServiceCalloutClaimsCodeEdit,sJsonRequestCodeEdit);
                ClaimsGetCodeEditDetails_DTO_HUM oCodeEditState = new ClaimsGetCodeEditDetails_DTO_HUM();
                oCodeEditState.sClaimNbr = sClmNbr;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                oCodeEditState.sContinuationState = con.addHttpRequest(oRequest);
                con.state = oCodeEditState;
                con.continuationMethod = 'processResponseCodeEdit';
                return con;
          }
          else 
          {
             return null;
          } 
    }

    /*
     * Returns an object 
     * <p>
     * This is the Callback method for Continous Object to process the Response data
     *
     * @param  Object 
     * @return Object
     */
    public static Object processResponseCodeEdit(Object state)
    {  
        try
        {
            ClaimsGetCodeEditDetails_DTO_HUM  oCodeEditState =  (ClaimsGetCodeEditDetails_DTO_HUM)state;
            HttpResponse oResponseCodeEdit = Continuation.getResponse(oCodeEditState.sContinuationState);
            return responseDeserializeCodeEdit(oResponseCodeEdit,oCodeEditState);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processResponseCodeEdit');
        }
        return null;
    }
     
    /*
    * Method Name   :  responseDeserializeCodeEdit
    * Description   :  This method is used dederialize the response
    * Return Type   :  ClaimsGetCodeEditDetails_DTO_HUM 
    * Parameters    :  HttpResponse 
    */
    public static List<ClaimsGetCodeEditDetails_DTO_HUM> responseDeserializeCodeEdit(HttpResponse sResponse,ClaimsGetCodeEditDetails_DTO_HUM  oCodeEditState)
    {
       try 
       {   
          string sClaimNumber = oCodeEditState.sClaimNbr;
          Integer statusCode = sResponse.getStatusCode();
          if (statusCode == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || statusCode == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
          {
              ClaimsServiceCodeEditDtlResponse_H_HUM oResponseCodeEdit = new ClaimsServiceCodeEditDtlResponse_H_HUM ();
             oResponseCodeEdit = (ClaimsServiceCodeEditDtlResponse_H_HUM )JSON.deserialize(sResponse.getBody(),ClaimsServiceCodeEditDtlResponse_H_HUM .class); 
              if(oResponseCodeEdit.GetCodeEditDetailsResponse != null)
              {
                 ClaimsServiceCodeEditDtlResponse_H_HUM.GetCodeEditDetailsResponse oClaimsCodeEditResponse = oResponseCodeEdit.GetCodeEditDetailsResponse;
                 ClaimDetails_S_HUM_V2 oService = new ClaimDetails_S_HUM_V2(); 
                 oCodeEditDTO = oService.CodeEditDetailsResponse(oClaimsCodeEditResponse );
              }
             return oCodeEditDTO;
         }
      } 
      catch(Exception ex)
        {
           HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'responseDeserializeCodeEdit');
           
        }  
        return null;
    } 
    /*
    * Method Name   :  initClaimStatementsRequest
    * Description   :  This method is used to Call Member Claims Statements Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    public object initClaimStatementsRequest()
    {
        return initStatementsRequest(EnumStatementType.Member); 
    }
    
    /*
    * Method Name   :  initPrvdrClaimStatementsRequest
    * Description   :  This method is used to Call Provider Claims Statements Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
     public object initPrvdrClaimStatementsRequest()
    {
    
        if(sRemittenceIds != null && sRemittenceIds != '')
        {
            String[] arrRemitId = sRemittenceIds.split('#');
            for(integer i=0; i< arrRemitId.size(); i++)
            {                    
                if(String.isNotBlank(arrRemitId[i])) lstRemitNumber.add(arrRemitId[i]);
            }
        }
        return initStatementsRequest(EnumStatementtype.Provider);
    }
    
    /*
    * Method Name   :  initStatementsRequest
    * Description   :  This method is used to invoke Member and Provider Claims Statements Service while loading the page
    * Return Type   :  continuation
    * Parameters    :  NA
    */
    
    public object initStatementsRequest(EnumStatementType eStatementType)
    {
    try
        {
            String sServiceName;
            String sContinuationName;
            MobiusServiceGetDocListRequest_H_HUM oDocListReq = new MobiusServiceGetDocListRequest_H_HUM();
            
            list<MobiusServiceGetDocListRequest_H_HUM.DocumentSearch> lstDocSearch = new list<MobiusServiceGetDocListRequest_H_HUM.DocumentSearch>();            
            if(eStatementType == EnumStatementType.Provider)
            {
                lstDocSearch.add(prepareDocSearchReq(EnumDocumentType.EOR));
                sServiceName = 'MobiusService_GetDocumentList';
                sContinuationName = 'processPrvdrClaimStatementsResponse';
            }
            else if(eStatementType == EnumStatementType.Member)
            {
                lstDocSearch.add(prepareDocSearchReq(EnumDocumentType.SS));
                lstDocSearch.add(prepareDocSearchReq(EnumDocumentType.EOB));
                sServiceName = 'MobiusService_GetDocumentList';  
                sContinuationName = 'processClaimStatementsResponse';
            }
            MobiusServiceGetDocListRequest_H_HUM.getDocument ogetDoc = new MobiusServiceGetDocListRequest_H_HUM.getDocument();                                         
            ogetDoc.DocumentSearch = lstDocSearch;

            MobiusServiceGetDocListRequest_H_HUM.getDocumentList ogetDocLst = new MobiusServiceGetDocListRequest_H_HUM.getDocumentList();                                         
            ogetDocLst.getDocument = ogetDoc;            

            oDocListReq.getDocumentList= ogetDocLst;
            if(oDocListReq != null)
            {
                HUM_Webservice_Callout__c oSvcHeaderClaimStatements = HUM_Webservice_Callout__c.getValues(sServiceName); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimStatements.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = sContinuationName;
                string sJsonRequest = JSON.Serialize(oDocListReq);
                HttpRequest oRequest = new HttpRequest();                
                if(eStatementType == EnumStatementType.Provider) 
                {
                    oRequest = oPrvdrStatements.setupRequest(oRequest,oSvcHeaderClaimStatements,sjsonRequest);
                    sPrvdrClaimStatementsRequest = con.addHttpRequest(oRequest);
                }
                else if(eStatementType == EnumStatementType.Member) 
                {                   
                    oRequest = oclaimStatements.setupRequest(oRequest,oSvcHeaderClaimStatements,sjsonRequest);
                    sClaimStatementsRequest = con.addHttpRequest(oRequest);                
                }               
                return con;            
            }
            else
            {
                return null;
            }
        }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'initPrvdrClaimStatementsRequest');
            return NULL;
        }
    }
    
    /*
    * Method Name   :  prepareDocSearchReq
    * Description   :  This method is used to prepare DocumentSearch ojbect of service request
    * Return Type   :  MobiusServiceGetDocListRequest_H_HUM.DocumentSearch
    * Parameters    :  NA
    */
    public MobiusServiceGetDocListRequest_H_HUM.DocumentSearch prepareDocSearchReq(EnumDocumentType docType)
    {    
        MobiusServiceGetDocListRequest_H_HUM.DocumentSearch oDoc = new MobiusServiceGetDocListRequest_H_HUM.DocumentSearch();
        List<MobiusServiceGetDocListRequest_H_HUM.TopicSearch> lstTS = new List<MobiusServiceGetDocListRequest_H_HUM.TopicSearch>();
        MobiusServiceGetDocListRequest_H_HUM.TopicSearch oTS = new MobiusServiceGetDocListRequest_H_HUM.TopicSearch();        
        MobiusServiceGetDocListRequest_H_HUM.TopicSearchList oTSL = new MobiusServiceGetDocListRequest_H_HUM.TopicSearchList();                                                
        
         if(docType == EnumDocumentType.EOR)
        {
            oDoc.DocumentServer = DocumentServer__c.getInstance('DocumentServerEOR').Value__c;
            oDoc.ReportName = 'EORSTMT';
            oDoc.Creator = 'EOR'; 
            Boolean isHO = ClaimSecurityDataLogic_D_HUM.isHomeOfficeMember(ClaimSecurityDataLogic_D_HUM.getMemberAccountID(sPolMemID)); 
            for(string sRN: lstRemitNumber)
            {
                oTS = new MobiusServiceGetDocListRequest_H_HUM.TopicSearch();
                if(isHO)
                {
                   oTS.TopicName = 'eorremitho';
                }
                else
                {
                    oTS.TopicName = 'eorremit';            
                }
                oTS.RemitID = sRN;   
                oTS.DocumentType = 'EOR';
                oTS.PlatformCode = sPlatformCode.escapeHtml4();                            
                oTS.Comparator = 'LIKE';
                oTS.SortOrder = 'Descending';                                
                lstTS.add(oTS);
            }
                
                oTSL.JoinType = 'OR';
                oTSL.TopicSearch = lstTS;                            
                oDoc.MaxRows = '24'; 
                oDoc.TopicSearchList = oTSL;                      
                return oDoc;
        }
        if(docType ==  EnumDocumentType.SS)
        {
            oDoc.DocumentServer = DocumentServer__c.getInstance('DocumentServerSS').Value__c;
            oDoc.ReportName = 'AFPTEST';
            oDoc.Creator = 'HUM';
            oTS.TopicName = 'PersonId';
            oTS.PersonId = getEnterpriseID(ApexPages.currentPage().getParameters().get('Id').escapeHTML4());   
            oTS.MemberGenKey = getMemberGenKey(ApexPages.currentPage().getParameters().get('Id').escapeHTML4());                       
        }
        if(docType ==  EnumDocumentType.EOB)
        {
            oDoc.DocumentServer = DocumentServer__c.getInstance('DocumentServerEOB').Value__c;
            oDoc.ReportName = 'EOBSTMT';
            oDoc.Creator = 'EOB';
            if(ePlatform == EnumPlatform.LV)
            {
                oTS.TopicName = 'eobmember';  
                oTS.MemberId = sMemberId.substringBefore('-');                            
            }
            else
            {
                oTS.TopicName = 'eobclaim';                                          
            }
        }
        oTS.PlatformCode = string.valueOf(ePlatform);                               
        oTS.ClaimNumber = sClaimNumber;        
        oDoc.MaxRows = '24';                   
        oTS.Comparator = 'LIKE';
        oTS.SortOrder = 'Descending';                
        lstTS.add(oTS);
        oTSL.JoinType = 'AND';
        oTSL.TopicSearch = lstTS;            
        oDoc.TopicSearchList = oTSL;                      
        return oDoc;
    }    
    
    
    /*
    * Method Name   :  processClaimStatementsResponse
    * Description   :  This method is used to get response from member claim statements service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processClaimStatementsResponse()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sClaimStatementsRequest);
            if(response != Null)
            {
                List<ClaimsDocumentsList_DTO_HUM> lstClaimsStatementsResp = new List<ClaimsDocumentsList_DTO_HUM>();
                lstClaimsStatementsResp = oclaimStatements.processResponse(response.getbody(), 'Member', sPolMemID);
                if(lstClaimsStatementsResp != null)
                {
                    if(!lstClaimsStatementsResp.isEmpty())
                    {
                        sjsonClaimStatementsdata  = JSON.serialize(lstClaimsStatementsResp); 
                        sjsonClaimStatementsdata  = '{"data":'+sjsonClaimStatementsdata +'}';
                        bShowClaimStatementsTable = true;
                    }  
                    else
                    {
                        bShowClaimStatementsServiceError = true;
                        ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.ClaimDetails_NoRecords_Msg);
                        ApexPages.addMessage(myMsg);                                            
                    }
                 }
                 else
                 {
                    bShowClaimStatementsServiceError = true;
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                    ApexPages.addMessage(myMsg);
                 }
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processClaimStatementsResponse');
        }
    }
    
    
     /*
    * Method Name   :  processPrvdrClaimStatementsResponse
    * Description   :  This method is used to get response from Claims provider statements call.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    public void processPrvdrClaimStatementsResponse()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sPrvdrClaimStatementsRequest);
            if(response != Null)
            {
                List<ClaimsDocumentsList_DTO_HUM> lstClaimsStatementsResp = new List<ClaimsDocumentsList_DTO_HUM>();
               lstClaimsStatementsResp = oPrvdrStatements.processResponse(response.getBody(), 'Provider', sPolMemID);
                if(lstClaimsStatementsResp != null)
                {
                    if(!lstClaimsStatementsResp.isEmpty())
                    {
                        sjsonPrvdrClaimStatementsdata  = JSON.serialize(lstClaimsStatementsResp); 
                        sjsonPrvdrClaimStatementsdata  = '{"data":'+sjsonPrvdrClaimStatementsdata +'}';
                        bShowPrvdrClaimStatementsTable = true;
                    }  
                    else
                    {
                        bShowPrvdrClaimStatementsServiceError = true;
                        ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Info,Label.ClaimDetails_NoRecords_Msg);
                        ApexPages.addMessage(myMsg);                                            
                    }
                 }
                 else
                 {
                    bShowPrvdrClaimStatementsServiceError = true;
                    ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.ClaimsSummary_Service_Error);
                    ApexPages.addMessage(myMsg);
                 }
                 
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'processPrvdrClaimStatementsResponse');
        }
    }
    
    /*
     * Method Name   :  getEnterpriseID
     * Description   :  This method is return EnterpriseID
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    @testvisible
    private String getEnterpriseID(ID sRecId )
    {
       List<POLICY_Member__C> lstPolicyMem = ClaimDetails_D_HUM_V2.getEnterpriseID(sRecId);
       if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())    return lstPolicyMem[0].Member__r.Enterprise_ID__c ;
       return NULL;
    } 
    
    /*
     * Method Name   :  getMemberGenKey
     * Description   :  This method returns MemberGenKey
     * Return Type   :  String 
     * Parameters    :  ID sRecId
     */
    @testvisible
    private String getMemberGenKey(ID sRecId )
    {
       List<POLICY_Member__C> lstPolicyMem = ClaimDetails_D_HUM_V2.getMemberGenKey(sRecId);
       if(NULL != lstPolicyMem && !lstPolicyMem.isEmpty())    return lstPolicyMem[0].Member__r.Mbr_Gen_Key__c ;
       return NULL;
    }       
    
    
    /*
     * Method Name   :  getClaimPolicyMemberID
     * Description   :  This method returns Policy Member Id for a MemberId of the claim
     * Return Type   :  String
     * Parameters    :  String sMemberId
     */ 
     
     public String getClaimPolicyMemberID(){
     string sMemberId = ApexPages.currentPage().getParameters().get('MemberId'); 
     ID polMemID = NULL;
     String claimPolMem='';
     String encryptedMbrID = '';
     Id pMemAccId = LoggingHelper_H_HUM.getAccountId(ApexPages.currentPage().getParameters().get('Id'),getSObjName(ApexPages.currentPage().getParameters().get('Id')));
     ClaimsSecurity_H_HUM oSecurity=new ClaimsSecurity_H_HUM();
     try{
      if(String.isNotBlank(sMemberId))
      { 
       if(sMemberId.contains('-')) 
        {    
          lstSplitData = sMemberId.split('-');    
          if(!(lstSplitData.isEmpty()))              
          encryptedMbrID = lstSplitData[0]; 
        } 
        else 
            {           
                encryptedMbrID = sMemberId;              
            } 
        polMemID =getMemberParameters(encryptedMbrID); 
           
      }
      if(polMemID!=null && LoggingHelper_H_HUM.getAccountId(polMemID,getSObjName(polMemID)) == pMemAccId)
      {
      claimPolMem=String.valueOf(polMemID);
      }
      else
      {
      claimPolMem=sPolMemID.escapeHtml4();
      }
     }
     catch(exception ex)
      {
       HUMExceptionHelper.bRestrictLog=true;
       listErrorLogs = HUMExceptionHelper.logErrors(ex, 'ClaimDetails_C_HUM_V3', 'getClaimPolicyMemberID');
      return null;
      }
     return claimPolMem;
    }   
    
    /*
     * Method Name : getMemberParameters
     * Description : This method contains logic to fetch the required parameters for the encrypted Member ID 
                       and to get the respective policy member id.
     * Return type : ID
     * Paramater   : Set<String>
     */

  public ID getMemberParameters(String MemberID) 
   {
    policy_member__c temp=new policy_member__c();
    ID polMemberID = NULL;     
    List<Member_ID__c> lstreturnedresults = new List<Member_ID__c>();
    set<id> PID = new set<id>();
    List<policy_member__c> Termedpolciymember=new List<policy_member__c>();
    Id pMemAccId = LoggingHelper_H_HUM.getAccountId(ApexPages.currentPage().getParameters().get('Id'),getSObjName(ApexPages.currentPage().getParameters().get('Id')));
    if(lstSplitData!= NULL && lstSplitData.size() == 2)
     {
     sDependentCode = lstSplitData[1];
     }
    if(MemberID!=''&&(String.isNotBlank(MemberID)))
     {
     lstreturnedresults=ClaimDetails_D_HUM_V2.queryMemberParameters(MemberID,sDependentCode);
      if(lstreturnedresults.size()>0 && lstreturnedresults != NULL)           
       {
        for(Member_ID__c PMIDS:lstreturnedresults)
                {
                  PID.add(PMIDS.Policy_Member__C);
                }  
          List<policy_member__c> selctedpolciymember=ClaimDetails_D_HUM_V2.queryPolicyMembers(PID);
           for(policy_member__c  objselpolmember : selctedpolciymember )
            {
                if(objselpolmember.Status__c=='Active' && LoggingHelper_H_HUM.getAccountId(objselpolmember.id,getSObjName(objselpolmember.id))== pMemAccId)
                {
                polMemberID = objselpolmember.id;                     
                break;
                }
                 if(objselpolmember.Status__c=='Termed')
                {
                 Termedpolciymember.add(objselpolmember);
                }
            } 
            
           if(polMemberID==null)
           {
           for(integer i=0;i<termedpolciymember.size() - 1 ; i++)
            {
             date effDate1 =date.parse(termedpolciymember[i].Effective_Date__c);
             date effDate2 =date.parse(termedpolciymember[i+1].Effective_Date__c);
              if(effDate1<effDate2 && LoggingHelper_H_HUM.getAccountId(temp.id,getSObjName(temp.id))== pMemAccId)
              {
              temp=termedpolciymember[i+1];
              }
             else
              {
              temp=termedpolciymember[i];
              }
            }
           polMemberID=temp.id; 
           }   
        }
     }
    return polMemberID;
   }  
   
   
   /*
    * Method Name : getLoggedinUserQueue
    * Description : This method is used to query the logged in users current queue
    * Return type : NA
    * Paramater   : NA
    */
    public void getLoggedinUserQueue()
     {
         sClaimInquiryTool = Endpoint_Url_HUM__c.getInstance('ClaimInquiryToolLink').Remote_Url__c;
         User ouser = ClaimDetails_D_HUM_V2.getLoggedinUserQueue(UserInfo.getUserId()); 
         if(String.isNotBlank(ouser.Current_Queue__c))
         {
             sDepartmentData = ouser.Current_Queue__c;
         }
     } 
     
     /*
    * Method Name : getSObjName
    * Description : This method is used get current record's object name.
    * Return type : String 
    * Parameter   : Id
    */
    public String getSObjName(Id recID)
    {
        String sObjName = '';
        If(recID != null)
        {
           sObjName = recID.getSobjectType().getDescribe().getName();
        }
        return sObjName;
    }

    private String getHTMLEncodedPageParameter(String param) {
        String paramValue = ApexPages.currentPage().getParameters().get(param);
        return ESAPI_C_HUM.encoder().SFDC_HTMLENCODE(paramValue);
    }

}