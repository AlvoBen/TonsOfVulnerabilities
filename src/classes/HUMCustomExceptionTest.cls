/******************************************************************************************************************************
Apex Class Name  : HUMCustomExceptionTest
Version          : 1.0 
Created Date     : July 13 2014
Function         : Test class for HUMCustomException

Modification Log :
 *   Modification ID     Developer                  Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *        1.0         Chaitanya Kumar               17152               07/13/2014          Original Version                                                                                
 *********************************************************************************************************************************/

@isTest
private class HUMCustomExceptionTest 
{
	 /*
     * Method Name   : getApplicationException
     * Description   : Based on the information passed to Custom Exception , check if exception is from Application
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void getApplicationException() 
    {
    	  //Load Constants from Custom Setting
          HUMTestDataHelperTest.getHUMConstantsData();
          HUMCustomException customException;
          String exceptionType;
          
          Test.startTest();
          customException = new  HUMCustomException(new HUMCustomExceptionHelper('System.DMLException','REQUIRED_FIELD_MISSING','',''));
          exceptionType = HUMCustomException.getExceptionType(customException.exceptionWrapper);
          Test.stopTest();
          
          System.assertEquals(exceptionType ,HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c);     
    }
    
     /*
     * Method Name   : getIntegrationException
     * Description   : Based on the information passed to Custom Exception , check if exception is from Application
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void getIntegrationException() 
    {
        //Load Constants from Custom Setting
          HUMTestDataHelperTest.getHUMConstantsData();
          HUMCustomException customException;
          String exceptionType;
          
          Test.startTest();
          customException =  new HUMCustomException(new HUMCustomExceptionHelper('101','Failied Integration','End Point','Integration Error'));
          exceptionType = HUMCustomException.getExceptionType(customException.exceptionWrapper);
          Test.stopTest();
          
          System.assertEquals(exceptionType ,HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c);  
    }
}