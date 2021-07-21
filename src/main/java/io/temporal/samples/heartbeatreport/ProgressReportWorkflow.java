package io.temporal.samples.heartbeatreport;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import java.util.List;

@WorkflowInterface
public interface ProgressReportWorkflow {
  @WorkflowMethod
  void exec();

  @SignalMethod
  void serviceResult(String result);

  @QueryMethod
  List<String> getResults();
}
