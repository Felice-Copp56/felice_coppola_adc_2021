import Implementation.AnonymousChatImpl;
import Interfaces.MessageListener;
import beans.ChatRoom;
import beans.Message;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnonymousChatTesting {

    private static AnonymousChatImpl peer0, peer1, peer2, peer3;
    HashMap<String, List<Message>> listHashMap = new HashMap<>();

    public AnonymousChatTesting() throws Exception {
        class MessageListenerImpl implements MessageListener {

            int peerid;
            Message message = null;
            HashMap<String, List<Message>> listMsg = new HashMap<>();

            public MessageListenerImpl(int peerid) {
                this.peerid = peerid;

            }

            public Object parseMessage(Object obj) {
                this.message = (Message) obj;
                TextIO textIO = TextIoFactory.getTextIO();
                TextTerminal terminal = textIO.getTextTerminal();
                //Verifico se il messaggio è diverso da null ed ha una roomName valido
                if (message != null && message.getRoomName() != null) {

                    //Se la lista dei messaggi della room non è null, allora posso ottenere la lista e aggiungere un nuovo messaggio
                    if (listHashMap.get(message.getRoomName()) != null) {

                        listHashMap.get(message.getRoomName()).add(message);//Aggiungo il messaggio
                        //terminal.printf("\n" + peerid + "] (Direct Message Received) Message received in room: " + message.getRoomName() + "ALLE ORE" + message.getData().toString() + "\n\n");
                    }
                }
                if (message != null) return message;
                return "not success";
            }

            public HashMap<String, List<Message>> getListMsg(String roomm) {
                return listMsg;
            }
        }


    }

    @BeforeAll
    static void inizializzaione() throws Exception {
        peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListenerImpl(0));
        peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListenerImpl(1));
        peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListenerImpl(2));
        peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListenerImpl(3));
    }

    //Test case per creazione della chatroom
    @Test
    void testCaseCreateChatRoom() {
        //Il peer0 cerca di creare la stanza e riesce in quanto non esiste una stanza con tale nome
        String ris = peer0.createChatRoom(new ChatRoom("1.1CreateRoom", new HashSet<>()));
        assertEquals("Successo", ris);
    }

    /*Test case per la creazione di una chatRoom già creata
        1. Un peer cerca di creare ma trova un messaggio di room già esistente
        2. Un peer cerca di creare una room ma riceve un messaggio di room alla quale è già joinato
    * */
    @Test
    void testCaseCreateRoomAlreadyJoinedorAlreadyCreated() {
        //Creo la stanza
        String ris1 = peer0.createChatRoom(new ChatRoom("1.2CreateRoom_AlreadyCreated", new HashSet<>()));
        assertEquals("Successo", ris1);
        //Provo a crearne un altra con lo stesso nome
        String ris2 = peer1.createChatRoom(new ChatRoom("1.2CreateRoom_AlreadyCreated", new HashSet<>()));
        //Riceve un messaggio di esistente
        assertEquals("Esistente", ris2);
        //Provo a creare una stanza dove sono già joinato
        String ris3 = peer0.createChatRoom(new ChatRoom("1.2CreateRoom_AlreadyCreated", new HashSet<>()));
        assertEquals("Esistente", ris3);

    }

    /*
    I peer provano ad effettuare il join ad una stanza
    *
    * */
    @Test
    void testCaseJoinRoom() {
        String ris1 = peer0.createChatRoom(new ChatRoom("2.1JoinRoom", new HashSet<>()));
        assertEquals("Successo", ris1);

        String ris2 = peer1.tryToJoinRoom("2.1JoinRoom");
        assertEquals("Successo", ris2);

        String ris3 = peer2.tryToJoinRoom("2.1JoinRoom");
        assertEquals("Successo", ris3);

        String ris4 = peer3.tryToJoinRoom("2.1JoinRoom");
        assertEquals("Successo", ris4);

    }

    /*
    I peer provano a collegarsi ad una stanza alla quale sono già joinati ricevendo
    un errore
    * */
    @Test
    void testCaseJoinRoomAlreadyJoined() {

        //Peer0 crea la stanza
        String ris1 = peer0.createChatRoom(new ChatRoom("2.2JoinRoomAlreadyJoined", new HashSet<>()));
        assertEquals("Successo", ris1);

        //Peer1 effettua il join
        String ris2 = peer1.tryToJoinRoom("2.2JoinRoomAlreadyJoined");
        assertEquals("Successo", ris2);

        //Peer2 effettua il join
        String ris3 = peer2.tryToJoinRoom("2.2JoinRoomAlreadyJoined");
        assertEquals("Successo", ris3);

        //Peer 2 prova a rieffettuarlo anche se è già joinato
        String ris4 = peer2.tryToJoinRoom("2.2JoinRoomAlreadyJoined");
        assertEquals("Joined", ris4);

    }

    /*
    Il peer 1 cerca di joinare in una stanza non esistente ricevendo un errore
    * */
    @Test
    void testCaseJoinRoomNotExistent() {
        String ris0 = peer1.tryToJoinRoom("2.3JoinRoomNotExistent");
        assertEquals("Fallimento", ris0);
    }

    /*
    I peer provano a lasciare una stanza
    * */
    @Test
    void testCaseLeaveRoom() throws IOException, ClassNotFoundException {

        //Peer0 effettua la creazione
        String ris1 = peer0.createChatRoom(new ChatRoom("3.1LeaveRoom", new HashSet<>()));
        assertEquals("Successo", ris1);

        //Peer1 effettua il join
        String ris2 = peer1.tryToJoinRoom("3.1LeaveRoom");
        assertEquals("Successo", ris2);

        //Peer0 cerca di uscire
        String ris3 = peer0.leaveRoom("3.1LeaveRoom");
        assertEquals("Leave", ris3);

        //Peer 1 cerca di uscire
        String ris4 = peer1.leaveRoom("3.1LeaveRoom");
        assertEquals("Leave", ris4);
    }

    /*
    I peer cercando di lasciare una stanza alla quale non sono joinati
    * */
    @Test
    void testCaseLeaveRoomNotJoined() throws IOException, ClassNotFoundException {

        //Peer0 effettua la creazione
        String ris1 = peer0.createChatRoom(new ChatRoom("3.2LeaveRoomNotJoined", new HashSet<>()));
        assertEquals("Successo", ris1);

        //Peer1 effettua la leave nonostante non sia nella stanza
        String ris2 = peer1.leaveRoom("3.2LeaveRoomNotJoined");
        assertEquals("Not joined", ris2);
    }

    /*
    Il peer 0 cerca di lasciare una stanza che non è stata creata e alla quale ovviamente
    non è joinato
    * */
    @Test
    void testCaseLeaveRoomNotCreated() throws IOException, ClassNotFoundException {

        //Peer0 effettua la leave di una stanza non creata
        String ris1 = peer0.leaveRoom("3.3LeaveRoomNotCreated");
        assertEquals("Not joined", ris1);

    }

    /*
    I peer cercando di inviare messaggi in una stanza
    * */
    @Test
    void testCaseSendMsg() throws ClassNotFoundException {

        //Peer0 effettua la creazione
        String ris1 = peer0.createChatRoom(new ChatRoom("4.1SendMsg", new HashSet<>()));
        assertEquals("Successo", ris1);

        //Peer1 effettua il join
        String ris2 = peer1.tryToJoinRoom("4.1SendMsg");
        assertEquals("Successo", ris2);

        //Peer2 effettua il join
        String ris3 = peer2.tryToJoinRoom("4.1SendMsg");
        assertEquals("Successo", ris3);

        //Peer3 effettua il join
        String ris4 = peer3.tryToJoinRoom("4.1SendMsg");
        assertEquals("Successo", ris4);

        //Viene inviato correttamente il messaggio
        Message msg = new Message("Default message","4.1SendMsg", Calendar.getInstance().getTime(), true);
        String risSend=peer1.tryToSendMsg("4.1SendMsg",msg);
        assertEquals("Sent",risSend);
    }

    /*
    Il peer1 cerca di inviare un messaggio in una stanza alla quale non è collegato
    * */
    @Test
    void testCaseSendMsgRoomNotJoined() throws ClassNotFoundException {
        String ris1=peer0.createChatRoom(new ChatRoom("4.2SendMsgNotJoined",new HashSet<>()));
        assertEquals("Successo",ris1);

        Message msg = new Message("Default message","4.2SendMsgNotJoined", Calendar.getInstance().getTime(), true);
        //Aspettiamo not in the room perché peer1 non è presente nella stanza
        String risSend=peer1.tryToSendMsg("4.2SendMsgNotJoined",msg);
        assertEquals("Not in the room",risSend);
    }

    /*Il peer0 distrugge una stanza
    * */
    @Test
    void testCaseDestroyRoom() throws IOException, ClassNotFoundException {
        String ris1=peer0.createChatRoom(new ChatRoom("5.1DestroyRoom",new HashSet<>()));
        assertEquals("Successo",ris1);

        String ris2=peer0.destroyRoom("5.1DestroyRoom");
        assertEquals("Destroyed",ris2);
    }

    /*Il peer0 cerca di distruggere una stanza con un altro utente connesso
    ricevendo un errore
    * */
    @Test
    void testCaseDestroyRoomWithMoreThan1Users() throws IOException, ClassNotFoundException {
        String ris1=peer0.createChatRoom(new ChatRoom("5.2DestroyRoomWithUsers",new HashSet<>()));
        assertEquals("Successo",ris1);

        String ris2=peer1.tryToJoinRoom("5.2DestroyRoomWithUsers");
        assertEquals("Successo",ris2);

        String ris3=peer0.destroyRoom("5.2DestroyRoomWithUsers");
        assertEquals("Not Destroyed",ris3);
    }

    /*Il peer0 cerca di distruggere una stanza non creata o alla quale non è joinato
    ricevendo errore
    * */
    @Test
    void testCaseDestroyRoomNotJoined() throws IOException, ClassNotFoundException {

        String ris3=peer0.destroyRoom("5.2DestroyRoomNotJoined");
        assertEquals("Not Found",ris3);
    }

    /*I peer verificano il numero di utenti, ricevendo un esito positivo
    * */
    @Test
    void testCaseShowUsers() throws ClassNotFoundException {
        String ris1=peer0.createChatRoom(new ChatRoom("6.1ShowUsers",new HashSet<>()));
        assertEquals("Successo",ris1);

        String ris2=peer1.tryToJoinRoom("6.1ShowUsers");
        assertEquals("Successo",ris2);

        int risShow=peer0.showUsers("6.1ShowUsers");
        assertEquals(2,risShow);


    }

    /*Il peer0 cerca di vedere gli utenti in una stanza alla quale non è joinato
    * */
    @Test
    void testCaseShowUsersRoomNotJoined() throws ClassNotFoundException {

        int risShow=peer0.showUsers("6.2ShowUsersRoomNotFound");
        assertEquals(-2,risShow);
    }

    /*
    I peer0 e peer1 cercano di verificare il numero di utenti anche dopo essere usciti, ricevendo
    un errore
    * */
    @Test
    void testCaseShowUsersAfterExit() throws ClassNotFoundException, IOException {
        String ris1 = peer0.createChatRoom(new ChatRoom("6.3ShowUsersAfterExit", new HashSet<>()));
        assertEquals("Successo", ris1);

        int risShow = peer0.showUsers("6.3ShowUsersAfterExit");
        assertEquals(1, risShow);

        String ris2 = peer1.tryToJoinRoom("6.3ShowUsersAfterExit");
        assertEquals("Successo", ris2);

        int ris2Show=peer1.showUsers("6.3ShowUsersAfterExit");
        assertEquals(2,ris2Show);

        String ris3 = peer1.leaveRoom("6.3ShowUsersAfterExit");
        assertEquals("Leave", ris3);

        int ris3Show=peer1.showUsers("6.3ShowUsersAfterExit");
        assertEquals(-2,ris3Show);

        String ris4 = peer0.leaveRoom("6.3ShowUsersAfterExit");
        assertEquals("Leave", ris4);

        int ris4Show=peer0.showUsers("6.3ShowUsersAfterExit");
        assertEquals(-2,ris4Show);


    }



    @AfterAll
    static void leaveNetwork() throws IOException, ClassNotFoundException {
        assertTrue(peer0.leaveNetwork());
        assertTrue(peer1.leaveNetwork());
        assertTrue(peer2.leaveNetwork());
        assertTrue(peer3.leaveNetwork());
    }


}



