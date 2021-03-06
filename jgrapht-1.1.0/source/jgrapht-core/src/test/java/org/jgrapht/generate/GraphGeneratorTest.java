/*
 * (C) Copyright 2003-2017, by John V Sichi and Contributors.
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
package org.jgrapht.generate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.*;
import org.jgrapht.*;
import org.jgrapht.alg.*;
import org.jgrapht.graph.*;
import org.junit.*;

/**
 * .
 *
 * @author John V. Sichi
 * @since Sep 17, 2003
 */
public class GraphGeneratorTest {
  // ~ Static fields/initializers ---------------------------------------------

  private static final int SIZE = 10;

  // ~ Instance fields --------------------------------------------------------

  private VertexFactory<Object> vertexFactory =
      new VertexFactory<Object>() {
        private int i;

        @Override
        public Object createVertex() {
          return ++i;
        }
      };

  // ~ Methods ----------------------------------------------------------------

  /** . */
  @Test
  public void testEmptyGraphGenerator() {
    GraphGenerator<Object, DefaultEdge, Object> gen = new EmptyGraphGenerator<>(SIZE);
    Graph<Object, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
    Map<String, Object> resultMap = new HashMap<>();
    gen.generateGraph(g, vertexFactory, resultMap);
    assertEquals(SIZE, g.vertexSet().size());
    assertEquals(0, g.edgeSet().size());
    assertTrue(resultMap.isEmpty());
  }

  /** . */
  @Test
  public void testLinearGraphGenerator() {
    GraphGenerator<Object, DefaultEdge, Object> gen = new LinearGraphGenerator<>(SIZE);
    Graph<Object, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
    Map<String, Object> resultMap = new HashMap<>();
    gen.generateGraph(g, vertexFactory, resultMap);
    assertEquals(SIZE, g.vertexSet().size());
    assertEquals(SIZE - 1, g.edgeSet().size());

    Object startVertex = resultMap.get(LinearGraphGenerator.START_VERTEX);
    Object endVertex = resultMap.get(LinearGraphGenerator.END_VERTEX);

    for (Object vertex : g.vertexSet()) {
      if (vertex == startVertex) {
        assertEquals(0, g.inDegreeOf(vertex));
        assertEquals(1, g.outDegreeOf(vertex));

        continue;
      }

      if (vertex == endVertex) {
        assertEquals(1, g.inDegreeOf(vertex));
        assertEquals(0, g.outDegreeOf(vertex));

        continue;
      }

      assertEquals(1, g.inDegreeOf(vertex));
      assertEquals(1, g.outDegreeOf(vertex));
    }
  }

  /** . */
  @Test
  public void testRingGraphGenerator() {
    GraphGenerator<Object, DefaultEdge, Object> gen = new RingGraphGenerator<>(SIZE);
    Graph<Object, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
    Map<String, Object> resultMap = new HashMap<>();
    gen.generateGraph(g, vertexFactory, resultMap);
    assertEquals(SIZE, g.vertexSet().size());
    assertEquals(SIZE, g.edgeSet().size());

    Object startVertex = g.vertexSet().iterator().next();
    assertEquals(1, g.outDegreeOf(startVertex));

    Object nextVertex = startVertex;
    Set<Object> seen = new HashSet<>();

    for (int i = 0; i < SIZE; ++i) {
      DefaultEdge nextEdge = g.outgoingEdgesOf(nextVertex).iterator().next();
      nextVertex = g.getEdgeTarget(nextEdge);
      assertEquals(1, g.inDegreeOf(nextVertex));
      assertEquals(1, g.outDegreeOf(nextVertex));
      assertTrue(!seen.contains(nextVertex));
      seen.add(nextVertex);
    }

    // do you ever get the feeling you're going in circles?
    assertTrue(nextVertex == startVertex);
    assertTrue(resultMap.isEmpty());
  }

  /** . */
  @Test
  public void testCompleteGraphGenerator() {
    Graph<Object, DefaultEdge> completeGraph = new SimpleGraph<>(DefaultEdge.class);
    CompleteGraphGenerator<Object, DefaultEdge> completeGenerator =
        new CompleteGraphGenerator<>(10);
    completeGenerator.generateGraph(
        completeGraph, new ClassBasedVertexFactory<>(Object.class), null);

    // complete graph with 10 vertices has 10*(10-1)/2 = 45 edges
    assertEquals(45, completeGraph.edgeSet().size());
  }

