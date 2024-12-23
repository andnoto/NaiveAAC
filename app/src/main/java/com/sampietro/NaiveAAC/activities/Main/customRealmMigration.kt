package com.sampietro.NaiveAAC.activities.Main

import io.realm.DynamicRealm
import io.realm.RealmMigration
import java.util.Locale

/**
 * <h1>customRealmMigration</h1>
 *
 * **customRealmMigration** Realm Migration due to schema change
 *
 *
 * Refer to [github](https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample)
 * commits by [Christian Melchior](https://github.com/cmelchior)
 *
 * @see RealmMigration
 *
 * @see MyApplication
 */
class customRealmMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion
        val schema = realm.schema
        if (oldVersion == 0L) {
            // Migrate from v0 to v1
            val storiesSchema = schema["Stories"]!!
            storiesSchema
                .addField("answerActionType", String::class.java)
                .transform { obj -> obj["answerActionType"] = " " }
            oldVersion++
        }
        //
        if (oldVersion == 1L) {
            // Migrate from v1 to v2
            val storiesSchema = schema["Stories"]!!
            storiesSchema
                .addField("answerAction", String::class.java)
                .transform { obj -> obj["answerAction"] = " " }
            oldVersion++
        }
        //
        if (oldVersion == 2L) {
            // Migrate from v2 to v3
            val gameParametersSchema = schema["GameParameters"]!!
            gameParametersSchema
                .addField("gameActive", String::class.java)
                .transform { obj -> obj["gameActive"] = "A" }
            oldVersion++
        }
        //
        if (oldVersion == 3L) {
            // Migrate from v3 to v4
            val storiesSchema = schema["Stories"]!!
            storiesSchema
                .addField("phraseNumberInt", Int::class.javaPrimitiveType)
                .addField("wordNumberInt", Int::class.javaPrimitiveType)
                .transform { obj ->
                    obj.setInt("phraseNumberInt", obj.getString("phraseNumber").toInt())
                    obj.setInt("wordNumberInt", obj.getString("wordNumber").toInt())
                }
                .removeField("phraseNumber")
                .removeField("wordNumber")
            oldVersion++
        }
        //
        if (oldVersion == 4L) {
            // Migrate from v4 to v5
            val soundsSchema = schema.create("Sounds")!!
            soundsSchema
                .addField("descrizione", String::class.java)
                .addField("uri", String::class.java)
                .addField("copyright", String::class.java)
                .addField("fromAssets", String::class.java)
                .addIndex("descrizione")
            val imagesSchema = schema["Images"]!!
            imagesSchema
                .addField("copyright", String::class.java)
                .addField("fromAssets", String::class.java)
                .transform { obj ->
                    obj["copyright"] = " "
                    obj["fromAssets"] = "Y"
                }
                .addIndex("descrizione")
            val videosSchema = schema["Videos"]!!
            videosSchema
                .addField("fromAssets", String::class.java)
                .transform { obj -> obj["fromAssets"] = "Y" }
                .addIndex("descrizione")
            val storiesSchema = schema["Stories"]!!
            storiesSchema
                .addField("video", String::class.java)
                .addField("sound", String::class.java)
                .addField("soundReplacesTTS", String::class.java)
                .addField("fromAssets", String::class.java)
                .addField("wordNumberIntInTheStory", Int::class.javaPrimitiveType)
                .transform { obj ->
                    obj["video"] = " "
                    obj["sound"] = " "
                    obj["soundReplacesTTS"] = "N"
                    obj["fromAssets"] = "Y"
                    obj["wordNumberIntInTheStory"] = 0
                }
            val historySchema = schema["History"]!!
            historySchema
                .addField("video", String::class.java)
                .addField("sound", String::class.java)
                .addField("soundReplacesTTS", String::class.java)
                .transform { obj ->
                    obj["video"] = " "
                    obj["sound"] = " "
                    obj["soundReplacesTTS"] = "N"
                }
            val gameParametersSchema = schema["GameParameters"]!!
            gameParametersSchema
                .addField("gameUseVideoAndSound", String::class.java)
                .addField("fromAssets", String::class.java)
                .transform { obj ->
                    obj["gameUseVideoAndSound"] = "Y"
                    obj["fromAssets"] = "Y"
                }
            val grammaticalExceptionsSchema = schema["GrammaticalExceptions"]!!
            grammaticalExceptionsSchema
                .addField("fromAssets", String::class.java)
                .transform { obj -> obj["fromAssets"] = "Y" }
            val listsOfNamesSchema = schema["ListsOfNames"]!!
            listsOfNamesSchema
                .addField("fromAssets", String::class.java)
                .transform { obj -> obj["fromAssets"] = "Y" }
            val phrasesSchema = schema["Phrases"]!!
            phrasesSchema
                .addField("fromAssets", String::class.java)
                .transform { obj -> obj["fromAssets"] = "Y" }
            val wordPairsSchema = schema["WordPairs"]!!
            wordPairsSchema
                .addField("fromAssets", String::class.java)
                .transform { obj -> obj["fromAssets"] = "Y" }
            oldVersion++
        }
        //
        if (oldVersion == 5L) {
            // Migrate from v5 to v6
            val bluetoothDevicesSchema = schema.create("BluetoothDevices")!!
            bluetoothDevicesSchema
                .addField("deviceUserName", String::class.java)
                .addField("deviceName", String::class.java)
                .addField("fromAssets", String::class.java)
            oldVersion++
        }
        //
        if (oldVersion == 6L) {
            // Migrate from v6 to v7
            val listsOfNamesSchema = schema["ListsOfNames"]!!
            listsOfNamesSchema
                .removeField("uriType")
                .removeField("uri")
                .addField("elementActive", String::class.java)
                .addField("isMenuItem", String::class.java)
                .transform { obj ->
                    obj["elementActive"] = "A"
                    obj["isMenuItem"] = "N"
                }
            val wordPairsSchema = schema["WordPairs"]!!
            wordPairsSchema
                .removeField("isMenuItem")
            oldVersion++
        }
        //
        if (oldVersion == 7L) {
            // Migrate from v7 to v8
            val listsOfNamesSchema = schema["ListsOfNames"]!!
            listsOfNamesSchema
                .addField("uriType", String::class.java)
                .addField("uri", String::class.java)
                .transform { obj ->
                    obj["uriType"] = " "
                    obj["uri"] = " "
                }
            oldVersion++
        }
        //
        check(oldVersion >= newVersion) {
            String.format(
                Locale.US,
                "Migration missing from v%d to v%d",
                oldVersion,
                newVersion
            )
        }
    }
}