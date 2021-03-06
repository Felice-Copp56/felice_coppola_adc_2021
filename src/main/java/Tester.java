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
                    case 1:
                        createNewRoomFirstOption();
                        break;/*inTheRoom(peerID,masterPeer);*/
                    case 2:
                        joinRoomSecondOption();
                        break;
                    case 3:
                        leaveRoomThirdOption();
                        break;
                    case 4:
                        sendMessageToRoomFourthOption();
                        break;
                    case 5:
                        leaveNetworkFifthOption();
                        break;
                    case 6:
                        destroyRoomSixthOption();
                        break;
                    case 7:
                        showUsersSeventhOption();
                        break;
                    case 8:
                        showMsg();
                        break;

                    default:
                        break;


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
            //Verifico se il messaggio ?? diverso da null ed ha una roomName valido
            if (message != null && message.getRoomName() != null && listHashMap.get(message.getRoomName()) != null) {

                //Se la lista dei messaggi della room non ?? null, allora posso ottenere la lista e aggiungere un nuovo messaggio

                listHashMap.get(message.getRoomName()).add(message);//Aggiungo il messaggio
                //terminal.printf("\n" + peerid + "] (Direct Message Received) Message received in room: " + message.getRoomName() + "ALLE ORE" + message.getData().toString() + "\n\n");

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
                case "Successo":
                    terminal.printf("\n ROOM CREATED SUCCESSFULLY\n");
                    listHashMap.put(roomName, new ArrayList<>());
                    break;
                case "Fallimento":
                    terminal.printf("\nERROR IN ROOM CREATION\n");
                    break;
                case "Esistente":
                    terminal.printf("\nROOM ALREADY EXISTS\n");
                    break;
            }
        }
    }


    public void joinRoomSecondOption() throws ClassNotFoundException {
        terminal.printf("\nENTER ROOM NAME\n");
        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");
        if (roomName != null && !roomName.isEmpty()) {

            String ris = peer.tryToJoinRoom(roomName);
            ChatRoom chatRoom = peer.findRoom(roomName); //Ottengo la room per fornire informazioni

            switch (ris) {
                case "Successo":
                    terminal.printf("\n SUCCESSFULLY JOINED TO %s\n", roomName + "THERE ARE " + (chatRoom.getUsers().size() - 1) + " USERS WITH YOU!");
                    listHashMap.put(roomName, new ArrayList<>());

                    break;
                case "Fallimento":
                    terminal.printf("\nERROR IN ROOM " + roomName + " SUBSCRIPTION, CHECK THE NAME\n");
                    break;
                case "Joined":
                    terminal.printf("\nYOU'RE ALREADY JOINED IN THE ROOM \n" + roomName);
                    break;

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
                case "Leave":
                    terminal.printf("\nSUCCESSFULLY LEAVE FROM\n" + roomName);
                    break;
                case "Not Leave":
                    terminal.printf("\nERROR IN LEAVE FROM\n" + roomName);
                    break;
                case "Not joined":
                    terminal.printf("\nERROR IN LEAVE FROM " + roomName + " MAYBE YOU'RE NOT JOINED\n");
                    break;
            }


        }
    }

    public void sendMessageToRoomFourthOption() throws ClassNotFoundException {
        terminal.printf("\nENTER ROOM NAME\n");
        String roomName = textIO.newStringInputReader()
                .withDefaultValue("default-room")
                .read("Name:");

        if (!peer.getMyChatRoomList().contains(roomName)) {

            terminal.printf("\nYOU'RE NOT IN THE ROOM" + " :" + roomName + "\n");
        } else {
            boolean out = true;
            Message messaggio = new Message();
            while (out) {
                terminal.printf("\nENTER THE MESSAGE FOR THE USERS OR WQ FOR EXIT\n");
                String msg = textIO.newStringInputReader().withDefaultValue("default-message").read("Message: ");
                if (msg.equals("WQ")) {
                    out = false;
                }

                if (roomName != null && !roomName.isEmpty() && out) {
                    messaggio.setMessage(msg);
                    messaggio.setData(Calendar.getInstance().getTime());
                    messaggio.setRoomName(roomName);
                    String ris = peer.tryToSendMsg(roomName, messaggio);
                    switch (ris) {
                        case "Sent":
                            //Se non ?? presente la chiave roomName con la lista, allora creo un arraylist per la stanza cosi da poter inserire il messaggio
                            listHashMap.computeIfAbsent(roomName, k -> new ArrayList<>());
                            listHashMap.get(roomName).add(messaggio);

                            terminal.printf("\nMESSAGE " + msg + " SUCCESSFULLY SENT " + roomName + "\n");
                            break;
                        case "not sent":
                            terminal.printf("\nMESSAGE " + msg + " DIDN'T SEND TO THE ROOM " + roomName + "\n");
                            break;
                        case "Not in the room":
                            terminal.printf("\n MESSAGE " + msg + " DON'T SEND, YOU AREN'T IN THE ROOM \n");
                            break;
                        case "Error":
                            terminal.printf("\n SOMETHING WENT WRONG AMMO \n");
                            break;
                    }
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
                    case "Leave":
                        terminal.printf("\nSUCCESSFULLY LEAVE FROM\n" + roomName);
                        break;
                    case "Not Leave":
                        terminal.printf("\nERROR IN LEAVE FROM\n" + roomName);
                        break;
                    case "Not joined":
                        terminal.printf("\nERROR IN LEAVE FROM " + roomName + " MAYBE YOU'RE NOT JOINED\n");
                        break;
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
                    case "Destroyed":
                        terminal.printf("\nSUCCESSFULLY DESTROYED THE ROOM \n" + roomName);
                        break;
                    case "Not Destroyed":
                        terminal.printf("\nERRROR IN ROOM \n" + roomName + " DESTROY YOU AREN'T ALONE, THERE ARE " + (chatRoom.getUsers().size() - 1) + " USERS WITH U");
                        break;
                    case "Not Found":
                        terminal.printf("\nERROR IN ROOM " + roomName + " DESTROY\n" + " MAYBE ROOM DOESN'T EXISTS");
                        break;
                }
            }
        } else {
            terminal.printf("\nYOU'RE ANSWER WAS NO, SO DO YOU WANNA LEAVE THE ROOM?\n");
            if (roomName != null && !roomName.isEmpty()) {
                String ris = peer.leaveRoom(roomName);
                switch (ris) {
                    case "Leave":
                        terminal.printf("\nSUCCESSFULLY LEAVE FROM\n" + roomName);
                        break;
                    case "Not Leave":
                        terminal.printf("\nERROR IN LEAVE FROM\n" + roomName);
                        break;
                    case "Not joined":
                        terminal.printf("\nERROR IN LEAVE FROM " + roomName + " MAYBE YOU'RE NOT JOINED\n");
                        break;
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
            int ris = peer.showUsers(roomName);

            //terminal.print("\nROOM " + roomName + " DESTROYED\n");

            if (ris == 1)
                terminal.printf("\n THERE IS ONLY YOU " + "IN " + roomName + " ROOM\n");
            else if (ris >= 0)
                terminal.printf("\n THERE ARE (INCLUDING YOU) " + (chatRoom.getUsers().size()) + " USERS  IN " + roomName + " ROOM\n");
            else if (ris == -1)
                terminal.printf("\nERROR IN ROOM \n" + roomName + " FIND, CHECK THE NAME ");
            else
                terminal.printf("\nPROBLEM WITH THE RETRIEVE OF " + roomName + " ROOM \n");
        }
    }

    public void showMsg() {

        String roomName = textIO.newStringInputReader().read("\n ENTER ROOM NAME THAT YOU WANT TO SEE THE MSGS ");
        if (peer.getMyChatRoomList().contains(roomName)) {
            List<Message> messageList = listHashMap.get(roomName);
            System.out.println("Messaggio size " + messageList.size());
            if (messageList.size() == 0) {
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
