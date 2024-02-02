package com.example.barcodetest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

public class MainActivity extends AppCompatActivity {
    WebView loading;
    ImageView imagen_producto;
    SearchableSpinner search_spinner_categoria;

    Button boton_escanear;
    Button boton_cambiar_existencias;
    Button boton_cambiar_ubicacion;
    Button boton_cambiar_minimo;
    Button boton_cambiar_maximo;
    Button boton_cambiar_dep_cat;

    TextView label_clave;
    TextView label_clave_alterna;
    TextView label_precio;
    TextView label_descripcion;
    TextView label_existencias;
    TextView label_precio_etiqueta;
    TextView label_num_piezas;
    TextView label_precio_compra;
    TextView label_dep_cat;
    TextView label_ubicacion;
    TextView label_minimo;
    TextView label_maximo;

    EditText input_existencias;
    EditText input_clave;
    EditText input_ubicacion;
    EditText input_minimo;
    EditText input_maximo;

    LinearLayout layout_precio_distinto;
    LinearLayout layout_precio_compra;
    LinearLayout layout_clave;
    LinearLayout layout_clave_alterna;
    LinearLayout layout_paquetes;
    LinearLayout layout_existencias;
    LinearLayout layout_editar_existencias;
    LinearLayout layout_editar_ubicacion;
    LinearLayout layout_imagen;
    LinearLayout layout_ubicacion;
    LinearLayout layout_dep_cat;
    LinearLayout layout_minimo;
    LinearLayout layout_maximo;
    LinearLayout layout_editar_maximo;
    LinearLayout layout_editar_minimo;
    LinearLayout layout_editar_dep_cat;

    String consulta = "";
    String ubicacion = "";
    Float nueva_existencia = null;
    Float nuevo_min = null;
    Float nuevo_max = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = findViewById(R.id.loading);
        imagen_producto = findViewById(R.id.imagen_producto);
        search_spinner_categoria = findViewById(R.id.search_spinner_categoria);

        boton_escanear = findViewById(R.id.boton_escanear);
        boton_cambiar_existencias = findViewById(R.id.boton_cambiar_existencias);
        boton_cambiar_ubicacion = findViewById(R.id.boton_cambiar_ubicacion);
        boton_cambiar_minimo = findViewById(R.id.boton_cambiar_minimo);
        boton_cambiar_maximo = findViewById(R.id.boton_cambiar_maximo);
        boton_cambiar_dep_cat = findViewById(R.id.boton_cambiar_dep_cat);

        label_clave = findViewById(R.id.label_clave);
        label_clave_alterna = findViewById(R.id.label_clave_alterna);
        label_descripcion = findViewById(R.id.label_descripcion);
        label_precio = findViewById(R.id.label_precio);
        label_precio_etiqueta = findViewById(R.id.label_precio_etiqueta);
        label_num_piezas = findViewById(R.id.label_num_piezas);
        label_existencias = findViewById(R.id.label_existencias);
        label_precio_compra = findViewById(R.id.label_precio_compra);
        label_dep_cat = findViewById(R.id.label_dep_cat);
        label_ubicacion = findViewById(R.id.label_ubicacion);
        label_minimo = findViewById(R.id.label_minimo);
        label_maximo = findViewById(R.id.label_maximo);

        input_existencias = findViewById(R.id.input_existencias);
        input_clave = findViewById(R.id.input_clave);
        input_ubicacion = findViewById(R.id.input_ubicacion);
        input_minimo = findViewById(R.id.input_minimo);
        input_maximo = findViewById(R.id.input_maximo);

