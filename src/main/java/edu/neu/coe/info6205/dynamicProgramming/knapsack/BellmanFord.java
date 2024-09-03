package edu.neu.coe.info6205.dynamicProgramming.knapsack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class BellmanFord {
    /**
     * Method to return the Maximum profit after selecting particular items using Bellman Ford Algorithm.
     *
     * @param graph the weights for each item.
     * @param source soruce Vertex.
     * @param target target Vertex.
     * @return Maximum profit after selecting items from the list of items available.
     */
    public static double bellmanFordAlgorihm(Graph graph,Vertex source,Vertex target){

        HashMap<Vertex,Double> shortestDistance = new HashMap();

        shortestDistance.put(source,0.0);
        shortestDistance.put(target,Double.MAX_VALUE);

        Queue<Vertex> queue=new LinkedList<>();
        queue.add(source);

        while(!queue.isEmpty())
        {
            source=queue.poll();
            Iterator it=graph.edges(source);
            while(it.hasNext())
            {
                Edge edge = (Edge) it.next();
                Vertex v1=edge.source();
                Vertex v2=edge.destination();
                double weight=edge.getEdgeWeight();

                if(shortestDistance.getOrDefault(v1,Double.MAX_VALUE)!= Double.MAX_VALUE && (shortestDistance.getOrDefault(v1,Double.MAX_VALUE)+weight)< shortestDistance.getOrDefault(v2,Double.MAX_VALUE))
                    shortestDistance.put(v2,shortestDistance.get(v1)+weight);

                queue.add(edge.destination());
            }
        }
        /**
         * Negating the shortest Distance
         */
        return (-1 * shortestDistance.get(target));

    }
}