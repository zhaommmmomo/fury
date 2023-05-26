// automatically generated by the FlatBuffers compiler, do not modify

package io.fury.integration_tests.state.generated;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class FBSMedia extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_2_0_0(); }
  public static FBSMedia getRootAsFBSMedia(ByteBuffer _bb) { return getRootAsFBSMedia(_bb, new FBSMedia()); }
  public static FBSMedia getRootAsFBSMedia(ByteBuffer _bb, FBSMedia obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public FBSMedia __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String uri() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer uriAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer uriInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public String title() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer titleAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public ByteBuffer titleInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 6, 1); }
  public int width() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int height() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public String format() { int o = __offset(12); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer formatAsByteBuffer() { return __vector_as_bytebuffer(12, 1); }
  public ByteBuffer formatInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 12, 1); }
  public long duration() { int o = __offset(14); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public long size() { int o = __offset(16); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public int bitrate() { int o = __offset(18); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean hasBitrate() { int o = __offset(20); return o != 0 ? 0!=bb.get(o + bb_pos) : false; }
  public String persons(int j) { int o = __offset(22); return o != 0 ? __string(__vector(o) + j * 4) : null; }
  public int personsLength() { int o = __offset(22); return o != 0 ? __vector_len(o) : 0; }
  public StringVector personsVector() { return personsVector(new StringVector()); }
  public StringVector personsVector(StringVector obj) { int o = __offset(22); return o != 0 ? obj.__assign(__vector(o), 4, bb) : null; }
  public byte player() { int o = __offset(24); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public String copyright() { int o = __offset(26); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer copyrightAsByteBuffer() { return __vector_as_bytebuffer(26, 1); }
  public ByteBuffer copyrightInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 26, 1); }

  public static int createFBSMedia(FlatBufferBuilder builder,
      int uriOffset,
      int titleOffset,
      int width,
      int height,
      int formatOffset,
      long duration,
      long size,
      int bitrate,
      boolean has_bitrate,
      int personsOffset,
      byte player,
      int copyrightOffset) {
    builder.startTable(12);
    FBSMedia.addSize(builder, size);
    FBSMedia.addDuration(builder, duration);
    FBSMedia.addCopyright(builder, copyrightOffset);
    FBSMedia.addPersons(builder, personsOffset);
    FBSMedia.addBitrate(builder, bitrate);
    FBSMedia.addFormat(builder, formatOffset);
    FBSMedia.addHeight(builder, height);
    FBSMedia.addWidth(builder, width);
    FBSMedia.addTitle(builder, titleOffset);
    FBSMedia.addUri(builder, uriOffset);
    FBSMedia.addPlayer(builder, player);
    FBSMedia.addHasBitrate(builder, has_bitrate);
    return FBSMedia.endFBSMedia(builder);
  }

  public static void startFBSMedia(FlatBufferBuilder builder) { builder.startTable(12); }
  public static void addUri(FlatBufferBuilder builder, int uriOffset) { builder.addOffset(0, uriOffset, 0); }
  public static void addTitle(FlatBufferBuilder builder, int titleOffset) { builder.addOffset(1, titleOffset, 0); }
  public static void addWidth(FlatBufferBuilder builder, int width) { builder.addInt(2, width, 0); }
  public static void addHeight(FlatBufferBuilder builder, int height) { builder.addInt(3, height, 0); }
  public static void addFormat(FlatBufferBuilder builder, int formatOffset) { builder.addOffset(4, formatOffset, 0); }
  public static void addDuration(FlatBufferBuilder builder, long duration) { builder.addLong(5, duration, 0L); }
  public static void addSize(FlatBufferBuilder builder, long size) { builder.addLong(6, size, 0L); }
  public static void addBitrate(FlatBufferBuilder builder, int bitrate) { builder.addInt(7, bitrate, 0); }
  public static void addHasBitrate(FlatBufferBuilder builder, boolean hasBitrate) { builder.addBoolean(8, hasBitrate, false); }
  public static void addPersons(FlatBufferBuilder builder, int personsOffset) { builder.addOffset(9, personsOffset, 0); }
  public static int createPersonsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startPersonsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addPlayer(FlatBufferBuilder builder, byte player) { builder.addByte(10, player, 0); }
  public static void addCopyright(FlatBufferBuilder builder, int copyrightOffset) { builder.addOffset(11, copyrightOffset, 0); }
  public static int endFBSMedia(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public FBSMedia get(int j) { return get(new FBSMedia(), j); }
    public FBSMedia get(FBSMedia obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

