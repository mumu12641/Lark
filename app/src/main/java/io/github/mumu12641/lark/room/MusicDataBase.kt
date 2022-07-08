package io.github.mumu12641.lark.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.mumu12641.lark.BaseApplication.Companion.context
import io.github.mumu12641.lark.entity.PlaylistSongCrossRef
import io.github.mumu12641.lark.entity.Song
import io.github.mumu12641.lark.entity.SongList

@Database(
    entities = [SongList::class, Song::class, PlaylistSongCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDataBase : RoomDatabase() {
    abstract val musicDao: MusicDao

    companion object {
        private val musicDataBase =
            Room.databaseBuilder(context, MusicDataBase::class.java, "MusicDataBase")
                .fallbackToDestructiveMigration()
                .build()
        @Synchronized
        fun getInstance(): MusicDataBase {
            return musicDataBase
        }
    }
}