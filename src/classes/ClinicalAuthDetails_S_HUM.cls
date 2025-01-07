/*****************************************************************************************
Apex class Name    : ClinicalAuthDetails_S_HUM
Version            : 1.0
Function           : This service class is used to request Clinical Authorization details 
Created On         : Dec 15 2015
Test Class         : ClinicalAuthorizationDetailService_T_HUM 

Modification Log:
*   Developer                   Code Review           Date               Description
* ------------------------------------------------------------------------------------------------------------------------------ 
*  Prasanthi Kandula            29039                 12/14/2015         Original Version 
*  Ranjeeth Nagishetty          29646                 02/01/2016         updated the code review comments
*  Manish Kumar                 26846                 05/04/2016         Included logging changes. 
*  Santhi Mandava               27191                 05/11/2016         Removed hard coded birth date in assignValuesToHelper method
*  Praveen Kumar Parimi         28299                 05/24/2016         Modified the formula to add 1 to the number of days.
*  Santhi Mandava               30830                 06/21/2016         Modified "retrieveProviderDetails" method to include "Select Service" address.
*  Kritika Popat                30830                 06/22/2016         Defect 225594.0001 related to address of provider
*  Manish Kumar                 31507                 06/30/2016         fixed defect#231998.0001.
*  Santhi Mandava               50212                 12/01/2016         Implemented Req - 290971. "Total Days" field value is being calculated from platform side.
                                                                            Now we have removed this calculation part by using "TotalDaysApproved".
*  Imran                        416071                12/03/2019         Implemented the ability to view additional fields on the Authorization/Referral Details page - Inpatient&Outpatient Authorization Details view and validate data in Authorization summary
*  Prasanthi Kandula                                  12/27/2019          REQ - 416086 Added communication records section on details page.   
*  Imran                                              12/30/2019          REQ - 416096 Added Letters records section on details page.
*  Imran                                              01/22/2020          REQ - 416096 Modified the filepath logic for Letters section.
*  Prasanthi Kandula                                  07/10/2020          Defect Fix  1037 - Details column on communication section is not mapped correctly   
* Rahul Chaurasia									  04/06/2021			 User Story 1279935: PR00094226 - SF- Auth/Referral Summary old version Decommission
********************************************************************************************************************************/

