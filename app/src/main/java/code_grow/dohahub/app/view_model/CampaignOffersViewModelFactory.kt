package code_grow.dohahub.app.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code_grow.dohahub.app.repository.CampaignsRepository

class CampaignOffersViewModelFactory(
    private val campaignId: Int,
    private val application: Application,
    private val repo: CampaignsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampaignOffersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampaignOffersViewModel(campaignId, application, repo) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}