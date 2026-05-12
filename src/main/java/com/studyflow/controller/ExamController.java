package com.studyflow.controller;

import com.studyflow.model.Exam;
import com.studyflow.model.ExamStatus;
import com.studyflow.model.Subject;
import com.studyflow.service.ExamService;
import com.studyflow.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for the exams screen. Wires UI actions to ExamService.
 */
public class ExamController extends BaseController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    @FXML
    private ComboBox<Subject> subjectComboBox;
    @FXML
    private DatePicker examDatePicker;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<ExamStatus> statusComboBox;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private TableView<Exam> examsTableView;
    @FXML
    private TableColumn<Exam, String> subjectColumn;
    @FXML
    private TableColumn<Exam, String> dateColumn;
    @FXML
    private TableColumn<Exam, String> locationColumn;
    @FXML
    private TableColumn<Exam, String> statusColumn;
    @FXML
    private Label examsMessageLabel;
    @FXML
    private ResourceBundle resources;

    private final ExamService examService;
    private final SubjectService subjectService;
    private final ObservableList<Exam> exams = FXCollections.observableArrayList();
    private final ObservableList<Subject> subjects = FXCollections.observableArrayList();

    public ExamController() {
        this(new ExamService(), new SubjectService());
    }

    ExamController(ExamService examService) {
        this(examService, new SubjectService());
    }

    ExamController(ExamService examService, SubjectService subjectService) {
        if (examService == null) {
            throw new IllegalArgumentException("ExamService is required");
        }
        if (subjectService == null) {
            throw new IllegalArgumentException("SubjectService is required");
        }
        this.examService = examService;
        this.subjectService = subjectService;
    }

    @FXML
    public void initialize() {
        configureStatusBox();
        configureSubjectBox();
        configureTable();
        configureSelection();
        loadExams();
    }

    @FXML
    public void handleAddExam() {
        try {
            Exam exam = buildExam(null);
            examService.createExam(exam);
            loadExams();
            clearForm();
            showMessage(text("exams.message.saved"));
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage());
        } catch (RuntimeException exception) {
            showMessage(text("exams.message.error"));
        }
    }

    @FXML
    public void handleUpdateExam() {
        Exam selected = examsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage(text("exams.message.selectExam"));
            return;
        }

        try {
            Exam updated = buildExam(selected.getId());
            examService.updateExam(updated);
            loadExams();
            clearForm();
            showMessage(text("exams.message.updated"));
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage());
        } catch (RuntimeException exception) {
            showMessage(text("exams.message.error"));
        }
    }

    @FXML
    public void handleDeleteExam() {
        Exam selected = examsTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage(text("exams.message.selectExam"));
            return;
        }

        try {
            examService.deleteExam(selected.getId().intValue());
            loadExams();
            clearForm();
            showMessage(text("exams.message.deleted"));
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage());
        } catch (RuntimeException exception) {
            showMessage(text("exams.message.error"));
        }
    }

    @FXML
    public void handleClearForm() {
        clearForm();
    }

    private void configureStatusBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(ExamStatus.values()));
        statusComboBox.setConverter(enumConverter(""));
        statusComboBox.setValue(ExamStatus.UPCOMING);
    }

    private void configureSubjectBox() {
        subjectComboBox.setItems(subjects);
        subjectComboBox.setEditable(true);
        StringConverter<Subject> converter = subjectConverter();
        subjectComboBox.setConverter(converter);
        subjectComboBox.setCellFactory(listView -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : converter.toString(item));
            }
        });
        subjectComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : converter.toString(item));
            }
        });
        loadSubjects();
    }

    private void configureTable() {
        subjectColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(findSubjectLabel(data.getValue())));
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(formatDate(data.getValue().getExamDate())));
        locationColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(safeText(data.getValue().getLocation())));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(localizeEnum(data.getValue().getStatus())));
        examsTableView.setItems(exams);
    }

    private void configureSelection() {
        updateActionState(null);
        examsTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    updateActionState(newValue);
                    populateForm(newValue);
                });
    }

    private void updateActionState(Exam selected) {
        boolean hasSelection = selected != null;
        updateButton.setDisable(!hasSelection);
        deleteButton.setDisable(!hasSelection);
    }

    private void loadExams() {
        try {
            exams.setAll(examService.getAllExams());
        } catch (RuntimeException exception) {
            exams.clear();
            showMessage(text("exams.message.error"));
        }
    }

    private void loadSubjects() {
        try {
            subjects.setAll(subjectService.getAllSubjects());
        } catch (RuntimeException exception) {
            subjects.clear();
            showMessage(text("exams.message.subjectsLoadFailed"));
        }
    }

    private Exam buildExam(Long id) {
        Exam exam = new Exam();
        exam.setId(id);
        Subject subject = resolveSubject();
        exam.setSubjectId(subject.getId());
        exam.setTitle(subject.getName());
        exam.setExamDate(examDatePicker.getValue());
        exam.setLocation(locationField.getText());
        exam.setStatus(statusComboBox.getValue());
        return exam;
    }

    private void populateForm(Exam exam) {
        if (exam == null) {
            return;
        }
        Subject match = findSubjectById(exam.getSubjectId());
        if (match == null) {
            subjectComboBox.getSelectionModel().clearSelection();
            subjectComboBox.getEditor().setText(safeText(exam.getTitle()));
        } else {
            subjectComboBox.setValue(match);
            subjectComboBox.getEditor().setText(safeText(match.getName()));
        }
        examDatePicker.setValue(exam.getExamDate());
        locationField.setText(safeText(exam.getLocation()));
        statusComboBox.setValue(exam.getStatus());
    }

    private void clearForm() {
        subjectComboBox.getSelectionModel().clearSelection();
        subjectComboBox.getEditor().clear();
        examDatePicker.setValue(null);
        locationField.clear();
        statusComboBox.setValue(ExamStatus.UPCOMING);
        examsTableView.getSelectionModel().clearSelection();
        updateActionState(null);
        showMessage("");
    }

    private void showMessage(String message) {
        examsMessageLabel.setText(message == null ? "" : message);
    }

    private String localizeEnum(Enum<?> value) {
        if (value == null) {
            return "";
        }
        String key = "exams.status." + value.name();
        if (resources != null && resources.containsKey(key)) {
            return resources.getString(key);
        }
        return formatEnum(value);
    }

    private StringConverter<ExamStatus> enumConverter(String emptyLabel) {
        return new StringConverter<>() {
            @Override
            public String toString(ExamStatus value) {
                if (value == null) {
                    return emptyLabel;
                }
                return localizeEnum(value);
            }

            @Override
            public ExamStatus fromString(String string) {
                return null;
            }
        };
    }

    private StringConverter<Subject> subjectConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Subject subject) {
                if (subject == null) {
                    return "";
                }
                return safeText(subject.getName());
            }

            @Override
            public Subject fromString(String string) {
                return null;
            }
        };
    }

    private Subject findSubjectById(Long subjectId) {
        if (subjectId == null) {
            return null;
        }
        for (Subject subject : subjects) {
            if (subjectId.equals(subject.getId())) {
                return subject;
            }
        }
        return null;
    }

    private Subject resolveSubject() {
        String editorText = safeText(subjectComboBox.getEditor().getText()).trim();
        String subjectName = editorText;
        if (subjectName.isEmpty()) {
            Subject selected = subjectComboBox.getValue();
            subjectName = selected == null ? "" : safeText(selected.getName()).trim();
        }
        if (subjectName.isEmpty()) {
            throw new IllegalArgumentException(text("exams.validation.subjectRequired"));
        }
        Subject subject = subjectService.getOrCreateSubjectByName(subjectName);
        if (findSubjectById(subject.getId()) == null) {
            subjects.add(subject);
        }
        return subject;
    }

    private String findSubjectLabel(Exam exam) {
        if (exam == null) {
            return "";
        }
        Subject subject = findSubjectById(exam.getSubjectId());
        if (subject != null && subject.getName() != null) {
            return subject.getName();
        }
        String title = safeText(exam.getTitle()).trim();
        if (!title.isEmpty()) {
            return title;
        }
        return exam.getSubjectId() == null ? "" : "#" + exam.getSubjectId();
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

    private String text(String key) {
        if (resources != null && resources.containsKey(key)) {
            return resources.getString(key);
        }
        return key;
    }
}
