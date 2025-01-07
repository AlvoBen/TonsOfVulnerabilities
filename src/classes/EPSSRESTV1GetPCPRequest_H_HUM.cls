/**************************************************************************** 
Apex Class Name  : EPSSRESTV1GetPCPRequest_H_HUM 
Version          : 1.0  
Created Date     : April 11, 2016
Function         : request helper for Get PCP method in EPS Service REST V1.0
Test Class       : EPSSRESTV1GetPCPRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey SHarma             30905             04/11/2016                   Original Version
***************************************************************************************************/
public class EPSSRESTV1GetPCPRequest_H_HUM
{
    public GetPcpRequest GetPcpRequest;
    
    public class GetPcpRequest 
    {
        public string ApplicationKey = '';
        public MemberContext MemberContext;
        public string StatusEvaluation = '';
        public string IsSourceView = '';
    }
    
    public class MemberContext 
    {
        public Member Member;
        public string DateOfService = '';
    }
    
    public class Member
    {
        public string DateOfBirth = '';
        public string Gender = '';
        public string SSN = '';
        public string Age = '';
        public Coverage Coverage;
    }
    
    public class Coverage 
    {
        public string DisplayGroupID = '';
        public string DependentCode = '';
        public string Source = '';
    }
}