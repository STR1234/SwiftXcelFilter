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
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SperrlistenSwingUI {
    JFrame frame;
    JPanel buttonPanel = new JPanel();
    JPanel textPanel = new JPanel();
    ActionListener clickListener;
    File selectedFile;
    boolean isFileSelected = false;

    public SperrlistenSwingUI() {
        this.initialiseFrame();
    }

    public void initialiseFrame(){
        this.frame = new JFrame("Sperrlistenprüfer");

        JButton auswaehlenButton = new JButton("Auswählen");
        JButton abbrechenButton = new JButton("Abbrechen");

        JLabel text = new JLabel("Bitte wählen Sie die zu prüfende .xlsx " + "Datei aus.");

        textPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        textPanel.setBackground(Color.white);
        textPanel.add(text);

        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.blue);
        buttonPanel.add(auswaehlenButton);
        buttonPanel.add(abbrechenButton);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(400, 200));
        frame.getContentPane().add(textPanel);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));
        frame.pack();
        frame.setVisible(true);

        addListeners(auswaehlenButton, "auswaehlen");
        addListeners(abbrechenButton, "abbrechen");
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

            sperrlistenpruefer.pruefe();

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
                            JLabel label = (JLabel) cp2;

                            label.setText("MaillisteNeu wurde " +
                                    "gespeichert unter: ");

                            JLabel speicherPfadLabel =
                                    new JLabel(sperrlistenpruefer.speicherPfad);

                            Font font = speicherPfadLabel.getFont();
                            Map attributes = font.getAttributes();
                            attributes.put(TextAttribute.UNDERLINE,
                                    TextAttribute.UNDERLINE_ON);
                            speicherPfadLabel.setFont(font.deriveFont(attributes));

                            speicherPfadLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            speicherPfadLabel.addMouseListener((new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    if (e.getClickCount() > 0) {
                                        try {
                                            //Java 9 and newer required - using
                                            // Desktop API
                                            File file = new File(sperrlistenpruefer.speicherPfad);
                                            Desktop desktop = Desktop.getDesktop();
                                            desktop.open(file);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }));

                            textPanel.add(speicherPfadLabel);
                            frame.setSize(speicherPfadLabel.getText().length() * 7,
                                    frame.getHeight());
                        }
                    }
                }
            }

            frame.repaint();
        }
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

    public void resetButton() {
        Component[] contentPaneComponent = this.frame.getContentPane().getComponents();

        for (int i = 0; i < contentPaneComponent.length; i++) {

            if (contentPaneComponent[i] instanceof JPanel) {
                JPanel panel = (JPanel) contentPaneComponent[i];
                Component[] panelComponent = panel.getComponents();

                for (int j = 0; j < panelComponent.length; j++) {
                    if (panelComponent[j] instanceof JButton) {
                        JButton button =
                                (JButton) panelComponent[j];
                        button.setEnabled(true);
                        button.setContentAreaFilled(false);
                        for (ActionListener al : button.getActionListeners()) {
                            button.removeActionListener(al);
                        }
                        addListeners(button, "action");
                    }
                }
            }
        }
    }
}
