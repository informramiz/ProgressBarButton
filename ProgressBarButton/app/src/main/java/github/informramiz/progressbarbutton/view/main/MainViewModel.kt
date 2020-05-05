package github.informramiz.progressbarbutton.view.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.informramiz.progressbarbutton.model.GitHubRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    fun downloadGlide() {
        viewModelScope.launch {
            GitHubRepository.getGlide(getApplication())
        }
    }
}
