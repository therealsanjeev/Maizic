package com.maizic.maizic.roomdatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Created by Sanjeev on 08,June,2021
 * therealsanjeev0@gmail.com
 */
@Database(entities = {RoomModel.class}, version = 1,exportSchema = false)
public abstract class LocalDataBase extends RoomDatabase {

    private static LocalDataBase instance;
    public abstract RoomDao roomDao();

    public static synchronized LocalDataBase getInstance(Context context){
        if(instance==null){
            //If instance is null that's mean database is not created and create new database
            instance = Room.databaseBuilder(context.getApplicationContext(),LocalDataBase.class,"camera_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
