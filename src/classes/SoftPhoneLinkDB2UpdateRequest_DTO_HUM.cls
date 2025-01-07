/******************************************************************************************************************
Apex class Name    : SoftPhoneLinkDB2UpdateRequest_DTO_HUM
Version            : 1.0
Function           : DTO Class for Bulk Agent Details Sync
Created On         : August 27 2019
Test Class         : SoftPhoneSync_T_HUM

Modification Log:
*   Version             Developer                  Date                     Description
* -----------------------------------------------------------------------------------------------------------------
*    1.0                SaiSree Lakhsmi            08/27/2019             Original Version
------------------------------------------------------------------------------------------------------------------*/
public class SoftPhoneLinkDB2UpdateRequest_DTO_HUM 
{
    public String SiteName = '';
    public String DefaultSoftphoneLinkName = '';
    public String NewDefaultSoftphoneLinkName = '';
    public String AlternateSoftphoneLinkName = '';
    public String NewAlternateSoftphoneLinkName = '';
    public SoftPhoneLinkDB2UpdateRequest_DTO_HUM(String SiteName,String DefaultSoftphoneLinkName,String NewDefaultSoftphoneLinkName,String AlternateSoftphoneLinkName,String NewAlternateSoftphoneLinkName)
    {
    	this.SiteName = SiteName;
    	this.DefaultSoftphoneLinkName = DefaultSoftphoneLinkName;
    	this.NewDefaultSoftphoneLinkName = NewDefaultSoftphoneLinkName;
    	this.AlternateSoftphoneLinkName = AlternateSoftphoneLinkName;
    	this.NewAlternateSoftphoneLinkName = NewAlternateSoftphoneLinkName;
    }
}