import { LightningElement, api,track,wire } from "lwc";
import Icons from "@salesforce/resourceUrl/CommunityIcons_SR_HUM";
import go365Icons from "@salesforce/resourceUrl/CommunityGo365_Icons_SR_HUM";
import humanaSupportTemplate from './communityFooter_LWCCommunity_HUM.html';
import go365Template from './communityFooterGo365_LWCCommunity_HUM.html';
import getTopicDetails from '@salesforce/apex/CommunityHomePageController_C_HUM.getTopicDetails';
import TopicToolsAndResources from '@salesforce/label/c.Community_Topic_Tools_And_Resources';
import Go365Footer_FbURL from '@salesforce/label/c.Community_Go365Footer_FbURL';
import Go365Footer_TwitterURL from '@salesforce/label/c.Community_Go365Footer_TwitterURL';
import Go365Footer_AppleStoreURL from '@salesforce/label/c.Community_Go365Footer_AppleStoreURL';
import Go365Footer_GoogleplayURL from '@salesforce/label/c.Community_Go365Footer_GoogleplayURL';
import Go365Footer_TNCURL from '@salesforce/label/c.Community_Go365Footer_TNCURL';
import Go365Footer_PrivacyURL from '@salesforce/label/c.Community_Go365Footer_PrivacyURL';
import Go365Footer_IntrntPrivacyURL from '@salesforce/label/c.Community_Go365Footer_IntrntPrivacyURL';
import Go365Footer_RightsRspnsibilityURL from '@salesforce/label/c.Community_Go365Footer_RightsRspnsibilityURL';
import Go365Footer_accessibilityURL from '@salesforce/label/c.Community_Go365Footer_accessibilityURL';
import Go365Footer_LegalURL from '@salesforce/label/c.Community_Go365Footer_LegalURL';
import Go365Footer_AboutURL from '@salesforce/label/c.Community_Go365Footer_AboutURL';
import Go365Footer_Satement from '@salesforce/label/c.Community_Go365Footer_Satement';
import TNCTopic from '@salesforce/label/c.Community_TNCTopic';
import HFooter_UsingYrInsuranceURL from '@salesforce/label/c.Community_HFooter_UsingYrInsuranceURL';
import HFooter_InternetPrivacyURL from '@salesforce/label/c.Community_HFooter_InternetPrivacyURL';
import HFooter_TakingCareOfCostURL from '@salesforce/label/c.Community_HFooter_TakingCareOfCostURL';
import HFooter_HealthWellBeingURL from '@salesforce/label/c.Community_HFooter_HealthWellBeingURL';
import HFooter_MemberResrceURL from '@salesforce/label/c.Community_HFooter_MemberResrceURL';
import HFooter_FindDoctorURL from '@salesforce/label/c.Community_HFooter_FindDoctorURL';
import HFooter_HumanaLoginURL from '@salesforce/label/c.Community_HFooter_HumanaLoginURL';
import HFooter_Go365URL from '@salesforce/label/c.Community_HFooter_Go365URL';
import HFooter_AboutHumanaURL from '@salesforce/label/c.Community_HFooter_AboutHumanaURL';
import HFooter_ContactUsURL from '@salesforce/label/c.Community_HFooter_ContactUsURL';
import HFooter_HumanaURL from '@salesforce/label/c.Community_HFooter_HumanaURL';
import HFooter_PrivacyURL from '@salesforce/label/c.Community_HFooter_PrivacyURL';
import HFooter_CorporateResURL from '@salesforce/label/c.Community_HFooter_CorporateResURL';
import HFooter_HPharmacyURL from '@salesforce/label/c.Community_HFooter_HPharmacyURL';
import HFooter_CaregiverURL from '@salesforce/label/c.Community_HFooter_CaregiverURL';
import HFooter_legalURL from '@salesforce/label/c.Community_HFooter_legalURL';
import HFooter_SiteMapURL from '@salesforce/label/c.Community_HFooter_SiteMapURL';
import HFooter_DisclmerLicensureURL from '@salesforce/label/c.Community_HFooter_DisclmerLicensureURL';
import HFooter_FraudWasteAbuseURL from '@salesforce/label/c.Community_HFooter_FraudWasteAbuseURL';
import HFooter_AccessibilityURL from '@salesforce/label/c.Community_HFooter_AccessibilityURL';
import HFooter_SystemRequirementURL from '@salesforce/label/c.Community_HFooter_SystemRequirementURL';
import HFooterLabel_UsingYrInsurance from '@salesforce/label/c.Community_HFooterLabel_UsingYrInsurance';
import HFooterLabel_TakingCntrlOfCost from '@salesforce/label/c.Community_HFooterLabel_TakingCntrlOfCost';
import HFooterLabel_Tools_Resources from '@salesforce/label/c.Community_HFooterLabel_Tools_Resources';
import HFooterLabel_TNC from '@salesforce/label/c.Community_HFooterLabel_TNC';
import HFooterCookieComplaince from '@salesforce/label/c.Community_HFooterCookieComplaince';
import HFooterLabel_IntrntPrivacyStatemnt from '@salesforce/label/c.Community_HFooterLabel_IntrntPrivacyStatemnt';
import HFooterLabel_HlthWellBeing from '@salesforce/label/c.Community_HFooterLabel_HlthWellBeing';
import HFooterLabel_MemberResrces from '@salesforce/label/c.Community_HFooterLabel_MemberResrces';
import HFooterLabel_FindDoctor from '@salesforce/label/c.Community_HFooterLabel_FindDoctor';
import HFooterLabel_SignIntoMyHumana from '@salesforce/label/c.Community_HFooterLabel_SignIntoMyHumana';
import HFooterLabel_SignInToGo365 from '@salesforce/label/c.Community_HFooterLabel_SignInToGo365';
import HFooterLabel_AboutHumana from '@salesforce/label/c.Community_HFooterLabel_AboutHumana';
import HFooterLabel_ContactUs from '@salesforce/label/c.Community_HFooterLabel_ContactUs';
import HFooterLabel_HumanaDotCom from '@salesforce/label/c.Community_HFooterLabel_HumanaDotCom';
import HFooterLabel_PrivacyPractices from '@salesforce/label/c.Community_HFooterLabel_PrivacyPractices';
import HFooterLabel_CrporateRes from '@salesforce/label/c.Community_HFooterLabel_CrporateRes';
import HFooterLabel_OtherHumanaSites from '@salesforce/label/c.Community_HFooterLabel_OtherHumanaSites';
import HFooterLabel_HumanaPharmacy from '@salesforce/label/c.Community_HFooterLabel_HumanaPharmacy';
import HFooterLabel_Go365 from '@salesforce/label/c.Community_HFooterLabel_Go365';
import HFooterLabel_Caregivers from '@salesforce/label/c.Community_HFooterLabel_Caregivers';
import HFooterLabel_Espa_ol from '@salesforce/label/c.Community_HFooterLabel_Espa_ol';
import HFooterLabel_Legal from '@salesforce/label/c.Community_HFooterLabel_Legal';
import HFooterLabel_PrivacyPolicies from '@salesforce/label/c.Community_HFooterLabel_PrivacyPolicies';
import HFooterLabel_SiteMap from '@salesforce/label/c.Community_HFooterLabel_SiteMap';
import HFooterLabel_DisclaimerLicensure from '@salesforce/label/c.Community_HFooterLabel_DisclaimerLicensure';
import HFooterLabel_FraudWasteAbuse from '@salesforce/label/c.Community_HFooterLabel_FraudWasteAbuse';
import HFooterLabel_Accessibility from '@salesforce/label/c.Community_HFooterLabel_Accessibility';
import HFooterLabel_SysRequirement from '@salesforce/label/c.Community_HFooterLabel_SysRequirement';
import HFooterLabel_Go365Community from '@salesforce/label/c.Community_HFooterLabel_Go365Community';
import HFooterLabel_RightResponsibility from '@salesforce/label/c.Community_HFooterLabel_RightResponsibility';
import HFooterLabel_AboutUs from '@salesforce/label/c.Community_HFooterLabel_AboutUs';


