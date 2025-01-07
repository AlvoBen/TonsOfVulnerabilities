/*********************************************************************************************************************************
Apex class Name    : PrimaryCare_CE_HUM.cls
Version            : 1.0
Function           : Controller for "PrimaryCare_VF_HUM"  page and PrimaryCareHistory_CMP_HUM component. MBE Service will be invoked to get PCP data and response will be returned to PrimaryCare_VF_HUM page   
Created On       : 16th March 2016
Test Class         : 

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Avinash Kumar Choubey                              03/16/2016                   Original Version as per REQ - 244445
* Avinash Kumar Choubey      30369                   03/29/2016                   fixed Review Comments
* Rajesh Keswani             27366                   05/05/2016                   Added EPS Service call
* Avinash Kumar Choubey      30980	                 06/23/2016                   Defect fix 230271
* Lakshmi Madduri									 10/08/2018					  Added product and platform for Req-355805
* Subhamay Mandal                                    01/24/2019                   REQ - 382711 AHT - Add Left Side Panel to PCP Tab (RF)
* Subhamay Mandal							         03/04/2019				      REQ - 386039 update in Acceptance Criteria
* Pallavi Shewale                                    04/30/2021                   User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member
* Apurva Urkude										 08/17/2021					  User Story 1801226 T1PRJ0002472 / 94010 - MVP MF 6 - PCP/PCD page - Strides
* Nirmal Garg										09/16/2021					 Added browser logic for coveo
* 	 Nirmal Garg									  11/11/2021			Added CRMS switch for coveo
****************************************************************************************************************************/

public with sharing class PrimaryCare_CE_HUM extends SObjectDescriptorImpl_H_HUM
{
	//public String sPolMemId='';
    Public Static final String VOB_PCP_REFDATEFORMT_HUM ='yyyy-MM-dd'; 
	public boolean bCoveoUser {get;set;}
	Public Static final String ChromeBrowser = 'Chrome/';
	Public Static final String EdgeBrowser = 'Edg';	
	
    /**
    * Returns void
    * <p>
    * This is parameterized constructor as this class is used as extension in PrimaryCare_VF_HUM Page
    *
    * @pram  ApexPages.StandardController controller
    * @return  void
    * @see
    */    
    public PrimaryCare_CE_HUM(ApexPages.StandardController controller) 
    {
		sPolMemId = ApexPages.currentpage().getParameters().get('Id'); 
        sRefDate = ApexPages.currentpage().getParameters().get('refDate');
        if (sPolMemId!= null )
        {
        	objPolMem = [select id, name,Effective_Date__c, End_Date__c, Policy__r.Product__r.ProductCode, Policy__r.Major_LOB_Frm__c, member__r.Enterprise_ID__C, Product__c, Policy_Platform__c from Policy_member__C where id =:sPolMemId limit 1];
          	sPersonID =objPolMem.member__r.Enterprise_ID__C ;
            String sTempProduct = objPolMem.Product__c;
        }

        if(sRefDate != null && sRefDate.length() == 8 )
        {  
        	sRefDate = sRefDate.substring(4, 8)+'-'+sRefDate.substring(0, 2)+'-'+sRefDate.substring(2, 4); 
        	bFromClaim= true;
        }
        else
        {	
            sRefDate= Datetime.now().format(VOB_PCP_REFDATEFORMT_HUM);
        }
        ApexPages.Message warMsg = new ApexPages.Message(ApexPages.Severity.INFO,Label.VOB_PCP_WARNINGMSG_HUM);
        ApexPages.addMessage(warMsg);
		string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');
		if(string.isNotBlank(sBrowserContext)){
			if(sBrowserContext.contains(ChromeBrowser) || sBrowserContext.contains(EdgeBrowser)){
				 bCoveoUser = HUMUtilityHelper.isCRMFunctionalityON('CRMSUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible();
			}
		}
    } 
     
     /**
    * Returns void
    * <p>
    * This is zero argument constructor
    * @return  void
    * @see
    */  
   public PrimaryCare_CE_HUM(){ }
    
   public String sPersonID {get;set;}
   public String sRefDate {get;set;}	
   public Boolean bFromClaim {get;set;} 
   public Policy_member__c objPolMem {get;set;} 
   public String sPolMemId {get;set;}
   public PrimaryCare_CE_HUM getThis()
   {
        return this;
   }
    
    /**
    * Returns Object
    * <p>
    * This method is used to invoke the service
    *
    * @pram  String : PersonId
    * @param  String : Referral Date
    * @return  Object
    * @see
    */    
    @RemoteAction
    public static Object getPcpPcdData(String personID, string refDate,string policyObject) 
    {
	    return  PrimaryCare_D_HUM.getPcpPcdData(personID,refDate,policyObject);     
    } 
    
     /**
    * Returns an object
    * <p>
    * This method is Process the response
    *
    * @pram  List<String> : Request Headers
    * @param  Object : State
    * @return  object
    * @see
    */
    public Static Object ProcessMbeResponse(List<String> Labels ,Object State)
    {
	    return PrimaryCare_D_HUM.ProcessMbeResponse(Labels, State);
    }
    /**
    * Returns an object
    * <p>
    * This method is Process the response of EPS Service
    *
    * @pram  List<String> : Request Headers
    * @param  Object : State
    * @return  object
    * @see
    */
    public Static Object pcpActiveHistoryFinalResponse(List<String> Labels ,Object State)
    {
	    return PrimaryCare_D_HUM.pcpActiveHistoryFinalResponse(Labels, State);
    }
    /**
     * Returns List<sObject> of record
     * <p>
     * Virtual required Method
     *
     * @return NULL
     */
    public List < sObject > getRecords()
    {
        return NULL;
    }

    /**
     * Returns sObject of record
     * <p>
     * Virtual required Method
     *
     * @return NULL
     */
    public sObject getRecord()
    {
        return NULL;
    }
}