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

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.*;
import org.slf4j.Logger;

public class UploadPacketsWorkflowImpl implements UploadPacketsWorkflow {

  private int numOfPacketTypes;
  private int numOfPacketTypesRequired;
  private int packetTypesUploaded = 0;

  private final Map<Integer, List<Packet>> packetTypeList = new HashMap<>();

  private final Logger logger = Workflow.getLogger(UploadPacketsWorkflowImpl.class);

  private final UploadPacketActivity activities =
      Workflow.newActivityStub(
          UploadPacketActivity.class,
          ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());

  @Override
  public String startUploads(int numOfPacketTypes, int numOfPacketTypesRequired) {
    this.numOfPacketTypes = numOfPacketTypes;
    this.numOfPacketTypesRequired = numOfPacketTypesRequired;

    List<Promise<Object>> typePromiseList = new ArrayList<>();

    // set up the packet type list
    for (int i = 1; i <= numOfPacketTypes; i++) {
      packetTypeList.put(i, new ArrayList<>());
      typePromiseList.add(
          Async.function(this::waitToReceivePackets, i)
              .thenApply(
                  (pt) ->
                      Async.function(activities::uploadPackets, pt)
                          .thenApply((ar) -> packetTypesUploaded++)));
    }

    Promise.anyOf(typePromiseList);

    Workflow.await(() -> packetTypesUploaded == numOfPacketTypes);
    return "done";
  }

  @Override
  public void receivePacket(Packet packet) {
    if (packetTypeList.containsKey(packet.getType())) {
      packetTypeList.get(packet.getType()).add(packet);
    } else {
      logger.warn("Invalid packet type: " + packet.getType());
    }
  }

  private List<Packet> waitToReceivePackets(int packetType) {
    Workflow.await(
        Duration.ofSeconds(10),
        () -> packetTypeList.get(packetType).size() == numOfPacketTypesRequired);
    return packetTypeList.get(packetType);
  }
}
