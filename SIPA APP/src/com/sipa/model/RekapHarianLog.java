package com.sipa.model;

// PERBAIKAN: Tambah 'public'
public class RekapHarianLog {
    public String tanggal;
    public String namesHadir, namesIzin, namesAlpha; 
    public int countHadir, countIzin, countAlpha, total; 

    public RekapHarianLog(String t, String nh, int ch, String ni, int ci, String na, int ca) {
        this.tanggal = t;
        this.namesHadir = nh; this.countHadir = ch;
        this.namesIzin = ni;  this.countIzin = ci;
        this.namesAlpha = na; this.countAlpha = ca;
        this.total = ch + ci + ca;
    }
}