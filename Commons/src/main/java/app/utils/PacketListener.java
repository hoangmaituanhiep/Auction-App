package app.utils;

import app.packets.PacketMessage;

public interface PacketListener {
  void onReceivingPacket(PacketMessage packet);
}
