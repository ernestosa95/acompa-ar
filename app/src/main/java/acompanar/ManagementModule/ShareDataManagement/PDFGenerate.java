package acompanar.ManagementModule.ShareDataManagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;

import androidx.core.content.FileProvider;

import acompanar.BasicObjets.FamiliarUnityClass;
import acompanar.BasicObjets.PersonClass;
import acompanar.BasicObjets.PollsterClass;
import acompanar.ManagementModule.StorageManagement.BDData;
import acompanar.ManagementModule.StorageManagement.SQLitePpal;
import com.example.acompanar.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PDFGenerate {

    private Context context;
    String datavivienda = "";
    HashMap<String, ArrayList> dataDomicilio = new HashMap<>();
    ArrayList<ArrayList> dataPersons = new ArrayList<>();

    FamiliarUnityClass family;
    ArrayList<PersonClass> Persons = new ArrayList<>();


    public PDFGenerate(Context oldContext){
        context = oldContext;
        FamiliarUnityClass family = new FamiliarUnityClass(context);
    }

    public void setDataFamilyForPDF(FamiliarUnityClass familia){
        family = familia;

        Archivos mngFile = new Archivos(context);
        HashMap<String, String> mapeo = mngFile.getMapCategoriesDomicilio();
        ArrayList<String> btnDomicilio = new Archivos(context).getDomicilioBotons();

        for (String btn : btnDomicilio) {
            ArrayList<String> vivienda = new Archivos(context).getCodeCabecerasDomicilioBoton(context, btn);

            ArrayList<String> dataToprint = new ArrayList<>();
            for (String key : vivienda) {
                if (familia.Data.containsKey(key)) {
                    if (familia.Data.get(key) != null && familia.Data.get(key).length() != 0) {
                        if (mapeo.get(familia.Data.get(key)) != null && mapeo.get(familia.Data.get(key)).length() != 0
                                && !familia.Data.get(key).equals("SI") && !familia.Data.get(key).equals("NO")) {

                            dataToprint.add(mapeo.get(key) + ": " + mapeo.get(familia.Data.get(key)));
                        } else {

                            dataToprint.add(mapeo.get(key) + ": " + familia.Data.get(key));
                        }
                    }
                }
            }
            dataDomicilio.put(btn, dataToprint);
        }
    }

    public void setDataPersonsForPDF(String latitude, String longitude, String date){
        BDData admin = new BDData(context, "BDData", null, 1);
        Persons = admin.SearchPersonsCoordinatesAndDate (context, latitude, longitude, date);
        Archivos mngFile = new Archivos(context);
        HashMap<String, String> mapeo = mngFile.getMapCategoriesPersonas();

        for (PersonClass p : Persons){
            ArrayList<String> pData =  new ArrayList<>();
            Object[] cab = p.Data.keySet().toArray();
            for (Object c : cab){
                if (!c.toString().equals("QR") && !c.toString().equals("LATITUD") && !c.toString().equals("LONGITUD")
                    && !c.toString().equals("FECHA")) {
                    if (p.Data.get(c) != null && p.Data.get(c).length() != 0) {
                        if (c.equals("EFECTOR")){
                            SQLitePpal sqLitePpal = new SQLitePpal(context, "DATA_PRINCIPAL", null, 1);
                            pData.add(c.toString() + ": " + sqLitePpal.NombreEfector4Code(p.Data.get(c.toString())));
                        }
                        else{
                            if (mapeo.get(p.Data.get(c)) != null && mapeo.get(p.Data.get(c)).length() != 0
                                    && !p.Data.get(c).equals("SI") && !p.Data.get(c).equals("NO")) {
                                pData.add(mapeo.get(c) + ": " + mapeo.get(p.Data.get(c.toString())));
                            }
                            else if (mapeo.get(c)!=null){
                                pData.add(mapeo.get(c) + ": " + p.Data.get(c));
                            }
                            else {
                                pData.add(c + ": " + p.Data.get(c));
                            }
                        }
                    }
                }
            }
            dataPersons.add(pData);
        }
    }
    public void createPDF(){
        String titulo = "INFORME DE VISITA DOMICILIARIA";

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
        // Insercion imagen cabecera relevAr -------------------------------------------------------
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cabecera_pdf);
        bitmapEscala = Bitmap.createScaledBitmap(bitmap, 1230,72,false);
        canvas.drawBitmap(bitmapEscala, 5, 5, paint);

        // Insercion titulo ------------------------------------------------------------------------
        canvas.drawText(titulo, 400, 125, textTitulo);

        // Fecha de registro -----------------------------------------------------------------------
        canvas.drawText("Fecha de registro: "+family.Data.get("FECHA"), 77, 185, textGeneral);
        // Fecha de registro -----------------------------------------------------------------------
        canvas.drawText("Responsable del registro: "+family.Data.get("ENCUESTADOR"), 620, 185, textGeneral);

        // DATOS GENERALES -------------------------------------------------------------------------
        // Recuadro --------------------------------------------------------------------------------
        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(2);
        int h = 220;
        canvas.drawRect(5, h, 1235, h+(altoTexTitulo+6*altoTexNormal), paint1);

        // Datos generales valores -----------------------------------------------------------------
        h = 245;
        canvas.drawText("DATOS GENERALES DE LA UNIDAD DOMICILIARIA", 10, h, textTitulo);
        // Calle -----------------------------------------------------------------------------------
        h = h+altoTexTitulo+5;
        if (family.Data.containsKey("CALLE") && family.Data.get("CALLE")!=null) {
            canvas.drawText("Calle: " + family.Data.get("CALLE"), 10, h, textGeneral);
        }else {
            canvas.drawText("Calle: " + "S/C", 10, h, textGeneral);
        }
        // Numero ----------------------------------------------------------------------------------
        if (family.Data.containsKey("NÚMERO") && family.Data.get("NÚMERO")!=null) {
            canvas.drawText("Numero: " + family.Data.get("NÚMERO"), 775, h, textGeneral);
        }else {
            canvas.drawText("Numero: " + "S/N", 775, h, textGeneral);
        }
        // Calle -----------------------------------------------------------------------------------
        h = h+altoTexNormal+5;
        canvas.drawText("Coordenadas: "+family.Data.get("LATITUD")+", "+family.Data.get("LONGITUD"), 10, h, textGeneral);
        // Calle -----------------------------------------------------------------------------------
        h = h+altoTexNormal+5;
        if (family.Data.containsKey("TELEFONO_FAMILIAR") && family.Data.get("TELEFONO_FAMILIAR")!=null) {
            canvas.drawText("Telefono familiar: " + family.Data.get("TELEFONO_FAMILIAR"), 10, h, textGeneral);
        }else {
            canvas.drawText("Telefono familiar: " + "No registra", 10, h, textGeneral);
        }
        // Observaciones familiares ----------------------------------------------------------------
        h = h+altoTexNormal+5;
        if (family.Data.containsKey("OBSERVACIONES_VIVIENDA") && family.Data.get("OBSERVACIONES_VIVIENDA")!=null) {
            canvas.drawText("Observaciones: " + family.Data.get("OBSERVACIONES_VIVIENDA"), 10, h, textGeneral);
        }else {
            canvas.drawText("Observaciones: " + "No registro observaciones", 10, h, textGeneral);
        }

        // DOMICILIO -------------------------------------------------------------------------------
        // Recuadro --------------------------------------------------------------------------------
        ArrayList<String> btnDomicilio = new Archivos(context).getDomicilioBotons();
        h = 220 + (altoTexTitulo + 6 * altoTexNormal);
        for (String btn : btnDomicilio) {
            Paint recuadro_vivienda = new Paint();
            recuadro_vivienda.setColor(Color.BLACK);
            recuadro_vivienda.setStyle(Paint.Style.STROKE);
            recuadro_vivienda.setStrokeWidth(2);
            h += altoTexNormal;
            canvas.drawRect(5, h, 1235, h + (2 * altoTexTitulo + 5 + (dataDomicilio.get(btn).size() * (altoTexNormal+5))), paint1);

            // Datos valores -----------------------------------------------------------------------
            h += altoTexNormal + 5;
            canvas.drawText("DATOS " + btn, 10, h, textTitulo);
            // Valores -----------------------------------------------------------------------------
            if(Objects.requireNonNull(dataDomicilio.get(btn)).size()!=0) {
                for (Object data : Objects.requireNonNull(dataDomicilio.get(btn))) {
                    h = h + altoTexNormal + 5;
                    canvas.drawText(data.toString(), 10, h, textGeneral);
                }
            }else{
                h = h + altoTexNormal + 5;
                canvas.drawText("NO SE REGISTRAN DATOS EN ESTE MODULO", 10, h, textGeneral);
            }
            h+=2*altoTexTitulo;
        }

        pdfDocument.finishPage(page1);

        // Pagina 2 --------------------------------------------------------------------------------
        PdfDocument.Page page2 = pdfDocument.startPage(pageInfo);
        canvas = page2.getCanvas();

        //------------------------------------------------------------------------------------------
        // Insercion imagen cabecera relevAr -------------------------------------------------------
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cabecera_pdf);
        bitmapEscala = Bitmap.createScaledBitmap(bitmap, 1230,72,false);
        canvas.drawBitmap(bitmapEscala, 5, 5, paint);

        // Insercion titulo ------------------------------------------------------------------------
        titulo = "DATOS DE LAS PERSONAS REGISTRADAS";
        canvas.drawText(titulo, 400, 125, textTitulo);

        // Fecha de registro -----------------------------------------------------------------------
        canvas.drawText("Fecha de registro: "+family.Data.get("FECHA"), 77, 185, textGeneral);
        // Fecha de registro -----------------------------------------------------------------------
        canvas.drawText("Responsable del registro: "+family.Data.get("ENCUESTADOR"), 620, 185, textGeneral);

        h=220;
        for (int i=0; i<dataPersons.size();i+=2){
            int h_aux = 0;

            Paint recuadro_p1 = new Paint();
            recuadro_p1.setColor(Color.BLACK);
            recuadro_p1.setStyle(Paint.Style.STROKE);
            recuadro_p1.setStrokeWidth(2);
            canvas.drawRect(5, h, 600, h + (altoTexNormal + (dataPersons.get(i).size() * (altoTexNormal+5))), recuadro_p1);

            for (Object value : dataPersons.get(i)){
                h = h + altoTexNormal + 5;
                h_aux += altoTexNormal + 5;
                canvas.drawText(value.toString(), 10, h, textGeneral);
            }
            h = h - h_aux;
            int h_aux1 = 0;
            if ((i+1)<dataPersons.size()) {
                Paint recuadro_p2 = new Paint();
                recuadro_p2.setColor(Color.BLACK);
                recuadro_p2.setStyle(Paint.Style.STROKE);
                recuadro_p2.setStrokeWidth(2);
                canvas.drawRect(615, h, 1230, h + (altoTexNormal + (dataPersons.get(i+1).size() * (altoTexNormal+5))), recuadro_p2);

                for (Object value : dataPersons.get(i + 1)) {
                    if (value.toString().length()<55) {
                        h = h + altoTexNormal + 5;
                        h_aux1 += altoTexNormal + 5;
                        canvas.drawText(value.toString(), 620, h, textGeneral);
                    }else{
                        h = h + altoTexNormal + 5;
                        h_aux1 += altoTexNormal + 5;
                        canvas.drawText(value.toString().substring(0,50), 620, h, textGeneral);
                        h = h + altoTexNormal + 5;
                        h_aux1 += altoTexNormal + 5;
                        canvas.drawText(value.toString().substring(50,value.toString().length()), 620, h, textGeneral);
                    }
                }
            }

            if (h_aux>h_aux1){
                h = h - h_aux1 + h_aux;
            }

            h+=altoTexTitulo;
        }

        pdfDocument.finishPage(page2);

        File file = new File(context.getExternalFilesDir(null).toString());
        String nombreArchivo = "PDFRelevar.pdf";
        File dir = new File(file, nombreArchivo);
        try{
            pdfDocument.writeTo(new FileOutputStream(dir));
        }catch (Exception e){
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    public Intent sharePDF(){

        createPDF();
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
