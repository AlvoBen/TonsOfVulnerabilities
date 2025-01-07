/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company. 
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */
jQuery.sap.declare("sap.ui.ux3.OverlayRenderer");sap.ui.ux3.OverlayRenderer={};
sap.ui.ux3.OverlayRenderer.render=function(r,c){var a=r;a.write("<div");a.writeControlData(c);a.addClass("sapUiUx3Overlay");if(this.addRootClasses){this.addRootClasses(a,c)}a.writeClasses();a.write(">");a.write("<div role='presentation'");a.addClass("sapUiUx3OverlayOverlay");if(this.addOverlayClasses){this.addOverlayClasses(a,c)}a.writeClasses();a.write(">");a.write("</div>");a.write("<span class='sapUiUx3OverlayFocusDummyPane' id='"+c.getId()+"-firstFocusDummyPaneFw'></span>");a.write("<span class='sapUiUx3OverlayFocusDummyPane' id='"+c.getId()+"-firstFocusDummyPaneBw'></span>");if(c.getOpenButtonVisible()){a.write("<a role=\"button\" aria-disabled=\"false\" class='sapUiUx3OverlayOpenButton' id='"+c.getId()+"-openNew' tabindex=\"0\" title=\""+c._getText("OVERLAY_OPEN_BUTTON_TOOLTIP")+"\">"+c._getText("OVERLAY_OPEN_BUTTON_TEXT")+"</a>")}if(c.getCloseButtonVisible()){a.write("<a role=\"button\" aria-disabled=\"false\" class='sapUiUx3OverlayCloseButton' id='"+c.getId()+"-close' tabindex=\"0\" title=\""+c._getText("OVERLAY_CLOSE_BUTTON_TOOLTIP")+"\"></a>")}if(this.renderContent){this.renderContent(a,c)}a.write("<span class='sapUiUx3OverlayFocusDummyPane' id='"+c.getId()+"-LastFocusDummyPane'></span>");a.write("</div>")};
