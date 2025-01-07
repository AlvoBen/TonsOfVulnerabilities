/*******************************************************************************************************************************
Apex Class Name : LINKS_CONSTANT_HUM
Version         : 1.0
Created On      : 05/05/2021
Function        : Constant class for link framework

* Modification Log:
* Developer Name            Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Lakshmi Madduri                                       05/05/2021              Original version
* Vinay Lingegowda				                        02/07/2022		        User Story 2977662	T1PRJ0314829 - MF 1 - AHT - Ability to navigate to most commonly used pages in CRM (RF)
*******************************************************************************************************************************/
public class LINKS_CONSTANT_HUM {
    Public Static final String STRING_HORIZONTAL='Horizontal';
    Public Static final String STRING_TARGET = 'TARGET';
    Public Static final String STRING_HIDDENTARGET='HIDDENTARGET';
    Public Static final String STRING_DISPLAYCUSTOMLINKSPAGENAME = 'DisplayCustomLinks_VF_HUM';
    Public Static final String STRING_AKA_NAME_SESSION_EXPIRED_ERR = 'The aka name session has expired, please logout of salesforce and log back in again to continue';
    public static final List<String> lstLinksdisplayCommonly = new List<String>{'DisplayCustomLinks_VF_HUM','AuthSummary_VF_Hum', 'BenefitsV2_VF_HUM','BillingDetailsV1_VF_HUM','ClaimDetails_VF_HUM_V3','HUMCaseActionLaunch','MemberIdCards_VF_HUM','PrimaryCare_VF_HUM'};
}