package ch.pantas.billsplitter.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

import ch.pantas.billsplitter.dataaccess.TagStore;
import ch.pantas.billsplitter.model.Tag;
import ch.yvu.myapplication.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TagAdapter extends BaseAdapter {
    @Inject
    private LayoutInflater inflater;

    List<Tag> tags = new LinkedList<Tag>();

    private TagDeletedListener tagDeletedListener;

    public void setTagDeletedListener(TagDeletedListener tagDeletedListener) {
        this.tagDeletedListener = tagDeletedListener;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int i) {
        return tags.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(R.layout.tag_item, null);
        }

        final Tag tag = tags.get(i);
        TextView nameField = (TextView) view.findViewById(R.id.tag_item_name);
        nameField.setText(tag.getName());


        ImageView deleteButton = (ImageView) view.findViewById(R.id.tag_item_delete);
        deleteButton.setVisibility(tag.isNew() ? GONE : VISIBLE);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tagDeletedListener != null) {
                    tagDeletedListener.onTagDelete(tag);
                }
            }
        });

        return view;
    }
}
