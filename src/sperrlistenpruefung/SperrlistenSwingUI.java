package sperrlistenpruefung;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SperrlistenSwingUI {
    JFrame frame;
    JPanel buttonPanel = new JPanel();
    JPanel textPanelEins = new JPanel();
    JPanel textPanelZwei = new JPanel();
    ActionListener clickListener;
    File selectedFile;
    boolean isFileSelected = false;
    String verwendetesOS;
    //Für spätere Sprachunterscheidung je nach Systemsprache.
    String buttonTextAuswählen;
    String buttonTextAbbrechen;
    String labelText;



    public SperrlistenSwingUI() {
        this.initialiseFrame();
    }

    public void initialiseFrame(){
        this.frame = new JFrame("Sperrlistenprüfer");

        JButton auswaehlenButton = new JButton("Auswählen");
        JButton abbrechenButton = new JButton("Abbrechen");

        JLabel textEins = new JLabel("Bitte wählen Sie die zu prüfende .xlsx "
                + "Datei aus.");
        textEins.setName("textEins");
        JLabel textZwei = new JLabel();
        textZwei.setName("textZwei");

        initializePanelsAndLabels(textPanelEins, textEins);
        initializePanelsAndLabels(textPanelZwei, textZwei);

        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.blue);
        buttonPanel.add(auswaehlenButton);
        buttonPanel.add(abbrechenButton);

        frame.getContentPane().setLayout(new FlowLayout());
        frame.setPreferredSize(new Dimension(400, 200));
        frame.getContentPane().add(textPanelEins);
        frame.getContentPane().add(textPanelZwei);
        frame.getContentPane().add(buttonPanel);
        frame.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        addListeners(auswaehlenButton, "auswaehlen");
        addListeners(abbrechenButton, "abbrechen");
    }

    public void initializePanelsAndLabels(JPanel textPanel, JLabel textLabel) {
        textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        textPanel.setBackground(Color.white);
        textPanel.add(textLabel);
    }

    public void fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("XLSX Datei",
                new String[] {"xlsx"});
        fileChooser.setFileFilter(filter);

        int optionChosen = fileChooser.showDialog(null, "Tabellendatei " +
                "auswählen");

        if (optionChosen == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();

            Sperrlistenpruefer sperrlistenpruefer =
                    new Sperrlistenpruefer(selectedFile.getAbsolutePath());

            verwendetesOS = sperrlistenpruefer.verwendetesOS;

            sperrlistenpruefer.pruefe();

            String maillistenNeuPfad = sperrlistenpruefer.maillisteNeuPfad;

            String entfernteAdressenPfad = sperrlistenpruefer.speicherPfad;

            for (Component cp : frame.getContentPane().getComponents()) {
                if (cp instanceof JPanel) {
                    JPanel panel = (JPanel) cp;
                    for (Component cp2 : (panel.getComponents())) {
                        if (cp2 instanceof JButton) {
                            JButton button = (JButton) cp2;
                            String buttonText = button.getText();
                            if (buttonText.equals("Auswählen")) {
                                panel.remove(cp2);
                            } else if (buttonText.equals("Abbrechen")) {
                                button.setText("Beenden");
                            }
                        }
                        if (cp2 instanceof JLabel) {
                            if (cp2.getName().equals("textEins")) {
                                JLabel label = (JLabel) cp2;

                                label.setText("MaillisteNeu wurde "
                                        + "gespeichert unter: ");

                                JLabel speicherPfadLabel = speicherPfadFactory(
                                        sperrlistenpruefer, maillistenNeuPfad);

                                speicherPfadLabel.addMouseListener((
                                        mouseAdapterFactory(sperrlistenpruefer,
                                                maillistenNeuPfad)));

                                textPanelEins.add(speicherPfadLabel);
                            } else if (cp2.getName().equals("textZwei")) {
                                JLabel label = (JLabel) cp2;

                                label.setText("Enternte Adressen wurde "
                                        + "gespeichert unter: ");

                                JLabel entfernteAdressenPfadLabel =
                                        speicherPfadFactory(sperrlistenpruefer,
                                                entfernteAdressenPfad);

                                entfernteAdressenPfadLabel.addMouseListener((
                                        mouseAdapterFactory(sperrlistenpruefer,
                                                entfernteAdressenPfad)));

                                textPanelZwei.add(entfernteAdressenPfadLabel);
                                frame.setSize(entfernteAdressenPfadLabel
                                        .getText()
                                        .length() * 14, frame.getHeight());
                            }

                            frame.setLocationRelativeTo(null);
                        }
                    }
                }
            }
            frame.repaint();
        }
    }

    public JLabel speicherPfadFactory(Sperrlistenpruefer sperrlistenpruefer,
                                      String dateiPfad) {
        JLabel speicherPfadLabel =
                new JLabel(dateiPfad);

        Font font = speicherPfadLabel.getFont();
        Map attributes = font.getAttributes();
        attributes.put(TextAttribute.UNDERLINE,
                TextAttribute.UNDERLINE_ON);
        speicherPfadLabel
                .setFont(font.deriveFont(attributes));

        speicherPfadLabel.setCursor(Cursor.
                getPredefinedCursor(Cursor.HAND_CURSOR));

        return speicherPfadLabel;
    }

    public MouseAdapter mouseAdapterFactory(
            Sperrlistenpruefer sperrlistenpruefer, String dateiPfad) {
        MouseAdapter mouseAdapter =
        new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    try {
                        //Java 9 and newer required - using
                        // Desktop API
                        File file =
                                new File(dateiPfad);
                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop =
                                    Desktop.getDesktop();

                            desktop.open(file);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        return mouseAdapter;
    }

    public void addListeners(JButton button, String buttonFunktion) {
            clickListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (buttonFunktion.equals("auswaehlen")) {
                        fileChooser();
                    } else if (buttonFunktion.equals("abbrechen")) {
                        frame.dispose();
                    } else if (buttonFunktion.equals("fileChooser")) {
                        isFileSelected = true;
                    }
                }
            };
            button.addActionListener(clickListener);
    }

    public void unselectButton() {
        for (Component cp : buttonPanel.getComponents()) {
            if (cp instanceof JToggleButton) {
                JToggleButton buttonFromPanel = (JToggleButton) cp;
                buttonFromPanel.setSelected(false);
            }
        }
    }
}
