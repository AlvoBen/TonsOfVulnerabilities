/**************************************************************************** 
Apex Class Name  : EPSServiceSearchGetPCPResponse_H_HUM 
Version          : 1.0  
Created Date     : March 03, 2016
Function         : response helper for Get PCP method in EPS Service
Test Class       : EPSServiceSearchGetPCPResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth            30353            03/03/2016                   Original Version
***************************************************************************************************/
public class EPSServiceSearchGetPCPResponse_H_HUM
{
    public PcpResponse PcpResponse;
    
    public class PcpResponse
    {
    	public list<EnterpriseProviders> EnterpriseProviders;
    }
    
    public class EnterpriseProviders
    {
    	public Demographics Demographics;
    	public ProviderName ProviderName;
    	public string DbaName = '';
    	public UniqueId UniqueId;
    	public string EnterpriseId = '';
    	public string Classification = '';
    	public list<SourceProviders> SourceProviders;
    	public PcpInfo PcpInfo;
    }
    
    public class UniqueId
    {
    	public string Source;
    }
    
    public class Demographics
    {
    	public list<Addresses> Addresses;
    }
    
    public class Addresses
    {
    	public string AddressType = '';
    	public list<Phones> Phones;
    }
    
    public class Phones
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
    
    public class SourceProviders
    {
    	public string Source = '';
    	public string ProviderID = '';
    	public list<SourceIdentifiers> SourceIdentifiers;
    }
    
    public class SourceIdentifiers
    {
    	public string IdentifierType = '';
    	public list<Identifiers> Identifiers;
    }
    
    public class Identifiers
    {
    	public string ID = '';
    }
    
    public class PcpInfo
    {
    	public string PcpType = '';
    	public string GrouperName = '';
    	public string GrouperNumber = '';
    }
}