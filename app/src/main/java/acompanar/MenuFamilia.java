package acompanar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import acompanar.ButtonsDeclarationFamiliarUnity.GeneralDescriptionFamiliarButton;
import acompanar.ButtonsDeclarationFamiliarUnity.AedesButton;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.StorageManagement.SQLitePpal;
import acompanar.ManagementModule.ButtonManagement.ButtonDeclaration;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.BasicObjets.PollsterClass;
import acompanar.BasicObjets.FamiliarUnityClass;
import acompanar.BasicObjets.PersonClass;
import acompanar.MenuPersona;
import com.example.acompanar.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.widget.Toast.*;

public class MenuFamilia extends AppCompatActivity {

    // Definicion de String para contener informacion
    private String Latitud, Longitud;
    private Double Latitudenviar=0.0, Longitudenviar=0.0;

    // Encuestador
    PollsterClass encuestador;

    // Defino Arrays para almacenar datos
    final private ArrayList<String> names = new ArrayList<>();
    private ArrayList<PersonClass> MiembrosFamiliares = new ArrayList<>();
    HashMap<String, View> ButtonsViews = new HashMap<>();

    // Defino la lisa de personas
    private ListView lv1;

    // Defino un Adaptador para el ListView
    ArrayAdapter<String> adapter;

    int position;

    LocationManager locationManager;
    LocationListener locationListener;
    FamiliarUnityClass familia;
    TextView avanceGeneral, avanceDengue;
    ConstraintLayout CLGeneral, CLDengue, BtnDengue;
    LinearLayout BotonesFamilia;
    HashMap<String, Switch> switch_buttons = new HashMap<>();

    SQLitePpal admin;
    BDData adminBData;

    Boolean AddData = Boolean.FALSE;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //evita la rotacion
        setContentView(R.layout.activity_menu_familia);

        // Eliminar el action bar
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.hide();

        // Evitar la rotacion
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
        encuestador = new PollsterClass(getApplicationContext());
        // Seteo el titulo de la action bar del activity
        admin = new SQLitePpal(getBaseContext(), "DATA_PRINCIPAL", null, 1);
        adminBData = new BDData(getBaseContext(), "BDData", null, 1);
        adminBData.deleteCacheUD();
        adminBData.CreateCategoriesCacheUD(this);
        encuestador.setID(admin.ObtenerActivado());
        lv1 = findViewById(R.id.list1);

        LatLong();

        // Defino el linearlayout que contiene los botones
        BotonesFamilia = findViewById(R.id.BtnsFamilia);

        //CrearBotonera();
        encuestador.ButtonsActivateDesactivate(this.findViewById(android.R.id.content), encuestador.FamilyButtons());
        familia=new FamiliarUnityClass(this);
        familia.data_enuestador = encuestador.DNI;


        RadioButton deshabitada = findViewById(R.id.CHECKDESHABITADA);
        RadioButton renuente = findViewById(R.id.CHECKRENUENTE);
        RadioButton vacia_habitada = findViewById(R.id.CHECKVACIAHABITADA);

