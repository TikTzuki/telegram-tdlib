import org.tiktuzki.telegram.*;

public class Test {
    public static void main(String[] args) {
        Client.setLogMessageHandler(0, ( var1, var2) ->
                System.out.println("Telegram-$var1: $var2")
        );
//        Client.create((r)->{}, null, null);
    }
}
