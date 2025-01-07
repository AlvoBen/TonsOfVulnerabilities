import { LightningElement,api} from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import fetchSwitchResults from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchSwitchResults';
import getTaskAccountInfo from '@salesforce/apex/GenericHelper_CRMRetail_H_HUM.getTaskAccountInfo';
import { loadStyle } from 'lightning/platformResourceLoader';
import taskInformationCss from '@salesforce/resourceUrl/crm_retail_task_Information_SR_HUM';
import CRMRetail_Checkin_Error_Message from '@salesforce/label/c.CRMRetail_Checkin_Error_Message';
import CRMRetail_Error_Label from '@salesforce/label/c.CRMRetail_Error_Label';
import CRMRetail_Error_variant from '@salesforce/label/c.CRMRetail_Error_variant';
import CRMRetail_BlockVisitor_Icon_Name from '@salesforce/label/c.CRMRetail_BlockVisitor_Icon_Name';
import CRMRetail_Check_Icon_Name from '@salesforce/label/c.CRMRetail_Check_Icon_Name';
import CRMRetail_Absence_Icon_Name from '@salesforce/label/c.CRMRetail_Absence_Icon_Name';
import CRMRetail_InactiveMember_Label from '@salesforce/label/c.CRMRetail_InactiveMember_Label';
import CRMRetail_Future_Member_Label from '@salesforce/label/c.CRMRetail_Future_Member_Label';
import CRMRetail_Account_Information from '@salesforce/label/c.CRMRetail_Account_Information';
import CRMRetail_HomeEmail_Field from '@salesforce/label/c.CRMRetail_HomeEmail_Field';
import CRMRetail_Name_Key from '@salesforce/label/c.CRMRetail_Name_Key';
import SearchVisitor_BirthdateLabel_HUM from '@salesforce/label/c.SearchVisitor_BirthdateLabel_HUM';
import CRMRetail_Member from '@salesforce/label/c.CRMRetail_Member';
import CRMRetail_HomePhone_Field from '@salesforce/label/c.CRMRetail_HomePhone_Field';
import CRMRetail_Deceased_Label from '@salesforce/label/c.CRMRetail_Deceased_Label';
import CRMRetail_Address_Field from '@salesforce/label/c.CRMRetail_Address_Field';
import CRMRetail_Visitor_Id_Field from '@salesforce/label/c.CRMRetail_Visitor_Id_Field';
import CRMRetail_Gender_Field from '@salesforce/label/c.CRMRetail_Gender_Field';
import CRMRetail_Task_Fetch_Error from '@salesforce/label/c.CRMRetail_Task_Fetch_Error';

export default class Crm_retail_task_account_information_LWC_HUM extends LightningElement {

    taskFetchError = CRMRetail_Task_Fetch_Error;;
    open = true;
    @api recordId;
    name;
    personEmail;
    birthdate;
    personHomePhone;
    gender;
    personMailingCity;
    personMailingCountry;
    personMailingStreet;
    personMailingPostalCode;
    personMailingState;
    personMailingAddress;
    nameURL;
    isMember = true;
    deceasedDate;
    visitorId;
    memberIcon;
    membershipStatus;
    accInfoTitle = CRMRetail_Account_Information;
    accountNameLabel = CRMRetail_Name_Key;
    homeEmailLabel = CRMRetail_HomeEmail_Field;
    birthdateLabel= SearchVisitor_BirthdateLabel_HUM;
    memberLabel = CRMRetail_Member;
    homePhoneLabel = CRMRetail_HomePhone_Field;
    deceasedLabel = CRMRetail_Deceased_Label;
    addressLabel = CRMRetail_Address_Field;
    visitorIdLabel = CRMRetail_Visitor_Id_Field;
    genderLabel = CRMRetail_Gender_Field;
    activeSectionMessage = CRMRetail_Account_Information;
    isSpinnerVisible = true;
    errorCaught = false;
    switchValue=false;
    isdeceasedDatePresent;

