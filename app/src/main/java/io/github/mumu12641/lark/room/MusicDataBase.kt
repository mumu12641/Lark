package io.github.mumu12641.lark.room

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tencent.mmkv.MMKV
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList

@Database(
    entities = [SongList::class, Song::class, PlaylistSongCrossRef::class],
    version = 12,
    exportSchema = true
)
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
                if (MMKV.defaultMMKV().decodeBool("First")) {
                    MMKV.defaultMMKV().encode("First",false)
                    execSQL("INSERT INTO SongList (songListId,songListTitle,createDate,songNumber,description,imageFileUri) VALUES(1,'Local','2022/7/14',0,'Local Music','Local Image');")
                    execSQL("INSERT INTO SongList (songListId,songListTitle,createDate,songNumber,description,imageFileUri) VALUES(2,'I like','2022/7/14',0,'Like Music','Like Image');")
                    execSQL("INSERT INTO SongList (songListId,songListTitle,createDate,songNumber,description,imageFileUri) VALUES(3,'History','2022/7/14',0,'History Music','History Image');")
                    setTransactionSuccessful()
                }
            } finally {
                endTransaction()
            }
        }
    }
}