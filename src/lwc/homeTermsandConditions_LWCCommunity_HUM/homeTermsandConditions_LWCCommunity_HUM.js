import { LightningElement,track ,api} from 'lwc';
import updateUser from '@salesforce/apex/CommunityHomePageController_C_HUM.updateUserRecord';
import fetchTermsandConditions from '@salesforce/apex/CommunityHomePageController_C_HUM.fetchTermsandConditions';
import humanaSupportTemplate from './homeTermsandConditions_LWCCommunity_HUM.html';
import go365Template from './homeTermsandConditionsGo365_LWCCommunity_HUM.html';
import homeTermsandConditionsLabel from '@salesforce/label/c.Community_Home_Terms_and_Conditions_Label';
import humanaTermsandConditionsLabel from '@salesforce/label/c.Humana_Community_Terms_and_Conditions_Label';
import go365TermsandConditionsLabel from '@salesforce/label/c.Go365_Community_Terms_and_Conditions_Label';
import acceptLabel from '@salesforce/label/c.Community_Accept_Label';

export default class HomeTermsandConditions_LWCCommunity_HUM extends LightningElement {
    @track isModalOpen = false;
    @track info;
    @api Go365;

    label = {
        homeTermsandConditionsLabel, 
        acceptLabel,
        humanaTermsandConditionsLabel,
        go365TermsandConditionsLabel
    };

    render() {
        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }

    connectedCallback(){
        /*
        * Method to fetch terms and condition record from community setup object
        */   
        fetchTermsandConditions()
            .then(result=>{
                if (result.Terms_and_Conditions__c)
                {
                    this.isModalOpen = true;
                    this.info= result.Terms_and_Conditions__c;
                }
                else 
                {
                    this.isModalOpen = false;
                }
            })
            .catch(error => {
                this.error = error;
                console.log('errored-->',error);
            });
        }

    acceptTC(){
        
        updateUser({})
        .then(result=>{
            console.log(' user update completed');
            window.location.reload();
        })
        .catch(error => {
            this.error = error;
            console.log('errored-->',error);
        });
        this.isModalOpen = false;
    }
}