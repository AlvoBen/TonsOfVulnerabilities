//------------------------------------
// Unified Rendering Modification API
//        (c) 2002, SAP AG
//------------------------------------

//GLOBAL VARIABLES
try {
	ur_system == null;
} catch(e) {
  ur_system = {doc : window.document , mimepath : "/SrTestSuite/resources/common/", stylepath : "/SrTestSuite/resources/style/", is508 : false };
}	
ur_system.browser_abbrev = "ie5";

//GENERAL FUCNTIONS 
function sapUrMapi_focusElement(sId) {
	oElem = document.getElementById(sId);
	if (oElem!=null) {
	   oElem.focus();
	}
}

var bSkipCtrlState  = false;
var bSkipAltState   = false;
var sSkipKey        = 'S';



function sapUrMapi_skipElement(sId,oEvent,bJumpToBegin) {
	iKey    = oEvent.keyCode;
	bAlt    = oEvent.altKey;
	bCtrl   = oEvent.ctrlKey;
	if ((bSkipCtrlState==bCtrl) && (bSkipAltState == bAlt) && (sSkipKey==String.fromCharCode(iKey))) {
		if (bJumpToBegin) {
		  document.getElementById(sId+"-skipstart").focus();
	  } else {
		  document.getElementById(sId+"-skipend").focus();
	  }  
	}
}



//------------------------------------
//BUTTON
//------------------------------------
//FOCUS
function sapUrMapi_Button_focus(sId) {
	sapUrMapi_focusElement(sId);
}

//------------------------------------
//CHECKBOX
//------------------------------------
//TOGGLE CHECKBOX
function sapUrMapi_CheckBox_toggle(sId,e) {
  var oInput = document.getElementById(sId);
  if (oInput.disabled) {return;}
  if (e.srcElement.tagName=="IMG") oInput.checked = (!oInput.checked);
	var oImg = document.getElementById(oInput.id + "_img");
  oImg.className = "urImgCbgImg";
	if (oInput.checked) oImg.className = "urImgCbgImgChk";
}
// SET CHECKED 
function sapUrMapi_CheckBox_setChecked(sId,bChecked) {
	var oInput = document.getElementById(sId);
  oInput.checked = bChecked;
	var oImg = document.getElementById(oInput.id + "_img");
  oImg.className = "urImgCbgImg";
	if (oInput.checked) oImg.className = "urImgCbgImgChk";
}//focus
function urUrMapi_CheckBox_focus(sId) {
  document.getElementById(sId+"_ctrl").focus();
}
//------------------------------------

//------------------------------------
//RADIOBUTTON
//------------------------------------
//TOGGLE RADIOBUTTON
function sapUrMapi_RadioButton_toggle(sId) {
  var oInput = document.getElementById(sId);
  var oInputGrp = document.getElementsByName(oInput.name);
  if (oInput.disabled) {return;}
  if (!oInput.checked){
	oInput.checked = !oInput.checked;
  }
  for (var i = 0; i < oInputGrp.length; i++){
	  var oImg = document.getElementById(oInputGrp[i].id + "_img");
	  if (oImg == null){
	  	continue;
	  }
	  if (oInputGrp[i].checked){
		  oImg.className = "urImgRbgImgChk";
	  }
	  else{
	     oImg.className = "urImgRbgImg";
	  }
  }
}
//focus
function sapUrMapi_RadioButton_focus(sId) {
  //document.getElementById(sId+"_ctrl").focus();
}
//------------------------------------

