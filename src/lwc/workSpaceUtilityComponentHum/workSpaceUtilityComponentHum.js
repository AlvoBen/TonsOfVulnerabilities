/*******************************************************************************************************************************
LWC JS Name : workSpaceUtilityComponentHum.js

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                           04/12/2021                 initial version(US-1892975)
* Ritik Agarwal                                           08/11/2021                 provide more customization for tab manipulations
* Supriya                                                 09/02/2021                 DF-3674
* Ritik Agarwal                                           01/10/2022                 create generic method to call directly LWc to LWC
* Nirmal Garg																					    03/29/2022								 Removed aura dependency for opening subtab.
* Ritik Agarwal                                           02/10/2022                 change openLWCSubtab method to prepolulate data 
* Muthukumar                                              09/08/2022                 US-3279519 update plan demographic
* Visweswara Rao J                                        02/07/2023                 User Story 4130775: T1PRJ0865978 -23998 / 4079568 C6, Lightning- Case management- Ability to create interactions for Home office policies- Jaguars
* Anuradha Gajbhe                                         07/28/2023                 US: 4826980: INC2411079- MF 4743214 - Go Live Incident resolve Quicklink data bleeding RAID 87.
* Vardhman Jain                                           10/20/2023                   US-5009031 update Commercial demographic
*********************************************************************************************************************************/
import {uConstants} from 'c/updatePlanDemographicConstants';

export const pageRef = (viewAllData, compName) => {
  return {
    type: "standard__component",
    attributes: {
      componentName: "c__" + compName
    },
    state: {
      c__data: JSON.stringify(viewAllData)
    }
  };
}
export const apiData = {
  openTab: (recId, recUrl) => {
    return {
      recordId: recId,
      url: recUrl,
      focus: true
    }
  },
  setTabLabel: (tabId, tabLabel) => {
    let tabData = {};
    tabId !== "" ? (tabData.tabId = tabId) : "";
    tabLabel !== "" ? (tabData.label = tabLabel) : "";
    return tabData;
  },
  openSubtab: (pTabId, URL, pageRef) => {
    let subtab = {};
    focusedTab ? (subtab.parentTabId = pTabId) : "";
    Object.keys(pageRef).length > 0 ? (subtab.pageReference = pg) : "";
    URL !== "" ? (subtab.parentTabId = urls) : "";
    return subtab;
  }
}


export const invokeWorkspaceAPI = (methodName, methodArgs) => {
  return new Promise((resolve, reject) => {
    const apiEvent = new CustomEvent("internalapievent", {
      bubbles: true,
      composed: true,
      cancelable: false,
      detail: {
        category: "workspaceAPI",
        methodName: methodName,
        methodArgs: methodArgs,
        callback: (err, response) => {
          if (err) {
            return reject(err);
          } else {
            return resolve(response);
          }
        }
      }
    });
    window.dispatchEvent(apiEvent);
  });
}

export const openLWCSubtab = (lwcName, data, tabProps , pageParams) => {
  let componentDef = {
    componentDef: "c:" + lwcName,

    attributes: {
      encodedData: data,
      pageRefData: pageParams
    }
  };

  // Encode the componentDefinition JS object to Base64 format to make it url addressable
  let encodedComponentDef = btoa(JSON.stringify(componentDef));

  invokeWorkspaceAPI("isConsoleNavigation").then((isConsole) => {
    if (isConsole) {
      invokeWorkspaceAPI("getFocusedTabInfo").then((focusedTab) => {
        if (focusedTab.isSubtab && focusedTab.parentTabId) {
          openSubtab(focusedTab.parentTabId, encodedComponentDef, tabProps);
        } else {
          openSubtab(focusedTab.tabId, encodedComponentDef, tabProps);
        }
      });
    }
  });
};

export const openSubtab = (parentTabId, encodedComponentDef, tabProps) => {
  invokeWorkspaceAPI("openSubtab", {
    parentTabId: parentTabId,

    url: "/one/one.app#" + encodedComponentDef,

    focus: true
  }).then((tabId) => {
    invokeWorkspaceAPI("setTabLabel", {
      tabId: tabId,

      label: tabProps.label
    });

    invokeWorkspaceAPI("setTabIcon", {
      tabId: tabId,

      icon: tabProps.icon,

      iconAlt: ""
    });
	if(tabProps.label===uConstants.Update_Plan_Demographics || tabProps.label===uConstants.Update_Commercial_Demographics){
      invokeWorkspaceAPI("disableTabClose",{
        tabId: tabId,
        disabled: tabProps.tabSwitch
    });
    }
  });
};

/*
Open subtab being used by link framework
*/

