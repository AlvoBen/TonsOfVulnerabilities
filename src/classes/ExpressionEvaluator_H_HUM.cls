/*******************************************************************************************************************************
Apex Class Name : ExpressionEvaluator_H_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : Calculate the boolean Expression

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------------------

* Rajesh Keswani             165476                   08/21/2017                 REQ - 326305 Original version
* Rajesh Keswani             175570                   01/29/2018                 Defect Fix 339913.0001; routing logic changed when APi name Is classification. 
* Syed Mubarak               225918                   05/09/2018                 Defect Fix #357403.0001
* Shailesh Mali                                       08/08/2019                 REQ - 405958 WorkQueueView optimization.
* Jasmeen Shangari                                    02/08/2021                 Azure ID 1790946 - Fix for Incident - INC0806755                      
*****************************************************************************************************************************************/
public with sharing class ExpressionEvaluator_H_HUM
{
    static Map<String, String> logicTypes = new Map<String, String>();
    static Map<String, Map<String, String>> expressionLogic = new Map<String, Map<String, String>>();
    public static Map <string, boolean > queViewItemMap = new Map <string, boolean > ();
    private static Boolean bSwitch_1790946 = HUMUtilityHelper.isCRMFunctionalityON('1790946');
    
    /**
     * Returns boolean
     * <p>
     * This method is used to validate the queueview eligibility.
     *
     * @param  Queue_View__c
     * @param  sObject
     * @param  integer count
     * @param List < Queue_View_Item__c > lstQVI
     * @param Map<String, HUMQueueViewCriteria__c> mapOfFilters
     * @see
     */
    public static boolean validateQueueView(Queue_View__c oQueueView, sObject oCaseTaskRec, List < Queue_View_Item__c > lstQVI, Map < String, HUMQueueViewCriteria__c > mapOfFilters, CTCI_Junction__c oCtci)
    {
        String ExpressionStr = (string) oQueueView.get('Expression__c');
        // logic to check case records with queue view item
        for (Queue_View_Item__c QVIobj: lstQVI)
        {
            String tempApiName ='';
            if( mapOfFilters.get(QVIobj.API_Name__c) != null)tempApiName =  mapOfFilters.get(QVIobj.API_Name__c).API_Name__c;
            String tempObjFieldValue = '';
            if (oCaseTaskRec.getSObjectType() == Schema.Case.getSObjectType())
            {
                if (oCtci != null)
                {
                    if (tempApiName.toLowerCase() == GLOBAL_CONSTANT_HUM.CASETRANSFER_CL_HUM && oCtci.Classification__c != null)
                    {
                        tempObjFieldValue = (String) oCtci.getSobject('classification__r').get('Name');
                    }
                    if (tempApiName.toLowerCase() == GLOBAL_CONSTANT_HUM.CASETRANSFER_INTENT_HUM && oCtci.Intent__c != null)
                    {
                        tempObjFieldValue = (String) oCtci.getSobject('Intent__r').get('Name');
                    }
                }
            }

            if (string.isNotBlank(tempApiName) && (oCaseTaskRec.getSObjectType() == Schema.Task.getSObjectType() || (tempApiName.toLowerCase() != GLOBAL_CONSTANT_HUM.CASETRANSFER_CL_HUM && tempApiName.toLowerCase() != GLOBAL_CONSTANT_HUM.CASETRANSFER_INTENT_HUM)))
            {
                tempObjFieldValue = (string) oCaseTaskRec.get(tempApiName);
            }
            if ('Equals'.equalsIgnoreCase(QVIobj.Field_Operator__c))
            {
                if (string.isNotBlank(tempObjFieldValue) && QVIobj.Field_Value__c == tempObjFieldValue) queViewItemMap.put(QVIobj.Name, true);
                else queViewItemMap.put(QVIobj.Name, false);
            }
            else if ('Not Equals'.equalsIgnoreCase(QVIobj.Field_Operator__c))
            {     
				if(bSwitch_1790946)
                {			
					if ((string.isNotBlank(tempObjFieldValue) && QVIobj.Field_Value__c != tempObjFieldValue) || string.isBlank(tempObjFieldValue))
						queViewItemMap.put(QVIobj.Name, true);                          
					else queViewItemMap.put(QVIobj.Name, false);
				}
				else
				{
					if ((string.isNotBlank(tempObjFieldValue) && QVIobj.Field_Value__c != tempObjFieldValue))
						queViewItemMap.put(QVIobj.Name, true);                          
					else queViewItemMap.put(QVIobj.Name, false);				
				}
            }
        }
        
        for (string QVIName: queViewItemMap.keyset())
        {
            if (ExpressionStr.contains(QVIName)) ExpressionStr = ExpressionStr.replaceAll(QVIName, '' + queViewItemMap.get(QVIName));

        }
        
        //passing the expressions to eval() method, eval() is validating the expression and returning the result as boolean value.
        return eval(ExpressionStr.toUpperCase());
        
    }

    /**
    * Evaluate a boolean expression 
    * @ReturnType boolean
    * @param string
    */
    public static boolean eval(String expression) {     
        // If expression contains all TRUE or FALSE
        if(expression.containsNone('FALSE')) { return TRUE; }        
        if(expression.containsNone('TRUE')) { return FALSE; }
        
        fillLogic();
        
        return Boolean.valueOf(evaluateExpression(expression));
    }    
    
    /**
    * Evaluate the expression
    * This method is used to simplify the expression.
    * @return type string
    * @param  string ExpressionStr
    */
    public static String evaluateExpression(String expression) {        
        for(String logicType : logicTypes.keySet()) {
            if(expression.contains(logicType)) {
                expression = simplifyExpression(expression, logicTypes.get(logicType));
            }
        }
        if(expression.contains('AND') || expression.contains('OR') || expression.contains('(')) {
            expression = evaluateExpression(expression);
        }
        return expression;
    }
    
    /**
    * Simplify the expression
    * simplifyExpression() method is used to simplify the expression based on the logicType
    * @ReturnType string
    * @param string, string
    */
    public static string simplifyExpression(String expression, String LogicType){
        Map<String, String> Logic = new Map<String, String>(expressionLogic.get(LogicType));
        for(String key : Logic.keySet()) {
            expression = expression.replace(key, Logic.get(key));             
        }
        return expression;
    } 
    
    /**
    * Fill AND and OR Logic
    * fillLogic() method is used to set Braces,AND,OR values in maps
    * @ReturnType void
    * @param -- no parameter
    */
    public static void fillLogic() {
        Map<String, String> ANDLogic = new Map<String, String>();
        Map<String, String> ORLogic = new Map<String, String>();
        Map<String, String> BRACELogic = new Map<String, String>();
        
        logicTypes.put('(', 'BRACES');
        logicTypes.put('AND', 'AND');
        logicTypes.put('OR', 'OR');        
        
        // Braces Logic
        BRACELogic.put('(TRUE)', 'TRUE');
        BRACELogic.put('(FALSE)', 'FALSE');
        expressionLogic.put('BRACES', BRACELogic);
        
        // AND Logic
        ANDLogic.put('TRUE AND TRUE', 'TRUE');
        ANDLogic.put('TRUE AND FALSE', 'FALSE');
        ANDLogic.put('FALSE AND TRUE', 'FALSE');
        ANDLogic.put('FALSE AND FALSE', 'FALSE');
        expressionLogic.put('AND', ANDLogic);
        
        // OR Logic
        ORLogic.put('TRUE OR TRUE', 'TRUE');
        ORLogic.put('TRUE OR FALSE', 'TRUE');
        ORLogic.put('FALSE OR TRUE', 'TRUE');
        ORLogic.put('FALSE OR FALSE', 'FALSE');
        expressionLogic.put('OR', ORLogic);
    }
}