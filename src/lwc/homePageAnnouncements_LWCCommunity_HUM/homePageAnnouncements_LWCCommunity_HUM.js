import { LightningElement,api,wire,track } from 'lwc';
import getAnnouncements from '@salesforce/apex/CommunityHomePageController_C_HUM.getAnnouncements';
import HumanaIcons from '@salesforce/resourceUrl/CommunityIcons_SR_HUM';
import Go365Icons from '@salesforce/resourceUrl/CommunityGo365_Icons_SR_HUM';
import announcementLabel from '@salesforce/label/c.Community_Announcements';
import humanaSupportTemplate from './homePageAnnouncements_LWCCommunity_HUM.html';
import go365Template from './homePageAnnouncementsGo365_LWCCommunity_HUM.html';
import announcementURLLabel from '@salesforce/label/c.Community_Announcement_URL_Click_Here';

export default class HomePageAnnouncements_LWCCommunity_HUM extends LightningElement {
    label = {
        announcementLabel, 
        announcementURLLabel,
    };

    @api Go365;
    @track lstAnnouncements;

    announceIcon=HumanaIcons +'/humanaIcons/megaphone.svg';
    announceIconGo365 = Go365Icons +'/Go365Icons/megaphone.svg';
    
    render() {
        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }

       /*
     * Method to fetch annonucements from community setup object
    */
    @wire(getAnnouncements, {
        
    })
    wiredAnnouncements({error, data}) {
        try{    
            
                if(data)
                {    
                    
                    
                    var AnnouncementList=[];
                    data.forEach(function(entry) {
                    
                    var eachObj = {};
                    
                    eachObj.Title = entry.Announcement_Title__c;
                    eachObj.Description = entry.Announcement_Description__c;
                    eachObj.Url = entry.Announcement_URL__c;
                    if (eachObj.Url) {
                        eachObj.showUrl = true;
                    }
                    else {
                        eachObj.showUrl = false;
                    }
                    
                    AnnouncementList.push(eachObj);
                });
                this.lstAnnouncements=AnnouncementList;
                
                }
                else
                {
                
                }
        }catch (error) {
            console.error("Error in wire call announcements:" , error);
        }      
    }
}