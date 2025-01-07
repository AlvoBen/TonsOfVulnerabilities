/**************************************************************************************************************************************************
@Apex class Name    : MTVResponseWrapper_DTO_HUM.cls
@Function           : This class serves as DTO class for MTV remark table from MemberPlan Page
@Version            : 1.0
@test class         : MTVResponseWrapper_DTO_HUM.cls

Modification Log:
* Developer Name         Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kajal Namdev                                  07/01/2021                      
************************************************************************************************************************************************* */
public class MTVResponseWrapper_DTO_HUM 
{
    @AuraEnabled public List<MTVMembers_Remarks_DTO_HUM> mtvMemberPremarkList;
    @AuraEnabled public boolean isOnSwitch;
    
}