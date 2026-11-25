package mn.edu.must.sqat;

/** Calculates letter grades and weighted course scores. */
public class GradeCalculator {
    public String letterGrade(double score) {
        if (!Double.isFinite(score) || score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }

        if (score >= 90) return "A";
        if (score >= 80) return "B";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    public double totalScore(double att, double lab, double quiz1, double quiz2, double exam) {
        validateComponent("attendance", att, 10);
        validateComponent("lab", lab, 40);
        validateComponent("quiz1", quiz1, 10);
        validateComponent("quiz2", quiz2, 10);
        validateComponent("exam", exam, 30);
        return att + lab + quiz1 + quiz2 + exam;
    }

    private void validateComponent(String name, double score, double maximum) {
        if (!Double.isFinite(score) || score < 0 || score > maximum) {
            throw new IllegalArgumentException(name + " must be between 0 and " + maximum);
        }
    }
}
