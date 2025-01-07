/******************************************************************************************************************
Apex class Name    : SearchMBEPlusResponse_DTO_HUM
Version            : 1.0
Function           : Response DTO for Mbe plus PID Search
Created On         : 08/21/2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                    Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                LakshmiMadduri               289399             02/01/2017            Original Version 
------------------------------------------------------------------------------------------------------------------*/
public with sharing class SearchMBEPlusResponse_DTO_HUM
{
	public GetMemberResponse GetMemberResponse;
	
	public class GetMemberResponse 
	{
		public Body Body;                                                                                                                       										
	}
	
	public class Body
	{
		public Member Member;
	}
	
	public class Member
	{
		public string EnterpriseId ='';
		public string Platform = '';
	}

}