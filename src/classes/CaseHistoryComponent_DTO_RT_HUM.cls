/******************************************************************************************************************************
Apex Class Name  : CaseHistoryComponent_DTO_RT_HUM
Version          : 1.0 
Created Date     : 11th April 2018
Function         : Wrapper Class for CaseHistoryComponent_C_HUM                   
Test Class       : 
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Pallavi Ravishankar      215883            11/04/2018        REQ - 323439 Converted case history datatable to reusable datatable component
 *  Asish Behera                               07/12/2018        REQ - 369794 AHT- Add Last Modified By to Case History Section (RF)                           
 *  Santhi Mandava                             02/18/2020        US - 860272 - Added "sDCN" string variable
 *  Subhamay Mandal							               01/13/2022			   User Story 2977653: T1PRJ0001827 - MF 1 - AHT - Ability to display count of linked cases in the Case History section (RF)
********************************************************************************************************************************/
Public with sharing class CaseHistoryComponent_DTO_RT_HUM
{
  public String Id {get;set;}
  public String sCaseIdVal {get;set;}
  public String sCaseNum {get;set;}
  public String sDCN{get;set;}
  public String sCreatedDate {get;set;}
  public String sClosedDate {get;set;}
  public String sClassification {get;set;}
  public string sIntent {get;set;}
  public String sProduct {get;set;}
  public String sStatus {get;set;}
  public String sOrigin {get;set;}
  public string sType {get;set;}
  public String sInteractingWith {get;set;}
  public String sInteractingWithType {get;set;}
  public String sInteractingAbout {get;set;}
  public String sPriority {get;set;}
  public String sCreatedBy {get;set;}
  public String sCreatedByQueue {get;set;}
  public boolean bDentalCase {get;set;}
  public boolean bMedicalcase{get;set;}
  public boolean bGeneralCase{get;set;}
  public String sLastModifiedBy {get;set;}
  public Integer sLinkedCaseCount {get; set;}
}