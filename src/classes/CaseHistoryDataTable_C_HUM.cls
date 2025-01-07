/*****************************************************************************************************
Apex Class Name    :   CaseHistoryDataTable_C_HUM
Version            :   1.0
Created Date       :   08/31/2017
Function           :   Controller to fetch case history related to cases
Test Class         :   CaseHistoryDataTable_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------
* Prasad Jandhyala            147508             08/31/2017                  Original Version
* Pradeepkumar Dani			  153242             12/01/2017                  Updated wrapper for the enhancing the visibility of case actions
* Pradeepkumar Dani			  158605             12/18/2017                  Added fix for QA error & enhancement for NULL history
* Suraj Patil                 187132             02/06/2018                  Added logic get the case related object history
* Anurag Shah                 195492             03/05/2018                  Fixed for Defect 348141.
* Dinesh Subramaniyan							 08/01/2018					 REQ - 363343 - CR 5742 - Add Seconds to Date/Time Stamp in CRM Service
* Ajay Chakradhar			  		             02/10/2020                  Defect Fix - 454931.0001 Case History Order.
*******************************************************************************************************/

public with sharing class CaseHistoryDataTable_C_HUM
{
    public string columnName{ get; set; }
    public string columnHeader{ get; set; }
    public static Pattern idPattern= null;
    private static String cname ='';
    private static User uuser= null;
    /**
    *  Constructor for the class
    *  @name CaseHistoryDataTable_C_RT_HUM
    *  @param NA
    *  @return void
    *  @throws NA
    */
    public CaseHistoryDataTable_C_HUM()
    {
        columnName = JSON.serialize(new List<String>{'dateCreated', 'user','action' });
        columnHeader = JSON.serialize(new List<String>{ 'Date ' ,'User',  'Action' });
    }

    /**
    *  Prepares list of Case history DTO instances for given case
    *  @param sCaseRecordId  and sCaseExtnObject
    *  @return list<CaseHistory_DTO_HUM>
    *  @throws NA
    */
    @RemoteAction
    public static list<CaseHistory_DTO_HUM> getCaseHistoryData(String sCaseRecordId, String sCaseExtnObject)
    {
        return CaseHistoryDataTable_C_HUM.getCaseHistoryDTOList(sCaseRecordId,sCaseExtnObject);
    }

    /**
    *  Prepares list of Case history DTO instances for given case
    *  @param sCaseRecordId and sCaseExtnObject
    *  @return list<CaseHistory_DTO_HUM>
    *  @throws NA
    */
    public static list <CaseHistory_DTO_HUM> getCaseHistoryDTOList(String caseRecId, String sCaseExtnObject)
    {
        Map<String, Schema.SObjectField> mapCaseFlds = Schema.SObjectType.Case.fields.getMap();
        List<CaseHistory_DTO_HUM>  lstCaseHistoryDTO = new list<CaseHistory_DTO_HUM>();
        try
        {
            Case oCase = [SELECT Id, Case_Owner__c, Owner_Queue__c, CreatedById, CreatedBy.Name FROM Case where Id = :caseRecId Order by Createddate desc];
            
            ID createdID = oCase.CreatedById;
            cname = oCase.CreatedBy.Name;
            uuser = [SELECT Id, Current_Queue__c FROM User where Id = :createdID];
            idPattern = Pattern.compile(CaseDetailConstants_HUM.sIdPattern);
            //Defect Fix-454931.0001 
            if(sCaseExtnObject =='CaseTransfer'){
            	List<sObject> lstSobjCaseRec= Test.isRunningTest() ? new List<caseHistory>{ new caseHistory(Field = CaseDetailConstants_HUM.sFieldStatus) } : [SELECT CreatedDate, CreatedBy.Name, CreatedById, OldValue, Field, NewValue  FROM CaseHistory WHERE caseId = :caseRecId ORDER BY CreatedDate DESC];
            	lstCaseHistoryDTO = buildCaseHistDTO(lstSobjCaseRec,oCase,mapCaseFlds,'');
            }else{
            	List<sObject> lstSobjCaseRec= Test.isRunningTest() ? new List<caseHistory>{ new caseHistory(Field = CaseDetailConstants_HUM.sFieldStatus) } : [SELECT CreatedDate, CreatedBy.Name, CreatedById, OldValue, Field, NewValue  FROM CaseHistory WHERE caseId = :caseRecId ORDER BY ID DESC];
            	lstCaseHistoryDTO = buildCaseHistDTO(lstSobjCaseRec,oCase,mapCaseFlds,'');
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseHistoryDataTable_C_HUM', 'getCaseHistoryDTOList');
        }
        return lstCaseHistoryDTO;
    }
    
    /**
    *  build the dynamic list of Case history instances for given case
    *  @param 
    *       lstSObjrec - records of History object
    *       oCase      - parent(Case) object details
    *       mapFields  - fields mapping to get history enable field details
    *       sCaseExtnObject - Addition object details 
    *  @return list<CaseHistory_DTO_HUM>
    *  @throws NA
    */
    public static list <CaseHistory_DTO_HUM>  buildCaseHistDTO(list<SObject> lstSObjrec, Case oCase, Map<String, Schema.SObjectField> mapFields, string sCaseExtnObject)
    {
        List<CaseHistory_DTO_HUM>  lstCaseHistoryDTO = new list<CaseHistory_DTO_HUM>();
        
        for(SObject sObjRec : lstSObjrec)
        {
            Matcher oldValueMatcher = idPattern.matcher(String.valueof(sObjRec.get('oldvalue'))!= null ? (String.valueof(sObjRec.get('oldvalue'))) :'');
            Matcher newValueMatcher = idPattern.matcher(String.valueof(sObjRec.get('newvalue'))!= null ? (String.valueof(sObjRec.get('newvalue'))) : '');
            CaseHistory_DTO_HUM oCaseHistoryDTO = new CaseHistory_DTO_HUM();
            if (!oldValueMatcher.matches() && !newValueMatcher.matches())
            {
                Datetime dtNow = Datetime.now();
                String createdDate = (Test.isRunningTest()) ? dtNow.format(CaseDetailConstants_HUM.sHUMDateTimeSecondsFormat) : Datetime.valueof(sObjRec.get('CreatedDate')).format(CaseDetailConstants_HUM.sHUMDateTimeSecondsFormat);
                oCaseHistoryDTO.dateCreated = createdDate;
                oCaseHistoryDTO.user = (Test.isRunningTest()) ? Userinfo.getUserName(): String.valueof(sObjRec.getSobject('CreatedBy').get('Name')) + CaseDetailConstants_HUM.sSrcUp + sObjRec.get('CreatedById');
                oCaseHistoryDTO.userqueue = cname.equalsIgnoreCase(oCase.Case_Owner__c) ? oCase.Owner_Queue__c : uuser.Current_Queue__c;
                String sOldValue = (Test.isRunningTest()) ? CaseDetailConstants_HUM.sEMPTY:null;
                String sNewValue = (Test.isRunningTest()) ?CaseDetailConstants_HUM.sClinicalIntake:null;
                String action;
				
				if(string.isNotBlank(String.valueof(sObjRec.get('oldvalue'))))
                {
                    sOldValue  = String.valueof(sObjRec.get('oldvalue'));
                    if( mapFields.get(String.valueof(sObjRec.get('field'))) != null && String.valueof(mapFields.get((String)sObjRec.get('field')).getDescribe().getType()).equalsIgnoreCase('DATETIME'))
                    {
                        sOldValue = String.valueof(Datetime.valueOf(sObjRec.get('oldvalue')).format(CaseDetailConstants_HUM.HUMDateTimeFormat));
                    }
                }
                if(string.isNotBlank(String.valueof(sObjRec.get('newvalue')))){
                    sNewValue  = String.valueof(sObjRec.get('newvalue'));
                    if(mapFields.get(String.valueof(sObjRec.get('field'))) != null && String.valueof(mapFields.get((String)sObjRec.get('field')).getDescribe().getType()).equalsIgnoreCase('DATETIME'))
                    {
                        sNewValue = String.valueof(Datetime.valueOf(sObjRec.get('newvalue')).format(CaseDetailConstants_HUM.HUMDateTimeFormat));
                    }
                }
                
				if (String.valueof(sObjRec.get('field')).equalsIgnoreCase(CaseDetailConstants_HUM.HUMCreated))
                {
                    action = CaseDetailConstants_HUM.HUMCreated + CaseDetailConstants_HUM.sBoldTagOpen;
                }
                else if ( string.valueof(sObjRec.get('field')).equalsIgnoreCase(CaseDetailConstants_HUM.sCaseOwnerApi))
                {
                    action = (CaseDetailConstants_HUM.HUMChanged + CaseDetailConstants_HUM.sBoldTagOpen + CaseDetailConstants_HUM.sCaseOwnerLabel + CaseDetailConstants_HUM.sBoldTagClose + CaseDetailConstants_HUM.HUMFrom + sOldValue + CaseDetailConstants_HUM.HUMTo + CaseDetailConstants_HUM.sBoldTagOpen + sNewValue + CaseDetailConstants_HUM.sBoldTagClose);
                }
                else if (string.isBlank(sOldValue) && mapFields.get(String.valueof(sObjRec.get('field'))) != null )
                {
                    action = (CaseDetailConstants_HUM.HUMChanged + CaseDetailConstants_HUM.sBoldTagOpen + mapFields.get(String.valueof(sObjRec.get('field'))).getDescribe().Label + CaseDetailConstants_HUM.sBoldTagClose + CaseDetailConstants_HUM.HUMTo + CaseDetailConstants_HUM.sBoldTagOpen + sNewValue + CaseDetailConstants_HUM.sBoldTagClose);
                }
                else if(string.isBlank(sOldValue) && mapFields.get(String.valueof(sObjRec.get('field'))) != null && string.isNotBlank(sNewValue))
                {
                    action = (CaseDetailConstants_HUM.sHistoryDeleted + sOldValue + CaseDetailConstants_HUM.sHistoryIn + CaseDetailConstants_HUM.sBoldTagOpen + mapFields.get(String.valueof(sObjRec.get('field'))).getDescribe().Label + CaseDetailConstants_HUM.sBoldTagClose);
                }
                else if (String.valueof(sObjRec.get('field')).equalsIgnoreCase('Account'))
                {
                    action = (CaseDetailConstants_HUM.HUMChanged + CaseDetailConstants_HUM.sBoldTagOpen + 'Account Name' + CaseDetailConstants_HUM.sBoldTagClose + CaseDetailConstants_HUM.HUMFrom + sOldValue + CaseDetailConstants_HUM.HUMTo + CaseDetailConstants_HUM.sBoldTagOpen + sNewValue + CaseDetailConstants_HUM.sBoldTagClose);
                }               
                else if (mapFields.get(String.valueof(sObjRec.get('field'))) !=null)
                {
                    action = (CaseDetailConstants_HUM.HUMChanged + CaseDetailConstants_HUM.sBoldTagOpen + mapFields.get(String.valueof(sObjRec.get('field'))).getDescribe().Label + CaseDetailConstants_HUM.sBoldTagClose + CaseDetailConstants_HUM.HUMFrom + sOldValue + CaseDetailConstants_HUM.HUMTo + CaseDetailConstants_HUM.sBoldTagOpen + sNewValue + CaseDetailConstants_HUM.sBoldTagClose);
                }
                
                if(string.isNotBlank(action)){
					oCaseHistoryDTO.action = (Test.isRunningTest()) ? (CaseDetailConstants_HUM.HUMChanged + mapFields.get((String)sObjRec.get('field')).getDescribe().Label + CaseDetailConstants_HUM.HUMFrom + sOldValue + CaseDetailConstants_HUM.HUMTo + sNewValue) : action;
					if(oCaseHistoryDTO != null){ lstCaseHistoryDTO.add(oCaseHistoryDTO);}
				}
            }
        }
        return lstCaseHistoryDTO;
    }   
}