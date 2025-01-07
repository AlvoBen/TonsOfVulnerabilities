/**************************************************************************** 
Apex Class Name  : EPSSRESTV1SearchProviderRequest_H_HUM 
Version          : 1.0  
Created Date     : April 11, 2016
Function         : request helper for Search Provider method in EPS Service REST V1.0
Test Class       : EPSSRESTV1SearchProviderRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date             Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma              30905            04/11/2016         Original Version
* Brian Clift                71723            01/25/2017         Added Address Line1 ,Address Line 2 and city fields to Address class 
                                                                 as part of QAA template enhancements.
***************************************************************************************************/
public class EPSSRESTV1SearchProviderRequest_H_HUM
{
    public SearchProviderRequest SearchProviderRequest;
    
    public class SearchProviderRequest 
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
        public String AddressLine1 = '';
        public String AddressLine2 = '';
        public string State = '';    
        public string Zip = ''; 
        public String City = '';        
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
        public string Classification = '';
        public string UsaStates = '';
        public string Specialties = '';
    }
    
    public class Identifier 
    {
        public string ID = '';
    }    
}