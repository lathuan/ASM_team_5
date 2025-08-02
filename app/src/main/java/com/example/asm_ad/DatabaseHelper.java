package com.example.asm_ad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserDatabase.db";
    public static final int DATABASE_VERSION = 6;

    // --- Bảng User ---
    public static final String TABLE_USER = "User";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_USERNAME = "username";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_FULLNAME = "fullName";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ROLE_ID = "RoleID";
    public static final String COLUMN_USER_CREATED_AT = "CreatedAt";

    // --- Bảng Role ---
    public static final String TABLE_ROLE = "Role";
    public static final String COLUMN_ROLE_ID = "RoleID";
    public static final String COLUMN_ROLE_NAME = "Name";

    // --- Bảng Income ---
    public static final String TABLE_INCOME = "Income";
    public static final String COLUMN_INCOME_ID = "IncomeID";
    public static final String COLUMN_INCOME_USER_ID = "UserID";
    public static final String COLUMN_INCOME_AMOUNT = "Amount";
    public static final String COLUMN_INCOME_DATE = "Date";
    public static final String COLUMN_INCOME_SOURCE = "Source";
    public static final String COLUMN_INCOME_DESCRIPTION = "Description";

    // --- Bảng Expense ---
    public static final String TABLE_EXPENSE = "Expense";
    public static final String COLUMN_EXPENSE_ID = "ExpenseID";
    public static final String COLUMN_EXPENSE_USER_ID = "UserID";
    public static final String COLUMN_EXPENSE_CATEGORY_ID = "CategoryID";
    public static final String COLUMN_EXPENSE_AMOUNT = "Amount";
    public static final String COLUMN_EXPENSE_DATE = "Date";
    public static final String COLUMN_EXPENSE_DESCRIPTION = "Description";
    public static final String COLUMN_EXPENSE_PAYMENT_METHOD = "PaymentMethod";

    // --- Bảng Goal ---
    public static final String TABLE_GOAL = "Goal";
    public static final String COLUMN_GOAL_ID = "GoalID";
    public static final String COLUMN_GOAL_USER_ID = "UserID";
    public static final String COLUMN_GOAL_NAME = "Name";
    public static final String COLUMN_GOAL_TARGET = "TargetAmount";
    public static final String COLUMN_GOAL_CURRENT = "CurrentAmount";
    public static final String COLUMN_GOAL_DEADLINE = "Deadline";
    public static final String COLUMN_GOAL_STATUS = "Status";

    // --- Bảng Budget ---
    public static final String TABLE_BUDGET = "Budget";
    public static final String COLUMN_BUDGET_ID = "BudgetID";
    public static final String COLUMN_BUDGET_USER_ID = "UserID";
    public static final String COLUMN_BUDGET_CATEGORY_ID = "CategoryID";
    public static final String COLUMN_BUDGET_AMOUNT = "Amount";
    public static final String COLUMN_BUDGET_START = "StartDate";
    public static final String COLUMN_BUDGET_END = "EndDate";
    public static final String COLUMN_BUDGET_PERIOD = "Period";

    // --- Bảng Category ---
    public static final String TABLE_CATEGORY = "Category";
    public static final String COLUMN_CATEGORY_ID = "CategoryID";
    public static final String COLUMN_CATEGORY_USER_ID = "UserID";
    public static final String COLUMN_CATEGORY_NAME = "Name";

    // --- Bảng Notification ---
    public static final String TABLE_NOTIFICATION = "Notification";
    public static final String COLUMN_NOTIFICATION_ID = "NotificationID";
    public static final String COLUMN_NOTIFICATION_USER_ID = "UserID";
    public static final String COLUMN_NOTIFICATION_TYPE = "Type";
    public static final String COLUMN_NOTIFICATION_MESSAGE = "Message";
    public static final String COLUMN_NOTIFICATION_IS_READ = "IsRead";

    // --- Bảng RecurringExpense ---
    public static final String TABLE_RECURRING_EXPENSE = "RecurringExpense";
    public static final String COLUMN_RECURRING_ID = "RecurringExpenseID";
    public static final String COLUMN_RECURRING_USER_ID = "UserID";
    public static final String COLUMN_RECURRING_CATEGORY_ID = "CategoryID";
    public static final String COLUMN_RECURRING_AMOUNT = "Amount";
    public static final String COLUMN_RECURRING_FREQUENCY = "Frequency";
    public static final String COLUMN_RECURRING_START = "StartDate";
    public static final String COLUMN_RECURRING_END = "EndDate";
    public static final String COLUMN_RECURRING_DESCRIPTION = "Description";
    // 10,Thêm vào các constant
    public static final String TABLE_FINANCE = "Finance";
    public static final String COLUMN_FINANCE_ID = "id";
    public static final String COLUMN_FINANCE_USER_ID = "user_id";
    public static final String COLUMN_FINANCE_BALANCE = "balance";
    public static final String COLUMN_FINANCE_TOTAL_EXPENSE = "total_expense";

    // Thêm vào các constant
