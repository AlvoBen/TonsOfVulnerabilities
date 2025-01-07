/*******************************************************************************************************************************************************
Apex Class Name  : Variable_H_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This class will store link data as wrapper
Test Class       : Variable_HT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu        27372                    10/05/2015                 Original Version
* Santhi Mandava            27484                    11/02/2015                 Removed 'Else' condition in 'Set' method.
************************************************************************************************************************************************/

public with sharing class Variable_H_HUM implements Expression_DI_HUM
{
    /*
    * Method Name : Variable_H_HUM
    * Description : This is constructor is used to initialize variables for this class
    * Return type : NA
    * Parameter   : String
    */
    public Variable_H_HUM(String name)
    {
        this.name = name;
    }
    
    public String  name  {get;private set;}
    public Boolean value {get;private set;}
    
    /*
    * Method Name : add
    * Description : This method will add an instance to a list
    * Return type : Expression_DI_HUM
    * Parameter   : Expression_DI_HUM
    */
    public Expression_DI_HUM add(Expression_DI_HUM expr)
    {
        return this;
    }
    
    /*
    * Method Name : set
    * Description : This method will set Boolean value to each child object
    * Return type : Expression_DI_HUM
    * Parameter   : String, Boolean
    */
    public Expression_DI_HUM set(String name, Boolean value)
    {
        if(this.name != null && this.name.equalsIgnoreCase(name))
        {
            this.value = value;
        }
        return this; 
    }
    
    /*
    * Method Name : evaluate
    * Description : This method will evaluate if link is eligible to be displayed on UI
    * Return type : Boolean
    * Parameter   : NA
    */
    public Boolean evaluate()
    {
        return value;
    }
}