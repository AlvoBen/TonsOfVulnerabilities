/*******************************************************************************************************************************
Apex Class Name : PharmacyBenefitsSoltrnRequest_H_HUM.cls
Version         : 1.0
TestClass       : PharmacyBenefits

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* M K Manoj                                           11/04/2022                  Original Version (US-3578373, 3578548)
*****************************************************************************************************************************************/
public with sharing class PharmacyBenefitsSoltrnResponse_H_HUM {

    public BalanceInformation BalanceInformation;

    public class BalanceInformation 
    {
        public List<Balance> Balance;
        public String error = '';
    }
    
    public class Balance 
    {
        public String Vendor = '';
        public String StartDate = '';
        public String EndDate = '';
        public decimal AvailableBalance;
        public boolean RollOverFlag;
        public String BalanceType = '';
        public planInformation planInformation;
        public List<Cards> Cards;
        public String ResponseCode = '';
        public String ResponseMessage = '';
    }
    
    public class planInformation 
    {
        public String ContractId = '';
        public String PlanNumber = '';
        public String PlanOptionNumber = '';
    }

    public class Cards{
        
            public String  CardNumber='';
            public String  Last4='';
            public String Status= '';

            }
    
   
}