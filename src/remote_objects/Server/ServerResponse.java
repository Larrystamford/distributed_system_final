package remote_objects.Server;

import remote_objects.Common.Marshal;
import remote_objects.Common.FacilityBooking;

import java.util.List;

public class ServerResponse extends Marshal implements Cloneable {
    int queryId;
    int status;
    int type;
    List<FacilityBooking> infos;

    public ServerResponse() {
        super();
    }

    public ServerResponse(int queryId, int status, List<FacilityBooking> infos) {
        super();
        this.queryId = queryId;
        this.status = status;
        this.infos = infos;
    }

    public int getQueryId() {
        return queryId;
    }

    public void setQueryId(int queryId) {
        this.queryId = queryId;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public List<FacilityBooking> getInfos() {
        return infos;
    }

    public void setInfos(List<FacilityBooking> infos) {
        this.infos = infos;
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
