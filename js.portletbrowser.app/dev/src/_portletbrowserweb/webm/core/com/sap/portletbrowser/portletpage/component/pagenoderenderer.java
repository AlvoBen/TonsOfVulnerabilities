package com.sap.portletbrowser.portletpage.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class PageNodeRenderer {
	
		//organize internal tree of renderers
		protected List<PageNodeRenderer> children;
	
		public abstract void renderBefore(PortalPageContext ctx) throws IOException;
		public abstract void renderAfter(PortalPageContext ctx) throws IOException;
		
		public void renderChildren(PortalPageContext ctx) throws IOException{
			if (children != null && children.size()>0){
				Iterator<PageNodeRenderer> iter = children.iterator();
				while(iter.hasNext()){
					iter.next().render(ctx);
				}
			}
		}
		
		public void render(PortalPageContext ctx) throws IOException{
			this.renderBefore(ctx);
			this.renderChildren(ctx);
			this.renderAfter(ctx);
		}

		
		public void add(PageNodeRenderer portletNode) {
			if (children != null){
				children.add(portletNode);
				return;
			}
			
			children = new ArrayList<PageNodeRenderer>();
			children.add(portletNode);
			
		}
		

}
