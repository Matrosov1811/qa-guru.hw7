package pav.matrosov;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilesTest {

    @Test
    @DisplayName("Загрузка файла по абсолютному пути (не рекомендуется)")
    void filenameShouldDisplayedAfterUploadActionAbsolutePathTest() {
        Selenide.open("https://www.west-wind.com/wconnect/wcscripts/FileUpload.wwd");
        File txtFile = new File("C:\\Users\\79654\\IdeaProjects\\qa-guru.hw7\\src\\test\\resources\\txtFile.txt");
        $("input[type='file']").uploadFile(txtFile);
        $("#filename").shouldHave(text("txtFile.txt"));

    }

    @Test
    @DisplayName("Загрузка файла по относительному пути (рекомендуется!)")
    void filenameShouldDisplayedAfterUploadActionFromClasspathTest() {
        Selenide.open("https://www.west-wind.com/wconnect/wcscripts/FileUpload.wwd");
        $("input[type='file']").uploadFromClasspath("txtFile.txt");
        $("#filename").shouldHave(text("txtFile.txt"));

    }

    @Test
    @DisplayName("Скачивание тесктового файла и его проверка")
    void txtFileDownloadTest() throws IOException {
        open("https://filesamples.com/formats/txt");
        File download = $("a[href$='sample1.txt']").download();
        String expectedText = "Utilitatis causa amicitia est quaesita";
        String fileContent = IOUtils.toString(new FileReader(download));
        assertTrue(fileContent.contains(expectedText));
    }

    @Test
    @DisplayName("Скачивание PDF файла и его проверка")
    void pdfFileDownloadTest() throws IOException {
        open("https://filesamples.com/formats/pdf");
        File download = $("a[href$='sample1.pdf']").download();
        PDF parsedPDF = new PDF(download);
        assertEquals(46, parsedPDF.numberOfPages);
    }

    @Test
    @DisplayName("Скачивание XLS файла и его проверка")
    void xlsFileDownloadTest() throws IOException {
        open("https://filesamples.com/formats/xls");
        File download = $("a[href$='sample1.xls']").download();
        XLS parsedXls = new XLS(download);
        boolean checkPassed = parsedXls.excel
                .getSheetAt(2)
                .getRow(3)
                .getCell(0)
                .getStringCellValue()
                .contains("http://www.cmu.edu/blackboard");

        assertTrue(checkPassed);

    }

    @Test
    @DisplayName("CSV парсинг")
    void parseCsvFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("sample1.csv");
             Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);

            List<String[]> strings = csvReader.readAll();
            assertEquals(9, strings.size());
        }
    }

    @Test
    @DisplayName("ZIP парсинг")
    void parseZipFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("123.zip");
            ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            Set<String> expectedFileNames = new HashSet<>();
            expectedFileNames.add("1.txt");
            expectedFileNames.add("2.txt");
            Set<String> realFileNames = new HashSet<>();
            while ((entry = zis.getNextEntry()) != null) {
                realFileNames.add(entry.getName());
            }


        }
    }
}
