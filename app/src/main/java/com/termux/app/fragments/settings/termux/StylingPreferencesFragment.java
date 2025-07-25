package com.termux.app.fragments.settings.termux;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.termux.app.R;

public class StylingPreferencesFragment extends PreferenceFragmentCompat {

    public static final String KEY_BACKGROUND_IMAGE = "background_image_uri";

    private ActivityResultLauncher<Intent> mGetContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            // Take persistable URI permission
                            final int takeFlags = data.getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            requireActivity().getContentResolver().takePersistableUriPermission(uri, takeFlags);

                            // Save URI to SharedPreferences
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
                            prefs.edit().putString(KEY_BACKGROUND_IMAGE, uri.toString()).apply();
                        }
                    }
                }
            });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.styling_preferences, rootKey);

        Preference backgroundImagePref = findPreference("background_image");
        if (backgroundImagePref != null) {
            backgroundImagePref.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                mGetContent.launch(intent);
                return true;
            });
        }
    }
}
