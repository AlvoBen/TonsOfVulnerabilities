/******************************************************************************************************************************
Apex Class Name  : HumanaCodes_DTO_HUM
Version          : 1.0 
Created Date     : 29th March 2019
Function         : Wrapper Class for Humana Codes                    
Test Class       : 
Modification Log :
 *  Developer              Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
    Satyam Malviya                            03/29/2019            Original Version                      
********************************************************************************************************************************/
public with sharing class HumanaCodes_DTO_HUM {
  public String Id {get;set;}
  public String sCode {get;set;}
  public String sCodeSet {get;set;}
  public String sDescription {get;set;}
  public String sStatus {get;set;}  
  public String sLastModifiedDate {get;set;}
}