package beans;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private String message;
    private String roomName;
    private Date data;
    private boolean myMsg;

    public Message(String messaggio, String roomName,Date data,boolean myMsg)
    {
        this.message=messaggio;
        this.roomName=roomName;
        this.data=data;
        this.myMsg=myMsg;
    }

    public Message() {

    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean isMyMsg() {
        return myMsg;
    }

    public void setMyMsg(boolean myMsg) {
        this.myMsg = myMsg;
    }
}
