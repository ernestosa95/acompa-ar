package acompanar.BasicObjets;

import android.content.Context;

import acompanar.ManagementModule.StorageManagement.BDData;
import com.example.acompanar.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PersonClass implements Serializable {
    //
    public String Nombre="", Apellido="", DNI="", Sexo="", QR="", Celular="", Fijo="", Mail="", Nacimiento="";
    public String Edad="", FactoresDeRiesgo="", Vacunas="", LoteVacuna="", CodfigoFactorRiesgo="";
    public String UnidadEdad="", Efector="", Observaciones="", Limpieza="", NombreContacto="";
    public String TelefonoContacto="", ParentezcoContacto="", Ocupacion="", Educacion="", Temperatura="";
    public String Tos="", Garganta="", Respiratorio="", Disgeusia="", Anosmia="", Vitamina="", FechaParto="";
    public String UltimoControlEmbarazo="", EnfermedadEmbarazo="", CertificadoDiscapacidad="";
    public String TipoDiscapacidad="", Acompañamiento="", TrastornoNiños="", Adicciones="", ActividadesOcio="";
    public String LugarOcio="", TipoViolencia="", ModalidadViolencia="", TrastornosMentales="", EnfermedadCronica="";
    public String PlanSocial="", HPV="", Latitud="", Longitud="", Fecha="", CodeHPV="", AntecedentesCronicos="", IdEncuestador="";

    public String oldName="", oldSurname="";

    private Context context;
    public HashMap<String, String> datosEditar = new HashMap<>();
    public HashMap<String,String> Data = new HashMap<>();
    public ArrayList<String> cabeceraPersona = new ArrayList<>();
    //private ButtonDeclaration buttonDeclaration;
    public PersonClass(Context oldcontext) {
        context = oldcontext;

        // DEFINICION DE LOS VALORES
        //Fecha = DateFormat.getDateInstance().format(new Date());
        DefineValues(context);
        //buttonDeclaration = new ButtonDeclaration(context);
        //cabeceraPersona.addAll(buttonDeclaration.NewsCategoriesPersons());
        cargarDatosEditar();

        // como el avance button se ejecuta cada vez debo tener esas variables en el Data
        /*for (int i=0; i<buttonDeclaration.NewsCategoriesPersons().size();i++) {
            Data.put(buttonDeclaration.NewsCategoriesPersons().get(i),"");
        }*/
    }

    public void setIdEncuestador(String ID){IdEncuestador=ID;}
    public String getIdEncuestador(){return IdEncuestador;}

    public void DeleteContext(){
        context = null;
        //buttonDeclaration = null;
    }

    public void ActualizeContext(Context oldcontext){context=oldcontext;}

    public ArrayList<String> PersonValues(){
        ArrayList<String> values = cabeceraPersona;
        return cabeceraPersona;
    }

    public void LoadData(){
        if (Nombre.length()!=0){Data.put(context.getString(R.string.nombre), Nombre);}
        if (Apellido.length()!=0){Data.put(context.getString(R.string.apellido), Apellido);}
        if (DNI.length()!=0){Data.put(context.getString(R.string.dni), DNI);}
        if (Sexo.length()!=0){Data.put(context.getString(R.string.sexo), Sexo);}
        if (QR.length()!=0){Data.put(context.getString(R.string.qr), QR);}
        if (Celular.length()!=0){Data.put(context.getString(R.string.celular), Celular);}
        if (Fijo.length()!=0){Data.put(context.getString(R.string.fijo), Fijo);}
        if (Mail.length()!=0){Data.put(context.getString(R.string.mail), Mail);}
        if (Nacimiento.length()!=0){Data.put(context.getString(R.string.fecha_nacimiento), Nacimiento);}
        if (FactoresDeRiesgo.length()!=0){Data.put(context.getString(R.string.factores_riesgo), FactoresDeRiesgo);}
        if (Efector.length()!=0){Data.put(context.getString(R.string.efector), Efector);}
        if (Observaciones.length()!=0){Data.put(context.getString(R.string.observaciones), Observaciones);}
        if (NombreContacto.length()!=0){Data.put(context.getString(R.string.nombre_apellido_contacto), NombreContacto);}
        if (TelefonoContacto.length()!=0){Data.put(context.getString(R.string.telefono_contacto), TelefonoContacto);}
        if (ParentezcoContacto.length()!=0){Data.put(context.getString(R.string.parentezco_contacto), ParentezcoContacto);}
        if (Ocupacion.length()!=0){Data.put(context.getString(R.string.ocupacion), Ocupacion);}
        if (Educacion.length()!=0){Data.put(context.getString(R.string.educacion), Educacion);}
        if (Vitamina.length()!=0){Data.put(context.getString(R.string.vitamina), Vitamina);}
        //if (FechaParto.length()!=0){Data.put(context.getString(R.string.fecha_parto), FechaParto);}
        //if (UltimoControlEmbarazo.length()!=0){Data.put(context.getString(R.string.ultimo_control), UltimoControlEmbarazo);}
        if (EnfermedadEmbarazo.length()!=0){Data.put(context.getString(R.string.enfermedad_relacionada_embarazo), EnfermedadEmbarazo);}
        if (CertificadoDiscapacidad.length()!=0){Data.put(context.getString(R.string.certificado_unico_discapacidad), CertificadoDiscapacidad);}
        if (TipoDiscapacidad.length()!=0){Data.put(context.getString(R.string.tipo_discapacidad), TipoDiscapacidad);}
        if (Acompañamiento.length()!=0){Data.put(context.getString(R.string.acompañamiento), Acompañamiento);}
        if (TrastornoNiños.length()!=0){Data.put(context.getString(R.string.transtornos_en_niños), TrastornoNiños);}
        if (Adicciones.length()!=0){Data.put(context.getString(R.string.adicciones), Adicciones);}
        if (ActividadesOcio.length()!=0){Data.put(context.getString(R.string.ocio), ActividadesOcio);}
        if (LugarOcio.length()!=0){Data.put(context.getString(R.string.donde_ocio), LugarOcio);}
        if (TipoViolencia.length()!=0){Data.put(context.getString(R.string.tipo_violencia), TipoViolencia);}
        if (ModalidadViolencia.length()!=0){Data.put(context.getString(R.string.modalidad_violencia), ModalidadViolencia);}
        if (TrastornosMentales.length()!=0){Data.put(context.getString(R.string.trastornos_mentales), TrastornosMentales);}
        if (EnfermedadCronica.length()!=0){Data.put(context.getString(R.string.enfermedad_cronica), EnfermedadCronica);}
        if (PlanSocial.length()!=0){Data.put(context.getString(R.string.plan_social), PlanSocial);}
        if (HPV.length()!=0){Data.put(context.getString(R.string.realizo_test_hpv), HPV);}
        if (Latitud.length()!=0){Data.put(context.getString(R.string.latitud), Latitud);}
        if (Longitud.length()!=0){Data.put(context.getString(R.string.longitud), Longitud);}
        if (Fecha.length()!=0){Data.put(context.getString(R.string.fecha), Fecha);}
        if (CodeHPV.length()!=0){Data.put(context.getString(R.string.code_hpv), CodeHPV);}
        if (AntecedentesCronicos.length()!=0){Data.put(context.getString(R.string.antecedentes_familiares), AntecedentesCronicos);}
        if (IdEncuestador.length()!=0){Data.put("ID_ENCUESTADOR", IdEncuestador);}
    }

    public void LoadDataHashToParamaters(){
        if(Data.get(context.getString(R.string.nombre))!=null){Nombre=Data.get(context.getString(R.string.nombre));}
        if(Data.get(context.getString(R.string.apellido))!=null){Apellido=Data.get(context.getString(R.string.apellido));}
        if(Data.get(context.getString(R.string.dni))!=null){DNI=Data.get(context.getString(R.string.dni));}
        if(Data.get(context.getString(R.string.sexo))!=null){Sexo=Data.get(context.getString(R.string.sexo));}
        if(Data.get(context.getString(R.string.qr))!=null){QR=Data.get(context.getString(R.string.qr));}
        if(Data.get(context.getString(R.string.celular))!=null){Celular=Data.get(context.getString(R.string.celular));}
        if(Data.get(context.getString(R.string.fijo))!=null){Fijo=Data.get(context.getString(R.string.fijo));}
        if(Data.get(context.getString(R.string.mail))!=null){Mail=Data.get(context.getString(R.string.mail));}
        if(Data.get(context.getString(R.string.fecha_nacimiento).replace("/",""))!=null){Nacimiento=Data.get(context.getString(R.string.fecha_nacimiento).replace("/",""));}
        if(Data.get(context.getString(R.string.factores_riesgo))!=null){FactoresDeRiesgo=Data.get(context.getString(R.string.factores_riesgo));}
        if(Data.get(context.getString(R.string.efector))!=null){Efector=Data.get(context.getString(R.string.efector));}
        if(Data.get(context.getString(R.string.observaciones))!=null){Observaciones=Data.get(context.getString(R.string.observaciones));}
        if(Data.get(context.getString(R.string.nombre_apellido_contacto))!=null){NombreContacto=Data.get(context.getString(R.string.nombre_apellido_contacto));}
        if(Data.get(context.getString(R.string.telefono_contacto))!=null){TelefonoContacto=Data.get(context.getString(R.string.telefono_contacto));}
        if(Data.get(context.getString(R.string.parentezco_contacto))!=null){ParentezcoContacto=Data.get(context.getString(R.string.parentezco_contacto));}
        if(Data.get(context.getString(R.string.ocupacion))!=null){Ocupacion=Data.get(context.getString(R.string.ocupacion));}
        if(Data.get(context.getString(R.string.educacion))!=null){Educacion=Data.get(context.getString(R.string.educacion));}
        if(Data.get(context.getString(R.string.vitamina))!=null){Vitamina=Data.get(context.getString(R.string.vitamina));}
        if(Data.get(context.getString(R.string.fecha_parto))!=null){FechaParto=Data.get(context.getString(R.string.fecha_parto));}
        if(Data.get(context.getString(R.string.ultimo_control))!=null){UltimoControlEmbarazo=Data.get(context.getString(R.string.ultimo_control));}
        if(Data.get(context.getString(R.string.enfermedad_relacionada_embarazo))!=null){EnfermedadEmbarazo=Data.get(context.getString(R.string.enfermedad_relacionada_embarazo));}
        if(Data.get(context.getString(R.string.certificado_unico_discapacidad))!=null){CertificadoDiscapacidad=Data.get(context.getString(R.string.certificado_unico_discapacidad));}
        if(Data.get(context.getString(R.string.tipo_discapacidad))!=null){TipoDiscapacidad=Data.get(context.getString(R.string.tipo_discapacidad));}
        if(Data.get(context.getString(R.string.acompañamiento))!=null){Acompañamiento=Data.get(context.getString(R.string.acompañamiento));}
        if(Data.get(context.getString(R.string.transtornos_en_niños))!=null){TrastornoNiños=Data.get(context.getString(R.string.transtornos_en_niños));}
        if(Data.get(context.getString(R.string.adicciones))!=null){Adicciones=Data.get(context.getString(R.string.adicciones));}
        if(Data.get(context.getString(R.string.ocio))!=null){ActividadesOcio=Data.get(context.getString(R.string.ocio));}
        if(Data.get(context.getString(R.string.donde_ocio).replace("?","").replace("¿",""))!=null)
        {LugarOcio=Data.get(context.getString(R.string.donde_ocio).replace("?","").replace("¿",""));}
        if(Data.get(context.getString(R.string.tipo_violencia))!=null){TipoViolencia=Data.get(context.getString(R.string.tipo_violencia));}
        if(Data.get(context.getString(R.string.modalidad_violencia))!=null){ModalidadViolencia=Data.get(context.getString(R.string.modalidad_violencia));}
        if(Data.get(context.getString(R.string.trastornos_mentales))!=null){TrastornosMentales=Data.get(context.getString(R.string.trastornos_mentales));}
        if(Data.get(context.getString(R.string.enfermedad_cronica))!=null){EnfermedadCronica=Data.get(context.getString(R.string.enfermedad_cronica));}
        if(Data.get(context.getString(R.string.plan_social))!=null){PlanSocial=Data.get(context.getString(R.string.plan_social));}
        if(Data.get(context.getString(R.string.realizo_test_hpv))!=null){HPV=Data.get(context.getString(R.string.realizo_test_hpv)); }
        if(Data.get(context.getString(R.string.latitud))!=null){Latitud=Data.get(context.getString(R.string.latitud));}
        if(Data.get(context.getString(R.string.longitud))!=null){Longitud=Data.get(context.getString(R.string.longitud));}
        if(Data.get(context.getString(R.string.fecha))!=null){Fecha=Data.get(context.getString(R.string.fecha));}
        if(Data.get(context.getString(R.string.code_hpv))!=null){CodeHPV=Data.get(context.getString(R.string.code_hpv));}
        if(Data.get(context.getString(R.string.antecedentes_familiares))!=null){AntecedentesCronicos=Data.get(context.getString(R.string.antecedentes_familiares));}
        if(Data.get("ID ENCUESTADOR")!=null){IdEncuestador=Data.get("ID ENCUESTADOR");}
    }

    public void DefineValues(Context context){
        cabeceraPersona.add(context.getString(R.string.celular));
        cabeceraPersona.add(context.getString(R.string.fijo));
        cabeceraPersona.add(context.getString(R.string.mail));
        cabeceraPersona.add(context.getString(R.string.factores_riesgo));
        cabeceraPersona.add(context.getString(R.string.efector));
        cabeceraPersona.add(context.getString(R.string.observaciones));
        cabeceraPersona.add(context.getString(R.string.nombre_apellido_contacto));
        cabeceraPersona.add(context.getString(R.string.telefono_contacto));
        cabeceraPersona.add(context.getString(R.string.parentezco_contacto));
        cabeceraPersona.add(context.getString(R.string.ocupacion));
        cabeceraPersona.add(context.getString(R.string.educacion));
        cabeceraPersona.add(context.getString(R.string.vitamina));
        cabeceraPersona.add(context.getString(R.string.fecha_nacimiento));
        cabeceraPersona.add(context.getString(R.string.fecha_parto));
        cabeceraPersona.add(context.getString(R.string.ultimo_control));
        cabeceraPersona.add(context.getString(R.string.enfermedad_relacionada_embarazo));
        cabeceraPersona.add(context.getString(R.string.certificado_unico_discapacidad));
        cabeceraPersona.add(context.getString(R.string.tipo_discapacidad));
        cabeceraPersona.add(context.getString(R.string.acompañamiento));
        cabeceraPersona.add(context.getString(R.string.transtornos_en_niños));
        cabeceraPersona.add(context.getString(R.string.adicciones));
        cabeceraPersona.add(context.getString(R.string.ocio));
        cabeceraPersona.add(context.getString(R.string.donde_ocio));
        cabeceraPersona.add(context.getString(R.string.tipo_violencia));
        cabeceraPersona.add(context.getString(R.string.modalidad_violencia));
        cabeceraPersona.add(context.getString(R.string.trastornos_mentales));
        cabeceraPersona.add(context.getString(R.string.enfermedad_cronica));
        cabeceraPersona.add(context.getString(R.string.plan_social));
        cabeceraPersona.add(context.getString(R.string.realizo_test_hpv));
        cabeceraPersona.add(context.getString(R.string.code_hpv));
        cabeceraPersona.add(context.getString(R.string.antecedentes_familiares));
        cabeceraPersona.add("ID ENCUESTADOR");

        cabeceraPersona.add(context.getString(R.string.nombre));
        cabeceraPersona.add(context.getString(R.string.apellido));
        cabeceraPersona.add(context.getString(R.string.dni));
        cabeceraPersona.add(context.getString(R.string.sexo));
        cabeceraPersona.add(context.getString(R.string.qr));
    }

    private void cargarDatosEditar(){
        datosEditar.put("DNI","");
        datosEditar.put("APELLIDO","");
        datosEditar.put("NOMBRE","");
        datosEditar.put("FECHA DE NACIMIENTO","");
        datosEditar.put("SEXO","");
        for (int i=0; i<cabeceraPersona.size(); i++){
            datosEditar.put(cabeceraPersona.get(i),"");
        }

        DNI=datosEditar.get("DNI");
        Nombre=datosEditar.get("NOMBRE");
        Sexo=datosEditar.get("SEXO");
        Apellido=datosEditar.get("APELLIDO");
        Celular=datosEditar.get("CELULAR");
        Fijo=datosEditar.get("FIJO");
        Mail=datosEditar.get("MAIL");
        Edad=datosEditar.get("FECHA DE NACIMIENTO");
        FactoresDeRiesgo=datosEditar.get("FACTORES DE RIESGO");
        Vacunas="";
        LoteVacuna="";
        CodfigoFactorRiesgo="";
        UnidadEdad="";
        Efector=datosEditar.get("EFECTOR");
        Observaciones=datosEditar.get("OBSERVACIONES");
        Limpieza="";
        NombreContacto=datosEditar.get("NOMBRE Y APELLIDO");
        TelefonoContacto=datosEditar.get("TELEFONO CONTACTO");
        ParentezcoContacto=datosEditar.get("PARENTEZCO");
        Ocupacion=datosEditar.get("INGRESO Y OCUPACION");
        Educacion=datosEditar.get("EDUCACION");
        Temperatura="";
        Tos="";
        Garganta="";
        Respiratorio="";
        Anosmia="";
        Disgeusia="";
        Vitamina="";
        FechaParto=datosEditar.get("DIA/MES/AÑO");
        UltimoControlEmbarazo=datosEditar.get("ULTIMO CONTROL");
        EnfermedadEmbarazo=datosEditar.get("ENFERMEDAD ASOCIADA AL EMBARAZO");
        CertificadoDiscapacidad=datosEditar.get("CERTIFICADO UNICO DE DISCAPACIDAD");
        TipoDiscapacidad=datosEditar.get("TIPO DE DISCAPACIDAD");
        Acompañamiento=datosEditar.get("ACOMPAÑAMIENTO");
        TrastornoNiños=datosEditar.get("TRASTORNOS EN NIÑOS");
        Adicciones=datosEditar.get("ADICCIONES");
        //ActividadesOcio=datosEditar.get("ACTIVIDADES DE OCIO");
        LugarOcio=datosEditar.get("¿DONDE REALIZA LAS ACTIVIDADES?");
        TipoViolencia=datosEditar.get("TIPO DE VIOLENCIA");
        ModalidadViolencia=datosEditar.get("MODALIDAD DE LA VIOLENCIA");
        TrastornosMentales=datosEditar.get("TRASTORNOS MENTALES");
        EnfermedadCronica=datosEditar.get("ENFERMEDADES CRONICAS");
        PlanSocial=datosEditar.get("PLAN SOCIAL");
    }

    public String FormatoGuardarDengue(){
        // DNI;APELLIDO;NOMBRE;EDAD;UNIDAD EDAD;FECHA DE NACIMIENTO;EFECTOR;FACTORES DE RIESGO;CODIGO SISA F. DE RIESGO;VACUNAS;LOTE DE VACUNA
        return DNI + ";" + Apellido + ";" + Nombre;
    }

    public void CalcularEdad(int year, int month, int day){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date1 = new Date();
        String fecha1 = dateFormat.format(date1);
        String[] partes = fecha1.split("-");
        int anioactual = Integer.parseInt(partes[0]);
        int mesactual = Integer.parseInt(partes[1]);
        int diaactual = Integer.parseInt(partes[2]);
        Date Actual = new Date(anioactual, mesactual, diaactual);
        if(Nacimiento!="mm dd, aaaa"){
            Date Nacimiento = new Date(year, month, day);
            long diferencia = Actual.getTime() - Nacimiento.getTime();
            long segsMilli = 1000;
            long minsMilli = segsMilli * 60;
            long horasMilli = minsMilli * 60;
            long diasMilli = horasMilli * 24;
            long mesesMillo = diasMilli * 30;
            long añosMilli = diasMilli * 365;
            long AñosTranscurridos = diferencia / añosMilli;
            if(AñosTranscurridos<2){
                long MesesTranscurridos = diferencia / mesesMillo;
                Edad=Long.toString(MesesTranscurridos);
                UnidadEdad="MESES";
            }
            else {Edad=Long.toString(AñosTranscurridos);
                UnidadEdad="AÑOS";}
        }else Edad="";
    }

    public void save(HashMap<String, String> values){
       BDData adminBData = new BDData(context, "BDData", null, 1);
       adminBData.insertDataCache(values);
       Data.putAll(values);
    }

    public HashMap<String, String> getDataCache(BDData admin){
        HashMap<String,String> value = new HashMap<>();
        value = admin.SearchDataCache();
        Data.putAll(value);
        return value;
    }

    public HashMap<String, String> getDataCacheUD(BDData admin){
        HashMap<String,String> value = new HashMap<>();
        value = admin.getValuesCacheUD();
        Data.putAll(value);
        return value;
    }
}

