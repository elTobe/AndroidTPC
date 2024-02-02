package com.example.barcodetest;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CambiarUbicacion extends AsyncTask<MainActivity, Void, Boolean> {

    MainActivity main;

    @Override
    protected Boolean doInBackground(MainActivity... mainActivities) {
        main = mainActivities[0];
        if(!main.consulta.trim().isEmpty()){
            try {
                SharedPreferences ajustes = PreferenceManager.getDefaultSharedPreferences(main);
                String URL = "jdbc:mysql://"+ ajustes.getString("server_ip",main.getString(R.string.ip_defecto)) +"/sicar";
                String USER = "root";
                String PASSWORD = "super";
                DriverManager.setLoginTimeout(10);
                Connection conexionBD = DriverManager.getConnection(URL, USER, PASSWORD);

                String CONSULTA = "UPDATE articulo SET localizacion=? WHERE (clave=? OR claveAlterna=?)";
                PreparedStatement consultaPreparada = conexionBD.prepareStatement(CONSULTA);
                consultaPreparada.setString(1,main.ubicacion);
                consultaPreparada.setString(2,main.consulta);
                consultaPreparada.setString(3,main.consulta);
                int rows = consultaPreparada.executeUpdate();

                Log.d("aDebug", "Filas afectadas por UPDATE : " + rows);
                if(rows>0){
                    return true;
                }else{
                    return false;
                }

            } catch (CommunicationsException e){
                Log.e("aDebug", "Error de conexion : " + e.getMessage());
            }
            catch (Exception e) {
                Log.e("aDebug", e.getMessage() );
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean cambiado) {
        main.loading.setVisibility(View.GONE);
        if (cambiado){
            main.label_ubicacion.setText(main.input_ubicacion.getText().toString());
            Toast toast = Toast.makeText(main, "Se ha actualizado la ubicacion correctamente",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else{
            Toast toast = Toast.makeText(main, "Verifique los datos. No se realizó ningun cambio",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
