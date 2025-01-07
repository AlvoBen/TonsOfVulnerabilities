/**************************************************************************** 
Apex Class Name  : MemberPreferencesLinkResponse_H_HUM 
Version          : 
Created Date     : 03/05/2018
Function         : Response helper for Encrypt Rest Service EncryptResult
Test Class       : MemberPreferencesLink_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review          Date                       Description
*---------------------------------------------------------------------------------------------------
*  Shivam Anand           197530               03/05/2018           REQ349581- Add Member Preferences Link to CRM Service Person Account Page
*  Vijaya Koriprolu       197530              03/06/2018           REQ - 349580- Add Member Preferences Link on CRM Policy Member Page
***************************************************************************************************/

public class MemberPreferencesLinkResponse_H_HUM
{
    public static MemberPreferencesLinkResponse_H_HUM parse(String response) 
    {
        return (MemberPreferencesLinkResponse_H_HUM) System.JSON.deserialize(response, MemberPreferencesLinkResponse_H_HUM.class);
    }

   public EncryptResponse EncryptResponse;
    
   public class EncryptResponse
   {
    public EncryptResult EncryptResult;
   }
    
    
    /*
     * EncryptValue: Encrypted Member Gen Key      
    */
   public class EncryptResult
    {
     public String EncryptValue;    
       
    }
 }