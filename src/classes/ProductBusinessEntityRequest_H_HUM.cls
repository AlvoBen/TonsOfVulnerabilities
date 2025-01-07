/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityRequest_H_HUM 
Version          : 1.0  
Created Date     : February 01,2016
Function         : request helper for Product Business Entity Service
Test Class       : ProductBusinessEntityRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             29829             02/01/2016                   Original Version
* Gargey Sharma             30736             03/29/2016                   REQ - 263787: Added ServiceDetail
* Kritika Popat             112848            07/25/2017                   REQ - 320577 Blended coverage
* Prasanthi Kandula         112848            07/25/2017                   REQ - 320577 Blended coverage
* Rajesh Keswani            109089	          07/24/2017                   REQ - 299431 Making Place of Service drop down dynamic
* Avinash Choubey           109914 	          07/24/2017                   REQ - 300751 Making Par Code” drop down dynamic(for CAS Members) and Sorted
***************************************************************************************************/
public class ProductBusinessEntityRequest_H_HUM
{
    public GetProduct GetProduct;
    
    public class GetProduct
    {
        public string AsOfDate = '';
        //ICDCodeList is not supported currently as per ICD 
        public ICDCode ICDCodeList;
        public ProductIdentifier ProductIdentifier;
        public IncludeType IncludeList;
        public ServiceDetail ServiceDetail;
        public IncludeAdditionalDetail IncludeAdditionalDetail;
    }
    
    public class ProductIdentifier
    {
        public string ProductId = '';
        public string ProductIdType = '';
    }
    
    public class IncludeType
    {
        public list<string> IncludeType;
    }
    
    public class ICDCode
    {
        //For now it is a string, might change to list of string in future.
        public string ICDCode = '';
    }
    
    public class ServiceDetail
    {
        public string ServiceCategoryCode = '';
        public string ServiceTypeId = '';
        public string PlaceOfServiceCode = '';
        public string Par = '';
        public string ParCode = '';
    }
    public class IncludeAdditionalDetail 
    {
        public string MasterServicesWithPOS = '';
        public string MasterServicesWithParCode = '';
        public String NonCoveredBenefit = '';
    }
}