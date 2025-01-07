/*****************************************************************************************************
 Apex Class Name  : UserAssociatedInformation_D_HUM 
 Version   : 1.0 
 Created Date  : 10/22/2018  

 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Suraj patil                299668           10/22/2018                   Original version
* Suraj Patil		               		      10/30/2018         		   REQ - 350626 - Power of attorney display from designee service.
* Sumit Pharande			 313567			  11/26/2018				   Changing DateTime logic to Date as we do not require time as we are using string and removing GMT as date is getting less by 1 day(12/31/9999)
* Suraj patil                		          11/28/2018                   Defect -392858 fix.
* Praveen Kumar Parimi                        04/04/2019  				   REQ - 393077 aka CA Ticket 8138421 - Reading the Member not found from DTO instead of reading it as fault.                 
* Santhi Mandava                              04/15/2019                   Replaced "Member_Id_Value__c" with Name
* Suraj Patil             		              02/06/2020           		   REQ - 852426 - CA Tciket - 8454688 -  Designee domain for DesigneeServiceMPG service
* Raja Sekhar Reddy M V                     10/28/2020                 User Story 1609149: PR00094816 - MF 5 - DEV - Add Go365 Medicaid Guardian designation form to Associated Forms and Information Section of Person Acct Page in CRM Service
* Ajay Chakradhar                             05/05/2021	               REQ- 2184294 - Replacing constants with Consumer Management Constants
*******************************************************************************************************/

