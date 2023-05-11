package code_grow.dohahub.app.kot_pref

import com.chibatching.kotpref.KotprefModel


object UserInfo : KotprefModel() {
    var userId by intPref(-1)
    var isSigned by booleanPref(false)
    var username by stringPref("")
    var brief by stringPref("")
    var description by stringPref("")
    var password by stringPref("")
    var email by stringPref("")
    var phoneNumber by stringPref("")
    var profilePicture by stringPref("")
    var facebookLink by stringPref("")
    var instagramLink by stringPref("")
    var youtubeLink by stringPref("")
    var behanceLink by stringPref("")
    var isProvider by booleanPref(false)
    var isVerified by booleanPref(false)
    var firebaseToken by stringPref("")

}