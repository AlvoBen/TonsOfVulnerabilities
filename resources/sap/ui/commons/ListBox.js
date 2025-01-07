/*!
 * SAP UI development toolkit for HTML5 (SAPUI5)
 * (c) Copyright 2009-2013 SAP AG or an SAP affiliate company. 
 * Licensed under the Apache License, Version 2.0 - see LICENSE.txt.
 */
jQuery.sap.declare("sap.ui.commons.ListBox");jQuery.sap.require("sap.ui.commons.library");jQuery.sap.require("sap.ui.core.Control");sap.ui.core.Control.extend("sap.ui.commons.ListBox",{metadata:{publicMethods:["getSelectedIndex","setSelectedIndex","addSelectedIndex","removeSelectedIndex","getSelectedIndices","setSelectedIndices","addSelectedIndices","isIndexSelected","getSelectedItem","getSelectedItems","clearSelection","scrollToIndex","setItems","setSelectedKeys","getSelectedKeys"],library:"sap.ui.commons",properties:{"editable":{type:"boolean",group:"Behavior",defaultValue:true},"enabled":{type:"boolean",group:"Behavior",defaultValue:true},"allowMultiSelect":{type:"boolean",group:"Behavior",defaultValue:false},"visible":{type:"boolean",group:"Appearance",defaultValue:true},"width":{type:"sap.ui.core.CSSSize",group:"Dimension",defaultValue:null},"height":{type:"sap.ui.core.CSSSize",group:"Dimension",defaultValue:null},"scrollTop":{type:"int",group:"Behavior",defaultValue:-1},"displayIcons":{type:"boolean",group:"Behavior",defaultValue:false},"displaySecondaryValues":{type:"boolean",group:"Misc",defaultValue:false},"valueTextAlign":{type:"sap.ui.core.TextAlign",group:"Appearance",defaultValue:sap.ui.core.TextAlign.Begin},"secondaryValueTextAlign":{type:"sap.ui.core.TextAlign",group:"Appearance",defaultValue:sap.ui.core.TextAlign.Begin},"minWidth":{type:"sap.ui.core.CSSSize",group:"Dimension",defaultValue:null},"maxWidth":{type:"sap.ui.core.CSSSize",group:"Dimension",defaultValue:null},"visibleItems":{type:"int",group:"Dimension",defaultValue:null}},defaultAggregation:"items",aggregations:{"items":{type:"sap.ui.core.Item",multiple:true,singularName:"item"}},associations:{"ariaDescribedBy":{type:"sap.ui.core.Control",multiple:true,singularName:"ariaDescribedBy"},"ariaLabelledBy":{type:"sap.ui.core.Control",multiple:true,singularName:"ariaLabelledBy"}},events:{"select":{}}}});sap.ui.commons.ListBox.M_EVENTS={'select':'select'};jQuery.sap.require("sap.ui.core.delegate.ItemNavigation");jQuery.sap.require("jquery.sap.strings");
sap.ui.commons.ListBox.prototype.init=function(){this.allowTextSelection(false);if(!this._bHeightInItems){this._bHeightInItems=false;this._iVisibleItems=-1}this._sTotalHeight=null;if(sap.ui.commons.ListBox._fItemHeight===undefined){sap.ui.commons.ListBox._fItemHeight=-1}if(sap.ui.commons.ListBox._iBordersAndStuff===undefined){sap.ui.commons.ListBox._iBordersAndStuff=-1}this._aSelectionMap=[];this._iLastDirectlySelectedIndex=-1;this._aActiveItems=null;if(sap.ui.Device.support.touch){jQuery.sap.require("sap.ui.core.delegate.ScrollEnablement");this._oScroller=new sap.ui.core.delegate.ScrollEnablement(this,this.getId()+"-list",{vertical:true,zynga:true,preventDefault:true})}};
sap.ui.commons.ListBox.prototype.onThemeChanged=function(){sap.ui.commons.ListBox._fItemHeight=-1;sap.ui.commons.ListBox._iBordersAndStuff=-1;this._sTotalHeight=null;if(!this._bHeightInItems){this._iVisibleItems=-1}this._skipStoreScrollTop=true;if(this.getDomRef()){this.invalidate()}};
sap.ui.commons.ListBox.prototype.onBeforeRendering=function(){if(this._skipStoreScrollTop){delete this._skipStoreScrollTop;return}this.getScrollTop()};
sap.ui.commons.ListBox.prototype.onAfterRendering=function(){var d=this.getDomRef();if(sap.ui.commons.ListBox._fItemHeight<=0){var s=sap.ui.getCore().getStaticAreaRef();var a=document.createElement("div");a.id="sap-ui-commons-ListBox-sizeDummy";s.appendChild(a);a.innerHTML='<div class="sapUiLbx sapUiLbxFlexWidth sapUiLbxStd"><ul><li class="sapUiLbxI"><span class="sapUiLbxITxt">&nbsp;</span></li></ul></div>';var I=a.firstChild.firstChild.firstChild;sap.ui.commons.ListBox._fItemHeight=I.offsetHeight;if(!!sap.ui.Device.browser.internet_explorer&&(document.documentMode==9||document.documentMode==10)){var c=document.defaultView.getComputedStyle(I.firstChild,"");var h=parseFloat(c.getPropertyValue("height").split("px")[0]);if(!(typeof h==="number")||!(h>0)){h=jQuery(I.firstChild).height()}var p=parseFloat(c.getPropertyValue("padding-top").split("px")[0]);var b=parseFloat(c.getPropertyValue("padding-bottom").split("px")[0]);var e=parseFloat(c.getPropertyValue("border-top-width").split("px")[0]);var f=parseFloat(c.getPropertyValue("border-bottom-width").split("px")[0]);sap.ui.commons.ListBox._fItemHeight=h+p+b+e+f}s.removeChild(a)}if(sap.ui.commons.ListBox._iBordersAndStuff==-1){var D=jQuery(this.getDomRef());var o=D.outerHeight();var g=D.height();sap.ui.commons.ListBox._iBordersAndStuff=o-g}if(this._bHeightInItems){if(this._sTotalHeight==null){this._calcTotalHeight();d.style.height=this._sTotalHeight}else{}}if(this._iVisibleItems=-1){this._updatePageSize()}var F=this.getFocusDomRef(),r=F.childNodes,j=[],k=this.getItems();this._aActiveItems=[];var A=this._aActiveItems;for(var i=0;i<r.length;i++){if(!(k[i]instanceof sap.ui.core.SeparatorItem)){A[j.length]=i;j.push(r[i])}}if(!this.oItemNavigation){var n=(!this.getEnabled()||!this.getEditable());this.oItemNavigation=new sap.ui.core.delegate.ItemNavigation(null,null,n);this.oItemNavigation.attachEvent(sap.ui.core.delegate.ItemNavigation.Events.AfterFocus,this._handleAfterFocus,this);this.addDelegate(this.oItemNavigation)}this.oItemNavigation.setRootDomRef(F);this.oItemNavigation.setItemDomRefs(j);this.oItemNavigation.setCycling(false);this.oItemNavigation.setSelectedIndex(this._getNavigationIndexForRealIndex(this.getSelectedIndex()));this.oItemNavigation.setPageSize(this._iVisibleItems);if(this.oScrollToIndexRequest){this.scrollToIndex(this.oScrollToIndexRequest.iIndex,this.oScrollToIndexRequest.bLazy)}else{var l=this.getProperty("scrollTop");if(l>-1){d.scrollTop=l}}var t=this;window.setTimeout(function(){if(t.oScrollToIndexRequest){t.scrollToIndex(t.oScrollToIndexRequest.iIndex,t.oScrollToIndexRequest.bLazy);t.oScrollToIndexRequest=null}else{var l=t.getProperty("scrollTop");if(l>-1){d.scrollTop=l}}},0)};
sap.ui.commons.ListBox.prototype._getNavigationIndexForRealIndex=function(I){var a=this.getItems();var n=I;for(var i=0;i<I;i++){if(a[i]instanceof sap.ui.core.SeparatorItem){n--}}return n};
sap.ui.commons.ListBox.prototype._updatePageSize=function(){var d=this.getDomRef();if(d){if(sap.ui.commons.ListBox._fItemHeight>0){this._iVisibleItems=Math.floor(d.clientHeight/sap.ui.commons.ListBox._fItemHeight)}else{}}};
sap.ui.commons.ListBox.prototype.scrollToIndex=function(i,l){var d=this.getDomRef();if(d){var I=jQuery.sap.byId(this.getId()+"-list").children("li[data-sap-ui-lbx-index="+i+"]");I=I.get(0);if(I){var s=I.offsetTop;if(!l){this.setScrollTop(s)}else{var c=d.scrollTop;var v=jQuery(d).height();if(c>=s){this.setScrollTop(s)}else if((s+sap.ui.commons.ListBox._fItemHeight)>(c+v)){this.setScrollTop(Math.ceil(s+sap.ui.commons.ListBox._fItemHeight-v))}else{}}}this.getScrollTop()}else{this.oScrollToIndexRequest={iIndex:i,bLazy:l}}return this};
sap.ui.commons.ListBox.prototype.getVisibleItems=function(){return this._iVisibleItems};
sap.ui.commons.ListBox.prototype.setVisibleItems=function(i){this.setProperty("visibleItems",i,true);this._iVisibleItems=i;if(i<0){this._bHeightInItems=false}else{this._bHeightInItems=true}this._sTotalHeight=null;var d=this.getDomRef();if(d){if(this._bHeightInItems){var f=d.firstChild?d.firstChild.firstChild:null;if(f||((sap.ui.commons.ListBox._fItemHeight>0)&&(sap.ui.commons.ListBox._iBordersAndStuff>0))){d.style.height=this._calcTotalHeight()}else{this.invalidate()}}else{d.style.height=this.getHeight();this._updatePageSize();if(this.oItemNavigation){this.oItemNavigation.setPageSize(this._iVisibleItems)}}}if(this._sTotalHeight==null){}return this};
sap.ui.commons.ListBox.prototype._calcTotalHeight=function(){var d=this._iVisibleItems*sap.ui.commons.ListBox._fItemHeight;this._sTotalHeight=(d+sap.ui.commons.ListBox._iBordersAndStuff)+"px";return this._sTotalHeight};
sap.ui.commons.ListBox.prototype.setHeight=function(h){this._bHeightInItems=false;this._iVisibleItems=-1;var d=this.getDomRef();if(d){d.style.height=h;this._updatePageSize();if(this.oItemNavigation){this.oItemNavigation.setPageSize(this._iVisibleItems)}}this.setProperty("height",h,true);return this};
sap.ui.commons.ListBox.prototype.setWidth=function(w){var d=this.getDomRef();if(d){d.style.width=w}this.setProperty("width",w,true);return this};
sap.ui.commons.ListBox.prototype.setScrollTop=function(s){var a=this.getDomRef();this.oScrollToIndexRequest=null;if(a){a.scrollTop=s}this.setProperty("scrollTop",s,true);return this};
sap.ui.commons.ListBox.prototype.getScrollTop=function(){var s=this.getDomRef();if(s){var a=s.scrollTop;this.setProperty("scrollTop",a,true);return a}else{return this.getProperty("scrollTop")}};
sap.ui.commons.ListBox.prototype.onfocusin=function(e){if(!!sap.ui.Device.browser.internet_explorer&&((sap.ui.Device.browser.version==7)||(sap.ui.Device.browser.version==8))&&(e.target!=this.getDomRef())&&(e.target.className!="sapUiLbxI")){var p=e.target.parentNode;if(jQuery(p).hasClass("sapUiLbxI")){p.focus()}}};
sap.ui.commons.ListBox.prototype.onmousedown=function(e){if(!!sap.ui.Device.browser.webkit&&e.target&&e.target.id===this.getId()){var i=document.activeElement?document.activeElement.id:this.getId();var t=this;window.setTimeout(function(){var s=t.getDomRef().scrollTop;jQuery.sap.focus(jQuery.sap.domById(i));t.getDomRef().scrollTop=s},0)}};
sap.ui.commons.ListBox.prototype.onclick=function(e){this._handleUserActivation(e)};
sap.ui.commons.ListBox.prototype.onsapspace=function(e){this._handleUserActivation(e)};
sap.ui.commons.ListBox.prototype.onsapspacemodifiers=sap.ui.commons.ListBox.prototype.onsapspace;sap.ui.commons.ListBox.prototype.onsapenter=sap.ui.commons.ListBox.prototype.onsapspace;sap.ui.commons.ListBox.prototype.onsapentermodifiers=sap.ui.commons.ListBox.prototype.onsapspace;
sap.ui.commons.ListBox.prototype._handleUserActivation=function(e){if(!this.getEnabled()||!this.getEditable()){return}var s=e.target;if(s.id===""||jQuery.sap.endsWith(s.id,"-txt")){s=s.parentNode;if(s.id===""){s=s.parentNode}}var a=jQuery(s).attr("data-sap-ui-lbx-index");if(typeof a=="string"&&a.length>0){var i=parseInt(a,10);var I=this.getItems();var o=I[i];if(I.length<=i){i=I.length-1}if(i>=0&&i<I.length){if(o.getEnabled()&&!(o instanceof sap.ui.core.SeparatorItem)){if(e.ctrlKey||e.metaKey){this._handleUserActivationCtrl(i,o)}else{if(e.shiftKey){this.setSelectedIndices(this._getUserSelectionRange(i));this.fireSelect({id:this.getId(),selectedIndex:i,selectedIndices:this.getSelectedIndices(),selectedItem:o,sId:this.getId(),aSelectedIndices:this.getSelectedIndices()});this._iLastDirectlySelectedIndex=i}else{this._handleUserActivationPlain(i,o)}}}}e.preventDefault();e.stopPropagation()}};
sap.ui.commons.ListBox.prototype._handleUserActivationPlain=function(i,I){this._iLastDirectlySelectedIndex=i;this.oItemNavigation.setSelectedIndex(this._getNavigationIndexForRealIndex(i));if(this.getSelectedIndex()!=i||this.getSelectedIndices().length>1){this.setSelectedIndex(i);this.fireSelect({id:this.getId(),selectedIndex:i,selectedIndices:this.getSelectedIndices(),selectedItem:I,sId:this.getId(),aSelectedIndices:this.getSelectedIndices()})}};
sap.ui.commons.ListBox.prototype._handleUserActivationCtrl=function(i,I){this._iLastDirectlySelectedIndex=i;this.oItemNavigation.setSelectedIndex(this._getNavigationIndexForRealIndex(i));if(this.isIndexSelected(i)){this.removeSelectedIndex(i)}else{this.addSelectedIndex(i)}this.fireSelect({id:this.getId(),selectedIndex:i,selectedIndices:this.getSelectedIndices(),selectedItem:I,sId:this.getId(),aSelectedIndices:this.getSelectedIndices()})};
sap.ui.commons.ListBox.prototype._getUserSelectionRange=function(I){if(this._iLastDirectlySelectedIndex==-1){return[]}var a=this.getItems();var r=[];if(this._iLastDirectlySelectedIndex<=I){for(var i=this._iLastDirectlySelectedIndex;i<=I;i++){if((i>-1)&&(a[i].getEnabled()&&!(a[i]instanceof sap.ui.core.SeparatorItem))){r.push(i)}}}else{for(var i=I;i<=this._iLastDirectlySelectedIndex;i++){if((i>-1)&&(a[i].getEnabled()&&!(a[i]instanceof sap.ui.core.SeparatorItem))){r.push(i)}}}return r};
sap.ui.commons.ListBox.prototype.getSelectedIndex=function(){for(var i=0;i<this._aSelectionMap.length;i++){if(this._aSelectionMap[i]){return i}}return-1};
sap.ui.commons.ListBox.prototype.setSelectedIndex=function(s){if((s<-1)||(s>this._aSelectionMap.length-1)){return}var I=this.getItems();if((s>-1)&&(!I[s].getEnabled()||(I[s]instanceof sap.ui.core.SeparatorItem))){return}for(var i=0;i<this._aSelectionMap.length;i++){this._aSelectionMap[i]=false}this._aSelectionMap[s]=true;if(this.oItemNavigation){this.oItemNavigation.setSelectedIndex(this._getNavigationIndexForRealIndex(s))}this.getRenderer().handleSelectionChanged(this);return this};
sap.ui.commons.ListBox.prototype.addSelectedIndex=function(s){if(!this.getAllowMultiSelect()){this.setSelectedIndex(s)}if((s<-1)||(s>this._aSelectionMap.length-1)){return}var i=this.getItems();if((s>-1)&&(!i[s].getEnabled()||(i[s]instanceof sap.ui.core.SeparatorItem))){return}if(this._aSelectionMap[s]){return}this._aSelectionMap[s]=true;this.getRenderer().handleSelectionChanged(this);return this};
sap.ui.commons.ListBox.prototype.removeSelectedIndex=function(i){if((i<0)||(i>this._aSelectionMap.length-1)){return}if(!this._aSelectionMap[i]){return}this._aSelectionMap[i]=false;this.getRenderer().handleSelectionChanged(this);return this};
sap.ui.commons.ListBox.prototype.clearSelection=function(){for(var i=0;i<this._aSelectionMap.length;i++){if(this._aSelectionMap[i]){this._aSelectionMap[i]=false}}this._iLastDirectlySelectedIndex=-1;if(this.oItemNavigation){this.oItemNavigation.setSelectedIndex(-1)}this.getRenderer().handleSelectionChanged(this);return this};
sap.ui.commons.ListBox.prototype.getSelectedIndices=function(){var r=[];for(var i=0;i<this._aSelectionMap.length;i++){if(this._aSelectionMap[i]){r.push(i)}}return r};
sap.ui.commons.ListBox.prototype.setSelectedIndices=function(s){var a=[];var I=this.getItems();for(var i=0;i<s.length;i++){if((s[i]>-1)&&(s[i]<this._aSelectionMap.length)){if(I[s[i]].getEnabled()&&!(I[s[i]]instanceof sap.ui.core.SeparatorItem)){a.push(s[i])}}}if(a.length>0){if(!this.getAllowMultiSelect()){a=[a[0]]}}for(var i=0;i<this._aSelectionMap.length;i++){this._aSelectionMap[i]=false}for(var i=0;i<a.length;i++){this._aSelectionMap[a[i]]=true}this.getRenderer().handleSelectionChanged(this);return this};
sap.ui.commons.ListBox.prototype.addSelectedIndices=function(s){var a=[];var I=this.getItems();for(var i=0;i<s.length;i++){if((s[i]>-1)&&(s[i]<this._aSelectionMap.length)){if(I[s[i]].getEnabled()&&!(I[s[i]]instanceof sap.ui.core.SeparatorItem)){a.push(s[i])}}}if(a.length>0){if(!this.getAllowMultiSelect()){a=[a[0]]}for(var i=0;i<a.length;i++){this._aSelectionMap[a[i]]=true}this.getRenderer().handleSelectionChanged(this)}return this};
sap.ui.commons.ListBox.prototype.isIndexSelected=function(i){if((i<-1)||(i>this._aSelectionMap.length-1)){return false}return this._aSelectionMap[i]};
sap.ui.commons.ListBox.prototype.setSelectedKeys=function(s){var I=this.getItems();var k;var K={};for(var i=0;i<s.length;i++){if(k=s[i]){K[k]=true}}var a=[];for(var j=0;j<I.length;j++){if((k=I[j].getKey())&&(K[k])){a.push(j)}}return this.setSelectedIndices(a)};
sap.ui.commons.ListBox.prototype.getSelectedKeys=function(){var I=this.getItems();var r=[];for(var i=0;i<this._aSelectionMap.length;i++){if(this._aSelectionMap[i]){r.push(I[i].getKey())}}return r};
sap.ui.commons.ListBox.prototype.getSelectedItem=function(){var i=this.getSelectedIndex();if((i<0)||(i>=this._aSelectionMap.length)){return null}return this.getItems()[i]};
sap.ui.commons.ListBox.prototype.getSelectedItems=function(){var I=this.getItems();var r=[];for(var i=0;i<this._aSelectionMap.length;i++){if(this._aSelectionMap[i]){r.push(I[i])}}return r};
sap.ui.commons.ListBox.prototype.setAllowMultiSelect=function(a){this.setProperty("allowMultiSelect",a);var o=false;var t=false;if(!a&&this._aSelectionMap){for(var i=0;i<this._aSelectionMap.length;i++){if(this._aSelectionMap[i]){if(!o){o=true}else{this._aSelectionMap[i]=false;t=true}}}}if(t){this.getRenderer().handleSelectionChanged(this)}return this};
sap.ui.commons.ListBox.prototype._handleAfterFocus=function(c){var i=c.getParameter("index");i=((i!==undefined&&i>=0)?this._aActiveItems[i]:0);this.getRenderer().handleARIAActivedescendant(this,i)};
sap.ui.commons.ListBox.prototype.setItems=function(I,d,n){this.bNoItemsChangeEvent=true;if(d){this.destroyItems()}else{this.removeAllItems()}for(var i=0,l=I.length;i<l;i++){this.addItem(I[i])}this.bNoItemsChangeEvent=undefined;if(!n){this.fireEvent("itemsChanged",{event:"setItems",items:I})}return this};
sap.ui.commons.ListBox.prototype.addItem=function(i){this.bNoItemInvalidateEvent=true;this.addAggregation("items",i);this.bNoItemInvalidateEvent=false;if(!this._aSelectionMap){this._aSelectionMap=[]}this._aSelectionMap.push(false);if(!this.bNoItemsChangeEvent){this.fireEvent("itemsChanged",{event:"addItem",item:i})}i.attachEvent("_change",this._handleItemChanged,this);return this};
sap.ui.commons.ListBox.prototype.insertItem=function(i,I){if((I<0)||(I>this._aSelectionMap.length)){return}this.bNoItemInvalidateEvent=true;this.insertAggregation("items",i,I);this.bNoItemInvalidateEvent=false;this._aSelectionMap.splice(I,0,false);this.invalidate();if(!this.bNoItemsChangeEvent){this.fireEvent("itemsChanged",{event:"insertItems",item:i,index:I})}i.attachEvent("_change",this._handleItemChanged,this);return this};
sap.ui.commons.ListBox.prototype.removeItem=function(e){var i=e;if(typeof(e)=="string"){e=sap.ui.getCore().byId(e)}if(typeof(e)=="object"){i=this.indexOfItem(e)}if((i<0)||(i>this._aSelectionMap.length-1)){if(!this.bNoItemsChangeEvent){this.fireEvent("itemsChanged",{event:"removeItem",item:e})}return}this.bNoItemInvalidateEvent=true;var r=this.removeAggregation("items",i);this.bNoItemInvalidateEvent=false;this._aSelectionMap.splice(i,1);this.invalidate();if(!this.bNoItemsChangeEvent){this.fireEvent("itemsChanged",{event:"removeItem",item:r})}r.detachEvent("_change",this._handleItemChanged,this);return r};
sap.ui.commons.ListBox.prototype.removeAllItems=function(){this.bNoItemInvalidateEvent=true;var r=this.removeAllAggregation("items");this.bNoItemInvalidateEvent=false;this._aSelectionMap=[];this.invalidate();if(!this.bNoItemsChangeEvent){this.fireEvent("itemsChanged",{event:"removeAllItems"})}for(var i=0;i<r.length;i++){r[i].detachEvent("_change",this._handleItemChanged,this)}return r};
sap.ui.commons.ListBox.prototype.destroyItems=function(){var I=this.getItems();for(var i=0;i<I.length;i++){I[i].detachEvent("_change",this._handleItemChanged,this)}this.bNoItemInvalidateEvent=true;var d=this.destroyAggregation("items");this.bNoItemInvalidateEvent=false;this._aSelectionMap=[];this.invalidate();if(!this.bNoItemsChangeEvent){this.fireEvent("itemsChanged",{event:"destroyItems"})}return d};
sap.ui.commons.ListBox.prototype.updateItems=function(){this.bNoItemsChangeEvent=true;this.updateAggregation("items");this.bNoItemsChangeEvent=undefined;this.fireEvent("itemsChanged",{event:"updateItems"})};
sap.ui.commons.ListBox.prototype.exit=function(){if(this.oItemNavigation){this.removeDelegate(this.oItemNavigation);this.oItemNavigation.destroy();delete this.oItemNavigation}if(this._oScroller){this._oScroller.destroy();this._oScroller=null}};
sap.ui.commons.ListBox.prototype.getFocusDomRef=function(){return jQuery.sap.domById(this.getId()+'-list')};
sap.ui.commons.ListBox.prototype.getIdForLabel=function(){return this.getId()+'-list'};
sap.ui.commons.ListBox.prototype._handleItemChanged=function(e){if(!this.bNoItemInvalidateEvent){this.fireEvent("itemInvalidated",{item:e.oSource})}};
