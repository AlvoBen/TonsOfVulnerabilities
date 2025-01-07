/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_H_HUM 
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is data layer class for controller DynamicCustomLinksControllerHUM
Test Class       : DynamicCustomLinks_HT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu        27372                    10/05/2015                 Original Version
* Santhi Mandava                                     11/04/2015                 Fixed link issue for CCS profile users.
************************************************************************************************************************************************/

public with sharing class DynamicCustomLinks_H_HUM
{
    /*
    * Method Name : DynamicCustomLinks_H_HUM
    * Description : This is constructor is used to initialize variables for this class
    * Return type : NA
    * Parameter   : NA
    */
    public DynamicCustomLinks_H_HUM()
    {
        sSectionName='';
        lstCustomLinks = new List<DynamicLinksWrapper_H_HUM>();
    }
    
    public String sSectionName{get; set;}
    public List<DynamicLinksWrapper_H_HUM> lstCustomLinks{get; set;}
}