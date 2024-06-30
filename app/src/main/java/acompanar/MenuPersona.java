package acompanar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import acompanar.ManagementModule.QRScannerManagement.ScannerQR;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.StorageManagement.SQLitePpal;
import acompanar.ManagementModule.UbicationManagement.ServicioGPS;
import com.example.acompanar.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import acompanar.BasicObjets.PersonClass;
import acompanar.BasicObjets.PollsterClass;
import acompanar.ButtonsDeclarationPersonal.BotonContactoBasico22;
import acompanar.ButtonsDeclarationPersonal.BotonDescripcionBasica22;
import acompanar.ButtonsDeclarationPersonal.EstadoFisico.BotonVitaminaD;
import acompanar.ButtonsDeclarationPersonal.PsicoSocial.BotonAdicciones;
import acompanar.ButtonsDeclarationPersonal.PsicoSocial.BotonTrastornosMentales;
import acompanar.ButtonsDeclarationPersonal.PsicoSocial.BotonTrastornosNiños;
import acompanar.ButtonsDeclarationPersonal.PsicoSocial.BotonViolencia;
import acompanar.ManagementModule.ButtonManagement.ButtonDeclaration;


public class MenuPersona extends AppCompatActivity {
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
// DEFINICION DE VARIABLES GLOBALES

    // Defino de forma global los String para recibir y devolver la informacion
    SQLitePpal admin;

    CheckBox indocumentado;
    PersonClass Persona;
    PollsterClass encuestador;
    HashMap<String, Switch> switch_buttons = new HashMap<>();
    HashMap<String, View> ButtonsViews = new HashMap<>();

    TextView txtNombre, txtApellido, txtDni, txtSexo, txtNacimiento, txtNacimientoEditar;

    private TabHost tabs;

    BDData adminBData;
    LinearLayout general, fisico, psico;

    BotonDescripcionBasica22 btnDescripcion;

