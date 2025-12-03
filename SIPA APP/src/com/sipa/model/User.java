package com.sipa.model;

// PERBAIKAN: Tambah 'public'
public class User {
    public String id, password, name, role, email, phone;
    public User(String i, String p, String n, String r, String e, String ph) {
        id=i; password=p; name=n; role=r; email=e; phone=ph;
    }
    @Override public String toString() { return name; }
}