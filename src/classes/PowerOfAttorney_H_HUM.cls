/****************************************************************************************
Apex Class Name  : PowerOfAttorney_H_HUM
Version          : 1.0 
Created Date     : AUGUST 14 2014
Function         : Helper class for PowerOfAttorney_C_HUM
Test Class      :  PowerOfAttorney_T_HUM   
Modification Log :
 *  Developer                   Code Review            Date                 Description
 * ----------------------------------------------------------------------------------------           
 *  Gargey Sharma                  17703            08/14/2014              Original Version  
  * Suresh Gottipati               236924           05/31/2018              REQ - 366200 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field
  * Santhi Mandava                                  04/15/2019              Replaced "Member_Id_Value__c" with Name
 *  Ajay Chakradhar                                 05/05/2021	            REQ- 2184294 - Replacing constants with Consumer Management Constants
 *******************************************************************************************/

public with sharing class PowerOfAttorney_H_HUM 
{ 
    Private String sPOA_DATEFORMAT = 'MM/dd/YYYY';
    Private String sPOA_CONSTRECORD = 'Record';
    Private String sPOA_SERVICENAME = 'CIMedicareMultipleMemberDetails';
    /**
    * This method is used to create request for CI Medicare
    * 
    * @param    list<Policy_Member__c>
    * @return   HttpRequest
    */ 
    public HttpRequest createRequestCIMed(list<Policy_Member__c> lstPM)
    {
        
        HUM_Webservice_Callout__c oCIMedicare = HUM_Webservice_Callout__c.getValues(sPOA_SERVICENAME);
        HttpRequest req = new HttpRequest();
        req.setMethod(oCIMedicare.Request_Type__c);  
        req.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oCIMedicare.Content_Type__c);
        req.setTimeout(Integer.valueof(oCIMedicare.Timeout__c) / 1000);
        req.setEndpoint(oCIMedicare.End_Point_URL__c);      
        req.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM, GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
        if (!Test.isRunningTest() && String.isNotBlank(oCIMedicare.Certificate_Name__c)) req.setClientCertificateName(oCIMedicare.Certificate_Name__c);
        req.setBody(createInput(lstPM));
        return req;
    }
    
    /**
    * This method is used to create input DTO
    * 
    * @param    list<Policy_Member__c>
    * @return   string
    */ 
    private string createInput(list<Policy_Member__c> lstPM)
    {
        
        CIMedicareMultipleMemberRequest_DTO_HUM oRequest = new CIMedicareMultipleMemberRequest_DTO_HUM();
        CIMedicareMultipleMemberRequest_DTO_HUM.GetMemberInfo oGetMemInfo = new CIMedicareMultipleMemberRequest_DTO_HUM.GetMemberInfo();
        list<CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails> lstPOAInputs = new list<CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails>();
        for(Policy_Member__c oPM : lstPM)
        {
            CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails oMedicareMember = new CIMedicareMultipleMemberRequest_DTO_HUM.MemberDetails();
            oMedicareMember.MedicareID ='';
            oMedicareMember.MemberCardID = oPM.Member_Ids__r[0].Name ;
            If(!String.isBlank(oPM.Policy__r.Source_Cust_Cov_Key__c) && oPM.Policy__r.Source_Cust_Cov_Key__c.length() >= 9)
            {
                oMedicareMember.GroupID = oPM.Policy__r.Source_Cust_Cov_Key__c.subString(0, 6);
                oMedicareMember.BenefitSequenceNumber = oPM.Policy__r.Source_Cust_Cov_Key__c.subString(6, 9);
            }
            oMedicareMember.AsOfDate = Datetime.now().format(sPOA_DATEFORMAT);
            oMedicareMember.LISInfo = 'N';
            oMedicareMember.LTSSInfo = 'N';
            oMedicareMember.ESRDInfo = 'N';
            oMedicareMember.SPAPInfo = 'N';
            oMedicareMember.SNPInfo = 'N';
            oMedicareMember.POAInfo = 'Y';
            lstPOAInputs.add(oMedicareMember);
        }
        oGetMemInfo.MemberDetails = lstPOAInputs;
        oRequest.GetMemberInfo = oGetMemInfo;
       
        string sReqBody = json.serialize(oRequest);
        return sReqBody;
    }
    
    /**
    * This method is used to parse CI Medicare response and assign it to map
    * 
    * @param    string
    * @return   map<string, list<string>>
    */ 
    public map<string, list<string>> parseCIResponse(string sResponse)
    {
        map<string, list<string>> mapServiceResponse = new map<string, list<string>>();
        CIMedicareMultipleMemberResponse_DTO_HUM oResponse = (CIMedicareMultipleMemberResponse_DTO_HUM)System.JSON.deserialize(sResponse, CIMedicareMultipleMemberResponse_DTO_HUM.class);
        
        if(oResponse.CIMedicareResponse != null)
        {
            CIMedicareMultipleMemberResponse_DTO_HUM.CIMedicareResponse oCIResp = new CIMedicareMultipleMemberResponse_DTO_HUM.CIMedicareResponse();
            oCIResp = oResponse.CIMedicareResponse;
            list<CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo> lstGetMedicareMem = new list<CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo>();
            if(oCIResp.GetMedicareMemberInfo !=null && !oCIResp.GetMedicareMemberInfo.isEmpty())
            {
                lstGetMedicareMem = oCIResp.GetMedicareMemberInfo;
                Integer iCount = 0;
                if(lstGetMedicareMem!=null && !lstGetMedicareMem.isEmpty())
                {
                    for(CIMedicareMultipleMemberResponse_DTO_HUM.GetMedicareMemberInfo oGetMedicareMem : lstGetMedicareMem)
                    {
                        CIMedicareMultipleMemberResponse_DTO_HUM.MedicareMemberLISSNPInfoResponse oMediMemResp = new CIMedicareMultipleMemberResponse_DTO_HUM.MedicareMemberLISSNPInfoResponse();
                        oMediMemResp = oGetMedicareMem.MedicareMemberLISSNPInfoResponse; 
                        CIMedicareMultipleMemberResponse_DTO_HUM.Errors oErrors = new CIMedicareMultipleMemberResponse_DTO_HUM.Errors();
                        oErrors = oMediMemResp.Errors;
                        CIMedicareMultipleMemberResponse_DTO_HUM.Error oErr = new CIMedicareMultipleMemberResponse_DTO_HUM.Error();
                        oErr = oErrors.Error;
                        if(oErr == null || (string.isBlank(oErr.Code) && string.isBlank(oErr.Description)))
                        {
                            if((!String.isBlank(oMediMemResp.POAIndicator) && oMediMemResp.POAIndicator == 'Y') && !(String.isBlank(oMediMemResp.POAFirstName) && 
				        		String.isBlank(oMediMemResp.POALastName) && String.isBlank(oMediMemResp.POAPhoneNumber) && String.isBlank(oMediMemResp.POAType)))
                            {
                                mapServiceResponse.put(sPOA_CONSTRECORD + iCount, new list<string>{oMediMemResp.POAFirstName,oMediMemResp.POALastName,oMediMemResp.POAPhoneNumber,oMediMemResp.POAType});
                                iCount = iCount + 1;
                            }
                        }
                    }
                }
            }
        }
        
        return mapServiceResponse;
      }
}