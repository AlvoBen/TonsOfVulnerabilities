/******************************************************************************************************************
Apex class Name    : Billing_Wrapper_LDTO_HUM
Version            : 1.0
Function           : Parameter transfer for Billing Service
Created On         : 05/23/2022
Test Class         : Billing_Wrapper_LDTO_LT_HUM

Modification Log:
* Developer                   Code Review            Date                       Description
* -----------------------------------------------------------------------------------------------------------------
* Prashant Moghe                                     05/23/2022                 Original Version
------------------------------------------------------------------------------------------------------------------*/
public with sharing class Billing_Wrapper_LDTO_HUM {
    public string sRecId;
    public String sGroupName;
    public String sObjName;
    public String sPlatformCode;
    public String sUserNetworkId;
    public Billing_Wrapper_LDTO_HUM(){
    }
    
    public Billing_Wrapper_LDTO_HUM(String sRecId, String sGroupName, String sObjName, String sPlatformCode, String sUserNetworkId){
        this.sRecId =   sRecId;
        this.sGroupName = sGroupName;
        this.sObjName = sObjName;
        this.sPlatformCode = sPlatformCode;
        this.sUserNetworkId = sUserNetworkId;
       }
}