/**************************************************************************** 
Apex Class Name  : EPSSRESTV1GetPCPResponse_H_HUM 
Version          : 1.0  
Created Date     : April 11, 2016
Function         : response helper for Get PCP method in EPS Service REST V1.0
Test Class       : EPSSRESTV1GetPCPResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30905             04/11/2016                   Original Version
***************************************************************************************************/
public class EPSSRESTV1GetPCPResponse_H_HUM
{
    public GetPcpResponse GetPcpResponse;
    
    public class GetPcpResponse
    {
        public list<EnterpriseProviders> EnterpriseProviders;
    }
    
    public class EnterpriseProviders
    {
        public Demographics Demographics;
        public ProviderName ProviderName;
        public string DbaName = '';
        public UniqueID UniqueID;
        public string EnterpriseID = '';
        public string Classification = '';
        public list<SourceProviders> SourceProviders;
        public PcpInfo PcpInfo;
    }
    
    public class UniqueID
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