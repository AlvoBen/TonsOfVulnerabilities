/*******************************************************************************************************************************************************
Apex Class Name  : LinkParameterLogic_H_HUM
Version          : 1.0
Created Date     : 04/15/2016
Function         : This is helper class to process Link Parameter logic
Test Class       : LinkParameterLogic_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar              25216                    04/15/2016                 Original Version
* Kritika Popat             38698                    09/08/2016                 REQ for Enable/Disable URL encoding
* Kritika Popat             43986                    10/18/2016                 Defect fix for null pointer exception in map 
* Anupama Tavva             78689                    03/22/2016                 Added isRequired field for Link paramter for the REQ-304159
* Anupama Tavva             84666                    04/17/2016                 Fix for Defect 2724720.0001
* Pradeepkumar Dani         88534                    04/28/2017                 REQ - 311469 - Rx Lookup PROD issue - Framework updates for link param 'ChildCollection' w.r.t isRequired flag
* Prasanthi Kandula         102588                   06/15/2017                 REQ - 318478 - CAS Link - Framework updates to handle more than one link conditional paramater records for link param 'Child collection'
* Satyanarayana chenna                               04/09/2021                 US 1918663 PR00094226 - CR AEP readiness - Low Security Issues 
* Lakshmi Madduri                                    05/05/2021                 2235996 - Separate constant class for link framework
***********************************************************************************************************************************************/

public class LinkParameterLogic_H_HUM
{    
   public Static Boolean isRequiredLinkparam_errorval;
    /*
    * Method Name : getQueryParameters
    * Description : This method is used to retrieve params and form map which will be used in rest of class
    * Return type : Map<String, String> 
    * Parameter   : List<Link_Parameter__c>, String, String, Boolean
    */
    public static Map<String, String> getQueryParameters(List<Link_Parameter__c> lstlinkparam, String sRecID, String objName, Boolean bIsCallBack)
    {   
        isRequiredLinkparam_errorval = false;
        Map<String, String> mapParams = new Map<String, String>();
        
        DynamicCustomLinks_D_HUM objDataLayer = new DynamicCustomLinks_D_HUM();
        
        sObject objResult = objDataLayer.getLinkQueryStringData(lstlinkparam, objName, sRecID);
        
        List<User> lstUser = objDataLayer.getUserPermissionSet(new Set<ID>{UserInfo.getUserId()});
       
        for(Link_Parameter__c oLnkParam : lstlinkparam)
        { 
          isRequiredLinkparam_errorval=processParameterType(oLnkParam, mapParams, lstUser, objResult, sRecID, bIsCallBack);
          if(isRequiredLinkparam_errorval)
          break;
        }
        return mapParams;
    }
    
    /*
    * Method Name : includeLinkParameterFormVF
    * Description : This method is used to put all values in map to form a query string
    * Return type : Map<String, String> 
    * Parameter   : Map<String, String>, Map<String, String>, String
    */
    public static Map<String, String> includeLinkParameterFormVF(Map<String, String> mapParams, Map<String, String> mapVFResults, String sRecID)
    {
        String paramValue = '';
        Map<String, String> tempMap = new Map<String, String>();
        String jsonStr = '';
        Set<String> setRecId = new Set<String>();
        Link_Parameter__c oLnkParam = NULL;
        
        for(String key : mapVFResults.keySet())
        {
            setRecId.add(key.split('\\.')[0]);
        }
        
        Map<Id, Link_Parameter__c> mapLnkParam = new Map<Id, Link_Parameter__c>(DynamicCustomLinks_D_HUM.getLinkParameters(setRecId));
        
        for(String key : mapVFResults.keySet())
        {
            tempMap.put(key,'');
            jsonStr = JSON.serialize(tempMap);
            
            if(mapParams.get(jsonStr) != NULL)
            {
               paramValue = mapVFResults.get(key);
               mapParams.remove(jsonStr);
               oLnkParam = mapLnkParam.get(key.split('\\.')[0]);
               if(oLnkParam.IsConditional__c)    paramValue = checkParameterCondition(paramValue, oLnkParam, false, sRecID, null);
               mapParams.put(oLnkParam.Variable_Name__c,paramValue);
            }
            tempMap.clear();
        }
        return mapParams;
    }
    
