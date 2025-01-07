/*******************************************************************************************************************************************************
Apex Class Name  : AuthRefRequest_DTO_HUM
Version          : 1.0
Created Date     : 5/11/2022

*****************************************************************************************************************************************************
Modification Log:

* Developer Name                        Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Aaron Speakman                        5/11/2022                  Original Version
************************************************************************************************************************************************/

public with sharing class AuthRefRequest_DTO_HUM{

    public Isauthrequired IsAuthRequiredRequest;
    
    public class Isauthrequired{
    
    public MemberDetail Member;
    public List<Provider> Provider ;
    public Servicedetails ServiceDetails ;
    public Contactdetails ContactDetails ;
    
    }

    public class MemberDetail{
        public string MemberID = '';
        public string DOB = '';
        public string LastName = '';
        public string FirstName = '';
    }
    
    public class Servicedetails
    {
        public List<Procedurecode> ProcedureCodes;
        public string DateOfService = '';
        public string PlaceOfService = '';
    }
    
    public class Procedurecode{
        public string Code = '';
    }
    
    public class Contactdetails
    {
        public string ContactName = '';
        public string ContactPhoneNumber = '';
    }
    
    public class Provider
    {
        public string ProviderType = '';
        public string NPI = '';
        public string TaxID = '';
        public string ProviderID = '';
    }

}