public with sharing class UserAssociatedInformation_D_HUM {
    
    Private Static String sGo365_Medicaid_Guardian = 'Go365 Medicaid Guardian';
    Private Static String sAOR= 'Appointment of Representative';
    Private Static String sPower_Of_Attorney = 'Power of Attorney';
    Private Static String sConsent_for_PHI= 'Consent for PHI';
    Private Static String sPOA_VALIDMEMBERTYPES = 'Member-Id-Base;';
    Private Static String sPOA_VALIDPLATFORMS = 'LV;';
    Private Static String sHUM_NO_ACTIVERECORD = 'No active record found';
      
    /**
    *  Call DesingeeHubService Method for PHIConsentInfo
    *  @name callServicePHIConsentInfo
    *  @param String - sAccountId
    *  @return DesigneeHubService_Request_DTO
    */
    public static Continuation callServicePHIConsentInfo(String sAccountId, string sEnterpriseId)
    {
        try
        {
            HUM_Webservice_Callout__c serviceDetails = HUM_Webservice_Callout__c.getValues('HUMGetPHIConsentInfo');
            integer iTIMEOUT_INT_SEC = Integer.valueof(serviceDetails.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SEC);
            con.continuationMethod = 'getPHIResponse';
            con.state = sAccountId;
            HttpRequest httpEditGetMemberReqObj = builtHttpRequest(serviceDetails);
            httpEditGetMemberReqObj.setBody(JSON.serialize(RequestStructureforDesignee(sAccountId, sEnterpriseId)));
            String strResponse = con.addHttpRequest(httpEditGetMemberReqObj);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_D_HUM', 'callServicePHIConsentInfo');
            return null;
        }
    }
    
    /**
    *  Create service request for DesingeeHubService
    *  @name builtHttpRequest
    *  @param Object of HUM_Webservice_Callout__c 
    *  @return HttpRequest 
    */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        try
        {
            HttpRequest request = new HttpRequest();
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, ServiceObj.Content_Type__c);
                request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM, GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM,'');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
       }
       catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_D_HUM', 'builtHttpRequest');
            return null;
        }
    }
    
    /**
    *  Create Input request for DesingeeHubService
    *  @name RequestStructureforDesignee
    *  @param String  sAccountId
    *  @return DesigneeHubService_Request_DTO
    */
    public static DesigneeHubService_Request_DTO RequestStructureforDesignee(String sAccountId, String sEnterpriseId)
    {
        try
        {
            DesigneeHubService_Request_DTO oRequest = new DesigneeHubService_Request_DTO();
            DesigneeHubService_Request_DTO.GetPHIConsentRequest oPHIConsentRequest= new DesigneeHubService_Request_DTO.GetPHIConsentRequest();
            DesigneeHubService_Request_DTO.POAInfo oPOAInfo;
             
            Set<string> setValidMemTypes = new set<string>(sPOA_VALIDMEMBERTYPES.split(';'));  
            oPHIConsentRequest.POAInfo = new List<DesigneeHubService_Request_DTO.POAInfo>();
            List<Policy_Member__c> lstPM = new List<Policy_Member__c>();
            
           oPHIConsentRequest.EnterpriseId = sEnterpriseId;
            
            lstPM =  checkConditionForPOA(sAccountId);
            if(lstPM != null && lstPM.size()>0)
            {
                oPHIConsentRequest.IncludeCIPOA ='Y';
                for(Policy_Member__c oPM : lstPM)
                {
                   oPOAInfo = new DesigneeHubService_Request_DTO.POAInfo();
                   
                    oPOAInfo.MemberCardID  ='';
                       oPOAInfo.MemberCardID = oPM.Member_Ids__r[0].Name;
                       
                    If(!String.isBlank(oPM.Policy__r.Source_Cust_Cov_Key__c) && oPM.Policy__r.Source_Cust_Cov_Key__c.length() >= 9)
                    {
                        oPOAInfo.GroupID = oPM.Policy__r.Source_Cust_Cov_Key__c.subString(0, 6);
                        oPOAInfo.BenefitSequenceNumber = oPM.Policy__r.Source_Cust_Cov_Key__c.subString(6, 9);
                    }
                    oPHIConsentRequest.POAInfo.add(oPOAInfo);
                }
            }
            else 
            {
                oPHIConsentRequest.IncludeCIPOA ='N';
            }
            List<User> currentUser = [Select Network_User_Id__c from User where ID=:userinfo.getuserID() ];
            oPHIConsentRequest.UserId = (currentUser != null ) ? currentUser[0].Network_User_Id__c : '';
            oPHIConsentRequest.RequestedDateTime = String.valueof(Datetime.now().format('yyyy-MM-dd\'T\'HH:mm:ss\'+\'hh:mm'));
            oRequest.GetPHIConsentRequest = oPHIConsentRequest;
            return oRequest;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_D_HUM', 'RequestStructureforDesignee');
            return null;
        }    
    }
   
    /**
    *  Parsing response from DesingeeHubService
    *  @name getPHIResponse
    *  @param State and Label
    *  @return list of UserInformation_DTO_HUM
    */
    public static object getPHIResponse(List < string > labels, Object State)
    {
        try{
            HttpResponse sResponse = (labels != null ) ? Continuation.getResponse(labels[0]) : null;
            if (sResponse != null && !sResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && ((sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                DesigneeHubService_Response_DTO objDesigneeHubResp = new DesigneeHubService_Response_DTO();
                objDesigneeHubResp = (DesigneeHubService_Response_DTO)System.JSON.deserialize(sResponse.getBody(), DesigneeHubService_Response_DTO.class);
                if (objDesigneeHubResp != null && objDesigneeHubResp.GetPHIConsentResponse!= null && objDesigneeHubResp.GetPHIConsentResponse.ResponseStatus != null && objDesigneeHubResp.GetPHIConsentResponse.ResponseStatus.Error != sHUM_NO_ACTIVERECORD)
                { 
                    List<UserInformation_DTO_HUM> lstUserInformation = new List<UserInformation_DTO_HUM>();
                    return lstUserInformation = processResponse(objDesigneeHubResp);
                }
            }
        }
        catch(Exception ex)
        {
        HUMExceptionHelper.logErrors(ex,'UserAssociatedInformation_D_HUM', 'getPHIResponse');
        }
        return null;
    }
    
    /**
    *  Parsing response from DesingeeHubService
    *  @name processResponse
    *  @param response object of DesigneeHubService_Response_DTO 
    *  @return list of UserInformation_DTO_HUM
    */
    public static list<UserInformation_DTO_HUM> processResponse(DesigneeHubService_Response_DTO resDesigneeHubService)
    {
        try
        {
            list<UserInformation_DTO_HUM> lstUserInformationDTO = new list<UserInformation_DTO_HUM>();
            DesigneeHubService_Response_DTO.GetPHIConsentResponse oDesigneeHubResponse = resDesigneeHubService.GetPHIConsentResponse;
            List<DesigneeHubService_Response_DTO.DesigneeList> lstDesignee = oDesigneeHubResponse.DesigneeList;
            DesigneeHubService_Response_DTO.RelationshipDetails oRelationship;
            DesigneeHubService_Response_DTO.Demographics oDemographics;
            DesigneeHubService_Response_DTO.GuardianAddress oGuardianAddress;
			DesigneeHubService_Response_DTO.PersonofAuthority oPersonofAuthority;
			DesigneeHubService_Response_DTO.Phone oPhone;
            DesigneeHubService_Response_DTO.POA oPOA;
			Date terminationDate;
            UserInformation_DTO_HUM userInfo;
            for (DesigneeHubService_Response_DTO.DesigneeList objListDesignee : lstDesignee )
            {
				oRelationship = new DesigneeHubService_Response_DTO.RelationshipDetails();
				oDemographics = new DesigneeHubService_Response_DTO.Demographics();
				oGuardianAddress = new DesigneeHubService_Response_DTO.GuardianAddress();
				oPersonofAuthority = new DesigneeHubService_Response_DTO.PersonofAuthority();
				oPhone = new DesigneeHubService_Response_DTO.Phone();
                oPOA = new DesigneeHubService_Response_DTO.POA();
				
				oRelationship = objListDesignee.RelationshipDetails;
				oDemographics = objListDesignee.Demographics;
				oGuardianAddress = oDemographics.GuardianAddress;
				oPersonofAuthority = oDemographics.PersonofAuthority;
				oPhone = oDemographics.Phone;
                oPOA = oRelationship.POA;
				terminationDate = null;
				userInfo = new UserInformation_DTO_HUM();
				if(oRelationship != null)
				{
					userInfo.DocumentType = (string.isNotBlank(oRelationship.DocumentType) && oRelationship.DocumentType != null)? oRelationship.DocumentType:'';
					if((string.isNotBlank(oRelationship.EffectiveDate))){
						    Date EffectiveDate =Date.valueOf(oRelationship.EffectiveDate); 
							userInfo.EffectiveDate = EffectiveDate.format();
						}

					if((string.isNotBlank(oRelationship.TerminationDate))){
							terminationDate = Date.valueOf(oRelationship.TerminationDate);
							userInfo.TerminationDate = TerminationDate.format();
						}

					userInfo.LevelOfConsent = (string.isNotBlank(oRelationship.LevelOfConsent)) ? oRelationship.LevelOfConsent : '';
					if(userInfo.LevelOfConsent == 'LimitedDisclosure' )
					{
						if((string.isNotBlank(oRelationship.ServiceStartDate))){
							Date ServiceStartDate = Date.valueOf(oRelationship.ServiceStartDate);
							userInfo.ServiceStartDate = ServiceStartDate.format();
						}else userInfo.ServiceStartDate ='';
						
						if((string.isNotBlank(oRelationship.ServiceEndDate))){
							Date ServiceEndDate = Date.Valueof(oRelationship.ServiceEndDate);
							userInfo.ServiceEndDate = ServiceEndDate.format();
						}else userInfo.ServiceEndDate ='';
						
					userInfo.LimitedDisclosureDetails = (string.isNotBlank(oRelationship.LimitedDisclosureDetails)) ? oRelationship.LimitedDisclosureDetails : '';
					userInfo.LineOfCoverage = (string.isNotBlank(oRelationship.LineOfCoverage)) ? oRelationship.LineOfCoverage : '';
					}
					userInfo.Relationship = (string.isNotBlank(oRelationship.Relationship)) ? oRelationship.Relationship : '';
					userInfo.SubmissionMehtod = (string.isNotBlank(oRelationship.SubmissionMehtod)) ? oRelationship.SubmissionMehtod : '';
				}
				 if ((userInfo.DocumentType.equalsIgnoreCase(sGo365_Medicaid_Guardian)
                        && (terminationDate == null || terminationDate > System.today()))
                        || (!userInfo.DocumentType.equalsIgnoreCase(sGo365_Medicaid_Guardian) && 
                        !userInfo.DocumentType.equalsIgnoreCase(sAOR))) 
                {					
					if(oPersonofAuthority != null)
					{
						string personofAuth = (string.isNotBlank(oPersonofAuthority.FirstName)) ? oPersonofAuthority.FirstName+' ' : '';
						personofAuth += (string.isNotBlank(oPersonofAuthority.MiddleName)) ? oPersonofAuthority.MiddleName+' ' : '';
						personofAuth += (string.isNotBlank(oPersonofAuthority.LastName)) ? oPersonofAuthority.LastName : '';
						userInfo.PersonofAuthority = personofAuth;
					}
					
					if(oGuardianAddress!= null)
					{
						string addressG = (string.isNotBlank(oGuardianAddress.AddressLineOne)) ? oGuardianAddress.AddressLineOne+', ' : '';
						addressG += (string.isNotBlank(oGuardianAddress.AddressLineTwo)) ? oGuardianAddress.AddressLineTwo+', ' : '';
						addressG += (string.isNotBlank(oGuardianAddress.City)) ? oGuardianAddress.City+', ' : '';
						addressG += (string.isNotBlank(oGuardianAddress.State)) ? oGuardianAddress.State+', ' : '';
						addressG += (string.isNotBlank(oGuardianAddress.Zip)) ? oGuardianAddress.Zip : '';
						userInfo.GuardianAddress = addressG  ;
					}
					
					 if(string.isNotBlank(oRelationship.DocumentType) && oRelationship.DocumentType == sPower_Of_Attorney)
					{
						userInfo.PhoneNumber = (oPhone != null && string.isNotBlank(oPhone.PhoneNumber)) ? oPhone.PhoneNumber : '';
						userInfo.FirstName = (string.isNotBlank(oPersonofAuthority.FirstName)) ? oPersonofAuthority.FirstName:'';
						userInfo.LastName = (string.isNotBlank(oPersonofAuthority.LastName)) ? oPersonofAuthority.LastName : '';
						userInfo.Type = (oPOA != null && string.isNotBlank(oPOA.Type)) ? oPOA.Type : '';
					}
						
					lstUserInformationDTO.add(userInfo);
				}
			}	
            Map<String,UserInformation_DTO_HUM> userInformationMap = new Map<String,UserInformation_DTO_HUM>();
            Map<String,UserInformation_DTO_HUM> userInformationMapSorted = new Map<String,UserInformation_DTO_HUM>();
            List<String> orderingList = new List<String>();
            List<UserInformation_DTO_HUM> sortedUserInformationList = new List<UserInformation_DTO_HUM>();
           
           Integer count = 1; 
            for (UserInformation_DTO_HUM objUserInfo: lstUserInformationDTO){
				if(!objUserInfo.DocumentType.equalsIgnoreCase(sConsent_for_PHI))
                {
                userInformationMap.put(objUserInfo.DocumentType+objUserInfo.PersonofAuthority.toUpperCase()+count ,objUserInfo);
                 }
                else
                {
                    userInformationMap.put(count+objUserInfo.DocumentType+objUserInfo.PersonofAuthority.toUpperCase(),objUserInfo);
                }
				count++;
			}
            //Add all elements of Set into a list
            orderingList.addAll(userInformationMap.keyset());
            //Sort the List
            orderingList.sort();
            for ( Integer i= 0;i < orderingList.size(); i++){
			            userInformationMapSorted.put(orderingList[i],userInformationMap.get(orderingList[i]));
            }
            sortedUserInformationList = userInformationMapSorted.values();
			
			//To keep password on 1 index
            List<UserInformation_DTO_HUM> lstFinalOrderedList = new List<UserInformation_DTO_HUM>();
            Integer k=2;
            for (UserInformation_DTO_HUM objListDesignee : sortedUserInformationList )
            {               
                objListDesignee.IndexNumber = k;
                lstFinalOrderedList.add(objListDesignee);
                k++;
            }
            return lstFinalOrderedList ;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_D_HUM', 'processResponse');
        }   return null;     
    }   
    
    /**
    *  Getting POA in formation to Call service
    *  @name checkConditionForPOA
    *  @param string accountid
    *  @return list of Policy_Member__c
    */   
    public static list<Policy_Member__c> checkConditionForPOA(String sAccountId)
    {
        try
        {
            list<Policy_Member__c> lstPM = new list<Policy_Member__c>();
            Set<string> setValidMemTypes = new set<string>(sPOA_VALIDMEMBERTYPES.split(';'));            
            Set<string> setValidPlatforms = new Set<string>(sPOA_VALIDPLATFORMS.split(';'));
            lstPM = [Select Id, Name, Policy__r.Source_Cust_Cov_Key__c, 
                    (Select  Name From Member_Ids__r Where Type__c IN : setValidMemTypes) 
                    From Policy_Member__c
                    Where Member__c =: sAccountId AND Policy__r.Platform__c IN : setValidPlatforms];
                       
            for(Integer i = 0; i < lstPM.size() ; i++)
            {
                 if(lstPM.get(i).Member_Ids__r.isEmpty() || string.isBlank(lstPM.get(i).Member_Ids__r[0].Name) )
                {
                    lstPM.remove(i);
                    i--;
                }          
            }
            if(lstPM != null && !(lstPM.isEmpty())) return lstPM;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'UserAssociatedInformation_D_HUM', 'checkConditionForPOA');
        }
        return null;
    }  
}