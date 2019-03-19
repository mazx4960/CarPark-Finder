package com.example.soymilk.hackntu;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Carpark.class}, version = 1)
public abstract class CarparkDatabase extends RoomDatabase {
    public abstract CarparkDAO carpackDAO();
}
