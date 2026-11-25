package mn.edu.must.sqat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Дүн бодогчийн нэгжийн тест")
class GradeCalculatorTest {
    private final GradeCalculator calculator = new GradeCalculator();

    @ParameterizedTest(name = "оноо {0} бол үсгэн дүн {1}")
    @CsvSource({
        "100, A", "90, A", "89.99, B", "80, B", "79.99, C",
        "70, C", "69.99, D", "60, D", "59.99, F", "0, F"
    })
    @DisplayName("Үсгэн дүнгийн заагийн оноог шалгах")
    void letterGradeBoundaryValues(double score, String expected) {
        // Arrange
        GradeCalculator subject = calculator;
        // Act
        String actual = subject.letterGrade(score);
        // Assert
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "оноо ({0}, {1}, {2}, {3}, {4}) нийлбэр {5}")
    @CsvSource({
        "0, 0, 0, 0, 0, 0",
        "10, 40, 10, 10, 30, 100",
        "10, 0, 0, 0, 0, 10",
        "0, 40, 0, 0, 0, 40",
        "0, 0, 10, 0, 0, 10",
        "0, 0, 0, 10, 0, 10",
        "0, 0, 0, 0, 30, 30"
    })
    @DisplayName("Нийт онооны доод, дээд болон бүрэлдэхүүн хэсгийн заагийг шалгах")
    void totalScoreBoundaryValues(double att, double lab, double quiz1, double quiz2,
                                  double exam, double expected) {
        // Arrange
        GradeCalculator subject = calculator;
        // Act
        double actual = subject.totalScore(att, lab, quiz1, quiz2, exam);
        // Assert
        assertEquals(expected, actual, 1e-9);
    }

    @Test
    @DisplayName("Сөрөг үсгэн дүнгийн оноог хориглох")
    void letterGradeRejectsNegativeScore() {
        // Arrange
        double invalidScore = -0.01;
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> calculator.letterGrade(invalidScore));
    }

    @Test
    @DisplayName("100-аас их үсгэн дүнгийн оноог хориглох")
    void letterGradeRejectsScoreAbove100() {
        // Arrange
        double invalidScore = 100.01;
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> calculator.letterGrade(invalidScore));
    }

    @Test
    @DisplayName("NaN үсгэн дүнгийн оноог хориглох")
    void letterGradeRejectsNaNScore() {
        // Arrange
        double invalidScore = Double.NaN;
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> calculator.letterGrade(invalidScore));
    }

    @Test
    @DisplayName("Бүх онооны бүрэлдэхүүнийг зөв нэмэх")
    void totalScoreAddsAllComponents() {
        // Arrange
        double att = 8.5, lab = 35, quiz1 = 9, quiz2 = 8, exam = 27;
        // Act
        double actual = calculator.totalScore(att, lab, quiz1, quiz2, exam);
        // Assert
        assertEquals(87.5, actual, 1e-9);
    }

    @ParameterizedTest(name = "{0}-р бүрэлдэхүүнд {1} оноо хүчингүй")
    @CsvSource({
        "0, -0.01", "0, 10.01",
        "1, -0.01", "1, 40.01",
        "2, -0.01", "2, 10.01",
        "3, -0.01", "3, 10.01",
        "4, -0.01", "4, 30.01"
    })
    @DisplayName("Бүрэлдэхүүн тус бүрийн доод ба дээд хязгаарыг мөрдүүлэх")
    void totalScoreRejectsOutOfRangeComponent(int component, double invalidValue) {
        // Arrange
        double[] scores = {0, 0, 0, 0, 0};
        scores[component] = invalidValue;
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> calculator.totalScore(scores[0], scores[1], scores[2], scores[3], scores[4]));
    }

    @Test
    @DisplayName("Хязгааргүй нийт оноог хориглох")
    void totalScoreRejectsInfiniteComponent() {
        // Arrange
        double invalidExam = Double.POSITIVE_INFINITY;
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> calculator.totalScore(0, 0, 0, 0, invalidExam));
    }
}
