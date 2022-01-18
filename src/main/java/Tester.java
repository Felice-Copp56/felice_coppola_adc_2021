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
    boolean exit = true;
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
            terminal.printf("\nStaring peer id: %d on master node: %s\n",
                    peerID, masterPeer);

            while (exit) {
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
            if (message != null && message.getRoomName() != null) {
                System.out.println("Sono dentro al null");
                if (listHashMap.get(message.getRoomName()) != null) {
                    System.out.println("Sono dentro al secondo null");
                    listHashMap.get(message.getRoomName()).add(message);//Aggiungo il messaggio
                    terminal.printf("\n" + peerid + "] (Direct Message Received) Message received in room: " + message.getRoomName() + "ALLE ORE" + message.getData().toString() + "\n\n");
                }
            }
            if (message != null) return message;
            return "not success";
        }

        public HashMap<String, List<Message>> getListMsg(String roomm) {
            return listMsg;
        }
    }


    public void inTheRoom(int peerID, String masterPeer) {
        try {
            terminal = textIO.getTextTerminal();
            terminal.printf("\nStaring peer id: %d on master node: %s\n",
                    peerID, masterPeer);

            while (exitIn) {
                printMenuInTheRoom(terminal);

                int option = textIO.newIntInputReader()
                        .withMaxVal(6)
                        .withMinVal(1)
                        .read("Option");
                switch (option) {
                    case 1 -> {
                        leaveRoomThirdOption();
                        exit = false;
                    }
                    case 2 -> sendMessageToRoomFourthOption();
                    case 3 -> leaveNetworkFifthOption();
                    case 4 -> destroyRoomSixthOption();
                    case 5 -> exitIn = false;
                    default -> {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                case "Successo" -> {terminal.printf("\n ROOM CREATED SUCCESSFULLY\n"); listHashMap.put(roomName,new ArrayList<>());}
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
                case "Successo" -> {terminal.printf("\n SUCCESSFULLY JOINED TO %s\n", roomName + "THERE ARE " + (chatRoom.getUsers().size() - 1) + " USERS WITH YOU!"); listHashMap.put(roomName,new ArrayList<>());}
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
        terminal.printf("\nENTER THE MESSAGE FOR THE USERS\n");
        String msg = textIO.newStringInputReader().withDefaultValue("default-message").read("Message: ");
        Message messaggio = new Message();
        if (roomName != null && !roomName.isEmpty()) {
            messaggio.setMessage(msg);
            messaggio.setData(Calendar.getInstance().getTime());
            messaggio.setRoomName(roomName);
            String ris = peer.tryToSendMsg(roomName, messaggio);
            switch (ris) {
                case "Sent" -> {
                    messaggio.setMyMsg(true);

                    if (listHashMap.get(roomName) == null)
                        System.out.println("Dentro al successo");
                            listHashMap.put(roomName, new ArrayList<>());
                        listHashMap.get(roomName).add(messaggio);

                    terminal.printf("Il messaggio " + msg + " è stato inviato correttamente alla room " + roomName);
                }
                case "not sent" -> terminal.printf("Il messaggio " + msg + " non è stato inviato correttamente alla room " + roomName);
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
                    case "Not destroyed" -> terminal.printf("\nERRROR IN ROOM \n" + roomName + " DESTROY YOU AREN'T ALONE, THERE ARE " + (chatRoom.getUsers().size() - 1) + " USERS WITH U");
                    case "Not found" -> terminal.printf("\nERROR IN ROOM " + roomName + " DESTROY\n" + " MAYBE ROOM DOESN'T EXISTS");
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
        String roomName = textIO.newStringInputReader().read("\n INSERT NAME");
        if (peer.getMyChatRoomList().contains(roomName)) {
            List<Message> messageList = listHashMap.get(roomName);
            System.out.println("Messaggio size "+messageList.size());
            for (Message message : messageList) {
                terminal.printf("Ricevuto: "+message.getMessage()+ "Alle ore:"+message.getData()+"\n");
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

    }

    public static void printMenuInTheRoom(TextTerminal terminal) {
        terminal.printf("\n1 - LEAVE ROOM\n");
        terminal.printf("\n2 - SEND MESSAGE TO ROOM\n");
        terminal.printf("\n3 - LEAVE NETWORK\n");
        terminal.printf("\n4 - DESTROY ROOM\n");
        terminal.printf("\n5 - EXIT MENU, IF U WANT TO READ MSG \n");


    }
}
