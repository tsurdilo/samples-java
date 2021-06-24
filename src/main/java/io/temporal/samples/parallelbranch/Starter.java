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

    WorkflowClient.start(workflow::startUploads);

    // Define some packets
    Packet packet11 = new Packet(1, 1, "type1content1");
    Packet packet12 = new Packet(1, 2, "type1content2");
    Packet packet13 = new Packet(1, 3, "type1content3");

    Packet packet21 = new Packet(2, 1, "type2content1");
    Packet packet22 = new Packet(2, 2, "type2content2");
    Packet packet23 = new Packet(2, 3, "type2content3");

    Packet packet31 = new Packet(3, 1, "type3content1");
    Packet packet32 = new Packet(3, 2, "type3content2");
    Packet packet33 = new Packet(3, 3, "type3content3");

    WorkflowStub untyped = WorkflowStub.fromTyped(workflow);

    // send signals in "somewhat random" order
    untyped.signal("receivePacket", packet11);
    untyped.signal("receivePacket", packet22);
    untyped.signal("receivePacket", packet33);
    untyped.signal("receivePacket", packet12);
    untyped.signal("receivePacket", packet23);
    untyped.signal("receivePacket", packet32);
    untyped.signal("receivePacket", packet13);
    untyped.signal("receivePacket", packet31);
    untyped.signal("receivePacket", packet21);

    String result = untyped.getResult(String.class);

    System.out.println(result);
    System.exit(0);
  }
}
