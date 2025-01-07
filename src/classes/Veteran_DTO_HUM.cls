/*****************************************************************************************
Apex class Name    : VeteranInfo_DTO_HUM
Version            : 1.0
Function           : Controller to VF Component: VeteranDetail_CMP_HUM
                     This class is used to display the Veteran Details
Created Date       : 03/04/2021
Test Class         : 

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Anjani Vadadi                                      03/04/2021          Original Version
 ****************************************************************************************************************************/

public with sharing class Veteran_DTO_HUM
{
    public String sVeteranStatus{get;set;}
    public String sVAHealthEnrollee{get;set;} 
    public String sPersonId{get;set;}
    public String sMemberGenKey{get;set;}
    public String sAccountName{get;set;}
    public String sAccountId{get;set;}
    public String veteranMessage{get;set;}
    public String veteranMessageType{get;set;}
}