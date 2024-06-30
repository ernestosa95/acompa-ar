package acompanar.ManagementModule.ShareDataManagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import acompanar.BasicObjets.PollsterClass;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.BasicObjets.FamiliarUnityClass;
import acompanar.BasicObjets.PersonClass;
import com.example.acompanar.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static android.os.Environment.getExternalStorageDirectory;

public class Archivos implements Serializable {

    // Defino los valores
    Context context;

    public Archivos(Context newContext){
        context = newContext;
    }

    //----------------------------------------------------------------------------------------------
    //AEDES
    public ArrayList<String> getCodeCabecerasAedes(Context oldcontext){
        context = oldcontext;
        ArrayList<String> values = new ArrayList<>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_aedes);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                values.add(data[0]);
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public HashMap<String, String> getMapCategoriesAedes(){
        HashMap<String, String> values = new HashMap<String,String>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_aedes);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                values.put(data[0], data[1]);
                //Log.e(data[0], data[1]);
                values.put(data[1], data[0]);
                //Log.e(data[1], data[0]);
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public void CreateFileShareAedes(ArrayList<String> dates){
        File nuevaCarpeta = new File(context.getExternalFilesDir(null).toString());
        //Log.e("url_aedes", context.getExternalFilesDir(null).toString());
        nuevaCarpeta.mkdirs();

        HashMap<String,String> mapCabecera = getMapCategoriesAedes();

        String NombreArchivo;
        BDData adminBDData = new BDData(context, "BDData", null, 1);
        //Log.e("fechasaedes", Integer.toString(dates.size()));
        for(int i=0; i<dates.size(); i++) {

            NombreArchivo = "RelevAr_Aedes" + dates.get(i).replace(" ","").
                    replace(".","") + ".csv";

            // RECUPERO LOS DATOS DEL DIA EN PARTICULAR

            ArrayList<HashMap<String, String>> values = new ArrayList<>();
            values = adminBDData.searchValuesAedesForDate(dates.get(i));
            //Log.e("values", Integer.toString(values.size()));
            String cabecera = "INFORME DIARIO DE INSPECCION Y DESTRUCCION DE RECIPIENTES - VIGILANCIA Y CONTROL DEL AEDES AEGYPTI\n";
            cabecera+= "FECHA DE LOS REGISTROS:,"+dates.get(i).replace(","," ")+"\n";
            // Cabecera principal ------------------------------------------------------------------
            cabecera+="COORDENADAS,,,,VIVIENDAS,,ELEMENTOS EN DESUSO - OTROS,,,BOTELLAS VARIAS,,,RECIPIENTES PLASTICOS,,," +
                    "MACETA-FLOREROS-PLANTA ACUATICA,,,HUECO O AXILA DE ARBOL,,,CANALETA PARA LLUVIA,,,CUBIERTAS,,," +
                    "PILETAS-CISTERNAS-ALJIBES,,,TANQUES BASJOS Y ELEVADOS-BARRILES-TONELES,,,TOTALES,,,\n";
            // Cabecera secundaria -----------------------------------------------------------------
            cabecera+= "LATITUD, LONGITUD, TIPO DE TRABAJO, LARVICIDA UTILIZADO, NUMERO DE RESIDENTES, CERRADAS O RENUENTES";
            for (int j=0; j<10; j++){cabecera+=",FA,T,I";}
            cabecera+=",DESTRUIDOS\n";
            for (HashMap<String, String> value : values){
                //----------------------------------------------------------------------------------
                cabecera+= value.get("LATITUD")+","+value.get("LONGITUD");
                //Log.e("LONGITU AEDES", value.get("LONGITUD"));
                //----------------------------------------------------------------------------------
                cabecera+=",";
                //Log.e("marca1_aedes", cabecera);
                for(int j=1; j<4; j++){
                    if(value.get("REAE1_"+j)!=null) {
                        cabecera += "-"+mapCabecera.get("REAE1_"+j).split("_")[1];
                    }
                }
                //----------------------------------------------------------------------------------
                cabecera+=",";
                for(int j=1; j<4; j++){
                    if(value.get("REAE3_"+j)!=null) {
                        cabecera += "-"+mapCabecera.get("REAE3_"+j).split("_")[1];
                    }
                }
                //----------------------------------------------------------------------------------
                cabecera+=",0";
                //----------------------------------------------------------------------------------
                cabecera+=",";
                if (value.get("REAE2_1")!=null){
                    cabecera+="si";
                }else { cabecera+="no";}
                //----------------------------------------------------------------------------------
                int[] totales = {0,0,0};

                for (int j=5; j<14; j++){
                    for (int k=1; k<4; k++){
                        if (value.get("REAE"+j+"_"+k)!=null) {
                            cabecera += "," + value.get("REAE" + j + "_" + k);
                            totales[k-1]+=Integer.parseInt(value.get("REAE" + j + "_" + k));
                        }else{cabecera+=",0";}
                    }
                }
                //----------------------------------------------------------------------------------
                cabecera+=","+totales[0]+","+totales[1]+","+totales[2];
                //----------------------------------------------------------------------------------
                cabecera+=",";
                if (value.get("REAE4")!=null){
                    cabecera+=value.get("REAE4");
                }else {cabecera+=0;}
                //----------------------------------------------------------------------------------
                cabecera+=",OPERARIO:,"+value.get("ENCUESTADOR")+"\n";
            }

            File dir = new File(nuevaCarpeta, NombreArchivo);
            //Log.e("root archivo", dir.getAbsolutePath());

            try {
                if (dir.exists()){dir.delete();}
                FileOutputStream fOut = new FileOutputStream(dir);

                //el true es para
                // que se agreguen los datos al final sin perder los datos anteriores
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                myOutWriter.append(cabecera);
                //myOutWriter.append(data);

                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        adminBDData.close();
    }
    //----------------------------------------------------------------------------------------------
    // Para la version 2.2

    //DOMICILIO
    public ArrayList<String> getCodeCabecerasDomicilio(Context oldcontext){
        context = oldcontext;
        ArrayList<String> values = new ArrayList<>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_domicilio);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                if(data[0].split("_")[1].equals("0")) {
                    values.add(data[0]);
                    //Log.e("cabeceras", data[0]);
                }
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public ArrayList<String> getCodeCabecerasDomicilioBoton(Context oldcontext, String boton){
        context = oldcontext;
        ArrayList<String> values = new ArrayList<>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_domicilio);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                if(data[2].equals(boton)) {
                    values.add(data[0]);
                    //Log.e("cabeceras", data[0]);
                }
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public ArrayList<String> getDomicilioBotons(){
        ArrayList<String> values = new ArrayList<>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_domicilio);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                if(!values.contains(data[2]) && !data[2].equals("NG")) {
                    values.add(data[2]);
                }
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public HashMap<String, String> getMapCategoriesDomicilio(){
        HashMap<String, String> values = new HashMap<String,String>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_domicilio);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                values.put(data[0], data[1]);
                //Log.e(data[0], data[1]);
                values.put(data[1], data[0]);
                //Log.e(data[1], data[0]);
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }
    //----------------------------------------------------------------------------------------------
    // Para la version 2.2

    //DOMICILIO
    public ArrayList<String> getCodeCabecerasPersonas(Context oldcontext){
        context = oldcontext;
        ArrayList<String> values = new ArrayList<>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_persona);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                if(data[0].split("_")[1].equals("0")) {
                    values.add(data[0]);
                    //Log.e("cabeceras", data[0]);
                }
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public ArrayList<String> getCodeCabecerasPersonasBoton(Context oldcontext, String boton){
        context = oldcontext;
        ArrayList<String> values = new ArrayList<>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_persona);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                if(data[2].equals(boton)) {
                    values.add(data[0]);
                    //Log.e("cabeceras", data[0]);
                }
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public ArrayList<String> getCodeCabecerasPersonasGuardable(Context oldcontext){
        context = oldcontext;
        ArrayList<String> values = new ArrayList<>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_persona);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                if(!data[2].equals("NG") && !data[2].equals("ND")) {
                    values.add(data[0]);
                    //Log.e("cabeceras", data[0]);
                }
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }

