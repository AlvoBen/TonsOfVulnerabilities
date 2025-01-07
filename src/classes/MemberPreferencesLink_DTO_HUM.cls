/**************************************************************************** 
Apex Class Name  : MemberPreferencesLink_DTO_HUM
Version          : 1.0
Created Date     : 03/05/2018 
Function         : DTO for Encrypt method in Encrypt Rest Service
Test Class       : MemberPreferencesLink_T_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Shivam Anand                                03/05/2018           REQ349581- Add Member Preferences Link to CRM Service Person Account Page
*  Vijaya Koriprolu                            03/06/2018           REQ - 349580- Add Member Preferences Link on CRM Policy Member Page
***************************************************************************************************/

public with sharing class MemberPreferencesLink_DTO_HUM
{ 
 /*
 * sEncValue : Encrypted Member Gen Key
 */
 public string sEncValue{get;set;}

}