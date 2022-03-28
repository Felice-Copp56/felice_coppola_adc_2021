package Implementation;

import Interfaces.AnonymousChat;
import Interfaces.MessageListener;
import beans.ChatRoom;
import beans.Message;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;

import java.util.*;


public class AnonymousChatImpl implements AnonymousChat {

    final private Peer peer;
    final private PeerDHT peerDHT;
    final private int DEFAULT_MASTER_PORT = 4000;
    private HashSet<String> myChatRoomList = new HashSet<>();


    public AnonymousChatImpl(int _id, String _master_peer,  MessageListener _listener) throws Exception {


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

    //Metodo che permette di controllare se una stanza è presente
    public ChatRoom findRoom(String roomName) throws ClassNotFoundException {
        try {
            if (roomName != null) {
                FutureGet futureGet = peerDHT.get(Number160.createHash(roomName)).start();
                futureGet.awaitUninterruptibly();
                if (futureGet.isSuccess()) {
                    if (futureGet.isEmpty()) {

                        return null;
                    }
                    //Ottengo la chatRoom
                    ChatRoom chatRoom = (ChatRoom) futureGet.dataMap().values().iterator().next().object();
                    return chatRoom;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Errore nella find");
        return null;
    }

    //Metodo che permette la creazione di una stanza
    public String createChatRoom(ChatRoom chatRoom) {
        try {
            //Verifico se esiste una room con lo stesso nome nelle mie rooms
            if (!myChatRoomList.isEmpty() && myChatRoomList.contains(chatRoom.getRoomName())) {
                return "Esistente";
            }
            ChatRoom alreadyExists = findRoom(chatRoom.getRoomName()); //Verifico se è già presente una room con lo stesso nome
            if (alreadyExists == null) {
                System.out.println(chatRoom.getRoomName());
                //Provo a creare la room
                boolean ris = createRoom(chatRoom.getRoomName()); //Chiamata al metodo createRoom dell'interfaccia AnonymousChat
                return ris ? "Successo" : "Fallimento";
            } else
                return "Esistente";
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "Fallimento";
    }

    //Metodo che crea effettivamente la stanza, chiamato dal metodo precedente
    @Override
    public boolean createRoom(String _room_name) {

        try {
            ChatRoom chatRoom = new ChatRoom(_room_name, new HashSet<>()); //Creo  la nuova chat anonima
            chatRoom.getUsers().add(peerDHT.peer().peerAddress()); //Aggiungo il peerAddress

            peerDHT.put(Number160.createHash(_room_name)).data(new Data(chatRoom)).start().awaitUninterruptibly();
            myChatRoomList.add(_room_name);
            System.out.println("Ho creato la stanza correttamente");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    //Metodo che richiamata joinRoom se non è già joinato in una stanza
    public String tryToJoinRoom(String _room_name) {
        if (myChatRoomList != null && myChatRoomList.contains(_room_name)) {
            return "Joined";
        }
        return joinRoom(_room_name);
    }

    //Metodo che garantisce il join
    @Override
    public String joinRoom(String _room_name) {
        try {

            ChatRoom chatRoom = findRoom(_room_name);
            if (chatRoom != null) {
                if (!chatRoom.getUsers().contains(peer.peerAddress())) {
                    chatRoom.addUser(peerDHT.peer().peerAddress());
                    try {
                        peerDHT.put(Number160.createHash(_room_name)).data(new Data(chatRoom)).start().awaitUninterruptibly();
                        myChatRoomList.add(_room_name);
                        System.out.println("Joinato");
                        return "Successo";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Errore nell'iff");
                return "Fallimento";
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return "Fallimento";
    }

    //Metodo che permette di lasciare una stanza
    @Override
    public String leaveRoom(String _room_name) throws ClassNotFoundException, IOException {
        if (myChatRoomList.contains(_room_name)){  //Verifico se la room che voglio lasciare è una room alla quale sono sottoscritto
            ChatRoom chatRoom = findRoom(_room_name); //Ottengo la room
            if (chatRoom!=null) {
                chatRoom.removeUser(peerDHT.peer().peerAddress()); //Rimuovo l'utente dalla room
                peerDHT.put(Number160.createHash(_room_name)).data(new Data(chatRoom)).start().awaitUninterruptibly();
                myChatRoomList.remove(_room_name); //Rimuovo la room dalla lista dell'utente
                return "Leave";
            }
            else
                return "Not leave";
        }
        System.out.println("Non joinato");
        return "Not joined";
    }
    //Metodo che verifica se è possibile inviare un messaggio
    public String tryToSendMsg(String _room_name, Message msg) throws ClassNotFoundException {
        if (msg!=null&&msg.getRoomName()!=null){
            if (myChatRoomList.contains(msg.getRoomName())){
                String ris = sendMessage(_room_name,msg.getMessage());
                if (ris.equals("Successo")) return "Sent";
                else return "not sent";
            }
            return "Not in the room";
        }
        return "Error";
    }

    //Metodo che invia un messaggio in una stanza
    @Override
    public String sendMessage(String _room_name, String _text_message) throws ClassNotFoundException {
        if (myChatRoomList.contains(_room_name)) { //Verifico di essere nella room per inviare il messaggio
            ChatRoom chatRoom = findRoom(_room_name);
            if (chatRoom != null && chatRoom.getUsers() != null) {  //Verifico l'esistenza della chat e degli utenti
                Message msg = new Message();
                msg.setRoomName(_room_name);
                msg.setMessage(_text_message);
                Date date = new Date();
                msg.setData(date);
                msg.setMyMsg(true);
                for (PeerAddress peerAddress : chatRoom.getUsers()) {
                    //Mando il messaggio agli altri peer e non a me stesso
                    if (!peerAddress.equals(peerDHT.peer().peerAddress())) {
                        FutureDirect futureDirect = peerDHT.peer().sendDirect(peerAddress).object(msg).start();
                        futureDirect.awaitUninterruptibly();
                    }
                }
                return "Successo";
            }
            return "Fail";
        }
        return "Fail";
    }


    //Metodo per lasciare il network
    @Override
    public boolean leaveNetwork() throws IOException, ClassNotFoundException {
        for (String subscriptedrooms:new ArrayList<String>(myChatRoomList)){
            leaveRoom(subscriptedrooms);
        }
        peerDHT.peer().announceShutdown().start().awaitUninterruptibly();
        return true;
    }

    //Metodo per la distruzione di una stanza
    @Override
    public String destroyRoom(String _room_name) throws ClassNotFoundException, IOException {
        if (myChatRoomList.contains(_room_name)) { //Verifico di essere nella room che voglio distruggere
            ChatRoom chatRoom = findRoom(_room_name); //Ottengo la room
            //Verifico di essere nella room e di essere l'unico
            System.out.println("Numero utenti "+chatRoom.getUsers().size());
            System.out.println("Contains o no "+chatRoom.getUsers().contains(peerDHT.peer().peerAddress()));
            if (chatRoom!=null&&chatRoom.getUsers().size()==1&&chatRoom.getUsers().contains(peerDHT.peer().peerAddress())){
                System.out.println("Sono dentro nel destroyurooom numero utenti"+chatRoom.getUsers().size());
                    //Faccio uscire il peer dalla room e la distruggo
                    leaveRoom(_room_name);
                    peerDHT.remove(Number160.createHash(_room_name)).start().awaitUninterruptibly();
                    return "Destroyed";
            }return "Not Destroyed";
        }
        return "Not Found";
    }

    //Metodo che mostra gli utenti in una stanza
    @Override
    public String showUsers(String _room_name) throws ClassNotFoundException {
        if (myChatRoomList.contains(_room_name)){
            ChatRoom chatRoom = findRoom(_room_name);
            if (chatRoom!=null&&chatRoom.getUsers().contains(peerDHT.peer().peerAddress())){
                return "Founded";
            }
            return "Not found";
        }
        return "Not joined";
    }

    //Metodo che permette di ottenere le chatroom alle quali è connesso un peer
    public HashSet<String> getMyChatRoomList() {
        return myChatRoomList;
    }
}
