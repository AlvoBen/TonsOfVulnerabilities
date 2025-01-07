/************************************************************************************************************************************************************************************       
Apex Class Name  : METEnrollmentDataTable_LDTO_HUM.cls 
Created Date     : November 2, 2022
Function         : DTO structure to hold MET Tasks
Test Class       : METEnrollmentCaseDetailTable_LT_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Pooja Kumbhar                                     11/02/2022                  User Story - 3863263 : T1PRJ0170850 - Lightning - RCC/Medicare (MET) Enrollment sections Case Details Page
************************************************************************************************************************************************************************************/
public with sharing class METEnrollmentDataTable_LDTO_HUM
{
  public string sTask = '';
  public string sAction = '';
  public string sSource = '';
  public string sStatus = '';
  public string sCreatedDate = '';
  public string sCreatedBy = '';
  public string sCreatedByQ = '';
  public string sLastModDate = '';
  public string sLastModifiedBy = '';
  public string sLastModByQ = '';
  public string sCancelledByDate = '';
  public string sCancelledBy = '';
  public string sCancelledByQ = '';
  public string sColSort = '';
  
   public METEnrollmentDataTable_LDTO_HUM()
   {}
   
  
}