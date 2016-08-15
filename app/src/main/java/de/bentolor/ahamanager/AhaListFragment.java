package de.bentolor.ahamanager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AhaListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.aha_title);
        List<Aha> mAhas = AhaLab.get(getActivity()).getAhas();

        setListAdapter(new AhaAdapter(mAhas));

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_aha_list, container, false);
        Button createAhaButton = (Button) v.findViewById(R.id.fragment_crime_create);
        createAhaButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View ahaButton) {
                showCreateAha();
            }
        });

        ListView listView = (ListView) v.findViewById(android.R.id.list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Use floating context menus on Froyo and Gingerbread
            registerForContextMenu(listView);
        } else {
            // Use contextual action bar on Honeycomb and higher
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.aha_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_crime:
                            AhaAdapter adapter = (AhaAdapter) getListAdapter();
                            AhaLab ahaLab = AhaLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    ahaLab.deleteAha(adapter.getItem(i));
                                }
                            }
                            mode.finish();
                            adapter.notifyDataSetChanged();
                            ahaLab.saveAhas();
                            return true;
                        default:
                            return false;
                    }

                }

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                }
            });
        }

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Aha aha = ((AhaAdapter) getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), AhaPagerActivity.class);
        i.putExtra(AhaFragment.EXTRA_AHA_ID, aha.getId());
        startActivity(i);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        AhaAdapter adapter = (AhaAdapter) getListAdapter();
        Aha aha = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                AhaLab ahaLab = AhaLab.get(getActivity());
                ahaLab.deleteAha(aha);
                adapter.notifyDataSetChanged();
                ahaLab.saveAhas();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AhaAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_aha_list, menu);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                showCreateAha();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.aha_list_item_context, menu);
    }

    private void showCreateAha() {
        Aha aha = new Aha();
        AhaLab.get(getActivity()).addAha(aha);
        Intent i = new Intent(getActivity(), AhaPagerActivity.class);
        i.putExtra(AhaFragment.EXTRA_AHA_ID, aha.getId());
        startActivityForResult(i, 0);
    }

    private class AhaAdapter extends ArrayAdapter<Aha> {
        public AhaAdapter(List<Aha> ahas) {
            super(getActivity(), 0, ahas);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_aha, parent, false);
            }

            // Configure for this crime
            Aha aha = getItem(position);

            TextView titleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(aha.getTitle());

            TextView dateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            CharSequence cs = DateFormat.format("EEEE, MMM dd, yyyy", aha.getDate());
            dateTextView.setText(cs);

            CheckBox solvedCheckedBox = (CheckBox) convertView.findViewById(R.id.aha_list_item_usefulCheckBox);
            solvedCheckedBox.setChecked(aha.isUseful());

            return convertView;
        }
    }
}
