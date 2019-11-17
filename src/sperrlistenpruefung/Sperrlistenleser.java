package sperrlistenpruefung;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;

public class Sperrlistenleser {
    String liste;

    ByteArrayInputStream byteArrayInputStream;

    byte[] allData;

    boolean streamGeoeffnet;

    XSSFWorkbook workbook;

    XSSFSheet sheet;

    boolean initialisiert = false;

    int laenge;

    boolean istSperrliste = false;

    public Sperrlistenleser(String liste, boolean istSperrliste) {
        this.liste = liste;
        this.istSperrliste = istSperrliste;
    }

    public void oeffneStream() {
        try {
            File pruefliste = new File(liste);

            this.allData = Files.readAllBytes(pruefliste.toPath());

            this.byteArrayInputStream =
                    new ByteArrayInputStream(allData);

            streamGeoeffnet = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialisiereWorkbook() {
        try {
            initialisiert = true;

            this.oeffneStream();

            this.workbook = new XSSFWorkbook(this.byteArrayInputStream);

            if (!this.istSperrliste) {
                this.sheet = workbook.getSheetAt(0);
            } else {
                this.sheet = workbook.getSheetAt(1);
            }

            this.schliesseStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void schliesseStream() {
        try {
            byteArrayInputStream.close();

            streamGeoeffnet = false;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String gibZeilenWert(int zeile, int spalte) {
        String zeilenWert = "Fehler";

        try {
            if (!initialisiert) {

                initialisiereWorkbook();

                initialisiert = true;
            }

            if (this.sheet.getRow(zeile) != null) {
                Cell cell = this.sheet.getRow(zeile).getCell(spalte);

                if (cell.getCellType() == CellType.STRING) {
                    zeilenWert = cell.getStringCellValue();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return zeilenWert;
    }

    public void loescheZeile(int zeile) {
        if (this.sheet.getRow(zeile) != null) {

            this.sheet.removeRow(this.sheet.getRow(zeile));
        }
    }

    public int gibLaenge() {
        this.laenge = this.sheet.getLastRowNum();

        return laenge;
    }

    public int gibZeilenAnzahl() {
        return this.sheet.getLastRowNum();
    }

}