package com.dtx804lab.brelax

import android.app.Application
import androidx.room.Room
import com.dtx804lab.brelax.activity.DiaryPage
import com.dtx804lab.brelax.activity.GamePage
import com.dtx804lab.brelax.activity.PageDestination
import com.dtx804lab.brelax.database.BrelaxDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

class BrelaxApplication : Application() {

    companion object {
        private const val DATABASE_NAME = "Guest"

        const val DAIRY_PAGE = "Dairy Page"
        const val GAME_PAGE = "Game Page"
    }

    private val pageModule = module {
        single(named(DAIRY_PAGE)) {
            DiaryPage()
        } bind PageDestination::class
        single(named(GAME_PAGE)) {
            GamePage()
        } bind PageDestination::class
    }
    private val databaseModule = module {
        single(createdAtStart = true) {
            Room.databaseBuilder(
                androidApplication(),
                BrelaxDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
        single {
            get<BrelaxDatabase>().dairyDao()
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BrelaxApplication)
            modules(pageModule, databaseModule)
        }
    }

}