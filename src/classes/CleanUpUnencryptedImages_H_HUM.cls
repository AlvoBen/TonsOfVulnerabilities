/*****************************************************************************************************************************************************************************
Class Name      : CleanUpUnencryptedImages_H_HUM
Version         : 1.0
Created On      : 11/01/2016
Function        : Helper class for Batch class to encrypt all unencrypted file data
Test Class      : CleanUpUnencryptedImages_T_HUM

 Modification Log: 
* Developer Name          Code Review              Date                           Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar                                     11/01/2016                     Original version
* Santhi Mandava          348723                   04/02/2019                     Cleanup cipher cloud references
*********************************************************************************************************************************************************************************/

public class CleanUpUnencryptedImages_H_HUM
{
   /*
    *  This method is used to encrypt all unencrypted file data from given List<Attachment>
    *  @name   getFileEncrypted
    *  @param  List<Attachment> - an instance of Attachment records which data has to be encrypted
    *  @return List<Attachment> - returns encrypted list of Attachment records
    *  @throws exception NA
    */ 
    public static List<Attachment> getFileEncrypted(List<Attachment> lstAttachment)
    {
        List<Attachment> lstAttach = new List<Attachment>();
        
        if(lstAttachment != null)
        {
            for(Integer i = 0 ; i < lstAttachment.size() ; i++)
            {
                lstAttach.add(new Attachment(
                   Id = lstAttachment[i].Id,
                   Body = lstAttachment[i].Body)
                );
            }
        }
        else
        {
            lstAttach = null;
        }
        return lstAttach;
    }
}