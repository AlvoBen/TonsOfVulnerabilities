import { LightningElement,wire,track,api } from 'lwc';
import getTredingTopics from '@salesforce/apex/CommunityHomePageController_C_HUM.getTrendingTopics';
import { NavigationMixin } from 'lightning/navigation';
import discoverTopicLabel from '@salesforce/label/c.Community_Discover_Topics';
import HumanaIcons from '@salesforce/resourceUrl/CommunityIcons_SR_HUM';
import Go365Icons from '@salesforce/resourceUrl/CommunityGo365_Icons_SR_HUM';
import humanaSupportTemplate from './discoverTopics_LWCCommunity_HUM.html';
import go365Template from './discoverTopicsGo365_LWCCommunity_HUM.html';
import followTopicLabel from '@salesforce/label/c.Community_Follow_Topics';


export default class discoverTopics_LWCCommunity_HUM extends NavigationMixin(LightningElement) {

    label = {
        discoverTopicLabel,
        followTopicLabel, 
    };

    @api Go365;
    @track listOfTopics;
    @track sfdcBaseURL;
    @track URLString;

    sfdcBaseURL=window.location.href;
    URLString=this.sfdcBaseURL.substring(0, this.sfdcBaseURL.indexOf("/s/"));
    humanaDiamondLogo=HumanaIcons +'/humanaIcons/diamond.svg';
    jewelIcon = Go365Icons + '/Go365Icons/jewel.svg';

    /*
     * Method to display template based on Community 
     */

    render() {
        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }

    /*
     * Method to display trending Topics  
     */
    @wire(getTredingTopics)
    wiredTopics({error, data}) {
        try{ 
            if(data){
                
                var TopicsList=[];
                var baseURL=this.URLString;
                data.forEach(function(entry) {
                
                var eachTopicObj = {};
                eachTopicObj.TopicId=entry.Id;
                eachTopicObj.TopicName=entry.Name;
                
                eachTopicObj.url= baseURL+'/s/topic/'+entry.Id+'/'+entry.Name;
                
                TopicsList.push(eachTopicObj);
                });
                this.listOfTopics=TopicsList;
                
            }

        }catch (error) {
            console.error("Error in wire call getTrendingTopics:" , error);
        }
    }

    /*
     * Method to navigate to All Topics page 
     */
    navigateToAllTopicsPage(){
        
        this[NavigationMixin.Navigate]({
            
            type: 'standard__webPage',
            attributes: {
                pageName: 'Topic_Catalog',
                url: this.URLString +'/s/topiccatalog'
               
            }
        });
        
    }
}