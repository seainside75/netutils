import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ReceiverNIO {
    public static int DEFAULT_MULTICAST_PORT = 5201;
    public static String multicastGroup = "224.9.2.250";

    public static final int MAX_PACKET_SIZE = 65536;

    CharBuffer charBuffer = null;
    Charset charset = Charset.defaultCharset();
    CharsetDecoder decoder = charset.newDecoder();
    static ByteBuffer message = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
    static boolean loop = true;

    static byte[] buffer = new byte[MAX_PACKET_SIZE];

    public static void main(String[] args) {

        NetworkInterface ni = null;
        String bindingIP = null;


        try {

            if (args.length == 1) {
                bindingIP = args[0];
            } else if (args.length == 3) {
                bindingIP = args[0];
                multicastGroup = args[1];
                DEFAULT_MULTICAST_PORT = Integer.parseInt(args[2]);

            } else {
                System.out.println("Usage: ReceiverNIO <bindingNetworkIP>");
                System.out.println("Usage: ReceiverNIO <bindingIP> <multicastgroup> <multicastport> ");
                System.exit(0);
            }
            System.out.println("Running with : "+multicastGroup+":"+DEFAULT_MULTICAST_PORT + " on "+ bindingIP);

            ni = NetworkInterface.getByInetAddress(InetAddress.getByName(bindingIP));
            System.out.println("Found NIC name: "+ ni.getName() + " displayed as"+ ni.getDisplayName());

            InetAddress group = InetAddress.getByName(multicastGroup);


            DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                    .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                    .bind(new InetSocketAddress(DEFAULT_MULTICAST_PORT))
                    .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            MembershipKey key = dc.join(group, ni);

            ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_PACKET_SIZE);

            int i = 0;
            System.out.println("Starting to listen....");
            while (true) {
                if (key.isValid()) {
                    byteBuffer.clear();
                    InetSocketAddress sa = (InetSocketAddress) dc.receive(byteBuffer);

                    byteBuffer.flip();

                    System.out.println("Multicast packet received " + i++ + " from " + sa.getHostString()
                            + " size:" +byteBuffer.limit());

                    // TODO: Parse message
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}