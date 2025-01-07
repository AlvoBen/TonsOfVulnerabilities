import { LightningElement,api,wire,track } from 'lwc';
import getPopularGroups from '@salesforce/apex/FeedDetailController_C_HUM.getGroups';
import HumanaIcons from '@salesforce/resourceUrl/CommunityIcons_SR_HUM';
import Go365Icons from '@salesforce/resourceUrl/CommunityGo365_Icons_SR_HUM';
import { CurrentPageReference } from 'lightning/navigation';
import { NavigationMixin } from 'lightning/navigation';
import recommendedLabel from '@salesforce/label/c.Community_Home_Recommended_Groups';
import pointsLabel from '@salesforce/label/c.Community_Home_Points';
import popularGroupsLabel from '@salesforce/label/c.Community_Feed_Popular_Groups';
import membersLabel from '@salesforce/label/c.Community_Members';
import humanaSupportTemplate from './feedDetailPopularGroups_LWCCommunity_HUM.html';
import go365Template from './feedDetailPopularGroupsGo365_LWCCommunity_HUM.html';

export default class FeedDetailPopularGroups_LWCCommunity_HUM extends NavigationMixin(LightningElement) {
    label = {
        recommendedLabel,
        pointsLabel,
        popularGroupsLabel,
        membersLabel,
    };
    @api Go365;
    @api recordId;
    @api recID;
    @track lstGroups1;
    @track lstGroups2;
    @track lstGroups;
    @api name1;
    @track isHomePage;
    @track sfdcBaseURL;
    @track URLString;

    sfdcBaseURL=window.location.href;
    URLString=this.sfdcBaseURL.substring(0, this.sfdcBaseURL.indexOf("/s/"));
    groupIcon=HumanaIcons +'/humanaIcons/group@3x.png';
    humanaDiamondLogo=HumanaIcons +'/humanaIcons/diamond.svg';

    jewelIcon=Go365Icons +'/Go365Icons/jewel.svg';
    groupIconGo365=Go365Icons +'/Go365Icons/group-icon.svg';

    /*
     * Method to display template based on the community  
     */

    render() {
        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }

      /*
     * Method to get current page reference   
     */
  @wire(CurrentPageReference)
  wiredPageRef (value) {
    if (!value) {
      return;
    }
    
    var s1 = value.attributes.name;

    var s2 = "Home";
    if(s1 == s2)
        {
            this.recID= "";
            this.isHomePage=true;
            
        }
    else    
        {
            this.recID= JSON.stringify(value.attributes.recordId);
            this.isHomePage=false;
            
        }
    this.name1= value.attributes.name;
  } 

/*
     * Method to fetch the popular groups using Apex call 
     */
    @wire(getPopularGroups, {
        recId: "$recID"
    })
    wiredPopularGroups({error, data}) {
        try{    
            
                if(data)
                {    
                    var GroupList=[];
                    var baseURL=this.URLString;
                    data.forEach(function(entry) {
                    
                    var eachGroupObj = {};
                    
                    eachGroupObj.Name = entry.Name;
                    eachGroupObj.Id = entry.Id;
                    eachGroupObj.MemberCount = entry.MemberCount;
                    
                    eachGroupObj.url= baseURL+'/s/group/'+entry.Id+'/'+entry.Name;
                    
                    
                    eachGroupObj.IsArchived = entry.IsArchived;
                    eachGroupObj.NetworkId = entry.NetworkId;
                    eachGroupObj.CollaborationType = entry.CollaborationType;
                    
                    GroupList.push(eachGroupObj);
                });
                
                this.lstGroups=GroupList;
                
                }
                else
                {
                
                }
        }catch (error) {
            console.error("Error in wire call showGroups:" , error);
        }      
    }

    /*
     * Method to navigate to All Groups page  
     */
    navigateToAllGroupsPage(){
        this[NavigationMixin.Navigate]({
            
            type: 'standard__objectPage',
            attributes: {
                objectApiName: 'CollaborationGroup',
                actionName: 'list'
            },
            state: {
                filterName: 'Recent'
            },

            
        });
        
    }

}