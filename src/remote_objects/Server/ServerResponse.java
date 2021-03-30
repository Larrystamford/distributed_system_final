package remote_objects.Server;

import remote_objects.Common.Marshal;
import remote_objects.Common.Booking;

import java.util.List;

public class ServerResponse extends Marshal implements Cloneable {
    int requestId;
    int status;
    int type;
    List<Booking> infos;

    public ServerResponse() {
        super();
    }

    public ServerResponse(int requestId, int status, List<Booking> infos) {
        super();
        this.requestId = requestId;
        this.status = status;
        this.infos = infos;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getStatus() {
        return status;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public List<Booking> getInfos() {
        return infos;
    }

    public ServerResponse clone() {
        try {
            return (ServerResponse) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
