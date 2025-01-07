/**************************************************************************** 
Apex Class Name  : RTISummary_DTO_HUM
Version          : 
Created Date     : January 10th, 2017
Function         : DTO for GetInteractionSummary method in RTI service
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Pallavi Ravishankar          61268                 01/04/2017          Original Version
*  Kritika Popat                111075                07/18/2017          Message in Print Card
*  Anjani  Vaddadi                                   21/02/2020           RTI VAT changes
*  Imran                                             04/06/2020           Story #956598 RTI date Issue
*  Prasanthi Kandula                                 04/16/2020           US 1003374 - To display send link on RTI Detail page
*  Anjani Vaddadi                                     7/03/2020           US - 1125628- Pass Message Definition Code (MDC) on Outbound Communications attached to CRM Cases via RTI
*  Prasanthi Kandula                            05/18/2021                US - 2082407 Added new card for channel Email
*  Rahul Chaurasia                              05/19/2021                US - 2082359 Added logic to display new card for channel Web
****************************************************************************************************/

public with sharing class RTISummary_DTO_HUM
{ 
    public string iconPath {get;set;}
    public String sInteractionkey {get;set;}
    public String sInteractionsrckey {get;set;}
    public String sSourceCode {get;set;}
    public String sChannelCode {get;set;}
    public String sChannelDescription {Get;Set;}
    Public String sSourceDescription {Get;Set;}
    Public string sDirectionCode{Get;Set;}
    Public DateTime dDateAndTime {get;set;}
    public string title {get;set;}
    public string subtitle {get;set;}
    public string buttonTarget {get;set;}
    public string viewLabel {get;set;}
    public string sName {Get;Set;}
    Public string sMessageName {Get;Set;}
    Public string index{Get;Set;}
    public string sMemberCardId {get; set;}
    public string sDependentCode {get; set;}
    public string sPlatformCode {get; set;}
    public string sSDRPersonId {get; set;}
    public string sPersonGenKey {get; set;}
    public string sMessageFulfillmentId {get; set;}
    Public boolean bPrintable {get; set;}
    Public string sMsgDefCode{get;set;}
    //VAT
    Public string campaignName {get;set;}
    Public string statusDescription {get;set;}
    Public string statusCode{get;set;}
    Public string PhoneNumber{get;set;}
    Public string subStatusDesc{get;set;} 
    Public string trackingID{get;set;}
    public string sRecipientName {get;set;}
    
    //Web
    Public string MessageDefinitionDescription{get;set;}
    Public string BusineesSubArea{get;set;}
    
    Public integer sNbRecords{get;set;}
}