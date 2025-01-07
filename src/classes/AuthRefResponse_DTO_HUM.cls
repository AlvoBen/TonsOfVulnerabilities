/*******************************************************************************************************************************************************
Apex Class Name  : AuthRefResponse_DTO_HUM
Version          : 1.0
Created Date     : 5/11/2022

*****************************************************************************************************************************************************
Modification Log:

* Developer Name                        Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Aaron Speakman                        5/11/2022                  Original Version
************************************************************************************************************************************************/

public with sharing class AuthRefResponse_DTO_HUM{

    public Isauthrequiredresponse IsAuthRequiredResponse;
    
    public class Isauthrequiredresponse
{
    public Transactiondetails TransactionDetails;
    public Memberdetails MemberDetails;
    public Servicedetails ServiceDetails;
    public string Message = '';
}

public class Transactiondetails
{
    public string Application = '';
    public string TransactionTs = '';
    public string SessionID = '';
    public string Environment = '';
    public string AuthorizationType = '';
    public string TotalResponseTime = '';
}

public class Memberdetails
{
    public string MemberID = '';
    public string DOB = '';
    public string LastName = '';
    public string FirstName = '';
    public string MiddleName = '';
    public string Gender = '';
}

public class Servicedetails
{
    public List<Procedurecode> ProcedureCodes;
    public string DateOfService = '';
    public string PolicyLOB = '';
    public string CDRNumber = '';
    public string ReferralNote = '';
    public string RedirectionMessage = '';
    public string IsAuthRequired = '';
    public string QuestionnaireRequired = '';
    public string ClinicalDocumentsRequired = '';
    public string QNRMessage = '';
}

public class Procedurecode
{
    public string Code = '';
    public string IsAuthRequired = '';
    public string ProcedureCodeMessage = '';
}



}