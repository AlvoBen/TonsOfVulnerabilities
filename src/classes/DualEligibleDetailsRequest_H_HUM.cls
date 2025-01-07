/*****************************************************************************************
Apex class Name    : DualEligibleDetailsRequest_H_HUM
Version            : 1.0
Function           : This is the Request class for GetDualInfo Service 
Created Date       : 05/18/2019 
Test Class         : DualEligibleDetailsRequest_H_T_HUM

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Pavan Karnam                                   05/18/2019          Request Class for Dual/Moop Project.   
*********************************************************************************************************************************/ 

public class DualEligibleDetailsRequest_H_HUM{
    
    public GetMember GetMember;
    
    public class GetMember{
        public Member Member;
        
    }
    public class Member
    {
        public DualStatusList DualStatusList;
        public DeemingPeriodList DeemingPeriodList;
        public GetCriteria GetCriteria;
    }
    
    public class DualStatusList{
        public string DualStatus = '';
    }
    
    public class DeemingPeriodList{
        public string DeemingPeriod = '';
    }
    
    public class GetCriteria{
        public PersonIdCriteria PersonIdCriteria;
        
    }
    public class PersonIdCriteria{
        public string  AsOfDate = '2016-01-01';
        public PeriodList PeriodList;
        public string  PersonId = '';
    }
    
    public class PeriodList{
        public list<String> Period = new list<String>{'Current','Past','Future'};
        
                        
    } 
      
}