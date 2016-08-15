package de.bentolor.ahamanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class AhaFragment extends Fragment {
    public static final String EXTRA_AHA_ID = "de.bentolor.ahamoment.aha_id";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_TIME = "time";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Aha mAha;
    private Button mDateButton;
    private Button mTimeButton;

    public static AhaFragment newInstance(UUID ahaId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_AHA_ID, ahaId);

        AhaFragment fragment = new AhaFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID ahaID = (UUID) getArguments().getSerializable(EXTRA_AHA_ID);

        mAha = AhaLab.get(getActivity()).getAha(ahaID);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_aha, container, false);

        if (NavUtils.getParentActivityName(getActivity()) != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText mTitleField = (EditText) v.findViewById(R.id.aha_title);
        mTitleField.setText(mAha.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mAha.setTitle(c.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable c) {
            }
        });

        mDateButton = (Button) v.findViewById(R.id.aha_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View dateButton) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mAha.getDate());
                dialog.setTargetFragment(AhaFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.aha_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View timeButton) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mAha.getDate());
                dialog.setTargetFragment(AhaFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        updateDate();

        CheckBox usefulBox = (CheckBox) v.findViewById(R.id.aha_useful);
        usefulBox.setChecked(mAha.isUseful());
        usefulBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAha.setUseful(isChecked);
            }
        });

        return v;
    }

    private void updateDate() {
        Date d = mAha.getDate();
        CharSequence c = DateFormat.format("EEEE, MMM dd, yyyy", d);
        CharSequence t = DateFormat.format("h:mm a", d);
        mDateButton.setText(c);
        mTimeButton.setText(t);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mAha.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mAha.setDate(date);
            updateDate();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_aha, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_crime:
                AhaLab ahaLab = AhaLab.get(getActivity());
                ahaLab.deleteAha(mAha);
                ahaLab.saveAhas();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AhaLab.get(getActivity()).saveAhas();
    }
}
