/**
   @Author Gargey Sharma
   @name Go365KidsDelegateHistory_DTO_HUM 
   @CreateDate 02/06/2016
   @Description DTO class for kids delegate history section
   @Version 1.0
   @reference REQ - 290969
   @Test Class Go365KidsDelegateHistory_T_HUM 
*/
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         	   70895                  02/06/2016                     Original version
********************************************************************************************************************************/
public with sharing class Go365KidsDelegateHistory_DTO_HUM 
{
	public string EffectiveDate{get; set;}
	public string Delegate{get; set;}
	public string Action{get; set;}    
}