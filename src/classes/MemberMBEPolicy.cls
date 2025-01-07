/****************************************************************************
Apex Class Name  : MemberMBEPolicy
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Helper Class to storing the JSON response parsed policy list values
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
***************************************************************************************************/

public class MemberMBEPolicy{
    public string sPolicyEnterpriseId {get;set;}
    public string sEffectiveDate {get;set;}
    public string sEndDate {get;set;}
    public string sMajorLineOfBusiness {get;set;}
    public string sProductLine {get;set;}
}