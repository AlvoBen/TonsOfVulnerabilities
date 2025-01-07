/*******************************************************************************************************************************
Apex Class Name : CommunityDataMasking_H_HUM 
Version         : 1.0
Created On      : 06/22/2020
Function        : This class is a utility class for Masking the PHI data in Humana Communities
Test Class      : CommunityHandleTriggers_T_HUM

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                                      06/22/2020                    Original version
* Shreya Agrawal                                      08/05/2020                    Updated to ignore the masking for ETL API Profile user
* Moshitha Gunasekaran                                09/07/2021                    Updated to ignore the masking for Coach profiles as Parent for Coach Feed will be member.
**************************************************************************************************************************************/

public class CommunityDataMasking_H_HUM {
    
    List<id> ListOfCommunityIds= new List<Id>();
    List<String> UserDetails= new List<String>();
    List<PHI_Pattern__mdt> PatternListMDT= new List<PHI_Pattern__mdt>();
    
    /**
* getPatternDetails
* Fetch all pattern details from custom metadata 
* @param 
* @return Void 
**/
    
    public void getPatternDetails(){
        
        PatternListMDT = [select id,DeveloperName, MasterLabel, Pattern_Value__c,ignoreMasking__c,Priority__c from PHI_Pattern__mdt order by Priority__c ];
        List<String> CommunityList= System.Label.CommunityName_HUM.split(',');
        
        User u= [SELECT Id, LastName, FirstName, City, Street, State, PostalCode, Country,Address FROM User where id=:userinfo.getUserId()];
        if(u.LastName!=null)UserDetails.add(u.LastName);
        if(u.FirstName!=null)UserDetails.add(u.FirstName);
        if(u.City!=null)UserDetails.add(u.City);
        if(u.Street!=null)UserDetails.add(u.Street);
        if(u.State!=null)UserDetails.add(u.State);
        if(u.PostalCode!=null)UserDetails.add(u.PostalCode);
        if(u.Country!=null)UserDetails.add(u.Country);
        
        for(Network cmnty:[Select id from Network where name IN : CommunityList ]){
            ListOfCommunityIds.add(cmnty.id);
        }
        
    }
    
    /**
* dataMaskingControllerForFeedItems
* Mask the required feed item data  
* @param List<FeedItem> FeedList
* @return Void 
**/
    
