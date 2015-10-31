package ch.pantas.billsplitter.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.dataaccess.EventStore;
import ch.pantas.billsplitter.model.Event;
import ch.pantas.billsplitter.services.ActivityStarter;
import ch.pantas.splitty.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class ChooseGroup extends RoboActivity {

    @InjectView(R.id.group_list)
    private ListView groupList;

    private List<Event> groups;

    @Inject
    private EventStore eventStore;

    @Inject
    private ActivityStarter activityStarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.choose_group);
        setContentView(R.layout.choose_group);
    }

    @Override
    protected void onResume() {
        super.onResume();

        groups = Lists.reverse(eventStore.getAll());
        groupList.setAdapter(new ArrayAdapter<Event>(this,
                R.layout.drawer_list_item, groups));

        groupList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event group = groups.get(position);
                activityStarter.startAddOrReplaceParticipant(ChooseGroup.this, group);
                finish();
            }
        });
    }
}