    public HashMap<String, String> getMapCategoriesPersonas(){
        HashMap<String, String> values = new HashMap<String,String>();

        try{
            InputStream fis = context.getResources().openRawResource(R.raw.categorias_persona);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String myData="";
            while ((myData=br.readLine())!=null){
                String[] data = myData.split(",");
                values.put(data[0], data[1]);
                //Log.e(data[0], data[1]);
                values.put(data[1], data[0]);
                //Log.e(data[1], data[0]);
            }

            br.close();
            in.close();
            fis.close();
        }
        catch (IOException e){
            Toast.makeText(context, R.string.ocurrio_error, Toast.LENGTH_SHORT).show();
        }

        return values;
    }
    //----------------------------------------------------------------------------------------------
    public Intent Compartir(String nombreArchivo){
        File nuevaCarpeta = new File(getExternalStorageDirectory(), "RelevAr");
        nuevaCarpeta.mkdirs();

        File dir = new File(nombreArchivo);
        //Uri path = FileProvider.getUriForFile(context, "com.example.relevar", dir);
        Uri path = Uri.fromFile(dir);
        //Uri path = Uri.parse(dir.toString());
        //Log.e("compartir", "marca 0");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, nombreArchivo);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Valores.");
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        return emailIntent;
        //context.startActivity(Intent.createChooser(emailIntent, "SUBIR ARCHIVO"));
    }

    public ArrayList<String> listadoFechasArchivo(){
        /* Se crea la lista de los archivos .csv que estan disponibles en la memoria interna en la carpeta
         * RelevAr*/
        ArrayList<String> listaFechasArchivos = new ArrayList<String>();
        ArrayList<String> listaRutasFechasArchivos = new ArrayList<String>();
        String RutaDirectorio = "/storage/emulated/0/RelevAr";
        File directorioactual = new File(RutaDirectorio);
        //Log.e("ruta madre", directorioactual.getAbsolutePath());
        File[] listaArchivos = new File[]{};
        //Log.e("list",directorioactual.listFiles().toString());
        if (directorioactual.listFiles() != null) {
            listaArchivos = directorioactual.listFiles();
        }

        int x=0;

        //Log.e("listado", Integer.toString(listaArchivos.length));
        for(int j=0; j<listaArchivos.length; j++){
            listaRutasFechasArchivos.add(listaArchivos[j].getPath());
            //Log.e("ruta_arc", listaArchivos[j].getPath());
        }

        Collections.sort(listaRutasFechasArchivos, String.CASE_INSENSITIVE_ORDER);

        for(int i=x; i<listaRutasFechasArchivos.size(); i++){
            File archivo = new File(listaRutasFechasArchivos.get(i));
            if(archivo.isFile()){
                String[] auxCorte = archivo.getName().split("-");
                if (auxCorte.length>3) {
                    String auxNombre = auxCorte[1] + "-" + auxCorte[2] + "-" + auxCorte[3];
                    listaFechasArchivos.add(auxNombre);
                }
            }
        }
        Collections.reverse(listaFechasArchivos);
        return listaFechasArchivos;
    }

