import { LightningElement,api } from 'lwc';

import humanaSupportTemplate from './articleDetailGamification_LWCCommunity_HUM.html';
import go365Template from './articleDetailGamificationGo365_LWCCommunity_HUM.html';
import extrapointsLabel from '@salesforce/label/c.Community_Gamification_Extra_Points';
import likePostLabel from '@salesforce/label/c.Community_Gamification_Like_a_Post';
import earnPerActionLabel from '@salesforce/label/c.Community_Gamification_Earn_Per_Action';
import plusPointsLabel from '@salesforce/label/c.Community_Gamification_Plus_5_Points';
import writeCommentLabel from '@salesforce/label/c.Community_Gamification_Write_Comment';
import plusPointsLabel1 from '@salesforce/label/c.Community_Gamification_Plus_15_Points';


export default class ArticleDetailGamification_LWCCommunity_HUM extends LightningElement {
  label={
    extrapointsLabel,
    likePostLabel,
    earnPerActionLabel,
    plusPointsLabel,
    writeCommentLabel,
    plusPointsLabel1,
};
    @api Go365;

    /*
     * Method to display required template based on Community
    */
    render() {
        return this.Go365 == true ? go365Template : humanaSupportTemplate;
      }


}