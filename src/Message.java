import java.io.Serializable;

public class Message implements Serializable {
    String nickname;
    String text;

    Message(String nickname, String text) {
        this.nickname = nickname;
        this.text = text;
    }
}