    renderedCallback() {      
        if(this.switchValue){
            if(this.isCssLoaded) return            
                loadStyle(this,taskInformationCss+'/crm_retail_task_Information_SR_HUM.css').then(()=>{
                    this.isCssLoaded = true;            
            })
            .catch(error=>{
                this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            }); 
        }      
        
    }  

    connectedCallback(){
        this.processInitial();
    }
    async processInitial()
    {
        try{       
            var switches = await fetchSwitchResults();
            if(switches && switches['Switch_3759560'])
            {
                this.switchValue = true;
                this.populateTaskAccountInfo();           
            }
        }catch(error){
            this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
        }        
    }
    populateTaskAccountInfo()
    {
        getTaskAccountInfo({recordId: this.recordId})
        .then((response) => {
            if(response)
            {
                if(response.isError){
                    this.errorCaught = true;
                    this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
                    this.isSpinnerVisible = false;
                }
                else{
                    var data = JSON.parse(response.sResult);
                    this.name = data.Name ? data.Name : '';
                    this.nameURL = data.Id ? window.location.origin + '/lightning/r/'+data.Id+'/view' :'';
                    
                    this.personEmail = data.PersonEmail ? data.PersonEmail : '';
                    this.personHomePhone = data.PersonHomePhone ? data.PersonHomePhone : '';
                    this.birthdate = data.Birthdate ? data.Birthdate : '';
                    this.gender = data.Gender ? data.Gender : '';
                    this.visitorId = data.visitorId ? data.visitorId : ''; 
                    this.deceasedDate = data.deceasedDate ? CRMRetail_Absence_Icon_Name : '';
                    this.isdeceasedDatePresent = data.deceasedDate ? true : false;
                    if(data.PersonMailingAddress){
                        if(data.PersonMailingAddress.street){
                            this.personMailingStreet = data.PersonMailingAddress.street;
                            this.personMailingAddress = this.personMailingStreet + '\n';
                        }
                        if(data.PersonMailingAddress.city){ 
                            this.personMailingCity = data.PersonMailingAddress.city;
                            this.personMailingAddress = this.personMailingAddress + this.personMailingCity;
                        }
                        if(data.PersonMailingAddress.state){
                            this.personMailingState = data.PersonMailingAddress.state;
                            this.personMailingAddress = this.personMailingAddress + this.personMailingState;
                        }
                        if(data.PersonMailingAddress.postalCode){
                            this.personMailingPostalCode = data.PersonMailingAddress.postalCode;
                            this.personMailingAddress = this.personMailingAddress + this.personMailingPostalCode + '\n';
                        }
                        if(data.PersonMailingAddress.country){
                            this.personMailingCountry = data.PersonMailingAddress.country;
                            this.personMailingAddress = this.personMailingAddress + this.personMailingCountry;
                        }
                    }
                    if(data.membershipStatus && (data.membershipStatus == this.memberLabel || data.membershipStatus == CRMRetail_Future_Member_Label)){
                        this.memberIcon = CRMRetail_Check_Icon_Name;
                    }else if(data.membershipStatus && data.membershipStatus == CRMRetail_InactiveMember_Label){
                        this.memberIcon = CRMRetail_BlockVisitor_Icon_Name;
                        this.membershipStatus = CRMRetail_InactiveMember_Label;
                    }else{
                        this.isMember = false;
                    }
                }   
                this.isSpinnerVisible = false;  
            }       
        })
        .catch(error =>{
            this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            this.isSpinnerVisible = false;  
        });
    }

    handleToggleSection(event) {
        const openSections = event.detail.openSections;
        this.activeSectionMessage = openSections; 
    }

    showToastMessage(message, title, variant) {
        var mode = (variant == 'success' || variant == 'warning') ? 'dismissable' : 'sticky';
        const evt = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: mode
        });
        this.dispatchEvent(evt);
    }
}