/**************************************************************************** 
Apex Class Name  : EPSServiceSearchProviderResponse_H_HUM 
Version          : 1.0  
Created Date     : March 02, 2016
Function         : request helper for Search Provider method in EPS Service
Test Class       : EPSServiceSearchProviderResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth           30353             03/02/2016                   Original Version
***************************************************************************************************/
public class EPSServiceSearchProviderResponse_H_HUM
{
    public SearchResponse SearchResponse;
    
    public class SearchResponse 
    {
        public List<EnterpriseProviders> EnterpriseProviders;
    }
    
    public class EnterpriseProviders 
    {
        public string EnterpriseID = '';
        public UniqueID UniqueID;
        public ProviderName ProviderName;
        public string DbaName = '';
        public string Classification = '';
        public Demographics Demographics;
        public List<Identifier> Identifiers;
        public List<Specialties> Specialties;
        public List<SourceProvider> SourceProviders;
        public List<Taxonomy> Taxonomy;
    }
    
    public class Demographics 
    {
        public List<Address> Addresses;
        public string Degree = '';
        public string DateOfBirth = '';
        public string Gender = '';
    }
    
    public class Address 
    {
        public string AddressType = '';
        public string Line1 = '';
        public string Line2 = '';
        public string City = '';
        public string State = '';
        public string Zip = '';
        public List<Phone> Phones;
    }
    
    public class Phone 
    {
        public string PhoneType = '';
        public string PhoneNumber = '';
        public string Extension = '';
    }
    
    public class ProviderName 
    {
        public string First = '';
        public string Last = '';
    }
    
    public class SourceProvider 
    {
        public string Source = '';
        public string ProviderId = '';
    }
    
    public class Specialties 
    {
        public string Description = '';
    }
    
    public class Identifier 
    {
        public list<IdentifierDetail> Identifiers;
        public string IdentifierType = '';
    }
    
    public class IdentifierDetail 
    {
        public string ID = '';
    }
    
    public class Taxonomy 
    {
        public string Code = '';
    }
    
    public class UniqueID 
    {
        public string Source = '';
    }
}