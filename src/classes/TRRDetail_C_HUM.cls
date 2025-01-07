/*****************************************************************************************************************************************************************************
Apex Class Name :  TRRDetail_C_HUM
Version: 2.0
Created On      : 05/02/2019

Modification Log :  
 * Developer Name           Code Review                  Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Seema Kaulgi                                       03/15/2019          REQ - 381422 SF - Ability for all CRM users to SEARCH for member specific Transaction Reply Reports (TRR) in CRM
*  Vardhman Jain                                      08/18/2021          REQ - US 2098896  - Add Fields to TRR Reply Detail
*  Nilesh Gadkar				      				  05/18/2023	  	  User Story 4407442: T1PRJ0191994- MF 128, 129- Display Member Race and Ethnicity in CRM Service- ‘TRR Reply Detail’ page (Classic)
*/

public class TRRDetail_C_HUM
{
	public HUMMemberSearchEnrollmentHelper TRRenrollmentDetailWrapper {Get;Set;}

	public string TRRenrollmentDetails {Get;Set;}
	
	public boolean trrSwitch {get;set;}

	public boolean SwitchRaceAndEthinicity {get;set;}
		
	public void displayDetails()
	{
		trrSwitch = HUMUtilityHelper.isCRMFunctionalityON('2098896');
		SwitchRaceAndEthinicity = HUMUtilityHelper.isCRMFunctionalityON('4407442');
		TRRenrollmentDetailWrapper = (HUMMemberSearchEnrollmentHelper)JSON.deserialize(TRRenrollmentDetails, HUMMemberSearchEnrollmentHelper.class);
	}

}