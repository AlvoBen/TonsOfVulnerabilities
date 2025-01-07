/*****************************************************************************************
Apex class Name    : DualEligibleDetailsResponse_H_HUM
Version            : 1.0
Function           : This is the Response class for GetDualInfo Service 
Created Date       : 05/18/2019 
Test Class         : DualEligibleDetailsResponse_H_T_HUM

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Pavan Karnanm                                    05/18/2019          Controller Class for Dual/Moop Project.   
*********************************************************************************************************************************/ 

public with sharing class DualEligibleDetailsResponse_H_HUM{
    
    
    public static DualEligibleDetailsResponse_H_HUM parse(String response) 
    {
        return (DualEligibleDetailsResponse_H_HUM) System.JSON.deserialize(response, DualEligibleDetailsResponse_H_HUM.class);
    }
    
    public GetMemberResponse GetMemberResponse;
    
    public class GetMemberResponse{
        public Body Body;
    }
    
    public class Body
    {
       public List<DualStatusList> DualStatusList;
        public List<DeemingPeriodList> DeemingPeriodList;
        
       
    }   
    
    public class DualStatusList{
    
        public String EffectiveDate = '';
        public String EndDate = '';
        public String Value = '';
        public String Code = ''; 
        public String Indicator = '';
          
    }
    
    
    
    
    public class DeemingPeriodList{
    
        public String EffectiveDate = '';
        public String EndDate = '';
        public String Indicator;
          
    }
    
}