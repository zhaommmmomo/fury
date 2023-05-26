// automatically generated by the FlatBuffers compiler, do not modify

package io.fury.integration_tests.state.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class FBSImage extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static FBSImage getRootAsFBSImage(ByteBuffer _bb) { return getRootAsFBSImage(_bb, new FBSImage()); }
  public static FBSImage getRootAsFBSImage(ByteBuffer _bb, FBSImage obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public FBSImage __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String uri() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer uriAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer uriInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public String title() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer titleAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public ByteBuffer titleInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 6, 1); }
  public int width() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int height() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public byte size() { int o = __offset(12); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public io.fury.integration_tests.state.generated.FBSMedia media() { return media(new io.fury.integration_tests.state.generated.FBSMedia()); }
  public io.fury.integration_tests.state.generated.FBSMedia media(io.fury.integration_tests.state.generated.FBSMedia obj) { int o = __offset(14); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }

  public static int createFBSImage(FlatBufferBuilder builder,
      int uriOffset,
      int titleOffset,
      int width,
      int height,
      byte size,
      int mediaOffset) {
    builder.startTable(6);
    FBSImage.addMedia(builder, mediaOffset);
    FBSImage.addHeight(builder, height);
    FBSImage.addWidth(builder, width);
    FBSImage.addTitle(builder, titleOffset);
    FBSImage.addUri(builder, uriOffset);
    FBSImage.addSize(builder, size);
    return FBSImage.endFBSImage(builder);
  }

  public static void startFBSImage(FlatBufferBuilder builder) { builder.startTable(6); }
  public static void addUri(FlatBufferBuilder builder, int uriOffset) { builder.addOffset(0, uriOffset, 0); }
  public static void addTitle(FlatBufferBuilder builder, int titleOffset) { builder.addOffset(1, titleOffset, 0); }
  public static void addWidth(FlatBufferBuilder builder, int width) { builder.addInt(2, width, 0); }
  public static void addHeight(FlatBufferBuilder builder, int height) { builder.addInt(3, height, 0); }
  public static void addSize(FlatBufferBuilder builder, byte size) { builder.addByte(4, size, 0); }
  public static void addMedia(FlatBufferBuilder builder, int mediaOffset) { builder.addOffset(5, mediaOffset, 0); }
  public static int endFBSImage(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public FBSImage get(int j) { return get(new FBSImage(), j); }
    public FBSImage get(FBSImage obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