//------------------------------------
//TABSTRIP
//------------------------------------
//TABSETACTIVEITEM
function sapUrMapi_TabStrip_setItemActive(sId,iIdx) {
	with (document) {
		oTabTable = getElementById(sId+"table");
		iTabLength  = parseInt(oTabTable.getAttribute("tabcount"));
		iSelTab= parseInt(oTabTable.getAttribute("selectedtab"));
		if ((iTabLength==1) || (iSelTab==iIdx)) {
			 return;
		}
		getElementById(sId+"tab"+iSelTab+"_a").className = "urTbsTxtOff"; 
		getElementById(sId+"tab"+iSelTab).className="urTbsLabelOff";
		getElementById(sId+"Prev").className="urTbsFirstAngOffPrevoff";
		getElementById(sId+"Next").className="urTbsLastOffNextoff";
		
		if (iSelTab==0) {
			getElementById(sId+"tab"+(iSelTab+1)+"Ang").className="urTbsAngOffOff";
		} else {
			getElementById(sId+"tab"+(iSelTab)+"Ang").className="urTbsAngOffOff";
			if (iSelTab==iTabLength-1) {
				getElementById(sId+"Prev").className="urTbsFirstAngOffPrevoff"
			} else {
				getElementById(sId+"tab"+(iSelTab+1)+"Ang").className="urTbsAngOffOff";
			}
		}
	
		getElementById(sId+"tab"+iIdx+"_a").className = "urTbsTxtOn"; 
		getElementById(sId+"tab"+iIdx).className="urTbsLabelOn";
		if (iIdx==0) {
			getElementById(sId+"Prev").className="urTbsFirstAngOnPrevoff";
	    getElementById(sId+"tab"+(iIdx+1)+"Ang").className="urTbsAngOnOff";
		} else {
			  getElementById(sId+"tab"+(iIdx)+"Ang").className="urTbsAngOffOn";
			  if (iIdx==iTabLength-1) {
			  	getElementById(sId+"Next").className="urTbsLastOnNextoff";
		    } else {
		     	getElementById(sId+"tab"+(iIdx+1)+"Ang").className="urTbsAngOnOff";
	        getElementById(sId+"Next").className="urTbsLastOffNextoff";
	      }
		}
	  oTabTable.setAttribute("selectedtab",iIdx)
		getElementById(sId+"content"+iSelTab).style.display="none";
		getElementById(sId+"content"+iIdx).style.display="block";
	}
}
//------------------------------------


//------------------------------------
//POPUP MENU
//------------------------------------
//HOVER
var sapPopupMenuLevel = 0;
var subMenus = new Array(null,null,null,null,null,null);
var subMenuItems = new Array(null,null,null,null,null,null);

function sapUrMapi_PopupMenu_hoverItem(mywindow,id,bIn) {
	//find the popup with the event
	oPopup = window.sapPopupStore[mywindow.mylevel];
	if (oPopup) {
	  oWhichRow = oPopup.frame.window.document.getElementById(id);
	} else {
	  oWhichRow = document.getElementById(id);
	}
	if (bIn) {
		sSubMenuId = oWhichRow.submenu;
		if (sSubMenuId!="") {
		  
		  if (!oPopup) {
		  	var iStartLevel=-1;
		  } else {
		  	var iStartLevel=oPopup.level;
		  }		  
		  if (iStartLevel<sapPopupMenuLevel) {
				for (var n=iStartLevel+1;n<sapPopupMenuLevel+1;n++) {
				  if (subMenus[n]!=null) {
  				  subMenus[n].hide();
  		      subMenuItems[n].className = 'urMnuRowOff';
  				}
				}
			  sapPopupMenuLevel=iStartLevel;
			}
		  
			var arrUrls = new Array(ur_system.stylepath+"ur_"+ur_system.browser_abbrev+".css");
			if (top.sapPopupStore) {
			  oStore = top.sapPopupStore
			} else {
			  oStore = window.sapPopupStore;
			}
			if (!oPopup) {
			   subwindow = window;
  			 sapPopupMenuLevel = 0;
			} else {
			subwindow = oPopup.frame.window;
			sapPopupMenuLevel = oPopup.level+1;
			}
			src = subwindow.event.srcElement.parentElement;
			if (src.tagName!='TR') {
				src=src.parentElement;
			}
			subMenu = new sapPopup(window,arrUrls,document.getElementById(sSubMenuId),src,subwindow.event,sapPopupMenuLevel);
		  subMenu.onblur=subMenu.hide;
		  subMenu.positionbehavior = sapPopupPositionBehavior.SUBMENU;
		  
		  subMenu.show();
			subMenus[sapPopupMenuLevel] = subMenu;
			subMenuItems[sapPopupMenuLevel] = oWhichRow;
			if (!oPopup) {
				window.document.onmousedown= sapUrMapi_PopupMenu_hideAll;
				window.document.getElementById("sapPopup_Event").style.display="none";
			}
		} else {
		  if (!oPopup) {
		  	var iStartLevel=-1;
		  } else {
		  	var iStartLevel=oPopup.level;
		  }		  
		  if (iStartLevel<sapPopupMenuLevel) {
				for (var n=iStartLevel+1;n<sapPopupMenuLevel+1;n++) {
				  if (subMenus[n]!=null) {
  				  subMenus[n].hide();
  		      subMenuItems[n].className = 'urMnuRowOff';
  				}
				}
			  sapPopupMenuLevel=iStartLevel;
			}
		}
		oWhichRow.className='urMnuRowOn';
	} else {
		sSubMenuId = oWhichRow.submenu;
		if (sSubMenuId!="") {
  	  //oWhichRow.className = 'urMnuRowOff';
  	} else {
  	  oWhichRow.className = 'urMnuRowOff';
  	}
	}
}

