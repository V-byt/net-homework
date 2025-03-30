public class Message {
    private String operation;
    private String text;

    public Message(String operation, String text) {
        this.operation = operation;
        this.text = text;
    }

    public Message() {}
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
