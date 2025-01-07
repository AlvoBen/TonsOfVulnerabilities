/************************************************************************************************************************************************************************************       
Apex Class Name  : CaseMentorDocuments_LDTO_Hum.cls 
Function         : DTO for MentorDocuments for related to Classification and Intent for logged in User
Test Class       : QuickStart_LT_HUM

**Modification Log
* Developer Name          Code Review #               Date                       Description
* Ajay Chakradhar            -----                 09/17/2023                 Original Version
************************************************************************************************************************************************************************************/
public class CaseMentorDocuments_LDTO_Hum {
    
    public Id idClassificationType{get; set;} 
    public String sBusinessGroup{get; set;}
    public String sClassificationName{get; set;}
    public String sIntentName{get; set;}
    public String sUserProfileName{get; set;}
    
    public CaseMentorDocuments_LDTO_Hum(Id idClassificationType, String sBusinessGroup, String sClassificationName, String sIntentName, String sUserProfileName){
        this.idClassificationType = idClassificationType;
        this.sBusinessGroup = sBusinessGroup;
        this.sClassificationName = sClassificationName;
        this.sIntentName = sIntentName;
        this.sUserProfileName = sUserProfileName;
    }
}