var oPopup;
function 	sapUrMapi_PopupMenu_hideAll() {
  for (var n=0;n<sapPopupMenuLevel+1;n++) {
	 if (subMenus[n]!=null) {
    subMenus[n].hide();
    }
  }
  if (oPopup) oPopup.hide();
  oPopup=null;
  window.document.onmousedown=null;
  sapPopupMenuLevel=0;
}

function 	sapUrMapi_PopupMenu_showMenu(idTrigger,idContent,enumAlignment ) {
	var styles = document.getElementsByTagName("LINK");
	var arrUrls = new Array(ur_system.stylepath+"ur_"+ur_system.browser_abbrev+".css");
	oPopup = new sapPopup(window,arrUrls,document.getElementById(idContent),document.getElementById(idTrigger),window.event,0);
  oPopup.onblur=oPopup.hide;
  if (!enumAlignment) enumAlignment= sapPopupPositionBehavior.MENULEFT
  oPopup.positionbehavior = enumAlignment;
  oPopup.show();
}

function sapUrMapi_PopupMenu_ExecuteLink(id) {
  oItem = window.document.getElementById(id);
  sTarget = oItem.target;
  sHref   = oItem.href;
  oTarget = top.frames[sTarget];
  if (oTarget) {
  	oTarget.location.href=sHref;
  } else {
    window.open(sHref,sTarget,"");
  }
}

//------------------------------------
//TRAY
//------------------------------------
//TOGGLE
//toggles the display of the tray's contents
function sapUrMapi_Tray_toggle( idTray) {
	var elBody = document.all(idTray+"-bd");
	var elExpander = document.all(idTray+"-exp");
	var elHeader = document.all(idTray+"-hd");
	var elExpandState = document.all(idTray+"-es");

	if ( elBody != null && elExpander != null )
	{
		if ( elBody.style.display == "none" )
		{
            /*
			if (HTMLB_SECTION508)
            {
              elHeader.title=String(elHeader.title).replace(TXT_HTMLB_TRAY_CLOSED,TXT_HTMLB_TRAY_OPENED);
              elExpander.title=TXT_HTMLB_TRAY_ICON_CLOSE;
            }
            */
            elBody.style.display = "";
			if ( elExpander.className == "urTrcExpClosedIco" ) {
				elExpander.className = "urTrcExpOpenIco";
		  }
			if ( elHeader.className == "urTrcHdBgClosedIco" )
				elHeader.className = "urTrcHdBgOpenIco";
			if ( elExpandState )
				elExpandState.value = "1";
 		}
		else
		{
            /*
			if (HTMLB_SECTION508)
            {
              elHeader.title=String(elHeader.title).replace(TXT_HTMLB_TRAY_OPENED,TXT_HTMLB_TRAY_CLOSED);
              elExpander.title=TXT_HTMLB_TRAY_ICON_OPEN;
            }
            */
			elBody.style.display = "none";
			if ( elExpander.className == "urTrcExpOpenIco" )
				elExpander.className = "urTrcExpClosedIco";
			if ( elHeader.className == "urTrcHdBgOpenIco" )
				elHeader.className = "urTrcHdBgClosedIco";
			if ( elExpandState )
				elExpandState.value = "0";
		}
	}
	return true;
}

