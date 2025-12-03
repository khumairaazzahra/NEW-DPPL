package com.sipa.model;

// PERBAIKAN: Tambah 'public'
public class MataKuliah {
    public String kode, nama, ruangan, waktu, dosen;
    public boolean sesiAktif;

    public MataKuliah(String k, String n, String r, String w, String d, boolean sesi) {
        kode=k; nama=n; ruangan=r; waktu=w; dosen=d; sesiAktif=sesi;
    }
    @Override public String toString() { return nama; }
}