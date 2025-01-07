/******************************************************************************************************************
Apex class Name    : MBEPlusRequestCoach_DTO_HUM
Version            : 1.0
Function           : Request DTO for Mbe plus PID Search
Created On         : 03/09/2021
Test Class         : 

Modification Log:
*   Version             Developer                            Date                    Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Jasmeen Shangari                     03/09/2021            Original Version 
------------------------------------------------------------------------------------------------------------------*/
public with sharing class MBEPlusRequestCoach_DTO_HUM
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