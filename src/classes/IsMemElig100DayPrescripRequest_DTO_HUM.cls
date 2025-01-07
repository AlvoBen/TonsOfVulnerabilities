/*******************************************************************************************************************************
Apex Class Name : IsMemElig100DayPrescripRequest_DTO_HUM.cls
Version         : 1.0
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Tharun Madishetti                                   04/14/2023                 US- 3837439: T1PRJ0714381- MF1: (PLT) 100 Day - CRM Supply Eligibility Indicator (Classic)
*****************************************************************************************************************************************/
public with sharing class IsMemElig100DayPrescripRequest_DTO_HUM {

    
    public SearchRequest SearchRequest;

	public class SearchRequest {
		public String PlanID;
	}
}