package com.europaweg.sentimentanalysis_plugin.window;

import com.europaweg.sentimentanalysis_plugin.data.DataCenter;
import com.europaweg.sentimentanalysis_plugin.writer.OutputsWriter;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class OutputWindow {
    private JPanel content;
    private JTable contentTable;
    private JButton generateButton;
    private JButton clearButton;
    private JButton closeButton;
    private JTextField titleTextField;

    public OutputWindow(Project project, ToolWindow toolWindow) {
        createUIComponents();
        setupListeners(project, toolWindow);
    }

    public JComponent getContent() {
        return content;
    }

    private void createUIComponents() {
        // Create components and layout
        content = new JPanel(new BorderLayout());

        // Panel title
        JLabel titleLabel = new JLabel("My Tool Window");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(titleLabel, BorderLayout.NORTH);

        // Document input panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleInputLabel = new JLabel("  Document Title: ");
        titleTextField = new JTextField();
        titlePanel.add(titleInputLabel, BorderLayout.WEST);
        titlePanel.add(titleTextField, BorderLayout.CENTER);
        content.add(titlePanel, BorderLayout.NORTH);

        // Table Panel
        contentTable = new JTable();
        contentTable.setModel(DataCenter.Companion.getTableModel());
        contentTable.setEnabled(true);
        content.add(new JScrollPane(contentTable), BorderLayout.CENTER);

        // buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        generateButton = new JButton("Generate");
        clearButton = new JButton("Clear");
        closeButton = new JButton("Close");

        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(closeButton);

        content.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupListeners(Project project, ToolWindow toolWindow) {
        // Add listeners for your buttons
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle generate button click
                Map<String, ArrayList<Pair<String, String>>> outputsMap = DataCenter.Companion.getOutputsMap();
                if (outputsMap.isEmpty()) {
                    Messages.showMessageDialog(
                            "Empty output can not be exported. Please run the sentiment " +
                                    "analysis plugin first.",
                            "Information",
                            Messages.getInformationIcon()
                    );
                    return;
                }

                // Get file name & title
                String title = titleTextField.getText();

                if (title.isEmpty()) {
                    Messages.showMessageDialog(
                            "Document title can not be empty.",
                            "Information",
                            Messages.getInformationIcon()
                    );
                    return;
                }

                // Ask user to give a folder path
                FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(
                        false,
                        true,
                        false,
                        false,
                        false,
                        false
                );
                fileChooserDescriptor.setTitle("Output File Selector (Sentiment Analysis Plugin)");
                fileChooserDescriptor.setDescription("Please choose the output folder.");
                @Nullable VirtualFile selectedFile = FileChooser.chooseFile(fileChooserDescriptor, null, null);
                if (selectedFile != null) {
                    String outputPath = selectedFile.getPath();

                    // Call the writeFile method to perform the file write operation
                    String filename = outputPath + File.separator + title + ".md";
                    String content = DataCenter.Companion.getContent(title);
                    OutputsWriter.writeFile(project, filename, content);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle clear button click
                DataCenter.Companion.reset();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle close button click
                toolWindow.hide(null);
            }
        });
    }
    public JPanel getOutputPanel() {
        return content;
    }
}
