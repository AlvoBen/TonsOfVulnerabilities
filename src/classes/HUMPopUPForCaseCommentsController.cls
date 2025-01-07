/******************************************************************************************************************************
Apex Class Name  : HUMPopUPForCaseCommentsController
Version          : 1.0 
Created Date     : Feb 12 2015
Function         : Controller to HUMPopUPForCaseComments VF component. It is used to display case
                   Comments on hover on the Case which is there in the Case History section on the
                   Policy Member Detail and Person Account pages 
Test Class       : HUMPopUPForCaseCommentsControllerTest
Modification Log :
 * Developer               Code Review         Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 * Manish Kumar            21062               02/13/2015         Original Version
 * Asha Deepika Koyyana    21062               03/04/2015         Implemented Code review Comments for CC(#21062)
 * Manish Kumar            21737               03/23/2015         Code fix for defect#176720, 176995 & 176790.
                                                                  Should not display more than 200 chars. Display Time in correct format. Display correct WorkQueue
                                                                  Code added/modified line#45, 67-73 & 95
 * Pooja Wadhwa                                                   Code fix for hover, as comments is encrypted removed code for substring and put it in VF 
 * Pooja Wadhwa                                                   Defect fix for hover text in new line   
 * Prudhvi Pamarthi        165023              01/19/2018         REQ - 328929 Fix for HUMExceptionHelper: DML                                                            
 * Sagar MN                                    01/29/2020         CheckMarx Dereference field Error fix
 * Santhi Mandava                              03/18/2020         Increased case comment visible character length to 500                       
 * Sunil Kumar Reddy                           05/11/2020         User story - 1083118 Removed rapid force permission set for case comments story                      
 *********************************************************************************************************************************/

public with sharing class HUMPopUPForCaseCommentsController
{
    @testVisible private Case oCaseRec = new Case();
    private integer iConstantOne =  Integer.valueOf(HUMConstants__c.getInstance('HUMConstantOne').IntegerValue__c) ;
    
    //Capture Any DML Exceptions in errorLog object
    public List<Error_Log__c> listErrorLogs 
    {
        get 
        {
            if(listErrorLogs==null) {return new List<Error_Log__c>();}
            return listErrorLogs;
        }
        set;
    }
    /*
     * Method Name : HUMPopUPForCaseCommentsController
     * Description : Constructor for this class used to initialize variables
     * Return type : NA
     * Paramater   : NA
     */
    public HUMPopUPForCaseCommentsController()
    {
        sCaseCommentString = HUMConstants__c.getInstance('HUMLastCaseComment').StringValue__c;
        sCurrent_Work_Queue = NULL;
    }
    
    public String sCaseId {get; set;}
    public String sCaseCommentString { get; set;}
    public String sCurrent_Work_Queue { get; set;}
    public String sCaseComment
    { 
        get
        {
            getComments();
            if(oCaseRec != NULL)    
                sCaseComment = oCaseRec.CaseComments.isEmpty() ? System.Label.HUMNoCaseComments : oCaseRec.CaseComments.get(oCaseRec.CaseComments.size()-iConstantOne).CommentBody;
            sCaseComment = FixNewLine(sCaseComment);                       
            return sCaseComment;
        } set; 
    } 
   
    public String FixNewLine(String sComment) {
        
        if (sComment!= null) 
        {
            sComment = sComment.replaceAll('\r\n', '<br/>').replaceAll('"', '\\"');
                if(sComment.length()>500)
                 {
                     sComment = sComment.substring(0,500) +'...';
                 }
        }
        return sComment;
    }    
    public boolean bDateDisplay 
    {
        get
        {
            if(!sCaseComment.equals(System.Label.HUMNoCaseComments ))
            {
                bDatedisplay = true;
            }
            else
            {
                bDatedisplay = false;   
            } 
            return bDatedisplay;
        }  set;
    }
    
     public String sLastModifiedDate
    {         
        get
        {
            String lastModDateRetValue='';
            if(!sCaseComment.equals(System.Label.HUMNoCaseComments) && checkCasecommentFieldPermission('parentid')){
                 If(Schema.sObjectType.Case.fields.LastModifiedDate.isAccessible())
                 {
                            lastModDateRetValue = oCaseRec.CaseComments.get(oCaseRec.CaseComments.size()-iConstantOne ).LastModifiedDate.format('MM/dd/YYYY\' \'hh:mm\' \'a');                     
                 }
            }else{
                lastModDateRetValue='';
            }
            Return lastModDateRetValue;
       } set; 
    }
    
    //check casecomment field permission
    public static boolean checkCasecommentFieldPermission( string strCCFld){
        list<string> newCasecommentCreateFields = new list<string>();
        newCasecommentCreateFields.add(strCCFld);
        map<string,Schema.sObjectField> mapSobject = Schema.sObjectType.CaseComment.fields.getMap();
        boolean isAccess = false;
        for(string str : newCasecommentCreateFields){
            if(mapSobject.get(str).getDescribe().isAccessible())
            {
                isAccess = true;
            }
        }
        return isAccess;
    }
    
    
    /*
     * Method Name : getComments
     * Description : Gets the Case Comments of that Case
     * Return type : void
     * Paramater   : NA
     */
    public void getComments()
    {
        try
        {
            if(sCaseId != NULL)
            {
                oCaseRec = [Select Id, Owner_Queue__c, (Select Id, CommentBody,LastModifiedDate From CaseComments order by LastModifiedDate DESC Limit :iConstantOne )From Case Where Id =: sCaseId];
                sCurrent_Work_Queue = [Select count() From CaseHistory Where CaseId =: sCaseId AND Field =: HUMConstants__c.getInstance('StringOwner').StringValue__c] > Integer.valueOf(HUMConstants__c.getInstance('ZERO').StringValue__c) ? oCaseRec.Owner_Queue__c : NULL;                
            }
        } 
        Catch(Exception e)
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs = HUMExceptionHelper.logErrors(e,'HumPopUPForCaseCommentsController','getComments');
        }
    }
  }