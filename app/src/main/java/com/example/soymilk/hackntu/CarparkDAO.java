package com.example.soymilk.hackntu;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface CarparkDAO {
    @Query("SELECT * FROM Carpark")
    List<Carpark> getAll();


    @Insert
    void insertAll(Carpark... carparks);

    @Delete
    void delete(Carpark carpack);
}
