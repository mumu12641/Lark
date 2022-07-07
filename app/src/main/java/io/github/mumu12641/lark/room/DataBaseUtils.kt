package io.github.mumu12641.lark.room

class DataBaseUtils {
    companion object{
        private val DataBase : MusicDataBase = MusicDataBase.getInstance()
        private val musicDao : MusicDao = DataBase.musicDao
    }
}