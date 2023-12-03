package Helper;

public class Message {
  private String message;
  private String attachedB64Image;

  private String uuid;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getAttachedB64Image() {
    return attachedB64Image;
  }

  public void setAttachedB64Image(String attachedB64Image) {
    this.attachedB64Image = attachedB64Image;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
}
