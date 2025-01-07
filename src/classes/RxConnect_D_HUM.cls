/************************************************************************************************************************************************************************************       
Apex class Name      : RxConnect_D_HUM
Created Date         : 11/11/2019
Function             : Data class for RxConnect_C_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Satyam Malviya								11/11/2019				   REQ-404584 - Original Version - Created new class for RX connect functionality
*  Satyam Malviya							   5/21/2020			       DF 766 Corrected Application key that have been passes to RXConnect Link Fix
************************************************************************************************************************************************************************************/
public with sharing class RxConnect_D_HUM
{
    public void init(RxConnect_C_HUM Rxclass)
    { 
        if (ApexPages.currentpage().getParameters().containsKey(GLOBAL_CONSTANT_HUM.ID3_HUM) && ApexPages.currentpage().getParameters().get(GLOBAL_CONSTANT_HUM.ID3_HUM) != NULL)
        {
            String polMemID = ApexPages.currentpage().getParameters().get(GLOBAL_CONSTANT_HUM.ID3_HUM); 
            String MemberType = GLOBAL_CONSTANT_HUM.RXLOOKUP_MEMBERIDBASE_TYPE;
            Member_ID__c memId = [SELECT id, Name, Type__c,
                                  Policy_Member__c FROM Member_ID__c WHERE Type__c =: MemberType AND Policy_Member__c =: polMemID];
            Rxclass.IdForMember = memId.Name;
        } 
    }
   public static EncryptionServiceRequest_DTO_HUM RequestStructureEncryptService(String MemberId)
    {
        try
        {
            EncryptionServiceRequest_DTO_HUM oReq = new EncryptionServiceRequest_DTO_HUM();
            EncryptionServiceRequest_DTO_HUM.Encrypt getEncrypt = new EncryptionServiceRequest_DTO_HUM.Encrypt();
            getEncrypt.Value = MemberId;
			getEncrypt.Application = GLOBAL_CONSTANT_HUM.RXConnect; 
            oReq.Encrypt = getEncrypt;
            return oReq;    
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'RxConnect_D_HUM', 'RequestStructureEncryptService');
            return null;
        }
    }
    public static String parseEncrypt(EncryptionServiceResponse_DTO_HUM oResponseEncrypt)
    {
        String sEncryptValue = '';
        if(oResponseEncrypt != null && oResponseEncrypt.EncryptResponse != null && oResponseEncrypt.EncryptResponse.EncryptResult != null && oResponseEncrypt.EncryptResponse.EncryptResult.EncryptValue != null)
        {
            sEncryptValue = oResponseEncrypt.EncryptResponse.EncryptResult.EncryptValue;
        }
        return sEncryptValue;
    }
}