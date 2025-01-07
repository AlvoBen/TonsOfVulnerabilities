package com.sap.ip.j2eeengine.consistency;

import java.io.Serializable;
import java.util.Vector;

public class CacheMessageContainer implements Serializable {
		private Vector mContent;
		
		public CacheMessageContainer(Vector content)
		{
			mContent = content;
		}

		public Vector getContent()
		{
			return mContent;
		}

		public String toString()
		{
			StringBuffer result = new StringBuffer();
			result.append("[");
			if (mContent != null)
			{
				boolean isFirst=true;
				for (java.util.Iterator iter=mContent.iterator();iter.hasNext();)
				{
					if (!isFirst)
					{
						result.append(",");
					}
					result.append(iter.next());
					isFirst=false;
				}				
			}
			result.append("]");
			return result.toString();
		}
}
