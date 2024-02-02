package com.example.barcodetest;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        SwitchPreference switch_can_edit;
        SwitchPreference switch_mostrar_precio_compra;
        SwitchPreference switch_editar_ubicacion;
        SwitchPreference switch_editar_min_max;
        SwitchPreference switch_editar_dep_cat;
        String user = "";
        String pass = "";
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            switch_can_edit = findPreference("editar_existencias");
            switch_mostrar_precio_compra = findPreference("mostrar_precio_compra");
            switch_editar_ubicacion = findPreference("editar_ubicacion");
            switch_editar_min_max = findPreference("editar_min_max");
            switch_editar_dep_cat = findPreference("editar_dep_cat");

            EditTextPreference server_ip = findPreference("server_ip");
            server_ip.setOnPreferenceChangeListener( (preference, newValue) -> {
                server_ip.setSummary(newValue.toString());
                return true;
            });

            EditTextPreference user_pass = findPreference("user_pass");
            user_pass.setOnPreferenceChangeListener( (preference, newValue) -> {
                String param = newValue.toString();
                user = param.substring(0, param.indexOf(":"));
                pass = param.substring(param.indexOf(":")+1,param.length());

                user_pass.setSummary("Accediendo ... ");

                CheckLogin checkLogin = new CheckLogin();
                checkLogin.execute(this);

                return false;
            });


            /// Valores por defecto
            SharedPreferences ajustes = PreferenceManager.getDefaultSharedPreferences(requireContext());
            server_ip.setSummary(ajustes.getString("server_ip", getString(R.string.ip_defecto)));
            user_pass.setSummary(ajustes.getString("user_pass", getString(R.string.usuario_defecto)));
            if(ajustes.getBoolean("editar_existencias",false) ){
                switch_can_edit.setEnabled(true);
                switch_mostrar_precio_compra.setEnabled(true);
                switch_editar_ubicacion.setEnabled(true);
                switch_editar_min_max.setEnabled(true);
                switch_editar_dep_cat.setEnabled(true);
            }
        }
    }
}