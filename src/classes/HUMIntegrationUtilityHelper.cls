/*******************************************************************************************************************************************************************************************************************************
Apex Class Name  : HUMIntegrationUtilityHelper
Version          : 1.0
Created Date     : June 22 2014
Function         : Here all the utility methods for the integration will be stored
Test Class       : HUMIntegrationUtilityHelperTest
********************************************************************************************************************************************************************************************************************************
Modification Log:
* Developer Name                  Code Review             Date                      Description
*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Srikant Joshi                      17339              06/22/2014                  Original version
* Chaitanya Vegendla                 17339              08/01/2014                  Added XML parsing utility method used in OHBP service.
* Shreya Choodamani                  17339              08/13/2014                  Added XML Parsing utility method used in Alerts Service. 
* Pradeep Raju						 20215				01/06/2015					Updated the code from lines 206 to 214
********************************************************************************************************************************************************************************************************************************/

public class HUMIntegrationUtilityHelper
{
    /*
    * Method name : xmlInputStringParser
    * Description : Returns a map with key as Tags and Value as Tag Values
    * Return Type : map<String,String>
    * Parameter   : String
    */
    public static map<String,String> xmlInputStringParser(String strXmlInput)
    {
        map<String,String> mapTagsToValues = new map<String,String> {};
        map<String,map<String,String>> mapConsolidated = new map<String,map<String,String>>();
        Dom.Document xmlDoc = new Dom.Document();
        boolean bDupliNode = false;
        boolean bDupliNodes = false;
        xmlDoc.load(strXmlInput);
        Dom.XMLNode rootNode = xmlDoc.getRootElement();
        if(rootNode != null)
        {
            Dom.XMLNode[] fullNode = rootNode.getChildElements();
            integer iCounter =0;
            integer iCounters =0;
            if(fullNode != null)
            {
                for(Dom.XMLNode eachFullNode : fullNode)
                {
                    if(eachFullNode != null)
                    {
                        for(Dom.XMLNode mpkeyValue : eachFullNode.getChildElements())
                        {
                            String  keyval = mpkeyValue.getParent().getName()+system.label.HUMSPhoneMappingObj_colon+mpkeyValue.getName();
                            if(mapTagsToValues.containsKey(keyval))
                            {
                                mapTagsToValues.put(keyval+string.valueof(iCounter),mpkeyValue.getText());
                                bDupliNode = true;
                            }
                            else
                            {
                                mapTagsToValues.put(keyval+string.valueof(iCounter),mpkeyValue.getText());
                                bDupliNode = true;
                            }
                            if(mpkeyValue != null)
                            {
                                for(Dom.XMLNode mpkeyValues : mpkeyValue.getChildElements())
                                {
                                    String  keyvals = mpkeyValues.getParent().getName()+system.label.HUMSPhoneMappingObj_colon+mpkeyValues.getName();
                                    if(mapTagsToValues.containsKey(keyvals))
                                    {
                                        mapTagsToValues.put(keyvals+string.valueof(iCounters),mpkeyValues.getText());
                                        bDupliNodes = true;
                                    }
                                    else
                                    {
                                        mapTagsToValues.put(keyvals+string.valueof(iCounters),mpkeyValues.getText());
                                        bDupliNodes = true;
                                    }
                                }
                                if(bDupliNodes) {iCounters++;}
                            }
                        }
                        if(bDupliNode) {iCounter++;}
                    }
                }
            }
            mapTagsToValues.put(system.label.HUM_NumberOfNodes,String.Valueof(iCounter));
        }
        return mapTagsToValues;
    }
    
    
    /*
    * Method name : collectChildNodes
    * Description : This method is called from xmlInputStringParser
    * Return Type : Map<String,String>
    * Parameter   : Dom.XMLNode , Map<String,String>
    */
    public static Map<String,String> collectChildNodes( Dom.XMLNode mpkeyValue, Map<String,String> resultantMap)
    {
        integer i = 0;
        for(Dom.XMLNode echFullNode : mpkeyValue.getChildElements())
        {
            String childKeyVal = echFullNode.getParent().getParent().getName()+system.label.HUMSPhoneMappingObj_colon+echFullNode.getParent().getName();
            if(echFullNode.getChildElements()==null||echFullNode.getChildElements().size()==0) {resultantMap.put(echFullNode.getName(),echFullNode.getText());}
            else {resultantMap = HUMIntegrationUtilityHelper.collectChildNodes(echFullNode,resultantMap);}
        }
        return resultantMap;
    }
       
    /*
    * Method name : getRepeatingValuesFromXMLString
    * Description : This method is called to get RepeatingValues From XMLString
    * Return Type : list<string>
    * Parameter   :  String, String
    */
    public static list<string> getRepeatingValuesFromXMLString(string xmlString, string keyField)
    {
        //We want this to be an ordered collection so we need a list and not a set
        integer foundStart = 0;
        integer foundEnd = 0;
        integer currentSpot = 0;
        string itemAdd = '';
        list<string> repeatingValues = new list<string>();
        
        if(xmlString.indexOf(system.label.HUMEESLegacyMemberSearchHelper_OpenBraces+keyField+ system.label.HUMEESLegacyMemberSearchHelper_CloseBraces) !=-1&& xmlString.indexOf(system.label.HUMEESLegacyMemberSearchHelper_OpenBraces+system.label.HUM_Slash+keyField+ system.label.HUMEESLegacyMemberSearchHelper_CloseBraces)!=-1)
        {
            do
            {
                foundStart = xmlString.indexOf(system.label.HUMEESLegacyMemberSearchHelper_OpenBraces + keyField + system.label.HUMEESLegacyMemberSearchHelper_CloseBraces, currentSpot);
                if(foundStart >= 0)
                {
                    foundEnd = xmlString.indexOf(system.label.HUMEESLegacyMemberSearchHelper_OpenBraces+system.label.HUM_Slash + keyField + system.label.HUMEESLegacyMemberSearchHelper_CloseBraces, foundStart + keyField.length());
                    itemAdd = xmlString.substring(foundStart + keyField.length() + 2, foundEnd);
                    repeatingValues.add(itemAdd);
                }
                currentSpot = foundEnd + keyField.length();
            }
            while (foundStart != -1);
        }
        return repeatingValues;
    }
}