/*******************************************************************************************************************************************************
Apex Class Name  : DCNValidation_DTO_HUM
Version          : 1.0
Created Date     : 06/29/2020
Function         : This class is wrapper class to serialize Data received from Ehub
Test Class       : DCNValidation_DTO_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sumanth Naredla                                 06/29/2020                  Original version
************************************************************************************************************************************************/

public with sharing class DCNValidation_DTO_HUM
{
    public string DCN {get; set;}
    public string Source {get; set;}
    
}