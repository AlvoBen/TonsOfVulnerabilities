public  with sharing class PharmacyBenefitsResponse_H_HUM
{
    public List<OTCBalance> OTCBalance;
    public String error = '';
    
    public class OTCBalance 
    {
        public String OTCVendor = '';
        public String OTCStartDate = '';
        public String OTCEndDate = '';
        public decimal OTCAvailableBalance;
        public planInformation planInformation;
        public String ResponseCode = '';
        public String ResponseMessage = '';
    }
    
    public class planInformation 
    {
        public String ContractId = '';
        public String PlanNumber = '';
        public String PlanOptionNumber = '';
    }
}