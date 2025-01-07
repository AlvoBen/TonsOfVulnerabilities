/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company. 
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */
jQuery.sap.declare("sap.ui.commons.MenuItem");jQuery.sap.require("sap.ui.commons.library");jQuery.sap.require("sap.ui.commons.MenuItemBase");sap.ui.commons.MenuItemBase.extend("sap.ui.commons.MenuItem",{metadata:{library:"sap.ui.commons",properties:{"text":{type:"string",group:"Appearance",defaultValue:''},"icon":{type:"sap.ui.core.URI",group:"Appearance",defaultValue:''}}}});
sap.ui.commons.MenuItem.prototype.render=function(r,i,m,I){var a=r;var s=i.getSubmenu();a.write("<li ");a.writeAttribute("class","sapUiMnuItm"+(m.checkEnabled(i)?"":" sapUiMnuItmDsbl"));if(i.getTooltip_AsString()){a.writeAttributeEscaped("title",i.getTooltip_AsString())}a.writeElementData(i);if(I.bAccessible){a.writeAttribute("role","menuitem");a.writeAttribute("aria-labelledby",m.getId()+" "+this.getId()+"-txt "+this.getId()+"-scuttxt");a.writeAttribute("aria-disabled",!m.checkEnabled(i));a.writeAttribute("aria-posinset",I.iItemNo);a.writeAttribute("aria-setsize",I.iTotalItems);if(s){a.writeAttribute("aria-haspopup",true);a.writeAttribute("aria-owns",s.getId())}}a.write("><div class=\"sapUiMnuItmL\"></div>");a.write("<div class=\"sapUiMnuItmIco\">");if(i.getIcon()){a.writeIcon(i.getIcon())}a.write("</div>");a.write("<div id=\""+this.getId()+"-txt\" class=\"sapUiMnuItmTxt\">");a.writeEscaped(i.getText());a.write("</div>");a.write("<div id=\""+this.getId()+"-scuttxt\" class=\"sapUiMnuItmSCut\"></div>");a.write("<div class=\"sapUiMnuItmSbMnu\">");if(s){a.write("<div>&nbsp;</div>")}a.write("</div>");a.write("<div class=\"sapUiMnuItmR\"></div>");a.write("</li>")};
sap.ui.commons.MenuItem.prototype.hover=function(h,m){if(h){jQuery(this.getDomRef()).addClass("sapUiMnuItmHov")}else{jQuery(this.getDomRef()).removeClass("sapUiMnuItmHov")}};
