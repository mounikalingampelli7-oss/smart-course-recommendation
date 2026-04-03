

import java.util.*;

public class RecommendationEngine {

    private Graph graph;
    private List<Course> allCourses;

    public RecommendationEngine() {
        this.graph = new Graph();
        this.allCourses = new ArrayList<>();
        buildCourseData();
        buildGraph();
    }

    
    private void buildCourseData() {
        allCourses.add(new Course("dsa",      "Data Structures & Algorithms",  "Intermediate", new String[]{"Java", "OOP"}));
        allCourses.add(new Course("webdev",   "Full Stack Web Development",    "Beginner",     new String[]{"HTML", "JavaScript"}));
        allCourses.add(new Course("ml",       "Machine Learning Basics",       "Intermediate", new String[]{"Python", "Math"}));
        allCourses.add(new Course("db",       "Database Design & SQL",         "Beginner",     new String[]{"SQL"}));
        allCourses.add(new Course("android",  "Android App Development",       "Intermediate", new String[]{"Java", "OOP"}));
        allCourses.add(new Course("spring",   "Spring Boot & REST APIs",       "Advanced",     new String[]{"Java", "SQL"}));
        allCourses.add(new Course("devops",   "DevOps & Cloud Basics",         "Advanced",     new String[]{"Linux", "Git"}));
        allCourses.add(new Course("nlp",      "Natural Language Processing",   "Advanced",     new String[]{"Python", "Math"}));
        allCourses.add(new Course("reactjs",  "React.js Deep Dive",            "Intermediate", new String[]{"JavaScript", "HTML"}));
        allCourses.add(new Course("sys",      "System Design Fundamentals",    "Advanced",     new String[]{"Java", "SQL"}));
    }

    
    private void buildGraph() {

        // Add all course nodes + edges from their required skills
        for (Course course : allCourses) {
            graph.addNode(course.getId());
            for (String skill : course.getRequiredSkills()) {
                graph.addEdge(skill, course.getId());
            }
        }

        // Skill to Skill prerequisite chains
        // These create multi-hop paths so BFS can discover
        // advanced courses indirectly from basic skills
        graph.addEdge("Java",       "OOP");      
        graph.addEdge("OOP",        "dsa");       
        graph.addEdge("OOP",        "android");   
        graph.addEdge("DSA",        "sys");      
        graph.addEdge("Python",     "ml");       
        graph.addEdge("ml",         "nlp");     
        graph.addEdge("SQL",        "spring");   
        graph.addEdge("JavaScript", "reactjs");   
        graph.addEdge("HTML",       "webdev");   
        graph.addEdge("Math",       "ml");       
        graph.addEdge("Linux",      "devops");    
        graph.addEdge("Git",        "devops");    
        graph.addEdge("Java",       "spring");    
        graph.addEdge("Java",       "sys");       
    }

    
    public List<Course> recommend(List<String> userSkills) {

        // Step 1: BFS — get distances of all reachable nodes
        HashMap<String, Integer> distances = graph.bfsRecommend(userSkills);

        // Step 2: Score each course
        List<Course> results = new ArrayList<>();
        for (Course course : allCourses) {
            if (distances.containsKey(course.getId())) {
                int distance = distances.get(course.getId());
                int score    = Math.max(0, 100 - distance * 20);
                if (score > 0) {
                    course.setScore(score);
                    results.add(course);
                }
            }
        }

        // Step 3: Sort by score — highest first
        // Comparator: compare b to a (descending order)
        results.sort((a, b) -> b.getScore() - a.getScore());

        return results;
    }

    // ── Debug helpers ────────────────────────────────────────
    public void printGraphStats() {
        System.out.println("Nodes : " + graph.getNodeCount());
        System.out.println("Edges : " + graph.getEdgeCount());
        graph.printGraph();
    }
}