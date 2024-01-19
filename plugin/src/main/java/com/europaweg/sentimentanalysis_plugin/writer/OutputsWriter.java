package com.europaweg.sentimentanalysis_plugin.writer;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class OutputsWriter {

    public static void writeFile(final Project project, final String filename, final String content) {
        Path path = Paths.get(filename);

        // Check if the file exists
        boolean fileExists = Files.exists(path);

        if (fileExists) {
            Messages.showMessageDialog(
                    "File already exists.",
                    "Information",
                    Messages.getInformationIcon()
            );
        } else {
            ApplicationManager.getApplication().runWriteAction(() -> {
                File file = new File(createFile(project, filename).getPath());
                try {
                    writeContentToFile(file, content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Messages.showMessageDialog(
                    "Sentiment analysis output file successfully created.",
                    "Information",
                    Messages.getInformationIcon()
            );
        }

    }

    private static File createFile(Project project, String filename) {
        return WriteCommandAction.runWriteCommandAction(project, (Computable<File>) () -> {
            File file = new File(filename);
            try {
                // Create the file if it doesn't exist
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating the file: " + e.getMessage());
            }

            return file;
        });
    }

    private static void writeContentToFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(String.valueOf(file))) {
            writer.write(content);
        }
    }
}

