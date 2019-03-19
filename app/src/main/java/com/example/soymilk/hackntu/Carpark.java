package com.example.soymilk.hackntu;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Carpark {
        @PrimaryKey
        public int CarparkID;

        @ColumnInfo(name = "Development")
        public String development;

        @ColumnInfo(name = "Location")
        public String location;

        @ColumnInfo(name = "AvailableLots")
        public String availableLots;

        @ColumnInfo(name = "LotType")
        public String lotType;
    }


