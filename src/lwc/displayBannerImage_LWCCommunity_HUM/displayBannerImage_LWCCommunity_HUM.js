import { LightningElement,api,wire,track } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import getBannerImages from '@salesforce/apex/CommunityHomePageController_C_HUM.getBannerImages';

export default class DisplayBannerImage_LWCCommunity_HUM extends LightningElement {

    @api recID;
    @api recordId;
    @api Go365;
    @track TopicDeskImage;
    @track TopicName;
    @track TopicMobImage;

/*
     * Method to get Current page reference 
 */

@wire(CurrentPageReference)
  wiredPageRef (value) {
    if (!value) {
      return;
    }
    this.recID= value.attributes.recordId;
  }  

@wire(getBannerImages, {
    recId: "$recID"
})
wiredBannerImages({error, data}) {
    try{    
            if(data)
            {   
                this.TopicDeskImage = data.Topic_Image__c;
                this.TopicName = data.Topic_Name__c;
                this.TopicMobImage = data.Topic_Image_Mobile__c;
            }
            else
            {
            console.log('*****',error);
            }
    }catch (error) {
        console.error("Error in wire call getBannerImages:" , error);
    }      
}

}