    public void dataMaskingControllerForFeedItems(List<FeedItem> FeedList){
        
        Integer cnt = Integer.valueOf(System.Label.Community_User_Detail_Character_Count);
        String str='05y'; // Prefix of a directMessage Feed record Id
        try{ 
            String profileName = CommunityUtilityHelper_C_HUM.getCurrentUserProfileName(); 
            //Restricting the masking of data for the ETL API Access profile
            if(!profileName.equalsIgnoreCase(CommunityConstants_C_HUM.ETL_API_ACCESS)){
                // Fetching all the patterns and User details
                getPatternDetails();
                //For Coaching Parent record will be member so NetworkScope will be set as ALL. But masking for link should be avoided for coaching.
                //So for coaching members Networkscope All condition should not verified
                boolean isCoaching = false;
                if(profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessManagerProfile) || profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessCoachProfile))
				{
                    isCoaching = true;
                }
                for(FeedItem eachFeed:FeedList){
                    if(!String.ValueOf(eachFeed.ParentId).startsWith(str) && (ListOfCommunityIds.contains(eachFeed.NetworkScope) || (eachFeed.NetworkScope==System.Label.AllNetworks_HUM && !isCoaching))){
                        //Masking based on the patterns
                        if(!PatternListMDT.isEmpty()){
                            for(PHI_Pattern__mdt eachPattern:PatternListMDT ){
                                if(eachFeed.body!=NULL){
                                    Matcher MatchEachPatternWithBody= Pattern.compile(eachPattern.Pattern_Value__c).matcher(eachFeed.body);
                                    if(MatchEachPatternWithBody.find()){
                                        eachFeed.body=MatchEachPatternWithBody.replaceAll(System.Label.PHIMaskingString);
                                    }
                                }
                                if(eachFeed.title!=NULL){
                                    Matcher MatchEachPatternWithTitle= Pattern.compile(eachPattern.Pattern_Value__c).matcher(eachFeed.title);
                                    if(MatchEachPatternWithTitle.find()){
                                        eachFeed.title=MatchEachPatternWithTitle.replaceAll(System.Label.PHIMaskingString);
                                    }
                                }
                            }
                        }
                        //masking the user details
                        if(!UserDetails.isEmpty()){
                            for(String eachDetail:UserDetails){
                                if(eachDetail.length()>cnt){
                                    if(eachFeed.body!=NULL){
                                        Matcher MatchEachPatternWithBody= Pattern.compile('\\b('+'(?i)'+eachDetail+')\\b').matcher(eachFeed.body);
                                        
                                        if(MatchEachPatternWithBody.find()){
                                            eachFeed.body=MatchEachPatternWithBody.replaceAll(System.Label.PHIMaskingString);
                                        }
                                    }
                                    if(eachFeed.title!=NULL){
                                        Matcher MatchEachPatternWithTitle= Pattern.compile('\\b('+'(?i)'+eachDetail+')\\b').matcher(eachFeed.title);
                                        
                                        if(MatchEachPatternWithTitle.find()){
                                            eachFeed.title=MatchEachPatternWithTitle.replaceAll(System.Label.PHIMaskingString);
                                        }
                                    }
                                }
                            }    
                        }
                    }
                }
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityDataMasking_H_HUM ', 'dataMaskingControllerForFeedItems');
        }
    }
    
    /**
* dataMaskingControllerForFeedComments
* Mask the required feed comment data  
* @param List<FeedComment> FeedCommentList
* @return Void 
**/
    
    public void dataMaskingControllerForFeedComments(List<FeedComment> FeedCommentList){
        
        String strBaseURL = (System.URL.getSalesforceBaseURL().toExternalForm()).subStringAfter('https://');
        Set<Id> FeedItemIds= new Set<Id>();
        List<FeedComment> CommunityFeedComments= new List<FeedComment>();
        Integer cnt = Integer.valueOf(System.Label.Community_User_Detail_Character_Count);
        String URLPattern='(([www])|(https?://))((?!.force.com)(?!.my.site.com)(?!.site.com)(?!.go365.com)(?!.medicare.go365.com)(?!.humana.com)([-\\w]*\\.)?)(?!force.com|'+strBaseURL+')(?!my.site.com|'+strBaseURL+')(?!site.com|'+strBaseURL+')(?!go365.com|'+strBaseURL+')(?!medicare.go365.com|'+strBaseURL+')(?!humana.com|'+strBaseURL+')([-\\w]*\\.)(?!force.com|.force.com)(?!my.site.com|.my.site.com)(?!site.com|.site.com)(?!go365.com|.go365.com)(?!medicare.go365.com|.medicare.go365.com)(?!humana.com|.humana.com)(.*?)(?= |$|"|<)';
        String str='05y';
        try{ 
            String profileName = CommunityUtilityHelper_C_HUM.getCurrentUserProfileName(); 
            //Restricting the masking of data for the ETL API Access profile
            if(!profileName.equalsIgnoreCase(CommunityConstants_C_HUM.ETL_API_ACCESS)){
                // Fetching all the patterns and User details
                getPatternDetails();
                //For Coaching Parent record will be member so NetworkScope will be set as ALL. But masking for link should be avoided for coaching.
                //So for coaching members Networkscope All condition should not verified
                boolean isCoaching = false;
                if(profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessManagerProfile) || profileName.equalsIgnoreCase(GLOBAL_CONSTANT_CH_HUM.sWellnessCoachProfile))
				{
                    isCoaching = true;
                }
                for(FeedComment eachComment:FeedCommentList){
                    FeedItemIds.add(eachComment.FeedItemId);
                }
                
                Map<id,FeedItem> FeedItems = null;
                if(isCoaching)
                {
                    FeedItems = new Map<id,FeedItem>([select id, networkScope from FeedItem where id in: FeedItemIds and (networkScope in: ListOfCommunityIds)]);
                }
                else
                {
                    FeedItems = new Map<id,FeedItem>([select id, networkScope from FeedItem where id in: FeedItemIds and (networkScope in: ListOfCommunityIds or networkScope =: System.Label.AllNetworks_HUM)]);
                }
                for(FeedComment eachFeed:FeedCommentList){  
                    
                    if(FeedItems.keySet().contains(eachFeed.FeedItemId) && !String.ValueOf(eachFeed.ParentId).startsWith(str) ){
                        //Masking based on the patterns
                        if(!PatternListMDT.isEmpty()){
                            for(PHI_Pattern__mdt eachPattern:PatternListMDT ){
                                if(eachFeed.CommentBody!=NULL){
                                    if((eachPattern.DeveloperName.equals(CommunityConstants_C_HUM.COMMUNITY_MASKING_WEBSITE_URL)) && isUserModerator() ){
                                        Matcher MatchEachPatternWithBody= Pattern.compile(URLPattern).matcher(eachFeed.CommentBody);
                                        if(MatchEachPatternWithBody.find()){
                                            eachFeed.CommentBody=MatchEachPatternWithBody.replaceAll(System.Label.PHIMaskingString);
                                        }
                                    }
                                    else{
                                        Matcher MatchEachPatternWithBody= Pattern.compile(eachPattern.Pattern_Value__c).matcher(eachFeed.CommentBody);
                                        if(MatchEachPatternWithBody.find()){
                                            eachFeed.CommentBody=MatchEachPatternWithBody.replaceAll(System.Label.PHIMaskingString);
                                        }
                                    }
                                }
                                
                            }
                        }
                        //masking the user details
                        if(!UserDetails.isEmpty()){
                            for(String eachDetail:UserDetails){
                                if(eachDetail.length()>cnt){
                                    if(eachFeed.CommentBody!=NULL){
                                        Matcher MatchEachPatternWithBody= Pattern.compile('\\b('+'(?i)'+eachDetail+')\\b').matcher(eachFeed.CommentBody);
                                        
                                        if(MatchEachPatternWithBody.find()){
                                            eachFeed.CommentBody=MatchEachPatternWithBody.replaceAll(System.Label.PHIMaskingString);
                                        }
                                    }
                                }  
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'CommunityDataMasking_H_HUM ', 'dataMaskingControllerForFeedComments');
        }
    }
    
     /**
    * isUserModerator
    * Method to check whether current user is a moderator  
    * @param 
    * @return boolean
    **/
    
    public boolean isUserModerator(){
        List<PermissionSetAssignment> listPermissionSetAssignment = new List<PermissionSetAssignment>();
        Set<String> setPermissionSetNames = new Set<String> {CommunityConstants_C_HUM.COMMUNITY_MODERATOR_PERMISSIONSET ,CommunityConstants_C_HUM.COMMUNITY_DELEGATED_ADMIN_PERMISSIONSET,CommunityConstants_C_HUM.COMMUNITY_ADMIN_PERMISSIONSET };
        listPermissionSetAssignment  = [SELECT Id FROM PermissionSetAssignment WHERE AssigneeId =: UserInfo.getUserId() AND PermissionSet.Name IN :setPermissionSetNames];
        if (!listPermissionSetAssignment.isEmpty())
            return true;
        else 
            return false;    
        }
}