    /*
    * Method Name : processParameterType
    * Description : This method is used to process parameters for a Link
    * Return type : void 
    * Parameter   : Link_Parameter__c, Map<String, String>, List<User>, sObject, String, Boolean
    */
    @testVisible
    private static boolean processParameterType(Link_parameter__c oLnkParam, Map<String, String> mapParams, List<User> lstUser, sObject objResult, String sRecID, Boolean bIsCallBack)
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
               mapParams.put(oLnkParam.Variable_Name__c,sdatabaseField);
            }
        }
        else if(oLnkParam.Variable_Type__c == 'VisualForce Page')
        {
          if(oLnkParam.isRequired__c && String.isBlank(oLnkParam.Variable_Source__c))
          {
              isRequiredLinkparam_error = true;
          }
          else
          {
            processVFParameterType(oLnkParam, objResult, sRecID, mapParams, bIsCallBack);
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
                mapParams.put(oLnkParam.Variable_Name__c, processStaticParameterType(oLnkParam));
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
                mapParams.put(oLnkParam.Variable_Name__c, processChildCollectionParameterType(oLnkParam, objResult, sRecID));
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
                mapParams.put(oLnkParam.Variable_Name__c, sQueriedField);
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
                mapParams.put(oLnkParam.Variable_Name__c, processCacheParameterType(oLnkParam, lstUser));
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
    @testVisible
    private static string processDatabaseFieldParameterType(Link_parameter__c oLnkParam, sObject objResult, String sRecID)
    {
        String sVal = getHierarchicalParameterValue(oLnkParam.Variable_Source__c, objResult);
        if(oLnkParam.IsConditional__c)    return  checkParameterCondition(sVal, oLnkParam, false, sRecID, objResult);
        return getFinalParamValue(oLnkParam.Encode__c, sVal);
    }
    
    /*
    * Method Name : processVFParameterType
    * Description : This method will process VisualForce parameter type
    * Return type : void
    * Parameter   : Link_Parameter__c, sObject, String, Map<String, String>, Boolean
    */
    @testVisible
    private static void processVFParameterType(Link_parameter__c oLnkParam, sObject objResult, String sRecID, Map<String, String> mapParams, Boolean bIsCallBack)
    {
        Map<String, String> mapVFLink = new Map<String, String>();
        
        if(!bIsCallBack)
        {
            String sEncodedVS = getFinalParamValue(oLnkParam.Encode__c, oLnkParam.Variable_Source__c);
            mapVFLink.put(String.valueOf(oLnkParam.Id) + '.' + sEncodedVS  + '.' + String.valueOf(oLnkParam.Link__c), '');
            mapParams.put(JSON.serialize(mapVFLink), '__LinkFraework__VisualForce__');
        }
    }
    
    /*
    * Method Name : processStaticParameterType
    * Description : This method will process Static parameter type
    * Return type : String 
    * Parameter   : Link_Parameter__c
    */
    @testVisible
    private static string processStaticParameterType(Link_Parameter__c oLnkParam)
    {
        if(String.isNotBlank(oLnkParam.Variable_Source__c))    return getFinalParamValue(oLnkParam.Encode__c, oLnkParam.Variable_Source__c);
        return '';
    }
    
    /*
    * Method Name : processChildCollectionParameterType
    * Description : This method will process Child Collection parameter type
    * Return type : String 
    * Parameter   : Link_Parameter__c, sObject, String
    */
    @testVisible
    private static string processChildCollectionParameterType(Link_Parameter__c oLnkParam, sObject objResult, String sRecID)
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
    @testVisible
    private static string processQueryByParameterType(Link_Parameter__c oLnkParam, sObject objResult, String sRecID)
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
    @testVisible
    private static string processCacheParameterType(Link_Parameter__c oLnkParam, List<User> lstUser)
    {
        String sAKAName = AKANameFetch_H_HUM.getAkaName(lstUser[0].Network_User_Id__c, oLnkParam.Variable_Source__c);
        if(String.isBlank(sAKAName))
        {
            throw new HUMCustomException(LINKS_CONSTANT_HUM.STRING_AKA_NAME_SESSION_EXPIRED_ERR);
            return NULL;
        }
        return sAKAName;
    }
    
    /*
    * Method Name : checkParameterCondition
    * Description : This method is used get the parameter value based on conditional parameters.
    * Return type : String 
    * Parameter   : String , List<Link_Parameter__c>, Boolean, String, sObject
    */
    @testVisible static private string checkParameterCondition(String sParamValue, Link_Parameter__c oLnkParam, Boolean bIsLooseRelation, String sRecID, sObject objResult)
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
    @testVisible
     private static String processIfConditionalType(Link_Conditional_Parameter__c oLnkCondParam, List<Link_Parameter__c> lstLnkParam, String sParamValue, Boolean bIsLooseRelation, String sRecID, sObject objResult)
    {
        isRequiredLinkparam_errorval = false;
        DynamicCustomLinks_D_HUM objDataLayer = new DynamicCustomLinks_D_HUM();
       
        if(oLnkCondParam.Variable_Type__c.equalsIgnoreCase('Static'))
        {
            if(sParamValue == oLnkCondParam.Conditional_Check__c )    return getFinalParamValue(oLnkCondParam.Encode__c, oLnkCondParam.Conditional_Value__c);
        }
        else if(!String.isBlank(lstLnkParam[0].Variable_Source__c) && oLnkCondParam.Variable_Type__c.equalsIgnoreCase('Database Field'))
        {
            String sEncode = objDataLayer.getChildCollection(lstLnkParam[0].Variable_Source__c, oLnkCondParam, sRecID, lstLnkParam[0].Key_Name__c, bIsLooseRelation, objResult);
            if (sEncode == null || String.isBlank(sEncode)) isRequiredLinkparam_errorval = true;
            return getFinalParamValue(oLnkCondParam.Encode__c, sEncode);
        }
        return '';
    }
    
    /*
    * Method Name : getHierarchicalParameterValue
    * Description : This method is used retrieve hierarchical parameter values.
    * Return type : String 
    * Parameter   : String , sObject 
    */
    @testVisible
    private static String getHierarchicalParameterValue(String sPramAPIName, sObject objRecord)
    {
        String sFinalVal = '';
        list<String> lstParams = sPramAPIName.split('\\.');
        
        if(lstParams.size() > 1)
        {
            sObject sObj = objRecord.getSObject(lstParams[0]);
            
            for(integer i = 1 ; i < lstParams.size() - 1 ; i++)
            {
                sObj = sObj.getSObject(lstParams[i]);
            }
            sFinalVal = (String)sObj.get(lstParams[lstParams.size()-1]); 
        }
        else
        {
            sFinalVal = (String)objRecord.get(lstParams[0]);
        }  
        return sFinalVal;
    }
    
    /*
    * Method Name : getFinalParamValue
    * Description : This method is used check for field "Encode__c" in Link_Parameter__cand if found true will encode that value else will return plain text.
    * Return type : String 
    * Parameter   : Boolean,String  
    */
    private static String  getFinalParamValue(Boolean encode,String svalue)
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
}