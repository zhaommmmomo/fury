/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fury.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

@BenchmarkMode({Mode.AverageTime})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 5)
@Threads(1)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@CompilerControl(value = CompilerControl.Mode.INLINE)
public class FuryBenchmark extends BaseFuryBenchmark{

  @Param({"10000"})
  private int iterate;
  @Param({"128"})
  private int size;
  private int[] intArr;
  private List<Object> list;
  private Map<Object, Object> map;
  private BeanA beanA;

  @Setup
  public void setUp() {
    super.setUp();
    intArr = new int[size];
    list = new ArrayList<>(size);
    map = new ConcurrentHashMap<>();
    for (int i = 0; i < size; i++) {
      intArr[i] = i;
      list.add(i);
      map.put(i, UUID.randomUUID().toString());
    }
    beanA = BeanA.createBeanA(size);
  }

  @Benchmark
  public Object systemCopyArrays() {
    int[] arr = null;
    for (int i = 0; i < iterate; i++) {
      arr = new int[size];
      System.arraycopy(intArr, 0, arr, 0, size);
    }
    return arr;
  }

  @Benchmark
  public void furyCopyArrays() {
    for (int i = 0; i < iterate; i++) {
      fury.copy(intArr);
    }
  }

  @Benchmark
  public void furyWithCodegenCopyArrays() {
    for (int i = 0; i < iterate; i++) {
      furyWithCodegen.copy(intArr);
    }
  }

  @Benchmark
  public void kyroCopyArrays() {
    for (int i = 0; i < iterate; i++) {
      kryo.copy(intArr);
    }
  }

  @Benchmark
  public void furyCopyArrayList() {
    for (int i = 0; i < iterate; i++) {
      fury.copy(list);
    }
  }

  @Benchmark
  public void furyWithCodegenCopyArrayList() {
    for (int i = 0; i < iterate; i++) {
      furyWithCodegen.copy(list);
    }
  }

  @Benchmark
  public void kyroCopyArrayList() {
    for (int i = 0; i < iterate; i++) {
      kryo.copy(list);
    }
  }

  @Benchmark
  public void furyCopyConcurrentHashMap() {
    for (int i = 0; i < iterate; i++) {
      fury.copy(map);
    }
  }

  @Benchmark
  public void furyWithCodegenCopyConcurrentHashMap() {
    for (int i = 0; i < iterate; i++) {
      furyWithCodegen.copy(map);
    }
  }

  @Benchmark
  public void kyroCopyConcurrentHashMap() {
    for (int i = 0; i < iterate; i++) {
      kryo.copy(map);
    }
  }

  @Benchmark
  public void furyCopyBean() {
    for (int i = 0; i < iterate; i++) {
      fury.copy(beanA);
    }
  }

  @Benchmark
  public void furyWithCodegenCopyBean() {
    for (int i = 0; i < iterate; i++) {
      furyWithCodegen.copy(beanA);
    }
  }

  @Benchmark
  public void kyroCopyBean() {
    for (int i = 0; i < iterate; i++) {
      kryo.copy(beanA);
    }
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(FuryBenchmark.class.getSimpleName())
        .result("FuryBenchmark_avg_time_jdk8.json")
        .resultFormat(ResultFormatType.JSON).build();
    new Runner(opt).run();
  }
}
