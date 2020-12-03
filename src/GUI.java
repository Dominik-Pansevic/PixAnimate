import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class GUI {
    private JPanel panel1;
    private JLabel animationLabel;
    private JTextField selectFilesTextField;
    private static String temporaryGifLocation;
    private JButton selectFilesLocationButton;
    private JButton selectButton;
    private JTextField saveAnimationTextField;
    private JButton changeLocationButton;
    private JButton saveButton;
    private JSlider speedSlider;
    private JTextField speedTextField;
    private JSlider pixelationStrengthSlider;
    private JTextField pixelationStrengthTextField;
    private JButton framePlusButton;
    private JButton frameMinusButton;
    private JButton applySpeedButton;
    private JButton applyButton;
    private JLabel frameLabel;
    private JLabel frameNumberLabel;
    private JTextField fileNameTextField;
    private JRadioButton seperateFramesRadioButton;
    private JRadioButton singleStripRadioButton;

    private static MainSystem mainSystem;
    private JFileChooser chooser;

    public static void main(String[] args) throws IOException {

        //font setup
        InputStream font_file = GUI.class.getResourceAsStream("/fonts/pixelated.ttf");
        InputStream font_file2 = GUI.class.getResourceAsStream("/fonts/pixelated2.ttf");
        InputStream font_file3 = GUI.class.getResourceAsStream("/fonts/pixelated3.ttf");
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, font_file);
            Font font2 = Font.createFont(Font.TRUETYPE_FONT, font_file2);
            Font font3 = Font.createFont(Font.TRUETYPE_FONT, font_file3);

            Font sizedFont1 = font.deriveFont(19f);
            Font sizedFont2 = font2.deriveFont(18f);
            Font sizedFont3 = font3.deriveFont(22f);

            UIManager.put("Button.font", sizedFont1);
            UIManager.put("Panel.font", sizedFont2);
            UIManager.put("CheckBox.font", sizedFont3);


        } catch (FontFormatException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("PixAnimate v1.0 - Animation Pixelator");

        frame.setContentPane(new GUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        // Shutdown Hook - what to do when the application terminates
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                File file = new File(temporaryGifLocation);

                if(file.delete())
                {
                    System.out.println("File deleted successfully");
                    System.out.println("Application terminated successfully");
                }
                else
                {
                    System.out.println("Failed to delete the file");
                }
            }
        }));

    }

    public GUI() throws IOException {

        mainSystem = new MainSystem();

        // ACTION LISTENERS ____________________________________________________
        selectFilesLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                openFileChooser(selectFilesTextField);

            }
        });
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {

                    if(selectFilesTextField.getText().trim().equals(""))
                    {
                        JOptionPane optionPane = new JOptionPane("Location not chosen!",JOptionPane.ERROR_MESSAGE);
                        JDialog dialog = optionPane.createDialog("Error");
                        dialog.setAlwaysOnTop(true); // to show top of all other application
                        dialog.setVisible(true); // to visible the dialog
                    }
                    else
                    {
                        mainSystem.addAnimationFrames(selectFilesTextField.getText());
                        temporaryGifLocation = selectFilesTextField.getText() + "/.testing.gif";
                        //update animation preview panel
                        createGif();
                        updateAnimationReplay();

                        //update frame preview panel
                        updateFrames();

                        //Enable Animation Editing/Saving Buttons
                        speedSlider.setEnabled(true);
                        applySpeedButton.setEnabled(true);
                        pixelationStrengthSlider.setEnabled(true);
                        applyButton.setEnabled(true);
                        framePlusButton.setEnabled(true);
                        frameMinusButton.setEnabled(true);
                        saveButton.setEnabled(true);
                        changeLocationButton.setEnabled(true);
                        fileNameTextField.setEnabled(true);
                        saveAnimationTextField.setEnabled(true);

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        });


        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                speedTextField.setText(Integer.toString(speedSlider.getValue()));
            }
        });
        applySpeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    mainSystem.createGifFromFrames(selectFilesTextField.getText(), mainSystem.getFrames().get(0).getFileName(), selectFilesTextField.getText() + "/.testing.gif",speedSlider.getValue());
                    updateAnimationReplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        pixelationStrengthSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                pixelationStrengthTextField.setText(Integer.toString(pixelationStrengthSlider.getValue()));

            }
        });
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                mainSystem.pixelateAllFrames(pixelationStrengthSlider.getValue());
                updateFrames();
                try {
                    createGif();
                    updateAnimationReplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    updateAnimationReplay();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        framePlusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(mainSystem.getSelectedFrame() < (mainSystem.getFrames().size() - 1))
                {
                    mainSystem.setSelectedFrame(mainSystem.getSelectedFrame() + 1);
                }
                else
                {
                    mainSystem.setSelectedFrame(0);
                }

                updateFrames();

            }
        });
        frameMinusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(mainSystem.getSelectedFrame() > 0)
                {
                    mainSystem.setSelectedFrame(mainSystem.getSelectedFrame() - 1);
                }
                else
                {
                    mainSystem.setSelectedFrame(mainSystem.getFrames().size() - 1);
                }

                updateFrames();
            }
        });
        changeLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openFileChooser(saveAnimationTextField);
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(saveAnimationTextField.getText().trim().equals("") || fileNameTextField.getText().trim().equals(""))
                {
                    JOptionPane optionPane = new JOptionPane("Something went wrong",JOptionPane.ERROR_MESSAGE);
                    JDialog dialog = optionPane.createDialog("Error");
                    dialog.setAlwaysOnTop(true); // to show top of all other application
                    dialog.setVisible(true); // to visible the dialog
                }
                else
                {
                    if(singleStripRadioButton.isSelected())
                    {
                        mainSystem.saveAnimationAsSingleFile(mainSystem.getFrames(),saveAnimationTextField.getText(),fileNameTextField.getText());
                    }
                    else
                    {
                        mainSystem.saveAnimationAsFiles(mainSystem.getFrames(),saveAnimationTextField.getText(),fileNameTextField.getText());
                    }

                    JOptionPane optionPane = new JOptionPane("File saved successfully at the specified location!",JOptionPane.INFORMATION_MESSAGE);
                    JDialog dialog = optionPane.createDialog("Success");
                    dialog.setAlwaysOnTop(true); // to show top of all other application
                    dialog.setVisible(true); // to visible the dialog
                }




            }
        });
        singleStripRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                singleStripRadioButton.setEnabled(false);
                singleStripRadioButton.setSelected(true);

                seperateFramesRadioButton.setEnabled(true);
                seperateFramesRadioButton.setSelected(false);

            }
        });
        seperateFramesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                seperateFramesRadioButton.setEnabled(false);
                seperateFramesRadioButton.setSelected(true);

                singleStripRadioButton.setEnabled(true);
                singleStripRadioButton.setSelected(false);

            }
        });
    }

    public void updateAnimationReplay() throws IOException {
        ImageIcon gifImage = mainSystem.createLabelIcon(selectFilesTextField.getText() + "/.testing.gif", 450);
        animationLabel.setIcon(gifImage);
    }

    public void updateFrames()
    {
        frameLabel.setIcon(mainSystem.getSelectedFrameImage(450));
        frameNumberLabel.setText("Frame: " + mainSystem.getSelectedFrame());
    }

    public void openFileChooser(JTextField textField)
    {
        chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //
        if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
            textField.setText(chooser.getSelectedFile().toString());
        } else {

        }
    }


    public void createGif() throws IOException {
        mainSystem.createGifFromFrames(selectFilesTextField.getText(), mainSystem.getFrames().get(0).getFileName(), selectFilesTextField.getText() + "/.testing.gif",speedSlider.getValue());
    }



}
