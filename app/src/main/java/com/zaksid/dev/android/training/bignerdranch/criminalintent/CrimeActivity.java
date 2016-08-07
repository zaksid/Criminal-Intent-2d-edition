package com.zaksid.dev.android.training.bignerdranch.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Activity class that holds CrimeFragment
 */
public class CrimeActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }

}
