/******************************************************************************************************************************
Apex Class Name  : PopUpComments_C_HUM
Version          : 1.0 
Created Date     : August 1 2017
Function         : Controller to PopUPComments component. It is used to display 
                   Comments on hover on the Case / Task 
Test Class       : PopUpComments_T_HUM 
Modification Log :
 * Developer            Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *Siddharth Mehta      08/01/2017         Original Version
 *Santhi Mandava       03/18/2020         Increased case comment visible character length to 500
 *Sunil Kumar Reddy    05/11/2020         User story - 1083118 Removed rapid force permission set for case comments story
 *********************************************************************************************************************************/

public with sharing class PopUpComments_C_HUM
{
   
    
    
    public String sObjectId {
                get; 
                set {
            sObjectId = value;
            getComments();            
        }}
   
    
    public String sLastModifiedDate { get; set;}
    public String sComments { get; set;}
    public boolean bDateDisplay {get; set; }
    
  
    
    /*
     * Method Name : fixNewLineAndLength
     * Description : Replaces New Lines and reduces length
     * Return type : String
     * Paramater   : String
     */
    private String fixNewLineAndLength(String sComment) {
        if (String.isNotBlank(sComment))  
        {  
                 sComment =  sComment.replaceAll('\r\n', '<br/>').replaceAll('"', '\\"');
                 if(sComment.length()>500)
                 {
                     sComment = sComment.substring(0,500) +'...';
                 }       
        }
        return sComment;
    } 
    
    /*
     * Method Name : getComments
     * Description : Gets the Case Comments of that Case
     * Return type : void
     * Paramater   : NA
     */
    private void  getComments()
    {
        sComments = System.Label.HUMNoCaseComments;
        bDatedisplay = false;
        sLastModifiedDate = '';
        
        try
        {
            if(String.isNotBlank(sObjectId))
            {
                 
                if(String.valueOf(Id.valueOf(sObjectId).getSObjectType()).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OBJECTNAME_TASK_HUM))
                {
                    
                    List<String> lstString = new List<String>();
                    lstString.add(System.Label.HumTaskLastModifiedQueue);
                    lstString.add(System.Label.HUMTaskDescription);
                    
                    List<Task_Field_History__c> lstFieldUpdates =    [SELECT Id,SObjFieldValue__c,Created_At__c FROM Task_Field_History__c  WHERE SObjID__c = :sObjectId AND SObjFieldName__c = :GLOBAL_CONSTANT_HUM.CASEMGMT_DESCRIPTION_HUM ORDER BY Created_At__c  DESC limit 1 ];
                     
                    if(!lstFieldUpdates.isEmpty() && String.isNotBlank(lstFieldUpdates.get(0).SObjFieldValue__c) ) {
                        
                         
                        bDatedisplay = true;
                        sComments = lstFieldUpdates.get(0).SObjFieldValue__c;
                        sComments = fixNewLineAndLength(sComments);
                        sLastModifiedDate = 'Last Task comment created on ' + lstFieldUpdates.get(0).Created_At__c.format('MM/dd/YYYY\' \'hh:mm\' \'a'); 
                    }
                }
                else 
                {   
                    Case oCaseRec = [Select Id, (Select Id, CommentBody,LastModifiedDate From CaseComments order by LastModifiedDate DESC Limit 1 )From Case Where Id =: sObjectId];
                     
                    if(!oCaseRec.CaseComments.isEmpty() && String.isNotBlank(oCaseRec.CaseComments.get(0).CommentBody) ) {
                        
                        
                        bDatedisplay = true;
                        sComments = oCaseRec.CaseComments.get(0).CommentBody;
                        sComments = fixNewLineAndLength(sComments);  
                        sLastModifiedDate = 'Last Case comment created on ' + oCaseRec.CaseComments.get(0).LastModifiedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a');
                    } 
                    
                }                
            }
        } 
        catch(Exception e)
        {
             HUMExceptionHelper.logErrors(e,'PopUpComments_C_HUM','getComments');
        }
    }
  }