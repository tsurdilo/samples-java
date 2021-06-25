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

package io.temporal.samples.uploadpackets;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import java.util.ArrayList;
import java.util.List;

public class UploadPacketActivityImpl implements UploadPacketActivity {
  @Override
  public List<Packet> generatePackets() {
    List<Packet> result = new ArrayList<>();
    result.add(new Packet(1, "content1"));
    result.add(new Packet(2, "content2"));
    result.add(new Packet(3, "content3"));
    return result;
  }

  @Override
  public void uploadPacket(Packet packet) {
    ActivityExecutionContext activityExecutionContext = Activity.getExecutionContext();
    System.out.println(
        "Activity "
            + activityExecutionContext.getInfo().getActivityId()
            + " - Uploaded packet with id: "
            + packet.getId());
    // simulate some upload work
    try {
      Thread.sleep(2 * 1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
