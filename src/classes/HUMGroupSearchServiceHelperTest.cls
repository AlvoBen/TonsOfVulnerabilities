/**************************************************************************** 
Apex Class Name  : HUMGroupSearchServiceHelperTest 
Version          : 2.0  
Created Date     : August 5 2015 
Description      : This class is used as test class for HUMGroupSearchServiceHelper
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shreya CHoodamani                                08/05/2014                     Original Version
* Ranjeeth N                                       08/27/2015                     updated test class
***************************************************************************************************/

@isTest
private Class HUMGroupSearchServiceHelperTest 
{
    static testMethod void testHUMIntGroupSearchServiceHelper()
    {
        User user = HUMTestDataHelperTest.createTestUser('TestUserGrpSrch1','Customer Care Specialist'); 
        insert user;
        system.assert(user.Id != null);
        System.runas(user) 
        {
            Test.StartTest(); 
            
                HUMGroupSearchServiceHelper grpSearchService = new HUMGroupSearchServiceHelper();
                HUMGroupSearchServiceHelper.RequestGroupSearch requestGroupSearch = new HUMGroupSearchServiceHelper.RequestGroupSearch();
                HUMGroupSearchServiceHelper.ResponseGroupSearch responseGroupSearch = new HUMGroupSearchServiceHelper.ResponseGroupSearch();
                HUMGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO arrayGrpExchngDtl = new HUMGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO();
                HUMGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO arrayGrpExchngInfoDtl= new HUMGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO();
                HUMGroupSearchServiceHelper.GroupExchangeDetailDTO grpExDetail = new HUMGroupSearchServiceHelper.GroupExchangeDetailDTO();
                system.assert(grpExDetail.GroupId == null); 
                HUMGroupSearchServiceHelper.GroupExchangeInfoDetailDTO grpExInfoDetail = new HUMGroupSearchServiceHelper.GroupExchangeInfoDetailDTO();
                system.assert(grpExInfoDetail.GroupId == null); 
                HUMGroupSearchServiceHelper.RequestGroupExchangeDTO reqGrpExc = new HUMGroupSearchServiceHelper.RequestGroupExchangeDTO();
                HUMGroupSearchServiceHelper.RequestGroupSearch reqGrpSeach = new HUMGroupSearchServiceHelper.RequestGroupSearch();
                system.assert(reqGrpSeach.GroupID == null); 
                HUMGroupSearchServiceHelper.ResponseGroupExchangeDTO  respGrpExc = new HUMGroupSearchServiceHelper.ResponseGroupExchangeDTO();
                HUMGroupSearchServiceHelper.ResponseGroupSearch resGrpSeach = new HUMGroupSearchServiceHelper.ResponseGroupSearch();
                system.assert(resGrpSeach.GroupName == null);  
                HUMGroupSearchServiceHelper.LinkedGroupDto  grplink  = new HUMGroupSearchServiceHelper.LinkedGroupDto();
                system.assert(grplink != null);
                HUMGroupSearchServiceHelper.GroupDto grppdto  = new HUMGroupSearchServiceHelper.GroupDto();
                system.assert(grppdto    != null);
                HUMGroupSearchServiceHelper.ArrayOfGroupOfferDto arrgroffer = new HUMGroupSearchServiceHelper.ArrayOfGroupOfferDto ();
                system.assert(arrgroffer!= null);
                HUMGroupSearchServiceHelper.GroupOfferDto  groffer = new HUMGroupSearchServiceHelper.GroupOfferDto ();
                system.assert(groffer != null);
                HUMGroupSearchServiceHelper.GroupCriteriaDTO   gcriteria = new HUMGroupSearchServiceHelper.GroupCriteriaDTO  ();
                system.assert(gcriteria != null);
                 HUMGroupSearchServiceHelper.GroupCriteriaBaseDTO  grcritbase= new HUMGroupSearchServiceHelper.GroupCriteriaBaseDTO ();
                system.assert(grcritbase!= null);
               
                HUMGroupSearchServiceHelper.GroupBenefitInfoDetailDTO gbenfitoff = new HUMGroupSearchServiceHelper.GroupBenefitInfoDetailDTO ();
                system.assert(gbenfitoff != null);
                HUMGroupSearchServiceHelper.RequestGroupSearch reqoff= new HUMGroupSearchServiceHelper.RequestGroupSearch ();
                system.assert(reqoff!= null);
                HUMGroupSearchServiceHelper.GroupExchangeInfoDetailDTO rexchnage = new HUMGroupSearchServiceHelper.GroupExchangeInfoDetailDTO ();
                system.assert(rexchnage != null);
               
               HUMGroupSearchServiceHelper.GroupDivisionDto grpdivoff= new HUMGroupSearchServiceHelper.GroupDivisionDto ();
               system.assert(grpdivoff!= null);
               
               HUMGroupSearchServiceHelper.GroupPlanDto grplann=  new HUMGroupSearchServiceHelper.GroupPlanDto ();
               system.assert(grplann!= null);
               
               HUMGroupSearchServiceHelper.ResponseGroupSearch respse=  new HUMGroupSearchServiceHelper.ResponseGroupSearch ();
               system.assert(respse!= null);
               
               HUMGroupSearchServiceHelper.ResponseGroupBenefitDTO respdto=  new HUMGroupSearchServiceHelper.ResponseGroupBenefitDTO ();
               system.assert(respdto!= null);
                
                HUMGroupSearchServiceHelper.ArrayOfGroupProductDto  arrdto=  new HUMGroupSearchServiceHelper.ArrayOfGroupProductDto ();
                system.assert(arrdto!= null);
                
                 HUMGroupSearchServiceHelper.GroupProductDto gpdto =  new HUMGroupSearchServiceHelper.GroupProductDto ();
                system.assert(gpdto != null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupProgramDto arraygrpname=  new HUMGroupSearchServiceHelper.ArrayOfGroupProgramDto ();
                system.assert(arraygrpname!= null);
                HUMGroupSearchServiceHelper.GroupProgramDto grprgm=  new HUMGroupSearchServiceHelper.GroupProgramDto ();
                 system.assert(grprgm!= null);
                 HUMGroupSearchServiceHelper.RequestGroupExchangeDTO RGEroup=  new HUMGroupSearchServiceHelper.RequestGroupExchangeDTO ();
                 system.assert(RGEroup!= null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupPhoneDto arayphonep=  new HUMGroupSearchServiceHelper.ArrayOfGroupPhoneDto ();
                 system.assert(arayphonep!= null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO araexchan=  new HUMGroupSearchServiceHelper.ArrayOfGroupExchangeDetailDTO ();
                 system.assert(araexchan!= null);
                 HUMGroupSearchServiceHelper.MasteredGroupResponseDto mstrgrp=  new HUMGroupSearchServiceHelper.MasteredGroupResponseDto ();
                 system.assert(mstrgrp!= null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupBenefitInfoDetailDTO  aryinfo=  new HUMGroupSearchServiceHelper.ArrayOfGroupBenefitInfoDetailDTO  ();
                 system.assert(aryinfo!= null);
                 HUMGroupSearchServiceHelper.GroupAddressDto  gaddr=  new HUMGroupSearchServiceHelper.GroupAddressDto ();
                 system.assert(gaddr!= null);
                 HUMGroupSearchServiceHelper.ArrayOfLinkedGroupDto arraylinked=  new HUMGroupSearchServiceHelper.ArrayOfLinkedGroupDto ();
                 system.assert(arraylinked!= null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupDivisionDto arraygrpdto=  new HUMGroupSearchServiceHelper.ArrayOfGroupDivisionDto ();
                 system.assert(arraygrpdto!= null);
                 HUMGroupSearchServiceHelper.GroupBenefitCriteriaDTO araybenftg=  new HUMGroupSearchServiceHelper.GroupBenefitCriteriaDTO ();
                 system.assert(araybenftg!= null);
                 HUMGroupSearchServiceHelper.GroupExchangeDetailDTO oGEdtio=  new HUMGroupSearchServiceHelper.GroupExchangeDetailDTO ();
                 system.assert(oGEdtio!= null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO  odtoarray=  new HUMGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO ();
                 system.assert(odtoarray!= null);
                 HUMGroupSearchServiceHelper.MasteredGroupCriteriaDto omstrgrp=  new HUMGroupSearchServiceHelper.MasteredGroupCriteriaDto ();
                 system.assert(omstrgrp!= null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupBenefitDto oarraydto=  new HUMGroupSearchServiceHelper.ArrayOfGroupBenefitDto ();
                 system.assert(oarraydto!= null);
                 HUMGroupSearchServiceHelper.GroupBenefitDto obenfit=  new HUMGroupSearchServiceHelper.GroupBenefitDto ();
                 system.assert(obenfit!= null);
                 HUMGroupSearchServiceHelper.GroupPhoneDto oaphonedto=  new HUMGroupSearchServiceHelper.GroupPhoneDto ();
                 system.assert(oaphonedto!= null);
                 HUMGroupSearchServiceHelper.ArrayOfGroupPlatformDto oarrypltf=  new HUMGroupSearchServiceHelper.ArrayOfGroupPlatformDto();
                 system.assert(oarrypltf!= null);
                 HUMGroupSearchServiceHelper.GroupPlatformDto oapltfmdto=  new HUMGroupSearchServiceHelper.GroupPlatformDto ();
                 system.assert(oapltfmdto!= null);
        
                
            
            Test.stopTest(); 
        }
    }
}