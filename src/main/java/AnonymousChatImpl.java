import Interfaces.AnonymousChat;
import Interfaces.MessageListener;
import beans.ChatRoom;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;

import java.util.HashSet;

public class AnonymousChatImpl implements AnonymousChat {

    final private Peer peer;
    final private PeerDHT peerDHT;
    final private int DEFAULT_MASTER_PORT = 4000;
    private HashSet<String> myChatRoomList;


    public AnonymousChatImpl(int _id, String _master_peer, final MessageListener _listener) throws Exception {
        myChatRoomList = new HashSet<>();

        peer = new PeerBuilder(Number160.createHash(_id)).ports(DEFAULT_MASTER_PORT + _id).start();
        peerDHT = new PeerBuilderDHT(peer).start();

        FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(DEFAULT_MASTER_PORT).start();
        fb.awaitUninterruptibly();
        if (fb.isSuccess()) {
            peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        } else {
            throw new Exception("Error in master peer bootstrap.");
        }

        peer.objectDataReply(new ObjectDataReply() {

            public Object reply(PeerAddress sender, Object request) throws Exception {
                return _listener.parseMessage(request);
            }
        });
    }
    //Metodo che permette di controllare se una
    public ChatRoom findRoom(String roomName){
        if (roomName!=null){
            FutureGet futureGet = peerDHT.get(Number160.createHash(roomName)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()){
                if (futureGet.isEmpty()){
                    return null;
                }
                //Ottengo la chatRoom
                try {
                    ChatRoom chatRoom = (ChatRoom) futureGet.dataMap().values().iterator().next().object();
                    return chatRoom;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public String createChatRoom(ChatRoom chatRoom){
        //Verifico se esiste una room con lo stesso nome nelle mie rooms
        if (!myChatRoomList.isEmpty()&& myChatRoomList.contains(chatRoom.getRoomName())){
            return "Room già presente all'interno della tua lista";
        }
        String roomName = chatRoom.getRoomName();
        ChatRoom alreadyExists = findRoom(roomName); //Verifico se è già presente una room con lo stesso nome
        if (alreadyExists==null){
            //Provo a creare la room
            boolean ris = createRoom(chatRoom.getRoomName()); //Chiamata al metodo createRoom dell'interfaccia AnonymousChat
            return ris?"Successo":"Fallimento";
        }
        else
            return "Chatroom già presente!";
    }

    @Override
    public boolean createRoom(String _room_name) {


            ChatRoom chatRoom = new ChatRoom(_room_name,new HashSet<>() ); //Creo  la nuova chat anonima
            chatRoom.getUsers().add(peerDHT.peer().peerAddress()); //Aggiungo il peerAddress
        try {
            peerDHT.put(Number160.createHash(_room_name)).data(new Data(chatRoom)).start().awaitUninterruptibly();
            myChatRoomList.add(_room_name);
            System.out.println("Ho creato la stanza correttamente");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean tryToJoinRoom(String _room_name){
        ChatRoom chatRoom = findRoom(_room_name);
        if (chatRoom!=null&&chatRoom.getUsers().contains(peer.peerAddress())) {
            return false;
        }
        return joinRoom(_room_name);
    }
    @Override
    public boolean joinRoom(String _room_name) {
        ChatRoom chatRoom = findRoom(_room_name);
        if (chatRoom!=null) {

            chatRoom.addUser(peerDHT.peer().peerAddress());
            try {
                peerDHT.put(Number160.createHash(_room_name)).data(new Data(chatRoom)).start().awaitUninterruptibly();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myChatRoomList.add(_room_name);
            return true;
        }
        return false;
    }

    @Override
    public boolean leaveRoom(String _room_name) {
        ChatRoom chatRoom = findRoom(_room_name);
        if (chatRoom!=null){
            chatRoom.removeUser(peerDHT.peer().peerAddress());
            try {
                peerDHT.put(Number160.createHash(_room_name)).data(new Data(chatRoom)).start().awaitUninterruptibly();
            } catch (IOException e) {
                e.printStackTrace();
            }
            myChatRoomList.remove(_room_name);
        }
        return false;
    }

    @Override
    public boolean sendMessage(String _room_name, String _text_message) {
        return false;
    }

    @Override
    public boolean leaveNetwork() {
        return false;
    }
}
