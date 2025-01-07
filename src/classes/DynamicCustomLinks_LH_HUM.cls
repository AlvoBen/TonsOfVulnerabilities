/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_LH_HUM 
Version          : 1.0
Created Date     : 07/10/2021
Function         : This is helper class for controller DynamicCustomLinks_LC_HUM
Test Class       : DynamicCustomLinks_LT_HUM
Code Coverage    : 100%
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Alwal                                  07/13/2021                   Original Version
***********************************************************************************************************************************************/

public with sharing class DynamicCustomLinks_LH_HUM
{
    /*
    * Method Name : DynamicCustomLinks_LH_HUM
    * Description : This is constructor is used to initialize variables for this class
    * Return type : NA
    * Parameter   : NA
    */
    public DynamicCustomLinks_LH_HUM()
    {
        sSectionName='';
        lstCustomLinks = new List<DynamicLinksWrapper_LH_HUM>();
    }
    
    @AuraEnabled public String sSectionName{get; set;}
    @AuraEnabled public List<DynamicLinksWrapper_LH_HUM> lstCustomLinks{get; set;}
}