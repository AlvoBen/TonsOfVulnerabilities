/**************************************************************************** 
Apex Class Name  : PBEBenefitsSearch_DTO_HUM   
Version          : 1.0  
Created Date     : June 7, 2016
Function         : Wrapper class for Benefit Search  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh keswani                             06/07/2016                 Original Version
***************************************************************************************************/

public with sharing class PBEBenefitsSearch_DTO_HUM
  {
	public Map<string,list<CoverageType_DTO_HUM>> lstOfLimits = new Map<string,list<CoverageType_DTO_HUM>>();
	public Map<string,list<string>> lstOfServiceComments = new Map<string,list<string>>();
	public list<CoverageType_DTO_HUM> LimitFalseLst = NULL;
  }