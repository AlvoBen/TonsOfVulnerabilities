/*******************************************************************************************************************************************************
Interface Name   : Expression_DI_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is interface for data layer class for controller DynamicCustomLinks_C_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu                                 10/05/2015                 Original Version
************************************************************************************************************************************************/

public interface Expression_DI_HUM
{
    /*
    * Method Name : add
    * Description : This method will add an instance to a list
    * Return type : Expression_DI_HUM
    * Parameter   : Expression_DI_HUM
    */
    Expression_DI_HUM add(Expression_DI_HUM expr);
    
    /*
    * Method Name : set
    * Description : This method will set boolean value to each child object
    * Return type : Expression_DI_HUM
    * Parameter   : String, Boolean
    */
    Expression_DI_HUM set(String name, Boolean value);
    
    /*
    * Method Name : evaluate
    * Description : This method will evaluate if link is eligible to be displayed on UI
    * Return type : Boolean
    * Parameter   : NA
    */
    Boolean evaluate();
}