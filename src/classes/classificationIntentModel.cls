public with Sharing class classificationIntentModel{
    @AuraEnabled
    public List<OptionModel> classificationToIntentValues;//label will be classification and value will be corrospond classification Id
    @AuraEnabled
    public Map<Id,List<OptionModel>> classificationToIntent = new Map<Id,List<OptionModel>>(); //classfification id to list of corrospond intent 
    @AuraEnabled
    public Map<String,Map<String,String>> mpOfclassificationIntentToCTCIId;
    @AuraEnabled
    Public Map<Id,CTCI_Junction__c> mapOfCTCIIdToCTCI; //This map is used to store CTCI id and CTCI object
    @AuraEnabled
    Public CTCI_Junction__c ctciObj; // this is used to send the ctci record in edit case.
    @AuraEnabled
    public String intentName; // used to populate value on intent picklist on Edit case if value is present
    @AuraEnabled
    public String classificationName; // used to populate value on classiciation picklist on Edit case if value is present
     
    /*
* Method Name : collectionToOption
* Description : This method is used to convert listof string into label vs value list.
* Return type : Object(it will be in combobox format like[{label:'xz',value:'cc'} , {label:'xz',value:'cc'}])
* Paramater   :Object(it will accept currently List<String> as an object but for future it can accept Map and set etc....)
*/
    public  Object collectionToOption(Object collectionData){
        List <String> collection;
        List<optionModel> lstOptModel = new List<optionModel>();
        if(collectionData instanceof List<String>){
            collection = (List<String>) collectionData;
        }
        
        for(Object lst : collection){
            lstOptModel.add(new optionModel(String.valueOf(lst) , String.valueOf(lst)));
        }
        return lstOptModel;
    }
        
    //this class is used for serving the combobox purpose on LWC which accept[{label:'abc',value:'aa'},{label:'abcd',value:'xyz'}] format
    public with sharing class OptionModel{
        @AuraEnabled
        public String label;
         @AuraEnabled
        public String value;
        
        public optionModel(String label , String value){
            this.label = label;
            this.value = value;
        }
    }
}