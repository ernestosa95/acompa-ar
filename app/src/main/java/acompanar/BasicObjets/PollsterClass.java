package acompanar.BasicObjets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import acompanar.ManagementModule.StorageManagement.BDUbicationManager;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.ButtonManagement.ButtonDeclaration;
import acompanar.ManagementModule.UbicationManagement.Ubicacion;
import acompanar.ManagementModule.StorageManagement.SQLitePpal;
import com.example.acompanar.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.*;
import static android.os.Environment.getExternalStorageDirectory;

import androidx.constraintlayout.widget.ConstraintLayout;

public class PollsterClass extends Activity implements Serializable {
    // DEFINICIONES DE VARIABLES A UTILIZAR
    private final List<String> Provincias = Arrays.asList("","ENTRE RÍOS");/*,"SANTA FE", "BUENOS AIRES","CABA","CATAMARCA",
                                    "CÓRDOBA","TIERRA DEL FUEGO","TUCUMÁN","SANTA CRUZ","RÍO NEGRO","CHUBUT",
                                    "MENDOZA","SAN JUAN","LA PAMPA","CHACO","CORRIENTES","MISIONES","FORMOSA",
                                    "SANTIAGO DEL ESTERO","SAN LUIS","LA RIOJA","SALTA","JUJUY","NEUQUÉN");*/

    private List<String> FamilyButtons = new ArrayList<String>();//Arrays.asList("DENGUE"),"VIVIENDA""INSPECCION EXTERIOR","SERVICIOS BASICOS"

    private List<String> PersonButtons = new ArrayList<String>(Arrays.asList());

    private final List<String> Notifications = Arrays.asList("HPV");

    private final List<String> Botones = new ArrayList<>();

    public String Nombre, Apellido, DNI="", Provincia, EfectorTrabajo;
    private Context context;
    public Ubicacion ubicacion;
    public Archivos archivos;

    ButtonDeclaration buttonDeclaration;

    private ConstraintLayout BtnInspeccionExterior, BtnDengue, BtnServiciosBasicos, BtnVivienda,
                                    BtnEducacion, BtnOcupacion, BtnContacto, BtnEfector, BtnObservaciones, BtnHPV,
                                    BtnFactoresRiesgo, BtnDiscapacidad, BtnEmbarazo, BtnVitaminaD, BtnEnfermedadesCronicas,
                                    BtnAcompañamiento, BtnTranstornosNiños, BtnTranstornosMentales,
                                    BtnAdicciones, BtnViolencia, BtnOcio;

    public HashMap<String, ConstraintLayout> btnAll = new HashMap<>();
    SQLitePpal adminPpal;

