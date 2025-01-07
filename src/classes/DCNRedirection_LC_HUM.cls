/*****************************************************************************************
Apex class Name    : DCNRedirection_LC_HUM
Version            : 1.0
Function           : Generates DCN URL   
Created Date       : 09/24/2021 
Test Class         : 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   

	Gowthami Thota										09/24/2021			DCN hyperlink redirection
 *********************************************************************************************************************************/
public with sharing class DCNRedirection_LC_HUM {
    	 public static String dcnURL;
    	 public static String ssoURL;
    	 public static String dcnNumber;

    
   public static map<ID,String> generateRedirectToDCNURL(list<Case> lstCaseDetails){
		map<ID,String> dcn_urlData = new map<ID,string>();
        if(Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL') != null && String.isNotBlank(Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c)){
        ssoURL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c; 
        }
        if(lstCaseDetails!=null && !lstCaseDetails.isEmpty()){
		for(Case objCase : lstCaseDetails){
            if(objCase.dcn_type__c == 'claim' || objCase.dcn_type__c == 'edoc'){
                    dcnURL = objCase.dcn_Url__c;
                    dcn_urlData.put(objCase.Id,dcnURL);
             }
            else{
                    dcnURL = HUMDCNApplicationUrl__c.getOrgDefaults().DCNImageURL__c;
                    string strDCNNumber = (objCase.DCN__c != '' && objCase.DCN__c != null)? objCase.DCN__c : (objCase.DCN__c==null) ? '' : objCase.DCN__c;
                    if(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL') != null && String.isNotBlank(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c) && (strDCNNumber).startsWithIgnoreCase('AC'))
                    { 
                        dcnURL = Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c ;
                        dcnURL = dcnURL+strDCNNumber;
                        dcn_urlData.put(objCase.Id,dcnURL);
                    }
                    else
                    { 
                       dcnURL = EncodingUtil.base64Encode(blob.valueOf(dcnURL+strDCNNumber));
                       dcnURL = ssoURL +'?HIDDENTARGET='+dcnURL;
                       dcn_urlData.put(objCase.Id,dcnURL);
                    }
                }
		}
        }
		return dcn_urlData;

	}
}