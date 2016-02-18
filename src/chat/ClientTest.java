package chat;
import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		Client client, client1;
		client = new Client("127.0.0.1");
		client1 = new Client("127.0.0.1");
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.startRunning();
        client1.startRunning();

	}

}
