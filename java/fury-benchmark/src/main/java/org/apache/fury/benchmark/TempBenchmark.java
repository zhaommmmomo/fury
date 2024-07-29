package org.apache.fury.benchmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.fury.test.bean.BeanA;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode({Mode.Throughput})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 5)
@Threads(1)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.SECONDS)
@CompilerControl(value = CompilerControl.Mode.INLINE)
public class TempBenchmark extends BaseFuryBenchmark {
  @Param({"128"})
  private int size;
  private List<Object> list;

  @Setup
  public void setUp() {
    super.setUp();
    list = new CopyOnWriteArrayList<>();
    for (int i = 0; i < size; i++) {
      list.add(i);
    }
  }

  @Benchmark
  public Object test() {
    return fury.copy(list);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(TempBenchmark.class.getSimpleName())
        .result("TempBenchmark.json")
        .resultFormat(ResultFormatType.JSON).build();
    new Runner(opt).run();
  }
}
