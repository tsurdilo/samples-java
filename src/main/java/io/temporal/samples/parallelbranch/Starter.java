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

package io.temporal.samples.parallelbranch;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {
  static final String TASK_QUEUE = "UploadPacketsTaskQueue";
  static final String WORKFLOW_ID = "UploadPacketsWorkflow";

  public static void main(String[] args) {
    WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    WorkflowClient client = WorkflowClient.newInstance(service);
    WorkerFactory factory = WorkerFactory.newInstance(client);
    Worker worker = factory.newWorker(TASK_QUEUE);
    worker.registerWorkflowImplementationTypes(UploadPacketsWorkflowImpl.class);
    worker.registerActivitiesImplementations(new UploadPacketActivityImpl());

    factory.start();

    UploadPacketsWorkflow workflow =
        client.newWorkflowStub(
            UploadPacketsWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId(WORKFLOW_ID)
                .setTaskQueue(TASK_QUEUE)
                .build());

    WorkflowClient.start(workflow::execute, 3);

    try {
      Thread.sleep(3 * 1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    WorkflowStub untyped = WorkflowStub.fromTyped(workflow);
    untyped.signal("approvePacket", 1);
    untyped.signal("approvePacket", 2);
    untyped.signal("approvePacket", 1);

    untyped.signal("approvePacket", 2);
    untyped.signal("approvePacket", 3);
    untyped.signal("approvePacket", 3);

    untyped.signal("approvePacket", 2);
    untyped.signal("approvePacket", 3);
    untyped.signal("approvePacket", 1);

    untyped.getResult(String.class);

    System.exit(0);
  }
}
