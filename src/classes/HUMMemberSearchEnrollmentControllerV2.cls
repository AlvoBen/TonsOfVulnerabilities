/*****************************************************************************************************************************************************************************
Apex Class Name : HUMMemberSearchEnrollmentControllerV2
Version         : 2.0
Created On      : 08/08/2015
Function        : Class contains methods For search On Member In legacy system based On the parameters passed from the visual force page
                  And passes the search results back to the visualforce page as well as method to create a New case record.
                  A detailed analysis was done On Date '12/15/2017' by developer "Vijaya Koriprolu" on Sharing without option for this class.
                  During the analysis it was found that this Class has  a method, which Is creating list Of accounts For  Unknown Account.
                  Some of the fields that are querying  in this method are Not accessible for customer care specialist. So restrictions to this class will break the functionality.
                  This was further discussed With Architects Amrish And Jeremy And got approval To keep the "Without Sharing" mode Of this Class As it Is.
Test Class      : HUMMemSearchEnrollmentControllerV2Test 

Modification Log: 
 * Developer Name           Code Review                  Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Manish Kumar               25519                     08/08/2015                 Original Version
*  Shreya Choodamani          25519                     08/11/2015                 Added Code for H1/OHBP Continuation
*  Harshith Mandya            25519                     08/12/2015                 Added code for CBIS Continuation   
*  Manish Kumar               25519                     08/17/2015                 Fixed major/minor bugs
*  Harshith Mandya            25519                     08/19/2015                 Review Comments Incorporation
*  Ranjeeth Nagishetty        25695                     08/21/2015                 added the zip code for OHBP service missed in TFS
*  Harshith Mandya            25695                     08/24/2015                 Updated Exception to show user friendly message defect #196538.0001
*  Manish Kumar               26266                     09/10/2015                 Fixed defect#197558. Changed service timeout to 10 sec from 100 sec. Line#602, 668.
*  Shreya Choodamani          29217                     01/12/2016                 Latency Check In
*  Sai Ayyagari                                         03/07/2018                 REQ - 351086 CA Ticket #7284565 - Can not use the search enrollment tab in CRM to pull up the platform data
*  Syed Mubarak T 										04/30/2019				   REQ - 380470 Application Search in Search Enrollment
*  Syed Mubarak T 										05/15/2019				   REQ - 380470 Defect fixes
*  Seema Kaulgi                                         03/15/2019                 REQ - 379048  SF - Ability for all CRM users to SEARCH for member specific Transaction Reply Reports (TRR) in CRM
*  Seema Kaulgi                                         03/15/2019                 REQ - 379048  SF Defect fixes
*  Amar Gadewar                                         04/23/2020                 User Story 1083354 : PR00091574 - MF 1 - AHT - Display TRC Short Name in Results within Search Enrollment page (RF)
*  Amar Gadewar                                         06/01/2020                 User Story 1163024 : PR00091574 - MF 1 - AHT - Display TRC Short Name in Results within Search Enrollment page (CRM)
*  Suresh Kokkeragadda                                  06/24/2020                 User Story 1203671: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 07/07 and 07/17 releases (CRM)
*  Firoja Begam                                         09/22/2020                 Fixed defect#1679 : US 1527967 - Regression:- getting exception in the Search Enrollment Tab
*  Firoja Begam                                         05/10/2021                 US-2229318 - PR00094232/T1PRJ0002736 - 2021 MF6 - Security - SF - CBIS Specialty Service Migration
*  Ajay Chakradhar                                      05/31/2021                 REQ - 1755482 - Replacing constants to improve performance
*  Firoja Begam                                         05/10/2021                 US-2229318 - PR00094232/T1PRJ0002736 - 2021 MF6 - Security - SF - CBIS Specialty Service Migration
*  Ajay Chakradhar                                      05/31/2021                 REQ - 1755482 - Replacing constants to improve performance
*  Vardhaman                                            06/07/2021         User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
*  Bhumika S                                            10/14/2022                 User Story 3883795: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs-CBIS
*  Nilesh Gadkar					                    12/22/2022		           User Story 3883798: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs_CIMTV
*  Nilesh Gadkar					                    06/29/2023		           User Story 4717548: SF - TECH Defect 7733 - Regression - User sees different exception both in TESTFULL/QA when invalid/incorrect data is entered		
*****************************************************************************************************************************************************************************/
public class HUMMemberSearchEnrollmentControllerV2 extends HUMSearchComponentBaseHelperV2
{
    private String sClassName = 'HUMMemberSearchEnrollmentControllerV2';
    
    private String sMethodName = '';
    @testVisible private HUMH1SearchResponseHelper.EnrollmentDetailResponse oRespMesageH1 = null;
    @testVisible private string oResponseOHBP = '';
    @testVisible private HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO oResponse = new HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO();
    private AsyncHUMAEServicesHistoryResponse.ResponseComplexTypeFuture oResponseMsg = NULL;
    
    private AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture oEESResponse = NULL;
    
    @testVisible private AsyncHUMPersonAllLegalService.GetMemberInformationXmlResponse_elementFuture oRequestOHBP;
    
    @testVisible private AsyncHUMH1SearchResponseHelper.EnrollmentDetailResponseFuture oResponseH1;
    
    @testVisible private HUMH1SearchRequestHelper.EnrollmentDetailRequest oReqMessage;
    
    @testVisible private AsyncHUMCBISSearchService.GetPendHistoryTermsDetailsResponse_elementFuture oResponseCBIS;
    
     /********CI/MTV Service**********/
     Public Boolean switchCIMTVSSN {get; set;}
     public string sRequestCIMTV;

    /********CBIS Specialty Service**********/ 
    Public Boolean switchOnOffCBIS {get; set;}
    Public Boolean switchCBISSSN {get; set;}
    public string sRequest;
    public String processResponseSpecialty = 'processResponseCBISSpecialty';
    public String cbisSearchEnrollmentV2 = 'HUMCbisSearchEnrollment_V2';
    public String humSearchEnrollmentNoRes = 'No Result(s) found';
    @TestVisible private HUMCBISSearch_S_HUM oServiceCBISSpecialty = new HUMCBISSearch_S_HUM ();
    public HUMCBISSearchResponse_H_HUM respond = new HUMCBISSearchResponse_H_HUM();
    private boolean bESAPILibSecurityfixSwitch {get;set;}    
    public class ServiceDownException extends Exception {}
    Private String HUMCBISCode = 'CBIS';
    Private String HUMTRRCode = 'Transaction Reply Report (TRR)';
    Private String HUMSearchEnrollmentSelectServices = 'Please select one of the options from Services';
    Private String HUMEESService = 'Customer Interface/Metavance';
    Private String HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage = 'Coverages:PlatformMemberCoverage';
    Private String HUMH1Service = 'H1 Marketplace Search';
    Private String HUMTrackEnrollmentService = 'Track Enrollment Status'; //Value not present in Mercury and Test- IMP - Label not present???
    Private String HUMAEService = 'Automated Enrollment (AE) History';
    Private String HUMSearchEnrollmentDate = 'Start Date OR End Date field should not be left Blank';
    Private String HumStartnEndDate = 'Please Enter Valid Date in the Format MM/DD/YYYY in Date field'; //Value not present in mercury and TEST - - IMP - Label not present???
    Private String HumSearchEnrollmentDateValid1 = 'Start Date Should always happen before End Date';
    Private String HUMSearchEnrollmentSsnservice = 'Please select one of the options from Services<br/>Please enter the SSN Number';
    Private String HumSearchenrollmentSsnReq = 'Please enter the SSN Number';
    Private String sHumSearchEnrollmentNoRes = 'No Result(s) found';
    Private String HUMSearchEnrollmentohbpSearch = 'ohbpSearch';
    Private String HUMOHBPSvc = 'OHBPService';
    Private String HUMOHBPReq = 'HUMOHBPService.soap';
    Private String HUMOHBPMemberSearchReq_Header = '<Member><Header><ClientMetaData><DataItem Name=\'ClientName\'>GCP-OHBP</DataItem><DataItem Name=\'ApplicationFunction\'>CCP.MemberSearch</DataItem><DataItem Name=\'ApplicationUser\'>GCP</DataItem><DataItem Name=\'ApplicationRequester\'>Requester</DataItem></ClientMetaData><ProxyInfo><ProxyItem Name=\'ServerName\'>ivr.medical.family</ProxyItem> <ProxyItem Name=\'PathInfo\'>IVR/Medical</ProxyItem><ProxyItem Name=\'Version\'>0.0</ProxyItem> </ProxyInfo> </Header><List Family=\'true\' Coverages=\'true\'><Inputs><SSN>';
    Private String HUMOHBPMemberSearchReq_End = '</SSN></Inputs></List></Member>';
    Private String HUMOHBPMember = 'Member';
    Private String HUMOHBPSearchFirstName = 'FirstName';
    Private String HUMOHBPSearchFstName = 'firstName';
    Private String HUMOHBPSearchLastName = 'LastName';
    Private String HUMOHBPSearchLstName = 'lastName';
    Private String HUMOHBPSearchBirthDate = 'DateOfBirth';
    Private String HUMOHBPSearchBirthDt = 'birthDate';
    Private String HUMOHBPSearchPlanID = 'PlanID';
    Private String HUMOHBPSearchIDPlan = 'planID';
    Private String HUMOHBPSearchTranID = 'TransactionID';
    Private String HUMOHBPSearchExtID = 'externalID';      
    Private String HUMOHBPSearchPlanName = 'PlanName';
    Private String HUMOHBPSearchProd = 'product';
    Private String HUMOHBPSearchEffDate = 'ProposedEffectiveDateEM';
    Private String HUMOHBPSearchEfectDate = 'effectiveDate';
    Private String HUMH1ExchRequestType = 'E';
    Private String HUMAEHistSvc = 'AEHistoryService';
    Private String HUMAEHisReq = 'HUMIntAEHistoryService.soap';
    Private String HUMAEHistReqHeader = 'CCPAEREQST';
    Private String HUMSearchEnrollmentCBISSearch = 'CBISSearch';
    Private String HUMCBISRestrictedError = 'This member cannot be accessed in CRM Service. Please utilize source system to access information.';
    Private String HUMSearchEnrollmentTrackEnrollment = 'TrackEnrollmentSearch';
    Private String HUMSearchEnrollmentAEHistory = 'HUMSearchEnrollmentAEHistory';
    Private String HUMSearchEnrollmentbuildUnknownAccountMap = 'buildUnknownAccountMap';
    Private String HUMSearchEnrollmentH1Exchange = 'H1ExchangeSearch';
    Private String APPLICATION_SEARCH = 'Application Search';
    Private String HUMBirthDatePattern = '^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$';
    Private String HUMAlphaNumericPattern = '^[a-zA-Z0-9-.\' , &]*$';
    Private String HumSearchFirstNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the First Name field.';
    Private String HUMSELastNameAlphaNumericPattern = '^[a-zA-Z0-9-.\' , &]*$';
    Private String HumSESearchLastNameAlphaNumericValidation = 'The only special characters that can be used are an apostrophe, a period, a dash, a comma or an ampersand in the Last Name field.';
    public string cimtvssn {get{ if(cimtvssn == null) {cimtvssn = ''; } return cimtvssn; }set;}
  /*
   * Method Name   :   HUMMemberSearchEnrollmentControllerV2 
   * Description   :   This is used to set the date value for validation and to set the unknown member account id.
   * Return Type   :   NA
   * Parameters    :   NA
   */
    public HUMMemberSearchEnrollmentControllerV2()
    {
        bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
        oSearchWrapper = new HUMSearchEnrollmentHelper();
        oSearchWrapper.sStartDate = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
        oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
        oSearchWrapper.dtMinDate = Date.today() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'));
        oSearchWrapper.dtMaxDate = Date.today();
        lstLegacyService = new list<HUMMemberSearchEnrollmentHelper>();
		
		oSearchWrapper.sEffectiveDateFrom = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
        oSearchWrapper.sEffectiveDateTo = (Datetime.now()).format('MM/dd/yyyy');
        oSearchWrapper.sReceivedDateFrom = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
        oSearchWrapper.sReceivedDateTo = (Datetime.now()).format('MM/dd/yyyy');
        
        // TRR Search fields
        oSearchWrapper.sHumanaReceivedDateFrom = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
        oSearchWrapper.sHumanaReceivedDateTo = (Datetime.now()).format('MM/dd/yyyy');
        
        switchOnOffCBIS = false;
        switchOnOffCBIS = HUMUtilityHelper.isCRMFunctionalityON('2229318');

        switchCBISSSN = false;
        switchCBISSSN = HUMUtilityHelper.isCRMFunctionalityON('3883795');
        
        switchCIMTVSSN = false;
        switchCIMTVSSN = HUMUtilityHelper.isCRMFunctionalityON('3883798');
    }

