<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components"%>

    <script>
        <!--
        function getit(){
        var test = document.getElementById("hidden_tab");
        var d1;
        if (test){
        test = test.firstChild;
        //alert("tab_name:"+test.value);
        if (test.value == "tab1form"){
        UR_TabChange('urID1034712678372',0,0,event);
        }
        if (test.value == "tab2form"){
        UR_TabChange('urID1034712678372',1,0,event);
        }
        if (test.value == "tab3form"){
        UR_TabChange('urID1034712678372',2,0,event);
        }
        }	
        }
		
		
        function setTab(tab){
        	elems = document.getElementsByName('hidden_tab');
        	//alert(elems);
        	for(var i = 0; i < elems.length; i++){
        		//alert(elems[i].id);
        		elems[i].firstChild.value = tab;
        	}
        
        }
         
        -->
    </script>
  
    <body onLoad="getit();">
  		
        <f:view>
            <p:portletPage>
		
               
                <h:form id="jdbc">
            		
                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td width="100%" colspan="2">
                                <table border="0" cellspacing="1" cellpadding="1" class="urTrcHdBgOpen" bgcolor="#FFFFFF" width="100%">
                                    <!--1st row: navigation path -->
                                    <tr>
                                        <td class="urTrcHdNotch" align="left"><img width="6px" src="resources/common/1x1.gif"/></td> 
                                        <td width="100%" bgcolor="#FFFFFF" class="urTrcHdBgOpen">
                                            <font class="urTrcTitHdr">
                                                <h:commandLink id="jdbc_root" value="JDBC Resources > JDBC Data Source" action="test"/>
                                                <h:outputText value=" > " rendered="#{JDBCResources.selectedResourceName != null}"/>
                                                <h:outputText value="#{JDBCResources.selectedResourceName}" rendered="#{JDBCResources.selectedResourceName != null}"/>
                                            </font>
                                        </td>
                                    </tr>
					
                                    <!--2nd row: Create / Remove -->
                                    <tr>
                                        <td valign="top" bgcolor="#ffffff" width="100%" colspan="2">&nbsp;
                                          <!--  <h:commandLink id="createJDBCResources" styleClass="urBtnEmphDsbl" disabled="true" value="Create" action="#{JDBCResources.createDataSource}" /> &nbsp;
                                            <h:commandLink id="removeJDBCResources" styleClass="urBtnEmph" value="Remove" action="#{JDBCResources.removeDataSource}" /> &nbsp; -->
                                        </td>
                                    </tr>
						
                                    <!--  3rd row: tabs with JDBC Data Sources -->
                                    <tr valign="top">
                                        <td colspan="2">
                                            <div id="urID1034712677991" style="position:relative;height:10em;overflow:auto;">
                                                <h:dataTable id="jdbc" headerClass="hdrClss" columnClasses="stdrwClss" value="#{JDBCResources.jdbc_resources}" var="jdbc" border='0' width="100%">
                                                    <h:column>                               
                                                        <h:selectBooleanCheckbox value="#{jdbc.selected}"/>
                                                    </h:column> 
                                                    <h:column>
                                                        <f:facet name="header"><h:outputText value="JDBC Data Source"/></f:facet> 
                                                        <h:commandLink value="#{jdbc.name}" actionListener="#{JDBCResources.selectedds}"/> 		                 	
                                                    </h:column> 
                                                </h:dataTable>	
                                            </div>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
      
  		
 	     									
                </h:form>			
                 
                <h:panelGrid columns="1" rendered="#{JDBCResources.selectedJDBCDataSource != null}">
                    <h:panelGroup>	
                        <table border="0" cellspacing="0" cellpadding="0" class="urGrcWhl" style="width:100%;background-color:#ffffff;">
                            <tbody>
                                <tr>
                                    <td>
                                        <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
                                            <tbody><tr valign="top"><td>
				  			
				  			
                                                <div id="urID1034712678372scroll" class="urTbsstripScrollDIV">
                                                    <table id="urID1034712678372table" class="urTbsstripTABLE" border="0" cellpadding="0" cellspacing="0" selectedtab="0" tabcount="3">
                                                        <tbody>
                                                            <tr>
                                                                <td id="urID1034712678372Prev" class="urTbsFirstAngOnPrevoff" valign="top"> </td>
                                                                <td id="urID1034712678372tab0" nowrap class="urTbsLabelOn"><span id="urID1034712678372tab0_a" onClick="UR_TabChange('urID1034712678372',0,0,event);" title="DataSource" class="urTbsTxtOn" style="text-decoration:none;">DataSource Properties</span></td>
                                                                <td valign="top" id="urID1034712678372tab1Ang" valign="top" class="urTbsAngOnOff"> </td>
                                                                <td id="urID1034712678372tab1" nowrap class="urTbsLabelOff"><span title="DataSource Descriptors" id="urID1034712678372tab1_a" onClick="UR_TabChange('urID1034712678372',1,0,event);" class="urTbsTxtOff">DataSource Additional Properties</span></td>
                                                                <td valign="top" id="urID1034712678372tab2Ang" valign="top" class="urTbsAngOffOff"> </td>
                                                                <td id="urID1034712678372tab2" nowrap class="urTbsLabelOff"><span title="Tab 2" id="urID1034712678372tab2_a" onClick="UR_TabChange('urID1034712678372',2,0,event);" class="urTbsTxtOff">DataSource Aliases</span></td>
                                                                <td id="urID1034712678372Next" class="urTbsLastOffNextoff" valign="top"> </td>
                                                            </tr>
                                                        </tbody>
                                                    </table>
                                                </div>
				  			
				  			
				  			
                                                <table class="urTbsWhl" width="100%" cellpadding="0" cellspacing="0" border="0">
                                                    <tbody>
                                                        <tr>
                                                            <td align="left" valign="top" class="urTbsCnt">
                                                                <!--DataSource Detail -->
                                                                <div id="urID1034712678372content0" class="urTbsDspSel" style="width:100%;height:200;line-height:1em;">
                                                                    <h:form id="tab1form">
                                                                    <div id="hidden_tab">
                                                                                <h:inputHidden id="tab_name" value="#{JDBCResources.mode}"/>
                                                                    </div>
                                                                    <h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">
                                                                        <f:facet name="caption">
                                                                            <h:outputText  value="#{JDBCResources.selectedJDBCDataSource.name}"/>
                                                                        </f:facet>
                                                                        <h:outputLabel value="SystemDataSource"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.system_datasource}"/>
                                                                        <h:outputLabel value="Application"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.name}"/>
                                                                        <h:outputLabel value="Description"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.description}"/>	
                                                                        <h:outputLabel value="Driver Name"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.driver}"/>	
                                                                        <h:outputLabel value="JDBC Version" rendered="#{!JDBCResources.selectedJDBCDataSource.system_datasource}"/>
                                                                        <h:selectOneMenu value="#{JDBCResources.selectedJDBCDataSource.jdbcVersion}" disabled="true" rendered="#{!JDBCResources.selectedJDBCDataSource.system_datasource}" onchange="setTab('tab1form');submit();">
                                                                            <f:selectItem itemLabel="1.x (no XA support)" itemValue="1"/>
                                                                            <f:selectItem itemLabel="2.0 (with XA support)" itemValue="2"/>
				    										
                                                                        </h:selectOneMenu>
                                                                        <f:facet name="footer">
                                                                            <h:panelGroup rendered="#{!JDBCResources.selectedJDBCDataSource.system_datasource}">
                                                                                <h:panelGrid id="jdbc1" captionClass="capHdr" columns="2" columnClasses="stdrwClss" rendered="#{JDBCResources.selectedJDBCDataSource.jdbcVersion ==1}">
                                                                                    <f:facet name="caption">
                                                                                        <h:outputText  value="JDBC 1.x"/>
                                                                                    </f:facet>
                                                                                    <h:outputLabel value="Driver Class Name"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.driverClass}"/>
                                                                                    <h:outputLabel value="DB URL"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.database_URL}"/>
                                                                                    <h:outputLabel value="User Name"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.user}"/>	
                                                                                    <h:outputLabel value="Password"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.password}"/>
                                                                                </h:panelGrid>
                                                                                <h:panelGrid id="jdbc2" captionClass="capHdr" columns="2" columnClasses="stdrwClss" rendered="#{JDBCResources.selectedJDBCDataSource.jdbcVersion ==2}">
                                                                                    <f:facet name="caption">
                                                                                        <h:outputText  value="JDBC 2.0"/>
                                                                                    </f:facet>
                                                                                    <h:outputLabel value="Object Factory"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.objectFactory}"/>
                                                                                    <h:outputLabel value="DataSource Type"/>
                                                                                    <h:selectOneMenu value="#{JDBCResources.selectedJDBCDataSource.dsType}" disabled="#{JDBCResources.selectedJDBCDataSource.system_datasource}" onchange="setTab('tab1form');submit();">
                                                                                        <f:selectItem itemLabel="Connection Pooled Data Source" itemValue="1"/>
                                                                                        <f:selectItem itemLabel="XA Data Source" itemValue="2"/>
                                                                                    </h:selectOneMenu>
                                                                                
                                                                                    <h:outputLabel value="CPDS Class Name" rendered="#{JDBCResources.selectedJDBCDataSource.dsType == 1}"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.cpdsClassName}" rendered="#{JDBCResources.selectedJDBCDataSource.dsType == 1}"/>	
                                                                                    <h:outputLabel value="XADS Class Name" rendered="#{JDBCResources.selectedJDBCDataSource.dsType == 2}"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.xadsClassName}" rendered="#{JDBCResources.selectedJDBCDataSource.dsType == 2}"/>
                                                                                </h:panelGrid>
                                                                            </h:panelGroup>
                                                                        </f:facet>
                                                                                           
                                                                    </h:panelGrid>
                                                                    </h:form>

				  						
                                                                </div>
				  					
                                                                <!--DataSource Aliases Detail -->
                                                                <div id="urID1034712678372content1" class="urTbsDsp" style="width:100%;height:200;line-height:1em;">
                                                                <h:form id="tab2form">   
                                                                      <div id="hidden_tab">
                                                                                <h:inputHidden id="tab_name" value="#{JDBCResources.mode}"/>
                                                                            </div>
                                                                        <h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">
                                                                            <f:facet name="caption">
                                                                                <h:outputText  value="#{JDBCResources.selectedJDBCDataSource.name}"/>
                                                                            </f:facet>
                                                                            <h:outputLabel value="Initial Connections"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.initialConnections}"/>
                                                                            <h:outputLabel value="Maximum Connections"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.maxConnections}"/>
                                                                            <h:outputLabel value="Maximum Time to Wait for Connection"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.maxTimeToWaitConnection}"/>		
                                                                            <h:outputLabel value="Connection LifeTime"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.connectionLifetime}"/>	
                                                                            <h:outputLabel value="Cleanup Thread"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.runCleanupThread}"/>	
                                                                            <h:outputLabel value="Default Connection Isolation"/>
                                                                            <h:selectOneMenu value="#{JDBCResources.selectedJDBCDataSource.isolationLevel}" disabled="true">
                                                                                <f:selectItem itemLabel="Default" itemValue="-1"/>
                                                                                <f:selectItem itemLabel="Transaction None" itemValue="0"/>
                                                                                <f:selectItem itemLabel="Transaction Read Uncommited" itemValue="1"/>
                                                                                <f:selectItem itemLabel="Transaction Read Commited" itemValue="2"/>				    											
                                                                                <f:selectItem itemLabel="Transaction Repeatable Read" itemValue="4"/>
                                                                                <f:selectItem itemLabel="Transaction Serializable" itemValue="8"/>		
                                                                            </h:selectOneMenu>
                                                                                              
                                                                            <h:outputLabel value="SQL Engine"/><h:outputText value="#{JDBCResources.selectedJDBCDataSource.sqlEngine}"/>
                                                                    
                                                                            <f:facet name="footer">
                                                                                <h:panelGroup rendered="#{!JDBCResources.selectedJDBCDataSource.system_datasource}">
                                                                                    <h:panelGrid id="jdbc1prop" captionClass="capHdr" columns="2" columnClasses="stdrwClss" rendered="#{JDBCResources.selectedJDBCDataSource.jdbcVersion ==1}">
                                                                                        <f:facet name="caption">
                                                                                            <h:outputText  value="Properties"/>
                                                                                        </f:facet>
                                                                                        <h:dataTable id="jdbcprop1tbl" headerClass="hdrClss" columnClasses="stdrwClss" value="#{JDBCResources.selectedJDBCDataSource.properties1x}" var="prop" border='0' width="100%">
                                                                                            <h:column>                               
                                                                                                <h:selectBooleanCheckbox value="true"/>
                                                                                            </h:column> 
                                                                                            <h:column>
                                                                                                <f:facet name="header"><h:outputText value="Name"/></f:facet> 
                                                                                                <h:outputText value="#{prop.name}"/> 		                 	
                                                                                            </h:column> 
                                                                                            <h:column>
                                                                                                <f:facet name="header"><h:outputText value="Value"/></f:facet> 
                                                                                                <h:outputText value="#{prop.value}"/> 		                 	
                                                                                            </h:column> 
                                                                                        </h:dataTable>	
                                                                                    </h:panelGrid>  
                                                                            
                                                                            
                                                                            
                                                                                    <h:panelGrid id="jdbc2prop" captionClass="capHdr" columns="2" columnClasses="stdrwClss" rendered="#{JDBCResources.selectedJDBCDataSource.jdbcVersion ==2}">
                                                                                        <f:facet name="caption">
                                                                                            <h:outputText  value="Properties"/>
                                                                                        </f:facet>
                                                                                        <h:dataTable id="jdbcprop2tbl" headerClass="hdrClss" columnClasses="stdrwClss" value="#{JDBCResources.selectedJDBCDataSource.properties2x}" var="prop" border='0' width="100%">
                                                                                            <h:column>                               
                                                                                                <h:selectBooleanCheckbox value="true"/>
                                                                                            </h:column> 
                                                                                            <h:column>
                                                                                                <f:facet name="header"><h:outputText value="Name"/></f:facet> 
                                                                                                <h:outputText value="#{prop.name}"/> 		                 	
                                                                                            </h:column> 
                                                                                            <h:column>
                                                                                                <f:facet name="header"><h:outputText value="Value"/></f:facet> 
                                                                                                <h:outputText value="#{prop.value}"/> 		                 	
                                                                                            </h:column> 
                                                                                        </h:dataTable>	
                                                                                    </h:panelGrid>     
                                                                                </h:panelGroup>
                                                                            </f:facet>   
                                                                    
                                                                    
                                                                    
                                                                    
                                                                        </h:panelGrid>	
                                                                </h:form>    
                                                                </div>
				  					
                                                                <!--DataSource Aliases Detail -->
                                                                <div id="urID1034712678372content2" class="urTbsDsp" style="width:100%;height:200;line-height:1em;">
                                                                        <h:form id="tab3form">
                                                                        <h:panelGrid columns="1">
                                                                        <h:panelGroup>
                                                                            <div id="hidden_tab">
                                                                                <h:inputHidden id="tab_name" value="#{JDBCResources.mode}"/>
                                                                            </div>
				  	
  																																	
                                                                            <h:dataTable id="aliases" headerClass="hdrClss" columnClasses="stdrwClss" value="#{JDBCResources.selectedJDBCDataSource.aliases}" var="dsalias" border='0' width="100%">
                                                                                <h:column>
                                                                                    <h:selectBooleanCheckbox value="#{dsalias.selected}" rendered="#{!JDBCResources.selectedJDBCDataSource.system_datasource}"/>
                                                                                </h:column>
                                                                                <h:column>
                                                                                    <f:facet name="header"><h:outputText value="JDBC DataSource Name"/></f:facet> 
                                                                                    <h:outputText value="#{dsalias.name}"/> 		                 	
                                                                                </h:column> 
                                                                                <h:column>
                                                                                    <f:facet name="header">	<h:outputText value="JDBC DataSource Deployer"/></f:facet> 
                                                                                    <h:outputText value="#{dsalias.deployer}"/> 		                 	
                                                                                </h:column> 
                                                                            </h:dataTable>
                                                                            <!-- datasource aliases operations -->
                                                                    
                                                                            <h:panelGrid columns="2"  rendered="#{!JDBCResources.selectedJDBCDataSource.system_datasource}">
                                                                                <h:commandLink id="removeJDBCDataSourceAlias" styleClass="urBtnEmph" value="Remove Selected DataSourceAliases" action="#{JDBCResources.removeDataSourceAlias}"  onclick="setTab('tab3form');" style="width:30ex"/>  
                                                                                <h:outputText value="" style="width:30ex"/>
                                                                                <h:inputText label="DataSourceAlias Name" title="Name"  style="width:30ex" value="#{JDBCResources.updatedDataSourceAlias}"/>
                                                                                <h:commandLink id="createJDBCDataSourceAlias" styleClass="urBtnEmph" value="Add DataSource Alias" action="#{JDBCResources.createDataSourceAlias}" style="width:30ex" onclick="setTab('tab3form');"/> 
                                                                                <h:selectOneMenu style="width:30ex"  value="#{JDBCResources.redirectToDataSource}" >
                                                                                    <f:selectItems value="#{JDBCResources.dataSourceItemList}"/>
                                                                                </h:selectOneMenu>
                                                                                <h:commandLink id="redirectJDBCDataSourceAlias" style="width:30ex"  styleClass="urBtnEmph" value="Redirect  Selected DataSource Aliases" action="#{JDBCResources.redirectDataSourceAlias}" onclick="setTab('tab3form');"/> 
                                                                            </h:panelGrid>
                                                                         
                                                                        </h:panelGroup>    
                                                                          </h:panelGrid> 
                                                                        </h:form>
                                                              </div>

                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                                </div>
                                            </td></tr></tbody>
                                        </table>
                                    </td>
                                </tr>
                            </tbody>
                        </table>      
        
  								
  								
  								
  								
                    </h:panelGroup>
                </h:panelGrid>


            </p:portletPage>       
        </f:view>
    </body>
