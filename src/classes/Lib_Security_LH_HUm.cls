/*******************************************************************************************************************************
Apex Class Name : Lib_Security_LH_HUm
Version         : 1.0
Created On      : 12/13/2021
Function        : This class is used for check the access on sobject before manipulating database on Humana level.

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                    12/13/2021               Original Version
*/
public with Sharing class Lib_Security_LH_HUm {
    
    /**
* Checks CREATE access on Object, and Fields accessed on the given record
* @param record to be checked for CRUD/FLS access.
*/
    public static void assertRead(SObject record) {
        assertRead(new SObject[]{record});
    } 
    
    /**
* Checks CREATE access on Object, and Fields accessed on the given records
* @param records to be checked for CRUD/FLS access.
*/
    public static void assertRead(SObject[] records) {
        assertAccess(AccessType.READABLE, records);        
    }
    /**
* Checks CREATE access on Object, and Fields accessed on the given record
* @param record to be checked for CRUD/FLS access.
*/
    public static void assertUpsert(SObject record) {
        assertUpsert(new SObject[]{record});
    } 
    
    /**
* Checks CREATE access on Object, and Fields accessed on the given records
* @param records to be checked for CRUD/FLS access.
*/
    public static void assertUpsert(SObject[] records) {
        assertAccess(AccessType.UPSERTABLE, records);        
    } 
    /**
* Checks CREATE access on Object, and Fields accessed on the given record
* @param record to be checked for CRUD/FLS access.
*/
    public static void assertCreate(SObject record) {
        assertCreate(new SObject[]{record});
    } 
    
    /**
* Checks CREATE access on Object, and Fields accessed on the given records
* @param records to be checked for CRUD/FLS access.
*/
    public static void assertCreate(SObject[] records) {
        assertAccess(AccessType.CREATABLE, records);        
    } 
    
    /** 
* @param accessType type of access to assert on the given records     
* @param records to be checked for CRUD/FLS access.
*/ 
    static void assertAccess(AccessType accessType, SObject[] records){
        if (records == null || accessType == null || records.isEmpty()) {
           throw createException('No records to found to check access' , null);
        } 
        Schema.DescribeSObjectResult dsr = records[0].getSObjectType().getDescribe();
        String objectName = dsr.getLabel(); 
        try{
            // Strip fields that are not updatable
            SObjectAccessDecision decision = Security.stripInaccessible(accessType, records);
            //K: SObject Name, V: Set of Field Names which are not accessible
            Map<String,Set<String>> removedFields = decision.getRemovedFields();
            for(String objName : removedFields.keySet()){
                String accessMissing = string.join(new List<String>(removedFields.get(objName)),',');
                throw createException('access missing on fields----'+accessMissing , null);
            }
        }catch(System.NoAccessException ex){
            throw createException(AccessType.name()+'access missing on Object'+objectName , ex);
        }
    }
    
    
    public static Exception createException(String msg, Exception ex) { 
        String errorMsg = formErrorMsge(msg , ex);
        // VF Page or Async Apex
        if(Apexpages.currentPage() != null) {
            return ex != null ? new AppException(errorMsg, ex) : new AppException(errorMsg);
        }
        return new System.AuraHandledException(errorMsg);
    } 
    
    public Static String formErrorMsge(String msg, Exception ex){
        String value = '\'{ errorMessage - }\' {0} \'{and Exception trace - }\' {1}';
        String exceptionMsge = ex!=null ? String.valueOf(ex.getMessage()) : '';
        return String.format(value, new List<String>{msg, exceptionMsge});  
    }
    
    // App Exception
    public virtual class AppException extends Exception {} 
}