    public boolean bEnrRes{get;set;}
    
    public boolean bIsEnrollmentError{get;set;}
    
    public boolean bIsEnrollmentErrors{get;set;}  
    
    public HUMEESLegacyMemberSearchHelper oEesLegacy{get;set;} 
    
    public HUMMemberSearchEnrollmentHelper humLegMemberObj {get;set;}
    
    public Map<String,map<String,String>> mapMemInfo {get;set;} 
    
    public string sServices{get;set;}
    
    public string sDisplayResult{get;set;}
    
    public list<HUMMemberSearchEnrollmentHelper> lstLegacyService{get;set;}  
    
    public boolean bIsCbisSelected{get;set;}
	
	public boolean bIsAMSearchSelected{get;set;} 
    
    Public String amSearchContinuationId {Get;Set;} 
	
	 Public String lstLegacyServiceJSON {Get;Set;}
    
    Public Boolean bCBISflag {Get;Set;}
    
    public String sExchangeID {get;set;}
    
    public String sSsNID {get;set;}
    
    public boolean bEnablebutton {get;set;}
    
    public boolean bIsWarningExist{get;set;}
    
    public boolean bIsError {get;set;}
  
    public   AsyncEnrollmentTrackResponse.ResponseComplexTypeFuture  respfuture;
    
    public boolean bIsTRRSearchSelected{get;set;} 
    
    public boolean bIsCIMTVSelected{get;set;}

    public string TRRSearchContinuationId {get;set;} 

   
        
            
    public List<Error_Log__c> lstLogErrors
    {
        get 
        {
            if(NULL == lstLogErrors)
            {
                return new List<Error_Log__c>();
            }
            else
            {
                return lstLogErrors;
            }
        }
        set;
    }   

    Public List<selectOption> ProviderStatevalue
    {   
        get
        {      
            return HUMUtilityHelper.getstateValue();  
        }
        set;
    }   
    
    public HUMSearchEnrollmentHelper oSearchWrapper {get; set;}

   
    
    public HUMMemberSearchEnrollmentControllerV2 getThis()
    {
        return this;
    }
    
    public String getSServices()
    {
        return sServices;
    }
    
    public void setSServices(String sServices)
    { 
        this.sServices= sServices; 
    }

    public List<SelectOption> getItems()
    {
        List<SelectOption> lstOptions = new List<SelectOption>(); 
        lstOptions.add(new SelectOption(HUMEESService,HUMEESService)); 
        lstOptions.add(new SelectOption(HUMTrackEnrollmentService,HUMTrackEnrollmentService)); 
        lstOptions.add(new SelectOption(HUMAEService,HUMAEService));
        lstOptions.add(new SelectOption(HUMH1Service,HUMH1Service));
        HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
        if( oHUMCbisFlag.CBIS_Flag__c == True )
        {
            lstOptions.add(new SelectOption(HUMCBISCode,HUMCBISCode)); // Added for CBIS Service on 10 06 2014
        }
		lstOptions.add(new SelectOption(APPLICATION_SEARCH,APPLICATION_SEARCH));
		lstOptions.add(new SelectOption(HUMTRRCode,HUMTRRCode)); 	
        return lstOptions; 
    }

       /*
     * Method Name   :  validationRule
     * Description   :  This Method is used to validate the input fields - means it will work if we have more than 1 error.
     * Return Type   :  boolean
     * Parameters    :  NA
     */ 
    private boolean validationRule()
    {   
        bIsError = false;
        if(switchCBISSSN && !HUMUtilityHelper.validateCBISSSN(oSearchWrapper.sFirstName, oSearchWrapper.sLastName, oSearchWrapper.sBirthdate, oSearchWrapper.sState, oSearchWrapper.sSSN))
            {
                bIsError = true; 
            }
        else if (string.isBlank(oSearchWrapper.sSSN))
       {
	
			if(!HUMUtilityHelper.validateCBISFirstName(oSearchWrapper.sFirstName)) bIsError = true;
			if(!HUMUtilityHelper.validateCBISLastName(oSearchWrapper.sLastName)) bIsError = true;
			if(!HUMUtilityHelper.validateCBISBirthDay(oSearchWrapper.sBirthdate)) bIsError = true;
			if(!HUMUtilityHelper.validateCBISState(oSearchWrapper.sState)) bIsError = true;
			if(!HUMUtilityHelper.validateBirthdate(oSearchWrapper.sBirthdate)) bIsError = true;
       }
        if(bIsError)
        { 
            bIsEnrollmentError = true;
            bIsEnrollmentErrors = false;
            bEnrRes = false;
            return false;
        }
        else if(!bIsError)
        { 
            bIsEnrollmentErrors = true;
            bIsEnrollmentError = false;
            return false;
        }
        return true;
    }
 
    /*
    * Method Name   :     Service()
    * Description   :    This Method is for getting value for CBIS ,TRR and AM Radio Button.
    * Return Type   :    NA
    * Parameters    :    NA
    */
    
    public void Service()
    {
        if(this.sServices == 'CBIS')
        {  
            bIsCbisSelected = sServices.equals(HUMCBISCode);
            bIsAMSearchSelected = false;
            bIsTRRSearchSelected = false;
            bIsCIMTVSelected = false;   
            reset();  
        }
        
    else if(this.sServices.equals(HUMTRRCode))
        {
            bIsCbisSelected=false;
            bIsAMSearchSelected = false;
            bIsTRRSearchSelected = true;
            bIsCIMTVSelected = false;  
            reset();  
        }
        
    else if(this.sServices.equals(APPLICATION_SEARCH))
        {
            bIsCbisSelected=false;
            bIsTRRSearchSelected = false;
            bIsAMSearchSelected = true;
            bIsCIMTVSelected = false;          
            reset();  
        }
    else if(this.sServices.equals(HUMEESService))
        {
            bIsCbisSelected=false;
            bIsTRRSearchSelected = false;
            bIsAMSearchSelected = false;
            bIsCIMTVSelected = true; 
            reset();
        }
        else 
        {
            bIsCbisSelected=false;
            bIsAMSearchSelected = false;
            bIsTRRSearchSelected = false;
            bIsCIMTVSelected = false;
        }       
    }        

    /*
    * Method Name   :   validationForCBIS() 
    * Description   :   This Method is for validating the inputs and calling the CBIS service.
    * Return Type   :   void
    * Parameters    :   NA
    */
    public void validationForCBIS()
    {
        validationRule();
        if(String.isBlank(sServices))
        {
            bEnrres = false;
            throw new HUMCustomException(HUMSearchEnrollmentSelectServices); 
        } 
        else if(sServices.equals(HUMCBISCode))
        {  
            bIsCbisSelected = true;                            
        }
    }

