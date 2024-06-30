package acompanar.ManagementModule.StorageManagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import acompanar.BasicObjets.PersonClass;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLitePpal extends SQLiteOpenHelper {
    final String CREAR_TABLA_EFECTORES = "CREATE TABLE EFECTORES (NOMBRE TEXT, PROVINCIA TEXT,NOMBRE_ID TEXT, LOCALIDAD TEXT, DEPARTAMENTO TEXT, CP TEXT)";
    final String CREAR_TABLA_TRABAJOS = "CREATE TABLE TRABAJOS (TRABAJO TEXT)";

    final String CREAR_TABLA_PUNTOS_REFERENCIA = "CREATE TABLE PUNTOS_REFERENCIA (LATITUD TEXT, LONGITUD TEXT, NOMBRE TEXT, TIPO TEXT, DESCRIPCION TEXT)";
    final String CREAR_TABLA_UNIFICADOS = "CREATE TABLE UNIFICADOS (FECHA TEXT)";
    final String CREAR_TABLA_BOTONES = "CREATE TABLE BOTONES (BOTON TEXT, ACTIVO BOOLEAN)";
    final String CREAR_TABLA_ENCUESTADORES = "CREATE TABLE ENCUESTADOR (ID TEXT,APELLIDO TEXT,PROVINCIA TEXT,DNI TEXT, ACTIVO BOOLEAN)";
    final String CREAR_TABLA_NOTIFICACIONES = "CREATE TABLE NOTIFICACIONES (NOTIFICACION TEXT, ACTIVO BOOLEAN)";
    final String CREAR_TABLA_MESSAGE_NOTIFICATIONS = "CREATE TABLE MESSAGE_NOTIFICATIONS (LATITUD TEXT," +
            "LONGITUD TEXT, NAME TEXT, SURNAME TEXT, DNI TEXT, TYPE_MESSAGE TEXT, STATE BOOLEAN, DATE TEXT)";
    final String CREAR_SERVERS = "CREATE TABLE SERVERS (URL TEXT, NAME TEXT)";
    final ArrayList<String> datos = new ArrayList<String>();

    public SQLitePpal(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_EFECTORES);
        db.execSQL(CREAR_TABLA_TRABAJOS);
        db.execSQL(CREAR_TABLA_ENCUESTADORES);
        db.execSQL(CREAR_TABLA_BOTONES);
        db.execSQL(CREAR_TABLA_UNIFICADOS);
        db.execSQL(CREAR_TABLA_NOTIFICACIONES);
        db.execSQL(CREAR_TABLA_MESSAGE_NOTIFICATIONS);
        db.execSQL(CREAR_TABLA_PUNTOS_REFERENCIA);
        db.execSQL(CREAR_SERVERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Verifica si existe una version mas antigua de la base de datos
        db.execSQL("DROP TABLE IF EXISTS EFECTORES");
        db.execSQL("DROP TABLE IF EXISTS TRABAJOS");
        db.execSQL("DROP TABLE IF EXISTS ENCUESTADOR");
        db.execSQL("DROP TABLE IF EXISTS BOTONES");
        db.execSQL("DROP TABLE IF EXISTS UNIFICADOS");
        db.execSQL("DROP TABLE IF EXISTS NOTIFICACIONES");
        db.execSQL("DROP TABLE IF EXISTS MESSAGE_NOTIFICATIONS");
        db.execSQL("DROP TABLE IF EXISTS SERVERS");
        db.execSQL("DROP TABLE IF EXISTS PUNTOS_REFERENCIA");
        onCreate(db);
    }

    public void CrearTablaUnificados(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS UNIFICADOS (FECHA TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS NOTIFICACIONES (NOTIFICACION TEXT, ACTIVO BOOLEAN)");
        db.execSQL("CREATE TABLE IF NOT EXISTS MESSAGE_NOTIFICATIONS (LATITUD TEXT," +
                "LONGITUD TEXT, NAME TEXT, SURNAME TEXT, DNI TEXT, TYPE_MESSAGE TEXT, STATE BOOLEAN, DATE TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS SERVERS (URL TEXT, NAME TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS PUNTOS_REFERENCIA (LATITUD TEXT, LONGITUD TEXT, NOMBRE TEXT, TIPO TEXT, DESCRIPCION TEXT)");
        try {
            db.execSQL("ALTER TABLE ENCUESTADOR ADD COLUMN EFECTOR TEXT");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        db.close();
    }

    public int CabeceraEfectores(){
        SQLiteDatabase db = this.getReadableDatabase();
        String aux = "SELECT * FROM EFECTORES";
        Cursor registros = db.rawQuery(aux, null);

        return registros.getColumnCount();
    }

    public void AddColumnEfectores(){
        SQLiteDatabase db = this.getWritableDatabase();
        String addColumn = "ALTER TABLE EFECTORES ADD COLUMN NOMBRE_ID TEXT";
        db.execSQL(addColumn);
        addColumn = "ALTER TABLE EFECTORES ADD COLUMN LOCALIDAD TEXT";
        db.execSQL(addColumn);
        addColumn = "ALTER TABLE EFECTORES ADD COLUMN DEPARTAMENTO TEXT";
        db.execSQL(addColumn);
        addColumn = "ALTER TABLE EFECTORES ADD COLUMN CP TEXT";
        db.execSQL(addColumn);
        db.close();
    }

    public ArrayList<String> Buscar_Registros(String Nombre){
        datos.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String aux = "SELECT DISTINCT NOMBRE FROM EFECTORES WHERE NOMBRE LIKE '%"+Nombre+"%'";
        //String aux1 = "SELECT * FROM EFECTORES";
        Cursor registros = db.rawQuery(aux, null);

        if(registros.moveToFirst()){
            String auxData = "";
            auxData = registros.getString(0);
            datos.add(auxData);

        while (registros.moveToNext()){
            String auxData1 = "";
            auxData1 = registros.getString(0);
            datos.add(auxData1);}
        }

        else{
            String auxData1 = "";
            datos.add(auxData1);
        }
        db.close();
        registros.close();
        return datos;
    }

    public String CodeEfector(String Nombre){
        datos.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String aux = "SELECT DISTINCT NOMBRE_ID FROM EFECTORES WHERE NOMBRE='"+Nombre+"'";
        //String aux1 = "SELECT * FROM EFECTORES";
        Cursor registros = db.rawQuery(aux, null);
        registros.moveToFirst();

        if (registros.getCount()!=0) {
            return registros.getString(0);
        }else{
            return Nombre;
        }
    }

    public String NombreEfector4Code(String code){
        datos.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String aux = "SELECT DISTINCT NOMBRE FROM EFECTORES WHERE NOMBRE_ID='"+code+"'";
        //String aux1 = "SELECT * FROM EFECTORES";
        Cursor registros = db.rawQuery(aux, null);
        registros.moveToFirst();

        if (registros.getCount()!=0) {
            return registros.getString(0);
        }else{
            return code;
        }
    }

    public boolean ExisteTrabajos(){
        SQLiteDatabase db = this.getReadableDatabase();

        //String aux1 = "SELECT name FROM sqlite_master WHERE name='TRABAJOS'";
        String aux2 = "SELECT * FROM TRABAJOS";

        Cursor registros = db.rawQuery(aux2, null);

        if(registros.getCount()==0){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean ExisteEfectores(String Provincia){
        SQLiteDatabase db = this.getReadableDatabase();

        //String aux1 = "SELECT name FROM sqlite_master WHERE name='TRABAJOS'";
        String aux2 = "SELECT * FROM EFECTORES WHERE PROVINCIA='"+Provincia+"'";

        Cursor registros = db.rawQuery(aux2, null);

        if(registros.getCount()==0){
            return false;
        }
        else{
            return true;
        }
    }

    public ArrayList<String> ProvinciasEfectores(){
        ArrayList<String> value = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        //String aux1 = "SELECT name FROM sqlite_master WHERE name='TRABAJOS'";
        String aux2 = "SELECT DISTINCT PROVINCIA FROM ENCUESTADOR";

        Cursor registros = db.rawQuery(aux2, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            value.add(registros.getString(0));
            registros.moveToNext();
        }
        return value;
    }

    public void DeleteAllEfectores(){
        ArrayList<String> value = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String aux2 = "DELETE FROM EFECTORES";

        //Cursor registros = db.rawQuery(aux2, null);
        db.delete("EFECTORES", null, null);

    }

    public boolean ExisteEfectorEnBD(String Nombre, String Provincia){
        SQLiteDatabase db = this.getReadableDatabase();

        //String aux1 = "SELECT name FROM sqlite_master WHERE name='TRABAJOS'";
        String aux2 = "SELECT * FROM EFECTORES WHERE NOMBRE='"+Nombre+"' AND PROVINCIA='"+Provincia+"'";

        Cursor registros = db.rawQuery(aux2, null);

        if(registros.getCount()==0){
            return false;
        }
        else{
            return true;
        }
    }

    public int CantEfectores(String Provincia){
        SQLiteDatabase db = this.getReadableDatabase();

        String aux2 = "SELECT * FROM EFECTORES WHERE PROVINCIA='"+Provincia+"'";

        Cursor registros = db.rawQuery(aux2, null);

        if(registros.getCount()==0){
            return 0;
        }
        else{
            return registros.getCount();
        }
    }

    public ArrayList<String> BuscarTrabajo(String Trabajo){
        datos.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        String aux = "SELECT DISTINCT TRABAJO FROM TRABAJOS WHERE TRABAJO LIKE '%"+Trabajo+"%'";
        String aux1 = "SELECT * FROM TRABAJOS";
        Cursor registros = db.rawQuery(aux, null);

        if(registros.moveToFirst()){
            String auxData = "";
            auxData = registros.getString(0);
            datos.add(auxData);

            while (registros.moveToNext()){
                String auxData1 = "";
                auxData1 = registros.getString(0);
                datos.add(auxData1);}
        }

        else{
            String auxData1 = "";
            datos.add(auxData1);
        }
        db.close();
        registros.close();
        return datos;
    }

    public void CrearUsuario(String Nombre, String Apellido, String DNI, String Provincia){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("ID", Nombre);
        registro.put("APELLIDO", Apellido);
        registro.put("PROVINCIA", Provincia);
        registro.put("DNI", DNI);
        registro.put("ACTIVO", true);
        db.insert("ENCUESTADOR", null, registro);
        db.close();
    }

    public ArrayList<String> Encuestadores(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> encuestadores = new ArrayList<>();
        String consulta ="SELECT * FROM ENCUESTADOR";
        Cursor a = db.rawQuery(consulta, null);
        while (a.moveToNext()){
            encuestadores.add(a.getString(0)+","+a.getString(1));
        }
        db.close();
        return encuestadores;
    }

    public ArrayList<String> AllEncuestadores(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> encuestadores = new ArrayList<>();
        String consulta ="SELECT * FROM ENCUESTADOR";
        Cursor a = db.rawQuery(consulta, null);
        while (a.moveToNext()){
            encuestadores.add(a.getString(0)+","+a.getString(1)+","+a.getString(3));
        }
        db.close();
        return encuestadores;
    }

    public boolean existeEncuestador(String Nombre, String Apellido){
        SQLiteDatabase db = this.getReadableDatabase();

        String aux2 = "SELECT * FROM ENCUESTADOR WHERE ID='"+Nombre+"' AND APELLIDO='"+Apellido+"'";

        Cursor registros = db.rawQuery(aux2, null);

        if(registros.getCount()==0){
            return false;
        }
        else{
            return true;
        }
    }

    public void desactivarUsuarios(){
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String cantidad = "SELECT DISTINCT * FROM ENCUESTADOR";
        Cursor cant = dbRead.rawQuery(cantidad, null);
        if(cant.getCount()!=0){
        SQLiteDatabase db = this.getWritableDatabase();
            //Establecemos los campos-valores a actualizar
            ContentValues valores = new ContentValues();
            valores.put("ACTIVO",Boolean.FALSE);

            //Actualizamos el registro en la base de datos
            db.update("ENCUESTADOR", valores, null, null);
        db.close();}
        dbRead.close();
    }

    public void activarUsuario(String Nombre, String Apellido){
        SQLiteDatabase db = this.getWritableDatabase();
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put("ACTIVO",Boolean.TRUE);
        //Actualizamos el registro en la base de datos
        String[] args = new String[]{Nombre, Apellido};
        db.update("ENCUESTADOR", valores, "ID=? AND APELLIDO=?" , args);
        db.close();
    }

    public String ObtenerActivado(){
        String encuestador="";
        SQLiteDatabase db = this.getReadableDatabase();
        String consulta ="SELECT ID, APELLIDO FROM ENCUESTADOR WHERE ACTIVO=1";
        Cursor a = db.rawQuery(consulta, null);
        a.moveToFirst();
        if(a.getCount()!=0){
        encuestador = a.getString(0)+" "+a.getString(1);}//+" "+a.getString(1);}
        return encuestador;
    }

    public HashMap<String, String> encuestadorActivado(){
        HashMap<String, String> encuestador = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String consulta ="SELECT ID, APELLIDO, PROVINCIA, DNI FROM ENCUESTADOR WHERE ACTIVO=1";
        Cursor a = db.rawQuery(consulta, null);
        a.moveToFirst();
        if(a.getCount()!=0) {
            encuestador.put("NOMBRE", a.getString(0));
            encuestador.put("APELLIDO", a.getString(1));
            encuestador.put("PROVINCIA", a.getString(2));
            encuestador.put("DNI", a.getString(3));
        }
        return encuestador;
    }

    public HashMap<String, String> searchEncuestadorWithDNI(String dni){
        HashMap<String, String> encuestador = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String consulta ="SELECT ID, APELLIDO, PROVINCIA, DNI FROM ENCUESTADOR WHERE DNI='"+dni+"'";
        Cursor a = db.rawQuery(consulta, null);
        a.moveToFirst();
        if(a.getCount()!=0) {
            encuestador.put("NOMBRE", a.getString(0));
            encuestador.put("APELLIDO", a.getString(1));
            encuestador.put("PROVINCIA", a.getString(2));
            encuestador.put("DNI", a.getString(3));
        }
        return encuestador;
    }

    public String ObtenerDniActivado(){
        String encuestador="";
        SQLiteDatabase db = this.getReadableDatabase();
        String consulta ="SELECT DNI FROM ENCUESTADOR WHERE ACTIVO=1";
        Cursor a = db.rawQuery(consulta, null);
        a.moveToFirst();
        encuestador = a.getString(0);
        return encuestador;
    }

    public void DesactivarBotones(){
        SQLiteDatabase dbRead = this.getReadableDatabase();
        String cantidad = "SELECT DISTINCT * FROM BOTONES";

        Cursor cant = dbRead.rawQuery(cantidad, null);
        if(cant.getCount()!=0){
            SQLiteDatabase db = this.getWritableDatabase();
            //Establecemos los campos-valores a actualizar
            ContentValues valores = new ContentValues();
            valores.put("ACTIVO",Boolean.FALSE);

//Actualizamos el registro en la base de datos
            db.update("BOTONES", valores, null, null);
            db.close();}
        dbRead.close();

    }

    public void ActivarBoton(String nombreBoton){
        SQLiteDatabase db = this.getWritableDatabase();
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put("ACTIVO",Boolean.TRUE);

        //Actualizamos el registro en la base de datos
        String[] args = new String[]{nombreBoton};
        db.update("BOTONES", valores, "BOTON=?" , args);
        db.close();
    }

    public void DesactivarBoton(String nombreBoton){
        SQLiteDatabase db = this.getWritableDatabase();
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put("ACTIVO",Boolean.FALSE);

        //Actualizamos el registro en la base de datos
        String[] args = new String[]{nombreBoton};
        db.update("BOTONES", valores, "BOTON=?" , args);
        db.close();
    }

    public Boolean EstadoBoton(String nombreBoton){
        SQLiteDatabase db = this.getReadableDatabase();

        //String aux1 = "SELECT name FROM sqlite_master WHERE name='TRABAJOS'";
        String aux2 = "SELECT ACTIVO FROM BOTONES WHERE BOTON='"+nombreBoton+"'";

        Cursor registros = db.rawQuery(aux2, null);
        registros.moveToFirst();
        if (registros.isNull(0) || registros.getShort(0) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<String> Botones(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> botones = new ArrayList<>();
        String consulta ="SELECT * FROM BOTONES";
        Cursor a = db.rawQuery(consulta, null);
        while (a.moveToNext()){
            botones.add(a.getString(0));
        }
        db.close();
        return botones;
    }

    public boolean ExisteFechaUnificados(String Fecha){
        SQLiteDatabase db = this.getReadableDatabase();

        //String aux1 = "SELECT name FROM sqlite_master WHERE name='TRABAJOS'";
        String aux2 = "SELECT * FROM UNIFICADOS WHERE FECHA='"+Fecha+"'";

        Cursor registros = db.rawQuery(aux2, null);

        if(registros.getCount()==0){
            return false;
        }
        else{
            return true;
        }
    }

    public void ActivarNotificacionDesactivar(String nombreNotificacion, Boolean state){
        SQLiteDatabase db = this.getWritableDatabase();
        //Establecemos los campos-valores a actualizar
        ContentValues valores = new ContentValues();
        valores.put("ACTIVO",state);

        //Actualizamos el registro en la base de datos
        String[] args = new String[]{nombreNotificacion};
        db.update("NOTIFICACIONES", valores, "NOTIFICACION=?" , args);
        db.close();
    }

    public void InsertNotification(String name, Boolean state){
        if (!ExistsNotification(name.toUpperCase())) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("NOTIFICACION", name);
            valores.put("ACTIVO", Boolean.TRUE);
            //NOTIFICACION TEXT, ACTIVO BOOLEAN
            db.insert("NOTIFICACIONES", null, valores);
            db.close();
        }
    }

    private boolean ExistsNotification(String name){
        boolean values = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM NOTIFICACIONES WHERE NOTIFICACION='"+name+"'";
        Cursor registros = db.rawQuery(search, null);

        if(registros.getCount()!=0){
            values = true;
        }
        db.close();

        return values;
    }

    public int CantNewNotification(){
        int value = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM MESSAGE_NOTIFICATIONS WHERE STATE=0";
        Cursor registros = db.rawQuery(search, null);

        if(registros.getCount()!=0){
            value = registros.getCount();
        }
        db.close();

        return value;
    }

    public int CantNotification(){
        int value = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM MESSAGE_NOTIFICATIONS WHERE STATE=1";
        Cursor registros = db.rawQuery(search, null);

        value = registros.getCount();
        db.close();

        return value;
    }

    public long CreateMessage(PersonClass person, String type_msg, boolean state){
        long value = 0;
        person.LoadDataHashToParamaters();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("LATITUD", person.Latitud);
        valores.put("LONGITUD", person.Longitud);
        valores.put("NAME", person.Nombre);
        valores.put("SURNAME", person.Apellido);
        valores.put("DNI", person.DNI);
        valores.put("DATE", person.Fecha);
        valores.put("TYPE_MESSAGE", type_msg);
        valores.put("STATE", Boolean.TRUE);
        value = db.insert("MESSAGE_NOTIFICATIONS", null, valores);

        db.close();

        return value;
    }

    public ArrayList<HashMap<String,String>> MessagesNew(){
        ArrayList<HashMap<String,String>> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM MESSAGE_NOTIFICATIONS WHERE STATE=1";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            //LATITUD TEXT,LONGITUD TEXT, NAME TEXT, SURNAME TEXT, DNI TEXT, TYPE_MESSAGE TEXT, STATE BOOLEAN
            HashMap<String,String> aux = new HashMap<>();
            aux.put("LATITUD", registros.getString(0));
            aux.put("LONGITUD", registros.getString(1));
            aux.put("NAME", registros.getString(2));
            aux.put("SURNAME", registros.getString(3));
            aux.put("DNI", registros.getString(4));
            aux.put("TYPE_MESSAGE", registros.getString(5));
            aux.put("DATE", registros.getString(7));
            values.add(aux);
            registros.moveToNext();
        }

        db.close();

        return values;
    }

    public ArrayList<HashMap<String,String>> MessagesDone(){
        ArrayList<HashMap<String,String>> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM MESSAGE_NOTIFICATIONS WHERE STATE=0";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            //LATITUD TEXT,LONGITUD TEXT, NAME TEXT, SURNAME TEXT, DNI TEXT, TYPE_MESSAGE TEXT, STATE BOOLEAN
            HashMap<String,String> aux = new HashMap<>();
            aux.put("LATITUD", registros.getString(0));
            aux.put("LONGITUD", registros.getString(1));
            aux.put("NAME", registros.getString(2));
            aux.put("SURNAME", registros.getString(3));
            aux.put("DNI", registros.getString(4));
            aux.put("TYPE_MESSAGE", registros.getString(5));
            aux.put("DATE", registros.getString(7));
            values.add(aux);
            registros.moveToNext();
        }

        db.close();

        return values;
    }

    public ArrayList<HashMap<String,String>> Latitudes(){
        ArrayList<HashMap<String,String>> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT LATITUD, DATE FROM MESSAGE_NOTIFICATIONS";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            //LATITUD TEXT,LONGITUD TEXT, NAME TEXT, SURNAME TEXT, DNI TEXT, TYPE_MESSAGE TEXT, STATE BOOLEAN
            HashMap<String,String> aux = new HashMap<>();
            aux.put("LATITUD", registros.getString(0));
            aux.put("DATE", registros.getString(1));
            values.add(aux);
            registros.moveToNext();
        }

        db.close();

        return values;
    }

    public long NotificationDone(String latitud, String longitud, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("STATE", Boolean.FALSE);

        String[] args = new String[]{latitud, date};
        //"LATITUD=? AND LONGITUD=? AND DATE=?"
        long x = db.update("MESSAGE_NOTIFICATIONS", valores, "LATITUD=? AND DATE=?" , args);
        db.close();
        return x;
    }

//--------------------------------------------------------------------------------------------------
// SERVERS
    public void InsertServer(String URL, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("URL", URL);
        valores.put("NAME", name);
        //NOTIFICACION TEXT, ACTIVO BOOLEAN
        db.insert("SERVERS", null, valores);
        db.close();
    }

    public ArrayList<HashMap<String,String>> GetServers(){
        ArrayList<HashMap<String,String>> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM SERVERS";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            //LATITUD TEXT,LONGITUD TEXT, NAME TEXT, SURNAME TEXT, DNI TEXT, TYPE_MESSAGE TEXT, STATE BOOLEAN
            HashMap<String,String> aux = new HashMap<>();
            aux.put("URL", registros.getString(0));
            aux.put("NAME", registros.getString(1));
            values.add(aux);
            registros.moveToNext();
        }

        db.close();

        return values;
    }

    public void UpdateEncuestador(String dni, String efector){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("EFECTOR", efector);

        String[] args = new String[]{dni};
        int x = db.update("ENCUESTADOR", valores, "DNI=?", args);
        db.close();
    }

    public String EfectorEncuestador(String dni){
        String efector = "";

        ArrayList<HashMap<String,String>> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT EFECTOR FROM ENCUESTADOR WHERE DNI="+dni;
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        efector = registros.getString(0);

        db.close();

        return efector;
    }

    public void InsertReferencePoint(String latitud, String longitud, String nombre, String tipo, String descripcion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("LATITUD", latitud);
        valores.put("LONGITUD", longitud);
        valores.put("NOMBRE", nombre);
        valores.put("TIPO", tipo);
        valores.put("DESCRIPCION", descripcion);

        db.insert("PUNTOS_REFERENCIA", null, valores);
        db.close();
    }

    public ArrayList<LatLng> SearchAllCordinatesReferencePoints(){
        ArrayList<LatLng> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT LATITUD, LONGITUD FROM PUNTOS_REFERENCIA";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            values.add(new LatLng(Double.parseDouble(registros.getString(0)), Double.parseDouble(registros.getString(1))));
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public Boolean IsReferencePoint(String latitud, String longitud){
        Boolean value = Boolean.FALSE;
        String consulta = "SELECT NOMBRE FROM PUNTOS_REFERENCIA WHERE LATITUD='"+latitud+"' AND LONGITUD='"+longitud+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor registros = db.rawQuery(consulta, null);
        Log.e("puntos ref", latitud + longitud +" numbre");

        if (registros.getCount()!=0){
            value = Boolean.TRUE;

        }
        return value;
    }

    public String getNameReferencePoint(String latitud, String longitud){
        String value = "";
        String consulta = "SELECT NOMBRE FROM PUNTOS_REFERENCIA WHERE LATITUD='"+latitud+"' AND LONGITUD='"+longitud+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor registros = db.rawQuery(consulta, null);

        registros.moveToFirst();
        if (registros.getCount()!=0){
            value = registros.getString(0);
        }

        return value;
    }
}
