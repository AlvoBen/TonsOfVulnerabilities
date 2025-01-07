/**************************************************************************** 
Apex Class Name  : CoverageType_DTO_HUM   
Version          : 1.0  
Created Date     : June 7, 2016
Function         : Wrapper class for Benefit Search  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh keswani                             06/07/2016                 Original Version
* Rajesh keswani           31032             06/29/2016                 Added new variable as part of REQ-270214
* Rajesh keswani           31996             07/01/2016                 Added new variable as part of place Of service
* Rajesh Keswani           39527             09/15/2016                 Implemented REQ - 282318 adding Notes column in benefits search table
* Praveen Kumar Parimi     56654             01/11/2017                 Added Tier Number and ProviderSubNetworkNumber as part of Implementing the changes for Tiering Project. 
* Kritika Popat            112848            07/25/2017                 REQ - 320577 Blended coverage
* Prasanthi Kandula        112848            07/25/2017                 REQ - 320577 Blended coverage
* Yogesh Gupta								 12/03/2019					REQ - 410445 Azure-731139 Adding Ada code on UI
***************************************************************************************************/

public with sharing class CoverageType_DTO_HUM
 {
    public String Par = '';
    public String NonPar = '';
    public String BenefitDescription = '';
    public String nodeType = '';
    public String limitName = '';
    public String limitText = '';
    public String limitComment = '';
    public String isLimit = '';
    public String limitAmount = '';
    public Boolean matched = false;
    public String CoverageType = '';
    public String benefitType = '';
    public String placeOfServiceCode = '';
    public String serviceComments = '';
    public String TierNumber = '';
    public String notes = '';
    public String sIsCoveredBenefit = '';
    public String ProviderSubNetworkNumber ='';    
    public string Networktype ='';
    public string ServiceCatCode ='';
    public string ServiceTypeID ='';
	public String AdaCode = '';
 }