// ============================================================
// Course.java
// What it does: Represents one course as an object (node in graph)
// Know this: id is used as the graph node key
// ============================================================

public class Course {

    private String id;              // unique key e.g. "dsa", "ml"
    private String name;            // display name
    private String level;           // Beginner / Intermediate / Advanced
    private String[] requiredSkills; // which skills lead to this course
    private int score;              // recommendation score (set during BFS)

    // Constructor — called when creating a new Course object
    public Course(String id, String name, String level, String[] requiredSkills) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.requiredSkills = requiredSkills;
        this.score = 0;
    }

    // Getters
    public String getId()                { return id; }
    public String getName()              { return name; }
    public String getLevel()             { return level; }
    public String[] getRequiredSkills()  { return requiredSkills; }
    public int getScore()                { return score; }

    // Setter — score is assigned after BFS runs
    public void setScore(int score)      { this.score = score; }

    // Used when printing results in Main.java
    @Override
    public String toString() {
        return String.format("%-45s | Level: %-14s | Score: %d",
                name, level, score);
    }
}