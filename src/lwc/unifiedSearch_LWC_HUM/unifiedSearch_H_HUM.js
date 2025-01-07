/*******************************************************************************************************************************
File Name : unifiedSearch_H_HUM.JS 
Version : 1.0
Created On : 01/18/2021
Function : This file contains search template for unifiedSearch_LWC_HUM component

Modification Log: 
* Modification ID		Date				Developer Name           	Description                                         
*------------------------------------------------------------------------------------------------------------------------------
* 1.0       			01/18/2021			Akshay Pai					Original Version	
* 1.1                   10/12/2021          Lakshmi Madduri             Adding new facets, Scrolling issue fix
* 1.2                   10/19/2021          Lakshmi Madduri             Changing placement of result section
* 1.3                   10/26/2021          Lakshmi Madduri             Sort on last published date
* 1.4                   11/1/2021           Lakshmi Madduri             Scrollbar for navigations tabs in unified search window
* 1.5                   12/22/2021          Priyank Shenwai             Fixed styling and alignment as per Bootstrap 5.x OSA upgrade
****************************************************************************************************************************/
const SEARCH_TEMPLATE = `<lightning-card title="">
<style>
    .coveo-dynamic-facet-collapsed .coveo-dynamic-facet-search {
    display: none !important;
    }
    .coveo-dynamic-facet-search {
    display: block !important;
    }
    .coveo-combobox-values .coveo-dynamic-facet-value {
    display: block;
    }
</style>

<div class="slds-is-relative coveo-spinner-container">
    <lightning-spinner class="slds-spinner_container"><div role="status" class="slds-spinner slds-spinner_brand slds-spinner_medium"><span class="slds-assistive-text">Loading...</span><div class="slds-spinner__dot-a"></div><div class="slds-spinner__dot-b"></div></div></lightning-spinner>
</div>

<div class="CoveoSearchInterface" data-auto-trigger-query="true">
    <div class="CoveoAnalytics" data-search-hub="CRMUnifiedSearch"></div>
    <div class="CoveoFixedHeader" style="z-index: 90; background-color: white; font-size: 26px; padding-top: 40px; color: #0059b3;">
        <div>
            <p style="text-align:center; text-decoration: underline;">Unified Search</p>
        </div>
        <div class='coveo-search-section' style="height: 35px !important; margin-left: auto !important;  max-width: 1200px !important;">
            <div style="font-size: 15px;" class="memberIdInfo coveo-facet-column">         
                <div class="caseNumber"></div>
                <div class="intDetails"></div>
            </div>
            <div class="CoveoSearchbox" data-enable-omnibox="true"></div>
        </div>        
    </div>  
    <div class="coveo-main-section">  
        <div class="coveo-facet-column" style="max-height: calc(90vh - 200px) !important;overflow-y: auto;">
            <div class="CoveoDynamicFacetManager" data-enable-reorder="false">
                <div class="CoveoDynamicFacet"  data-title ="Source" data-field ="@source" data-tab="All" data-number-of-values="10"  data-enable-facet-search="false" data-collapsed-by-default="true" data-enable-collapse="true"></div> 
                <div class="CoveoDynamicFacet"  data-title="Guidance Package" data-field="@hu_ctp_doc" data-tab="All" data-number-of-values="10" data-enable-facet-search="false" data-collapsed-by-default="true" data-enable-collapse="true" data-value-caption='{ "true": "Yes", "false": "No" }'></div>
                <div class="CoveoDynamicFacet"  data-title="Benefit Grid" data-field="@hu_is_benefit_grid" data-tab="All" data-number-of-values="10" data-enable-facet-search="false" data-collapsed-by-default="true" data-enable-collapse="true" data-value-caption='{ "true": "Yes", "false": "No" }'></div>
                <div class="CoveoDynamicFacet"  data-title="Function" data-field="@function" data-tab="All" data-number-of-values="10" data-enable-facet-search="true" data-collapsed-by-default="true" data-enable-collapse="true" ></div>
                <div class="CoveoDynamicFacet"  data-title="Sub Function" data-field="@subfunction" data-tab="All" data-number-of-values="10" data-enable-facet-search="true" data-collapsed-by-default="true" data-enable-collapse="true"></div>
                <div class="CoveoDynamicFacet"  data-title="Platform" data-field="@platform" data-tab="All" data-number-of-values="10" data-enable-facet-search="true" data-collapsed-by-default="true" data-enable-collapse="true"></div>
                <div class="CoveoDynamicFacet"  data-title="File Type" data-field="@a_content_type" data-tab="All" data-number-of-values="10" data-enable-facet-search="true" data-collapsed-by-default="true" data-enable-collapse="true"></div>
            </div>
        </div>
        <div class="coveo-results-header">
            <div class="coveo-results-header-left">
                <div class="CoveoResultsPerPage"></div>
            </div>
            <div class="coveo-results-header-right">
                <div class="CoveoPager"></div>         
            </div>
        </div>
        <div class="coveo-results-column">
            <div class="CoveoShareQuery"></div>
            <div class="CoveoExportToExcel"></div>
            <div class="CoveoPreferencesPanel">
                <div class="CoveoResultsPreferences"></div>
                <div class="CoveoResultsFiltersPreferences"></div>
            </div>                   
            <div class="coveo-results-header">
                <div class="coveo-summary-section">
                    <span class="CoveoQuerySummary"></span>
                    <span class="CoveoQueryDuration"></span>
                </div>
                <div class="coveo-result-layout-section">
                    <span class="CoveoResultLayoutSelector"></span>
                </div>
                <div class="coveo-sort-section">
                    <span class="CoveoSort" data-sort-criteria="relevancy" data-caption="Relevance"></span>
                    <span class="CoveoSort" data-sort-criteria="@lastpublisheddate descending,@lastpublisheddate ascending" data-caption=" Published Date"  data-field="@lastpublisheddate" ></span>
                    <span class="CoveoSort" data-sort-criteria="@title descending, @title ascending" data-caption="Title"></span>
                </div>
            </div>
            <div class="CoveoHiddenQuery"></div>
            <div class="CoveoErrorReport"></div>
            <div class="resultSection" style="max-height: calc(80vh - 200px) !important;overflow-y: auto; padding-right:10px;">                
              <div class="CoveoDidYouMean"></div>
              <div class="CoveoBreadcrumb"></div>
              <div class="CoveoResultList"  data-layout="list" data-wait-animation="fade" data-auto-select-fields-to-include="true"></div> 				          
            </div>
        </div>
    </div>
</div>
</lightning-card>`
export default SEARCH_TEMPLATE;