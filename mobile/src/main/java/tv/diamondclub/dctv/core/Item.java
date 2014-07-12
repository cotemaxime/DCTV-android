package tv.diamondclub.dctv.core;

/**
 * Created by maxime on 7/10/14.
 */
public class Item {
    private String id;
    private String text;
    private String content;

    public Item(String id, String text, String content)
    {
        this.id = id;
        this.text = text;
        this.content = content;
    }

    public String getText() { return this.text; }

    public String getContent() { return this.content; }

    public String getId() { return this.id; }
}
