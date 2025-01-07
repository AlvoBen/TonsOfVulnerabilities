import { LightningElement,wire,track,api } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import sendMessage from '@salesforce/apex/UserProfileController_C_HUM.sendMessage';
import sendMessageLabel from '@salesforce/label/c.Community_User_Send_Message';
import closeLabel from '@salesforce/label/c.Community_User_Close';
import PlacehlderText from '@salesforce/label/c.Community_DirectMsg_PlacehlderText';
import humanaSupportTemplate from './userDirectMessage_LWCCommunity_HUM.html';
import go365Template from './userDirectMessageGo365_LWCCommunity_HUM.html';
import Id from '@salesforce/user/Id';

export default class UserDirectMessage_LWCCommunity_HUM extends NavigationMixin(LightningElement) {
    
    label = {
        sendMessageLabel,
        closeLabel,
        PlacehlderText
    };
    @track isModalOpen = false;
    @track isButtonVisible = true;
    @track sfdcBaseURL;
    @track URLString;
    @track URLString1;
    @api fullUrl;
    @api Go365;
    @track userID;
    userID=Id;

    sfdcBaseURL=window.location.href;
    URLString=this.sfdcBaseURL.substring(0, this.sfdcBaseURL.indexOf("/s/"));
    URLString1=this.sfdcBaseURL.substring(this.sfdcBaseURL.indexOf("profile/"));
    
    /*
        * Method to decide the template based on the community
        */

    render() {
        if (this.URLString1.includes(this.userID.substring(0,15)))
        {
            this.isButtonVisible = true;
        } else {
            this.isButtonVisible = false;
        }

        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }

    
        

      /*
     * Method to open the message popup
    */
    openmodal() {
        
        this.isModalOpen = true;
    }

     /*
     * Method to close the message popup
    */

    closemodal() {
        this.isModalOpen = false;
    }
    
    /*
     * Method to send message to moderator
    */
    sendmessage(evt) {
        let inputCmpList = this.template.querySelectorAll(".msg");
        let inputCmp;
        if(evt.target.dataset.device === "mobile"){
            inputCmp = inputCmpList[1];

        }else{
            inputCmp = inputCmpList[0]; 
        }                
        
        let value = inputCmp.value;
        if(value){
            this.fullUrl = this.URLString + '/s/messages/';
            sendMessage({param:value}).then(result => {
                if(result){
                    this.fullUrl = this.fullUrl + result;
                    
                    this[NavigationMixin.Navigate]({
                        type: 'standard__webPage',
                        attributes: {
                            url: this.fullUrl
                        }
                    },
                    true 
                    );
                }
            })
            .catch(error => {
                
            });
            this.isModalOpen = false;
        }
    }
}