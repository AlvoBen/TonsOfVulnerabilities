import { LightningElement,api,wire,track } from 'lwc';
import getArticles from '@salesforce/apex/UserProfileController_C_HUM.getBookmarkedArticles';
import humanaSupportTemplate from './userBookmarkedArticles_LWCCommunity_HUM.html';
import go365Template from './userBookmarkedArticlesGo365_LWCCommunity_HUM.html';
import BookmarkArticlesHeader from '@salesforce/label/c.Community_BookmarkArticlesHeader';
import Id from '@salesforce/user/Id';

export default class UserBookmarkedArticles_LWCCommunity_HUM extends LightningElement {

    label={
        BookmarkArticlesHeader,
    };
    
    @track lstArticles;
    @track sfdcBaseURL;
    @track URLString;
    @track URLString1;
    @api Go365;
    @track isButtonVisible = true;
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
     * Method to fetch the Bookmarked Articles
    */
    @wire(getArticles, {

    })
    wiredArticles({error, data}) {
        try{    
            
                if(data)
                {    
                    var ArticleList=[];
                    var baseURL=this.URLString;
                    
                    data.forEach(function(entry) {
                    
                    var eachObj = {};
                    
                    eachObj.Name = entry.Name;
                    eachObj.Id = entry.Id;
                    eachObj.Title = entry.ArticleId__r.Title;
                    eachObj.UrlName = entry.ArticleId__r.UrlName;
                    eachObj.Summary = entry.ArticleId__r.Summary;
                    eachObj.url= baseURL+'/s/article/'+entry.ArticleId__c+'/'+entry.ArticleId__r.UrlName;
                    
                    ArticleList.push(eachObj);
                });
                
                this.lstArticles=ArticleList;
                
                }
                else
                {
                
                }
        }catch (error) {
            console.error("Error in wire call show bookmarked articles:" , error);
        }      
    }

}