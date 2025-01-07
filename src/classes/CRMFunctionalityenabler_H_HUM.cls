/*******************************************************************************************************************************
Apex Class Name :  CRMFunctionalityenabler_H_HUM
Version         : 1.0
Created On      : 01/28/2020
Function        : This class used for general purpose to activate the functionality 
Test Class      : CRMFunctionalityenabler

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty                                  01/28/2020                  Original Version
*********************************************************************************************************************************/

Public with sharing class CRMFunctionalityenabler_H_HUM
{
public static boolean checkcriteria {get;set;}
public static  CRM_Functionality_Enablement__mdt Checkfunct;

    Public static  boolean Checkflagvalue( string customvalue)
    
    {
    try {
        dateTime todaysDate = dateTime.now();
        
        if(customvalue!=null) Checkfunct=[select id ,DeveloperName,End_Date__c,Start_Date__c,Active__c 
        from CRM_Functionality_Enablement__mdt where DeveloperName=:customvalue and Active__c =true  limit 1];
       
       if(Checkfunct != null)
       {
           if(Checkfunct.Start_Date__c != null && Checkfunct.End_Date__c!=null)
           {
           
            if(Checkfunct.Start_Date__c<= todaysDate && Checkfunct.End_Date__c >= todaysDate)
            {
                checkcriteria =true;
                system.debug('date starte'+checkcriteria );
            }
            else
            {
                checkcriteria =false;
            }
           }
           
          else if(Checkfunct.Start_Date__c != null || Checkfunct.End_Date__c!=null)
              {
                   if(Checkfunct.Start_Date__c <= todaysDate)
                       {    
                           checkcriteria =true;
                           system.debug('only start'+checkcriteria );
                       }
                   else if(Checkfunct.End_Date__c>=todaysDate)
                       {
                           checkcriteria =true;
                       }
                   else 
                       {
                           checkcriteria =false;
                       }
              }
             
             else if(Checkfunct.Start_Date__c == null && Checkfunct.End_Date__c==null)
             {
                 checkcriteria  =true;
                 system.debug('simplecheckwthoutdate'+checkcriteria );
             }
             
        } 
        system.debug('criteria'+checkcriteria );
        boolean bflag = checkcriteria ;
        return bflag;
   }
     catch(Exception ex)
        {
            //HUMExceptionHelper.logErrors(ex, 'CRMFunctionalityenabler', 'Checkflagvalue');
            return false;
        }   
   }
   
}