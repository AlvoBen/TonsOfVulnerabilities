/*******************************************************************************************************************************************************
Apex Class Name  : ExpressionEvaluate_H_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is data layer class for controller DynamicCustomLinks_C_HUM
Test Class       : ExpressionEvaluate_HT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu        20725                    10/05/2015                 Original Version
************************************************************************************************************************************************/

public with sharing Class ExpressionEvaluate_H_HUM
{
    @testVisible Integer i = 0;
    @testVisible Boolean bIsBracesStared = false;
    @testVisible Expression_DI_HUM oExprPrimary = NULL;
    @testVisible Expression_DI_HUM oExprChild = NULL;
    @testVisible List<String> lstVals = NULL;
    
    /*
    * Method Name : evaluateExpression
    * Description : This method is used to evaluate expression
    * Return type : Map<String, Boolean>
    * Parameter   : Link__c, Map<String, Boolean>
    */
    public Map<String, Boolean> evaluateExpression(Link__c objLink, Map<String, Boolean> mapLnkCrt)
    {
        lstVals = objLink.Expression__c.split(' ');
        Map<String, Boolean> mapFinal = new Map<String, Boolean>();
        resetVariables();
        while (i <= lstVals.size() - 1) 
        {
            if(lstVals[i].Contains('('))
            {
                bIsBracesStared = true;
                oExprChild = createExpression(lstVals[i + 1]);
                oExprChild.Add(new Variable_H_HUM(lstVals[i].substring(1)));
                i = i + 2; 
            } 
            else If(lstVals[i].Contains(')'))
            {
                oExprChild.Add(new Variable_H_HUM(lstVals[i].substring(0,lstVals[i].indexof(')'))));
                If(oExprPrimary != null) 
                {
                    oExprPrimary.Add(oExprChild);
                    oExprChild = null;
                }
                bIsBracesStared = false;
                i = i+1;
            }
            else
            {
                validatePrimaryExpression();
                i = i + 1;
            } 
        }
        
        for(String objVal : mapLnkCrt.keyset())
        {
             oExprPrimary.set(objVal, mapLnkCrt.get(objVal));
        }
        
        mapFinal.put(String.valueOf(objLink.Id), oExprPrimary.evaluate());
        
        return mapFinal;
    }
    
    /*
    * Method Name : resetVariables
    * Description : This method is used to reset variables
    * Return type : void
    * Parameter   : NA
    */
    @testVisible
    private void resetVariables()
    {
        i = 0;
        bIsBracesStared = false;
        oExprPrimary = null;
        oExprChild = NULL;
    }
    
    /*
    * Method Name : validatePrimaryExpression
    * Description : This method is used to evaluate primary expression
    * Return type : void
    * Parameter   : NA
    */
    @testVisible
    private void validatePrimaryExpression()
    {
        if(!bIsBracesStared)
        {
            String sCond = '';
            if(lstVals[i] != 'OR' && lstVals[i] != 'AND')
            {
                if(i < lstVals.size() - 1)
                {
                    sCond = lstVals[i+1];
                    If(oExprPrimary == null) oExprPrimary = createExpression(sCond);
                    oExprPrimary.Add(new Variable_H_HUM(lstVals[i]));
                    i = i + 1; 
                }
                else if(i == lstVals.size()-1 )
                {
                    sCond = lstVals[i-1];
                    oExprPrimary.Add(new Variable_H_HUM(lstVals[i]));
                }
            }
            else
            {
                sCond = lstVals[i];
                If(oExprPrimary == null) oExprPrimary = createExpression(sCond);
                If(oExprChild !=null) 
                {
                    oExprPrimary.Add(oExprChild);
                    oExprChild = null;
                }
            }
        }
        else if(bIsBracesStared && lstVals[i] != 'OR' && lstVals[i] != 'AND')
        {
            oExprChild.Add(new Variable_H_HUM(lstVals[i]));
        }      
    }
    
    /*
    * Method Name : createExpression
    * Description : This method is used to evaluate expression
    * Return type : Expression_DI_HUM
    * Parameter   : String
    */
    public Expression_DI_HUM createExpression(String sCondition)
    {
        Expression_DI_HUM oExpr = NULL;
        
        if(sCondition == 'AND')
        {
            oExpr = new AndComposite_H_HUM();
        }
        else
        {
            oExpr = new OrComposite_H_HUM();
        }
        return oExpr;
   }
}