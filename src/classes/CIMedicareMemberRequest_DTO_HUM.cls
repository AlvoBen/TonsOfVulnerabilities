/**************************************************************************************************************************************
Apex Class Name : CIMedicareMemberRequest_DTO_HUM
Version         : 1.0
Created On      : 07/29/2016
Function        : class used to build response for CI Medicare Member service
Test Class      : CIMedicareMemberRequest_DTO_T_HUM

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth             34224                   07/29/2016                   Original Version      
****************************************************************************************************************************************/
public with sharing class CIMedicareMemberRequest_DTO_HUM
{    
    public CIMedicareMember CIMedicareMemberRequest;
    
    public class CIMedicareMember
    {
        public String MedicareID = '';
        public String MemberCardID = '';
        public String GroupID = '';
        public String BenefitSequenceNumber = '';
        public String AsOfDate = '';
        public String LISInfo = '';
        public String LTSSInfo = '';
        public String ESRDInfo = '';
        public String SPAPInfo = '';
        public String SNPInfo = '';
        public String POAInfo = '';
    }
}