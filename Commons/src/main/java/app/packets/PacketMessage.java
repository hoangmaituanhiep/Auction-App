package app.packets;

import java.io.Serializable;

public class PacketMessage implements Serializable{
    private Message type;
    private Object payload;
    public PacketMessage(Message type, Object payload) {
        this.type = type;
        this.payload = payload;
    }
    public Message getType() {
        return type;
    }
    public void setType(Message type) {
        this.type = type;
    }
    public Object getPayload() {
        return payload;
    }
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    
}
