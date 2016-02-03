import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by Bamdad Aghili on 2/22/14.
 */
public class Sender {
    File file = new File("out.jpg");
    private String hostname;
    private int port;
    private Socket socket;
//    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    /**
     * creates socket, connect to destination hostname with the specified port
     */
    public Sender(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws Exception {
        try {
            socket = new Socket(hostname, port);
            socket.setSoTimeout(115000);
        } catch (IOException e) {
//            log.error("error connecting: {}", e);
            throw new Exception("error connecting to host");
        }
    }

    /**
     * sends message to destination host
     */


    public void write(String message) throws Exception {

        ByteBuffer b = ByteBuffer.allocate(message.length());
        b.put(message.getBytes());
        socket.getOutputStream().write(b.array());
//        socket.getOutputStream().flush();


//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            message.write(byteArrayOutputStream);
//            byteArrayOutputStream.flush();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            objectOutputStream.writeInt(byteArrayOutputStream.size());
//            objectOutputStream.write(byteArrayOutputStream.toByteArray());
//            socket.getOutputStream().flush();
//            objectOutputStream.flush();
    }

    /**
     */
    public String read() throws Exception {

        byte[] lenBuffer = new byte[6];
//            byte[] len = new byte[4];
//            len[0] = (byte) socket.getInputStream().read();
//            len[1] = (byte) socket.getInputStream().read();
//            len[2] = (byte) socket.getInputStream().read();
//            len[3] = (byte) socket.getInputStream().read();
        //int size = ByteBuffer.wrap(len).getInt();
        socket.getInputStream().read(lenBuffer);
        int len = Integer.parseInt(new String(lenBuffer));
        System.out.println("Message len:" + len);
        byte[] buffer = new byte[len];
//            byte[] len = new byte[4];
//            len[0] = (byte) socket.getInputStream().read();
//            len[1] = (byte) socket.getInputStream().read();
//            len[2] = (byte) socket.getInputStream().read();
//            len[3] = (byte) socket.getInputStream().read();
        //int size = ByteBuffer.wrap(len).getInt();
        int read = 0;
        while (read < len) {
            read += socket.getInputStream().read(buffer, read, len - read);
        }
        return new String(buffer);
    }

    public byte[] readBytes() throws Exception {

        byte[] lenBuffer = new byte[6];
//            byte[] len = new byte[4];
//            len[0] = (byte) socket.getInputStream().read();
//            len[1] = (byte) socket.getInputStream().read();
//            len[2] = (byte) socket.getInputStream().read();
//            len[3] = (byte) socket.getInputStream().read();
        //int size = ByteBuffer.wrap(len).getInt();
        socket.getInputStream().read(lenBuffer);
        int len = Integer.parseInt(new String(lenBuffer));
        System.out.println("Message len:" + len);
        byte[] buffer = new byte[len];
//            byte[] len = new byte[4];
//            len[0] = (byte) socket.getInputStream().read();
//            len[1] = (byte) socket.getInputStream().read();
//            len[2] = (byte) socket.getInputStream().read();
//            len[3] = (byte) socket.getInputStream().read();
        //int size = ByteBuffer.wrap(len).getInt();
        int read = 0;
        while (read < len) {
            read += socket.getInputStream().read(buffer, read, len - read);
        }
        return buffer;
    }

    public void disconnect() throws Exception {
        try {
            socket.close();  // Close the socket and its streams
        } catch (IOException e) {
        }
    }
}