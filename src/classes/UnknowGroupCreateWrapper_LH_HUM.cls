/*******************************************************************************************************************************
Component Name : UnKnowGroupCreateWrapper_LH_HUM
Version        : 1.0
Created On     : 6/23/2021
Function       : This component is for creating Unknow Group account functionality on Group Search tab
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
	1.0			   Surendra Vemulapalli								06/23/2021					US1464387- Creating Unknown Group Account (Health Cloud Lightning App)
**************************************************************************************************************************************/

public without sharing class UnknowGroupCreateWrapper_LH_HUM {

    @AuraEnabled  public String unAccountName;
    @AuraEnabled  public String unTaxId;
    @AuraEnabled  public String unBillingCity;
    @AuraEnabled  public String unWorkEmail;
    @AuraEnabled  public String unGroupNumber;
    @AuraEnabled  public String unBillingStreet;
    @AuraEnabled  public String unZipCode;
    @AuraEnabled  public String unPhone;
    @AuraEnabled  public String unState;
 
      
    public UnknowGroupCreateWrapper_LH_HUM() {
        
        this.unAccountName = unAccountName;
        this.unTaxId= unTaxId;
        this.unBillingCity=unBillingCity;
        this.unWorkEmail=unWorkEmail;
        this.unGroupNumber= unGroupNumber;
        this.unBillingStreet =unBillingStreet;
        this.unZipCode	=unZipCode;
        this.unPhone	=unPhone;
        this.unState = unState;
    }
}