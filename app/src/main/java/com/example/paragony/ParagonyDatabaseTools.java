package com.example.paragony;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

public class ParagonyDatabaseTools extends SQLiteOpenHelper {


    private Context context;
    private static final String DATABASE_NAME = "Paragony.db";
    private static final String TARGET_FOLDER = "/databases/";
    private static final String ID_COLUMN = "id";
    private static final String MAIN_TABLE_NAME = "recipts";
    private static final String TABLE_ITEMS_NAME = "items";
    private static final String TABLE_TAGS_NAME = "tags";
    private static final String TABLE_SETTINGS_NAME = "settings";
    private static final int DATABASE_VERSION = 1;

    public static final double TAX_A = 23;
    public static final double TAX_B = 8;
    public static final double TAX_C = 5;
    public static final double TAX_D = 0;
    public static final double TAX_E = 0;
    public static final double TAX_F = 0;
    public static final double TAX_G = 0;

    private static final String DATABASE_PATH = Environment.
            getExternalStoragePublicDirectory(Environment.
                    DIRECTORY_DOCUMENTS).toString() +
            "/databases/" + "Paragony.db";


    public ParagonyDatabaseTools(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println("CREATING DATABASE:");

        String createQuery = "CREATE TABLE IF NOT EXISTS " + MAIN_TABLE_NAME + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date DATE, " +
                "receipt_id TEXT, " +
                "shop TEXT, " +
                "address TEXT, " +
                "nip TEXT, " +
                "total REAL, " +
                "total_tax REAL, " +
                "taxA REAL, " +
                "taxB REAL, " +
                "taxC REAL, " +
                "taxD REAL, " +
                "taxE REAL, " +
                "taxF REAL, " +
                "taxG REAL, " +
                "currency TEXT " +
                ");";


        db.execSQL(createQuery);

        this.onCreateItems(db);
        this.onCreateTags(db);
        this.onCreateSettings(db);

    }

    public void dropDatabase(Context app) {


        System.out.println("DROPPING DATABASE:");
        app.deleteDatabase(DATABASE_NAME);


    }

    public List<Integer> getTaxValues() {

        SQLiteDatabase db = this.getReadableDatabase();

        List<Integer> taxValues = new ArrayList<>();

        Cursor cursor = null;

        String query = "SELECT * FROM " + this.TABLE_SETTINGS_NAME;


        if (db != null) {

            cursor = db.rawQuery(query, null);

            cursor.moveToNext();

            for (int i = 1; i < cursor.getColumnCount(); i++) {

                taxValues.add(cursor.getInt(i));

            }

        }

        return taxValues;
    }

    public int getRowHeight() {

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = null;

        String query = "SELECT * FROM " + this.TABLE_SETTINGS_NAME;


        cursor = db.rawQuery(query, null);

        cursor.moveToNext();


        return cursor.getInt(0);
    }

    public boolean setRowHeight(int h) {

        SQLiteDatabase db = getWritableDatabase();

        String setQuery = "UPDATE " + TABLE_SETTINGS_NAME + " SET row_height=" + String.format("%d", h);

        db.execSQL(setQuery);

        return true;
    }

    public boolean setTaxValue(int value, String tax_type) {

        SQLiteDatabase db = getWritableDatabase();

        String setQuery = "UPDATE " + TABLE_SETTINGS_NAME + " SET "+tax_type+"=" + String.format("%d", value);

        db.execSQL(setQuery);

        return true;
    }


    private void onCreateItems(SQLiteDatabase db) {

        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS_NAME + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date DATE, " +
                "receipt_id String, " +
                "shop TEXT, " +
                "item TEXT, " +
                "quantity REAL, " +
                "per_unit REAL, " +
                "tax REAL, " +
                "tax_type REAL, " +
                "price REAL, " +
                "currency TEXT, " +
                "tags TEXT " +
                ");";


        db.execSQL(createQuery);

    }

