package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.CampaignsRepository

class CampaignsViewModelFactory(
    private val isMyCampaigns: Boolean,
    val application: Application,
    val repo: CampaignsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampaignsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampaignsViewModel(isMyCampaigns, application, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}