package com.sipa.model;

// PERBAIKAN: Tambah 'public'
public class PresensiLog {
    public String nimMhs, namaMhs, kodeMk, status, tanggal, waktuCheckIn, catatan; 
    public PresensiLog(String nim, String nama, String k, String s, String t, String w, String c) {
        nimMhs=nim; namaMhs=nama; kodeMk=k; status=s; tanggal=t; waktuCheckIn=w; catatan=c;
    }
}