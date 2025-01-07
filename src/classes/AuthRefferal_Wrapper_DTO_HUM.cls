/*******************************************************************************************************************************************************
Apex Class Name  : AuthRefferal_Wrapper_DTO_HUM
Version          : 1.0
Created Date     : 12/08/2015
Function         : This class is wrapper class to store data from service
Test Class       : AuthRefferal_Wrapper_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar               29042                 12/08/2015                 Original Version
* Ranjeeth N                 29646                 01/27/2016                 Changed the class name to DTO as per the standards
* Manish Kumar               30647                 03/26/2015                 Added for continuos call to serice REQ-262442
************************************************************************************************************************************************/

public with sharing class AuthRefferal_Wrapper_DTO_HUM
{
    public String sAuthorizationOrReferralNumber {set; get;}
    public String sAuthorizationType {set; get;}
    public String sOverallStatus {set; get;}              
    public String sAdmFirstDay {set; get;}
    public String sDischargeLastDay{set; get;}
    public String sServiceType {set; get;}
    public String sTreatingProvider {get; set;}
    public String sFacility {set; get;}
    public String sRequestingrovider {set; get;}
    public String sGroupId {set; get;}
    public Boolean bIsAccessible {set; get;}
    public Integer Totalcount {get;set;}
    public String recId {get;set;}
}