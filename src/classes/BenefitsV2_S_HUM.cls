/*******************************************************************************************************************************
Apex Class Name :  BenefitsV2_S_HUM.cls
Version: 1.0
Created On      : 04/11/2018 
Function        : Service Class for BenefitsV2_C_HUM.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Pradeepkumar Dani         29802                     02/08/2016                  Original Version
* Pradeepkumar Dani         30467                     03/09/2016                  Added changes for REQ - 237281
* Samridh Manucha           30467                     03/11/2016                  Sending Request for MBE service
* Vishal Verma              30727                     03/30/2016                  Defect: 221163.0001 - Added code to handle exception
* Rajesh Keswani            24929                     03/31/2016                  Defect: 221233.0001 - Added AsOfDate field in request of GBE
* Pradeepkumar Dani         24929                     04/13/2016                  Added changes for CAS Policy Logic
* Rajesh Keswani            25869                     04/22/2016                  MBE DTO creation
* Avinash Kumar Choubey     31092                     04/12/2016                  Added boolean variable bHasCRMS520Per for  for REQ 241731
* Rajesh Keswani            26063                     04/26/2016                  Changes for PBE DTO creation
* Rajesh Keswani            26826                     04/27/2016                  Changes for GBE DTO creation
* Vishal Verma              26826                     04/29/2016                  REQ 184827 : Method to invoke And Parse PBE Servie from Benefits Search
* Pradeepkumar Dani         27381                     05/12/2016                  Modifed for defect fix#224889
* Avinash Kumar Choubey     27559                     05/09/2016                  intialized variable sOneClickURl And sReimbursementAppURL  for  for REQ 252624
* Rajesh Keswani            30160                     06/09/2016                  REQ: 224050 - Implemented Benefit Search logic
* Rajesh Keswani            31427                     06/29/2016                  Defect fix: 230939
* Pradeepkumar Dani         31811                     07/04/2016                  Added changes for standard values for logging - Defect#230623.0001
* Pradeepkumar Dani         32373                     07/11/2016                  Updated/Removed standard values for logging - Defect#232935.0001
* Rajesh Keswani            33023                     06/16/2016                  Implemented sorting in Benefit Search logic.
* Praveen Kumar Parimi      56654                     01/11/2017                  Creating a New version to reference the New Controller as part of Tiering Project.
* Rajesh Keswani            109089                    07/24/2017                  REQ - 299431 Making Place of Service drop down dynamic
* Avinash Choubey           109914                    07/24/2017                  REQ - 300751 Making Par Code� drop down dynamic(for CAS Members) and Sorted
* Santhi Mandava            85904                     04/20/2017                  Implemented requirement 300121 - Merged rolledback code
* Vijaya Koriprolu          138809                    10/07/2017                  REQ-304935 : VOB Prefill the Reimbursement tool with Provider Contract Number
* Sumit Pharande            162950                    01/09/2018                  Upgraded to MBE Plus
* Melkisan Selvaraj         220187                    04/11/2018                  Cloned from BenefitsV1_VF_HUM for REQ - 360140
* Rajesh Keswani                                      04/18/2018                  REQ - 360142; Update Benefits Service and Data Layer classes for PBE Second call
* Rajesh Keswani   			229751					  05/15/2018				  REQ - 361224; Adding ADA code in Benefits search section
* Vishal Verma				229766					  05/16/2018                  REQ - 355782 : Add Dental Paid Thru Date to Benefits and Accums Summary section
* Joel George										  06/15/2018				  REQ - 366156
* Anjali Kumar                                        04/08/2018                  REQ 363358 - Made changes for Maternity code in service category
* Sayali Gavane										  07/14/2020				  REQ_1206896-CA ticket - 9261131 (Sections in benefit page are Not displaying)
* Sai Kumar Boga                                      11/10/2020                  REQ - 1567028  CALL_MF03_FR02: KY MMC 2021(Added per CR17)_CRM service must create a NATIVE AMERICAN indicator
* Sayali Gavane									 	  11/25/2020				 REQ - 1457838 - PR00095581 - MF 53 - Out of Area Indicator for CRM
* Sayali Gavane                                    	  01/19/2020                  REQ_1450427-CA ticket - 9261131 (Sections in benefit page are not displaying) 
* Prasanthi Kandula                                   03/25/2021                  US:1437221 Displaying Paid Thru Date for DEN and MED Benefit Summary     
* Satyanarayana chenna                                04/09/2021                  US:1918663 PR00094226 - CR AEP readiness - Low Security Issues
* Rahul Chaurasia									  04/19/2021				 1895456 - T1PRJ0002736 - 2021 MF6  - CR AEP readiness - Low Security Issues 
* Suraj Patil                              			  05/31/2021			      USER STORY 1867803 T1PRJ0003278/PR00095917 - MF01 - Tooth History Data Fields
* Nirmal Garg										  11/26/2021				Defect 4215 - removed dependency from global constant and hum constant
* Pinky Vijur                                         01/13/2022                US 2606118 T1PRJ0002606- MF6 - SF-Tech- BenefitsV2_S_HUM Error research and fix
* Akash Surwase                                       04/27/2022                 User Story 2444097: T1PRJ0054938 MF09 - PLT - CRM Must Display New Fields in Medicaid Details Section  for OH MCD
* Akash Surwase                                       04/29/2022                 User Story 3134909: T1PRJ0054938 MF09 - Medicaid Policy Detail - Native Amercian to Indigenous Indicator
* Bhumika S                                           11/09/2022                 User Story 3672198: T1PRJ0163283 MCD-NS-LA 3.5.1.2: CRM Indigenous Indicator for All Medicaid States        
*********************************************************************************************************************************/

