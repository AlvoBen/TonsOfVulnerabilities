/*******************************************************************************************************************************
Apex Class Name : HUMDueDateHelper
Version         : 1.0
Created On      : 7/1/2014
Function        : Used to set a global variable to skip the dueDate functionality upon case record update
Test Class      : HumUpdateCaseMapToTaskHelperTest  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* mrudula jaddu               17093                           7/1/2014                  Original Version
*******************************************************************************************************************************/

public with sharing class HUMDueDateHelper{
    
    public static boolean bSkipFlag = false;
}