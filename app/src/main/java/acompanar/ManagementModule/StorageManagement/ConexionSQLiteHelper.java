package acompanar.ManagementModule.StorageManagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {
    final String CREAR_TABLA_ENCUESTADORES = "CREATE TABLE ENCUESTADOR (ID TEXT)";

    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Genera las tablas
        db.execSQL(CREAR_TABLA_ENCUESTADORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionantigua, int versionueva) {
        // Verifica si existe una version mas antigua de la base de datos
        db.execSQL("DROP TABLE IF EXISTS ENCUESTADOR");
        onCreate(db);
    }
}