public with sharing class BenefitsV2_S_HUM
{
	Private BenefitsV2_D_HUM oDataBenefits = New BenefitsV2_D_HUM();
	Public string sAlternateRemId = '';
	public List<Policy_Member__c> pollist;
    public MBEResponseV1_DTO_HUM mbeDetails;
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String  VOB_TERMEDDATE_HUM = '12/31/9999';
    Public Static final String HUM_PLATFORM_CODE_LV = 'LV';
    Public Static final String HUM_PLATFORM_CODE_CI = 'CI';
    Public Static final String HUM_PLATFORM_CODE_CAS = 'CAS';
    Public Static final String  VOB_CURRENT_HUM = 'Current';
    Public Static final String  VOB_PAST_HUM = 'Past';
    Public Static final String  VOB_FUTURE_HUM = 'Future';
    Public Static final String HUM_MBE_GBE_PBE_Response = 'parseMBE_GBE_PBEResponse';
    Public Static final String HUM_PCP_SER_CONTENTTYPE = 'content-type';
    Public Static final String HUM_PCP_SER_CONECTION = 'Connection';
    Public Static final String HUM_PCP_SER_KEEPALIVE = 'keep-alive';
    Public Static final String HUM_PCP_SER_SOAPACTION = 'SOAPAction';
    Public Static final String Race_Code_G = 'G';
    Public Static final String Race_Code_i  ='i';
	Public Static final String Race_Code_AI  ='AI';
    Public Static final String Namerican_Indc_Y  ='Y';
    Public Static final String Namerican_Indc_N  ='N';
    public Static final String MBEFaultCode= '83.1.1';
    public Static final String PBEFaultCode= '111111';
    public Static final String PBEFaultCode1= '000305';
    Public Static final String VOB_PBE_FAULT_CODE = 'faultcode';
	Public Static final String VOB_BENEFITSEARCH_PROCESSCALL_HUM = 'processBenefitSearch';
	Public Static final Integer HUMServiceTimeOut = 120;
	Public Static final Integer HTTP_OK = 200;
    Public Static final Integer HTTP_ACCEPTED = 202; 
	public Static final String OOAIndicator = 'OutOfAreaIndicator';
	Public Static final String  HUM_BENEFITS_MED = 'MED';
    Public Static final String  HUM_BENEFITS_DEN = 'DEN';
	Public Static final String VOB_REMAPPURL_HUM = 'REMAPPURL';
	/**
	* constructor method For BenefitsV2_C_HUM
	* <p>
	* Method to set the values for BenefitsV2_C_HUM.
	*
	@param benefitsCls              BenefitsV2_C_HUM class instance
	*/
	public void init(BenefitsV2_C_HUM benefitsCls)
	{
		try
		{
			benefitsCls.objPolMem = new Policy_Member__c();
			benefitsCls.bIsDental = false;
			benefitsCls.bIsMedical = false;
			benefitsCls.bIsVASPolicy = false;
			benefitsCls.sMBEServiceResponse = '{}';
			benefitsCls.sGBEServiceResponse = '{}';
			benefitsCls.sPBEServiceResponse = '{}';
			benefitsCls.bRenderErrorMsg = false;
			benefitsCls.oEventObj = new Event();
			benefitsCls.benableCase = false;
			benefitsCls.bSerDateGreater = false;
			benefitsCls.bHasCRMS520Per = false;
			benefitsCls.sSFDCBaseURL = null;
			// req 252624 changes start
			benefitsCls.sOneClickURL = null;
			benefitsCls.sReimbursementAppURL = null;			
			// req 252624 changes end
			benefitsCls.bIsDenGroup = false;
			benefitsCls.sOOAGroup = null;
			benefitsCls.MBEFaultString = null;
            benefitsCls.PBEFaultString = null;
            benefitsCls.bSwitchErrorMsg = false;  
			assignValues(benefitsCls);
		}
		catch (Exception ex)
		{
			//As DML is not currently allowed , set the boolean flag to stop insertion 
			HUMExceptionHelper.bRestrictLog = true;
			benefitsCls.lstLogErrors = HUMExceptionHelper.logErrors(ex, 'BenefitsV2_S_HUM', 'init');
		}
	}

	/**
	* assigning values to variables on load of page
	* <p>
	* Method to set the values for BenefitsV2_C_HUM. 
	*
	@param benefitsCls              BenefitsV2_C_HUM class instance
	*/
	public void assignValues(BenefitsV2_C_HUM benefitsCls)
	{
		if (ApexPages.currentpage().getParameters().containsKey('Id'))
		{
			benefitsCls.sPolMemId = ApexPages.currentpage().getParameters().get('Id');
            benefitsCls.objPolMem = oDataBenefits.getPolicyMember_SOQL(benefitsCls,ApexPages.currentpage().getParameters().get('Id'));
			benefitsCls.sAttachmentKey = benefitsCls.objPolMem.Policy__r.Policy_External_ID__c;
			benefitsCls.objPolPlan = oDataBenefits.getPolicyPlan_SOQL(benefitsCls);
			benefitsCls.sPolEndDate = benefitsCls.objPolMem.End_Date__c;
		}
		benefitsCls.standardMapForLogging.put('Policy Member Id', new Map<String, Object>{ 'Policy Member Id' => benefitsCls.objPolMem.Name });
		If(benefitsCls.objPolPlan.Benefit_Coverage__c != null)
		{
			benefitsCls.standardMapForLogging.put('Benefit Package ID', new Map<String, Object>{ 'Benefit Package ID' => benefitsCls.objPolPlan.Benefit_Coverage__c });
		}
		else
		{
			benefitsCls.standardMapForLogging.put('Benefit Package ID', new Map<String, Object>{ 'Benefit Package ID' => benefitsCls.objPolMem.Policy__r.Benefit_Coverage__c });
		}
		if (ApexPages.currentpage().getParameters().get('refdate') != null)
		{
			if (VOB_TERMEDDATE_HUM <> benefitsCls.sPolEndDate && Date.parse(benefitsCls.sDate) > Date.parse(benefitsCls.sPolEndDate))
			{
				benefitsCls.bSerDateGreater = true;
			}
		}
		if (NULL != benefitsCls.objPolMem && benefitsCls.objPolMem.Policy__r.Product__r.Name == HUM_BENEFITS_MED)
		{
			benefitsCls.bIsMedical = true;
		}
		else if (NULL != benefitsCls.objPolMem && benefitsCls.objPolMem.Policy__r.Product__r.Name == HUM_BENEFITS_DEN)
		{
			benefitsCls.bIsDental = true;
			benefitsCls.sOOAGroup = HUMConstants__c.getInstance('OOAGroup').StringValue__c;
			List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name = 'CRMS_520_Benefits_Read_Only' limit 1];
			if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
			{
				benefitsCls.bHasCRMS520Per = true;
				benefitsCls.sSFDCBaseURL = System.URL.getSalesforceBaseUrl().toExternalForm();
				HUMToothHistoryAppURL__c objAppURL = HUMToothHistoryAppURL__c.getValues(VOB_REMAPPURL_HUM);
				if (objAppURL != null && null != objAppURL.CCPAppURL__C)
				{
					benefitsCls.sReimbursementAppURL = '' + objAppURL.CCPAppURL__C;
					benefitsCls.sOneClickURL = '' + objAppURL.oneClickURL__c;
				}
			}
			If (benefitsCls.bIsDental == True && benefitsCls.objPolMem.Group_Number__c == benefitsCls.sOOAGroup)
            {                
               benefitsCls.bIsDenGroup = true; 
            }
		}
		if (benefitsCls.objPolMem.Policy_Platform__c == HUM_PLATFORM_CODE_LV || benefitsCls.objPolMem.Policy_Platform__c == HUM_PLATFORM_CODE_CI || benefitsCls.objPolMem.Policy_Platform__c == HUM_PLATFORM_CODE_CAS)
		{
			benefitsCls.bIsVASPolicy = true;
		}
	}
