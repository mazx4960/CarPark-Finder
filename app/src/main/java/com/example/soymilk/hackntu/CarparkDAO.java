package com.example.soymilk.hackntu;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface CarparkDAO {
    @Query("SELECT * FROM Carpark")
    List<Carpark> getAll();


    @Insert
    void insertAll(Carpark... carparks);

    @Delete
    void delete(Carpark carpack);
}
