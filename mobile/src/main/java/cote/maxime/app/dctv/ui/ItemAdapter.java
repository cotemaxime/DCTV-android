package cote.maxime.app.dctv.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cote.maxime.app.dctv.R;
import cote.maxime.app.dctv.core.Item;
import cote.maxime.app.dctv.persistence.Persistence;

public class ItemAdapter extends ArrayAdapter<Item>
{
    private int resource;

    public ItemAdapter(Context context, int resource, List<Item> items)
    {
        super(context, resource, items);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout conView;
        Item i = this.getItem(position);

        if(convertView == null)
        {
            conView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, conView, true);
        }
        else
            conView = (LinearLayout) convertView;

        TextView tTitle = (TextView) conView.findViewById(R.id.itemTitle);
        tTitle.setText(i.getText());
        TextView tLink = (TextView) conView.findViewById(R.id.itemLink);

        if(i.getLink().equals(""))
            tLink.setVisibility(View.GONE);
        else
            tLink.setText(i.getLink());

        TextView tContent = (TextView) conView.findViewById(R.id.itemContent);
        tContent.setText(i.getContent());

        return conView;
    }

    @Override
    public void remove(Item item)
    {
        super.remove(item);
        Persistence.getInstance().removeItem(item);
    }

    public void removeAll()
    {
        for(int i = getCount() - 1; i >= 0; i--)
            remove(getItem(i));
    }
}