//------------------------------------
//TREE
//------------------------------------
//COLLAPSE ALL
function sapUrMapi_Tree_collapseAll(sIdPrefix) {
    var eRootNode = document.getElementById(sIdPrefix + "-whl");
    var eNodes = eRootNode.getElementsByTagName("DIV");

    //now loop through all the child nodes
    for (var i = 0; i < eNodes.length; i++){
        var sStatus = eNodes[i].getAttribute("status");
        if (sStatus != null && sStatus != "closed"){

            //we have a true node so toggle it's children and norgie
            var childDiv = document.getElementById(eNodes[i].id + ":children");
            var exp = document.getElementById(eNodes[i].id + ":exp");

            //now do our transforms
            if (childDiv != null && childDiv.childNodes.length > 0 && childDiv.style.display != "none"){
                childDiv.style.display="none";
                eLength = exp.className.length;
                eNodes[i].setAttribute("status", "closed");
                exp.className = exp.className.substr(0,eLength-2) + "Clo";
            }
        }
    }
}

/* TREE EXPAND NODE */
function sapUrMapi_Tree_toggle(sNodeId) {
	var nodeDiv = document.getElementById( sNodeId);

	var childrenDiv = document.getElementById( nodeDiv.id + ":children" );
	var expander = document.getElementById( nodeDiv.id + ":exp" );

	if( nodeDiv.status == "closed" )
	{
		if( childrenDiv != null) childrenDiv.style.display="";
		eLength = expander.className.length;
		expander.className = expander.className.substr(0,eLength-3) + "Op";
        nodeDiv.setAttribute("status", "open");
	}
	else
	{
		if( childrenDiv != null) childrenDiv.style.display="none";
		eLength = expander.className.length;
		expander.className = expander.className.substr(0,eLength-2) + "Clo";
		nodeDiv.setAttribute("status", "closed");
	}
}
//TREE INVOKE CLICK
function sapUrMapi_Tree_InvokeNodeClick(sNodeId) {
	document.getElementById(sNodeId+":exp").onclick(); 
}




//------------------------------------
//INPUT FIELD
//------------------------------------
//SET INVALID
function sapUrMapi_InputField_setInvalid(sId,bSet,sTooltip) {
	oInput = document.getElementById(sId);
	sClass = oInput.className;
	sClass = sClass.substring(sClass.indexOf("urEdfi")+6,sClass.length);
	if (sClass==oInput.className) sClass = sClass.substring(sClass.indexOf("urEdf")+5,sClass.length);
	if (bSet) {
		if (!oInput.orgtitle) oInput.setAttribute("orgtitle",oInput.title);
		if (sTooltip) {
			 oInput.title=sTooltip;
		} else {
			 oInput.title="";
		}
	  oInput.className="urEdfi"+sClass;
	} else {
		if (!oInput.orgtitle) oInput.setAttribute("orgtitle",oInput.title);
		if (!sTooltip) {
			 oInput.title=oInput.orgtitle;
	  } else {
			 oInput.title=sTooltip;
	  }
	  oInput.className="urEdf"+sClass;
	}
}

function sapUrMapi_InputField_focus(sId) {
   sapUrMapi_focusElement(sId);
}

function sapUrMapi_InputField_setValue(sId,sValue) {
  document.getElementById(sId).value=sValue;
}

function sapUrMapi_InputField_getValue(sId) {
  return document.getElementById(sId).value;
}

