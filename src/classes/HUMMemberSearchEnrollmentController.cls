/*****************************************************************************************************************************************************************************
Apex Class Name : HUMMemberSearchEnrollmentController 
Version         : 1.0
Created On      : 05/23/2014
Function        : Class contains methods for search on Member in legacy system based on the parameters passed from the visual force page
                  and passes the search results back to the visualforce page as well as method to create a new case record.
Test Class      : HUMMemberSearchEnrollmentControllerTest

Modification Log: 
 * Developer Name           Code Review                  Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* mrudula jaddu              17165                     05/23/2014                 Original Version
* shruthi karanth            17165                     08/06/2014                 Code cleanup
* mrudula jaddu              17165                     08/07/2014                 Code cleanup
* shruthi karanth            17355                     08/14/2014                 Fixed defect : 149858.0001
                                                                                  Made changes as per updated wsdl for H1 exchange And AE History
* shruthi karanth            17917                     09/14/2014                 Fixed Issue with AE History search
* shruthi karanth            18890                     10/03/2014                 Fixed defect : 156720.0001 Reset button Not clearing result section
* Pranali Choudhari          18890                     10/27/2014                 Added code for CBIS service    
* Raja Sekhar Reddy M V      18988                     11/05/2014                 Added code for resetting Date of birth in CBIS Search  
* Palak Papneja              18988                     11/06/2014                 Made changes as per code review 18988 
* Pranali Choudhari          18988                     11/11/2014                 Made changes to cbis service for defect 160176  
* Tanushree Hazari           19257                     11/17/2014                 Removed lines from validationForNonCBIS() related to defect 160986                                                                                                
* Kritika P Popat            19401                     11/19/2014                 Modified code for REQ-70321 
* Pradeep Veeravali          19401                     11/19/2014                 made changes as per updated wsdl for H1 Exchange Service
* Pradeep Veeravali                                    11/25/2014                 modified to fix test calss failure as per humsearchHelper [for call transfer]
* Raja Sekhar Reddy M V      19566                     11/26/2014                 Made changes to CBIS Service for Defect  162139 & 161803
* Shreya Choodamani          19566                     11/26/2014                 Made changes to fix defect#: 162575.0001 in the UnknownMemSearch()
* Pranali Choudhari          19566                     12/01/2014                 Made changes to naming conventions as per review #19566
* Kritika Popat              19747                     12/04/2014                 Modified code for REQ-66102 
* Raja Sekhar Reddy M V      19747                     12/08/2014                 Modified code for REQ-66104  
* Pradeep Raju               19747                     12/09/2014                 Updated the class at lines# 65,225,352,365 to 381
* Raja Sekhar Reddy M V      20105                     12/26/2014                 Modified code for H1 Market place service And defect# 163821, 162139, 161803, 163829.  
* Raja Sekhar Reddy M V      20281                     01/10/2015                 Modified Code for Defect # 167586
* Ranjeeth Nagishetty        21021                     02/17/2015                 REQ - 121513:State of Florida Messaging on Search Enrollment Tab
* Raja Sekhar Reddy M V      21466                     03/04/2015                 Modified Code for Defect # 172077
* Raja Sekhar Reddy M V      21721                     03/18/2015                 Modified Code for Defect # 173665
* Ranjeeth Nagishetty        22697                     04/27/2015                 REQ-183534 for Defect# 172077
* Ranjeeth Nagishetty        23864                     06/10/2015                 REQ - 191244 :Fetching the Zipcode for OHBP service to form the External ID
* Vardhaman                                            06/07/2021                 User Story#-1914200 -T1PRJ0002736 - MF 6 - PLT - Sonar Cube remediation/Consumer Management (URL Parameters should be Escaped/Sanitized)
*****************************************************************************************************************************************************************************/

