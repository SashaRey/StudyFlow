package com.studyflow.controller;

import com.studyflow.dto.AnalyticsSummary;
import com.studyflow.service.AnalyticsService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import java.util.ResourceBundle;

public class AnalyticsController extends BaseController {

    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label completedTasksLabel;
    @FXML
    private Label pendingTasksLabel;
    @FXML
    private Label overdueTasksLabel;
    @FXML
    private Label totalStudyTimeLabel;
    @FXML
    private Label studySessionsCountLabel;
    @FXML
    private ProgressBar completionProgressBar;
    @FXML
    private Label completionRateLabel;
    @FXML
    private Label analyticsMessageLabel;
    @FXML
    private PieChart taskStatusPieChart;
    @FXML
    private ResourceBundle resources;

    private final AnalyticsService analyticsService;

    public AnalyticsController() {
        this(new AnalyticsService());
    }

    AnalyticsController(AnalyticsService analyticsService) {
        if (analyticsService == null) {
            throw new IllegalArgumentException("AnalyticsService is required");
        }
        this.analyticsService = analyticsService;
    }

    @FXML
    public void initialize() {
        loadAnalytics();
    }

    private void loadAnalytics() {
        try {
            AnalyticsSummary summary = analyticsService.getSummary();
            totalTasksLabel.setText(String.valueOf(summary.getTotalTasks()));
            completedTasksLabel.setText(String.valueOf(summary.getCompletedTasks()));
            pendingTasksLabel.setText(String.valueOf(summary.getPendingTasks()));
            overdueTasksLabel.setText(String.valueOf(summary.getOverdueTasks()));
            totalStudyTimeLabel.setText(analyticsService.formatStudyTime(summary.getTotalStudyMinutes()));
            studySessionsCountLabel.setText(String.valueOf(summary.getStudySessionsCount()));

            double completionRate = analyticsService.calculateCompletionRate(summary);
            double completionProgress = analyticsService.calculateCompletionProgress(summary);
            completionRateLabel.setText(String.format("%.0f%%", completionRate));
            completionProgressBar.setProgress(completionProgress);

            updateTaskStatusChart(summary);

            if (summary.getTotalTasks() == 0 && summary.getStudySessionsCount() == 0) {
                analyticsMessageLabel.setText(text("analytics.empty"));
            } else {
                analyticsMessageLabel.setText("");
            }
        } catch (Exception exception) {
            analyticsMessageLabel.setText(text("analytics.loadError"));
            resetAnalyticsView();
        }
    }

    private void updateTaskStatusChart(AnalyticsSummary summary) {
        taskStatusPieChart.getData().clear();

        int completedTasks = summary.getCompletedTasks();
        int pendingTasks = summary.getPendingTasks();
        int overdueTasks = summary.getOverdueTasks();

        if (completedTasks == 0 && pendingTasks == 0 && overdueTasks == 0) {
            setNoDataChart();
        } else {
            taskStatusPieChart.setData(FXCollections.observableArrayList(
                    new PieChart.Data(text("analytics.chart.completed"), completedTasks),
                    new PieChart.Data(text("analytics.chart.pending"), pendingTasks),
                    new PieChart.Data(text("analytics.chart.overdue"), overdueTasks)
            ));
        }

        taskStatusPieChart.setLegendVisible(true);
        taskStatusPieChart.setLabelsVisible(true);
    }

    private void resetAnalyticsView() {
        totalTasksLabel.setText("0");
        completedTasksLabel.setText("0");
        pendingTasksLabel.setText("0");
        overdueTasksLabel.setText("0");
        totalStudyTimeLabel.setText("0 min");
        studySessionsCountLabel.setText("0");
        completionRateLabel.setText("0%");
        completionProgressBar.setProgress(0);
        setNoDataChart();
    }

    private void setNoDataChart() {
        taskStatusPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data(text("analytics.chart.noData"), 1)
        ));
    }

    private String text(String key) {
        if (resources != null && resources.containsKey(key)) {
            return resources.getString(key);
        }
        return key;
    }
}
