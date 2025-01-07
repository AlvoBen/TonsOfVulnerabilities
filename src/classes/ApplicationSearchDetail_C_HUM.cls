/*
class Name        : ApplicationSearchDetail_C_HUM
Version          : 1.0
Created Date     : 05/03/2019
Function         : This is a controller for showing the Application Search results detail in ApplicationSearchDetail_VF_HUM page 
Test class		: HUMMemberSearchEnrollemtControllerV2Test
***********************************************************************************************************************************
Modification Log:    
   
* Developer Name               Review Number              Date                   Description
*----------------------------------------------------------------------------------------------------------------------------------
* Syed Mubarak T                	                   05/03/2019               Original Version
* Kiran Kumar B										   07/04/2022				User Story 2568652: Billing- Search Enrollment tab- add 'Billing Payment Option Type' field
* Ravikanth									           05/18/2023               User Story 4404737: T1PRJ0191994- MF 126, 127- Display Member Race and Ethnicity in CRM Service- ‘Member Application Detail’ page (Classic)
***********************************************************************************************************************************
*/

public class ApplicationSearchDetail_C_HUM 
{
    public HUMMemberSearchEnrollmentHelper enrollmentDetailWrapper {get;set;}
    
    public String enrollmentDetails {get;set;}
	public Boolean bPaymentOptTypeSwitch {Get { return HUMUtilityHelper.isCRMFunctionalityON('2568652');} Set;}
    public boolean appMemRaceAndEthSwitch {get;set;}
	
	/*
   * Method Name   :   displayDetails 
   * Description   :   This is used to get called using Action Function on page load to deserialize the JSON and display the details
   * Return Type   :   NA
   * Parameters    :   NA
   */
    
    public void displayDetails()
    {
		if(String.isNotBlank(enrollmentDetails))
		{
			appMemRaceAndEthSwitch = HUMUtilityHelper.isCRMFunctionalityON('4404737');
            enrollmentDetailWrapper = (HUMMemberSearchEnrollmentHelper)JSON.deserialize(enrollmentDetails, HUMMemberSearchEnrollmentHelper.class);
		}
    }
}