/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_Response_H_HUM
Version          : 1.0
Created Date     : 08/28/2018
Function         :Response Helper to get account, policy and policy member Info
Test Class       : Claim_Send_Statement_Response_T_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
*  Vijaya Koriprolu                           08/28/2018          Original Version
***************************************************************************************************/


public class Claim_Send_Statement_Response_H_HUM
{
    public static Claim_Send_Statement_Response_H_HUM parse(String response) 
    {
    
    return (Claim_Send_Statement_Response_H_HUM) System.JSON.deserialize(response, Claim_Send_Statement_Response_H_HUM.class);
    }

public StandardizeAddressResponse StandardizeAddressResponse;

    public class StandardizeAddressResponse{
    
        public String SuccessFlag;
        public String AddressLine1;
        public String City;
        public String StateCode;
        public String ZipCode;
        public String StateID;
        public String ZipCodePlus;
        public String CountyName;
        public String CountyID;
        public String Valid;
        public String ChangeIndicator;
    }
}