package beans;

import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.PeerAddress;

import java.io.Serializable;
import java.util.HashSet;

public class ChatRoom implements Serializable {
    private String roomName;
    private HashSet<PeerAddress> users;

    public ChatRoom(){}

    public ChatRoom(String roomName,HashSet<PeerAddress> users){
        this.roomName=roomName;
        this.users=users;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public HashSet<PeerAddress> getUsers() {
        return users;
    }

    public void setUsers(HashSet<PeerAddress> users) {
        this.users = users;
    }
    public void removeUser(PeerAddress peerAddress){
        if(this.users!=null) this.users.remove(peerAddress);
    }
    public void addUser(PeerAddress peerAddress){
        if(this.users==null)
            this.users=new HashSet<>();
        this.users.add(peerAddress);
    }
}
