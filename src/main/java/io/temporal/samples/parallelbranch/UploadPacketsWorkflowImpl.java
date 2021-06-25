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
import io.temporal.workflow.CompletablePromise;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.*;

import org.slf4j.Logger;

public class UploadPacketsWorkflowImpl implements UploadPacketsWorkflow {


    private final Logger logger = Workflow.getLogger(UploadPacketsWorkflowImpl.class);

    private final Map<Integer, PacketApproval> packetApprovals = new HashMap<>();

    private class PacketApproval {
        private final Packet packet;
        private int approvalsLeft;
        private CompletablePromise<Void> approved = Workflow.newPromise();

        private PacketApproval(Packet packet, int approvalsLeft) {
            this.packet = packet;
            this.approvalsLeft = approvalsLeft;
        }

        public Promise<Void> getUploaded() {
            return approved;
        }

        public void approve() {
            if (--approvalsLeft == 0) {
                approved.completeFrom(Async.procedure(activities::uploadPacket, packet));
            }
        }

    }

    private final UploadPacketActivity activities =
            Workflow.newActivityStub(
                    UploadPacketActivity.class,
                    ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(5)).build());

    @Override
    public void execute(int approvalsNeeded) {
        List<Promise<Void>> packetsUploaded = new ArrayList<>();
        List<Packet> packets = activities.generatePackets();
        // set up the packet type list
        for (Packet packet : packets) {
            PacketApproval approval = new PacketApproval(packet, approvalsNeeded);
            packetApprovals.put(packet.getId(), approval);
            packetsUploaded.add(approval.getUploaded());
        }

        Promise.allOf(packetsUploaded);
    }

    @Override
    public void approvePacket(int packetId) {
        packetApprovals.get(packetId).approve();
    }
}
