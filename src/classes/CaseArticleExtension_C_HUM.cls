/**
@Author      : Prasad Jandhyala
@name        : CaseArticleExtension_C_HUM
@CreateDate  : 11/02/2017
@Description : This Class serves As the controller For CaseArticle_CMP_HUM component
@Version     : 1.0
@reference   : CaseArticle_CMP_HUM

Modification Log:
* Developer Name           Code Review                 Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Prasad Jandhyala         149162                    11/02/2017                     Original version
* Pradeepkumar Dani     149162                    11/17/2017                       re-factoring
* Mitra Bharadwaj                                 1/29/2021                        US-1830387 Long running Case Management component - VFRemote- CaseArticleExtension_C_HUM invoke(getCaseArticles)
********************************************************************************************************************************/

public with sharing class CaseArticleExtension_C_HUM
{
    public string columnName{ get;set; }
    public string columnHeader{ get;set; }

    /**
    *  Constructor for the class
    *  @name CaseArticleExtension_C_HUM
    *  @param NA
    *  @return void
    *  @throws NA
    */
    public CaseArticleExtension_C_HUM()
    {
        columnName = json.serialize(new List<String>{ 'AttachDate', 'Action' ,'ArtcleTitle' ,'ModifiedDate', 'CreatedBy' , 'Summary' });
        columnHeader = json.serialize(new List<String>{ 'AttachDate', 'Action' ,'Article Title' ,'Modified Date', 'Created By' , 'Summary' });
    }

    /**
    * This  Remote Method is invoked from Component and generates list of Case Articles associated to the case.
    *
    * @param    sCaseRecordId                       Case record ID
    * @return                                       List of Case Articles records
    */
    @RemoteAction
    public static List<CaseArticle_DTO_HUM> getCaseArticles(String sCaseRecordId)
    {
        return CaseArticleExtension_C_HUM.getAtricleDTOList(sCaseRecordId);
    }

    /**
    * This method generates list of Case Articles associated to the case.
    *
    * @param    sCaseRecordId                       Case record ID
    * @return   list<CaseArticle_DTO_HUM>           List of Case Articles records
    */
    public static list<CaseArticle_DTO_HUM> getAtricleDTOList(String sCaseRecordId)
    {
        list<CaseArticle_DTO_HUM> lstCaseArticleDTO = new list<CaseArticle_DTO_HUM>();
        list<CaseArticle> caseArticlesList = [SELECT Id, CaseId, KnowledgeArticleId, KnowledgeArticleVersionId,KnowledgeArticleVersion.Summary, KnowledgeArticleVersion.VersionNumber, KnowledgeArticleVersion.LastModifiedDate, KnowledgeArticleVersion.title, CreatedBy.name, CreatedBy.Id, CreatedDate FROM CaseArticle where caseId = :sCaseRecordId order by CreatedDate desc];        // Added KnowledgeVersion fields in the same query
    
        for (CaseArticle oCaseArticle : caseArticlesList)
        {
            CaseArticle_DTO_HUM oCaseArticleDTO = new CaseArticle_DTO_HUM();
        
        
                oCaseArticleDTO.ModifiedDate = oCaseArticle.KnowledgeArticleVersion.LastModifiedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                oCaseArticleDTO.AttachDate = oCaseArticle.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                String articleLink = oCaseArticle.KnowledgeArticleVersionId + CaseDetailConstants_HUM.sArticleKavVersion + oCaseArticle.KnowledgeArticleVersion.VersionNumber + CaseDetailConstants_HUM.sArticleCaseIdURLParam + sCaseRecordId;
                oCaseArticleDTO.ArtcleTitle = oCaseArticle.KnowledgeArticleVersion.title + CaseDetailConstants_HUM.sSrcUp + articleLink;
                oCaseArticleDTO.Summary =oCaseArticle.KnowledgeArticleVersion.Summary;
                String kavTitle = oCaseArticle.KnowledgeArticleVersion.title;
                oCaseArticleDTO.Action = CaseDetailConstants_HUM.sArticleDetach + CaseDetailConstants_HUM.sArticleDeleteRecord + oCaseArticle.id;
            
            oCaseArticleDTO.CreatedBy = oCaseArticle.CreatedBy.Name + CaseDetailConstants_HUM.sSrcUp + oCaseArticle.CreatedById;
            lstCaseArticleDTO.add(oCaseArticleDTO);
        }
        return lstCaseArticleDTO;
    }

    /**
    * This Remote Method is invoked from Component and generates list of Case Articles associated to the case.
    *
    * @param    sCaseRecordId                       Case record ID
    * @return                                       List of Case Articles records
    */
    @RemoteAction
    public static List<CaseArticle_DTO_HUM> updatedCaseArticles(String sCaseRecordId, string detachId)
    {
        try
        {
            if (string.isNotBlank(detachId))
            {
                CaseArticle ca = new CaseArticle(id = detachId);
                delete ca;
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CaseArticleExtension_C_HUM', 'updatedCaseArticles');
        }
        return CaseArticleExtension_C_HUM.getAtricleDTOList(sCaseRecordId);
    }
}