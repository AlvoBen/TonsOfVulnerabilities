/*******************************************************************************************************************************************************
Apex Class Name  : AndComposite_H_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This class implements data layer interface to implement evaluate functionality
Test Class       : AndComposite_HT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu                                 10/05/2015                 Original Version
************************************************************************************************************************************************/

public with sharing class AndComposite_H_HUM extends Composite_H_HUM
{
    /*
    * Method Name : evaluate
    * Description : This method is used to override evaluate method from Interface
    * Return type : Boolean
    * Parameter   : NA
    */
    public override Boolean evaluate()
    {
        for(Expression_DI_HUM expr : children)
        {
            if(!expr.evaluate()) return false;
        }
        
        return true;
    }
}