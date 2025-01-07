/*******************************************************************************************************************************
Apex Class Name : GroupBenefits_X_HUM.cls
Version         : 1.0
Created On      : 04/30/2018
Function        : Class contains methods for services to fetch Group benefits information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani                                     04/30/2018                   Original Version
* Rakesh Patni                                       05/15/2018                   Added functionality for getting sdate to display in current benefit period component
* Joel George										 07/02/2018					  Added changes for benefit Search
* Melkisan Selvaraj		       271010			     08/06/2018					  REQ - 362101 To auto populate benefits grid 
* Melkisan Selvaraj			   289968			     09/24/2018					  REQ - 376382 Modify rules for CAS benefit grids to auto-populate
* Avinash Kumar Choubey     	                       11/1/2018                 REQ 378715 changes
* Dinesh Subramaniyan				      04/18/2018		  		  REQ - 396416 - Modify logic for CAS benefit grids to auto-populate
* Sayali Nemade										   05/17/2020				  REQ - 737779 Dental Benefits Snapshot Description
* Riya Agarwal										   06/09/2020				        Added switch functionality for REQ - 953363
* Sayali Nemade 							           06/10/2020                REQ - 1127524 - Switch Added for Group Search CA ticket
* Riya Agarwal                                         01/13/2021          		  REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid
*********************************************************************************************************************************/

