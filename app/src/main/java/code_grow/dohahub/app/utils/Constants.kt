package code_grow.dohahub.app.utils

object Constants {
    const val BASE_URL = "https://www.dohahub.com/"

    const val GLOBAL = "global/"
    const val PRODUCT = "productapi/"
    const val ARTICLE = "Blogapi/"
    const val SERVICE = "servicesapi/"
    const val CAMPAIGN = "Campaignapi/"
    const val CHAT = "Messageapi/"


    /* global */
    const val LOGIN = "login"
    const val REGISTER = "signup"
    const val SOCIAL_CHECK = "check_input"
    const val UPDATE_TOKEN = "update_token"
    const val FORGET_PASSWORD = "forget_password" // send code
    const val CHANGE_PASSWORD = "change_password"
    const val EDIT_PROFILE = "edit"
    const val UPLOAD_IMAGE = "upload_image_api"
    const val UPLOAD_USER_IMAGE = "upload_user_image_api"
    const val CITIES = "cities"
    const val CATEGORIES = "get_categories"
    const val HOME_DATA = "home_data"


    /* product */
    const val PRODUCTS = "get_products"
    const val PRODUCT_CATEGORIES = "get_store_category"
    const val ADD_PRODUCT = "add_product"
    const val DELETE_PRODUCT = "delete_product"
    const val EDIT_PRODUCT = "edit_product"
    const val UPLOAD_PRODUCT_IMAGE = "upload_image_api"

    /* article */
    const val ARTICLES = "get_blogs"
    const val UPLOAD_ARTICLE_IMAGE = "upload_image_api"
    const val ADD_ARTICLE = "add_blog"
    const val EDIT_ARTICLE = "edit_blog"
    const val DELETE_ARTICLE = "delete_blog"
    const val COMMENTS = "get_comments"
    const val ADD_COMMENT = "add_comment"

    /* service */
    const val PROVIDER_DETAILS = "get_provider_info"
    const val PROVIDERS = "get_services"
    const val SERVICE_CHECKOUT = "add_checkout"
    const val ORDERS = "orders"
    const val ACCEPT_ORDER = "accept_order"
    const val FINISH_ORDER = "finish_order"
    const val DELETE_ORDER = "delete_my_order"
    const val PROVIDER_FEEDBACK = "provider_feedback"
    const val CUSTOMER_FEEDBACK = "customer_feedback"
    const val ADD_SERVICE = "add_service"
    const val EDIT_SERVICE = "edit_service"
    const val REMOVE_SERVICE = "remove_service"

    /* chat */
    const val GET_MESSAGES = "get_messages"
    const val SEND_MESSAGE = "send_message"

    /* campaign */
    const val CAMPAIGNS = "get_campaigns"
    const val CAMPAIGN_CATEGORIES = "get_categories"
    const val UPLOAD_CAMPAIGN_PHOTO = "upload_image_api"
    const val ADD_CAMPAIGN = "add_campaign"
    const val EDIT_CAMPAIGN = "edit_campaign"
    const val DELETE_CAMPAIGN = "delete_campaign"
    const val GET_OFFERS = "get_campaign_offers"
    const val SEND_OFFER = "submit_offer"
    const val ACCEPT_OFFER = "accept_offer"

    const val TERMS_CONDITIONS = "${BASE_URL}homes/terms"
    const val PRIVACY_POLICY = "${BASE_URL}homes/privacy"
}