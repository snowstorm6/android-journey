package com.example.epoxyrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp

/**
 * Created by NGUYEN VAN SON on 26/05/2021.
 * sonnv@onemount.com
 */

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class UserLayoutView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val rootLayout: ConstraintLayout
    private val tvName: TextView
    private val tvEmail: TextView
    private val tvAge: TextView

    init {
        View.inflate(context, R.layout.user_layout_view, this)
        rootLayout = findViewById(R.id.itemLayout)
        tvName = findViewById(R.id.nameTextView)
        tvEmail = findViewById(R.id.emailTextView)
        tvAge = findViewById(R.id.ageTextView)
    }

    @TextProp
    fun setName(name: CharSequence) {
        tvName.text = name
    }

    @TextProp
    fun setEmail(email: CharSequence) {
        tvEmail.text = email
    }

    @TextProp
    fun setAge(age: CharSequence) {
        tvAge.text = age
    }

    @ModelProp
    fun setBackground(background: Int) {
        rootLayout.setBackgroundColor(background)
    }

    @CallbackProp
    fun setItemClickListener(listener: OnClickListener?) {
        rootLayout.setOnClickListener(listener)
    }
}