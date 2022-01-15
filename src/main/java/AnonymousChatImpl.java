import Interfaces.AnonymousChat;
import Interfaces.MessageListener;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.natpmp.Message;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;

import java.io.IOException;
import java.net.InetAddress;

public class AnonymousChatImpl implements AnonymousChat {

    final private Peer peer;
    final private PeerDHT peerDHT;
    final private int DEFAULT_MASTER_PORT=4000;

    public AnonymousChatImpl(int _id, String _master_peer, final MessageListener _listener) throws Exception {
        peer= new PeerBuilder(Number160.createHash(_id)).ports(DEFAULT_MASTER_PORT+_id).start();
        peerDHT = new PeerBuilderDHT(peer).start();

        FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(DEFAULT_MASTER_PORT).start();
        fb.awaitUninterruptibly();
        if(fb.isSuccess()) {
            peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }else {
            throw new Exception("Error in master peer bootstrap.");
        }

        peer.objectDataReply(new ObjectDataReply() {

            public Object reply(PeerAddress sender, Object request) throws Exception {
                return _listener.parseMessage(request);
            }
        });
    }

    @Override
    public boolean createRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean joinRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean leaveRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean sendMessage(String _room_name, String _text_message) {
        return false;
    }
}
