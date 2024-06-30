package acompanar.ManagementModule.StorageManagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BDUbicationManager extends SQLiteOpenHelper {
    final String CREATE_UBICATIONS_TABLE = "CREATE TABLE UBICATIONS (DATE TEXT, TIME TEXT, LATITUD TEXT, LONGITUD TEXT, USER TEXT)";

    public BDUbicationManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_UBICATIONS_TABLE);
        createUbicationTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS FAMILIES");
    }

    public void createUbicationTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS UBICATIONS (DATE TEXT, TIME TEXT, LATITUD TEXT, LONGITUD TEXT, USER TEXT)");
    }

    //Insertar una ubicacion en la base de datos
    public String insertUbicationBD(String date, String time, String latitude, String longitude, String user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("DATE", date);
        valores.put("TIME", time);
        valores.put("LATITUD", latitude);
        valores.put("LONGITUD", longitude);
        valores.put("USER", user);

        db.insert("UBICATIONS", null, valores);
        db.close();

        return date;
    }

    public String readUbications4Date(String date) throws JSONException {
        JSONArray data = new JSONArray();

        //Busco los datos por la fecha
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM UBICATIONS WHERE DATE='"+date+"'";
        Cursor registros = db.rawQuery(search, null);

        //Los paso a formato JSON
        registros.moveToFirst();
        for (int i = 0; i < registros.getCount(); i++) {
            JSONObject ubication = new JSONObject();
            ubication.put("DATE", registros.getString(0));
            ubication.put("TIME", registros.getString(1));
            ubication.put("LATITUD", registros.getString(2));
            ubication.put("LONGITUD", registros.getString(3));
            ubication.put("USER", registros.getString(4));
            data.put(ubication);
            registros.moveToNext();
        }
        db.close();

        return data.toString();
    }

    public ArrayList<String> datesUbications(){
        ArrayList<String> dates = new ArrayList<>();

        //Busco los datos y elimino los repetidos
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT DISTINCT DATE FROM UBICATIONS";
        Cursor registros = db.rawQuery(search, null);

        //
        registros.moveToFirst();
        for (int i = 0; i < registros.getCount(); i++) {
            dates.add(registros.getString(0));
            registros.moveToNext();
        }
        db.close();

        return dates;
    }
}
