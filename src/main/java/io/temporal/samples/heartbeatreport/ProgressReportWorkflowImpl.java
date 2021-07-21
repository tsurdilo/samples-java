package io.temporal.samples.heartbeatreport;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ProgressReportWorkflowImpl implements ProgressReportWorkflow {

  private List<String> serviceResults = new ArrayList<>();

  private final ReportActivities activities =
      Workflow.newActivityStub(
          ReportActivities.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(10)).build());

  @Override
  public void exec() {
    activities.invokeServices();
  }

  @Override
  public void serviceResult(String result) {
    serviceResults.add(result);
  }

  @Override
  public List<String> getResults() {
    return serviceResults;
  }
}
