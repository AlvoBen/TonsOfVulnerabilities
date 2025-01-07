/**************************************************************************** 
Apex Class Name  : AddClaimNum_CaseEdit_DTO_HUM
Version          : 1.0  
Created Date     : 11/18/2019
Function         : class to add claim numbers
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Vijaya Koriprolu                             11/18/2019                Wrapper class for adding claim numbers
***************************************************************************************************/
public with sharing class AddClaimNum_CaseEdit_DTO_HUM
{
    public string sClaimText {get; set;}
    public string sClaimNumber {get; set;}
    public string sSecondCol {get; set;}
    public string sThirdCol {get; set;}
    public string sFourthCol {get; set;}
    public string sFifthCol {get; set;}
}