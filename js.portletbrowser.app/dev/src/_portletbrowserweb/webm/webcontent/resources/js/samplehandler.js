//--------------------------------------------------------------------------------------
// Sample Event Handler for PopupMenu Events
//--------------------------------------------------------------------------------------

//BREADCRUMB
function UR_BreadCrumbItemClick(sId,sKey,event) {
	  //alert("BreadCrumbItemClick on "+sId+" and the Key was "+sKey);
}
function UR_BreadCrumbSingleLinkClick(sId,event) {
	  //alert("BreadCrumbSingleLinkClick on "+sId);
}

//BUTTON 
//CLICK
function UR_ButtonClick(Id, state,event) {
  //alert("My Id equals "+Id);
}

//LINK 
//CLICK
function UR_LinkClick(Id,event) {
  //alert("My Id equals "+Id);
}
//DRAG
function UR_Drag(Id,event) {
  //alert("My Id equals "+Id);
}
//DRAGSTART
function UR_DragStart(Id,event) {
  //alert("My Id equals "+Id);
}
//MOUSEOVER
function UR_LinkMouseOver(Id,event) {
  //alert("My Id equals "+Id);
}
//RIGHTCLICK
function UR_LinkRightClick(Id,event) {
  //alert("My Id equals "+Id);
}


//TABSTRIP
//TABCHANGE
function UR_TabChange(strId, intNewIdx, intOldIdx,event) {
	sapUrMapi_TabStrip_setItemActive(strId,intNewIdx);
}


// POPUPMENUITEMMOUSEOVER, POPUPMENUITEMMOUSEOUT
function UR_MnuToggle(mywindow,id,bIn,event) {
  sapUrMapi_PopupMenu_hoverItem(mywindow,id,bIn);
}
//POPUPMENUITEMSELECT
function UR_MnuItemSelect(id, bOn,event) {
  alert("selected popupitem="+id);
  //alert((bOn)?" and it was checked":" and it was not checked");
}
function UR_MnuItemLinkClick(id, bOn,event) {
	sapUrMapi_PopupMenu_ExecuteLink(id);
}

// CHECKBOX 
// if checkbox label clicked, change it's image's class
function UR_CbxClick(sId,event){
	sapUrMapi_CheckBox_toggle(sId,event);
  return false;
}

function UR_CbxBlur(sId,event) {
	 
}
function UR_CbxFocus(sId,event) {
	 
}

// RADIOBUTTON 
// if radiobutton label clicked, change it's image's class
function UR_RbgChange(sId,event){
	sapUrMapi_RadioButton_toggle(sId);
  return false;
}

function UR_RbgBlur(sId,event) {
	 
}
function UR_RbgFocus(sId,event) {
	 
}

//LISTBOX
function UR_ListBoxChange(event) {
}
function UR_ListBoxBlur(event) {
}
function UR_ListBoxFocus(event) {
}

//DROPDOWNLISTBOX
function UR_DDListBoxChange(sId,event) {
	var rememberKey=sapUrMapi_DropDownListBox_getSelectedKey(sId);
	//alert("The selectedKey Is " + sapUrMapi_DropDownListBox_getSelectedKey(sId));
	//alert("The selectedIndex Is " + sapUrMapi_DropDownListBox_getSelectedIndex(sId));
	//alert("The selectedIndex will be set to index 0");
	sapUrMapi_DropDownListBox_setSelectedIndex(sId,0);
	//alert("The selectedKey Is Now " + sapUrMapi_DropDownListBox_getSelectedKey(sId));
	sapUrMapi_DropDownListBox_setSelectedKey(sId,rememberKey);
	//alert("The selectedKey was reset to your selection " + sapUrMapi_DropDownListBox_getSelectedKey(sId));
	
}
function UR_DDListBoxBlur(sId,event) {
}
function UR_DDListBoxFocus(sId,event) {
}

//TRAY
//toggles the display of the tray's contents
function UR_TrcToggle( idTray,event) {
	sapUrMapi_Tray_toggle(idTray,event); 
	return true;
}
//onmenu
function UR_TrayOptionMenu( idTray, idTrigger, idContent ,event) {
	sapUrMapi_PopupMenu_showMenu(idTrigger,idContent,sapPopupPositionBehavior.MENURIGHT ); 
	return true;
}

//TREE
/* TREE COLLAPSE ALL*/
function UR_TreCollapseAll( sIdPrefix ,event){
	sapUrMapi_Tree_collapseAll(sIdPrefix );	
}

/* TREE EXPAND NODE */
function UR_TreNdToggle( sNodeId ,event)
{
	sapUrMapi_Tree_toggle (sNodeId );	
}

/* TREE CLICK NODE */
function UR_TreNdClick(event){
    //alert("You clicked on a Tree Node.");
}
/* TREE NODE DRAG ENTER */
function UR_TreDragEnter(event ){
    //alert("You Drag Entered");
}
/* TREE NODE RIGHT CLICK*/
function UR_TreCLick(event){
}
/* TREE NODE MOSUEOVER */
function UR_TreMouseOver(event){
}

