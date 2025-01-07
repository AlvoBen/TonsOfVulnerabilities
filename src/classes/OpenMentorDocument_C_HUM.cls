/*******************************************************************************************************************************
Apex Class Name : OpenMentorDocument_C_HUM.cls
Version         : 1.0
Created On      : August 1 2017
Function        : Class contains methods for opening the Mentor URL
Test Class      : OpenMentorDocument_T_HUM
Modification Log: 
* Developer Name            Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Siddharth Mehta                                     08/01/2017                 Original Version
* Sai Ayyagari				164945		              01/19/2018		         REQ - 350396 Fix for HUMExceptionHelper, store the list of exceptions captured
* Dinesh Subramaniyan                              	  03/14/2018                 REQ - 357679 - Add links to Procedure Codes on Claims Detail Page (RF)
* Darshan Sharma								      04/05/2018				 REQ - 361047 - Ability to update Mentor Links in QuickStart via Business Configuration
* Darshan Sharma		   					  	      04/27/2018				 REQ - 361223 Ability to update Mentor Links in QuickStart via Business Configuration (CRM)
* Subhamay Mandal									  05/01/2018				 Fix for medium security vulnerabilities from checkmarx
* Asish Behera			                              10/22/2018		       	 REQ - 377320: AHT - Claims Detail - Diagnosis Codes (RF)
* Amar Gadewar										  02/25/2019				 REQ - 386053 Link Revenue Code to Mentor on Claim Detail page (RF).
*****************************************************************************************************************************************/
public with sharing class OpenMentorDocument_C_HUM {
    
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
    /**
    * redirect to mentor document links
    * @return PageReference
    */
    public PageReference redirectMentorDocument() {
        PageReference pageRef = null;
        
     	try {
	    		String strLinkName = (ApexPages.currentpage().getParameters().containsKey('link_name')) ? ApexPages.currentpage().getParameters().get('link_name') : '';
	    		String sMentorCode = (ApexPages.currentpage().getParameters().containsKey('mentor_Code')) ? ApexPages.currentpage().getParameters().get('mentor_Code') : '';
	    		String sFilterCodeSet = (ApexPages.currentpage().getParameters().containsKey('filter_CodeSet')) ? ApexPages.currentpage().getParameters().get('filter_CodeSet') : '';
		    	HUMCertificateURL__c objHssURL = HUMCertificateURL__c.getValues('HSSRedirectURL');
            	Boolean bDownloadDcoument;
            	String strDocURL;
            
		    	
                if(String.isNotBlank(strLinkName))
                {
                    
                    CustomLink__c mentorLink = [Select Download_MentorLink__c,URL__c from CustomLink__c where Id = : strLinkName];
                    bDownloadDcoument = mentorLink.Download_MentorLink__c;
                    strDocURL = mentorLink.URL__c;
                    
                    if(objHssURL != null && null != objHssURL.CertificateListURL__c && String.isNotBlank(strDocURL)) {
                        
                        String strURL = objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(strDocURL));
                        if(bDownloadDcoument) {
                            strURL = strDocURL;
                        }
                        pageRef = new PageReference(strURL);
                        pageRef.setRedirect(true);             
                    }
                }
		    		
		    		if(String.isNotBlank(sMentorCode))
		    		{
		    			Boolean isRapidForceApp;
		    			isRapidForceApp = HUMUtilityHelper.IsRapidForceAPP();
		    			HUMCertificateURL__c objMenURL = new HUMCertificateURL__c();
		    			
		    			if( isRapidForceApp)
		    			{
				    		objMenURL = HUMCertificateURL__c.getValues('MentorRedirectURLRapidForce'); 	//Mentor Change
				    	}
				    	else
				    	{
				    		objMenURL = HUMCertificateURL__c.getValues('MentorRedirectURL'); 				    		
				    	}  
				    	
				    	String sPlainurl =  objMenURL.CertificateListURL__c+HUMConstants.sProcFilterType+sMentorCode;
				    	
				    	if (String.isNotBlank(sFilterCodeSet)) {
				    		sPlainurl += '&filters={"CodeSet":"'+ sFilterCodeSet +'"}';
				    	}
				    	//Mentor Change
				    	if(HUMMentorToSFDCController.IsRsoUser() && isRapidForceApp)
				    	{
				    		sPlainurl += '&filterset=' + HUMConstants.HUMCompassRSOParam;
				    	}
				    	
				    	String strURL = objHssURL.CertificateListURL__c + EncodingUtil.base64Encode(blob.valueof(sPlainurl.escapeHtml4()));
			        	pageRef = new PageReference(strURL);
			        	pageRef.setRedirect(true);  
	    			}
            
        } catch(Exception e) {
             HUMExceptionHelper.bRestrictLog=true;
            listErrorLogs =HUMExceptionHelper.logErrors(e, 'OpenMentorDocument_C_HUM', 'redirectMentorDocument');
        }
        return pageRef;
    }
}