var oDatePicker;
var dActDate;
function sapUrMapi_InputField_showDatePicker(sId,iYear,iMonth,iDay,iFirstDayOfWeek) {
	var arrUrls = new Array(ur_system.stylepath+"ur_"+ur_system.browser_abbrev+".css");
  if (oDatePicker) {
    var oCal = sapUrMapi_DatePicker_make(sId,iYear,iMonth,iDay,iFirstDayOfWeek);
  	oDatePicker.frame.window.document.getElementsByTagName("BODY")[0].innerHTML=oCal.innerHTML;
  } else {
    dActDate  = new Date(iYear,iMonth,iDay);
    var oCal = sapUrMapi_DatePicker_make(sId,iYear,iMonth,iDay,iFirstDayOfWeek);
    oDatePicker = sapPopup(window,arrUrls,oCal,document.getElementById(sId+"-picker"),window.event,0);
		oDatePicker.positionbehavior = sapPopupPositionBehavior.MENURIGHT;
		oDatePicker.onblur = sapUrMapi_hideDatePicker;
		oDatePicker.position.left=oDatePicker.position.left-1;
		oDatePicker.position.top=oDatePicker.position.top-1;
  	oDatePicker.show();
  }

}
var aMonthNames = new Array ("January","February","March","April","May","June","July","August","September","October","November","December");
var aDayNames   = new Array ("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");
var aDayNameAbbrevs   = new Array ("Su","Mo","Tu","We","Th","Fr","Sa");
var aDayCount   = new Array (31,28,31,30,31,30,31,31,30,31,30,31);
function sapUrMapi_hideDatePicker() {
	hidePopupMenu();
	oDatePicker.onblur=null;
	oDatePicker=null;
}
function sapUrMapi_DatePicker_select(sId,e) {
	sDay = e.srcElement.id;
  if (sDay) {
    var aDate = sDay.split("-");
    UR_InputFieldDateSelect(sId,parseInt(aDate[2]),parseInt(aDate[1]),parseInt(aDate[0]));
  }
}
function sapUrMapi_DatePicker_addZero(iInt) {
	return iInt<10?"0"+iInt:iInt;
}


function sapUrMapi_DatePicker_make(sId,iYear,iMonth,iDay,iFirstDayOfWeek) {
  sapUrMapi_Date_setDayCount(iMonth,iYear);
  var oCal = document.getElementById("ur_date_picker");
  if (!oCal) {
	  var oBody = document.getElementsByTagName("BODY")[0];
	  var oCal = document.createElement("SPAN");
	  oCal.id="ur_date_picker";
	  oCal.style.position="absolute";
	  oCal.style.left="-1999px";
	  oCal.style.top="-1999px";
	  oBody.appendChild(oCal);
  }
  var sCalHtml = "<table onmousedown=\"me.sapUrMapi_DatePicker_select('"+sId+"',event);\" class=urCalPicWhl cellpaddding=0 cellspacing=0 border=0><tr>";
  var pm = iMonth-1;
  var nm = iMonth+1;
  var dy = iDay;
  var py = iYear;
  var ny = iYear;
  if (pm==-1) {pm = 11;py--;}
  if (nm==12) {nm = 0;ny++;}
  if (dy>28) {dy=25}
  sCalHtml    += "<td class=urCalArr onclick=\"me.sapUrMapi_InputField_showDatePicker('"+sId+"',"+py+","+pm+","+dy+","+iFirstDayOfWeek+");\">&nbsp;&laquo;&laquo;</td>";
  sCalHtml    += "<td colspan=5 class=urCalHdr nowrap align=center>"+aMonthNames[iMonth]+" "+iYear+"</td>";
  sCalHtml    += "<td class=urCalArr onclick=\"me.sapUrMapi_InputField_showDatePicker('"+sId+"',"+ny+","+nm+","+dy+","+iFirstDayOfWeek+");\">&nbsp;&raquo;&raquo;</td></tr>";
  sCalHtml    += "<tr>";
  for (var i=iFirstDayOfWeek;i<aDayNameAbbrevs.length;i++) {
    sCalHtml    += "<td class=urCalName>"+aDayNameAbbrevs[i]+"</td>";
  }
  for (var i=0;i<iFirstDayOfWeek;i++) {
    sCalHtml    += "<td class=urCalName>"+aDayNameAbbrevs[i]+"</td>";
  }
  var dDate  = new Date(iYear,iMonth,1);
  if (iFirstDayOfWeek>dDate.getDay()){
    var dStart = new Date(dDate.getTime()-((dDate.getDay()-iFirstDayOfWeek+7)*1000*60*60*24));
  } else {
    var dStart = new Date(dDate.getTime()-((dDate.getDay()-iFirstDayOfWeek)*1000*60*60*24));
  }
  for (var i=0;i<6;i++) {
    sCalHtml    += "<tr class=urCalRow style=\"cursor:hand;\">";
  	for (var n=0;n<7;n++) {
				var sClass="";
				var sId=dStart.getUTCFullYear()+"-"+(dStart.getMonth()+1)+"-"+dStart.getDate();
	  		if (dStart.getMonth()!=iMonth) {
	  			sClass="urCalIna";
	  		} else {
	  			sClass="";
	  		}
	  		if ((dStart.getYear()==dActDate.getYear()) && (dStart.getMonth()==dActDate.getMonth()) && (dStart.getDate()==dActDate.getDate())) {
	  			sClass+=" urCalDaySelEmp";
	  		}
	  		if ((dStart.getYear()==new Date().getYear()) && (dStart.getMonth()==new Date().getMonth()) && (dStart.getDate()==new Date().getDate())) {
	  			sClass+=" urCalTod";
	  		}
	      sCalHtml    += "<td id="+sId+" class="+sClass+">"+dStart.getDate()+"</td>";
	  		
	  		dStart = new Date(dStart.getTime()+(1000*60*60*24));
  	} 
  	if ((dStart.getDay()==iFirstDayOfWeek)&&(dStart.getMonth()>iMonth)) {
  	  //break;
  	}
    sCalHtml    += "</tr>";
  }
  
  dStart
  sCalHtml    += "</tr></table>";
  oCal.innerHTML=sCalHtml;
 
  return oCal;
  
}

