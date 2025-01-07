public with sharing class PharmacyBenefitsDSTRequest_H_HUM
{
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