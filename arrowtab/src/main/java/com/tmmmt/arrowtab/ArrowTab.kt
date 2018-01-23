package com.tmmmt.arrowtab

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by Bhuvanesh BS on 23/1/18.
 * Created for TmmmT.
 */
class ArrowTab : LinearLayout, View.OnClickListener {

    private val minTabSize = 2
    private val maxTabSize = 3
    private var tabRadius = 2
    private var tabStroke = 1
    private var tabColorNormal = Color.parseColor("white")
    private var tabColorSelected = Color.parseColor("red")
    private var tabTextColorNormal = Color.parseColor("black")
    private var tabTextColorSelected = Color.parseColor("white")
    private val tabNormalBackground by lazy { createStroke(tabColorNormal) }
    private val tabSelectedBackground by lazy { createStroke(tabColorSelected) }
    private var tabTitles: Array<CharSequence>? = null
    private var selectedItem = -1 // fist item selected initially
    private var selectionListenerInterface: SelectionListener? = null
    private var selectionListenerFunction: ((which: Int) -> Unit)? = null

    private var tabSize = 2
        set(value) {
            field = when {
                value < minTabSize -> minTabSize
                value > maxTabSize -> maxTabSize
                else -> value
            }
        }

    constructor(context: Context) : super(context) {
        buildUi(context, null)
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        buildUi(context, attr)
    }

    constructor(context: Context, attr: AttributeSet, styleAttr: Int) : super(context, attr, styleAttr) {
        buildUi(context, attr)
    }

    private fun buildUi(context: Context, attr: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attr, R.styleable.ArrowTab, 0, 0)
        getValues(typedArray)

        for (i in 0 until tabSize) {
            val tvNormal = TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                text = tabTitles?.get(i)
                background = tabNormalBackground
                gravity = Gravity.CENTER
                setTextColor(tabTextColorNormal)
            }
            val tvSelected = TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                text = tabTitles?.get(i)
                background = tabSelectedBackground
                gravity = Gravity.CENTER
                setTextColor(tabTextColorSelected)
            }
            val frameLayout = FrameLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    if (i != 0) marginStart = toPx(2)
                }
                id = 102030 + i
                setOnClickListener(this@ArrowTab)
                addView(tvSelected)
                addView(tvNormal)
            }

            addView(frameLayout)
        }
    }

    private fun getValues(typedArray: TypedArray) {
        tabSize = typedArray.getInt(R.styleable.ArrowTab_tab_size, 2)
        tabColorNormal = typedArray.getColor(R.styleable.ArrowTab_tab_color_normal, Color.parseColor("white"))
        tabColorSelected = typedArray.getColor(R.styleable.ArrowTab_tab_color_selected, Color.parseColor("red"))
        tabTextColorNormal = typedArray.getColor(R.styleable.ArrowTab_tab_text_color_normal, Color.parseColor("black"))
        tabTextColorSelected = typedArray.getColor(R.styleable.ArrowTab_tab_text_color_selected, Color.parseColor("white"))
        try {
            tabTitles = typedArray.getTextArray(R.styleable.ArrowTab_tab_titles)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        typedArray.recycle()
    }

    private fun createStroke(backgroundColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            setStroke(toPx(tabStroke), tabColorSelected)
            cornerRadius = toPx(tabRadius).toFloat()
        }
    }

    override fun onClick(v: View) {
        changeSelection(selectedItem, v.id)
    }

    private fun changeSelection(lastSelection: Int, currentSelection: Int) {
        var direction = Direction.LEFT
        if (lastSelection == currentSelection) return
        when {
            currentSelection == lastSelection + 2 -> direction = Direction.RIGHT
            currentSelection == lastSelection - 2 -> direction = Direction.LEFT
            lastSelection < currentSelection -> direction = Direction.LEFT
            lastSelection > currentSelection -> direction = Direction.RIGHT
        }

        val lastItem = findViewById<FrameLayout>(lastSelection)
        val currentItem = findViewById<FrameLayout>(currentSelection)

        lastItem?.getChildAt(0)?.apply {
            bringToFront()
            circleReveal(direction, duration = 500)
        }
        currentItem?.getChildAt(0)?.apply {
            bringToFront()
            circleReveal(direction, duration = 500)
        }
        selectedItem = currentSelection
        val selectedTab = getSelectedTabPosition(currentSelection)
        selectionListenerInterface?.onTabSelected(selectedTab)
        selectionListenerFunction?.invoke(selectedTab)
    }

    private fun getSelectedTabPosition(id: Int): Int {
        return (0 until childCount).firstOrNull { getChildAt(it).id == id } ?: 0
    }

    fun setSelection(position: Int) {
        if (position < tabSize) getChildAt(position).performClick()
    }

    fun setSelectionListener(selectionListener: SelectionListener) {
        this.selectionListenerInterface = selectionListener
    }

    fun setSelectionListener(selectionListener: (which: Int) -> Unit) {
        this.selectionListenerFunction = selectionListener
    }

    @SuppressLint("NewApi")
    private fun View.circleReveal(direction: Direction = Direction.CENTER, duration: Long = -1) {
        var cx = width / 2
        var cy = height / 2
        var finalRadius = Math.hypot(width.toDouble(), height.toDouble()).toFloat()

        this.visibility = View.INVISIBLE
        when (direction) {
            Direction.CENTER -> {
                finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            }
            Direction.RIGHT -> {
                cx = width; cy = height / 2
            }
            Direction.BOTTOM_RIGHT -> {
                cx = width; cy = height
            }
            Direction.BOTTOM -> {
                cx = width / 2; cy = bottom
            }
            Direction.BOTTOM_LEFT -> {
                cx = 0; cy = bottom
            }
            Direction.LEFT -> {
                cx = 0; cy = height / 2
            }
            Direction.TOP_LEFT -> {
                cx = 0; cy = 0
            }
            Direction.TOP -> {
                cx = width / 2; cy = 0
            }
            Direction.TOP_RIGHT -> {
                cx = width; cy = 0
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy, 0f, finalRadius)
                if (duration != (-1).toLong()) anim.duration = duration
                this.visibility = View.VISIBLE
                anim.start()
            } else
                this.visibility = View.VISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    private fun toDp(px: Int): Int = (px / Resources.getSystem().displayMetrics.density).toInt()

    enum class Direction {
        TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT, TOP_LEFT, CENTER
    }

    interface SelectionListener {
        fun onTabSelected(which: Int)
    }
}