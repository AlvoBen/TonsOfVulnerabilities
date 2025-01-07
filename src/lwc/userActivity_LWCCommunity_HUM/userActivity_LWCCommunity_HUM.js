import { LightningElement,api,wire,track } from 'lwc';
import getUserActivity from '@salesforce/apex/UserProfileController_C_HUM.getUserActivity';
import postLabel from '@salesforce/label/c.Community_User_Post';
import answeredLabel from '@salesforce/label/c.Community_User_Answered';
import awaitingLabel from '@salesforce/label/c.Community_User_Awaiting_Response';


export default class UserActivity_LWCCommunity_HUM extends LightningElement {

    label = {
        postLabel,
        answeredLabel,
        awaitingLabel,
    };

    @track mapUserActivity;
    @track var1;
    @track posted;
    @track answered;
    @track unanswered;

     /*
     * Method to fetch answered , unanswered , posted question of logged in user
    */

    @wire(getUserActivity, {
        
    })
    wiredUserActivity({error, data}) {
    try{    
            if(data)
            {   
                this.var1 = JSON.stringify(data);
                this.posted = JSON.parse(this.var1).Posted;
                this.answered = JSON.parse(this.var1).Answered;
                this.unanswered = JSON.parse(this.var1).Unanswered;

                this.mapUserActivity = JSON.parse(data) ;
            }
            else
            {
                console.log('User Activity component-4*****',error);
            }
    }catch (error) {
        console.error("Error in wire call User Activity component:" , error);
    }    
    }

}