    /*
    * Method Name   :   validationForNonCBIS() 
    * Description   :   This Method is for validating the inputs and calling the non-CBIS service.
    * Return Type   :   void
    * Parameters    :   NA
    */
    public void validationForNonCBIS()
    {
        if(!String.isBlank(oSearchWrapper.sSSN))
        {
            if((String.isBlank(oSearchWrapper.sStartDate)) || (String.isBlank(oSearchWrapper.sEndDate)))
            {
                bEnrres = false;
                throw new HUMCustomException(HUMSearchEnrollmentDate);  
            }
            else  if(!Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sStartDate))
            {
                bEnrres = false;
                throw new HUMCustomException(HumStartnEndDate);
            }
            else  if(!Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sEndDate))
            {
                bEnrres = false;
                throw new HUMCustomException(HumStartnEndDate);
            }          
            else if((Date.parse(oSearchWrapper.sStartDate).daysBetween(Date.parse(oSearchWrapper.sEndDate))) < 0)
            {
                bEnrres = false;
                throw new HUMCustomException(HumSearchEnrollmentDateValid1); 
            }     
        }
    }
 /*
    * Method Name   :   validationForTRR() 
    * Description   :   This Method is for validating the inputs and calling the TRR service.
    * Return Type   :   boolean
    * Parameters    :   NA
    */
    public boolean validationForTRR()
    {                
        boolean validData = true; 
        // Validation for Empty Request only with Humana Received startdate and Humana Received enddate        
        if((string.isBlank(oSearchWrapper.sFirstName) && string.isBlank(oSearchWrapper.sLastName) && string.isBlank(oSearchWrapper.sBirthdate)) && string.isBlank(oSearchWrapper.sMedicareId) )
        {            
            validData = false;                    
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.TRR_Addition_Field_Addition_Error,true);  
           
       }
       Else{
        // Validation on MediacreID Min And Max length        
        If (String.isNotBlank(oSearchWrapper.sMedicareId) && (oSearchWrapper.sMedicareId.length() < 10 || oSearchWrapper.sMedicareId.length()>12))
        {              
             validData = false;             
             HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.TRR_Search_Medicare_Error,true);  
     
         }        
        
        // Validation for Searchcombination FirstName,LastName And DOB        
        If (String.isNotBlank(oSearchWrapper.sFirstName) ||string.isNotBlank(oSearchWrapper.sLastName) || string.isNotBlank(oSearchWrapper.sBirthdate))
           {
               If (String.isBlank(oSearchWrapper.sFirstName) || string.isBlank(oSearchWrapper.sLastName) || string.isBlank(oSearchWrapper.sBirthdate))
               {
                     validData = false;
                     HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.TRR_FN_LN_BD_Error,true);                 
               }
               else
               {
                  if(String.isNotBlank(oSearchWrapper.sFirstName) && !Pattern.matches(HUMAlphaNumericPattern, oSearchWrapper.sFirstName))
                  {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumSearchFirstNameAlphaNumericValidation,true);
                      validData = false;
                  }
                  if(String.isNotBlank(oSearchWrapper.sLastName) && !Pattern.matches(HUMSELastNameAlphaNumericPattern, oSearchWrapper.sLastName))
                  {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumSESearchLastNameAlphaNumericValidation,true);
                      validData = false;
                  }
                  if(String.isNotBlank(oSearchWrapper.sBirthdate) && !Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sBirthdate))
                  {
                    validData = false;
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumStartnEndDate,true);
                  }
                }
          }
          
          if(string.isNotBlank(oSearchWrapper.sHumanaReceivedDateFrom) || string.isNotBlank(oSearchWrapper.sHumanaReceivedDateTo))
          {
              if(string.isBlank(oSearchWrapper.sHumanaReceivedDateFrom)){
                  validData = false;
                  HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.TRR_Search_Humana_Date_Error,true);  
              }
              else if(string.isBlank(oSearchWrapper.sHumanaReceivedDateTo)){
                  validData = false;
                  HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.TRR_Search_Humana_Date_Error,true);  
              } 
              else if(!Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sHumanaReceivedDateFrom) || !Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sHumanaReceivedDateTo))
              {
                validData = false;
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumStartnEndDate,true);
              }
              else if((Date.parse(oSearchWrapper.sHumanaReceivedDateFrom).daysBetween(Date.parse(oSearchWrapper.sHumanaReceivedDateTo))) < 0)
              {
                validData = false;
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumSearchEnrollmentDateValid1,true);
              }               
          }
          
       } 
       return validData ;
       
    }
    
    /*
    * Method Name   :   errorExists
    * Description   :   This Method is for validating the inputs
    * Return Type   :   Boolean
    * Parameters    :   NA
    */
    private Boolean errorExists()
    {
        unknownMemSearch();
        if(bEnrRes == false) return true;
        else return false;
    }
    
   
    /*
   * Method Name   :    searchEESService
   * Description   :    Method to search Member in EES System                        
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
    public Continuation searchEESService()
    {
        if(switchCIMTVSSN)
        {
            oSearchWrapper.sSSN = cimtvssn;
            bEnrres = false;
            if(HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(oSearchWrapper.sFirstName, oSearchWrapper.sLastName, 
                                                oSearchWrapper.sBirthdate, oSearchWrapper.sState, oSearchWrapper.sSSN)) 
            {
                IF(string.isEmpty(oSearchWrapper.sSSN))
                {
                    return searchEESService_Other();
                }
                ELSE
                {
                    return searchEESService_SSN('');
                }
            }
            else 
            {
                return null;
            }
        }
        else {    
        
            HUM_Webservice_Callout__c objWebserviceCalloutEES = HUM_Webservice_Callout__c.getInstance('EESLegacySearchService');
            Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutEES.Timeout__c)/1000;
            
            Continuation oConEES;
            if(errorExists())   return NULL;
            oEesLegacy = new HUMEESLegacyMemberSearchHelper();
            if(objWebserviceCalloutEES != null)
            {
                if(objWebserviceCalloutEES.Active__c)
                {
                    oConEES = new Continuation(TIMEOUT_INT_SECS);
                    oConEES.continuationMethod = 'EESServiceProcessResponse';
                    oEESResponse = oEesLegacy.submitAsync(oConEES, oSearchWrapper.sSSN);
                }
            }
            return oConEES;
        }
    }
    public Continuation searchEESService_SSN(String searchBy)
    {
        HUM_Webservice_Callout__c objWebserviceCalloutEES = HUM_Webservice_Callout__c.getInstance('EESLegacySearchService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutEES.Timeout__c)/1000;
        
        Continuation oConEES;
        if(errorExists())   return NULL;
        oEesLegacy = new HUMEESLegacyMemberSearchHelper();
       	if(objWebserviceCalloutEES != null)
        {
            if(objWebserviceCalloutEES.Active__c)
            {
                oConEES = new Continuation(TIMEOUT_INT_SECS);
                if(searchBy == system.label.HUMEESLegacyMemberSearchHelper_FirstName)
                    {oConEES.continuationMethod = 'EESServiceProcessResponseNew';}
                else 
                    {oConEES.continuationMethod = 'EESServiceProcessSSNResponse';}
                oEESResponse = oEesLegacy.submitAsync(oConEES, oSearchWrapper.sSSN);
            }
        }
        return oConEES;
    }

    
    public Continuation searchEESService_Other()
    {
        try
        {
            HUM_Webservice_Callout__c objWebserviceCalloutEES = HUM_Webservice_Callout__c.getInstance('EESEnhancedService');
            Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutEES.Timeout__c)/1000;
            Continuation oConEES = new Continuation(TIMEOUT_INT_SECS);
            oConEES.ContinuationMethod='EESServiceProcessResponse_Other';
            Http http = new Http();
            HttpRequest request = new HttpRequest();
            HttpResponse response;
            request.setEndpoint(objWebserviceCalloutEES.End_Point_URL__c);
            request.setMethod(objWebserviceCalloutEES.Request_Type__c);
            request.setHeader('Content-Type', objWebserviceCalloutEES.Content_Type__c);
            if(!Test.isRunningTest())
            request.setClientCertificateName(objWebserviceCalloutEES.Certificate_Name__c);
            request.setBody(HUMMemberSearchEnrollment_H_EESService.ESSServiceRequestBody(oSearchWrapper.sfirstName.toUpperCase(), oSearchWrapper.slastName.toUpperCase(), oSearchWrapper.sBirthdate, oSearchWrapper.sState));
            sRequestCIMTV = oConEES.addHttpRequest(request);
            return oConEES;
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'HUMMemberSearchEnrollmentControllerV2','searchEESService_Other');     
        } 
        return  null;    
    }
    
    /*
   * Method Name   :    EESServiceProcessResponse_Other
   * Description   :    Process successful response into search results                     
   * Return Type   :    list<HUMMemberSearchEnrollmentHelper>
   * Parameters    :    NA
   */

    public continuation EESServiceProcessResponse_Other()
    {
        try 
        {
            HttpResponse response = Continuation.getResponse(sRequestCIMTV);
            list<HUMMemberSearchEnrollmentHelper> lhseh = new list<HUMMemberSearchEnrollmentHelper>();    
            string lstResults = response.getBody();
            return ProcessResponse_OtherDemographic(lstResults);
        }
        catch(CalloutException e)
        {
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'EESServiceProcessResponse_Other');
            if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) 
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
            HUMExceptionHelper.bShowErrorOnPage = true;
            return null;
        }
        catch(Exception ex)
        {
            bEnrres = false;
            HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchEnrollmentV2', 'EESServiceProcessResponse_Other');
            HUMExceptionHelper.bShowErrorOnPage = true;
            return null;
        }
    }
    public Continuation ProcessResponse_OtherDemographic(String lstResults)
    {
        try
        {
            Map<string,Object> results = 
            (Map<string,Object>)JSON.deserializeUntyped(lstResults);
            Map<string,Object> results1 = ( Map<string,Object>)results.get('GetPlatformMembersResponse');
            IF(results1 != null)
            {
                bEnrres = true;
                FOR(object platformMember : (List<Object>)results1.get('platformMembers'))
                {
                    Map<string, object> mso = (map<string, object>) platformMember;
                    if(platformMember != null)
                    {
                        if(  (string)mso.get('ssn') != null )
                        {
                            oSearchWrapper.sSSN = (string)mso.get('ssn');
                        }
                    }
                }
            }
            ELSE IF(results.get('Fault') != null)
            {
                // THROW AN ERROR
                bEnrres = false;
                throw new HUMCustomException('No Result(s) found');
            }
            IF(string.isEmpty(oSearchWrapper.sSSN))
            {
                    // THROW AN ERROR
                    bEnrres = false;
                    throw new HUMCustomException('No Result(s) found');
            }
            else 
            {
                    return searchEESService_SSN(system.label.HUMEESLegacyMemberSearchHelper_FirstName);
            }
        }
        catch(CalloutException e)
        {
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'EESServiceProcessResponse_Other');
            if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) 
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
            HUMExceptionHelper.bShowErrorOnPage = true;
            return null;
        }
        catch(Exception ex)
        {
            bEnrres = false;
            HUMExceptionHelper.logErrors(ex, 'HUMMemberSearchEnrollmentV2', 'EESServiceProcessResponse_Other');
            HUMExceptionHelper.bShowErrorOnPage = true;
            return null;
        }
    }

   
       /*
   * Method Name   :    EESServiceProcessResponse
   * Description   :    Callback Method that will be invoked for EES Service                      
   * Return Type   :    Object
   * Parameters    :    NA
   */
    public Object EESServiceProcessResponse()
    {
        try
        {
            oEesLegacy = new HUMEESLegacyMemberSearchHelper();
            if(!Test.isRunningTest())   lstLegacyService.addAll(oEesLegacy.parseEEESResponse(oEESResponse.getValue(), oSearchWrapper.sSSN));
            if(lstLegacyService.size() == 0)
            {
                bEnrres = false;
                throw new HUMCustomException('No Result(s) found');
            }
            return null;
        }
        catch(CalloutException e)
        {
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'EESServiceProcessResponse');
            if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        catch(Exception ex)
        {
            bIsEnrollmentError =  true;
            bEnrres = false;
            HUMExceptionHelper.logErrors(ex, 'AEServiceProcessResponse', 'EESServiceProcessResponse');
        }
        return NULL;
    }
       /*
   * Method Name   :    EESServiceProcessResponseNew
   * Description   :    Callback Method that will be invoked for EES Service                      
   * Return Type   :    Object
   * Parameters    :    NA
   */
  public Object EESServiceProcessResponseNew()
  {
      try
      {
          oEesLegacy = new HUMEESLegacyMemberSearchHelper();
          if(!Test.isRunningTest())   lstLegacyService.addAll(oEesLegacy.parseEEESResponseNew(oEESResponse.getValue(), oSearchWrapper.sSSN));
          if(lstLegacyService.size() == 0)
          {
              bEnrres = false;
              throw new HUMCustomException('No Result(s) found');
          }
          return null;
      }
      catch(CalloutException e)
      {
          lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'EESServiceProcessResponse');
          if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
          HUMExceptionHelper.bShowErrorOnPage = true;
      }
      catch(Exception ex)
      {
          bIsEnrollmentError =  true;
          bEnrres = false;
          HUMExceptionHelper.logErrors(ex, 'AEServiceProcessResponse', 'EESServiceProcessResponse');
      }
      return NULL;
  }
       /*
   * Method Name   :    EESServiceProcessSSNResponse
   * Description   :    Callback Method that will be invoked for EES Service                      
   * Return Type   :    Object
   * Parameters    :    NA
   */
   
  public Object EESServiceProcessSSNResponse()
  {
      try
      {
          oEesLegacy = new HUMEESLegacyMemberSearchHelper();
          if(!Test.isRunningTest())   lstLegacyService.addAll(oEesLegacy.parseEEESXMLResponse(oEESResponse.getValue(), oSearchWrapper.sSSN));
          if(lstLegacyService.size() == 0)
          {
              bEnrres = false;
              throw new HUMCustomException('No Result(s) found');
          }
          return null;
      }
      catch(CalloutException e)
      {
          lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'EESServiceProcessSSNResponse');
          if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
          HUMExceptionHelper.bShowErrorOnPage = true;
      }
      catch(Exception ex)
      {
          bIsEnrollmentError =  true;
          bEnrres = false;
          HUMExceptionHelper.logErrors(ex, 'AEServiceProcessResponse', 'EESServiceProcessSSNResponse');
      }
      return NULL;
  }
    
  /*
   * Method Name   :    searchH1Service
   * Description   :    Method to search Member in H1Exchange Service                        
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
    public Continuation searchH1Service()
    {
        HUM_Webservice_Callout__c objWebserviceCalloutH1 = HUM_Webservice_Callout__c.getInstance('H1ExchangeService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutH1.Timeout__c)/1000;
        
        String sExchangeID = ApexPages.currentPage().getParameters().get('exchangeID');
        sExchangeID = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sExchangeID) : sExchangeID;
        if(NULL != sExchangeID) osearchWrapper.sSSN = sExchangeID;
        
        Continuation oConH1;
      
        if(NULL == sExchangeID && errorExists())   return NULL;
        
        sMethodName = System.Label.HUMSearchEnrollmentH1Exchange;
        AsyncHUMH1SearchService.AsyncAM65AM65Port requestService = new AsyncHUMH1SearchService.AsyncAM65AM65Port(objWebserviceCalloutH1.End_Point_URL__c,objWebserviceCalloutH1.Certificate_Name__c,Integer.valueOf(objWebserviceCalloutH1.Timeout__c));            
        oReqMessage = new HUMH1SearchRequestHelper.EnrollmentDetailRequest ();
        oReqMessage.mbrSsn = osearchWrapper.sSSN;
        oReqMessage.mbrDob = '';
        oReqMessage.effDateFr = '';
        oReqMessage.effDateTo = '';
        oReqMessage.recptDateFr = '';
        oReqMessage.recptDateTo = '';
        oReqMessage.zipCode = '';
        HUMH1SearchRequestHelper.EnrollmentDetailReq oReq = new HUMH1SearchRequestHelper.EnrollmentDetailReq();
        oReq.searchType = system.label.HUMH1ExchRequestType;
        if(objWebserviceCalloutH1 != null)
        {
            if(objWebserviceCalloutH1.Active__c)
            {
                oConH1 = new Continuation(TIMEOUT_INT_SECS);
                oConH1.continuationMethod = 'H1ServiceProcessResponse';
                if(!Test.isRunningTest())
                oResponseH1 = requestService.beginAM65AM65Operation(oConH1,oReqMessage,oReq.searchType);
            }
        }
        return oConH1;
    }
    
    /*
   * Method Name   :    callH1Service
   * Description   :    Method to fetch value returned from callout                        
   * Return Type   :    HUMH1SearchResponseHelper.EnrollmentDetailResponse
   * Parameters    :    NA
   */
    public HUMH1SearchResponseHelper.EnrollmentDetailResponse callH1Service()
    {
        try
        {
            if(!Test.isRunningTest()) return oResponseH1.getValue();
            else    throw new HUMCustomException('');
        }
        catch(Exception e)
        {
            bEnrres = false;
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog =true;
            lstLogErrors= HUMExceptionHelper.logErrors(e,sClassName, sMethodName);
            HUMExceptionHelper.bShowErrorOnPage = true;
            return null;
        }
        return null;
    } 
    
    /*
   * Method Name   :    H1ServiceProcessResponse
   * Description   :    Callback method which will be invoked for asyn H1Exchange service                        
   * Return Type   :    Object
   * Parameters    :    NA
   */
    public Object H1ServiceProcessResponse()
    {
        HUMH1SearchResponseHelper.EnrollmentDetail oMember = null;
        
        if(!Test.isRunningTest())
        oRespMesageH1 = callH1Service();
        if(oRespMesageH1 != null && oRespMesageH1.EnrollmentDetailList != null && oRespMesageH1.EnrollmentDetailList.EnrollmentDetail != null)
        {
            for(Integer iIndex = 0; iIndex < oRespMesageH1.EnrollmentDetailList.EnrollmentDetail.size(); iIndex++)
            {    
                 if(oRespMesageH1.EnrollmentDetailList.EnrollmentDetail[iIndex].strSsn == oSearchWrapper.sSSN)
                  {
                    oMember = oRespMesageH1.EnrollmentDetailList.EnrollmentDetail[iIndex];            
                    humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oMember);             
                    lstLegacyService.add(humLegMemberObj);
                    bEnrres = true;
                    sSSNID = oReqMessage.mbrSsn;
                  }
            }
        }
        if(NULL == lstLegacyService || (NULL != lstLegacyService && lstLegacyService.isEmpty()) ) return searchOHBPService();
        return null;
    }
    
     /*
   * Method Name   :    H1PageExchangeSearch()
   * Description   :    This Method is for calling H1 Exchange system to search extra fields for a member not found in salesforce.
   * Return Type   :    void
   * Parameters    :    NA
   */  
  public void H1PageExchangeSearch()
  { 
      sExchangeID = ApexPages.currentPage().getParameters().get('exchangeID');
       sExchangeID = bESAPILibSecurityfixSwitch ? ESAPI.encoder().SFDC_HTMLENCODE(sExchangeID) : sExchangeID;
      osearchWrapper.sSSN = sExchangeID;
      searchH1Service();
  } 
    
  /*
   * Method Name   :    searchOHBPService
   * Description   :    Method to search Member in OHBP System                        
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
   
    public Continuation searchOHBPService()
    {
        HUM_Webservice_Callout__c objWebserviceCalloutOHBP = HUM_Webservice_Callout__c.getInstance('OHBPService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutOHBP.Timeout__c)/1000;
        // Ininitializing the continuation and defining the callback method
        Continuation oConOHBP;
       
        // Validating data for OHPB Service
        if(errorExists())   return NULL;
        
        // Creating request for async callout
        sMethodName = System.label.HUMSearchEnrollmentohbpSearch;
        AsyncHUMPersonAllLegalService.Asyncsoap requestService = new AsyncHUMPersonAllLegalService.Asyncsoap(objWebserviceCalloutOHBP.End_Point_URL__c,objWebserviceCalloutOHBP.Certificate_Name__c,Integer.valueOf(objWebserviceCalloutOHBP.Timeout__c));
        string sXml = system.label.HUMOHBPMemberSearchReq_Header;
        sXml += osearchWrapper.sSSN+system.label.HUMOHBPMemberSearchReq_End; 
        
        // Async callout for OHBP
        if(objWebserviceCalloutOHBP != null)
        {
            if(objWebserviceCalloutOHBP.Active__c)
            {
                oConOHBP =  new Continuation(TIMEOUT_INT_SECS);
                oConOHBP.continuationMethod = 'OHBPServiceProcessResponse';
                if(!Test.isRunningTest()) oRequestOHBP = requestService.beginGetMemberInformationXml(oConOHBP, sXml);
            }
        }
        return oConOHBP;
    }
    
    /*
   * Method Name   :    callOHBPService
   * Description   :    Method to fetch value returned from callout                        
   * Return Type   :    String
   * Parameters    :    NA
   */
    public string callOHBPService()
    {
        if(!Test.isRunningTest()) return oRequestOHBP.getValue();
        return null;
    }
    
    /*
   * Method Name   :    OHBPServiceProcessResponse
   * Description   :    Callback method which will be invoked for asyn OHBP service                        
   * Return Type   :    Object
   * Parameters    :    NA
   */
    public Object OHBPServiceProcessResponse()
    {
        try
        {
            if(!Test.isRunningTest())   oResponseOHBP = callOHBPService();
            
            // Fetching and populating the result returned from callout
            List<String> lstGetMemberNodes = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPMember);
        
            //Parse Each Member for Account information
            for(String sMember : lstGetMemberNodes)
            {
                  Account oAcc = new Account();    
                  //Get Name
                  List<String> lstFirstName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchFirstName);               
                  Map<String,String> mapAccount = new Map<String,String>();
        
                  if(NULL != lstFirstName && !lstFirstName.isEmpty()) mapAccount.put(system.label.HUMOHBPSearchFstName, lstFirstName[0]);        
                  //Get LastName
                  List<String> lstLastName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchLastName); 
                  if(NULL != lstLastName && !lstLastName.isEmpty()) mapAccount.put(system.label.HUMOHBPSearchLstName, lstLastName[0]);
        
                  //Get DateOfBirth             
                  List<String> lstDateOfBirth = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchBirthDate);
                  if(NULL != lstDateOfBirth && !lstDateOfBirth.isEmpty()) mapAccount.put(system.label.HUMOHBPSearchBirthDt, lstDateOfBirth[0]); 
                  
                  // Group ID 
                  List<String> lstPlanID = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchPlanID);
                  if(NULL != lstPlanID && !lstPlanID.isEmpty())  mapAccount.put(system.label.HUMOHBPSearchIDPlan, lstPlanID[0]);
        
                  //ExternalID
                  List<String> lstExternalID = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchTranID);
                  if(NULL != lstExternalID && !lstExternalID.isEmpty())    mapAccount.put(system.label.HUMOHBPSearchExtID, lstExternalID[0]);
        
                  //Product
                  List<String> lstProductName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP, system.label.HUMOHBPSearchPlanName);
                  if(NULL != lstProductName && !lstProductName.isEmpty())   mapAccount.put(system.label.HUMOHBPSearchProd, lstProductName[0]);
        
                  List<String> lstEffectiveDate = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP, system.label.HUMOHBPSearchEffDate);
                  if(NULL != lstEffectiveDate  &&  !lstEffectiveDate.isEmpty())   mapAccount.put(system.label.HUMOHBPSearchEfectDate, lstEffectiveDate[0]);
                
                   //Zip
                 list<string> lstzip = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP, 'Zip');
                 if(NULL != lstzip  &&  !lstzip.isEmpty())   mapAccount.put('PersonMailingPostalCode', lstzip [0]);
                 
                  humLegMemberObj = new HUMMemberSearchEnrollmentHelper(mapAccount);
                  lstLegacyService.add(humLegMemberObj );   
            }
            if(lstLegacyService.isEmpty())
            {
                bEnrres = false;                    
                throw new HUMCustomException(HumSearchEnrollmentNoRes);
                return null;
            }
            bEnrres=true;
            if(Test.isRunningTest())    throw new HUMCustomException('');
        }
        catch(CalloutException e)
        {
            if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) 
            {
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,'No Result(s) found',true);
            }
            else
            {
                lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'OHBPServiceProcessResponse');
            }
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        catch(Exception e)
        {
            bEnrres = false;
          HUMExceptionHelper.bShowErrorOnPage = true;
          HUMExceptionHelper.bRestrictLog = true;
          lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'OHBPServiceProcessResponse');
          HUMExceptionHelper.bShowErrorOnPage = true;
        }
        return null;
    }
 
  /*
   * Method Name   :    searchTrackEnrollmentSearvice()
   * Description   :    Method which will be called for searching Member in Track Enrollment System.                        
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
    public Continuation searchTrackEnrollmentSearvice()
    {
        
        HUM_Webservice_Callout__c objWebServiceCalloutTE = HUM_Webservice_Callout__c.getValues('TrackEnrollmentService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebServiceCalloutTE.Timeout__c)/1000;
        Continuation oConTE;
        if(errorExists())   return NULL;
        
        if(objWebServiceCalloutTE != null)
        {
            if(objWebServiceCalloutTE.Active__c)
            {
                oConTE = new Continuation(TIMEOUT_INT_SECS);
                oConTE.continuationMethod = 'TrackEnrollmentSearviceProcessResponse';
                TrackEnrollmentSearch(oConTE);
            }
        }
        
        return oConTE;
    }
    
     /*
   * Method Name   :    TrackEnrollmentSearviceProcessResponse()
   * Description   :    Callback Method which will be invoked for Track Enrollment System.                        
   * Return Type   :    Object
   * Parameters    :    NA
   */
    public Object TrackEnrollmentSearviceProcessResponse()
    {   
        try
        {
            if(!Test.isRunningTest()) processTrackResponseResult(respfuture.getvalue());
            else    throw new HUMCustomException('');
        }
        catch(CalloutException e)
        {
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'TrackEnrollmentSearviceProcessResponse');
            if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out'))
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
         catch(Exception e)
        {          
            if(e.getmessage() == system.label.HUMCBISRestrictedError)
              {
                HUMExceptionHelper.bShowErrorOnPage = true;
              }  
            else { HUMExceptionHelper.bShowErrorOnPage = false; }
            HUMExceptionHelper.bShowErrorOnPage = true;
              HUMExceptionHelper.bRestrictLog = true;
              lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'TrackEnrollmentSearviceProcessResponse');
              HUMExceptionHelper.bShowErrorOnPage = true;
         
        }
        
        return null;
    }
    
     /*
   * Method Name   :    searchAEService()
   * Description   :    Method which will be called for searching Member in Automated Enrollment System                      
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
    public Continuation searchAEService()
    {
        HUM_Webservice_Callout__c objWebServiceCalloutAE = HUM_Webservice_Callout__c.getValues(HUMAEHistSvc);
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebServiceCalloutAE.Timeout__c)/1000;
        
        Continuation oConAE;
        if(errorExists())   return NULL;
        if(objWebServiceCalloutAE != null)
        {
            if(objWebServiceCalloutAE.Active__c)
            {
                oConAE = new Continuation(TIMEOUT_INT_SECS);
                oConAE.ContinuationMethod = 'AEServiceProcessResponse';
                AEHistorySearch(oConAE);
            }
        }
        return oConAE;
    }
    
     /*
   * Method Name   :    AEServiceProcessResponse()
   * Description   :    Callback Method which will be invoked for Automated Enrollment Service                     
   * Return Type   :    Object
   * Parameters    :    NA
   */
    public Object AEServiceProcessResponse()
    {
        try
        {
            if(!Test.isRunningTest()) processAEResponseResult(oResponseMsg.getValue());
        }
        catch(CalloutException e)
        {
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'AEServiceProcessResponse');
            if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        catch(Exception ex)
        {
            bIsEnrollmentError =  true;
            HUMExceptionHelper.logErrors(ex, 'AEServiceProcessResponse', 'AEServiceProcessResponse');
        }
        return null;
    }
    

    /*
   * Method Name   :    seachCBISCodeSpeciality()
   * Description   :    Method which will be called for searching Member in CBIS_V2 for REST Service.                        
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
    public Continuation seachCBISCodeSpeciality()
    {
        if(errorExists())   return NULL;
        HUMSearchEnrollmentHelper oSearchWrapperRequest = new HUMSearchEnrollmentHelper();
        if(String.isBlank(oSearchWrapper.sSSN))
        {
            oSearchWrapperRequest.sFirstName = oSearchWrapper.sFirstName;
            oSearchWrapperRequest.sLastName = oSearchWrapper.sLastName;
            if(NULL != oSearchWrapper.sBirthdate){
                Date dt = Date.parse(oSearchWrapper.sBirthdate);
                DateTime dtAndTime = DateTime.newInstance(dt.year(),dt.month(),dt.day(),0,0,0);
                oSearchWrapperRequest.sBirthdate = dtAndTime.format('yyyy-MM-dd\'T\'HH:mm:\'00\'');
            }
            oSearchWrapperRequest.sState = oSearchWrapper.sState;
        }
        else
        {
            oSearchWrapperRequest.sFirstName ='' ;
            oSearchWrapperRequest.sLastName = '';
            oSearchWrapperRequest.sBirthdate = '';
            oSearchWrapperRequest.sState = '';
        	oSearchWrapperRequest.sSSN = oSearchWrapper.sSSN;
        }
           
        try{
             HUM_Webservice_Callout__c ServiceObj = HUM_Webservice_Callout__c.getValues(cbisSearchEnrollmentV2);
             Integer iTIMEOUT_INT_SECS = Integer.valueof(ServiceObj.Timeout__c)/1000;
             Continuation cont = new Continuation(iTIMEOUT_INT_SECS );
             cont.continuationMethod=processResponseSpecialty;
             HttpRequest request = new HttpRequest();
             oServiceCBISSpecialty.setupRequest(request, ServiceObj, oSearchWrapperRequest); 
             sRequest = cont.addHttpRequest(request);
             return cont;
            }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'HUMMemberSearchEnrollmentControllerV2','seachCBISCodeSpeciality');     
            } 
            return NULL;
    }
    
    
    /*
     * Method Name   :    processResponseCBISSpecialty
     * Description   :    This method process Http Response from CBIS Specialty REST Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    public object processResponseCBISSpecialty()
    {   
        try 
        { 
          HttpResponse response = Continuation.getResponse(sRequest); 
          boolean isResponseValid = checkResponseValidity(response);
          if(isResponseValid)
              { 
                respond = (HUMCBISSearchResponse_H_HUM)System.JSON.deserialize(response.getBody(),HUMCBISSearchResponse_H_HUM.class);
			    if(respond.GetPendHistoryTermsDetailsResponse.Status.IsSuccesful<>null && respond.GetPendHistoryTermsDetailsResponse.Status.IsSuccesful.equals(GlobalCommonConstants_HUM.STRING_TRUE))
                {
                 if(respond<>null && respond.GetPendHistoryTermsDetailsResponse<>null && respond.GetPendHistoryTermsDetailsResponse.ResponseItems<>null && respond.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem<>null)
                 { 
                  setCBISMemberSpecialtyDetails(respond);
                 }
                }else{
                 throw new HUMCustomException(humSearchEnrollmentNoRes);   
                }
              }
              else
              {
                HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), 'HUMMemberSearchEnrollmentControllerV2', 'processResponseCBISSpecialty');
              }
        }
        catch(Exception e)
        {   
         lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentControllerV2', 'CBISCodeProcessResponse');
        }
 		return null;  
    }
    
    /*
     * Method Name   :  setCBISMemberSpecialtyDetails()
     * Description   :  This method is used too set CBIS Specialty Search Result from RESTService
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       public void setCBISMemberSpecialtyDetails(HUMCBISSearchResponse_H_HUM resObj){
       bIsWarningExist=false;
       HUMCBISSearchResponse_H_HUM.ResponseItem oMember = NULL;
       lstLegacyService = new List<HUMMemberSearchEnrollmentHelper>();
       if(resObj != NULL)
       {
         if(resObj.GetPendHistoryTermsDetailsResponse<>null && resObj.GetPendHistoryTermsDetailsResponse.ResponseItems<>null && resObj.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem<>null)
		 {
          for(Integer iIndex = 0; iIndex < resObj.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem.size(); iIndex++)
            {    
              oMember = resObj.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem[iIndex];   
              humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oMember); 
              lstLegacyService.add(humLegMemberObj );  
              bEnrres = true;
            }
			if(!bIsWarningExist) 
            {
              bIsWarningExist=humLegMemberObj.bdisplayInteractbutton ;
            }
            if(bIsWarningExist) throw new HUMCustomException(system.label.HUMCBISRestrictedError);
         }
        }
        if(lstLegacyService.isEmpty()) 
         {
          bEnrres = false;                    
          throw new HUMCustomException(humSearchEnrollmentNoRes);
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
            HUMExceptionHelper.logErrors(ex,'HUMMemberSearchEnrollmentControllerV2','checkResponseValidity');
            returnValue = false;
        }
        return returnValue;
    }

  /*
   * Method Name   :    seachCBISCode()
   * Description   :    Method which will be called for searching Member in CBIS System.                        
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
    public Continuation seachCBISCode()
    {
        HUM_Webservice_Callout__c objWebserviceCalloutCBIS = HUM_Webservice_Callout__c.getInstance('HUMCbisSearchEnrollment');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutCBIS.Timeout__c)/1000;
        HUMCBISSearchServiceHelper.PendHistoryandTermsRequestDTO cbisRequestDTO;
        
        // Initializing continuation instance and defining callback method
        Continuation oConCBIS;
        
        // Validation check for CBIS
        if(errorExists())   return NULL;
        
        // Creating request for async callout 
        AsyncHUMCBISSearchService.AsyncEndPoint_GetPendHistoryAndTermsDetails requestService= new AsyncHUMCBISSearchService.AsyncEndPoint_GetPendHistoryAndTermsDetails();
        requestService.clientCertName_x = objWebserviceCalloutCBIS.Certificate_Name__c;
        requestService.timeout_x = Integer.valueOf(objWebserviceCalloutCBIS.Timeout__c);
        requestService.endpoint_x = objWebserviceCalloutCBIS.End_Point_URL__c;
        
        
        cbisRequestDTO = new HUMCBISSearchServiceHelper.PendHistoryandTermsRequestDTO();
        cbisRequestDTO.FirstName= oSearchWrapper.sFirstName;
        cbisRequestDTO.LastName= oSearchWrapper.sLastName;
        if(NULL != oSearchWrapper.sBirthdate)   cbisRequestDTO.DOB = date.parse(oSearchWrapper.sBirthdate);
        cbisRequestDTO.stateCode= oSearchWrapper.sState;   
        
        
        oResponseCBIS = new AsyncHUMCBISSearchService.GetPendHistoryTermsDetailsResponse_elementFuture();
        if(objWebserviceCalloutCBIS != null)
        {
            if(objWebserviceCalloutCBIS.Active__c)
            {
                oConCBIS = new Continuation(TIMEOUT_INT_SECS);
                oConCBIS.continuationMethod = 'CBISCodeProcessResponse';
                // callout to CBIS System
                if(!Test.isRunningTest()) oResponseCBIS = requestService.beginGetPendHistoryTermsDetails(oConCBIS,cbisRequestDTO);
            }
        }
        return oConCBIS;
    }
    
   /*
   * Method Name   :    callCBISService()
   * Description   :    Method to fetch the value returned from the callout                        
   * Return Type   :    HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO
   * Parameters    :    NA
   */
    public HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO callCBISService()
    {
        if(!Test.isRunningTest()) return oResponseCBIS.getValue();// returning the value retrieved from callout
        return null;
    } 
    
    /*
   * Method Name   :    CBISCodeProcessResponse()
   * Description   :    Callback method which will be invoked for CBIS Service                      
   * Return Type   :    Object
   * Parameters    :    NA
   */
    public Object CBISCodeProcessResponse()
    {
        try
        {
            
            bIsWarningExist=false;
            if(!Test.isRunningTest()) oResponse = callCBISService();
            
            // parsing the response 
            HUMCBISSearchServiceHelper.ResponseItem oMember = NULL;
            
            lstLegacyService = new List<HUMMemberSearchEnrollmentHelper>();
            
            if(oResponse != null && oResponse.ResponseItems != null && oResponse.ResponseItems.ResponseItem != null)
            {
                for(Integer iIndex = 0; iIndex < oResponse.ResponseItems.ResponseItem.size(); iIndex++)
                {    
                    oMember = oResponse.ResponseItems.ResponseItem[iIndex];   
                    humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oMember); 
                    lstLegacyService.add(humLegMemberObj );  
                    bEnrres = true;
                }
                
                 if (!bIsWarningExist) 
                {
                    bIsWarningExist=humLegMemberObj.bdisplayInteractbutton ;
                }
                
                if (bIsWarningExist) throw new HUMCustomException(system.label.HUMCBISRestrictedError);
            }
            if(lstLegacyService.isEmpty())
            {
                bEnrres = false;                    
                throw new HUMCustomException(HumSearchEnrollmentNoRes);
                return null;
            }
        }
        catch(CalloutException e)
        {
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'CBISCodeProcessResponse');
            if(e.getMessage().containsIgnoreCase('Web service callout failed') || e.getMessage().containsIgnoreCase('IO Exception: Time out')) HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.Web_Service_Callout_Timeout_Error,true);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        catch(Exception e)
        {          
            if(e.getmessage() == system.label.HUMCBISRestrictedError)
              {
                HUMExceptionHelper.bShowErrorOnPage = true;
              }  
            else { HUMExceptionHelper.bShowErrorOnPage = false; }
              HUMExceptionHelper.bShowErrorOnPage = true;
              HUMExceptionHelper.bRestrictLog = true;
              lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentV2', 'CBISCodeProcessResponse');
              HUMExceptionHelper.bShowErrorOnPage = true;
            return null;
        } 
    return null;
    }
       
	/*
	* Method Name   :    TRRSearchService()
	* Description   :    This Method is sending request to external service
	* Return Type   :    Continuation
	* Parameters    :    NA
	*/
  
	public Continuation TRRSearchService()
	{
		bEnrres = false;
		lstLegacyService = new list<HUMMemberSearchEnrollmentHelper>();
		// Setup Request     
		if(!validationForTRR())  return NULL; // Validate search fields wrapper for TRR  
    
		// Setup Request
		
		HUM_Webservice_Callout__c TRRSearch= HUM_Webservice_Callout__c.getValues('TRR_Search');    
   
		integer TIMEOUT_INT_SECS = Integer.ValueOf(TRRSearch.Timeout__c);

		HttpRequest request= new HttpRequest();    
		request.setMethod(TRRSearch.Request_Type__c);  
		request.setHeader(Label.HUMServiceTesterController_content_type,TRRSearch.Content_Type__c);
		request.setTimeout(Integer.valueof(TRRSearch.Timeout__c));
		request.setEndpoint(TRRSearch.End_Point_URL__c);
		if (!Test.isRunningTest()) request.setClientCertificateName(TRRSearch.Certificate_Name__c);    
                
    
		TRRServiceRestRequest_DTO_HUM.TRRRequestClass oRequest= new TRRServiceRestRequest_DTO_HUM.TRRRequestClass();
		if((string.isNotBlank(oSearchWrapper.sMedicareId)))
		    {
		        oRequest.MedicareId = oSearchWrapper.sMedicareId;
		    }
		    if((string.isNotBlank(oSearchWrapper.sFirstName)))
		    {   
		        oRequest.FirstName = oSearchWrapper.sFirstName;
		    }            
		    if((string.isNotBlank(oSearchWrapper.sLastName)))
		    {                
		        oRequest.LastName = oSearchWrapper.sLastName;
		    } 
		    if((string.isNotBlank(oSearchWrapper.sHumanaReceivedDateFrom )))
		    {
		        oRequest.HumanaReceivedDateFrom =oSearchWrapper.sHumanaReceivedDateFrom ;
		    }
		    if((string.isNotBlank(oSearchWrapper.sHumanaReceivedDateTo ))){
                
		        oRequest.HumanaReceivedDateTo =oSearchWrapper.sHumanaReceivedDateTo ;
		    }
		    if((string.isNotBlank(oSearchWrapper.sBirthdate)))
		    {
		        oRequest.DateOfBirth = oSearchWrapper.sBirthdate;
		    }
    
    
		TRRServiceRestRequest_DTO_HUM oRequestbody = new TRRServiceRestRequest_DTO_HUM();
		oRequestbody.TRRRequest = oRequest;
    
		string sBody = JSON.serialize(oRequestbody); 
		request.setBody(sBody);
    
		Continuation oConTRR;
		if(TRRSearch != null)
		{
		    if(TRRSearch.Active__c)
		    {                
		        oConTRR = new Continuation(120);
		        oConTRR.continuationMethod ='TRRServiceResponse';
		        TRRSearchContinuationId = oConTRR.addHttpRequest(request);
                
		    }
		}
		return oConTRR;      

	}
  
	/*
	* Method Name   :    TRRServiceResponse()
	* Description   :    This Method is for parsing the response received from external service
	* Return Type   :    Object
	* Parameters    :    NA
	*/
  
	public Object TRRServiceResponse()
	{
		try
		{
			HttpResponse res = Continuation.getResponse(TRRSearchContinuationId);              
			string response = res.getBody();      
			TRRServiceRestResponse_DTO_HUM TRRRDetres = null;
			boolean successResponse = false;
      
			if(res.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM && res.getbody() != null && res.getbody().contains('TRRResponse'))
			{
          
			  TRRRDetres = (TRRServiceRestResponse_DTO_HUM)System.JSON.deserialize(res.getbody(), TRRServiceRestResponse_DTO_HUM.class);
          
			  successResponse = true;  
          
			}
			else
			{
			  HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',res.getbody(),HUM_Webservice_Callout__c.getValues('TRR_Search').End_Point_URL__c,'Connection Error');
			  throw new HUMCustomException(h);
			  successResponse = false;
			}
            
			if(successResponse)
			{
			   TRRServiceRestResponse_DTO_HUM.TRRResponse TRRResponse = new TRRServiceRestResponse_DTO_HUM.TRRResponse();
			   TRRResponse= TRRRDetres.TRRResponse;            
			   TRRServiceRestResponse_DTO_HUM.TRRDetails TRRDetails = new TRRServiceRestResponse_DTO_HUM.TRRDetails();
			   TRRDetails = TRRResponse.TRRDetails ; 
           
           
			   if(TRRDetails.TRRDetail != null && !TRRDetails.TRRDetail.isEmpty())
			   {               
			       List<TRRServiceRestResponse_DTO_HUM.TRRDetail> lstTRRDetail = new List<TRRServiceRestResponse_DTO_HUM.TRRDetail>();                      
			       lstTRRDetail = TRRDetails.TRRDetail ;                              
			       Integer TRRResultCounter = 1;
                              
			       For(TRRServiceRestResponse_DTO_HUM.TRRDetail objTRRDetail : lstTRRDetail){                                         
			           lstLegacyService.add(new HUMMemberSearchEnrollmentHelper(objTRRDetail,TRRResultCounter));
			           TRRResultCounter++;
			           bEnrres = true; 
			       }               
			       // Take entire results as JSON string
			       lstLegacyServiceJSON = JSON.serialize(lstLegacyService);              
                                        
			  }
			  else
			  {               
			       HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.TRR_No_Results_Found_Error,true);
			  }
                      
			 }
		}
		catch(Exception e)
		{
			HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentControllerV2','TRRServiceResponse');      
		}
    
		return null;
	}
  

  /*
   * Method Name   :    processAEResponseResult
   * Description   :    Method to fetch the value returned from the callout                        
   * Return Type   :    NA
   * Parameters    :    NA
   */
    @testVisible private void processAEResponseResult(HUMAEServicesHistoryResponse.ResponseComplexType oResponseComplex)
    {
        if(oResponseComplex != null && oResponseComplex.PendHistoryList != null)
        {
            HUMAEServicesHistoryResponse.PendHistoryList oPendHistoryList = oResponseComplex.PendHistoryList;
            HUMAEServicesHistoryResponse.PendHistory oPendHistory = NULL;
            if(oPendHistoryList.PendHistory != null)
            {
                for(Integer iIndex = 0; iIndex < oPendHistoryList.PendHistory.size(); iIndex++)
                {
                    if(oPendHistoryList.PendHistory[iIndex].Member_SSN == oSearchWrapper.sSSN || oPendHistoryList.PendHistory[iIndex].SSN == oSearchWrapper.sSSN)
                    {
                        oPendHistory = oPendHistoryList.PendHistory[iIndex];
                        break;
                    }
                }
                if(oPendHistory != null)
                {
                    humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oPendHistory);
                    lstLegacyService.add(humLegMemberObj);
                    bEnrres = true;
                }
                if(lstLegacyService.size() == 0)
                {
                    bEnrres = false;
                    throw new HUMCustomException('No Result(s) found');
                }
            }
        }
    }
    
     /*
   * Method Name   :    processTrackResponseResult
   * Description   :    Method to fetch the value returned from the callout                        
   * Return Type   :    NA
   * Parameters    :    NA
   */
    @testVisible private void processTrackResponseResult(enrollmentTrackResponse.ResponseComplexType  oResponseMsg)
    {
          if(oResponseMsg != null && oResponseMsg.TrackList != null && oResponseMsg.TrackList.Track != null)
            {
                for(enrollmentTrackResponse.Track oTrack : oResponseMsg.TrackList.track)
                {
                    humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oTrack);
                    bEnrres = true;
                    lstLegacyService.add(humLegMemberObj ); 
                }
            }
            
            if(lstLegacyService.isEmpty())
            {
                bEnrres = false;
                throw new HUMCustomException('No Result(s) found');
            }
    }
    
  /*
   * Method Name   :   UnknownMemSearch()
   * Description   :   This Method is for validating the inputs and calling the legacy system for unknown member Details.
   * Return Type   :   PageReference
   * Parameters    :   NA
   */
    public PageReference UnknownMemSearch()
    {
        try
        {
            sMethodName = 'UnknownMemSearch';
            lstLegacyService = new list<HUMMemberSearchEnrollmentHelper>();
            humLegMemberObj = null;
            if(String.isBlank(sServices) && String.isBlank(oSearchWrapper.sSSN))
            {
                bEnrres = false;
                throw new HUMCustomException(HUMSearchEnrollmentSsnservice); 
            }
            else if(String.isBlank(sServices))
            {
                bEnrres = false;
                throw new HUMCustomException(HUMSearchEnrollmentSelectServices); 
            }
            // Added for CBIS Service on 10 06 2014
            else if(String.isBlank(oSearchWrapper.sSSN) && (bIsCbisSelected == null || bIsCbisSelected == false))
            {
                bEnrres = false;
                throw new HUMCustomException(HumSearchenrollmentSsnReq); 
                 return null;       
            } 
            // Added for CBIS Service on 10 06 2014
            else if(bIsCbisSelected)
            {
                bEnrres = true;  
                bEnablebutton=true;
                validationForCBIS();    
            }
            else
            {
                bEnrRes = true;
                bEnablebutton=false;
                validationForNonCBIS();
            }
            
      return null;                                       
    }  
    catch(Exception e)
    {
      bIsEnrollmentError =  true;
      HUMExceptionHelper.logErrors(e, sClassName, sMethodName);
      return null;
    }        
  }

    /*
   * Method Name   :    TrackEnrollmentSearch()
   * Description   :    This Method is for calling Track Enrollment to search for a member not found in salesforce.                        
   * Return Type   :    NA
   * Parameters    :    NA
   */
  private void TrackEnrollmentSearch(Continuation oCon)
  { 
    try
    {
      sMethodName = HUMSearchEnrollmentTrackEnrollment;
      HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues('TrackEnrollmentService');
        AsyncAsyncTrackEnroll.AsyncBasicHttpBinding_ITwoWayAsync  oTrackEnrollmentService = new   AsyncAsyncTrackEnroll.AsyncBasicHttpBinding_ITwoWayAsync();
        oTrackEnrollmentService.clientCertName_x =oSvcHeaders.Certificate_Name__c;
        oTrackEnrollmentService.endpoint_x =oSvcHeaders.End_Point_URL__c;
        oTrackEnrollmentService.timeout_x = Integer.valueOf(oSvcHeaders.Timeout__c);
      Datetime todayDate = System.now(); //Datetime.valueof(System.now().format('yyyy-MM-dd\'T\'hh:mm:ss'));
      date d= null;
      date startdate= Date.parse(oSearchWrapper.sStartDate);
      date enddate =Date.parse(oSearchWrapper.sEndDate);
     
      if(!Test.isRunningTest() && oTrackEnrollmentService <> null)
          respfuture= oTrackEnrollmentService.beginSearchTrack(oCon,todayDate  ,oSearchWrapper.sSSN, startdate, enddate , 'Y', '', '', null, '');
        if(Test.isRunningTest())    throw new HUMCustomException('');
      }
    catch(Exception e)
    {
      HUMExceptionHelper.bShowErrorOnPage = false;
      HUMExceptionHelper.bRestrictLog = true;
      lstLogErrors= HUMExceptionHelper.logErrors(e, sClassName, sMethodName);
      HUMExceptionHelper.bShowErrorOnPage = true;
    }
  }

  /*
   * Method Name   :    AEHistorySearch()
   * Description   :    This Method is for calling AE History to search for a member not found in salesforce.
   * Return Type   :    void
   * Parameters    :    NA
   */
    private void AEHistorySearch(Continuation oCon) 
    { 
        try
        {
            sMethodName = HUMSearchEnrollmentAEHistory;
            HUM_Webservice_Callout__c svcHeaders = HUM_Webservice_Callout__c.getValues(HUMAEHistSvc); 
            AsyncHUMAETempURI.AsyncBasicHttpBinding_ITwoWayAsync oAEHistoryService = new AsyncHUMAETempURI.AsyncBasicHttpBinding_ITwoWayAsync();
            oAEHistoryService.endpoint_x = svcHeaders.End_Point_URL__c;
            oAEHistoryService.clientCertName_x = svcHeaders.Certificate_Name__c;
            oAEHistoryService.timeout_x = Integer.valueOf(svcHeaders.Timeout__c);
            
            if(!Test.isRunningTest() && oAEHistoryService <> null)
                oResponseMsg = oAEHistoryService.beginGetPendHistoryDetails(oCon, HUMAEHistReqHeader, oSearchWrapper.sSSN, '', '', Date.parse(oSearchWrapper.sStartDate), Date.parse(oSearchWrapper.sEndDate));
            if(Test.isRunningTest())    throw new HUMCustomException('');
        }
        catch(Exception e)
        {
            bEnrres = false;
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors= HUMExceptionHelper.logErrors(e,sClassName, sMethodName);
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
  }

  
  /*
   * Method Name   :   reset()
   * Description   :   This Method is used to clear data in Search Enrollment screen
   * Return Type   :   PageReference
   * Parameters    :   NA
   */
  public PageReference reset()
  {        
    oSearchWrapper.sStartDate = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
    oSearchWrapper.sEndDate   = (Datetime.now()).format('MM/dd/yyyy');             
    oSearchWrapper.sSSN = null ;
    cimtvssn = null;
    if(lstLegacyService != null ) lstLegacyService.clear(); 
    bEnrres = false;
    /* Added to clear all data. On 10 06 2014. */
    oSearchWrapper.sFirstName= null ;
    oSearchWrapper.sLastName= null ;
    oSearchWrapper.sState= null ;
    oSearchWrapper.sBirthdate = null;
	
    oSearchWrapper.sMedicareId = null;
    oSearchWrapper.sApplicationId = null;
    oSearchWrapper.sOECConfirmationId = null;
    oSearchWrapper.sBarCode = null;
    oSearchWrapper.sMedicaidId = null; 
	oSearchWrapper.sEffectiveDateFrom = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
    oSearchWrapper.sEffectiveDateTo = (Datetime.now()).format('MM/dd/yyyy');
    oSearchWrapper.sReceivedDateFrom = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
    oSearchWrapper.sReceivedDateTo = (Datetime.now()).format('MM/dd/yyyy');
    oSearchWrapper.sHumanaReceivedDateFrom = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
    oSearchWrapper.sHumanaReceivedDateTo = (Datetime.now()).format('MM/dd/yyyy');
    if(this.sServices !=null){
        if(this.sServices == 'CBIS')
        {
            this.sServices = 'CBIS';
        }
        else if(this.sServices.equals(HUMTRRCode))
        {        
            this.sServices = HUMTRRCode;
        }
        else if(this.sServices.equals(APPLICATION_SEARCH))
        {
            this.sServices = APPLICATION_SEARCH;
        }
    }else{
        this.sServices = Null;
    }
    return null;
  } 

  /*Method Name    :  buildUnknownAccountMap     
   * Description   :   Create list of accounts for  Unknown Account
   * Return Type   :   void   
   * Parameters    :   Account ID
   */
  public void buildUnknownAccountMap(Id iAccntId)
  {
    try
    {
      sMethodName = HUMSearchEnrollmentbuildUnknownAccountMap;
      String sQry = 'select firstName, lastname, name, Group_Number__c, Birthdate__c, PersonMailingState,';
      sQry += 'Source_Platform_Code__c, PersonHomePhone from account where Id = :iAccntId LIMIT 1';
      Account oAcc = Database.Query(sQry);      
      if(oAcc != null)
      {              
        mapRecordData = new Map<Integer,Account> {0 => oAcc};
        lstLegacyService.add(new HUMMemberSearchEnrollmentHelper(oAcc)); 
        bEnrres = true; 
        iCounter = 0;
        iPagesize = Integer.valueof(10);
      }
    }
    catch(Exception e)
    {              
        bEnrres = false;
      HUMExceptionHelper.logErrors(e,sClassName, sMethodName);
    } 
  }
  
  
  /*Method Name    :  AMSearchService     
   * Description   :  to send http request through continuation for AM Search
   * Return Type   :   Continuation   
   * Parameters    :   
   */
  
  public Continuation AMSearchService()
  {
	bEnrres = false;
	lstLegacyService = new List<HUMMemberSearchEnrollmentHelper>();
    if(!validateAMSearchFields())   return NULL; // Validate search fields wrapper for AM
    
    // Setup Request
    HUM_Webservice_Callout__c AMSearch = HUM_Webservice_Callout__c.getValues('Application_Search');
    
    Integer TIMEOUT_INT_SECS = Integer.ValueOf(AMSearch.Timeout__c);

    HttpRequest request = new HttpRequest();
    request.setMethod(AMSearch.Request_Type__c);
    request.setHeader(Label.HUMServiceTesterController_content_type,AMSearch.Content_Type__c);
    request.setTimeout(Integer.valueOf(AMSearch.Timeout__c));
    request.setEndpoint(AMSearch.End_Point_URL__c);
    if (!Test.isRunningTest()) request.setClientCertificateName(AMSearch.Certificate_Name__c);
    
    
    ApplicationSearch_Request_DTO_HUM.EnrollmentRequestClass reqFields = new ApplicationSearch_Request_DTO_HUM.EnrollmentRequestClass();
    
    reqFields.MedicareId = oSearchWrapper.sMedicareId;
    reqFields.FirstName = oSearchWrapper.sFirstName;
    reqFields.LastName =oSearchWrapper.sLastName;
    reqFields.MemberDOB = oSearchWrapper.sBirthdate;
    reqFields.ApplicationId = oSearchWrapper.sApplicationId;
    reqFields.OECConfirmationId = oSearchWrapper.sOECConfirmationId;
    reqFields.BarCode = oSearchWrapper.sBarCode;
    reqFields.MedicaidId = oSearchWrapper.sMedicaidId;
    reqFields.EffectiveDateFrom = oSearchWrapper.sEffectiveDateFrom;
    reqFields.EffectiveDateTo = oSearchWrapper.sEffectiveDateTo;
    reqFields.ReceivedDateFrom = oSearchWrapper.sReceivedDateFrom;
    reqFields.ReceivedDateTo = oSearchWrapper.sReceivedDateTo;
    reqFields.SearchType = 'M';
    
    ApplicationSearch_Request_DTO_HUM requestBody = new ApplicationSearch_Request_DTO_HUM();
    requestBody.EnrollmentRequest = reqFields;
    
    
    String sBody = JSON.serialize(requestBody);
    request.setBody(sBody);

    Continuation oConAM;
        if(AMSearch != null)
        {
            if(AMSearch.Active__c)
            {
                oConAM = new Continuation(120);
                oConAM.continuationMethod ='AMSearchServiceResponse';
                amSearchContinuationId = oConAM.addHttpRequest(request);
            }
        }
        return oConAM;      

  }
  
  /*Method Name    :  AMSearchServiceResponse     
   * Description   :  AM  Search response method
   * Return Type   :   Object   
   * Parameters    :   
   */
  
  public Object AMSearchServiceResponse()
  {
    try
    {
        HttpResponse res = Continuation.getResponse(amSearchContinuationId);
        String response = res.getBody();
        ApplicationSearch_Response_DTO_HUM responseBody = null;
        Boolean successResponse = false;
        
        if(res.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM && res.getbody() != null && res.getbody().contains('EnrollmentResponse'))
            {
                responseBody = (ApplicationSearch_Response_DTO_HUM)System.JSON.deserialize(res.getbody(), ApplicationSearch_Response_DTO_HUM.class);
                successResponse = true;
            }
            else
            {
                HUMCustomExceptionHelper h = new HUMCustomExceptionHelper('',res.getbody(),HUM_Webservice_Callout__c.getValues('Application_Search').End_Point_URL__c,'Connection Error');
                throw new HUMCustomException(h);
                successResponse = false;
            }
            
            if(successResponse)
            {
                ApplicationSearch_Response_DTO_HUM.EnrollmentResponseWrapper resultWrapper = new ApplicationSearch_Response_DTO_HUM.EnrollmentResponseWrapper();
                resultWrapper = responseBody.EnrollmentResponse;
                if(resultWrapper.EnrollmentDetail != null && !resultWrapper.EnrollmentDetail.isEmpty())
                {
                    List<ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper> lstEnrollmentDetails =  new List<ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper>();
                    lstEnrollmentDetails = resultWrapper.EnrollmentDetail;
                    Integer amResultCounter = 1;
                    for(ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetail: resultWrapper.EnrollmentDetail)
                    {
                        lstLegacyService.add(new HUMMemberSearchEnrollmentHelper(eDetail,amResultCounter));
                        amResultCounter++;
                        bEnrres = true;
                        
                    }
                    // Take entire results as JSON String
                    lstLegacyServiceJSON = JSON.serialize(lstLegacyService);
                    
                }
                else
                {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_No_Results_Found_Error,true);
                }
                
            }
    }
    catch(Exception e)
    {
        HUMExceptionHelper.logErrors(e,'HUMMemberSearchEnrollmentControllerV2','AMSearchServiceResponse');
    }
    
    return null;
  }
  
  /*Method Name    :  validateAMSearchFields     
   * Description   :  Validation method for AM Search combinations
   * Return Type   :   boolean   
   * Parameters    :   
   */
  
  public boolean validateAMSearchFields()
  {
    boolean validData = true;
    // No Valid combinations were entered
    if(String.isBlank(oSearchWrapper.sMedicareId) && String.isBlank(oSearchWrapper.sMedicaidId) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sApplicationId) && String.isBlank(oSearchWrapper.sOECConfirmationId) && String.isBlank(oSearchWrapper.sBarCode))
    {
        HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.label.AM_Additional_Fields_Error,true);
        validData = false;
    }
    else //rest other combinations
    {
        // Medicare Id
        if(String.isNotBlank(oSearchWrapper.sMedicareId) && (oSearchWrapper.sMedicareId.length() < 10 || oSearchWrapper.sMedicareId.length() > 12))
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_Search_Medicare_Error,true);
            validData = false;
        }
        // Medicaid Id
        if(String.isNotBlank(oSearchWrapper.sMedicaidId) && (oSearchWrapper.sMedicaidId.length() < 3 || oSearchWrapper.sMedicaidId.length() > 20))
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_Medicaid_Error,true);
            validData = false;
        }
        // Validation of FirstName, lastName && DOB
        if(String.isNotBlank(oSearchWrapper.sFirstName) || String.isNotBlank(oSearchWrapper.sLastName) || String.isNotBlank(oSearchWrapper.sBirthdate))
        {
            if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName) || String.isBlank(oSearchWrapper.sBirthdate))
            {
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_Name_Error,true);
                validData = false;
            }
            else
            {
                if(String.isNotBlank(oSearchWrapper.sFirstName) && !Pattern.matches(HUMAlphaNumericPattern, oSearchWrapper.sFirstName))
                {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_Search_Invalid_Name_Error,true);
                    validData = false;
                }
                else if(String.isNotBlank(oSearchWrapper.sLastName) && !Pattern.matches(HUMAlphaNumericPattern, oSearchWrapper.sLastName))
                {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_Search_Invalid_Name_Error,true);
                    validData = false;
                }
                if(String.isNotBlank(oSearchWrapper.sBirthdate) && !Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sBirthdate))
		        {
		            validData = false;
		            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumStartnEndDate,true);
		        }
            }
        }
        // Validation for Received Date fields
        if(String.isNotBlank(oSearchWrapper.sEffectiveDateFrom) || String.isNotBlank(oSearchWrapper.sEffectiveDateTo))
        {
			if(String.isBlank(oSearchWrapper.sEffectiveDateFrom) || String.isBlank(oSearchWrapper.sEffectiveDateTo))
			{
				HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_Effective_Date_Error,true);
                validData = false;
			}
			else if(!Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sEffectiveDateFrom) || !Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sEffectiveDateTo))
			{
				validData = false;
				HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumStartnEndDate,true);
			}
			else if((Date.parse(oSearchWrapper.sEffectiveDateFrom).daysBetween(Date.parse(oSearchWrapper.sEffectiveDateTo))) < 0)
			{
				validData = false;
				HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumSearchEnrollmentDateValid1,true);
			}
                
        }
        if((String.isNotBlank(oSearchWrapper.sReceivedDateFrom) || String.isNotBlank(oSearchWrapper.sReceivedDateTo)))
        {
			if(String.isBlank(oSearchWrapper.sReceivedDateFrom) || String.isBlank(oSearchWrapper.sReceivedDateTo))
			{
				HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,System.Label.AM_Received_Date_Error,true);
                validData = false;
			}
			else if(!Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sReceivedDateFrom) || !Pattern.matches(HUMBirthDatePattern, oSearchWrapper.sReceivedDateTo))
			{
				validData = false;
				HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumStartnEndDate,true);
			}
			else if(Date.parse(oSearchWrapper.sReceivedDateFrom).daysBetween(Date.parse(oSearchWrapper.sReceivedDateTo)) <0)
			{
				validData = false;
				HUMExceptionHelper.addMessageToPage(ApexPages.Severity.Warning,HumSearchEnrollmentDateValid1,true);
			}
                
        }
        
    }
    
    return validData;
  
  }

}