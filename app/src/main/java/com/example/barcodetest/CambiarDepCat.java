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
import java.sql.ResultSet;

public class CambiarDepCat extends AsyncTask<MainActivity, Void, Boolean> {

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

                String departamento = main.search_spinner_categoria.getSelectedItem().toString().split("\\|")[0].trim();
                String categoria = main.search_spinner_categoria.getSelectedItem().toString().split("\\|")[1].trim();

                Log.d("aDebug", "categoria : " + categoria);
                Log.d("aDebug", "departamento : " + departamento);

                String CONSULTA = "SELECT cat_id FROM categoria LEFT JOIN departamento ON categoria.dep_id=departamento.dep_id WHERE categoria.nombre=? AND departamento.nombre=?";
                PreparedStatement consultaPreparada = conexionBD.prepareStatement(CONSULTA);
                consultaPreparada.setString(1,categoria);
                consultaPreparada.setString(2,departamento);
                ResultSet r1 = consultaPreparada.executeQuery();

                if( !r1.next() ){
                    return false;
                }

                String CONSULTA2 = "UPDATE articulo SET cat_id=? WHERE (clave=? OR claveAlterna=?)";
                PreparedStatement consultaPreparada2 = conexionBD.prepareStatement(CONSULTA2);
                consultaPreparada2.setString(1,r1.getString("cat_id"));
                consultaPreparada2.setString(2,main.consulta);
                consultaPreparada2.setString(3,main.consulta);
                int rows = consultaPreparada2.executeUpdate();

                Log.d("aDebug", "Filas afectadas por UPDATE : " + rows);
                if(rows>0){
                    return true;
                }else {
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
            String dep_cat = main.search_spinner_categoria.getSelectedItem().toString();
            main.label_dep_cat.setText( dep_cat.replace(" | ","\n") );
            Toast toast = Toast.makeText(main, "Se ha actualizado la categoría y departamento correctamente",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else{
            Toast toast = Toast.makeText(main, "Verifique los datos. No se realizó ningun cambio",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
