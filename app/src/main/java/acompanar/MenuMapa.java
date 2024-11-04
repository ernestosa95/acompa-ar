package acompanar;

import static java.lang.Thread.sleep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import acompanar.ManagementModule.ButtonManagement.ButtonViewBasic;
import acompanar.ManagementModule.ShareDataManagement.Bluetooth;
import com.example.relevar.ManagementModule.StastisticsManagement.Stats;
import acompanar.ManagementModule.StorageManagement.BDUbicationManager;
import acompanar.ManagementModule.StorageManagement.EfectoresSearchAdapter;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.ShareDataManagement.FHIR.FHIR;
import acompanar.ManagementModule.UbicationManagement.ServicioGPS;
import acompanar.ManagementModule.StorageManagement.SQLitePpal;
import acompanar.BasicObjets.PollsterClass;
import acompanar.BasicObjets.FamiliarUnityClass;
import acompanar.BasicObjets.PersonClass;
import acompanar.MenuFamilia;
import acompanar.MenuVisualizacionDatos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.acompanar.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuMapa extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 123;

    // Creo al encuestador
    PollsterClass encuestador;
    // Mapa
    private MapView mapView;
    LatLng latLng;
    ArrayList<LatLng> recorrido = new ArrayList<>();
    Polyline ruta;
    AutoCompleteTextView autoEfector;

    ArrayList<String> datesview = new ArrayList<>();

    ConstraintLayout ITrecorrido;

    //String direccion = "http://relevar.ddns.net:1492";

    HashMap<String, Switch> switch_buttons = new HashMap<>();
    BDData adminBDData;
    SQLitePpal admin;
    TextView cantFamiliasCercanas, cantNotifications;
    ArrayList<HashMap<String,String>> mssgs;

    private GoogleMap googleMap;

    String direccion = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_mapa);

        // Eliminar el action bar
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.hide();

        // Evitar la rotacion
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        // Obtengo el encuestador que esta activado desde la base de datos
        encuestador = new PollsterClass(this);


        admin = new SQLitePpal(getBaseContext(), "DATA_PRINCIPAL", null, 1);
        encuestador.setID(admin.ObtenerActivado());

        adminBDData = new BDData(getBaseContext(), "BDData", null, 1);

        // Creo el mapa y lo centro en las coodenadas -60 -30
        mapView = findViewById(R.id.MAPA);
        Bundle mapBundle = null;

        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapBundle);
        mapView.getMapAsync(this);

        latLng = new LatLng(-60, -30);

        ITrecorrido = (ConstraintLayout) findViewById(R.id.RECORRIDOCL);
        ITrecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TerminarRecorrido(view);
            }
        });

        boolean estado = encuestador.ubicacion.funcionaServicioGPS(ServicioGPS.class);
        TextView recorridoTXT = findViewById(R.id.textView40);
        if (estado) {
            recorridoTXT.setText(getString(R.string.terminar_recorrido));
        } else {
            recorridoTXT.setText(getString(R.string.iniciar_recorrido));
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        datesview.add(DateFormat.getDateInstance().format(new Date()));
        ArrayList<String> dates = adminBDData.Dates();
        if(!dates.contains(DateFormat.getDateInstance().format(new Date()))) {
            dates.add(DateFormat.getDateInstance().format(new Date()));
        }
        datesview.addAll(dates);

        cantFamiliasCercanas = findViewById(R.id.TEXTVIEWCANTCERCANOS);
        cantNotifications = findViewById(R.id.TEXTVIEWNUMBERMSG);

        BDUbicationManager bdUbicationMannager = new BDUbicationManager(this, "BDData", null, 1);
        bdUbicationMannager.createUbicationTable();
        bdUbicationMannager.close();

        //Saludo
        TextView saludo = (TextView) findViewById(R.id.textView24);
        saludo.setText("Hola, "+encuestador.Nombre+" "+encuestador.Apellido);

        TextView seguimientos = (TextView) findViewById(R.id.textView27);
        seguimientos.setText(adminBDData.getNumberSeguimientos());

        ConstraintLayout seguimientos_view = findViewById(R.id.Seguimientos);
        seguimientos_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUrl();
            }
        });

        //readData();
        adminBDData.CoordinateAssignation();

    }

    public void SelectDate(View view){
        // Defino las caracteristicas
        int TamanioLetra = 20;

        // Defino los contenedores
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        textView.setText(R.string.fecha);
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        ArrayList<String> dates = adminBDData.Dates();
        if(!dates.contains(DateFormat.getDateInstance().format(new Date()))) {
            dates.add(DateFormat.getDateInstance().format(new Date()));
        }

        @SuppressLint("ResourceType") String color1 = getResources().getString(R.color.colorPar);
        @SuppressLint("ResourceType") String color2 = getResources().getString(R.color.colorImpar);
        ArrayList<CheckBox> checksdates = new ArrayList<>();
        for(int i=0; i<dates.size();i++){
            if((i%2)==0){
                checksdates.add(encuestador.DateCheckSelect(mainLayout, dates.get(i),TamanioLetra, color1, 35));
            }else{
                checksdates.add(encuestador.DateCheckSelect(mainLayout, dates.get(i),TamanioLetra, color2, 35));
            }
            if(datesview.contains(checksdates.get(checksdates.size()-1).getText().toString())){
                checksdates.get(checksdates.size()-1).setChecked(true);
            }
        }

        builder.setPositiveButton("LISTO", (dialogInterface, i) -> {
            datesview.clear();
            for (int k=0; k<checksdates.size(); k++){
                if(checksdates.get(k).isChecked()){
                    datesview.add(checksdates.get(k).getText().toString());
                }
            }
            if(datesview.size()==0) {
                datesview.add(DateFormat.getDateInstance().format(new Date()));
            }
            dialogInterface.dismiss();
        });

        // Defino un ScrollView para visualizar todos
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sv.setVerticalScrollBarEnabled(true);
        sv.addView(mainLayout);

        builder.setView(sv);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    public void FamiliesNear(View view){
        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        assert provider != null;
        @SuppressLint("MissingPermission") final Location[] location = {service.getLastKnownLocation(provider)};

        location[0] = service.getLastKnownLocation(provider);

        ArrayList<FamiliarUnityClass> x = adminBDData.MinnusThirtyMeters(getBaseContext(),
                Double.parseDouble(String.valueOf(location[0].getLatitude())),
                Double.parseDouble(String.valueOf(location[0].getLongitude())));

        if (x.size()!=0) {
            SetNotificationFamilyNear(x);
        }else{
            Toast.makeText(getBaseContext(), "NO SE ENCUENTRAN FAMILIAS REGISTRADAS EN MENOS DE 30 MTS", Toast.LENGTH_SHORT).show();
        }
    }

    private void SetNotificationFamilyNear(ArrayList<FamiliarUnityClass> families){
        // Defino los contenedores
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        textView.setText(getString(R.string.familia_a_menos_30));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(18F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        for (int i=0; i<families.size(); i++) {
            mainLayout.addView(CreateNotificationFamily(mainLayout, families.get(i)));
        }

        builder.setPositiveButton("LISTO", null);

        // Defino un ScrollView para visualizar todos
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sv.setVerticalScrollBarEnabled(true);
        sv.addView(mainLayout);

        builder.setView(sv);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private View CreateNotificationFamily(LinearLayout parent, FamiliarUnityClass families){
        families.LoadDataHashToParameters();
        View child = getLayoutInflater().inflate(R.layout.notification_family, parent, false);
        ArrayList<PersonClass> persons = adminBDData.SearchPersonsCoordinatesFilatories(this, families.Latitud, families.Longitud);

        ArrayList<TextView> data = new ArrayList<>();
        data.add(child.findViewById(R.id.TEXTVIEWPERSON1));
        data.add(child.findViewById(R.id.TEXTVIEWPERSON2));
        data.add(child.findViewById(R.id.TEXTVIEWPERSON3));

        for (int i=0; i<data.size(); i++){

            if ((persons.size()-1) >= i) {
                persons.get(i).LoadDataHashToParamaters();
                String aux = persons.get(i).Apellido + " " + persons.get(i).Nombre;
                data.get(i).setText(aux);
                data.get(i).setTextSize(18);
            }
        }

        if (families.SituacionHabitacional.equals("RENUENTE")){
            data.get(0).setText(families.SituacionHabitacional);
            data.get(0).setTextSize(18);
        }
        if (families.SituacionHabitacional.equals(getString(R.string.vivienda_deshabitada))){
            data.get(0).setText(families.SituacionHabitacional);
            data.get(0).setTextSize(18);
        }
        if (families.SituacionHabitacional.equals("VACIA HABIT.")){
            data.get(0).setText(families.SituacionHabitacional);
            data.get(0).setTextSize(18);
        }

        ConstraintLayout button = child.findViewById(R.id.NOTIFICATION);
        button.setOnClickListener(view -> {
            String pos = families.Latitud +" "+families.Longitud;
            ViewInfoFamily(pos);
        });

        ConstraintLayout color = child.findViewById(R.id.LAYAOUTCOLORNOTIFICATION);
        if (families.codigoColor[0].equals("V")){
            color.setBackground(getDrawable(R.drawable.verde));
        }
        if (families.codigoColor[0].equals("A")){
            color.setBackground(getDrawable(R.drawable.amarillo));
        }
        if (families.codigoColor[0].equals("R")){
            color.setBackground(getDrawable(R.drawable.rojo));
        }

        return child;
    }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // ESCUCHA AL SERVICIO DE GPS Y ACTUALIZA EL RECORRIDO
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            recorrido = intent.getParcelableArrayListExtra("RECORRIDO");
        }
    };

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    public void SendFHIR(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.send_to_gnu, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText url = view1.findViewById(R.id.urlfhir);
        TextView nameserver = view1.findViewById(R.id.NAMESERVER);
        ArrayList<HashMap<String, String>> urls = admin.GetServers();
        if (urls.size() != 0){
            url.setText(urls.get(urls.size()-1).get("URL"));
            nameserver.setText(urls.get(urls.size()-1).get("NAME"));
        }
        EditText user = view1.findViewById(R.id.userfhir);
        EditText pass = view1.findViewById(R.id.passfhir);

        //------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------
        // Defino el Layaout que va a contener a los Check
        int TamanioLetra = 20;
        LinearLayout mainLayout = (LinearLayout) view1.findViewById(R.id.FECHASCOMPARTIRFHIR);
        ConstraintLayout fechas = view1.findViewById(R.id.constraintLayoutfechas);
        fechas.setVisibility(View.GONE);

        Log.e("msg", "control 0");
        ArrayList<String> dates = adminBDData.Dates();
        if(!dates.contains(DateFormat.getDateInstance().format(new Date()))) {
            dates.add(DateFormat.getDateInstance().format(new Date()));
        }

        @SuppressLint("ResourceType") String color1 = getResources().getString(R.color.colorPar);
        @SuppressLint("ResourceType") String color2 = getResources().getString(R.color.colorImpar);
        ArrayList<CheckBox> checksdates = new ArrayList<>();
        Log.e("msg", "control 1");
        for(int i=0; i<dates.size();i++){
            Log.e("msg", Integer.toString(i)+"x");
            if((i%2)==0){
                checksdates.add(encuestador.DateCheckSelect(mainLayout, dates.get(i),TamanioLetra, color1, 35));
            }else{
                checksdates.add(encuestador.DateCheckSelect(mainLayout, dates.get(i),TamanioLetra, color2, 35));
            }
            if(datesview.contains(checksdates.get(checksdates.size()-1).getText().toString())){
                checksdates.get(checksdates.size()-1).setChecked(true);
            }
        }
        //------------------------------------------------------------------------------------------
        //------------------------------------------------------------------------------------------


        // Recupero las personas y familias a enviar
        ConstraintLayout dataserver = view1.findViewById(R.id.constraintLayout13);
        Button next = view1.findViewById(R.id.ENVIARFHIR2);
        next.setText("SIGUIENTE");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataserver.setVisibility(View.GONE);
                fechas.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
            }
        });

        Button send = view1.findViewById(R.id.ENVIARFHIR);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datesview.clear();
                for (int k=0; k<checksdates.size(); k++){
                    if(checksdates.get(k).isChecked()){
                        datesview.add(checksdates.get(k).getText().toString());
                    }
                }
                if(datesview.size()==0) {
                    datesview.add(DateFormat.getDateInstance().format(new Date()));
                }
                /*ArrayList<ClasePersona> personsdni = adminBDData.SearchAllPersonsWithDNI(getBaseContext());
                for (int i=0; i<personsdni.size(); i++){
                    personsdni.get(i).LoadDataHashToParamaters();
                    ClaseFamilia familia = adminBDData.SearchFamilyCoordinate(getBaseContext(), personsdni.get(i).Latitud, personsdni.get(i).Longitud);
                    FHIR fhir = new FHIR(getBaseContext(), personsdni.get(i), familia);
                    String str_url = url.getText().toString();
                    fhir.sendJson(str_url, user.getText().toString(), pass.getText().toString());
                }*/

                //String str_url = url.getText().toString();
                //fhir.sendJson(str_url, user.getText().toString(), pass.getText().toString());
                FHIR fhir = new FHIR(view1.getContext(), null, null);
                String str_url = url.getText().toString();
                fhir.sendJson(str_url, user.getText().toString(), pass.getText().toString(), datesview);
            }
        });

        Button cancel = view1.findViewById(R.id.CANCELARSEND);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button server = view1.findViewById(R.id.SERVERSEND);
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder_server = new AlertDialog.Builder(builder.getContext());
                LayoutInflater Inflater_server = getLayoutInflater();
                View view_server = Inflater_server.inflate(R.layout.config_server, null);
                builder_server.setView(view_server);
                final AlertDialog dialog_server = builder_server.create();
                dialog_server.show();

                EditText url_server = view_server.findViewById(R.id.urlserver);
                EditText port_server = view_server.findViewById(R.id.porserver);
                Button verificar = view_server.findViewById(R.id.VERIFICARSERVER);

                verificar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Construyo la url
                        String direccion = "http://"+url_server.getText().toString() +":"+ port_server.getText().toString();
                        // Envio de los datos
                        RequestQueue requestQueue = Volley.newRequestQueue(builder_server.getContext());
                        StringRequest stringRequestData = new StringRequest(Request.Method.POST, direccion+"/test", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(builder_server.getContext(), response, Toast.LENGTH_SHORT).show();
                                if (response.equals("ipc")) {
                                    admin.InsertServer(direccion + "/fhir_json", response);
                                    dialog_server.dismiss();
                                    url.setText(direccion + "/fhir_json");
                                    nameserver.setText(response);
                                }
                            }
                        }, new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                Toast.makeText(builder_server.getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }){@Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<>();
                            try {
                                params.put("USER", "admin");
                            }catch (Exception e){}
                            return params;
                        }
                        };
                        requestQueue.add(stringRequestData);
                    }
                });
            }
        });
        // Creo el objeto fhir y envio
        //FHIR fhir = new FHIR(this,);

    }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//  ENVIO DE CASOS

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
//  LECTURA DE CASOS
    public void checkUrl(){

    // Construyo la url
    ArrayList<HashMap<String,String>> servers = admin.GetServers();

    if(servers.size()!=0) {
        for (int i = 0; i < servers.size(); i++) {
            String url_obj = servers.get(i).get("URL");
            // Envio de los datos
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequestData = new StringRequest(Request.Method.POST, url_obj + "/test", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", response);
                    if (response.equals("ipc")) {
                        Toast.makeText(getBaseContext(), "Conectado a la red", Toast.LENGTH_SHORT).show();
                        Log.w("marca 0", "ok");
                        readData(url_obj);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), "No se puede comprobar la conexion con la red", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put("USER", "admin");
                    } catch (Exception e) {
                    }
                    return params;
                }
            };
            requestQueue.add(stringRequestData);

        }
    } else if (servers.size()==0) {
        // TODO pedir configuracion del servidor
        Toast.makeText(getBaseContext(), "No hay ninguna red configurada", Toast.LENGTH_SHORT).show();
    }

}

    private void readData(String url){

        // Construyo la url

        if (url.length()!=0) {

            // Envio de los datos
            RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());

            StringRequest stringRequestData = new StringRequest(Request.Method.POST, url + "/get_list", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(builder_server.getContext(), response, Toast.LENGTH_SHORT).show();
                    //if (response.equals("ipc")) {
                    //Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_SHORT).show();

                    try {

                        JSONArray casos = new JSONArray(response.toString());
                        ArrayList<PersonClass> personas = new ArrayList<>();

                        for (int i = 0; i < casos.length(); i++) {
                            BDData casosBDData = new BDData(getBaseContext(), "BDData", null, 1);

                            PersonClass person = new PersonClass(getBaseContext());
                            JSONObject personJson = casos.getJSONObject(i);
                            String name = personJson.getString("nombre");

                            person.Data.put("DNI", personJson.getString("dni"));
                            person.Data.put("NOMBRE", personJson.getString("nombre"));
                            person.Data.put("APELLIDO", personJson.getString("apellido"));

                            String formatoOriginal = "yyyy-MM-dd";
                            String formatoDeseado = "MMM dd, yyyy";
                            SimpleDateFormat formato = new SimpleDateFormat(formatoOriginal);
                            // Convertimos la cadena a un objeto Date
                            Date fecha = formato.parse(personJson.getString("fecha_nacimiento"));
                            // Creamos un objeto SimpleDateFormat para el formato deseado
                            SimpleDateFormat formatoNuevo = new SimpleDateFormat(formatoDeseado);
                            person.Data.put("FECHA", formatoNuevo.format(fecha));
                            if (personJson.getString("status").equals("activo")) {
                                person.Data.put("RE60_0", "SI");
                            }
                            if (!personJson.getString("efector").equals("null")) {
                                person.Data.put("EFECTOR", personJson.getString("efector"));
                            }

                            if (!casosBDData.existCase(personJson)) {
                                Log.e("Person", personJson.toString());
                                casosBDData.insert_person(person);
                                TextView seguimientos = (TextView) findViewById(R.id.textView27);
                                seguimientos.setText(casosBDData.getNumberSeguimientos());
                            }

                            Log.d("Person", "Name: " + name);
                            casosBDData.close();
                        }

                    } catch (JSONException | ParseException e) {
                        throw new RuntimeException(e);
                    }

                    showCases();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getBaseContext(), "No se recuperaron los datos", Toast.LENGTH_SHORT).show();
                    showCases();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    try {
                        params.put("USER", "admin");
                    } catch (Exception e) {
                    }
                    return params;
                }
            };
            requestQueue.add(stringRequestData);
            //}
            //});
        }else{
            showCases();
            Toast.makeText(getBaseContext(), "No conectado a la red", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCases(){

        ButtonViewBasic viewBasic = new ButtonViewBasic(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.basic_alert, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView cabecera = view1.findViewById(R.id.TXTGralCabecera);
        cabecera.setText("Seguimientos");

        Button guardar = view1.findViewById(R.id.GUARDARGrl);
        guardar.setVisibility(View.VISIBLE);
        guardar.setText("ENVIAR MIS REGISTROS A LA RED");
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), "Aun no disponible", Toast.LENGTH_SHORT).show();
            }
        });

        LinearLayout ly = view1.findViewById(R.id.LYGralOptions);
        BDData casosBDData = new BDData(getBaseContext(), "BDData", null, 1);
        ArrayList<PersonClass> cases = casosBDData.getCases(this);
        for (int i=0; i<cases.size(); i++){
            String identificacion = cases.get(i).Data.get("NOMBRE") + " " + cases.get(i).Data.get("APELLIDO");
            View view = viewBasic.generateCase(identificacion, "Status: "+"ACTIVO");

            Button btn = view.findViewById(R.id.Btncase);
            int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), identificacion, Toast.LENGTH_SHORT).show();
                    if(adminBDData.existCaseWithinCoordinates(cases.get(finalI).Data.get("NOMBRE"),
                            cases.get(finalI).Data.get("APELLIDO"),
                            cases.get(finalI).Data.get("DNI"))){
                        //Toast.makeText(getBaseContext(), "hay que actualizar", Toast.LENGTH_SHORT).show();
                        updateCaseWithinCoordinate(cases.get(finalI).Data.get("NOMBRE"),
                                cases.get(finalI).Data.get("APELLIDO"),
                                cases.get(finalI).Data.get("DNI"),
                                cases.get(finalI).Data.get("FECHA"));
                    }else {
                        // Encontrar los datos de la familia
                        ArrayList<PersonClass> person = adminBDData.SearchPersonsNameLastnameDNI(getBaseContext(),
                                cases.get(finalI).Data.get("NOMBRE"),
                                cases.get(finalI).Data.get("APELLIDO"),
                                cases.get(finalI).Data.get("DNI"));

                        if (person.size()>0) {
                            Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
                            Modif.putExtra("LATITUD", person.get(0).Data.get("LATITUD"));
                            Modif.putExtra("LONGITUD", person.get(0).Data.get("LONGITUD"));
                            startActivity(Modif);
                            dialog.dismiss();
                            finish();
                        }
                    }
                }
            });

            ly.addView(view);
        }
        casosBDData.close();

        ImageButton cancelar = view1.findViewById(R.id.CANCELARGrl);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void updateCaseWithinCoordinate(String name, String apellido, String dni, String dob){
        // Chequeo funcionamiento del GPS
        boolean estado = encuestador.ubicacion.funcionaServicioGPS(ServicioGPS.class);
        if (!estado) {

            /* Si no esta iniciado el servicio creo una alert para solicitar el inicio del gps*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater Inflater = getLayoutInflater();
            View view1 = Inflater.inflate(R.layout.message_yes_no, null);
            builder.setView(view1);
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.show();

            TextView txt1 = view1.findViewById(R.id.CONSULTA);
            txt1.setText("¿Iniciar recorrido?");

            Button si = view1.findViewById(R.id.BTNSI);
            si.setOnClickListener(view2 -> {

                /* Iniciar el recorrido y pasar el foco de la app a un nuevo activity para poder
                 * cargar los datos de una familia*/
                Intent intent = new Intent(getBaseContext(), ServicioGPS.class);
                startService(intent);
                TextView recorridoTXT = findViewById(R.id.textView40);
                recorridoTXT.setText(getString(R.string.terminar_recorrido));
                dialog.dismiss();

                SelectEfectorWithinCoordinates(name, apellido, dni, dob);
                //SelectFamily();
            });

            // Cerrar el alert de iniciar el recorrido
            Button no = view1.findViewById(R.id.BTNNO);
            no.setOnClickListener(view22 -> dialog.dismiss());
        } else {
            if(encuestador.EfectorTrabajo.length()==0) {
                SelectEfectorWithinCoordinates(name, apellido, dni, dni);
            }else {
                //SelectFamily();
                // Paso a actualizar directamente
                Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
                Modif.putExtra("IDENCUESTADOR", encuestador.getID());
                Modif.putExtra("COORDINATES", "NO");
                Modif.putExtra("NAME", name);
                Modif.putExtra("SURNAME", apellido);
                Modif.putExtra("DNI", dni);
                Modif.putExtra("DOB", dob);
                startActivityForResult(Modif, 1);

            }
        }
    }

    private void SelectEfectorWithinCoordinates(String name, String apellido, String dni, String dob){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = LayoutInflater.from(this);
        View view1 = Inflater.inflate(R.layout.alert_efector, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        // AUTOCOMPLETE TEXTVIEW DE LOS TRABAJOS
        autoEfector = view1.findViewById(R.id.AutoEfector);
        List<String> efectores = new ArrayList<String>();
        EfectoresSearchAdapter searchAdapter = new EfectoresSearchAdapter(this, efectores);
        autoEfector.setThreshold(1);
        autoEfector.setAdapter(searchAdapter);
        //if(encuestador.EfectorTrabajo!=null) autoEfector.setText(encuestador.EfectorTrabajo);

        final Button guardar = view1.findViewById(R.id.GUARDAREFECTOR);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoEfector.getText().toString().length()!=0) {
                    encuestador.EfectorTrabajo = admin.CodeEfector(autoEfector.getText().toString());
                    admin.UpdateEncuestador(encuestador.DNI, encuestador.EfectorTrabajo);
                    dialog.dismiss();
                    //SelectFamily();
                    Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
                    Modif.putExtra("IDENCUESTADOR", encuestador.getID());
                    Modif.putExtra("COORDINATES", "NO");
                    Modif.putExtra("COORDINATES", "NO");
                    Modif.putExtra("NAME", name);
                    Modif.putExtra("SURNAME", apellido);
                    Modif.putExtra("DNI", dni);
                    Modif.putExtra("DOB", dob);
                    startActivityForResult(Modif, 1);
                }else {Toast.makeText(getBaseContext(), "DEBE SELECCIONAR EL EFECTOR DESDE EL QUE ESTA TRABAJANDO" , Toast.LENGTH_SHORT).show();}
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELAREFECTOR);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final TextView AgregarManualmente = view1.findViewById(R.id.AGREGARMANUALMENTEEFECTOR);
        AgregarManualmente.setVisibility(View.GONE);
    }

