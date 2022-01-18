import Interfaces.MessageListener;
import beans.Message;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MessageListenerImpl implements MessageListener {

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
        if (message != null && message.getRoomName() != null) {
            if (listMsg.get(message.getRoomName()) != null) {
                listMsg.get(message.getRoomName()).add(message);//Aggiungo il messaggio


                terminal.printf("\n" + peerid + "] (Direct Message Received) Message received in room: " + message.getRoomName() + "ALLE ORE" + message.getData().toString() + "\n\n");
                return "success";
            }
        }
        return "not success";
    }

    public HashMap<String, List<Message>> getListMsg(String roomm) {
        return listMsg;
    }
}
