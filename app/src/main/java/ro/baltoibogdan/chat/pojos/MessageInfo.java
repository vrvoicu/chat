package ro.baltoibogdan.chat.pojos;

public class MessageInfo {

    private String toFrom;
    private String message;

    public MessageInfo(String toFrom, String message) {
        this.toFrom = toFrom;
        this.message = message;
    }

    public String getToFrom() {
        return toFrom;
    }

    public void setToFrom(String toFrom) {
        this.toFrom = toFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
