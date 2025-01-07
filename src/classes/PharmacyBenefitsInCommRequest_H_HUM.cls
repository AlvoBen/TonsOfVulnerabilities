public with sharing class PharmacyBenefitsInCommRequest_H_HUM
{
  public String MemberIdentityQualifier;
  public String MemberIdentity;
  public List<VendorInformation> VendorInformation;
  
  public class VendorInformation
  {
    public String VendorName = '';  
    public List<VendorParams> VendorParams;
  }
  
  public class VendorParams
  {
    public String VendorKey = '';  
    public String VendorValue= '';
  }
}