function sapUrMapi_Date_setDayCount(iMonth, iYear) {   
	if ((iMonth == 1) && ((iYear % 400 == 0)) || ((iYear % 4 == 0) && (iYear % 100 != 0))) aDayCount[1] = 29;
}




//------------------------------------
//TABLEVIEW
//------------------------------------
//SELECTROW
function sapUrMapi_Table_selectRow(sTableId,iRow,bOnChange) {
  var oInput = document.getElementById(sTableId+iRow);
  var oInputGrp = document.getElementsByName(oInput.name);
  if (oInputGrp.length==1) {
    if (!bOnChange) oInput.checked = (!oInput.checked);
		var oImg = document.getElementById(oInput.id + "_img");
	  oImg.className = "urImgCbgImg";
		if (oInput.checked) oImg.className = "urImgCbgImgChk";
		// find corresponding row
		var oRow = oInput.parentElement;
		while(!oRow.rr) {
			oRow=oRow.parentElement;
		}
		for (var n=0;n<oRow.cells.length;n++) {
			var oCell = oRow.cells[n];
			if (oInput.checked) {
			  oCell.setAttribute("unselectedclass",oCell.className);
			  //oCell.className = oCell.className + " urETbvCellSel";
			} else {
	  	  //oCell.className=oCell.getAttribute("unselectedclass");
			}
		}
  } else {
   if (!oInput.checked){
	   oInput.checked = !oInput.checked;
   }
    for (var i = 0; i < oInputGrp.length; i++){
	    var oImg = document.getElementById(oInputGrp[i].id + "_img");
	    if (oInputGrp[i].checked){
		    oImg.className = "urImgRbgImgChk";
	    } else {
	     oImg.className = "urImgRbgImg";
	    }
   }
  }
}

function sapUrMapi_Table_getClickedRowIndex(sId) {
 	 oSrc = window.event.srcElement;
   var obj = event.srcElement;
   while ( (obj!=null) && (obj.tagName!='TD') )
      obj = obj.parentElement;
   if(obj==null) return;
   var parent = obj.parentElement;
   var rowIndex = parent.rr;
   return parseInt(parent.rr);
} 

