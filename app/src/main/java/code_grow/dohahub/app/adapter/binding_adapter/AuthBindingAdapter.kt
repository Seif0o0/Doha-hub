package code_grow.dohahub.app.adapter.binding_adapter


import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import code_grow.dohahub.app.R
import code_grow.dohahub.app.retrofit.Resource
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("error_message")
fun TextInputEditText.setInputErrorMessage(errorMessage: String) {
    if (errorMessage.isNotEmpty())
        error = errorMessage
}


@BindingAdapter("progress_state")
fun SpinKitView.setProgressVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Loading -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("user_type")
fun CardView.setUserTyp(isProvider: Boolean) {
    if (isProvider) {
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.auth_yellow_color))
        cardElevation = resources.getDimension(com.intuit.sdp.R.dimen._2sdp)
    } else {
        setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        cardElevation = 0f
    }
}

@BindingAdapter("user_type")
fun TextView.setUserType(isProvider: Boolean) {
    if (isProvider)
        setTextColor(ContextCompat.getColor(context, R.color.auth_main_background))
    else
        setTextColor(ContextCompat.getColor(context, R.color.auth_subtitle_text_color))
}
