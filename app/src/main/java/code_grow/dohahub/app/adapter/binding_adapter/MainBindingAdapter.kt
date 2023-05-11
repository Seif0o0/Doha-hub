package code_grow.dohahub.app.adapter.binding_adapter

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import code_grow.dohahub.app.R
import code_grow.dohahub.app.kot_pref.UserInfo
import code_grow.dohahub.app.retrofit.Resource
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.makeramen.roundedimageview.RoundedImageView


@BindingAdapter("rounded_image_url")
fun RoundedImageView.loadImage(url: String) {
    if (url == "logo")
        setImageResource(R.mipmap.ic_launcher)
    else
        Glide.with(this)
            .load(url)
            .into(this)
}

@BindingAdapter("image_url")
fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .into(this)
}

@BindingAdapter("disable_click")
fun ImageView.disableClicks(state: Resource) {
    isClickable = when (state) {
        is Resource.Loading -> false
        else -> true
    }
}

@BindingAdapter("progress_state")
fun FrameLayout.setProgressVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Loading -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("empty_list_state")
fun TextView.setEmptyListTextVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Empty -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("empty_list_state")
fun RecyclerView.setEmptyListVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Success<*> -> View.VISIBLE
        else -> View.INVISIBLE
    }
}

@BindingAdapter("failed_state")
fun ConstraintLayout.setErrorLayoutVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Failed -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("play_animation")
fun LottieAnimationView.startAnimation(errorMessage: String?) {
    if (errorMessage == null)
        return
    setAnimation(
        when (errorMessage) {
            context.getString(R.string.no_internet_connection) -> "no_internet_connection.json"
            else -> "error_dialog_animation.json"
        }
    )
    playAnimation()
    repeatCount = LottieDrawable.INFINITE
}

@BindingAdapter("cancel_animation")
fun LottieAnimationView.cancelAnimation(boolean: Boolean) {
    if (boolean)
        cancelAnimation()
}

@BindingAdapter("is_category_clicked")
fun CardView.productCategoryClicked(isClicked: Boolean) {
    setCardBackgroundColor(
        ContextCompat.getColor(
            context,
            if (isClicked) R.color.market_clicked_items_background else R.color.market_items_background
        )
    )
}

@BindingAdapter("is_service_selected")
fun ImageView.serviceSelected(isSelected: Boolean) {
    setImageResource(if (isSelected) R.drawable.ic_checked_service else R.drawable.ic_unchecked_service)
}

@BindingAdapter("is_category_clicked")
fun TextView.productCategoryClicked(isClicked: Boolean) {
    setTextColor(
        ContextCompat.getColor(
            context,
            if (isClicked) R.color.market_clicked_category_text_color else R.color.market_category_text_color
        )
    )
}

@BindingAdapter("formatted_price")
fun TextView.setFormattedPrice(price: Double) {
    this.text = String.format(context.getString(R.string.price_with_currency), price.toString())
}

@BindingAdapter("formatted_date")
fun TextView.setFormattedDate(date: String) {
    if (date.isEmpty())
        return
    if (!date.contains("-")) {
        text = date
        return
    }

    val splitDate = date.split("-")
    text = splitDate[2].plus("/").plus(splitDate[1]).plus("/").plus(splitDate[0])
}

@BindingAdapter("upload_image_state")
fun Group.setReportVisibility(state: Resource) {
    visibility = when (state) {
        is Resource.Success<*>, Resource.Loading -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("error_message")
fun EditText.setLoginError(errorMessage: String) {
    if (errorMessage.isNotEmpty())
        error = errorMessage
}

@BindingAdapter("first_progress_state", "second_progress_state", "third_progress_state")
fun FrameLayout.setThreeProgressVisibility(state1: Resource, state2: Resource, state3: Resource) {
    visibility =
        if (state1 is Resource.Loading || state2 is Resource.Loading || state3 is Resource.Loading) View.VISIBLE
        else View.GONE
}

@BindingAdapter("my_order_status")
fun MaterialButton.orderSubmitBtnStatus(status: Int) {
    /*0 => waiting for approve . also customer can delete the order in this case .
        1 => Accepted . he can start sending message . he can make finish to the order .
        2= > order finished , he can make a rate .*/
    when (status) {
        0 -> {
            text = context.getString(R.string.delete_my_order)
            backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.orders_delete_button_background)
        }
        1 -> {
            text = context.getString(R.string.finish_order)
            backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.orders_yellow_color)
        }
        2 -> {
            text = context.getString(R.string.submit_feedback)
            backgroundTintList =
                ContextCompat.getColorStateList(context, R.color.orders_yellow_color)
        }
    }
}

