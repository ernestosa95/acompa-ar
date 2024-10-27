package acompanar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.EfectoresSearchAdapter;
import acompanar.ManagementModule.StorageManagement.SQLitePpal;
import acompanar.BasicObjets.PollsterClass;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.example.acompanar.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

// Descripcion de la Activity:

//--------------------------------------------------------------------------------------------------
public class MenuInicio extends AppCompatActivity {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 123;

    // Widgets
    Button empezar;
    Spinner SPProvincias;
    EditText Nombre, Apellido;
    PollsterClass encuestador;
    Archivos archivos;
    SQLitePpal admin;
    BDData adminBDData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicio);

        // Boton para comenzar a cargar datos
        empezar = findViewById(R.id.EMPEZAR);
        encuestador = new PollsterClass(getApplicationContext());
        archivos = new Archivos(getApplicationContext());
        //archivos.agregarCabecera();
        Nombre = findViewById(R.id.NOMBREINGRESADO);
        Apellido = findViewById(R.id.APELLIDOINGRESADO);
        // Eliminar el action bar
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.hide();

        // Evitar la rotacion
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        //Button btnUrl = findViewById(R.id.BTNURL);
        String urlString = "https://drive.google.com/drive/folders/1-N3vJG2IOSBu0Pjnl8s29bPoyPkV1W_s?usp=sharing";
        /*btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri link = Uri.parse(urlString);
                Intent i = new Intent(Intent.ACTION_VIEW, link);
                startActivity(i);
            }
        });*/

        TextView version = findViewById(R.id.versionTXT);
        version.setText("1.0.0");


        //encuestador.archivos.RecuperarDatosCsv();
        //encuestador.archivos.RecuperarDatosCsvGeneral();
        // Solicito multiples permisos para las diferentes servicios que se utilizan
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.BLUETOOTH_CONNECT},
                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

        admin = new SQLitePpal(getBaseContext(), "DATA_PRINCIPAL", null, 1);
        adminBDData = new BDData(getBaseContext(), "BDData", null, 1);
        try {
            adminBDData.CrearTablaUnificados();
            adminBDData.renameColumns();
        }catch (Exception e){
            Toast.makeText(getBaseContext(), "error 1 " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton updateBD = findViewById(R.id.UPDATEBD);
        updateBD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateBD();
            }
        });

        try {
            UpdateBDEfectores();
        }catch (Exception e){
            Toast.makeText(getBaseContext(), R.string.ocurrio_error + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // PASAR AL MENU PPAL, PRIMERO ELIGIENDO UN USUARIO

    private void UpdateBDEfectores(){
        //Consulto la cabecera de la base de datos actual para actualizarla si corresponde
        //makeText(getBaseContext(), "Col "+admin.CabeceraEfectores(), LENGTH_SHORT).show();
        if(admin.CabeceraEfectores()==2){
            UpBdEfectores upBdEfectores = new UpBdEfectores();
            upBdEfectores.execute();
        }
    }

    public void Ingresar(View view) {
        actualizarBotonera();
        adminBDData.CreateColumnsAedes(getApplicationContext());
        adminBDData.CreateColumnsDomicilio(getApplicationContext());

        if(encuestador.checkUbicacionGPS(getContentResolver())){
            if(encuestador.existe(Nombre.getText().toString(), Apellido.getText().toString())){
                encuestador.activarUsuario(Nombre.getText().toString(), Apellido.getText().toString());
                Intent intent = new Intent(getBaseContext(), MenuMapa.class);
                startActivityForResult(intent, 1);
            }
            else{
                makeText(getBaseContext(), "NO EXISTE UN USUARIO CON ESE NOMBRE Y APELLIDO", LENGTH_SHORT).show();
            }
        }
        else{makeText(getBaseContext(), "UBICACION DEL TELEFONO DESACTIVADA, POR FAVOR ACTIVARLA", LENGTH_SHORT).show();}
    }

    private void UpdateBD() {
        UpBdEfectores upBdEfectores = new UpBdEfectores();
        upBdEfectores.execute();
    }

    //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // PERMITO CREAR UN NUEVO USUARIO
    public void crearUsuario(View view){
        // Creo el Alert dialog con los recursos layout creados para este alert_crear_encuestador
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view_nuevo_usuario = Inflater.inflate(R.layout.alert_crear_usuario, null);
        builder.setView(view_nuevo_usuario);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        PollsterClass encuestador = new PollsterClass(getBaseContext());
        //archivos.agregarCabecera();
        SPProvincias = view_nuevo_usuario.findViewById(R.id.PROVINCIA);
        ArrayAdapter<String> adaptadorProvincias = new ArrayAdapter<>(this, R.layout.spiner_personalizado, encuestador.Provincias());
        SPProvincias.setAdapter(adaptadorProvincias);

        // Creo los edittext para ingresar datos de nombre, apellido, dni
        final EditText NonmbreEncuestador = view_nuevo_usuario.findViewById(R.id.EditCrearEncuestador);
        final EditText ApellidoEncuestador = view_nuevo_usuario.findViewById(R.id.editApellidoEncuestador);
        final EditText DNI = view_nuevo_usuario.findViewById(R.id.editDNI);

        // AUTOCOMPLETE TEXTVIEW DE LOS TRABAJOS
        /*AutoCompleteTextView autoEfector = view_nuevo_usuario.findViewById(R.id.autoEfector);
        List<String> efectores = new ArrayList<String>();
        EfectoresSearchAdapter searchAdapter = new EfectoresSearchAdapter(this, efectores);
        autoEfector.setThreshold(1);
        autoEfector.setAdapter(searchAdapter);*/

        /*Creo y asigno las funciones al boton de crear encuentador, corroboro que ninguno de los
        campos este vacio, desactivo todos los encuestadores y luego inserto en la base de datos el
        nuevo, activandolo, paso siguiente paso la app a la activity MenuMapa*/
        Button crear = view_nuevo_usuario.findViewById(R.id.GUARDARCREARENCUESTADOR);
        crear.setOnClickListener(view1 -> {
                if(DNI.getText().toString().length()!=0) {
                    if (SPProvincias.getSelectedItem().toString().length() != 0) {
                        if(NonmbreEncuestador.getText().toString().length()!=0){
                            if(ApellidoEncuestador.getText().toString().length()!=0){

                                encuestador.crearUsuario(NonmbreEncuestador.getText().toString(),
                                                            ApellidoEncuestador.getText().toString(),
                                                            DNI.getText().toString(),
                                                            SPProvincias.getSelectedItem().toString());
                                //admin.UpdateEncuestador(encuestador.DNI, encuestador.EfectorTrabajo);

                        // Creo en segundo plano las bases de datos correspondientes a este
                        // encuestador
                        BdEfectores bdEfectores = new BdEfectores();
                        bdEfectores.execute();
                        dialog.dismiss();
                            } else {
                                makeText(getBaseContext(), "INGRESE UN APELLIDO", LENGTH_SHORT).show();
                            }
                            } else {
                                makeText(getBaseContext(), "INGRESE UN NOMBRE", LENGTH_SHORT).show();
                            }
                            } else {
                                makeText(getBaseContext(), "INGRESE UNA PROVINCIA", LENGTH_SHORT).show();
                            }
                }else{makeText(getBaseContext(), "INGRESE DNI", LENGTH_SHORT).show();}
        });

        Button close = view_nuevo_usuario.findViewById(R.id.CANCELARCREARENCUESTADOR);
        close.setOnClickListener(view12 -> dialog.dismiss());
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // ESTA ES UNA EXTENSION PARA ACTUALIZAR LAS BASES DE DATOS

    @SuppressLint("StaticFieldLeak")
    private class UpBdEfectores extends AsyncTask<Void, Void, Void> {

        // Creo un progress dialog para mostrar mientras se ejecuta este codigo
        ProgressDialog pd;

        /*Antes de comenzar la ejecucion se inicia el progress dialog con los siguientes atributos*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MenuInicio.this);
            pd.setMessage("Cargando datos, aguarde");
            pd.setCancelable(false);
            pd.show();
        }

        /* Este es el codigo que se ejecuta en segundo plano mientras el usuario ve un cartel de
         * cargando datos*/
        @Override
        protected Void doInBackground(Void... voids) {
            pd.setMessage("Borrando efectores, espere");

            ArrayList<String> provincias = admin.ProvinciasEfectores();
            for (String o: provincias){
                Log.e("provincias crear", o);
            }
            admin.DeleteAllEfectores();

            pd.setMessage("Actualizando campos de la bd");
            admin.AddColumnEfectores();

            pd.setMessage("Creando nueva base de efectores, espere");
            String myData;
            try {
                for (int k=0; k<provincias.size(); k++) {
                    InputStream fis = getResources().openRawResource(R.raw.efectores_er);//new FileInputStream(R.raw.efectores);
                    DataInputStream in = new DataInputStream(fis);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    SQLiteDatabase Bd1 = admin.getWritableDatabase();

                    while ((myData = br.readLine()) != null) {
                        if (myData.split(",")[1].equals(provincias.get(k))) {
                            Log.e("efector 2 crear", myData.split(",")[2]);
                            ContentValues registro = new ContentValues();
                            registro.put("NOMBRE", myData.split(",")[0]);
                            registro.put("PROVINCIA", myData.split(",")[1]);
                            registro.put("NOMBRE_ID", myData.split(",")[2]);
                            registro.put("LOCALIDAD", myData.split(",")[3]);
                            registro.put("DEPARTAMENTO", myData.split(",")[4]);
                            registro.put("CP", myData.split(",")[5]);
                            Bd1.insert("EFECTORES", null, registro);
                        }
                    }
                    Bd1.close();
                    br.close();
                    in.close();
                    fis.close();
                }
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
            }

            return null;
        }
        /* Despues de la ejecucion del codigo en segundo plano debo detener el alert que me indica
         * que se estan cargando los datos*/
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
            }
        }

    }

    // ESTA ES UNA EXTENSION PARA PODER CREAR LAS BASES DE DATOS EN SEGUNDO PLANO

    @SuppressLint("StaticFieldLeak")
    private class BdEfectores extends AsyncTask<Void, Void, Void> {

        // Creo un progress dialog para mostrar mientras se ejecuta este codigo
        ProgressDialog pd;

        /*Antes de comenzar la ejecucion se inicia el progress dialog con los siguientes atributos*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MenuInicio.this);
            pd.setMessage("Cargando datos, aguarde");
            pd.setCancelable(false);
            pd.show();
        }

        /* Este es el codigo que se ejecuta en segundo plano mientras el usuario ve un cartel de
        * cargando datos*/
        @Override
        protected Void doInBackground(Void... voids) {
            /* Se crea una conexion con la base de datos, se corrobora que los datos de la provincia
            * seleccionada por el encuestador no esten cargados y se comienza a leer desde efectores.csv
            * solo eligiendo los efectores correspondientes a esta provincia, posteriomente se los
            * inserta en la base de datos.
            * Esta accion se realiza una vez por provincia*/
            String myData;
            try {
                InputStream fis = getResources().openRawResource(R.raw.efectores_er);//new FileInputStream(R.raw.efectores);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                SQLiteDatabase Bd1 = admin.getWritableDatabase();
                if(SPProvincias!=null){
                if (!admin.ExisteEfectores(SPProvincias.getSelectedItem().toString())) {
                    while ((myData = br.readLine()) != null) {
                            if (myData.split(",")[1].equals(SPProvincias.getSelectedItem().toString())) {
                                Log.e("efector 2 crear", myData.split(",")[2]);
                                ContentValues registro = new ContentValues();
                                registro.put("NOMBRE", myData.split(",")[0]);
                                registro.put("PROVINCIA", myData.split(",")[1]);
                                registro.put("NOMBRE_ID", myData.split(",")[2]);
                                registro.put("LOCALIDAD", myData.split(",")[3]);
                                registro.put("DEPARTAMENTO", myData.split(",")[4]);
                                registro.put("CP", myData.split(",")[5]);
                                Bd1.insert("EFECTORES", null, registro);
                            }
                    }
                    Bd1.close();
                    br.close();
                    in.close();
                    fis.close();

                }}
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
            }

            /* Como la base de datos de los trabajos solo ejecuta una vez desde la instalacion de la
            * app aprovecho y tambien cargo la base de datos de los botones*/
            /* Tambien debo crear la base de datos de los trabajos, esto se hace una vez, jsto despues
            * de instalar la app, para eso corroboro que la misma no este creada, leo los datos desde
            * trabajo.csv y los inserto en la base de datos correspondiente*/
            try{
                InputStream fis = getResources().openRawResource(R.raw.trabajos);//new FileInputStream(R.raw.efectores);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                SQLiteDatabase Bd1 = admin.getWritableDatabase();
                //String myData="";
                if(!admin.ExisteTrabajos()){
                    while ((myData=br.readLine())!=null){
                        ContentValues registro = new ContentValues();
                        registro.put("TRABAJO", myData);
                        Bd1.insert("TRABAJOS", null, registro);
                    }

                    Bd1.close();
                    br.close();
                    in.close();
                    fis.close();
                }
            }
            catch (IOException e){
                Toast.makeText(getBaseContext(), R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
            }

            /* Obtengo el listado de botones de la base de datos si no tiene alguno de los botone
            * que estan listado en en el arraylist lo agrego*/
            SQLiteDatabase Bd1 = admin.getWritableDatabase();
            ArrayList<String> botonesActuales = admin.Botones();

            for(int i=0; i<encuestador.Botones().size(); i++) {
                if (Bd1 == null || !Bd1.isOpen())
                {
                    Bd1 = admin.getWritableDatabase();
                }
                if(botonesActuales.size()!=0){
                if (!botonesActuales.contains(encuestador.Botones().get(i))) {
                    ContentValues botonesValores = new ContentValues();
                    botonesValores.put("BOTON", encuestador.Botones().get(i));
                    botonesValores.put("ACTIVO", false);
                    Bd1.insert("BOTONES", null, botonesValores);
                }}
                else{
                    ContentValues botonesValores = new ContentValues();
                    botonesValores.put("BOTON", encuestador.Botones().get(i));
                    botonesValores.put("ACTIVO", false);
                    Bd1.insert("BOTONES", null, botonesValores);

                }
            }
            Bd1.close();

            return null;
        }

        /* Despues de la ejecucion del codigo en segundo plano debo detener el alert que me indica
        * que se estan cargando los datos*/
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
            }
        }

    }

    // ACTUALIZO BOTONERA
    private void actualizarBotonera(){
        /* Obtengo el listado de botones de la base de datos si no tiene alguno de los botone
         * que estan listado en en el arraylist lo agrego*/
        admin.CrearTablaUnificados();
        SQLiteDatabase Bd1 = admin.getWritableDatabase();
        ArrayList<String> botonesActuales = admin.Botones();

        for(int i=0; i<encuestador.Botones().size(); i++) {
            if (Bd1 == null || !Bd1.isOpen())
            {
                Bd1 = admin.getWritableDatabase();
            }
            if(botonesActuales.size()!=0){
                if (!botonesActuales.contains(encuestador.Botones().get(i))) {
                    ContentValues botonesValores = new ContentValues();
                    botonesValores.put("BOTON", encuestador.Botones().get(i));
                    botonesValores.put("ACTIVO", false);
                    Bd1.insert("BOTONES", null, botonesValores);
                }}
            else{
                ContentValues botonesValores = new ContentValues();
                botonesValores.put("BOTON", encuestador.Botones().get(i));
                botonesValores.put("ACTIVO", false);
                Bd1.insert("BOTONES", null, botonesValores);

            }
        }
        Bd1.close();

        for(int i=0; i<encuestador.Notifications().size();i++){
            admin.InsertNotification(encuestador.Notifications().get(i),true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cierro la BD
        admin.close();
    }
}
