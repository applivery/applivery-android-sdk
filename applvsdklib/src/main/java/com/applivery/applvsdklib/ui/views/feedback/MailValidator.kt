package com.applivery.applvsdklib.ui.views.feedback

import java.util.regex.Pattern

class MailValidator {

    private val mailAddressPattern: Pattern = Pattern.compile(
        """[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+"""
    )

    fun isValid(mail: String): Boolean {
        return mailAddressPattern.matcher(mail).matches()
    }
}
