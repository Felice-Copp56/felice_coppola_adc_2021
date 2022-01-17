import beans.ChatRoom;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;

public class Tester {
    @Option(name = "-m", aliases = "--masterip", usage = "the master peer ip address", required = true)
    private static String master;

    @Option(name = "-id", aliases = "--identifierpeer", usage = "the unique identifier for this peer", required = true)
    private static int id;

    AnonymousChatImpl peer;
    TextIO textIO = TextIoFactory.getTextIO();
    TextTerminal terminal;

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
            while (true) {
                printMenu(terminal);

                int option = textIO.newIntInputReader()
                        .withMaxVal(6)
                        .withMinVal(1)
                        .read("Option");
                switch (option) {
                    case 1 -> createNewRoomFirstOption();
                    case 2 -> joinRoomSecondOption();
                    case 3 -> leaveRoomThirdOption();
                    case 4 -> sendMessageToRoomFourthOption();
                    case 5 -> leaveNetworkFifthOption();
                    case 6 -> destroyRoomSixthOption();
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
                case "Successo" -> terminal.printf("\n ROOM CREATED SUCCESSFULLY\n");
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
                case "Successo" -> terminal.printf("\n SUCCESSFULLY JOINED TO %s\n", roomName + "THERE ARE "+(chatRoom.getUsers().size()-1)+" USERS WITH YOU!");
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
        if (roomName != null && !roomName.isEmpty()) {
            String ris = peer.sendMessage(roomName, msg);
            switch (ris) {
                case "Successo" -> terminal.printf("Il messaggio " + msg + " è stato inviato correttamente alla room " + roomName);
                case "Fail" -> terminal.printf("Il messaggio " + msg + " non è stato inviato correttamente alla room " + roomName);
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
            if (roomName!=null&&!roomName.isEmpty()) {
                ChatRoom chatRoom = peer.findRoom(roomName);
                String ris =peer.destroyRoom(roomName);

                //terminal.print("\nROOM " + roomName + " DESTROYED\n");
                switch (ris) {
                    case "Destroyed" -> terminal.printf("\nSUCCESSFULLY DESTROYED THE ROOM \n" + roomName);
                    case "Not destroyed" -> terminal.printf("\nERRROR IN ROOM \n" + roomName+" DESTROY YOU AREN'T ALONE, THERE ARE "+(chatRoom.getUsers().size()-1)+" USERS WITH U");
                    case "Not found" -> terminal.printf("\nERROR IN ROOM " + roomName + " DESTROY\n"+" MAYBE ROOM DOESN'T EXISTS");
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
    public static void printMenu(TextTerminal terminal) {
        terminal.printf("\n1 - CREATE ROOM\n");
        terminal.printf("\n2 - JOIN ROOM\n");
        terminal.printf("\n3 - LEAVE ROOM\n");
        terminal.printf("\n4 - SEND MESSAGE TO ROOM\n");
        terminal.printf("\n5 - LEAVE NETWORK\n");
        terminal.printf("\n6 - DESTROY ROOM\n");

    }
}
