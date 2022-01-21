import Implementation.AnonymousChatImpl;
import Interfaces.MessageListener;
import beans.ChatRoom;
import beans.Message;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.*;

public class Tester {
    @Option(name = "-m", aliases = "--masterip", usage = "the master peer ip address", required = true)
    private static String master;

    @Option(name = "-id", aliases = "--identifierpeer", usage = "the unique identifier for this peer", required = true)
    private static int id;

    AnonymousChatImpl peer;
    TextIO textIO = TextIoFactory.getTextIO();
    TextTerminal terminal;
    boolean exit = false;
    boolean exitIn = true;
    HashMap<String, List<Message>> listHashMap = new HashMap<>();


    public Tester(String masterPeer, int peerID) {
        try {
            peer = new AnonymousChatImpl(peerID, masterPeer, new MessageListenerImpl(peerID));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String masterPeer = args[0];
        int peerID = Integer.parseInt(args[1]);
        System.out.println("master:" + masterPeer + "peerid" + peerID);
        Tester tester = new Tester(masterPeer, peerID);
        tester.launchAnonymous(peerID, masterPeer);

    }

    public void launchAnonymous(int peerID, String masterPeer) {
        try {
            terminal = textIO.getTextTerminal();
            textIO.getTextTerminal().getProperties().setInputColor("cyan");
            terminal.printf("\nStaring peer id: %d on master node: %s\n",
                    peerID, masterPeer);

            if (textIO.newStringInputReader().equals("WQ")) {
                exit = false;
                printMenu(terminal);
            }
            while (!exit) {
                printMenu(terminal);

                int option = textIO.newIntInputReader()
                        .withMaxVal(8)
                        .withMinVal(1)
                        .read("Option");

                switch (option) {
                    case 1 -> createNewRoomFirstOption(); /*inTheRoom(peerID,masterPeer);*/
                    case 2 -> joinRoomSecondOption();
                    case 3 -> leaveRoomThirdOption();
                    case 4 -> sendMessageToRoomFourthOption();
                    case 5 -> leaveNetworkFifthOption();
                    case 6 -> destroyRoomSixthOption();
                    case 7 -> showUsersSeventhOption();
                    case 8 -> showMsg();

                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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



    public void createNewRoomFirstOption() throws Exception {
        terminal.printf("\nENTER ROOM NAME\n");

        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");
        if (roomName != null && !roomName.isEmpty()) {
            ChatRoom chatRoom = new ChatRoom(roomName, new HashSet<>());
            String ris = peer.createChatRoom(chatRoom);

            switch (ris) {
                case "Successo" -> {
                    terminal.printf("\n ROOM CREATED SUCCESSFULLY\n");
                    listHashMap.put(roomName, new ArrayList<>());
                }
                case "Fallimento" -> terminal.printf("\nERROR IN ROOM CREATION\n");
                case "Esistente" -> terminal.printf("\nROOM ALREADY EXISTS\n");
            }
        }
    }


    public void joinRoomSecondOption() throws ClassNotFoundException {
        terminal.printf("\nENTER ROOM NAME\n");
        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");
        if (roomName != null && !roomName.isEmpty()) {
            System.out.println("Sono dentro ");
            String ris = peer.tryToJoinRoom(roomName);
            ChatRoom chatRoom = peer.findRoom(roomName); //Ottengo la room per fornire informazioni

            switch (ris) {
                case "Successo" -> {
                    terminal.printf("\n SUCCESSFULLY JOINED TO %s\n", roomName + "THERE ARE " + (chatRoom.getUsers().size() - 1) + " USERS WITH YOU!");
                    listHashMap.put(roomName, new ArrayList<>());
                }
                case "Fallimento" -> terminal.printf("\nERROR IN ROOM " + roomName + " SUBSCRIPTION, CHECK THE NAME\n");
                case "Joined" -> terminal.printf("\nYOU'RE ALREADY JOINED IN THE ROOM \n" + roomName);

            }
        }
    }

    public void leaveRoomThirdOption() throws ClassNotFoundException, IOException {
        terminal.printf("\nENTER ROOM NAME\n");
        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");
        if (roomName != null && !roomName.isEmpty()) {
            String ris = peer.leaveRoom(roomName);

            switch (ris) {
                case "Leave" -> terminal.printf("\nSUCCESSFULLY LEAVE FROM\n" + roomName);
                case "Not Leave" -> terminal.printf("\nERROR IN LEAVE FROM\n" + roomName);
                case "Not joined" -> terminal.printf("\nERROR IN LEAVE FROM " + roomName + " MAYBE YOU'RE NOT JOINED\n");
            }


        }
    }

    public void sendMessageToRoomFourthOption() throws ClassNotFoundException {
        terminal.printf("\nENTER ROOM NAME\n");
        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");
        boolean out = true;
        Message messaggio = new Message();
        while (out) {
            terminal.printf("\nENTER THE MESSAGE FOR THE USERS OR WQ FOR EXIT\n");
            String msg = textIO.newStringInputReader().withDefaultValue("default-message").read("Message: ");
            if (msg.equals("WQ")) {
                out = false;
            }

            if (roomName != null && !roomName.isEmpty()&&out) {
                messaggio.setMessage(msg);
                messaggio.setData(Calendar.getInstance().getTime());
                messaggio.setRoomName(roomName);
                String ris = peer.tryToSendMsg(roomName, messaggio);
                switch (ris) {
                    case "Sent" -> {
                        //Se non è presente la chiave roomName con la lista, allora creo un arraylist per la stanza cosi da poter inserire il messaggio
                        listHashMap.computeIfAbsent(roomName, k -> new ArrayList<>());
                        listHashMap.get(roomName).add(messaggio);

                        terminal.printf("\nMESSAGE " + msg + " SUCCESSFULLY SENT " + roomName + "\n");
                    }
                    case "not sent" -> terminal.printf("\nMESSAGE " + msg + " DIDN'T SEND TO THE ROOM " + roomName + "\n");
                    case "Not in the room" -> terminal.printf("\n MESSAGE "+msg+" DON'T SEND, YOU AREN'T IN THE ROOM \n");
                    case "Error" -> terminal.printf("\n SOMETHING WENT WRONG AMMO \n");
                }
            }
        }
    }

    public void leaveNetworkFifthOption() throws IOException, ClassNotFoundException {
        terminal.printf("\nARE YOU SURE? S/N \n");
        String answer = textIO.newStringInputReader().withDefaultValue("N").read("Answer:");
        if (answer.equals("S")) {
            peer.leaveNetwork();
            terminal.print("\nDISCONNECTED FROM THE NETWORK\n");
        } else {
            terminal.printf("\nYOU'RE ANSWER WAS NO, SO DO YOU WANNA LEAVE FROM SOME ROOM?\n");
            terminal.printf("\nENTER ROOM NAME\n");
            String roomName = textIO.newStringInputReader()
                    .withDefaultValue("default-room")
                    .read("Name:");
            if (roomName != null && !roomName.isEmpty()) {
                String ris = peer.leaveRoom(roomName);

                switch (ris) {
                    case "Leave" -> terminal.printf("\nSUCCESSFULLY LEAVE FROM\n" + roomName);
                    case "Not Leave" -> terminal.printf("\nERROR IN LEAVE FROM\n" + roomName);
                    case "Not joined" -> terminal.printf("\nERROR IN LEAVE FROM " + roomName + " MAYBE YOU'RE NOT JOINED\n");
                }
            }

        }
    }

    public void destroyRoomSixthOption() throws IOException, ClassNotFoundException {
        terminal.printf("\nENTER ROOM NAME THAT YOU WANT TO DESTROY\n");
        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");
        terminal.printf("\nARE YOU SURE? S/N \n");
        String answer = textIO.newStringInputReader().withDefaultValue("N").read("Answer:");
        if (answer.equals("S")) {
            if (roomName != null && !roomName.isEmpty()) {
                ChatRoom chatRoom = peer.findRoom(roomName);
                String ris = peer.destroyRoom(roomName);

                //terminal.print("\nROOM " + roomName + " DESTROYED\n");
                switch (ris) {
                    case "Destroyed" -> terminal.printf("\nSUCCESSFULLY DESTROYED THE ROOM \n" + roomName);
                    case "Not Destroyed" -> terminal.printf("\nERRROR IN ROOM \n" + roomName + " DESTROY YOU AREN'T ALONE, THERE ARE " + (chatRoom.getUsers().size() - 1) + " USERS WITH U");
                    case "Not Found" -> terminal.printf("\nERROR IN ROOM " + roomName + " DESTROY\n" + " MAYBE ROOM DOESN'T EXISTS");
                }
            }
        } else {
            terminal.printf("\nYOU'RE ANSWER WAS NO, SO DO YOU WANNA LEAVE THE ROOM?\n");
            if (roomName != null && !roomName.isEmpty()) {
                String ris = peer.leaveRoom(roomName);
                switch (ris) {
                    case "Leave" -> terminal.printf("\nSUCCESSFULLY LEAVE FROM\n" + roomName);
                    case "Not Leave" -> terminal.printf("\nERROR IN LEAVE FROM\n" + roomName);
                    case "Not joined" -> terminal.printf("\nERROR IN LEAVE FROM " + roomName + " MAYBE YOU'RE NOT JOINED\n");
                }
            }

        }
    }

    public void showUsersSeventhOption() throws ClassNotFoundException {
        terminal.printf("\nENTER ROOM NAME THAT YOU WANT TO SEE THE USERS\n");
        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");


        if (roomName != null && !roomName.isEmpty()) {
            ChatRoom chatRoom = peer.findRoom(roomName);
            String ris = peer.showUsers(roomName);

            //terminal.print("\nROOM " + roomName + " DESTROYED\n");
            switch (ris) {
                case "Founded" -> terminal.printf("\n THERE ARE  \n" + (chatRoom.getUsers().size()) + "USERS  IN " + roomName + " ROOM\n");
                case "Not found" -> terminal.printf("\nERROR IN ROOM \n" + roomName + " FIND, CHECK THE NAME ");
                case "Not joined" -> terminal.printf("\nYOU AREN'T IN THE " + roomName + " ROOM \n" + " JOIN ROOM FIRST");
            }
        }
    }

    public void showMsg() {

        String roomName = textIO.newStringInputReader().read("\n INSERT NAME ");
        if (peer.getMyChatRoomList().contains(roomName)) {
            List<Message> messageList = listHashMap.get(roomName);
            System.out.println("Messaggio size " + messageList.size());
            if (messageList.size()==0){
                terminal.printf("\n NO NEW MESSAGE IN THE CHAT, TRY LATER\n");
            }
            for (Message message : messageList) {
                terminal.printf("\n  " + message.getMessage() + " Alle ore: " + message.getData() + "\n");
            }
        }
    }

    public static void printMenu(TextTerminal terminal) {
        terminal.printf("\n1 - CREATE ROOM\n");
        terminal.printf("\n2 - JOIN ROOM\n");
        terminal.printf("\n3 - LEAVE ROOM\n");
        terminal.printf("\n4 - SEND MESSAGE TO ROOM\n");
        terminal.printf("\n5 - LEAVE NETWORK\n");
        terminal.printf("\n6 - DESTROY ROOM\n");
        terminal.printf("\n7 - SHOW USERS ROOM\n");
        terminal.printf("\n8 - SHOW MESSAGES ROOM\n");

    }
}