  @Test
  public void testCompleteGraphGeneratorWithDirectedGraph() {
    Graph<Object, DefaultEdge> completeGraph = new SimpleDirectedGraph<>(DefaultEdge.class);
    CompleteGraphGenerator<Object, DefaultEdge> completeGenerator =
        new CompleteGraphGenerator<>(10);
    completeGenerator.generateGraph(
        completeGraph, new ClassBasedVertexFactory<>(Object.class), null);

    // complete graph with 10 vertices has 10*(10-1) = 90 edges
    assertEquals(90, completeGraph.edgeSet().size());
  }

  /** . */
  @Test
  public void testScaleFreeGraphGenerator() {
    Graph<Object, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    ScaleFreeGraphGenerator<Object, DefaultEdge> generator = new ScaleFreeGraphGenerator<>(500);
    generator.generateGraph(graph, vertexFactory, null);
    ConnectivityInspector<Object, DefaultEdge> inspector = new ConnectivityInspector<>(graph);
    assertTrue("generated graph is not connected", inspector.isGraphConnected());

    try {
      new ScaleFreeGraphGenerator<>(-50);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
    }

    try {
      new ScaleFreeGraphGenerator<>(-50, 31337);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
    }

    generator = new ScaleFreeGraphGenerator<>(0);
    Graph<Object, DefaultEdge> empty = new DefaultDirectedGraph<>(DefaultEdge.class);
    generator.generateGraph(empty, vertexFactory, null);
    assertTrue("non-empty graph generated", empty.vertexSet().size() == 0);
  }

  /** . */
  @Test
  public void testCompleteBipartiteGraphGenerator() {
    Graph<Object, DefaultEdge> completeBipartiteGraph = new SimpleGraph<>(DefaultEdge.class);
    CompleteBipartiteGraphGenerator<Object, DefaultEdge> completeBipartiteGenerator =
        new CompleteBipartiteGraphGenerator<>(10, 4);
    completeBipartiteGenerator.generateGraph(
        completeBipartiteGraph, new ClassBasedVertexFactory<>(Object.class), null);

    // Complete bipartite graph with 10 and 4 vertices should have 14
    // total vertices and 4*10=40 total edges
    assertEquals(14, completeBipartiteGraph.vertexSet().size());
    assertEquals(40, completeBipartiteGraph.edgeSet().size());
  }

  /** . */
  @Test
  public void testHyperCubeGraphGenerator() {
    Graph<Object, DefaultEdge> hyperCubeGraph = new SimpleGraph<>(DefaultEdge.class);
    HyperCubeGraphGenerator<Object, DefaultEdge> hyperCubeGenerator =
        new HyperCubeGraphGenerator<>(4);
    hyperCubeGenerator.generateGraph(
        hyperCubeGraph, new ClassBasedVertexFactory<>(Object.class), null);

    // Hypercube of 4 dimensions should have 2^4=16 vertices and
    // 4*2^(4-1)=32 total edges
    assertEquals(16, hyperCubeGraph.vertexSet().size());
    assertEquals(32, hyperCubeGraph.edgeSet().size());
  }

  /** . */
  @Test
  public void testStarGraphGenerator() {
    Map<String, Object> map = new HashMap<>();
    Graph<Object, DefaultEdge> starGraph = new SimpleGraph<>(DefaultEdge.class);
    StarGraphGenerator<Object, DefaultEdge> starGenerator = new StarGraphGenerator<>(10);
    starGenerator.generateGraph(starGraph, new ClassBasedVertexFactory<>(Object.class), map);

    // Star graph of order 10 should have 10 vertices and 9 edges
    assertEquals(9, starGraph.edgeSet().size());
    assertEquals(10, starGraph.vertexSet().size());
    assertTrue(map.get(StarGraphGenerator.CENTER_VERTEX) != null);
  }