@BindingAdapter("order_status")
fun Group.orderSendMessageStatus(status: Int) {
    visibility = if (status == 1) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("order_status_feedback")
fun Group.orderFeedbackStatus(status: Int) {
    visibility = if (status == 2) View.VISIBLE else View.GONE
}


@BindingAdapter("client_order_status")
fun TextView.orderStatusText(status: Int) {
    /*0 => waiting for your approve . ( he will make confirm )
    1 => Confirmed , he can start sending message
    2 => Finished*/
    text =
        when (status) {
            0 -> context.getString(R.string.order_status_pending)
            1 -> context.getString(R.string.order_status_confirmed)
            else -> context.getString(R.string.order_status_finished)
        }
}

@BindingAdapter("client_order_status")
fun MaterialButton.clientOrderSubmitBtnStatus(status: Int) {
    /*0 => waiting for your approve . ( he will make confirm )
    1 => Confirmed , he can start sending message
    2 => Finished*/
    when (status) {
        0 -> {
            visibility = View.VISIBLE
            text = context.getText(R.string.accept_order)
        }
        1 ->
            visibility = View.GONE
        2 -> {
            visibility = View.VISIBLE
            text = context.getText(R.string.submit_feedback)
        }
    }
}

@BindingAdapter("auto_complete_listener")
fun AutoCompleteTextView.removeError(emptyData: Boolean) {
    onItemClickListener =
        AdapterView.OnItemClickListener { _, _, _, _ ->
            error = null
        }
}

@BindingAdapter("suggested_price", "price", "campaign_status")
fun TextView.campaignPrice(suggestedPrice: String, price: String, campaignStatus: Int) {
    this.setFormattedPrice(
        if (campaignStatus == 0) {
            if (suggestedPrice.isEmpty()) 0.0 else suggestedPrice.toDouble()
        } else {
            if (price.isEmpty()) 0.0 else price.toDouble()
        }
    )
}

@BindingAdapter("campaign_status", "send_offer_button", "campaign_user_id")
fun MaterialButton.sendCampaignBtnsVisibility(
    campaignStatus: Int,
    isSendBtn: Boolean,
    campaignUserId: Int
) {
    visibility = if (campaignStatus == 0) {
        if (isSendBtn) {
            if (campaignUserId == UserInfo.userId) View.INVISIBLE else View.VISIBLE
        } else View.INVISIBLE
    } else {
        if (isSendBtn) View.INVISIBLE else View.VISIBLE
    }
}

@BindingAdapter("campaign_status", "edit_campaign_button")
fun MaterialButton.editCampaignsBtnsVisibility(campaignStatus: Int, isEditBtn: Boolean) {
    visibility = if (campaignStatus == 0) {
        if (isEditBtn) View.VISIBLE else View.GONE
    } else {
        if (isEditBtn) View.GONE else View.VISIBLE
    }
}

@BindingAdapter("campaign_status")
fun FrameLayout.campaignEditable(campaignStatus: Int) {
    visibility = if (campaignStatus == 0) View.GONE else View.VISIBLE
}

@BindingAdapter("web_view", "article_body")
fun TextView.setBody(isWebView: Boolean, body: String) {
    text = if (isWebView)
        HtmlCompat.fromHtml(body, HtmlCompat.FROM_HTML_MODE_LEGACY)
    else body
}





