/*****************************************************************************************************************************************************************************
@Apex Class Name  : ClinicalProgramResponse_LH_HUM
@Version          : 1.0
@Created Date     : 
@Test class       : HealthResource_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                                         Original Version, Wrapper for Health Resource functionality
******************************************************************************************************************************************************************************/

public with sharing class ClinicalProgramResponse_LH_HUM {

    @AuraEnabled public List<EnrolledProgramDetail> EnrolledProgramDetails = new List<EnrolledProgramDetail>();   
    @AuraEnabled public List<EligibleProgramDetail> EligibleProgramDetails = new List<EligibleProgramDetail>();
    @AuraEnabled public string internalError;
    
    @AuraEnabled public Boolean bInterventionEnroll = false;
    @AuraEnabled public Boolean bProgramEligible = false;
    
    public class EnrolledProgramDetail{
        @AuraEnabled public String programName;
        @AuraEnabled public String InterventionVendor;        
        @AuraEnabled public String CareManagerName;
        @AuraEnabled public String phoneNumber; 
        @AuraEnabled public String BeginDate;
        @AuraEnabled public String EndDate;
    }
    public class EligibleProgramDetail{
        @AuraEnabled public String sprogramName;      
        @AuraEnabled public String InterventionVendor;        
        @AuraEnabled public String BeginDate;
        @AuraEnabled public String EndDate;
        @AuraEnabled public String Comments;
        @AuraEnabled public string ProgramCode;
    }  
}