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

import io.temporal.activity.Activity;
import java.util.List;

public class UploadPacketActivityImpl implements UploadPacketActivity {
  @Override
  public void uploadPackets(List<Packet> packets) {
    // simulate 1 sec "upload"
    try {
      Thread.sleep(1 * 1000);
    } catch (Exception e) {
      throw Activity.wrap(e);
    }
    for (Packet p : packets) {
      System.out.println(
          "Activity - Uploaded packet with type: " + p.getType() + " and id: " + p.getId());
    }
  }
}
