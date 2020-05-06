package github.informramiz.progressbarbutton.view.main

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import github.informramiz.progressbarbutton.model.DownloadStatus
import github.informramiz.progressbarbutton.model.GitHubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

private enum class Repo(val owner: String, val repoName: String, val outputFileName: String) {
    GLIDE("bumptech", "glide", "glide.zip"),
    THIS_PROJECT("udacity", "nd940-c3-advanced-android-programming-project-starter", "nd940.zip"),
    RETROFIT("square", "retrofit", "retrofit.zip"),
    UNKNOWN("", "", "");

    val url: String
        get() {
            return "https://github.com/$owner/$repoName/archive/master.zip"
        }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context = application

    private val _downloadStatus = MutableLiveData<DownloadStatus>()
    val downloadStatus: LiveData<DownloadStatus>
        get() = _downloadStatus

    private var selectedRepo = Repo.UNKNOWN

    fun downloadRepo() {
        if (selectedRepo == Repo.UNKNOWN) {
            return
        }
        viewModelScope.launch {
            GitHubRepository.downloadRepoManually(context, selectedRepo.url, selectedRepo.outputFileName)
                .flowOn(Dispatchers.IO)
                .collect { status ->
                    _downloadStatus.value = status
                }
        }
    }

    fun onGlideClick() {
        selectedRepo = Repo.GLIDE
    }

    fun onThisProjectClick() {
        selectedRepo = Repo.THIS_PROJECT
    }

    fun onRetrofitClick() {
        selectedRepo = Repo.RETROFIT
    }
}