public with sharing class ClinicalAuthDetails_S_HUM
{
   private AuthReferral_Query_D_HUM oAuthReferral = new AuthReferral_Query_D_HUM();
   public Map<String, Map<String, Object>> standardMapForLogging = new Map<String, Map<String, Object>>();
  
   /**
    *  This method is used to assign values to the request helper class
    *  @name assignValuesToHelperV2
    *  @param String, String - Selected authorization record id, selected object id
    *  @return ClinicalAuthDetailsRequest_H_HUMV2 - Auth detail service request
    *  @throws NA
    */
    
    public ClinicalAuthDetailsRequest_H_HUM_V2 assignValuesToHelperV2(string sAuthId, string sEnterpriseID, string sDateOfBirth)
    {
        ClinicalAuthDetailsRequest_H_HUM_V2 oclinicalAuthdetails = new ClinicalAuthDetailsRequest_H_HUM_V2 ();     
        ClinicalAuthDetailsRequest_H_HUM_V2.AuthDetailRequest oAuthReqHum = new ClinicalAuthDetailsRequest_H_HUM_V2.AuthDetailRequest();     
        ClinicalAuthDetailsRequest_H_HUM_V2.PageMetaData_Communication oPageMetaData = new ClinicalAuthDetailsRequest_H_HUM_V2.PageMetaData_Communication();
        ClinicalAuthDetailsRequest_H_HUM_V2.Filter_Communication oFilterComm = new ClinicalAuthDetailsRequest_H_HUM_V2.Filter_Communication();
        ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Communication oSortDetailComm = new ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Communication();
        List<ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Communication> lstSortDetailComm = new List<ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Communication>();
        ClinicalAuthDetailsRequest_H_HUM_V2.PageMetaData_Letters oPageMetaDataLetters = new ClinicalAuthDetailsRequest_H_HUM_V2.PageMetaData_Letters();
        ClinicalAuthDetailsRequest_H_HUM_V2.Filter_Letters oFilterLetters = new ClinicalAuthDetailsRequest_H_HUM_V2.Filter_Letters();
        ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Letters oSortDetailLetters = new ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Letters();
        List<ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Letters> lstSortDetailLetters = new List<ClinicalAuthDetailsRequest_H_HUM_V2.SortDetail_Letters>();

        
        
        if(String.isNotBlank(sAuthId) )
        {       
            oAuthReqHum.Authid = sAuthId; 
            oAuthReqHum.MemberId = sEnterpriseID;
            oAuthReqHum.MemberIdType = GLOBAL_CONSTANT_HUM.HTTPREQUEST_PERSONID_HUM;
            oAuthReqHum.DateOfBirth = sDateOfBirth;           
			oPageMetaData.PageSize = 50;
			oPageMetaData.PageNumber = 1;
			oAuthReqHum.PageMetaData_Communication = oPageMetaData;
			oAuthReqHum.Filter_Communication = oFilterComm;
			oAuthReqHum.SortDetail_Communication = lstSortDetailComm;   
			oPageMetaDataLetters.PageSize = 50;
			oPageMetaDataLetters.PageNumber = 1;
			oAuthReqHum.PageMetaData_Letters = oPageMetaDataLetters;
			oAuthReqHum.Filter_Letters  = oFilterLetters;
			oAuthReqHum.SortDetail_Letters =lstSortDetailLetters;	   
            oclinicalAuthdetails.AuthDetailRequest= oAuthReqHum;
        }
        return oclinicalAuthdetails;     
    }
  
   /**
    *  This method is used to generate the service request
    *  @name setHttpRequest
    *  @param HttpRequest , HUM_Webservice_Callout__c,String  - Request object,
                             custom setting record for auth detail service,request body
    *  @return HttpRequest - Updated request object
    *  @throws NA
    */
   public HttpRequest setHttpRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcClinicalAuthDetail, String sRequestBody){
        oRequest.setMethod(oSvcClinicalAuthDetail.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcClinicalAuthDetail.End_Point_URL__c);
        if(!Test.isRunningTest()){ oRequest.setClientCertificateName(oSvcClinicalAuthDetail.Certificate_Name__c); }
        oRequest.setHeader(Label.HUMServiceTesterController_content_type, oSvcClinicalAuthDetail.Content_Type__c);
        return oRequest;
    }
  
   /**
    *  This method is used to retreive Authorization Details from response
    *  @name retrieveAuthDetail
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.Authorization> - List of auth details received from the service
    *  @return ClinicalAuthDetailsWrapper_DTO_HUM - Wrapper class object which will be used to display auth details.
    *  @throws NA
    */ 
  public ClinicalAuthDetailsWrapper_DTO_HUM retrieveAuthDetail(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthDetail)
   {
      ClinicalAuthDetailsWrapper_DTO_HUM oAuthDetailsWrapper = new ClinicalAuthDetailsWrapper_DTO_HUM();
      if(lstAuthDetail != null && !lstAuthDetail.isEmpty())
      {
          oAuthDetailsWrapper.sAuthID = lstAuthDetail[0].AuthID;
          oAuthDetailsWrapper.sAuthStatus = lstAuthDetail[0].AuthStatus;
          standardMapForLogging.put('AuthStatus', new Map<String, Object>{'Overall Status' => oAuthDetailsWrapper.sAuthStatus});
          
          if(String.isNotBlank(lstAuthDetail[0].CreatedDate))    oAuthDetailsWrapper.sCreatedDate = formateDate(lstAuthDetail[0].CreatedDate);
          if(String.isNotBlank(lstAuthDetail[0].NextReviewDate))    oAuthDetailsWrapper.sNextReviewDate = formateDate(lstAuthDetail[0].NextReviewDate);  
          oAuthDetailsWrapper.sContactMethod = lstAuthDetail[0].ContactMethod;
          oAuthDetailsWrapper.sRequestType = lstAuthDetail[0].RequestType;
          oAuthDetailsWrapper.sAuthType = lstAuthDetail[0].AuthType;
          oAuthDetailsWrapper.sDenialReason   = lstAuthDetail[0].DenialReason;
          oAuthDetailsWrapper.sClaimsPaymentNotes = lstAuthDetail[0].ClaimPaymentNotes;                    
          standardMapForLogging.put('Auth Type', new Map<String, Object>{'Auth Type' => lstAuthDetail[0].AuthType});
          if(oauthDetailsWrapper.sAuthType == GLOBAL_CONSTANT_HUM.AUTHTYPE_BHINP_HUM || oauthDetailsWrapper.sAuthType == GLOBAL_CONSTANT_HUM.AUTHTYPE_INP_HUM)
          {
              standardMapForLogging.put('AuthID', new Map<String, Object>{'Auth/Referral #' => oAuthDetailsWrapper.sAuthID});
          }
          else
          {
              standardMapForLogging.put('AuthID', new Map<String, Object>{'Auth/Referral #' => oAuthDetailsWrapper.sAuthID});
          }
          oAuthDetailsWrapper.sContactMethod = lstAuthDetail[0].ContactMethod; 
          oAuthDetailsWrapper.sIsReferral  = lstAuthDetail[0].IsReferral; 
       }
       else 
       {
           oAuthDetailsWrapper = null;
       }
       return oAuthDetailsWrapper;
   }  
   
   /**
    *  This method is used to retreive Referral Information for InPatient from response
    *  @name retrieveReferralInformationIP
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.Authorization> - List of auth details received from the service
    *  @return ClinicalAuthDetailsWrapper_DTO_HUM - Wrapper class object which will be used to display auth details.
    *  @throws NA
    */
   public ClinicalAuthDetailsWrapper_DTO_HUM retrieveReferralInformationIP(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthDetail)
   {
      ClinicalAuthDetailsWrapper_DTO_HUM oAuthDetailsWrapperIP = new ClinicalAuthDetailsWrapper_DTO_HUM();
      ClinicalAuthDetailsResponse_H_HUM.InpatientDetails oInpatient = lstAuthDetail[0].InpatientDetails;
      
      if(String.isNotBlank(oInpatient.AdmissionDate))
      {
          oAuthDetailsWrapperIP.sAdmissionDate = formateDate(oInpatient.AdmissionDate);
          standardMapForLogging.put('AdmissionDate', new Map<String, Object>{'Admission Date' => oAuthDetailsWrapperIP.sAdmissionDate});
      }
      
      oAuthDetailsWrapperIP.sAdmissionType = oInpatient.AdmissionType;
      
      if(String.isNotBlank(oInpatient.LastCoveredDate))  oAuthDetailsWrapperIP.sLastCoveredDate = formateDate(oInpatient.LastCoveredDate);
      
      if(String.isNotBlank(oInpatient.DischargeDate))
      {
          oAuthDetailsWrapperIP.sDischargeDate = formateDate(oInpatient.DischargeDate);
          standardMapForLogging.put('DischargeDate', new Map<String, Object>{'Discharge Date' =>  oAuthDetailsWrapperIP.sDischargeDate});
      }
       
      oAuthDetailsWrapperIP.sTotalDaysApproved = oInpatient.TotalDaysApproved;
      return oAuthDetailsWrapperIP;
   }
   
   /**
    *  This method is used to retreive Referral Information for OutPatient from response
    *  @name retrieveReferralInformationOP
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.Authorization>  - List of auth details received from the service
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> -  Wrapper class object which will be used to display auth details
    *  @throws NA
    */
   public ClinicalAuthDetailsWrapper_DTO_HUM retrieveReferralInformationOP(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthDetail)
   {
      
      ClinicalAuthDetailsWrapper_DTO_HUM oAuthDetailsWrapperOP = new ClinicalAuthDetailsWrapper_DTO_HUM();
      ClinicalAuthDetailsResponse_H_HUM.OutpatientDetails oOutpatient = lstAuthDetail[0].OutpatientDetails;
      oAuthDetailsWrapperOP.sServiceType = oOutpatient.ServiceType;
      standardMapForLogging.put('Service Type', new Map<String, Object>{'Service Type' =>  oAuthDetailsWrapperOP.sServiceType});
      
      if(String.isNotBlank(oOutpatient.FirstDay))
      {
          oAuthDetailsWrapperOP.sFirstDay = formateDate(oOutpatient.FirstDay);
          standardMapForLogging.put('FirstDay', new Map<String, Object>{'First Day' =>  oAuthDetailsWrapperOP.sFirstDay});
      }
      if(String.isNotBlank(oOutpatient.LastDay))
      {
          oAuthDetailsWrapperOP.sLastDay = formateDate(oOutpatient.LastDay);
          standardMapForLogging.put('LastDay', new Map<String, Object>{'Last Day' =>  oAuthDetailsWrapperOP.sLastDay});
      }

      If(String.isNotBlank(oOutpatient.TotalDaysApproved) && oOutpatient.TotalDaysApproved.isNumeric())
      {
          oAuthDetailsWrapperOP.iTotalDays = Integer.ValueOf(oOutpatient.TotalDaysApproved);
      }
      return oAuthDetailsWrapperOP;
   }
      
   /**
    *  This method is used to retreive provider information for InPatient/OutPatient from response
    *  @name retrieveProviderDetails
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.Authorization>  - List of auth details received from the service
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of provider details
    *  @throws NA
    */
   public List<ClinicalAuthDetailsWrapper_DTO_HUM> retrieveProviderDetails(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthDetail)
     {
           List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProvider = new List<ClinicalAuthDetailsWrapper_DTO_HUM>(); 
           List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProvider1 = new List<ClinicalAuthDetailsWrapper_DTO_HUM>(3); 
           ClinicalAuthDetailsResponse_H_HUM.ProviderDetails oProviderDetail = lstAuthDetail[0].ProviderDetails;
           List<ClinicalAuthDetailsResponse_H_HUM.ProviderDetail> lstProviderDetail = oproviderdetail.ProviderDetail;
           for(ClinicalAuthDetailsResponse_H_HUM.ProviderDetail oProvider : lstProviderDetail)
           {
               ClinicalAuthDetailsWrapper_DTO_HUM oProv = new ClinicalAuthDetailsWrapper_DTO_HUM();
               ClinicalAuthDetailsResponse_H_HUM.AddressDetails oAddress = oProvider.AddressDetails;
               List<ClinicalAuthDetailsResponse_H_HUM.Address> lstAddress = oAddress.Address;
               if(lstAddress != null && !lstAddress.isEmpty())
               {
                  for(ClinicalAuthDetailsResponse_H_HUM.Address objAdd : lstAddress)
                  {
                      if(objAdd.AddressType == 'Select Service')
                      {
                          oProv.sReqProviderCity = objAdd.City; 
                          oProv.sReqProviderState = objAdd.State;
                          oProv.sReqProviderPhnNum = objAdd.PhoneNumber;
                          if(string.isBlank(objAdd.ZipCode_ext))
                          {
                              oProv.sReqProviderZipCode = objAdd.ZipCode;
                          }
                          else
                          {
                              oProv.sReqProviderZipCode = objAdd.ZipCode+'-'+objAdd.ZipCode_ext;
                          }
                          oProv.sReqProviderStreet = objAdd.Street_1+' ' +objAdd.Street_2; 
                          break;
                      }
                  }
              }
              oProv.sReqProviderID = oProvider.Id;
              oProv.sReqProviderName = oProvider.Name;
              oProv.sReqProviderTaxID = oProvider.TaxID;
              oProv.sReqProviderPartStatus = oProvider.ParticipatingStatus;
              oProv.sReqProviderType = oProvider.Type;
              lstProvider.add(oProv);
              
              populateStandardMapForLoggingForProvider(oProv);
           }
           return assignProviderDetails(lstProvider);   
     }  

   /**
    *  This method is used to populate standard map which will be used in logging.
    *  @name populateStandardMapForLoggingForProvider
    *  @param ClinicalAuthDetailsWrapper_DTO_HUM  - Selected auth detail record.
    *  @return Void
    *  @throws NA
    */
    @testVisible private void populateStandardMapForLoggingForProvider(ClinicalAuthDetailsWrapper_DTO_HUM oProv) 
    {
        if(oProv.sReqProviderType.containsIgnoreCase('RequestingProvider'))
        {
            standardMapForLogging.put('Requesting Provider Name', new Map<String, Object>{'Requesting Provider' => oProv.sreqProviderName});
            standardMapForLogging.put('Requesting Provider TaxId', new Map<String, Object>{'Requesting Provider TaxId' =>  oProv.sReqProviderTaxID});
            standardMapForLogging.put('Requesting Provider Id', new Map<String, Object>{'Requesting Provider Id' => oProv.sReqProviderID});
        }
        else if(oProv.sReqProviderType.containsIgnoreCase('TreatingProvider'))
        {
            standardMapForLogging.put('Treating Provider Name', new Map<String, Object>{'Treating Provider' => oProv.sreqProviderName});
            standardMapForLogging.put('Treating Provider TaxId', new Map<String, Object>{'Treating Provider TaxId' =>  oProv.sReqProviderTaxID});
            standardMapForLogging.put('Treating Provider Id', new Map<String, Object>{'Treating Provider Id' => oProv.sReqProviderID});
        }
        else if(oProv.sReqProviderType.containsIgnoreCase('Facility'))
        {
            standardMapForLogging.put('Facility Provider Name', new Map<String, Object>{'Facility' => oProv.sreqProviderName});
            standardMapForLogging.put('Facility Provider TaxId', new Map<String, Object>{'Facility Provider TaxId' =>  oProv.sReqProviderTaxID});
            standardMapForLogging.put('Facility Provider Id', new Map<String, Object>{'Facility Provider Id' => oProv.sReqProviderID});
        }
    }
    
   /**
    *  This method is used to assign provider type and sort the order by type
    *  @name assignProviderDetails
    *  @param List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of auth details received from the service
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of auth details received from the service
    *  @throws NA
    */ 
     public List<ClinicalAuthDetailsWrapper_DTO_HUM>  assignProviderDetails(List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProvider ) 
     {
       List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProvider1 = new List<ClinicalAuthDetailsWrapper_DTO_HUM>(3); 
       for(ClinicalAuthDetailsWrapper_DTO_HUM oprov : lstprovider)
           {
              if(oprov.sreqProviderType == GLOBAL_CONSTANT_HUM.AUTHDETAIL_REQUESTING_HUM+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM)
               {
                  oprov.sreqProviderType = GLOBAL_CONSTANT_HUM.AUTHDETAIL_REQUESTING_HUM+' '+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM;
                  lstprovider1.set(0,oprov);
               }
               else if(oprov.sreqProviderType == GLOBAL_CONSTANT_HUM.AUTHDETAIL_TREATING_HUM+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM)
               { 
                  oprov.sreqProviderType = GLOBAL_CONSTANT_HUM.AUTHDETAIL_TREATING_HUM+' '+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM; 
                  lstprovider1.set(1,oprov);  
               }
               else if(oprov.sreqProviderType == GLOBAL_CONSTANT_HUM.AUTHDETAIL_FACILITY_HUM)
               {
                   oprov.sreqProviderType = GLOBAL_CONSTANT_HUM.AUTHDETAIL_FACILITY_HUM+' '+GLOBAL_CONSTANT_HUM.AUTHDETAIL_PROVIDER_HUM;
                   lstprovider1.set(2,oprov);
               }
           } 
          return  lstprovider1;
     } 
       
   /**
    *  This method is used to retreive diagnosis codes for inpatient/outpatient from response
    *  @name retrieveDiagnosisCodes
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.Authorization> - Auth details received from service
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of diagnosis codes
    *  @throws NA
    */
     public List<ClinicalAuthDetailsWrapper_DTO_HUM> retrieveDiagnosisCodes(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthDetail) 
     {
         List<ClinicalAuthDetailsWrapper_DTO_HUM> lstDiagnosis = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
        if(lstAuthDetail != null && !lstAuthDetail.isEmpty())
        {
            ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetails oDiagnosisDetail = lstAuthDetail[0].DiagnosisDetails;
            List<ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetail> lstDiagnosisDetail = oDiagnosisDetail.DiagnosisDetail;
            for(ClinicalAuthDetailsResponse_H_HUM.DiagnosisDetail oDiagnosis : lstDiagnosisDetail)
            {
            ClinicalAuthDetailsWrapper_DTO_HUM oDiag = new ClinicalAuthDetailsWrapper_DTO_HUM();
            oDiag.sDiagnosisType = oDiagnosis.Type;
            oDiag.sDiagnosisCode = oDiagnosis.Code;
            oDiag.sDiagnosisDescription = oDiagnosis.Description;
            oDiag.sDiagnosisStatus = oDiagnosis.Status;
            lstDiagnosis.add(oDiag);
            }
        }
        return lstDiagnosis;
    }  
   
   /**
    *  This method is used to retrieve procedure codes for in patient from response
    *  @name retrieveProcedureCodes
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.Authorization> - Auth details received from service
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of procedure codes
    *  @throws NA
    */
    public List<ClinicalAuthDetailsWrapper_DTO_HUM> retrieveProcedureCodes(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthDetail)
    {
        List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProcedure = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
        If(lstAuthDetail != null && !lstAuthDetail.isEmpty())
        {
            ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails oProcedureDetail = lstAuthDetail[0].ProcedureDetails;
            List<ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail> lstProcedureDetail = oProcedureDetail.ProcedureDetail;
            for(ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail oProcedure : lstProcedureDetail)
            {
            ClinicalAuthDetailsWrapper_DTO_HUM oProcedureCode = new ClinicalAuthDetailsWrapper_DTO_HUM();
            oProcedureCode.sProcedureCode = oProcedure.Code;
            oProcedureCode.sProcedureDescription = oProcedure.Description;
            oProcedureCode.sProcedureStatus = oProcedure.Status;
            lstProcedure.add(oProcedureCode);
            }
        }
        return lstProcedure;
    } 
    
   /**
    *  This method is used to retrieve procedure codes for out patient from response
    *  @name retrieveProcedureCodesOutPatient
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.Authorization> - Auth details received from service
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of procedure codes
    *  @throws NA
    */
    public List<ClinicalAuthDetailsWrapper_DTO_HUM> retrieveProcedureCodesOutPatient(List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthDetail)
    {
        List<ClinicalAuthDetailsWrapper_DTO_HUM> lstProcedureOP = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
        If(lstAuthDetail != null && !lstAuthDetail.isEmpty())
        {
            ClinicalAuthDetailsResponse_H_HUM.ProcedureDetails oProcedureDetailOP = lstAuthDetail[0].ProcedureDetails;
            List<ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail> lstProcedureDetailOP = oproceduredetailOP.ProcedureDetail;
            for(ClinicalAuthDetailsResponse_H_HUM.ProcedureDetail oProcedureOP : lstProcedureDetailOP)
            {
                ClinicalAuthDetailsWrapper_DTO_HUM oProcedureCodeOP = new ClinicalAuthDetailsWrapper_DTO_HUM();
                oProcedureCodeOP.sProcedureCode = oProcedureOP.Code;
                oProcedureCodeOP.sProcedureDescription = oProcedureOP.Description;
                oProcedureCodeOP.sProcedureRequestingUnits = oProcedureOP.Visit.RequestedUnits;
                oProcedureCodeOP.sProcedureAuthorizedUnits = oProcedureOP.Visit.ApprovedUnits;
                oProcedureCodeOP.sProcedureTypeofUnits = oProcedureOP.Visit.TypeOfUnits;
                
                if(oProcedureCodeOP.sProcedureTypeofUnits.containsIgnoreCase('NUMBER OF VISITS'))
                standardMapForLogging.put('Number Of Visits', new Map<String, Object>{'Number Of Visits' => oProcedureCodeOP.sProcedureAuthorizedUnits});
                
                oProcedureCodeOP.sProcedureStatus = oProcedureOP.Status;
                lstProcedureOP.add(oProcedureCodeOP);
            }
        }
        return lstProcedureOP;
    } 
    
   /**
    *  This method is used to format the date into MM/dd/yyyy
    *  @name formateDate
    *  @param String - Datetime value in string format
    *  @return String  - Datetime value in MM/dd/yyyy format
    *  @throws NA
    */
    private string formateDate(String sDateTime)
    {
      String[] sArrayDate = (sDateTime.substringBefore('T')).split('-');
      String sDate = sArrayDate[1]+'/'+sArrayDate[2]+'/'+sArrayDate[0];
      return sDate;
    }

                  /**
    *  This method is used to parse communication records retrieved from service
    *  @name parseCommunicationRecords
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.CommunicationRecords> - List of communication records
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of communication records received from service
    *  @throws NA
    */ 
    public List<ClinicalAuthDetailsWrapper_DTO_HUM> parseCommunicationRecords(List<ClinicalAuthDetailsResponse_H_HUM.CommunicationRecords> lstCommRecords)
    {
        List<ClinicalAuthDetailsWrapper_DTO_HUM> lstCommunicationRecords = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
        if(lstCommRecords != null && !lstCommRecords.isEmpty())
        {
            for(ClinicalAuthDetailsResponse_H_HUM.CommunicationRecords oCommunication : lstCommRecords)
            {
                ClinicalAuthDetailsWrapper_DTO_HUM oComm = new ClinicalAuthDetailsWrapper_DTO_HUM();
                oComm.sRecordType = oCommunication.RecordType;
                oComm.sCommCreatedDate = oCommunication.CreatedDate;  
                oComm.sCommCreatedBy = oCommunication.CreatedBy;                
                if(oCommunication.LastModifiedDate != null) oComm.sCommLastModifiedDate = oCommunication.LastModifiedDate.format();
                oComm.sCommLastModifiedBy =  oCommunication.LastModifiedBy;
                oComm.sDepartment =  oCommunication.Department;
                oComm.sCommunicationType = oCommunication.CommunicationType;    
                oComm.sCommContactMethod = oCommunication.ContactMethod;  
                oComm.sContactType = oCommunication.ContactType;  
                oComm.sContactAttemptNo = oCommunication.ContactAttemptNo;
                string sContactResultTemp = '';
                if(oCommunication.ContactResult !=null && !oCommunication.ContactResult.isEmpty())
                {
                    for(String scntRes : oCommunication.ContactResult)    
                    {
                        sContactResultTemp = sContactResultTemp + scntRes + ';';
                    }
                }
                oComm.sContactResult = sContactResultTemp;
                oComm.sContactResultOther = oCommunication.ContactResultOther; 
                oComm.sNotes = oCommunication.Notes;  
                oComm.sDetails = oCommunication.Details; 
                lstCommunicationRecords.add(oComm);
            }
        }
        return lstCommunicationRecords;
     }
     
     
         /*
    *  This method is used to parse Letters records retrieved from service
    *  @name parseLettersRecords
    *  @param List<ClinicalAuthDetailsResponse_H_HUM.LettersRecords> - List of Letters records              
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM> - List of Letters records received from service
    *  @throws NA
    */
  
     public List<ClinicalAuthDetailsWrapper_DTO_HUM> parseLettersRecords(List<ClinicalAuthDetailsResponse_H_HUM.LettersRecords> lstLettersRecords)
     {
     
     List<ClinicalAuthDetailsWrapper_DTO_HUM> lstLetterRecords = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
    
        if(lstLettersRecords != null && !lstLettersRecords .isEmpty())
        {
            for(ClinicalAuthDetailsResponse_H_HUM.LettersRecords oLetter : lstLettersRecords)            
            {            
                ClinicalAuthDetailsWrapper_DTO_HUM oLetters = new ClinicalAuthDetailsWrapper_DTO_HUM(); 
                oLetters.bCheck = false;
                oLetters.sLettersType =   oLetter.LetterType;
                oLetters.sLettersAssociatedAuthID = oLetter.AssociatedAuthID;  
                oLetters.sLettersAssociatedCaseID = oLetter.AssociatedCaseID;
                oLetters.sLettersCreatedBy = oLetter.CreatedBy;                
                if(String.isNotBlank(oLetter.CreatedDate))    oLetters.sLettersCreatedDate = formateDate(oLetter.CreatedDate);  
                oLetters.sLettersTemplateName =  oLetter.TemplateName;
                oLetters.sLettersStatus = oLetter.Status;
                oLetters.sLettersFullfillmentStatus = oLetter.FullfillmentStatus;
                oLetters.sLettersP2POfferedinLetter = oLetter.P2POfferedinLetter;
                oLetters.sLettersLetterName = oLetter.LetterName;
                oLetters.sLettersVoidRemarks = oLetter.VoidRemarks;
                oLetters.sLettersLastModifiedBy = oLetter.LastModifiedBy;
                oLetters.sFilePath = oLetter.FilePath;                                
                if(oLetter.LastModifiedDate != null) oLetters.sLettersLastModifiedDate = oLetter.LastModifiedDate.format();
                lstLetterRecords.add(oLetters);
            }
        }  
        return lstLetterRecords;      
     } 

     
    /**
    *  This method is used to assign values to the request helper class
    *  @name assignValuesToCommReq
    *  @param String, String, String - authorization id, policy member id, pagenumber
    *  @return CommunicationRecordsRequest_H_HUM - communication records service request
    *  @throws NA
    */
    public CommunicationRecordsRequest_H_HUM assignValuesToCommReq(String sAuthId,  String sCommunicationPageNumber, string sEnterpriseID, string sDateOfBirth)
    {
        CommunicationRecordsRequest_H_HUM oCommunicationRequest = new CommunicationRecordsRequest_H_HUM();
        CommunicationRecordsRequest_H_HUM.GetCommunicationRecordsRequest oCommRecsReq = new CommunicationRecordsRequest_H_HUM.GetCommunicationRecordsRequest();
        CommunicationRecordsRequest_H_HUM.Filter oCommRecFilter = new CommunicationRecordsRequest_H_HUM.Filter();
        CommunicationRecordsRequest_H_HUM.PageMetaData oPageMetaData = new CommunicationRecordsRequest_H_HUM.PageMetaData();
        
        oCommRecsReq.MemberId = sEnterpriseID;
        oCommRecsReq.MemberIdType = GLOBAL_CONSTANT_HUM.HTTPREQUEST_PERSONID_HUM;
        oCommRecsReq.DateOfBirth = sDateOfBirth;
        oPageMetaData.PageSize = 50; 
        oPageMetaData.PageNumber = Integer.ValueOf(sCommunicationPageNumber);
        oCommRecsReq.PageMetaData = oPageMetaData;
        oCommRecFilter.EntityID = sAuthId;
        oCommRecsReq.Filter = oCommRecFilter;      
        oCommunicationRequest.GetCommunicationRecordsRequest = oCommRecsReq;
        return oCommunicationRequest;       
    }
    
            /**
    *  This method is used to assign values to the request helper class
    *  @name assignValuesToLettersReq
    *  @param String, String, String - authorization id, policy member id, pagenumber
    *  @return LettersRecordsRequest_H_HUM - Letters records service request
    *  @throws NA
    */

      public LettersRecordsRequest_H_HUM  assignValuesToLettersReq(String sAuthId, String sPolMemId, String sLettersPageNumber)
      {    
          LettersRecordsRequest_H_HUM oLetterRequest = new LettersRecordsRequest_H_HUM();
          LettersRecordsRequest_H_HUM.GetLettersRequest oLetterReq = new LettersRecordsRequest_H_HUM.GetLettersRequest();
          LettersRecordsRequest_H_HUM.Filter oLetterFilter = new LettersRecordsRequest_H_HUM.Filter();
          LettersRecordsRequest_H_HUM.PageMetaData oPageMetaData = new LettersRecordsRequest_H_HUM.PageMetaData();
          
          Account oAccount = oAuthReferral.getMemberAccount(sPolMemId);
          oLetterReq.MemberId = oAccount.Enterprise_ID__c;          
          oLetterReq.MemberIdType = GLOBAL_CONSTANT_HUM.HTTPREQUEST_PERSONID_HUM;
          oLetterReq.DateOfBirth = oAccount.Birthdate__c;
          oPageMetaData.PageSize = 50;
          oPageMetaData.PageNumber = Integer.ValueOf(sLettersPageNumber);
          oLetterReq.PageMetaData = oPageMetaData;
          oLetterFilter.EntityID = sAuthId;
          oLetterReq.Filter = oLetterFilter;        
          oLetterRequest.GetLettersRequest= oLetterReq;      
          return oLetterRequest;
      }

    
    /**
    *  This method is used to setup request for communication records service
    *  @name setHttpRequestCommRecs
    *  @param HttpRequest, HUM_Webservice_Callout__c, String sRequestBody
    *  @return HttpRequest 
    *  @throws NA
    */  
    public HttpRequest setHttpRequestCommRecs(HttpRequest oRequest,HUM_Webservice_Callout__c oCommRecordsService, String sRequestBody)
    {
       oRequest.setMethod(oCommRecordsService.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oCommRecordsService.End_Point_URL__c);
        if(!Test.isRunningTest())    
        {
            oRequest.setClientCertificateName(oCommRecordsService.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oCommRecordsService.Timeout__c));
        oRequest.setHeader('content-type', oCommRecordsService.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');
        return oRequest;
    }
    
          /**
    *  This method is used to setup request for Letters records service
    *  @name setHttpRequestLettersRecs
    *  @param HttpRequest, HUM_Webservice_Callout__c, String sRequestBody
    *  @return HttpRequest 
    *  @throws NA
    */
    public HttpRequest setHttpRequestLettersRecs(HttpRequest oRequest,HUM_Webservice_Callout__c oLettersRecordsService, String sRequestBody)
    {
        oRequest.setMethod(oLettersRecordsService.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oLettersRecordsService.End_Point_URL__c);
        if(!Test.isRunningTest())    
        {
            oRequest.setClientCertificateName(oLettersRecordsService.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oLettersRecordsService.Timeout__c));
        oRequest.setHeader('content-type', oLettersRecordsService.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');            
        return oRequest;
    }

    
    
    /**
    *  This method is used to process response from communication records service
    *  @name responseWrapCommRecs
    *  @param HttpResponse, List<ClinicalAuthDetailsWrapper_DTO_HUM> - response structure, list of communication records fetched in earlier service call
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM>
    *  @throws NA
    */  
    public List<ClinicalAuthDetailsWrapper_DTO_HUM> responseWrapCommRecs(HttpResponse sResponse, List<ClinicalAuthDetailsWrapper_DTO_HUM> lstCommunicationRecords)
    {
        CommunicationRecordsResponse_H_HUM oResponse = new CommunicationRecordsResponse_H_HUM();
        if(String.isNotBlank(sResponse.getBody()))    oResponse = (CommunicationRecordsResponse_H_HUM)JSON.deserialize(sResponse.getBody(),CommunicationRecordsResponse_H_HUM.class);
        if(oResponse != NULL)
        {
            if(oResponse.CommunicationRecordsResponse != null)
            {
                CommunicationRecordsResponse_H_HUM.CommunicationRecordsResponse  oAuthResponse = oResponse.CommunicationRecordsResponse ;
                CommunicationRecordsResponse_H_HUM.CommunicationRecordsList oAuthorizations = oAuthResponse.CommunicationRecordsList;
                List<CommunicationRecordsResponse_H_HUM.CommunicationRecords> lstAuthorization = oAuthorizations.CommunicationRecords;
                List<ClinicalAuthDetailsWrapper_DTO_HUM> lstNewCommunicationRecords = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
                if(lstAuthorization != null && !lstAuthorization.isEmpty())
                {
                    for(CommunicationRecordsResponse_H_HUM.CommunicationRecords oCommunication : lstAuthorization)
                    {
                        ClinicalAuthDetailsWrapper_DTO_HUM oComm = new ClinicalAuthDetailsWrapper_DTO_HUM();
                        oComm.sRecordType = oCommunication.RecordType;
                        oComm.sCommCreatedDate = oCommunication.CreatedDate;  
                        oComm.sCommCreatedBy = oCommunication.CreatedBy;
                        if(oCommunication.LastModifiedDate != null) oComm.sCommLastModifiedDate = oCommunication.LastModifiedDate.format();                        
                        oComm.sCommLastModifiedBy = oCommunication.LastModifiedBy;
                        oComm.sDepartment =  oCommunication.Department;
                        oComm.sCommunicationType = oCommunication.CommunicationType;    
                        oComm.sCommContactMethod = oCommunication.ContactMethod;  
                        oComm.sContactType = oCommunication.ContactType;  
                        oComm.sContactAttemptNo = oCommunication.ContactAttemptNo;
                        string sCntResTemp = '';
                        if(oCommunication.ContactResult !=null && !oCommunication.ContactResult.isEmpty())
                        {
                            for(String sCntRes : oCommunication.ContactResult)    
                            {
                                sCntRes = sCntResTemp + sCntRes + ';';
                            }
                        }
                        oComm.sContactResult = sCntResTemp;
                        oComm.sContactResultOther = oCommunication.ContactResultOther; 
                        oComm.sNotes = oCommunication.Notes;  
                        oComm.sDetails = oCommunication.Details; 
                        lstNewCommunicationRecords.add(oComm);
                    }
                }
                lstCommunicationRecords.addAll(lstNewCommunicationRecords);
            }
        }
        return lstCommunicationRecords;
    }
    
            /**
    *  This method is used to process response from Letters records service
    *  @name responseWrapLettersRecs
    *  @param HttpResponse, List<ClinicalAuthDetailsWrapper_DTO_HUM> - response structure, list of Letters records fetched in earlier service call
    *  @return List<ClinicalAuthDetailsWrapper_DTO_HUM>
    *  @throws NA
    */  

    public List<ClinicalAuthDetailsWrapper_DTO_HUM> responseWrapLettersRecs(HttpResponse sResponse, List<ClinicalAuthDetailsWrapper_DTO_HUM> lstLettersRecords)
    {

           LettersRecordsResponse_H_HUM oResponse = new LettersRecordsResponse_H_HUM();
           if(String.isNotBlank(sResponse.getBody()))    oResponse = (LettersRecordsResponse_H_HUM)JSON.deserialize(sResponse.getBody(),LettersRecordsResponse_H_HUM.class);            
            if(oResponse != NULL)
           {
               if(oResponse.LettersResponse != null)
               {                   
                   LettersRecordsResponse_H_HUM.LettersResponse  oAuthResponse = oResponse.LettersResponse ;
                   LettersRecordsResponse_H_HUM.LettersRecordsList oAuthorizations = oAuthResponse.LettersRecordsList;
                   List<LettersRecordsResponse_H_HUM.LettersRecords> lstAuthorization = oAuthorizations.LettersRecords;                   
                   List<ClinicalAuthDetailsWrapper_DTO_HUM> lstLettersRecordsWrapper = new List<ClinicalAuthDetailsWrapper_DTO_HUM>();
        if(lstAuthorization != null && !lstAuthorization.isEmpty())
        {            
            for(LettersRecordsResponse_H_HUM.LettersRecords oLetters : lstAuthorization)
            {
                ClinicalAuthDetailsWrapper_DTO_HUM oLetter = new ClinicalAuthDetailsWrapper_DTO_HUM();                
                oLetter.sLettersType = oLetters.LetterType;
                oLetter.sLettersAssociatedAuthID = oLetters.AssociatedAuthID;
                oLetter.sLettersAssociatedCaseID = oLetters.AssociatedCaseID;
                oLetter.sLettersCreatedBy = oLetters.CreatedBy;
                oLetter.sLettersCreatedDate = oLetters.CreatedDate;
                oLetter.sLettersTemplateName = oLetters.TemplateName;
                oLetter.sLettersStatus = oLetters.Status;
                oLetter.sLettersFullfillmentStatus = oLetters.FullfillmentStatus;
                oLetter.sLettersP2POfferedinLetter = oLetters.P2POfferedinLetter;
                oLetter.sLettersLetterName = oLetters.LetterName;
                oLetter.sLettersVoidRemarks = oLetters.VoidRemarks;
                oLetter.sLettersLastModifiedBy = oLetters.LastModifiedBy;
                if(oLetters.LastModifiedDate != null) oLetter.sLettersLastModifiedDate = oLetters.LastModifiedDate.format(); 
                oLetter.sFilePath = oLetters.FilePath;                
                lstLettersRecordsWrapper.add(oLetter);
            }
        }        
        lstLettersRecords.addAll(lstLettersRecordsWrapper);        
               }
           }           
           return lstLettersRecords;
       }
}