/*******************************************************************************************************************************
Apex Class Name : CreditableCoverageResponse_DTO_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Wrapper class for Creditable Coverage Response .

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sumit Kumar                                       13/02/2016                  Original Version
*********************************************************************************************************************************/

public with sharing class CreditableCoverageResponse_DTO_HUM
{
    Public SaveCCTemplateResponse SaveCCTemplateResponse;
    public class SaveCCTemplateResponse 
    {
        public String CRMCaseNumber='';
        public String Type='';
        public String ReturnCode='';
        public String Description='';
    }
    
}