function sapUrMapi_Table_getClickedColIndex(sId) {
 	 oSrc = window.event.srcElement;
   var obj = event.srcElement;
   while ( (obj!=null) && (obj.tagName!='TD') )
      obj = obj.parentElement;
   oCell = obj;
   while ( (obj!=null) && (obj.tagName!='TABLE') )
      obj = obj.parentElement;
   if ( obj==null ) return;
   var colidx =  oCell.cellIndex - parseInt( obj.nmi );
   return colidx;
} 



//------------------------------------
//DROPDOWNLISTBOX
//------------------------------------
//GET SELECTION
function sapUrMapi_DropDownListBox_getSelectedKey(sId) {
	oSelect = document.getElementById(sId);
	return oSelect.options[oSelect.selectedIndex].value;
}
//SET SELECTION
function sapUrMapi_DropDownListBox_setSelectedKey(sId,sKey) {
	oSelect = document.getElementById(sId);
	for (var n=0;n<oSelect.options.length;n++) {
		if (oSelect.options[n].value==sKey) {
			oSelect.selectedIndex=n; return; 
		}
	}
}
//GET SELECTED INDEX
function sapUrMapi_DropDownListBox_getSelectedIndex(sId) {
	return document.getElementById(sId).selectedIndex;
}
//SET SELECTED INDEX
function sapUrMapi_DropDownListBox_setSelectedIndex(sId,iIndex) {
	document.getElementById(sId).selectedIndex=iIndex;
}
//FOCUS
function sapUrMapi_DropDownListBox_focus(sId) {
   sapUrMapi_focusElement(sId);
}

//------------------------------------
//LISTBOX
//------------------------------------
//FOCUS
function sapUrMapi_ListBox_focus(sId) {
   sapUrMapi_focusElement(sId);
}

//------------------------------------
//MESSAGEBAR
//------------------------------------
enumUrMessageBarType = {ERROR:"Error",WARNING:"Warning",OK:"Ok",STOP:"Stop",LOADING:"Loading",NONE:"None"};

function sapUrMapi_MessageBar_setType(sId,vMessageBarType) {
	var oMBar = document.getElementById(sId);
	if (vMessageBarType==enumUrMessageBarType.NONE) {
		oMBar.style.display = 'none'; 
		return;
	} else {
		if (vMessageBarType==enumUrMessageBarType.ERROR) {
		   oMBar.className="urMsgBarErr";	
		} else {
		   oMBar.className="urMsgBarStd";	
		}
		oMBar.style.display = 'block';
    var oMBarImg  = document.getElementById(sId+"_ur_bar_img");
    oMBarImg.className = "urMsgBarImg"+vMessageBarType;
	}
}

function sapUrMapi_MessageBar_getType(sId) {
	var oMBar = document.getElementById(sId);
  if (oMBar.style.display == 'none') {
  	return enumUrMessageBarType.NONE;
  } else {
    var oMBarImg  = document.getElementById(sId+"_ur_bar_img");
    alert(oMBarImg.className);
    if ((oMBarImg.className).indexOf(enumUrMessageBarType.ERROR)>-1) return enumUrMessageBarType.ERROR;
    if ((oMBarImg.className).indexOf(enumUrMessageBarType.WARNING)>-1) return enumUrMessageBarType.WARNING;
    if ((oMBarImg.className).indexOf(enumUrMessageBarType.LOADING)>-1) return enumUrMessageBarType.LOADING;
    if ((oMBarImg.className).indexOf(enumUrMessageBarType.STOP)>-1) return enumUrMessageBarType.STOP;
    if ((oMBarImg.className).indexOf(enumUrMessageBarType.OK)>-1) return enumUrMessageBarType.OK;
  }
}
function sapUrMapi_MessageBar_setText(sId,sText) {
	var oMBarText = document.getElementById(sId+"_ur_bar_text");
	oMBarText.innerText = sText;
}
function sapUrMapi_MessageBar_getText(sId) {
	var oMBarText = document.getElementById(sId+"_ur_bar_text");
	return oMBarText.innerText;
}

//LABEL
function sapUrMapi_Label_FocusLabeledElement(sForId) {
  sapUrMapi_focusElement(sForId);	
}
