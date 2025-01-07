import { LightningElement,api,wire,track } from 'lwc';
import getUserProfileGamification from '@salesforce/apex/CommunityGamificationController_C_HUM.getUserProfileGamification';
import humanaSupportTemplate from './userProfileGamificationActivities_LWCCommunity_HUM.html';
import go365Template from './userProfileGamificationActivitiesGo365_LWCCommunity_HUM.html';
import UserProfileGamification_SubHeader from '@salesforce/label/c.Community_UserProfileGamification_SubHeader';
import UserGamification_Heading from '@salesforce/label/c.Community_UserGamification_Heading';

import UserGamification_Like from '@salesforce/label/c.Community_User_Gamification_Like';
import UserGamification_MorePosts from '@salesforce/label/c.Community_User_Gamification_More_Posts';
import UserGamification_Earn_Per_Action from '@salesforce/label/c.Community_Gamification_Earn_Per_Action';
import UserGamification_Points from '@salesforce/label/c.Community_User_Gamification_Points';
import UserGamification_Answer from '@salesforce/label/c.Community_User_Gamification_Answer';
import UserGamification_Question from '@salesforce/label/c.Community_User_Gamification_Question';
import UserGamification_Have from '@salesforce/label/c.Community_User_Gamification_Have';
import UserGamification_Best_Answer from '@salesforce/label/c.Community_User_Gamification_Best_Answer';
import UserGamification_Write from '@salesforce/label/c.Community_User_Gamification_Write';
import UserGamification_Comments from '@salesforce/label/c.Community_User_Gamification_Comments';
import UserGamification_Good from '@salesforce/label/c.Community_User_Gamification_Good';
import UserGamification_Receive from '@salesforce/label/c.Community_User_Gamification_Receive';
import UserGamification_Liked from '@salesforce/label/c.Community_User_Gamification_Liked';
import UserGamification_Posts_Already from '@salesforce/label/c.Community_User_Gamification_Posts_Already';
import UserGamification_Answered from '@salesforce/label/c.Community_User_Gamification_Answered';
import UserGamification_Already from '@salesforce/label/c.Community_User_Gamification_Already';
import UserGamification_Received from '@salesforce/label/c.Community_User_Gamification_Received';
import UserGamification_Great from '@salesforce/label/c.Community_User_Gamification_Great';
import UserGamification_Answers from '@salesforce/label/c.Community_User_Gamification_Answers';
import UserGamification_More from '@salesforce/label/c.Community_User_Gamification_More';
import UserGamification_BestAnswer from '@salesforce/label/c.Community_User_Gamification_Best_Answers';
import UserGamification_Posts from '@salesforce/label/c.Community_User_Gamification_Posts';
import UserGamification_Likes from '@salesforce/label/c.Community_User_Gamification_Likes';
import UserGamification_Congrats_Support from '@salesforce/label/c.Community_User_Gamification_Congrats_Message_For_Support';
import UserGamification_Congrats_Commercial from '@salesforce/label/c.Community_User_Gamification_Congrats_Message_For_Commercial';
import Id from '@salesforce/user/Id';

export default class UserProfileGamificationActivities_LWCCommunity_HUM extends LightningElement {

    label={
        UserProfileGamification_SubHeader,
        UserGamification_Heading,
        UserGamification_Like,
        UserGamification_MorePosts,
        UserGamification_Earn_Per_Action,
        UserGamification_Points,
        UserGamification_Answer,
        UserGamification_Question,
        UserGamification_Have,
        UserGamification_Best_Answer,
        UserGamification_Write,
        UserGamification_Comments,
        UserGamification_Good,
        UserGamification_Receive,
        UserGamification_Liked,
        UserGamification_Posts_Already,
        UserGamification_Answered,
        UserGamification_Already,
        UserGamification_Received,
        UserGamification_Great,
        UserGamification_Answers,
        UserGamification_More,
        UserGamification_BestAnswer,
        UserGamification_Posts,
        UserGamification_Likes,
        UserGamification_Congrats_Support,
        UserGamification_Congrats_Commercial,
    };

