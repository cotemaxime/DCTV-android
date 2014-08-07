package cote.maxime.app.dctv.core;

/**
 * Created by maxime on 7/10/14.
 */
public class Item {
    private String id;
    private String text;
    private String content;
    private String link;

    public Item(String id, String text, String content, String link)
    {
        this.id = id;
        this.text = text;
        this.content = content;
        this.link = link;
    }

    public String getText() { return this.text; }

    public String getContent() { return this.content; }

    public String getId() { return this.id; }

    public String getLink() {return this.link; }
}
