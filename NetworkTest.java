import java.io.IOException;

public class NetworkTest {
    public static void main(String[] args) throws IOException {
        Network a = new Network(1, 20);
        a.generate2D4N();
        a.printNetworkToFile("2D4N.txt");
    }
}
