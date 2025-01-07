/*******************************************************************************************************************************
Apex Class Name : CoachMemberSearchWrapper_DTO_HUM
Version         : 1.0
Created On      : 03/10/2021
Function        : This class is used to hold the membersearch input values 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                                  03/11/2021                Original Version
*******************************************************************************************************************************************************************/

 public with sharing class CoachMemberSearchWrapper_DTO_HUM
{

    @AuraEnabled  public String sMemberId{get; set;}
    @AuraEnabled  public String sSuffix{get; set;}
    @AuraEnabled  public String sBirthdate{get; set;}
    @AuraEnabled  public String sPhone{get; set;}
    @AuraEnabled  public String sGroupNumber{get; set;}
    @AuraEnabled  public String sPID{get; set;}
    @AuraEnabled  public String sState{get; set;}
    @AuraEnabled  public String sPostalCode{get; set;}
    @AuraEnabled  public String rxNumber{get; set;}
    @AuraEnabled  public String sFirstName{get; set;}
    @AuraEnabled  public String sLastName{get; set;}
    @AuraEnabled  public Boolean sUnknownMemCheck{get;set;}


    
    public CoachMemberSearchWrapper_DTO_HUM() {
        this.sMemberId= sMemberId;
        this.sSuffix= sSuffix;
        this.sBirthdate= sBirthdate;
        this.sPhone= sPhone;
        this.sGroupNumber= sGroupNumber;
        this.sPID= sPID;
        this.sState= sState;
        this.sPostalCode= sPostalCode;
        this.rxNumber= rxNumber;
        this.sFirstName= sFirstName;
        this.sLastName= sLastName;
        this.sUnknownMemCheck=sUnknownMemCheck;
    }
    
}