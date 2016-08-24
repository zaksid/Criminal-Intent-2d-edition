package com.zaksid.dev.android.training.bignerdranch.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.Toast;

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
    private final static int REQUEST_DATE = 100;
    private final static int REQUEST_TIME = 101;
    private final static int REQUEST_CONTACT = 102;
    private final static int PERMISSIONS_REQUEST_READ_CONTACTS = 200;

    private Intent pickContact;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private Button reportButton;
    private Button suspectButton;
    private ImageButton callSuspectButton;
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
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setText(getCrimeReport())
                    .setSubject(getString(R.string.crime_report_subject))
                    .setChooserTitle(getString(R.string.send_report))
                    .createChooserIntent();

                startActivity(intent);
            }
        });

        pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        callSuspectButton = (ImageButton) view.findViewById(R.id.crime_call_suspect);
        callSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + crime.getSuspectPhoneNumber()));
                startActivity(intent);
            }
        });

        suspectButton = (Button) view.findViewById(R.id.crime_suspect);
        suspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasReadContactPermission = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CONTACTS);

                if (hasReadContactPermission != PackageManager.PERMISSION_GRANTED) {
                    // No permission granted

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_CONTACTS)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                        new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.contacts_access_explanation))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                        PERMISSIONS_REQUEST_READ_CONTACTS);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .create()
                            .show();
                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                } else {
                    startActivityForResult(pickContact, REQUEST_CONTACT);
                }

            }
        });

        if (crime.getSuspect() != null) {
            suspectButton.setText(String.format("%s %s", getString(R.string.suspect_on_button), crime.getSuspect()));
            callSuspectButton.setVisibility(View.VISIBLE);
            callSuspectButton.setEnabled(crime.getSuspectPhoneNumber() != null);
        } else {
            callSuspectButton.setVisibility(View.GONE);
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
            getContactFromDB(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(pickContact, REQUEST_CONTACT);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), getString(R.string.contacts_access_denied), Toast.LENGTH_SHORT)
                        .show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private void getContactFromDB(Intent data) {
        final Uri CONTACT_URI = data.getData();
        final String[] PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        };
        final String SELECTION = null;
        final String[] SELECTION_ARGS = null;
        final String SORT_ORDER = null;

        Cursor cursor = getActivity().getContentResolver()
            .query(CONTACT_URI, PROJECTION, SELECTION, SELECTION_ARGS, SORT_ORDER);

        assert cursor != null;

        String contactId;
        String hasPhone;

        try {
            if (cursor.getCount() == 0) {
                return;
            }

            cursor.moveToFirst();

            String suspectName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            crime.setSuspect(suspectName);
            crime.setSuspectPhoneNumber(null);

            suspectButton.setText(String.format("%s %s", getString(R.string.suspect_on_button), suspectName));
        } finally {
            cursor.close();
        }

        if (crime.getSuspect() != null || hasPhone.equalsIgnoreCase("1")) {
            getContactPhoneNumberFromDB(contactId);
        }
    }

    private void getContactPhoneNumberFromDB(String contactId) {
        final Uri COMMON_DATA_KIND_PHONE_URI = Phone.CONTENT_URI;
        final String[] PROJECTION = new String[]{
            Phone.NUMBER,
            Phone.TYPE
        };
        final String SELECTION = Phone.CONTACT_ID + " = ?";
        final String[] SELECTION_ARGS = new String[]{contactId};
        final String SORT_ORDER = null;

        Cursor cursor = getActivity().getContentResolver()
            .query(COMMON_DATA_KIND_PHONE_URI, PROJECTION, SELECTION, SELECTION_ARGS, SORT_ORDER);

        assert cursor != null;

        try {
            if (cursor.getCount() == 0) {
                return;
            }

            boolean mobileDefined = false;

            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));

                switch (type) {
                    case Phone.TYPE_MOBILE:
                        crime.setSuspectPhoneNumber(phoneNumber);
                        mobileDefined = true;

                        break;

                    case Phone.TYPE_WORK:
                        if (!mobileDefined)
                            crime.setSuspectPhoneNumber(phoneNumber);

                        break;
                }

                callSuspectButton.setVisibility(View.VISIBLE);
                callSuspectButton.setEnabled(crime.getSuspectPhoneNumber() != null);
            }

        } finally {
            cursor.close();
        }
    }
}
