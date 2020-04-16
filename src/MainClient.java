import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainClient {

    public static void main(String[] args) throws InterruptedException {
        try(Socket socket = new Socket("127.0.0.1", 3345);
            BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
//            OutputStream oos = new DataOutputStream(socket.getOutputStream());
//            DataInputStream ois = new DataInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
        {
            System.out.println("Всем клиентам чата привет, остальным соболезнуем!");
            System.out.println("Введите никнейм:");
            String nickname = br.readLine();
            System.out.println("Приятного общения;)");

            Thread thr = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Message message = (Message) in.readObject();
                            //String inp = ois.readUTF();
                            System.out.println(message.nickname + ": " + message.text);
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            thr.start();

            while(!socket.isOutputShutdown()){
                if(br.ready()){
                    String clientCommand = br.readLine();
                    //oos.writeUTF(clientCommand);
                    Message message = new Message(nickname, clientCommand);
                    out.writeObject(message);
                    out.flush();
                    System.out.println("Message sent");
                }
            }

            System.out.println("Closing connections & channels on clentSide - DONE.");

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}