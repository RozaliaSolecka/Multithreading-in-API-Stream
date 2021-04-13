package sample;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageProcessingJob {
    public static final String STATUS_WAITING = "Waiting";
    public static final String STATUS_INIT = "Working";
    public static final String STATUS_DONE = "Converted";

    final SimpleStringProperty status;
    final DoubleProperty progress;
    final File file;

    public ImageProcessingJob(File file) {
        this.file = new File(file.getPath());
        this.status = new SimpleStringProperty(STATUS_WAITING);
        this.progress = new SimpleDoubleProperty(0);
    }

    public File getFile() {
        return file;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public DoubleProperty getProgressProperty() {
        return progress;
    }

    public SimpleStringProperty getStatusProperty() {
        return status;
    }

    public void convertToGrayscale(File originalFile, File outputDir, DoubleProperty progressProp, ImageProcessingJob job) {

        Platform.runLater(() -> job.setStatus(ImageProcessingJob.STATUS_INIT));

        try {
            BufferedImage original = ImageIO.read(originalFile);
            BufferedImage grayscale = new BufferedImage(
                    original.getWidth(), original.getHeight(), original.getType());

            for (int i = 0; i < original.getWidth(); i++) {
                for (int j = 0; j < original.getHeight(); j++) {
                    int red = new Color(original.getRGB(i, j)).getRed();
                    int green = new Color(original.getRGB(i, j)).getGreen();
                    int blue = new Color(original.getRGB(i, j)).getBlue();
                    int luminosity = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                    int newPixel = new Color(luminosity, luminosity, luminosity).getRGB();
                    grayscale.setRGB(i, j, newPixel);
                }
                double progress = (1.0 + i) / original.getWidth();
                Platform.runLater(() -> progressProp.set(progress));
            }
            Path outputPath = Paths.get(outputDir.getAbsolutePath(), originalFile.getName());
            ImageIO.write(grayscale, "jpg", outputPath.toFile());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Platform.runLater(() -> job.setStatus(ImageProcessingJob.STATUS_DONE));

    }
}
