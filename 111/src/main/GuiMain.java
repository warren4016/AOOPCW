package main;

import Model.Model;
import Model.ModelInterface;
import view.CombinedView;
import controller.GameController;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GuiMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String dictionaryFile = "dictionary.txt";

                Path path = Paths.get(dictionaryFile);
                if (!Files.exists(path)) {
                    try {
                        Files.createFile(path);
                        System.out.println("Dictionary file created: " + dictionaryFile);
                    } catch (IOException ex) {
                        System.err.println("Failed to create dictionary file: " + ex.getMessage());
                        return;
                    }
                }

                // 使用字典文件路径创建 Model 对象
                ModelInterface model = new Model(dictionaryFile);
                ((Model) model).setRandomWords(true);
                GameController controller = new GameController(model);
                CombinedView combinedView = new CombinedView(model, controller);
                controller.setView(combinedView);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}