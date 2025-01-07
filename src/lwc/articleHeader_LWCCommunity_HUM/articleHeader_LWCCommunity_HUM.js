import { LightningElement,api,wire,track } from 'lwc';
import getKnowledgeFeedDetails from '@salesforce/apex/ArticleDetailController_C_HUM.getKnowledgeFeedDetails';
import bookmarkController from '@salesforce/apex/ArticleDetailController_C_HUM.bookmarkController';
import { CurrentPageReference } from 'lightning/navigation';
import HumanaIcons from '@salesforce/resourceUrl/CommunityIcons_SR_HUM';
import Go365Icons from '@salesforce/resourceUrl/CommunityGo365_Icons_SR_HUM';
import articlePointsLabel from '@salesforce/label/c.Community_Article_Points';
import articleLikesLabel from '@salesforce/label/c.Community_Article_Likes';
import articleCommentsLabel from '@salesforce/label/c.Community_Article_Comments';
import articleFollowersLabel from '@salesforce/label/c.Community_Article_Followers';
import humanaSupportTemplate from './articleHeader_LWCCommunity_HUM.html';
import go365Template from './articleHeaderGo365_LWCCommunity_HUM.html';


export default class ArticleHeader_LWCCommunity_HUM extends LightningElement {
    label={
        articlePointsLabel,
        articleLikesLabel,
        articleCommentsLabel,
        articleFollowersLabel,
    };

    @api Go365;
    @track articleUrl;
    @track articleTitle;
    @track articleId;
    @track articleTotalViewCount;
    @track articleCreatedDate;
    @track createdBy;
    @track articleLikeCount;
    @track articleCommentsCount;
    @track articleFollowersCount;
    @track isBookmarked;
    
    likeIcon=HumanaIcons +'/humanaIcons/thumbsUp@3x.png';
    commentIcon=HumanaIcons +'/humanaIcons/round-bubble@3x.png';
    starIcon= HumanaIcons +'/humanaIcons/star.svg';
    starSelectedIcon= HumanaIcons +'/humanaIcons/star-selected.svg';
    followIcon= HumanaIcons +'/humanaIcons/userPlus@3x.png';
    diamondIcon =HumanaIcons+ "/humanaIcons/diamond.svg"; 

    jewelIcon = Go365Icons + '/Go365Icons/jewel.svg';
    likeIconGo365=Go365Icons +'/Go365Icons/thumbs-up.svg';
    commentIconGo365=Go365Icons +'/Go365Icons/round-bubble.svg';
    starIconGo365= Go365Icons +'/Go365Icons/star.svg';
    starSelectedIconGo365= Go365Icons +'/Go365Icons/icons-shapes-star.svg';
    followIconGo365= Go365Icons +'/Go365Icons/user-plus.svg';
    
    /*
     * Method to display required template based on Community
    */

    render() {
        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }

    /*
     * Method to get current page reference 
    */

    @wire(CurrentPageReference)
    wiredPageRef(value){
        if (!value) {
                  return;
                }
        this.articleUrl = value.attributes.urlName;
    }

    /*
     * Method to get knowledge feed for an Article
    */

    @wire(getKnowledgeFeedDetails, {
        ArticleUrlName: "$articleUrl"
    })
    wiredFeedDetails({error, data}) {
        try{
            
                if(data)
                {    
                    this.articleLikeCount = data.likesCount;
                    this.articleCommentsCount = data.CommentsCount;
                    this.articleFollowersCount = data.FollowersCount;

                    var articleDetail = data.ArticleDetail;
                    this.articleTitle = articleDetail.Title;
                    this.articleId = articleDetail.Id;
                    this.articleTotalViewCount = articleDetail.ArticleTotalViewCount;
                    this.articleCreatedDate = articleDetail.CreatedDate;
                    this.createdBy = articleDetail.CreatedBy.CommunityNickname;
                    
                    let bookmarkedrec=articleDetail.ArticleBookmarks__r;
                    
                    let eachRec=bookmarkedrec[0];
                    this.isBookmarked=eachRec.Bookmarked__c;
                    
                }
        }catch (error) {
            console.error("Error in wire call getRecord:" , error);
        }
    }

    /*
     * Method to bookmark an Article
    */

    handleBookmark(){
        bookmarkController({ articleId: this.articleId})
        .then(result=>{
            this.isBookmarked=result;
        })
        .catch(error => {
            this.error = error;
            console.log('errored-->',error);
        });

    }

}