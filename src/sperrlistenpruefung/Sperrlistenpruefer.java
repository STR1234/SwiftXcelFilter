package sperrlistenpruefung;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class Sperrlistenpruefer {
    Sperrlistenleser mailListenLeser;
    Sperrlistenleser sperrListenLeser;
    ArrayList zuSperrendeAdressZeilen = new ArrayList();
    ArrayList enternteAdressen = new ArrayList();
    String mailListenPfad;
    String verwendetesOS = System.getProperty("os.name");
    Boolean isUnixOS = false;
    String speicherPfad;
    String maillisteNeuPfad;

    public Sperrlistenpruefer(String ListenPfad) {
        this.mailListenPfad = ListenPfad;
        this.mailListenLeser =
                new Sperrlistenleser(mailListenPfad, false);
        this.sperrListenLeser =
                new Sperrlistenleser(mailListenPfad, true);

        sperrListenLeser.initialisiereWorkbook();
        mailListenLeser.initialisiereWorkbook();

        if (this.verwendetesOS.equals("Mac OS X") || verwendetesOS.equals("nix")
                || verwendetesOS.equals("nux")
                || verwendetesOS.equals("aix")) {
            isUnixOS = true;
        }
        initialisiereSpeicherPfad();
    }

    public void initialisiereSpeicherPfad() {
        if (isUnixOS) {
            //Unix-Variante
            this.speicherPfad = "/";
        } else {
            //Windows-Variante
            this.speicherPfad = "";
        }
    }

    public void pruefe() {
        mailAdressenFilter();
        modifiziere();
        schreibeGeloeschteAdressen();
    }

    public void mailAdressenFilter() {

        int sperrListenLaenge = sperrListenLeser.gibLaenge();
        int mailListenLaenge = mailListenLeser.gibLaenge();

        for (int i = 1; i <= sperrListenLaenge; i++) {
            for (int j = 1; j <= mailListenLaenge; j++) {
                String aktuelleAdresse =
                        mailListenLeser.gibZeilenWert(j, 1);
                if (aktuelleAdresse.equals(
                    sperrListenLeser.gibZeilenWert(i, 1))) {
                    this.zuSperrendeAdressZeilen.add(j);

                    // Wir merken uns die bereits entfernten Mailadressen.
                    // Ohne Duplikate.
                    if (!this.enternteAdressen.contains(aktuelleAdresse)) {
                        this.enternteAdressen.add(aktuelleAdresse);
                    }
                }
            }
        }
    }

    public void modifiziere() {
        Iterator<Integer> sperrAdressIterator =
                zuSperrendeAdressZeilen.iterator();
        while (sperrAdressIterator.hasNext()) {
            int zeile = sperrAdressIterator.next();
            mailListenLeser.loescheZeile(zeile);
        }
        schiebeZellen();
        schreibe("MaillisteNeu.xlsx",
                false);
    }

    public void schiebeZellen() {
        int zeilenAnzahl = mailListenLeser.gibZeilenAnzahl();
        int ersteStelleZelleLeer = 0;
        int aktuelleStelle = 0;
        boolean keineZellenLeer = true;
        boolean komplettDurchlaufen = false;

        for (int i = 1; keineZellenLeer && i < zeilenAnzahl; i++) {
            Row row = mailListenLeser.sheet.getRow(i);
            if (row == null ||
                    row.getCell(1).getCellType() == CellType.BLANK) {
                keineZellenLeer = false;
                ersteStelleZelleLeer = i;

            }
        }

        for (int i = ersteStelleZelleLeer; i < zeilenAnzahl
                && (mailListenLeser.sheet.getRow(i) == null ||
                mailListenLeser.sheet.getRow(i).
                        getCell(1).getCellType() ==
                        CellType.BLANK); i++) {
            if (i != zeilenAnzahl - 1) {
                aktuelleStelle = i + 1;
            } else {
                komplettDurchlaufen = true;
            }
        }

        this.schiebeUndEntferne(ersteStelleZelleLeer, aktuelleStelle);

        if (mailListenLeser.sheet.
                getRow(ersteStelleZelleLeer + 1) == null) {
            ersteStelleZelleLeer++;
        }
        while (!komplettDurchlaufen) {
            for (int i = aktuelleStelle; i < zeilenAnzahl &&
                    mailListenLeser.sheet.getRow(i) == null; i++) {
                aktuelleStelle = i + 1;
            }

            this.schiebeUndEntferne(ersteStelleZelleLeer, aktuelleStelle);

            if (aktuelleStelle == zeilenAnzahl) {
                komplettDurchlaufen = true;
            }

            if (mailListenLeser.sheet.
                    getRow(ersteStelleZelleLeer + 1) == null) {
                ersteStelleZelleLeer++;
            }
        }

    }

    public void schreibe(String sheetName,
                         boolean istEntfernteAdressenWorkbook) {
        try {
            neuerListenPfad(sheetName, istEntfernteAdressenWorkbook);
            FileOutputStream fileOutputStream =
                    new FileOutputStream(
                            new File(this.speicherPfad));

            if (!istEntfernteAdressenWorkbook) {
                mailListenLeser.workbook.write(fileOutputStream);
            } else {
                sperrListenLeser.geloeschteAdressenWorkbook
                        .write(fileOutputStream);
            }

            System.out.println(sheetName + " erstellt.");
            fileOutputStream.close();

            if (!istEntfernteAdressenWorkbook) {
                mailListenLeser.workbook.close();
            } else {
                sperrListenLeser.geloeschteAdressenWorkbook.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void schiebeUndEntferne(int ersteStelleZelleLeer,
                                  int aktuelleStelle) {
        Cell cell = mailListenLeser.sheet.getRow(aktuelleStelle).
                getCell(1);

        mailListenLeser.sheet.createRow(ersteStelleZelleLeer);
        mailListenLeser.sheet.getRow(ersteStelleZelleLeer)
                .createCell(1);

        mailListenLeser.sheet.getRow(ersteStelleZelleLeer).getCell(1).
                setCellValue(cell.getStringCellValue());

       mailListenLeser.sheet.
               removeRow(mailListenLeser.sheet.getRow(aktuelleStelle));
    }

    public String neuerListenPfad(String sheetName,
                                  boolean istEntfernteAdressenWorkbook) {
        String aktuellerListenPfad = this.mailListenPfad;

        //Some console prints to check program status.
        System.out.println("AktuellerListenPfad:");
        System.out.println(aktuellerListenPfad);

        String[] neuerListenPfad;

        // Nur für Testausgaben:
        //   System.out.println("Das verwendete OS: " + verwendetesOS);

        //Unter MacOS werden / diese Schrägstriche genutzt
        if (isUnixOS) {
            neuerListenPfad = aktuellerListenPfad.split("/");
            neuerListenPfad[neuerListenPfad.length - 1] = sheetName;
        } else if (verwendetesOS.contains("Windows")) {
            neuerListenPfad = aktuellerListenPfad.split("\\\\");
            neuerListenPfad[neuerListenPfad.length - 1] = "\\" + sheetName;
        } else {
            neuerListenPfad = aktuellerListenPfad.split("/");
            neuerListenPfad[neuerListenPfad.length - 1] = sheetName;
            System.out.println("Pfadname kann unter diesem OS fehlerhaft sein" +
                    ".");
        }

        for (int i = 0; i < neuerListenPfad.length; i++) {
            if (i == 0) {
                if (!isUnixOS) {
                    //Windows
                    this.speicherPfad = neuerListenPfad[i];
                } else {
                    //MacOS
                    this.speicherPfad = this.speicherPfad + neuerListenPfad[i];
                    System.out.println(neuerListenPfad[i]);
                }

            } else if (i < neuerListenPfad.length - 1) {
                if (!isUnixOS) {
                    //Windows
                    this.speicherPfad = this.speicherPfad
                            + "\\" + neuerListenPfad[i];
                } else {
                    //MacOS
                    this.speicherPfad = this.speicherPfad
                            + neuerListenPfad[i] + "/";
                    System.out.println(neuerListenPfad[i]);
                }
            } else {
                this.speicherPfad = this.speicherPfad + neuerListenPfad[i];
                System.out.println(neuerListenPfad[i]);
            }
        }

        if (!istEntfernteAdressenWorkbook) {
            this.maillisteNeuPfad = this.speicherPfad;
        }

        System.out.println(this.speicherPfad);
        return aktuellerListenPfad;
    }

    public void schreibeGeloeschteAdressen() {
        Iterator<String> entfernteAdressenIterator =
                enternteAdressen.iterator();

        int i = 0;
        while (entfernteAdressenIterator.hasNext()) {
            sperrListenLeser.entfernteAdressenSheet.autoSizeColumn(1);

            XSSFRow entfernteAdressenRow =
                    sperrListenLeser.entfernteAdressenSheet
                    .createRow(i);

            if (i == 0) {
                entfernteAdressenRow.createCell(1)
                        .setCellValue("Entfernte Adressen");
            }

            String entfernteAdresse = entfernteAdressenIterator.next();

            entfernteAdressenRow.createCell(1)
                    .setCellValue(entfernteAdresse);
            i++;
        }
        initialisiereSpeicherPfad();
        schreibe("EntfernteAdressen.xlsx",
                true);
    }
}
