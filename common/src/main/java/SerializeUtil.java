import java.nio.charset.StandardCharsets;

public class SerializeUtil {
    public static byte[] serialize(Message message){
        String res = message.getOperation() + " " + message.getText();
        return res.getBytes(StandardCharsets.UTF_8);
    }

    public static Message deserialize(String str){
        String[] strs = str.split(" ");
        Message message = new Message();
        message.setOperation(strs[0]);
        if(strs.length > 1)
            message.setText(strs[1]);
        return message;
    }

}
