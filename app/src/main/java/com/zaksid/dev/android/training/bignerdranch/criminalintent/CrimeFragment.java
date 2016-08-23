package com.zaksid.dev.android.training.bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;


/**
 * Class that holds view of a single crime
 */
public class CrimeFragment extends Fragment {

    public final static CharSequence DATE_TIME_FORMAT = "EEEE, LLL d, yyyy | h:mm a"; // i.e. Saturday, Jul 23, 2016 | 5.12 AM

    private final static CharSequence DATE_FORMAT = "EEEE, LLL d, yyyy"; // i.e. Saturday, Jul 23, 2016
    private final static CharSequence TIME_FORMAT = "h:mm a"; // i.e. 5.12 AM
    private final static String ARG_CRIME_ID = "crime_id";
    private final static String DIALOG_DATE = "DialogDate";
    private final static String DIALOG_TIME = "DialogTime";
    private final static int REQUEST_DATE = 0;
    private final static int REQUEST_TIME = 1;
    private final static int REQUEST_CONTACT = 2;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private Button reportButton;
    private Button suspectButton;
    private CheckBox isSolvedCheckbox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        crime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        titleField = (EditText) view.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                crime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        dateButton = (Button) view.findViewById(R.id.crime_date);
        updateDateOnButton(dateButton, DATE_FORMAT);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        timeButton = (Button) view.findViewById(R.id.crime_time);
        updateDateOnButton(timeButton, TIME_FORMAT);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fragmentManager, DIALOG_TIME);
            }
        });


        isSolvedCheckbox = (CheckBox) view.findViewById(R.id.crime_solved);
        isSolvedCheckbox.setChecked(crime.isSolved());
        isSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        reportButton = (Button) view.findViewById(R.id.crime_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.crime_report_subject);
                // Force a chooser to be shown every time an implicit intent is used to start an activity
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        suspectButton = (Button) view.findViewById(R.id.crime_suspect);
        suspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (crime.getSuspect() != null) {
            suspectButton.setText(String.format("%s %s", getString(R.string.suspect_on_button), crime.getSuspect()));
        }

        PackageManager packageManager = getActivity().getPackageManager();
        // If there is no contacts app - disable button (in other case the app will crash)
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.setEnabled(false);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_remove_crime:
                CrimeLab.get(getActivity()).removeCrime(crime);
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            updateDateOnButton(dateButton, DATE_FORMAT);
        }

        if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            crime.setDate(date);
            updateDateOnButton(timeButton, TIME_FORMAT);
        }

        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor cursor = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

            assert cursor != null;
            try {
                if (cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                crime.setSuspect(suspect);
                suspectButton.setText(String.format("%s %s", getString(R.string.suspect_on_button), suspect));
            } finally {
                cursor.close();
            }
        }
    }

    private void updateDateOnButton(Button button, CharSequence formatterString) {
        button.setText(DateFormat.format(formatterString, crime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString = getString(crime.isSolved()
            ? R.string.crime_report_solved
            : R.string.crime_report_unsolved);

        String dateString = DateFormat.format(DATE_FORMAT, crime.getDate()).toString();

        String suspectString = crime.getSuspect();
        suspectString = (suspectString == null)
            ? getString(R.string.crime_report_no_suspect)
            : getString(R.string.crime_report_suspect, suspectString);

        return getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspectString);
    }
}
