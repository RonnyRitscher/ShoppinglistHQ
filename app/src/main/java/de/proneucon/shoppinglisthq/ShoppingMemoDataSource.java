package de.proneucon.shoppinglisthq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
Diese Klasse ist unser Data Access Object und für das Verwalten der Daten verantwortlich.
Es unterhält die Datenbankverbindung und ist für das Hinzufügen, Auslesen und Löschen von Datensätzen zuständig.
Außerdem wandelt es Datensätze in Java-Objekte für uns um, so dass der Code unserer Benutzeroberfläche
nicht direkt mit den Datensätzen arbeiten muss.
 */
public class ShoppingMemoDataSource {

    //LOG-TAG
    private static final String TAG = ShoppingMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ShoppingMemoDbHelper dbHelper;

    //Für Abfragen des SQLite-Daten mit dem Cursor
    private String[] columns = {
            ShoppingMemoDbHelper.COLUMN_ID ,
            ShoppingMemoDbHelper.COLUMN_PRODUCT ,
            ShoppingMemoDbHelper.COLUMN_QUANTITY
    };



    //CONSTRUKTOR
    public ShoppingMemoDataSource(Context context) {
        Log.d(TAG, "ShoppingMemoDataSource: DataSource erzeugt jetzt den dbHelper");
        dbHelper = new ShoppingMemoDbHelper(context);
    }


    //METHODE zum erstellen eines ShoppingMemoEintrags
    public ShoppingMemo createShoppingMemo(String product , int quantity){
        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT , product);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY , quantity);

        //Beschaffen der ID des Eintrags
        long insertId = database.insert(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST , null , values);

            //Cursor
        Cursor cursor =
                database.query(
                        ShoppingMemoDbHelper.TABLE_SHOPPING_LIST ,                  // WELCHE LISTE
                        columns ,                                                   // STRING[]
                        ShoppingMemoDbHelper.COLUMN_ID + "=" + insertId ,   // WHERE
                        null, null , null , null);   // EINSTELLUNGEN

        cursor.moveToFirst(); //Setzt den cursor auf den Anfang
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();

        return shoppingMemo;
    }

    //-----------------------------------------------------------------
    // METHODE zum AUSLESEN der EINTRÄGE
    // gibt den Inhalt der Reihen aus, auf dem der cuser platziert wird
    private ShoppingMemo cursorToShoppingMemo(Cursor cursor) {
        // Besorge den Index
        int idIndex = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_QUANTITY);

        //Die dahinter liegenden Werte beschaffen:
        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);

        // ShoppingMemo-Objekt mit den Werten erzeugen und übergeben
        ShoppingMemo shoppingMemo = new ShoppingMemo(product, quantity, id);
        return  shoppingMemo;

    }

    //----------------------------------------------
    //METHODE um ALLE EINRÄGE darzustellen
    public List<ShoppingMemo> getAllShoppingMemos(){
        List<ShoppingMemo> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                columns, null, null, null, null, null);

        cursor.moveToFirst();

        ShoppingMemo shoppingMemo; //Hier kommt der Cuser anschließend rein

        while(!cursor.isAfterLast()){
            shoppingMemo = cursorToShoppingMemo(cursor);
            shoppingMemoList.add(shoppingMemo); //Hinzufügen der einzelnen  SM zur SML
            Log.d(TAG, "getAllShoppingMemos:  ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext(); //cursior auf das nachste Element
        }

        cursor.close();  //cursor schließen
        return shoppingMemoList; // Liste zurückgeben
    }





    //---------------------------------------------
    //METHODE zum ÖFFNEN der DB
    public void open(){
        Log.d(TAG, "open: Eine Referenz auf die Datenbank wird angefragt.");
        database = dbHelper.getWritableDatabase(); // erstellt die Connection zur Datenbank
        Log.d(TAG, "open: Referenz erhalten. Pfad zur DB: " + database.getPath());
    }
    //METHODE zum SCHLIESSEN der DB
    public void close(){
        dbHelper.close();
        Log.d(TAG, "close: Datenbank mit Hilfe des DbHelpers geschlossen.");
    }
}