        layout_precio_distinto = findViewById(R.id.layout_precio_distinto);
        layout_clave = findViewById(R.id.layout_clave);
        layout_precio_compra = findViewById(R.id.layout_precio_compra);
        layout_clave_alterna = findViewById(R.id.layout_clave_alterna);
        layout_paquetes = findViewById(R.id.layout_paquetes);
        layout_existencias = findViewById(R.id.layout_existencias);
        layout_editar_existencias = findViewById(R.id.layout_editar_existencia);
        layout_imagen = findViewById(R.id.layout_imagen);
        layout_dep_cat = findViewById(R.id.layout_dep_cat);
        layout_editar_ubicacion = findViewById(R.id.layout_editar_ubicacion);
        layout_ubicacion = findViewById(R.id.layout_ubicacion);
        layout_minimo = findViewById(R.id.layout_minimo);
        layout_maximo = findViewById(R.id.layout_maximo);
        layout_editar_minimo = findViewById(R.id.layout_editar_minimo);
        layout_editar_maximo = findViewById(R.id.layout_editar_maximo);
        layout_editar_dep_cat = findViewById(R.id.layout_editar_dep_cat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search_spinner_categoria.setTitle("Seleccion de categoria");
        search_spinner_categoria.setPositiveButton("OK");

        layout_precio_distinto.setVisibility(View.GONE);

        loading.getSettings().setJavaScriptEnabled(true);
        loading.getSettings().setAllowFileAccess(true);
        loading.setWebViewClient(new WebViewClient());
        loading.loadUrl("file:///android_asset/loading_page.html");
        loading.setVisibility(View.GONE);

        boton_escanear.setOnClickListener(view -> {
            on_boton_escanear_pressed();
        });

        boton_cambiar_existencias.setOnClickListener(view -> {
            on_boton_cambiar_existencias_pressed(input_existencias.getText().toString());
        });

        boton_cambiar_maximo.setOnClickListener(view -> {
            on_boton_cambiar_maximo_pressed(input_maximo.getText().toString());
        });

        boton_cambiar_minimo.setOnClickListener(view -> {
            on_boton_cambiar_minimo_pressed(input_minimo.getText().toString());
        });

        boton_cambiar_ubicacion.setOnClickListener(view -> {
            on_boton_cambiar_ubicacion_pressed(input_ubicacion.getText().toString());
        });

        boton_cambiar_dep_cat.setOnClickListener(view -> {
            on_boton_cambiar_dep_cat_pressed();
        });

        input_existencias.setOnEditorActionListener((textView, i, keyEvent) -> {
            on_boton_cambiar_existencias_pressed(textView.getText().toString());
            return true;
        });

        input_minimo.setOnEditorActionListener((textView, i, keyEvent) -> {
            on_boton_cambiar_minimo_pressed(textView.getText().toString());
            return true;
        });

        input_maximo.setOnEditorActionListener((textView, i, keyEvent) -> {
            on_boton_cambiar_maximo_pressed(textView.getText().toString());
            return true;
        });

        input_ubicacion.setOnEditorActionListener((textView, i, keyEvent) -> {
            on_boton_cambiar_ubicacion_pressed(textView.getText().toString());
            return true;
        });

        input_clave.setOnEditorActionListener((textView, i, keyEvent) -> {
            on_input_clave_pressed(textView.getText().toString());
            return true;
        });

        actualizarInterfaz();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void on_boton_escanear_pressed(){
        ScanOptions opciones = new ScanOptions();
        opciones.setPrompt("Vol+ Activar linterna\nVol- Apagar linterna");
        opciones.setBeepEnabled(false);
        opciones.setCaptureActivity(CapturaCodigoBarras.class);
        lanzador_captura_codebar.launch(opciones);
    }

    public void on_boton_cambiar_existencias_pressed(String texto){
        if (!texto.trim().isEmpty()) {
            try {
                nueva_existencia = Float.parseFloat(texto);
                CambiarExistencias update = new CambiarExistencias();
                update.execute(this);
                loading.setVisibility(View.VISIBLE);
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (NumberFormatException e){
                Toast toast = Toast.makeText(this, "Escriba una cantidad valida",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Log.e("aDebug", e.getMessage());
            } catch(Exception e) {
                Log.e("aDebug", e.getMessage());
            }
        }else{
            Toast toast = Toast.makeText(this, "Campo vacio",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    public void on_boton_cambiar_minimo_pressed(String texto){
        if (!texto.trim().isEmpty()) {
            try {
                nuevo_min = Float.parseFloat(texto);
                CambiarMinimo update = new CambiarMinimo();
                update.execute(this);
                loading.setVisibility(View.VISIBLE);
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (NumberFormatException e){
                Toast toast = Toast.makeText(this, "Escriba una cantidad valida",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Log.e("aDebug", e.getMessage());
            } catch(Exception e) {
                Log.e("aDebug", e.getMessage());
            }
        }else{
            Toast toast = Toast.makeText(this, "Campo vacio",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    public void on_boton_cambiar_maximo_pressed(String texto){
        if (!texto.trim().isEmpty()) {
            try {
                nuevo_max = Float.parseFloat(texto);
                CambiarMaximo update = new CambiarMaximo();
                update.execute(this);
                loading.setVisibility(View.VISIBLE);
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch (NumberFormatException e){
                Toast toast = Toast.makeText(this, "Escriba una cantidad valida",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Log.e("aDebug", e.getMessage());
            } catch(Exception e) {
                Log.e("aDebug", e.getMessage());
            }
        }else{
            Toast toast = Toast.makeText(this, "Campo vacio",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    public void on_boton_cambiar_ubicacion_pressed(String texto){
        if (!texto.trim().isEmpty()) {
            try {
                ubicacion = texto;
                CambiarUbicacion update = new CambiarUbicacion();
                update.execute(this);
                loading.setVisibility(View.VISIBLE);
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            } catch(Exception e) {
                Log.e("aDebug", e.getMessage());
            }
        }else{
            Toast toast = Toast.makeText(this, "Campo vacio",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    public void on_boton_cambiar_dep_cat_pressed(){
        String texto_actual = search_spinner_categoria.getSelectedItem().toString().trim();
        if ( !texto_actual.isEmpty() && !texto_actual.equals("N/A") ) {
            try {
                CambiarDepCat update = new CambiarDepCat();
                update.execute(this);
                loading.setVisibility(View.VISIBLE);
            } catch(Exception e) {
                Log.e("aDebug", e.getMessage());
            }
        }else{
            Toast toast = Toast.makeText(this, "Departamento o Categoria no valido",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    public void on_input_clave_pressed(String texto){
        if(!texto.trim().isEmpty()){
            loading.setVisibility(View.VISIBLE);
            consulta = texto.trim();
            try{
                ConsultaBD consulta = new ConsultaBD();
                consulta.execute(this);
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }catch(Exception e){
                Log.e("aDebug", e.getMessage());
            }
        }else{
            Toast toast = Toast.makeText(this, "Campo vacio",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    ActivityResultLauncher<ScanOptions> lanzador_captura_codebar = registerForActivityResult(new ScanContract(), codebar -> {
        loading.setVisibility(View.VISIBLE);
        if(codebar.getContents() != null){
            consulta = codebar.getContents();
            input_clave.setText(codebar.getContents());
            try{
                ConsultaBD consulta = new ConsultaBD();
                consulta.execute(this);
            }catch(Exception e){
                Log.e("aDebug", e.getMessage());
            }
        }else{
            loading.setVisibility(View.GONE);
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        actualizarInterfaz();
    }

    public void actualizarInterfaz(){
        SharedPreferences ajustes = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean editar_existencias = ajustes.getBoolean("editar_existencias", false);
        Boolean editar_dep_cat = ajustes.getBoolean("editar_dep_cat", false);
        Boolean editar_min_max = ajustes.getBoolean("editar_min_max", false);
        Boolean editar_ubicacion = ajustes.getBoolean("editar_ubicacion", false);
        Boolean mostrar_precio_compra = ajustes.getBoolean("mostrar_precio_compra", false);
        Boolean mostrar_clave = ajustes.getBoolean("mostrar_clave", true);
        Boolean mostrar_clave_alterna = ajustes.getBoolean("mostrar_clave_alterna", true);
        Boolean mostrar_imagen = ajustes.getBoolean("mostrar_imagen", true);
        Boolean mostrar_paquetes = ajustes.getBoolean("mostrar_paquetes", false);
        Boolean mostrar_existencias = ajustes.getBoolean("mostrar_existencias", false);
        Boolean mostrar_dep_cat = ajustes.getBoolean("mostrar_dep_cat", false);
        Boolean mostrar_ubicacion = ajustes.getBoolean("mostrar_ubicacion", true);
        Boolean mostrar_min_max = ajustes.getBoolean("mostrar_min_max", false);

        layout_editar_existencias.setVisibility(    editar_existencias      ? View.VISIBLE : View.GONE);
        layout_editar_dep_cat.setVisibility(        editar_dep_cat          ? View.VISIBLE : View.GONE);
        layout_editar_minimo.setVisibility(         editar_min_max          ? View.VISIBLE : View.GONE);
        layout_editar_maximo.setVisibility(         editar_min_max          ? View.VISIBLE : View.GONE);
        layout_editar_ubicacion.setVisibility(      editar_ubicacion        ? View.VISIBLE : View.GONE);
        layout_precio_compra.setVisibility(         mostrar_precio_compra   ? View.VISIBLE : View.GONE);
        layout_clave.setVisibility(                 mostrar_clave           ? View.VISIBLE : View.GONE);
        layout_clave_alterna.setVisibility(         mostrar_clave_alterna   ? View.VISIBLE : View.GONE);
        layout_imagen.setVisibility(                mostrar_imagen          ? View.VISIBLE : View.GONE);
        layout_paquetes.setVisibility(              mostrar_paquetes        ? View.VISIBLE : View.GONE);
        layout_existencias.setVisibility(           mostrar_existencias     ? View.VISIBLE : View.GONE);
        layout_dep_cat.setVisibility(               mostrar_dep_cat         ? View.VISIBLE : View.GONE);
        layout_ubicacion.setVisibility(             mostrar_ubicacion       ? View.VISIBLE : View.GONE);
        layout_minimo.setVisibility(                mostrar_min_max         ? View.VISIBLE : View.GONE);
        layout_maximo.setVisibility(                mostrar_min_max         ? View.VISIBLE : View.GONE);
    }
}