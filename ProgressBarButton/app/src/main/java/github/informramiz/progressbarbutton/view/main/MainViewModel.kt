package github.informramiz.progressbarbutton.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.informramiz.progressbarbutton.model.GitHubRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    fun downloadGlide() {
        viewModelScope.launch {
            GitHubRepository.getGlide()
        }
    }
}
