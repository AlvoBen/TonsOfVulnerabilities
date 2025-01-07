export const ClaimDetails = [
  [
    {
      "accordian": true,
      "emptycell": true,
      "isActionColumn": true,
      "compoundx": false,
      "label": "",
      "value": "",
      "fieldName": "Id",
      "disabled": "No"
    },
    {
      "compoundx": true,
      "label": "Claim Dates",
      "headerHelpText":"Last Processed Date : The most recent processing of a claim will always display as the default view. Select other process date options to see previous processing details.<br> Begin DOS : Begin Date of Service <br> End DOS : End Date of Service",
      "value": "Dat",
      "compoundvalue": [
        {
          "text": true,
          "label": "Last Proc Date",
          "fieldName": "sLastProcessDate",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Begin DOS",
          "value": "10",
          "fieldName": "sServiceStartDate",
          "disabled": "No"
        }, {
          "text": true,
          "label": "End DOS",
          "value": "10",
          "fieldName": "sServiceEndDate",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Procedure",
      "headerHelpText": "EX : Explanation <br> Proc Cd : CPT/HCPCS Procedure Code <br> Proc Desc : CPT/HCPS Procedure Description",
      "value": "Proc",
      "compoundvalue": [
        {
          "text": true,
          "label": "EX",
          "value": "11",
          "fieldName": "sExLineItem",
          "disabled": "No",
          "hidden":false,
        },
        { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "sServiceCode", "value": "10", "disabled": "No"},
        { "link": true, "label": "Proc Cd",  "fieldName": "sServiceCode", "value": "10", "disabled": "No", "actionName": "SUBTAB_EXTERNAL_URL", "linkToChange": "sServiceCodeLink",    "navToItem": "sServiceCodeLink","pageName": "Claim_Details_LWC"},
        {
          "text": true,
          "label": "Proc Desc",
          "value": "10",
          "fieldName": "sServiceDesc",
          "disabled": "No"
        }]
    }, {
      "compoundx": true,
      "label": "Amounts",
      "headerHelpText": "Billed : Billed Charge Amount <br> Allowed : Allowed Amount <br> Discount : Plan/Provider Discount Amount <br> Excluded : Excluded Amount <br> Denied : Denied/Benefit Exclusion Amount",
      "value": "Pend",
      "compoundvalue": [
        {
          "text": true,
          "label": "Billed",
          "fieldName": "sChargeAmt",
          "value": "11",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Allowed",
          "value": "10",
          "fieldName": "sBenAllowAmt",
          "disabled": "No"
        }, {
          "text": true,
          "label": "Discount",
          "value": "10",
          "fieldName": "sProvWriteOff",
          "disabled": "No"
        },
		{
          "text": true,
          "label": "Excluded",
          "value": "10",
          "fieldName": "sExcludeAmt",
          "disabled": "No"
        },{
          "text": true,
          "label": "Denied",
          "value": "10",
          "fieldName": "sBenDenyAmt",
          "disabled": "No"
        }]
    },{
      "compoundx": true,
      "label": "Copay/Coins Amounts",
      "headerHelpText": "Ded : Deductible <br> Coins : Coinsurance",
      "value": "otherAmt",
      "compoundvalue": [
		{
          "text": true,
          "label": "Copay",
          "value": "11",
          "fieldName": "sCopayAmt",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Ded",
          "fieldName": "sDeductAmt",
          "value": "10",
          "disabled": "No"
        },
        {
          "text": true,
          "label": "Coins",
          "value": "10",
          "fieldName": "sCoInsAmt",
          "disabled": "No"
        }]
    },{
		"compoundx": true,
      "label": "Paid Amounts",
      "headerHelpText1": "Mbr Resp : Member<br>Responsible <br> Paid : Plan Paid Amount <br> OIC Paid : Other Insurance<br>Carriers <br>Amount",
      "value": "mbrAmt",
      "compoundvalue": [	
		 {
          "text": true,
          "label": "Mbr Resp",
          "value": "11",
          "fieldName": "sMbrRespAmt",
          "disabled": "No"
        },
		{
          "text": true,
          "label": "Paid",
          "value": "10",
          "fieldName": "sPaidAmt",
          "disabled": "No"
        },{
          "text": true,
          "label": "OIC Paid",
          "value": "10",
          "fieldName": "sCobPaidAmt",
          "disabled": "No"
        }]
        
	}
  ]];
  export const denClaimDetails = [
    [
      {
        "accordian": true,
        "emptycell": true,
        "isActionColumn": true,
        "compoundx": false,
        "label": "",
        "value": "",
        "fieldName": "Id",
        "disabled": "No"
      },
      {
        "compoundx": true,
        "label": "Claim Dates",
        "headerHelpText":"Last Processed Date : The most recent processing of a claim will always display as the default view. Select other process date options to see previous processing details.<br> Begin DOS : Begin Date of Service <br> End DOS : End Date of Service",
        "value": "Dat",
        "compoundvalue": [
          {
            "text": true,
            "label": "Last Proc Date",
            "fieldName": "sLastProcessDate",
            "value": "11",
            "disabled": "No"
          },
          {
            "text": true,
            "label": "Begin DOS",
            "value": "10",
            "fieldName": "sServiceStartDate",
            "disabled": "No"
          }, {
            "text": true,
            "label": "End DOS",
            "value": "10",
            "fieldName": "sServiceEndDate",
            "disabled": "No"
          }]
      }, {
        "compoundx": true,
        "label": "Procedure",
        "headerHelpText": "EX : Explanation <br> Proc Cd : CPT/HCPCS Procedure Code <br> Proc Desc : CPT/HCPS Procedure Description",
        "value": "Proc",
        "compoundvalue": [
          {
            "text": true,
            "label": "EX",
            "value": "11",
            "fieldName": "sExLineItem",
            "disabled": "No"
          },
          { "Id": true, "link": false, "icon": false, "hidden": true, "label": "Id", "fieldName": "sServiceCode", "value": "10", "disabled": "No"},
          { "link": true, "label": "Proc Cd",  "fieldName": "sServiceCode", "value": "10", "disabled": "No", "actionName": "SUBTAB_EXTERNAL_URL", "linkToChange": "sServiceCodeLink",    "navToItem": "sServiceCodeLink","pageName": "Claim_Details_LWC"},
          {
            "text": true,
            "label": "Proc Desc",
            "value": "10",
            "fieldName": "sServiceDesc",
            "disabled": "No"
          }]
      }, {
        "compoundx": true,
        "label": "Amounts",
        "headerHelpText": "Billed : Billed Charge Amount <br> Allowed : Allowed Amount <br> Discount : Plan/Provider Discount Amount <br> Excluded : Excluded Amount <br> Denied : Denied/Benefit Exclusion Amount",
        "value": "Pend",
        "compoundvalue": [
          {
            "text": true,
            "label": "Billed",
            "fieldName": "sChargeAmt",
            "value": "11",
            "disabled": "No"
          },
          {
            "text": true,
            "label": "Allowed",
            "value": "10",
            "fieldName": "sBenAllowAmt",
            "disabled": "No"
          }, {
            "text": true,
            "label": "Discount",
            "value": "10",
            "fieldName": "sProvWriteOff",
            "disabled": "No"
          },
      {
            "text": true,
            "label": "Excluded",
            "value": "10",
            "fieldName": "sExcludeAmt",
            "disabled": "No"
          },{
            "text": true,
            "label": "Denied",
            "value": "10",
            "fieldName": "sBenDenyAmt",
            "disabled": "No"
          }]
      },{
        "compoundx": true,
        "label": "Copay/Coins Amounts",
        "headerHelpText": "Ded : Deductible <br> Coins : Coinsurance",
        "value": "otherAmt",
        "compoundvalue": [
      {
            "text": true,
            "label": "Copay",
            "value": "11",
            "fieldName": "sCopayAmt",
            "disabled": "No"
          },
          {
            "text": true,
            "label": "Ded",
            "fieldName": "sDeductAmt",
            "value": "10",
            "disabled": "No"
          },
          {
            "text": true,
            "label": "Coins",
            "value": "10",
            "fieldName": "sCoInsAmt",
            "disabled": "No"
          }]
      },{
      "compoundx": true,
        "label": "Paid Amounts",
        "headerHelpText1": "Mbr Resp : Member<br>Responsible <br> Paid : Plan Paid Amount <br> OIC Paid : Other Insurance<br>Carriers",
        "value": "mbrAmt",
        "compoundvalue": [	
       {
            "text": true,
            "label": "Mbr Resp",
            "value": "11",
            "fieldName": "sMbrRespAmt",
            "disabled": "No"
          },
      {
            "text": true,
            "label": "Paid",
            "value": "10",
            "fieldName": "sPaidAmt",
            "disabled": "No"
          },{
            "text": true,
            "label": "OIC Paid",
            "value": "10",
            "fieldName": "sCobPaidAmt",
            "disabled": "No"
          }]
    }
    ]];