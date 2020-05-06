package github.informramiz.progressbarbutton.view.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.informramiz.progressbarbutton.model.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    fun downloadGlide() {
        viewModelScope.launch {
            GitHubRepository.getGlide(getApplication())
                .flowOn(Dispatchers.IO)
                .collect {

                }
        }
    }
}
