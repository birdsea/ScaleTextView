package io.github.birdsea

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class ScaleTextView : HorizontalScrollView, View.OnTouchListener,
    ScaleGestureDetector.OnScaleGestureListener {

    // 拡大・縮小範囲
    var maxScale: Float = 2.0f
    var minScale: Float = 1.0f

    // 現在の拡大率
    var nowScale: Float = 1.0f

    // テキスト
    var text: String
        set(text: String) {
            textView.text = text
        }
        get() :String {
            return textView.text.toString()
        }

    private val scaleGesture: ScaleGestureDetector = ScaleGestureDetector(context, this)
    private val scrollView: ScrollView = ScrollView(context)
    private val textView: TextView = TextView(context)
    private var initializeFlg = false
    private var nowScrollX: Float = 0.0f
    private var nowScrollY: Float = 0.0f
    private var defaultWidth: Int = 0
    private var defaultRight: Int = 0
    private var defaultTopText: Int = 0
    private var defaultBottomText: Int = 0
    private var defaultLeftText: Int = 0
    private var defaultRightText: Int = 0
    private var defaultHeightText: Int = 0
    private var defaultWidthText: Int = 0

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        this.addView(scrollView)
        scrollView.addView(textView)

        // ビューの幅・高さは外側のビューに合わせる
        scrollView.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        scrollView.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        textView.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        textView.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT

        // コンテンツの内容で幅が変わらないようにテキストエリアの幅を設定する
        val displayMetrics = resources.displayMetrics
        val pxWidth = Math.round(displayMetrics.widthPixels.toFloat())
        textView.maxWidth = pxWidth
        textView.minWidth = pxWidth

        setOnTouchListener(this)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val count = ev.pointerCount

        val actionMasked = ev.actionMasked
        if (count == 2) {
            // マルチタップの場合のイベントは子コンポーネントにイベントを送らない
            return true
        }
        this.onTouchEvent(ev)
        return false
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val count = event.pointerCount

        return if (count == 2) {
            scaleGesture.onTouchEvent(event)
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {

        // 拡大率設定
        setScale(detector.scaleFactor)

        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {

        // 現在のスクロール位置を取得して設定
        nowScrollX = this.scrollX / nowScale
        nowScrollY = scrollView.scrollY / nowScale

        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    /**
     * 拡大率設定処理
     */
    private fun setScale(scaleFactor: Float) {

        // 最初の一回だけの処理
        if (!initializeFlg) {
            // 基準のサイズを保存
            defaultWidth = scrollView.width
            defaultRight = scrollView.right
            defaultLeftText = textView.left
            defaultRightText = textView.right
            defaultTopText = textView.top
            defaultBottomText = textView.bottom
            defaultWidthText = textView.width
            defaultHeightText = textView.height

            initializeFlg = true
        }

        // 拡大率を求める
        nowScale = nowScale - (1 - scaleFactor)

        // 拡大率の範囲
        if (nowScale <= minScale) {
            nowScale = minScale
        } else if (maxScale <= nowScale) {
            nowScale = maxScale
        }

        // スクロールビューの拡大・縮小

        // 拡大・縮小後の高さ・幅を求める
        val afterWidth = Math.round(defaultWidth * nowScale)

        // 拡大・縮小前後の差
        val diffWidth = (afterWidth - defaultWidth) / 2

        // 拡大・縮小後のスクロールビューの横幅を設定
        scrollView.right = defaultRight + diffWidth * 2


        // テキストビューの拡大・縮小

        // 拡大・縮小
        textView.scaleX = nowScale
        textView.scaleY = nowScale

        // 拡大・縮小後の幅・高さを求める
        val afterWidthText = Math.round(defaultWidthText * nowScale)
        val afterHeightText = Math.round(defaultHeightText * nowScale)

        // 拡大・縮小前後の差
        val diffWidthText = (afterWidthText - defaultWidthText) / 2
        val diffHeightText = (afterHeightText - defaultHeightText) / 2


        // 拡大・縮小後の４辺の長さを設定
        textView.left = defaultLeftText - diffWidth
        textView.right = defaultRightText + diffWidth
        textView.top = defaultTopText - diffHeightText
        textView.bottom = defaultBottomText + diffHeightText

        // 拡大・縮小後のX・Y座標の設定
        textView.x = defaultLeftText + diffWidthText * nowScale
        textView.y = defaultTopText + diffHeightText * nowScale

        // 拡大・縮小後のスクロール位置の設定
        this.scrollX = Math.round(nowScrollX * nowScale)
        scrollView.scrollY = Math.round(nowScrollY * nowScale)
    }
}
