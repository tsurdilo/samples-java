package io.temporal.samples.heartbeatreport;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ReportActivities {
  void invokeServices();
}