    // CONSTRUCTOR: Obtengo los datos del encuestador que esta activado
    @SuppressLint("ResourceType")
    public PollsterClass(Context baseContext){
        context = baseContext;
        PersonButtons.addAll(Arrays.asList());

        //"EDUCACION","EFECTOR","OBSERVACIONES","CONTACTO","INGRESO Y OCUPACION",,"HPV""ACOMPAÑAMIENTO","TRASTORNOS EN NIÑOS","TRASTORNOS MENTALES","ADICCIONES","VIOLENCIA","OCIO"
        buttonDeclaration = new ButtonDeclaration(context);
        FamilyButtons.addAll(buttonDeclaration.FamilyButtons);
        PersonButtons.addAll(buttonDeclaration.PersonButtons);

        Botones.addAll(FamilyButtons);
        Botones.addAll(PersonButtons);

        archivos = new Archivos(context);
        ubicacion = new Ubicacion(context);
        adminPpal = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
        HashMap<String, String> encuestador_activado = adminPpal.encuestadorActivado();
        if (encuestador_activado.size()!=0){
            Nombre = encuestador_activado.get("NOMBRE");
            Apellido = encuestador_activado.get("APELLIDO");
            DNI = encuestador_activado.get("DNI");
            Provincia = encuestador_activado.get("PROVINCIA");
        }
        EfectorTrabajo="";
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // Vistas y control de los botones
    private void DeclaredButtonsView(View view){
        // Definicion de los botones de familia
        //BtnInspeccionExterior = (ConstraintLayout) view.findViewById(R.id.BTNEXTERIOR);
        //btnAll.put(context.getString(R.string.externo_vivienda),BtnInspeccionExterior);
        //BtnServiciosBasicos = (ConstraintLayout) view.findViewById(R.id.BTNSERVICIOSBASICOS);
        //btnAll.put(context.getString(R.string.servicios_basicos),BtnServiciosBasicos);
        //BtnVivienda = (ConstraintLayout) view.findViewById(R.id.BTNVIVIENDAOLD);
        //btnAll.put(context.getString(R.string.vivienda),BtnVivienda);
        BtnDengue = (ConstraintLayout) view.findViewById(R.id.BTNDENGUE);
        btnAll.put("DENGUE",BtnDengue);

        // Definicion de los botones de persona
        //BtnEducacion = (ConstraintLayout) view.findViewById(R.id.BTNEDUCACION);
        //btnAll.put(context.getString(R.string.educacion),BtnEducacion);
        //BtnOcupacion = (ConstraintLayout) view.findViewById(R.id.BTNTRABAJO);
        //btnAll.put(context.getString(R.string.ocupacion),BtnOcupacion);
        //BtnContacto = (ConstraintLayout) view.findViewById(R.id.BTNCONTACTO);
        //btnAll.put(context.getString(R.string.contacto),BtnContacto);
        //BtnEfector = (ConstraintLayout) view.findViewById(R.id.BTNEFECTOR);
        //btnAll.put(context.getString(R.string.efector),BtnEfector);
        //BtnObservaciones = (ConstraintLayout) view.findViewById(R.id.BTNOBSERVACIONES);
        //btnAll.put(context.getString(R.string.observaciones),BtnObservaciones);
        //BtnHPV = (ConstraintLayout) view.findViewById(R.id.BTNHPV);
        //btnAll.put(context.getString(R.string.HPV),BtnHPV);
        //BtnFactoresRiesgo = (ConstraintLayout) view.findViewById(R.id.BTNFACTORES);
        //btnAll.put(context.getString(R.string.factores_riesgo),BtnFactoresRiesgo);
        //BtnDiscapacidad = (ConstraintLayout) view.findViewById(R.id.BTNDISCAPACIDAD);
        //btnAll.put(context.getString(R.string.discapacidad),BtnDiscapacidad);
        //BtnEmbarazo = (ConstraintLayout) view.findViewById(R.id.BTNEMBARAZO);
        //btnAll.put(context.getString(R.string.embarazo),BtnEmbarazo);
        //BtnVitaminaD = (ConstraintLayout) view.findViewById(R.id.BTNVITAMINA);
        //btnAll.put(context.getString(R.string.vitamina),BtnVitaminaD);
        //BtnEnfermedadesCronicas = (ConstraintLayout) view.findViewById(R.id.BTNENFERMEDADCRONICA);
        //btnAll.put(context.getString(R.string.enfermedad_cronica),BtnEnfermedadesCronicas);
        //BtnAcompañamiento = (ConstraintLayout) view.findViewById(R.id.BTNACOMPAÑAMIENTO);
        //btnAll.put(context.getString(R.string.acompañamiento),BtnAcompañamiento);
        //BtnTranstornosNiños = (ConstraintLayout) view.findViewById(R.id.BTNTRANSTORNOSENNIÑOS);
        //btnAll.put(context.getString(R.string.transtornos_en_niños),BtnTranstornosNiños);
        //BtnTranstornosMentales = (ConstraintLayout) view.findViewById(R.id.BTNTRANSTORNOSMENTALES);
        //btnAll.put(context.getString(R.string.transtornos_mentales),BtnTranstornosMentales);
        //BtnAdicciones = (ConstraintLayout) view.findViewById(R.id.BTNADICCIONES);
        //btnAll.put(context.getString(R.string.adicciones),BtnAdicciones);
        //BtnViolencia = (ConstraintLayout) view.findViewById(R.id.BTNVIOLENCIA);
        //btnAll.put(context.getString(R.string.violencia),BtnViolencia);
        //BtnOcio = (ConstraintLayout) view.findViewById(R.id.BTNOCIO);
        //btnAll.put(context.getString(R.string.ocio),BtnOcio);
    }

    public void ButtonsActivateDesactivate(View view, ArrayList<String> buttons){
        DeclaredButtonsView(view);

        for(int i=0; i<buttons.size();i++){
            SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
                if(btnAll.get(buttons.get(i))!=null) {
                    if (!admin.EstadoBoton(buttons.get(i))) {
                        btnAll.get(buttons.get(i)).setVisibility(View.GONE);
                    } else {
                        btnAll.get(buttons.get(i)).setVisibility(View.VISIBLE);
                    }
                }
            admin.close();
        }
    }

    public void ADbtn(HashMap<String, View> childs, ArrayList<String> buttons){
        for(int i=0; i<buttons.size();i++){
            SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
            if(childs.get(buttons.get(i))!=null) {
                if (!admin.EstadoBoton(buttons.get(i))) {
                    childs.get(buttons.get(i)).setVisibility(View.GONE);
                    //btnAll.get(buttons.get(i)).setVisibility(View.GONE);
                } else {
                    childs.get(buttons.get(i)).setVisibility(View.VISIBLE);
                }
            }
            admin.close();
        }
    }

    public ArrayList<String> Botones(){
        return new ArrayList<String>(Botones);
    }

    public ArrayList<String> Notifications(){return new ArrayList<String>(Notifications);}

    public ArrayList<String> FamilyButtons(){
        return new ArrayList<String>(FamilyButtons);
    }

    public ArrayList<String> PersonButtons(){
        return new ArrayList<String>(PersonButtons);
    }

    public Switch comportamientoSwitch(Switch switch_editar){
        final SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
        String nombre = switch_editar.getText().toString();
        if(admin.EstadoBoton(nombre)){
            switch_editar.setChecked(true);
        }
        else{
            switch_editar.setChecked(false);
        }

        switch_editar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("WrongConstant")
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    admin.ActivarBoton(switch_editar.getText().toString());
                } else {
                    admin.DesactivarBoton(switch_editar.getText().toString());
                }
            }
        });
        return switch_editar;
    }

    @SuppressLint("ResourceAsColor")
    public final Switch ButtonSwitch(LinearLayout Contenedor, int AltoContenedor, String Texto, int TamañoLetra, String color){
        LinearLayout layout16       = new LinearLayout(context);
        layout16.setOrientation(LinearLayout.HORIZONTAL);
        layout16.setVerticalGravity(Gravity.CENTER_VERTICAL);
        final Switch sabin = new Switch(context);
        sabin.setText(Texto);
        sabin.setTextSize(TamañoLetra);
        sabin.setTextColor(WHITE);
        sabin.setButtonTintList(ColorStateList.valueOf(WHITE));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        lp.setMargins(10, 15, 10, 15);
        sabin.setLayoutParams(lp);

        layout16.addView(sabin);

        layout16.setBackgroundColor(parseColor(color));
        layout16.setMinimumHeight(AltoContenedor);
        Contenedor.addView(layout16);
        return sabin;
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    public CheckBox DateCheckSelect(LinearLayout Contenedor, String Texto,
                                    int TamañoLetra, String ColorPares, int AltoContenedor){
            LinearLayout layout16 = new LinearLayout(context);
            layout16.setOrientation(LinearLayout.HORIZONTAL);
            layout16.setVerticalGravity(Gravity.CENTER_VERTICAL);
            final CheckBox sabin = new CheckBox(context);
            sabin.setText(Texto);
            sabin.setTextSize(TamañoLetra);
            sabin.setTextColor(WHITE);
            sabin.setButtonTintList(ColorStateList.valueOf(WHITE));
            layout16.addView(sabin);
            layout16.setBackgroundColor(Color.parseColor(ColorPares));
            layout16.setMinimumHeight(AltoContenedor);
            Contenedor.addView(layout16);
            return sabin;
    }

    public void DeleteContext(){context = null;}

    public String datosEncuestador(){
        return (Apellido+" "+Nombre+" "+DNI);
    }

    public boolean checkUbicacionGPS(ContentResolver contentResolver){
        return ubicacion.checkUbicacionGPS(contentResolver);
    }

    public boolean existe(String nombre, String apellido){
        SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
        return admin.existeEncuestador(nombre,apellido);
    }

    public void activarUsuario(String nombre, String apellido){
        SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
        admin.desactivarUsuarios();
        admin.activarUsuario(nombre, apellido);
    }

    public ArrayList<String> Provincias(){
        return new ArrayList<String>(Provincias);
    }

    public void crearUsuario(String nombre, String apellido, String dni, String provincia){
        SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
        admin.desactivarUsuarios();
        admin.CrearUsuario(nombre, apellido, dni, provincia);
    }

    public void cerrarSesion(){
        SQLitePpal admin = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
        admin.desactivarUsuarios();
    }

    public String ID="";

    public void setID(String ID) {this.ID = ID;}

    public String getID() {
        return ID;
    }

    public String getAllEncuentadores(){
        String data = "";
        ArrayList<String> users = adminPpal.AllEncuestadores();
        for (int i=0; i<users.size(); i++){
            data+=users.get(i)+"-";
        }
        return data;
    }

    public void GuardarRecorrido(String Latitud, String Longitud){
        //permisosEscribir();
        // Agrego la cabecera en .csv
        File ReleVar = new File(Environment.getExternalStorageDirectory() +
                "/RelevAr");
        File nuevaCarpeta = new File(ReleVar, "RECORRIDOS");
        nuevaCarpeta.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date1 = new Date();
        String fecha = dateFormat.format(date1);
        String NombreArchivo = "Recorridos-"+fecha+".csv";

        File dir = new File(nuevaCarpeta, NombreArchivo);

        Calendar calendario = new GregorianCalendar();
        int hora, minutos, segundos;
        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);

        if(Latitud!=null && Longitud!=null){

            String cabecera = Integer.toString(hora)+":"+Integer.toString(minutos)+":"+Integer.toString(segundos)+";"+Latitud+" "+Longitud+";"
                    +";"+CantidadRegistros()+";"+ID+"\n";
            try {
                FileOutputStream fOut = new FileOutputStream(dir, true); //el true es para
                // que se agreguen los datos al final sin perder los datos anteriores
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(cabecera);
                myOutWriter.close();
                fOut.close();

            } catch (IOException e){
                e.printStackTrace();
            }}

        //Agrego en esta parte el guardar en una base de datos con el objetivo de reemplazar el csv
        BDUbicationManager bdUbicationMannager = new BDUbicationManager(context, "BDData", null, 1);
        int dia =calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH);
        int año = calendario.get(Calendar.YEAR);
        String date = Integer.toString(dia)+":"+Integer.toString(mes+1)+":"+Integer.toString(año);
        String time = Integer.toString(hora)+":"+Integer.toString(minutos)+":"+Integer.toString(segundos);
        Log.e("Coordenadas", bdUbicationMannager.insertUbicationBD(date, time, Latitud, Longitud, DNI));
        bdUbicationMannager.close();
    }

    private String CantidadRegistros(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date1 = new Date();
        String fecha = dateFormat.format(date1);
        String NombreArchivo = "RelevAr-"+fecha+".csv";

        int cant = 0;
        String linea = "";
        try {
            File nuevaCarpeta = new File(getExternalStorageDirectory(), "RelevAr");
            nuevaCarpeta.mkdirs();
            File dir = new File(nuevaCarpeta, NombreArchivo);
            String strLine = "";
            // leer datos
            String myData = "";

            FileInputStream fis = new FileInputStream(dir);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            linea = br.readLine();

            while((linea =br.readLine())!= null){
                cant++;
            }

            br.close();
            in.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.toString(cant);
    }

    public ArrayList<LatLng> Marcadores (String fechaVisualizacion){
        ArrayList<LatLng> marcadores = new ArrayList<>();
        String linea;
        try {
            File nuevaCarpeta = new File(getExternalStorageDirectory(), "RelevAr");
            nuevaCarpeta.mkdirs();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date1 = new Date();
            String fecha = dateFormat.format(date1);
            String NombreArchivo = "RelevAr-" +fechaVisualizacion;//+ fecha + ".csv";
            File dir = new File(nuevaCarpeta, NombreArchivo);
            String strLine = "";
            // leer datos
            String myData = "";

            FileInputStream fis = new FileInputStream(dir);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            linea = br.readLine();
            while((linea =br.readLine())!= null){
                String[] aux = linea.split(";");
                String[] coordenadas = aux[2].split(" ");
                marcadores.add(new LatLng(Double.parseDouble(coordenadas[0]), Double.parseDouble(coordenadas[1])));
            }

            br.close();
            in.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return marcadores;
    }

    public ArrayList<String> CodigoColores (String fechaInteres){
        ArrayList<String> marcadores = new ArrayList<>();
        String linea;
        try {
            File nuevaCarpeta = new File(getExternalStorageDirectory(), "RelevAr");
            nuevaCarpeta.mkdirs();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date1 = new Date();
            String fecha = dateFormat.format(date1);
            String NombreArchivo = "RelevAr-" +fechaInteres;//+ fecha + ".csv";
            File dir = new File(nuevaCarpeta, NombreArchivo);
            String strLine = "";
            // leer datos
            String myData = "";

            FileInputStream fis = new FileInputStream(dir);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            linea = br.readLine();
            while((linea =br.readLine())!= null){
                String[] aux = linea.split(";");

                marcadores.add(aux[3]);
            }

            br.close();
            in.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return marcadores;
    }

    public ArrayList<String> CodigoCartografia (String fechaInteres){
        ArrayList<String> marcadores = new ArrayList<>();
        String linea;
        try {
            File nuevaCarpeta = new File(getExternalStorageDirectory(), "RelevAr");
            nuevaCarpeta.mkdirs();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date1 = new Date();
            String fecha = dateFormat.format(date1);
            String NombreArchivo = "RelevAr-" +fechaInteres;//+ fecha + ".csv";
            File dir = new File(nuevaCarpeta, NombreArchivo);
            String strLine = "";
            // leer datos
            String myData = "";

            FileInputStream fis = new FileInputStream(dir);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            linea = br.readLine();
            while((linea =br.readLine())!= null){
                String[] aux = linea.split(";");
                if(aux.length<=10){
                    marcadores.add("");
                }
                else {
                    marcadores.add(aux[10]);}
            }

            br.close();
            in.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return marcadores;
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // Vistas de las notificaciones
    public String CreateNotification(SQLitePpal adminPpal, ArrayList<PersonClass> persons){
        String value = "";

        for (int i=0; i<persons.size();i++) {
            if (persons.get(i).HPV.equals("SI")){
                adminPpal.CreateMessage(persons.get(i),"HPV", Boolean.TRUE);
            }
            if (persons.get(i).Data.containsKey(context.getString(R.string.prueba_cancer_colon)) &&
                    persons.get(i).Data.get(context.getString(R.string.prueba_cancer_colon)) != null) {
                if (persons.get(i).Data.get(context.getString(R.string.prueba_cancer_colon)).equals("SI")) {
                    adminPpal.CreateMessage(persons.get(i), "COLON", Boolean.TRUE);
                }
            }
        }
        return value;
    }

    public ArrayList<View> NewNotifications(LinearLayout parent){
        ArrayList<View> values = new ArrayList<>();

        ArrayList<HashMap<String,String>> mssgs = adminPpal.MessagesNew();

        for (int i=0; i<mssgs.size(); i++) {
            View child = getLayoutInflater().inflate(R.layout.notification_message, parent, false);
            TextView mssg = child.findViewById(R.id.TEXTVIEWMESSAGE);
            mssg.setText(mssgs.get(i).get("TYPE_MESSAGE"));
            values.add(child);
        }

        return values;
    }

}
