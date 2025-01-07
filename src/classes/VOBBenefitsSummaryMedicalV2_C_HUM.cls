/************************************************************************************************************************************************
  Apex Class Name  : VOBBenefitsSummaryMedicalV2_C_HUM
  Version          : 1.0 
  Created Date     : 20/05/2020
  Function         : Class to VOBBenefitsSummaryMedicalV2_CMP_HUM.component
  Test Class       : N/A
  
  * Version                   Developer Name            Code Review                 Date                       Description
 -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  * 1.0                       Sunil Kumar Reddy                                        20/05/2020                 Original Version
  * 1.1                       Suresh Kokkeragadda                                      06/23/2020                 User Story 1200954: PR00091574 - MF 1 - AHT - Link Hospital Network Codes to Search Code Sets on Benefits and Accums page (CRM)
  * 1.2                       Prasanthi Kandula                                        03/25/2021                 US:1437221 Displaying Paid Thru Date for DEN and MED Benefit Summary
  ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
  public with sharing class VOBBenefitsSummaryMedicalV2_C_HUM {
    public Boolean bOnAndOffFlag{get;set;}
	public Boolean bDisplayPTDOnMedical {get;set;}
    public VOBBenefitsSummaryMedicalV2_C_HUM() {
        bOnAndOffFlag = HUMUtilityHelper.isCRMFunctionalityON('1200954');
		bDisplayPTDOnMedical = HUMUtilityHelper.isCRMFunctionalityON('1437221_Med'); 
    }
}