    public ArrayList<String> listadoFechasArchivoAedes(){
        /* Se crea la lista de los archivos .csv que estan disponibles en la memoria interna en la carpeta
         * RelevAr*/
        ArrayList<String> listaFechasArchivos = new ArrayList<String>();
        ArrayList<String> listaRutasFechasArchivos = new ArrayList<String>();
        String RutaDirectorio = "/storage/emulated/0/RelevAr/PLANILLAS DENGUE";
        File directorioactual = new File(RutaDirectorio);
        File[] listaArchivos = directorioactual.listFiles();

        int x=0;

        for(File archivo : listaArchivos){
            listaRutasFechasArchivos.add(archivo.getPath());
        }

        Collections.sort(listaRutasFechasArchivos, String.CASE_INSENSITIVE_ORDER);

        for(int i=x; i<listaRutasFechasArchivos.size(); i++){
            File archivo = new File(listaRutasFechasArchivos.get(i));
            if(archivo.isFile()){
                String[] auxCorte = archivo.getName().split("-");
                String auxNombre = auxCorte[1]+"-"+auxCorte[2]+"-"+auxCorte[3].replace(".csv","");
                listaFechasArchivos.add(auxNombre);}
        }
        Collections.reverse(listaFechasArchivos);
        return listaFechasArchivos;
    }

