/*****************************************************************************************
Apex class Name    : MemberIconsConfiguration_S_HUM 
Version            : 1.0
Function           : Helper class to MemberIconsConfiguration_C_AHT_HUM 
                     This class is used to view/edit the list of member icons
Created Date       : 11/01/2019
Test Class         : MemberIconsConfiguration_ST_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                      11/01/2019             Original Version
 *   Amit Agarwal                                        06/25/2020             User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *   Y Sunil Kumar Reddy                                 02/12/2021             User Story 1874757: PR00091574 - MF 1 - Quality - Relabel "Open Cases" Repeat Caller Attributes and Interaction Count Issue Fix (CRM) 
 *   Hima Bindu Ramayanam                                11/13/2023             US 5129477: DevSecOps Security Misconfiguration/DF-11852(Not from CRM Qtest)
 ****************************************************************************************************************************/
public with sharing class MemberIconsConfiguration_S_HUM 
{
    
    /*
    * This is used to save icon details
    * @param  MemberIcon_DTO_HUM - Selected icon custom setting object
              String - Selected pages
              String - Selected permission sets
              String - Selected Profiles
    * @return N/A
    */
    public static void Save(MemberIcon_DTO_HUM objIconDto,String sPages,String sPermissions, String sProfiles)
    {
        try
        {
            MemberIcon_HUM__c objIcon = objIconDto.getMemberIconCustomSetting();
            objIcon.PageName__c = String.join(sPages.trim().split('\\s*,\\s'), ',');
            objIcon.PermissionSet__c = String.join(sPermissions.trim().split('\\s*,\\s'), ',');
            objIcon.Profile__c =String.join(sProfiles.trim().split('\\s*,\\s'), ',');
            update objIcon;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberIconsConfiguration_S_AHT_HUM','Save');
        }
    }

    /*
    * This is used to retrieve all icon documents
    * @param  Map<String,MemberIcon_DTO_HUM > - icon custom setting map
    * @return Map<String,List<String>> - icons with document details
    */
    public static Map<String,List<String>> getDocumentDetails(Map<String,MemberIcon_DTO_HUM > mapIconsDto )
    {
        String sFolderId = MemberIconsConfiguration_D_HUM.getDocumentFolderId();
        List<Document> lstDocuments = MemberIconsConfiguration_D_HUM.getDocuments(sFolderId);
        Map<String,List<String>> mapIconImages = new Map<String,List<String>>();
        String sDocurl = '';
        for(MemberIcon_DTO_HUM objIcon : mapIconsDto.values())
        {
            String sDocId;
            sDocurl = 'https://'+URL.getSalesforceBaseUrl().getHost()+'/p/doc/DocumentUploadUi?id=';
            for(Document objDoc : lstDocuments)
            {
                if(objIcon.sDocumentName == objDoc.Name)
                {
                    sDocId =  objDoc.id;
                    break;
                }
            }
            sDocurl = sDocurl +sDocId + '&pid='+sFolderId;           
            mapIconImages.put(objIcon.sName,new List<String>{sDocId ,sDocurl});
        }
        return mapIconImages;
    }

    /*
    * This is used to transfor the DTO to Custom Setting
    * @param  void 
    * @return Map<String,MemberIcon_DTO_HUM>
    */
    public static Map<String,MemberIcon_DTO_HUM> getAllIconsData()
    {

        Map<String,MemberIcon_DTO_HUM> mapIconsDto = new Map<String,MemberIcon_DTO_HUM>();

        Map<String,MemberIcon_HUM__c> mapIcons = MemberIcon_HUM__c.getAll();

        for (String memIconName : mapIcons.keySet())
        {
            MemberIcon_HUM__c memIcon = mapIcons.get(memIconName);

            mapIconsDto.put(memIconName, new MemberIcon_DTO_HUM(memIcon));

        }

        return mapIconsDto;
    }
    
    
    /*
    * This is used to transfor the DTO to Custom Setting
    * @param  void 
    * @return Map<String,RepeatCallerConfiguration_DTO_HUM>
    */
    public static Map<String,RepeatCallerConfiguration_DTO_HUM> getRepeatCallerSettings()
    {

        Map<String,RepeatCallerConfiguration_DTO_HUM> mapRCDTO = new Map<String,RepeatCallerConfiguration_DTO_HUM>();

        Map<String,RepeatCallerSettings_HUM__c> mapRCSettings = RepeatCallerSettings_HUM__c.getAll();

        for (String sLobName: mapRCSettings.keySet())
        {
            RepeatCallerSettings_HUM__c objRC = mapRCSettings.get(sLobName);

            mapRCDTO.put(sLobName, new RepeatCallerConfiguration_DTO_HUM(objRC));

        }
        return mapRCDTO;
    }
    
     /*
    * This is used to save icon details
    * @param  MemberIcon_DTO_HUM - Selected icon custom setting object
              String - Selected pages
              String - Selected permission sets
              String - Selected Profiles
    * @return N/A
    */
    public static void sveRCSettings(RepeatCallerConfiguration_DTO_HUM objRCDto,String sInteractionOrigin,String sInteractingWithType)
    {
        try
        {
            CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('5129477');
            RepeatCallerSettings_HUM__c objRC = objRCDto.getRepeatCallerCustomSettings();
            objRC.Interaction_Origin__c = String.join(sInteractionOrigin.trim().split('\\s*,\\s'), ',');
            objRC.Interacting_With_type__c = String.join(sInteractingWithType.trim().split('\\s*,\\s'), ',');
            if(objSwitch?.IsON__c == true){
                if(Schema.sObjectType.RepeatCallerSettings_HUM__c.fields.Interaction_Origin__c.isUpdateable() && Schema.sObjectType.RepeatCallerSettings_HUM__c.fields.Interacting_With_type__c.isUpdateable()){
                    update objRC;
                }
            } else update objRC;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberIconsConfiguration_S_AHT_HUM','Save');
        }
    }
     /*
    * This is used to get InteractingWithType Values
    * @param  N/A
    * @return List<String>
    */
    public static  List<String> getInteractingWithTypeValues()
    {
        List<String> lstValues = new List<String>();
        Schema.DescribeFieldResult fieldResult =    Interaction__c.Interacting_With_type__c.getDescribe();
        List<Schema.PicklistEntry> ple = fieldResult.getPicklistValues();
        for( Schema.PicklistEntry pickListVal : ple)
        {
            lstValues.Add(pickListVal.getLabel());
        }     
        return lstValues;
    }
     /*
    * This is used to get InteractionOrigin Values
    * @param  N/A
    * @return List<String>
    */
    public static List<String> getInteractionOriginValues()
    {
        List<String> lstValues = new List<String>();
        Schema.DescribeFieldResult fieldResult = Interaction__c.Interaction_Origin__c.getDescribe();
        Schema.DescribeFieldResult caseResult = case.origin.getDescribe();
        List<Schema.PicklistEntry> ple = fieldResult.getPicklistValues();
        for( Schema.PicklistEntry pickListVal : ple)
        {
            lstValues.Add(pickListVal.getLabel());
        }     
        for( Schema.PicklistEntry ref : caseResult.getPicklistValues())
        {
            lstValues.Add(ref.getLabel());
        }  
        lstValues.sort();
        return new List<String>(new Set<String>(lstValues));
    }
}