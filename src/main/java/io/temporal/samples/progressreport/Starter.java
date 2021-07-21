/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.samples.progressreport;

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
    while (!received) {
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
