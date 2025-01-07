/**************************************************************************************************************************************
Apex Class Name : CIMedicareMemberResponse_DTO_HUM
Version         : 1.0
Created On      : 07/29/2016
Function        : class used to build response for CI Medicare Member service
Test Class      : CIMedicareMemberResponse_DTO_T_HUM

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth             34224                   07/29/2016                   Original Version      
****************************************************************************************************************************************/
public with sharing class CIMedicareMemberResponse_DTO_HUM
{
    public MedicareMemberInfoResponse MedicareMemberInfoResponse;
    
    public class MedicareMemberInfoResponse
    {
        public String LISIndicator = '';
        public String SubsidyEffectDate = '';
        public String SubsidyEndDate = '';
        public String LISSubsidyLevel = '';
        public String LISCopayCategory = '';
        public String LTSSIndicator = '';
        public String ESRDIndicator = '';
        public String SPAPIndicator = '';
        public String SNPIndicator = '';
        public String SNPTypeCode = '';
        public String POAIndicator = '';
        public String POALastName = '';
        public String POAFirstName = '';
        public String POAPhoneNumber = '';
        public String POAType = '';
        public String POAApplDate = '';
        public ErrorList Errors = null;
    } 
        
    public class ErrorList
    {
        public list<Error> Error = null;
    }
    
    public class Error 
    {
        public String code = '';
        public String description = '';
    }    
}