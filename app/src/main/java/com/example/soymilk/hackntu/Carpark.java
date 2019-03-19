package com.example.soymilk.hackntu;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Carpark {
        @PrimaryKey
        public int CarparkID;

        @ColumnInfo(name = "Development")
        public String development;

        @ColumnInfo(name = "Location")
        public String location;

        @ColumnInfo(name = "AvailableLots")
        public int availableLots;

        @ColumnInfo(name = "LotType")
        public String lotType;

        public Carpark(){}

        public Carpark(int carparkID, String development, String location, int availableLots, String lotType) {
                CarparkID = carparkID;
                this.development = development;
                this.location = location;
                this.availableLots = availableLots;
                this.lotType = lotType;
        }
}


