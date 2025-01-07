/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityRequestV2_H_HUM
Version          : 1.0  
Created Date     : April 18,2018
Function         : request helper for Product Business Entity Service 1.3
Test Class       : BenefitsV2_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rajesh Keswani           220187           18/04/2018                   Original Version
* Anjali Kumar                              4/08/2018                    REQ 363358 - Made changes for Maternity code in service category
***************************************************************************************************/
public with sharing class PBEServiceTypeV2DTO_HUM {
    public  Map<string,List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>> mapServiceCatServicetype = null;
    public  Map<string,string> mapServiceCatCode = null;
}