
import java.util.*;

public class Graph {

    
    private HashMap<String, List<String>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

   
    public void addNode(String name) {
        adjacencyList.putIfAbsent(name, new ArrayList<>());
    }

    
    public void addEdge(String from, String to) {
        addNode(from);  // ensure both nodes exist
        addNode(to);
        List<String> neighbors = adjacencyList.get(from);
        if (!neighbors.contains(to)) {  // avoid duplicate edges
            neighbors.add(to);
        }
    }

    
    public HashMap<String, Integer> bfsRecommend(List<String> startSkills) {

        Set<String> visited      = new HashSet<>();  
        Queue<String> queue      = new LinkedList<>(); 
        HashMap<String, Integer> distances = new HashMap<>(); 

       
        for (String skill : startSkills) {
            if (adjacencyList.containsKey(skill)) {
                queue.add(skill);
                visited.add(skill);
                distances.put(skill, 0);  
            }
        }

      
        while (!queue.isEmpty()) {

            String current = queue.poll();       
            int currentDist = distances.get(current); 

        
            List<String> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());

            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) { 
                    visited.add(neighbor);
                    distances.put(neighbor, currentDist + 1);  
                    queue.add(neighbor);           
                }
            }
        }

        return distances;  
    }

    //  Utility Methods 

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

    // Print the full adjacency list 
    public void printGraph() {
        System.out.println("\n=== Adjacency List ===");
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            System.out.println("  " + entry.getKey() + "  →  " + entry.getValue());
        }
        System.out.println("======================");
    }
}