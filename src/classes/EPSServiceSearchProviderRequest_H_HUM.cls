/**************************************************************************** 
Apex Class Name  : EPSServiceSearchProviderRequest_H_HUM 
Version          : 1.0  
Created Date     : March 01, 2016
Function         : request helper for Search Provider method in EPS Service
Test Class       : EPSServiceSearchProviderRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth            30353            03/01/2016                   Original Version
***************************************************************************************************/
public class EPSServiceSearchProviderRequest_H_HUM
{
    public SearchRequest SearchRequest;
    
    public class SearchRequest 
    {
        public string ApplicationKey = '';
        public SearchCriteria SearchCriteria;
    }
    
    public class SearchCriteria 
    {
        public string ProviderName = '';
        public Address Address;
        public Identifiers Identifiers;
        public Filter Filter;
    }
    
    public class Address 
    {
        public string AddressType = '';  
        public string State = '';    
        public string Zip = ''; 
    }
    
    public class Identifiers 
    {
        public List<Identifier> Identifiers;
        public string IdentifierType = '';   
    }
    
    public class Filter 
    {
        public string ExactMatch = '';   
        public string ParOnly = ''; 
        public string UsaStates = '';    
        public string Classification = '';  
        public string Specialties = '';  
    }
    
    public class Identifier 
    {
        public string ID = '';
    }    
}