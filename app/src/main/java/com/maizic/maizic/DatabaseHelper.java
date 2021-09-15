package com.maizic.maizic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, 12);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS device_info (id integer primary key AUTOINCREMENT,dev_id integer,name text,ip text,port integer,username text,password text,domain text,save_type integer,new_msg_count integer,last_fresh_time long,last_get_time long,online_stat integer,online_stat_time long,one_key_alarm_state integer,product_id integer,synchronization_sign integer,can_update integer,face BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS device_info2 (id integer primary key AUTOINCREMENT,dev_id integer,name text,ip text,port integer,username text,password text,domain text,save_type integer,new_msg_count integer,last_fresh_time long,last_get_time long,online_stat integer,online_stat_time long,one_key_alarm_state integer,product_id integer,synchronization_sign integer,can_update integer,face BLOB)");
        db.execSQL("CREATE TABLE IF NOT EXISTS rec_info (id integer primary key AUTOINCREMENT,file_id integer,file_size integer,name text,start_hour integer,start_min integer,start_sec integer,file_time_len integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS mr_server_info (id integer primary key AUTOINCREMENT,server_id integer,is_init integer,init_time integer,domain text,ip text)");
        db.execSQL("CREATE TABLE IF NOT EXISTS server_face (id integer primary key AUTOINCREMENT,server_id integer,data_size integer,face blob)");
        db.execSQL("CREATE TABLE IF NOT EXISTS alarm_picture (id integer primary key AUTOINCREMENT,save_id int,alarm_id int,dev_id int,alarm_type int,alarm_level int,alarm_msg text,alarm_time text,str_alarm_image text,str_image_ip text,has_position integer,save_time long,image blob)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ptzx_face (id integer primary key AUTOINCREMENT,dev_id int,ptzx_id int,save_time long,image blob)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("onUpgrade: " + oldVersion + newVersion);
        if (oldVersion < 8) {
            try {
                db.execSQL(" drop table device_info");
                db.execSQL(" drop table rec_info");
                db.execSQL(" drop table mr_server_info");
                db.execSQL(" drop table alarm_picture");
                db.execSQL(" drop table ptzx_face");
            } catch (Exception e) {
            }
            onCreate(db);
        } else if (oldVersion == 8) {
            System.out.println("oldVersion == 8");
            db.execSQL("alter table device_info add one_key_alarm_state integer");
            db.execSQL("CREATE TABLE IF NOT EXISTS device_info2 (id integer primary key AUTOINCREMENT,dev_id integer,name text,ip text,port integer,username text,password text,domain text,save_type integer,new_msg_count integer,last_fresh_time long,last_get_time long,online_stat integer,online_stat_time long,one_key_alarm_state integer,product_id integer,synchronization_sign integer,face BLOB)");
            db.execSQL("alter table device_info add product_id integer");
            db.execSQL("alter table device_info add synchronization_sign integer");
        } else if (oldVersion == 9) {
            System.out.println("oldVersion == 9");
            db.execSQL("CREATE TABLE IF NOT EXISTS device_info2 (id integer primary key AUTOINCREMENT,dev_id integer,name text,ip text,port integer,username text,password text,domain text,save_type integer,new_msg_count integer,last_fresh_time long,last_get_time long,online_stat integer,online_stat_time long,one_key_alarm_state integer,product_id integer,synchronization_sign integer,face BLOB)");
            db.execSQL("alter table device_info add product_id integer");
            db.execSQL("alter table device_info add synchronization_sign integer");
        } else if (oldVersion == 10) {
            db.execSQL("alter table device_info2 add synchronization_sign integer");
            db.execSQL("alter table device_info add synchronization_sign integer");
            db.execSQL("alter table alarm_picture add str_alarm_image text");
            db.execSQL("alter table alarm_picture add str_image_ip text");
            db.execSQL("alter table alarm_picture add has_position integer");
        } else if (oldVersion == 11) {
            db.execSQL("alter table device_info add column can_update integer");
            db.execSQL("alter table device_info2 add column can_update integer");
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion < oldVersion) {
            try {
                db.execSQL(" drop table device_info");
                db.execSQL(" drop table rec_info");
                db.execSQL(" drop table mr_server_info");
                db.execSQL(" drop table alarm_picture");
                db.execSQL(" drop table server_face");
                db.execSQL(" drop table device_info2");
            } catch (Exception e) {
            }
            onCreate(db);
        }
    }
}
