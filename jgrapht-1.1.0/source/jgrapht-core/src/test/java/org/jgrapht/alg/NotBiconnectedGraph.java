/*
 * (C) Copyright 2007-2017, by France Telecom and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg;

import org.jgrapht.graph.*;

/** @since July 5, 2007 */
public class NotBiconnectedGraph extends SimpleGraph<String, DefaultEdge> {
  // ~ Static fields/initializers ---------------------------------------------

  /** */
  private static final long serialVersionUID = 6518961051694377584L;

  // ~ Constructors -----------------------------------------------------------

  public NotBiconnectedGraph() {
    super(DefaultEdge.class);

    addVertices();
    addEdges();
  }

  // ~ Methods ----------------------------------------------------------------

  private void addEdges() {
    addEdge("0", "2");
    addEdge("0", "3");
    addEdge("3", "1");
    addEdge("1", "4");
    addEdge("4", "5");
    addEdge("5", "3");
  }

  private void addVertices() {
    addVertex("0");
    addVertex("1");
    addVertex("2");
    addVertex("3");
    addVertex("4");
    addVertex("5");
  }
}

// End NotBiconnectedGraph.java
