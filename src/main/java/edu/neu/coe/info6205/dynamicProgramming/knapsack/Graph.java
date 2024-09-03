package edu.neu.coe.info6205.dynamicProgramming.knapsack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
/**
 * The {@code Graph} class represents a graph with edges and vertices.
 */
public class Graph<T> {

    private HashMap<T, LinkedList<Edge>> adjacent;
    private Vertex root;

    public Vertex getVertex() {
        return root;
    }
    /**
     * Initializes the root vertex of the grpah and also adjacent list.
     * @param  root soruce vertex
     */
    public Graph(Vertex root){
        this.root=root;
        adjacent =new HashMap<T,LinkedList<Edge>>();
    }
    /**
     * Adds a new Vertex into the adjacency list of vertices.
     */
    public void addVertex(T v){
        adjacent.put(v,new LinkedList<Edge>());
    }
    /**
     * Adds entry to the given Vertex as a adjacent vertex.
     */
    public void addEdge(Vertex U, Vertex V, double edgeWeight){
        adjacent.getOrDefault(U,new LinkedList<Edge>()).add(new Edge(U,V,edgeWeight));
    }
    /**
     * Returns the directed edges incident from vertex {@code v}.
     *
     * @param  v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     */
    public Iterator<Edge> edges(T v){
        if(adjacent.get(v)!=null)
            return adjacent.get(v).iterator();
        else
            return null;
    }
}