public with sharing class GroupBenefits_X_HUM extends SObjectDescriptorImpl_H_HUM
{
	private GroupBenefits_S_HUM oGrpServiceLayer;
	Public Static final String GROUPBENEFITS_SOURCECUSTCOVKEY_HUM = 'source_cust_cov_key__c';
    Public Static final String GROUPBENEFITS_COVPLANENDDATE_HUM = 'Coverage_Plan_end_date__c';
    Public Static final String GROUPBENEFITS_PRODUCTNAME_HUM = 'product__r.Name';
    Public Static final Set<String> RestrictedCASProdType = New Set<String>{'MCD','MGP','MGR','MES','MPD','MRP'};
    public static final string KMDOCUMENT = 'KMDocument';
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';




	//================ Group Search changes ========================

	public List<string> listServiceType{ get; set; }
	public String selectedCategory{ get; set; }
	public boolean bIsDental{ get; set; }
	public boolean bIsMedical{ get; set; }
	public Policy_Member__c objPolMem{ get; set; }
	public Policy_Plan_Details__c objPolPlan{get;set;}
	public string sCoverageAppURL{get;set;}
	public  string ServiceTypeDTOStr{ get; set; }

		//================ Group Search changes ========================

	public static boolean IsAutoLaunchMentor{ get; set; }
    public static boolean bIsCasPolicy{ get;set; }
    public static boolean bKMD{ get; set;}
    public string sKMDocRequest = '';
	
	//REQ - 737779
    public Boolean bSwitchBenefitSnapshot{ get; set;}
	//REQ - 953363
    public Boolean bSwitchBenefitPeriod{ get; set;}
	//REQ - 1127524
	public Boolean bSwitchGroupSearch{ get; set;}
	
	
		/**
		*
		* <p>
		* Parameterized Constructor Method to for the class utilizing standard controller
		*/
		public GroupBenefits_X_HUM(ApexPages.StandardController stdController) {
		if(!test.isRunningTest())
		stdController.addFields(new list<string>{GROUPBENEFITS_SOURCECUSTCOVKEY_HUM,GROUPBENEFITS_COVPLANENDDATE_HUM,GROUPBENEFITS_PRODUCTNAME_HUM});
		this.policyObject= (Policy__C)stdController.getRecord();
		oGrpServiceLayer = new GroupBenefits_S_HUM ();
		oGrpServiceLayer.init(this);
		asOfDate = GroupBenefits_D_HUM.populateAsOfDate(policyObject);
		//Mentor Benefit Grid Changes
		MentorGridLaunch();
        bKMD = false;
		//REQ - 737779 Switch for Benefits Snapshot description
         bSwitchBenefitSnapshot = HUMUtilityHelper.isCRMFunctionalityON('737779');
		 //REQ - 953363 Switch for Benefits Period field 
         bSwitchBenefitPeriod =  HUMUtilityHelper.isCRMFunctionalityON('953363');
		 //REQ - 1127524 Switch for Benefits Group Search CA ticket
        bSwitchGroupSearch = HUMUtilityHelper.isCRMFunctionalityON('1127524');
        
	}
	/**
	*
	* <p>
	* Non parameterized Constructor Method to for the class  to be used by the components
	*/
	public GroupBenefits_X_HUM(){
		oEventObj = new Event();
		sDate = GroupBenefits_D_HUM.populateDateofService();

		//===================== Group Search Changes ===================
		CoverageApp_Endpoint_HUM__c oCoverageAppURL = CoverageApp_Endpoint_HUM__c.getInstance('HumanaCoverageAppLink');
		if(oCoverageAppURL != null && string.isNotBlank(oCoverageAppURL.CoverageApp_Endpoint_URL__c))
		{
			sCoverageAppURL = oCoverageAppURL.CoverageApp_Endpoint_URL__c;
		}
		//===================== Group Search Changes ===================
	}
	public Policy__c policyObject{get;set;}
	public Policy_Plan_Details__c policyPlanObject{get;set;}
	public boolean bHasCRMS520Per{get;set;}
	public string sSFDCBaseURL{get;set;}
	public string sReimbursementAppURL{get;set;}
	public string sOneClickURL{get;set;}
	public string asOfDate{get;set;}
	public boolean benableCase{get;set;}
	public boolean bSerDateGreater{get;set;}
	public Event oEventObj{ get; set; }
	public string sDate{get;set;}
		/**
		* Returns request object of continuation object
		* <p>
		* Method to invoke the GBE and PBE service
		*
		* @param  GroupBenefits_RequestDTO_HUM request structure
		* @return continuation object
		*/
		@RemoteAction
		public static Object getGBEPBEResponse(GroupBenefits_RequestDTO_HUM requestStructure)
	{
		return  GroupBenefits_S_HUM.SendRequestPBEGBEService(requestStructure);
	}

	/**
	* Returns responses of RemoteAction method
	* <p>
	* Method to get the Benefits Search Data
	*
	* @param Object           State of Continaution
	* @return                 Object Continuation response
	*/
	public static Object ProcessDataPBEGBEResponse(List<string> labels ,Object State)
	{
		return GroupBenefits_S_HUM.ProcessDataPBEGBEResponse(labels,State);
	}
	/**
	* Returns List<sObject> of record
	* <p>
	* Virtual required Method
	*
	* @return NULL
	*/
	public List < sObject > getRecords()
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

	//===================Begin Group Search Changes ======================

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
		public static Object getBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate,Boolean isCAS, string ADACode)
	{
		return GroupBenefits_S_HUM.getBenefitSearch(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode);
	}

	/**
	* Returns responses of RemoteAction method
	* <p>
	* Method to get the Benefits Search Data
	*
	* @param Object           State of Continaution
	* @return                 Object Continuation response
	*/
	public Static Object processBenefitSearch(List<string> labels ,Object State)
	{
		return GroupBenefits_S_HUM.processBenefitSearch(labels,State);
	}

	/**
	* Returns this instance of the class
	* <p>
	* Method to store the Current Page Instance for Side Panel
	*
	* @return this
	*/
	public GroupBenefits_X_HUM getThis()
	{
		return this;
	}

	//===================END Group Search Changes ======================
	
   public void MentorGridLaunch()
    {
                //Mentor Benefit Grid Changes
        bIsCasPolicy = false;
		IsAutoLaunchMentor = false;
		if (ApexPages.currentpage().getParameters().get('sectionId') == null || ApexPages.currentpage().getParameters().get('sectionId') != 'accumulatorInfomation')
		{
			if (policyObject.Platform__c == 'EM')
			{
				If((policyObject.Benefit_Coverage__c.substring(0, 2).contains('SF') && policyObject.Benefit_Coverage__c.substring(2, 3).isAlpha()) || (policyObject.Benefit_Coverage__c.substring(0, 2).contains('FI') && policyObject.Benefit_Coverage__c.substring(2, 3).isAlpha())|| policyObject.Benefit_Coverage__c.substring(0, 2).contains('FE')) 
				{
					IsAutoLaunchMentor = true;
				}
			}
			else if (policyObject.Platform__c == 'LV' && !RestrictedCASProdType.contains(this.policyObject.Major_LOB__c))
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
       // String sjsonRequest = JSON.serialize(GroupBenefits_D_HUM.RequestStructureKDMService(KMDRequestDTO));
        httpKMDReqObj.setBody(JSON.serialize(GroupBenefits_D_HUM.RequestStructureKDMService(KMDRequestDTO)));
        MentorServiceResponse_DTO_HUM responseKMD = new MentorServiceResponse_DTO_HUM();
        oConMentor.state = responseKMD;
        sKMDocRequest=oConMentor.addHttpRequest(httpKMDReqObj);
        }catch(Exception e)
        {
            
             HUMExceptionHelper.logErrors(e, 'GroupBenefits_X_HUM', 'MentorGridService');          
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
               bKMD = GroupBenefits_D_HUM.ParseStructureKMDocument(oResponseKMD);
            }
            else
            {

                bKMD = false;
            }

        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'GroupBenefits_X_HUM', 'parseKMDResponse');
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
                request.setHeader(HUM_PCP_SER_CONECTION,HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(HUM_PCP_SER_SOAPACTION,'');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
    }
         
}