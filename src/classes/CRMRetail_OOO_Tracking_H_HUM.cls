/*******************************************************************************************************************************
Apex Class Name  : CRMRetail_OOO_Tracking_H_HUM 
Version          : 1.0
Created Date     : 07/20/2021
Function         : Helper class for CRMRetail_OOO_Tracking_LWC_HUM            

Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  Vinoth L                                     07/21/2021               User Story - 2492158 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Track All Screenings
*  Abhishek Maurya                              07/21/2021               User Story - 2491779 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: One-on-One Tracking Enhancements 
*  Vinoth L                                     09/02/2021               User Story - 2508432 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Call DH&A 
*  Abhishek Maurya                              09/02/2021               User Story - 2599455 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Update Existing 1x1 Tracking
*  Mohamed Thameem                              27/05/2023               User Story - 4647349 T1PRJ0154546 / SF / MF9 Storefront - Add Last 3 One-On-One Information
*  Deepak Khandelwal                            04/04/2024               User Story - 5714924 T1PRJ1371022: MF: 5571284  / SF / Storefront - CxOne - HIGH/Medium- CRMRetail_OOO_Tracking_H_HUM VIT37935981 VIT37935988  
*******************************************************************************************************************************/
public with sharing class CRMRetail_OOO_Tracking_H_HUM {
    
    public class OneOnOneException extends Exception {} 
    Static boolean flag = false;
    Static boolean isFetchedRecord = true;
    Static List<Sobject> listOfRecordsToDelete = new List<Sobject>();
    Static List<CRMRetail_SDoHDetermt__c> lstOfSdohToDel = new List<CRMRetail_SDoHDetermt__c>();
    static List<CRMRetail_SDoHDetermt__c> lstOfSdoHRecords= new List<CRMRetail_SDoHDetermt__c>();
     static List<CRMRetail_SDoHDetermt__c> lstOfSdohToUpsert = new List<CRMRetail_SDoHDetermt__c>();
    
    static List<CRMRetailSDoH_DTO_HUM> lstOfSdohToInsrtOnDhna= new List<CRMRetailSDoH_DTO_HUM>();
    static List<CRMRetailSDoH_DTO_HUM> lstOfSdohToUpdtOnDhna= new List<CRMRetailSDoH_DTO_HUM>();
    static List<CRMRetailSDoH_DTO_HUM> lstOfSdohToDelOnDhna= new List<CRMRetailSDoH_DTO_HUM>();
    static Set<String> finalAPIStatus = new Set<String>();
    static Set<String> setOfDetToUpdtVLikely =new Set<String>();
    static Set<String> setOfDetToUpdtVUnLikely =new Set<String>();
    
    @AuraEnabled
    public static List<String> callSDohAPIOnUpdateOrInsert(String accEnterpriseId,String oooRecordId,Datetime createdDate,List<String> sdohDeterToInstVryLkly, List<String> sdohDeterToInstVryUnLkly,List<String> sdohDeterToUpdtVryLkly,List<String> sdohDeterToUpdtVryUnLkly, List<String> sdohDeterToDelVryLkly,List<String> sdohDeterToDelVryUnLkly,List<String> listOfStatus){
        try{
        finalAPIStatus.addAll(listOfStatus);
        if(listOfStatus.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE)){
            duplicateCheckBefInst(createdDate,sdohDeterToInstVryLkly,sdohDeterToInstVryUnLkly,accEnterpriseId);
            if(callInsertService(createdDate,sdohDeterToInstVryLkly,sdohDeterToInstVryUnLkly,accEnterpriseId)){
                finalAPIStatus.remove(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE);
            }
            else{
                listOfStatus.clear();
                listOfStatus.addAll(finalAPIStatus);
                return listOfStatus;
            }
        }
        if(listOfStatus.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE) || (setOfDetToUpdtVLikely.size()>0 || setOfDetToUpdtVUnLikely.size()>0)){
            if(callUpdateService(createdDate,sdohDeterToUpdtVryLkly,sdohDeterToUpdtVryUnLkly,accEnterpriseId)){
                finalAPIStatus.remove(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE);
            }else{
                listOfStatus.clear();
                listOfStatus.addAll(finalAPIStatus);
                return listOfStatus;
            }
        }
        if(listOfStatus.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE)){
            if(callDeleteService(createdDate,sdohDeterToDelVryLkly,sdohDeterToDelVryUnLkly,accEnterpriseId)){
                finalAPIStatus.remove(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE);
            }else{
                listOfStatus.clear();
                listOfStatus.addAll(finalAPIStatus);
                return listOfStatus;
            }
        }
        listOfStatus.clear();
        }
        catch(Exception ex){
                            HUMExceptionHelper.logErrors(ex, 'CRMRetail_OOO_Tracking_H_HUM ', 'callSDohAPIOnUpdateOrInsert ');
                            return listOfStatus;
                        }
        return listOfStatus;
    }
    static void duplicateCheckBefInst(DateTime createdDate,List<String> sdohDeterToInstVryLkly,List<String> sdohDeterToInstVryUnLkly,String accEnterpriseId){
        if((sdohDeterToInstVryLkly != NULL && sdohDeterToInstVryLkly.size()>0) || (sdohDeterToInstVryUnLkly != NULL && sdohDeterToInstVryUnLkly.size()>0)){
            Date dateWithoutTime;
            if(createdDate != NULL){
                dateWithoutTime = createdDate.date();
            }
            else{
                dateWithoutTime = system.today();
            }
            List<CRMRetail_SDoHDetermt__c> lstOfSdoh = [SELECT Id,CRMRetail_OOO_Tracking__r.Account__r.Enterprise_ID__C,Determinant__c from CRMRetail_SDoHDetermt__c 
                                                        where DAY_ONLY(createddate)=: dateWithoutTime AND CRMRetail_OOO_Tracking__r.Account__r.Enterprise_ID__C =: accEnterpriseId ];
            if(lstOfSdoh.size()>0){                
                for(CRMRetail_SDoHDetermt__c sdohRef : lstOfSdoh){
                    if(sdohDeterToInstVryLkly.contains(sdohRef.Determinant__c))
                    {
                        setOfDetToUpdtVLikely.add(sdohRef.Determinant__c);
                    }else if(sdohDeterToInstVryUnLkly != NULL && sdohDeterToInstVryUnLkly.size()>0 && sdohDeterToInstVryUnLkly.contains(sdohRef.Determinant__c)){
                        setOfDetToUpdtVUnLikely.add(sdohRef.Determinant__c);
                    }
                }
            }
        }
    }
    static boolean callInsertService(DateTime createdDate,List<String> sdohDeterToInstVryLkly,List<String> sdohDeterToInstVryUnLkly,String accEnterpriseId){
        Boolean result=false;
        if(sdohDeterToInstVryLkly != NULL && sdohDeterToInstVryLkly.size()>0){
            if(setOfDetToUpdtVLikely.size()>0){
                Set<String> tempLstLikely = new Set<String>(sdohDeterToInstVryLkly);
                tempLstLikely.removeAll(setOfDetToUpdtVLikely);
                sdohDeterToInstVryLkly.clear();
                sdohDeterToInstVryLkly = new List<String>(tempLstLikely);
            }
            if(sdohDeterToInstVryLkly.size()>0){
            lstOfSdohToInsrtOnDhna.addALL(prepareDHNAData(createdDate,sdohDeterToInstVryLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE)); 
            }
        } 
        if(sdohDeterToInstVryUnLkly != NULL && sdohDeterToInstVryUnLkly.size()>0){
             if(setOfDetToUpdtVUnLikely.size()>0){
                Set<String> tempLstUnLikely = new Set<String>(sdohDeterToInstVryUnLkly);
                tempLstUnLikely.removeALL(setOfDetToUpdtVUnLikely);
                sdohDeterToInstVryUnLkly.clear();
                sdohDeterToInstVryUnLkly = new List<String>(tempLstUnLikely);
             }
            if(sdohDeterToInstVryUnLkly.size()>0){
            lstOfSdohToInsrtOnDhna.addALL(prepareDHNAData(createdDate,sdohDeterToInstVryUnLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE));
            }
        } 
        if(lstOfSdohToInsrtOnDhna.size()>0){
            result = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(lstOfSdohToInsrtOnDhna,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_CREATE);
        }else{result=True;}
        return result;
    }
    static boolean callUpdateService(DateTime createdDate,List<String> sdohDeterToUpdtVryLkly,List<String> sdohDeterToUpdtVryUnLkly,String accEnterpriseId){
        Boolean result=false;
        if(sdohDeterToUpdtVryLkly.size() != NULL && sdohDeterToUpdtVryLkly.size()>0){
            if(setOfDetToUpdtVLikely.size()>0){
                sdohDeterToUpdtVryLkly.addAll(setOfDetToUpdtVLikely);
            }
            lstOfSdohToUpdtOnDhna.addAll(prepareDHNAData(createdDate,sdohDeterToUpdtVryLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE));
        } else{ 
            if(setOfDetToUpdtVLikely.size()>0){
                sdohDeterToUpdtVryLkly.addAll(setOfDetToUpdtVLikely);
                lstOfSdohToUpdtOnDhna.addAll(prepareDHNAData(createdDate,sdohDeterToUpdtVryLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE));
            }
        } 
        if(sdohDeterToUpdtVryUnLkly.size() != NULL && sdohDeterToUpdtVryUnLkly.size()>0){
            if(setOfDetToUpdtVUnLikely.size()>0){
                sdohDeterToUpdtVryUnLkly.addAll(setOfDetToUpdtVUnLikely);
            }
            lstOfSdohToUpdtOnDhna.addAll(prepareDHNAData(createdDate,sdohDeterToUpdtVryUnLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE));
        } else{
            if(setOfDetToUpdtVUnLikely.size()>0){
                sdohDeterToUpdtVryUnLkly.addAll(setOfDetToUpdtVUnLikely);
                lstOfSdohToUpdtOnDhna.addAll(prepareDHNAData(createdDate,sdohDeterToUpdtVryUnLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE));
            }
        }
        if(lstOfSdohToUpdtOnDhna.size()>0){
            result = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(lstOfSdohToUpdtOnDhna,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_UPDATE);
        }else{result= true;}
        return result;
    }
    static boolean callDeleteService(DateTime createdDate,List<String> sdohDeterToDelVryLkly,List<String> sdohDeterToDelVryUnLkly, String accEnterpriseId){
        Boolean result=false;
        if(sdohDeterToDelVryLkly.size() != NULL && sdohDeterToDelVryLkly.size()>0){
            lstOfSdohToDelOnDhna.addAll(prepareDHNAData(createdDate,sdohDeterToDelVryLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE));
        } 
        if(sdohDeterToDelVryUnLkly.size() != NULL && sdohDeterToDelVryUnLkly.size()>0){
            lstOfSdohToDelOnDhna.addAll(prepareDHNAData(createdDate,sdohDeterToDelVryUnLkly, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY,accEnterpriseId,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE));
        } 
        if(lstOfSdohToDelOnDhna.size()>0){
            result = CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(lstOfSdohToDelOnDhna,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE);
        }else{result= true;}
        return result;
    }
    static List<CRMRetailSDoH_DTO_HUM> prepareDHNAData(DateTime createdDate,List<String> listOfDeterminants, String desrciption, String enterpriseId,String eventType){
        List<CRMRetailSDoH_DTO_HUM> lstOfReqBodies = new List<CRMRetailSDoH_DTO_HUM>();
        CRMRetailSDoH_DTO_HUM dtoRef;
        for(String determinant : listOfDeterminants){
            dtoRef = new CRMRetailSDoH_DTO_HUM();
            dtoRef.sdrPersonId = enterpriseId;
            if(eventType == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE){
                if(createdDate != NULL){
                dtoRef.screeningDate = String.valueOf(createdDate.date());
                }
                else{
                dtoRef.screeningDate = String.valueOf(system.Today());      
                }
            }
            else{
                dtoRef.screeningDate = String.valueOf(system.Today());  
            }
            dtoRef.metricName = determinant;
            if(desrciption ==GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY){
                dtoRef.metricRating = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_CHOSEN_LIST;
            }else{
                dtoRef.metricRating = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_AVAILABLE_LIST;
            }
            dtoRef.metricDescription = desrciption;
            dtoRef.metricSource = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HNC;
            dtoRef.timestamp = system.now();
            lstOfReqBodies.add(dtoRef);
        }
        return lstOfReqBodies;
    }
    /**
    *  Fetch SDoh Records with the Description Very Likely 
    *  @name getScreenedResult
    *  @params String oooRecordId - holds the ooo tracking id
    *  @return List<String>
    *  @throws NA    
    */
    @AuraEnabled
    public static Map<String,Object>getScreenedResult(String oooRecordId){
        MAp<String,Object> mapOfDeterminantsByDescription = new Map<String,Object>();
        List<String> listOfLikelyDeter =new List<String>();
        List<String> listOfUnLikelyDeter =new List<String>();
        String enterpriseId;
        for(CRMRetail_SDoHDetermt__c sdohRef : [SELECT Id,Description__c,Createddate,Determinant__c,CRMRetail_OOO_Tracking__r.Account__r.Enterprise_Id__c from CRMRetail_SDoHDetermt__c where CRMRetail_OOO_Tracking__c =:oooRecordId ]){
            if(sdohRef.Description__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY){
                listOfLikelyDeter.add(sdohRef.Determinant__c);  
            }else{
                listOfUnLikelyDeter.add(sdohRef.Determinant__c);  
            }
            if(String.isBlank(enterpriseId)){
                enterpriseId = sdohRef.CRMRetail_OOO_Tracking__r.Account__r.Enterprise_Id__c;
            }
        }
        if(listOfLikelyDeter.size()>0){
            mapOfDeterminantsByDescription.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,listOfLikelyDeter);
        }
        if(listOfUnLikelyDeter.size()>0){
            mapOfDeterminantsByDescription.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY,listOfUnLikelyDeter);
        } 
        if(String.isNotBlank(enterpriseId))
        {
           mapOfDeterminantsByDescription.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ENTERPRISE_ID_KEY,enterpriseId);            
        }
        return mapOfDeterminantsByDescription;
    }
    @AuraEnabled
    public static void checksOperactionTypeForSDohRecords(String oooRecordId,Datetime createdDate,List<String> sdohDeterToInstVryLkly,List<String> sdohDeterToInstVryUnLkly,List<String> sdohDeterToDelVryLkly,List<String> sdohDeterToDelVryUnLkly,List<String> sdohDeterToUpdtVryLkly,List<String> sdohDeterToUpdtVryUnLkly){
        try{
            if(sdohDeterToInstVryLkly.size() != NULL && sdohDeterToInstVryLkly.size()>0){
                updateSdohRecord(oooRecordId,sdohDeterToInstVryLkly,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY);
                lstOfSdohToUpsert.addAll(createSdohRecords(oooRecordId,createdDate,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY,sdohDeterToInstVryLkly));
            } 
            if(sdohDeterToInstVryUnLkly.size() != NULL && sdohDeterToInstVryUnLkly.size()>0){
                updateSdohRecord(oooRecordId,sdohDeterToInstVryUnLkly,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY);
                lstOfSdohToUpsert.addAll(createSdohRecords(oooRecordId,createdDate,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY,sdohDeterToInstVryUnLkly));
            } 
            if(sdohDeterToUpdtVryLkly.size() != NULL && sdohDeterToUpdtVryLkly.size()>0){                                
                updateSdohRecord(oooRecordId,sdohDeterToUpdtVryLkly,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY);
            } 
            if(sdohDeterToUpdtVryUnLkly.size() != NULL && sdohDeterToUpdtVryUnLkly.size()>0){                                
                updateSdohRecord(oooRecordId,sdohDeterToUpdtVryUnLkly,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY);
            } 
            if(sdohDeterToDelVryLkly.size() != NULL && sdohDeterToDelVryLkly.size()>0){                                
                deletedSdohRecord(oooRecordId,sdohDeterToDelVryLkly);
            }
            if(sdohDeterToDelVryUnLkly.size() != NULL && sdohDeterToDelVryUnLkly.size()>0){                                
                deletedSdohRecord(oooRecordId,sdohDeterToDelVryUnLkly);
            }
            if(lstOfSdohToUpsert.size()>0){
              
                GenericHelper_CRMRetail_H_HUM.checkAccessForDML('UPSERT',lstOfSdohToUpsert, 'CRMRetail_OOO_Tracking_H_HUM','checksOperactionTypeForSDohRecords'); 
            }
            if(lstOfSdohToDel.size()>0){
              
                GenericHelper_CRMRetail_H_HUM.checkAccessForDML(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DELETE_EVENT_TYPE,lstOfSdohToDel, 'CRMRetail_OOO_Tracking_H_HUM','checksOperactionTypeForSDohRecords');  
            }
        }catch(Exception ex){             
            HUMExceptionHelper.logErrors(ex, 'CRMRetail_OOO_Tracking_H_HUM ', 'CRMRetail_OOO_Tracking_H_HUM ');
            throw new OneOnOneException();
        }
    }
    /**
    *  Checks for previous SDoH records to be inserted for the One-On-One Tracking Record created
    *  @name createSdohRecords
    *  @param String oooRecordId - holds the ooo tracking id,DateTime CreatedDate- Created Date of OOO tracking record
    *  @return void
    *  @throws NA    
    */
    public static  List<CRMRetail_SDoHDetermt__c> createSdohRecords(String oooRecordId,Datetime createdDate,String desrciption,List<String> listOfDeterminants){      
        List<CRMRetail_SDoHDetermt__c> lstOfSDoHRecordsToBeInserted = new List<CRMRetail_SDoHDetermt__c>();
        CRMRetail_SDoHDetermt__c sDoHRecord;
        for(String determinent : listOfDeterminants){
            if(!setOfDetToUpdtVLikely.contains(determinent) && !setOfDetToUpdtVUnLikely.contains(determinent)){
                sDoHRecord = new CRMRetail_SDoHDetermt__c(); 
                if(Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.Name.isUpdateable() && Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.Determinant__c.isUpdateable() && Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.CRMRetail_OOO_Tracking__c.isUpdateable() && Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.Source__c.isUpdateable()  && Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.Score_Date__c.isUpdateable() && Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.Rating__c.isUpdateable() && Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.Description__c.isUpdateable() && Schema.SObjectType.CRMRetail_SDoHDetermt__c.fields.Shared__c.isUpdateable()) 
                {                           
                    sDoHRecord.Name = determinent;            
                    sDoHRecord.Determinant__c = determinent;    
                    sDoHRecord.CRMRetail_OOO_Tracking__c = oooRecordId;
                    sDoHRecord.Source__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HNC;
                    sDoHRecord.Score_Date__c = createdDate.date(); 
                    if(desrciption ==GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY){
                        sDoHRecord.Rating__c= GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_CHOSEN_LIST;
                    }else{
                        sDoHRecord.Rating__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_AVAILABLE_LIST;
                    }
                    sDoHRecord.Description__c = desrciption;
                    sDohRecord.Shared__c = true;
                    lstOfSDoHRecordsToBeInserted.add(sDoHRecord);
                }
            }
        }
        return lstOfSDoHRecordsToBeInserted;       
    }
    static void updateSdohRecord(String oooRecordId,List<String> listOfDeterminants,String description){
        if(isFetchedRecord){
            lstOfSdoHRecords = getSDoHRecords(oooRecordId);
        }
        for(CRMRetail_SDoHDetermt__c ref:lstOfSdoHRecords){
            if(listOfDeterminants.contains(ref.Determinant__c)){
                if(description == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY){
                    ref.Description__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY;
                    ref.Rating__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_CHOSEN_LIST;
                    setOfDetToUpdtVLikely.add(ref.Determinant__c);
                }else{
                    ref.Description__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_VERY_UNLIKELY;
                    ref.Rating__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_AVAILABLE_LIST;
                    setOfDetToUpdtVUnLikely.add(ref.Determinant__c);
                }
                ref.Shared__c = false;
                lstOfSdohToUpsert.add(ref);
            }
        }
    }
    static void deletedSdohRecord(String oooRecordId,List<String> listOfDeterminants){
        List<CRMRetail_SDoHDetermt__c> lstOfSdoHRecords= new List<CRMRetail_SDoHDetermt__c>();
        lstOfSdoHRecords = getSDoHRecords(oooRecordId);
        for(CRMRetail_SDoHDetermt__c ref:lstOfSdoHRecords){
            if(listOfDeterminants.contains(ref.Determinant__c)){lstOfSdohToDel.add(ref);}
        }
    }
    /**
    *  Checks for previous SDoH records to be deleted on Delete
    *  @name deleteSDoHBeforeOOOTrackingDelete
    *  @param lstRecords Holds the list of all One-On-One Tracking records to be deleted
    *  @return void
    *  @throws NA    
    */
    @AuraEnabled
    public static String deleteSDoHBeforeOOOTrackingDelete(String oooRecordId,Boolean isOverrideButton){
        String result;
        List<CRMRetail_SDoHDetermt__c> lstOfSdoHRecords= new List<CRMRetail_SDoHDetermt__c>();
        CRMRetail_OOO_Tracking__c oooRecord = [SELECT Id,SDoH_Screening_Completed__c,Account__r.Enterprise_Id__C,Account__C from CRMRetail_OOO_Tracking__c WHERE Id =:oooRecordId LIMIT 1];
        result = oooRecord.Account__c;
        try{
            if(isOverrideButton){
                listOfRecordsToDelete.add(oooRecord);    
            }            
            if(oooRecord.SDoH_Screening_Completed__c){
                lstOfSdoHRecords = getSDoHRecords(oooRecord.Id);
                if(lstOfSdoHRecords.size() > 0){
                    List<CRMRetailSDoH_DTO_HUM> lstOfSdohDto = prepareDhnaDataOnDel(lstOfSdoHRecords,oooRecord.Account__r.Enterprise_Id__C);
                    if(lstOfSdohDto.size()>0){
                        if(!CRMRetail_SDoH_Helper_HUM.callSDoHServiceAPIOnShare(lstOfSdohToDelOnDhna,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_DHNA_DELETE)){
                           return result = GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_ERROR; 
                        }
                    }
                    listOfRecordsToDelete.addAll(lstOfSdoHRecords);
                }
            }                                    
            if(listOfRecordsToDelete.size()>0){                                
                delete listOfRecordsToDelete;
            }            
        }
        catch(Exception ex){                 
            HUMExceptionHelper.logErrors(ex, 'CRMRetail_OOO_Tracking_H_HUM ', 'CRMRetail_OOO_Tracking_H_HUM '); 
            Throw New OneOnOneException();
        }
        return oooRecord.Account__c;
    }
    /**
    *  Collect SDoH records 
    *  @name getSDoHRecords
    *  @param setOfOneOnOneTrackingIdsToDelete Holds the Set of all One-On-One Tracking record Ids to be deleted
    *  @return void
    *  @throws NA    
    */
    public static List<CRMRetail_SDoHDetermt__c> getSDoHRecords(String oneOnOneId){
        List<CRMRetail_SDoHDetermt__c> listOfSdoHRecords= new List<CRMRetail_SDoHDetermt__c>();
        listOfSdoHRecords = [SELECT Id,Determinant__c,Source__c,Rating__c,Description__c from CRMRetail_SDoHDetermt__c where CRMRetail_OOO_Tracking__c =: oneOnOneId];
        isFetchedRecord = false;
        return listOfSdoHRecords;
    }
    static List<CRMRetailSDoH_DTO_HUM> prepareDhnaDataOnDel(List<CRMRetail_SDoHDetermt__c> lstOfSdoh,String enterpriseId){
        List<CRMRetailSDoH_DTO_HUM> lstOfReqBodies = new List<CRMRetailSDoH_DTO_HUM>();
        CRMRetailSDoH_DTO_HUM dtoRef;
        for(CRMRetail_SDoHDetermt__c sdohRef : lstOfSdoh){
            dtoRef = new CRMRetailSDoH_DTO_HUM();
            dtoRef.sdrPersonId = enterpriseId;
            dtoRef.screeningDate = sdohRef.Source__c;
            dtoRef.metricName = sdohRef.Determinant__c;
            dtoRef.metricRating = sdohRef.Rating__c;
            dtoRef.metricDescription = sdohRef.Description__c;
            dtoRef.metricSource = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HNC;
            dtoRef.timestamp = system.now();
            lstOfReqBodies.add(dtoRef);
        }
        return lstOfReqBodies;
    }

    /** 
    *  @name : getRecentOOORecords
    *  @param : String accountId, String oooId
    *  @return : CRMRetail_Result_DTO_HUM
    */
    @AuraEnabled
    public static CRMRetail_Result_DTO_HUM getRecentOOORecords(String accountId, String oooId){

        CRMRetail_Result_DTO_HUM oooResponse = new CRMRetail_Result_DTO_HUM();
        try{
            if(String.isBlank(accountId))
            {
                List<CRMRetail_OOO_Tracking__c> oooList = [select id, Account__c from CRMRetail_OOO_Tracking__c where id = :oooId];
                if(oooList.size()>0)
                {
                    accountId = oooList[0].Account__c;
                }
            }
            oooResponse.sResult = [SELECT Id, CreatedDate, CreatedBy.Name, Other_Comments__c, Description_of_Visit__c, Appointment_Date__c, Appointment_Time__c FROM CRMRetail_OOO_Tracking__c WHERE Account__C = :accountId and id != :oooId order by Appointment_Date__c desc,Appointment_Time__c desc LIMIT :Integer.valueOf(System.label.CRMRetail_Recent_OOO_Record_Limit)];
        }
        catch(Exception ex){ 
            oooResponse.isError = true;               
            HUMExceptionHelper.logErrors(ex, 'CRMRetail_OOO_Tracking_H_HUM', 'getRecentOOORecords'); 
        }
        return oooResponse;
    }
}