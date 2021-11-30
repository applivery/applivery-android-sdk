package com.applivery.applvsdklib.ui.views

import android.text.Editable
import android.text.TextWatcher

class TextChangedListener(
    val onTextChanged: (String) -> Unit
) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(s: Editable?) {
        onTextChanged(s?.toString().orEmpty())
    }
}