/**
    * Returns MBEPlusRequest_DTO_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for MBE service .
    *
    * @param    PolMemobj   an policy member record object derived from SOQL results.
    * @return  MBEPlusRequest_DTO_HUM      request structure of the wrapper class MBEServiceRequest_H_HUM
    */
    
    public  MBEPlusRequest_DTO_HUM raceStructureMBEService(List<Policy_Member__C> oPolMem)
    {        
        try
        {
			set<id> lstPolId = new set<id>();
			for(Policy_Member__C obj : oPolMem) lstPolId.add(obj.id);
			
            MBEPlusRequest_DTO_HUM oReq = new MBEPlusRequest_DTO_HUM();
            MBEPlusRequest_DTO_HUM.GetMember getMember = new MBEPlusRequest_DTO_HUM.GetMember();            
            MBEPlusRequest_DTO_HUM.Member raceReq = new MBEPlusRequest_DTO_HUM.Member();
            MBEPlusRequest_DTO_HUM.PersonIdCriteria prsnID = new MBEPlusRequest_DTO_HUM.PersonIdCriteria();            
            MBEPlusRequest_DTO_HUM.GetCriteria getCriteria = new MBEPlusRequest_DTO_HUM.GetCriteria();
            MBEPlusRequest_DTO_HUM.Period per = new MBEPlusRequest_DTO_HUM.Period();
            List<String> lstper = new List<String>{ VOB_CURRENT_HUM, VOB_PAST_HUM, VOB_FUTURE_HUM };
        
                pollist = new List<Policy_Member__c>([SELECT Id, Name,Member__r.Enterprise_Id__c,Issue_State__c FROM Policy_Member__c WHERE Id IN :lstPolId limit 1]);
            
                per.Period = lstper;
                prsnID.PeriodList = per;
                prsnID.AsOfDate = string.valueof(date.today());
            	prsnID.PersonId = pollist[0].Member__r.Enterprise_Id__c;
            	getCriteria.PersonIdCriteria = prsnID;
            	raceReq.GetCriteria = getCriteria;
            	getMember.Member = raceReq;
            	oReq.GetMember = getMember;
                Return oReq;              
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'BenefitsV2_S_HUM', 'raceStructureMBEService');
            return null;
        }
    }


	/**
	* Returns continuation object
	* <p>
	* Method to call continuation serive for the serive for MBE,PBE and GBE services .
	*
	* @param benefitsCls              BenefitsV2_C_HUM class instance
	* @return                                continuation
	*/
	public Continuation SendRequestMBEService(BenefitsV2_C_HUM benefitsCls)
	{
		try
		{
			HUM_Webservice_Callout__c MBEServiceDetails = HUM_Webservice_Callout__c.getValues('MBEPlusService');
			HUM_Webservice_Callout__c GBEServiceDetails = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
			HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
			// Create continuation with a timeout
			integer iTIMEOUT_INT_SECS = Integer.valueof(GBEServiceDetails.Timeout__c) / 1000;
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
			// Set callback method should be common for all three services (MBE,GBE,PBE)
			con.continuationMethod = HUM_MBE_GBE_PBE_Response;
			//Build MBE Request 
			HttpRequest httpMBEReqObj = BenefitsV2_S_HUM.builtHttpRequest(MBEServiceDetails);
			httpMBEReqObj.setBody(JSON.serialize(BenefitsV2_D_HUM.RequestStructureMBEService(benefitsCls.objPolMem)));
			// Build GBE Request 
			HttpRequest httpGBEReqObj = BenefitsV2_S_HUM.builtHttpRequest(GBEServiceDetails);
			httpGBEReqObj.setBody(JSON.serialize(BenefitsV2_D_HUM.RequestStructureGBEService(benefitsCls.objPolMem, benefitsCls.objPolPlan, benefitsCls.sDate)));
			// Build PBE Request 
			HttpRequest httpPBEReqObj = BenefitsV2_S_HUM.builtHttpRequest(PBEServiceDetails);
			httpPBEReqObj.setBody(JSON.serialize(BenefitsV2_D_HUM.RequestStructurePBEService(benefitsCls)));
			// Add callout requests to continuation for all three requests
			benefitsCls.sRequestLabelMBE = con.addHttpRequest(httpMBEReqObj);
			benefitsCls.sRequestLabelGBE = con.addHttpRequest(httpGBEReqObj);
			benefitsCls.sRequestLabelPBE = con.addHttpRequest(httpPBEReqObj);
			return con;

		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'BenefitsV2_S_HUM', 'SendRequestMBEService');
			return Null;
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
		try
		{
			if (ServiceObj != null && ServiceObj.Active__c)
			{
				request.setMethod(ServiceObj.Request_Type__c);
				if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
				if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
				request.setHeader(HUM_PCP_SER_CONECTION, HUM_PCP_SER_KEEPALIVE);
				request.setEndpoint(ServiceObj.End_Point_URL__c);
				request.setHeader(HUM_PCP_SER_SOAPACTION, '');
				if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
			}
			return request;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'BenefitsV2_S_HUM', 'builtHttpRequest');
			return Null;
		}
	}

	/**
	* Returns null but sets the values of responses to UI
	* <p>
	* Callback method to process the serive for MBE,PBE and GBE services .
	*
	* @param benefitsCls          BenefitsV2_C_HUM class instance
	* @return                                   NULL
	*/
	public Object ProcessDataHumanaOneResponse(BenefitsV2_C_HUM benefitsCls)
	{
		try
		{
			//escapeEcmaScript method is used to escape all hidden special characters
			HttpResponse responseMBE = Continuation.getResponse(benefitsCls.sRequestLabelMBE);
			benefitsCls.sMBEServiceResponse = responseMBE.getBody();
			benefitsCls.sMBEServiceResponse = benefitsCls.sMBEServiceResponse.escapeJava();
			HttpResponse responseGBE = Continuation.getResponse(benefitsCls.sRequestLabelGBE);
			benefitsCls.sGBEServiceResponse = responseGBE.getBody();
			benefitsCls.sGBEServiceResponse = benefitsCls.sGBEServiceResponse.escapeJava();
			HttpResponse responsePBE = Continuation.getResponse(benefitsCls.sRequestLabelPBE);

			parseMBE(benefitsCls, responseMBE);
			parsePBE(benefitsCls, responsePBE);
			parseGBE(benefitsCls, responseGBE);

			benefitsCls.standardMapvalue = JSON.serialize(benefitsCls.standardMapForLogging);
			return NULL;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'BenefitsV2_S_HUM', 'ProcessDataHumanaOneResponse');
			return null;
		}
	}
    /*
    *  This method is used to parse the MBE service response
    *  @name  parseMbeRaceresponse
    *  @param response - MBE service response
    *  @return MBEResponseV1_DTO_HUM - wrapper class to store data from service
    */
    public MBEResponseV1_DTO_HUM parseMbeRaceresponse(String  response)
    {
         try
         { 
            
            MBEPlusResponse_DTO_HUM  oResponseMBE = new MBEPlusResponse_DTO_HUM ();
            oResponseMBE = (MBEPlusResponse_DTO_HUM)System.JSON.deserialize(response, MBEPlusResponse_DTO_HUM.class);
            if(oResponseMBE.GetMemberResponse != null)
            {
                    MBEPlusResponse_DTO_HUM.GetMemberResponse oMBEResponse = oResponseMBE.GetMemberResponse;
                    MBEPlusResponse_DTO_HUM.Body oBody = oMBEResponse.Body;
                    MBEPlusResponse_DTO_HUM.Member oMember = oBody.Member;
                    MBEPlusResponse_DTO_HUM.RaceList oRaceList = oMember.RaceList;
                   
                         
            if(oRaceList != null) {
        
                    List<MBEPlusResponse_DTO_HUM.Race> oRace  = oRaceList.Race;
                        
                      
            if(oRace != null && oRace.size() > 0)
                      {
                    mbeDetails = buildMbeWrapperList(oRace);
                      }
           }
            }
        
    return mbeDetails;
        }
     catch(Exception ex)
         {     
             HUMExceptionHelper.logErrors(ex, 'BenefitsV2_S', 'parseMbeRaceresponse');
             return null;
         }   
     }
    
  
     /*
    *  This method is used to parse the MBE service reponse
    *  @name  buildMbeWrapperList
    *  @param MList<MBEPlusResponse_DTO_HUM.Race> oRace
        
    *  @return MBEResponseV1_DTO_HUM - wrapper class to store data from service
    */
    @TestVisible private MBEResponseV1_DTO_HUM buildMbeWrapperList( List<MBEPlusResponse_DTO_HUM.Race> oRace)
    {
        try
        {
            MBEResponseV1_DTO_HUM objWrapperMediacid = new MBEResponseV1_DTO_HUM ();
            if(oRace[0].Code!= null){
               if((oRace[0].Code.equalsIgnoreCase(Race_Code_G) || oRace[0].Code.equalsIgnoreCase(Race_Code_i)) && (pollist[0].issue_state__c != 'IL'))
                {
                    objWrapperMediacid.Code = Namerican_Indc_Y; 
                }
				else if((oRace[0].Code.equalsIgnoreCase(Race_Code_AI) && (pollist[0].issue_state__c == 'IL') && (HUMUtilityHelper.isCRMFunctionalityON('2444097') || HUMUtilityHelper.isCRMFunctionalityON('3134909')))){
					objWrapperMediacid.Code = Namerican_Indc_Y;
				}
                else{
                    objWrapperMediacid.Code = Namerican_Indc_N; 
                }      
            } 
           
    return objWrapperMediacid ;
        }
        catch(Exception ex)
        {            
            HUMExceptionHelper.logErrors(ex, 'BenefitsV2_S_HUM', 'buildMbeWrapperList');
            return null;
        }
    }

	/**
	* void Method
	* <p>
	* Method to Parse MBE response
	*
	* @param  benefitsCls Class Instance .
	* @param  HttpResponse for MBE
	*/
	public void parseMBE(BenefitsV2_C_HUM benefitsCls, HttpResponse responseMBE)
	{
		//MBE reponse started 
		String MBEresposeBody = responseMBE.getBody();
		if(!checkResponseValidity(responseMBE))
		{
			benefitsCls.MBEDTOResponseFlag = false;
			 if(benefitsCls.bSwitchErrorMsg == true)
            {                
                MBEresposeBody = MBEresposeBody .replace('-', '_');                
                FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
                faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(MBEresposeBody , FaultResponse_DTO_HUM.class);                
                if (faultResponse.Fault != null )
                {                                             
                    if(faultResponse.Fault.faultcode != null && faultResponse.Fault.faultcode.contains(MBEFaultCode))
                    {                    
                        benefitsCls.MBEFaultString = faultResponse.Fault.faultcode.stripHtmlTags();                                              
                    }                           
                }
            }  
		}
		else
		{
			benefitsCls.MBEDTOResponseFlag = true;
	            MBEPlusResponse_DTO_HUM  oResponseMBE = (MBEPlusResponse_DTO_HUM)System.JSON.deserialize(MBEresposeBody, MBEPlusResponse_DTO_HUM.class);
	            MBEResponseV1_DTO_HUM objResponse = BenefitsV2_D_HUM.getMBEDTOData(oResponseMBE, benefitsCls.objPolMem, benefitsCls);
			If(null != objResponse && null != objResponse.sCoverageType)
			{
				benefitsCls.standardMapForLogging.put('Coverage Type', new Map<String, Object>{ 'Coverage Type' => objResponse.sCoverageType });
			}
			Boolean bIsOutOfAreaPresent = false;
            If(objResponse != Null && objResponse.IndicatorList != Null && objResponse.IndicatorList.Indicator != Null && !objResponse.IndicatorList.Indicator.isEmpty())
            {
                For(Integer i =0; i < objResponse.IndicatorList.Indicator.size();i++)
                {
                    If(objResponse.IndicatorList.Indicator[i].Name == 'OutOfArea')
                    {
                        benefitsCls.standardMapForLogging.put('Out of Area Indicator', new Map<String, Object>{ 'Out of Area Indicator' => objResponse.IndicatorList.Indicator[i].Value});
                        bIsOutOfAreaPresent = true;
					}
				}
			}
			if (!bIsOutOfAreaPresent) benefitsCls.standardMapForLogging.put('Out of Area Indicator', new Map<String, Object>{ 'Out of Area Indicator' => '' });
			If(objResponse != Null && objResponse.PolicyIndicatorList!= Null && objResponse.PolicyIndicatorList.Indicator != Null && !objResponse.PolicyIndicatorList.Indicator.isEmpty())
            {                                
                For(Integer i =0; i < objResponse.PolicyIndicatorList.Indicator.size();i++)
                {
                    If(objResponse.PolicyIndicatorList.Indicator[i].Name == OOAIndicator)
                    {
                        benefitsCls.standardMapForLogging.put('Out of Area Indicator', new Map<String, Object>{ 'Out of Area Indicator' => objResponse.PolicyIndicatorList.Indicator[i].Value});                       
                    }
                }
            } 
            If(objResponse != Null && objResponse.OutOfAreaList!= Null && objResponse.OutOfAreaList.OutOfArea != Null && !objResponse.OutOfAreaList.OutOfArea.isEmpty())
            {                               
                    If(objResponse.OutOfAreaList.OutOfArea[0].StartDate !=null && objResponse.OutOfAreaList.OutOfArea[0].EndDate != null)
                    {
                        String sOutOfAreaDate = objResponse.OutOfAreaList.OutOfArea[0].StartDate + '-' + objResponse.OutOfAreaList.OutOfArea[0].EndDate;
                        benefitsCls.standardMapForLogging.put('Out of Area Date', new Map<String, Object>{ 'Out of Area Date' => sOutOfAreaDate});                       
                    }                
            }
			benefitsCls.vMBEServiceRes = JSON.serialize(objResponse);
			if (string.isNotBlank(benefitsCls.vMBEServiceRes)) benefitsCls.vMBEServiceRes = benefitsCls.vMBEServiceRes.escapeEcmaScript();
		}
	}
    
	/**
	* void Method
	* <p>
	* Method to Parse PBE response
	*
	* @param  benefitsCls Class Instance .
	* @param  HttpResponse for PBE
	*/
	public void parsePBE(BenefitsV2_C_HUM benefitsCls, HttpResponse responsePBE)
	{
		//PBE reponse started 
		String PBEresposeBody = responsePBE.getBody();
		if(!checkResponseValidity(responsePBE))
		{
			benefitsCls.PBEDTOResponseFlag = false;
			if(benefitsCls.bSwitchErrorMsg == true)
            {
                PBEresposeBody = PBEresposeBody.replace('-', '_');
                FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
                faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(PBEresposeBody , FaultResponse_DTO_HUM.class);
                if (faultResponse.Fault != null && faultResponse.Fault.detail != null)
                {                                             
                    if(faultResponse.Fault.detail.errorInfo.error_message != null && faultResponse.Fault.detail.errorInfo.error_message.contains(PBEFaultCode))
                    {                    
                        benefitsCls.PBEFaultString = PBEFaultCode;                                              
                    } 
                    else if(faultResponse.Fault.detail.errorInfo.error_message != null && faultResponse.Fault.detail.errorInfo.error_message.contains(PBEFaultCode1))
                    {                    
                        benefitsCls.PBEFaultString = PBEFaultCode1;                                            
                    }                                                  
                }
            }
		}
		else
		{
			benefitsCls.PBEDTOResponseFlag = true;
			ProductBusinessEntityResponseV2_H_HUM oResponsePBE = (ProductBusinessEntityResponseV2_H_HUM)System.JSON.deserialize(PBEresposeBody, ProductBusinessEntityResponseV2_H_HUM.class);
			PBEResponseV2_DTO_HUM DTOobj = BenefitsV2_D_HUM.getPBEDTOData(oResponsePBE);
			Map<String, String> mapServiceCategoryName;
			List<ProductBusinessEntityResponseV2_H_HUM.ServiceType> sameServiceCategoryTypeList;
			Map<string, List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>> mapServiceCategory;
			mapServiceCategory = new Map<string, List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>>();
            mapServiceCategoryName = new Map<String,String>();
			if (DTOobj != null && DTOobj.ServiceCategoryList != null && DTOobj.ServiceCategoryList.ServiceCategory != null && DTOobj.ServiceCategoryList.ServiceCategory.size() > 0 && DTOobj.ServiceTypeList != null && DTOobj.ServiceTypeList.ServiceType != null)
			{
                String sCodeVal;
				for (ProductBusinessEntityResponseV2_H_HUM.ServiceCategory serviceCatObj : DTOobj.ServiceCategoryList.ServiceCategory)
				{
					sameServiceCategoryTypeList = new List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>();
                    if(serviceCatObj.CodeList.Code !=null){
                      sCodeVal = '';
                      for(String s : serviceCatObj.CodeList.Code){
                        if(sCodeVal != ''){
                            sCodeVal = sCodeVal + ','+s;
                          }else{
                            sCodeVal = s;
                          }
                      }
                    }
					for (ProductBusinessEntityResponseV2_H_HUM.ServiceType serviceTypeObj : DTOobj.ServiceTypeList.ServiceType)
					{
                        if (serviceCatObj.CodeList.Code.contains(serviceTypeObj.ServiceCategoryCode)) {
                          sameServiceCategoryTypeList.add(serviceTypeObj);
                        }
                    }
                    mapServiceCategory.put(sCodeVal, sameServiceCategoryTypeList);
                    mapServiceCategoryName.put(serviceCatObj.Name,sCodeVal);
				}
			}
			if (mapServiceCategory != null && mapServiceCategory.size() > 0)
			{
				benefitsCls.ServiceTypeDTO = new PBEServiceTypeV2DTO_HUM();
				benefitsCls.ServiceTypeDTO.mapServiceCatServicetype = mapServiceCategory;
                benefitsCls.ServiceTypeDTO.mapServiceCatCode = mapServiceCategoryName;
				benefitsCls.ServiceTypeDTOStr = JSON.serialize(benefitsCls.ServiceTypeDTO).escapeEcmaScript();
			}

			
			benefitsCls.standardMapForLogging.put('Benefits Period', new Map<String, Object>{ 'Benefits Period' => DTOobj.PackageInfo.AccumulationPeriod });
			benefitsCls.standardMapForLogging.put('Plan Option', new Map<String, Object>{ 'Plan Option' => DTOobj.PackageInfo.Name });
			benefitsCls.standardMapForLogging.put('Deductible Type', new Map<String, Object>{ 'Deductible Type' => DTOobj.Deductible.TypeCode });
			benefitsCls.standardMapForLogging.put('HDHP', new Map<String, Object>{ 'HDHP' => DTOobj.IsHDHP });
			benefitsCls.vPBEServiceRes = JSON.serialize(DTOobj);
			if (string.isNotBlank(benefitsCls.vPBEServiceRes)) benefitsCls.vPBEServiceRes = benefitsCls.vPBEServiceRes.escapeEcmaScript();
			sAlternateRemId = DTOobj.PackageInfo.AlternateReimburseId;
		}
	}

	/*
	* Method Name : AlternateNumber
	* Description : This method is used to return Alternate Reimbursement Id
	* Return type : String
	*/
	public String AlternateNumber()
	{
		return sAlternateRemId;
	}



	/**
	* void Method
	* <p>
	* Method to Parse GBE response
	*
	* @param  benefitsCls Class Instance .
	* @param  HttpResponse for GBE
	*/
	public void parseGBE(BenefitsV2_C_HUM benefitsCls, HttpResponse responseGBE)
	{
		// prepare GBE DTO data
		String GBEresposeBody = responseGBE.getBody();
		if (!checkResponseValidity(responseGBE))
		{
			benefitsCls.GBEDTOResponseFlag = false;
		}
		else
		{
			benefitsCls.GBEDTOResponseFlag = true;
			GBEServiceResponse_H_HUM oResponseGBE = (GBEServiceResponse_H_HUM)System.JSON.deserialize(GBEresposeBody, GBEServiceResponse_H_HUM.class);
			GBEResponse_DTO_HUM objResponse = BenefitsV2_D_HUM.getGBEDTOData(oResponseGBE);
			benefitsCls.standardMapForLogging.put('Market', new Map<String, Object>{ 'Market' => objResponse.sMarketName.trim() });
			benefitsCls.standardMapForLogging.put('Network Description', new Map<String, Object>{ 'Network Description' => objResponse.sBenefitNetwork });
			benefitsCls.standardMapForLogging.put('Selling Ledger Number', new Map<String, Object>{ 'Selling Ledger Number' => objResponse.sSellingLedger });
			benefitsCls.standardMapForLogging.put('Last Renewal Date', new Map<String, Object>{ 'Last Renewal Date' => objResponse.sLastRenewalDate });
			benefitsCls.vGBEServiceRes = JSON.serialize(objResponse);
			if (string.isNotBlank(benefitsCls.vGBEServiceRes)) benefitsCls.vGBEServiceRes = benefitsCls.vGBEServiceRes.escapeEcmaScript();
		}
	}
   public static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        try {
            if(response != null) {
                integer statusCode = response.getStatusCode();
                Boolean isStatusOk = (statusCode == HTTP_OK_HUM) ? true : (statusCode == HTTP_ACCEPTED_HUM) ? true : false;
                Boolean isNoFault = (response.getBody().containsIgnoreCase(VOB_FAULT_CODE)) ? false : true;
                returnValue = (isStatusOk && isNoFault) ? true : false;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex,'BenefitsV2_S_HUM','checkResponseValidity');
            returnValue = false;
        }
        return returnValue;
    }

	/**
	* void Method
	* <p>
	* Method to setting boolean value used to show Go Button in UI
	*
	* @param  benefitsCls Class Instance .
	*/
	public void renderGoButton(BenefitsV2_C_HUM benefitsCls)
	{
		if (benefitsCls.oEventObj.ActivityDate != null)
		{
			benefitsCls.benableCase = true;
			benefitsCls.sDate = DateTime.NewInstance(benefitsCls.oEventObj.ActivityDate.year(), benefitsCls.oEventObj.ActivityDate.month(), benefitsCls.oEventObj.ActivityDate.day()).format('MM/dd/YYYY');
			if ((VOB_TERMEDDATE_HUM <> benefitsCls.sPolEndDate && benefitsCls.oEventObj.ActivityDate > Date.parse(benefitsCls.sPolEndDate))
				|| benefitsCls.oEventObj.ActivityDate < Date.parse(benefitsCls.objPolMem.Effective_Date__c))
			{
				benefitsCls.bSerDateGreater = true;
			}
			else
			{
				benefitsCls.bSerDateGreater = false;
			}
		}
		else
		{
			benefitsCls.benableCase = false;
		}
	}

	/**
	* Returns responses of continuation  method
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
	* @return                 Object
	*/
	public static Object getBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate, Boolean isCAS, string ADACode)
	{
		try
		{
			HUM_Webservice_Callout__c PBEServiceDetails = HUM_Webservice_Callout__c.getValues('ProductBusinessEntity_V2');
			HttpRequest httpPBEReqObj = builtHttpRequest(PBEServiceDetails);
			httpPBEReqObj.setBody(JSON.serialize(BenefitsV2_D_HUM.RequestStructureBenefitSearch(sServiceCategory, sTypeOfService, sPlaceOfService, sPar, platformCode, productKey, refdate, isCAS, ADACode)));
			integer iTIMEOUT_INT_SECS = HUMServiceTimeOut;
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
			con.continuationMethod = VOB_BENEFITSEARCH_PROCESSCALL_HUM;
			con.addHttpRequest(httpPBEReqObj);
			con.state = sPar;
			return con;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'BenefitsV2_S_HUM', 'getBenefitSearch');
			return null;
		}
	}

	/**
	* Returns the response for Benefit Search
	* <p>
	* Method to Parse the Benefit Search response .
	*
	* @param benefitsCls          Object State
	* @return                     Object
	*/
	public Static Object processBenefitSearch(List<string> labels, Object State)
	{
		try
		{
			HttpResponse responsePBE = Continuation.getResponse(labels[0]);
			String PARValue = (String)State;
			if (responsePBE != null && (responsePBE.getStatusCode() == HTTP_OK || responsePBE.getStatusCode() == HTTP_ACCEPTED))
			{
				ProductBusinessEntityResponseV2_H_HUM oResponse = (ProductBusinessEntityResponseV2_H_HUM)System.JSON.deserialize(responsePBE.getBody(), ProductBusinessEntityResponseV2_H_HUM.class);
				PBEBenefitsSearch_DTO_HUM benefitsSearchDTOobj = BenefitsV2_D_HUM.getBenefitsSearchDTO(oResponse, PARValue);
				return benefitsSearchDTOobj;
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'BenefitsV2_S_HUM', 'processBenefitSearch');
			return null;
		}
	}

	/**
	* Returns the Continuation object
	* <p>
	* Method to get Paid thru date from Billing Profile serice .
	*
	* @param String          sNetworkID
	* @param String          sSearchID
	* @param String          sPlatform
	* @return                Object
	*/
	public Static Continuation processBillingProfiles(String sNetworkID, String sSearchID, String sPlatform, String sExchangeType, string sProductType, string sProduct,Boolean bSwitchForNewPBS)
	{
		HUM_Webservice_Callout__c oBillingServiceDetails = HUM_Webservice_Callout__c.getValues('BillingServiceV2_GetBillingAccountInfo');
		// Create continuation with a timeout
		integer iTIMEOUT_INT_SECS = Integer.valueof(oBillingServiceDetails.Timeout__c) / 1000;
		Continuation con = new Continuation(iTIMEOUT_INT_SECS);
		con.continuationMethod = 'parseBillingProfileResponse';
		HttpRequest httpBillingReqObj = builtHttpRequest(oBillingServiceDetails);
		httpBillingReqObj.setBody(JSON.serialize(BenefitsV2_D_HUM.RequestStructureBillingService(sNetworkID, sPlatform, sSearchID, sExchangeType,sProductType,sProduct,bSwitchForNewPBS)));
		con.addHttpRequest(httpBillingReqObj);
		return con;
	}

	/**
	* Returns the response of Billing Profile
	* <p>
	* Method to Parse the Billing Profile response .
	*
	* @param                      Object State
	* @return                     Object
	*/
	public Static Object parseBillingProfileResponse(List < string > labels, Object State)
	{
		HttpResponse oHttpResponse = Continuation.getResponse(labels[0]);
		String sPaidThoughDate;
		if (oHttpResponse != null && ((oHttpResponse.getStatusCode() == HTTP_OK_HUM || oHttpResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!oHttpResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
		{
			BillingProfileResponseV2_DTO_HUM billingResponse = (BillingProfileResponseV2_DTO_HUM)System.JSON.deserialize(oHttpResponse.getBody(), BillingProfileResponseV2_DTO_HUM.class);
			sPaidThoughDate = BenefitsV2_D_HUM.ParseStructureBillingService(billingResponse);
		}
		return sPaidThoughDate;
	}
	
	/**
  * Returns the Continuation object
  * <p>
  * Method to get Paid thru date from Billing Profile serice .
  *
  * @param String          sMemberId
  * @param String          sMemberDependentCode
  * @return                Object
  */
  public Static Continuation callToothHistoryService(String sMemberId, String sMemberDependentCode)
  {
    HUM_Webservice_Callout__c oToothHistoryServiceDetails = HUM_Webservice_Callout__c.getValues('ToothHistory_GetToothHistory');
    // Create continuation with a timeout
    integer iTIMEOUT_INT_SECS = Integer.valueof(oToothHistoryServiceDetails.Timeout__c) / 1000;
    Continuation con = new Continuation(iTIMEOUT_INT_SECS);
    con.continuationMethod = 'parseToothHistoryResponse';
    HttpRequest httpToothHistoryReqObj = builtHttpRequest(oToothHistoryServiceDetails);
    httpToothHistoryReqObj.setBody(JSON.serialize(BenefitsV2_D_HUM.RequestToothHistoryService(sMemberId, sMemberDependentCode)));
    con.addHttpRequest(httpToothHistoryReqObj);
    return con;
  }

    /**
    * Returns the response of Tooth History Profile
    * <p>
    * Method to Parse the Tooth History response .
    *
    * @param                      Object State
    * @return                     Object
    */
    public Static Object parseToothHistoryResponse(List < string > labels, Object State)
    {
        HttpResponse oHttpResponse = Continuation.getResponse(labels[0]);
        if (oHttpResponse != null && ((oHttpResponse.getStatusCode() == 200 || oHttpResponse.getStatusCode() == 202) && (!oHttpResponse.getBody().containsIgnoreCase('faultcode')) || Test.isRunningTest()))
		{
            ToothServiceResponse_H_HUM ToothHistoryResponse = (ToothServiceResponse_H_HUM)System.JSON.deserialize(oHttpResponse.getBody(), ToothServiceResponse_H_HUM.class);
            return BenefitsV2_D_HUM.ParseToothHistoryRes(ToothHistoryResponse );
        }
        return null;
    }
}