        if(getIntent().getSerializableExtra("LATITUD")!=null && getIntent().getSerializableExtra("FECHA")!=null) {
            AddData = Boolean.FALSE;
            MiembrosFamiliares = adminBData.SearchPersonsCoordinatesAndDate(this,
                    getIntent().getStringExtra("LATITUD"),
                    getIntent().getStringExtra("LONGITUD"),
                    getIntent().getStringExtra("FECHA"));
            familia=adminBData.SearchFamilyCoordinateAndDate(this,
                    getIntent().getStringExtra("LATITUD"),
                    getIntent().getStringExtra("LONGITUD"),
                    getIntent().getStringExtra("FECHA"));

            // paso todos los datos al cacheud
            adminBData.updateCacheUD(familia.Data);

            if(Objects.equals(familia.Data.get(getString(R.string.situacion_habitacional)), getString(R.string.vivienda_renuente))){
                renuente.setChecked(true);
            }else{
                if(Objects.equals(familia.Data.get(getString(R.string.situacion_habitacional)), getString(R.string.vivienda_deshabitada))){
                    deshabitada.setChecked(true);
                }else{
                    if (Objects.equals(familia.Data.get(getString(R.string.situacion_habitacional)), "VACIA HAB.")){
                        vacia_habitada.setChecked(true);
                    }
                }
            }

            for (int i=0; i<MiembrosFamiliares.size();i++){
                MiembrosFamiliares.get(i).Fecha = familia.Fecha;
                names.add(MiembrosFamiliares.get(i).Apellido + " " + MiembrosFamiliares.get(i).Nombre+ " (" + MiembrosFamiliares.get(i).DNI+")");
            }
            ListeVer();


            GeneralDescriptionFamiliarButton generalDescriptionFamiliarButton = new GeneralDescriptionFamiliarButton(this, familia, this.findViewById(android.R.id.content), false, false, false);
            generalDescriptionFamiliarButton.ColorAvanceGeneralFamilia();
        }
        else if (getIntent().getSerializableExtra("LATITUD")!=null){
                AddData = Boolean.TRUE;
                familia.Latitud = getIntent().getStringExtra("LATITUD");
                familia.Longitud = getIntent().getStringExtra("LONGITUD");

                ArrayList<PersonClass> auxPersons = adminBData.SearchPersonsCoordinatesFilatories(this,
                        familia.Latitud,
                        familia.Longitud);

                for (int i=0; i<auxPersons.size(); i++){
                    auxPersons.get(i).LoadDataHashToParamaters();
                    PersonClass clasePersona = new PersonClass(this);
                    clasePersona.Latitud = familia.Latitud;
                    clasePersona.Longitud = familia.Longitud;
                    clasePersona.Nombre = auxPersons.get(i).Nombre;
                    clasePersona.Apellido = auxPersons.get(i).Apellido;
                    clasePersona.DNI = auxPersons.get(i).DNI;
                    clasePersona.Nacimiento = auxPersons.get(i).Nacimiento;
                    clasePersona.Sexo = auxPersons.get(i).Sexo;
                    clasePersona.QR = "false";

                    MiembrosFamiliares.add(clasePersona);
                }

                for (int i=0; i<MiembrosFamiliares.size();i++){
                    names.add(MiembrosFamiliares.get(i).Apellido + " " + MiembrosFamiliares.get(i).Nombre+ " (" + MiembrosFamiliares.get(i).DNI+")");

                }
                ListeVer();
        }

        if (getIntent().getSerializableExtra("COORDINATES") != null) {
            if (getIntent().getSerializableExtra("COORDINATES").equals("NO")) {
                Toast.makeText(this, "hay que actualizar", Toast.LENGTH_SHORT).show();
                PersonClass clasePersona = new PersonClass(this);
                //clasePersona.Latitud = familia.Latitud;
                //clasePersona.Longitud = familia.Longitud;
                clasePersona.Nombre = getIntent().getSerializableExtra("NAME").toString();
                clasePersona.Apellido = getIntent().getSerializableExtra("SURNAME").toString();
                clasePersona.DNI = getIntent().getSerializableExtra("DNI").toString();
                clasePersona.Nacimiento = getIntent().getSerializableExtra("DOB").toString();
                clasePersona.Sexo = "X";
                clasePersona.QR = "false";
                clasePersona.Data.put("RE60_0", "SI");
                MiembrosFamiliares.add(clasePersona);
            } else {
                Log.w("marca 1", getIntent().getSerializableExtra("COORDINATES") + "v");
            }
        }

        CLGeneral = findViewById(R.id.AVANCEGENERAL);
        avanceGeneral = findViewById(R.id.COMPLETADOGENERAL);
        BtnDengue = findViewById(R.id.BTNDENGUE);
        CLDengue = findViewById(R.id.AVANCEDENGUE);
        avanceDengue = findViewById(R.id.COMPLETADODENGUE);

        // Definicion rapida de botones

        ButtonDeclaration buttonDeclaration = new ButtonDeclaration(this);
        // Agrego la vista de cada boton al menu
        LinearLayout LLfamilia = (LinearLayout) findViewById(R.id.BtnsFamilia);

