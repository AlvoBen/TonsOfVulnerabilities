/************************************************************************************************
Apex Class Name  : HUMVerifyFamilyWSDL 
Version          : 1.0 
Created Date     : July 16  2014
Function         : System Generated Proxy class for PersonServiceFlattenedWSDL_External_DP_Dev WSDL
Test Class       : HUMVerifyFamilyWSDLTest 
Log:
*     Developer Name       Code Review          Date                 Description
* -------------------------------------------------------------------------------------------------             
*     Kritika Popat           17076                23/05/2014            Original Version
***************************************************************************************************/

public class HUMVerifyFamilyWSDL {
    public class UpdateDemographicsResponse_element {
        public String UpdateDemographicsResult;
        private String[] UpdateDemographicsResult_type_info = new String[]{'UpdateDemographicsResult','http://tempuri.org/',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'UpdateDemographicsResult'};
    }
    public class UpdateDemographics_element {
        public String actionType;
        public HUMVerifyFamilyDatacontract.PersonDemographicsLastVerifiedInfoDTO lastVerifieDOI;
        public String memberId;
        public String trimSize;
        public HUMVerifyFamilyDatacontract.UpdatedMemberDemographicsDetailsDTO demographicsInputDTO;
        private String[] actionType_type_info = new String[]{'actionType','http://tempuri.org/',null,'0','1','true'};
        private String[] lastVerifieDOI_type_info = new String[]{'lastVerifieDOI','http://tempuri.org/',null,'0','1','true'};
        private String[] memberId_type_info = new String[]{'memberId','http://tempuri.org/',null,'0','1','true'};
        private String[] trimSize_type_info = new String[]{'trimSize','http://tempuri.org/',null,'0','1','true'};
        private String[] demographicsInputDTO_type_info = new String[]{'demographicsInputDTO','http://tempuri.org/',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'actionType','lastVerifieDOI','memberId','trimSize','demographicsInputDTO'};
    }
    public class GetMemberInformationResponse_element {
        public HUMVerifyFamilyDatacontract.ArrayOfMemberDTO GetMemberInformationResult;
        private String[] GetMemberInformationResult_type_info = new String[]{'GetMemberInformationResult','http://tempuri.org/',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'GetMemberInformationResult'};
    }
    public class GetLastVerifiedDatesResponse_element {
        public HUMVerifyFamilyDatacontract.OIDemographicsOutputDTO GetLastVerifiedDatesResult;
        private String[] GetLastVerifiedDatesResult_type_info = new String[]{'GetLastVerifiedDatesResult','http://tempuri.org/',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'GetLastVerifiedDatesResult'};
    }
    public class GetLastVerifiedDates_element {
        public HUMVerifyFamilyDatacontract.OIDemographicsInputDTO oiDemographicDatesDTO;
        private String[] oiDemographicDatesDTO_type_info = new String[]{'oiDemographicDatesDTO','http://tempuri.org/',null,'0','1','true'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'oiDemographicDatesDTO'};
    }
    public class GetMemberInformation_element {
        public Long memberGenKey;
        private String[] memberGenKey_type_info = new String[]{'memberGenKey','http://tempuri.org/',null,'0','1','false'};
        private String[] apex_schema_type_info = new String[]{'http://tempuri.org/','true','false'};
        private String[] field_order_type_info = new String[]{'memberGenKey'};
    }
    public class soap {
        public String endpoint_x; //= 'https://dev-crmservicews.humana.com/PersonService.svc';
        public Map<String,String> inputHttpHeaders_x;
        public Map<String,String> outputHttpHeaders_x;
        public String clientCertName_x;
        public String clientCert_x;
        public String clientCertPasswd_x;
        public Integer timeout_x;
        private String[] ns_map_type_info = new String[]{'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.Member', 'HUMVerifyFamilyDatacontract', 'http://tempuri.org/', 'HUMVerifyFamilyWSDL', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.Enums', 'HUMVerifyFamilyDatacontract', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects', 'HUMVerifyFamilyDatacontract', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.ServiceFaults', 'HUMVerifyFamilyDatacontract', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common', 'HUMVerifyFamilyDatacontract', 'http://schemas.datacontract.org/2004/07/Humana.GuidanceCareServices.Common.DataTransferObjects.OIDemographics', 'HUMVerifyFamilyDatacontract'};
        public String UpdateDemographics(String actionType,HUMVerifyFamilyDatacontract.PersonDemographicsLastVerifiedInfoDTO lastVerifieDOI,String memberId,String trimSize,HUMVerifyFamilyDatacontract.UpdatedMemberDemographicsDetailsDTO demographicsInputDTO) {
            HUMVerifyFamilyWSDL.UpdateDemographics_element request_x = new HUMVerifyFamilyWSDL.UpdateDemographics_element();
            request_x.actionType = actionType;
            request_x.lastVerifieDOI = lastVerifieDOI;
            request_x.memberId = memberId;
            request_x.trimSize = trimSize;
            request_x.demographicsInputDTO = demographicsInputDTO;
            HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element response_x;
            Map<String, HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element> response_map_x = new Map<String, HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/UpdateDemographics',
              'http://tempuri.org/',
              'UpdateDemographics',
              'http://tempuri.org/',
              'UpdateDemographicsResponse',
              'HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.UpdateDemographicsResult;
        }
        public HUMVerifyFamilyDatacontract.OIDemographicsOutputDTO GetLastVerifiedDates(HUMVerifyFamilyDatacontract.OIDemographicsInputDTO oiDemographicDatesDTO) {
            HUMVerifyFamilyWSDL.GetLastVerifiedDates_element request_x = new HUMVerifyFamilyWSDL.GetLastVerifiedDates_element();
            request_x.oiDemographicDatesDTO = oiDemographicDatesDTO;
            HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element response_x;
            Map<String, HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element> response_map_x = new Map<String, HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/GetLastVerifiedDates',
              'http://tempuri.org/',
              'GetLastVerifiedDates',
              'http://tempuri.org/',
              'GetLastVerifiedDatesResponse',
              'HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.GetLastVerifiedDatesResult;
        }
        public HUMVerifyFamilyDatacontract.ArrayOfMemberDTO GetMemberInformation(Long memberGenKey) {
            HUMVerifyFamilyWSDL.GetMemberInformation_element request_x = new HUMVerifyFamilyWSDL.GetMemberInformation_element();
            request_x.memberGenKey = memberGenKey;
            HUMVerifyFamilyWSDL.GetMemberInformationResponse_element response_x;
            Map<String, HUMVerifyFamilyWSDL.GetMemberInformationResponse_element> response_map_x = new Map<String, HUMVerifyFamilyWSDL.GetMemberInformationResponse_element>();
            response_map_x.put('response_x', response_x);
            WebServiceCallout.invoke(
              this,
              request_x,
              response_map_x,
              new String[]{endpoint_x,
              'http://tempuri.org/IPersonService/GetMemberInformation',
              'http://tempuri.org/',
              'GetMemberInformation',
              'http://tempuri.org/',
              'GetMemberInformationResponse',
              'HUMVerifyFamilyWSDL.GetMemberInformationResponse_element'}
            );
            response_x = response_map_x.get('response_x');
            return response_x.GetMemberInformationResult;
        }
    }
}