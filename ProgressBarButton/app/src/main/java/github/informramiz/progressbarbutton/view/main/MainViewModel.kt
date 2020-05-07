package github.informramiz.progressbarbutton.view.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import github.informramiz.progressbarbutton.R
import github.informramiz.progressbarbutton.model.DownloadStatus
import github.informramiz.progressbarbutton.model.GitHubRepository
import github.informramiz.progressbarbutton.view.notifications.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

private enum class Repo(val owner: String, val repoName: String, val outputFileName: String) {
    GLIDE("bumptech", "Glide", "glide.zip"),
    THIS_PROJECT("udacity", "nd940-c3-advanced-android-programming-project-starter", "nd940.zip"),
    RETROFIT("square", "Retrofit", "retrofit.zip"),
    UNKNOWN("", "", "");

    val url: String
        get() {
            return "https://github.com/$owner/$repoName/archive/master.zip"
        }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()

    private val _downloadStatus = MutableLiveData<DownloadStatus>()
    val downloadStatus: LiveData<DownloadStatus>
        get() = _downloadStatus

    private var selectedRepo = Repo.UNKNOWN

    fun downloadRepo() {
        if (selectedRepo == Repo.UNKNOWN) {
            _downloadStatus.value =
                DownloadStatus.DownloadFailed(Exception(context.getString(R.string.msg_no_repo_selected)))
            return
        }
        viewModelScope.launch {
            GitHubRepository.downloadRepo(
                context,
                selectedRepo.owner,
                selectedRepo.repoName,
                selectedRepo.outputFileName
            )
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

    fun onDownloadComplete() {
        val repoName =
            if (selectedRepo == Repo.THIS_PROJECT) context.getString(R.string.download_current_repo) else selectedRepo.repoName
        NotificationUtils.sendRepoDownloadedNotification(context, repoName)
    }
}
