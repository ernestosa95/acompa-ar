package acompanar.BasicObjets;

import static android.widget.Toast.makeText;

import android.content.Context;

import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.ShareDataManagement.Archivos;
import com.example.acompanar.R;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FamiliarUnityClass implements Serializable {
    public String Latitud="", Longitud="", TipoVivienda="", TelefonoFamiliar="", DueñoVivienda="";
    public String CantidadPiezas="", LugarCocinar="", UsaParaCocinar="", Paredes="", Revoque="", HieloCalle="", PerrosCalle="";
    public String Pisos="", Techo="", Cielorraso="", Agua="", AguaOrigen="", Excretas="", Electricidad="", Fecha="";
    public String Gas="", AguaLluvia="", Arboles="", Baño="", BañoTiene="", calle = "", numero = "", numeroCartografia = "";
    public String SituacionHabitacional="", data_enuestador="", ObservacionesVivienda="";
    public int cantidadMayores = 0, cantidadMenores = 0;
    public String[] codigoColor = {"V"};

    public HashMap<String,String> Data = new HashMap<>();

    private ArrayList<String> cabeceraFamilia = new ArrayList<>();
    public final ArrayList<String> cabeceraVivienda = new ArrayList<>();
    public final ArrayList<String> cabeceraServiciosBasicos = new ArrayList<>();
    public final ArrayList<String> cabeceraInspeccionExterior = new ArrayList<>();
    private HashMap<String, String> datosIngresados = new HashMap<>();
    public HashMap<String, String> datosEditar = new HashMap<>();
    public HashMap<String, String> data22 = new HashMap<>();

    // Datos de dengue
    public String Tanques, Piletas, Cubiertas, Canaleta, Hueco, Macetas;
    public String RecipientesPlasticos, Botellas, ElementosDesuso, SituacionVivienda;
    public String TipoTrabajo, TotalTratados, TotalFocoAedico, TotalIspeccionado;
    public String Larvicida, Destruidos;
    private Context context;
    private Archivos mngArchivo;

    public HashMap<String, String> dataAedes = new HashMap<>();

    public FamiliarUnityClass(Context oldcontext) {
        context=oldcontext;
        mngArchivo = new Archivos(context);

        // DEFINO LOS VALORES QUE CONTEMPLA LA CLASE FAMILIA
        DefineValues();

        Fecha = DateFormat.getDateInstance().format(new Date());

        // DENGUE
        InicializarDengue();

        //AEDES - inicializo para poder corroborar si hay un llenado previo
        ArrayList <String> cabeceras = mngArchivo.getCodeCabecerasAedes(context);
        for (int i=0; i<cabeceras.size();i++){
            dataAedes.put(cabeceras.get(i), null);
        }
    }

    public void DeleteContext(){
        context = null;
        mngArchivo = null;
    }

    // DEFINO LOS DATOS QUE CONTEMPLA FAMILIA
    private void DefineValues(){
        // VIVIENDA
        cabeceraVivienda.add(context.getString(R.string.cantidad_piezas));
        cabeceraVivienda.add(context.getString(R.string.dueño_vivienda));
        cabeceraVivienda.add(context.getString(R.string.baño));
        cabeceraVivienda.add(context.getString(R.string.baño_tiene));
        cabeceraVivienda.add(context.getString(R.string.lugar_cocinar));
        cabeceraVivienda.add(context.getString(R.string.usa_para_cocinar));
        cabeceraVivienda.add(context.getString(R.string.pisos));
        cabeceraVivienda.add(context.getString(R.string.cielorraso));

        //SERVICIOS BASICOS
        cabeceraServiciosBasicos.add(context.getString(R.string.agua));
        cabeceraServiciosBasicos.add(context.getString(R.string.origenagua));
        cabeceraServiciosBasicos.add(context.getString(R.string.agua_lluvia));
        cabeceraServiciosBasicos.add(context.getString(R.string.electricidad));
        cabeceraServiciosBasicos.add(context.getString(R.string.gas));
        cabeceraServiciosBasicos.add(context.getString(R.string.excretas));

        //Inspeccion Exterior
        cabeceraInspeccionExterior.add(context.getString(R.string.tipo_de_vivienda));
        cabeceraInspeccionExterior.add(context.getString(R.string.paredes));
        cabeceraInspeccionExterior.add(context.getString(R.string.revestimiento));
        cabeceraInspeccionExterior.add(context.getString(R.string.techo));
        cabeceraInspeccionExterior.add(context.getString(R.string.arboles));
        cabeceraInspeccionExterior.add(context.getString(R.string.hielo));
        cabeceraInspeccionExterior.add(context.getString(R.string.perros_sueltos));

        cabeceraFamilia.add(context.getString(R.string.telefono_familiar));
        cabeceraFamilia.add(context.getString(R.string.latitud));
        cabeceraFamilia.add(context.getString(R.string.longitud));
        cabeceraFamilia.add(context.getString(R.string.calle));
        cabeceraFamilia.add(context.getString(R.string.numero));
        cabeceraFamilia.add(context.getString(R.string.encuestador));
        cabeceraFamilia.add(context.getString(R.string.mayores));
        cabeceraFamilia.add(context.getString(R.string.menores));
        cabeceraFamilia.add(context.getString(R.string.codigo_color));
        cabeceraFamilia.add(context.getString(R.string.observaciones_vivienda));

        cabeceraFamilia.addAll(cabeceraVivienda);
        cabeceraFamilia.addAll(cabeceraServiciosBasicos);
        cabeceraFamilia.addAll(cabeceraInspeccionExterior);
    }

    public ArrayList<String> FamilyValues(){return cabeceraFamilia;}
    public ArrayList<String> ViviendaValues(){return cabeceraVivienda;}
    public ArrayList<String> ServiciosBasicosValues(){return cabeceraServiciosBasicos;}
    public ArrayList<String> InspeccionExteriorValues(){return cabeceraInspeccionExterior;}

    public void ActualizeContext(Context oldcontext){
        context=oldcontext;
        //encuestador = new ClaseEncuestador(context);
    }

    // BASE DE DATOS
    public void LoadData(){
        if (Integer.toString(cantidadMenores).length()!=0){Data.put(context.getString(R.string.menores), Integer.toString(cantidadMenores));}
        if (Integer.toString(cantidadMayores).length()!=0){Data.put(context.getString(R.string.mayores), Integer.toString(cantidadMayores));}
        if (calle.length()!=0){Data.put(context.getString(R.string.calle), calle);}
        if (numero.length()!=0){Data.put(context.getString(R.string.numero), numero);}
        if (Longitud.length()!=0){Data.put(context.getString(R.string.longitud), Longitud);}
        if (Latitud.length()!=0){Data.put(context.getString(R.string.latitud), Latitud);}
        if (TipoVivienda.length()!=0){Data.put(context.getString(R.string.tipo_de_vivienda), TipoVivienda);}
        if (TelefonoFamiliar.length()!=0){Data.put(context.getString(R.string.telefono_familiar), TelefonoFamiliar);}
        if (DueñoVivienda.length()!=0){Data.put(context.getString(R.string.dueño_vivienda), DueñoVivienda);}
        if (CantidadPiezas.length()!=0){Data.put(context.getString(R.string.cantidad_piezas), CantidadPiezas);}
        if (LugarCocinar.length()!=0){Data.put(context.getString(R.string.lugar_cocinar), LugarCocinar);}
        if (UsaParaCocinar.length()!=0){Data.put(context.getString(R.string.usa_para_cocinar), UsaParaCocinar);}
        if (Paredes.length()!=0){Data.put(context.getString(R.string.paredes), Paredes);}
        if (Revoque.length()!=0){Data.put(context.getString(R.string.data_revoque), Revoque);}
        if (Pisos.length()!=0){Data.put(context.getString(R.string.pisos), Pisos);}
        if (Techo.length()!=0){Data.put(context.getString(R.string.techo), Techo);}
        if (Cielorraso.length()!=0){Data.put(context.getString(R.string.cielorraso), Cielorraso);}
        if (Agua.length()!=0){Data.put(context.getString(R.string.agua), Agua);}
        if (AguaOrigen.length()!=0){Data.put(context.getString(R.string.origenagua), AguaOrigen);}
        if (Excretas.length()!=0){Data.put(context.getString(R.string.excretas), Excretas);}
        if (Electricidad.length()!=0){Data.put(context.getString(R.string.electricidad), Electricidad);}
        if (Gas.length()!=0){Data.put(context.getString(R.string.gas), Gas);}
        if (AguaLluvia.length()!=0){Data.put(context.getString(R.string.agua_lluvia), AguaLluvia);}
        if (Arboles.length()!=0){Data.put(context.getString(R.string.arboles), Arboles);}
        if (Baño.length()!=0){Data.put(context.getString(R.string.baño), Baño);}
        if (BañoTiene.length()!=0){Data.put(context.getString(R.string.baño_tiene), BañoTiene);}
        if (HieloCalle.length()!=0){Data.put(context.getString(R.string.hielo).replace("/",""), HieloCalle);}
        if (PerrosCalle.length()!=0){Data.put(context.getString(R.string.perros_sueltos), PerrosCalle);}
        if (Fecha.length()!=0){Data.put(context.getString(R.string.fecha), Fecha);}
        if (SituacionHabitacional.length()!=0){Data.put(context.getString(R.string.situacion_habitacional), SituacionHabitacional);}
        if (codigoColor[0].length()!=0){Data.put(context.getString(R.string.codigo_color), codigoColor[0]);}
        if (ObservacionesVivienda.length()!=0){Data.put(context.getString(R.string.observaciones_vivienda), ObservacionesVivienda);}
        Data.put(context.getString(R.string.encuestador), data_enuestador);
    }

    public void LoadDataHashToParameters(){
        if(Data.get(context.getString(R.string.menores))!=null){cantidadMenores=Integer.parseInt(Data.get(context.getString(R.string.menores)));}
        if(Data.get(context.getString(R.string.mayores))!=null){cantidadMayores=Integer.parseInt(Data.get(context.getString(R.string.mayores)));}
        if(Data.get(context.getString(R.string.calle))!=null){calle=Data.get(context.getString(R.string.calle));}
        if(Data.get(context.getString(R.string.numero))!=null){numero=Data.get(context.getString(R.string.numero));}
        if(Data.get(context.getString(R.string.longitud))!=null){Longitud=Data.get(context.getString(R.string.longitud));}
        if(Data.get(context.getString(R.string.latitud))!=null){Latitud=Data.get(context.getString(R.string.latitud));}
        if(Data.get(context.getString(R.string.tipo_de_vivienda))!=null){TipoVivienda=Data.get(context.getString(R.string.tipo_de_vivienda));}
        if(Data.get(context.getString(R.string.telefono_familiar))!=null){TelefonoFamiliar=Data.get(context.getString(R.string.telefono_familiar));}
        if(Data.get(context.getString(R.string.dueño_vivienda))!=null){DueñoVivienda=Data.get(context.getString(R.string.dueño_vivienda));}
        if(Data.get(context.getString(R.string.cantidad_piezas))!=null){CantidadPiezas=Data.get(context.getString(R.string.cantidad_piezas));}
        if(Data.get(context.getString(R.string.lugar_cocinar))!=null){LugarCocinar=Data.get(context.getString(R.string.lugar_cocinar));}
        if(Data.get(context.getString(R.string.usa_para_cocinar).replace(".",""))!=null){UsaParaCocinar=Data.get(context.getString(R.string.usa_para_cocinar).replace(".",""));}
        if(Data.get(context.getString(R.string.paredes))!=null){Paredes=Data.get(context.getString(R.string.paredes));}
        if(Data.get(context.getString(R.string.data_revoque))!=null){Revoque=Data.get(context.getString(R.string.data_revoque));}
        if(Data.get(context.getString(R.string.pisos))!=null){Pisos=Data.get(context.getString(R.string.pisos));}
        if(Data.get(context.getString(R.string.techo))!=null){Techo=Data.get(context.getString(R.string.techo));}
        if(Data.get(context.getString(R.string.cielorraso))!=null){Cielorraso=Data.get(context.getString(R.string.cielorraso));}
        if(Data.get(context.getString(R.string.agua))!=null){Agua=Data.get(context.getString(R.string.agua));}
        if(Data.get(context.getString(R.string.origenagua))!=null){AguaOrigen=Data.get(context.getString(R.string.origenagua));}
        if(Data.get(context.getString(R.string.excretas))!=null){Excretas=Data.get(context.getString(R.string.excretas));}
        if(Data.get(context.getString(R.string.electricidad))!=null){Electricidad=Data.get(context.getString(R.string.electricidad));}
        if(Data.get(context.getString(R.string.gas))!=null){Gas=Data.get(context.getString(R.string.gas));}
        if(Data.get(context.getString(R.string.agua_lluvia))!=null){AguaLluvia=Data.get(context.getString(R.string.agua_lluvia));}
        if(Data.get(context.getString(R.string.arboles))!=null){Arboles=Data.get(context.getString(R.string.arboles));}
        if(Data.get(context.getString(R.string.baño))!=null){Baño=Data.get(context.getString(R.string.baño));}
        if(Data.get(context.getString(R.string.baño_tiene))!=null){BañoTiene=Data.get(context.getString(R.string.baño_tiene));}
        if(Data.get(context.getString(R.string.hielo).replace("/",""))!=null){HieloCalle=Data.get(context.getString(R.string.hielo).replace("/",""));}
        if(Data.get(context.getString(R.string.perros_sueltos))!=null){PerrosCalle=Data.get(context.getString(R.string.perros_sueltos));}
        if(Data.get(context.getString(R.string.fecha))!=null){Fecha=Data.get(context.getString(R.string.fecha));}
        if(Data.get(context.getString(R.string.situacion_habitacional))!=null){SituacionHabitacional=Data.get(context.getString(R.string.situacion_habitacional));}
        if(Data.get(context.getString(R.string.codigo_color))!=null){codigoColor[0]=Data.get(context.getString(R.string.codigo_color));}
        if(Data.get(context.getString(R.string.observaciones_vivienda))!=null){ObservacionesVivienda=Data.get(context.getString(R.string.observaciones_vivienda));}
    }

    public ArrayList<String> DatosCargadosCsv(){
        ArrayList<String> cabecera = new ArrayList<>();
            if(TipoVivienda.length()!=0){
                cabecera.add(cabeceraFamilia.get(0));
                datosIngresados.put(cabeceraFamilia.get(0), TipoVivienda);
            }
            if(DueñoVivienda.length()!=0){
                cabecera.add(cabeceraFamilia.get(1));
                datosIngresados.put(cabeceraFamilia.get(1), DueñoVivienda);
            }
            if(CantidadPiezas.length()!=0){
                cabecera.add(cabeceraFamilia.get(2));
                datosIngresados.put(cabeceraFamilia.get(2), CantidadPiezas);
            }
            if(LugarCocinar.length()!=0){
                cabecera.add(cabeceraFamilia.get(3));
                datosIngresados.put(cabeceraFamilia.get(3), LugarCocinar);
            }
            if(UsaParaCocinar.length()!=0){
                cabecera.add(cabeceraFamilia.get(4));
                datosIngresados.put(cabeceraFamilia.get(4), UsaParaCocinar);
            }
            if(Paredes.length()!=0){
                cabecera.add(cabeceraFamilia.get(5));
                datosIngresados.put(cabeceraFamilia.get(5), Paredes);
            }
            if(Revoque.length()!=0){
                cabecera.add(cabeceraFamilia.get(6));
                datosIngresados.put(cabeceraFamilia.get(6), Revoque);
            }
            if(Pisos.length()!=0){
                cabecera.add(cabeceraFamilia.get(7));
                datosIngresados.put(cabeceraFamilia.get(7), Pisos);
            }
            if(Cielorraso.length()!=0){
                cabecera.add(cabeceraFamilia.get(8));
                datosIngresados.put(cabeceraFamilia.get(8), Cielorraso);
            }
            if(Techo.length()!=0){
                cabecera.add(cabeceraFamilia.get(9));
                datosIngresados.put(cabeceraFamilia.get(9), Techo);
            }
            if(Agua.length()!=0){
                cabecera.add(cabeceraFamilia.get(10));
                datosIngresados.put(cabeceraFamilia.get(10), Agua);
            }
            if(AguaOrigen.length()!=0){
                cabecera.add(cabeceraFamilia.get(11));
                datosIngresados.put(cabeceraFamilia.get(11), AguaOrigen);
            }
            if(Excretas.length()!=0){
                cabecera.add(cabeceraFamilia.get(12));
                datosIngresados.put(cabeceraFamilia.get(12), Excretas);
            }
            if(Electricidad.length()!=0){
                cabecera.add(cabeceraFamilia.get(13));
                datosIngresados.put(cabeceraFamilia.get(13), Electricidad);
            }
            if(Gas.length()!=0){
                cabecera.add(cabeceraFamilia.get(14));
                datosIngresados.put(cabeceraFamilia.get(14), Gas);
            }
            if(AguaLluvia.length()!=0){
                cabecera.add(cabeceraFamilia.get(15));
                datosIngresados.put(cabeceraFamilia.get(15), AguaLluvia);
            }
            if(Arboles.length()!=0){
                cabecera.add(cabeceraFamilia.get(16));
                datosIngresados.put(cabeceraFamilia.get(16), Arboles);
            }
            if(Baño.length()!=0){
                cabecera.add(cabeceraFamilia.get(17));
                datosIngresados.put(cabeceraFamilia.get(17), Baño);
            }
            if(BañoTiene.length()!=0){
                cabecera.add(cabeceraFamilia.get(18));
                datosIngresados.put(cabeceraFamilia.get(18), BañoTiene);
            }
            if(HieloCalle.length()!=0){
                cabecera.add(cabeceraFamilia.get(19));
                datosIngresados.put(cabeceraFamilia.get(19), HieloCalle);
            }
            if(PerrosCalle.length()!=0){
              cabecera.add(cabeceraFamilia.get(20));
               datosIngresados.put(cabeceraFamilia.get(20), HieloCalle);
            }
            if(TelefonoFamiliar.length()!=0){
               cabecera.add(cabeceraFamilia.get(21));
               datosIngresados.put(cabeceraFamilia.get(21), TelefonoFamiliar);
            }
        return cabecera;
    }

    public HashMap<String,String> DatosIngresados(){
        /* Esta funcion necesita de la ejecucion de la anterior para llenar el Hashmap con los
        * datos*/
        this.DatosCargadosCsv();
        return datosIngresados;
    }

    // FORMATO PARA DENGUE
    private void InicializarDengue(){
        Tanques = "0;0;0";
        Piletas = "0;0;0";
        Cubiertas = "0;0;0";
        Canaleta = "0;0;0";
        Hueco = "0;0;0";
        Macetas = "0;0;0";
        RecipientesPlasticos = "0;0;0";
        Botellas = "0;0;0";
        ElementosDesuso = "0;0;0";
        SituacionVivienda = "";
        TipoTrabajo="";
        TotalFocoAedico="";
        TotalIspeccionado="";
        TotalTratados="";
        Larvicida="";
        Destruidos="";
    }

    public String FormatoGuardarDengue(){
        // DNI;APELLIDO;NOMBRE;EDAD;UNIDAD EDAD;FECHA DE NACIMIENTO;EFECTOR;FACTORES DE RIESGO;CODIGO SISA F. DE RIESGO;VACUNAS;LOTE DE VACUNA
        String guardar = SituacionVivienda+";"+Tanques+";"+Piletas+";"+Cubiertas+";"+Canaleta+";"+Hueco+";"+
                Macetas+";"+RecipientesPlasticos+";"+Botellas+";"+ElementosDesuso+";"+TotalIspeccionado+";"+
                TotalTratados+";"+Destruidos+";"+TotalFocoAedico+";"+Larvicida+";"+TipoTrabajo;
        return guardar;
    }

    public HashMap<String, String> getDataCache(BDData admin){
        HashMap<String,String> value = new HashMap<>();
        value = admin.getValuesCacheUD();
        Data.putAll(value);
        return value;
    }
}