//    public static final String TABLE_SETTINGS = "Settings";
//    public static final String COLUMN_SETTINGS_ID = "id";
//    public static final String COLUMN_SETTINGS_USER_ID = "user_id";
//    public static final String COLUMN_SETTINGS_DARK_MODE = "dark_mode";
//    public static final String COLUMN_SETTINGS_NOTIFICATIONS = "notifications";
//    public static final String COLUMN_SETTINGS_NOTIFICATION_SOUND = "notification_sound";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng Role
        db.execSQL("CREATE TABLE " + TABLE_ROLE + " (" +
                COLUMN_ROLE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_ROLE_NAME + " TEXT NOT NULL)");

        // Bảng User
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ROLE_ID + " INTEGER NOT NULL, " +
                COLUMN_USER_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_USER_FULLNAME + " TEXT NOT NULL, " +
                COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                COLUMN_USER_PHONE + " TEXT NOT NULL, " +
                COLUMN_USER_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_USER_ROLE_ID + ") REFERENCES " + TABLE_ROLE + "(" + COLUMN_ROLE_ID + "))");

        // Bảng Income
        db.execSQL("CREATE TABLE " + TABLE_INCOME + " (" +
                COLUMN_INCOME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INCOME_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_INCOME_AMOUNT + " REAL NOT NULL, " +
                COLUMN_INCOME_DATE + " TEXT NOT NULL, " +
                COLUMN_INCOME_SOURCE + " TEXT, " +
                COLUMN_INCOME_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_INCOME_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))");

        // Bảng Category
        db.execSQL("CREATE TABLE " + TABLE_CATEGORY + " (" +
                COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_CATEGORY_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))");

        // Bảng Expense
        db.execSQL("CREATE TABLE " + TABLE_EXPENSE + " (" +
                COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXPENSE_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_EXPENSE_CATEGORY_ID + " INTEGER NOT NULL, " +
                COLUMN_EXPENSE_AMOUNT + " REAL NOT NULL, " +
                COLUMN_EXPENSE_DATE + " TEXT NOT NULL, " +
                COLUMN_EXPENSE_DESCRIPTION + " TEXT, " +
                COLUMN_EXPENSE_PAYMENT_METHOD + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_EXPENSE_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_EXPENSE_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + "))");

        // Bảng Goal
        db.execSQL("CREATE TABLE " + TABLE_GOAL + " (" +
                COLUMN_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GOAL_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_GOAL_NAME + " TEXT NOT NULL, " +
                COLUMN_GOAL_TARGET + " REAL NOT NULL, " +
                COLUMN_GOAL_CURRENT + " REAL DEFAULT 0, " +
                COLUMN_GOAL_DEADLINE + " TEXT, " +
                COLUMN_GOAL_STATUS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_GOAL_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))");

        // Bảng Budget
        db.execSQL("CREATE TABLE " + TABLE_BUDGET + " (" +
                COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BUDGET_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_BUDGET_CATEGORY_ID + " INTEGER NOT NULL, " +
                COLUMN_BUDGET_AMOUNT + " REAL NOT NULL, " +
                COLUMN_BUDGET_START + " TEXT, " +
                COLUMN_BUDGET_END + " TEXT, " +
                COLUMN_BUDGET_PERIOD + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_BUDGET_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_BUDGET_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + "))");

        // Bảng Notification
        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATION + " (" +
                COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTIFICATION_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_NOTIFICATION_TYPE + " TEXT, " +
                COLUMN_NOTIFICATION_MESSAGE + " TEXT, " +
                COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_NOTIFICATION_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))");

        // Bảng RecurringExpense
        db.execSQL("CREATE TABLE " + TABLE_RECURRING_EXPENSE + " (" +
                COLUMN_RECURRING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RECURRING_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_RECURRING_CATEGORY_ID + " INTEGER NOT NULL, " +
                COLUMN_RECURRING_AMOUNT + " REAL NOT NULL, " +
                COLUMN_RECURRING_FREQUENCY + " TEXT NOT NULL, " +
                COLUMN_RECURRING_START + " TEXT, " +
                COLUMN_RECURRING_END + " TEXT, " +
                COLUMN_RECURRING_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_RECURRING_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_RECURRING_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + "))");

        //FINANCE
        db.execSQL("CREATE TABLE " + TABLE_FINANCE + " (" +
                COLUMN_FINANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FINANCE_USER_ID + " INTEGER NOT NULL UNIQUE, " +
                COLUMN_FINANCE_BALANCE + " REAL DEFAULT 0, " +
                COLUMN_FINANCE_TOTAL_EXPENSE + " REAL DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_FINANCE_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))");


        // Insert role default
        db.execSQL("INSERT INTO " + TABLE_ROLE + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (1, 'Học sinh')");
        db.execSQL("INSERT INTO " + TABLE_ROLE + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (2, 'Admin')");
    }


    public long addFinanceRecord(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FINANCE_USER_ID, userId);
        values.put(COLUMN_FINANCE_BALANCE, 0);
        values.put(COLUMN_FINANCE_TOTAL_EXPENSE, 0);
        return db.insert(TABLE_FINANCE, null, values);
    }

    public boolean updateUserBalance(int userId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FINANCE_BALANCE, newBalance);
        int rowsAffected = db.update(TABLE_FINANCE, values,
                COLUMN_FINANCE_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public boolean updateUserExpense(int userId, double newExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FINANCE_TOTAL_EXPENSE, newExpense);
        int rowsAffected = db.update(TABLE_FINANCE, values,
                COLUMN_FINANCE_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public double getUserBalance(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_FINANCE_BALANCE + " FROM " + TABLE_FINANCE +
                " WHERE " + COLUMN_FINANCE_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        double balance = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                balance = cursor.getDouble(0);
            }
            cursor.close();
        }
        return balance;
    }

    public double getUserTotalExpense(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_FINANCE_TOTAL_EXPENSE + " FROM " + TABLE_FINANCE +
                " WHERE " + COLUMN_FINANCE_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }
        return 0;
    }


    public boolean hasFinanceRecord(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_FINANCE +
                " WHERE " + COLUMN_FINANCE_USER_ID + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
        }
        return false;
    }

    // Thêm phương thức này vào DatabaseHelper.java
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Phương thức cập nhật mật khẩu mới
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USER, values,
                COLUMN_USER_EMAIL + " = ?", new String[]{email});

        return rowsAffected > 0;
    }

    // Thêm
    public boolean updateUserProfile(int userId, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update(
                TABLE_USER,
                values,
                COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );
        return rowsAffected > 0;
    }



    //xóa toàn bộ dữ liệu ng dùng
    public boolean deleteAllUserData(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            // Xóa tất cả dữ liệu liên quan đến user
            db.delete(TABLE_RECURRING_EXPENSE, COLUMN_RECURRING_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_NOTIFICATION, COLUMN_NOTIFICATION_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_BUDGET, COLUMN_BUDGET_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_GOAL, COLUMN_GOAL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_EXPENSE, COLUMN_EXPENSE_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_CATEGORY, COLUMN_CATEGORY_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_INCOME, COLUMN_INCOME_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_FINANCE, COLUMN_FINANCE_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            db.delete(TABLE_USER, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Thêm phương thức quản lý thời gian thực cho biểu đồ
    public double[] getWeeklyExpensesRealTime(int userId) {
        double[] weeklyExpenses = new double[4]; // Tuần gần nhất đến 4 tuần trước
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT strftime('%W', " + COLUMN_EXPENSE_DATE + ") AS week, " +
                "SUM(" + COLUMN_EXPENSE_AMOUNT + ") as total " +
                "FROM " + TABLE_EXPENSE + " " +
                "WHERE " + COLUMN_EXPENSE_USER_ID + " = ? " +
                "GROUP BY week " +
                "ORDER BY week DESC " +
                "LIMIT 4";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        int index = 3; // Hiển thị theo thứ tự: Tuần cũ -> mới
        if (cursor.moveToFirst()) {
            do {
                double sum = cursor.getDouble(1);
                if (index >= 0) {
                    weeklyExpenses[index] = sum;
                    index--;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weeklyExpenses;
    }
//Thêm phương thức theo dõi biểu đồ theo tháng
    public float[] getMonthlyExpenses(int userId) {
        float[] monthlyExpenses = new float[12];
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT strftime('%m', " + COLUMN_EXPENSE_DATE + ") AS month, " +
                "SUM(" + COLUMN_EXPENSE_AMOUNT + ") as total " +
                "FROM " + TABLE_EXPENSE + " " +
                "WHERE " + COLUMN_EXPENSE_USER_ID + " = ? " +
                "AND strftime('%Y', " + COLUMN_EXPENSE_DATE + ") = strftime('%Y', 'now') " +
                "GROUP BY month " +
                "ORDER BY month ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                int month = Integer.parseInt(cursor.getString(0)) - 1;
                float total = cursor.getFloat(1);
                if (month >= 0 && month < 12) {
                    monthlyExpenses[month] = total;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return monthlyExpenses;
    }
//chức năng tính và trả về tổng số tiền thu nhập của một người dùng cụ thể dựa trên userId
    public double getUserTotalIncome(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_INCOME_AMOUNT + ") FROM " + TABLE_INCOME +
                " WHERE " + COLUMN_INCOME_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        double totalIncome = 0;
        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return totalIncome;
    }




    // Thêm phương thức close()
    @Override
    public void close() {
        super.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FINANCE);
        onCreate(db);
    }
}