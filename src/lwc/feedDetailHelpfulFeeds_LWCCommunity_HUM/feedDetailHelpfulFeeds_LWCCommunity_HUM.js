import { LightningElement,api,wire,track } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import HelpfulArticlesIcons from '@salesforce/resourceUrl/Communities_Helpful_RecommendedArticle';
import Go365Icons from '@salesforce/resourceUrl/CommunityGo365_Icons_SR_HUM';
import getHelpfulArticles from '@salesforce/apex/FeedDetailController_C_HUM.getRecommendations';
import helpfulArticleLabel from '@salesforce/label/c.Community_Helpful_Articles';
import humanaSupportTemplate from './feedDetailHelpfulFeeds_LWCCommunity_HUM.html';
import go365Template from './feedDetailHelpfulFeedsGo365_LWCCommunity_HUM.html';

let indexnext = 2;
let indexprev = 0;
let containerSel;

export default class FeedDetailHelpfulFeeds_LWCCommunity_HUM extends LightningElement {
    label = {
        helpfulArticleLabel, 
    };
    @api Go365;
    @api articleUrl;   
    @track lstArticles;
    @api recordId;
    @api recID;
    @track prevFlag = false;
    @track nextFlag = true;
    @track sfdcBaseURL;
    @track URLString;

    sfdcBaseURL=window.location.href;
    URLString=this.sfdcBaseURL.substring(0, this.sfdcBaseURL.indexOf("/s/"));
    
    nextImg = HelpfulArticlesIcons+'/Communities_Helpful_RecommendedArticle/NextIcon.svg';
    prevImg = HelpfulArticlesIcons+'/Communities_Helpful_RecommendedArticle/PreviousIcon.svg';
    docImg = HelpfulArticlesIcons+'/Communities_Helpful_RecommendedArticle/document.svg';

    docImgGo365 = Go365Icons+'/Go365Icons/document.svg';
    nextImgGo365 = Go365Icons+'/Go365Icons/NextIcon.svg';
    prevImgGo365 = Go365Icons+'/Go365Icons/PreviousIcon.svg';

    /*
     * Method to display template based on Community 
     */
    render() {
      return this.Go365 == true ? go365Template : humanaSupportTemplate;
    }
    
    /*
     * Method to get Current page reference 
     */
    @wire(CurrentPageReference)
  wiredPageRef (value) {
    if (!value) {
      return;
    }
    this.articleUrl = value.attributes.urlName;
    this.recID= value.attributes.recordId;
  }  

/*
     * Method to display helpful Articles using Apex call 
     */
    @wire(getHelpfulArticles, {
        recId: "$recID",
        source: "FeedDetail",
        articleType: "article"
    })
    wiredHelpfulArticles({error, data}) {
        try{    
                if(data)
                {   
                    var baseURL=this.URLString; 
                    var ArticleList=[];
                    data.forEach(function(entry) {
                    var eachArticleObj = {};
                    
                    eachArticleObj.Title = entry.Title;
                    eachArticleObj.ArticleCreatedDate = entry.ArticleCreatedDate;
                    eachArticleObj.UrlNAme = entry.UrlName;
                    eachArticleObj.url= baseURL + '/s/article/'+entry.Id+'/'+entry.UrlName;
                    eachArticleObj.viewcount = entry.ArticleTotalViewCount;
                    ArticleList.push(eachArticleObj);
                });
                
                this.lstArticles=ArticleList;
                if(this.lstArticles.length < 4){
                  this.nextFlag = false;
                }
                }
                else
                {
                console.log('*****',error);
                }
        }catch (error) {
            console.error("Error in wire call showArticles:" , error);
        }      
    }

    /*
     * Method to display horizontal scroll 
     */

    showNext(){
        if(this.nextFlag){
          containerSel = this.template.querySelectorAll('.articleHyperLinkCard');
          indexnext = indexnext+1;
          indexnext=Math.min(Math.max(indexnext,0),containerSel.length-1);
          containerSel[indexnext].scrollIntoView({behavior:"smooth", block:"nearest", inline:"nearest"});
          if(indexnext == containerSel.length-1){
              this.nextFlag = false;
          }
          this.prevFlag = true;
        }
      }

      /*
     * Method to display horizontal scroll 
     */
      showPrev(){
        if(this.prevFlag){
          containerSel = this.template.querySelectorAll('.articleHyperLinkCard');
          indexprev = indexnext-3;
          indexprev=Math.min(Math.max(indexprev,0),containerSel.length-1);
          containerSel[indexprev].scrollIntoView({behavior:"smooth", block:"nearest", inline:"nearest"});
          indexnext = indexnext-1;
          if(indexprev == 0){
              this.prevFlag = false;
          }
          this.nextFlag = true;
        }
      }
      
      /*
     * Method to disable horizontal scroll 
     */
      get disablePrev(){
        return !this.prevFlag;
      }
      
      /*
     * Method to disable horizontal scroll 
     */
      get disableNext(){
        return !this.nextFlag;
      }
}