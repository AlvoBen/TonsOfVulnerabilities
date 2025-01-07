/***************************************************************************************************************************************************************************
Apex Class Name  : HUMInqTempUriOrgServiceTest
Version          : 1.0
Created Date     : Aug 6 2014
Function         :Test class for HUMInqTempUriOrgService
* Modification ID     Developer                Code Review            Date                 Description
* -------------------------------------------------------------------------------------------------------------------------
* 1.0              Shreya Choodamani              15767             08/06/2014           Original Version
* 1.1              Vishal Verma                   22273             04/06/2014           Added more data in mock response
* 1.2              Pallavi r                      71716             02/14/2017           test class updated for updated wsdl - req 300069
*****************************************************************************************************************************************************************************/
@isTest
global class HUMInqTempUriOrgTest implements WebServiceMock{
        global void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType){
           HUMInqSchemasDataContractOrgService.InquirySearchResultDTO respElement = new HUMInqSchemasDataContractOrgService.InquirySearchResultDTO();
           HUMInqTempUriOrgService.GetInquiriesResponse_element inqResponse = new HUMInqTempUriOrgService.GetInquiriesResponse_element();
           HUMInqTempUriOrgService.GetInquiriesXMLResponse_element inqResponseXML = new HUMInqTempUriOrgService.GetInquiriesXMLResponse_element();
           
           HUMInqSchemasDataContractOrgService.ArrayOfInquiryDTO arrayInquiry = new HUMInqSchemasDataContractOrgService.ArrayOfInquiryDTO();
           HUMInqSchemasDataContractOrgService.InquiryDTO inqDTO = new HUMInqSchemasDataContractOrgService.InquiryDTO();
           List<HUMInqSchemasDataContractOrgService.InquiryDTO> lstInqDTO = new List<HUMInqSchemasDataContractOrgService.InquiryDTO>(); 
           
           inqDTO.CustomerGenKey=12345678;
           inqDTO.CustomerCoverageSequenceNumber=34567;
           inqDTO.CoveragePlanEffectiveDate = datetime.newInstance(2013, 07, 01);
           inqDTO.CreatedTimeStamp = datetime.newInstance(2013, 07, 01);  
           inqDTO.CoverageEndDate = datetime.newInstance(2013, 07, 01); 
           inqDTO.ContactId = 3456;
           inqDTO.InquiryType ='aType';
           inqDTO.CategoryCode = 'aCategory';
           inqDTO.PolicyType = 'aProduct';
           inqDTO.DispositionCodeDescription = 'aDisposition';
           inqDto.InqRFirstName ='aFirst Name';
           inqDto.InqRLastName='aLast Name';
           inqDTO.InqRTypeCode = 'aInquiringFor';
           inqDto.InqAFirstName = 'aFirst Name'; 
           inqDto.InqALastName = 'aLast Name';
           inqDTO.InqRTypeCode = 'aInquiringForType';
           inqDTO.StatusCodeDescription = 'aStatus';
           inqDTO.PriorityCode = 'aPriority';
           inqDTO.OwnerTeamName = 'aOwnerTeam';
           inqDTO.ReasonCode = 'aReason';
           inqDTO.OwnerDepartmentName = 'aOwnerDepartment';
           lstInqDTO.add(inqDTO);
           
                      
           inqDTO = new HUMInqSchemasDataContractOrgService.InquiryDTO();
           inqDTO.CustomerGenKey=1245678;
           inqDTO.CustomerCoverageSequenceNumber=34567;
           inqDTO.CoveragePlanEffectiveDate = datetime.newInstance(2014, 07, 01);
           inqDTO.CreatedTimeStamp = datetime.newInstance(2014, 07, 01);  
           inqDTO.CoverageEndDate = datetime.newInstance(2014, 07, 01); 
           inqDTO.ContactId = 34561;
           inqDTO.InquiryType = 'bType';
           inqDTO.CategoryCode = 'bCategory';
           inqDTO.PolicyType = 'bProduct';
           inqDTO.DispositionCodeDescription = 'bDisposition';
           inqDto.InqRFirstName ='bFirst Name';
           inqDto.InqRLastName='bLast Name';
           inqDTO.InqRTypeCode = 'bInquiringFor';
           inqDto.InqAFirstName = 'bFirst Name'; 
           inqDto.InqALastName = 'bLast Name';
           inqDTO.InqRTypeCode = 'bInquiringForType';
           inqDTO.StatusCodeDescription = 'bStatus';
           inqDTO.PriorityCode = 'bPriority';
           inqDTO.OwnerTeamName = 'bOwnerTeam';
           inqDTO.ReasonCode = 'bReason';
           inqDTO.OwnerDepartmentName = 'bOwnerDepartment';
           lstInqDTO.add(inqDTO);
           
           HUMInqSchemasDataContractOrgService.InquiryDTO inqDTO1 = new HUMInqSchemasDataContractOrgService.InquiryDTO();
           inqDTO1.CustomerGenKey=1234567;
           inqDTO1.CustomerCoverageSequenceNumber=3456;
           inqDTO1.CoveragePlanEffectiveDate = datetime.newInstance(2013, 07, 01);
           inqDTO.CreatedTimeStamp = datetime.newInstance(2014, 07, 01); 
           inqDTO1.CoverageEndDate = datetime.newInstance(2013, 07, 01); 
           inqDTO1.ContactId = 3456;
           inqDTO1.InquiryType = 'Type';
           inqDTO1.CategoryCode = 'Category';
           inqDTO1.PolicyType = 'Product';
           inqDTO1.DispositionCodeDescription = 'Disposition';
           inqDto1.InqRFirstName ='First Name';
           inqDto1.InqRLastName='Last Name';
           inqDTO1.InqRTypeCode = 'InquiringFor';
           inqDto1.InqAFirstName = 'First Name'; 
           inqDto1.InqALastName = 'Last Name';
           inqDTO1.InqRTypeCode = 'InquiringForType';
           inqDTO1.StatusCodeDescription = 'Status';
           inqDTO1.PriorityCode = 'Priority';
           inqDTO1.OwnerTeamName = 'OwnerTeam';
           inqDTO1.ReasonCode = 'Reason';
           inqDTO1.OwnerDepartmentName = 'OwnerDepartment';
           lstInqDTO.add(inqDTO1);
           arrayInquiry.InquiryDTO = lstInqDTO;
           respElement.InquirySearchDetails = arrayInquiry;
           inqResponse.GetInquiriesResult = respElement;
           response.put('response_x', inqResponse);
           inqResponseXML.GetInquiriesXMLResult = respElement;
           response.put('response_x', inqResponseXML);
           }

}