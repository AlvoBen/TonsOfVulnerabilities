/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.xml.stream.adapters.impl;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author D042774
 *
 */
public class AdapterPool {
    private Deque<SimpleContentElementAdapter> _simpleContentElement;
    private Deque<AttributeValueAdapter> _attributeValue;
    private Deque<ChangedElementAdapter> _changedElement;
    private Deque<ChangeSummaryAdapter> _changedSummary;
    private Deque<NonSequencedElementAdapter> _nonSequencedElement;
    private Deque<ReferencedElementAdapter> _referencedElement;
    private Deque<SequencedElementAdapter> _sequencedElement;

    public SimpleContentElementAdapter getSimpleContentElementAdapter() {
        if (_simpleContentElement == null) {
            _simpleContentElement = new ArrayDeque<SimpleContentElementAdapter>();
        } else {
            SimpleContentElementAdapter adapter = _simpleContentElement.pollFirst();
            if (adapter != null) {
                return adapter;
            }
        }
        return new SimpleContentElementAdapter(this);
    }

    /**
     * @param pAdapter
     */
    public void returnAdapter(SimpleContentElementAdapter pSimpleContentElementAdapter) {
        _simpleContentElement.addFirst(pSimpleContentElementAdapter);
    }

    public AttributeValueAdapter getAttributeValueAdapter() {
        if (_attributeValue == null) {
            _attributeValue = new ArrayDeque<AttributeValueAdapter>();
        } else {
            AttributeValueAdapter adapter = _attributeValue.pollFirst();
            if (adapter != null) {
                return adapter;
            }
        }
        return new AttributeValueAdapter(this);
    }

    /**
     * @param pAttributeValueAdapter
     */
    public void returnAdapter(AttributeValueAdapter pAttributeValueAdapter) {
        _attributeValue.addFirst(pAttributeValueAdapter);
    }

    public ChangedElementAdapter getChangedElementAdapter() {
        if (_changedElement == null) {
            _changedElement = new ArrayDeque<ChangedElementAdapter>();
        } else {
            ChangedElementAdapter adapter = _changedElement.pollFirst();
            if (adapter != null) {
                return adapter;
            }
        }
        return new ChangedElementAdapter(this);
    }

    /**
     * @param pChangedElementAdapter
     */
    public void returnAdapter(ChangedElementAdapter pChangedElementAdapter) {
        _changedElement.addFirst(pChangedElementAdapter);
    }

    public ChangeSummaryAdapter getChangeSummaryAdapter() {
        if (_changedSummary == null) {
            _changedSummary = new ArrayDeque<ChangeSummaryAdapter>();
        } else {
            ChangeSummaryAdapter adapter = _changedSummary.pollFirst();
            if (adapter != null) {
                return adapter;
            }
        }
        return new ChangeSummaryAdapter(this);
    }

    /**
     * @param pChangeSummaryAdapter
     */
    public void returnAdapter(ChangeSummaryAdapter pChangeSummaryAdapter) {
        _changedSummary.addFirst(pChangeSummaryAdapter);
    }

    public NonSequencedElementAdapter getNonSequencedElementAdapter() {
        if (_nonSequencedElement == null) {
            _nonSequencedElement = new ArrayDeque<NonSequencedElementAdapter>();
        } else {
            NonSequencedElementAdapter adapter = _nonSequencedElement.pollFirst();
            if (adapter != null) {
                return adapter;
            }
        }
        return new NonSequencedElementAdapter(this);
    }

    /**
     * @param pNonSequencedElementAdapter
     */
    public void returnAdapter(NonSequencedElementAdapter pNonSequencedElementAdapter) {
        _nonSequencedElement.addFirst(pNonSequencedElementAdapter);
    }

    public ReferencedElementAdapter getReferencedElementAdapter() {
        if (_referencedElement == null) {
            _referencedElement = new ArrayDeque<ReferencedElementAdapter>();
        } else {
            ReferencedElementAdapter adapter = _referencedElement.pollFirst();
            if (adapter != null) {
                return adapter;
            }
        }
        return new ReferencedElementAdapter(this);
    }

    /**
     * @param pReferencedElementAdapter
     */
    public void returnAdapter(ReferencedElementAdapter pReferencedElementAdapter) {
        _referencedElement.addFirst(pReferencedElementAdapter);
    }

    public SequencedElementAdapter getSequencedElementAdapter() {
        if (_sequencedElement == null) {
            _sequencedElement = new ArrayDeque<SequencedElementAdapter>();
        } else {
            SequencedElementAdapter adapter = _sequencedElement.pollFirst();
            if (adapter != null) {
                return adapter;
            }
        }
        return new SequencedElementAdapter(this);
    }

    /**
     * @param pSequencedElementAdapter
     */
    public void returnAdapter(SequencedElementAdapter pSequencedElementAdapter) {
        _sequencedElement.addFirst(pSequencedElementAdapter);
    }
}
