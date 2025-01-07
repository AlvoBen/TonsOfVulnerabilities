/*******************************************************************************************************************************
Apex Class Name : Public class AccountComparisionHelper_H_HUM
Version         : 1.0
Created On      : 11/29/2018
Function        : This class is used to sort account object records based on lastname.
Test Class      : AccountComparisionHelper_T_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Santhi Mandava                                      11/29/2018                 Original Version
*******************************************************************************************************************************************************************/
Public class AccountComparisionHelper_H_HUM Implements Comparable
{
    Public Account objAccount{get;set;}
    Public String sLastName{get;set;}
    
    Public AccountComparisionHelper_H_HUM (Account objAcc)
    {
        this.objAccount = objAcc;
        this.sLastName = objAcc.LastName;
    }
    
    public Integer compareTo(Object compareTo)
    {
        AccountComparisionHelper_H_HUM objCompareTo = (AccountComparisionHelper_H_HUM)compareTo;
        return sLastName.compareTo(((AccountComparisionHelper_H_HUM)compareTo).sLastName);
    }
}