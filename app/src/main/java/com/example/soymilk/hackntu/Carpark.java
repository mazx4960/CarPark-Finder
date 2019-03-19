package com.example.soymilk.hackntu;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Carpark {
        @PrimaryKey
        private int CarparkID;

        @ColumnInfo(name = "Development")
        private String development;

        @ColumnInfo(name = "Location")
        private String location;

        @ColumnInfo(name = "AvailableLots")
        private int availableLots;

        @ColumnInfo(name = "LotType")
        private String lotType;

        public Carpark(int carparkID, String development, String location, int availableLots, String lotType) {
                CarparkID = carparkID;
                this.development = development;
                this.location = location;
                this.availableLots = availableLots;
                this.lotType = lotType;
        }
}


