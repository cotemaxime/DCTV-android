package tv.diamondclub.dctv.core;

import java.util.Date;

/**
 * Created by maxime on 7/10/14.
 */
public class Item {
    private String text;
    private Date date;

    public Item(String text, Date date)
    {
        this.text = text;
        this.date = date;
    }

    public String getDate() { return this.text; }

    public String getText() { return this.date.toString(); }
}
