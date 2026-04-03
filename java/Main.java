// ============================================================
// Main.java — Entry Point
// This is where the JVM starts execution (main method)
// Handles terminal input/output using Scanner
// ============================================================

import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RecommendationEngine engine = new RecommendationEngine();

        // ── Welcome banner ───────────────────────────────────
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     CourseMap — Recommendation System    ║");
        System.out.println("║     Core DS : Graph + HashMap + BFS      ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // ── Show graph stats ─────────────────────────────────
        System.out.println("\n── Graph Info ──────────────────────────────");
        engine.printGraphStats();

        // ── Available skills list ────────────────────────────
        String[] availableSkills = {
            "Java", "Python", "JavaScript", "HTML",
            "SQL", "Math", "OOP", "Linux", "Git", "DSA"
        };

        System.out.println("\n── Available Skills ────────────────────────");
        for (int i = 0; i < availableSkills.length; i++) {
            System.out.printf("  [%2d]  %s%n", (i + 1), availableSkills[i]);
        }

        // ── Get user input ───────────────────────────────────
        System.out.println("\nEnter skill numbers (comma-separated), e.g: 1,3,7");
        System.out.print("Your choice: ");
        String input = scanner.nextLine().trim();

        // Parse selected skill numbers → skill names
        List<String> selectedSkills = new ArrayList<>();
        for (String part : input.split(",")) {
            try {
                int index = Integer.parseInt(part.trim()) - 1;
                if (index >= 0 && index < availableSkills.length) {
                    selectedSkills.add(availableSkills[index]);
                }
            } catch (NumberFormatException e) {
                System.out.println("  Skipped invalid input: " + part.trim());
            }
        }

        if (selectedSkills.isEmpty()) {
            System.out.println("\nNo valid skills selected. Exiting.");
            scanner.close();
            return;
        }

        System.out.println("\nSelected skills : " + selectedSkills);

        // ── Run recommendation ───────────────────────────────
        List<Course> results = engine.recommend(selectedSkills);

        System.out.println("\n── Recommended Courses ─────────────────────");
        if (results.isEmpty()) {
            System.out.println("  No courses found for your selected skills.");
            System.out.println("  Try selecting different or more skills.");
        } else {
            System.out.printf("  %-3s  %-45s  %-16s  %s%n",
                    "Rank", "Course", "Level", "Score");
            System.out.println("  " + "-".repeat(75));
            for (int i = 0; i < results.size(); i++) {
                System.out.printf("  #%-2d  %s%n", (i + 1), results.get(i));
            }
        }

        System.out.println("\n── Run Again? ──────────────────────────────");
        System.out.print("Press Y to restart, any other key to exit: ");
        String again = scanner.nextLine().trim();
        scanner.close();

        if (again.equalsIgnoreCase("Y")) {
            main(args); // restart
        } else {
            System.out.println("\nGoodbye! Good luck at the expo.");
        }
    }
}