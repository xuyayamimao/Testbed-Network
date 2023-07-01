import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class NetworkTest {
    public static void main(String[] args) {
        Network a = new Network(1, 20);
        a.generate2D4N();
        a.printNetwork();
    }
}
