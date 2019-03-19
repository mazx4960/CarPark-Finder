package com.example.soymilk.hackntu;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Carpark.class}, version = 1)
public abstract class CarparkDatabase extends RoomDatabase {
    public abstract CarparkDAO carpackDAO();
}
