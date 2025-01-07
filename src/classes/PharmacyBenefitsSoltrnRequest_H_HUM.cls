/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefitsSoltrnRequest_H_HUM.cls
Version         : 1.0
TestClass       : PharmacyBenefits

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* M K Manoj                                           11/04/2022                  Original Version (US-3578373, 3578548)
*****************************************************************************************************************************************/
public with sharing class PharmacyBenefitsSoltrnRequest_H_HUM {
  
  public String MemberIdentityQualifier;
  public String MemberIdentity;
  public List<PlanInformation> PlanInformation;
  
  public class PlanInformation
  {
    public String ContractId = '';  
    public String PlanNumber = '';
    public String PlanOptionNumber = '';
  }
}