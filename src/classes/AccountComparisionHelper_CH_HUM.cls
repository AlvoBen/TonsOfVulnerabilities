/*******************************************************************************************************************************
Apex Class Name : Public class AccountComparisionHelper_CH_HUM
Version         : 1.0
Created On      : 03/09/2021
Function        : This class is used to sort account object records based on lastname.
Test Class      : 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                                  03/09/2021                 Original Version
*******************************************************************************************************************************************************************/
public class AccountComparisionHelper_CH_HUM implements Comparable
{
    public Account objAccount{get;set;}
    public String sLastName{get;set;}
    
    public AccountComparisionHelper_CH_HUM (Account objAcc)
    {
        this.objAccount = objAcc;
        this.sLastName = objAcc.LastName;
    }
    
    public Integer compareTo(Object compareTo)
    {
        AccountComparisionHelper_CH_HUM objCompareTo = (AccountComparisionHelper_CH_HUM)compareTo;
        return sLastName.compareTo(((AccountComparisionHelper_CH_HUM)compareTo).sLastName);
    }
}