        for(int i=0; i<buttonDeclaration.FamilyButtons.size(); i++) {
            View addView = buttonDeclaration.ButtonsViews.get(buttonDeclaration.FamilyButtons.get(i));
            ButtonsViews.put(buttonDeclaration.FamilyButtons.get(i), addView);
            LLfamilia.addView(addView);

            // Otorgo el funcionamiento a los botones
            ConstraintLayout btnAction = addView.findViewById(R.id.BTNGNRL);
            int finalI = i;
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDataDomicilio();
                    buttonDeclaration.executeButtons(buttonDeclaration.FamilyButtons.get(finalI));
                }
            });
        }
        encuestador.ADbtn(ButtonsViews, encuestador.FamilyButtons());


    }

    public void SwitchButton(View view){
        // Defino las caracteristicas
        String ColorPares = "#4A4A4A";
        String ColorImpares = "#4f5f6c";

        // Defino los contenedores
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        textView.setText(getString(R.string.botones));
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        // Defino el Layaout que va a contener a los Check
        LinearLayout mainLayout       = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        for(int i=0; i<encuestador.FamilyButtons().size();i++){
            Switch aux;
            if((i%2)==0){
                aux = encuestador.ButtonSwitch(mainLayout,20,encuestador.FamilyButtons().get(i),20, ColorPares);}
            else{
                aux = encuestador.ButtonSwitch(mainLayout,20,encuestador.FamilyButtons().get(i),20, ColorImpares);
            }
            aux = encuestador.comportamientoSwitch(aux);
            switch_buttons.put(encuestador.FamilyButtons().get(i), aux);
        }

        View view_context = this.findViewById(android.R.id.content);
        builder.setPositiveButton("LISTO", (dialogInterface, i) -> {
            encuestador.ButtonsActivateDesactivate(view_context, encuestador.FamilyButtons());
            encuestador.ADbtn(ButtonsViews, encuestador.FamilyButtons());
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

    protected void onStart() {
        super.onStart();
        ListeVer();
        }
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // OBTENCION DE LOS DATOS DE LONGTUD Y LATITUD
    private void LatLong(){
    //public void LatLong(View view){
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Latitud=Double.toString(location.getLatitude());
                Longitud=Double.toString(location.getLongitude());
                Latitudenviar = location.getLatitude();
                Longitudenviar = location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Toast.makeText(this, "GPS funcionando", Toast.LENGTH_SHORT).show();
        }else {
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Toast.makeText(this, "NETWORK, su GPS no esta funcionando, puede que los datos no sean precisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // AGREGAR, EDITAR O ELIMINAR UNA PERSONA Y VISUALIZARLAS
    public void NuevaPersona(View view){
        final RadioButton deshabitada = findViewById(R.id.CHECKDESHABITADA);
        final RadioButton renuente = findViewById(R.id.CHECKRENUENTE);
        final RadioButton vacia_habitada = findViewById(R.id.CHECKVACIAHABITADA);
        if(!deshabitada.isChecked() && !vacia_habitada.isChecked()){
            if(!renuente.isChecked()){
                getDataDomicilio();
        Intent Modif= new Intent (this, MenuPersona.class);
        startActivityForResult(Modif, 1);}
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater Inflater = getLayoutInflater();
                final View view_alert = Inflater.inflate(R.layout.message_yes_no, null);
                builder.setView(view_alert);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                dialog.show();

                final TextView msg = view_alert.findViewById(R.id.CONSULTA);
                msg.setText("NO SE PUEDE CARGAR UNA PERSONA SI\nESTA SELECCIONADO RENUENTE\n¿DESEA BORRAR LA SELECCION?");

                final Button SI = view_alert.findViewById(R.id.BTNSI);
                SI.setOnClickListener(v -> {
                    renuente.setChecked(false);
                    dialog.dismiss();
                });

                final Button NO = view_alert.findViewById(R.id.BTNNO);
                NO.setOnClickListener(v -> dialog.dismiss());
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater Inflater = getLayoutInflater();
            final View view_alert = Inflater.inflate(R.layout.message_yes_no, null);
            builder.setView(view_alert);
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.show();

            final TextView msg = view_alert.findViewById(R.id.CONSULTA);
            msg.setText("NO SE PUEDE CARGAR UNA PERSONA SI\nESTA SELECCIONADO DESHABITADA\n¿DESEA BORRAR LA SELECCION?");

            final Button SI = view_alert.findViewById(R.id.BTNSI);
            SI.setOnClickListener(v -> {
                deshabitada.setChecked(false);
                dialog.dismiss();
            });

            final Button NO = view_alert.findViewById(R.id.BTNNO);
            NO.setOnClickListener(v -> dialog.dismiss());
        }
    }

    private void ListeVer(){
        names.clear();
        for (int i=0; i<MiembrosFamiliares.size(); i++){
            names.add(MiembrosFamiliares.get(i).Apellido+" "+MiembrosFamiliares.get(i).Nombre+" ("+MiembrosFamiliares.get(i).DNI+")");
        }
        adapter = new ArrayAdapter<>(this, R.layout.spiner_personalizado, names);
        lv1.setAdapter(adapter);

        // Elegir entre eliminar y editar

        //realizar accion con el listview
        lv1.setOnItemClickListener((parent, view, p1, id) -> {
            //Elegir entre Eliminar y Editar
            position=p1;
            EliminarEditar();
        });
    }

    private void EliminarEditar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.alert_eliminar_editar, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView txtnombre = view1.findViewById(R.id.MOSTRARNOMBREAPELLIDO);
        String infoPerson = MiembrosFamiliares.get(position).Nombre+" "+MiembrosFamiliares.get(position).Apellido;
        txtnombre.setText(infoPerson);

        TextView txtdni = view1.findViewById(R.id.MOSTRARDNI);
        txtdni.setText(MiembrosFamiliares.get(position).DNI);

        TextView txtfecha = view1.findViewById(R.id.MOSTRARFECHANACIMIENTO);
        txtfecha.setText( MiembrosFamiliares.get(position).Nacimiento);

        TextView riesgo = view1.findViewById(R.id.MOSTRARFACTORRIESGO);
        riesgo.setText(MiembrosFamiliares.get(position).FactoresDeRiesgo);

        TextView txtcelular = view1.findViewById(R.id.MOSTRARCELULAR);
        String infoCel = "CEL: "+  MiembrosFamiliares.get(position).Celular;
        txtcelular.setText(infoCel);

        TextView txtfijo = view1.findViewById(R.id.MOSTRARFIJO);
        String infoFijo = "FIJO: "+  MiembrosFamiliares.get(position).Fijo;
        txtfijo.setText(infoFijo);

        TextView txtnombrecontacto = view1.findViewById(R.id.MOSTRARPERSONACONACTO);
        txtnombrecontacto.setText(MiembrosFamiliares.get(position).NombreContacto);

        TextView telcontacto = view1.findViewById(R.id.MOSTRARTELEFONOCONTCTO);
        telcontacto.setText(MiembrosFamiliares.get(position).TelefonoContacto);

        Button editar = view1.findViewById(R.id.EDITAR);
        //Toast.makeText(getBaseContext(), MiembrosFamiliares.get(position).Sexo, Toast.LENGTH_SHORT).show();
        editar.setOnClickListener(view -> {
            Intent Modif= new Intent (getBaseContext(), MenuPersona.class);
            MiembrosFamiliares.get(position).DeleteContext();

            if (adminBData.ExistPersonFiliatories(MiembrosFamiliares.get(position))){
                if(AddData){
                    Toast.makeText(getApplicationContext(), "Agregar nuevo registro", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Editar registro", Toast.LENGTH_SHORT).show();
                }
            }else {
                //Toast.makeText(getApplicationContext(), "Editar antes de guardar", Toast.LENGTH_SHORT).show();
            }

            Modif.putExtra("PERSONA", MiembrosFamiliares.get(position));
            Modif.putExtra("AddData", AddData.toString());
            MiembrosFamiliares.remove(position);
            startActivityForResult(Modif, 1);
            dialog.dismiss();
        });

        ImageButton cancelar = view1.findViewById(R.id.CANCELAREDICION);
        cancelar.setOnClickListener(view -> dialog.dismiss());

        Button eliminar = view1.findViewById(R.id.ELIMINAR);
        eliminar.setOnClickListener(view -> {
            MiembrosFamiliares.remove(position);
            names.remove(position);
            ListeVer();
            dialog.dismiss();
        });
    }

    // Recibir los datos de la carga de personas
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode == RESULT_OK){
                PersonClass Persona=new PersonClass(getBaseContext());

                assert data != null;
                Bundle bundle = data.getExtras();
                if(bundle!=null){
                    Persona = (PersonClass) bundle.getSerializable("PERSONA");
                    Persona.ActualizeContext(this);
                    Persona.Latitud = familia.Latitud;
                    Persona.Longitud = familia.Longitud;
                    Persona.Fecha = familia.Fecha;
                    //Toast.makeText(this, Persona.Data.get(getString(R.string.antecedentes_cancer_colon)) + "ENFAMILIA", Toast.LENGTH_SHORT).show();
                }

                //Agrego la persona como miembro de la familia
                MiembrosFamiliares.add(Persona);
            }

            if(resultCode == 3){
                FamiliarUnityClass aux = new FamiliarUnityClass(this);
                assert data != null;
                Bundle bundle = data.getExtras();
                if(bundle!=null){
                    aux = (FamiliarUnityClass) bundle.getSerializable("DENGUE");
                }
                //Toast.makeText(this, aux.RecipientesPlasticos, Toast.LENGTH_SHORT).show();
                familia.SituacionVivienda = aux.SituacionVivienda;
                familia.TipoTrabajo = aux.TipoTrabajo;
                familia.ElementosDesuso = aux.ElementosDesuso;
                familia.Botellas = aux.Botellas;
                familia.RecipientesPlasticos = aux.RecipientesPlasticos;
                familia.Macetas = aux.Macetas;
                familia.Hueco = aux.Hueco;
                familia.Canaleta = aux.Canaleta;
                familia.Cubiertas = aux.Cubiertas;
                familia.Piletas = aux.Piletas;
                familia.Tanques = aux.Tanques;
                familia.TotalFocoAedico = aux.TotalFocoAedico;
                familia.TotalIspeccionado = aux.TotalIspeccionado;
                familia.TotalTratados = aux.TotalTratados;
                familia.Larvicida = aux.Larvicida;
                familia.Destruidos = aux.Destruidos;

                familia.dataAedes = aux.dataAedes;
                ColorAvanceDengue();
            }
                ListeVer();
        }
    }


//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // SERVICIOS BASICOS
    public void Dengue(View view){
        Intent Modif= new Intent (this, AedesButton.class);
        Bundle bundle = new Bundle();
        familia.DeleteContext();
        bundle.putSerializable("DATOSDENGUE", familia);
        Modif.putExtras(bundle);
        startActivityForResult(Modif, 1);
    }

    private void ColorAvanceDengue(){
        // Cambio los colores de avance

        float avance = 0;
        if (!familia.Tanques.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.Piletas.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.Cubiertas.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.Canaleta.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.Hueco.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.Macetas.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.RecipientesPlasticos.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.Botellas.equals("0;0;0")){
            avance+=1;
        }
        if (!familia.ElementosDesuso.equals("0;0;0")){
            avance+=1;
        }
        if (familia.SituacionVivienda.length()!=0){
            avance+=1;
        }
        if (familia.TipoTrabajo.length()!=0){
            avance+=1;
        }
        if (familia.TotalTratados.length()!=0){
            avance+=1;
        }
        if (familia.TotalFocoAedico.length()!=0){
            avance+=1;
        }
        if (familia.TotalIspeccionado.length()!=0){
            avance+=1;
        }
        if (familia.Larvicida.length()!=0){
            avance+=1;
        }
        if (familia.Destruidos.length()!=0){
            avance+=1;
        }
        if (familia.HieloCalle.length()!=0){
            avance+=1;
        }
        if (familia.PerrosCalle.length()!=0){
            avance+=1;
        }

        if(avance>0 && avance<18){
            CLDengue.setBackgroundResource(R.drawable.amarillo);
            double porcentaje = Math.round((avance/18)*100);
            String aux = getString(R.string.completado)+" "+ porcentaje +"%";
            avanceDengue.setText(aux);
        }
        if(avance==18){
            CLDengue.setBackgroundResource(R.drawable.verde);
            String avanceText = getString(R.string.completado)+" 100%";
            avanceDengue.setText(avanceText);
        }
        if(avance==0){
            CLDengue.setBackgroundResource(R.drawable.rojo);
            String avanceText = getString(R.string.completado)+" 00%";
            avanceDengue.setText(avanceText);
        }
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // FAMILIA GENERAL
    public void familiaGeneral(View view){
        RadioButton deshabitada = findViewById(R.id.CHECKDESHABITADA);
        RadioButton renuente = findViewById(R.id.CHECKRENUENTE);
        RadioButton vacia_habitada = findViewById(R.id.CHECKVACIAHABITADA);
        //Log.e("renuente deshabitada", Boolean.toString(renuente.isChecked())+Boolean.toString(deshabitada.isChecked()));
        GeneralDescriptionFamiliarButton generalDescriptionFamiliarButton = new GeneralDescriptionFamiliarButton(this, familia,
                this.findViewById(android.R.id.content), renuente.isChecked(), deshabitada.isChecked(), vacia_habitada.isChecked());
        generalDescriptionFamiliarButton.vista();
        familia = generalDescriptionFamiliarButton.returnFamilia();
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // Boton atras
    @Override
    public void onBackPressed(){
        if(MiembrosFamiliares.size()==0){
            locationManager.removeUpdates(locationListener);
            finish();
        }else{
                /* Si no esta iniciado el servicio creo una alert para solicitar el inicio del gps*/
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater Inflater = getLayoutInflater();
                View view1 = Inflater.inflate(R.layout.message_yes_no, null);
                builder.setView(view1);
                builder.setCancelable(false);
                final AlertDialog dialog = builder.create();
                dialog.show();

                TextView txt1 = view1.findViewById(R.id.CONSULTA);
                txt1.setText("¿Cerrar y borrar datos?");

                Button si = view1.findViewById(R.id.BTNSI);
                si.setOnClickListener(view2 -> {
                    finish();
                });

                Button no = view1.findViewById(R.id.BTNNO);
                no.setOnClickListener(view2 -> {
                    dialog.dismiss();
                });
        }
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // GUARDAR GRUPO FAMILIAR
    public void SaveWithHouse(View view){
        SaveDB(view);
        RadioButton deshabitada = findViewById(R.id.CHECKDESHABITADA);
        RadioButton renuente = findViewById(R.id.CHECKRENUENTE);
        RadioButton vacia_habitada = findViewById(R.id.CHECKVACIAHABITADA);
        if (!renuente.isChecked() && !deshabitada.isChecked() && !vacia_habitada.isChecked()){
            familiaGeneral(view);
        }
    }

    public void getDataDomicilio(){
        Archivos archivos = new Archivos(this);
        ArrayList<String> cat = archivos.getCodeCabecerasDomicilio(this);

        Object[] keys = familia.getDataCache(adminBData).keySet().toArray();

        HashMap<String, String> dataAux = familia.getDataCache(adminBData);

        for (String c : cat){

            for (Object o : keys) {
                //Log.e("insert family 0.0", o.toString()+" "+familia.Data.get(o.toString()));
                if (c.equals(o.toString())) {
                    familia.Data.put(c, dataAux.get(c));
                }
            }
        }
        //Log.e("insert family 0.1", familia.Data.get("REDO14_0"));
        adminBData.deleteCache();
    }

    public void SaveDB(View view){
        getDataDomicilio();
        //makeText(getBaseContext(), "tipo de vivienda" + familia.Data.get("REDO14_0"), LENGTH_SHORT).show();
        //Log.e("insert family 0", familia.Data.get("REDO14_0")+" values");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        final View view_alert_save = Inflater.inflate(R.layout.message_yes_no, null);
        builder.setView(view_alert_save);
        builder.setCancelable(false);
        final AlertDialog dialog_alert_save = builder.create();
        dialog_alert_save.show();

        Button btnYes = view_alert_save.findViewById(R.id.BTNSI);

        btnYes.setOnClickListener(view1 -> {
            //Log.e("insert family 1", familia.Data.get("REDO14_0")+" values");
            //CHECKEO DE LA SITUACION HABITACIONAL
            RadioButton deshabitada = findViewById(R.id.CHECKDESHABITADA);
            RadioButton renuente = findViewById(R.id.CHECKRENUENTE);
            RadioButton vacia_habitada = findViewById(R.id.CHECKVACIAHABITADA);

            familia.data_enuestador = encuestador.datosEncuestador();
            if(deshabitada.isChecked()){
                familia.SituacionHabitacional = getString(R.string.vivienda_deshabitada);
            }
            if(renuente.isChecked()){
                familia.SituacionHabitacional = getString(R.string.vivienda_renuente);
            }
            if (vacia_habitada.isChecked()){
                familia.SituacionHabitacional = "VACIA HAB.";
            }
            if(MiembrosFamiliares.size()!=0){
                familia.SituacionHabitacional = getString(R.string.habitada);
            }

            if(familia.Latitud.length()==0 && familia.Longitud.length()==0 || AddData) {

                if (!familia.SituacionHabitacional.equals("")) {
                    if (Latitudenviar != 0.0 && Longitudenviar != null) {
                        for (int k = 0; k < MiembrosFamiliares.size(); k++) {
                            PersonClass value = MiembrosFamiliares.get(k);
                            MiembrosFamiliares.get(k).Data.put(getString(R.string.latitud), Double.toString(Latitudenviar));
                            MiembrosFamiliares.get(k).Data.put(getString(R.string.longitud), Double.toString(Longitudenviar));
                            MiembrosFamiliares.get(k).Data.put(getString(R.string.situacion_habitacional),familia.SituacionHabitacional);
                            MiembrosFamiliares.get(k).Data.put("FECHA",familia.Fecha);
                            //makeText(getBaseContext(), value.Data.get(getString(R.string.antecedentes_cancer_colon))+"ANTESDEGUARAR", LENGTH_SHORT).show();

                            value.LoadData();
                            Object[] keys = MiembrosFamiliares.get(k).Data.keySet().toArray();
                            for (Object i : keys){
                                //Log.e(i.toString(), MiembrosFamiliares.get(k).Data.get(i.toString())+" values data en familia");
                            }
                            adminBData.insert_person(value);
                        }
                        //Log.e("insert family 2", familia.Data.get("REDO14_0")+" values");
                        encuestador.CreateNotification(admin, MiembrosFamiliares);
                        //makeText(getBaseContext(), , LENGTH_SHORT).show();
                        if (!AddData) {
                            familia.Latitud = Latitudenviar.toString();
                            familia.Longitud = Longitudenviar.toString();
                        }
                        familia.ActualizeContext(getBaseContext());
                        familia.LoadData();
                        //familia.Data.putAll(adminBData.getValuesCacheUD());
                        //Log.e("insert family 0", familia.Latitud);
                        adminBData.insert_family(familia);
                        adminBData.CoordinateAssignation();

                        /* Guardo Dengue si es que se cargaron datos en este modulo*/
                        if (familia.TipoTrabajo.length() != 0) {
                            familia.dataAedes.put("LATITUD", familia.Latitud);
                            familia.dataAedes.put("LONGITUD", familia.Longitud);
                            familia.dataAedes.put("ENCUESTADOR", encuestador.datosEncuestador());
                            familia.dataAedes.put("FECHA", familia.Fecha);
                            adminBData.insertValuesAedes(familia.dataAedes);
                        }
                        //makeText(getBaseContext(), "FAMILIA GUARDADA", LENGTH_SHORT).show();
                        dialog_alert_save.dismiss();
                        finish();
                    }else{
                        makeText(getBaseContext(), "NO SE PUEDE TOMAR DATOS DEL GPS", LENGTH_SHORT).show();
                    }
                } else {
                    makeText(getBaseContext(), "DEBE INGRESAR UNA PERSONA O INDICAR QUE LA VIVIENDA ESTA RENUENTE O DESHABITADA", LENGTH_SHORT).show();
                }
            }
            else{
                for (int i=0; i<MiembrosFamiliares.size(); i++){
                    makeText(getBaseContext(), MiembrosFamiliares.get(i).Fecha, LENGTH_SHORT).show();
                    MiembrosFamiliares.get(i).Fecha = familia.Fecha;
                    if(adminBData.ExistPerson(MiembrosFamiliares.get(i))){
                        adminBData.UpdatePerson(MiembrosFamiliares.get(i));
                    }else{
                        PersonClass value = MiembrosFamiliares.get(i);
                        value.Latitud = familia.Latitud;
                        value.Longitud = familia.Longitud;
                        value.Fecha = familia.Fecha;
                        value.LoadData();
                        adminBData.insert_person(value);
                    }

                }
                //Log.e("insert family 1.1", familia.codigoColor[0]);
                familia.LoadData();

                adminBData.UpdateFamily(familia);
                adminBData.CoordinateAssignation();
                dialog_alert_save.dismiss();
                finish();
            }
        });

        Button btnNO = view_alert_save.findViewById(R.id.BTNNO);
        btnNO.setOnClickListener(view12 -> dialog_alert_save.dismiss());

        TextView msg = view_alert_save.findViewById(R.id.CONSULTA);
        msg.setText(getString(R.string.guardar_datos));
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> aux = familia.getDataCache(adminBData);
        //Log.e("familia", "esta aca");
        Object[] objs = aux.keySet().toArray();
        if (objs.length!=0 && objs!=null){
        for (Object obj : objs){
            //Log.e(obj.toString()+" cargado en familia", aux.get(obj.toString())+" value");
        }}
        adminBData.deleteCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cierro la BD
        admin.close();
        adminBData.close();
    }

}