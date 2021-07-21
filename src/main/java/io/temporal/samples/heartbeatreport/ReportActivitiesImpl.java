package io.temporal.samples.heartbeatreport;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class ReportActivitiesImpl implements ReportActivities {

  private static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
  private static final WorkflowClient client = WorkflowClient.newInstance(service);

  @Override
  public void invokeServices() {
    ActivityExecutionContext ctx = Activity.getExecutionContext();
    ProgressReportWorkflow workflow =
        client.newWorkflowStub(ProgressReportWorkflow.class, ctx.getInfo().getWorkflowId());

    ctx.getInfo().getWorkflowId();
    // simulate 3rd party services invocations
    // first
    sleep(3);
    workflow.serviceResult("result1");
    sleep(3);
    workflow.serviceResult("result2");
    sleep(3);
    workflow.serviceResult("result3");
  }

  private void sleep(int seconds) {
    try {
      Thread.sleep(seconds * 1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
