/*******************************************************************************************************************************
LWC Name : homeDiscussionFeed_LWCCommunity_HUM.js
Function    : This LWC component uses to display the feed items on community home page

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Nitaj Titans                                   08/03/2021                   Original Version
********************************************************************************************************************************/
import { LightningElement, track } from 'lwc';
import getDiscussionCompConfig from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getDiscussionCompConfig';
import getUserType from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.isRendered';
import getSearchFeedItems from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getSearchFeedItems';
import getSortedFeedItems from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getSortedFeedItems';
import getLoadedOrRefreshedFeedItems from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getLoadedOrRefreshedFeedItems';
import submitFeedComment from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.submitFeedComment';
import submitOrRemoveFeedLike from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.submitOrRemoveFeedLike';
import getHideOrMoreComments from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getHideOrMoreComments';
import getMoreFeedItems from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getMoreFeedItems';
import getLatestFeedOptions from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getLatestFeedOptions';
import getFeedOptions from '@salesforce/apex/CommunityDiscussionFeedController_C_HUM.getFeedOptions';
import sortByLabel from '@salesforce/label/c.Sort_HUM';
import selectLabel from '@salesforce/label/c.Select_HUM';
import searchLabel from '@salesforce/label/c.Search_HUM';
import noFeedLabel from '@salesforce/label/c.NoFeed_HUM';
import noContentLabel from '@salesforce/label/c.Nocontent_HUM';
import likeLabel from '@salesforce/label/c.Community_User_Gamification_Like';
import likedLabel from '@salesforce/label/c.Liked_HUM';
import commentLabel from '@salesforce/label/c.Comment_HUM';
import answerLabel from '@salesforce/label/c.Community_User_Gamification_Answer';
import peopleLikedLabel from '@salesforce/label/c.Peopleliked_HUM';
import moreCommentsLabel from '@salesforce/label/c.MoreComments_HUM';
import hideCommentsLabel from '@salesforce/label/c.HideComments_HUM';
import writeCommentLabel from '@salesforce/label/c.Writecomment_HUM';
import viewmoreLabel from '@salesforce/label/c.ViewMore_HUM';
export default class HomeDiscussionFeed_LWCCommunity_HUM extends LightningElement {
    label = {
        sortByLabel,
        selectLabel,
        searchLabel,
        noFeedLabel,
        noContentLabel,
        likeLabel,
        likedLabel,
        commentLabel,
        answerLabel,
        peopleLikedLabel,
        moreCommentsLabel,
        hideCommentsLabel,
        writeCommentLabel,
        viewmoreLabel,
    };
    sortByValue;
    @track feedItemList;
    @track sortFeedItem;
    @track selectFeedItem;
    @track searchFeedItem;
    @track inputValue = '';
    @track clearIconFlag = false;
    @track hasNoData = false;
    @track expandTextarea = 'slds-publisher slds-publisher_comment';
    @track valuecomment;
    @track showCommentSection = false;
    @track isLoading = false;
    @track showViewMore = true;
    @track oldFeedItemList;
    @track showUnlike = true;
    @track itemsLength = 50;
    @track latestFeedOptions = [];
    @track feedOptions = [];
    @track isGuestUser = false;
    @track isCompVisible = false;
    /** this method is hide or show the comment textarea when click on comment button */
    handleCommentSection(event) {
        let targetId = event.target.dataset.id;
        let element = this.template.querySelector('[data-comment-area="' + targetId + '"]');
        element.className = element.className == 'slds-publisher slds-publisher_comment' ? 'slds-publisher slds-publisher_comment slds-is-active' : 'slds-publisher slds-publisher_comment';
    }
    /** this method is used to expand the text area when clicks on textbox */
    handleCommentClick(event) {
        let targetId = event.target.dataset.id;
        let element = this.template.querySelector('[data-comment-area="' + targetId + '"]');
        element.className = 'slds-publisher slds-publisher_comment slds-is-active';
    }
    connectedCallback() {
        getDiscussionCompConfig().then(data => {
            if(data != null){
                if(data.IsON__c){
                    this.isCompVisible = true;
                    this.fetchUserType();
                }else{
                    this.isCompVisible = false;
                }
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });  
    }
    fetchUserType(){
        getUserType().then(data => {
            if(data == true){
                this.isGuestUser = true;
            }else{
                this.initData();
            }
        }).catch(error => {
            console.log("Error Occured", error);
        }); 
    }
    /**Load data when initializing */
    initData(){
        getLatestFeedOptions().then(data => {
            if (data) {
                for (let key in data) {
                    const opt = { label: key, value: data[key] };
                    this.latestFeedOptions = [...this.latestFeedOptions, opt];
                }
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });
        getFeedOptions().then(data => {
            if (data) {
                for (let key in data) {
                    const opt = { label: key, value: data[key] };
                    this.feedOptions = [...this.feedOptions, opt];
                }
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });
        this.getMostRecentActivities();
    }
    /**this method will run when user click on refresh button */
    handleRefresh() {
        this.sortByValue = '';
        this.selectFeedItem = null;
        this.inputValue = null;
        this.template.querySelectorAll('lightning-menu-item').forEach(element => {
            element.prefixIconName = 'utility:';
        });
        this.clearIconFlag = false;
        this.getMostRecentActivities();
    }
    /** this method is run when initialization of page and also refreshing of the page/component */
    getMostRecentActivities() {
        this.isLoading = true;
        getLoadedOrRefreshedFeedItems({
            feedItemLength: this.itemsLength,
            isCompVisible : this.isCompVisible,
            isLoadMore : false
        })
            .then(result => {
                this.isLoading = false;
                this.feedItemList = result.slice(0,10);
                console.log('feed list', this.feedItemList.length);
                this.hasNoData = this.feedItemList.length == 0 ? true : false;
                this.showViewMore = this.feedItemList.length == 0 ? false : true;
                this.sortByValue = this.latestFeedOptions[2].label;
                this.sortFeedItem = this.latestFeedOptions[2].label;
            })
            .catch(error => {
                this.isLoading = false;
                console.log('error getMostRecentActivities -->', error);
            });
    }
    /**this method is used to get the latest sorted feeds when user selects from dropdown field */
    handleSortItemChange(event) {
        this.isLoading = true;
        this.selectFeedItem = null;
        this.inputValue = null;
        this.template.querySelectorAll('lightning-menu-item').forEach(element => {
            element.prefixIconName = 'utility:';
        });
        this.clearIconFlag = false;
        this.sortFeedItem = event.target.value;
        const feedItemDetails = { selectItem: this.selectFeedItem, searchInput: this.inputValue, sortItem: this.sortFeedItem, feedItemsLength: this.itemsLength, isComponentVisible : this.isCompVisible };
        getSortedFeedItems({
            feedItemDetails: JSON.stringify(feedItemDetails)
        })
            .then(result => {
                this.feedItemList = result.slice(0,10);
                console.log('feed list', this.feedItemList.length);
                this.hasNoData = this.feedItemList.length == 0 ? true : false;
                this.showViewMore = this.feedItemList.length == 0 ? false : true;
                this.isLoading = false;
            })
            .catch(error => {
                this.isLoading = false;
                console.log('error handleSortItemChange -->', error);
            });
    }
    /** this method is used when user search for a feed and click enter */
    handleKeyUp(event) {
        this.inputValue = event.target.value;
        if (this.inputValue != '') {
            this.clearIconFlag = true;
        } else {
            this.clearIconFlag = false;
        }
        let keyData = event.keyCode;
        if (keyData == 13 && this.inputValue != '') {
            this.isLoading = true;
            const feedItemDetails = { selectItem: this.selectFeedItem, searchInput: this.inputValue, sortItem: this.sortFeedItem, feedItemsLength: this.itemsLength, isComponentVisible : this.isCompVisible };
            getSearchFeedItems({
                feedItemDetails: JSON.stringify(feedItemDetails)
            })
                .then(result => {
                    this.feedItemList = result.slice(0,10);
                    console.log('feed list', this.feedItemList.length);
                    this.hasNoData = this.feedItemList.length == 0 ? true : false;
                    this.showViewMore = this.feedItemList.length == 0 ? false : true;
                    this.isLoading = false;
                    this.sortByValue = '';
                    this.sortFeedItem = '';
                })
                .catch(error => {
                    this.isLoading = false;
                    console.log('error handleKeyUp -->', error);
                });
        }
    }
    /** this method is used when user selects for filteration while searhcing for a feed */
    handleFilteration(event) {
        this.template.querySelectorAll('lightning-menu-item').forEach(element => {
            element.prefixIconName = 'utility:';
        });
        this.selectFeedItem = event.detail.value;
        const selectedValue = this.template.querySelector('[data-id=' + this.selectFeedItem + ']');
        selectedValue.prefixIconName = "utility:check";
        if (this.inputValue) {
            this.isLoading = true;
            const feedItemDetails = { selectItem: this.selectFeedItem, searchInput: this.inputValue, sortItem: this.sortFeedItem, feedItemsLength: this.itemsLength, isComponentVisible : this.isCompVisible };
            getSearchFeedItems({
                feedItemDetails: JSON.stringify(feedItemDetails)
            })
                .then(result => {
                    this.feedItemList = result.slice(0,10);
                    console.log('feed list', this.feedItemList.length);
                    this.hasNoData = this.feedItemList.length == 0 ? true : false;
                    this.showViewMore = this.feedItemList.length == 0 ? false : true;
                    this.isLoading = false;
                    this.sortByValue = '';
                    this.sortFeedItem = '';
                })
                .catch(error => {
                    console.log('error handleFilteration -->', error);
                    this.isLoading = false;
                });
        }
    }
    /**this method is used to clear the value in search input when clicks on clear icon */
    resetData() {
        this.clearIconFlag = false;
        this.inputValue = '';
    }
    /**this method is used to insert the feedcomment when user submit comment */
    submitComment(event) {
        let feedItemsIds = [];
        for (let i = 0; i < this.feedItemList.length; i++) {
            feedItemsIds.push(this.feedItemList[i].recordId);
        }
        this.isLoading = true;
        const feedItemId = event.target.dataset.id;
        let comment = this.template.querySelector('[data-text-area="' + feedItemId + '"]');
        const allData = { feedItemId: feedItemId, commentBody: comment.value };
        submitFeedComment({
            feedItemsIds: feedItemsIds,
            commentDetails: JSON.stringify(allData)
        })
            .then(result => {
                comment.value = '';
                let element = this.template.querySelector('[data-comment-area="' + feedItemId + '"]');
                element.className = 'slds-publisher slds-publisher_comment';
                this.feedItemList = result;
                console.log('feed list', this.feedItemList.length);
                this.hasNoData = this.feedItemList.length == 0 ? true : false;
                //this.showViewMore = this.feedItemList.length < 10 ? false : true;
                this.isLoading = false;
            })
            .catch(error => {
                this.isLoading = false;
                console.log('error submitComment-->', error);
            });
    }
    /**this method is used submit like of a feed item */
    submitOrRemoveFeedLike(event) {
        let nameOfButton = event.target.name;
        let feedItemsIds = [];
        for (let i = 0; i < this.feedItemList.length; i++) {
            feedItemsIds.push(this.feedItemList[i].recordId);
        }
        this.isLoading = true;
        const feedItemId = event.target.dataset.id;
        submitOrRemoveFeedLike({
            feedItemsIds: feedItemsIds,
            feedItemId: feedItemId,
            buttonName: nameOfButton
        })
            .then(result => {
                this.feedItemList = result;
                console.log('feed list', this.feedItemList.length);
                this.hasNoData = this.feedItemList.length == 0 ? true : false;
                //this.showViewMore = this.feedItemList.length < 10 ? false : true;
                this.isLoading = false;
            })
            .catch(error => {
                this.isLoading = false;
                console.log('error submitOrRemoveFeedLike -->', error);
            });
    }
    /** this method is used to hide the comments or display more comments of feed items when clicks on hide or more comments link */
    handleMoreOrHideComments(event) {
        let feedItemsIds = [];
        this.feedItemList.forEach(element => {
            feedItemsIds.push(element.recordId);
        });
        console.log('feedItemsIds :', feedItemsIds);
        const feedItemId = event.target.dataset.id;
        this.isLoading = true;
        getHideOrMoreComments({
            feedItemsIds: feedItemsIds,
            feedItemId: feedItemId
        })
            .then(result => {
                this.feedItemList = result;
                console.log('feed list', this.feedItemList.length);
                this.hasNoData = this.feedItemList.length == 0 ? true : false;
                //this.showViewMore = this.feedItemList.length < 10 ? false : true;
                this.isLoading = false;
            })
            .catch(error => {
                this.isLoading = false;
                console.log('error handleMoreOrHideComments-->', error);
            });
    }
    /**this method is used to get more feed items when clicks on view more button */
    handleMoreFeeds(){
        //view more functionality for upto 50 feed items.
        let itemsLength = this.feedItemList.length + 10;
        if(itemsLength <= 50){
            this.isLoading = true;
            const feedItemDetails = { selectItem: this.selectFeedItem, searchInput: this.inputValue, sortItem: this.sortFeedItem, feedItemsLength: this.itemsLength, isComponentVisible : this.isCompVisible };
            getMoreFeedItems({
                feedItemDetails: JSON.stringify(feedItemDetails)
            })
                .then(result => {
                    this.feedItemList = result.slice(0,itemsLength);
                    console.log('feed list', this.feedItemList.length);
                    this.showViewMore = this.feedItemList.length < itemsLength ? false : true;
                    this.hasNoData = this.feedItemList.length == 0 ? true : false;
                    this.isLoading = false;
                })
                .catch(error => {
                    this.isLoading = false;
                    console.log('error handleMoreFeeds-->', error);
                });
        } else{
            this.showViewMore = this.feedItemList.length < itemsLength ? false : true;
        }
    }
}