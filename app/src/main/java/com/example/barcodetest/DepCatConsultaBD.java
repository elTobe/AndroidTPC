package com.example.barcodetest;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class DepCatConsultaBD extends AsyncTask<MainActivity, Void, ResultSet> {
    MainActivity main;

    @Override
    protected ResultSet doInBackground(MainActivity... mainActivities) {
        main = mainActivities[0];
        try {

            if (!main.consulta.trim().isEmpty()) {
                SharedPreferences ajustes = PreferenceManager.getDefaultSharedPreferences(main);
                String URL = "jdbc:mysql://"+ ajustes.getString("server_ip",main.getString(R.string.ip_defecto)) +"/sicar";
                String USER = "consultas";
                String PASSWORD = "123456";
                DriverManager.setLoginTimeout(10);
                Connection conexionBD = DriverManager.getConnection(URL, USER, PASSWORD);

                String CONSULTA = "SELECT categoria.nombre AS catnombre,departamento.nombre AS depnombre FROM categoria LEFT JOIN departamento ON categoria.dep_id=departamento.dep_id WHERE categoria.status!=-1 AND departamento.status!=-1";
                PreparedStatement consultaPreparada = conexionBD.prepareStatement(CONSULTA);
                ResultSet resultado = consultaPreparada.executeQuery();
                return resultado;
            }
            return null;

        } catch (CommunicationsException e){
            Log.e("aDebug", "Error de conexion : " + e.getMessage());
        }
        catch (Exception e) {
            Log.e("aDebug", e.getMessage() );
        }
        return null;
    }

    @Override
    protected void onPostExecute(ResultSet resultado) {
        if(resultado != null){
            try{
                if(resultado.next()) {
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) main.search_spinner_categoria.getAdapter();
                    do{
                        String dep_cat = resultado.getString("depnombre") + " | " + resultado.getString("catnombre");
                        if( !dep_cat.equals(main.label_dep_cat.getText().toString().replace("\n"," | ")) ){
                            adapter.add(dep_cat);
                        }
                    }while( resultado.next() );
                    adapter.notifyDataSetChanged();
                }
            }catch (Exception e) {
                Log.e("aDebug", e.getMessage() );
            }
        }
        main.actualizarInterfaz();
    }

}
