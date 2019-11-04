package com.stho.software.nyota.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NyotaDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "nyota.db";

    public NyotaDatabaseHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // see: https://dzone.com/articles/create-a-database-android-application-in-android-s
    // https://medium.com/@ssaurel/learn-to-save-data-with-sqlite-on-android-b11a8f7718d3
    // https://developer.android.com/training/data-storage/sqlite
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_STARS_TABLE = "CREATE TABLE Stars ("
                + "StarId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Name TEXT,"
                + "Code TEXT,"
                + "RightAscension DOUBLE,"
                + "Declination DOUBLE,"
                + "ApparentMagnitude DOUBLE,"
                + "Color INT"
                + ")";
        final String CREATE_CONSTELLATIONS_TABLE = "CREATE TABLE Constellations ("
                + "ConstellationId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "Name TEXT"
                + ")";
        final String CREATE_CONSTELLATION_STARS_TABLE = "CREATE TABLE ConstellationStars ("
                + "ConstellationStarsId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ConstellationId INTEGER,"
                + "StarId INTEGER"
                + ")";
        final String CREATE_CONSTELLATION_LINES_TABLE = "CREATE TABLE ConstellationLines ("
                + "ConstellationLineId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "LineNumber INTEGER,"
                + "ConstellationId INTEGER,"
                + "StarId INTEGER"
                + ")";
        final String CREATE_CONSTELLATION_NAMES_TABLE = "CREATE TABLE ConstellationNames ("
                + "ConstellationNameId INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "ConstellationId INTEGER,"
                + "Language TEXT,"
                + "Name TEXT"
                + ")";
        sqLiteDatabase.execSQL(CREATE_STARS_TABLE);
        sqLiteDatabase.execSQL(CREATE_CONSTELLATIONS_TABLE);
        sqLiteDatabase.execSQL(CREATE_CONSTELLATION_STARS_TABLE);
        sqLiteDatabase.execSQL(CREATE_CONSTELLATION_LINES_TABLE);
        sqLiteDatabase.execSQL(CREATE_CONSTELLATION_NAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String DROP_STARS_TABLE = "DROP TABLE IF EXISTS Stars";
        final String DROP_CONSTELLATIONS_TABLE = "DROP TABLE IF EXISTS Constellations";
        final String DROP_CONSTELLATION_STARS_TABLE = "DROP TABLE IF EXISTS ConstellationStars";
        final String DROP_CONSTELLATION_LINES_TABLE = "DROP TABLE IF EXISTS ConstellationLines";
        final String DROP_CONSTELLATION_NAMES_TABLE = "DROP TABLE IF EXISTS ConstellationNames";
        sqLiteDatabase.execSQL(DROP_STARS_TABLE);
        sqLiteDatabase.execSQL(DROP_CONSTELLATIONS_TABLE);
        sqLiteDatabase.execSQL(DROP_CONSTELLATION_STARS_TABLE);
        sqLiteDatabase.execSQL(DROP_CONSTELLATION_LINES_TABLE);
        sqLiteDatabase.execSQL(DROP_CONSTELLATION_NAMES_TABLE);
        onCreate(sqLiteDatabase);
    }

    private static final String STARS = "Stars";
    private static final String FIELD_STAR_ID = "StarId";
    private static final String FIELD_NAME = "Name";
    private static final String FIELD_CODE = "Code";
    private static final String FIELD_RIGHT_ASCENSION = "RightAscension";
    private static final String FIELD_DECLINATION = "Declination";
    private static final String FIELD_APPARENT_MAGNITUDE = "ApparentMagnitude";
    private static final String FIELD_COLOR = "Color";
    private static final String[] STAR_COLUMNS = {
            FIELD_STAR_ID,
            FIELD_NAME,
            FIELD_CODE,
            FIELD_RIGHT_ASCENSION,
            FIELD_DECLINATION,
            FIELD_APPARENT_MAGNITUDE,
            FIELD_COLOR
    };

 /*
    public void deleteOne(Star star) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STARS, FIELD_NAME + "=?", new String[] { String.valueOf(star.getName())});
        db.close();
    }

    public Star getPlayer(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(STARS,
                STAR_COLUMNS,
                FIELD_NAME + "=?",
                new String[] { name },
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Star star = new Star();
        Player player = new Player();
        player.setId(Integer.parseInt(cursor.getString(0)));
        player.setName(cursor.getString(1));
        player.setPosition(cursor.getString(2));
        player.setHeight(Integer.parseInt(cursor.getString(3)));

        return player;
    }

    public List<Player> allPlayers() {

        List<Player> players = new LinkedList<Player>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Player player = null;

        if (cursor.moveToFirst()) {
            do {
                player = new Player();
                player.setId(Integer.parseInt(cursor.getString(0)));
                player.setName(cursor.getString(1));
                player.setPosition(cursor.getString(2));
                player.setHeight(Integer.parseInt(cursor.getString(3)));
                players.add(player);
            } while (cursor.moveToNext());
        }

        return players;
    }

    public void addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, player.getName());
        values.put(KEY_POSITION, player.getPosition());
        values.put(KEY_HEIGHT, player.getHeight());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, player.getName());
        values.put(KEY_POSITION, player.getPosition());
        values.put(KEY_HEIGHT, player.getHeight());

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(player.getId()) });

        db.close();

        return i;
    }

    public int writeStar(String name, String shortName, double rightAscension, double declination, double magnitude) {
        this.
        final String WRITE_STAR = "INSERT INTO Stars (Name, Code, RightAscension,  Declination, ApparentMagnitude, ColorIndex DOUBLE) VALUES(,\"\n" +
                "                + \"ColorIndex INT\"(VALUES"
    }
*/
}
