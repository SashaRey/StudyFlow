package com.studyflow.controller;

import com.studyflow.exception.ValidationException;
import com.studyflow.model.ScheduleEntry;
import com.studyflow.service.ScheduleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the schedule screen. Wires UI actions to ScheduleService.
 */
public class ScheduleController extends BaseController {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private TableView<ScheduleEntry> scheduleTable;
    @FXML
    private TableColumn<ScheduleEntry, String> subjectIdColumn;
    @FXML
    private TableColumn<ScheduleEntry, String> startTimeColumn;
    @FXML
    private TableColumn<ScheduleEntry, String> endTimeColumn;
    @FXML
    private TableColumn<ScheduleEntry, String> locationColumn;
    @FXML
    private TableColumn<ScheduleEntry, String> notesColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ResourceBundle resources;

    private final ScheduleService scheduleService;
    private final ObservableList<ScheduleEntry> entries = FXCollections.observableArrayList();

    public ScheduleController() {
        this(new ScheduleService());
    }

    ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @FXML
    public void initialize() {
        configureTable();
        configureSelection();
        loadEntries();
    }

    @FXML
    public void handleAddEntry() {
        showScheduleDialog(null).ifPresent(this::createEntry);
    }

    @FXML
    public void handleEditEntry() {
        ScheduleEntry selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        showScheduleDialog(selected).ifPresent(this::updateEntry);
    }

    @FXML
    public void handleDeleteEntry() {
        ScheduleEntry selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(text("schedule.alert.delete.title"));
        confirm.setHeaderText(text("schedule.alert.delete.header"));
        confirm.setContentText(text("schedule.alert.delete.content"));

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            scheduleService.deleteEntry(selected.getId());
            loadEntries();
        } catch (ValidationException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showError(text("schedule.alert.delete.failed.title"), text("schedule.alert.delete.failed.message"));
        }
    }

    private void configureTable() {
        subjectIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(formatId(data.getValue().getSubjectId())));
        startTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(formatDateTime(data.getValue().getStartTime())));
        endTimeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(formatDateTime(data.getValue().getEndTime())));
        locationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(safeText(data.getValue().getLocation())));
        notesColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(safeText(data.getValue().getNotes())));
        scheduleTable.setItems(entries);
    }

    private void configureSelection() {
        updateActionState(null);
        scheduleTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateActionState(newValue));
    }

    private void updateActionState(ScheduleEntry selected) {
        boolean hasSelection = selected != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    private void loadEntries() {
        try {
            entries.setAll(scheduleService.getAllEntries());
        } catch (Exception exception) {
            showError(text("schedule.alert.load.failed.title"), text("schedule.alert.load.failed.message"));
        }
    }

    private void createEntry(ScheduleEntry entry) {
        try {
            scheduleService.createEntry(entry);
            loadEntries();
        } catch (ValidationException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showError(text("schedule.alert.create.failed.title"), text("schedule.alert.create.failed.message"));
        }
    }

    private void updateEntry(ScheduleEntry entry) {
        try {
            scheduleService.updateEntry(entry);
            loadEntries();
        } catch (ValidationException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showError(text("schedule.alert.update.failed.title"), text("schedule.alert.update.failed.message"));
        }
    }

    private Optional<ScheduleEntry> showScheduleDialog(ScheduleEntry existing) {
        boolean isEdit = existing != null;
        Dialog<ScheduleEntry> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? text("schedule.dialog.edit.title") : text("schedule.dialog.add.title"));

        ButtonType saveButtonType = new ButtonType(isEdit ? text("schedule.dialog.save.button") : text("schedule.dialog.add.button"), ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField subjectIdField = new TextField();
        DatePicker startDatePicker = new DatePicker();
        TextField startTimeField = new TextField();
        startTimeField.setPromptText(text("schedule.dialog.field.timeHint"));
        DatePicker endDatePicker = new DatePicker();
        TextField endTimeField = new TextField();
        endTimeField.setPromptText(text("schedule.dialog.field.timeHint"));
        TextField locationField = new TextField();
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);

        if (isEdit) {
            subjectIdField.setText(formatId(existing.getSubjectId()));
            if (existing.getStartTime() != null) {
                startDatePicker.setValue(existing.getStartTime().toLocalDate());
                startTimeField.setText(existing.getStartTime().toLocalTime().format(TIME_FORMAT));
            }
            if (existing.getEndTime() != null) {
                endDatePicker.setValue(existing.getEndTime().toLocalDate());
                endTimeField.setText(existing.getEndTime().toLocalTime().format(TIME_FORMAT));
            }
            locationField.setText(existing.getLocation());
            notesArea.setText(existing.getNotes());
        }

        grid.add(new Label(text("schedule.dialog.field.subjectId")), 0, 0);
        grid.add(subjectIdField, 1, 0);
        grid.add(new Label(text("schedule.dialog.field.startDate")), 0, 1);
        grid.add(buildDateTimeBox(startDatePicker, startTimeField), 1, 1);
        grid.add(new Label(text("schedule.dialog.field.endDate")), 0, 2);
        grid.add(buildDateTimeBox(endDatePicker, endTimeField), 1, 2);
        grid.add(new Label(text("schedule.dialog.field.location")), 0, 3);
        grid.add(locationField, 1, 3);
        grid.add(new Label(text("schedule.dialog.field.notes")), 0, 4);
        grid.add(notesArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button != saveButtonType) {
                return null;
            }

            Long id = isEdit ? existing.getId() : null;
            Long subjectId = parseLong(subjectIdField.getText());
            LocalDateTime startTime = parseDateTime(startDatePicker, startTimeField);
            LocalDateTime endTime = parseDateTime(endDatePicker, endTimeField);
            return new ScheduleEntry(id, subjectId, startTime, endTime, locationField.getText(), notesArea.getText());
        });

        return dialog.showAndWait();
    }

    private HBox buildDateTimeBox(DatePicker datePicker, TextField timeField) {
        HBox box = new HBox(8);
        box.getChildren().addAll(datePicker, timeField);
        return box;
    }

    private LocalDateTime parseDateTime(DatePicker datePicker, TextField timeField) {
        LocalDate date = datePicker.getValue();
        String timeText = timeField.getText();
        if (date == null || timeText == null || timeText.isBlank()) {
            return null;
        }
        try {
            LocalTime time = LocalTime.parse(timeText.trim(), TIME_FORMAT);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    private void showValidationError(String message) {
        showError(text("schedule.alert.validation.title"), message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String formatId(Long value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? "" : DISPLAY_FORMAT.format(value);
    }

    private static Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String text(String key) {
        if (resources != null && resources.containsKey(key)) {
            return resources.getString(key);
        }
        return key;
    }
}