public with sharing class HUMMemberSearchEnrollmentController extends HUMSearchComponentBaseHelper
{
  private String sClassName = 'HUMMemberSearchEnrollmentController';
  private String sMethodName = '';
  private HUM_CBIS_Enabled__c oHUMCbisFlag = HUM_CBIS_Enabled__c.getValues('HUM_CBIS_Flag');
  private boolean bESAPILibSecurityfixSwitch {get;set;}
  /*
   * Method Name   :   HUMMemberSearchEnrollmentController 
   * Description   :   This Is used to set the date value for validation And to set the unknown member account id.
   * Return Type   :   NA
   * Parameters    :   NA
   */
  public HUMMemberSearchEnrollmentController()
  {
    bESAPILibSecurityfixSwitch = HUMUtilityHelper.isCRMFunctionalityON('1914200');
    oSearchWrapper.sStartDate = (Datetime.now() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'))).format('MM/dd/yyyy');
    oSearchWrapper.sEndDate = (Datetime.now()).format('MM/dd/yyyy');
    oSearchWrapper.dtMinDate = Date.today() - Integer.ValueOf(HUMUtilityHelper.getIntegerValue('SEARCH_ENROLL_DATERANGE'));
    oSearchWrapper.dtMaxDate = Date.today();
    lstLegacyService = new list<HUMMemberSearchEnrollmentHelper>();
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
  
  public boolean bCBISflag {get;set;}
  
  Public String sExchangeID {get;set;}
  
  Public String sSsNID {get;set;}
  
  public boolean bEnablebutton {get;set;}
  
  public boolean bIsWarningExist{get;set;}
  
  public boolean bIsError {get;set;}
  
   
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

  public HUMMemberSearchEnrollmentController getThis()
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
    lstOptions.add(new SelectOption(Label.HUMEESService,Label.HUMEESService)); 
    lstOptions.add(new SelectOption(Label.HUMTrackEnrollmentService,Label.HUMTrackEnrollmentService)); 
    lstOptions.add(new SelectOption(Label.HUMAEService,Label.HUMAEService));
    lstOptions.add(new SelectOption(Label.HUMH1Service,Label.HUMH1Service));
    if( oHUMCbisFlag.CBIS_Flag__c == True )
    {
     lstOptions.add(new SelectOption(Label.HUMCBISCode,Label.HUMCBISCode)); // Added for CBIS Service on 10 06 2014
    }
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
        if(!validateCBISFirstName()) bIsError = true;
        if(!validateCBISLastName()) bIsError = true;
        if(!validateCBISBirthDay()) bIsError = true;
        if(!validateCBISState()) bIsError = true;
        if(!validateBirthdate()) bIsError = true;
        if(bIsError)
        { 
            bIsEnrollmentError = true;
            bIsEnrollmentErrors = False;
            return false;
        }
        else if(!bIsError)
        { 
            bIsEnrollmentErrors = true;
            bIsEnrollmentError = False;
            return false;
        }
        
        return true;
    }
 

  /*
   * Method Name   :   CbisService()
   * Description   :    This Method is for getting value for CBIS Radio Button.
   * Return Type   :    NA
   * Parameters    :    NA
   */
  public void CbisService()
  {    
    bIsCbisSelected = sServices.equals(System.Label.HUMCBISCode); 
    if(this.sServices == 'CBIS')
    { 
    reset();  
    }Else{}
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
        throw new HUMCustomException(System.Label.HUMSearchEnrollmentSelectServices); 
      } 
      else if(sServices.equals(System.Label.HUMCBISCode))
      {  
        bIsCbisSelected = true;                            
        CBISSearch();
      }
      
  }

  /*
   * Method Name   :   callSearchServices()
   * Description   :   This Method is for validating the inputs and calling the non-CBIS service.
   * Return Type   :   void
   * Parameters    :   NA
   */
  public void callSearchServices()
  {
    if(String.isBlank(sServices))
    {
      bEnrres = false;
      throw new HUMCustomException(System.Label.HUMSearchEnrollmentSelectServices); 
    } 
    else if(sServices.equals(System.Label.HUMEESService))
    {            
      mapMemInfo = new map<String,map<String,String>>();     
      oEesLegacy = new HUMEESLegacyMemberSearchHelper();
      mapMemInfo = oEesLegacy.submit(oSearchWrapper.sSSN); 

      if(NULL != mapMemInfo && !mapMemInfo.isEmpty())
      {
        humLegMemberObj = new HUMMemberSearchEnrollmentHelper(mapMemInfo,null);
        lstLegacyService.add(humLegMemberObj );

        for(Integer count = 0 ; count < Integer.valueOf(system.Label.ListSizePageMemSearch) ; count++)
        {
          if(mapMemInfo.containsKey(system.label.HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage +count))
            lstLegacyService.add(new HUMMemberSearchEnrollmentHelper(mapMemInfo, count));   
        }
        bEnrres = true;                                     
      } 
    }             
    else if(sServices.equals(System.Label.HUMH1Service))
    {
      H1ExchangeSearch();
      if(NULL == lstLegacyService || (NULL != lstLegacyService && lstLegacyService.isEmpty()) ) ohbpSearch();
      if(NULL != lstLegacyService && !lstLegacyService.isEmpty())    bEnrres=true;
    }                 
    else if(sServices.equals(System.Label.HUMTrackEnrollmentService))
    {
      TrackEnrollmentSearch();    
    }
    else if(sServices.equals(System.Label.HUMAEService))
    {                              
      AEHistorySearch();  
    } 
    // Added for CBIS Service on 10 06 2014
    else if(sServices.equals(System.Label.HUMCBISCode))
    {  
      bIsCbisSelected = true;                            
      CBISSearch();  
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
        throw new HUMCustomException(System.Label.HUMSearchEnrollmentDate);  
      }
  
      else  if(!Pattern.matches(System.Label.HUMDatePattern  , oSearchWrapper.sStartDate))
      {
          bEnrres = false;
          throw new HUMCustomException(system.Label.HumStartnEndDate);
      }
      else  if(!Pattern.matches(System.Label.HUMDatePattern  , oSearchWrapper.sEndDate))
      {
          bEnrres = false;
          throw new HUMCustomException(system.Label.HumStartnEndDate);
      }          
      else if((Date.parse(oSearchWrapper.sStartDate).daysBetween(Date.parse(oSearchWrapper.sEndDate))) < 0)
      {
        bEnrres = false;
        throw new HUMCustomException(System.Label.HumSearchEnrollmentDateValid1); 
      }     
      else
      {
        callSearchServices();                    
      }
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
        throw new HUMCustomException(System.Label.HUMSearchEnrollmentSsnservice); 
      }
      else if(String.isBlank(sServices))
      {
        bEnrres = false;
        throw new HUMCustomException(System.Label.HUMSearchEnrollmentSelectServices); 
      }
      // Added for CBIS Service on 10 06 2014
      else if(String.isBlank(oSearchWrapper.sSSN) && (bIsCbisSelected == null || bIsCbisSelected == false))
      {
        bEnrres = false;
        throw new HUMCustomException(System.Label.HumSearchenrollmentSsnReq); 
        return null;       
      } 
      // Added for CBIS Service on 10 06 2014
      else if(String.isBlank(oSearchWrapper.sSSN) && bIsCbisSelected == True)
      {
        bEnablebutton=true;
        validationForCBIS();    
      }
      else
      {
        bEnablebutton=false;
        validationForNonCBIS();
      }
        
      if(!lstLogErrors.isEmpty()) HUMExceptionHelper.saveLog(lstLogErrors,true);

      if(NULL == humLegMemberObj && bIsCbisSelected == True && bIsEnrollmentError == False)
      {
        bEnrres = false;                    
        throw new HUMCustomException(System.Label.HumSearchEnrollmentNoRes);
      }
      else if(NULL == humLegMemberObj && bIsCbisSelected == True && bIsEnrollmentErrors == True)
      {
        bEnrres = false;                    
        throw new HUMCustomException(System.Label.HumSearchEnrollmentNoRes);
      }
     
       else if(NULL == humLegMemberObj && bIsCbisSelected == True && bIsEnrollmentErrors == True)
      {
        bEnrres = false;                    
        throw new HUMCustomException(System.Label.HumSearchEnrollmentNoRes);
      }
      else if(NULL == humLegMemberObj && bIsCbisSelected == False)
      {
        bEnrres = false;                    
        throw new HUMCustomException(System.Label.HumSearchEnrollmentNoRes);
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
   * Method Name   :    ohbpSearch() 
   * Description   :    This Method is for calling ohbp system to search for a member not found in salesforce.
   * Return Type   :    void
   * Parameters    :    NA
   */ 
  private void ohbpSearch() 
  {
    try 
    { 
      sMethodName = System.label.HUMSearchEnrollmentohbpSearch;  
      HUMOHBPService.soap ohbpService = (HUMOHBPService.soap)HUMSearchHelper.getServiceRequestInstance(system.label.HUMOHBPSvc,
          system.label.HUMOHBPReq);
      String sXml = system.label.HUMOHBPMemberSearchReq_Header;
      sXml += osearchWrapper.sSSN+system.label.HUMOHBPMemberSearchReq_End; 

      if(ohbpService != null)
      {
        String sOHBPResponse = ohbpService.getMemberInformationXml(sXml);       
        //Get the Member Nodes  
        List<String> lstGetMemberNodes = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse,system.label.HUMOHBPMember);
        
        //Parse Each Member for Account information
        for(String sMember : lstGetMemberNodes)
        {
          Account oAcc = new Account();    
          //Get Name
          List<String> lstFirstName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse,system.label.HUMOHBPSearchFirstName);               
          Map<String,String> mapAccount = new Map<String,String>();

          if(NULL != lstFirstName && !lstFirstName.isEmpty()) mapAccount.put(system.label.HUMOHBPSearchFstName, lstFirstName[0]);        
          //Get LastName
          List<String> lstLastName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse,system.label.HUMOHBPSearchLastName); 
          if(NULL != lstLastName && !lstLastName.isEmpty()) mapAccount.put(system.label.HUMOHBPSearchLstName, lstLastName[0]);

          //Get DateOfBirth             
          List<String> lstDateOfBirth = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse,system.label.HUMOHBPSearchBirthDate);
          if(NULL != lstDateOfBirth && !lstDateOfBirth.isEmpty()) mapAccount.put(system.label.HUMOHBPSearchBirthDt, lstDateOfBirth[0]); 

          // Group ID 
          List<String> lstPlanID = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse,system.label.HUMOHBPSearchPlanID);
          if(NULL != lstPlanID && !lstPlanID.isEmpty())  mapAccount.put(system.label.HUMOHBPSearchIDPlan, lstPlanID[0]);

          //ExternalID
          List<String> lstExternalID = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse,system.label.HUMOHBPSearchTranID);
          if(NULL != lstExternalID && !lstExternalID.isEmpty())    mapAccount.put(system.label.HUMOHBPSearchExtID, lstExternalID[0]);

          //Product
          List<String> lstProductName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse, system.label.HUMOHBPSearchPlanName);
          if(NULL != lstProductName && !lstProductName.isEmpty())   mapAccount.put(system.label.HUMOHBPSearchProd, lstProductName[0]);

          List<String> lstEffectiveDate = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse, system.label.HUMOHBPSearchEffDate);
          if(NULL != lstEffectiveDate  &&  !lstEffectiveDate.isEmpty())   mapAccount.put(system.label.HUMOHBPSearchEfectDate, lstEffectiveDate[0]);
           
           //Zip
          list<string> lstzip = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(sOHBPResponse, 'Zip');
          if(NULL != lstzip  &&  !lstzip.isEmpty())   mapAccount.put('PersonMailingPostalCode', lstzip [0]);
          
          humLegMemberObj = new HUMMemberSearchEnrollmentHelper(mapAccount);
          lstLegacyService.add(humLegMemberObj );                
        }           
      }                
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
   * Method Name   :    H1ExchangeSearch()
   * Description   :    This Method is for calling H1 Exchange system to search for a member not found in salesforce.
   * Return Type   :    void
   * Parameters    :    NA
   */   
  public void H1ExchangeSearch()
  { 
    try
    {
      sMethodName = System.Label.HUMSearchEnrollmentH1Exchange;
      HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues('H1ExchangeService');            
      HumH1ExchangeRequestHelper.EnrollmentDetailRequest  oReqMessage = new HumH1ExchangeRequestHelper.EnrollmentDetailRequest ();
      oReqMessage.mbrSsn = osearchWrapper.sSSN;
      oReqMessage.mbrDob = '';
      oReqMessage.effDateFr = '';
      oReqMessage.effDateTo = '';
      oReqMessage.recptDateFr = '';
      oReqMessage.recptDateTo = '';
      oReqMessage.zipCode = '';
       
      HumH1ExchangeRequestHelper.EnrollmentDetailReq oReq = new HumH1ExchangeRequestHelper.EnrollmentDetailReq();
      oReq.searchType = system.label.HUMH1ExchRequestType;
      
      HumH1ExchangeResponseHelper.EnrollmentDetail oMember = null;
      HumH1ExchangeService.AM65AM65Port oH1ExchService = new HumH1ExchangeService.AM65AM65Port(oSvcHeaders.End_Point_URL__c, oSvcHeaders.Certificate_Name__c, Integer.valueOf(oSvcHeaders.Timeout__c)); 
                     
      HumH1ExchangeResponseHelper.EnrollmentDetailResponse oResponseMsg =  null;
      oResponseMsg = oH1ExchService.AM65AM65Operation(oReqMessage,oReq.searchType); 
      
      if(oResponseMsg != null && oResponseMsg.EnrollmentDetailList != null && oResponseMsg.EnrollmentDetailList.EnrollmentDetail != null)
      {
        for(Integer iIndex = 0; iIndex < oResponseMsg.EnrollmentDetailList.EnrollmentDetail.size(); iIndex++)
        {    
         if(oResponseMsg.EnrollmentDetailList.EnrollmentDetail[iIndex].strSsn == oSearchWrapper.sSSN)
          {
            oMember = oResponseMsg.EnrollmentDetailList.EnrollmentDetail[iIndex];            
            humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oMember);             
            lstLegacyService.add(humLegMemberObj);
            bEnrres = true;
            sSSNID = oReqMessage.mbrSsn;
          }
        }
      }
    }
    catch(Exception e)
    {
      HUMExceptionHelper.bShowErrorOnPage = false;
      HUMExceptionHelper.bRestrictLog =true;
      lstLogErrors= HUMExceptionHelper.logErrors(e,sClassName, sMethodName);
      HUMExceptionHelper.bShowErrorOnPage = true;
    }
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
      H1ExchangeSearch();
  }  

  /*
   * Method Name   :    TrackEnrollmentSearch()
   * Description   :    This Method is for calling Track Enrollment to search for a member not found in salesforce.                        
   * Return Type   :    NA
   * Parameters    :    NA
   */
  private void TrackEnrollmentSearch()
  { 
    try
    {
      sMethodName = System.Label.HUMSearchEnrollmentTrackEnrollment;
      HUM_Webservice_Callout__c oSvcHeaders = HUM_Webservice_Callout__c.getValues('TrackEnrollmentService');
      HUMIntTrackEnrollmentService.BasicHttpBinding_ITwoWayAsync oTrackEnrollmentService = (HUMIntTrackEnrollmentService.BasicHttpBinding_ITwoWayAsync)(new HUMIntTrackEnrollmentService.BasicHttpBinding_ITwoWayAsync(oSvcHeaders.End_Point_URL__c,oSvcHeaders.Certificate_Name__c,Integer.valueOf(oSvcHeaders.Timeout__c)));   
      HUMIntTrackEnrollmentResponseHelper.ResponseComplexType oResponseMsg = null;
      Datetime todayDate = DateTime.now();
      if(oTrackEnrollmentService <> null)  oResponseMsg = oTrackEnrollmentService.SearchTrack(System.now().format('yyyy-MM-dd\'T\'hh:mm:ss'),oSearchWrapper.sSSN, Date.parse(oSearchWrapper.sStartDate), Date.parse(oSearchWrapper.sEndDate), 'Y', '', '', null, '');

      if(oResponseMsg != null && oResponseMsg.TrackList != null && oResponseMsg.TrackList.Track != null)
      {
        for(HUMIntTrackEnrollmentResponseHelper.Track oTrack : oResponseMsg.TrackList.track)
        {
          humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oTrack);
          bEnrres = true;
          lstLegacyService.add(humLegMemberObj ); 
        }

      }
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
  private void AEHistorySearch() 
  { 
    try
    {           
      sMethodName = System.Label.HUMSearchEnrollmentAEHistory;
      HUMIntAEHistoryService.BasicHttpBinding_ITwoWayAsync oAEHistoryService = (HUMIntAEHistoryService.BasicHttpBinding_ITwoWayAsync)HUMSearchHelper.getServiceRequestInstance(system.label.HUMAEHistSvc,
          system.label.HUMAEHisReq);
      HUMIntAEHistoryResponseHelper.ResponseComplexType oResponseMsg = null;
      HUMIntAEHistoryResponseHelper.PendHistoryList oPendHistoryList = new HUMIntAEHistoryResponseHelper.PendHistoryList();
      HUMIntAEHistoryResponseHelper.PendHistory oPendHistory = null;

      if(oAEHistoryService <> null)
      { 
        oResponseMsg = oAEHistoryService.GetPendHistoryDetails(system.label.HUMAEHistReqHeader, oSearchWrapper.sSSN, '', '', Date.parse(oSearchWrapper.sStartDate), Date.parse(oSearchWrapper.sEndDate));
      }
      
      if(oResponseMsg != null && oResponseMsg.PendHistoryList != null)
      { 
        oPendHistoryList  = oResponseMsg.PendHistoryList;
        if(oPendHistoryList.PendHistory != null)
        {
          for(Integer iIndex = 0; iIndex < oPendHistoryList.PendHistory.size(); iIndex++)
          {   
            if(oPendHistoryList.PendHistory[iIndex].Member_SSN == oSearchWrapper.sSSN || oPendHistoryList.PendHistory[iIndex].SSN == oSearchWrapper.sSSN)
            {
              oPendHistory = new HUMIntAEHistoryResponseHelper.PendHistory();
              oPendHistory = oPendHistoryList.PendHistory[iIndex];
              break;
            }
          }
          if(oPendHistory != null)
          { 
            humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oPendHistory);
            lstLegacyService.add(humLegMemberObj );
            bEnrres = true;
          }
        }   
      }
    }   
    catch(Exception e)
    {
      HUMExceptionHelper.bShowErrorOnPage = false;
      HUMExceptionHelper.bRestrictLog = true;
      lstLogErrors= HUMExceptionHelper.logErrors(e,sClassName, sMethodName);
      HUMExceptionHelper.bShowErrorOnPage = true;
    }
  }

  /*
   * Method Name   :    CBISSearch()
   * Description   :    This Method is for calling CBIS Service to search for a member not found in salesforce.
   * Return Type   :    void
   * Parameters    :    NA
   */
  private void CBISSearch() 
  {           
    try
    {   
      bIsWarningExist=false;
      sMethodName = System.Label.HUMSearchEnrollmentCBISSearch;         
      HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails oCbisService = (HUMIntCBISService.EndPoint_GetPendHistoryAndTermsDetails)HUMSearchHelper.getServiceRequestInstance(
          'HUMCbisSearchEnrollment','HUMIntCBISService.EndPoint_GetAEHistoryServices');
      HUMIntCBISServiceHelper.PendHistoryandTermsRequestDTO oPendHistoryRequestDTO = new HUMIntCBISServiceHelper.PendHistoryandTermsRequestDTO ();
      oPendHistoryRequestDTO.FirstName= oSearchWrapper.sFirstName;
      oPendHistoryRequestDTO.LastName= oSearchWrapper.sLastName;
      oPendHistoryRequestDTO.DOB= date.parse(oSearchWrapper.sBirthdate);                         
      oPendHistoryRequestDTO.stateCode= oSearchWrapper.sState;            
      HUMIntCBISServiceHelper.PendHistoryandTermsResponseDTO oResponse = null;
      List<HUMIntCBISServiceHelper.ResponseItem> oResponseItemList = new List<HUMIntCBISServiceHelper.ResponseItem>();
      HUMIntCBISServiceHelper.ResponseItem oResponseItem = new HUMIntCBISServiceHelper.ResponseItem();            
      if(oCbisService <> null) oResponse = oCbisService.GetPendHistoryTermsDetails(oPendHistoryRequestDTO);

      if(oResponse <> null &&  oResponse.ResponseItems <> null)
      {
        oResponseItemList = oResponse.ResponseItems.ResponseItem;            
        if(oResponseItemList != null)
        {
          for(Integer iInt = 0; iInt < oResponseItemList.size(); iInt++)
          {
            oResponseItem = oResponseItemList[iInt];
            humLegMemberObj = new HUMMemberSearchEnrollmentHelper(oResponseItem);
            
            if (!bIsWarningExist) 
            {
                bIsWarningExist=humLegMemberObj.bdisplayInteractbutton ;
            }
            
            lstLegacyService.add(humLegMemberObj );    
          }
          
        }             
      
        bEnrres = true;
        if (bIsWarningExist) throw new HUMCustomException(system.label.HUMCBISRestrictedError);
  
      }         
    }
    catch(Exception e)
    {            
      if(e.getmessage() == system.label.HUMCBISRestrictedError)
      {
      HUMExceptionHelper.bShowErrorOnPage = true;
      }
      else { HUMExceptionHelper.bShowErrorOnPage = false; }
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
    if(lstLegacyService != null ) lstLegacyService.clear(); 
    bEnrres = false;
    /* Added to clear all data. On 10 06 2014. */
    oSearchWrapper.sFirstName= null ;
    oSearchWrapper.sLastName= null ;
    oSearchWrapper.sState= null ;
    oSearchWrapper.sBirthdate = null;
    if(this.sServices == 'CBIS')
    {
        this.sServices = 'CBIS';
    }Else{
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
      sMethodName = System.Label.HUMSearchEnrollmentbuildUnknownAccountMap;
      String sQry = 'select firstName, lastname, name, Group_Number__c, Birthdate__c, PersonMailingState,';
      sQry += 'Source_Platform_Code__c, PersonHomePhone from account where Id = :iAccntId LIMIT 1';
      Account oAcc = Database.Query(sQry);      
      if(oAcc != null)
      {              
        mapRecordData = new Map<Integer,Account> {0 => oAcc};
        lstLegacyService.add(new HUMMemberSearchEnrollmentHelper(oAcc)); 
        bEnrres = true; 
        iCounter = 0;
        iPagesize = Integer.valueof(Label.ListSizePageMemSearch);
      }
    }
    catch(Exception e)
    {              
      HUMExceptionHelper.logErrors(e,sClassName, sMethodName);
    } 
  }
}