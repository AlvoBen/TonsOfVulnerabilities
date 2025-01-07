/*******************************************************************************************************************************
Apex Class Name : HUMMentorToSFDCController 
Version         : 1.6
Created On      : 6/16/2014
Function        : This class contains method to attach articles from Mentor system to Salesforce cases

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pranali Choudhari        17109                       06/16/2014                   Original Version
 * Pranali Choudhari        17109 						06/18/2014            		 Added logic for duplicate article
 * Pranali Choudhari        17109 						06/21/2014            		 Added logic for closed case
 * Pranali Choudhari        17109  						06/24/2014            		 Added utility exception handling logic
 * Pranali Choudhari        17109  						06/25/2014            		 Changed the logic for obtaining caseId from payload
 * Pranali Choudhari		17109						08/05/2014					 Incorporated changes to name of class
 * Rakesh Patni                                         08/12/2019                   REQ-405880 - Launch Compass Functionality
 * Ravikanth               BUG 4095997                  12/09/2022                   Bug-4095997(6786) - Regression Compass is throwing an error-There was an error rendering Canvas application [SFDCMentorCanvasApp]. Canvas can not locate an installed canvas app with the namespace [] and API name [SFDCMentorCanvasApp].

 ****************************************************************************************************************************/

global with sharing class HUMMentorToSFDCController
{ 

	/*
	 * Method Name : HUMMentor2SFDC
	 * Description : Constructor for the class 
	 * Return type : None
	 * Paramater   : standard controller 
	 */
	public HUMMentorToSFDCController(ApexPages.StandardController controller) 
	{	
        bShowCaseLink = false;
        sfilterSet = '';
        String orgId = UserInfo.getOrganizationId();
        Boolean bInsideRapidForce = HUMUtilityHelper.IsRapidForceAPP();
            if(bInsideRapidForce)  {
                sAppName = HUMConstants.HumMentorCanvasAppRF;
            }
            else {
                 //QA
                    if(orgId == '00Dc0000003uIioEAE')
                    {
                      sAppName = HUMConstants.HUMQA_MentorCanvasApp;
                    }
                     else 
                    {
                       sAppName = HUMConstants.HUMMentorCanvasApp;
                    }
                 }
  

        if(ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMCompassCaseId)) iCaseId = ApexPages.currentPage().getParameters().get(HUMConstants.HUMCompassCaseId);                  
        if(ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMCompassCaseName)) sCaseName= ApexPages.currentPage().getParameters().get(HUMConstants.HUMCompassCaseName);                
        if(ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMCompassFunction)) sFunction = ApexPages.currentPage().getParameters().get(HUMConstants.HUMCompassFunction);
        if(ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMCompassSubFunction)) sSubFunction= ApexPages.currentPage().getParameters().get(HUMConstants.HUMCompassSubFunction);
        if(ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMCompassIntent)) sIntent = ApexPages.currentPage().getParameters().get(HUMConstants.HUMCompassIntent);
        if(ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMCompassClassification)) sClassification= ApexPages.currentPage().getParameters().get(HUMConstants.HUMCompassClassification);
        if(ApexPages.currentPage().getParameters().containsKey(HUMConstants.HUMCompassPgSource)) sPgName= ApexPages.currentPage().getParameters().get(HUMConstants.HUMCompassPgSource);     

        if(!bInsideRapidForce && !String.isBlank(iCaseId) && iCaseId.length()<=18)
        {
        	bShowCaseLink = true;
        }      
        if(IsRsoUser() && bInsideRapidForce)
        {
            sfilterSet = HUMConstants.HUMCompassRSOParam;
        }
        sPgTitle = bInsideRapidForce?HUMConstants.HUMCompassButtonRF:'';
	}

    //Variable that holds the page title
    public static string sPgTitle{get;set;}
	//Variable to get Case Id passed from visualforce page URL
	public static string iCaseId{get;set;}

    //Variable to get Case Name passed from visualforce page URL
	public static String sCaseName{get;set;}
    
    //Variable to pass the result string to JS callback function 
    public static String sResultString{get;set;}
    
    //Variable to get function passed from visualforce page URL
    public static String sFunction{get;set;}
    
    //Variable to get sSubFunction passed from visualforce page URL
    public static String sSubFunction{get;set;}
    
    //Variable to get sIntent passed from visualforce page URL
    public static String sIntent{get;set;}
    
    //Variable to get sClassification passed from visualforce page URL
    public static String sClassification{get;set;}
    
    //Variable to get sPgName passed from visualforce page URL
    public static String sPgName{get;set;}   
    
    //Variable to get sPgName passed from visualforce page URL
    public static String sfilterSet{get;set;}

    //Variable to get current Environment Reference.
    public static String sAppName{get;set;}

    //Variable to render Back to Case Link
    public static boolean bShowCaseLink{get;set;}
	/*
	* Method Name : attachtoCase
	* Description : Get the attributes from payload and insert external article into sfdc
	* Return type : String 
	* Paramater   : Requires 1 argument, String payload      
	*/
    
    @RemoteAction
    global static String attachToCase(String sMsg, string oObjectId)
    {
        Compass__c oCompass = new Compass__c();   
        Set<String> setDocumentId = new Set<String>();
        List<Compass__c> lstCompassList = new List<Compass__c>();
        List<Compass_Staging__c> lstQSAttachToCase = new List<Compass_Staging__c>(); 
        Case ocase = new Case();
        try
        {	
            HUMMentorToSFDCHelper compVarc = (HUMMentorToSFDCHelper)JSON.deserialize(sMsg,HUMMentorToSFDCHelper.class);
            if(String.isNotBlank(oObjectId))
            {
                if(!oObjectId.startsWith(HUMConstants.HUMCompassCaseStartWith) && oObjectId.length()>HUMConstants.HUMCompassMaxIDLength)
                {
                    for(Compass_Staging__c ocmp : [Select Document_ID__c  from Compass_Staging__c where name =: oObjectId])
                    {                
                        setDocumentId.add(ocmp.Document_ID__c);
                    }
                    if(!(setDocumentId.contains(compVarc.ObjectId)))
                    {
                        Compass_Staging__c oQSCompass = new Compass_Staging__c();
	                    oQSCompass.Document_ID__c = compVarc.ObjectId;
	                    oQSCompass.Document_Name__c = compVarc.DocumentName;
	                    oQSCompass.Document_URL__c = compVarc.DocumentURL;
	                    oQSCompass.name = oObjectId;
	                    lstQSAttachToCase.add(oQSCompass);
	                    
	                    List<Database.SaveResult> srQSMentorRec = Database.insert(lstQSAttachToCase);
	                    boolean isError = HUMExceptionHelper.processSaveResults(srQSMentorRec ,lstQSAttachToCase ,'HUMMentorToSFDCController','attachToCase','QS_Mentor_Link__c');
	                    
	                    sResultString = (!isError) ? System.Label.HUMCanvasAlertInserted : System.Label.HUMCanvasAlertInsertError;                                           
                    }
                    else
                    {
                        sResultString = System.Label.HUMCanvasAlertDuplicate;
                    }
                }
				else
				{
					if(String.isNotEmpty(oObjectId)) ocase = [Select Id, Status from Case where Id =:oObjectId limit 1];
					
					for(Compass__c ocmp : [Select Id, Document_ID__c, Case__c from Compass__c where Case__c =: oObjectId])
					{                
						setDocumentId.add(ocmp.Document_ID__c);
					}
					
					//Check for no duplicates and case status not equals to closed and cancelled
					if(!(setDocumentId.contains(compVarc.ObjectId)) && ocase.Status!= System.Label.HUMCanvasCaseStatusClosed && 
					   ocase.Status!= System.Label.HUMCanvasStatusCancelled)
					{
						List<Compass__c> lstCompassInsertList = new List<Compass__c>();
						if(compVarc!=null)
						{
							oCompass.Document_ID__c = compVarc.ObjectId;
							oCompass.Document_Name__c = compVarc.DocumentName;
							oCompass.Document_URL__c = compVarc.DocumentURL;
							oCompass.Case__c = oObjectId;
							lstCompassInsertList.add(oCompass);
							
							List<Database.SaveResult> srCompassRec = Database.insert(lstCompassInsertList);
							boolean isError = HUMExceptionHelper.processSaveResults(srCompassRec ,lstCompassInsertList ,'HUMMentorToSFDCController','attachToCase','Compass__c');
							
							sResultString = (!isError) ? System.Label.HUMCanvasAlertInserted : System.Label.HUMCanvasAlertInsertError;                               
						}   
						else
						{
							sResultString = System.Label.HUMCanvasAlertInsertError;
						}
					}
					
					//Check for insertion of article for closed or cancelled case
					else if(ocase.Status == System.Label.HUMCanvasCaseStatusClosed || ocase.Status == System.Label.HUMCanvasStatusCancelled)
					{
						sResultString = System.Label.HUMCanvasAlertClosedCase; 
					}
					
					//Else check for duplicates
					else if(setDocumentId.contains(compVarc.ObjectId)  && ocase.Status!= System.Label.HUMCanvasCaseStatusClosed &&
							ocase.Status!= System.Label.HUMCanvasStatusCancelled )
					{              
						sResultString = System.Label.HUMCanvasAlertDuplicate;          
					}
					
					//Else there is error in insertion
					else
					{
						sResultString = System.Label.HUMCanvasAlertInsertError;                      
					}               
				}
			}
            return sResultString;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMMentorToSFDCController','attachToCase');
            sResultString = System.Label.HUMCanvasAlertInsertError;
            return sResultString;
        }
    }
    /*
	* Method Name : IsRsoUser
	* Description : This method is to find if current user is RSO user
	* Return type : Boolean 
	* Paramater   : N/A    
	*/
    public static Boolean IsRsoUser()
    {
        Id CurrentId = UserInfo.getUserId();
        Set<String> setRsoPermission= new Set<String>();
        Set<String> setNonRsoPermission= new Set<String>();
        Boolean isRso = false , isNRso = false;
        
        List<MentorPermission__mdt> lstMentorPermissions = [Select id,Permission_Set_Name__c,Is_RSO__c from MentorPermission__mdt];
        for(MentorPermission__mdt mPerm : lstMentorPermissions)
        {
            if(mPerm.Is_RSO__c)
            {
                setRsoPermission.add(mPerm.Permission_Set_Name__c);
            }
            else
            {
                setNonRsoPermission.add(mPerm.Permission_Set_Name__c);
            }
        }
        
        for(PermissionSetAssignment psa : [SELECT Id, PermissionSetId, PermissionSet.Name, PermissionSet.ProfileId, PermissionSet.Profile.Name, AssigneeId, Assignee.Name FROM PermissionSetAssignment WHERE AssigneeId =: CurrentId ])
        {
            if(setRsoPermission.contains(psa.PermissionSet.Name) && !isRso)
            {
                isRso = true;
            }
            else if(setNonRsoPermission.contains(psa.PermissionSet.Name) && !isNRso)
            {
                isNRso = true;
            }
            
            if(isRso && isNRso)
            {
            	break;
            }
        }
        
        if(isRso && isNRso)
            return false;
        else if (isNRso)
            return false;
        else if (isRso)
            return true;
        else
            return false;
        
    }   
}