package sperrlistenpruefung;

public class SperrlistenprueferTest {
    public static void main(String[] args) {
  /**      Sperrlistenleser sperrlistenleser = new Sperrlistenleser("/Users/" +
                "stephanr/Desktop/"
                + "PrueflistenTestFiles.nosync/TestSperrliste01.xlsx");
   */
   /**     Sperrlistenpruefer sperrlistenpruefer =
                new Sperrlistenpruefer("/Users/stephanr/Desktop/"
                + "PrueflistenTestFiles.nosync/MailListeTest01.xlsx",
                        "/Users/stephanr/Desktop/"
                                + "PrueflistenTestFiles.nosync/" +
                                "TestSperrMailliste01.xlsx");
       // sperrlistenleser.gibZeilenWert(1,2);
    */
        Sperrlistenpruefer sperrlistenpruefer =
                new Sperrlistenpruefer("/Users/stephanr/" +
                        "Desktop/PrueflistenTestFiles.nosync/" +
                        "Test für Mailing.xlsx");

        sperrlistenpruefer.pruefe();
    }
}
