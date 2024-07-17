/*
 * Copyright 2014 Ruediger Moeller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.fury.benchmark;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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


@BenchmarkMode({Mode.AverageTime})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 5)
@Threads(4)
@Fork(0)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArraysBenchmark extends BaseFuryBenchmark {

  private int[] intArr;
  private String[] strArr;

  @Param({"8", "16", "128", "100000"})
  public int size;

  @Setup
  public void setUp() {
    super.setUp();
    kryo.register(int[].class);
    kryo.register(String[].class);

    intArr = new int[size];
    strArr = new String[size];
    for (int i = 0; i < size; i++) {
      intArr[i] = i;
      strArr[i] = UUID.randomUUID().toString();
    }
  }

  @Benchmark
  public Object systemCopyArrasBenchmark() {
    int[] arr = new int[size];
    System.arraycopy(intArr, 0, arr, 0, size);
    return arr;
  }

  @Benchmark
  public Object arraysCopyArrasBenchmark() {
    return Arrays.copyOf(intArr, size);
  }

  @Benchmark
  public Object furyCopyArrasBenchmark() {
    return fury.copy(intArr);
  }

  @Benchmark
  public Object furyWithCodegenArrasBenchmark() {
    return furyWithCodegen.copy(intArr);
  }

  @Benchmark
  public Object kryoCopyArrasBenchmark() {
    return kryo.copy(intArr);
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(ArraysBenchmark.class.getSimpleName())
        .result("ArraysBenchmark.json")
        .resultFormat(ResultFormatType.JSON).build();
    new Runner(opt).run();
  }
}