    BotonContactoBasico22 btnContacto;
    //--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
// CLASES DE CREACION Y ACTUALIZACION DE LAS VARIABLES
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_persona);

        // Evitar la rotacion
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        Persona = new PersonClass(getBaseContext());

        //Persona.Fecha = DateFormat.getDateInstance().format(new Date());
        encuestador = new PollsterClass(getApplicationContext());
        encuestador.ButtonsActivateDesactivate(this.findViewById(android.R.id.content), encuestador.PersonButtons());
        Persona.setIdEncuestador(encuestador.DNI);
        // Eliminar el action bar
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.hide();

        adminBData = new BDData(getBaseContext(), "BDData", null, 1);
        adminBData.deleteCache();
        adminBData.deleteCacheUD();
        adminBData.CreateCategoriesCacheUD(this);

        txtNombre = findViewById(R.id.TXTNOMBRE);
        txtApellido = findViewById(R.id.TXTAPELLIDO);
        txtDni = findViewById(R.id.TXTDNI);
        txtSexo = findViewById(R.id.TXTSEXO);
        txtNacimiento = findViewById(R.id.TXTFECHANACIMIENTO);

        if (getIntent().getStringExtra("LATITUD") != null && Objects.requireNonNull(getIntent().getStringExtra("LATITUD")).length() != 0) {
            if (getIntent().getStringExtra("LONGITUD") != null) {
                if (getIntent().getStringExtra("FECHA") != null) {
                    if (getIntent().getStringExtra("NOMBRE") != null) {
                        if (getIntent().getStringExtra("APELLIDO") != null) {
                            Persona = adminBData.SearchPersonsCoordinatesDateNameLastname(this,
                                    getIntent().getStringExtra("LATITUD"),
                                    getIntent().getStringExtra("LONGITUD"),
                                    getIntent().getStringExtra("FECHA"),
                                    getIntent().getStringExtra("NOMBRE"),
                                    getIntent().getStringExtra("APELLIDO")
                            );

                            Persona.LoadDataHashToParamaters();
                            Persona.oldSurname = Persona.Apellido;
                            Persona.oldName = Persona.Nombre;
                            // Inicializo los campos de edicion
                            txtDni.setText(Persona.DNI);
                            txtApellido.setText(Persona.Apellido);
                            txtNombre.setText(Persona.Nombre);

                            txtSexo.setText(Persona.Data.get("SEXO"));

                            if (!Persona.Nacimiento.equals("")) {
                                txtNacimiento.setText(Persona.Nacimiento);
                            } else {
                                txtNacimiento.setText(getString(R.string.fecha_formato));
                            }
                        }
                    }
                }
            }
        }

        if (getIntent().getSerializableExtra("PERSONA") != null) {
            if (!getIntent().getSerializableExtra("PERSONA").equals("")) {

                Persona = (PersonClass) getIntent().getSerializableExtra("PERSONA");
                // paso todos los datos al cacheud
                txtDni.setText(Persona.DNI);
                txtApellido.setText(Persona.Apellido);
                txtNombre.setText(Persona.Nombre);
                txtSexo.setText(Persona.Sexo);

                if (!Persona.Nacimiento.equals("")) {
                    txtNacimiento.setText(Persona.Nacimiento);
                } else {
                    txtNacimiento.setText(getString(R.string.fecha_formato));
                }
            }
        }

        if (Boolean.parseBoolean(getIntent().getStringExtra("AddData"))){
            //Toast.makeText(getBaseContext(), "es un agregar", Toast.LENGTH_SHORT).show();
            fixData();
        }

        // Codigo de funcionamiento de los tabs
        tabs = findViewById(R.id.TABS);
        tabs.setup();

        TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.titulo_tabs, null);
        tv.setText("GENERAL");
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        TabHost.TabSpec spec_general = tabs.newTabSpec("mytab1");
        spec_general.setContent(R.id.GENERAL);
        //spec.setIndicator("GENERAL");
        spec_general.setIndicator(tv);
        tabs.addTab(spec_general);

        TabHost.TabSpec spec_fisico = tabs.newTabSpec("mytab2");
        spec_fisico.setContent(R.id.FISICO);
        TextView tv1 = (TextView) LayoutInflater.from(this).inflate(R.layout.titulo_tabs, null);
        tv1.setText("ESTADO FISICO");
        tv1.setGravity(Gravity.CENTER_HORIZONTAL);
        spec_fisico.setIndicator(tv1);
        tabs.addTab(spec_fisico);

        TabHost.TabSpec spec_psico = tabs.newTabSpec("mytab3");
        spec_psico.setContent(R.id.PSICO);
        TextView tv3 = (TextView) LayoutInflater.from(this).inflate(R.layout.titulo_tabs, null);
        tv3.setText("PSICO SOCIAL");
        tv3.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        spec_psico.setIndicator(tv3);
        tabs.addTab(spec_psico);

        tabs.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#4A4A4A"));
        tabs.getTabWidget().getChildAt(1).setBackgroundColor(Color.BLACK);
        tabs.getTabWidget().getChildAt(2).setBackgroundColor(Color.BLACK);

        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int tab = tabs.getCurrentTab();
                tabs.getTabWidget().getChildAt(0).setBackgroundColor(Color.BLACK);
                tabs.getTabWidget().getChildAt(1).setBackgroundColor(Color.BLACK);
                tabs.getTabWidget().getChildAt(2).setBackgroundColor(Color.BLACK);
                tabs.getTabWidget().getChildAt(tab).setBackgroundColor(Color.parseColor("#4A4A4A"));
            }
        });

        adminBData.updateCacheUD(Persona.Data);

        // Definicion rapida de botones

        ButtonDeclaration buttonDeclaration = new ButtonDeclaration(this);
        // Agrego la vista de cada boton al menu
        general = (LinearLayout) findViewById(R.id.LLGENERAL);
        fisico = (LinearLayout) findViewById(R.id.LLFISICO);
        psico = (LinearLayout) findViewById(R.id.LLPSICO);

        for(int i=0; i<buttonDeclaration.PersonButtons.size(); i++) {
            View addView = buttonDeclaration.ButtonsViews.get(buttonDeclaration.PersonButtons.get(i));
            ButtonsViews.put(   buttonDeclaration.PersonButtons.get(i),
                                addView);

            if (buttonDeclaration.PersonButtonsGeneral.contains(buttonDeclaration.PersonButtons.get(i))) {
                general.addView(addView);
            }
            if (buttonDeclaration.PersonButtonsEstadoFisico.contains(buttonDeclaration.PersonButtons.get(i))) {
                fisico.addView(addView);
            }
            if (buttonDeclaration.PersonButtonsPsico.contains(buttonDeclaration.PersonButtons.get(i))) {
                psico.addView(addView);
            }

            // Otorgo el funcionamiento a los botones
            ConstraintLayout btnAction = addView.findViewById(R.id.BTNGNRL);
            int finalI = i;

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonDeclaration.executeButtons(buttonDeclaration.PersonButtons.get(finalI));
                    //Toast.makeText(getBaseContext(), Persona.Data.get(getString(R.string.realizo_prueba_colon)), Toast.LENGTH_SHORT).show();
                }
            });
        }
        encuestador.ADbtn(ButtonsViews, encuestador.PersonButtons());

        admin = new SQLitePpal(getBaseContext(), "DATA_PRINCIPAL", null, 1);

        View viewGRL = findViewById(R.id.BTNGEN);
        btnDescripcion = new BotonDescripcionBasica22(this, viewGRL, "DESCRIPCION");

        View viewCON = findViewById(R.id.BTNCON);
        btnContacto = new BotonContactoBasico22(this, viewCON, "CONTACTO");
    }

    private void fixData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.message_yes_no, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //obtengo la fecha del ultimo registro
        ArrayList<String> fechas_familia = new ArrayList<>();
        fechas_familia = adminBData.DatesFamily(Persona.Latitud, Persona.Longitud);
        String ultimaFecha = fechas_familia.get(fechas_familia.size()-1);
        String txt = "TENEMOS DATOS PRECARGADOS:";

        PersonClass paux = adminBData.SearchPersonsCoordinatesDateregisterNameLastname(this, Persona.Latitud, Persona.Longitud, ultimaFecha, Persona.Nombre, Persona.Apellido);

        ArrayList<String> keyData = new ArrayList<>();
        Archivos archivos = new Archivos(this);
        keyData.addAll(archivos.getCodeCabecerasPersonasBoton(this, "CONTACTO"));
        keyData.addAll(archivos.getCodeCabecerasPersonasBoton(this, "DESCRIPCION"));
        HashMap<String,String> options = archivos.getMapCategoriesPersonas();

        HashMap<String,String> auxData = new HashMap<>();

        for (String key : keyData){
            if (paux.Data.get(key)!=null) {
                if (paux.Data.get(key).length()!=0) {
                    String aux = options.get(key);
                    aux += ": ";
                    aux += paux.Data.get(key);
                    txt += "\n" + aux;
                    auxData.put(key, paux.Data.get(key));
                }
            }
        }

        if (txt.equals("TENEMOS DATOS PRECARGADOS:")){
            dialog.dismiss();
        }else {
            txt += "\n\n" + "¿Desea que los carguemos?";
        }
        //PersonClass paux = adminBData.
        TextView txt1 = view1.findViewById(R.id.CONSULTA);
        txt1.setText(txt);

        Button si = view1.findViewById(R.id.BTNSI);
        si.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), ServicioGPS.class);
            stopService(intent);

            Persona.Data.putAll(auxData);
            adminBData.updateCacheUD(Persona.Data);
            btnContacto.buttonViewBasic.buttonColor(btnContacto.context, "CONTACTO", btnContacto.layout, btnContacto.avanceTxt);
            btnDescripcion.buttonViewBasic.buttonColor(btnDescripcion.context, "DESCRIPCION", btnDescripcion.layout, btnDescripcion.avanceTxt);
            dialog.dismiss();
        });

        Button no = view1.findViewById(R.id.BTNNO);
        no.setOnClickListener(view -> dialog.dismiss());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){

            if(resultCode == RESULT_OK){

                    //Toast.makeText(this, data.getStringExtra("DNI_ESCANEADO"), Toast.LENGTH_SHORT).show();
                    txtDni.setText(data.getStringExtra("DNI_ESCANEADO"));
                    Persona.DNI = data.getStringExtra("DNI_ESCANEADO");
                    txtApellido.setText(data.getStringExtra("APELLIDO_ESCANEADO"));
                    Persona.Apellido = data.getStringExtra("APELLIDO_ESCANEADO");
                    txtNombre.setText(data.getStringExtra("NOMBRE_ESCANEADO"));
                    Persona.Nombre = data.getStringExtra("NOMBRE_ESCANEADO");
                    txtNacimiento.setText(data.getStringExtra("FECHA_NACIMIENTO_ESCANEADO"));
                    Persona.Nacimiento = data.getStringExtra("FECHA_NACIMIENTO_ESCANEADO");
                    Persona.Sexo = data.getStringExtra("SEXO_ESCANEADO");
                    txtSexo.setText(data.getStringExtra("SEXO_ESCANEADO"));
                    Persona.QR = Boolean.toString(true);
                    int año, mes, dia;
                    String[] convertir = Persona.Nacimiento.split("/");
                    dia = Integer.parseInt(convertir[0]);
                    mes = Integer.parseInt(convertir[1]);
                    año = Integer.parseInt(convertir[2]);
                    Persona.CalcularEdad(año,mes,dia);

            }

        }
        if(resultCode == 10){
            Toast.makeText(this, data.getStringExtra("CODEQR"), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "leyo", Toast.LENGTH_SHORT).show();
    }

    // Se inicializa el scrollbar para la fecha
    public void Fecha (View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.basic_dialog_date, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        DatePicker calendarView = view1.findViewById(R.id.calendarViewDate);
        calendarView.setMaxDate(new Date().getTime());

        Button save = view1.findViewById(R.id.GUARDARDATE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = calendarView.getYear();
                int month = calendarView.getMonth()+1;
                int day = calendarView.getDayOfMonth();
                txtNacimientoEditar.setText(Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year));
                //Toast.makeText(getBaseContext(), auxFecha[0] , Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button cancel = view1.findViewById(R.id.CANCELARDATE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    // Leer QR
    public void escanear(View view){
        Intent Modif= new Intent (this, ScannerQR.class);
        startActivityForResult(Modif, 1);
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    public void SwitchButton(View view){
    // Defino las caracteristicas
    int TamañoLetra = 20;
    String ColorPares = "#4A4A4A";
    String ColorImpares = "#4f5f6c";
    int AltoContenedor = 80;

    // Defino los contenedores
    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MiEstiloAlert);
    TextView textView = new TextView(this);
    textView.setText("BOTONES");
    textView.setPadding(20, 30, 20, 30);
    textView.setTextSize(20F);
    textView.setBackgroundColor(Color.parseColor("#4588BC"));
    textView.setTextColor(Color.WHITE);
    builder.setCustomTitle(textView);

    // Defino el Layaout que va a contener a los Check
    LinearLayout mainLayout = new LinearLayout(this);
    mainLayout.setOrientation(LinearLayout.VERTICAL);

    for(int i=0; i<encuestador.PersonButtons().size();i++){
        Switch aux;
        if((i%2)==0){
            aux = encuestador.ButtonSwitch(mainLayout,TamañoLetra,encuestador.PersonButtons().get(i),TamañoLetra, ColorPares);}
        else{
            aux = encuestador.ButtonSwitch(mainLayout,TamañoLetra,encuestador.PersonButtons().get(i),TamañoLetra, ColorImpares);
        }
        aux = encuestador.comportamientoSwitch(aux);
        switch_buttons.put(encuestador.PersonButtons().get(i), aux);
    }

    View view_context = this.findViewById(android.R.id.content);
    builder.setPositiveButton("LISTO", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            encuestador.ButtonsActivateDesactivate(view_context, encuestador.PersonButtons());
            encuestador.ADbtn(ButtonsViews, encuestador.PersonButtons());
            dialogInterface.dismiss();
        }

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

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // Tomar los datos de identificacion de la persona
    public void DatosIdentificar(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater Inflater = getLayoutInflater();
        View view1 = Inflater.inflate(R.layout.alert_informacion_personal, null);
        builder.setView(view1);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText edtNombre = view1.findViewById(R.id.EDTNOMBRE);
        if(Persona.Nombre.length()!=0){edtNombre.setText(Persona.Nombre);}
        final EditText edtApellido = view1.findViewById(R.id.EDTAPELLIDO);
        if(Persona.Apellido.length()!=0){edtApellido.setText(Persona.Apellido);}
        final EditText edtDni = view1.findViewById(R.id.EDTDNI);
        if(Persona.DNI.length()!=0){edtDni.setText(Persona.DNI);}
        txtNacimientoEditar = view1.findViewById(R.id.EDTFECHANACIMIENTO);
        txtNacimientoEditar.setText(Persona.Nacimiento);
        final RadioButton masculino = view1.findViewById(R.id.MASCULINO);
        final RadioButton femenino = view1.findViewById(R.id.FEMENINO);
        final RadioButton nobinario = view1.findViewById(R.id.NOBINARIO);
        if (Persona.Sexo.equals("M")){masculino.setChecked(true);}
        if (Persona.Sexo.equals("F")){femenino.setChecked(true);}
        if (Persona.Sexo.equals("X")){nobinario.setChecked(true);}
        indocumentado = view1.findViewById(R.id.CHECKINDOCUMENTADO);
        if (Persona.Data.containsKey("RE37_0")){
            if (Persona.Data.get("RE37_0").equals("SI"))
            {
                indocumentado.setChecked(true);
            }
        }

        final Button guardar = view1.findViewById(R.id.GUARDARINFORMACIONPERSONAL);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNacimientoEditar.getText().toString().length()>=8 && txtNacimientoEditar.getText().toString().length()<=10) {
                    Persona.Nombre = edtNombre.getText().toString();
                    txtNombre.setText(Persona.Nombre);
                    Persona.Apellido = edtApellido.getText().toString();
                    txtApellido.setText(Persona.Apellido);
                    if (masculino.isChecked()) {
                        Persona.Sexo = "M";
                    }
                    if (femenino.isChecked()) {
                        Persona.Sexo = "F";
                    }
                    if (nobinario.isChecked()){
                        Persona.Sexo = "X";
                    }
                    txtSexo.setText(Persona.Sexo);
                    Persona.DNI = edtDni.getText().toString();
                    txtDni.setText(Persona.DNI);
                    Persona.Nacimiento = txtNacimientoEditar.getText().toString();
                    txtNacimiento.setText(Persona.Nacimiento);
                    if (indocumentado.isChecked()) {
                        //TODO: revisar esta parte principalmente con el else
                        if (!Persona.DNI.contains("INDOC")) {
                            Persona.Data.put("RE37_0", "SI");
                            txtDni.setText("INDOC. " + Persona.DNI);
                        }
                    }
                    dialog.dismiss();
                }else{
                    Toast.makeText(getBaseContext(), "LA FECHA DE NACIMIENTO NO ES VALIDA" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button cancelar = view1.findViewById(R.id.CANCELARINFORMACIONPERSONAL);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // DEFINO EL BOTON DESCRIPCION GENERAL
    // Agregar al vector que corresponda el nombre del boton
    public void BotonDescripcionGenral(View view) {
        String title = "DESCRIPCION GENERAL";
        /*PersonButtonsEstadoFisico.add(title);
        // Agrego el boton al menu de opciones
        ButtonsViews.put(title, generateBtnMenu(R.drawable.discapacitado, title));*/
        // Se le da el comportamiento al boton
        //buttons.put(title, new BotonDescripcionBasica22(this, ButtonsViews.get(title)));
        Method[] aux = btnDescripcion.getClass().getMethods();
        for (int i=0; i< aux.length; i++){
            if (aux[i].getName().equals("vista")) {
                try {
                    aux[i].invoke(btnDescripcion, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    // DEFINO EL BOTON DE METODOS DE CONTACTO
    // Agregar al vector que corresponda el nombre del boton
    public void BotonContactoGeneral(View view) {
        //BotonContactoBasico22 btn = new BotonContactoBasico22(this, view);
        Method[] aux = btnContacto.getClass().getMethods();
        for (int i=0; i< aux.length; i++){
            if (aux[i].getName().equals("vista")) {
                try {
                    aux[i].invoke(btnContacto, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }


//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // VITAMINA D
    public void VitaminaD(View view){
        BotonVitaminaD botonVitaminaD = new BotonVitaminaD(this,
                Persona,
                this.findViewById(android.R.id.content));
        botonVitaminaD.vista();
        Persona = botonVitaminaD.returnPersona();
    }

// SECCION PSICO FISICO
//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // ACOMPAÑAMIENTO
    /*public void Acompañamiento(View view){
        BotonAcompañamiento botonAcompañamiento = new BotonAcompañamiento(this,
                Persona,
                this.findViewById(android.R.id.content));
        botonAcompañamiento.vista();
        Persona = botonAcompañamiento.returnPersona();
    }*/

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // TRASTORNOS NIÑOS
    public void TrastornosNiños(View view){
        BotonTrastornosNiños botonTrastornosNiños = new BotonTrastornosNiños(this,
                Persona,
                this.findViewById(android.R.id.content));
        botonTrastornosNiños.vista();
        Persona = botonTrastornosNiños.returnPersona();
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // ADICCIONES
    public void Adicciones(View view){
    BotonAdicciones botonAdicciones = new BotonAdicciones(this,
            Persona,
            this.findViewById(android.R.id.content));
    botonAdicciones.vista();
    Persona = botonAdicciones.returnPersona();
}

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // TRASTORNOD MENTALES
    public void TrantornosMentales(View view){
        BotonTrastornosMentales botonTrastornosMentales = new BotonTrastornosMentales(this,
                Persona,
                this.findViewById(android.R.id.content));
        botonTrastornosMentales.vista();
        Persona = botonTrastornosMentales.returnPersona();
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // VIOLENCIA
    public void Violencia(View view){
        BotonViolencia botonViolencia = new BotonViolencia(this,
                Persona,
                this.findViewById(android.R.id.content));
        botonViolencia.vista();
        Persona = botonViolencia.returnPersona();
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
// SECCION DE DEFINICION DE DEFINICION DE LAS OPCIONES DE GUARDADO DE LAS PERSONAS
    public void GuardarPersona(View view){
        getDataPerson();
        // Defino el intent para permitir pasar los datos

            Intent Modif1 = new Intent(this, MenuFamilia.class);
            Persona.getDataCache(adminBData);
            adminBData.deleteCache();

            // Reviso que no se carguen datos nuevos, si se cargaron los obtengo
            if (txtNombre.getText().toString() != null) {
                Persona.Nombre = txtNombre.getText().toString();
            }
            if (txtApellido.getText().toString() != null) {
                Persona.Apellido = txtApellido.getText().toString();
            }
            if (txtDni.getText().toString() != null) {
                Persona.DNI = txtDni.getText().toString();
            }
            if (txtSexo.getText().toString() != null) {
                Persona.Sexo = txtSexo.getText().toString();
            }

            if (Persona.QR != Boolean.toString(true)) {
                Persona.QR = Boolean.toString(false);
            }

            // Si uno de los campos listados abajo es nulo lo reemplazo por S/D
            if (Persona.Edad == null) {
                Persona.Edad = "";
            }
            if (Persona.UnidadEdad == null) {
                Persona.UnidadEdad = "";
            }
            if (Persona.CodfigoFactorRiesgo == null) {
                Persona.CodfigoFactorRiesgo = "";
            }
            if (Persona.Observaciones == null) {
                Persona.Observaciones = "";
            }

            if (Persona.Efector.length()==0){
                Persona.Efector = admin.EfectorEncuestador(encuestador.DNI);
                //Toast.makeText(this, Persona.Efector, Toast.LENGTH_SHORT).show();
            }

            if (Persona.Fecha.length()==0){
                Persona.Fecha = DateFormat.getDateInstance().format(new Date());
            }
        if (Persona.Nombre.length()!=0 && Persona.Apellido.length()!=0 && (Persona.DNI.length()!=0 || Persona.Data.get("RE37_0").equals("SI")) && Persona.Fecha.length()!=0 && Persona.Sexo.length()!=0) {
            if((Persona.DNI.length()<=8 && Persona.DNI.length()>=7) || (indocumentado.isChecked())) {
                if(Persona.Nacimiento.length()<=10 && Persona.Nacimiento.length()>=8) {
                    Bundle bundle = new Bundle();
                    Persona.DeleteContext();
                    bundle.putSerializable("PERSONA", Persona);

                    Modif1.putExtras(bundle);

                    setResult(RESULT_OK, Modif1);
                    finish();
                }else{
                    Toast.makeText(this, "LA FECHA DE NACIMIENTO NO ES VALIDA", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "EL DNI NO ES VALIDO", Toast.LENGTH_SHORT).show();
            }

        } else{
            Toast.makeText(this, "ES NECESARIO INGRESAR TODOS LOS DATOS IDENTIFICATORIOS", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDataPerson(){
        Archivos archivos = new Archivos(this);
        ArrayList<String> cat = archivos.getCodeCabecerasPersonas(this);
        Object[] keys = Persona.getDataCacheUD(adminBData).keySet().toArray();
        HashMap<String, String> dataAux = Persona.getDataCacheUD(adminBData);
        for (String c : cat){
            for (Object o : keys) {
                if (c.equals(o.toString())) {
                    //familia.data22.put(c, dataAux.get(c));
                    //Log.e(c+"UD guardar persona", Integer.toString( dataAux.get(c).length()));
                    Persona.Data.putAll(dataAux);
                }
            }
        }
        adminBData.deleteCache();
    }

    @Override
    public void onBackPressed() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cierro la BD
        adminBData.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> aux = Persona.getDataCache(adminBData);
        /*if(Persona.Data.get(getString(R.string.code_hpv)) != null && aux.containsKey(getString(R.string.code_hpv))) {
            Persona.CodeHPV = Persona.Data.get(getString(R.string.code_hpv));
            TestHPV(this.getCurrentFocus());
        }*/
        adminBData.deleteCache();
    }
}