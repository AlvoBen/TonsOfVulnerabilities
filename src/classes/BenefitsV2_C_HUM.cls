/*******************************************************************************************************************************
Apex Class Name : BenefitsV2_C_HUM.cls
Version         : 1.0
Created On      : 04/11/2018
Function        : Class contains methods for services to fetch benefits information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Pradeepkumar Dani         30259                      03/16/2016                 Original Version
* Pradeepkumar Dani         30467                      03/25/2016                 Changes for Cost Share Reduction Table & MBE Service changes
* Vishal Verma              30727                      03/30/2016                 Defect: 221163.0001 - Added code to handle exception
* Pradeepkumar Dani         24929                      04/13/2016                 Added boolean variable for CAS Policy
* Avinash Choubey           31077                      04/18/2016                 Design Change in All Accums Req ( 237142, 246888 etc)
* Rajesh Keswani            25869                      04/22/2016                 MBE DTO creation
* Avinash Kumar Choubey     31092                      04/12/2016                 Added boolean variable bHasCRMS520Per for  for REQ 241731
* Rajesh Keswani            26826                      04/26/2016                 Created variable for PBE DTO REQ
* Rajesh Keswani            26826                      04/26/2016                 Created variable for GBE DTO REQ
* Vishal Verma              26826                      04/29/2016                 REQ 184827 : Method to call PBE Servie from Benefits Search
* Pradeepkumar Dani         27342                      05/12/2016                 Modifed for defect fix#225347
* Shreya Choodamani         27342                      05/09/2016                 REQ 262414 : Added variable For rendering the Mentor Link On Benefit Search Component
* Avinash Kumar Choubey     27342                      05/09/2016                 Added String variable sOneClickURl and sReimbursementAppURL  for  for REQ 252624
* Rajesh Keswani            27613                      05/17/2016                 Policy Rider changes.
* Rajesh Keswani            27613                      05/19/2016                 Added JS variable for platform
* Rajesh Keswani            30160                      06/09/2016                 REQ: 224050 - Implemented Benefit Search logic
* Vishal Verma              30160                      06/15/2016                 Defect 229504  - Modified Sdate
* Rajesh Keswani            31811                      06/29/2016                 Defect fix : 230939
* Pradeepkumar Dani         31811                      07/04/2016                 Added changes for standard values for logging - Defect#230623.0001
* Avinash Choubey           32583                      07/12/2016                 Unused Method removed REQ - 269975
* Apoorv Jain                                          08/25/2016                 REQ - 269936 : VOB - Mentor Link for All segments
* Rajesh Keswani            38022                      09/01/2016                 Implemented Tooth History REQ - 280369, REQ-270375, REQ -270375.
* Rajesh Keswani            50216                      12/02/2016                 Defect Fix 251352.0001.
* Praveen Kumar Parimi      56654                      12/07/2016                 Implementing the changes as part of Tiering Project and creating a new version.
* Rajesh Keswani            109089                     07/24/2017                 REQ - 299431 Making Place of Service drop down dynamic
* Avinash Choubey           109914                     07/24/2017                 REQ - 300751 Making Par Code” drop down dynamic(for CAS Members) and Sorted
* Santhi Mandava            85904                      04/20/2017                 Implemented requirement 300121 - Merging rolledback code
* Muralidhar Kollu          102572                     15/06/2017                 REQ - 304936 VOB – Accumulator section of the Benefits and Accumulator page (MF5)
* Vijaya Koriprolu          138809                     10/07/2017                 REQ-304935 : VOB Prefill the Reimbursement tool with Provider Contract Number
* Melkisan Selvaraj         220187                     04/11/2018                 Cloned from BenefitsV1_VF_HUM for REQ - 360140
* Vishal Verma		        229766       	           05/16/2018                 REQ - 355782 : Add Dental Paid Thru Date to Benefits and Accums Summary section
* Rajesh Keswani   			229751					   05/15/2018				  REQ - 361224; Adding ADA code in Benefits search section
* Melkisan Selvaraj			271010					   08/06/2018				  REQ - 362101 To auto populate benefits grid
* Melkisan Selvaraj			289968			           09/24/2018				  REQ - 376382 Modify rules for CAS benefit grids to auto-populate
* Avinash Kumar Choubey     	                       11/1/2018                 REQ 378715 changes
* Prudhvi Pamarthi                                     02/18/2019                 REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Dinesh Subramaniyan				       			   04/18/2018		  		  REQ - 396416 - Modify logic for CAS benefit grids to auto-populate
* Sumanth Naredla                                      04/13/2019                 REQ - 396258 - User Story - Add Jump Links to Benefits Page (GBORF) (ID# 55)
* Yogesh Gupta                                      	11/25/2019                 REQ - 410405 - Relocate Tooth History - Dental
* Deepankar Binjola         62335                      11/11/2019                 REQ-410411 Relocate Reimbursement Limit - Dental
* Shailesh Mali										   01/20/2020				  REQ - 791947 Accumulator SOAP to REST Conversion
* Subhamay Mandal                                      05/06/2020                 REQ-1083096 Increase the height of link panel on Benefits and Accums page (RF)
* Sayali Nemade										   05/17/2020				  REQ - 737779 Dental Benefits Snapshot Description
* Amar Gadewar                                 		   06/01/2020                 User Story 1163048: PR00091574 - MF 1 - AHT - Increase the height of link panel on Benefits and Accums page (CRM)
* Suresh Kokkeragadda                          		   06/25/2020                 User Story 1203671: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 07/07 and 07/17 releases (CRM)
* Sayali Gavane											07/14/2020				  REQ_1206896-CA ticket - 9261131 (Sections in benefit page are not displaying)	
* Sayali Gavane									 	   11/25/2020				 REQ - 1457838 - PR00095581 - MF 53 - Out of Area Indicator for CRM
* Riya Agarwal                                         01/13/2021          		  REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid
* Sayali Gavane                                    	   01/19/2021                 REQ_1450427-CA ticket - 9261131 (Sections in benefit page are not displaying) 
* Ranadheer Goud                                  	   02/01/2021                 REQ - 1629104 PR00094920- MF 2  - Rx Benefits- Benefits and Accumulators page- remove the 'Pharmacy Mail Order Benefit Snapshot' section (RxXP/CRMS) (ID# 5)
* Prasanthi Kandula                                    03/25/2021                 US:1437221 Displaying Paid Thru Date for DEN and MED Benefit Summary 
* Suraj Patil                              			   05/31/2021			      USER STORY 1867803 T1PRJ0003278/PR00095917 - MF01 - Tooth History Data Fields    
* Nirmal Garg										09/16/2021					 Added browser logic for coveo
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
* Nirmal Garg										  11/26/2021				Defect 4215 - removed dependency from global constant and hum constant
*********************************************************************************************************************************/
public with sharing class BenefitsV2_C_HUM extends SObjectDescriptorImpl_H_HUM
{
    private BenefitsV2_S_HUM oServiceBenefits = new BenefitsV2_S_HUM();
    private BenefitsV2_D_HUM oDataBenefits = new BenefitsV2_D_HUM();
    public String sRequestLabelMBE ='';
    public String sRequestLabelGBE = '';
    public String sRequestLabelPBE = '';
    public Map<string, List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>> mapServiceCatServicetype = null;
    public Map<String, Map<String, Object>> standardMapForLogging = new Map<String, Map<String, Object>>();
	Public Static final String SORTDATE_HUM ='CreatedDate';
    public String sSortExp = SORTDATE_HUM;
	 Public Static final String Sort_Expression_DESC_HUM = 'DESC';
    public String sSortDirection = Sort_Expression_DESC_HUM;
    public static boolean IsAutoLaunchMentor{ get;set; }
    public static boolean bIsCasPolicy{ get;set; }
    public static boolean bKMD{ get; set;}
    public list<string> lstFloatLinkelem{get;set;}
    public String sKMDocRequest = '';
    @testVisible private List<Case> lstFinalCases = null;
    public boolean sRestSwitchCall{get;set;}
    public boolean bToothHistory{get;set;}
	public String columnName {get; private set;}
	public String columnHeader{get; private set;}
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';
	Public Static final Integer Constant_100_HUM = 100;
	Public Static final String Sort_Expression_ASC_HUM = 'ASC';
	Public Static final String VOB_DATE_ERROR_HUM = 'Please utilize calendar for Date of Service selection';
	 Public Static final Integer PageSize_HUM = 5;
	 Public Static final Set<String> RestrictedCASProdType = New Set<String>{'MCD','MGP','MGR','MES','MPD','MRP'};
	 public static final string KMDOCUMENT = 'KMDocument';
	 Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
	Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
	Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    /**
    * invokes service layer for constructing the variables.
    * <p>
    * Constructor Method to for the class , Initialize variables
    */
    public BenefitsV2_C_HUM()
    {
		oServiceBenefits.init(this);
		bIsLinkRendered = false;
        bExistingCaseScenario = false;
        bShowPanel = false;
		bToothHistory = CRMFunctionalityenabler_H_HUM.Checkflagvalue('Tooth_History');
		createFloatingList();
        bKMD = false;
        sPlatformValue = oDataBenefits.getPlatformValueForPolicyRider(objPolMem);
        CoverageApp_Endpoint_HUM__c oCoverageAppURL = CoverageApp_Endpoint_HUM__c.getInstance('HumanaCoverageAppLink');
        if (oCoverageAppURL != null && string.isNotBlank(oCoverageAppURL.CoverageApp_Endpoint_URL__c))
        {
            sCoverageAppURL = oCoverageAppURL.CoverageApp_Endpoint_URL__c;
        }
        IsAutoLaunchMentor = false;
        bdisableButton = false;
        if(objPolMem != null)bdisableButton = objPolMem.ETL_Record_Deleted__c;
        MentorGridLaunch();
		
		sRestSwitchCall = HUMUtilityHelper.isCRMFunctionalityON('791947');
		
		//REQ - 737779 Switch for Benefits Snapshot description
        bSwitchBenefitSnapshot = HUMUtilityHelper.isCRMFunctionalityON('737779');  
		//REQ_1206896- Switch for CA ticket - 9261131 
		bSwitchFaultSection = HUMUtilityHelper.isCRMFunctionalityON('1206896');	
        //REQ 1450427-Switch for CA Tickets- SF - TECH Incident INC0214040 - CRM Service - Benefit Snapshot - "Section information did not load"
		bSwitchErrorMsg = HUMUtilityHelper.isCRMFunctionalityON('1450427');
        // US:1437221 Updated Paid Thru Date logic on benefits summary
        bDisplayPTDOnDental = HUMUtilityHelper.isCRMFunctionalityON('1437221_Den');        
		columnName = json.serialize(new List<String>{'sDateOfService','sTooth','sSurfaceCode','sADACode','sDescription','sClaimNumber','sStatus'});
        columnHeader = json.serialize(new List<String>{'Date of Service','Tooth','Surface Code','ADA Code','Description','Claim Number','Status'}); 
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    }

	public Boolean bdisableButton {get; set;}
    public string sCaseID{ get; set; }
	public string sTHCaseNumber{ get; set; }
	public string sRedirectURL{ get; set; }
	public id  objID{ get; set; }
	public string sPlatformValue{ get; set; }
	public string sCoverageAppURL{ get; set; }
	public boolean bIsLinkRendered{ get; set; }
	public boolean isLinkRendered{ get; set; }
	public string sPolMemId{ get; set; }
	public boolean bIsDental{ get; set; }
	public boolean bIsMedical{ get; set; }
	public boolean bIsVASPolicy{ get; set; }
	public Policy_Member__c objPolMem{ get; set; }
	public Policy_Plan_Details__c objPolPlan{ get; set; }
	public String sMBEServiceResponse{ get; set; }
	public String sGBEServiceResponse{ get; set; }
	public String sPBEServiceResponse{ get; set; }
	public transient PBEServiceTypeV2DTO_HUM ServiceTypeDTO{ get; set; }
	public transient string ServiceTypeDTOStr{ get; set; }
	public Map<string, string> mapServiceType{ get; set; }
	public List<string> listServiceType{ get; set; }
	public String selectedCategory{ get; set; }
	public Boolean PBEDTOResponseFlag{ get; set; }
	public String vPBEServiceRes{ get; set; }
	public Boolean MBEDTOResponseFlag{ get; set; }
	public String vMBEServiceRes{ get; set; }
	public Boolean GBEDTOResponseFlag{ get; set; }
	public string vGBEServiceRes{ get; set; }
	public boolean bRenderErrorMsg{ get; set; }
	public Event oEventObj{ get; set; }
	public string sPolEndDate{ get; set; }
	public Boolean bSerDateGreater{ get; set; }
	public boolean benableCase{ get; set; }
	public Boolean bHasCRMS520Per{ get; set; }
	public string sSFDCBaseURL{ get; set; }
		// req 252624 changes start
	public string sOneClickURL{ get; set; }
	public String sCaseNumber{ get; set; }
	public string sReimbursementAppURL{ get; set; }
	public Boolean bShowPanel{ get; set; }
		// req 252624 changes end
	public String standardMapvalue{ get; set; }{standardMapvalue = ''; }
	public String sAttachmentKey{ get; set; }
	public string sAlternateReimburseId{ get; set; }
	public string sCode{ get; set; }
	public string sAsoInd{ get; set; }
	public string sDateOfService{ get; set; }
	//REQ 737779
    public Boolean bSwitchBenefitSnapshot{get;set;}
	//REQ 1206896
    public Boolean bSwitchFaultSection {get;set;}  	
	public boolean bIsDenGroup{ get; set; }
	public string sOOAGroup{ get; set; }
	//REQ 1450427  	
	public String MBEFaultString {get;set;}
    public String PBEFaultString {get;set;}
	public Boolean bSwitchErrorMsg {get;set;}
    public Boolean bDisplayPTDOnDental {get;set;}
		//List to store Constructor Exception Logs 
		public List<Error_Log__c> lstLogErrors
	{
		get
	{
		if (lstLogErrors == null) return new List<Error_Log__C>();
	return lstLogErrors;
	}
	set;
	}

		public string sDate
	{
		get
	{
		if (ApexPages.currentpage().getParameters().containsKey('refdate') && ApexPages.currentpage().getParameters().get('refdate') != '')
		{
			string s = ApexPages.currentpage().getParameters().get('refdate');
			return s.substring(0, 2) + '/' + s.substring(2, 4) + '/' + s.substring(4, 8);
		}
		else
		{
			DateTime d = datetime.now();
			return d.format('MM/dd/yyyy');
		}
	return null;
	}
	set;
	}

		// PBE service DTO changes
		public Object DTOObj
	{
		get
	{
		return ServiceTypeDTO;
	}
		set
	{
		ServiceTypeDTO = (PBEServiceTypeV2DTO_HUM)value;
	}
	}

	public String sSelectedTHRadio{ get; set; }
	public boolean bExistingCaseScenario{ get; set; }
	public boolean bDisableToothNavBtn{ get; set; }
		//tooth history
		public List<Case> lstCases
	{
		get
	{
		if (stdsetcon != null)
		{

			return (List < Case >) stdsetcon.getRecords();
		}
		else
		{
			return null;
		}
	}
	set;
	}

		/*
		* Method Name : iConstantValue
		* Description : This property is used to get/set iConstantValue.
		* Return type : integer
		* Parameter   : NA
		*/
		public integer iConstantValue
	{
		get
	{
		iConstantValue = Constant_100_HUM;
	return iConstantValue;
	}
	set;
	}

		/*
		* Method Name : hasNext
		* Description : This property is used to get/set hasNext .
		* Return type : Boolean
		* Parameter   : NA
		*/
		public Boolean hasNext
	{
		get
	{
		if (stdsetcon != null)
		{
			return stdsetcon.getHasNext();
		}
		else
		{
			return null;
		}
	}
	set;
	}

		/*
		* Method Name : hasPrevious
		* Description : This property is used to get/set hasPrevious.
		* Return type : Boolean
		* Parameter   : NA
		*/
		public Boolean hasPrevious
	{
		get
	{
		if (stdsetcon != null)
		{
			return stdsetcon.getHasPrevious();
		}
		else
		{
			return null;
		}
	}
	set;
	}

		/*
		* Method Name : sSortExpression
		* Description : This property is used to get/set sSortExpression.
		* Return type : String
		* Parameter   : NA
		*/
		public String sSortExpression
	{
		get
	{
		return sSortExp;
	}
		set
	{
		if (value == sSortExp)
		{
			sSortDirection = (sSortDirection == Sort_Expression_DESC_HUM) ? Sort_Expression_ASC_HUM : Sort_Expression_DESC_HUM;
		}
		else
		{
			sSortDirection = Sort_Expression_DESC_HUM;
		}
	sSortExp = value;
	}
	}

		/*
		* Method Name : pageNumber
		* Description : This property is used to get/set pageNumber.
		* Return type : Integer
		* Parameter   : NA
		*/
		public Integer pageNumber
	{
		get
	{
		if (stdsetcon != null)
		{
			return stdsetcon.getPageNumber();
		}
		else
		{
			return null;
		}
	}
	set;
	}

	public ApexPages.StandardSetController stdsetcon{ get; set; }
	public String sSelectedCaseId{ get; set; }

		/**
		* Returns this instance of the class
		* <p>
		* Method to store the Current Page Instance for Side Panel
		*
		* @return this
		*/
		public BenefitsV2_C_HUM getThis()
	{
		return this;
	}

	/**
	* Returns List<sObject> of record
	* <p>
	* Virtual required Method
	*
	* @return NULL
	*/
	public List<sObject> getRecords()
	{
		return NULL;
	}

	/**
	* Returns sObject of record
	* <p>
	* Virtual required Method
	*
	* @return NULL
	*/
	public sObject getRecord()
	{
		return NULL;
	}

	/**
	* Returns boolean value based of Humana one
	* <p>
	* Method to get Humana One value
	*
	* @param
	* @return  boolean
	*/
	public Boolean getbIsHumanaOne()
	{
		return true;
	}

	/**
	* Returns responses of RemoteAction method
	* <p>
	* Method to get the Accums Data
	*
	* @param String           Enterprise Id of the policy Member
	* @param String           Type Of benfit
	* @param String           flag to indicate beyond two years
	* @param String           MemberGenKey
	* @return  Object               RemoteAction method
	*/
	@RemoteAction
		public static Object getAccumsData(String personId, String typeOfbenefit, String refDate)
	{
		return AccumsType_D_HUM.getAccumsData(personId, typeOfbenefit, refDate);

	}

	/**
	* Returns responses of continuation object
	* <p>
	* Callback Method to parse the service response
	* @Paramm Object State of the Continuation Call for Accums
	* @return continuation object
	*/
	public static Object prepareServiceData(object state)
	{
		return AccumsType_D_HUM.prepareServiceData(state);
	}
	
	/**
	* Returns responses of RemoteAction method
	* <p>
	* Method to get the Accums Data From REST
	*
	* @param String           Enterprise Id of the policy Member
	* @param String           Type Of benefit
	* @return  Object         RemoteAction method
	*/
	@RemoteAction
		public static Object getAccumsDataREST(String personId, String typeOfbenefit, String refDate)
	{
		return AccumsType_D_HUM_V2.getAccumsDataREST(personId, typeOfbenefit, refDate);

	}

	/**
	* Returns responses of continuation object
	* <p>
	* Callback Method to parse the service response
	* @Paramm Object State of the Continuation Call for Accums
	* @return continuation object
	*/
	public static Object prepareServiceDataREST(object state)
	{
		return AccumsType_D_HUM_V2.prepareServiceDataREST(state);
	}

	/**
	* Returns request object of continuation object
	* <p>
	* Method to invoke the MBE,PBE and GBE continuation requests
	*
	* @return continuation object
	*/
	public Continuation invokeMBEService()
	{
		if (lstLogErrors != NULL && lstLogErrors.size() == 0)
		{
			return oServiceBenefits.SendRequestMBEService(this);
		}
		return NULL;
	}

	/**
	* Returns responses of continuation object
	* <p>
	* Callback Method to parse the service response
	*
	* @return Object
	*/
	public Object parseMBE_GBE_PBEResponse()
	{
		return oServiceBenefits.ProcessDataHumanaOneResponse(this);
	}

	/**
	* Returns responses of AlternateReimburseId
	* <p>
	* Callback Method to parse the service response
	*
	* @return Object
	*/
	public void getAlternateReimburseID()
	{
		sAlternateReimburseId = oServiceBenefits.AlternateNumber();
	}

	/**
	* Void Method
	* <p>
	* Method to setting boolean value used to show Go Button in UI
	* @return Void
	*/
	public void renderGoButton()
	{
		oServiceBenefits.renderGoButton(this);
	}

	/**
	* Returns pageReference url with params
	* <p>
	* Method to form the url after adding refdate value
	*
	* @return pageref pagereferece of current page
	*/
	public pageReference executeGo()
	{
		pageReference pageref = ApexPages.currentpage();
		string s = DateTime.NewInstance(oEventObj.ActivityDate.year(), oEventObj.ActivityDate.month(), oEventObj.ActivityDate.day()).format('MMddYYYY');
		sDate = s;
		pageref.getParameters().put('refdate', s);
		return pageref;
	}

	/**
	* void method
	* <p>
	* Method to set the error message
	* @return Void
	*/
	public void showErrorMessage()
	{
		bRenderErrorMsg = true;
		ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR, VOB_DATE_ERROR_HUM));
	}

	/**
	* Returns responses of RemoteAction method
	* <p>
	* Method to get the Benefits Search Data
	*
	* @param String           Service Category Id
	* @param String           Type Of Service
	* @param String           Place of Service
	* @param String           Par /non Par
	* @param String           Platform Code
	* @param String           Product Key
	* @param String           Ref Date
	* @return                 Continuation Object
	*/
	@RemoteAction
		public static Object getBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate, Boolean isCAS, string ADACode)
	{
		return BenefitsV2_S_HUM.getBenefitSearch(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode);
	}

	/**
	* Returns responses of RemoteAction method
	* <p>
	* Method to get the Benefits Search Data
	*
	* @param Object           State of Continaution
	* @return                 Object Continuation response
	*/
	public Static Object processBenefitSearch(List<string> labels, Object State)
	{
		return BenefitsV2_S_HUM.processBenefitSearch(labels, State);
	}

	/**
	* Method is used to prepare Service Type list
	* <p>
	* @param
	* @return void
	*/

	public void getListOfServiceType()
	{

	}

	public void displayCaseList()
	{
		lstFinalCases = BenefitsV2_D_HUM.retrieveAccountCases(objPolMem.Member__c, sSortExpression, sSortDirection);
		stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
		if (stdsetcon != Null)
		{
			stdsetcon.setPageSize(PageSize_HUM);
			bExistingCaseScenario = true;
			bDisableToothNavBtn = true;
		}
	}

	/*
	* Method Name : sortResultPage
	* Description : This method is used to sort the case records.
	* Return type : Void
	* Parameter   : NA
	*/
	public void sortResultPage()
	{
		try
		{
			lstFinalCases = BenefitsV2_D_HUM.retrieveAccountCases(objPolMem.Member__c, sSortExpression, sSortDirection);
			stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
			if (stdsetcon != Null)    stdsetcon.setPageSize(PageSize_HUM);
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'BenefitsV2_C_HUM', 'sortResultPage');
		}
	}

	/*
	* Method Name : previous
	* Description : This method returns the previous page of records.
	* Return type : Void
	* Parameter   : NA
	*/
	public void previous()
	{
		stdsetcon.previous();
	}

	/*
	* Method Name : first
	* Description : This method returns the first page of records.
	* Return type : Void
	* Parameter   : NA
	*/
	public void first()
	{
		stdsetcon.first();
	}

	/*
	* Method Name : last
	* Description : This method returns the last page of records.
	* Return type : Void
	* Parameter   : NA
	*/
	public void last()
	{
		stdsetcon.last();
	}
	public void lastpage()
	{
		stdsetcon.last();
	}

	/*
	* Method Name : next
	* Description : This method returns the next page of records.
	* Return type : Void
	* Parameter   : NA
	*/
	public void next()
	{
		stdsetcon.next();
	}

	/*
	* Method Name : search
	* Description : This method is used filter cases by CaseNumber. This is an exact match search
	* Return type : Void
	* Parameter   : NA
	*/
	public void searchTHData()
	{
		if (String.isBlank(sCaseNumber))
		{
			stdsetcon = new ApexPages.StandardSetController(lstFinalCases);
		}
		else
		{
			stdsetcon = new ApexPages.StandardSetController(LoggingProcessLogic_S_HUM.SearchCases(sCaseNumber, lstFinalCases));
		}
		if (stdsetcon != Null)
		{
			stdsetcon.setPageSize(PageSize_HUM);
		}
	}
	/*
	* Method Name : getSortDirection
	* Description : This method is used to get the sortdirection.
	* Return type : String
	* Parameter   : NA
	*/
	public String getSortDirection()
	{
		if (String.isBlank(sSortDirection))
		{
			return Sort_Expression_DESC_HUM;
		}
		else
		{
			return sSortDirection;
		}
	}

	/**
	* This method is used to Call Case creation helper class to create new case
	*
	* @param  None
	* @return  Void
	*/
	public void createCaseAndRedirect()
	{
		try
		{
			objID = ApexPages.currentPage().getParameters().get('id');
			HUMNewCaseRedirectExtension caseRedExt = new HUMNewCaseRedirectExtension(NULL);
			caseRedExt.objectID = objID;
			caseRedExt.sObjectName = objID.getSobjectType().getDescribe().getName();
			caseRedExt.sStandardUrlForTab = ApexPages.CurrentPage().getParameters().get('standardURL');
			caseRedExt.funcOnPageLoad();
			caseRedExt.saveCase();
			sCaseID = caseRedExt.caseID;
			sTHCaseNumber = caseRedExt.sCaseNumber;
			sRedirectURL = caseRedExt.sRedirectURL;
			bShowPanel = false;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'BenefitsV2_C_HUM', 'createCaseAndRedirect');
		}
	}

	/**
	* This method is used to enable radio buttons
	*
	* @param  None
	* @return  Void
	*/
	public void showRadioBtn()
	{
		bShowPanel = true;
	}
	public void search()
	{

	}

	/**
	* This method is used to disable radio buttons
	*
	* @param  None
	* @return  Void
	*/
	public void disableRadioBtn()
	{
		bShowPanel = false;
	}
	/**
    * This method is used to Call Billing Service
    * @param  String NetwordId
          
    * @return  Void
    */   
	@RemoteAction
    public static Continuation getBillingProfiles(String sNetworkID, String sSearchID, String sPlatform, String sExchangeType, string sProductType,string sProduct, Boolean bSwitchForNewPBS)
    {
    	try
    	{
    		return BenefitsV2_S_HUM.processBillingProfiles(sNetworkID, sSearchID, sPlatform, sExchangeType, sProductType, sProduct, bSwitchForNewPBS);           
    	}
    	catch(Exception ex)
    	{
    		 HUMExceptionHelper.logErrors(ex,'BenefitsV2_C_HUM','getBillingProfiles');
    		 return null;
    	}
    }
    
    /**
    * Returns responses of RemoteAction method
    * <p>
    * Method to get the Parse Billing Service Response
    *
    * @param Object           State of Continaution
    * @return                 Object Continuation response
    */
    public Static Object parseBillingProfileResponse(List < string > labels ,Object State)
    {
    	try
    	{
        	return BenefitsV2_S_HUM.parseBillingProfileResponse(labels,State);
    	}
    	catch(Exception ex)
    	{
    		 HUMExceptionHelper.logErrors(ex,'BenefitsV2_C_HUM','parseBillingProfileResponse');
    		return null;
    	}
    } 
    
    public void MentorGridLaunch()
    {
                //Mentor Benefit Grid Changes
        bIsCasPolicy = false;
		IsAutoLaunchMentor = false;
        if (ApexPages.currentpage().getParameters().get('sectionId') == 'claimViewBenefits' ||((ApexPages.currentpage().getParameters().get('sectionId') == null || ApexPages.currentpage().getParameters().get('sectionId') == 'AccumulatorInformation') && ApexPages.currentpage().getParameters().get('refdate') == ''))
        {
            if (objPolMem.Policy__r.Platform__c == 'EM')
            {
                if((objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('SF') && objPolMem.Policy__r.Benefit_Coverage__c.substring(2, 3).isAlpha()) || (objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('FI') && objPolMem.Policy__r.Benefit_Coverage__c.substring(2, 3).isAlpha())|| objPolMem.Policy__r.Benefit_Coverage__c.substring(0, 2).contains('FE'))   
                {
                    IsAutoLaunchMentor = true;
                }
            }
            else if (objPolMem.Policy__r.Platform__c == 'LV' && !RestrictedCASProdType.contains(objPolMem.Product_Type__c))
            {
                bIsCasPolicy = true;
            }
            else
            {       
                IsAutoLaunchMentor = false; 
            }
        }
    }
    
     @testVisible public Continuation MentorGridService()
    {
         continuation oConMentor;
         try
        { 
        MentorServiceRequest_DTO_HUM KMDRequestDTO = new MentorServiceRequest_DTO_HUM();            
        HUM_Webservice_Callout__c KMDServiceDetails = HUM_Webservice_Callout__c.getValues(KMDOCUMENT);
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(KMDServiceDetails.Timeout__c) / 1000;
        oConMentor = new Continuation(iTIMEOUT_INT_SECS);
        // Set callback method should be common for KM Document services 
        oConMentor.continuationMethod = 'parseKMDResponse';
        HttpRequest httpKMDReqObj = builtHttpRequest(KMDServiceDetails);
        String sjsonRequest = JSON.serialize(BenefitsV2_D_HUM.RequestStructureKDMService(KMDRequestDTO));
        httpKMDReqObj.setBody(sjsonRequest);
        MentorServiceResponse_DTO_HUM responseKMD = new MentorServiceResponse_DTO_HUM();
        oConMentor.state = responseKMD;
        sKMDocRequest=oConMentor.addHttpRequest(httpKMDReqObj);
        }catch(Exception e)
        {
            
             HUMExceptionHelper.logErrors(e, 'BenefitsV2_C_HUM', 'MentorGridService');          
        }

        return oConMentor;

    }
 public void parseKMDResponse()
    {
        try
        {
      
         MentorServiceResponse_DTO_HUM oResponseKMD;
          HttpResponse responseKMD = Continuation.getResponse(sKMDocRequest);            
            if (responseKMD != null && (responseKMD.getStatusCode() == HTTP_OK_HUM || responseKMD.getStatusCode() == HTTP_ACCEPTED_HUM) || Test.isRunningTest())
            {
                oResponseKMD = (MentorServiceResponse_DTO_HUM) System.JSON.deserialize(responseKMD.getBody(), MentorServiceResponse_DTO_HUM.class);
               bKMD = BenefitsV2_D_HUM.ParseStructureKMDocument(oResponseKMD);
            }
            else
            {
                bKMD = false;
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'BenefitsV2_C_HUM', 'parseKMDResponse');
        }
        
        
    } 
     
    
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(HUM_PCP_SER_CONECTION, HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(HUM_PCP_SER_SOAPACTION,'');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
    }
	
	 public void createFloatingList()
    {
        lstFloatLinkelem = new list<string>();
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits1);   
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits2);
        If (bIsMedical) {
		//Commenting "Pharmacy Mail Order Benefit Snapshot" floating link as part of REQ - 1629104
        //lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits3); 
        }      
         If (bIsDental) {
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits7);
        }
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits4);   
		//Adding a check for Dental to relocate the Tooth History Section
		if(bIsDental && !bToothHistory) {
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits9); 
        }
        If (bIsDental) {
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits10);
        }  		
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits5); 
		if(bIsDental && bToothHistory) {
		lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits9); 
		} 		
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits6); 
        If (bIsDental) {
        lstFloatLinkelem.add(FloatingLinkConstants_HUM.sfloatLinkBenefits8);  
        }    
    }
	
	/**
    *  Gets list of tooth history from ToothHistory service
    *  @name getToothHistory
    *  @param String,String
    *  @return Continuation object
    */
    @RemoteAction
    public static Continuation getToothHistory(String sPolicyMemberId)
    {        
        List<Member_ID__c> lstMemberId = [SELECT Name, Member_Dependent_Code__c  FROM Member_ID__c WHERE Policy_Member__c = : String.escapeSingleQuotes(sPolicyMemberId) AND Type__c = : 'Member-Id-Base' LIMIT 1];  
        if(lstMemberId != null && !lstMemberId.isEmpty()){
            return BenefitsV2_S_HUM.callToothHistoryService(lstMemberId[0].Name,lstMemberId[0].Member_Dependent_Code__c);
        }
        return null;
    }
    
	/**
    *  Parse list of tooth history data from ToothHistory service
    *  @name parseToothHistoryResponse
    *  @param List < string > ,Object
    *  @return object
    */
    public Static Object parseToothHistoryResponse(List < string > labels ,Object State)
    {
        try
        {
            return BenefitsV2_S_HUM.parseToothHistoryResponse(labels,State);
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex,'BenefitsV2_C_HUM','parseToothHistoryResponse');
            return null;
        }
    } 
}