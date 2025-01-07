/*******************************************************************************************************************************************************
Apex Class Name  : OrComposite_H_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is Helper class which override evaluate function of an interface
Test Class       : OrComposite_HT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu                                 10/05/2015                 Original Version
************************************************************************************************************************************************/

public with sharing class OrComposite_H_HUM extends Composite_H_HUM
{
    /*
    * Method Name : evaluate
    * Description : This method will evaluate the expression and return Boolean result
    * Return type : Boolean
    * Parameter   : NA
    */
    public override Boolean evaluate()
    {
        for(Expression_DI_HUM expr : children)
        {
            if(expr.evaluate()) return true;
        }
        return false;
    }
}