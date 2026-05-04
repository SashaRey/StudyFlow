package com.studyflow.controller;

import com.studyflow.exception.ValidationException;
import com.studyflow.model.Task;
import com.studyflow.model.TaskPriority;
import com.studyflow.model.TaskStatus;
import com.studyflow.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the tasks screen. Wires UI actions to TaskService.
 */
public class TaskController extends BaseController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    @FXML
    private TableView<Task> tasksTable;
    @FXML
    private TableColumn<Task, String> titleColumn;
    @FXML
    private TableColumn<Task, String> dueDateColumn;
    @FXML
    private TableColumn<Task, String> priorityColumn;
    @FXML
    private TableColumn<Task, String> statusColumn;
    @FXML
    private TableColumn<Task, String> categoryColumn;
    @FXML
    private ComboBox<TaskStatus> statusFilter;
    @FXML
    private ComboBox<TaskPriority> priorityFilter;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button completeButton;
    @FXML
    private ResourceBundle resources;

    private final TaskService taskService;
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private FilteredList<Task> filteredTasks;

    public TaskController() {
        this(new TaskService());
    }

    TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @FXML
    public void initialize() {
        configureTable();
        configureFilters();
        configureSelection();
        loadTasks();
    }

    @FXML
    public void handleAddTask() {
        showTaskDialog(null).ifPresent(this::createTask);
    }

    @FXML
    public void handleEditTask() {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        showTaskDialog(selected).ifPresent(this::updateTask);
    }

    @FXML
    public void handleDeleteTask() {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(text("tasks.alert.delete.title"));
        confirm.setHeaderText(text("tasks.alert.delete.header"));
        confirm.setContentText(text("tasks.alert.delete.content"));

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            taskService.deleteTask(selected.getId());
            loadTasks();
        } catch (ValidationException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showError(text("tasks.alert.delete.failed.title"), text("tasks.alert.delete.failed.message"));
        }
    }

    @FXML
    public void handleCompleteTask() {
        Task selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        try {
            taskService.markTaskAsCompleted(selected.getId());
            loadTasks();
        } catch (ValidationException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showError(text("tasks.alert.complete.failed.title"), text("tasks.alert.complete.failed.message"));
        }
    }

    private void configureTable() {
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(safeText(data.getValue().getTitle())));
        dueDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(formatDate(data.getValue().getDueDate())));
        priorityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(localizeEnum(data.getValue().getPriority(), "tasks.priority.")));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(localizeEnum(data.getValue().getStatus(), "tasks.status.")));
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(safeText(data.getValue().getCategory())));
    }

    private void configureFilters() {
        String statusAll = text("tasks.filters.statusAll");
        String priorityAll = text("tasks.filters.priorityAll");

        statusFilter.setItems(buildFilterItems(TaskStatus.values()));
        priorityFilter.setItems(buildFilterItems(TaskPriority.values()));
        statusFilter.setConverter(enumConverter(statusAll, "tasks.status."));
        priorityFilter.setConverter(enumConverter(priorityAll, "tasks.priority."));
        statusFilter.setCellFactory(listView -> new FilterCell<>(statusAll, "tasks.status."));
        priorityFilter.setCellFactory(listView -> new FilterCell<>(priorityAll, "tasks.priority."));
        statusFilter.setButtonCell(new FilterCell<>(statusAll, "tasks.status."));
        priorityFilter.setButtonCell(new FilterCell<>(priorityAll, "tasks.priority."));

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        priorityFilter.valueProperty().addListener((observable, oldValue, newValue) -> updateFilters());

        statusFilter.getSelectionModel().select(null);
        priorityFilter.getSelectionModel().select(null);

        filteredTasks = new FilteredList<>(tasks, task -> true);
        SortedList<Task> sortedTasks = new SortedList<>(filteredTasks);
        sortedTasks.comparatorProperty().bind(tasksTable.comparatorProperty());
        tasksTable.setItems(sortedTasks);
    }

    private void configureSelection() {
        updateActionState(null);
        tasksTable.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateActionState(newValue));
    }

    private void updateActionState(Task selected) {
        boolean hasSelection = selected != null;
        editButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
        completeButton.setDisable(!hasSelection);
    }

    private void updateFilters() {
        TaskStatus status = statusFilter.getValue();
        TaskPriority priority = priorityFilter.getValue();

        filteredTasks.setPredicate(task -> {
            if (status != null && task.getStatus() != status) {
                return false;
            }
            return priority == null || task.getPriority() == priority;
        });
    }

    private void loadTasks() {
        try {
            tasks.setAll(taskService.getAllTasks());
        } catch (Exception exception) {
            showError(text("tasks.alert.load.failed.title"), text("tasks.alert.load.failed.message"));
        }
    }

    private void createTask(Task task) {
        try {
            taskService.createTask(task);
            loadTasks();
        } catch (ValidationException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showError(text("tasks.alert.create.failed.title"), text("tasks.alert.create.failed.message"));
        }
    }

    private void updateTask(Task task) {
        try {
            taskService.updateTask(task);
            loadTasks();
        } catch (ValidationException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showError(text("tasks.alert.update.failed.title"), text("tasks.alert.update.failed.message"));
        }
    }

    private Optional<Task> showTaskDialog(Task existing) {
        boolean isEdit = existing != null;
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? text("tasks.dialog.edit.title") : text("tasks.dialog.add.title"));

        ButtonType saveButtonType = new ButtonType(isEdit ? text("tasks.dialog.save.button") : text("tasks.dialog.add.button"), ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        TextField titleField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        DatePicker dueDatePicker = new DatePicker();
        ComboBox<TaskStatus> statusBox = new ComboBox<>(FXCollections.observableArrayList(TaskStatus.values()));
        ComboBox<TaskPriority> priorityBox = new ComboBox<>(FXCollections.observableArrayList(TaskPriority.values()));
        TextField categoryField = new TextField();
        statusBox.setConverter(enumConverter("", "tasks.status."));
        priorityBox.setConverter(enumConverter("", "tasks.priority."));
        statusBox.setMaxWidth(Double.MAX_VALUE);
        priorityBox.setMaxWidth(Double.MAX_VALUE);

        if (isEdit) {
            titleField.setText(existing.getTitle());
            descriptionArea.setText(existing.getDescription());
            dueDatePicker.setValue(existing.getDueDate());
            statusBox.setValue(existing.getStatus());
            priorityBox.setValue(existing.getPriority());
            categoryField.setText(existing.getCategory());
        } else {
            statusBox.setValue(TaskStatus.PENDING);
            priorityBox.setValue(TaskPriority.MEDIUM);
        }

        grid.add(new Label(text("tasks.dialog.field.title")), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label(text("tasks.dialog.field.description")), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label(text("tasks.dialog.field.dueDate")), 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(new Label(text("tasks.dialog.field.status")), 0, 3);
        grid.add(statusBox, 1, 3);
        grid.add(new Label(text("tasks.dialog.field.priority")), 0, 4);
        grid.add(priorityBox, 1, 4);
        grid.add(new Label(text("tasks.dialog.field.category")), 0, 5);
        grid.add(categoryField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {
            if (button != saveButtonType) {
                return null;
            }

            TaskStatus status = statusBox.getValue() == null ? TaskStatus.PENDING : statusBox.getValue();
            TaskPriority priority = priorityBox.getValue() == null ? TaskPriority.MEDIUM : priorityBox.getValue();
            Long id = isEdit ? existing.getId() : null;

            return new Task(id, titleField.getText(), descriptionArea.getText(), dueDatePicker.getValue(), status, priority, categoryField.getText());
        });

        return dialog.showAndWait();
    }

    private void showValidationError(String message) {
        showError(text("tasks.alert.validation.title"), message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String safeText(String value) {
        return value == null ? "" : value;
    }

    private static String formatDate(LocalDate date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }

    private static String formatEnum(Enum<?> value) {
        if (value == null) {
            return "";
        }
        String text = value.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private String localizeEnum(Enum<?> value, String prefix) {
        if (value == null) {
            return "";
        }
        String key = prefix + value.name();
        if (resources != null && resources.containsKey(key)) {
            return resources.getString(key);
        }
        return formatEnum(value);
    }

    private <T extends Enum<T>> StringConverter<T> enumConverter(String emptyLabel, String prefix) {
        return new StringConverter<>() {
            @Override
            public String toString(T value) {
                if (value == null) {
                    return emptyLabel;
                }
                String key = prefix + value.name();
                if (resources != null && resources.containsKey(key)) {
                    return resources.getString(key);
                }
                return formatEnum(value);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    private static <T> ObservableList<T> buildFilterItems(T[] values) {
        ObservableList<T> items = FXCollections.observableArrayList();
        items.add(null);
        items.addAll(values);
        return items;
    }

    private class FilterCell<T extends Enum<T>> extends javafx.scene.control.ListCell<T> {
        private final String emptyLabel;
        private final String prefix;

        private FilterCell(String emptyLabel, String prefix) {
            this.emptyLabel = emptyLabel;
            this.prefix = prefix;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                return;
            }
            if (item == null) {
                setText(emptyLabel);
                return;
            }
            setText(localizeEnum(item, prefix));
        }
    }

    private String text(String key) {
        if (resources != null && resources.containsKey(key)) {
            return resources.getString(key);
        }
        return key;
    }
}

