package com.sap.portletbrowser.portletpage.component;

import java.io.IOException;
import java.util.Iterator;

public class LayoutNode extends PageNodeRenderer {

	public static enum Direction {
		HORIZONTAL, VERTICAL;
	}

	// this is the default direction of rendering
	public Direction direction = Direction.VERTICAL;

	public LayoutNode(String direction2) {
		setDirection(direction2);
	}

	public void renderBefore(PortalPageContext ctx) throws IOException {
		ctx.getResponse().getWriter().write("<table width='100%'>");
	}

	public void renderAfter(PortalPageContext ctx) throws IOException {
		ctx.getResponse().getWriter().write("</table>");

	}

	public void renderChildren(PortalPageContext ctx) throws IOException {

		if (direction == Direction.HORIZONTAL) {
			ctx.getResponse().getWriter().write("<tr>");
		}

		// <tr> if v
		if (children != null && children.size() > 0) {
			Iterator<PageNodeRenderer> iter = children.iterator();
			while (iter.hasNext()) {
				PageNodeRenderer node = iter.next();
				if (direction == Direction.HORIZONTAL) {
					int i = children.size();
					if (i >= 2) {
						int width = new Double(100 / i).intValue();
						ctx.getResponse().getWriter().write(
								"<td width=\"" + width
										+ "%\" style=\"vertical-align: top\">");
					} else {
						ctx.getResponse().getWriter().write("<td>");
					}
				} else {
					ctx.getResponse().getWriter().write("<tr><td>");
				}
				node.render(ctx);
				if (direction == Direction.HORIZONTAL) {
					ctx.getResponse().getWriter().write("</td>");
				} else {
					ctx.getResponse().getWriter().write("</td></tr>");
				}
			}
		}

		if (direction == Direction.HORIZONTAL) {
			ctx.getResponse().getWriter().write("</tr>");
		}

	}

	public void setDirection(String direction) {
		if ("horizontal".equalsIgnoreCase(direction)) {
			this.direction = Direction.HORIZONTAL;
		}
	}

}
