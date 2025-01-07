/*
LWC Name        : GenericUpdateTabLabel.js
Function        : JS file for updating tab label.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    06/01/2023                   Original Version
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
export default class GenericUpdateTabLabel extends LightningElement { }

const updateTabLabel = (labelname, iconname, title) => {
    invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
        if (isConsole) {
            invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                if (title && focusedTab?.customTitle === title) {
                    invokeWorkspaceAPI('setTabLabel', {
                        tabId: focusedTab.tabId,
                        label: labelname
                    });
                    invokeWorkspaceAPI('setTabIcon', {
                        tabId: focusedTab.tabId,
                        icon: iconname
                    });
                } else {
                    invokeWorkspaceAPI('setTabLabel', {
                        tabId: focusedTab.tabId,
                        label: labelname
                    });
                    invokeWorkspaceAPI('setTabIcon', {
                        tabId: focusedTab.tabId,
                        icon: iconname
                    });
                }
            });
        }
    });
}

export { updateTabLabel }