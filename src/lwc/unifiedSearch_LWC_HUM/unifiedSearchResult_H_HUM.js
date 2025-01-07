/*******************************************************************************************************************************
File Name : unifiedSearchResult_H_HUM.JS 
Version : 1.0
Created On : 01/18/2021
Function : This file contains result template for unifiedSearch_LWC_HUM component

Modification Log: 
* Modification ID		Date				Developer Name           	Description                                         
*------------------------------------------------------------------------------------------------------------------------------
* 1.0       			01/18/2021			Akshay Pai					Orginal Version	
****************************************************************************************************************************/
const RESULT_TEMPLATE = `<div class="coveo-result-frame">
<div class="coveo-result-row" style="margin-bottom: 7px;">
    <div class="coveo-result-cell" style="vertical-align: top; width: 32px;">
        <span class="CoveoIcon" data-small="true" data-with-label="false" ></span>
    </div> 
    <div class="coveo-result-cell" style="vertical-align: top; font-size: 14px; padding-left: 10px;">
        <div class="coveo-result-row" style="font-size: 15px; margin: 0;" ></div>
        <a class="CoveoResultLink" data-open-in-sub-tab="true"></a>
    </div>
    <div class="coveo-result-cell" style="float: right; vertical-align: top;"> 
        <div class="CoveoFieldValue" data-text-caption="Published Date:" data-field="@lastpublisheddate"  style="float: right; vertical-align: top; font-size: 14px;color: #0059b3;" data-facet="null" ></div>
        <br/>
        <div class="CoveoSalesforceQuickview" data-quickview-url="UnifiedSearchQuickView_VF_HUM" data-use-advanced-quickview="true" style ="text-align: right;" ><button>Quick View</button></div>
    </div>
</div>

<div class="coveo-result-row">
    <div class="coveo-result-cell">
        <span class="CoveoExcerpt" ></span>
    </div>
</div> 
<div class="coveo-result-row">
    <div class="coveo-result-row">
        <div class="coveo-result-cell">
            <div class="CoveoFieldValue" data-text-caption="Source:" data-field="@source" data-facet="null" ></div>
        </div>
    </div>
</div>
</div>`

export default RESULT_TEMPLATE;