    private void onCreateTags(SQLiteDatabase db) {

        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_TAGS_NAME + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tag TEXT, " +
                "date INTEGER, " +
                "shop INTEGER, " +
                "address INTEGER, " +
                "nip INTEGER, " +
                "receipt_id INTEGER, " +
                "item INTEGER, " +
                "quantity INTEGER, " +
                "per_unit INTEGER, " +
                "price INTEGER, " +
                "total_tax INTEGER, " +
                "total INTEGER, " +
                "currency INTEGER" +
                ");";


        db.execSQL(createQuery);

        List<String> initTagList = new ArrayList<>();


        String[] initTags = {"date", "shop", "data", "tax", "price", "currency", "quantity"};

        ContentValues contentValues = new ContentValues();

        initTagList.addAll(Arrays.asList(initTags));


        for (int i = 0; i < initTagList.size(); i++) {

            System.out.println(String.format("iTag: %s, index: %d", initTagList.get(i), i));
            contentValues.put("tag", initTagList.get(i));


            long result = db.insert(TABLE_TAGS_NAME, null, contentValues);

            if (result == -1) {


                //       Toast.makeText(this.context, "Failed to save", Toast.LENGTH_SHORT).show();

                System.out.println(String.format("save failed"));

            } else {

                System.out.println(String.format("save succesfull"));

                //   Toast.makeText(this.context, "tags table updated", Toast.LENGTH_SHORT).show();
            }


        }

    }

