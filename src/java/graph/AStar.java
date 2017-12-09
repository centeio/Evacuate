package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In computer science, A* is a computer algorithm that is widely used in path finding and graph traversal, the process 
 * of plotting an efficiently traversable path between multiple points, called nodes.
 * <p>
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A* Algorithm (Wikipedia)</a>
 * <br>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AStar<T extends Comparable<T>> {

    public AStar() { }

    /**
     * Find the path using the A* algorithm from start vertex to end vertex or NULL if no path exists.
     * 
     * @param graph
     *          Graph to search.
     * @param start
     *          Start vertex.
     * @param goal
     *          Goal vertex.
     * 
     * @return 
     *          List of Edges to get from start to end or NULL if no path exists.
     */
    public static List<Edge> aStar(Graph graph, Vertex start, Vertex goal) {
        final int size = graph.getVertices().size(); // used to size data structures appropriately
        final Set<Vertex> closedSet = new HashSet<Vertex>(size); // The set of nodes already evaluated.
        final List<Vertex> openSet = new ArrayList<Vertex>(size); // The set of tentative nodes to be evaluated, initially containing the start node
        openSet.add(start);
        final Map<Vertex,Vertex> cameFrom = new HashMap<Vertex,Vertex>(size); // The map of navigated nodes.

        final Map<Vertex,Integer> gScore = new HashMap<Vertex,Integer>(); // Cost from start along best known path.
        gScore.put(start, 0);

        // Estimated total cost from start to goal through y.
        final Map<Vertex,Integer> fScore = new HashMap<Vertex,Integer>();
        for (Vertex v : graph.getVertices())
            fScore.put(v, Integer.MAX_VALUE);
        fScore.put(start, heuristicCostEstimate(start,goal));

        final Comparator<Vertex> comparator = new Comparator<Vertex>() {
            /**
             * {@inheritDoc}
             */
            @Override
            public int compare(Vertex o1, Vertex o2) {
                if (fScore.get(o1) < fScore.get(o2))
                    return -1;
                if (fScore.get(o2) < fScore.get(o1))
                    return 1;
                return 0;
            }
        };

        while (!openSet.isEmpty()) {
            final Vertex current = openSet.get(0);
            if (current.equals(goal))
                return reconstructPath(cameFrom, goal);

            openSet.remove(0);
            closedSet.add(current);
            for (Edge edge : current.getNeighbors()) {
                final Vertex neighbor = edge.getNeighbor(current);
                if (closedSet.contains(neighbor))
                    continue; // Ignore the neighbor which is already evaluated.

                final int tenativeGScore = gScore.get(current) + distanceBetween(current,neighbor); // length of this path.
                if (!openSet.contains(neighbor))
                    openSet.add(neighbor); // Discover a new node
                else if (tenativeGScore >= gScore.get(neighbor))
                    continue;

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tenativeGScore);
                final int estimatedFScore = gScore.get(neighbor) + heuristicCostEstimate(neighbor, goal);
                fScore.put(neighbor, estimatedFScore);

                // fScore has changed, re-sort the list
                Collections.sort(openSet,comparator);
            }
        }

        return null;
    }

    /**
     * Default distance is the edge cost. If there is no edge between the start and next then
     * it returns Integer.MAX_VALUE;
     */
    protected static int distanceBetween(Vertex start, Vertex next) {
        for (Edge e : start.getNeighbors()) {
            if (e.getNeighbor(start).equals(next))
                return e.getCost();
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Default heuristic: cost to each vertex is 1.
     */
    @SuppressWarnings("unused") 
    protected static int heuristicCostEstimate(Vertex start, Vertex goal) {
        return 1;
    }

    private static List<Edge> reconstructPath(Map<Vertex,Vertex> cameFrom, Vertex current) {
        final List<Edge> totalPath = new ArrayList<Edge>();

        while (current != null) {
            final Vertex previous = current;
            current = cameFrom.get(current);
            if (current != null) {
                final Edge edge = current.getEdge(previous);
                totalPath.add(edge);
            }
        }
        Collections.reverse(totalPath);
        return totalPath;
    }
}
