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


import com.esotericsoftware.kryo.Kryo;
import org.apache.fury.Fury;
import org.apache.fury.config.Language;
import org.openjdk.jmh.annotations.Setup;

public abstract class BaseFuryBenchmark {
  protected Fury fury;
  protected Fury furyWithCodegen;
  protected Kryo kryo;

  @Setup
  public void setUp() {
    fury = Fury.builder()
        .withLanguage(Language.JAVA)
        .withCopyRefTracking(true)
        .requireClassRegistration(false)
        .withCodegen(false)
        .build();
    furyWithCodegen = Fury.builder()
        .withLanguage(Language.JAVA)
        .withCopyRefTracking(true)
        .requireClassRegistration(false)
        .withCodegen(true)
        .build();
    kryo = new Kryo();
    kryo.setReferences(true);
    kryo.setRegistrationRequired(false);
  }
}