export const openSubTabLinkFramework = (url, tabname, icon, clickTabId, focusIn) =>{
  let componentname = '';
  let stateJson = {};
  let isLwc = false;
  try {
      for(let i=0; i<url.split('?').length; i++){
          if(i == 1){
              componentname = 'c:'+url.split('?')[1];
          }else if(i > 1){
              //adding for multiple parameters
              for(let j=0; j< url.split('?')[i].split('&').length; j++)
              {
                  let property = url.split('?')[i].split('&')[j].split('=')[0];
                  stateJson[property] =  url.split('?')[i].split('&')[j].split('=')[1];
              }

          }else if(i == 0 && url.split('?')[0] == 'lwc'){
              isLwc = true;
          }
      }
  } catch (error) {
  }
          let compDefinition = {
      componentDef: componentname,
      attributes: stateJson
  };
  // Base64 encode the compDefinition JS object
  let encodedCompDef = btoa(JSON.stringify(compDefinition));
  invokeWorkspaceAPI("isConsoleNavigation").then((isConsole) => {
    if (isConsole) {
        invokeWorkspaceAPI("openSubtab", {
          parentTabId:  clickTabId,
          url: isLwc ? '/one/one.app#' + encodedCompDef : url,
          focus: focusIn
        }).then((tabId) => {
          invokeWorkspaceAPI("setTabLabel", {
            tabId: tabId,
            label: tabname
          });
          invokeWorkspaceAPI("setTabIcon", {
            tabId: tabId,
            icon: icon,
            iconAlt: ""
          });
        });
    }
  });
}

/**
 * Open new subtab by invoking workspace api. if the tab is already open , it will be activated.
 * @param {Object} params params required to pass to new tab
 * @param {string} auraName Aura component to be rendered in the new tab. ex: title, nameOfScreen
 * @param {Object} parentRef Parent reference
 */
export const openSubTab = async (
  params,
  auraName = "viewAllComponentHum",
  parentRef,
  standardPageRef = undefined,
  tabProps
) => {
  const me = parentRef;
  let pageReference = standardPageRef
    ? standardPageRef
    : pageRef(params, auraName);
  let subTabDetail;
  let tabName = "";
  let tabProperties = tabProps
    ? tabProps
    : {
        openSubTab: true,
        isFocus: true,
        callTabLabel: true,
        callTabIcon: true
      };
  if (await invokeWorkspaceAPI("isConsoleNavigation")) {
    let focusedTab = await invokeWorkspaceAPI("getFocusedTabInfo");
    if (focusedTab && focusedTab.subtabs && focusedTab.subtabs.length > 0) {
      focusedTab.subtabs.forEach((item) => {
        if (item.customTitle === params.title) {
          invokeWorkspaceAPI("openTab", {
            url: item.url
          });
          tabName = item.customTitle;
        }
      });
    }

    if (focusedTab.isSubtab) {
      try {
        tabName = await getTabDetailsFromSubtab(focusedTab, params);
      } catch (error) {
        tabName = "";
      }
    }
    if (focusedTab && tabName !== params.title) {
      if (tabProperties.openSubTab) {
        subTabDetail = await invokeWorkspaceAPI("openSubtab", {
          parentTabId:
            focusedTab.hasOwnProperty("isSubtab") && focusedTab.isSubtab
              ? focusedTab.parentTabId
              : focusedTab.tabId,
          pageReference: pageReference,
          focus: tabProperties.isFocus
        });
      }
      if (tabProperties.callTabLabel) {
        await invokeWorkspaceAPI("setTabLabel", {
          tabId: subTabDetail,
          label: params.title
        });
      }
      if (tabProperties.callTabIcon) {
        await invokeWorkspaceAPI("setTabIcon", {
          tabId: subTabDetail,
          icon: params.icon,
          iconAlt: params.nameOfScreen
        });
        let urls = await invokeWorkspaceAPI("getTabURL", {
          tabId: subTabDetail
        });
        me.subTabDetails = urls;
        me.subTabId = me.subTabDetails;
      }
    }
  }
};

/**
 * Description - this method is used to get parent tab details from subtab
 * @param {*} workspace response
 */
export const getTabDetailsFromSubtab = async (focusedTab, compDetails) => {
  let tabName = "";
  try {
    let tabDetails = await invokeWorkspaceAPI("getTabInfo", {
      tabId: focusedTab.parentTabId
    });
    if (tabDetails && tabDetails.subtabs && tabDetails.subtabs.length > 0) {
      tabDetails.subtabs.forEach((item) => {
        if (item.customTitle === compDetails.title) {
          invokeWorkspaceAPI("openTab", {
            url: item.url
          });
          tabName = item.customTitle;
        }
      });
    }
    return new Promise((resolve, reject) => {
      if (tabName !== "") {
        return resolve(tabName);
      } else {
        return reject(tabName);
      }
    });
  } catch (error) {
    console.log(
      "error while fetching parentTabDetails--",
      JSON.stringify(error)
    );
  }
};
export const opentab = (cmpName, stateData, focus) => {
    invokeWorkspaceAPI('openTab', {
      pageReference: {
        "type": "standard__component",
        "attributes": {
          "componentName": cmpName
        },
        "state": stateData
      },
      focus: focus
    }).
    catch(function (error) {
      console.log('error: ' + JSON.stringify(error));
    });
  };