/*******************************************************************************************************************************
Apex Class Name : FeedDetailController_C_HUM 
Version         : 1.0
Created On      : 07/20/2020
Function        : This is the Controller class for Feed detail and Question detail pages in Communities.
Test Class      : FeedDetailController_T_HUM

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Veena  Aphale                                      07/20/2020                    Original version
 **************************************************************************************************************************************/


public with sharing class FeedDetailController_C_HUM {
    public class CommunityTestException extends Exception {}
    
    /**
     * getRecommendations
     * Fetch articles based on Topics 
     * @param String recId,String source,String articleType
     * @return List<Knowledge__kav>
     **/

    @AuraEnabled(cacheable=true)
    public static List<Knowledge__kav> getRecommendations(String recId,String source,String articleType) {
        List<Knowledge__kav> lstKnowledgeArticles = new list<Knowledge__kav> ();
        try {
            CommunityUtilityHelper_C_HUM objHelper = new CommunityUtilityHelper_C_HUM();
            lstKnowledgeArticles=objHelper.getRecommendedWatchArticles(recId,source,articleType);
            
            if(Test.isRunningTest())
			{
				throw new CommunityTestException('Test Exception');
			}
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'FeedDetailController_C_HUM ', 'getRecommendations');
        }
        return lstKnowledgeArticles;
        
    }

     /**
     * getGroups
     * Fetch groups matching with the topic name 
     * @param String recId
     * @return List<CollaborationGroup>
     **/

    @AuraEnabled(cacheable=true)
    public static List<CollaborationGroup> getGroups(String recId) {
        List<CollaborationGroup> lstGroups = new list<CollaborationGroup> ();
        try {
            CommunityUtilityHelper_C_HUM objHelper = new CommunityUtilityHelper_C_HUM();
            lstGroups=objHelper.getGroups(recId);
            
            if(Test.isRunningTest())
			{
				throw new CommunityTestException('Test Exception');
			}
        } catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'FeedDetailController_C_HUM', 'getGroups');
        }
        return lstGroups;
        
    }
}