import Implementation.AnonymousChatImpl;
import beans.ChatRoom;
import beans.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;

public class AnonymousChatTesting {

    private static AnonymousChatImpl peer0, peer1, peer2, peer3;
    HashMap<String, List<Message>> listHashMap = new HashMap<>();

    /*public AnonymousChatTesting() throws Exception {
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
                    System.out.println("Sono dentro al null");
                    //Se la lista dei messaggi della room non è null, allora posso ottenere la lista e aggiungere un nuovo messaggio
                    if (listHashMap.get(message.getRoomName()) != null) {
                        System.out.println("Sono dentro al secondo null");
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


        peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListenerImpl(0));
        peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListenerImpl(1));
        peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListenerImpl(2));
        peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListenerImpl(3));


    }*/
    @BeforeAll
    static void inizializzaione() throws Exception {
        peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListenerImpl(0));
        peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListenerImpl(1));
        peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListenerImpl(2));
        peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListenerImpl(3));
    }


}



