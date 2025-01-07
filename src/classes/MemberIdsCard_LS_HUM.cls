/*******************************************************************************************************************************
Apex Class Name : MemberIdsCard_LC_HUM
Version         : 1.0
Created On      : 06/29/2021
Function        : This class is service class for creating dynamic one to one mapping for MemberIds card and it can also use for other card for same scenario 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        06/29/2020               Original Version
*******************************************************************************************************************************************************************/

public with sharing class MemberIdsCard_LS_HUM {

     /*
    * Method name : createMemberIdData
    * Description : This method will return the one to one mapping object
    * Parameter   :Map<String,String>mpOfFieldVsLabel, MemberPlan membplan, ObjectVSFieldNameMapping__mdt[] objFldMetadata
    */
    public static Object createMemberIdData( Map<String,String>mpOfFieldVsLabel, sObject membplan, ObjectVSFieldNameMapping__mdt[] objFldMetadata){
        List<MemberIdCardData> memberIdCardModel = new List<MemberIdCardData>();
        MemberIdCardData memcard; 
       
        for(ObjectVSFieldNameMapping__mdt objFldmaping : objFldMetadata){
            Object fldValue='';
           
            if(mpOfFieldVsLabel.get(objFldmaping.MasterLabel).contains(',')){
                String referenceField = mpOfFieldVsLabel.get(objFldmaping.MasterLabel);
                List<String> fldApi = referenceField.split(',');
                fldValue = membplan.getsObject(fldApi[0]).get(fldApi[1]);
            }else{
                fldValue =  membplan.get(mpOfFieldVsLabel.get(objFldmaping.MasterLabel));
            }
            
            if(fldValue!=null && fldValue!=''){
                memcard = new MemberIdCardData(objFldmaping.MasterLabel,fldValue);
                memberIdCardModel.add(memcard);
            }
        }
        return memberIdCardModel.size()>0 ? memberIdCardModel : null;
    }
    
    /*Inner class to carry data to UI to show it on memberplan detail page on MemberIds card
    */
    public with sharing class MemberIdCardData{
        @AuraEnabled
        public String label;
        @AuraEnabled
        public Object value;
        
        public MemberIdCardData(String label , Object value){
            this.label = label;
            this.value = value;
        }
    }
}