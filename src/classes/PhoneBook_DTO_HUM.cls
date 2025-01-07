/***********************************************************************************************************************************
* Apex Class Name  : PhoneBook_DTO_HUM 
* Version          : 1.0
* Created Date     : 22nd January, 2021
* Function         : CRM Search Integration
* Test Class       : 
****************************************************************************
* Modification Log:
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Isha Gupta		                           01/22/2021            Original Version
**********************************************************************************************************************************/
public with sharing class PhoneBook_DTO_HUM {
    public GetCallDataResponse GetCallDataResponse;
	public class GetCallDataResponse {
		public GetCallDataResult GetCallDataResult;
	}
	public class GetCallDataResult {
		public String Application;
		public String UUID;	
		public CallDataValues CallDataValues;
	}
	public class CallDataValues {
		public CallDataValuesList[] CallDataValuesList;
	}
	public class CallDataValuesList {
		public String DataName;	
		public String DataValue;	
	}
	

}