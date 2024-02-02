package com.example.barcodetest;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.preference.PreferenceManager;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ConsultaBD extends AsyncTask<MainActivity, Void, ResultSet> {
    MainActivity main;
    float precioForzado = 0;
    float numUnidades = 0;

    @Override
    protected ResultSet doInBackground(MainActivity... mainActivities) {
        main = mainActivities[0];
        try {
            if (main.consulta.contains("/")) {
                String precioForzadoString = main.consulta.substring(0, main.consulta.indexOf("/"));
                precioForzado = Float.parseFloat(precioForzadoString);
                main.consulta = main.consulta.substring(main.consulta.indexOf("/") + 1, main.consulta.length());
            } else {
                precioForzado = 0;
            }

            if (main.consulta.contains("*")) {
                String unidadesString = main.consulta.substring(0, main.consulta.indexOf("*"));
                numUnidades = Float.parseFloat(unidadesString);
                main.consulta = main.consulta.substring(main.consulta.indexOf("*") + 1, main.consulta.length());
            } else {
                numUnidades = 0;
            }

            if (!main.consulta.trim().isEmpty()) {
                SharedPreferences ajustes = PreferenceManager.getDefaultSharedPreferences(main);
                String URL = "jdbc:mysql://"+ ajustes.getString("server_ip",main.getString(R.string.ip_defecto)) +"/sicar";
                String USER = "consultas";
                String PASSWORD = "123456";
                DriverManager.setLoginTimeout(10);
                Connection conexionBD = DriverManager.getConnection(URL, USER, PASSWORD);

                String CONSULTA = "SELECT articulo.*,articuloimagen.*,imagen.imagen AS img,categoria.nombre AS catnombre,departamento.nombre AS depnombre FROM articulo LEFT JOIN articuloimagen ON articulo.art_id=articuloimagen.art_id LEFT JOIN imagen ON articuloimagen.img_id=imagen.img_id LEFT JOIN categoria ON categoria.cat_id=articulo.cat_id LEFT JOIN departamento ON departamento.dep_id=categoria.dep_id WHERE (clave=? OR claveAlterna=?) AND articulo.status!=-1 ";
                PreparedStatement consultaPreparada = conexionBD.prepareStatement(CONSULTA);
                consultaPreparada.setString(1, main.consulta);
                consultaPreparada.setString(2, main.consulta);

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
        main.loading.setVisibility(View.GONE);
        if(resultado != null){
            try{
                if(resultado.next()){

                    NumberFormat format2d = new DecimalFormat("#.00");
                    String precio = resultado.getString("precio1");
                    main.label_precio.setText( "$" + format2d.format( Float.parseFloat(precio) ));

                    NumberFormat format3d = new DecimalFormat("#.###");
                    if( resultado.getString("tipo").equals("2") ){
                        main.input_existencias.setText( "0" );
                        main.input_maximo.setText("0");
                        main.input_minimo.setText("0");
                        main.label_existencias.setText( "N/A (Paq)" );
                        main.label_maximo.setText( "N/A (Paq)" );
                        main.label_minimo.setText( "N/A (Paq)" );
                    }else{
                        String existencias = resultado.getString("existencia");
                        main.nueva_existencia = Float.parseFloat(existencias);
                        main.input_existencias.setText(format3d.format( Float.parseFloat(existencias) ));
                        main.label_existencias.setText( format3d.format( Float.parseFloat(existencias) ) );

                        String minimos = resultado.getString("invMin");
                        main.nuevo_min = Float.parseFloat(minimos);
                        main.input_minimo.setText(format3d.format( Float.parseFloat(minimos) ));
                        main.label_minimo.setText( format3d.format( Float.parseFloat(minimos) ) );

                        String maximos = resultado.getString("invMax");
                        main.nuevo_max = Float.parseFloat(maximos);
                        main.input_maximo.setText(format3d.format( Float.parseFloat(maximos) ));
                        main.label_maximo.setText( format3d.format( Float.parseFloat(maximos) ) );
                    }

                    if (precioForzado != 0){

                        main.layout_precio_distinto.setVisibility(View.VISIBLE);
                        String precio_etiqueta = format2d.format( precioForzado );
                        main.label_precio_etiqueta.setText( "$" + precio_etiqueta );
                        main.label_num_piezas.setText("");

                    }else if (numUnidades != 0) {

                        main.layout_precio_distinto.setVisibility(View.VISIBLE);
                        String precio_piezas = format2d.format( numUnidades * Float.parseFloat(resultado.getString("precio1")) );
                        String num_piezas = format3d.format( numUnidades );
                        main.label_precio_etiqueta.setText( "$" + precio_piezas );
                        main.label_num_piezas.setText(" (" + num_piezas + " pz)");

                    }else{

                        main.layout_precio_distinto.setVisibility(View.GONE);

                    }

                    String dep_cat = resultado.getString("depnombre") + " | " + resultado.getString("catnombre");
                    main.label_dep_cat.setText( dep_cat.replace(" | ","\n") );

                    List<String> items = new ArrayList<>();
                    items.add(dep_cat);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(main, android.R.layout.simple_spinner_item, items);
                    main.search_spinner_categoria.setAdapter(adapter);
                    DepCatConsultaBD depCatConsultaBD = new DepCatConsultaBD();
                    depCatConsultaBD.execute(main);

                    main.label_clave.setText(resultado.getString("clave"));
                    main.label_clave_alterna.setText(resultado.getString("claveAlterna"));
                    main.label_descripcion.setText(resultado.getString("descripcion"));
                    main.label_precio_compra.setText( "$" + format2d.format( Float.parseFloat( resultado.getString("precioCompra") ) ) );
                    main.label_ubicacion.setText(resultado.getString("localizacion"));
                    main.input_ubicacion.setText(resultado.getString("localizacion"));

                    Blob blob = resultado.getBlob("img");
                    if (blob != null) {
                        byte[] blobData = blob.getBytes(1, (int) blob.length());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(blobData, 0, blobData.length);
                        main.imagen_producto.setImageBitmap(bitmap);
                    }else{
                        main.imagen_producto.setImageResource(R.drawable.not_image);
                    }
                }else{
                    main.nueva_existencia = null;
                    main.nuevo_min = null;
                    main.nuevo_max = null;
                    main.label_descripcion.setText("NO ENCONTRADO");
                    main.label_clave.setText("N/A");
                    main.label_clave_alterna.setText("N/A");
                    main.label_precio.setText("N/A");
                    main.label_ubicacion.setText("N/A");
                    main.input_existencias.setText("");
                    main.input_minimo.setText("");
                    main.input_maximo.setText("");
                    main.input_ubicacion.setText("");
                    main.label_precio_compra.setText("N/A");
                    main.imagen_producto.setImageResource(R.drawable.not_image);
                    main.label_dep_cat.setText("N/A");
                    main.label_existencias.setText("N/A");
                    main.label_minimo.setText("N/A");
                    main.label_maximo.setText("N/A");

                    List<String> items = new ArrayList<>();
                    items.add("N/A");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(main, android.R.layout.simple_spinner_item, items);
                    main.search_spinner_categoria.setAdapter(adapter);

                }
            }catch (Exception e) {
                Log.e("aDebug", e.getMessage());
            }
        }else{
            main.nueva_existencia = null;
            main.nuevo_min = null;
            main.nuevo_max = null;
            new AlertDialog.Builder(main)
                    .setMessage("No se conecto a la Base de Datos\nRevise su conexion o la IP del servidor")
                    .setPositiveButton("Aceptar", null)
                    .show();
        }
        LikeConsultaBD likeConsultaBD = new LikeConsultaBD();
        likeConsultaBD.execute(main);
    }

}
