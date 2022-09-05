package io.github.mumu12641.lark.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.BaseApplication.Companion.kv
import io.github.mumu12641.lark.entity.Converters
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList

@Database(
    entities = [SongList::class, Song::class, PlaylistSongCrossRef::class],
    version = 22,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MusicDataBase : RoomDatabase() {
    abstract val musicDao: MusicDao

    companion object {
        private val musicDataBase =
            Room.databaseBuilder(context, MusicDataBase::class.java, "MusicDataBase")
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()

        @Synchronized
        fun getInstance(): MusicDataBase {
            return musicDataBase
        }
    }

    class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) = db.run {
            beginTransaction()
            try {
                if (kv.decodeInt("first") <= 1) {
                    kv.encode("first", 2)
                    execSQL("INSERT INTO SongList (songListId,songListTitle,createDate,songNumber,description,imageFileUri,type) VALUES(1,'Local','2022/7/14',0,'Local Music','Local Image',0);")
                    execSQL("INSERT INTO SongList (songListId,songListTitle,createDate,songNumber,description,imageFileUri,type) VALUES(2,'Favorite Songs','2022/7/14',0,'Favorite','Like Image',1);")
                    execSQL("INSERT INTO SongList (songListId,songListTitle,createDate,songNumber,description,imageFileUri,type) VALUES(3,'History','2022/7/14',0,'History Music','History Image',0);")
                    setTransactionSuccessful()
                }
            } finally {
                endTransaction()
            }
        }
    }
}