export default class communityFooter_LWCCommunity_HUM extends LightningElement {

  label={
    Go365Footer_FbURL,
    Go365Footer_TwitterURL,
    Go365Footer_AppleStoreURL,
    Go365Footer_GoogleplayURL,
    Go365Footer_TNCURL,
    Go365Footer_PrivacyURL,
    Go365Footer_IntrntPrivacyURL,
    Go365Footer_RightsRspnsibilityURL,
    Go365Footer_accessibilityURL,
    Go365Footer_LegalURL,
    Go365Footer_AboutURL,
    Go365Footer_Satement,
    HFooter_UsingYrInsuranceURL,
    HFooter_TakingCareOfCostURL,
    HFooter_InternetPrivacyURL,
    HFooter_HealthWellBeingURL,
    HFooter_MemberResrceURL,
    HFooter_FindDoctorURL,
    HFooter_HumanaLoginURL,
    HFooter_Go365URL,
    HFooter_AboutHumanaURL,
    HFooter_ContactUsURL,
    HFooter_HumanaURL,
    HFooter_PrivacyURL,
    HFooter_CorporateResURL,
    HFooter_HPharmacyURL,
    HFooter_CaregiverURL,
    HFooter_legalURL,
    HFooter_SiteMapURL,
    HFooter_DisclmerLicensureURL,
    HFooter_FraudWasteAbuseURL,
    HFooter_AccessibilityURL,
    HFooter_SystemRequirementURL,
    HFooterLabel_UsingYrInsurance,
    HFooterLabel_TakingCntrlOfCost,
    HFooterLabel_Tools_Resources,
    HFooterLabel_TNC,
    HFooterLabel_IntrntPrivacyStatemnt,
    HFooterLabel_HlthWellBeing,
    HFooterLabel_MemberResrces,
    HFooterLabel_FindDoctor,
    HFooterLabel_SignIntoMyHumana,
    HFooterLabel_SignInToGo365,
    HFooterLabel_AboutHumana,
    HFooterLabel_ContactUs,
    HFooterLabel_HumanaDotCom,
    HFooterLabel_PrivacyPractices,
    HFooterLabel_CrporateRes,
    HFooterLabel_OtherHumanaSites,
    HFooterLabel_HumanaPharmacy,
    HFooterLabel_Go365,
    HFooterLabel_Caregivers,
    HFooterLabel_Espa_ol,
    HFooterLabel_Legal,
    HFooterLabel_PrivacyPolicies,
    HFooterLabel_SiteMap,
    HFooterLabel_DisclaimerLicensure,
    HFooterLabel_FraudWasteAbuse,
    HFooterLabel_Accessibility,
    HFooterLabel_SysRequirement,
    HFooterLabel_Go365Community,
    HFooterLabel_RightResponsibility,
    HFooterLabel_AboutUs,
    HFooterCookieComplaince
    

  };

