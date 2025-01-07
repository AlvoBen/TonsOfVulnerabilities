/*******************************************************************************************************************************************************
Apex Class Name  : LinkParameterLogic_LH_HUM
Version          : 1.0
Created Date     : 13/07/2021
Function         : This is helper class to process Link Parameter logic
Test Class       : DynamicCustomLinks_LT_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Alwal                                  07/13/2021                   Original Version
***********************************************************************************************************************************************/
public with sharing class LinkParameterLogic_LH_HUM {

    /*
    * Method Name : getQueryParameters
    * Description : This method is used to retrieve params and form map which will be used in rest of class
    * Return type : Map<String, String> 
    * Parameter   : List<Link_Parameter__c>, String, String, Boolean
    */
    public static Map<String, String> getQueryParameters(List<Link_Parameter__c> lstlinkparam, String sRecID, String objName, Boolean bIsCallBack, Boolean isRequiredLinkparam_errorval)
    {   
        isRequiredLinkparam_errorval = false;
        Map<String, String> mapParams = new Map<String, String>();
        DynamicCustomLinks_LC_HUM.mapParams = mapParams;
        sObject objResult = DynamicCustomLinks_LD_HUM.getLinkQueryStringData(lstlinkparam, objName, sRecID);
        List<User> lstUser = DynamicCustomLinks_LD_HUM.getUserPermissionSet(new Set<ID>{UserInfo.getUserId()});
       
        for(Link_Parameter__c oLnkParam : lstlinkparam)
        { 
            IF(oLnkParam != null){
                DynamicCustomLinks_LC_HUM.isRequiredLinkparam_errorval = processParameterType(oLnkParam, lstUser, objResult, sRecID, bIsCallBack, DynamicCustomLinks_LC_HUM.isRequiredLinkparam_errorval);
                if(DynamicCustomLinks_LC_HUM.isRequiredLinkparam_errorval) break; 
            }
            
        }
        return  DynamicCustomLinks_LC_HUM.mapParams;
    }

    /*
    * Method Name : processParameterType
    * Description : This method is used to process parameters for a Link
    * Return type : void 
    * Parameter   : Link_Parameter__c, Map<String, String>, List<User>, sObject, String, Boolean
    */
    
    public static boolean processParameterType(Link_parameter__c oLnkParam, List<User> lstUser, sObject objResult, String sRecID, Boolean bIsCallBack, Boolean isRequiredLinkparam_errorval)
    {
        boolean isRequiredLinkparam_error = false;
        if(oLnkParam.Variable_Type__c == 'Database Field')
        { 
            string sdatabaseField = processDatabaseFieldParameterType(oLnkParam, objResult, sRecID);
            if (oLnkParam.isRequired__c && String.isBlank(sdatabaseField))
            {
               isRequiredLinkparam_error = true;
            }
            else
            {
                DynamicCustomLinks_LC_HUM.mapParams.put(oLnkParam.Variable_Name__c,sdatabaseField);
            }
        }
        else if(oLnkParam.Variable_Type__c == 'VisualForce Page' || oLnkParam.Variable_Type__c.equals('Lightning Web Component'))
        {
          if(oLnkParam.isRequired__c && String.isBlank(oLnkParam.Variable_Source__c))
          {
              isRequiredLinkparam_error = true;
          }
          else
          {
            processVFParameterType(oLnkParam, objResult, sRecID, bIsCallBack);
          }
        }
        else if(oLnkParam.Variable_Type__c  == 'Static')
        {
            if(oLnkParam.isRequired__c && String.isBlank(oLnkParam.Variable_Source__c))
            {
                isRequiredLinkparam_error = true;
            }
            else
            {
                DynamicCustomLinks_LC_HUM.mapParams.put(oLnkParam.Variable_Name__c, processStaticParameterType(oLnkParam));
            }
        }
        else if(oLnkParam.Variable_Type__c  == 'Child Collection')
        {
           if(oLnkParam.isRequired__c && String.isBlank(oLnkParam.Variable_Source__c))
           {
               isRequiredLinkparam_error = true;
           }
           else
           {
                DynamicCustomLinks_LC_HUM.mapParams.put(oLnkParam.Variable_Name__c, processChildCollectionParameterType(oLnkParam, objResult, sRecID));
                if (oLnkParam.isRequired__c && isRequiredLinkparam_errorval) isRequiredLinkparam_error = true;
           }
        }
        else if(oLnkParam.Variable_Type__c  == 'Query By')
        {
           string sQueriedField = processQueryByParameterType(oLnkParam, objResult, sRecID);
             if (oLnkParam.isRequired__c && String.isBlank(sQueriedField))
             {
                 isRequiredLinkparam_error = true;
             }
             else
             {
                DynamicCustomLinks_LC_HUM.mapParams.put(oLnkParam.Variable_Name__c, sQueriedField);
             }
        }
        else if(oLnkParam.Variable_Type__c  == 'Cache' && !lstUser.isEmpty())
        {
          if(oLnkParam.isRequired__c && String.isBlank(oLnkParam.Variable_Source__c))
            {
                isRequiredLinkparam_error = true;
            }
            else
            {
                DynamicCustomLinks_LC_HUM.mapParams.put(oLnkParam.Variable_Name__c, processCacheParameterType(oLnkParam, lstUser));
            }
        }
        return isRequiredLinkparam_error;
    }

    /*
    * Method Name : processDatabaseFieldParameterType
    * Description : This method will process Database field parameter type
    * Return type : String 
    * Parameter   : Link_Parameter__c, sObject, String
    */
    
    public static string processDatabaseFieldParameterType(Link_parameter__c oLnkParam, sObject objResult, String sRecID)
    {
        String sVal = getHierarchicalParameterValue(oLnkParam.Variable_Source__c, objResult);
        if(oLnkParam.IsConditional__c)    return  checkParameterCondition(sVal, oLnkParam, false, sRecID, objResult);
        return getFinalParamValue(oLnkParam.Encode__c, sVal);
    }

    /*
    * Method Name : getHierarchicalParameterValue
    * Description : This method is used retrieve hierarchical parameter values.
    * Return type : String 
    * Parameter   : String , sObject 
    */
    
    public static String getHierarchicalParameterValue(String sPramAPIName, sObject objRecord)
    {
        String sFinalVal = '';
        list<String> lstParams = sPramAPIName.split('\\.');
        
        if(lstParams.size() > 1)
        {
            sObject sObj;
            if(objRecord !=null){
                sObj = objRecord.getSObject(lstParams[0]);
            }   
            
            for(integer i = 1 ; i < lstParams.size() - 1 ; i++)
            {
                if(sObj!=null){
                    sObj = sObj.getSObject(lstParams[i]);
                }
            }
            if(sObj!=null){
                sFinalVal = (String)sObj.get(lstParams[lstParams.size()-1]);
            }
             
        }
        else
        {
            if(objRecord.get(lstParams[0]) instanceof Date){
            	sFinalVal = string.valueOf(objRecord.get(lstParams[0]));   
            }
            else{ sFinalVal = (String)objRecord.get(lstParams[0]);}
        }  
        return sFinalVal;
    }

    /*
    * Method Name : getFinalParamValue
    * Description : This method is used check for field "Encode__c" in Link_Parameter__cand if found true will encode that value else will return plain text.
    * Return type : String 
    * Parameter   : Boolean,String  
    */
    public static String  getFinalParamValue(Boolean encode,String svalue)
    {
        if(encode)
        {
            return EncodingUtil.urlEncode(svalue, 'UTF-8');
        }
        else
        {
            return svalue;
        }
    }

    /*
    * Method Name : processVFParameterType
    * Description : This method will process VisualForce parameter type
    * Return type : void
    * Parameter   : Link_Parameter__c, sObject, String, Map<String, String>, Boolean
    */
    
    public static void processVFParameterType(Link_parameter__c oLnkParam, sObject objResult, String sRecID, Boolean bIsCallBack)
    {
        Map<String, String> mapVFLink = new Map<String, String>();
        
        if(!bIsCallBack)
        {
            String sEncodedVS = getFinalParamValue(oLnkParam.Encode__c, oLnkParam.Variable_Source__c);
            mapVFLink.put(String.valueOf(oLnkParam.Id) + '.' + sEncodedVS  + '.' + String.valueOf(oLnkParam.Link__c) + '.' + oLnkParam.Variable_Name__c, '');
            DynamicCustomLinks_LC_HUM.mapParams.put(JSON.serialize(mapVFLink), '__LinkFraework__VisualForce__');
        }
    }

    /*
    * Method Name : processStaticParameterType
    * Description : This method will process Static parameter type
    * Return type : String 
    * Parameter   : Link_Parameter__c
    */
    
    public static string processStaticParameterType(Link_Parameter__c oLnkParam)
    {
        if(String.isNotBlank(oLnkParam.Variable_Source__c)) {
            return getFinalParamValue(oLnkParam.Encode__c, oLnkParam.Variable_Source__c);
        }else{
             return '';
        }
       
    }

    /*
    * Method Name : processChildCollectionParameterType
    * Description : This method will process Child Collection parameter type
    * Return type : String 
    * Parameter   : Link_Parameter__c, sObject, String
    */
    
    public static string processChildCollectionParameterType(Link_Parameter__c oLnkParam, sObject objResult, String sRecID)
    {
        if(oLnkParam.IsConditional__c)    return checkParameterCondition(NULL, oLnkParam, false, sRecID, objResult);
        return '';
    }

    /*
    * Method Name : processQueryByParameterType
    * Description : This method will process Query By parameter type
    * Return type : String 
    * Parameter   : Link_Parameter__c, sObject, String
    */
    
    public static string processQueryByParameterType(Link_Parameter__c oLnkParam, sObject objResult, String sRecID)
    {
        if(oLnkParam.IsConditional__c)    return checkParameterCondition(NULL, oLnkParam, true, sRecID, objResult);
        return '';
    }


    /*
    * Method Name : processCacheParameterType
    * Description : This method will process Cache parameter type
    * Return type : String 
    * Parameter   : Link_Parameter__c, List<User>
    */
    public static string processCacheParameterType(Link_Parameter__c oLnkParam, List<User> lstUser)
    {
        String sAKAName = AKANameFetch_H_HUM.getAkaName(lstUser[0].Network_User_Id__c, oLnkParam.Variable_Source__c);
        if(String.isBlank(sAKAName) && !test.isRunningTest())
        {
            throw new HUMCustomException(HUMConstants.AKA_NAME_SESSION_EXPIRED_ERR);
        }
        return sAKAName;
    }

    /*
    * Method Name : checkParameterCondition
    * Description : This method is used get the parameter value based on conditional parameters.
    * Return type : String 
    * Parameter   : String , List<Link_Parameter__c>, Boolean, String, sObject
    */
    
    public static string checkParameterCondition(String sParamValue, Link_Parameter__c oLnkParam, Boolean bIsLooseRelation, String sRecID, sObject objResult)
    {
        String sParamVal = '';
        for(Link_Conditional_Parameter__c oLnkCondParam : oLnkParam.Link_Conditional_Parameters__r)
        {
            if(oLnkCondParam.Conditional_Type__c == 'IF')
            {
                List<Link_Parameter__c > lstLnkParam = new List<Link_Parameter__c >();
                lstLnkParam.add(oLnkParam);
                sParamVal = processIfConditionalType(oLnkCondParam, lstLnkParam, sParamValue, bIsLooseRelation, sRecID, objResult);
                if(sParamVal != null && String.isNotBlank(sParamVal))
                {
                    break;
                }
            }
            else 
            {
                sParamVal  = getFinalParamValue(oLnkCondParam.Encode__c, oLnkCondParam.Conditional_Value__c);
                break;
            }
        }
        return sParamVal;
    }

     /*
    * Method Name : processIfConditionalType
    * Description : This method is used get the parameter value based on conditional parameters.
    * Return type : String 
    * Parameter   : Link_Conditional_Parameter__c , Link_Parameter__c, String, Boolean, String, sObject
    */
    
    public static String processIfConditionalType(Link_Conditional_Parameter__c oLnkCondParam, list<Link_Parameter__c> oLnkParam, String sParamValue, Boolean bIsLooseRelation, String sRecID, sObject objResult)
    {
        DynamicCustomLinks_LC_HUM.isRequiredLinkparam_errorval = false;
        
        if(oLnkCondParam.Variable_Type__c.equalsIgnoreCase('Static'))
        {
            if(sParamValue == oLnkCondParam.Conditional_Check__c )    return getFinalParamValue(oLnkCondParam.Encode__c, oLnkCondParam.Conditional_Value__c);
        }
        else if(!String.isBlank(oLnkParam[0].Variable_Source__c) && oLnkCondParam.Variable_Type__c.equalsIgnoreCase('Database Field'))
        {
            String sEncode = DynamicCustomLinks_LD_HUM.getChildCollection(oLnkParam[0].Variable_Source__c, oLnkCondParam, sRecID, oLnkParam[0].Key_Name__c, bIsLooseRelation, objResult);
            if (sEncode == null || String.isBlank(sEncode)) DynamicCustomLinks_LC_HUM.isRequiredLinkparam_errorval = true;
            return getFinalParamValue(oLnkCondParam.Encode__c, sEncode); 
        }
        return '';
    }

    
}