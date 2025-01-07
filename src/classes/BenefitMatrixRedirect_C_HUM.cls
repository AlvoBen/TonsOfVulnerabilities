/*******************************************************************************************************************************
Apex Class Name : BenefitMatrixRedirect_C_HUM.cls
Version         : 1.0
Created On      : 10/04/2017
Function        : Class contains methods for redirecting to the Mentor URL and passing parameters to find Benefit Matrix documents.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Dinesh Subramaniyan                                10/04/2017                   Original Version
* Amar Gadewar										 07/03/2018				   	  REQ - 367117 - AHT Clean up
* Keerti Durbha                                      04/05/2019                   REQ - 392177 - Fix Mentor Benifit Matrix Link
*****************************************************************************************************************************************/
public with sharing class BenefitMatrixRedirect_C_HUM 
{
    /*
    * Returns the URL to redirect to Mentor Search page
    * @return PageReference: Page Reference of the Mentor application
    */
    public PageReference benefitMatrixRec()
    {
       PageReference returnPage = null;
        try
        {
            Policy_Member__c objPolMember = getRecord();
            String sBaseURL;  
             boolean bCRMGBORFAccess = false;                                                            
    
     bCRMGBORFAccess =  UtilityHelper_H_AHT_HUM.ischeckCustomPermissionAccess();
        
          if(bCRMGBORFAccess == true)
          {
             sBaseURL  = GLOBAL_CONSTANT_HUM.VOB_TABLEOFCONENTS_URL ;  
            }
          else  {
             sBaseURL  = setBaseURL(); 
             }     
            
            if(objPolMember.Policy_Platform__c == GLOBAL_CONSTANT_HUM.VOB_PLATFORM_MTV_HUM || objPolMember.Policy_Platform__c == GLOBAL_CONSTANT_HUM.VOB_PLATFORM_EM_HUM)
            {
             if(bCRMGBORFAccess  ==false){

                string sMTVPolicy = HUMConstants.PARAMTITLE_MENTORREDIRECT_HUM + HUMConstants.BENEFITMATRIX_MTV_PLATFORM_HUM + HUMConstants.BENEFITMATRIX_FILTER_DOCUMENTS_HUM;
                sBaseURL = sBaseURL + sMTVPolicy;
                sBaseURL = hssBaseEncode(sBaseURL);   
                
                }
                
                returnPage = new PageReference(sBaseURL);
                returnPage.setRedirect(true); 
                
            } else if(objPolMember.Policy_Platform__c == GLOBAL_CONSTANT_HUM.VOB_PLATFORM_CAS_HUM  || objPolMember.Policy_Platform__c == GLOBAL_CONSTANT_HUM.VOB_PLATFORM_LV_HUM  || objPolMember.Policy_Platform__c == GLOBAL_CONSTANT_HUM.VOB_PLATFORM_CI_HUM)
            {
               if(bCRMGBORFAccess  ==false){
                               string sCASPolicy = HUMConstants.PARAMTITLE_MENTORREDIRECT_HUM + HUMConstants.BENEFITMATRIX_CAS_PLATFORM_HUM + HUMConstants.BENEFITMATRIX_FILTER_DOCUMENTS_HUM;
                sBaseURL = sBaseURL + sCASPolicy;
                sBaseURL = hssBaseEncode(sBaseURL);  
                }
                
                returnPage = new PageReference(sBaseURL);
                returnPage.setRedirect(true); 
                
               
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'BenefitMatrixRedirect_C_HUM', 'benefitMatrixRec');
        }
        return returnPage;
    }

    /*
    * Encodes the URL by passing it to EncodingUtility and return the value benefitMatrixRec function
    * @param String sEndpoint: End Point that needs to be encoded
    * @return String: Endcoded endPoint
    */
    private String hssBaseEncode(String sEndpoint)
    {
        if(sEndpoint.contains(HUMConstants.TARGET_MENTORREDIRECT_HUM))
        {
            sEndpoint= sEndpoint.subStringBefore(HUMConstants.TARGET_MENTORREDIRECT_HUM) + HUMConstants.TARGET_MENTORREDIRECT_HUM + EncodingUtil.base64Encode(blob.valueof(sEndpoint.subStringAfter(HUMConstants.TARGET_MENTORREDIRECT_HUM)));
        }
        return sEndpoint;
    }

  
   /*
    * To get the values from Policy Member object
    * @return Policy_Member__c : Policy Member Object assoicated with the Benefits
    */
    private Policy_Member__c getRecord()
    {
    	Policy_Member__c objPolMember;
    	String sPolMemId;
        if(ApexPages.currentpage().getParameters().containsKey('Id'))
        {
            sPolMemId = ApexPages.currentpage().getParameters().get('Id');
        } 
        if(NULL != sPolMemId)
        {
            objPolMember = [Select Id ,Product__c ,Policy_Platform__c From Policy_Member__c Where Id =: sPolMemId];
        } 
        
        return objPolMember;
    }
    
    
   /*
    * Sets the base URL from accessing the value from HUMCertificateURL__c custom settings object
    */
    private string setBaseURL()
    {
        String sURL;
        HUMCertificateURL__c objAppURL = HUMCertificateURL__c.getValues('MentorRedirectURL');
        HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
        if(objAppURL!= null && null!= objAppURL.CertificateListURL__c && objHssURL != null && null!= objHssURL .CertificateListURL__c) 
        {
            sURL = objHssURL.CertificateListURL__c + objAppURL.CertificateListURL__c;
                      
        }
        return sURL; 
    }
    
}