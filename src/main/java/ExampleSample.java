import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;

public class ExampleSample {

    //una variabile d'istanza peerDHT
    final private PeerDHT peer;

    public ExampleSample(int peerId) throws Exception {
        //
        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(peerId)).ports(4000 + peerId).start()).start();

        System.out.println("Peer: " + peer);

        FutureBootstrap fb = this.peer.peer().bootstrap().inetAddress(InetAddress.getByName("127.0.0.1")).ports(4001).start();

        fb.awaitUninterruptibly();

        if (fb.isSuccess()) {
            peer.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }
    }

    //Il metodo effettua una peerget tramite la stringa name
    private String get(String name) throws ClassNotFoundException, IOException{
        FutureGet futureGet = peer.get(Number160.createHash(name)).start();
        futureGet.awaitUninterruptibly();
        if (futureGet.isSuccess()){
            //.next restituisce un oggetto di tipo data, che espone il metodo .object (vedere documentazione)
            return futureGet.dataMap().values().iterator().next().object().toString();
        }
        return "not found";
    }
    private void store(String name, String ip)throws IOException{
        peer.put(Number160.createHash(name)).data(new Data(ip)).start().awaitUninterruptibly();
    }

    //Chi non può mancare è il masterpeer, nel nostro 4001 il peer final private
    public static void main(String[] args) throws Exception {
        ExampleSample dns = new ExampleSample(Integer.parseInt(args[0]));
        if (args.length==3){
            dns.store(args[1],args[2]);
        }
        if (args.length==2){
            System.out.println("Name:"+args[1]+"IP:"+dns.get(args[1]));
        }
    }
}
