/******************************************************************************************************************
Apex class Name    : SearchMBEPlusRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for Mbe plus PID Search
Created On         : 08/21/2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                    Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Lakshmi Madduri             289399              02/01/2017            Original Version 
------------------------------------------------------------------------------------------------------------------*/
public with sharing class SearchMBEPlusRequest_DTO_HUM
{
	public GetMember GetMember;
	
	public class GetMember 
	{
		public Member Member;
	}
	
	public class Member
    {
	    public PlatformPointerList PlatformPointerList;
	    public GetCriteria GetCriteria;
	}
	
	public class PlatformPointerList
	{
		public string PlatformProxyKey ='';
	}
	
	public class GetCriteria
	{
		public SourcePersonIdCriteria SourcePersonIdCriteria;
	}
	
	public class SourcePersonIdCriteria
	{
        public string AsOfDate = '';
        public string SourcePersonId = '';
        public List<string> Platform;
        public PeriodList PeriodList;
	}
	
	public class PeriodList
	{
	    public List<String> Period;
	}
}