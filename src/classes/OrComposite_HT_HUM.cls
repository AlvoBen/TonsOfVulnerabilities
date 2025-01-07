/***************************************************************************************************
Apex Class Name  : OrComposite_HT_HUM
Version          : 1.0
Created Date     : October 19 2015
Function         : Test Class for OrComposite_H_HUM
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
* Ranjeeth Nagishetty                             10/19/2015                     Original Version
***************************************************************************************************/

@isTest
private class OrComposite_HT_HUM
{
   /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */          
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        System.assert(oUser.id != null);
        system.runAs(oUser)
        {
            TestDataSetup_H_HUM.setupCustomLinksData();
        }
    }
    
    /*
    * Method Name : testCompositeOR
    * Description : Test method to test composite patter
    * Return type : void
    * Parameter   : NA
    */
    static testMethod void testCompositeOR()
    {
        User oUser = [Select Id,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('Cincinnati CRU',oUser.Current_Queue__c);
        system.runAs(oUser)
        {
            OrComposite_H_HUM oComposite  = new OrComposite_H_HUM();
        
            oComposite.evaluate();
            Expression_DI_HUM   expr = (new OrComposite_H_HUM())
                                   .add(new Variable_H_HUM ('1'))
                                   .add((new AndComposite_H_HUM ())
                                   .add(new Variable_H_HUM ('2'))
                                   .add(new Variable_H_HUM ('3'))
                                   )
                                  .set('1',false)
                                  .set('2',true)
                                  .set('3',false);
          System.assertequals(expr.evaluate(),false);
          expr.set('3',true);
         System.assertEquals(expr.evaluate(),true);
       }
    }
  }