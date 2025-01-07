/*************************************************************************************************************************************************************
 Apex Class Name : RTIApplicationValues_H_HUM
 Version         : 1.0
 Created On      :  05/18/2021
 Function        : Helper class for retrieving Channel values for RTI Channels from custom metadata type
 Test Class      : RTISummaryService_T_HUM

 Modification Log:
 * Developer Name                  Code Review            Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Prasanthi Kandula                                      05/18/2021                 US - 2082407 Created class to display new channel codes
 *************************************************************************************************************************************************************/


public with sharing class RTIApplicationValues_H_HUM
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
        for (RTI_Application_Value__mdt validValue : [ select Attribute_Label__c, Attribute_Value__c,
                                                                Field_Label__c, Parent_Attribute_Value__c, Disposition_Code__c,Category_Code__c,
                                                                Status_Code__c,
                                                                Parent_Field_Label__c, Sequence__c from 
                                                                RTI_Application_Value__mdt where
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
                theFieldList.addValidValue(validValue); 
               }
        }
        return attributeMap;
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
        public void addValidValue(RTI_Application_Value__mdt validValue) 
        {
            
            AttributeValue theAttributeValue = new AttributeValue();
            theAttributeValue.value = validValue.Attribute_Value__c;
            theAttributeValue.label = validValue.Attribute_Label__c;
            theAttributeValue.sequence = validValue.Sequence__c;
            theAttributeValue.dispositionCode = validValue.Disposition_Code__c;
            theAttributeValue.channelCategoryCode = validValue.Category_Code__c;
            theAttributeValue.statuscode = validValue.Status_Code__c;
                                                                
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
        public string dispositionCode;
        public string channelCategoryCode;
        public string statuscode;
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