/**
   @Author Gargey Sharma
   @name Go356KidsDelegates_DTO_HUM
   @CreateDate 12/09/2016
   @Description Wrapper class for data table for Go365 Kids Delegate section
   @Version 1.0
   @reference REQ - 290967: Go365 Kids Delegates section
   @Test Class 
*/ 
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         		70895                12/09/2016                     Original version
********************************************************************************************************************************/
public with sharing class Go356KidsDelegates_DTO_HUM 
{
    public string Name{get; set;}
    public string DateOfBirth{get; set;}
    public string StartDate{get; set;}
}