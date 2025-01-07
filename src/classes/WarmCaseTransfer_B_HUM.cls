/*******************************************************************************************************************************
Apex Class Name : WarmCaseTransfer_B_HUM
Version         : 1.0
Created On      : 01/31/2017
Function        : One time Batch Class to Update the status of the cases which were being reopened by 'CustomerService Build'
                   A detailed analysis was done on date '03/11/2017' by developer "Vijaya Koriprolu" on Sharing without option for this class.
                   During the analysis it was found that this class will update the status of case and it is updatable for every profile, which also handles exception’s from the batch job and will store it  in error logs
                   This was further discussed with Architects Amrish And Jeremy And got approval to keep the "Without Sharing" mode of this class as it Is.
Test Class      : WarmCaseTransfer_T_HUM 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Vamsi Kondragunta                      59634                  01/31/2017              Created;To correct data of issue; REQ - 296903
*******************************************************************************************************************************/
/**
    * Wrapper class to pass the case history
    * This is a batch job to correct the records the were being updated by 'CustomerService Build' ;
    * The cases closed by CSR's were being reopened and were being kept to 'In progress';
    * The purpose of this batch job is to correct those records
    * @return  None
    */ 

global class WarmCaseTransfer_B_HUM implements Database.Batchable<sObject>
{          
    global Database.QueryLocator start(Database.BatchableContext BC)
    { 
        String sStatus='status';
        String sClosed='Closed';
        String sIStatus='In Progress';
        String SUserid='';
        String SUserName='CustomerService Build';    // CustomerService Build for Prod,QA and Ranjeeth Nagishetty for Testfull
        List<User> lstUsers = [select id from user where name=:SUserName];
        if(lstUsers.size() > 0)
            SUserid = lstUsers[0].Id;
        String query ='select id,CaseId,oldvalue,newvalue from casehistory where field=:sStatus and case.lastmodifiedbyId=:SUserid and createdbyid=:SUserid and Case.Status=:sIStatus';
        return Database.getQueryLocator(query);
        
    }
    
    global void execute(Database.BatchableContext BC, List<CaseHistory> Scope)
    {
        List<CaseHisoryWrapper> lstCH = new List<CaseHisoryWrapper>();
        for(CaseHistory ch : Scope)
            lstCH.add(new CaseHisoryWrapper(Id.valueOf(ch.CaseId), String.valueOf(ch.oldValue), String.valueOf(ch.newValue)));
        executeWrapper(lstCH);
    } 
    
    global void finish(Database.BatchableContext BC)
    {
        
    }
  /**
    * Returns None
    * <p>
    * Wrapper class to pass the case history
    *     * 
    * @param  List<String>  lstAllergies CaseHisoryWrapper
    * @param  List<String>
    * @return  None
    */  
    public void executeWrapper(List<CaseHisoryWrapper> casehistoryobjs){
        List<Case> lstUpdateNew = new List<Case>();
        
        String errorMsg;
        
        try{
            for(CaseHisoryWrapper cCaseHis : casehistoryobjs) 
            {
                if(cCaseHis.oldvalue == 'Closed' && cCaseHis.newvalue == 'In Progress')
                {
                    lstUpdateNew.add(new case(Id=cCaseHis.CaseId, status='Closed'));
                }
                    
            }
            
            if(lstUpdateNew.size() > 0 )
            {
                Database.SaveResult[] results = Database.update(lstUpdateNew, false);
                processSaveResults(results, casehistoryobjs, 'WarmCaseTransfer_B_HUM', 'execute', 'CaseHistory');
            }
        }catch (Exception e) {              
            HUMExceptionHelper.logErrors(e,'WarmCaseTransfer_B_HUM','execute');
        } 
    }
  /**
    * Returns None
    * <p>
    * Wrapper class to pass the case history
    *     * 
    * 
    * @return  None
    */    
    public class CaseHisoryWrapper{
        public Id caseId { get; set;}
        public String oldValue { get; set;}
        public String newValue { get; set;}
        public CaseHisoryWrapper(Id cId, String oValue, String nValue){
            caseId = cId;
            oldValue = oValue;
            newValue = nValue;
        }
    }
    
   /**
    * Returns None
    * <p>
    * class to handle exception's from the batch job and to store in error logs.
    *     * 
    * @param  List<String>  listSaveResults 
    * @param  List<String> CaseHisoryWrapper
    * @Param  string sClassName
    * @Param  string sMethodName
    * @Param  string ObjectType
    * @return  None
    */   
    public void processSaveResults(List < Database.SaveResult > listSaveResults, List < CaseHisoryWrapper > listObjects, String sClassName, String sMethodName, String ObjectType) 
    {
        string ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
        
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c > ();
        if (listSaveResults != null) 
        {
            Database.Error error;
            for (Integer count = 0; count < listSaveResults.size(); count++) 
            {
                if (!listSaveResults[count].isSuccess()) 
                {
                    error = listSaveResults[count].getErrors()[0];
                    listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(error.getStatusCode()), Error_Message__c = error.getMessage(), class_name__c = sClassName, Method_Name__c = sMethodName, Object_Type__c = ObjectType, Record_ID__c = listObjects[count].caseId, Type__c = ERROR_APPLICATION));
                }
            }
            
            if(listErrorLogs.size() > 0)
            {
                insert listErrorLogs;
            }
        }
        
    }
}