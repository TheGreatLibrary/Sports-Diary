package com.sinya.projects.sportsdiary.core.data.dataBase

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(db: SupportSQLiteDatabase) {} }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Добавляем state
            db.execSQL("""
                    ALTER TABLE data_training
                    ADD COLUMN state INTEGER NOT NULL DEFAULT 1
                """.trimIndent())

            // 2. Добавляем order_index
            db.execSQL("""
                    ALTER TABLE data_training
                    ADD COLUMN order_index INTEGER NOT NULL DEFAULT 0
                """.trimIndent())

            db.execSQL("""
                    UPDATE data_training
                    SET order_index = (
                        SELECT COUNT(*)
                        FROM data_training dt2
                        WHERE dt2.training_id = data_training.training_id
                          AND dt2.exercises_id < data_training.exercises_id
                    )
                """.trimIndent())
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL("""
                    ALTER TABLE data_type_trainings
                    ADD COLUMN order_index INTEGER NOT NULL DEFAULT 0
                """.trimIndent())

            db.execSQL("""
                    UPDATE data_type_trainings
                    SET order_index = (
                        SELECT COUNT(*)
                        FROM data_type_trainings dt2
                        WHERE dt2.type_id = data_type_trainings.type_id
                          AND dt2.exercise_id < data_type_trainings.exercise_id
                    )
                """.trimIndent())
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                    INSERT OR IGNORE INTO type_training (id, name)
                    VALUES (1, 'not_category')
                """.trimIndent())

            db.execSQL("""
                    CREATE TABLE training_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        serial_num INTEGER NOT NULL,
                        type_id INTEGER NOT NULL DEFAULT 1,
                        date TEXT NOT NULL,
                        FOREIGN KEY (type_id) 
                        REFERENCES type_training(id) 
                        ON DELETE SET DEFAULT
                    )
                """.trimIndent())

            db.execSQL("""
                    INSERT INTO training_new (id, serial_num, type_id, date)
                    SELECT 
                        t.id,
                        t.serial_num,
                        CASE 
                            WHEN t.type_id IN (SELECT id FROM type_training) 
                            THEN t.type_id 
                            ELSE 1 
                        END as type_id,
                        t.date
                    FROM trainings t
                """.trimIndent())

            db.execSQL("DROP TABLE trainings")

            db.execSQL("ALTER TABLE training_new RENAME TO trainings")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_type_id ON trainings (type_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_date ON trainings (date DESC)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_type_id_date ON trainings (type_id, date)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_date_serial_num ON trainings (date, serial_num)")

            db.execSQL("""
                    UPDATE data_training
                    SET training_id = 1
                    WHERE training_id NOT IN (SELECT id FROM trainings)
                """.trimIndent())

            db.execSQL("""
                    UPDATE data_type_trainings
                    SET type_id = 1
                    WHERE type_id NOT IN (SELECT id FROM type_training)
                """.trimIndent())
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                    CREATE TABLE trainings_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        serial_num INTEGER NOT NULL,
                        type_id INTEGER NULL,
                        date TEXT NOT NULL,
                        FOREIGN KEY (type_id)
                        REFERENCES type_training(id)
                        ON DELETE SET NULL
                    )""".trimIndent()
            )

            db.execSQL("""
                    INSERT INTO trainings_new (id, serial_num, type_id, date)
                    SELECT
                        t.id,
                        t.serial_num,
                        CASE
                            WHEN t.type_id IS NULL THEN NULL
                            WHEN t.type_id NOT IN (SELECT id FROM type_training) THEN NULL
                            WHEN t.type_id = 1 THEN NULL
                            ELSE t.type_id
                        END,
                        t.date
                    FROM trainings t
                    """.trimIndent()
            )

            db.execSQL("DROP TABLE trainings")

            db.execSQL("ALTER TABLE trainings_new RENAME TO trainings")

            db.execSQL("DELETE FROM type_training WHERE id = 1")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_type_id ON trainings (type_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_date ON trainings (date DESC)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_type_id_date ON trainings (type_id, date)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_trainings_date_serial_num ON trainings (date, serial_num)")

            db.execSQL("""
                    DELETE FROM data_training
                    WHERE training_id NOT IN (SELECT id FROM trainings)
                    """.trimIndent()
            )

            db.execSQL("""
                    DELETE FROM data_type_trainings
                    WHERE type_id NOT IN (SELECT id FROM type_training)
                    """.trimIndent()
            )

            db.execSQL("""
                    CREATE TABLE data_mornings_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        note TEXT,
                        date TEXT NOT NULL,
                        plan_id INTEGER NULL,
                        FOREIGN KEY (plan_id)
                        REFERENCES plan_mornings(id)
                        ON DELETE SET NULL
                    )""".trimIndent()
            )

            db.execSQL("""
                    INSERT INTO data_mornings_new (id, note, date, plan_id)
                    SELECT
                        id,
                        note,
                        date,
                        CASE
                            WHEN plan_id NOT IN (SELECT id FROM plan_mornings) THEN NULL
                            ELSE plan_id
                        END
                    FROM data_mornings
                    """.trimIndent()
            )

            db.execSQL("DROP TABLE data_mornings")
            db.execSQL("ALTER TABLE data_mornings_new RENAME TO data_mornings")

            db.execSQL("CREATE INDEX IF NOT EXISTS index_data_mornings_plan_id ON data_mornings (plan_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_data_mornings_date ON data_mornings (date DESC)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_data_mornings_date_plan_id ON data_mornings (date, plan_id)")

            db.execSQL("""
                    DELETE FROM plan_mornings
                    WHERE id = 0
                    """.trimIndent()
            )
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("PRAGMA foreign_keys=OFF")

            db.execSQL("CREATE TABLE IF NOT EXISTS `forces` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
            db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `force_translation` (
                        `force_id` INTEGER NOT NULL,
                        `language` TEXT NOT NULL,
                        `name` TEXT UNIQUE NOT NULL,
                        PRIMARY KEY(`force_id`, `language`),
                        FOREIGN KEY(`force_id`) REFERENCES `forces`(`id`) ON DELETE CASCADE
                    )
                """)
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_force_translation_force_id` ON `force_translation` (`force_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_force_translation_language` ON `force_translation` (`language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_force_translation_force_id_language` ON `force_translation` (`force_id`, `language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_force_translation_name` ON `force_translation` (`name`)")

            // Levels
            db.execSQL("CREATE TABLE IF NOT EXISTS `levels` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
            db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `level_translation` (
                        `level_id` INTEGER NOT NULL,
                        `language` TEXT NOT NULL,
                        `name` TEXT UNIQUE NOT NULL,
                        PRIMARY KEY(`level_id`, `language`),
                        FOREIGN KEY(`level_id`) REFERENCES `levels`(`id`) ON DELETE CASCADE
                    )
                """)
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_level_translation_level_id` ON `level_translation` (`level_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_level_translation_language` ON `level_translation` (`language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_level_translation_level_id_language` ON `level_translation` (`level_id`, `language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_level_translation_name` ON `level_translation` (`name`)")

            // Mechanics
            db.execSQL("CREATE TABLE IF NOT EXISTS `mechanics` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
            db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `mechanic_translation` (
                        `mechanic_id` INTEGER NOT NULL,
                        `language` TEXT NOT NULL,
                        `name` TEXT UNIQUE NOT NULL,
                        PRIMARY KEY(`mechanic_id`, `language`),
                        FOREIGN KEY(`mechanic_id`) REFERENCES `mechanics`(`id`) ON DELETE CASCADE
                    )
                """)
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_mechanic_translation_mechanic_id` ON `mechanic_translation` (`mechanic_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_mechanic_translation_language` ON `mechanic_translation` (`language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_mechanic_translation_mechanic_id_language` ON `mechanic_translation` (`mechanic_id`, `language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_mechanic_translation_name` ON `mechanic_translation` (`name`)")

            // Equipments
            db.execSQL("CREATE TABLE IF NOT EXISTS `equipments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
            db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `equipment_translation` (
                        `eq_id` INTEGER NOT NULL,
                        `language` TEXT NOT NULL,
                      `name` TEXT UNIQUE NOT NULL,
                        PRIMARY KEY(`eq_id`, `language`),
                        FOREIGN KEY(`eq_id`) REFERENCES `equipments`(`id`) ON DELETE CASCADE
                    )
                """)
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_equipment_translation_eq_id` ON `equipment_translation` (`eq_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_equipment_translation_language` ON `equipment_translation` (`language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_equipment_translation_eq_id_language` ON `equipment_translation` (`eq_id`, `language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_equipment_translation_name` ON `equipment_translation` (`name`)")

            // Categories
            db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
            db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `category_translation` (
                        `category_id` INTEGER NOT NULL,
                        `language` TEXT NOT NULL,
                       `name` TEXT UNIQUE NOT NULL,
                        PRIMARY KEY(`category_id`, `language`),
                        FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON DELETE CASCADE
                    )
                """)
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_category_translation_category_id` ON `category_translation` (`category_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_category_translation_language` ON `category_translation` (`language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_category_translation_category_id_language` ON `category_translation` (`category_id`, `language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_category_translation_name` ON `category_translation` (`name`)")

            migrateReferenceData(db, "force", "forces", "force_translation", "force_id")
            migrateReferenceData(db, "level", "levels", "level_translation", "level_id")
            migrateReferenceData(db, "mechanic", "mechanics", "mechanic_translation", "mechanic_id")
            migrateReferenceData(db, "equipment", "equipments", "equipment_translation", "eq_id")
            migrateReferenceData(db, "category", "categories", "category_translation", "category_id")

            db.execSQL("""
            CREATE TABLE `exercises_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `icon` TEXT NOT NULL,
                `is_custom` INTEGER NOT NULL DEFAULT 0,
                `force_id` INTEGER,
                `level_id` INTEGER,
                `mechanic_id` INTEGER,
                `equipment_id` INTEGER,
                `category_id` INTEGER
            )
        """)

            db.execSQL("""
            INSERT INTO exercises_new (id, icon, is_custom, force_id, level_id, mechanic_id, equipment_id, category_id)
            SELECT 
                e.id,
                e.icon,
                0 AS is_custom,
                (SELECT f.id FROM forces f 
                 JOIN force_translation ft ON f.id = ft.force_id 
                 WHERE ft.name = et.force LIMIT 1),
                (SELECT l.id FROM levels l 
                 JOIN level_translation lt ON l.id = lt.level_id 
                 WHERE lt.name = et.level LIMIT 1),
                (SELECT m.id FROM mechanics m 
                 JOIN mechanic_translation mt ON m.id = mt.mechanic_id 
                 WHERE mt.name = et.mechanic LIMIT 1),
                (SELECT eq.id FROM equipments eq 
                 JOIN equipment_translation eqt ON eq.id = eqt.eq_id 
                 WHERE eqt.name = et.equipment LIMIT 1),
                (SELECT c.id FROM categories c 
                 JOIN category_translation ct ON c.id = ct.category_id 
                 WHERE ct.name = et.category LIMIT 1)
            FROM exercises e
            LEFT JOIN exercise_translations et ON e.id = et.exercise_id
            GROUP BY e.id
        """)

            db.execSQL("""
            CREATE TABLE `exercise_translations_new` (
                `exercise_id` INTEGER NOT NULL,
                `language` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `rule` TEXT NOT NULL,
                PRIMARY KEY(`exercise_id`, `language`)
            )
        """)

            db.execSQL("""
            INSERT INTO exercise_translations_new (exercise_id, language, name, description, rule)
            SELECT exercise_id, language, name, description, rule
            FROM exercise_translations
        """)

            // Удаляем старые таблицы
            db.execSQL("DROP TABLE exercise_translations")
            db.execSQL("DROP TABLE exercises")

            db.execSQL("""
            CREATE TABLE `exercises` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `icon` TEXT NOT NULL,
                `is_custom` INTEGER NOT NULL DEFAULT 0,
                `force_id` INTEGER,
                `level_id` INTEGER,
                `mechanic_id` INTEGER,
                `equipment_id` INTEGER,
                `category_id` INTEGER,
                FOREIGN KEY(`force_id`) REFERENCES `forces`(`id`) ON DELETE SET NULL,
                FOREIGN KEY(`level_id`) REFERENCES `levels`(`id`) ON DELETE SET NULL,
                FOREIGN KEY(`mechanic_id`) REFERENCES `mechanics`(`id`) ON DELETE SET NULL,
                FOREIGN KEY(`equipment_id`) REFERENCES `equipments`(`id`) ON DELETE SET NULL,
                FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON DELETE SET NULL
            )
        """)

            db.execSQL("INSERT INTO exercises SELECT * FROM exercises_new")
            db.execSQL("DROP TABLE exercises_new")

            db.execSQL("""
                CREATE TABLE `exercise_translations` (
                    `exercise_id` INTEGER NOT NULL,
                    `language` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `rule` TEXT NOT NULL,
                    PRIMARY KEY(`exercise_id`, `language`),
                    FOREIGN KEY(`exercise_id`) REFERENCES `exercises`(`id`) ON DELETE CASCADE
                )
            """)

            db.execSQL("INSERT INTO exercise_translations SELECT * FROM exercise_translations_new")
            db.execSQL("DROP TABLE exercise_translations_new")

            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_translations_exercise_id` ON `exercise_translations` (`exercise_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_translations_language` ON `exercise_translations` (`language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_translations_exercise_id_language` ON `exercise_translations` (`exercise_id`, `language`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_translations_name` ON `exercise_translations` (`name`)")

            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercises_force_id` ON `exercises` (`force_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercises_level_id` ON `exercises` (`level_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercises_mechanic_id` ON `exercises` (`mechanic_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercises_equipment_id` ON `exercises` (`equipment_id`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercises_category_id` ON `exercises` (`category_id`)")

            db.execSQL("PRAGMA foreign_keys=ON")
        }

        private fun migrateReferenceData(
            db: SupportSQLiteDatabase,
            columnName: String,
            baseTable: String,
            translationTable: String,
            foreignKeyColumn: String
        ) {
            val cursor = db.query("""
                SELECT exercise_id, $columnName, language
                FROM exercise_translations
                WHERE $columnName IS NOT NULL AND $columnName != ''
                ORDER BY exercise_id
            """)

            val valueToBaseId = mutableMapOf<Long, Long>()

            cursor.use {
                while (it.moveToNext()) {
                    val exerciseId = it.getLong(0)
                    val value = it.getString(1)
                    val language = it.getString(2)

                    val baseId = valueToBaseId.getOrPut(exerciseId) {
                        db.execSQL("INSERT INTO $baseTable DEFAULT VALUES")
                        db.query("SELECT last_insert_rowid()").use { c ->
                            c.moveToFirst()
                            c.getLong(0)
                        }
                    }

                    db.execSQL("""
                        INSERT OR IGNORE INTO $translationTable ($foreignKeyColumn, language, name)
                        VALUES (?, ?, ?)
                    """, arrayOf(baseId, language, value))
                }
            }
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                UPDATE data_training SET state = 1
                """.trimIndent())
        }
    }

}