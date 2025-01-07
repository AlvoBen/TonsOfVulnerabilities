/**************************************************************************** 
Apex Class Name  : GBEServiceRequest_H_HUM 
Version          : 1.0  
Created Date     : February 09, 2016
Function         : Request helper for GetGroupInfo method in GBE Service
Test Class       : GBEServiceRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    29854             02/09/2015                    Original Version
* ChiranjeeviRao Ravuri    30345             03/02/2016                    REQ-255066:Made changes as per new request structure
* Adil Mohammed            30619             03/23/2016                    REQ - 263776 : Made changes as per the new request structure
* Firoja Begam                               10/19/2020                    REQ - 1279297 : PR00095366 - MF 1 - new fields in policy details section (group policy page)
* Firoja Begam                               02/10/2021                    US-1760349 - PR00095180 - CRM 6.01 Add PEO Indicator for MTV and CAS products  to Member Policy Page (copy)
***************************************************************************************************/
public class GBEServiceRequest_H_HUM
{   
    public GroupDetailDTO GroupDetailDTO;
    public class GroupDetailDTO {
        public String GroupId = '';
        public String SubGroupId = '';
        public String BenefitPackageId = '';
        public String Platform = '';
        public String StartDate = '';
        public String EndDate = '';
        public String AsOfDate = '';
        public String BenefitSequenceNumber = '';
        public String InclusionCriteria = ''; 
        public String DivisionStartPosition = '';
        public String DivisionRecordsToReturn = '';
        public String VendorOfferProgramGroup = '';
        public String VendorOfferProgramSubGroup = '';
        /****Added for PEO Indicator Field ****/
        public String Association = '';
    }

}