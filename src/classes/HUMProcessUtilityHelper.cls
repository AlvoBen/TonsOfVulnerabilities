/****************************************************************************
Apex Class Name  : HUMProcessUtilityHelper
Version          : 1.0 
Created Date     : JULY 26 2014
Function         : Acts as Utility class for templates functionality.
Test Class       : HUMProcessUtilityHelperTest
Modification 
Log : *  Developer                     Code Review        Date                 Description * 
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
      *  Sagar Tapadia                   17703           07/26/2014         Original Version 
      *  Sagar Tapadia                   17703           07/31/2014         Added Method to handle Person Service Response 
      *  Sagar Tapadia                   17703           08/12/2014         Updated QMS Service Request Parameters 
      *  Sagar Tapadia                   17703           08/14/2014         Added Logic to handle Queues in QMS Service 
      *  Vivek Batham                    17840           09/04/2014         Changes for decrypting values 
      *  Shruthi Karanth                 17840           09/04/2014         Changes for decrypting values 
      *  Vishal Verma                    17987           09/20/2014         Breaking the methods into smaller functions as per Humana standards 
      *  Manish Kumar                    18575           10/15/2014         Modified code for Defect#158177 
      *  SuryaKumari Medicherla          19911           12/12/2014         Code modified to replace custom settings with custom object (REQ-72809) 
      *  Vishal Verma                    19911           12/18/2014         Modified code as per Humana standards
      *  Vishal Verma                    20418           01/20/2015         Removed generateArrayAttachmentsQMSService method as it was not used. 
      *  Pradeep Veeravali               20976           02/13/2015         Modified code with class generated from new Person Service WSDL with extra fields for COB relationshipcode
      *  Pradeep Veeravali               21052           02/19/2015         Modified to Fix Issue#62 
      *  Pradeep Veeravali               21106           02/22/2015         Modified Code to Fix COB relationshipcode in ConstructMap added enterpriseId checking condition
      *  Pradeep Veeravali               21252           02/27/2015         Modified to Fix Defect#174419
      *  Shreya Choodamani               23334           05/21/2015         Added code as per REQ-123923 for Sending attachment for QMS service at line 178 and added method getAttachment
      *  Vishal Verma                    29834           02/09/2016         Defect 210750.0001 : Added fetchInteractionId method to fetch the interactiion to be passed in the QMS Service
      *  Vishal Verma                    30548           03/17/2016         Defect 216758.0001 : Passing the Comment entered from Change Owner Page to QMS
      *  Vishal Verma                    30572           03/21/2016         Defect 216758.0001 : Modified assignCaseValuestoQMS method to pass Comment entered from Change Case Owner .
      *  Shikhar Mehta                                   06/21/2016         REQ - 237000 : EIP fix , removed dpass calls for decryption.
      * ChiranjeeviRao Ravuri            30921           4/07/2016          REQ - 262439:Replace Attachment_URL__c field with Attachment_URL_Text__c field.   
                                                                            Removed field'Attachment_URL_Text__c' can not be filtered in query call and added if condition at line 236,to explicitly check for not null values.  
      * Sharan Shanmugam                 47072           11/07/2016         Added logic in cloneCase method to pick only active queues. 
      * Saurabh Kaple                    162071          01/10/2018         REQ - 333069 - Genkey conversion- updates to person service request
      * Suresh Gottipati                                 06/18/2018         REQ - 366201 - Added Member_Id.Member_Id_Value field in code which is replacement of Name Field                                                                                                                                                 
      * Santhi Mandava                                   04/15/2019         Replaced "Member_Id_Value__c" with Name
	  * Mohammed Noor					  				 07/24/2020			User Story 825176 PR00093155 - MF 18 - COB Template Changes for using MBEPlus Service.
	  * Mohammed Noor					  				 08/14/2020			User Story 825176 PR00093155 - Added the formatting for OILastVerified Date and use Policy Plan Details to match ProductId
	  * Mohammed Noor					  				 09/09/2020			User Story 1474107 PR00093155 - Removed Start Date and End Date criteria from ProducId matching logic.
	  * Mohammed Noor                                    11/13/2020			Changes done for User Story 1654207.
	  * Mohammed Noor									 02/09/2021			USER STORY 1700807 - PR00093155 MF-18 Added new node OICoordinationOfBenefitList
	  * Mohammed Noor									 02/23/2021			USER STORY 1700807 - REMOVE NONE Override for OI End date.
	  * Mohammed Noor									 03/15/2021			USER STORY 1700807 - PR00093155 MF-18 Added On/Off Switch to include the new OICOB node in the request
      * Mithra Bharadwaj                                                    US#2089572 SonarQube Changes
      * Prasuna Pattabhi                                   11/11/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant - Account And Policy Pages
      * Prasuna Pattabhi                                                      11/12/2021                   US#2709768 MF1 - Architectural Remediation - Global Constant CaseManagement_Constants_HUM reference added
      *******************************************************************************************************************************************************************************************************************/
