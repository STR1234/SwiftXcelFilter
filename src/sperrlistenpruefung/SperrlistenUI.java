package sperrlistenpruefung;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class SperrlistenUI extends Application {
    FileChooser fileChooserWindow;
    Stage stage;
  /**  public static void main(String[] args) {
        launch(args);
    }
*/
    public SperrlistenUI() {
    }

    @Override
    public void start(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öffne Mail- und Sperrlistentabelle");
        fileChooser.getExtensionFilters().
                add(new FileChooser.
                        ExtensionFilter("XLSX Dateien"
                        , "*.xlsx"));

        File listendatei;

        fileChooserWindow = fileChooser;
        stage = primaryStage;
        listendatei = fileChooser.showOpenDialog(primaryStage);

        if (listendatei != null) {
            Sperrlistenpruefer sperrlistenpruefer =
                    new Sperrlistenpruefer(listendatei.getAbsolutePath());

            sperrlistenpruefer.pruefe();

            Stage speicherOrt = new Stage();

            speicherOrt.initModality(Modality.APPLICATION_MODAL);
            speicherOrt.initOwner(primaryStage);
            VBox speicherOrtVbox = new VBox(20);
            speicherOrtVbox.getChildren().add(new Text("MaillisteNeu wurde " +
                    "gespeichert unter: " + sperrlistenpruefer.speicherPfad));
            Scene speicherOrtScene = new Scene(speicherOrtVbox, 800, 20);
            speicherOrt.setScene(speicherOrtScene);
            speicherOrt.show();
        } else {
           // System.exit(1);
          //  primaryStage.hide();
            stage.hide();
            SperrlistenSwingUIStarter.swingUI.unselectButton();
            SperrlistenSwingUIStarter.swingUI.frame.requestFocus();
            SperrlistenSwingUIStarter.swingUI.notifyAll();

            try {
                this.wait();
                stage.show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    public void showStage() {
        stage.show();
    }

}
