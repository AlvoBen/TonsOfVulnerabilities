/*******************************************************************************************************************************
Apex Class Name : CreditableCoverageRequest_DTO_HUM.cls
Version         : 1.0
Created On      : 21/02/2018
Function        : Wrapper class for Creditable Coverage .

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sumit Kumar                                         21/02/2016                  Original Version
*********************************************************************************************************************************/

public with sharing class CreditableCoverageRequest_DTO_HUM
{
    Public SaveCCTemplate SaveCCTemplateRequest;
    public class SaveCCTemplate
    {
        Public String ReceiptDate='';
        Public String MemberID='';
        Public String EnrolleeFirstName=''; 
        Public String EnrolleeLastName='';
        Public String CRMCaseNumber='';
        Public String CRMCaseCreateDate=''; 
        Public String TrackID='';
        Public String EmpUnionCoverage='';
        Public String EmpUnionCoverageStartDate='';
        Public String EmpUnionCoverageEndDate='';
        Public String StateCoverage='';
        Public String StateCoverageStartDate='';
        Public String StateCoverageEndDate='';
        Public String VABenefits='';
        Public String VABenefitsStartDate='';
        Public String VABenefitsEndDate='';
        Public String TricareMilitaryCoverage='';
        Public String TricareMilitaryCoverageStartDate='';
        Public String TricareMilitaryCoverageEndDate='';
        Public String MedigapCreditableCoverage='';
        Public String MedigapCreditableCoverageStartDate='';
        Public String MedigapCreditableCoverageEndDate='';
        Public String IndianHealthService='';
        Public String IndianHealthServiceStartDate='';
        Public String IndianHealthServiceEndDate='';
        Public String PaceCoverage='';
        Public String PaceCoverageStartDate='';
        Public String PaceCoverageEndDate='';
        Public String DiffSourceCoverage='';
        Public String DiffSourceCoverageStartDate='';
        Public String DiffSourceCoverageEndDate='';
        Public String HumanaCoverage='';
        Public String HumanaCoverageStartDate='';
        Public String HumanaCoverageEndDate='';
        Public String ExtraMedicareCoverage='';
        Public String ExtraMedicareCoverageStartDate='';
        Public String ExtraMedicareCoverageEndDate='';
        Public String HurricaneKatrinaCoverage='';
        Public String HurricaneKatrinaCoverageStartDate='';
        Public String HurricaneKatrinaCoverageEndDate='';
        Public String PuertoRicoReformaCoverage='';
        Public String PuertoRicoReformaCoverageStartDate='';
        Public String PuertoRicoReformaCoverageEndDate='';
        Public String NeverHadCreditibleCoverage='';
    }

}