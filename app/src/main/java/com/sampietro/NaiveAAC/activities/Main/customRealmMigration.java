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
 * @see io.realm.RealmMigration
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

        if (oldVersion < newVersion) {
            throw new IllegalStateException(String.format(Locale.US, "Migration missing from v%d to v%d", oldVersion, newVersion));
        }
    }
}
