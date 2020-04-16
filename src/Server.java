import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {

    static Map<String, ObjectOutputStream> OutList = new HashMap<String, ObjectOutputStream>();

    static class LisenerSpeaker implements Runnable {
//        DataInputStream in;
//        DataOutputStream out;
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket client;
        String key;
        LisenerSpeaker(Socket client, String key) {
            this.key = key;
            this.client = client;
            try {
                this.in = new ObjectInputStream(this.client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                this.out = new ObjectOutputStream(this.client.getOutputStream());
                OutList.put(key, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                while (!client.isClosed()) {
                    //String inp = key + ": " + in.readUTF();
                    Message message = (Message) in.readObject();
                    for (String outKey: OutList.keySet()) {
                        ObjectOutputStream outValue = OutList.get(outKey);
                        if (!outKey.equals(key)) {
                            outValue.writeObject(message);
                            outValue.flush();
                        }
                    }
                    //out.writeUTF(inp);
                    //out.flush();
                }
                in.close();
                OutList.remove(key).close();
                client.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws InterruptedException {

        try (ServerSocket server= new ServerSocket(3345)) {

            Boolean work = true;
            int i = 0;

            while (work) {
                Socket client = server.accept();
                i++;
                String nickname = "Client" + Integer.toString(i);
                System.out.println("Connection accepted." + '(' + nickname + ')');
                service.submit(new LisenerSpeaker(client, nickname));
            }

            //System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}