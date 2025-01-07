/*****************************************************************************************************
 Apex Class Name  : QuickStartHPSpecialtyCalls_DTO_HUM
 Version   : 1.0 
 Created Date  : 05/10/2021
 Function  : DTO to return Service Model Type, GA Rights and Hide LogCode Data data   
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* AshokKumar Nutalapati                         05/10/2021                 Original Version
*******************************************************************************************************/
public with sharing class QuickStartHPSpecialtyCalls_DTO_HUM 
{
    public String  ServiceModelType{get;set;}
    public Boolean GARights{get;set;}
    public Boolean HideLogCode{get;set;}
}