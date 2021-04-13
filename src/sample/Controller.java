package sample;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

public class Controller implements Initializable {

    @FXML
    Button choseImagesButton;
    @FXML
    Button chooseFolderButton;
    @FXML
    Label informationLabel;
    @FXML
    MenuButton optionsButton;
    @FXML
    TableView<ImageProcessingJob> table;
    @FXML
    TableColumn<ImageProcessingJob, String> imageNameColumn;
    @FXML
    TableColumn<ImageProcessingJob, Double> progressColumn;
    @FXML
    TableColumn<ImageProcessingJob, String> statusColumn;

    private ObservableList<ImageProcessingJob> jobs = FXCollections.observableArrayList();   //A list that enables listeners to track changes when they occur
    private File directory = null;
    private ForkJoinPool pool;
    private Boolean chosenOption = false;
    private Integer numberOfThreads = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imageNameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getFile().getName()));
        statusColumn.setCellValueFactory(p -> p.getValue().getStatusProperty());
        progressColumn.setCellFactory(ProgressBarTableCell.<ImageProcessingJob>forTableColumn());
        progressColumn.setCellValueFactory(p -> p.getValue().getProgressProperty().asObject());

        jobs = FXCollections.observableList(new ArrayList<>());
        table.setItems(jobs);
    }

    @FXML
    public void chooseImages(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG images", "*.jpg"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            jobs.clear();
            for (File f : selectedFiles) {
                jobs.add(new ImageProcessingJob(f));
            }
        } else {
            return;
        }
    }

    @FXML
    public void chooseDirectory(ActionEvent actionEvent) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directory = directoryChooser.showDialog(null);
    }

    @FXML
    void processFiles(ActionEvent event) {   //po naciśnięciu OK

        if (jobs != null && directory != null && chosenOption == true) {

            optionsButton.setDisable(true);
            choseImagesButton.setDisable(true);
            chooseFolderButton.setDisable(true);

            if (numberOfThreads > 0) {
                pool = new ForkJoinPool(numberOfThreads);
            } else {
                pool = ForkJoinPool.commonPool();
            }
            pool.submit(this::backgroundJob);

        } else if (jobs.isEmpty() || directory == null || chosenOption == false) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Warning!");
            alert.setContentText("Files, directory and option must be chosen");
            alert.showAndWait();
            return;
        }
    }

    public void sequential(ActionEvent actionEvent) {
        optionsButton.setText("sequential");
        chosenOption = true;
        numberOfThreads = 1;
    }

    public void commonPool(ActionEvent actionEvent) {
        optionsButton.setText("commonPool");
        chosenOption = true;
        numberOfThreads = 0;
    }

    public void threads2(ActionEvent actionEvent) {
        optionsButton.setText("2 threads");
        chosenOption = true;
        numberOfThreads = 2;
    }

    public void threads4(ActionEvent actionEvent) {
        optionsButton.setText("4 threads");
        chosenOption = true;
        numberOfThreads = 4;
    }

    public void threads8(ActionEvent actionEvent) {
        optionsButton.setText("8 threads");
        chosenOption = true;
        numberOfThreads = 8;
    }

    private void backgroundJob() {

        long start = System.currentTimeMillis();

        if (numberOfThreads == 1) {
            jobs.stream().forEach(job -> job.convertToGrayscale(job.getFile(), directory, job.getProgressProperty(), job));
        } else {
            jobs.parallelStream().forEach(job -> job.convertToGrayscale(job.getFile(), directory, job.getProgressProperty(), job));
        }

        long end = System.currentTimeMillis(); //czas po zakończeniu operacji [ms]
        long duration = end - start; //czas przetwarzania [ms]

        Platform.runLater(() -> informationLabel.setText("Conversion time: " + duration + " ms"));
    }
}