  humanaLogo = Icons + "/humanaIcons/fill-1@3x.png";
  go365Logo = go365Icons + "/Go365Icons/go365Icon.svg";
  fbIcon = go365Icons + "/Go365Icons/fb-icon.svg";
  twitterIcon = go365Icons + "/Go365Icons/twitter-icon.svg";
  appleStoreIcon = Icons + "/humanaIcons/apple@3x.png";
  playStoreIcon = Icons + "/humanaIcons/google@3x.png";
  longitudeIcon = Icons + "/humanaIcons/longitude@3x.png";
  newWindowIcon = Icons + "/humanaIcons/new-window@3x.png";
  privacyoptionsIcon = Icons + "/humanaIcons/privacyoptions29x14.png";

  @api Go365;
  @track sfdcBaseURL;
  @track URLString;
  @track TermsConditionsURL;
  @track TopicURL='#';
  @track Go365Community;

    sfdcBaseURL=window.location.href;
    URLString=this.sfdcBaseURL.substring(0, this.sfdcBaseURL.indexOf("/s/"));
    TermsConditionsURL= this.URLString +'/s/' + TNCTopic;
    Go365Community = this.URLString +'/s/';
    

  render() {
    return this.Go365 ? go365Template: humanaSupportTemplate;
  }

  

  backToTop() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
  }

  @wire(getTopicDetails, {
    topicName: TopicToolsAndResources
    })
  wiredTopicDetail({error, data}) {
    try{    
        if(data)
            {    
              if(data.Id!=null){
                this.TopicURL= this.URLString +'/s/topic/'+data.Id+'/'+data.Name;
              }
              else{
                this.TopicURL ='#';
              }
                
            }
      }catch (error) {
        console.error("Error in wire call getTopicDetails:" , error);
      }    
  }

  handleOneTrust() {
    OneTrust.ToggleInfoDisplay(); 
  }

}