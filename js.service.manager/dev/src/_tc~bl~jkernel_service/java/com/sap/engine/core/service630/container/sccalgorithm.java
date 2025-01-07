package com.sap.engine.core.service630.container;

/**
 * This class determines strong connected components in the component graph
 */
class SCCAlgorithm {

  private ComponentWrapper[] nodes;
  private ComponentWrapper[][] adjList;
  private int[] components;
  private int[] dfsnumber;
  private int[] highwater;
  private boolean[] onStack;
  private int lastdfsnumber;
  private int[] stack;
  private int head;

  SCCAlgorithm(ComponentWrapper[] nodes, ComponentWrapper[][] adjList) {
    this.nodes = nodes;
    this.adjList = adjList;
    components = new int[nodes.length];
    dfsnumber = new int[nodes.length];
    highwater = new int[nodes.length];
    onStack = new boolean[nodes.length];
    lastdfsnumber = 0;
    stack = new int[nodes.length];
    head = 0;
  }

  int[] strongComponents() {
    lastdfsnumber = 0;
    head = 0;

    for (int i = 0; i < nodes.length; i++) {
      dfsnumber[i] = -1;
    } 

    for (int i = 0; i < nodes.length; i++) {
      if (dfsnumber[i] == -1) {
        strong(i);
      }
    } 

    return components;
  }

  private void push(int what) {
    stack[head] = what;
    head++;
  }

  private int pop() {
    head--;
    return stack[head];
  }

  private void strong(int v) {
    int w;
    lastdfsnumber++;
    dfsnumber[v] = lastdfsnumber;
    highwater[v] = lastdfsnumber;
    push(v);
    onStack[v] = true;

    if (adjList[v] != null) {
      for (int i = 0; i < adjList[v].length; i++) {
        w = adjList[v][i].getNodeId();

        if (dfsnumber[w] == -1) {
          strong(w);

          if (highwater[w] < highwater[v]) {
            highwater[v] = highwater[w];
          }
        } else if ((dfsnumber[w] < dfsnumber[v]) && onStack[w] && (dfsnumber[w] < highwater[v])) {
          highwater[v] = dfsnumber[w];
        }
      } 
    }

    if (highwater[v] == dfsnumber[v]) {
      do {
        w = pop();
        components[w] = v;
        onStack[w] = false;
      } while (w != v);
    }
  }

}