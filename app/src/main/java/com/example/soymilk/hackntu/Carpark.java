package com.example.soymilk.hackntu;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Carpark {

        @PrimaryKey(autoGenerate = true)
        public int rowId;

        @ColumnInfo(name = "CarParkID")
        public String CarParkID;

        @ColumnInfo(name = "Development")
        public String development;

        @ColumnInfo(name = "Location")
        public String location;

        @ColumnInfo(name = "AvailableLots")
        public int availableLots;

        @ColumnInfo(name = "LotType")
        public String lotType;

        public Carpark(){}

        public Carpark(String carparkID, String development, String location, int availableLots, String lotType) {
                CarParkID = carparkID;
                this.development = development;
                this.location = location;
                this.availableLots = availableLots;
                this.lotType = lotType;
        }
}