    @api Go365;
    @track NextLevel;
    @track UserPostCount;
    @track UserCommentCount;
    @track UserLikeReceivedCount;
    @track UserCommentReceivedCount;
    @track LikeSomethingCountRequired;
    @track LikeSomethingPoints
    @track AnswerAQuestionCountRequired;
    @track AnswerAQuestionPoints;
    @track PostQuestionCountRequired;
    @track PostQuestionPoints;
    @track WriteAPostCountRequired;
    @track WriteAPostpoints;
    @track ReceiveALikeCountRequired;
    @track ReceiveALikePoints;
    @track ReceiveACommentCountRequired;
    @track ReceiveACommentPoints;
    @track WriteACommentCountRequired;
    @track WriteACommentPoints;
    @track ReceiveAnAnswerCountRequired;
    @track ReceiveAnAnswerPoints;
    @track YourAnswerMarkedBestCountRequired;
    @track YourAnswerMarkedBestPoints;
    @track isLastLevel = false;
    @track isVisible = true;
    @track URLString1;
    @track sfdcBaseURL;
    @track userID;
    userID=Id;
    
    sfdcBaseURL=window.location.href;
    
    URLString1=this.sfdcBaseURL.substring(this.sfdcBaseURL.indexOf("profile/"));

        /*
        * Method to decide the template based on the community
        */
    render() {

        if (this.URLString1.includes(this.userID.substring(0,15)))
        {
            this.isVisible = true;
        } else {
            this.isVisible = false;
        }
        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }


      /*
     * Method to get the gamification values for the User
    */

    @wire(getUserProfileGamification,{})
    wiredActivities({error, data}) {
            
            try{    
                  if(data)
                  {   
                        this.NextLevel = data.nextLevelName;
                        if(this.NextLevel.includes('Congratulations'))
                        {
                            this.isLastLevel = true;
                        }
                        this.UserPostCount = data.UserChatterActivity.PostCount;
                        this.UserCommentCount = data.UserChatterActivity.CommentCount;
                        this.UserLikeReceivedCount = data.UserChatterActivity.LikeReceivedCount;
                        this.UserCommentReceivedCount = data.UserChatterActivity.CommentReceivedCount;
                        this.LikeSomethingCountRequired = data.LikeSomethingCountRequired;
                        this.LikeSomethingPoints = data.LikeSomethingPoints;
                        this.AnswerAQuestionCountRequired = data.AnswerAQuestionCountRequired;
                        this.AnswerAQuestionPoints = data.AnswerAQuestionPoints;
                        this.PostQuestionCountRequired = data.PostQuestionCountRequired;
                        this.PostQuestionPoints = data.PostQuestionPoints;
                        this.WriteAPostCountRequired = data.WriteAPostCountRequired;
                        this.WriteAPostpoints = data.WriteAPostpoints;
                        this.ReceiveALikeCountRequired = data.ReceiveALikeCountRequired;
                        this.ReceiveALikePoints = data.ReceiveALikePoints;
                        this.ReceiveACommentCountRequired = data.ReceiveACommentCountRequired;
                        this.ReceiveACommentPoints = data.ReceiveACommentPoints;
                        this.WriteACommentCountRequired = data.WriteACommentCountRequired;
                        this.WriteACommentPoints = data.WriteACommentPoints;
                        this.ReceiveAnAnswerCountRequired = data.ReceiveAnAnswerCountRequired;
                        this.ReceiveAnAnswerPoints = data.ReceiveAnAnswerPoints;
                        this.YourAnswerMarkedBestCountRequired = data.YourAnswerMarkedBestCountRequired;
                        this.YourAnswerMarkedBestPoints = data.YourAnswerMarkedBestPoints;
                       
                  }
                  else
                  {
                  
                  }
          }catch (error) {
              console.error("Error in wire call for user Profile  Gamification:" , error);
          } 
      }
}