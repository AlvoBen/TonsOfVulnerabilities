/*************************************************************************************************************************************************************
 Apex Class Name : RTIApplicationChannelValues_H_HUM
 Version         : 1.0
 Created On      : 12/13/2016
 Function        : Helper class for retrieving Channel values for RTI Channels from custom metadata type
 Test Class      : RTIApplicationChannelValues_T_HUM
 
 Modification Log:
 * Developer Name                  Code Review            Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Pallavi Ravishankar                                    12/13/2016                 Original Version
 * Kritika Popat                                          02/07/2017                 Code review fixes
 * Prathyu Tushar                                         07/30/2018                 RTI All categoryCode to support MF9 Switch
 *Rahul Chaurasia										  03/22/2021				 1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service
 *************************************************************************************************************************************************************/


public with sharing class RTIApplicationChannelValues_H_HUM
{
    /*
    * Method Name   :  getApplicationFieldAttributes 
    * Description   :  Getting AccountID for that member.
    * Return Type   :  Map<String, RTIApplicationChannelValues_H_HUM.Attributes>, key is the field label and value is list of attributes associated with the field
    * Parameters    :  applicationName - Name of Application in which to return the Attributes
    */
    public static Map<String, Attributes> getApplicationFieldAttributes (String applicationName)
    {
        Map<String, Attributes> attributeMap = new Map<String, Attributes>();
        Boolean flag;
        for (Application_Valid_Value__mdt validValue : [ select Attribute_Label__c, Attribute_Value__c,
                                                                Field_Label__c, Parent_Attribute_Value__c, 
                                                                Parent_Field_Label__c, Sequence__c from 
                                                                Application_Valid_Value__mdt where
                                                                IsActive__c = true order by 
                                                                Sequence__c limit 1000]) {
                                                                          
            if (String.isNotBlank(validValue.Field_Label__c))
            {
                Attributes theFieldList = new Attributes();
                if (!attributeMap.containsKey(validValue.Field_Label__c)) 
                {
                    attributeMap.put(validValue.Field_Label__c, theFieldList);
                }
                else
                {
                    theFieldList = attributeMap.get(validValue.Field_Label__c);
                }
                System.debug(validValue.Parent_Attribute_Value__c);
                if( validValue.Parent_Attribute_Value__c == 'DM' && validValue.Parent_Attribute_Value__c != null && validValue.Parent_Attribute_Value__c != ''){
                    flag = checkstaus(validValue.Attribute_Label__c);
                    if(flag)
                        theFieldList.addValidValue(validValue); 
                }
                else{
                    theFieldList.addValidValue(validValue); 
                }
            }
        }
        return attributeMap;
    }  
     
    public static string getCategoryCode(){
        Map<String, Boolean> mapCategoryCodes = new Map<String, Boolean>();
        String sAllCode = '';
        Boolean flag;
        try{
            for( Application_Valid_Value__mdt temp : [SELECT Id, Label, Application_Name__c,
                                                             Attribute_Label__c, Attribute_Value__c,
                                                             Field_Label__c, IsActive__c,
                                                             Parent_Attribute_Value__c, Parent_Field_Label__c,
                                                             Sequence__c FROM Application_Valid_Value__mdt where
                                                              IsActive__c = true and Parent_Attribute_Value__c = 'DM'
                                                               and Attribute_Label__c != 'All'] ){
                    if(sAllCode == '')
                        sAllCode = temp.Attribute_Value__c;
                    else
                        sAllCode = sAllCode + ',' + temp.Attribute_Value__c;    
             }
        }catch(exception e){
            System.debug('Error: '+e.getMessage());
        }
        return sAllCode;
    }
     
    public static boolean checkstaus(String sName){
        boolean bSuccess = true;
        List<RTI_Switch__c> lstSwitch = [SELECT IsEnabled__c, Name, Id FROM RTI_Switch__c where IsEnabled__c = true and Name = :sName]; 
        if(lstSwitch.size() != 1)
             bSuccess = false; 
         return bSuccess;
    }

    public class Attributes
    {    
        Map<String, List<AttributeValue>> attributeMap = new Map<String, List<AttributeValue>>();
      
       /*
        * Method Name   :  getAttributeValues
        * Description   :  Retrun the list of attribute values (e.g. picklist values) for the associated parent field value
        * Return Type   :  List<RTIApplicationChannelValues_H_HUM.AttributeValue>, list of attribute values (e.g. picklist options)
        * Parameters    :  parentValue - If dependent attributes, the value from the dependent field, otherwise null
       */      
        public List<AttributeValue> getAttributeValues(String parentValue)
        {
            if (parentValue == null)
            {
                parentValue ='root';
            }
            List<AttributeValue> retValue = new List<AttributeValue>();
            if (attributeMap.containsKey(parentValue)) 
            {
                retValue = attributeMap.get(parentValue);
            }
            retValue.sort();
            return retValue;
       }   
       
       /*
        * Method Name   :  addValidValue
        * Description   :  Add a metadata type record as a attribute associated with the attributes
        * Return Type   :  void
        * Parameters    :  validValue - The Application Valid Value Metadata type record to add
       */          
        public void addValidValue(Application_Valid_Value__mdt validValue) 
        {
            
            AttributeValue theAttributeValue = new AttributeValue();
            theAttributeValue.value = validValue.Attribute_Value__c;
            theAttributeValue.label = validValue.Attribute_Label__c;
            theAttributeValue.sequence = validValue.Sequence__c;
            String parentValue = validValue.Parent_Attribute_Value__c;
            
            if (String.isBlank(parentValue))
            {
                parentValue = 'root';
            }
            List<AttributeValue> theAttributeValueList = new List<AttributeValue>();
            if (!attributeMap.containsKey(parentValue))
            {
                attributeMap.put(parentValue, theAttributeValueList);
            }
            else 
            {
                theAttributeValueList = attributeMap.get(parentValue);
            }
            
            theAttributeValueList.add(theAttributeValue);  
        }        
    }  
  
    public class AttributeValue implements comparable
    {
        public String label;
        public String value;
        public Decimal sequence; 
        public Integer compareTo(Object cmpObj) 
        {
            Integer retval = 0;
            AttributeValue cmpAttributeValue = (AttributeValue) cmpObj;
            if (cmpAttributeValue != null && cmpAttributeValue.sequence != null && sequence != null)
            {        
                if (cmpAttributeValue.sequence > sequence)  { retVal = -1; }
                else if (cmpAttributeValue.sequence < sequence) { retVal = 1; }   
            }  
            return retVal;
        }
    }    
}