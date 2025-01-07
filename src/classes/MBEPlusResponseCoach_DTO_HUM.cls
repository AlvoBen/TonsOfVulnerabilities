/******************************************************************************************************************
Apex class Name    : MBEPlusResponseCoach_DTO_HUM
Version            : 1.0
Function           : Response DTO for Mbe plus PID Search
Created On         : 03/09/2021
Test Class         : 

Modification Log:
*   Version             Developer                        Date                  Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Jasmeen Shangari                03/09/2021            Original Version 
------------------------------------------------------------------------------------------------------------------*/
public with sharing class MBEPlusResponseCoach_DTO_HUM
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