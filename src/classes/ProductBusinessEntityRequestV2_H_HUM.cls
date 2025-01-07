/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityRequestV2_H_HUM
Version          : 1.0  
Created Date     : April 12,2018
Function         : request helper for Product Business Entity Service 1.3
Test Class       : ProductBusinessEntityRequestV2_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rakesh Patni                            12/04/2018                   Original Version
* Rajesh Keswani          220187          08/05/2018                   ADA Code dropdown for Benefit Search section
***************************************************************************************************/
public class ProductBusinessEntityRequestV2_H_HUM
{
    public GetProduct GetProduct;
    
    public class GetProduct
    {
    	public string SubVersion=''; // newly added for  PBE 18.08 changes
        public string AsOfDate ='';
        public ICDCodeList ICDCodeList;
        public ProductIdentifier ProductIdentifier;
        public IncludeList IncludeList;
        public ServiceDetail ServiceDetail;
        public IncludeAdditionalDetail IncludeAdditionalDetail;
    }
    
    public class ProductIdentifier
    {
        public string ProductId = '';
        public string ProductIdType = '';
    }
    
    public class IncludeList
    {
        public list<string> IncludeType;
    }
    
    public class ICDCodeList
    {
        public list<string> ICDCode;
    }
    
    public class ServiceDetail
    {
        public list<string> ServiceCategoryCode; //converted to list from string for  PBE 18.08 changes
        public string ServiceTypeId = '';
        public string PlaceOfServiceCode = '';
        public string Par = '';
        public string ParCode = '';
        public string AdaCode = '';
    }
    public class IncludeAdditionalDetail 
    {
        public string MasterServicesWithPOS = '';
        public string MasterServicesWithParCode = '';
        public String NonCoveredBenefit = '';
        public list<string> HipaaList;
        public string ServiceCategory ='';
        public string ServiceName ='';
        public string MasterServicesWithAdaCode='';
    }
}