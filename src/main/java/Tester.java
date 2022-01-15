import Interfaces.MessageListener;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Tester {
    @Option(name="-m", aliases="--masterip", usage="the master peer ip address", required=true)
    private static String master;

    @Option(name="-id", aliases="--identifierpeer", usage="the unique identifier for this peer", required=true)
    private static int id;

    AnonymousChatImpl peer;
    TextIO textIO = TextIoFactory.getTextIO();
    TextTerminal terminal;
    public Tester(String masterPeer, int peerID) {
        try {
            peer = new AnonymousChatImpl(id, masterPeer, new MessageListenerImpl(peerID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String masterPeer= args[0];
        int peerID=Integer.parseInt(args[1]);
        Tester tester = new Tester(masterPeer,peerID);
        tester.launchAnonymous(peerID, masterPeer);

    }

    public void launchAnonymous(int peerID, String masterPeer){
        try
        {
            terminal = textIO.getTextTerminal();
            terminal.printf("\nStaring peer id: %d on master node: %s\n",
                    peerID, masterPeer);
            while(true) {
                printMenu(terminal);

                int option = textIO.newIntInputReader()
                        .withMaxVal(5)
                        .withMinVal(1)
                        .read("Option");
                switch (option) {
                    case 1 -> {
                        terminal.printf("\nENTER ROOM NAME\n");
                        String roomName = textIO.newStringInputReader()
                                .withDefaultValue("default-room")
                                .read("Name:");
                        if (peer.createRoom(roomName))
                            terminal.printf("\nROOM %s SUCCESSFULLY CREATED\n", roomName);
                        else
                            terminal.printf("\nERROR IN ROOM CREATION\n");
                    }
                    case 2 -> {
                        terminal.printf("\nENTER TOPIC NAME\n");
                        String sname = textIO.newStringInputReader()
                                .withDefaultValue("default-topic")
                                .read("Name:");
                        if (peer.joinRoom(sname))
                            terminal.printf("\n SUCCESSFULLY JOINED TO %s\n", sname);
                        else
                            terminal.printf("\nERROR IN ROOM SUBSCRIPTION\n");
                    }
                    case 3 -> {
                        terminal.printf("\nENTER TOPIC NAME\n");
                        String uname = textIO.newStringInputReader()
                                .withDefaultValue("default-topic")
                                .read("Name:");
                        if (peer.leaveRoom(uname))
                            terminal.printf("\n SUCCESSFULLY UNSUBSCRIBED TO %s\n", uname);
                        else
                            terminal.printf("\nERROR IN TOPIC UN SUBSCRIPTION\n");
                    }
                    case 4 -> {
                        terminal.printf("\nENTER ROOM NAME\n");
                        String tname = textIO.newStringInputReader()
                                .withDefaultValue("default-room")
                                .read(" Name:");
                        terminal.printf("\nENTER MESSAGE\n");
                        String message = textIO.newStringInputReader()
                                .withDefaultValue("default-message")
                                .read(" Message:");
                        if (peer.sendMessage(tname, message))
                            terminal.printf("\n SUCCESSFULLY PUBLISH MESSAGE ON ROOM %s\n", tname);
                        else
                            terminal.printf("\nERROR IN ROOM PUBLISH\n");
                    }
                    default -> {
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void printMenu(TextTerminal terminal) {
        terminal.printf("\n1 - CREATE ROOM\n");
        terminal.printf("\n2 - JOIN ROOM\n");
        terminal.printf("\n3 - LEAVE ROOM\n");
        terminal.printf("\n4 - SEND MESSAGE TO ROOM\n");

    }
}
