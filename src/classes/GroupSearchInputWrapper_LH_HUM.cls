/*******************************************************************************************************************************
Apex Class Name : GroupSearchInputWrapper_LH_HUM
Version         : 1.0
Created On      : 09/18/2020
Function        : This class is used for input wrapper from the LWC component
Test Class		: GroupSearch_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	09/18/2020                	Original Version
*******************************************************************************************************************************************************************/
public class GroupSearchInputWrapper_LH_HUM {
    
    @AuraEnabled  public String sGroupName{get; set;}
    @AuraEnabled  public String sGroupNumber{get; set;}
    @AuraEnabled  public String sState{get; set;}
    @AuraEnabled  public Boolean sUnknownGroupCheck{get;set;}
   
    public GroupSearchInputWrapper_LH_HUM() {
        this.sGroupName= sGroupName;
        this.sGroupNumber= sGroupNumber;
        this.sState= sState;
        this.sUnknownGroupCheck = sUnknownGroupCheck;
       }
 	}