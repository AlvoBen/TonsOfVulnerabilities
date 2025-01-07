/********************************************************************************************************************************************
Apex Class Name  : HUMIntegrationUtilityHelperTest
Version          : 1.0
Created On       : August 09 2014
Function         : Test class to HUMIntegrationUtilityHelper Apex class

Modification Log :
* Developer Name          Code Review              Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma              17339                08/09/2014             Original Version
* Shreya Choodamani         17339                08/14/2014             Added Test Classes for Alert and Alert Prompt Save functions
* Anil Pilaka               18114                09/23/2014             Fixed test class failure due to mandatory Alert Id on alert prompts
* Pradeep Raju              20215                01/07/2015             Updated the HUMIntegrationUtilityHelper as part of REQ-66086
* Ranjeeth Nagishetty                            01/10/2015             REQ-95950 validation rule test class
* Shreya Choodamani                              01/12/2015             Fix the test class failure.
********************************************************************************************************************************************/

@isTest
private class HUMIntegrationUtilityHelperTest
{
    /*
    * Method name : TestxmlInputStringParser
    * Description : Test Method to return tags and tag values in a map
    * Return Type : void
    * Parameter   : NA
    */
    private static testMethod void TestxmlInputStringParser()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            map<String,String> mapTagsToValues=new  map<String,String>();
            String str = '<books><book1><book2><book3 author="Chatty">Test1</book3></book2></book1></books>';
            Test.startTest();
            mapTagsToValues= HUMIntegrationUtilityHelper.xmlInputStringParser(str);
            system.assertEquals(mapTagsToValues.size()>0, true);
            Test.stopTest();
        }
    }
    
    
    /*
    * Method name : TestcollectChildNodes
    * Description : Test Method to get child nodes
    * Return Type : void
    * Parameter   : NA
    */
    private static testMethod void TestcollectChildNodes()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            Map<String,String> resultantMap = new Map<String,String>();
            map<String,String> mapTagsToValues=new  map<String,String>();
            String str = '<books><book1><book2><book3><book4 author="Chatty"></book4></book3></book2></book1></books>';
            Dom.Document domDoc = new Dom.Document();
            domDoc.load(str);
            dom.XmlNode location = domDoc.getRootElement();
            dom.XmlNode location1;
            dom.XmlNode location2;
            dom.XmlNode location3;
            for( dom.XmlNode child : location.getChildElements())
            {
                location1 = child ;
            }
            for( dom.XmlNode child : location1.getChildElements())
            {
                location2 = child ;
            }
            Test.startTest();
            mapTagsToValues=HUMIntegrationUtilityHelper.collectChildNodes(location2 ,resultantMap );
            system.assertEquals(mapTagsToValues.size()>0, true);
            Test.stopTest();
        }
    }
    
   
    
    /*
    * Method name : TestgetRepeatingValuesFromXMLString
    * Description : This method is called to get RepeatingValues From XMLString
    * Return Type : void
    * Parameter   : NA
    */
    private static testmethod void TestgetRepeatingValuesFromXMLString()
    {
        User u= HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        System.assert(u.id != null);
        system.runas(u)
        {
            list<string> repeatingValues = new list<string>();
            String xmlStr = '<books><book author="Chatty">Foo bar</book>' +'<book author="Sassy">Baz</book></books>';
            String str = 'books';
            Test.startTest();
            repeatingValues=HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(xmlStr ,str);
            system.assertEquals(repeatingValues.size()>0,true);
            Test.stopTest();
        }
    }
}