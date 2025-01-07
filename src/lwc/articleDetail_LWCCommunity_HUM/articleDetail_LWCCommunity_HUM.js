import { LightningElement,api,wire,track } from 'lwc';
import getArticle from '@salesforce/apex/ArticleDetailController_C_HUM.getArticle';
import { CurrentPageReference } from 'lightning/navigation';

export default class articleDetail_LWCCommunity_HUM extends LightningElement {
    @api Article_Detail_Page;
    @track articleUrl;
    @track articleTitle;
    @track articleBody;
    @track articleId;
    
    /*
     * Method to get the current page reference
    */
    
    @wire(CurrentPageReference)
    wiredPageRef(value){
        if (!value) {
                  return;
                }
        this.articleUrl = value.attributes.urlName;
    }

    /*
     * Method to get the article details using Apex call
    */

    @wire(getArticle, {
        ArticleUrlName: "$articleUrl"
    })
    wiredArticle({error, data}) {
        try{
            
                if(data)
                {   
                    this.articleId = data.Id;        
                    this.articleTitle = data.Title;
                    this.articleBody = data.Article_Body__c;
                    
                }
        }catch (error) {
            console.error("Error in wire call getRecord:" , error);
        }
    }
    
}