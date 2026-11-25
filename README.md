# Лаборатори №4 — Нэгжийн тест (JUnit 5)

## Оюутны мэдээлэл ба орчин

- Бүтэн нэр: **Эрдэнэцогт Ундрах** (**Undrakh Erdenetsogt**)
- Оюутны код: **b252270135**
- JUnit Jupiter: `5.10.2` (compile target `17`)
- Maven Surefire: `3.2.5`

### `java -version` ба `mvn -version` командын бүтэн гаралт

```text
$ java -version
java version "25.0.2" 2026-01-20 LTS
Java(TM) SE Runtime Environment (build 25.0.2+10-LTS-69)
Java HotSpot(TM) 64-Bit Server VM (build 25.0.2+10-LTS-69, mixed mode, sharing)

$ mvn -version
Apache Maven 3.10.0 (c43a36b8d67be7e0805a411bc0898af1a51f5472)
Maven home: /opt/homebrew/Cellar/maven/3.10.0/libexec
Java version: 27.0.1.0.3, vendor: Homebrew, runtime: /opt/homebrew/Cellar/openjdk/27.0.1.0.3/libexec/openjdk.jdk/Contents/Home
Default locale: en_MN, platform encoding: UTF-8, time zone: Asia/Ulaanbaatar
OS name: "mac os x", version: "26.2", arch: "aarch64", family: "mac"
```

## Тест ажиллуулах

Java 17+ болон Maven суулгасан орчинд төслийн хавтсаас:

```sh
mvn test
```

Тестийн дэлгэрэнгүй гаралтууд `results/` дотор байна (`results/mvn-test.txt`, `results/mvn-test-mutant.txt`, `results/mvn-test-restored.txt`).

## Тестийн бүтэц ба методын жагсаалт (Нийт 8 тестийн метод → 32 execution)

`GradeCalculatorTest` класс нь AAA (Arrange–Act–Assert) загвар, монгол `@DisplayName` тайлбар болон `@ParameterizedTest` (`@CsvSource`) ашигласан **нийт 8 тестийн методтой** (parameterized утгууд задарч нийт **32 тест** болж ажиллана):

1. `letterGradeBoundaryValues(double score, String expected)` — `@ParameterizedTest` (10 заагийн утга: `100, 90, 89.99, 80, 79.99, 70, 69.99, 60, 59.99, 0`)
2. `totalScoreBoundaryValues(...)` — `@ParameterizedTest` (7 заагийн хослол: `0` болон бүрэлдэхүүн тус бүрийн дээд оноо)
3. `letterGradeRejectsNegativeScore()` — `@Test` (сөрөг оноо `-0.01`-ийг хориглох)
4. `letterGradeRejectsScoreAbove100()` — `@Test` (`100.01` оноог хориглох)
5. `letterGradeRejectsNaNScore()` — `@Test` (`Double.NaN` утгыг хориглох)
6. `totalScoreAddsAllComponents()` — `@Test` (5 бүрэлдэхүүний энгийн нийлбэр `87.5`-ийг шалгах)
7. `totalScoreRejectsOutOfRangeComponent(int component, double invalidValue)` — `@ParameterizedTest` (10 хүчингүй утга: бүрэлдэхүүн бүрийн доод/дээд хязгаар зөрчил)
8. `totalScoreRejectsInfiniteComponent()` — `@Test` (`Double.POSITIVE_INFINITY` утгыг хориглох)

## Мутацийн туршилт

1. `GradeCalculator.java` дахь `score >= 90` нөхцөлийг түр `score > 90` болгон өөрчилсөн.
2. `mvn test` ажиллуулахад 32 тестээс **1 тест унаж** (`BUILD FAILURE`), бодит гаралт `results/mvn-test-mutant.txt` файлд хадгалагдсан:
   - **Унасан тестийн нэр:** `GradeCalculatorTest.letterGradeBoundaryValues[2]` (parameterized тест дэх `90, A` заагийн утга, 27-р мөр)
   - **Алдааны мессеж:** `expected: <A> but was: <B>`
3. Нөхцөлийг `score >= 90` болгон буцааж сэргээгээд `mvn test` дахин ажиллуулахад **32 тест, 0 failure, 0 error** (`BUILD SUCCESS`) болсон (`results/mvn-test-restored.txt`).

## Миний дүгнэлт (Хамгийн сонирхолтой тест ба мутацийн алдааны тухай)

Энэ лабораторийн ажлын хамгийн сонирхолтой хэсэг нь `letterGradeBoundaryValues` parameterized тест болон мутацийн туршилтаар илэрсэн `letterGradeBoundaryValues[2]` (`expected: <A> but was: <B>`) алдаа байлаа. Програм хангамжийн логикт `>=` тэмдгийг санамсаргүйгээр `>` болгож бичих (off-by-one буюу нэг нэгжийн зөрүүний алдаа) нь бодит хөгжүүлэлтэд хамгийн түгээмэл гардаг ч энгийн тохиолдлын тестээр баригдахад маш хэцүү байдаг. Хэрэв бид зөвхөн `95` эсвэл `85` гэх мэт дундаж оноогоор тест бичсэн бол `score > 90` мутант код бүх тестийг ногоон давах байсан. Харин яг босго дээрх `90` болон `89.99` гэсэн заагийн утгуудыг (Boundary Value Analysis) `@CsvSource`-д оруулж өгснөөр `90` оноо авсан оюутанд `A`-ийн оронд `B` өгөх ноцтой алдааг шууд барьж чадсан. Мөн `Double.NaN` болон `Double.POSITIVE_INFINITY` зэрэг хөвөгч таслалтай тооны онцгой тохиолдлуудыг шалгасан тестүүд нь зөвхөн энгийн тоон хязгаар бус өгөгдлийн төрлийн гаж утгуудаас кодыг хамгаалах ач холбогдолтойг ойлгуулсан. Эцэст нь мутацийн туршилт нь бичсэн нэгжийн тестүүд маань үнэхээр кодын логик алдааг илрүүлэх чадвартай эсэхийг баталгаажуулдаг сайн хэмжүүр боллоо.
