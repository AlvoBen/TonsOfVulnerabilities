import { LightningElement,track,wire,api } from 'lwc';
import getHelpfulArticles from '@salesforce/apex/ArticleDetailController_C_HUM.getRecommendations';
import { CurrentPageReference } from 'lightning/navigation';
import RecommendedWatchIcons from '@salesforce/resourceUrl/Communities_Helpful_RecommendedArticle';
import Go365Icons from '@salesforce/resourceUrl/CommunityGo365_Icons_SR_HUM';
import recommendedWatchLabel from '@salesforce/label/c.Community_Article_Recommended_Watch';
import humanaSupportTemplate from './articleRecommendedWatch_LWCCommunity_HUM.html';
import go365Template from './articleRecommendedWatchGo365_LWCCommunity_HUM.html';

let indexnext = 2;
let indexprev = 0;
let containerSel;
export default class ArticleRecommendedWatch_LWCCommunity_HUM extends LightningElement {
label={
        recommendedWatchLabel,
    };

@api Go365;
@api articleUrl;
@api source;
@api articletype;  
@track lstArticles;
@track prevFlag = false;
@track nextFlag = true;
@track sfdcBaseURL;
@track URLString;

sfdcBaseURL=window.location.href;
URLString=this.sfdcBaseURL.substring(0, this.sfdcBaseURL.indexOf("/s/"));

nextImg = RecommendedWatchIcons+'/Communities_Helpful_RecommendedArticle/NextIcon.svg';
prevImg = RecommendedWatchIcons+'/Communities_Helpful_RecommendedArticle/PreviousIcon.svg';
defaultImg = RecommendedWatchIcons+'/Communities_Helpful_RecommendedArticle/RecommendedWatch.svg';
 
defaultImgGo365 = Go365Icons +'/Go365Icons/play-icon.svg';
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
    
    var s1 = value.attributes.name;
    
    var s2 = "Home";
    if(s1 == s2)
        {
            this.articleUrl = "";
            this.source="HomePage";
            this.articletype="video";
            
        }
    else    
        {
            this.articleUrl = value.attributes.urlName;
            this.source="ArticleDetail";
            this.articletype="video";
            
        }
    
      }  
     
    /*
     * Method to get helpful articles using an Apex call 
     */

    @wire(getHelpfulArticles, {
        urlName: "$articleUrl",
        source: "$source",
        articleType: "$articletype"
    })
    wiredHelpfulArticles({error, data}) {
        try{    
            
                if(data)
                {    
                    
                    var baseURL=this.URLString; 
                    this.lstArticles = data ;
                    var ArticleList=[];
                    data.forEach(function(entry) {
                    
                    var eachArticleObj = {};
                    
                    eachArticleObj.Title = entry.Title;
                    eachArticleObj.ArticleCreatedDate = entry.ArticleCreatedDate;
                    eachArticleObj.UrlNAme = entry.UrlName;
                    
                    eachArticleObj.url= baseURL+'/s/article/'+entry.Id+'/'+entry.UrlName;
                    
                    ArticleList.push(eachArticleObj);
                    });
                
                    this.lstArticles=ArticleList;
                    if(this.lstArticles.length < 4){
                        this.nextFlag = false;
                    }
                }
        }catch (error) {
            console.error("Error in wire call recommended watch:" , error);
        }    
        }

    /*
    * Method to display the horizontal scroll for Article list
    */
        showNext(){
            containerSel = this.template.querySelectorAll('.recWatch-card');
            indexnext = indexnext+1;
            indexnext=Math.min(Math.max(indexnext,0),containerSel.length-1);
            containerSel[indexnext].scrollIntoView({ behavior: "smooth", block: "nearest", inline: "nearest" });
            if(indexnext == containerSel.length-1){
                this.nextFlag = false;
            }
            this.prevFlag = true;
        }

    /*
     * Method to display the horizontal scroll for Article list
     */

        showPrev(){
            containerSel = this.template.querySelectorAll('.recWatch-card');
            indexprev = indexnext-3;
            indexprev=Math.min(Math.max(indexprev,0),containerSel.length-1);
            containerSel[indexprev].scrollIntoView({ behavior: "smooth", block: "nearest", inline: "nearest" });
            indexnext = indexnext-1;
            if(indexprev == 0){
                this.prevFlag = false;
            }
            this.nextFlag = true;
        }

    /*
     * Method to disable  horizontal scroll 
     */
        get disablePrev() {
            return !this.prevFlag;
        }
    
    /*
     * Method to disable  horizontal scroll 
     */
        get disableNext() {
            return !this.nextFlag;
        }
    
    

}