    private void onCreateSettings(SQLiteDatabase db) {

        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS_NAME + " (" +
                "row_height INTEGER, " +
                "taxA INTEGER, " +
                "taxB INTEGER, " +
                "taxC INTEGER, " +
                "taxD INTEGER, " +
                "taxE INTEGER, " +
                "taxF INTEGER, " +
                "taxG INTEGER " +
                ");";

        ContentValues contentValues = new ContentValues();

        contentValues.put("row_height", 80);
        contentValues.put("taxA", TAX_A);
        contentValues.put("taxB", TAX_B);
        contentValues.put("taxC", TAX_C);
        contentValues.put("taxD", TAX_D);
        contentValues.put("taxE", TAX_E);
        contentValues.put("taxF", TAX_F);
        contentValues.put("taxG", TAX_G);


        db.execSQL(createQuery);

        long result = db.insert(TABLE_SETTINGS_NAME, null, contentValues);

        if (result == -1) {


            //       Toast.makeText(this.context, "Failed to save", Toast.LENGTH_SHORT).show();

            System.out.println(String.format("save failed"));

        } else {

            System.out.println(String.format("save succesfull"));

            //   Toast.makeText(this.context, "tags table updated", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MAIN_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS_NAME);
        onCreate(db);


    }


    public void addNewCategory(String category) {

        SQLiteDatabase db = getWritableDatabase();


        String createQuery_TAGS = "ALTER TABLE " + this.TABLE_TAGS_NAME + " ADD COLUMN  " +
                category + " INTEGER " + " LAST ";


        db.execSQL(createQuery_TAGS);


    }

    public void renameCategory(String oldCategoryName, String newCategoryName) {

        SQLiteDatabase db = getWritableDatabase();

        String createQuery_TAGS = "ALTER TABLE " + this.TABLE_TAGS_NAME + " RENAME COLUMN " +
                oldCategoryName + " TO " + newCategoryName;

        db.execSQL(createQuery_TAGS);
    }

    public void removeCategory(String category) {

        SQLiteDatabase db = getWritableDatabase();

        String createQuery_TAGS = "ALTER TABLE " + this.TABLE_TAGS_NAME + " DROP COLUMN " +
                category;

        db.execSQL(createQuery_TAGS);
    }

    public void addNewTag(String tag) {

        //  String createQuery = "INSERT INTO " + TABLE_TAGS_NAME + " ( tag ) VALUES (" + tag + ");";

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("tag", tag);

        long result = db.insert(TABLE_TAGS_NAME, null, contentValues);

        if (result == -1) {


            //  Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show();

            System.out.println(String.format("save failed"));

        } else {

            System.out.println(String.format("save succesfull"));
            //  Toast.makeText(context, "new tag saved", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean removeTag(String tag) {

        System.out.println(String.format("INTOOLS: %s", tag));

        SQLiteDatabase db = getWritableDatabase();


        return db.delete(TABLE_TAGS_NAME, "tag=\"" + tag + "\"", null) > 0;

        //  return  db.delete(TABLE_TAGS_NAME, "id=9",null) > 0;


    }

    public void addReceipt(ReceiptSchema receiptSchema) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        System.out.println(String.format("total: %s", receiptSchema.total_sum));
        System.out.println(String.format("total_tax: %s", receiptSchema.total_tax));
        System.out.println(String.format("taxA: %s", receiptSchema.taxA));
        System.out.println(String.format("taxB: %s", receiptSchema.taxB));
        System.out.println(String.format("taxC: %s", receiptSchema.taxC));
        System.out.println(String.format("taxD: %s", receiptSchema.taxD));
        System.out.println(String.format("taxE: %s", receiptSchema.taxE));
        System.out.println(String.format("taxF: %s", receiptSchema.taxF));
        System.out.println(String.format("taxG: %s", receiptSchema.taxG));


        contentValues.put("date", receiptSchema.date);
        contentValues.put("shop", receiptSchema.shop);
        contentValues.put("address", receiptSchema.address);
        contentValues.put("receipt_id", receiptSchema.receipt_id);
        contentValues.put("nip", receiptSchema.nip);
        contentValues.put("total", receiptSchema.total_sum);
        contentValues.put("total_tax", receiptSchema.total_tax);
        contentValues.put("taxA", receiptSchema.taxA);
        contentValues.put("taxB", receiptSchema.taxB);
        contentValues.put("taxC", receiptSchema.taxC);
        contentValues.put("taxD", receiptSchema.taxD);
        contentValues.put("taxE", receiptSchema.taxE);
        contentValues.put("taxF", receiptSchema.taxF);
        contentValues.put("taxG", receiptSchema.taxG);
        contentValues.put("currency", receiptSchema.currency);

        long result = database.insert(MAIN_TABLE_NAME, null, contentValues);

        if (result == -1) {


            //  Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show();

            System.out.println(String.format("save failed"));

        } else {

            System.out.println(String.format("save succesfull"));
            //  Toast.makeText(context, "new tag saved", Toast.LENGTH_SHORT).show();
        }

        this.addItems(receiptSchema.itemsList);


    }

    public void addItems(List<ItemSchema> items) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues;

        for (ItemSchema item : items) {

            contentValues = new ContentValues();

            System.out.println(String.format("date: %s", item.date));
            System.out.println(String.format("shop: %s", item.shop));
            System.out.println(String.format("receipt_id: %s", item.receipt_id));
            System.out.println(String.format("item: %s", item.item));
            System.out.println(String.format("quantity: %s", item.quantity));
            System.out.println(String.format("per_unit: %s", item.per_unit));
            System.out.println(String.format("price: %s", item.price));
            System.out.println(String.format("tax: %s", item.tax));
            System.out.println(String.format("price: %s", item.tax_type));
            System.out.println(String.format("currency: %s", item.currency));
            System.out.println(String.format("tags: %s", item.tags));

            contentValues.put("date", item.date);
            contentValues.put("shop", item.shop);
            contentValues.put("receipt_id", item.receipt_id);
            contentValues.put("item", item.item);
            contentValues.put("quantity", item.quantity);
            contentValues.put("per_unit", item.per_unit);
            contentValues.put("price", item.price);
            contentValues.put("tax", item.tax);
            contentValues.put("tax_type", item.tax_type);
            contentValues.put("currency", item.currency);
            contentValues.put("tags", item.tags);


            long result = database.insert(TABLE_ITEMS_NAME, null, contentValues);

            if (result == -1) {


                //  Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show();

                System.out.println(String.format("save failed"));

            } else {

                System.out.println(String.format("save succesfull"));
                //  Toast.makeText(context, "new tag saved", Toast.LENGTH_SHORT).show();
            }


        }


    }


