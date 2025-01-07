package com.sap.engine.lib.schema.validator.identity.xpathmatcher;

import com.sap.engine.lib.schema.util.LexicalTokenizer;
import com.sap.engine.lib.schema.util.Tools;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: ivan-m
 * Date: 2004-2-13
 * Time: 12:53:33
 * To change this template use Options | File Templates.
 */
public final class NodeMatchingStructureFactory {

  protected static NodeMatchingStructure create(String xPathExpr, Hashtable prefixesMapping) {
    if(xPathExpr.indexOf("|") >= 0) {
      return(createChoiceNodeMatchingStructure(xPathExpr, prefixesMapping));
    }
    return(createNodeMatchingStructure(xPathExpr, prefixesMapping));
  }
  
  private static NodeMatchingStructure createNodeMatchingStructure(String xPathExpression, Hashtable prefixesMapping) {
    return(createNodeMatchingStructure(new LexicalTokenizer(xPathExpression, new String[]{"//", "/"}), prefixesMapping));
  }

  private static ElementMatchingStructure createChoiceNodeMatchingStructure(String xPathExpression, Hashtable prefixesMapping) {
    StringTokenizer strTokenizer = new StringTokenizer(xPathExpression, "|");
    NodeMatchingStructure[] structures = new NodeMatchingStructure[strTokenizer.countTokens()]; 
    for(int i = 0; i < structures.length; i++) {
      String elemMatchingStrucXPathExpr = strTokenizer.nextToken();
      structures[i] = createNodeMatchingStructure(elemMatchingStrucXPathExpr, prefixesMapping);
    }
    return(new ChoiceElementMatchingStructure(structures));
  }

  private static NodeMatchingStructure createNodeMatchingStructure(LexicalTokenizer tokenizer, Hashtable prefixesMapping) {
    String token = null;
    while((token = tokenizer.next()) != null) {
      if(!token.equals("/")) {
        if(token.equals("//")) {
          token = tokenizer.next();
          if(!token.equals(".")) {
            if(token.startsWith("@")) {
              token = token.substring(1);
							String[] uriAndName = parseQName(token, prefixesMapping);
							return(new DescendentAttributeMatchingStructure(uriAndName[0], uriAndName[1]));
            } else {
							String[] uriAndName = parseQName(token, prefixesMapping);
							return(new DescendentElementMatchingStructure(uriAndName[0], uriAndName[1], createNodeMatchingStructure(tokenizer, prefixesMapping)));
            }
          }
        } else {
          if(token.startsWith("@")) {
            token = token.substring(1);
						String[] uriAndName = parseQName(token, prefixesMapping);
						return(new AttributeMatchingStructure(uriAndName[0], uriAndName[1]));
          } else if(token.startsWith(".")) {
          	return(new ContextElementMatchingStructure(createNodeMatchingStructure(tokenizer, prefixesMapping)));
          } else {
						String[] uriAndName = parseQName(token, prefixesMapping);
						return(new ChildElementMatchingStructure(uriAndName[0], uriAndName[1], createNodeMatchingStructure(tokenizer, prefixesMapping)));
          }
        }
      }
    }
    return(null);
  }

  private static String[] parseQName(String qName, Hashtable prefixesMapping) {
  	if(qName.equals("*")) {
  		return(new String[]{null, null});
  	} else {
			String[] prefixAndName = Tools.parseQName(qName);
			String prefix = prefixAndName[0];
			String name = prefixAndName[1].equals("*") ? null : prefixAndName[1]; 
			String uri = null;
			if(prefix == null || prefix.equals("")) {
				uri = "";
			} else {
				uri = (String)(prefixesMapping.get(prefix));
				if(uri == null) {
					uri = "";
				}
			}
			return(new String[]{uri, name});
  	}
  }
}
