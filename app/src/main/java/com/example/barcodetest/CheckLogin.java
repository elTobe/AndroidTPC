package com.example.barcodetest;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckLogin extends AsyncTask<SettingsActivity.SettingsFragment, Void, String> {

    Boolean can_edit = false;
    SettingsActivity.SettingsFragment ajustes_screen;

    @Override
    protected String doInBackground(SettingsActivity.SettingsFragment... settingsActivities) {
        ajustes_screen = settingsActivities[0];

        try{
            SharedPreferences ajustes = PreferenceManager.getDefaultSharedPreferences(ajustes_screen.requireContext());
            String URL = "jdbc:mysql://"+ ajustes.getString("server_ip",ajustes_screen.getString(R.string.ip_defecto)) +"/sicar";
            String USER = "consultas";
            String PASSWORD = "123456";
            DriverManager.setLoginTimeout(10);
            Connection conexionBD = DriverManager.getConnection(URL, USER, PASSWORD);

            String CONSULTA = "SELECT * FROM usuario LEFT JOIN permiso ON usuario.rol_id=permiso.rol_id LEFT JOIN accion ON permiso.acc_id=accion.acc_id where usuario.usuario=? and accion.acc_id=86";
            PreparedStatement consultaPreparada = conexionBD.prepareStatement(CONSULTA);
            consultaPreparada.setString(1, ajustes_screen.user);
            ResultSet resultado = consultaPreparada.executeQuery();

            if (resultado.next()){
                if (resultado.getString("permiso").equals("1")){
                    can_edit = true;
                }
                byte[] bytes = ajustes_screen.pass.getBytes();
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] digest = messageDigest.digest(bytes);
                BigInteger bigInt = new BigInteger(1, digest);
                String hashText = bigInt.toString(16);
                while (hashText.length() < 32) {
                    hashText = "0" + hashText;
                }
                if (hashText.equals(resultado.getString("password"))){
                    String pass_asteriscos = "";
                    for (int i = 0; i < ajustes_screen.pass.length(); i++ ){
                        pass_asteriscos = pass_asteriscos + "*";
                    }
                    return(ajustes_screen.user + ":" + pass_asteriscos);
                }
            }
        }catch (Exception e){
            Log.e("aDebug",e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        EditTextPreference usuario_pass = ajustes_screen.findPreference("user_pass");
        SharedPreferences ajustes = PreferenceManager.getDefaultSharedPreferences(ajustes_screen.requireContext());
        SharedPreferences.Editor editor = ajustes.edit();

        if (result != null){
            editor.putString("user_pass", result);
            usuario_pass.setSummary(result);
            Toast toast = Toast.makeText(ajustes_screen.requireContext(), "Sesion iniciada", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else{
            usuario_pass.setSummary(ajustes.getString("user_pass","ventas:******"));
            Toast toast = Toast.makeText(ajustes_screen.requireContext(), "No se pudo iniciar sesion", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

        if(can_edit){
            ajustes_screen.switch_can_edit.setEnabled(true);
            ajustes_screen.switch_can_edit.setChecked(true);
            ajustes_screen.switch_editar_ubicacion.setEnabled(true);
            ajustes_screen.switch_editar_ubicacion.setChecked(true);
            ajustes_screen.switch_mostrar_precio_compra.setEnabled(true);
            ajustes_screen.switch_mostrar_precio_compra.setChecked(true);
            ajustes_screen.switch_editar_dep_cat.setEnabled(true);
            ajustes_screen.switch_editar_dep_cat.setChecked(true);
            ajustes_screen.switch_editar_min_max.setEnabled(true);
            ajustes_screen.switch_editar_min_max.setChecked(true);
            editor.putBoolean("editar_existencias",true);
            editor.putBoolean("editar_ubicacion",true);
            editor.putBoolean("mostrar_precio_compra", true);
            editor.putBoolean("editar_dep_cat", true);
            editor.putBoolean("editar_min_max", true);

        }else{
            ajustes_screen.switch_can_edit.setEnabled(false);
            ajustes_screen.switch_can_edit.setChecked(false);
            ajustes_screen.switch_editar_ubicacion.setEnabled(false);
            ajustes_screen.switch_editar_ubicacion.setChecked(false);
            ajustes_screen.switch_mostrar_precio_compra.setEnabled(false);
            ajustes_screen.switch_mostrar_precio_compra.setChecked(false);
            ajustes_screen.switch_editar_dep_cat.setEnabled(false);
            ajustes_screen.switch_editar_dep_cat.setChecked(false);
            ajustes_screen.switch_editar_min_max.setEnabled(false);
            ajustes_screen.switch_editar_min_max.setChecked(false);
            editor.putBoolean("editar_existencias",false);
            editor.putBoolean("editar_ubicacion",false);
            editor.putBoolean("mostrar_precio_compra", false);
            editor.putBoolean("editar_dep_cat", false);
            editor.putBoolean("editar_min_max", false);
        }

        editor.apply();
    }
}
