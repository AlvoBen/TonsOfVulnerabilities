/**************************************************************************** 
Apex Class Name  : EPSServiceSearchGetPCPRequest_H_HUM 
Version          : 1.0  
Created Date     : March 02, 2016
Function         : request helper for Get PCP method in EPS Service
Test Class       : EPSServiceSearchGetPCPRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shruthi Karanth            30353            03/02/2016                   Original Version
***************************************************************************************************/
public class EPSServiceSearchGetPCPRequest_H_HUM
{
    public PcpRequest PcpRequest;
    
    public class PcpRequest 
    {
        public string ApplicationKey = '';
        public MemberContext MemberContext;
    }
    
    public class MemberContext 
    {
        public Member Member;
        public string DateOfService = '';
    }
    
    public class Member
    {
    	public string Source = '';
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
    }
}