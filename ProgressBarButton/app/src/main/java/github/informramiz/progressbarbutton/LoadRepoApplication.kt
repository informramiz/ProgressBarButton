package github.informramiz.progressbarbutton

import android.app.Application
import timber.log.Timber

/**
 * Created by Ramiz Raja on 06/05/2020
 */
class LoadRepoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}