/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.NamespaceContext;

public class PathReference extends Reference {
    private final List<PathReferenceStep> _steps;
    
    public PathReference() {
        _steps = Collections.emptyList();
    }
    
    public PathReference(List<PathReferenceStep> pSteps) {
        _steps = pSteps;
    }

    /**
     * 
     * @param pReference
     * @param pSubPath Must be trimmed an must start with '#'
     * @param pNamespaceContext
     */
    public PathReference(PathReference pReference, String pSubPath, NamespaceContext pNamespaceContext) {
        if (pSubPath.startsWith("#/")) {
            _steps = new ArrayList<PathReferenceStep>();
            iteratePathSteps(pSubPath.substring(2), pNamespaceContext);
        } else {
            _steps = new ArrayList<PathReferenceStep>(pReference._steps);
            iteratePathSteps(pSubPath.substring(1), pNamespaceContext);
        }
    }
    
    public PathReference(PathReference pReference, PathStep pStep) {
        _steps = new ArrayList<PathReferenceStep>(pReference._steps.size() + 1);
        _steps.addAll(pReference._steps);
        _steps.add(pStep);
    }

    public PathReference(PathReferenceStep pStep) {
        _steps = Collections.singletonList(pStep);
    }

    /**
     * @param pReference
     */
    private void iteratePathSteps(String pReference, NamespaceContext pNamespaceContext) {
        StringTokenizer tokenizer = new StringTokenizer(pReference, "/");
        while (tokenizer.hasMoreTokens()) {
            String step = tokenizer.nextToken();
            if (!"..".equals(step)) {
                int index = 0;
                int lastDot = step.lastIndexOf('.');
                if (lastDot > 0 && step.charAt(lastDot-1) != '\\') {
                    index = Integer.parseInt(step.substring(lastDot+1));
                    step = step.substring(0, lastDot);
                } else {
                    int openParenthesis = step.lastIndexOf('[');
                    if (openParenthesis > 0) {
                        index = Integer.parseInt(step.substring(openParenthesis+1, step.length()-1)) - 1;
                        step = step.substring(0, openParenthesis);
                    }
                }
                _steps.add(PathStep.parse(step, index, pNamespaceContext));
            } else {
                _steps.remove(_steps.size()-1);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object pObj) {
        if (pObj == this) {
            return true;
        }
        if (!(pObj instanceof PathReference)) {
            return false;
        }
        return _steps.equals(((PathReference)pObj)._steps);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _steps.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (!_steps.isEmpty()) {
            final Iterator<PathReferenceStep> it = _steps.iterator();
            final PathReferenceStep firstStep = it.next();
            if (!firstStep.isIdRef()) {
                buf.append("#/");
            }
            buf.append(firstStep);
            
            PathReferenceStep pathStep;
            // only non-idref has next
            while (it.hasNext()) {
                pathStep = it.next();
                buf.append('/');
                buf.append(pathStep);
            }
        }
        return buf.toString();
    }

    static class PathStep implements PathReferenceStep {
        private final String _uri;
        private final String _name;
        private final int _index;
        
        private volatile int _hashCode = 0;
        
        public PathStep(String pUri, String pName, int pIndex) {
            _uri = pUri;
            _name = pName;
            _index = pIndex;
        }
        
        public static PathStep parse(String pReference, int pIndex, NamespaceContext pNamespaceContext) {
            int nsDots = pReference.lastIndexOf(':') ;
            String uri;
            String name;
            if (nsDots >= 0) {
                name = pReference.substring(nsDots + 1);
                uri = pNamespaceContext.getNamespaceURI(pReference.substring(0, nsDots));
            } else {
                name = pReference;
                uri = "";
            }
            return new PathStep(uri, name, pIndex);
        }

        public String getUri() {
            return _uri;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object pObj) {
            if (pObj == this) {
                return true;
            }
            if (!(pObj instanceof PathReferenceStep)) {
                return false;
            }
            PathReferenceStep step = (PathReferenceStep)pObj;
            String stepUri = step.getUri();
            if (stepUri == null) {
                stepUri = "";
            }
            String uri = _uri;
            if (uri == null) {
                uri = "";
            }
            return step.getIndex() == _index &&
                   step.getName().equals(_name) &&
                   stepUri.equals(uri);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            if (_hashCode == 0) {
                int result = 17;
                result = 37*result + _name.hashCode();
                result = 37*result + _index;
                _hashCode = result;
            }
            return _hashCode;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder(_name);
            if (!isIdRef()) {
                buf.append('.');
                buf.append(_index);
            }
            return buf.toString();
        }
        
        public boolean isIdRef() {
            return _index < 0;
        }

        public String getName() {
            return _name;
        }

        public int getIndex() {
            return _index;
        }
    }

    public List<PathReferenceStep> getSteps() {
        return _steps;
    }
    
    @Override
    public boolean isKeyReference() {
        return false;
    }

}