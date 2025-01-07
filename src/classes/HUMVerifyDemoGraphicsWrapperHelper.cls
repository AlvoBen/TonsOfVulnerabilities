/*******************************************************************************************************************************
Apex Class Name : HUMVerifyDemoGraphicsWrapperHelper
Version         : 1.0
Created On      : 09/08/2014
Function        : Used to display Verify Demographics section on Account Page
Test Class      : HUMVerifyDemoGraphicsWrapperHelperTest  

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana         17674                     09/08/2014                  Original Version
* Ankima Srivastava										 03/05/2021					 Made the variable aura enabled
*******************************************************************************************************************************/  
  
public with sharing class HUMVerifyDemoGraphicsWrapperHelper
{
    @AuraEnabled public String sLastVerifiedBy {get;set;}
    @AuraEnabled public Date sLastVerifiedOn {get;set;}
    @AuraEnabled public integer iDaysSinceLastVerified {get;set;}
}