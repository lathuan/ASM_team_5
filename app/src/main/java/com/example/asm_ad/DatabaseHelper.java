package com.example.asm_ad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserDatabase.db";
    public static final int DATABASE_VERSION = 2;
    // --- Bảng User ---
    public static final String TABLE_USER = "User"; // Đổi tên từ "users"
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_FULLNAME = "fullName";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ROLE_ID = "RoleID"; // Cột mới: Khóa ngoại
    public static final String COLUMN_USER_CREATED_AT = "CreatedAt"; // Cột mới

    // --- Bảng Role ---
    public static final String TABLE_ROLES = "Role";
    public static final String COLUMN_ROLE_ID = "RoleID";
    public static final String COLUMN_ROLE_NAME = "Name";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng Role trước vì bảng User sẽ tham chiếu đến nó
        String CREATE_ROLES_TABLE = "CREATE TABLE " + TABLE_ROLES + " (" +
                COLUMN_ROLE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ROLE_NAME + " TEXT NOT NULL)";
        db.execSQL(CREATE_ROLES_TABLE);

        // 2. Chèn dữ liệu mặc định cho bảng Role
        db.execSQL("INSERT INTO " + TABLE_ROLES + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (1, 'Học sinh')");
        db.execSQL("INSERT INTO " + TABLE_ROLES + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (2, 'Admin')");

        // 3. Tạo bảng User với các cột mới và khóa ngoại
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_USER_FULLNAME + " TEXT NOT NULL, " +
                COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                COLUMN_USER_PHONE + " TEXT NOT NULL, " +
                COLUMN_USER_ROLE_ID + " INTEGER NOT NULL, " +
                COLUMN_USER_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP, " + // Tự động lấy ngày giờ hiện tại
                "FOREIGN KEY(" + COLUMN_USER_ROLE_ID + ") REFERENCES " + TABLE_ROLES + "(" + COLUMN_ROLE_ID + "))";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ theo đúng thứ tự: bảng tham chiếu (User) trước, bảng gốc (Role) sau
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
        // Gọi lại onCreate để tạo lại toàn bộ cấu trúc CSDL mới
        onCreate(db);
    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
//        onCreate(db);
//    }
}