    public List<String> getCtegories(String TABLE) {


        List<String> categories = new ArrayList<>();


        List<String> except = Arrays.asList("id", "tag");

        SQLiteDatabase db = getReadableDatabase();

//        String query = "  SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = "
//                + this.TABLE_ITEMS_NAME + " AND TABLE_SCHEMA=" + this.DATABASE_NAME + ";";

        String query = " PRAGMA table_info(" + TABLE + ");";

        Cursor cursor = null;

        if (db != null) {


            cursor = db.rawQuery(query, null);

            if (cursor.getCount() == 0) {

                System.out.println("no data");

            } else {

                while (cursor.moveToNext()) {

                    String category = cursor.getString(1);

                    if (!(except.stream().anyMatch(ex -> ex.contains(category)))) {


                        categories.add(category);

                    }
                }
            }
        } else {

            System.out.println(String.format("NULL CONTENT"));
        }


        Collections.sort(categories);

        return categories;

    }

    public void updateTagTable(List<String> tags) {

        List<String> categories = this.getCtegories(TABLE_TAGS_NAME);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        for (int i = 0; i < categories.size(); i++) {

            if (tags.indexOf(categories.get(i)) > 0) {

                contentValues.put(categories.get(i), true);


            }
        }


        long result = db.insert(TABLE_TAGS_NAME, null, contentValues);

        if (result == -1) {


            Toast.makeText(this.context, "Failed to save", Toast.LENGTH_SHORT).show();

            System.out.println(String.format("save failed"));

        } else {

            System.out.println(String.format("save succesfull"));

            Toast.makeText(this.context, "tags table updated", Toast.LENGTH_SHORT).show();
        }

    }


    public void assignTagsToItem(List<String> tags) {

        List<String> storedTags = this.getTags();
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        for (String newTag : tags) {

            if (storedTags.indexOf(newTag) < 0) {

                contentValues.put(TABLE_TAGS_NAME, newTag);

            }
        }
    }


    public List<String> getTags() {

        String query = "SELECT * FROM " + this.TABLE_TAGS_NAME;

        SQLiteDatabase db = this.getReadableDatabase();


        List<String> tags = new ArrayList<>();

        Cursor cursor = null;

        if (db != null) {

            cursor = db.rawQuery(query, null);


            if (cursor.getCount() == 0) {

                System.out.println("no data");

            } else {

                int column_index = cursor.getColumnIndex("tag");

                if (column_index > 0) {

                    while (cursor.moveToNext()) {

                        tags.add(cursor.getString(column_index));

                    }
                } else {

                    System.out.println(String.format("No such column"));
                }
            }

        }

        Collections.sort(tags);

        return tags;

    }

    public void dumpDB(Context app) {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();


        File dbDir = new File(root + this.TARGET_FOLDER);


        File f = app.getDatabasePath(this.DATABASE_NAME);

        if (!f.exists()) {
            System.out.println("DB doesn't exist!");
            return;
        }

        System.out.println(String.format("database path:%s", dbDir));


        File file = new File(dbDir, "db_" + new Date().getTime() + ".db");

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(f);
            fos = new FileOutputStream(file.toString());

            while (true) {
                int i = fis.read();
                if (i != -1) {
                    fos.write(i);
                } else {
                    break;
                }
            }
            fos.flush();

            Toast.makeText(app, "DB dump OK", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(app, "DB dump ERROR", Toast.LENGTH_LONG).show();
        } finally {
            try {
                fos.close();
                fis.close();
            } catch (IOException ioe) {

                ioe.printStackTrace();

            }
        }
    }
}
