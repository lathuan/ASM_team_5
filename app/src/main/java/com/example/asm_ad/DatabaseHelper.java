package com.example.asm_ad;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "UserDatabase.db";
    public static final int DATABASE_VERSION = 3;

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

        // Insert role default
        db.execSQL("INSERT INTO " + TABLE_ROLE + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (1, 'Học sinh')");
        db.execSQL("INSERT INTO " + TABLE_ROLE + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (2, 'Admin')");
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
        onCreate(db);
    }
}