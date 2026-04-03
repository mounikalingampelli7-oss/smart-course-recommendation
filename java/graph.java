// ============================================================
// Graph.java
// Core DS  : HashMap<String, List<String>>  (Adjacency List)
// Algorithm: BFS — Breadth First Search
//
// EXAM QUESTIONS YOU MUST ANSWER:
//   Q: Why HashMap for adjacency list?
//   A: O(1) average lookup time — fast to find neighbors
//
//   Q: Why BFS and not DFS?
//   A: BFS finds shortest path first — so closest (most
//      relevant) courses are discovered before distant ones
//
//   Q: Time complexity of BFS?
//   A: O(V + E) — V = nodes (vertices), E = edges
//
//   Q: Space complexity?
//   A: O(V + E) — storing the adjacency list + visited set
// ============================================================

import java.util.*;

public class Graph {

    // THE core data structure of this entire project
    // Key   = node name (a skill like "Java" or a course id like "dsa")
    // Value = list of nodes this node has a directed edge TO
    private HashMap<String, List<String>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    // ── addNode ─────────────────────────────────────────────
    // Adds a node to the graph if it doesn't already exist
    // putIfAbsent: only inserts if key is not already present
    public void addNode(String name) {
        adjacencyList.putIfAbsent(name, new ArrayList<>());
    }

    // ── addEdge ─────────────────────────────────────────────
    // Adds a DIRECTED edge from → to
    // Example: addEdge("Java", "dsa")
    // Means: knowing Java makes the DSA course reachable
    public void addEdge(String from, String to) {
        addNode(from);  // ensure both nodes exist
        addNode(to);
        List<String> neighbors = adjacencyList.get(from);
        if (!neighbors.contains(to)) {  // avoid duplicate edges
            neighbors.add(to);
        }
    }

    // ── bfsRecommend ────────────────────────────────────────
    // INPUT : list of skill nodes the user selected
    // OUTPUT: HashMap of every reachable node → its distance
    //         from the starting skills
    //
    // HOW BFS WORKS (explain this in your demo!):
    //   1. Put all starting skills in a queue at distance 0
    //   2. Dequeue a node, look at all its neighbors
    //   3. If neighbor not visited → mark it, record distance,
    //      enqueue it
    //   4. Repeat until queue is empty
    //   Result: every reachable node has its shortest distance
    public HashMap<String, Integer> bfsRecommend(List<String> startSkills) {

        Set<String> visited      = new HashSet<>();   // tracks visited nodes
        Queue<String> queue      = new LinkedList<>(); // BFS queue (FIFO)
        HashMap<String, Integer> distances = new HashMap<>(); // result map

        // STEP 1: Enqueue all selected skills as starting points
        for (String skill : startSkills) {
            if (adjacencyList.containsKey(skill)) {
                queue.add(skill);
                visited.add(skill);
                distances.put(skill, 0);  // starting nodes = distance 0
            }
        }

        // STEP 2: BFS loop — runs until no more nodes to visit
        while (!queue.isEmpty()) {

            String current = queue.poll();          // dequeue front node
            int currentDist = distances.get(current); // its distance from start

            // Look at every neighbor of current node
            List<String> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());

            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {  // only process unvisited
                    visited.add(neighbor);
                    distances.put(neighbor, currentDist + 1); // distance = parent + 1
                    queue.add(neighbor);             // enqueue for further exploration
                }
            }
        }

        return distances;  // contains all reachable nodes + their distances
    }

    // ── Utility Methods ─────────────────────────────────────

    public int getNodeCount() {
        return adjacencyList.size();
    }

    public int getEdgeCount() {
        int count = 0;
        for (List<String> neighbors : adjacencyList.values()) {
            count += neighbors.size();
        }
        return count;
    }

    // Prints the full adjacency list — useful for debugging
    public void printGraph() {
        System.out.println("\n=== Adjacency List ===");
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            System.out.println("  " + entry.getKey() + "  →  " + entry.getValue());
        }
        System.out.println("======================");
    }
}