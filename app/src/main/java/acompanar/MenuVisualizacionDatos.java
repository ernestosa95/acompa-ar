package acompanar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import acompanar.ManagementModule.ShareDataManagement.PDFGenerate;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.BasicObjets.FamiliarUnityClass;
import acompanar.BasicObjets.PersonClass;
import acompanar.ManagementModule.StorageManagement.SQLitePpal;
import com.example.acompanar.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MenuVisualizacionDatos extends AppCompatActivity {

    LinearLayout persons1, persons2;
    String latitude, longitude;
    FamiliarUnityClass familia;
    ArrayList<PersonClass> familiarmembers;

    LinearLayout temporal;
    BDData admin;

    ImageView imageView;
    TextView situacionhabitacional;

    ConstraintLayout lineaBlanca;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacion_datos);

        // Eliminar el action bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        // Evitar la rotacion
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        latitude = getIntent().getStringExtra("LATITUDE");
        longitude = getIntent().getStringExtra("LONGITUDE");
        admin = new BDData(getBaseContext(), "BDData", null, 1);

        imageView = findViewById(R.id.IMAGEVIEWICONMAPA);
        situacionhabitacional = findViewById(R.id.SITUACIONHABITACIONAL);
        lineaBlanca = findViewById(R.id.LineaTemporal);

        //LISTA TEMPORAL
        ConstraintLayout clfamilia = findViewById(R.id.CLFAMILIA);
        clfamilia.setVisibility(View.GONE);

        temporal = findViewById(R.id.LINEARTEMPORAL);

        ArrayList<String> fechas_familia = new ArrayList<>();
        fechas_familia = admin.DatesFamily(latitude, longitude);

        LayoutInflater Inflater = LayoutInflater.from(this);

        for (String fecha : fechas_familia) {
            final View view = Inflater.inflate(R.layout.basic_temporal_event, null);
            TextView txtfecha = view.findViewById(R.id.TXTFECHAREGISTRO);
            txtfecha.setText(fecha);
            TextView personas = view.findViewById(R.id.TXTCANTIDADPERSONAS);
            personas.setText(Integer.toString(FamiliarMembersDate(latitude, longitude, fecha).size()));
            ConstraintLayout color = view.findViewById(R.id.CLESTADOCOLOR);

            ArrayList<String> aux = admin.SearchStuacionHabitacional(latitude,longitude,fecha);
            if(aux.get(1).equals("V")){
                color.setBackgroundResource(R.drawable.verde);
                //Toast.makeText(getBaseContext(), "V" , Toast.LENGTH_SHORT).show();
            }
            if(aux.get(1).equals("A")){
                color.setBackgroundResource(R.drawable.amarillo);
                //Toast.makeText(getBaseContext(), "A" , Toast.LENGTH_SHORT).show();
            }
            if(aux.get(1).equals("R")){
                color.setBackgroundResource(R.drawable.rojo);
                //Toast.makeText(getBaseContext(), "R" , Toast.LENGTH_SHORT).show();
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), fecha , Toast.LENGTH_SHORT).show();
                    ShowDataDate(fecha);
                }
            });

            temporal.addView(view);
        }

        //Busco la ultima situacion habitacional y la seteo en la vista
        String ultimaFecha = fechas_familia.get(fechas_familia.size()-1);
        ArrayList<String> aux = admin.SearchStuacionHabitacional(latitude,longitude,ultimaFecha);
        imageView.setImageResource(R.drawable.button_redondo_blanco);

        if(aux.get(1).equals("V")){
            imageView.setImageResource(R.drawable.icono_mapa_verde);
            //Toast.makeText(getBaseContext(), "V" , Toast.LENGTH_SHORT).show();
        }
        if(aux.get(1).equals("A")){
            imageView.setImageResource(R.drawable.icono_mapa_amarilla);
            //Toast.makeText(getBaseContext(), "A" , Toast.LENGTH_SHORT).show();
        }
        if(aux.get(1).equals("R")){
            imageView.setImageResource(R.drawable.icono_mapa_rojo);
            //Toast.makeText(getBaseContext(), "R" , Toast.LENGTH_SHORT).show();
        }

        situacionhabitacional.setText(aux.get(0));

        ConstraintLayout addDATA = findViewById(R.id.ADDREGISTER);
        addDATA.setVisibility(View.VISIBLE);
        ConstraintLayout editDATA = findViewById(R.id.EDITREGISTER);
        editDATA.setVisibility(View.GONE);
        Button pdf = findViewById(R.id.button11);
        pdf.setVisibility(View.GONE);

        // Codigo de funcionamiento de los tabs
        TabHost tabs = findViewById(R.id.TABVIEWFAMILY);
        tabs.setup();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200,75);
        layoutParams.setMargins(5,5,5,2);

        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.titulo_tabs,null);
        tv.setText(getString(R.string.vivienda));
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        tv.setTextColor(Color.parseColor("#4A4A4A"));
        tv.setTextSize(15);
        tv.setLayoutParams(layoutParams);
        TabHost.TabSpec spec = tabs.newTabSpec("mytab1");
        spec.setContent(R.id.VIVIENDAVIEW);
        spec.setIndicator(tv);
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mytab2");
        spec.setContent(R.id.SERVICIOSBASICOSVIEW);
        TextView tv1 = (TextView)LayoutInflater.from(this).inflate(R.layout.titulo_tabs,null);
        tv1.setText(getString(R.string.servicios_basicos));
        tv1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        tv1.setTextColor(Color.parseColor("#4A4A4A"));
        tv1.setTextSize(13);
        tv1.setLayoutParams(layoutParams);
        spec.setIndicator(tv1);
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mytab3");
        spec.setContent(R.id.INSPECCIONEXTERIORVIE);
        TextView tv3 = (TextView)LayoutInflater.from(this).inflate(R.layout.titulo_tabs,null);
        tv3.setText(getString(R.string.inspeccion_domiciliaria));
        tv3.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        tv3.setTextColor(Color.parseColor("#4A4A4A"));
        tv3.setTextSize(12);
        tv3.setLayoutParams(layoutParams);
        spec.setIndicator(tv3);
        tabs.addTab(spec);

        tabs.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.background_button_light_blue));
        tabs.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));
        tabs.getTabWidget().getChildAt(2).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int tab = tabs.getCurrentTab();
                tabs.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));
                tabs.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));
                tabs.getTabWidget().getChildAt(2).setBackground(getDrawable(R.drawable.background_button_extra_light_grey));
                tabs.getTabWidget().getChildAt(tab).setBackground(getDrawable(R.drawable.background_button_light_blue));
            }
        });
    }

    public void volverTemporal(View view){
        ConstraintLayout clfamilia = findViewById(R.id.CLFAMILIA);
        clfamilia.setVisibility(View.GONE);
        ScrollView scrolltemporal = findViewById(R.id.SCTEMPORAL);
        scrolltemporal.setVisibility(View.VISIBLE);
        ConstraintLayout addDATA = findViewById(R.id.ADDREGISTER);
        addDATA.setVisibility(View.VISIBLE);
        ConstraintLayout editDATA = findViewById(R.id.EDITREGISTER);
        editDATA.setVisibility(View.GONE);
        lineaBlanca.setVisibility(View.VISIBLE);

        //Busco la ultima situacion habitacional y la seteo en la vista
        ArrayList<String> fechas_familia = new ArrayList<>();
        fechas_familia = admin.DatesFamily(latitude, longitude);
        String ultimaFecha = fechas_familia.get(fechas_familia.size()-1);
        ArrayList<String> aux = admin.SearchStuacionHabitacional(latitude,longitude,ultimaFecha);

        if(aux.get(1).equals("V")){
            imageView.setImageResource(R.drawable.icono_mapa_verde);
        }
        if(aux.get(1).equals("A")){
            imageView.setImageResource(R.drawable.icono_mapa_amarilla);
        }
        if(aux.get(1).equals("R")){
            imageView.setImageResource(R.drawable.icono_mapa_rojo);
        }

        situacionhabitacional.setText(aux.get(0));
    }

    private void ShowDataDate(String date){

        ConstraintLayout addDATA = findViewById(R.id.ADDREGISTER);
        addDATA.setVisibility(View.GONE);
        ConstraintLayout editDATA = findViewById(R.id.EDITREGISTER);
        editDATA.setVisibility(View.VISIBLE);
        lineaBlanca.setVisibility(View.GONE);
        Button pdf = findViewById(R.id.button11);
        pdf.setVisibility(View.VISIBLE);

        ConstraintLayout clfamilia = findViewById(R.id.CLFAMILIA);
        clfamilia.setVisibility(View.VISIBLE);
        ScrollView scrolltemporal = findViewById(R.id.SCTEMPORAL);
        scrolltemporal.setVisibility(View.GONE);
        familiarmembers = FamiliarMembersDate(latitude, longitude, date);
        Log.e("cantidad de personas", Integer.toString(familiarmembers.size()));

        persons1 = findViewById(R.id.PERSONSLIST1);
        int clild1 = persons1.getChildCount();
        for (int i=0; i<clild1; i++){
            persons1.getChildAt(i).setVisibility(View.GONE);
        }
        persons2 = findViewById(R.id.PERSONSLIST2);

        for(int i=0; i<familiarmembers.size(); i++){
            if(i<(familiarmembers.size()/2) || i==0){
                persons1.addView(CreateButtonPersons(persons1,
                        familiarmembers.get(i).Data.get("NOMBRE"), familiarmembers.get(i).Data.get("APELLIDO"),
                        familiarmembers.get(i).Data.get("DNI")));
            }else{
                persons2.addView(CreateButtonPersons(persons2,
                        familiarmembers.get(i).Data.get("NOMBRE"), familiarmembers.get(i).Data.get("APELLIDO"),
                        familiarmembers.get(i).Data.get("DNI")));
            }
        }

        familia = new FamiliarUnityClass(this);
        familia = admin.SearchFamilyCoordinateAndDate(this,latitude,longitude,date);
        familia.Latitud=latitude;
        familia.Longitud=longitude;
        familia.LoadDataHashToParameters();



        TextView datosvivienda = findViewById(R.id.TEXTVIEWVIVIENDA);
        String datavivienda = "";
        for(int i=0; i<familia.ViviendaValues().size();i++){
            if (familia.Data.containsKey(familia.ViviendaValues().get(i).replace(".","")) &&
                    familia.Data.get(familia.ViviendaValues().get(i).replace(".",""))!=null &&
                    familia.Data.get(familia.ViviendaValues().get(i).replace(".","")).length()!=0){

                datavivienda+= familia.ViviendaValues().get(i)+": "+
                    familia.Data.get(familia.ViviendaValues().get(i).replace(".",""))+"\n";
            }
        }

        Archivos mngFile = new Archivos(this);
        ArrayList<String> keysUD = mngFile.getCodeCabecerasDomicilio(this);
        HashMap<String, String> mapeo = mngFile.getMapCategoriesDomicilio();
        for (String key : keysUD){
            if (familia.Data.containsKey(key)){
                //Log.e("key marca error", key);
                if (familia.Data.get(key)!=null && familia.Data.get(key).length()!=0) {

                    if (mapeo.get(familia.Data.get(key)) != null && mapeo.get(familia.Data.get(key)).length() != 0
                        && !familia.Data.get(key).equals("SI") && !familia.Data.get(key).equals("NO")) {
                        datavivienda += mapeo.get(key) + ": ";
                        datavivienda += mapeo.get(familia.Data.get(key)) + "\n";
                    } else {
                        datavivienda += mapeo.get(key) + ": ";
                        datavivienda += familia.Data.get(key) + "\n";
                    }
                }
            }
        }
        datosvivienda.setText(datavivienda);

        TextView datoserviciosbasicos = findViewById(R.id.TEXTVIESERVICIOSBASICOS);
        String dataserviciosbasicos = "";
        for(int i=0; i<familia.ServiciosBasicosValues().size();i++){
            if (familia.Data.containsKey(familia.ServiciosBasicosValues().get(i).replace(".","")) &&
                    familia.Data.get(familia.ServiciosBasicosValues().get(i).replace(".",""))!=null){

                dataserviciosbasicos+= familia.ServiciosBasicosValues().get(i)+": "+
                        familia.Data.get(familia.ServiciosBasicosValues().get(i).replace(".",""))+"\n";

            }
        }
        datoserviciosbasicos.setText(dataserviciosbasicos);

        TextView datosinspeccionexterior = findViewById(R.id.TEXTVIEWINSPECCIONEXTERIOR);
        String datainspeccionexterior = "";
        for(int i=0; i<familia.InspeccionExteriorValues().size();i++){
            if (familia.Data.containsKey(familia.InspeccionExteriorValues().get(i).replace(".","").replace("/","")) &&
                    familia.Data.get(familia.InspeccionExteriorValues().get(i).replace(".","").replace("/",""))!=null){

                datainspeccionexterior+= familia.InspeccionExteriorValues().get(i)+": "+
                        familia.Data.get(familia.InspeccionExteriorValues().get(i).replace(".","").replace("/",""))+"\n";

            }
        }
        datosinspeccionexterior.setText(datainspeccionexterior);


        if(familia.Data.get("CODIGO_COLOR").equals("V")){
            imageView.setImageResource(R.drawable.icono_mapa_verde);
        }
        if(familia.Data.get("CODIGO_COLOR").equals("A")){
            imageView.setImageResource(R.drawable.icono_mapa_amarilla);
        }
        if(familia.Data.get("CODIGO_COLOR").equals("R")){
            imageView.setImageResource(R.drawable.icono_mapa_rojo);
        }

        situacionhabitacional.setText(familia.Data.get(getString(R.string.situacion_habitacional)));
    }

    private View CreateButtonPersons(LinearLayout parent, String strname, String strlastname, String strdni){

        View child = getLayoutInflater().inflate(R.layout.button_info_person, parent, false);
        TextView name = child.findViewById(R.id.NOMBREBUTTONVIEW);
        name.setText(strname);
        TextView apellido = child.findViewById(R.id.APELLIDOBUTTONVIEW);
        apellido.setText(strlastname);
        TextView dni = child.findViewById(R.id.DNIBUTTONVIEW);
        String dnimostrar = "("+strdni+")";
        dni.setText(dnimostrar);
        ConstraintLayout button = child.findViewById(R.id.PERSONVIEWBUTTON);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                DataPerson(dni.getText().toString().replace("(", "").replace(")",""));
                //Toast.makeText(getBaseContext(), name.getText().toString() , Toast.LENGTH_SHORT).show();
            }
        });

        return child;
    }

    private ArrayList<PersonClass> FamiliarMembersDate(String latitude, String longitude, String date){
        ArrayList<PersonClass> values = new ArrayList<>();

        values = admin.SearchPersonsCoordinatesAndDate (this, latitude, longitude, date);

        return values;
    }
    private SQLitePpal sqLitePpal;
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void DataPerson(String dni){
        // Defino las caracteristicas
        PersonClass mostrar = new PersonClass(this);
        for (int j=0; j<familiarmembers.size(); j++){
            if(familiarmembers.get(j).Data.get(getString(R.string.dni)).equals(dni)){
                mostrar = familiarmembers.get(j);
            }
        }
        sqLitePpal = new SQLitePpal(getBaseContext(), "DATA_PRINCIPAL", null, 1);
        // Defino los contenedores
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
        TextView textView = new TextView(this);
        String identificacion = "";
        if(mostrar.Data.get(getString(R.string.apellido))!=null){
            identificacion = mostrar.Data.get(getString(R.string.apellido));
        }
        if(mostrar.Data.get(getString(R.string.nombre))!=null){
            identificacion += " " + mostrar.Data.get(getString(R.string.nombre));
        }
        if(mostrar.Data.get(getString(R.string.dni))!=null){
            identificacion += " ("+mostrar.Data.get(getString(R.string.dni))+")";
        }
        textView.setText(identificacion);
        textView.setPadding(20, 30, 20, 30);
        textView.setTextSize(18F);
        textView.setBackgroundColor(Color.parseColor("#4588BC"));
        textView.setTextColor(Color.WHITE);
        builder.setCustomTitle(textView);

        TextView textView1 = new TextView(this);
        textView1.setTextColor(getColor(R.color.colorBlueLigth));

        Archivos mngFile = new Archivos(this);
        ArrayList<String> keysUD = mngFile.getCodeCabecerasPersonasGuardable(this);
        HashMap<String, String> mapeo = mngFile.getMapCategoriesPersonas();
        String aux22 = "";
        for (String key : keysUD){
            if (mostrar.Data.containsKey(key)){
                Log.e("key marca error", key);
                if (mostrar.Data.get(key)!=null && mostrar.Data.get(key).length()!=0) {
                    aux22 += mapeo.get(key) + ": ";
                    if (mapeo.get(mostrar.Data.get(key)) != null) {
                        aux22 += mapeo.get(mostrar.Data.get(key)) + "\n";
                    } else {
                        if (key.equals("EFECTOR")){ aux22 += sqLitePpal.NombreEfector4Code(mostrar.Data.get(key)) + "ef\n"; }
                        aux22 += mostrar.Data.get(key) + "\n";
                    }
                }
            }
        }

        if(aux22.length()==0){aux22="NO SE REGISTRARON DATOS";}
        textView1.setText(aux22);

        builder.setPositiveButton("LISTO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        });

        // Defino un ScrollView para visualizar todos
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        sv.setVerticalScrollBarEnabled(true);
        sv.addView(textView1);

        builder.setView(sv);
        // Create and show the alert dialog
        //builder.setView(textView1);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void EditData(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        final View view_alert = Inflater.inflate(R.layout.message_yes_no, null);
        builder.setView(view_alert);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button btnYes = view_alert.findViewById(R.id.BTNSI);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
                Modif.putExtra("LATITUD", familia.Latitud);
                Modif.putExtra("LONGITUD", familia.Longitud);
                Modif.putExtra("FECHA", familia.Fecha);
                startActivity(Modif);
                dialog.dismiss();
                finish();
            }
        });

        Button btnNO = view_alert.findViewById(R.id.BTNNO);
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView msg = view_alert.findViewById(R.id.CONSULTA);
        msg.setText("¿Desea editar los datos registrados?");
    }

    public void AddData(View view){
        ArrayList<String> fechas_familia = new ArrayList<>();
        fechas_familia = admin.DatesFamily(latitude, longitude);
        String ultimaFecha = fechas_familia.get(fechas_familia.size()-1);

        if (!ultimaFecha.equals(DateFormat.getDateInstance().format(new Date()))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater Inflater = getLayoutInflater();
            final View view_alert = Inflater.inflate(R.layout.message_yes_no, null);
            builder.setView(view_alert);
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            dialog.show();

            familia = admin.SearchFamilyCoordinate(this, latitude, longitude);

            Button btnYes = view_alert.findViewById(R.id.BTNSI);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent Modif = new Intent(getBaseContext(), MenuFamilia.class);
                    Modif.putExtra("LATITUD", latitude);
                    Modif.putExtra("LONGITUD", longitude);

                    startActivity(Modif);
                    finish();

                }
            });

            Button btnNO = view_alert.findViewById(R.id.BTNNO);
            btnNO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            TextView msg = view_alert.findViewById(R.id.CONSULTA);
            msg.setText("¿Desea agregar datos a la familia registrada?");
        }else {
            Toast.makeText(getBaseContext(), "Ya tiene un registro de hoy, para agregar datos puede editarlo" , Toast.LENGTH_SHORT).show();
        }
    }

    public void sendPDF(View view){
        //Archivos archivos = new Archivos(getBaseContext());
        //archivos.CreatePDF(familia, familiarmembers);
        PDFGenerate pdfGenerate = new PDFGenerate(this);
        pdfGenerate.setDataFamilyForPDF(familia);
        pdfGenerate.setDataPersonsForPDF(familia.Data.get("LATITUD"), familia.Data.get("LONGITUD"),familia.Data.get("FECHA"));
        startActivity(pdfGenerate.sharePDF());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cierro la BD
        admin.close();
    }
}