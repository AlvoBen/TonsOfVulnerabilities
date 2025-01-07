/*******************************************************************************************************************************************************
Apex Class Name  : Composite_H_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is abstract class implements interface to base component class
Test Class       : Composite_HT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu                                 10/05/2015                 Original Version
************************************************************************************************************************************************/

public with sharing abstract class Composite_H_HUM implements Expression_DI_HUM
{
    /*
    * Method Name : Composite_H_HUM
    * Description : This is constructor is used to initialize variables for this class
    * Return type : NA
    * Parameter   : NA
    */
    public Composite_H_HUM()
    {
        this.children = new List<Expression_DI_HUM >();
    }
    
    public List<Expression_DI_HUM > children {get; private set;}
    
    public Boolean hasChildren
    {
        get
        {
            return !children.isEmpty();
        }
    }
    
    /*
    * Method Name : add
    * Description : This method will add an instance to a list
    * Return type : Expression_DI_HUM
    * Parameter   : Expression_DI_HUM
    */
    public Expression_DI_HUM add(Expression_DI_HUM expr)
    {
        children.add(expr); return this;
    }
    
    /*
    * Method Name : set
    * Description : This method will set Boolean value to each child object
    * Return type : Expression_DI_HUM
    * Parameter   : String, Boolean
    */
    public Expression_DI_HUM set(String name, Boolean value){
        for(Expression_DI_HUM expr : children) expr.set(name,value);
        return this;
    }
    
    /*
    * Method Name : evaluate
    * Description : This method is not overridden
    * Return type : Boolean
    * Parameter   : NA
    */
    public abstract Boolean evaluate();
}