//--------------------------------------------------------------------------------------------------
    public void Compartir() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.share_files, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        LinearLayout mainLayout = view1.findViewById(R.id.FECHASCOMPARTIRVIEW);

        ArrayList<String> dates = adminBDData.Dates();
        if (!datesview.contains(DateFormat.getDateInstance().format(new Date()))) {
            dates.add(DateFormat.getDateInstance().format(new Date()));
        }

        ArrayList<String> datesShare = new ArrayList<>();
        @SuppressLint("ResourceType") String color1 = getResources().getString(R.color.colorPar);
        @SuppressLint("ResourceType") String color2 = getResources().getString(R.color.colorImpar);
        ArrayList<CheckBox> checksdates = new ArrayList<>();
        Collections.reverse(dates);
        for (int i = 0; i < dates.size(); i++) {
            if ((i % 2) == 0) {
                checksdates.add(encuestador.DateCheckSelect(mainLayout, dates.get(i),
                        20, color1, 40));
            } else {
                checksdates.add(encuestador.DateCheckSelect(mainLayout, dates.get(i),
                        20, color2, 40));
            }
            //String aux = checksdates.get(checksdates.size()-1).getText().toString();
            //if(datesShare.contains(aux)){
            checksdates.get(checksdates.size() - 1).setChecked(true);
            //}
        }

        Button btnCompartir = view1.findViewById(R.id.BTNCOMPARTIRFECHA);
        Archivos archivos = new Archivos(this);
        btnCompartir.setOnClickListener(view2 -> {
            datesShare.clear();
            for (int k = 0; k < checksdates.size(); k++) {
                if (checksdates.get(k).isChecked()) {
                    datesShare.add(checksdates.get(k).getText().toString());
                }
            }
            if (datesShare.size() != 0) {
                archivos.CreateFileShare(datesShare);
                //view1.getContext().startActivity(Intent.createChooser(
                //        archivos.ShareSomeFiles(datesShare, "RelevAr"),
                //

                view1.getContext().startActivity(archivos.ShareSomeFiles(datesShare, "RelevAr"));
                dialog.dismiss();
            } else {
                Toast.makeText(getBaseContext(), "NO SELECCIONO UNA FECHA", Toast.LENGTH_SHORT).show();
            }
        });

        Button aedes = view1.findViewById(R.id.BTNCOMPARTIRDENGUE);
        aedes.setOnClickListener(view22 -> {
            datesShare.clear();
            for (int k = 0; k < checksdates.size(); k++) {
                if (checksdates.get(k).isChecked()) {
                    datesShare.add(checksdates.get(k).getText().toString());
                }
            }
            if (datesShare.size() != 0) {
                archivos.CreateFileShareAedes(datesShare);
                view1.getContext().startActivity(archivos.ShareSomeFiles(datesShare, "Aedes"));
                dialog.dismiss();
            }else {
                Toast.makeText(getBaseContext(), "NO SELECCIONO UNA FECHA", Toast.LENGTH_SHORT).show();
            }

        });

        final Button EnviarGNU = view1.findViewById(R.id.enviarGNU);
        EnviarGNU.setOnClickListener(view23 -> {
            datesShare.clear();
            for (int k = 0; k < checksdates.size(); k++) {
                if (checksdates.get(k).isChecked()) {
                    datesShare.add(checksdates.get(k).getText().toString());
                }
            }
            if (datesShare.size() != 0) {
                archivos.CreateFileShareGNUHealth(datesShare);
                view1.getContext().startActivity(archivos.ShareSomeFiles(datesShare, "RelevAr"));
                dialog.dismiss();
            } else {
                Toast.makeText(getBaseContext(), "NO SELECCIONO UNA FECHA", Toast.LENGTH_SHORT).show();
            }
        });

        final ImageButton cancelar = view1.findViewById(R.id.CANCELAR);
        cancelar.setOnClickListener(v -> dialog.dismiss());
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // NUEVA FAMILIA
    @SuppressLint("MissingPermission")
    private void SelectFamily(){
        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        assert provider != null;
        @SuppressLint("MissingPermission") final Location[] location = {service.getLastKnownLocation(provider)};

        location[0] = service.getLastKnownLocation(provider);

        ArrayList<FamiliarUnityClass> x = new ArrayList<>();
        if(location[0]!=null) {
            x = adminBDData.MinnusMeters(getBaseContext(),
                    Double.parseDouble(String.valueOf(location[0].getLatitude())),
                    Double.parseDouble(String.valueOf(location[0].getLongitude())),
                    50);
        }else {
            Toast.makeText(getBaseContext(), "NO SE PUEDEN TOMAR DATOS DEL GPS AUN" , Toast.LENGTH_SHORT).show();
        }

        if (x != null && x.size()!=0){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater Inflater = LayoutInflater.from(this);
                final View view = Inflater.inflate(R.layout.basic_alert, null);
                view.setFocusable(true);
                builder.setView(view);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                dialog.show();

                //Cabecera
                TextView txtCabecera = view.findViewById(R.id.TXTGralCabecera);
                txtCabecera.setText("FAMILIAS CERCANAS");

                //Crear el linerLayout que va a contener las diferentes categorias
                LinearLayout lyOptions = view.findViewById(R.id.LYGralOptions);

                for (int i = 0; i < x.size(); i++) {
                    lyOptions.addView(CreateNotificationFamily(lyOptions, x.get(i)));
                }

                ButtonViewBasic buttonViewBasic = new ButtonViewBasic(this);
                String texto = "Si la familia no se encuentra en el listado o quiere registrar una nueva familia, seleccione: NUEVA FAMILIA";
                View txt = buttonViewBasic.generateText("IMPORTANTE", this, texto);
                lyOptions.addView(txt);

                //Boton guardar
                Button guardar = view.findViewById(R.id.GUARDARGrl);
                guardar.setText("NUEVA FAMILIA");
                guardar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
                        Modif.putExtra("IDENCUESTADOR", encuestador.getID());
                        Modif.putExtra("COORDINATES", "SI");
                        startActivityForResult(Modif, 1);
                    }
                });

                ImageButton cancelar = view.findViewById(R.id.CANCELARGrl);
                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        }else{
            Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
            Modif.putExtra("IDENCUESTADOR", encuestador.getID());
            startActivityForResult(Modif, 1);
        }
    }

    @SuppressLint("SetTextI18n")
    public void NuevaFamilia(View view) {

        /* Corroboro el estado del servicio de GPS ya que para tomar datos de la persona debe estar
        * tomando datos de ubicación*/
        boolean estado = encuestador.ubicacion.funcionaServicioGPS(ServicioGPS.class);
        if (!estado) {

            /* Si no esta iniciado el servicio creo una alert para solicitar el inicio del gps*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater Inflater = getLayoutInflater();
            View view1 = Inflater.inflate(R.layout.message_yes_no, null);
            builder.setView(view1);
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.show();

            TextView txt1 = view1.findViewById(R.id.CONSULTA);
            txt1.setText("¿Iniciar recorrido?");

            Button si = view1.findViewById(R.id.BTNSI);
            si.setOnClickListener(view2 -> {

                /* Iniciar el recorrido y pasar el foco de la app a un nuevo activity para poder
                * cargar los datos de una familia*/
                Intent intent = new Intent(getBaseContext(), ServicioGPS.class);
                startService(intent);
                TextView recorridoTXT = findViewById(R.id.textView40);
                recorridoTXT.setText(getString(R.string.terminar_recorrido));
                dialog.dismiss();

                SelectEfector();
                //SelectFamily();
            });

            // Cerrar el alert de iniciar el recorrido
            Button no = view1.findViewById(R.id.BTNNO);
            no.setOnClickListener(view22 -> dialog.dismiss());
        } else {
            if(encuestador.EfectorTrabajo.length()==0) {
                SelectEfector();
            }else {
                SelectFamily();
            }
        }
    }

    private void SelectEfector(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = LayoutInflater.from(this);
        View view1 = Inflater.inflate(R.layout.alert_efector, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        // AUTOCOMPLETE TEXTVIEW DE LOS TRABAJOS
        autoEfector = view1.findViewById(R.id.AutoEfector);
        List<String> efectores = new ArrayList<String>();
        EfectoresSearchAdapter searchAdapter = new EfectoresSearchAdapter(this, efectores);
        autoEfector.setThreshold(1);
        autoEfector.setAdapter(searchAdapter);
        //if(encuestador.EfectorTrabajo!=null) autoEfector.setText(encuestador.EfectorTrabajo);

        final Button guardar = view1.findViewById(R.id.GUARDAREFECTOR);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoEfector.getText().toString().length()!=0) {
                    encuestador.EfectorTrabajo = admin.CodeEfector(autoEfector.getText().toString());
                    admin.UpdateEncuestador(encuestador.DNI, encuestador.EfectorTrabajo);
                    dialog.dismiss();
                    SelectFamily();
                    //Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
                    //Modif.putExtra("IDENCUESTADOR", encuestador.getID());
                    //startActivityForResult(Modif, 1);
                }else {Toast.makeText(getBaseContext(), "DEBE SELECCIONAR EL EFECTOR DESDE EL QUE ESTA TRABAJANDO" , Toast.LENGTH_SHORT).show();}
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELAREFECTOR);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final TextView AgregarManualmente = view1.findViewById(R.id.AGREGARMANUALMENTEEFECTOR);
        AgregarManualmente.setVisibility(View.GONE);
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // RECORRIDO
    // TERMINAR RECORRIDO
    @SuppressLint("SetTextI18n")
    public void TerminarRecorrido(View viewbtn) {
        TextView recorridoTXT = findViewById(R.id.textView40);
        /* Consulto la situacion del gps*/
        boolean estado = encuestador.ubicacion.funcionaServicioGPS(ServicioGPS.class);

        if (estado) {
            /* Si esta iniciado la opcion que debe estar disponible es terminar el recorrido, o
            * reiniciarlo*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater Inflater = getLayoutInflater();
            View view1 = Inflater.inflate(R.layout.message_yes_no, null);
            builder.setView(view1);
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.show();

            TextView txt1 = view1.findViewById(R.id.CONSULTA);
            txt1.setText("¿Terminar recorrido?");

            Button si = view1.findViewById(R.id.BTNSI);
            si.setOnClickListener(view -> {
                Intent intent = new Intent(getBaseContext(), ServicioGPS.class);
                stopService(intent);
                recorridoTXT.setText(getString(R.string.reiniciar_recorrido));
                dialog.dismiss();
            });

            Button no = view1.findViewById(R.id.BTNNO);
            no.setOnClickListener(view -> dialog.dismiss());
        } else {

            /* Si el recorrido no esta iniciado, la opcion disponible es iniciar el recorrido*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater Inflater = getLayoutInflater();
            View view1 = Inflater.inflate(R.layout.message_yes_no, null);
            builder.setView(view1);
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.show();

            TextView txt1 = view1.findViewById(R.id.CONSULTA);
            txt1.setText("¿Iniciar recorrido?");

            Button si = view1.findViewById(R.id.BTNSI);
            si.setOnClickListener(view -> {
                Intent intent = new Intent(getBaseContext(), ServicioGPS.class);
                startService(intent);
                //PararServicio.setText(getString(R.string.terminar_recorrido));
                recorridoTXT.setText(getString(R.string.terminar_recorrido));
                dialog.dismiss();
            });

            Button no = view1.findViewById(R.id.BTNNO);
            no.setOnClickListener(view -> dialog.dismiss());
        }
    }

    public void Statistics(View view){
        Intent intent = new Intent(getBaseContext(), Stats.class);
        startActivityForResult(intent, 1);
    }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // MAPA
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap map) {

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        LatLng posinicial = new LatLng(-31.9862369, -59.2871663);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posinicial,7));

        this.googleMap = map;

        // Funcion que le da al boton centrar la funcionalidad
        ImageButton centrar = findViewById(R.id.IMGBTCENTRAR);
        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        assert provider != null;
        final Location[] location = {service.getLastKnownLocation(provider)};
        centrar.setOnClickListener(v -> {
            try{
                LatLng userLocation = new LatLng(location[0].getLatitude(), location[0].getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,17));}
            catch (Exception e) {
                Toast.makeText(getBaseContext(), "AGUARDE, EL GPS SE ESTA LOCALIZANDO" , Toast.LENGTH_SHORT).show();
            }
        });

        // funcionalidad de los ioconos de marcado para que cuando se los toca se centre el mapa sobre
        // ellos y se despliegue la ventana secundaria de informacion
        map.setOnMarkerClickListener(marker -> {

            LatLng position = marker.getPosition();
            String pos = position.latitude +" "+position.longitude;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position,20));
            if (admin.IsReferencePoint(Double.toString(position.latitude), Double.toString(position.longitude))){
                Toast.makeText(getBaseContext(), "PUNTO DE REFERENCIA" , Toast.LENGTH_SHORT).show();
            }else{
                ViewInfoFamily(pos);
            }

            return false;
        });

        // Este handler (lo que hace es ejecutar cada cierto tiempo el codigo que tiene adentro),
        // borra y vuelve a dibujar los marcadores para mantener actualizado el mapa, cada vez que
        // se carga una nueva familia
        final Handler handlerMarker = new Handler();
        handlerMarker.postDelayed(new Runnable() {
            @Override
            public void run() {
                map.clear();

                ruta = map.addPolyline(new PolylineOptions()
                        .clickable(true).color(Color.parseColor("#69A4D1")));
                int ultimo = recorrido.size();
                if(ultimo!=0){
                    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(recorrido.get(ultimo-1),17));
                    ruta.setPoints(recorrido);}

                ArrayList<LatLng> marcadores = new ArrayList<>();
                ArrayList<String> codigoColores = new ArrayList<>();

                for (int i=0; i<datesview.size(); i++){
                    marcadores.addAll(adminBDData.SearchAllCordinatesForDate(datesview.get(i)));
                    if (marcadores.size()!=0){
                        codigoColores.addAll(adminBDData.ColorCodeForDate(getBaseContext(),
                                getString(R.string.codigo_color),datesview.get(i)));
                    }
                }

                if (marcadores.size()!=0) {
                        for (int i = 0; i < marcadores.size(); i++) {
                            MarkerOptions mo1 = new MarkerOptions();
                            mo1.position(marcadores.get(i));
                            Bitmap drawableBitmap = null;
                            if (codigoColores.get(i).equals("R")) {
                                drawableBitmap = getBitmap(R.drawable.icono_mapa_rojo);
                            }
                            if (codigoColores.get(i).equals("A")) {
                                drawableBitmap = getBitmap(R.drawable.icono_mapa_amarilla);
                            }
                            if (codigoColores.get(i).equals("V")) {
                                drawableBitmap = getBitmap(R.drawable.icono_mapa_verde);
                            }
                            mo1.icon(BitmapDescriptorFactory.fromBitmap(drawableBitmap));
                            map.addMarker(mo1);
                        }
                }

                //TODO: agregar texto de nombre para identificar el punto de referencia
                ArrayList<LatLng> marcadoresReferencePoints = new ArrayList<>();
                marcadoresReferencePoints.addAll(admin.SearchAllCordinatesReferencePoints());
                if (marcadoresReferencePoints.size()!=0){
                    for (int i = 0; i < marcadoresReferencePoints.size(); i++) {
                        MarkerOptions mo1 = new MarkerOptions();
                        mo1.position(marcadoresReferencePoints.get(i));
                        mo1.title(admin.getNameReferencePoint(Double.toString(marcadoresReferencePoints.get(i).latitude), Double.toString(marcadoresReferencePoints.get(i).longitude)));

                        Bitmap drawableBitmap = null;
                        drawableBitmap = getBitmap(R.drawable.icono_mapa_violeta);
                        mo1.icon(BitmapDescriptorFactory.fromBitmap(drawableBitmap));
                        map.addMarker(mo1);;
                    }
                }

                handlerMarker.postDelayed(this, 5000);
            }
        }, 0);

    }

    public void ShareMap(View view){
        googleMap.snapshot(this);

    }

    /* Transformo los xml de los iconos en un bitmap para que puedan ser graficados */
    private Bitmap getBitmap(int drawableRes){
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap( (int)getResources().getDimension(R.dimen.ancho_map), (int)getResources().getDimension(R.dimen.alto_map), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, (int)getResources().getDimension(R.dimen.alto_icono),
                (int)getResources().getDimension(R.dimen.ancho_icono));
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        assert provider != null;
        @SuppressLint("MissingPermission") final Location[] location = {service.getLastKnownLocation(provider)};

        final Handler handlerNearFamily = new Handler();
        handlerNearFamily.postDelayed(new Runnable() {
            @SuppressLint({"MissingPermission", "SetTextI18n"})
            @Override
            public void run() {
                location[0] = service.getLastKnownLocation(provider);
                if (location[0]!=null) {
                    cantFamiliasCercanas.setText(Integer.toString(adminBDData.MinnusThirtyMeters(getBaseContext(),
                            Double.parseDouble(String.valueOf(location[0].getLatitude())),
                            Double.parseDouble(String.valueOf(location[0].getLongitude()))).size()));
                }
                cantNotifications.setText(Integer.toString(admin.CantNotification()));
                handlerNearFamily.postDelayed(this, 30000);
            }
        },0);

        TextView cantidad = findViewById(R.id.cantidadregistros);
        cantidad.setText(Integer.toString(adminBDData.CantFamilies(this)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //Cierro la BD
        adminBDData.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // MENU DE ACTIVACION DE BOTONES
    private void SwitchButtons(){
        // Defino las caracteristicas
        int TamanioLetra = 20;
        String ColorPares = "#4A4A4A";
        String ColorImpares = "#4f5f6c";

        // Defino los contenedores
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        textView.setText(R.string.botones);
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        LinearLayout mainLayout       = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        for(int i=0; i<encuestador.Botones().size();i++){
            Switch aux;
            if((i%2)==0){
                aux = encuestador.ButtonSwitch(mainLayout,TamanioLetra,encuestador.Botones().get(i),TamanioLetra, ColorPares);}
            else{
                aux = encuestador.ButtonSwitch(mainLayout,TamanioLetra,encuestador.Botones().get(i),TamanioLetra, ColorImpares);
            }
            aux = encuestador.comportamientoSwitch(aux);
            switch_buttons.put(encuestador.Botones().get(i), aux);
        }

        builder.setPositiveButton("LISTO", (dialogInterface, i) -> dialogInterface.dismiss());

        // Defino un ScrollView para visualizar todos
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sv.setVerticalScrollBarEnabled(true);
        sv.addView(mainLayout);

        builder.setView(sv);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // Asigno la informacion a los diferentes widgets
    private void ViewInfoFamily(String coordenadas){
        Intent Modif = new Intent(getBaseContext(), MenuVisualizacionDatos.class);
        String latitude = coordenadas.split(" ")[0];
        String longitude = coordenadas.split(" ")[1];
        Modif.putExtra("LATITUDE", latitude);
        Modif.putExtra("LONGITUDE", longitude);
        startActivityForResult(Modif, 1);
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // Busqueda de familias por personas
    public void SearchPerson(View view){
        /* Si esta iniciado la opcion que debe estar disponible es terminar el recorrido, o
         * reiniciarlo*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.search_person, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText lastname = view1.findViewById(R.id.EDITAPELLIDOBUSCAR);
        EditText name = view1.findViewById(R.id.EDITNOMBREBUSCAR);
        EditText dni = view1.findViewById(R.id.EDITDNIBUSCAR);
        LinearLayout layout = view1.findViewById(R.id.LINEARRESULTADOS);

        Button btn = view1.findViewById(R.id.BTNSEARCHPERSON);
        final ArrayList<PersonClass>[] x = new ArrayList[]{new ArrayList<>()};
        btn.setOnClickListener(view2 -> {
            x[0] = adminBDData.SearchPersonsNameLastnameDNI(getBaseContext(),
                    name.getText().toString(),
                    lastname.getText().toString(),
                    dni.getText().toString());
            //Toast.makeText(getBaseContext(), Integer.toString(x[0].size()), Toast.LENGTH_SHORT).show();

            layout.removeAllViews();
            for (int i=0; i<x[0].size(); i++){
                layout.addView(CreatePersonFound(layout, x[0].get(i)));
            }

        });

        ImageView close = view1.findViewById(R.id.BTNCLOSEBUSCAR);
        close.setOnClickListener(view22 -> dialog.dismiss());
    }

    private View CreatePersonFound(LinearLayout parent, PersonClass person){
        person.LoadDataHashToParamaters();
        View child = getLayoutInflater().inflate(R.layout.notification_family, parent, false);

        ArrayList<TextView> data = new ArrayList<>();
        data.add(child.findViewById(R.id.TEXTVIEWPERSON1));
        data.add(child.findViewById(R.id.TEXTVIEWPERSON2));
        data.add(child.findViewById(R.id.TEXTVIEWPERSON3));

        String aux = person.Apellido + " " + person.Nombre;
        data.get(0).setText(aux);
        data.get(0).setTextSize(18);

        ConstraintLayout button = child.findViewById(R.id.NOTIFICATION);
        button.setOnClickListener(view -> {
            String pos = person.Latitud +" "+person.Longitud;
            ViewInfoFamily(pos);
        });

        ConstraintLayout color = child.findViewById(R.id.LAYAOUTCOLORNOTIFICATION);
        color.setBackground(getDrawable(R.drawable.verde));

        return child;
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    //Configuraciones
    @SuppressLint("ResourceAsColor")
    public void Configurations(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = LayoutInflater.from(this);
        final View view_alert = Inflater.inflate(R.layout.alert_basic22, null);
        view_alert.setFocusable(true);
        builder.setView(view_alert);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Server
        ConstraintLayout server = (ConstraintLayout) view_alert.findViewById(R.id.serverBTN);
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder_server = new AlertDialog.Builder(builder.getContext());
                LayoutInflater Inflater_server = getLayoutInflater();
                View view_server = Inflater_server.inflate(R.layout.config_server, null);
                builder_server.setView(view_server);
                final AlertDialog dialog_server = builder_server.create();
                dialog_server.show();

                EditText url = view_server.findViewById(R.id.urlserver);

                Button verificar = view_server.findViewById(R.id.VERIFICARSERVER);
                verificar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Construyo la url
                        String direccion = "http://"+url.getText().toString() +":5000";//+ port_server.getText().toString();
                        // Envio de los datos
                        RequestQueue requestQueue = Volley.newRequestQueue(builder_server.getContext());
                        StringRequest stringRequestData = new StringRequest(Request.Method.POST, direccion+"/test", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(builder_server.getContext(), response, Toast.LENGTH_SHORT).show();
                                if (response.equals("ipc")) {
                                    admin.InsertServer(direccion, response);
                                    dialog_server.dismiss();
                                    //url.setText(direccion + "/fhir_json");
                                    //nameserver.setText(response);
                                }
                            }
                        }, new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                Toast.makeText(builder_server.getContext(), "No se puede comprobar la conexion", Toast.LENGTH_SHORT).show();
                            }
                        }){@Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<>();
                            try {
                                params.put("USER", "admin");
                            }catch (Exception e){}
                            return params;
                        }
                        };
                        requestQueue.add(stringRequestData);
                    }
                });
            }
        });

        // Compartir
        ConstraintLayout compartir = (ConstraintLayout) view_alert.findViewById(R.id.compartirBTN);
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Compartir();
            }
        });

        // Bluetooth
        ConstraintLayout bth = (ConstraintLayout) view_alert.findViewById(R.id.bluetoothBTN);
        bth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bluetooth(view);
                Toast.makeText(getBaseContext(), "No disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // Enviar
        ConstraintLayout enviar = (ConstraintLayout) view_alert.findViewById(R.id.enviarBTN);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendFHIR(view);
            }
        });

        // Compartir mapa
        ConstraintLayout cmapa = (ConstraintLayout) view_alert.findViewById(R.id.compartirmapaBTN);
        cmapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareMap(view);
            }
        });

        // Estadisticas
        ConstraintLayout estadisticas = (ConstraintLayout) view_alert.findViewById(R.id.estadisticasBTN);
        estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Statistics(view);
            }
        });

        // Ubicaciones referencia
        ConstraintLayout referencias = (ConstraintLayout) view_alert.findViewById(R.id.referenciasBTN);
        referencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferencePoints(view);
            }
        });

        // Estadisticas
        ImageView cerrar = (ImageView) view_alert.findViewById(R.id.cerrarBTN);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    private void SwitchNotifications(){
        // Defino los contenedores
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        textView.setText(getString(R.string.notificaciones));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(16F);
        textView.setHeight(100);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0,5,0,2);
        layoutParams.gravity = 2;

        Switch hpvNotifications = new Switch(this);
        hpvNotifications.setText(R.string.HPV);
        hpvNotifications.setLayoutParams(layoutParams);
        hpvNotifications.setTextSize(22F);
        hpvNotifications.setBackgroundColor(R.color.colorImpar);
        hpvNotifications.setOnCheckedChangeListener((compoundButton, b) -> {
            admin.ActivarNotificacionDesactivar(getString(R.string.HPV), hpvNotifications.isChecked());
            admin.close();
        });
        mainLayout.addView(hpvNotifications);

        builder.setPositiveButton("LISTO", null);

        // Defino un ScrollView para visualizar todos
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sv.setVerticalScrollBarEnabled(true);
        sv.addView(mainLayout);

        builder.setView(sv);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    //Mensajes
    public void Messages(View view){
        /* Si esta iniciado la opcion que debe estar disponible es terminar el recorrido, o
         * reiniciarlo*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.messages, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Codigo de funcionamiento de los tabs
        TabHost tabs = view1.findViewById(R.id.TABMESSAGES);
        tabs.setup();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200,75);
        layoutParams.setMargins(5,5,5,2);

        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.titulo_tabs,null);
        tv.setText(getString(R.string.nuevo));
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.parseColor("#4A4A4A"));
        tv.setTextSize(10);
        tv.setLayoutParams(layoutParams);
        TabHost.TabSpec spec = tabs.newTabSpec("mytab1");
        spec.setContent(R.id.TABNUEVO);
        spec.setIndicator(tv);
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mytab2");
        spec.setContent(R.id.TABHECHO);
        TextView tv1 = (TextView)LayoutInflater.from(this).inflate(R.layout.titulo_tabs,null);
        tv1.setText(getString(R.string.hecho));
        tv1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        tv1.setTextColor(Color.parseColor("#4A4A4A"));
        tv1.setTextSize(10);
        tv1.setLayoutParams(layoutParams);
        spec.setIndicator(tv1);
        tabs.addTab(spec);

        tabs.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.background_button_light_blue));
        tabs.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));

        tabs.setOnTabChangedListener(tabId -> {
            int tab = tabs.getCurrentTab();
            tabs.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));
            tabs.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));
            tabs.getTabWidget().getChildAt(tab).setBackground(getDrawable(R.drawable.background_button_light_blue));
            MessagesDone(view1);
        });

        LinearLayout news = view1.findViewById(R.id.TABNUEVOS);
        mssgs = admin.MessagesNew();
        for (int i=0; i<mssgs.size(); i++){
            news.addView(CreateView(news, mssgs.get(i).get("NAME"),
                    mssgs.get(i).get("SURNAME"),
                    mssgs.get(i).get("DNI"),
                    mssgs.get(i).get("TYPE_MESSAGE"),
                    mssgs.get(i).get("DATE"),
                    mssgs.get(i).get("LATITUD"),
                    mssgs.get(i).get("LONGITUD"), false));
        }

        ImageView close = view1.findViewById(R.id.CLOSENOTIFICATION);
        close.setOnClickListener(view2 -> dialog.dismiss());
    }

    private void MessagesDone(View view1){
        LinearLayout dones = view1.findViewById(R.id.TABHECHOS);
        mssgs = admin.MessagesDone();
        dones.removeAllViews();
        for (int i=0; i<mssgs.size(); i++){
            dones.addView(CreateView(dones, mssgs.get(i).get("NAME"),
                    mssgs.get(i).get("SURNAME"),
                    mssgs.get(i).get("DNI"),
                    mssgs.get(i).get("TYPE_MESSAGE"),
                    mssgs.get(i).get("DATE"),
                    mssgs.get(i).get("LATITUD"),
                    mssgs.get(i).get("LONGITUD"), true));
        }
    }

    private View CreateView(LinearLayout parent, String name, String lastname, String dni, String typemsg,
                                    String date, String latitud, String longitud, Boolean done){

        View child = getLayoutInflater().inflate(R.layout.notification_message, parent, false);
        TextView mssg = child.findViewById(R.id.TEXTVIEWMESSAGE);
        String mssgText = name+" "+lastname+" ("+dni+") realizo test de "+typemsg+" el "+date;
        mssg.setText(mssgText);
        TextView lat = child.findViewById(R.id.latitud);
        lat.setText(latitud);
        TextView lon = child.findViewById(R.id.longitud);
        lon.setText(longitud);
        TextView fecha = child.findViewById(R.id.fechaesc);
        fecha.setText(date);
        Button btn = child.findViewById(R.id.BTNHECHO);
        btn.setOnClickListener(view -> {
            Toast.makeText(getBaseContext(), Long.toString(admin.NotificationDone(lat.getText().toString(),
                                    lon.getText().toString(),
                                    fecha.getText().toString())), Toast.LENGTH_SHORT).show();
            child.setVisibility(View.GONE);
        });
        if (done){
            btn.setVisibility(View.GONE);
        }
        return child;
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // Desactivo el boton de volver atras
    @Override
    public void onBackPressed(){
        // Defino los contenedores
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        textView.setText(getString(R.string.relevar));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(22F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        final LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        // Defino los parametros
        int TamanioLetra =20;

        // Telefono Celular
        LinearLayout layout0       = new LinearLayout(this);
        layout0.setOrientation(LinearLayout.HORIZONTAL);
        layout0.setVerticalGravity(Gravity.CENTER_VERTICAL);
        final TextView descripcion = new TextView(getApplicationContext());
        descripcion.setText(getString(R.string.cerrar_salir_sesion));
        descripcion.setGravity(Gravity.CENTER_HORIZONTAL);
        descripcion.setTextSize(TamanioLetra);
        descripcion.setTextColor(Color.WHITE);
        descripcion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout0.setMinimumHeight(100);
        layout0.addView(descripcion);

        mainLayout.addView(layout0);

        // Add OK and Cancel buttons
        builder.setPositiveButton("SI", (dialog, which) -> {
            encuestador.cerrarSesion();
            finish();
        }).setNegativeButton("NO", (dialogInterface, i) -> {

        });

        builder.setView(mainLayout);
        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void pdf(View view){
        Archivos archivos = new Archivos(getBaseContext());
        //archivos.CreatePDF();
    }

    //----------------------------------------------------------------------------------------------
    public void Bluetooth(View view){
        Intent intent = new Intent(getBaseContext(), Bluetooth.class);
        startActivityForResult(intent, 1);
    }

    @SuppressLint("MissingPermission")
    public void ReferencePoints(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = LayoutInflater.from(this);
        final View view_alert = Inflater.inflate(R.layout.basic_alert, null);
        view_alert.setFocusable(true);
        builder.setView(view_alert);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //Cabecera
        TextView txtCabecera = view_alert.findViewById(R.id.TXTGralCabecera);
        txtCabecera.setText("REGISTRO DE PUNTOS DE REFERENCIA");

        //Crear el linerLayout que va a contener las diferentes categorias
        LinearLayout lyOptions = view_alert.findViewById(R.id.LYGralOptions);
        ButtonViewBasic buttonViewBasic = new ButtonViewBasic(this);

        View nombre = buttonViewBasic.generateTextEdit("NOMBRE", this);
        lyOptions.addView(nombre);

        ArrayList<String> optionTipos = new ArrayList<>();
        optionTipos.add("CLUB");
        optionTipos.add("COMERCIO");
        optionTipos.add("ESCUELAS");
        optionTipos.add("OTRO");
        View tipo = buttonViewBasic.generateSpinnerMultipleSelect("Tipo de lugar", optionTipos);
        lyOptions.addView(tipo);

        View descripcion = buttonViewBasic.generateTextEdit("Descripciòn", this);
        lyOptions.addView(descripcion);

        //Boton guardar
        Button guardar = view_alert.findViewById(R.id.GUARDARGrl);
        guardar.setText("REGISTRAR");
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonViewBasic.getValueText(nombre).length()!=0) {
                    if (buttonViewBasic.getValueSpiner(tipo).length()!=0) {
                        if (buttonViewBasic.getValueText(descripcion).length()!=0) {
                            String namePoint = buttonViewBasic.getValueText(nombre);
                            String tipoPoint = buttonViewBasic.getValueSpiner(tipo);
                            String descripcionPoint = buttonViewBasic.getValueText(descripcion);
                            LocationManager service = (LocationManager)
                                    getSystemService(LOCATION_SERVICE);
                            Criteria criteria = new Criteria();
                            String provider = service.getBestProvider(criteria, false);
                            assert provider != null;
                            final Location[] location = {service.getLastKnownLocation(provider)};
                            try {
                                admin.InsertReferencePoint(Double.toString(location[0].getLatitude()), Double.toString(location[0].getLongitude()), namePoint, tipoPoint, descripcionPoint);
                            } catch (Exception e) {
                                Toast.makeText(getBaseContext(), "AGUARDE, EL GPS SE ESTA LOCALIZANDO", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getBaseContext(), "DEBE INGRESAR UNA DESCRIPCION", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getBaseContext(), "DEBE SELECCIONAR UN TIPO DE PUNTO", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getBaseContext(), "DEBE INGRESAR UN NOMBRE", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton cancelar = view_alert.findViewById(R.id.CANCELARGrl);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void onSnapshotReady(@Nullable Bitmap bitmap) {
        // Save the snapshot to a file
        File nuevaCarpeta = new File(this.getExternalFilesDir(null).toString());
        File file = new File(nuevaCarpeta.getAbsolutePath()+"/"+ "map.png");//new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "map.png");
        Log.e("ruta", file.getAbsolutePath()+".");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Uri contentUri = Uri.fromFile(file);
        contentUri = FileProvider.getUriForFile(this, "com.example.relevar", new File("//storage/emulated/0/Android/data/com.example.relevar/files/map.png"));

        // Create an intent to share the content file
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

        // Start the share activity
        startActivity(Intent.createChooser(shareIntent, "Compartir imagen"));

    }
}
