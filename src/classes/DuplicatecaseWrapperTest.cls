/**********************************************************************************************************************************************************************************************       
Apex Class Name  : DuplicatecaseWrapperTest_T_HUM.cls 
Created Date     : Sep 9, 2020
Function         : Executes SOQL in support of the Duplicate cases
Test Class       : DuplicatecaseWrapperTest_T_HUM

**Modification Log
* Developer Name                   Date                       Description
*----------------------------------------------------------------------------------------------------------------------
* Kiran Kumar                     05/29/2020                  Original Version
***************************************************************************************************************************/

@istest
private class DuplicatecaseWrapperTest {
    @isTest
    static void validateDuplicateWrapperTest(){
        DuplicatecaseWrapper wrapObj = new DuplicatecaseWrapper();
        wrapObj.Title = 'Dummy';
        wrapObj.Id = '44444444';
        wrapObj.URL = 'https://Google.com';
        wrapObj.lastcomment = 'test comment';
        wrapObj.commentdate = '09/5/2020';
        system.assertEquals(wrapObj.Title, 'Dummy');
    }

}