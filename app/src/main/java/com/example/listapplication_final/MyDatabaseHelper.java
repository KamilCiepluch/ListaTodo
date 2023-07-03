package com.example.listapplication_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;


public class MyDatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME ="List.db";
    private static final int DATABASE_VERSION =4;

    public MyDatabaseHelper(@NonNull Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query1 = "CREATE TABLE IF NOT EXISTS TAGS " +
                "(   ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    NAME VARCHAR(30) UNIQUE NOT NULL," +
                "    IS_ACTIVE INT(1) DEFAULT 0);";

        String query2 = "CREATE TABLE IF NOT EXISTS DATA" +
                        "(" +
                        "    ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "    TITLE VARCHAR(50)," +
                        "    DESCRIPTION VARCHAR(500)," +
                        "    CREATION_TIME DATE," +
                        "    EXECUTION_TIME DATE," +
                        "    STATUS INTEGER," +
                        "    NOTIFICATIONS INTEGER," +
                        "    CATEGORY_ID INTEGER," +
                        "    IMAGE BLOB);";

        String query3 = "CREATE TABLE IF NOT EXISTS ATTACHMENTS" +
                        "(" +
                        "    ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "    DATA_ID INTEGER," +
                        "    PATH VARCHAR(50));";
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion<newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS ATTACHMENTS;");
            db.execSQL("DROP TABLE IF EXISTS DATA;");
            db.execSQL("DROP TABLE IF EXISTS TAGS;");
            onCreate(db);
        }
    }


    void addTag(String tag)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME",tag.trim().toUpperCase());
        // not valid error
        try {
            db.insertOrThrow("TAGS",null,values);
        }catch (Exception ignored){}
    }
    void addTag(String tag, boolean isActive)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NAME",tag.trim().toUpperCase());
        int active = isActive ? 1:0;
        values.put("IS_ACTIVE", active);
        // not valid error
        try {
            db.insertOrThrow("TAGS",null,values);
        }catch (Exception ignored){}
    }

    void updateTagStatus(int tagID, boolean isActive)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int active = isActive ? 1 : 0;
        values.put("IS_ACTIVE", active);
        String[] whereArgs = {String.valueOf(tagID)};
        db.update("TAGS", values, "ID = ?", whereArgs);
        db.close();
    }
    void deleteTag(String tag) {
        int tagId = getTagID(tag);
        SQLiteDatabase db = this.getWritableDatabase();
        try {
           db.delete("TAGS", "ID = ?", new String[]{String.valueOf(tagId)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void printTags()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAGS",null);
        while (cursor.moveToNext())
        {
            String item = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            Log.wtf("test", item);
        }
        cursor.close();
    }

    int getTagID(String tagName) {
        int tagId = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM TAGS WHERE NAME =\"" + tagName.toUpperCase()+"\"", null);
        if (cursor.moveToFirst()) {
            tagId = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
        }
        cursor.close();
        return tagId;
    }

    public String getTagName(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAGS WHERE ID="+ id ,null);
        String item = null;
        if (cursor.moveToNext())
        {
            item = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            Log.wtf("test", item);
        }
        cursor.close();
        return item;
    }





    public  ArrayList<Integer> getTagsID()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAGS",null);
        ArrayList<Integer> tags_id = new ArrayList<Integer>();
        while (cursor.moveToNext())
        {
            Integer tag_id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            tags_id.add(tag_id);
        }
        cursor.close();
        return tags_id;
    }


    public ArrayList<String> getTagsNames()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAGS",null);
        ArrayList<String> tags = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String item = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            tags.add(item);
        }
        cursor.close();
        return tags;
    }

    public ArrayList<TagModel> getTagsNamesAsTagsArray()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAGS",null);
        ArrayList<TagModel> tags = new ArrayList<>();
        while (cursor.moveToNext())
        {
            int tagID = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String item = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow("IS_ACTIVE")) == 1;
            TagModel model = new TagModel(tagID,item,isActive);
            tags.add(model);
        }
        cursor.close();
        return tags;
    }

    public ArrayList<TagModel> getActiveTagsArray()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAGS WHERE IS_ACTIVE=1",null);
        ArrayList<TagModel> tags = new ArrayList<>();
        while (cursor.moveToNext())
        {
            int tagID = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String item = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            boolean isActive = cursor.getInt(cursor.getColumnIndexOrThrow("IS_ACTIVE")) == 1;
            TagModel model = new TagModel(tagID,item,isActive);
            tags.add(model);
        }
        cursor.close();
        return tags;
    }
    public ArrayList<Integer> getActiveTagsIDsArray()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TAGS WHERE IS_ACTIVE=1",null);
        ArrayList<Integer> tags = new ArrayList<>();
        while (cursor.moveToNext())
        {
            int tagID = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            tags.add(tagID);
        }
        cursor.close();
        return tags;
    }
    public int getCountByTasksWithTag(String tagName)
    {
        int categoryId = getTagID(tagName);
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(categoryId)};
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM DATA WHERE CATEGORY_ID=?", selectionArgs);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


    public long addData(DataModel data)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TITLE", data.getTitle());
        values.put("DESCRIPTION", data.getDescription());
        values.put("CREATION_TIME", data.getCreationTime());
        values.put("EXECUTION_TIME", data.getExecutionTime());
        values.put("STATUS", data.getFinished());
        values.put("NOTIFICATIONS", data.getNotifications());
        values.put("CATEGORY_ID", data.getCategoryId());
        values.put("IMAGE",data.getImage());
        long id = db.insert("DATA", null, values);
        db.close();
        return id;
    }

    public long updateData(DataModel data)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TITLE", data.getTitle());
        values.put("DESCRIPTION", data.getDescription());
        values.put("CREATION_TIME", data.getCreationTime());
        values.put("EXECUTION_TIME", data.getExecutionTime());
        values.put("STATUS", data.getFinished());
        values.put("NOTIFICATIONS", data.getNotifications());
        values.put("CATEGORY_ID", data.getCategoryId());
        values.put("IMAGE",data.getImage());
        long id = db.update("DATA", values,"ID=?" ,new String[]{String.valueOf(data.getPrimaryKey())});
        db.close();
        return id;
    }

    public void addAttachments(List<String> paths, long data_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        for(String path:paths)
        {
            ContentValues values = new ContentValues();
            values.put("DATA_ID", data_id);
            values.put("PATH", path);
            db.insert("ATTACHMENTS", null, values);
        }
        db.close();
    }

    public void updateAttachments(List<String> paths, long data_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("ATTACHMENTS", "DATA_ID=?",new String[]{String.valueOf(data_id)});
        for (String path : paths) {
            ContentValues values = new ContentValues();
            values.put("DATA_ID", data_id);
            values.put("PATH", path);
            db.insert("ATTACHMENTS", null, values);
        }
        db.close();
    }
    public List<DataModel> getTaskList()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DATA",null);
        List<DataModel> list = new ArrayList<>();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            String creationTime = cursor.getString(cursor.getColumnIndexOrThrow("CREATION_TIME"));
            String executionTime = cursor.getString(cursor.getColumnIndexOrThrow("EXECUTION_TIME"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("STATUS"));
            int notifications = cursor.getInt(cursor.getColumnIndexOrThrow("NOTIFICATIONS"));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY_ID"));
            byte[] image=  cursor.getBlob(cursor.getColumnIndexOrThrow("IMAGE"));

            DataModel data = new DataModel(id, title,description,creationTime,executionTime,
                    status> 0, notifications>0, categoryId,image);


            list.add(data);
        }
        cursor.close();
        return list;
    }


    public List<DataModel> getTasksListWithActiveNotifications()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DATA WHERE NOTIFICATIONS=1",null);
        List<DataModel> list = new ArrayList<>();
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            String creationTime = cursor.getString(cursor.getColumnIndexOrThrow("CREATION_TIME"));
            String executionTime = cursor.getString(cursor.getColumnIndexOrThrow("EXECUTION_TIME"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("STATUS"));
            int notifications = cursor.getInt(cursor.getColumnIndexOrThrow("NOTIFICATIONS"));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY_ID"));
            byte[] image=  cursor.getBlob(cursor.getColumnIndexOrThrow("IMAGE"));

            DataModel data = new DataModel(id, title,description,creationTime,executionTime,
                    status> 0, notifications>0, categoryId,image);


            list.add(data);
        }
        cursor.close();
        return list;
    }


    public DataModel getTask(Long taskID)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DATA WHERE ID=" +taskID ,null);
        DataModel result = null;
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            String creationTime = cursor.getString(cursor.getColumnIndexOrThrow("CREATION_TIME"));
            String executionTime = cursor.getString(cursor.getColumnIndexOrThrow("EXECUTION_TIME"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("STATUS"));
            int notifications = cursor.getInt(cursor.getColumnIndexOrThrow("NOTIFICATIONS"));
            int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("CATEGORY_ID"));
            byte[] image=  cursor.getBlob(cursor.getColumnIndexOrThrow("IMAGE"));

             result = new DataModel(id, title,description,creationTime,executionTime,
                    status> 0, notifications>0, categoryId,image);

        }
        cursor.close();
        return result;
    }

    public ArrayList<String> getAttachmentsList(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ATTACHMENTS where DATA_ID= "+id,null);
        ArrayList<String> list = new ArrayList<>();
        while (cursor.moveToNext())
        {
            String path = cursor.getString(cursor.getColumnIndexOrThrow("PATH"));
            list.add(path);
        }
        cursor.close();
        return list;
    }

    public void  printAttachments()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ATTACHMENTS",null);
        while (cursor.moveToNext())
        {
            String path = cursor.getString(cursor.getColumnIndexOrThrow("PATH"));
            Log.wtf("PATH", path );
        }
        cursor.close();
    }


    public void updateDataImage(int id, byte [] image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("IMAGE", image);

        // on below line we are calling a update method to update our database and passing our values.
        // and we are comparing it with name of our course which is stored in original name variable.
        db.update("DATA", values, "id=?", new String[]{Integer.toString(id)});
        db.close();
    }
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("TAGS", null, null);
        db.delete("DATA", null, null);
        db.delete("ATTACHMENTS", null, null);
        db.close();
    }
}