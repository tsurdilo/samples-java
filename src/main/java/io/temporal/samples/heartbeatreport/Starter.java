package io.temporal.samples.heartbeatreport;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.util.List;

public class Starter {
  public static void main(String[] args) {
    WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    WorkflowClient client = WorkflowClient.newInstance(service);
    WorkerFactory factory = WorkerFactory.newInstance(client);
    Worker worker = factory.newWorker("testTaskQueue");

    worker.registerWorkflowImplementationTypes(ProgressReportWorkflowImpl.class);
    worker.registerActivitiesImplementations(new ReportActivitiesImpl());

    factory.start();

    WorkflowOptions workflowOptions =
        WorkflowOptions.newBuilder()
            .setTaskQueue("testTaskQueue")
            .setWorkflowId("testWorkflow")
            .build();
    ProgressReportWorkflow workflow =
        client.newWorkflowStub(ProgressReportWorkflow.class, workflowOptions);

    WorkflowClient.start(workflow::exec);

    boolean received = false;
    int current = 0;
    while (received != true) {
      List<String> activityServiceResult = workflow.getResults();
      if (!activityServiceResult.isEmpty()) {
        try {
          System.out.println("Got result: " + activityServiceResult.get(current));
          current++;
        } catch (Exception e) {
          // ignore for test...
        }
      }
      if (current == 3) {
        received = true;
      }
    }

    System.exit(0);
  }
}
