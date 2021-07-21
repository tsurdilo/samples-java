package io.temporal.samples.progressreport;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ReportActivities {
  void invokeServices();
}
