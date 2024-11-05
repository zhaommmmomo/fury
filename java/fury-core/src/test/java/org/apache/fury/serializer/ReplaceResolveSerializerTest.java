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

package org.apache.fury.serializer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.fury.Fury;
import org.apache.fury.FuryTestBase;
import org.apache.fury.config.Language;
import org.apache.fury.util.Preconditions;
import org.testng.annotations.Test;

public class ReplaceResolveSerializerTest extends FuryTestBase {

  @Data
  public static class CustomReplaceClass1 implements Serializable {
    public transient String name;

    public CustomReplaceClass1(String name) {
      this.name = name;
    }

    private Object writeReplace() {
      return new Replaced(name);
    }

    private static final class Replaced implements Serializable {
      public String name;

      public Replaced(String name) {
        this.name = name;
      }

      private Object readResolve() {
        return new CustomReplaceClass1(name);
      }
    }
  }

  @Test(dataProvider = "referenceTrackingConfig")
  public void testCommonReplace(boolean referenceTracking) {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(referenceTracking)
            .build();
    CustomReplaceClass1 o1 = new CustomReplaceClass1("abc");
    fury.registerSerializer(CustomReplaceClass1.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(CustomReplaceClass1.Replaced.class, ReplaceResolveSerializer.class);
    serDeCheck(fury, o1);
    assertTrue(
        fury.getClassResolver().getSerializer(o1.getClass()) instanceof ReplaceResolveSerializer);

    ImmutableList<Integer> list1 = ImmutableList.of(1, 2, 3, 4);
    fury.registerSerializer(list1.getClass(), new ReplaceResolveSerializer(fury, list1.getClass()));
    serDeCheck(fury, list1);

    ImmutableMap<String, Integer> map1 = ImmutableMap.of("k1", 1, "k2", 2);
    fury.registerSerializer(map1.getClass(), new ReplaceResolveSerializer(fury, map1.getClass()));
    serDeCheck(fury, map1);
    assertTrue(
        fury.getClassResolver().getSerializer(list1.getClass())
            instanceof ReplaceResolveSerializer);
    assertTrue(
        fury.getClassResolver().getSerializer(map1.getClass()) instanceof ReplaceResolveSerializer);
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testCommonReplace(Fury fury) {
    CustomReplaceClass1 o1 = new CustomReplaceClass1("abc");
    fury.registerSerializer(CustomReplaceClass1.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(CustomReplaceClass1.Replaced.class, ReplaceResolveSerializer.class);
    copyCheck(fury, o1);

    ImmutableList<Integer> list1 = ImmutableList.of(1, 2, 3, 4);
    fury.registerSerializer(list1.getClass(), new ReplaceResolveSerializer(fury, list1.getClass()));
    copyCheck(fury, list1);

    ImmutableMap<String, Integer> map1 = ImmutableMap.of("k1", 1, "k2", 2);
    fury.registerSerializer(map1.getClass(), new ReplaceResolveSerializer(fury, map1.getClass()));
    copyCheck(fury, map1);
  }

  @Data
  public static class CustomReplaceClass2 implements Serializable {
    public boolean copy;
    public transient int age;

    public CustomReplaceClass2(boolean copy, int age) {
      this.copy = copy;
      this.age = age;
    }

    // private `writeReplace` is not available to subclass and will be ignored by
    // `java.io.ObjectStreamClass.getInheritableMethod`
    Object writeReplace() {
      if (age > 5) {
        return new Object[] {copy, age};
      } else {
        if (copy) {
          return new CustomReplaceClass2(copy, age);
        } else {
          return this;
        }
      }
    }

    Object readResolve() {
      if (copy) {
        return new CustomReplaceClass2(copy, age);
      }
      return this;
    }
  }

  @Test(dataProvider = "referenceTrackingConfig")
  public void testWriteReplaceCircularClass(boolean referenceTracking) {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(referenceTracking)
            .build();
    fury.registerSerializer(CustomReplaceClass2.class, ReplaceResolveSerializer.class);
    for (Object o :
        new Object[] {
          new CustomReplaceClass2(false, 2), new CustomReplaceClass2(true, 2),
        }) {
      assertEquals(jdkDeserialize(jdkSerialize(o)), o);
      fury.registerSerializer(o.getClass(), ReplaceResolveSerializer.class);
      serDeCheck(fury, o);
    }
    CustomReplaceClass2 o = new CustomReplaceClass2(false, 6);
    Object[] newObj = (Object[]) serDe(fury, (Object) o);
    assertEquals(newObj, new Object[] {o.copy, o.age});
    assertTrue(
        fury.getClassResolver().getSerializer(CustomReplaceClass2.class)
            instanceof ReplaceResolveSerializer);
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testCopyReplaceCircularClass(Fury fury) {
    fury.registerSerializer(CustomReplaceClass2.class, ReplaceResolveSerializer.class);
    for (Object o :
        new Object[] {
          new CustomReplaceClass2(false, 2), new CustomReplaceClass2(true, 2),
        }) {
      fury.registerSerializer(o.getClass(), ReplaceResolveSerializer.class);
      copyCheck(fury, o);
    }
  }

  public static class CustomReplaceClass3 implements Serializable {
    public Object ref;

    private Object writeReplace() {
      // JDK serialization will update reference table, which change deserialized object
      //  graph, `ref` and `this` will be same.
      return ref;
    }

    private Object readResolve() {
      return ref;
    }
  }

  @Test
  public void testWriteReplaceSameClassCircularRef() {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    fury.registerSerializer(CustomReplaceClass3.class, ReplaceResolveSerializer.class);
    {
      CustomReplaceClass3 o1 = new CustomReplaceClass3();
      o1.ref = o1;
      CustomReplaceClass3 o2 = (CustomReplaceClass3) jdkDeserialize(jdkSerialize(o1));
      assertSame(o2.ref, o2);
      CustomReplaceClass3 o3 = (CustomReplaceClass3) serDe(fury, o1);
      assertSame(o3.ref, o3);
    }
    {
      CustomReplaceClass3 o1 = new CustomReplaceClass3();
      CustomReplaceClass3 o2 = new CustomReplaceClass3();
      o1.ref = o2;
      o2.ref = o1;
      {
        CustomReplaceClass3 newObj1 = (CustomReplaceClass3) jdkDeserialize(jdkSerialize(o1));
        // reference relationship updated by `CustomReplaceClass4.writeReplace`.
        assertSame(newObj1.ref, newObj1);
        assertSame(((CustomReplaceClass3) newObj1.ref).ref, newObj1);
      }
      {
        CustomReplaceClass3 newObj1 = (CustomReplaceClass3) serDe(fury, o1);
        // reference relationship updated by `CustomReplaceClass4.writeReplace`.
        assertSame(newObj1.ref, newObj1);
        assertSame(((CustomReplaceClass3) newObj1.ref).ref, newObj1);
      }
    }
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testWriteReplaceSameClassCircularRef(Fury fury) {
    fury.registerSerializer(CustomReplaceClass3.class, ReplaceResolveSerializer.class);
    {
      CustomReplaceClass3 o1 = new CustomReplaceClass3();
      o1.ref = o1;
      CustomReplaceClass3 copy = fury.copy(o1);
      assertNotSame(copy, o1);
      assertSame(copy, copy.ref);
    }
    {
      CustomReplaceClass3 o1 = new CustomReplaceClass3();
      CustomReplaceClass3 o2 = new CustomReplaceClass3();
      CustomReplaceClass3 o3 = new CustomReplaceClass3();
      o1.ref = o2;
      o2.ref = o3;
      o3.ref = o1;
      {
        CustomReplaceClass3 newObj1 = fury.copy(o1);
        assertNotSame(newObj1, o1);
        assertNotSame(newObj1.ref, o2);
        assertSame(newObj1, ((CustomReplaceClass3) ((CustomReplaceClass3) newObj1.ref).ref).ref);
      }
    }
  }

  public static class CustomReplaceClass4 implements Serializable {
    public Object ref;

    private Object writeReplace() {
      // return ref will incur infinite loop in java.io.ObjectOutputStream.writeObject0
      // for jdk serialization.
      return this;
    }

    private Object readResolve() {
      return ref;
    }
  }

  @Test
  public void testWriteReplaceDifferentClassCircularRef() {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    fury.registerSerializer(CustomReplaceClass3.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(CustomReplaceClass4.class, ReplaceResolveSerializer.class);
    CustomReplaceClass3 o1 = new CustomReplaceClass3();
    CustomReplaceClass4 o2 = new CustomReplaceClass4();
    o1.ref = o2;
    o2.ref = o1;
    {
      CustomReplaceClass4 newObj1 = (CustomReplaceClass4) jdkDeserialize(jdkSerialize(o1));
      assertSame(newObj1.ref, newObj1);
      assertSame(((CustomReplaceClass4) newObj1.ref).ref, newObj1);
    }
    {
      CustomReplaceClass4 newObj1 = (CustomReplaceClass4) serDe(fury, (Object) o1);
      assertSame(newObj1.ref, newObj1);
      assertSame(((CustomReplaceClass4) newObj1.ref).ref, newObj1);
    }
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testWriteReplaceDifferentClassCircularRef(Fury fury) {
    fury.registerSerializer(CustomReplaceClass3.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(CustomReplaceClass4.class, ReplaceResolveSerializer.class);
    {
      CustomReplaceClass3 o1 = new CustomReplaceClass3();
      CustomReplaceClass4 o2 = new CustomReplaceClass4();
      o1.ref = o2;
      o2.ref = o1;
      CustomReplaceClass3 copy = fury.copy(o1);
      assertNotSame(copy, o1);
      assertNotSame(copy.ref, o1.ref);
      assertSame(copy, ((CustomReplaceClass4) copy.ref).ref);
    }
    {
      CustomReplaceClass3 o1 = new CustomReplaceClass3();
      CustomReplaceClass4 o2 = new CustomReplaceClass4();
      CustomReplaceClass3 o3 = new CustomReplaceClass3();
      CustomReplaceClass4 o4 = new CustomReplaceClass4();
      o1.ref = o2;
      o2.ref = o3;
      o3.ref = o4;
      o4.ref = o1;
      CustomReplaceClass3 copy = fury.copy(o1);
      assertNotSame(copy, o1);
      assertNotSame(copy.ref, o1.ref);
      assertSame(copy, ((CustomReplaceClass4) ((CustomReplaceClass3) ((CustomReplaceClass4) copy.ref).ref).ref).ref);
    }
  }

  public static class Subclass1 extends CustomReplaceClass2 {
    int state;

    public Subclass1(boolean copy, int age, int state) {
      super(copy, age);
      this.state = state;
    }

    Object writeReplace() {
      if (age > 5) {
        return new Object[] {copy, age};
      } else {
        if (copy) {
          return new Subclass1(copy, age, state);
        } else {
          return this;
        }
      }
    }

    Object readResolve() {
      if (copy) {
        return new Subclass1(copy, age, state);
      }
      return this;
    }
  }

  @Test(dataProvider = "referenceTrackingConfig")
  public void testWriteReplaceSubClass(boolean referenceTracking) {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(referenceTracking)
            .build();
    fury.registerSerializer(CustomReplaceClass2.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(Subclass1.class, ReplaceResolveSerializer.class);
    for (Object o :
        new Object[] {
          new Subclass1(false, 2, 10), new Subclass1(true, 2, 11),
        }) {
      assertEquals(jdkDeserialize(jdkSerialize(o)), o);
      fury.registerSerializer(o.getClass(), ReplaceResolveSerializer.class);
      serDeCheck(fury, o);
    }
    Subclass1 o = new Subclass1(false, 6, 12);
    Object[] newObj = (Object[]) serDe(fury, (Object) o);
    assertEquals(newObj, new Object[] {o.copy, o.age});
    assertTrue(
        fury.getClassResolver().getSerializer(Subclass1.class) instanceof ReplaceResolveSerializer);
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testWriteReplaceSubClass(Fury fury) {
    fury.registerSerializer(CustomReplaceClass2.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(Subclass1.class, ReplaceResolveSerializer.class);
    for (Object o :
        new Object[] {
          new Subclass1(false, 2, 10), new Subclass1(true, 2, 11),
        }) {
      fury.registerSerializer(o.getClass(), ReplaceResolveSerializer.class);
      copyCheck(fury, o);
    }
  }

  public static class Subclass2 extends CustomReplaceClass2 {
    int state;

    public Subclass2(boolean copy, int age, int state) {
      super(copy, age);
      this.state = state;
    }

    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      s.writeInt(state);
    }

    private void readObject(java.io.ObjectInputStream s) throws Exception {
      s.defaultReadObject();
      this.state = s.readInt();
    }
  }

  @Test(dataProvider = "referenceTrackingConfig")
  public void testWriteReplaceWithWriteObject(boolean referenceTracking) {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(referenceTracking)
            .build();
    fury.registerSerializer(CustomReplaceClass2.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(Subclass2.class, ReplaceResolveSerializer.class);
    for (Object o :
        new Object[] {
          new Subclass2(false, 2, 10), new Subclass2(true, 2, 11),
        }) {
      assertEquals(jdkDeserialize(jdkSerialize(o)), o);
      fury.registerSerializer(o.getClass(), ReplaceResolveSerializer.class);
      serDeCheck(fury, o);
    }
    Subclass2 o = new Subclass2(false, 6, 12);
    assertEquals(jdkDeserialize(jdkSerialize(o)), new Object[] {o.copy, o.age});
    Object[] newObj = (Object[]) serDe(fury, (Object) o);
    assertEquals(newObj, new Object[] {o.copy, o.age});
    assertTrue(
        fury.getClassResolver().getSerializer(Subclass2.class) instanceof ReplaceResolveSerializer);
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testWriteReplaceWithWriteObject(Fury fury) {
    fury.registerSerializer(CustomReplaceClass2.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(Subclass2.class, ReplaceResolveSerializer.class);
    for (Object o :
        new Object[] {
            new Subclass2(false, 2, 10), new Subclass2(true, 2, 11),
        }) {
      copyCheck(fury, o);
    }
  }

  public static class CustomReplaceClass5 {
    private Object writeReplace() {
      throw new RuntimeException();
    }

    private Object readResolve() {
      throw new RuntimeException();
    }
  }

  public static class Subclass3 extends CustomReplaceClass5 implements Serializable {}

  @Test
  public void testUnInheritableReplaceMethod() {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    fury.registerSerializer(CustomReplaceClass5.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(Subclass3.class, ReplaceResolveSerializer.class);
    assertTrue(jdkDeserialize(jdkSerialize(new Subclass3())) instanceof Subclass3);
    assertTrue(serDe(fury, new Subclass3()) instanceof Subclass3);
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testUnInheritableReplaceMethod(Fury fury) {
    fury.registerSerializer(Subclass3.class, ReplaceResolveSerializer.class);
    Subclass3 subclass3 = new Subclass3();
    Subclass3 copy = fury.copy(new Subclass3());
    assertNotSame(subclass3, copy);
  }

  public static class CustomReplaceClass6 {
    Object writeReplace() {
      return 1;
    }
  }

  @Test
  public void testReplaceNotSerializable() {
    Fury fury =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    fury.registerSerializer(CustomReplaceClass6.class, ReplaceResolveSerializer.class);
    assertThrows(Exception.class, () -> jdkSerialize(new CustomReplaceClass6()));
    assertEquals(serDe(fury, new CustomReplaceClass6()), 1);
  }

  @Data
  @AllArgsConstructor
  public static class SimpleCollectionTest {
    public List<Integer> integerList;
    public ImmutableList<String> strings;
  }

  @Test
  public void testImmutableListResolve() {
    Fury fury1 =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    Fury fury2 =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    roundCheck(fury1, fury2, ImmutableList.of(1, 2));
    roundCheck(fury1, fury2, ImmutableList.of("a", "b"));
    roundCheck(
        fury1, fury2, new SimpleCollectionTest(ImmutableList.of(1, 2), ImmutableList.of("a", "b")));
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testImmutable(Fury fury) {
    fury.registerSerializer(ImmutableList.of(1, 2).getClass(), ReplaceResolveSerializer.class);
    fury.registerSerializer(SimpleCollectionTest.class, ReplaceResolveSerializer.class);
    fury.registerSerializer(ImmutableMap.of("1", 2).getClass(), ReplaceResolveSerializer.class);
    fury.registerSerializer(SimpleMapTest.class, ReplaceResolveSerializer.class);
    copyCheck(fury, ImmutableList.of(1, 2));
    copyCheck(fury, ImmutableList.of("a", "b"));
    copyCheck(fury, new SimpleCollectionTest(ImmutableList.of(1, 2), ImmutableList.of("a", "b")));
    copyCheck(fury, ImmutableMap.of("1", 2));
    copyCheck(fury, ImmutableMap.of(1, 2));
    copyCheck(fury, new SimpleMapTest(ImmutableMap.of("k", 2), ImmutableMap.of(1, 2)));
  }

  @Data
  @AllArgsConstructor
  public static class SimpleMapTest {
    public Map<String, Integer> map1;
    public ImmutableMap<Integer, Integer> map2;
  }

  @Test
  public void testImmutableMapResolve() {
    Fury fury1 =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    Fury fury2 =
        Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(false)
            .withRefTracking(true)
            .build();
    roundCheck(fury1, fury2, ImmutableMap.of("k", 2));
    roundCheck(fury1, fury2, ImmutableMap.of(1, 2));
    roundCheck(fury1, fury2, new SimpleMapTest(ImmutableMap.of("k", 2), ImmutableMap.of(1, 2)));
  }

  public static class InheritanceTestClass {
    private byte f1;

    public InheritanceTestClass(byte f1) {
      this.f1 = f1;
    }

    public Object writeReplace() {
      return new InheritanceTestClassProxy(f1);
    }
  }

  public static class InheritanceTestClassProxyBase implements Serializable {
    // Mark as transient to make object serializer unable to work, then only
    // `writeObject/readObject` can be used for serialization.
    transient byte[] data;

    private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.write(data.length);
      stream.write(data);
    }

    private void readObject(ObjectInputStream stream) throws IOException {
      int size = stream.read();
      data = new byte[size];
      int read = stream.read(data);
      Preconditions.checkArgument(read == size);
    }
  }

  public static class InheritanceTestClassProxy extends InheritanceTestClassProxyBase {
    public InheritanceTestClassProxy(byte f1) {
      data = new byte[] {f1};
    }

    public Object readResolve() {
      return new InheritanceTestClass(data[0]);
    }
  }

  @Test
  public void testInheritance() {
    Fury fury = Fury.builder().requireClassRegistration(false).build();
    byte[] bytes = fury.serialize(new InheritanceTestClass((byte) 10));
    InheritanceTestClass o = (InheritanceTestClass) fury.deserialize(bytes);
    assertEquals(o.f1, 10);
  }

  @Test(dataProvider = "furyCopyConfig")
  public void testInheritance(Fury fury) {
    fury.registerSerializer(InheritanceTestClass.class, ReplaceResolveSerializer.class);
    InheritanceTestClass o = fury.copy(new InheritanceTestClass((byte) 10));
    assertEquals(o.f1, 10);
  }

  static class WriteReplaceExternalizable implements Externalizable {
    private transient int f1;

    public WriteReplaceExternalizable(int f1) {
      this.f1 = f1;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      throw new RuntimeException();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      throw new RuntimeException();
    }

    private Object writeReplace() {
      return new ReplaceExternalizableProxy(f1);
    }
  }

  static class ReplaceExternalizableProxy implements Serializable {
    private int f1;

    public ReplaceExternalizableProxy(int f1) {
      this.f1 = f1;
    }

    private Object readResolve() {
      return new WriteReplaceExternalizable(f1);
    }
  }

  @Test
  public void testWriteReplaceExternalizable() {
    WriteReplaceExternalizable o =
        serDeCheckSerializer(
            getJavaFury(),
            new WriteReplaceExternalizable(10),
            ReplaceResolveSerializer.class.getName());
    assertEquals(o.f1, 10);
  }

  static class ReplaceSelfExternalizable implements Externalizable {
    private transient int f1;
    private transient boolean newInstance;

    public ReplaceSelfExternalizable(int f1, boolean newInstance) {
      this.f1 = f1;
      this.newInstance = newInstance;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      out.writeInt(f1);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      f1 = in.readInt();
    }

    private Object writeReplace() {
      return newInstance ? new ReplaceSelfExternalizable(f1, false) : this;
    }
  }

  @Test
  public void testWriteReplaceSelfExternalizable() {
    ReplaceSelfExternalizable o =
        serDeCheckSerializer(
            getJavaFury(),
            new ReplaceSelfExternalizable(10, false),
            ReplaceResolveSerializer.class.getName());
    assertEquals(o.f1, 10);
    ReplaceSelfExternalizable o1 =
        serDeCheckSerializer(
            getJavaFury(),
            new ReplaceSelfExternalizable(10, true),
            ReplaceResolveSerializer.class.getName());
    assertEquals(o1.f1, 10);
  }
}
