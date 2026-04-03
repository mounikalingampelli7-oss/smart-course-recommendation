// ============================================================
// RecommendationEngine.java
// What it does: Builds the graph, runs BFS, scores + ranks courses
//
// SCORING FORMULA (know this!):
//   score = 100 - (distance × 20)
//   distance 1 → score 80   (directly reachable via one edge)
//   distance 2 → score 60   (two hops away)
//   distance 3 → score 40
//   distance 4 → score 20
//   distance 5+ → score 0   (not recommended)
// ============================================================

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

    // ── Course Data ─────────────────────────────────────────
    // Each Course: (id, display name, level, required skills[])
    // The id must match the graph node name exactly
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

    // ── Build the Graph ─────────────────────────────────────
    // Creates directed edges: skill → course
    // Also creates skill → skill chains (prerequisite paths)
    private void buildGraph() {

        // Add all course nodes + edges from their required skills
        for (Course course : allCourses) {
            graph.addNode(course.getId());
            for (String skill : course.getRequiredSkills()) {
                graph.addEdge(skill, course.getId());
            }
        }

        // Skill → Skill prerequisite chains
        // These create multi-hop paths so BFS can discover
        // advanced courses indirectly from basic skills
        graph.addEdge("Java",       "OOP");       // Java → OOP (skill unlock)
        graph.addEdge("OOP",        "dsa");       // OOP  → DSA course
        graph.addEdge("OOP",        "android");   // OOP  → Android
        graph.addEdge("DSA",        "sys");       // DSA  → System Design
        graph.addEdge("Python",     "ml");        // Python → ML
        graph.addEdge("ml",         "nlp");       // ML course → NLP course
        graph.addEdge("SQL",        "spring");    // SQL → Spring Boot
        graph.addEdge("JavaScript", "reactjs");   // JS  → React
        graph.addEdge("HTML",       "webdev");    // HTML → Web Dev
        graph.addEdge("Math",       "ml");        // Math → ML
        graph.addEdge("Linux",      "devops");    // Linux → DevOps
        graph.addEdge("Git",        "devops");    // Git   → DevOps
        graph.addEdge("Java",       "spring");    // Java  → Spring Boot
        graph.addEdge("Java",       "sys");       // Java  → System Design
    }

    // ── Main Recommendation Method ──────────────────────────
    // 1. Calls graph.bfsRecommend() with user's skills
    // 2. Computes score for each course based on distance
    // 3. Filters out score = 0 courses
    // 4. Sorts by score descending (highest first)
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