    public void RecuperarDatosCsv(){
        //FECHAS DE LOS ARCHIVOS CSV PRESENTES
        //Log.e("recuperar", "datos");
        ArrayList<String> fechas = this.listadoFechasArchivo();
        //Toast.makeText(context, Integer.toString(fechas.size()), Toast.LENGTH_SHORT).show();
        File nuevaCarpeta = new File(getExternalStorageDirectory(), "RelevAr");
        //("ruta", nuevaCarpeta.getAbsolutePath());
        nuevaCarpeta.mkdirs();

        //("fechas", Integer.toString(fechas.size()));
        for(int i=0; i<fechas.size(); i++){
            //
            // ("fechas", Integer.toString(i));
            String fecha = fechas.get(i).replace(".csv","");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(fecha);
                fecha = DateFormat.getDateInstance().format(date);
            }catch (ParseException e){}

            HashMap<String,String> data = new HashMap<>();
            String[] cabecera;

            String NombreArchivo = "RelevAr-" + fechas.get(i);
            File dir = new File(nuevaCarpeta, NombreArchivo);
            String strLine = "";
            try {
                FileInputStream fis = new FileInputStream(dir);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                strLine = br.readLine();
                //
                // ("marca", strLine);
                cabecera = strLine.split(";");

                while ((strLine = br.readLine())!=null){
                    for (int j = 0; j < cabecera.length; j++){
                        data.put(cabecera[j].replace(".","").replace("?","").
                                        replace("¿","").replace("/","")
                                ,strLine.split(";")[j]);
                    }

                    //CARGO LOS DATOS DE LA FAMILIA
                    FamiliarUnityClass familiarUnityClass = new FamiliarUnityClass(context);
                    familiarUnityClass.Data.put(context.getString(R.string.latitud), data.get("COORDENADAS").split(" ")[0]);
                    familiarUnityClass.Data.put(context.getString(R.string.longitud), data.get("COORDENADAS").split(" ")[1]);
                    familiarUnityClass.Data.put(context.getString(R.string.fecha), fecha);
                    familiarUnityClass.Data.put(context.getString(R.string.codigo_color), data.get("ESTADO"));
                    familiarUnityClass.Data.put(context.getString(R.string.numero), data.get("NUMERO"));

                    PersonClass clasePersona = new PersonClass(context);
                    clasePersona.Data.put(context.getString(R.string.fecha), fecha);
                    clasePersona.Data.put(context.getString(R.string.latitud), data.get("COORDENADAS").split(" ")[0]);
                    clasePersona.Data.put(context.getString(R.string.longitud), data.get("COORDENADAS").split(" ")[1]);
                    clasePersona.Data.put(context.getString(R.string.ocio), data.get("ACTIVIDADES DE OCIO"));
                    clasePersona.Data.put(context.getString(R.string.donde_ocio), data.get("DONDE REALIZA LAS ACTIVIDADES"));
                    clasePersona.Data.put(context.getString(R.string.fecha_nacimiento).replace("/",""),
                            data.get("FECHA DE NACIMIENTO"));
                    clasePersona.Data.put(context.getString(R.string.ultimo_control), data.get("ULTIMO CONTROL"));
                    clasePersona.Data.put(context.getString(R.string.fecha_parto), data.get("DIAMESAÑO"));

                    Object[] objects = data.keySet().toArray();
                    for (int j = 0; j < objects.length; j++) {
                        if (familiarUnityClass.FamilyValues().contains(objects[j])){
                            familiarUnityClass.Data.put(objects[j].toString(), data.get(objects[j]));
                        }
                        if (clasePersona.PersonValues().contains(objects[j])){
                            clasePersona.Data.put(objects[j].toString(), data.get(objects[j]));
                        }
                    }

                    BDData adminBDData = new BDData(context, "BDData", null, 1);
                    familiarUnityClass.LoadDataHashToParameters();
                    if (!adminBDData.ExistFamily(familiarUnityClass)) {
                        if (clasePersona.Data.get(context.getString(R.string.nombre)).equals(context.getString(R.string.vivienda_renuente))){
                            familiarUnityClass.Data.put(context.getString(R.string.situacion_habitacional), context.getString(R.string.vivienda_renuente));
                            adminBDData.insert_family(familiarUnityClass);
                        }else {
                            if (clasePersona.Data.get(context.getString(R.string.nombre)).length() == 0 &&
                                    clasePersona.Data.get(context.getString(R.string.apellido)).length() == 0 &&
                                    clasePersona.Data.get(context.getString(R.string.dni)).length() == 0) {
                                familiarUnityClass.Data.put(context.getString(R.string.situacion_habitacional), context.getString(R.string.vivienda_deshabitada));
                                adminBDData.insert_family(familiarUnityClass);
                            }else{
                                familiarUnityClass.Data.put(context.getString(R.string.situacion_habitacional), context.getString(R.string.habitada));
                                adminBDData.insert_family(familiarUnityClass);
                                adminBDData.insert_person(clasePersona);
                            }
                        }
                    }
                    adminBDData.close();
                }

                br.close();
                in.close();
                fis.close();
            } catch (IOException e) {
                Log.e("msg", e.toString());
            }
        }

    }

    public void RecuperarDatosCsvGeneral(){
        //FECHAS DE LOS ARCHIVOS CSV PRESENTES
        //Log.e("recuperar", "datos");
        ArrayList<String> fechas = this.listadoFechasArchivo();
        //Toast.makeText(context, Integer.toString(fechas.size()), Toast.LENGTH_SHORT).show();
        File nuevaCarpeta = new File(getExternalStorageDirectory(), "RelevAr");
        //File nuevaCarpeta = new File(context.getExternalFilesDir(null).toString());
        //Log.e("ruta", nuevaCarpeta.getAbsolutePath());
        nuevaCarpeta.mkdirs();


        //Log.e("fechas", Integer.toString(fechas.size()));
        //for(int i=0; i<fechas.size(); i++){
            //String fecha = fechas.get(i).replace(".csv","");
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //try {
            //    Date date = format.parse(fecha);
            //    fecha = DateFormat.getDateInstance().format(date);
            //}catch (ParseException e){}

            HashMap<String,String> data = new HashMap<>();
            String[] cabecera;



            String NombreArchivo = "RelevArImportar.csv";
            File dir = new File(nuevaCarpeta, NombreArchivo);
            String strLine = "";
            try {
                FileInputStream fis = new FileInputStream(dir);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                strLine = br.readLine();
                //Log.e("marca", strLine);
                cabecera = strLine.split(";");

                while ((strLine = br.readLine())!=null){
                    for (int j = 0; j < cabecera.length; j++){
                        data.put(cabecera[j].replace(".","").replace("?","").
                                        replace("¿","").replace("/","")
                                ,strLine.split(";")[j]);
                    }

                    //CARGO LOS DATOS DE LA FAMILIA
                    FamiliarUnityClass familiarUnityClass = new FamiliarUnityClass(context);
                    familiarUnityClass.Data.put(context.getString(R.string.latitud), data.get("COORDENADAS").split(" ")[0]);
                    familiarUnityClass.Data.put(context.getString(R.string.longitud), data.get("COORDENADAS").split(" ")[1]);
                    familiarUnityClass.Data.put(context.getString(R.string.fecha), "2022-09-22");
                    familiarUnityClass.Data.put(context.getString(R.string.codigo_color), data.get("ESTADO"));
                    familiarUnityClass.Data.put(context.getString(R.string.numero), data.get("NUMERO"));

                    PersonClass clasePersona = new PersonClass(context);
                    clasePersona.Data.put(context.getString(R.string.fecha), "2022-09-22");
                    clasePersona.Data.put(context.getString(R.string.latitud), data.get("COORDENADAS").split(" ")[0]);
                    clasePersona.Data.put(context.getString(R.string.longitud), data.get("COORDENADAS").split(" ")[1]);
                    clasePersona.Data.put(context.getString(R.string.ocio), data.get("ACTIVIDADES DE OCIO"));
                    clasePersona.Data.put(context.getString(R.string.donde_ocio), data.get("DONDE REALIZA LAS ACTIVIDADES"));
                    clasePersona.Data.put(context.getString(R.string.fecha_nacimiento).replace("/",""),
                            data.get("FECHA DE NACIMIENTO"));
                    clasePersona.Data.put(context.getString(R.string.ultimo_control), data.get("ULTIMO CONTROL"));
                    clasePersona.Data.put(context.getString(R.string.fecha_parto), data.get("DIAMESAÑO"));

                    Object[] objects = data.keySet().toArray();
                    for (int j = 0; j < objects.length; j++) {
                        if (familiarUnityClass.FamilyValues().contains(objects[j])){
                            familiarUnityClass.Data.put(objects[j].toString(), data.get(objects[j]));
                        }
                        if (clasePersona.PersonValues().contains(objects[j])){
                            clasePersona.Data.put(objects[j].toString(), data.get(objects[j]));
                        }
                    }

                    BDData adminBDData = new BDData(context, "BDData", null, 1);
                    familiarUnityClass.LoadDataHashToParameters();
                    if (!adminBDData.ExistFamily(familiarUnityClass)) {
                        if (clasePersona.Data.get(context.getString(R.string.nombre)).equals(context.getString(R.string.vivienda_renuente))){
                            familiarUnityClass.Data.put(context.getString(R.string.situacion_habitacional), context.getString(R.string.vivienda_renuente));
                            adminBDData.insert_family(familiarUnityClass);
                        }else {
                            if (clasePersona.Data.get(context.getString(R.string.nombre)).length() == 0 &&
                                    clasePersona.Data.get(context.getString(R.string.apellido)).length() == 0 &&
                                    clasePersona.Data.get(context.getString(R.string.dni)).length() == 0) {
                                familiarUnityClass.Data.put(context.getString(R.string.situacion_habitacional), context.getString(R.string.vivienda_deshabitada));
                                adminBDData.insert_family(familiarUnityClass);
                            }else{
                                familiarUnityClass.Data.put(context.getString(R.string.situacion_habitacional), context.getString(R.string.habitada));
                                adminBDData.insert_family(familiarUnityClass);
                                adminBDData.insert_person(clasePersona);
                            }
                        }
                    }
                    adminBDData.close();
                }

                br.close();
                in.close();
                fis.close();
            } catch (IOException e) {
                Log.e("msg", e.toString());
            }
        //}

    }

    public Intent ShareSomeFiles(ArrayList<String> datesfiles, String root){
        //CreateFileShare(datesfiles);
        //File nuevaCarpeta = new File(getExternalStorageDirectory(), root);

        String initCab = "";
        if(root.equals("RelevAr")){
            initCab="RelevAr";
        }
        if (root.equals("Aedes")){
            initCab="RelevAr_Aedes";
        }
        //nuevaCarpeta.mkdirs();

        //-------------------------------------------------
        File dir_aux = context.getExternalFilesDir(null);
        File dir = new File(dir_aux.toString()+"/RelevAr");
        String path = dir.getAbsolutePath();

        File carpeta = new File(path);
        File nuevaCarpeta = new File(context.getExternalFilesDir(null).toString());
        String[] listado = nuevaCarpeta.list();
        if (listado == null || listado.length == 0) {
            Log.e("carpetas", "No hay elementos dentro de la carpeta actual");
        }
        else {
            for (int i=0; i< listado.length; i++) {
                Log.e("carpetas", listado[i]);
            }
        }
        //--------------------------------------------------------------------------------------------

        ArrayList<Uri> uris = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        String msg = "Fechas de registro compartidos:\n";
        //Log.e("vompartir", "marca 1");



        for (int i=0; i<datesfiles.size(); i++){
            //files.add(new File(nuevaCarpeta, initCab + "-" + datesfiles.get(i).replace(".","").
            //        replace(" ","") + ".csv"));
            files.add(new File(nuevaCarpeta.getAbsolutePath()+"/"+ initCab + datesfiles.get(i).replace(".","").
                    replace(" ","") + ".csv"));
            ///storage/emulated/0/Android/data/com.example.relevar/files
            ///storage/emulated/0/Android/data/com.example.relevar/files/RelevAr16ago2022.csv
            //Log.e("fileprovider", String.valueOf(FileProvider.getUriForFile(context, "com.example.relevar", files.get(files.size()-1))));
            //uris.add(FileProvider.getUriForFile(context, "com.example.relevar", files.get(files.size()-1)));
            //uris.add(Uri.fromFile(files.get(files.size()-1)));
            if (files.get(files.size()-1).exists()) {
                uris.add(FileProvider.getUriForFile(context, "com.example.relevar", files.get(files.size()-1)));
                //Log.e("uris", uris.get(uris.size() - 1).toString());
            }else{
                Log.e("arcvivo no existe", files.get(files.size() - 1).toString());
            }
            msg += "◉ "+datesfiles.get(i)+"\n";
        }

        PollsterClass encuestador = new PollsterClass(context);
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ARCHIVOS COMPARTIDO DESDE RELEVAR POR "+
                encuestador.datosEncuestador());
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uris);

        return emailIntent;
    }

    public void CreateFileShare(ArrayList<String> dates){
        //File nuevaCarpeta = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "RelevAr");
        File nuevaCarpeta = new File(context.getExternalFilesDir(null).toString());
        //Log.e("url", context.getExternalFilesDir(null).toString());
        nuevaCarpeta.mkdirs();

        String NombreArchivo;
        BDData adminBDData = new BDData(context, "BDData", null, 1);
        for(int i=0; i<dates.size(); i++) {

            NombreArchivo = "RelevAr" + dates.get(i).replace(" ","").
                    replace(".","") + ".csv";

            // RECUPERO LOS DATOS DEL DIA EN PARTICULAR

            ArrayList<FamiliarUnityClass> families = new ArrayList<>();
            families.addAll(adminBDData.SearchFamilyDate(context, dates.get(i)));

            HashMap<String, ArrayList<PersonClass>> persons = new HashMap<>();
            for (int j=0; j<families.size(); j++){
                persons.put(families.get(j).Data.get("LONGITUD"),
                        adminBDData.SearchPersonsCoordinates(context,
                                families.get(j).Data.get("LATITUD"),
                                families.get(j).Data.get("LONGITUD")));
            }

            //CREO LA CABECERA
            ArrayList<String> categories = new ArrayList<>();
            categories.add("LATITUD");
            categories.add("LONGITUD");
            categories.add("SITUACION HABITACIONAL");
            categories.add("DNI");
            categories.add("APELLIDO");
            categories.add("NOMBRE");
            categories.add("QR");
            String cab = "LATITUD;LONGITUD;SITUACION HABITACIONAL;DNI;APELLIDO;NOMBRE;QR;";
            String data = "";
            HashMap<String,String> options = getMapCategoriesDomicilio();
            for (int j=0; j<families.size(); j++) {
                // CABECERA DE LA FAMILIA
                Object[] objects = families.get(j).Data.keySet().toArray();
                for (int k = 0; k < objects.length; k++) {
                    if (families.get(j).Data.get(objects[k].toString())!=null) {
                        if (!categories.contains(objects[k].toString())) {
                            categories.add(objects[k].toString());
                            if(options.get(objects[k].toString().replace(" ","_"))!=null) {
                                cab += options.get(objects[k].toString().replace(" ","_")) + ";";
                            }else{
                                cab += objects[k].toString() + ";";
                            }
                        }
                    }
                }

                // CABECERA DE LAS PERSONAS
                for (int k = 0; k < persons.get(families.get(j).Data.get("LONGITUD")).size(); k++){
                    PersonClass aux = persons.get(families.get(j).Data.get("LONGITUD")).get(k);
                    Object[] objects1 = aux.Data.keySet().toArray();
                    for (int l = 0; l < objects1.length; l++){
                        if (aux.Data.get(objects1[l].toString())!=null) {
                            if (!categories.contains(objects1[l].toString())) {
                                categories.add(objects1[l].toString());
                                cab += objects1[l].toString() + ";";
                            }
                        }
                    }
                }
            }
            cab += "\n";

            for (int j=0; j<families.size(); j++){
                for (int l = 0; l<persons.get(families.get(j).Data.get("LONGITUD")).size(); l++) {
                    PersonClass aux = persons.get(families.get(j).Data.get("LONGITUD")).get(l);
                    String[] cat = cab.split(";");
                    /*for (int k = 0; k < cat.length; k++) {
                        if (families.get(j).Data.get(cat[k])!=null) {
                            data += families.get(j).Data.get(cat[k]) + ";";
                        }else {
                            if (aux.Data.get(cat[k])!=null) {
                                data += aux.Data.get(cat[k]) + ";";
                            } else {
                                data += ";";
                            }
                        }

                    }*/
                    for (String cabecera : categories){
                        if (families.get(j).Data.get(cabecera)!=null){
                            if (options.get(cabecera.replace(" ","_"))!=null && options.get(families.get(j).Data.get(cabecera))!=null){
                                data += options.get(families.get(j).Data.get(cabecera)) + ";";
                            }else {
                                data += families.get(j).Data.get(cabecera) + ";";
                            }
                        }else{
                            if (aux.Data.get(cabecera)!=null) {
                                data += aux.Data.get(cabecera) + ";";
                            } else {
                                data += ";";
                            }
                        }
                    }
                }
                data += "\n";
            }

            File dir = new File(nuevaCarpeta, NombreArchivo);
            //Log.e("root archivo", dir.getAbsolutePath());
            //File dir = new File(NombreArchivo);


            try {
                if (dir.exists()){dir.delete();}
                FileOutputStream fOut = new FileOutputStream(dir);

                //el true es para
                // que se agreguen los datos al final sin perder los datos anteriores
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                myOutWriter.append(cab);
                myOutWriter.append(data);

                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        adminBDData.close();
        }

    public void CreateFileShareGNUHealth(ArrayList<String> dates){
        File nuevaCarpeta = new File(context.getExternalFilesDir(null).toString());
        //nuevaCarpeta.mkdirs();

        String NombreArchivo;

        for(int i=0; i<dates.size(); i++) {

            NombreArchivo = "RelevAr" + dates.get(i).replace(" ","").
                    replace(".","") + ".csv";

            // RECUPERO LOS DATOS DEL DIA EN PARTICULAR
            BDData adminBDData = new BDData(context, "BDData", null, 1);
            ArrayList<FamiliarUnityClass> families = new ArrayList<>();
            families.addAll(adminBDData.SearchFamilyDate(context, dates.get(i)));

            HashMap<String, ArrayList<PersonClass>> persons = new HashMap<>();
            for (int j=0; j<families.size(); j++){
                persons.put(families.get(j).Data.get("LONGITUD"),
                        adminBDData.SearchPersonsCoordinates(context,
                                families.get(j).Data.get("LATITUD"),
                                families.get(j).Data.get("LONGITUD")));
            }
            adminBDData.close();

            //CREO LA CABECERA
            String cab = "CALLE;NUMERO;COORDENADAS;ESTADO;GRUPO FAMILIAR;MENORES;MAYORES;DNI;APELLIDO;NOMBRE;FECHA DE NACIMIENTO;SEXO;QR;NUMERO CASA CARTOGRAFIA;" +
                    "TIPO DE VIVIENDA;DUEÑO DE LA VIVIENDA;CANTIDAD DE PIEZAS;LUGAR PARA COCINAR;USA PARA COCINAR;" +
                    "MATERIAL PREDOMINANTE EN LAS PAREDES EXTERIORES;REVESTIMIENTO EXTERNO O REVOQUE;MATERIAL DE LOS PISOS;" +
                    "CIELORRASO;MATERIAL PREDOMINANTE EN LA CUBIERTA EXTERIOR DEL TECHO;AGUA;ORIGEN AGUA;EXCRETAS;ELECTRICIDAD;" +
                    "GAS;ALMACENA AGUA DE LLUVIA;ÁRBOLES;BAÑO;EL BAÑO TIENE;NIEVE Y/O HIELO EN LA CALLE;PERROS SUELTOS;TELEFONO FAMILIAR;" +
                    "CELULAR;FIJO;MAIL;FACTORES DE RIESGO;EFECTOR;NOMBRE Y APELLIDO;TELEFONO CONTACTO;PARENTEZCO;INGRESO Y OCUPACION;" +
                    "EDUCACION;VITAMINA D;FECHA PROBABLE DE PARTO;ULTIMO CONTROL DE EMBARAZO;ENFERMEDAD ASOCIADA AL EMBARAZO;" +
                    "CERTIFICADO UNICO DE DISCAPACIDAD;TIPO DE DISCAPACIDAD;ACOMPAÑAMIENTO;TRASTORNOS EN NIÑOS;ADICCIONES;" +
                    "ACTIVIDADES OCIO;¿DONDE REALIZA LAS ACTIVIDADES?;TIPO DE VIOLENCIA;MODALIDAD DE LA VIOLENCIA;" +
                    "TRASTORNOS MENTALES;ENFERMEDADES CRONICAS;PLAN SOCIAL;REALIZO TEST HPV;CODIGO HPV;OBSERVACIONES VIVIENDA\n";
            String data = "";

            for (int j=0; j<families.size(); j++){
                for (int l = 0; l<persons.get(families.get(j).Data.get("LONGITUD")).size(); l++) {
                    PersonClass aux = persons.get(families.get(j).Data.get("LONGITUD")).get(l);
                    //Toast.makeText(context, Integer.toString(families.size()), Toast.LENGTH_SHORT).show();
                    String[] cat = cab.split(";");
                    for (int k = 0; k < cat.length; k++) {
                        if (families.get(j).Data.get(cat[k].replace("/",""))!=null) {
                            data += families.get(j).Data.get(cat[k].replace("/","")) + ";";
                        }else {
                            if (aux.Data.get(cat[k].replace("?","").replace("¿",""))
                                    !=null) {
                                data += aux.Data.get(cat[k].replace("?","").replace("¿","")) + ";";
                            } else {
                                if (cat[k].equals("COORDENADAS")){
                                    data += aux.Data.get("LATITUD") +" "+ aux.Data.get("LONGITUD") + ";";
                                }else {
                                    if (cat[k].equals("ESTADO")){
                                        data += families.get(j).Data.get(context.getString(R.string.codigo_color)) +";";
                                    }else {
                                        if(cat[k].equals("ACTIVIDADES OCIO")) {
                                            data += aux.Data.get("OCIO") +";";
                                        }else{
                                            if (cat[k].equals("REVESTIMIENTO EXTERNO O REVOQUE")){
                                                data += families.get(j).Data.get(context.getString(R.string.data_revoque)) +";";
                                            }else {
                                                if(cat[k].equals("NUMERO")){
                                                    data += families.get(j).Data.get(context.getString(R.string.numero)) +";";
                                                }else {
                                                    if(cat[k].equals("GRUPO FAMILIAR")){
                                                        data += Integer.toString(Integer.parseInt(families.get(j).Data.get(context.getString(R.string.menores))) +
                                                                Integer.parseInt(families.get(j).Data.get(context.getString(R.string.mayores)))) +
                                                                ";";
                                                    }else {
                                                        if(cat[k].equals("FECHA DE NACIMIENTO")){
                                                            data += aux.Data.get(context.getString(R.string.fecha_nacimiento).replace("/","")) +";";
                                                        }else {
                                                            if(cat[k].equals("ULTIMO CONTROL DE EMBARAZO")){
                                                                data += aux.Data.get("ULTIMO CONTROL") +";";
                                                            }else {
                                                                if(cat[k].equals("FECHA PROBABLE DE PARTO")){
                                                                    data += aux.Data.get(context.getString(R.string.fecha_parto)) +";";
                                                                }else {
                                                                    data += ";";
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }

                    }
                    data += "\n";
                }

            }

            File dir = new File(nuevaCarpeta, NombreArchivo);
            //File dir = new File(NombreArchivo);
            try {
                FileOutputStream fOut = new FileOutputStream(dir);

                //el true es para
                // que se agreguen los datos al final sin perder los datos anteriores
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                myOutWriter.append(cab);
                myOutWriter.append(data);

                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void CreatePDF(FamiliarUnityClass familia, ArrayList<PersonClass> personas){
        String titulo = "INFORME DE VISITA DOMICILIARIA";
        String cabecera_general = "Fecha de registro: "+ familia.Fecha + "                                               Profesional: "+ familia.Data.get("ENCUESTADOR");
        String data_familia = "VIVIENDA\nCantidad de ambientes/habitaciones/piezas: 2\nLa vivienda es: PROPIA";

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        TextPaint textTitulo = new TextPaint();
        TextPaint textGeneral = new TextPaint();
        Bitmap bitmap, bitmapEscala;

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1240,  1754, 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo);

        Canvas canvas = page1.getCanvas();

        //------------------------------------------------------------------------------------------
        // Definiciones ----------------------------------------------------------------------------
        int altoTexNormal = 20;
        int altoTexTitulo = 25;
        // Caracteristicas titulo ------------------------------------------------------------------
        textTitulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textTitulo.setTextSize(altoTexTitulo);

        // Caracteristicas texto comun -------------------------------------------------------------
        textGeneral.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        textGeneral.setTextSize(altoTexNormal);

        //------------------------------------------------------------------------------------------
        // Insercion icono relevAr -----------------------------------------------------------------
        bitmap = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.icon_relevar);
        bitmapEscala = Bitmap.createScaledBitmap(bitmap, 80,80,false);
        canvas.drawBitmap(bitmapEscala, 580, 10, paint);

        // Insercion titulo ------------------------------------------------------------------------
        canvas.drawText(titulo, 387, 125, textTitulo);

        // Insercion cabecera ----------------------------------------------------------------------
        canvas.drawText(cabecera_general, 77, 185, textGeneral);

        // Insercion data vivienda -----------------------------------------------------------------
        ArrayList<String> vivienda = familia.cabeceraVivienda;
        ArrayList<String> servicios_basicos = familia.cabeceraServiciosBasicos;
        ArrayList<String> inspeccion_exterior = familia.cabeceraInspeccionExterior;

        canvas.drawText("VIVIENDA", 77, 225, textTitulo);
        int renglones = 225;
        for (String ud : vivienda){
            renglones+= 10+altoTexNormal;
            if (familia.Data.get(ud)!=null) {
                canvas.drawText(ud + ": " + familia.Data.get(ud), 77, renglones, textGeneral);
            }else{
                canvas.drawText(ud + ": " + "sin dato", 77, renglones, textGeneral);
            }
        }
        canvas.drawText("SERVICIOS BASICOS", 620, 225, textTitulo);
        renglones=225;
        for (String ud : servicios_basicos){
            renglones+=10+altoTexNormal;
            if (familia.Data.get(ud)!=null) {
                canvas.drawText(ud +": "+familia.Data.get(ud), 620, renglones, textGeneral);
            }else{
                canvas.drawText(ud + ": " + "sin dato", 620, renglones, textGeneral);
            }
        }
        canvas.drawText("INSPECCION EXTERIOR", 77, 525, textTitulo);
        renglones=525;
        for (String ud : inspeccion_exterior){
            renglones+=10+altoTexNormal;
            if (familia.Data.get(ud)!=null) {
                canvas.drawText(ud +": "+familia.Data.get(ud), 77, renglones, textGeneral);
            }else{
                canvas.drawText(ud + ": " + "sin dato", 77, renglones, textGeneral);
            }
        }

        //------------------------------------------------------------------------------------------
        // Personas --------------------------------------------------------------------------------
        renglones = 800;
        for (PersonClass persona : personas) {
            renglones+=10+altoTexNormal;
            String cabecera_persona = "Apellido, Nombre: "+persona.Data.get("APELLIDO")+", "+persona.Data.get("NOMBRE");
            canvas.drawText(cabecera_persona, 77, renglones, textTitulo);
            cabecera_persona = "DNI: "+ persona.Data.get("DNI");
            canvas.drawText(cabecera_persona, 930, renglones, textTitulo);

            renglones += 10+altoTexNormal;
            cabecera_persona = "Sexo: "+ persona.Data.get("SEXO");
            canvas.drawText(cabecera_persona, 77, renglones, textTitulo);
            cabecera_persona = "Fecha de nacimiento: "+ persona.Data.get("DIA/MES/AÑO");
            canvas.drawText(cabecera_persona, 620, renglones, textTitulo);

            renglones += 20+altoTexNormal;
            ArrayList<String> cabPersona = persona.cabeceraPersona;
            for (String per : cabPersona){
                if (persona.Data.containsKey(per)) {
                    if (persona.Data.get(per)!=null) {
                        canvas.drawText(per + ": " + persona.Data.get(per), 77, renglones, textGeneral);
                        renglones += 10 + altoTexNormal;
                    }
                }
            }
        }

        pdfDocument.finishPage(page1);

        File file = new File(context.getExternalFilesDir(null).toString());
        String nombreArchivo = "PDFRelevar.pdf";
        File dir = new File(file, nombreArchivo);
        try{
            //Log.e("ruta", dir.getAbsolutePath());
            pdfDocument.writeTo(new FileOutputStream(dir));
            //Log.e("marca", "pdf hecho");
        }catch (Exception e){
            e.printStackTrace();
            //Log.e("marca", "fallo");
        }
        pdfDocument.close();
    }

    public Intent SharePDF(){
        //-------------------------------------------------
        File dir_aux = context.getExternalFilesDir(null);
        File dir = new File(dir_aux.toString()+"/RelevAr");
        String path = dir.getAbsolutePath();

        File carpeta = new File(path);
        File nuevaCarpeta = new File(context.getExternalFilesDir(null).toString());
        String[] listado = nuevaCarpeta.list();
        if (listado == null || listado.length == 0) {
            Log.e("carpetas", "No hay elementos dentro de la carpeta actual");
        }
        else {
            for (int i=0; i< listado.length; i++) {
                Log.e("carpetas", listado[i]);
            }
        }
        //--------------------------------------------------------------------------------------------

        ArrayList<Uri> uris = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        String msg = "Fechas de registro compartidos:\n";
        //Log.e("vompartir", "marca 1");

        files.add(new File(nuevaCarpeta.getAbsolutePath()+"/"+ "PDFRelevar" + ".pdf"));

        if (files.get(files.size()-1).exists()) {
            uris.add(FileProvider.getUriForFile(context, "com.example.relevar", files.get(files.size()-1)));
            //Log.e("uris", uris.get(uris.size() - 1).toString());
        }else{
            Log.e("arcvivo no existe", files.get(files.size() - 1).toString());
        }
        msg += "◉ "+"de una familia"+"\n";


        PollsterClass encuestador = new PollsterClass(context);
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ARCHIVOS COMPARTIDO DESDE RELEVAR POR "+
                encuestador.datosEncuestador());
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uris);

        return emailIntent;
    }
}

















