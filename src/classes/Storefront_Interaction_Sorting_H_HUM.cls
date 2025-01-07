/********************************************************************************************************************************************************
Apex Class Name  : Storefront_Interaction_Sorting_H_HUM
Version          : 1.0 
Created Date     : May 14 2021
Modification Log :       
* Developer                         Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
*  Luke P. Cecil                   	05/14/2021           User Story 2200821: T1PRJ0001894 - MF 8 - CRM Storefront Home Page: Pagination Column Sort
*******************************************************************************************************************************************************/
public with sharing class Storefront_Interaction_Sorting_H_HUM implements Comparable  {
    public Storefront_Interaction__c interRefComp;
    public static string sortingDirection;    
    public Storefront_Interaction_Sorting_H_HUM(Storefront_Interaction__c interRef) {
        interRefComp = interRef;
    }       
    public Integer compareTo(Object compareTo){             
        Storefront_Interaction_Sorting_H_HUM comparableAccountName = (Storefront_Interaction_Sorting_H_HUM)compareTo;               
        Integer returnValue = 0;
        STring nameToComp;
        STring name;
        
        name =interRefComp.account__r.name;
        nameToComp = comparableAccountName.interRefComp.account__r.name;
        if(sortingDirection=='asc'){
            if( name> nameToComp){           
                returnValue = 1;
            }
            else if(name < nameToComp){           
                returnValue = -1;
            }
        }else{
            if( name > nameToComp){           
                returnValue = 1;
            }
            else if(name < nameToComp){           
                returnValue = -1;
            }
        }
              
        return returnValue;             
    }
}