//TEXTVIEW
//TEXTVIEWCLICK
function UR_TextViewClick(Id,event) {
}
//TEXTVIEWMOUSEOVER
function UR_TextViewMouseOver(Id,event) {
}
//TEXTVIEWRIGHTCLICK) {
function UR_TextViewRightClick(Id,event) {
}

//Image events
//IMAGECLICK) {
function UR_ImageClick(Id,event) {
}
//IMAGEMOUSEOVER) {
function UR_ImageMouseOver(Id,event){
}
//IMAGERIGHTCLICK) {
function UR_ImageRightClick(Id,event) {
}


//INPUTFIELD
// INPUTFIELDBLUR
function UR_InputFieldBlur(id,event) {
	 //sapUrMapi_InputField_setInvalid(id,false);
}

// INPUTFIELDCHANGE
function UR_InputFieldChange(id,event) {
}

// INPUTFIELDFIELDHELPCLICK
function UR_InputFieldHelpClick(id,event) {
  sapUrMapi_InputField_showDatePicker(id,2002,7,31,0)
}

// INPUTFIELDDATESELECT
function UR_InputFieldDateSelect(sId,iDay,iMonth,iYear,event) {
	sapUrMapi_InputField_setValue(sId,iDay+"-"+iMonth+"-"+iYear);
	sapUrMapi_hideDatePicker();
}

// INPUTFIELDFOCUS
function UR_InputFieldFocus(id,event) {
	 //sapUrMapi_InputField_setInvalid(id,true,"laksdjlkasjdlakjd");
}

// INPUTFIELDKEYDOWN
function UR_InputFieldKeyDown(id,event) {
}

// INPUTFIELDKEYPRESS
function UR_InnputFieldKeyPress(id,event) {
}

// INPUTFIELDKEYUP
function UR_InnputFieldKeyUp(id,event){
}


//TABLEVIEW
//TABLECELLCLICK
function UR_TableCellClick(sId,idxRow,idxCol,event) {
	//alert("TableCellClick Event on Row "+idxRow+" and Col"+idxCol);
}
//TABLEHEADERCELLCLICK
function UR_TableHeaderCellClick(sId,idxCol,event) {
	//alert("TableHeaderCellClick Event on Col"+idxCol);
}
//TABLENAVIGATIONBOTTOM
function UR_TableNavBottom(sId,event) {
	alert("TABLENAVIGATIONBOTTOM Event");
}
//TABLENAVIGATIONTOP
function UR_TableNavTop(sId,event) {
	alert("TABLENAVIGATIONTOP Event");
}
//TABLENAVIGATIONLINEDOWN
function UR_TableNavLineDown(sId,event) {
	alert("TABLENAVIGATIONLINEDOWN Event");
}
//TABLENAVIGATIONLINEUP
function UR_TableNavLineUp(sId,event) {
	alert("TABLENAVIGATIONLINEUP Event");
}
//TABLENAVIGATIONPAGEDOWN
function UR_TableNavPageDown(sId,event) {
	alert("TABLENAVIGATIONPAGEDOWN Event");
}
//TABLENAVIGATIONPAGEUP
function UR_TableNavPageUp(sId,event) {
	alert("TABLENAVIGATIONPAGEUP Event");
}
//TABLESELECTROW
function UR_TableSelectRow(sId,idxRow,event) {
	sapUrMapi_Table_selectRow(sId,idxRow,false);
	return false; 
}
//TABLECLICK
function UR_TableClick(sId,event) {
   sapUrMapi_Table_getClickedRowIndex(sId,event); 
   sapUrMapi_Table_getClickedColIndex(sId,event); 
}
//MENU ITEM
//MENUBARITEMCLICK
function UR_TextViewClick(sId,event) {

}
//MENUBARITEMMOUSEOVER
function UR_TextViewMouseOver(sId,event) {

}
//MENUBARITEMRIGHTCLICK
function UR_TextViewRightClick(sId,event) {

}

//DATENAVIGATOR
//DATENAVIGATORDAYCLICK
function UR_DateNavigatorDayClick(sId,sDayId,event) {
	alert(sDayId);
}
//DATENAVIGATORWEEKCLICK
function UR_DateNavigatorWeekClick(sId,sWeekId,event) {
	alert(sWeekId);
}
//DATENAVIGATORMONTHCLICK 
function UR_DateNavigatorMonthClick(sId,sMonthId,event) {
	alert(sMonthId);
}
//DATENAVIGATORNAVIGATE
function UR_DateNavigatorNavigate(sId,bUp,event) {
	if (bUp) {
	  alert("Navigate Up");
	} else {
	  alert("Navigate Down");
	}
}

//LABEL
//FOCUS
function UR_LabelFocus(sId, sForId,event) {
  sapUrMapi_Label_FocusLabeledElement(sForId);
}