  /** . */
  @Test
  public void testGridGraphGenerator() {
    int rows = 3;
    int cols = 4;

    // the form of these two classes helps debugging
    class StringVertexFactory implements VertexFactory<String> {
      int index = 1;

      @Override
      public String createVertex() {
        return String.valueOf(index++);
      }
    }

    class StringEdgeFactory implements EdgeFactory<String, String> {
      @Override
      public String createEdge(String sourceVertex, String targetVertex) {
        return sourceVertex + '-' + targetVertex;
      }
    }

    GridGraphGenerator<String, String> generator = new GridGraphGenerator<>(rows, cols);
    Map<String, String> resultMap = new HashMap<>();

    // validating a directed and undirected graph
    Graph<String, String> directedGridGraph = new DefaultDirectedGraph<>(new StringEdgeFactory());
    generator.generateGraph(directedGridGraph, new StringVertexFactory(), resultMap);
    validateGridGraphGenerator(rows, cols, directedGridGraph, resultMap);

    resultMap.clear();
    Graph<String, String> undirectedGridGraph = new SimpleGraph<>(new StringEdgeFactory());
    generator.generateGraph(undirectedGridGraph, new StringVertexFactory(), resultMap);
    validateGridGraphGenerator(rows, cols, undirectedGridGraph, resultMap);
  }

  public void validateGridGraphGenerator(
      int rows, int cols, Graph<String, String> gridGraph, Map<String, String> resultMap) {
    // graph structure validations
    int expectedVerticeNum = rows * cols;
    assertEquals(
        "number of vertices is wrong ("
            + gridGraph.vertexSet().size()
            + "), should be "
            + expectedVerticeNum,
        expectedVerticeNum,
        gridGraph.vertexSet().size());
    int expectedEdgesNum =
        (((rows - 1) * cols) + ((cols - 1) * rows))
            * ((gridGraph.getType().isUndirected()) ? 1 : 2);
    assertEquals(
        "number of edges is wrong ("
            + gridGraph.edgeSet().size()
            + "), should be "
            + expectedEdgesNum,
        expectedEdgesNum,
        gridGraph.edgeSet().size());

    int cornerVertices = 0, borderVertices = 0, innerVertices = 0, neighborsSize;
    int expCornerVertices = 4;
    int expBorderVertices = Math.max(((rows - 2) * 2) + ((cols - 2) * 2), 0);
    int expInnerVertices = Math.max((rows - 2) * (cols - 2), 0);
    Set<String> neighbors = new HashSet<>();

    for (String v : gridGraph.vertexSet()) {
      neighbors.clear();
      neighbors.addAll(Graphs.neighborListOf(gridGraph, v));
      neighborsSize = neighbors.size();
      assertTrue(
          "vertex with illegal number of neighbors (" + neighborsSize + ").",
          (neighborsSize == 2) || (neighborsSize == 3) || (neighborsSize == 4));
      if (neighborsSize == 2) {
        cornerVertices++;
      } else if (neighborsSize == 3) {
        borderVertices++;
      } else if (neighborsSize == 4) {
        innerVertices++;
      }
    }
    assertEquals(
        "there should be exactly "
            + expCornerVertices
            + " corner (with two neighbors) vertices. "
            + " actual number is "
            + cornerVertices
            + ".",
        expCornerVertices,
        cornerVertices);
    assertEquals(
        "there should be exactly "
            + expBorderVertices
            + " border (with three neighbors) vertices. "
            + " actual number is "
            + borderVertices
            + ".",
        expBorderVertices,
        borderVertices);
    assertEquals(
        "there should be exactly "
            + expInnerVertices
            + " inner (with four neighbors) vertices. "
            + " actual number is "
            + innerVertices
            + ".",
        expInnerVertices,
        innerVertices);

    // result map validations
    Set<String> keys = resultMap.keySet();
    assertEquals("result map contains should contains exactly 4 corner verices", 4, keys.size());

    for (String key : keys) {
      neighbors.clear();
      neighbors.addAll(Graphs.neighborListOf(gridGraph, resultMap.get(key)));
      neighborsSize = neighbors.size();
      assertEquals("corner vertex should have exactly 2 neighbors", 2, neighborsSize);
    }
  }
}

// End GraphGeneratorTest.java
