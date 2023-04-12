package com.sampietro.NaiveAAC.activities.Main;

import androidx.annotation.NonNull;

import java.util.Locale;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * <h1>customRealmMigration</h1>
 * <p><b>customRealmMigration</b> Realm Migration due to schema change
 * <p>
 * Refer to <a href="https://github.com/realm/realm-java/tree/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample">github</a>
 * commits by <a href="https://github.com/cmelchior">Christian Melchior</a>
 *
 * @see RealmMigration
 * @see MyApplication
 */
public class customRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            // Migrate from v0 to v1
            RealmObjectSchema storiesSchema = schema.get("Stories");
            assert storiesSchema != null;
            storiesSchema
                    .addField("answerActionType", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("answerActionType", " " );
                        }
                    })
            ;
            oldVersion++;
        }
        //
        if (oldVersion == 1) {
            // Migrate from v1 to v2
            RealmObjectSchema storiesSchema = schema.get("Stories");
            assert storiesSchema != null;
            storiesSchema
                    .addField("answerAction", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("answerAction", " " );
                        }
                    })
            ;
            oldVersion++;
        }
        //
        if (oldVersion == 2) {
            // Migrate from v2 to v3
            RealmObjectSchema gameParametersSchema = schema.get("GameParameters");
            assert gameParametersSchema != null;
            gameParametersSchema
                    .addField("gameActive", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("gameActive", "A" );
                        }
                    })
            ;
            oldVersion++;
        }
        //
        if (oldVersion == 3) {
            // Migrate from v3 to v4
            RealmObjectSchema storiesSchema = schema.get("Stories");
            assert storiesSchema != null;
            storiesSchema
                    .addField("phraseNumberInt", int.class)
                    .addField("wordNumberInt", int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.setInt("phraseNumberInt", Integer.parseInt(obj.getString("phraseNumber")));
                            obj.setInt("wordNumberInt", Integer.parseInt(obj.getString("wordNumber")));
                        }
                    })
                    .removeField("phraseNumber")
                    .removeField("wordNumber")
            ;
            oldVersion++;
        }
        //
        if (oldVersion == 4) {
            // Migrate from v4 to v5
            RealmObjectSchema soundsSchema = schema.create("Sounds");
            assert soundsSchema != null;
            soundsSchema
                    .addField("descrizione", String.class)
                    .addField("uri", String.class)
                    .addField("copyright", String.class)
                    .addField("fromAssets", String.class)
                    .addIndex("descrizione")
            ;
            RealmObjectSchema imagesSchema = schema.get("Images");
            assert imagesSchema != null;
            imagesSchema
                    .addField("copyright", String.class)
                    .addField("fromAssets", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("copyright", " " );
                            obj.set("fromAssets", " " );
                        }
                    })
                    .addIndex("descrizione")
            ;
            RealmObjectSchema videosSchema = schema.get("Videos");
            assert videosSchema != null;
            videosSchema
                    .addField("fromAssets", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("fromAssets", " " );
                        }
                    })
                    .addIndex("descrizione")
            ;
            RealmObjectSchema storiesSchema = schema.get("Stories");
            assert storiesSchema != null;
            storiesSchema
                    .addField("video", String.class)
                    .addField("sound", String.class)
                    .addField("soundReplacesTTS", String.class)
                    .addField("fromAssets", String.class)
                    .addField("wordNumberIntInTheStory", int.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("video", " " );
                            obj.set("sound", " " );
                            obj.set("soundReplacesTTS", "N" );
                            obj.set("fromAssets", " " );
                            obj.set("wordNumberIntInTheStory", 0 );
                        }
                    })
            ;
            RealmObjectSchema historySchema = schema.get("History");
            assert historySchema != null;
            historySchema
                    .addField("video", String.class)
                    .addField("sound", String.class)
                    .addField("soundReplacesTTS", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("video", " " );
                            obj.set("sound", " " );
                            obj.set("soundReplacesTTS", "N" );
                        }
                    })
            ;
            RealmObjectSchema gameParametersSchema = schema.get("GameParameters");
            assert gameParametersSchema != null;
            gameParametersSchema
                    .addField("gameUseVideoAndSound", String.class)
                    .addField("fromAssets", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("gameUseVideoAndSound", "Y" );
                            obj.set("fromAssets", " " );
                        }
                    })
            ;
            RealmObjectSchema grammaticalExceptionsSchema = schema.get("GrammaticalExceptions");
            assert grammaticalExceptionsSchema != null;
            grammaticalExceptionsSchema
                    .addField("fromAssets", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("fromAssets", " " );
                        }
                    })
            ;
            RealmObjectSchema listsOfNamesSchema = schema.get("ListsOfNames");
            assert listsOfNamesSchema != null;
            listsOfNamesSchema
                    .addField("fromAssets", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("fromAssets", " " );
                        }
                    })
            ;
            RealmObjectSchema phrasesSchema = schema.get("Phrases");
            assert phrasesSchema != null;
            phrasesSchema
                    .addField("fromAssets", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("fromAssets", " " );
                        }
                    })
            ;
            RealmObjectSchema wordPairsSchema = schema.get("WordPairs");
            assert wordPairsSchema != null;
            wordPairsSchema
                    .addField("fromAssets", String.class)
                    .transform(new RealmObjectSchema.Function() {
                        @Override
                        public void apply(@NonNull DynamicRealmObject obj) {
                            obj.set("fromAssets", " " );
                        }
                    })
            ;
            oldVersion++;
        }
//
        if (oldVersion < newVersion) {
            throw new IllegalStateException(String.format(Locale.US, "Migration missing from v%d to v%d", oldVersion, newVersion));
        }
    }
}