public with sharing class HUMProcessUtilityHelper
{
   
    Public Static final String VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String MEMBERIDDEPENDENTCODE = 'MemberIDDependentCode';
    Public Static final String MEMBERIDBASE = 'MemberIdBase';
    Public Static final String PCP_MBEPLUSSERVICENAME_HUM = 'MBEPlusService';
    Public Static final String VOB_CURRENT_HUM = 'Current';
    Public Static final String VOB_PAST_HUM = 'Past';
    Public Static final String VOB_FUTURE_HUM = 'Future';
    Public Static final String VOB_PCP_REFDATEFORMT_HUM ='yyyy-MM-dd';
    Public Static final String HYPHEN_HUM = '-';
    
    /*     
    * Method Name : generateQMSServiceRequest     
    * Description : call this method to generate Request for QMS Service.     
    * Return type : HUMIntQMSService.QualityReferralCRMRequestDataContract     
    * Paramater   : Id,String     
    */    
    public static HUMIntQMSService.QualityReferralCRMRequestDataContract generateQMSServiceRequest(Id caseId,String grpName , String sCaseComment)    
    {        
       
        Set<Id> setPmId = new Set<Id>();        
               
        String sQueueName = '';        
        HUMIntQMSService.QualityReferralCRMRequestDataContract qReferral = new HUMIntQMSService.QualityReferralCRMRequestDataContract();        
        if(caseId != null)        
        {            
            //Query for Case Record to get values of fields            
            for(Case oCase:[Select id,CaseNumber,CreatedDate,Type,Status,Department__c, OwnerId, Owner_Queue__c,Interaction_ID__c,CreatedBy.FirstName,Policy_Member__c,CreatedBy.LastName,LastModifiedDate,CreatedBy.Current_Queue__c,Interacting_About__c,Interacting_With__r.Name,CreatedBy.Id, Owner.FirstName, LastModifiedBy.Name,(Select Id,CommentBody from CaseComments),(Select Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Attachment_URL_Text__c From Attachements__r) from Case where id =:caseId])            
            {                
                //Adding policy member id to set                
                if(oCase.Policy_Member__c != null) setPmId.add(oCase.Policy_Member__c);                
                if(!String.isEmpty(oCase.CreatedBy.Current_Queue__c))                
                {                    
                    qReferral.CreatedQueueName = String.valueOf(oCase.CreatedBy.Current_Queue__c);                    
                    sQueueName = String.valueOf(oCase.CreatedBy.Current_Queue__c);                  
                }                
                qReferral = assignCaseValuestoQMS(qReferral,oCase , sCaseComment);                
                               
                qReferral.ToQueue = grpName;            
                qReferral.InteractingAbout = oCase.Interacting_About__c ; 
                qReferral.InteractingWith = oCase.Interacting_With__r.Name  ;
            }
            qReferral = assignFromWorkQueueSelector(sQueueName, qReferral);        
      }        
      //Getting values related to policy member        
      if(setPmId.size()>0) qReferral = assignMemberValues(setPmId, qReferral);        
      if(qReferral != null) return qReferral;                
      return null;    
    }
    
    
            
    /*     
    * Method Name : assignFromWorkQueueSelector     
    * Description : call this method to generate Request for Work Queue Selector.     
    * Return type : HUMIntQMSService.QualityReferralCRMRequestDataContract     
    * Paramater   : String, HUMIntQMSService.QualityReferralCRMRequestDataContract     
    */    
    private static HUMIntQMSService.QualityReferralCRMRequestDataContract assignFromWorkQueueSelector(String sQueueName, HUMIntQMSService.QualityReferralCRMRequestDataContract qReferral){        
        //Adding values related to Owner and Created by User Queues.        
        if(!String.isEmpty(sQueueName))        
        {               
            sQueueName = sQueueName.replace(' ','_');            
            List<Work_Queue_Setup__c> lstWorkQueue = [Select Department__c,Service_Center__c from Work_Queue_Setup__c Where Work_Queue_Name__c=:sQueueName AND IsActive__c = TRUE];            
            if(lstWorkQueue.size()>0)            
            {                
                qReferral.CreatedDepartmentName = lstWorkQueue[0].Department__c;                
                qReferral.OwnerDepartmentName = lstWorkQueue[0].Department__c;                
                qReferral.CreatedSiteName = lstWorkQueue[0].Service_Center__c;                
                qReferral.OwnerSiteName  = lstWorkQueue[0].Service_Center__c;            
            }        
        }        
        return qReferral;        
    }        
    
    /*     
    * Method Name : assignCaseValuestoQMS     
    * Description : Assign Case values to QMS Service Instance     
    * Return type : HUMIntQMSService.QualityReferralCRMRequestDataContract     
    * Paramater   : HUMIntQMSService.QualityReferralCRMRequestDataContract , Case     
    */    
    
    private static HUMIntQMSService.QualityReferralCRMRequestDataContract assignCaseValuestoQMS(HUMIntQMSService.QualityReferralCRMRequestDataContract qReferral ,Case oCase , String sCaseComm)    
    {        
                
        list<string> lstCommentBody = new list<string>();        
        if(qReferral != null && oCase != null)        
        {    
            if(oCase.CaseComments.size()>0)            
            {                
                {                
                qReferral.CaseNotes = '';
                for(CaseComment cComment:oCase.CaseComments)                
                {                    
                    //Loading Case Notes with Case Comments                                         
                     qReferral.CaseNotes+= cComment.CommentBody  + ' ~ ';                
                                   
                    }
                }
            }          
            //Adding Request Parameters related to Case for QMS Service            
            qReferral.CaseId = long.valueOf(oCase.CaseNumber);            
            qReferral.CreatedId = oCase.CreatedById;            
            qReferral.CaseCreatedDate = oCase.CreatedDate;            
            qReferral.CaseStatus = oCase.Status;            
            qreferral.CaseType = oCase.Type;            
            qReferral.InteractionID = fetchInteractionId(oCase.id);            
            qReferral.CreatedDate = oCase.CreatedDate;            
            qReferral.ReferralDate = system.now();            
            qReferral.OwnerId = oCase.OwnerId;            
            qReferral.OwnerFirstName = oCase.Owner.FirstName;                           
            qReferral.OwnerQueueName = oCase.Owner_Queue__c;            
            qReferral.CreatedFirstName = oCase.CreatedBy.FirstName;            
            qReferral.CreatedLastName = oCase.CreatedBy.LastName;            
            qReferral.LastModifiedDate = oCase.LastModifiedDate;            
            qReferral.LastModifiedBy = oCase.LastModifiedBy.Name;            
            qReferral.Attachments = getAttachment(oCase.id);         
        }        
        return qReferral;    
    } 
    
    /*     
    * Method Name : fetchInteractionId    
    * Description : call this method to fetch the latest interaction id.     
    * Return type : long
    * Paramater   : Id     
    */ 
    
    private static long fetchInteractionId(Id caseId)
    {
        if(caseId != null)
        {
            // Fetch the latest Interaction for the Case .
            List<Case_Interaction__c> lstCaseInteraction = [Select Id, Interaction__c, Interaction__r.Name from Case_Interaction__c where Case__c = :  caseId order by lastmodifieddate desc limit 1];
            if(!lstCaseInteraction.isEmpty())
            {
                if(lstCaseInteraction[0].Interaction__r.Name != null)
                {
                    if(lstCaseInteraction[0].Interaction__r.Name.isNumeric())
                    {
                        return long.valueof(lstCaseInteraction[0].Interaction__r.Name);                 
                    }
                    
                }
            }
        }
        return 0;
    }
    
    
    /*     
    * Method Name : getAttachment     
    * Description : call this method to add attachment details to QMS request.     
    * Return type : HUMIntQMSService.ArrayOfAttachmentDataContract    
    * Paramater   : Id     
    */ 
    private static HUMIntQMSService.ArrayOfAttachmentDataContract getAttachment(Id caseId)
    {
        HUMIntQMSService.ArrayOfAttachmentDataContract attachmentArray = new HUMIntQMSService.ArrayOfAttachmentDataContract();
        List<HUMIntQMSService.AttachmentDataContract> lstAttachment = new List<HUMIntQMSService.AttachmentDataContract>();
        for(Attachment__c att : [Select Attachment_URL_Text__c , Name , createdDate , lastModifiedDate from Attachment__c where Related_To_Case__c = :caseId])
        {
            if(att.Attachment_URL_Text__c !=null){
            HUMIntQMSService.AttachmentDataContract attachmentObj = new HUMIntQMSService.AttachmentDataContract();
            attachmentObj.AttachmentURL = att.Attachment_URL_Text__c;
            attachmentObj.FileName = att.Name;
            attachmentObj.CreatedDate = att.createdDate;
            attachmentObj.LastModifiedDate = att.lastModifiedDate;
            lstAttachment.add(attachmentObj);
            }
        }
        attachmentArray.AttachmentDataContract = lstAttachment;
        if(lstAttachment.isEmpty()) return null;
        else return attachmentArray;
    }   
    
    
    /*     
    * Method Name : assignMemberValues     
    * Description : call this method to populate Request for      
    * Return type : HUMIntQMSService.QualityReferralCRMRequestDataContract     
    * Paramater   : set<Id>, HUMIntQMSService.QualityReferralCRMRequestDataContract, List<String>, Map<String,String>     
    */    
     
    private static HUMIntQMSService.QualityReferralCRMRequestDataContract assignMemberValues(Set<Id> setPmId, HUMIntQMSService.QualityReferralCRMRequestDataContract qReferral)    
    { 
         
        list<Policy_Member__c> pmList =  new list<Policy_Member__c>(); 
            
            pmList = [Select Id,(Select Id,Name from Member_Ids__r where Type__c =: Label.MemberIdBase),Member__r.FirstName,Member__r.LastName,Member__r.BirthDate__c,Member__r.RecordType.Name, Member__r.Name from Policy_Member__c where Id IN:setPmId Limit 1]; 
               
        if(pmList.size() != NULL && pmList.size() >0)        
        {             
            if(!String.isEmpty(pmList[0].Member__r.RecordType.Name))            
            {                
                if(pmList[0].Member__r.RecordType.Name.containsIgnoreCase(System.label.HUMCOBMemberLabel))                
                {   
                    qReferral.MemberDOB = pmList[0].Member__r.BirthDate__c ; 
                    qReferral.MemberFirstName = pmList[0].Member__r.FirstName ;       
                    qReferral.MemberLastName = pmList[0].Member__r.LastName ; 
                                       
            if(!pmList[0].Member_Ids__r.isEmpty()) {
                qReferral.MemberID = pmList[0].Member_Ids__r[0].Name;  
            }
                          
                }            
            }         
        }                
                      
        return qReferral;    
    }        
    
	/*     
    * Method Name : parseMBERespForCOBDetails     
    * Description : This method is used to fetch the COB details DTO from MBEPlus Response      
    * Return type : list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> - list of COB details wrapper
    * Paramater   : policyMemRec - Policy_Member__c record for the COB details, oMBEResponse - MBEPlus Response, list<Policy_Plan_Details__c>     
    */
    public static list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> parseMBERespForCOBDetails(MBEPlusOIResponse_DTO_HUM oMBEResponse, Policy_Member__c policyMemRec, list<Policy_Plan_Details__c> lstPolicyPlans)
    {        
        list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBDetails = new list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit>();
        map<String, MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> mapCOBDetails = new map<String, MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit>();                         
        try
        {
	        if(oMBEResponse!=null && policyMemRec!=null && oMBEResponse.GetMemberResponse != null && oMBEResponse.GetMemberResponse.Body != null && oMBEResponse.GetMemberResponse.Body.Member != null && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
	        {
	            MBEPlusOIResponse_DTO_HUM.Member oMemberDTO = oMBEResponse.GetMemberResponse.Body.Member;
	            list<MBEPlusOIResponse_DTO_HUM.SoldProduct> lstSoldProducts = new list<MBEPlusOIResponse_DTO_HUM.SoldProduct>(); 
	            if(oMemberDTO!=null && oMemberDTO.SoldProductList!=null && oMemberDTO.SoldProductList.SoldProduct!=null)
	            {
		            MBEPlusOIResponse_DTO_HUM.SoldProductList oSoldProductList = oMemberDTO.SoldProductList;
		            lstSoldProducts = oSoldProductList.SoldProduct;
	            }
	            if(lstSoldProducts!=null && !lstSoldProducts.isEmpty())
	            {
	                for(MBEPlusOIResponse_DTO_HUM.SoldProduct oSoldProduct : lstSoldProducts)
	                {                    
	                    if(oSoldProduct.InsuranceProduct != null && oSoldProduct.InsuranceProduct.SoldProductDetailList != null && oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail !=null	                       
	                       && String.isNotBlank(oSoldProduct.InsuranceProduct.Source) && String.isNotBlank(policyMemRec.Policy_Platform__c) && oSoldProduct.InsuranceProduct.Source.equals(policyMemRec.Policy_Platform__c)
	                       && String.isNotBlank(oSoldProduct.InsuranceProduct.ProductId) && checkProductIDFromCRM(oSoldProduct.InsuranceProduct.ProductId, lstPolicyPlans))
	                    {                                                                        	                       	                    
	                        String sGroupIDCRM = policyMemRec.Policy_Platform__c.equalsIgnoreCase(VOB_PLATFORM_LV_HUM) || policyMemRec.Policy_Platform__c.equalsIgnoreCase(CaseManagement_Constants_HUM.HumHPRecType) ? policyMemRec.Policy__r.Source_Cust_Cov_Key__c.subString(0,6) : policyMemRec.Group_Number__c;  
								                        		                        
	                        list<MBEPlusOIResponse_DTO_HUM.SoldProductDetail> lstSoldProductDetail = oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail;                       
	                        if(lstSoldProductDetail!=null && !lstSoldProductDetail.isEmpty())
	                        {
	                            for(MBEPlusOIResponse_DTO_HUM.SoldProductDetail oSoldProductDetail : lstSoldProductDetail)
	                            {                                                                                       
	                                String sGroupID='';
	                                String sMemberRelationship='';
	                                String sMemberIDBase='';
	                                if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList!=null && 
	                                   oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey !=null && !oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey.isEmpty())
	                                {
	                                	for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey grpPlatformKey : oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey)
	                                	{                                		
	                                		if(String.isNotBlank(grpPlatformKey.KeyType) && grpPlatformKey.KeyType.equalsIgnoreCase(CaseManagement_Constants_HUM.CASESERVICE_ACCOUNTGROUPTYPEID))
	                                		{
	                                			sGroupID = grpPlatformKey.KeyValue;
	                                			break;
	                                		}
	                                	}
	                                }
	                                if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole!=null && !oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole.isEmpty())
	                                {
	                                	for(MBEPlusOIResponse_DTO_HUM.CoveredRole oCoveredRole: oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole)
	                                	{
	                                		if(String.isNotBlank(oCoveredRole.MemberMasterId) && oCoveredRole.MemberMasterId.equals(policyMemRec.Member__r.Enterprise_ID__c))
	                                		{
	                                			if(oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey!=null && !oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey.isEmpty())
	                                			{
	                                				for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey memPlatformKey : oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey)
				                                	{
				                                		if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(MEMBERIDDEPENDENTCODE))
				                                		{
				                                			sMemberRelationship = memPlatformKey.KeyValue;				                   
				                                		}
				                                		if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(MEMBERIDBASE))
				                                		{
				                                			sMemberIDBase = memPlatformKey.KeyValue;
				                                		}
				                                	}
	                                			}
	                                			break; 
	                                		}
	                                	}
	                                }                                
	                                if(oSoldProductDetail.InsuranceCoverage != null && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.StartDate)
	                                   && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.EndDate) && oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList != null
	                                   && oSoldProductDetail.InsuranceCoverage.StartDate.equals(policyMemRec.Effective_Date__c) && oSoldProductDetail.InsuranceCoverage.EndDate.equals(policyMemRec.End_Date__c)
	                                   && String.isNotBlank(sMemberIDBase) && String.isNotBlank(sMemberRelationship) && String.isNotBlank(policyMemRec.Name) && policyMemRec.Name.equalsIgnoreCase(sMemberIDBase+sMemberRelationship) 
	                                   && String.isNotBlank(sGroupID) && String.isNotBlank(sGroupIDCRM) && sGroupID.equalsIgnoreCase(sGroupIDCRM) && oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList.CoordinationOfBenefit!=null
	                                   && !oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList.CoordinationOfBenefit.isEmpty())
	                                {                                    		                                    
	                                    String sMapKey = '';
	                                    for(MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit oCOBDTO : oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList.CoordinationOfBenefit)
	                                    {
	                                    	if(oCOBDTO!=null)
	                                    	{
	                                    		sMapKey = '';
	                                    		if(String.isNotBlank(oCOBDTO.CarrierName))	sMapKey += oCOBDTO.CarrierName;
												if(String.isNotBlank(oCOBDTO.EffectiveDate))	sMapKey += oCOBDTO.EffectiveDate;
												if(String.isNotBlank(oCOBDTO.EndDate))	sMapKey += oCOBDTO.EndDate;
												if(String.isNotBlank(oCOBDTO.Primary))	sMapKey += oCOBDTO.Primary;
												if(String.isNotBlank(oCOBDTO.OILastVerifiedDate))	sMapKey += oCOBDTO.OILastVerifiedDate;
												if(String.isNotBlank(oCOBDTO.LastUpdatedDate))	sMapKey += oCOBDTO.LastUpdatedDate;
												if(String.isNotBlank(sMapKey)) mapCOBDetails.put(sMapKey, oCOBDTO);
	                                    	}
	                                    }		                                                                                                                                                                                                
	                                }                                
	                            }
	                        }		                    
	                    }
	                }
	            }                       
	        }
	        if(mapCOBDetails!=null && !mapCOBDetails.isEmpty())	lstCOBDetails.addAll(mapCOBDetails.values());	        
        }
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'parseMBERespForCOBDetails');	
        }
        return lstCOBDetails;
    }
	
	/*     
    * Method Name : parseMBERespForOICOBDetails     
    * Description : This method is used to fetch the COB details DTO from MBEPlus Response      
    * Return type : list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> - list of COB details wrapper
    * Paramater   : policyMemRec - Policy_Member__c record for the COB details, oMBEResponse - MBEPlus Response, list<Policy_Plan_Details__c>     
    */
    public static list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> parseMBERespForOICOBDetails(MBEPlusOIResponse_DTO_HUM oMBEResponse, Policy_Member__c policyMemRec, list<Policy_Plan_Details__c> lstPolicyPlans)
    {        
        list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstCOBDetails = new list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();
        map<String, MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> mapCOBDetails = new map<String, MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();                         
        try
        {
	        if(oMBEResponse!=null && policyMemRec!=null && oMBEResponse.GetMemberResponse != null && oMBEResponse.GetMemberResponse.Body != null && oMBEResponse.GetMemberResponse.Body.Member != null && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
	        {
	            MBEPlusOIResponse_DTO_HUM.Member oMemberDTO = oMBEResponse.GetMemberResponse.Body.Member;
	            list<MBEPlusOIResponse_DTO_HUM.SoldProduct> lstSoldProducts = new list<MBEPlusOIResponse_DTO_HUM.SoldProduct>(); 
	            if(oMemberDTO!=null && oMemberDTO.SoldProductList!=null && oMemberDTO.SoldProductList.SoldProduct!=null)
	            {
		            MBEPlusOIResponse_DTO_HUM.SoldProductList oSoldProductList = oMemberDTO.SoldProductList;
		            lstSoldProducts = oSoldProductList.SoldProduct;
	            }
	            if(lstSoldProducts!=null && !lstSoldProducts.isEmpty())
	            {
	                for(MBEPlusOIResponse_DTO_HUM.SoldProduct oSoldProduct : lstSoldProducts)
	                {                    
	                    if(oSoldProduct.InsuranceProduct != null && oSoldProduct.InsuranceProduct.SoldProductDetailList != null && oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail !=null	                       
	                       && String.isNotBlank(oSoldProduct.InsuranceProduct.Source) && String.isNotBlank(policyMemRec.Policy_Platform__c) && oSoldProduct.InsuranceProduct.Source.equals(policyMemRec.Policy_Platform__c)
	                       && String.isNotBlank(oSoldProduct.InsuranceProduct.ProductId) && checkProductIDFromCRM(oSoldProduct.InsuranceProduct.ProductId, lstPolicyPlans))
	                    {                                                                        	                       	                    
	                        String sGroupIDCRM = policyMemRec.Policy_Platform__c.equalsIgnoreCase(VOB_PLATFORM_LV_HUM) || policyMemRec.Policy_Platform__c.equalsIgnoreCase(CaseManagement_Constants_HUM.HumHPRecType) ? policyMemRec.Policy__r.Source_Cust_Cov_Key__c.subString(0,6) : policyMemRec.Group_Number__c;  
								                        		                        
	                        list<MBEPlusOIResponse_DTO_HUM.SoldProductDetail> lstSoldProductDetail = oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail;                       
	                        if(lstSoldProductDetail!=null && !lstSoldProductDetail.isEmpty())
	                        {
	                            for(MBEPlusOIResponse_DTO_HUM.SoldProductDetail oSoldProductDetail : lstSoldProductDetail)
	                            {                                                                                       
	                                String sGroupID='';
	                                String sMemberRelationship='';
	                                String sMemberIDBase='';
	                                if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList!=null && 
	                                   oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey !=null && !oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey.isEmpty())
	                                {
	                                	for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey grpPlatformKey : oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey)
	                                	{                                		
	                                		if(String.isNotBlank(grpPlatformKey.KeyType) && grpPlatformKey.KeyType.equalsIgnoreCase(CaseManagement_Constants_HUM.CASESERVICE_ACCOUNTGROUPTYPEID))
	                                		{
	                                			sGroupID = grpPlatformKey.KeyValue;
	                                			break;
	                                		}
	                                	}
	                                }
	                                if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole!=null && !oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole.isEmpty())
	                                {
	                                	for(MBEPlusOIResponse_DTO_HUM.CoveredRole oCoveredRole: oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole)
	                                	{
	                                		if(String.isNotBlank(oCoveredRole.MemberMasterId) && oCoveredRole.MemberMasterId.equals(policyMemRec.Member__r.Enterprise_ID__c))
	                                		{
	                                			if(oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey!=null && !oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey.isEmpty())
	                                			{
	                                				for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey memPlatformKey : oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey)
				                                	{
				                                		if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(MEMBERIDDEPENDENTCODE))
				                                		{
				                                			sMemberRelationship = memPlatformKey.KeyValue;				                   
				                                		}
				                                		if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(MEMBERIDBASE))
				                                		{
				                                			sMemberIDBase = memPlatformKey.KeyValue;
				                                		}
				                                	}
	                                			}
	                                			break; 
	                                		}
	                                	}
	                                }                                
	                                if(oSoldProductDetail.InsuranceCoverage != null && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.StartDate)
	                                   && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.EndDate) && oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList != null
	                                   && oSoldProductDetail.InsuranceCoverage.StartDate.equals(policyMemRec.Effective_Date__c) && oSoldProductDetail.InsuranceCoverage.EndDate.equals(policyMemRec.End_Date__c)
	                                   && String.isNotBlank(sMemberIDBase) && String.isNotBlank(sMemberRelationship) && String.isNotBlank(policyMemRec.Name) && policyMemRec.Name.equalsIgnoreCase(sMemberIDBase+sMemberRelationship) 
	                                   && String.isNotBlank(sGroupID) && String.isNotBlank(sGroupIDCRM) && sGroupID.equalsIgnoreCase(sGroupIDCRM) && oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit!=null
	                                   && !oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit.isEmpty())
	                                {                                    		                                    
	                                    String sMapKey = '';
	                                    for(MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oCOBDTO : oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit)
	                                    {
	                                    	if(oCOBDTO!=null)
	                                    	{
	                                    		sMapKey = '';
	                                    		if(String.isNotBlank(oCOBDTO.CarrierName))	sMapKey += oCOBDTO.CarrierName;
												if(String.isNotBlank(oCOBDTO.EffectiveDate))	sMapKey += oCOBDTO.EffectiveDate;
												if(String.isNotBlank(oCOBDTO.EndDate))	sMapKey += oCOBDTO.EndDate;
												if(String.isNotBlank(oCOBDTO.Primary))	sMapKey += oCOBDTO.Primary;
												if(String.isNotBlank(oCOBDTO.OILastVerifiedDate))	sMapKey += oCOBDTO.OILastVerifiedDate;
												if(String.isNotBlank(oCOBDTO.LastUpdatedDate))	sMapKey += oCOBDTO.LastUpdatedDate;
												if(String.isNotBlank(sMapKey)) mapCOBDetails.put(sMapKey, oCOBDTO);
	                                    	}
	                                    }		                                                                                                                                                                                                
	                                }                                
	                            }
	                        }		                    
	                    }
	                }
	            }                       
	        }
	        if(mapCOBDetails!=null && !mapCOBDetails.isEmpty())	lstCOBDetails.addAll(mapCOBDetails.values());	        
        }
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'parseMBERespForOICOBDetails');	
        }
        return lstCOBDetails;
    }
    
    /*     
    * Method Name : prepareCOBDetailsDTO     
    * Description : This method is used to Create the COB Details DTO for the COB details returned from Service.      
    * Return type : list<HUMPersonServiceResponseHelper> - list of COB details wrapper
    * Paramater   : policyMem - Policy_Member__c record for the COB details, lstCOBDetails - COB details from Service response     
    */    
    public static list<HUMPersonServiceResponseHelper> prepareCOBDetailsDTO(Policy_Member__c policyMem, list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBDetails)
    {
        list<HUMPersonServiceResponseHelper> lstCOBDetailsDTO = new list<HUMPersonServiceResponseHelper>();
        HUMPersonServiceResponseHelper cobDetailsDTO;
    	try
    	{
	        if(policyMem!=null && lstCOBDetails!=null && !lstCOBDetails.isEmpty())
	        {
	            for(MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit cobData : lstCOBDetails)
	            {
	                cobDetailsDTO = new HUMPersonServiceResponseHelper();                                            
	                cobDetailsDTO.Member = policyMem.Member__r.Name;
	                cobDetailsDTO.Relationship = policyMem.Relationship__c;
	                cobDetailsDTO.memberRelationship = policyMem.Name.substring(policyMem.Name.length()-2, policyMem.Name.length());
	                cobDetailsDTO.OILastVerifieddate = formatDateMethod(cobData.OILastVerifiedDate);                
	                cobDetailsDTO.OICarriername = cobData.CarrierName;
	                cobDetailsDTO.OIEffectiveDate = cobData.EffectiveDate;
	                cobDetailsDTO.OIEndDate = cobData.EndDate;
	                cobDetailsDTO.OIPrimary = String.isNOtBlank(cobData.Primary)&&cobData.Primary.equals(CaseManagement_Constants_HUM.MRDOCID) ?  CaseManagement_Constants_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_Y_HUM : CaseManagement_Constants_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_N_HUM;
	                lstCOBDetailsDTO.add(cobDetailsDTO);
	            }
	        }
    	}
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'prepareCOBDetailsDTO');	
        }
        return lstCOBDetailsDTO;                                        
    }
	
	/*     
    * Method Name : prepareOICOBDetailsDTO     
    * Description : This method is used to Create the COB Details DTO for the COB details returned from Service.      
    * Return type : list<HUMPersonServiceResponseHelper> - list of COB details wrapper
    * Paramater   : policyMem - Policy_Member__c record for the COB details, lstCOBDetails - COB details from Service response     
    */    
    public static list<HUMPersonServiceResponseHelper> prepareOICOBDetailsDTO(Policy_Member__c policyMem, list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstCOBDetails)
    {
        list<HUMPersonServiceResponseHelper> lstCOBDetailsDTO = new list<HUMPersonServiceResponseHelper>();
        HUMPersonServiceResponseHelper cobDetailsDTO;
    	try
    	{
	        if(policyMem!=null && lstCOBDetails!=null && !lstCOBDetails.isEmpty())
	        {
	            for(MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit cobData : lstCOBDetails)
	            {
	                cobDetailsDTO = new HUMPersonServiceResponseHelper();                                            
	                cobDetailsDTO.Member = policyMem.Member__r.Name;
	                cobDetailsDTO.Relationship = policyMem.Relationship__c;
	                cobDetailsDTO.memberRelationship = policyMem.Name.substring(policyMem.Name.length()-2, policyMem.Name.length());
	                cobDetailsDTO.OILastVerifieddate = formatDateMethod(cobData.OILastVerifiedDate);                
	                cobDetailsDTO.OICarriername = cobData.CarrierName;
	                cobDetailsDTO.OIEffectiveDate = cobData.EffectiveDate;
	                cobDetailsDTO.OIEndDate = cobData.EndDate;
	                cobDetailsDTO.OIPrimary = String.isNOtBlank(cobData.Primary)&&cobData.Primary.equals(CaseManagement_Constants_HUM.MRDOCID) ? CaseManagement_Constants_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_Y_HUM : CaseManagement_Constants_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_N_HUM;
	                lstCOBDetailsDTO.add(cobDetailsDTO);
	            }
	        }
    	}
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'prepareCOBDetailsDTO');	
        }
        return lstCOBDetailsDTO;                                        
    }
    
    /*     
    * Method Name : prepareEmptyCOBDetails     
    * Description : This method is used to Create the empty COB Details DTO when there is no COB details returned from Service or any error.      
    * Return type : list<HUMPersonServiceResponseHelper> - list of COB details wrapper
    * Paramater   : policyMem - Policy_Member__c record for the COB details     
    */
    public static list<HUMPersonServiceResponseHelper> prepareEmptyCOBDetails(Policy_Member__c policyMem)
    {
        list<HUMPersonServiceResponseHelper> lstEmptyCOBDetails = new list<HUMPersonServiceResponseHelper>();
        try
        {
	        if(policyMem!=null)
	        {
	            HUMPersonServiceResponseHelper cobDetailsDTO = new HUMPersonServiceResponseHelper();                                            
	            cobDetailsDTO.Member = policyMem.Member__r.Name;
	            cobDetailsDTO.Relationship = policyMem.Relationship__c;
	            cobDetailsDTO.memberRelationship = '';
	            cobDetailsDTO.OILastVerifieddate = '';
	            cobDetailsDTO.OICarriername = '';
	            cobDetailsDTO.OIEffectiveDate = '';
	            cobDetailsDTO.OIEndDate = '';
	            cobDetailsDTO.OIPrimary = '';
	            lstEmptyCOBDetails.add(cobDetailsDTO);
	        }
        }
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'prepareEmptyCOBDetails');	
        }
        return lstEmptyCOBDetails;
    }
           
    /*     
    * Method Name : builtHttpRequest     
    * Description : This method is used to Create the MBEPLus Request DTO for COB Template      
    * Return type : HttpRequest     
    * Paramater   : None     
    */
    public static HttpRequest builtHttpRequest()
    {       
        HttpRequest request = new HttpRequest();
        try
        {
            HUM_Webservice_Callout__c MBEServiceDetails = HUM_Webservice_Callout__c.getValues(PCP_MBEPLUSSERVICENAME_HUM);
            if(MBEServiceDetails != null)
            {
                request.setEndPoint(MBEServiceDetails.End_Point_URL__c);
                if(!Test.isRunningTest() && String.isNotBlank(MBEServiceDetails.Certificate_Name__c)) request.setClientCertificateName(MBEServiceDetails.Certificate_Name__c);
                request.setMethod(MBEServiceDetails.Request_Type__c);
                request.setTimeOut(Integer.valueOf(MBEServiceDetails.Timeout__c));
                request.setHeader(Label.HUMServiceTesterController_content_type, MBEServiceDetails.Content_Type__c);
            }                        
            return request;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'builtHttpRequest');
            return Null;
        }
    }
    
    /*     
    * Method Name : createMBEPlusRequest     
    * Description : This method is used to Create the MBEPLus Request DTO for COB Template      
    * Return type : MBEPlusOIRequest_DTO_HUM     
    * Paramater   : sEnterpriseID - input personId for the MBEPlus request, Boolean to add OICOB node in the request     
    */
    public static MBEPlusOIRequest_DTO_HUM createMBEPlusRequest(String sEnterpriseID, Boolean bAddOICOBNode)
    {
        MBEPlusOIRequest_DTO_HUM req;
        try
        {
	        if(String.isNotBlank(sEnterpriseID) && bAddOICOBNode!=null)
	        {
		        req = new MBEPlusOIRequest_DTO_HUM();
		        MBEPlusOIRequest_DTO_HUM.GetMember getMember = new MBEPlusOIRequest_DTO_HUM.GetMember();            
		        MBEPlusOIRequest_DTO_HUM.Member   mem = new MBEPlusOIRequest_DTO_HUM.Member();      
		        MBEPlusOIRequest_DTO_HUM.DualStatusList objDualStatusList = new MBEPlusOIRequest_DTO_HUM.DualStatusList();
                mem.DualStatusList  = objDualStatusList;
		        mem.SoldProductList = createSoldProductList(bAddOICOBNode);
		        
		        mem.GetCriteria = createGetCriteriaList(sEnterpriseID);        
		        
		        getMember.Member = mem;
		        req.GetMember = getMember;
	        }
        }
        catch(Exception e)
        {
        	HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'createMBEPlusRequest');
        }
        return req;
    }
    
    /*     
    * Method Name : createSoldProductList     
    * Description : This method is used to create the input request for fetching the COB Detals      
    * Return type : MBEPlusOIRequest_DTO_HUM.SoldProductList     
    * Paramater   : Boolean flag to add OICOB Node to request
    */
    @testVisible
    private static MBEPlusOIRequest_DTO_HUM.SoldProductList createSoldProductList(Boolean bAddOICOBNode)
    {
        /* Start of Create SOld product list */
        MBEPlusOIRequest_DTO_HUM.SoldProductList objSoldProductList = new MBEPlusOIRequest_DTO_HUM.SoldProductList();
        List<MBEPlusOIRequest_DTO_HUM.SoldProduct> listObjSoldProduct = new List<MBEPlusOIRequest_DTO_HUM.SoldProduct>();
        MBEPlusOIRequest_DTO_HUM.SoldProduct ObjSoldProduct = new MBEPlusOIRequest_DTO_HUM.SoldProduct();
        MBEPlusOIRequest_DTO_HUM.InsuranceProduct objInsuranceProduct = new MBEPlusOIRequest_DTO_HUM.InsuranceProduct();
        MBEPlusOIRequest_DTO_HUM.SoldProductDetailList soldProDetailList = new MBEPlusOIRequest_DTO_HUM.SoldProductDetailList();
        MBEPlusOIRequest_DTO_HUM.SoldProductDetail  soldProductDetail  = new MBEPlusOIRequest_DTO_HUM.SoldProductDetail();
        MBEPlusOIRequest_DTO_HUM.InsuranceCoverage  insuranceCoverage  = new MBEPlusOIRequest_DTO_HUM.InsuranceCoverage();        
        MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList coveredRolPlatPointList = new MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList();
        MBEPlusOIRequest_DTO_HUM.CoveredRoleList coverRoleList= new MBEPlusOIRequest_DTO_HUM.CoveredRoleList(); 
        MBEPlusOIRequest_DTO_HUM.CoveredRole coveredRol = new MBEPlusOIRequest_DTO_HUM.CoveredRole();
        MBEPlusOIRequest_DTO_HUM.CoordinationOfBenefitList COBList = new MBEPlusOIRequest_DTO_HUM.CoordinationOfBenefitList();
        insuranceCoverage.CoordinationOfBenefitList = COBList;
		if(bAddOICOBNode!=null && bAddOICOBNode)
        {
        	MBEPlusOIRequest_DTO_HUM.OICoordinationOfBenefitList OICOBList = new MBEPlusOIRequest_DTO_HUM.OICoordinationOfBenefitList();
        	insuranceCoverage.OICoordinationOfBenefitList = OICOBList;
        }
        MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList grpList = new MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList();
        insuranceCoverage.GroupPlatformPointerList = grpList;

                
        coveredRol.CoveredRolePlatformPointerList= coveredRolPlatPointList;
        coverRoleList.CoveredRole= coveredRol;
        insuranceCoverage.CoveredRoleList =coverRoleList;
        soldProductDetail.InsuranceCoverage = insuranceCoverage;
        soldProDetailList.SoldProductDetail = soldProductDetail;
        objInsuranceProduct.SoldProductDetailList = soldProDetailList;      
        ObjSoldProduct.InsuranceProduct = objInsuranceProduct;
        listObjSoldProduct.add(objSoldProduct);
        objSoldProductList.SoldProduct = listObjSoldProduct;
        /* End of Create SOld product list */
        return objSoldProductList;
    }

    /*     
    * Method Name : createGetCriteriaList     
    * Description : This method is used to create the input request Criteria for MBEPlus Service      
    * Return type : MBEPlusOIRequest_DTO_HUM.GetCriteria     
    * Paramater   : sEnterpriseID - input personId for the MBEPlus request     
    */
    @testVisible
    private static MBEPlusOIRequest_DTO_HUM.GetCriteria createGetCriteriaList(String sEnterpriseID)
    {        
        /* Start of Create getCriteriaList*/
        MBEPlusOIRequest_DTO_HUM.GetCriteria  getCriteriaList = new MBEPlusOIRequest_DTO_HUM.GetCriteria();
        MBEPlusOIRequest_DTO_HUM.PersonIdCriteria  personIdCriteria  = new MBEPlusOIRequest_DTO_HUM.PersonIdCriteria();
        personIdCriteria.PersonId = sEnterpriseID;
        MBEPlusOIRequest_DTO_HUM.PeriodList periodValues = new MBEPlusOIRequest_DTO_HUM.PeriodList();         
        periodValues.Period = new list<String>{ VOB_CURRENT_HUM, VOB_PAST_HUM, VOB_FUTURE_HUM };
        personIdCriteria.PeriodList = periodValues;        
        personIdCriteria.AsOfDate = DateTime.now().format(VOB_PCP_REFDATEFORMT_HUM);
        getCriteriaList.PersonIdCriteria  = personIdCriteria;
        /* End of Create getCriteriaList*/
        return getCriteriaList;
    }
                
        
    /*     
        * Method Name : generatePersonServiceResponse     
        * Description : call this method to generate Response for Person Service and create wrapper for COB templates.     
        * Return type : Map< String, HUMPersonServiceResponseHelper >     
        * Paramater   : List<Policy_Member__c>, String     
        */    
        public static Map< String, HUMPersonServiceResponseHelper > generatePersonServiceResponse(List<Policy_Member__c> lstPolicyMember,String sEnterpriseId , string sMemGenKey)    
        {        
            Map< String, HUMPersonServiceResponseHelper > mapExtIdOI = new Map<String, HUMPersonServiceResponseHelper > {};        
            Map<String,String> mapPolicyType = new Map<String,String>();        
            for (Policy_Member__c sObjPolMem: [Select Id,Name, Member__r.Name, Member__c,Member__r.Enterprise_ID__c,Policy__c,Policy__r.Product_Type__c,Policy__r.Policy_External_ID__c,Policy__r.Product__r.ProductCode,                                            
            Policy_Member_External_ID__c, Relationship__c from Policy_Member__c where Id IN:lstPolicyMember])         
            {            
                HUMPersonServiceResponseHelper respWrap = new HUMPersonServiceResponseHelper();            
                respWrap.Member = sObjPolMem.Member__r.Name;            
                respWrap.Relationship = sObjPolMem.Relationship__c;            
                if(sObjPolMem.Policy_Member_External_ID__c != null)            
                {                
                    mapExtIdOI.put(sObjPolMem.Policy_Member_External_ID__c, respWrap);                               
                    mapPolicyType.put(sObjPolMem.Policy_Member_External_ID__c,sObjPolMem.Policy__r.Product__r.ProductCode);            
                }        
            }
            HUMVerifyFamilyCOBService.soap oHumSoap = new HUMVerifyFamilyCOBService.soap();
            HUM_Webservice_Callout__c verifySvcAuth = HUM_Webservice_Callout__c.getValues(System.Label.HUMAddressVerification);        
            oHumSoap.clientCertName_x = verifySvcAuth.Certificate_Name__c;         
            oHumSoap.endpoint_x =  verifySvcAuth.End_Point_URL__c;       
            oHumSoap.timeout_x = Integer.valueOf(verifySvcAuth.Timeout__c);    
            HUMVerifyFamilyCOBService.ArrayOfMemberDTO res = verifySvcAuth.Active__c != null ? oHumSoap.GetMemberInformation(sEnterpriseId,long.valueOf(sMemGenKey)) : null;
            mapExtIdOI = processPersonServiceResponse(res,mapExtIdOI, mapPolicyType, sEnterpriseId);                  
            return mapExtIdOI;              
        }              
        
        /*     
        * Method Name : processPersonServiceResponse     
        * Description : call this method to process the Response for Person Service .     
        * Return type : Map< String, HUMPersonServiceResponseHelper>     
        * Paramater   : HUMVerifyFamilyCOBService.ArrayOfMemberDTO , Map< String, HUMPersonServiceResponseHelper>, Map<String,String>, String     
        */    
        private static Map< String, HUMPersonServiceResponseHelper> processPersonServiceResponse(HUMVerifyFamilyCOBService.ArrayOfMemberDTO res ,Map< String, HUMPersonServiceResponseHelper> mapExtIdOI, Map<String,String> mapPolicyType, String sEnterpriseId)        
        {        
            try        
            {            
                //Getting Member related data from Response            
            if(res != null && res.MemberDTO.size() > 0 && res.MemberDTO[0].FamilyMembers != null  && res.MemberDTO[0].FamilyMembers.MemberDTO != null)
                {                
                if( res.MemberDTO[0].FamilyMembers.MemberDTO.size() > 0)
                    {       
                    for(HUMVerifyFamilyCOBService.MemberDTO arrymemD : res.MemberDTO[0].FamilyMembers.MemberDTO)
                        {                        
                            if(arrymemD.MemberCoverage != null)                        
                            {   
                                for(HUMVerifyFamilyCOBService.MemberCoverageDTO memCov: arrymemD.MemberCoverage.MemberCoverageDTO)                            
                                {                                                                                                      
                                    mapExtIdOI = constructMap(mapPolicyType ,sEnterpriseId , mapExtIdOI , memCov ,arrymemD);                            
                                }                                   
                            }                       
                        }                         
                    }            
                }  
                     
            }                     
            catch(Exception ex)        
            {            
                //Parameters - Exception , classNAme , Method Name            
                HUMExceptionHelper.logErrors(ex,'HUMProcessUtilityHelper','processPersonServiceResponse');                                          
            }         
            return mapExtIdOI;     
        }       
        
        /*     
        * Method Name : constructMap     
        * Description : Method is called to construct the Response Map .     
        * Return type : Map< String, HUMPersonServiceResponseHelper>     
        * Paramater   : Map<String,String> ,String , Map< String, HUMPersonServiceResponseHelper>,HUMVerifyFamilyCOBService.MemberCoverageDTO,  HUMVerifyFamilyCOBService.MemberDTO      
        */    
        private static  Map< String, HUMPersonServiceResponseHelper> constructMap (Map<String,String> mapPolicyType , String sEnterpriseId , Map< String, HUMPersonServiceResponseHelper> mapExtIdOI , HUMVerifyFamilyCOBService.MemberCoverageDTO memCov , HUMVerifyFamilyCOBService.MemberDTO arrymemD )            
        {                   
            for (String sExternalId: mapExtIdOI.keyset())         
            {                            
                List < String > lstExternalIdSplit = sExternalId.split('\\|'); 
                //ExternalId is loaded through ETL for each policyMember in format : TenantID+'|'+'ODS'+'|'+MemberPersonID+'|'+CUST-GEN-KEY+'|'+CUST-COV-SEQ-NBR+'|'+CoverageEffectiveDate                                           
                if (lstExternalIdSplit.size() > 3)             
                {                                                        
                    //Filter response based on External Id                
                    if(!(String.isEmpty(lstExternalIdSplit[3]) && String.isEmpty(lstExternalIdSplit[4])))                
                    {                                                                                     
                    
                        if (lstExternalIdSplit[3] == String.Valueof(long.valueof(memCov.CustomerGenKey)) && lstExternalIdSplit[4] == String.Valueof(long.Valueof(memCov.CustCovSeqNbr)) && lstExternalIdSplit[2] == String.Valueof(long.valueof(memCov.EnterpriseId)))
                        {                                                                               
                            if(mapPolicyType != null  && mapPolicyType.containsKey(sExternalId) && mapPolicyType.get(sExternalId) != null && mapPolicyType.get(sExternalId).containsIgnoreCase(system.label.HUM_Med))                        
                            {                            
                                mapExtIdOI.get(sExternalId).OIMedicalLastVerifieddate = (arrymemD.MemberProperties != null && arrymemD.MemberProperties.OIMedVerifiedTS != null) ? String.valueOf(arrymemD.MemberProperties.OIMedVerifiedTS) : null;                        
                            }                        
                            else if(mapPolicyType != null && mapPolicyType.containsKey(sExternalId) && mapPolicyType.get(sExternalId) != null && mapPolicyType.get(sExternalId).containsIgnoreCase(system.label.HUM_Den))                        
                            {                            
                                mapExtIdOI.get(sExternalId).OIDentalLastVerifieddate = (arrymemD.MemberProperties != null && arrymemD.MemberProperties.OIDenVerifiedTS != null) ? String.valueOf(arrymemD.MemberProperties.OIDenVerifiedTS) : null;                        
                            }              
                            if(memCov.COB != null && memCov.COB.COBDTO != null  && mapExtIdOI.containsKey(sExternalId))                        
                            {                                                           
                            mapExtIdOI.get(sExternalId).OICarriername = memCov.COB.COBDTO[Integer.valueof(system.label.HUMConstantZero)].CarrierName;                            
                            mapExtIdOI.get(sExternalId).OIEffectiveDate = memCov.COB.COBDTO[Integer.valueof(system.label.HUMConstantZero)].EffectiveDate;                            
                            mapExtIdOI.get(sExternalId).OIEndDate = memCov.COB.COBDTO[Integer.valueof(system.label.HUMConstantZero)].EndDate;                            
                            mapExtIdOI.get(sExternalId).OIPrimary = memCov.COB.COBDTO[Integer.valueof(system.label.HUMConstantZero)].Primary;                                                            
                            }
                            // add MemberRelationship column to MapExtIdOI  
                            if(memCov.SrcPlatformCode == system.label.HUMCOBPlatformCodeEM)
                            {                                
                              mapExtIdOI.get(sExternalId).memberRelationship = memCov.MemberIDDependentCode;                              
                            }                            
                            else if(memCov.SrcPlatformCode == system.label.HUMCOBPlatformCodeLV)
                            {                                
                            if(memCov.SourceHeaderKeys.MemberCoverageSourceDTO != null && memCov.SourceHeaderKeys.MemberCoverageSourceDTO.size()>0)
                                {
                                    for(HUMVerifyFamilyCOBService.MemberCoverageSourceDTO memCovSource : memCov.SourceHeaderKeys.MemberCoverageSourceDTO)
                                    {
                                        if(memCovSource.Source == system.label.HUMCOBPlatformLVSourceMC && memCovSource.SourceCode == system.label.HUMCOBPlatformLVSourceMCSourceCode)
                                        {                                             
                                             mapExtIdOI.get(sExternalId).memberRelationship= getMemberRelationshipCodeFromKey(memCovSource.Key, arrymemD.FirstName);                                            
                                        }
                                    }
                                }        
                            }                                         
                        }                
                    }               
                }                                            
            }  
               
            return mapExtIdOI;
                
        }            
        
	
	/*     
	* Method Name : getMemberRelationshipCodeFromKey
	* Description : Method is called to retrieve RelationshipCode form Key.     
	* Return type : String     
	* Paramater   : String, String       
	*/  
	private static String getMemberRelationshipCodeFromKey(String Key, String firstName)
	{
		Key = Key.trim();
		Integer relCodeIndex = key.indexof(firstName.trim())+firstName.length();
		String relCode = key.substring(relCodeIndex).trim();
		// length of RelationshipCode is always 2            
		relCode = relCode.substring(Integer.valueof(system.label.HUMConstantZero),2);                        
		return relCode;
	}
	
	/*     
	* Method Name : formatDateMethod
	* Description : Method is used to format the Date from 'YYYY-MM-DD' to 'MM/DD/YYYY'     
	* Return type : String     
	* Paramater   : String
	*/
	@testVisible
	private static String formatDateMethod(String sInputDate)
	{
		String sOutputDate = sInputDate;  
		if(String.isNotBlank(sInputDate) && sInputDate.contains(CaseManagement_Constants_HUM.HYPHEN_HUM))
		{
			list<String> lstDateStr = sInputDate.split(CaseManagement_Constants_HUM.HYPHEN_HUM);
            if(lstDateStr!=null && lstDateStr.size() == 3) sOutputDate = lstDateStr[1]+'/'+lstDateStr[2]+'/'+lstDateStr[0];
		}		
		return sOutputDate;
	}
	
	/*     
	* Method Name : checkProductIDFromCRM
	* Description : Method is used to match ProducId from MBE+ reponse with the ProductId in CRM.     
	* Return type : Boolean     
	* Paramater   : String, list<Policy_Plan_Details__c>  
	*/
	@testVisible
	private static Boolean checkProductIDFromCRM(String sProductId, list<Policy_Plan_Details__c> lstPolicyPlans)
	{
		Boolean bProductIDMatched = false;
		
		if(String.isNotBlank(sProductId) && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
		{
			for(Policy_Plan_Details__c oPolicyPlan : lstPolicyPlans)
			{				
				if(oPolicyPlan.Product__c!=null && String.isNotBlank(oPolicyPlan.Product__r.Sold_Product_Key_Value__c) && sProductId.equals(oPolicyPlan.Product__r.Sold_Product_Key_Value__c))
			   	{
			   		bProductIDMatched = true;
			   		break;
				}			
			}			
		}
		return bProductIDMatched;
	}	
		
}