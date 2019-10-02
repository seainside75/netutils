import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class ReceiverOldIO {
    public static  int DEFAULT_MULTICAST_PORT = 5201;
    public static  String multicastGroup = "224.9.2.250";

    public static final int MAX_PACKET_SIZE = 65536;

    CharBuffer charBuffer = null;
    Charset charset = Charset.defaultCharset();
    CharsetDecoder decoder = charset.newDecoder();
    static ByteBuffer message = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
    static boolean loop = true;

    static byte[] buffer = new byte[MAX_PACKET_SIZE];

    public static void main(String[] args) {
        if (args.length >1) {
            multicastGroup = args[1];
            DEFAULT_MULTICAST_PORT = Integer.parseInt(args[2]);
        }

        try {
            MulticastSocket mSocket = new MulticastSocket( DEFAULT_MULTICAST_PORT);
            mSocket.joinGroup(InetAddress.getByName(multicastGroup));
            DatagramPacket p = new DatagramPacket(buffer, MAX_PACKET_SIZE);
            while (loop) {
                try {
                    mSocket.receive(p);
                    System.out.println("Packet Received whose length is:"+p.getLength());
                } catch (SocketTimeoutException ex) {
                    System.out.println("Socket Timed out");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}