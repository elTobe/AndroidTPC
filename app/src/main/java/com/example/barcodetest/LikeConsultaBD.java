package com.example.barcodetest;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
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

public class LikeConsultaBD extends AsyncTask<MainActivity, Void, ResultSet> {
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

                String[] separadas = main.consulta.split(" ");
                String CONSULTA = "SELECT clave,descripcion,precio1 FROM articulo WHERE";
                for(int i = 0; i < separadas.length; i++){
                    CONSULTA = CONSULTA + " (clave LIKE ? OR claveAlterna LIKE ? OR descripcion LIKE ?) AND";
                }
                CONSULTA = CONSULTA +  " status!=-1";

                PreparedStatement consultaPreparada = conexionBD.prepareStatement(CONSULTA);

                for(int i = 0; i < separadas.length; i++){
                    consultaPreparada.setString(i*3 + 1, "%" + separadas[i] + "%");
                    consultaPreparada.setString(i*3 + 2, "%" + separadas[i] + "%");
                    consultaPreparada.setString(i*3 + 3, "%" + separadas[i] + "%");
                }

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
        TableLayout tabla = main.findViewById(R.id.tabla_similares);
        tabla.removeAllViews();
        if(resultado != null){
            try{
                if(resultado.next()) {
                    int i = 0;
                    do{
                        TableRow fila = new TableRow(main);

                        TextView clave = new TextView(main);
                        clave.setText(resultado.getString("clave"));
                        clave.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                        fila.addView(clave);

                        TextView descripcion = new TextView(main);
                        descripcion.setText(resultado.getString("descripcion"));
                        descripcion.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
                        fila.addView(descripcion);

                        NumberFormat format2d = new DecimalFormat("#.00");
                        String precio_s = resultado.getString("precio1");
                        TextView precio = new TextView(main);
                        precio.setText("$" + format2d.format( Float.parseFloat(precio_s) ));
                        precio.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                        fila.addView(precio);

                        fila.setOnClickListener(view -> {
                            TextView clave_texview = (TextView) fila.getChildAt(0);
                            String texto = clave_texview.getText().toString();
                            main.input_clave.setText(texto);
                            main.on_input_clave_pressed(texto);
                        });

                        tabla.addView(fila);
                        i++;
                    }while( resultado.next() && i < 50 );
                }
            }catch (Exception e) {
                Log.e("aDebug", e.getMessage() );
            }
        }
        main.actualizarInterfaz();
    }

}
