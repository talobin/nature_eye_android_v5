package dji.v5.ux.accessory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import dji.v5.ux.R
import dji.v5.ux.core.extension.show

/**
 * Description :RTK坐标系和RTK服务类型选择器
 *
 * @author: Byte.Cai
 *  date : 2022/7/25
 *
 * Copyright (c) 2022, DJI All Rights Reserved.
 */
open class DescSpinnerCell @kotlin.jvm.JvmOverloads constructor(
    context: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mSpinner: Spinner? = null
    private var mSummary: TextView? = null
    private var mDesc: TextView? = null
    private var mAdapter: ArrayAdapter<String>? = null

    private var mSelectedPosition = 0
    private val mSelectedListener: OnItemSelectedListener? = null


    init {
        initView()
        initListener()
        initAttrs()

    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.uxsdk_spinner_desc_layout, this, true)
        mSpinner = findViewById(R.id.spinner)
        mSummary = findViewById(R.id.summary)
        mDesc = findViewById(R.id.desc)

        mAdapter = object : ArrayAdapter<String>(context, R.layout.uxsdk_spinner_item_bord) {
            override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                // 右边的下拉图标
                if (position != 0 && checkRightCompoundDrawable(view)) {
                    val rightDrawable = view.compoundDrawables[2].mutate()
                    rightDrawable.alpha = 0
                    view.setCompoundDrawables(null, null, rightDrawable, null)

                }
                if (mSelectedPosition == position) {
                    view.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    view.setTextColor(ContextCompat.getColor(context, R.color.uxsdk_white_75_percent))
                }
                return view
            }

            private fun checkRightCompoundDrawable(view: TextView?): Boolean {
                return view != null && view.compoundDrawables.size == 4 && view.compoundDrawables[2] != null
            }
        }
        mAdapter?.setDropDownViewResource(R.layout.uxsdk_spinner_item_drop)

    }


    private fun initListener() {
        mSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mSelectedListener?.onItemSelected(this@DescSpinnerCell, mSelectedPosition, position, mAdapter?.getItem(position))
                mSelectedPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //不需要实现
            }

        }
        mSpinner?.adapter = mAdapter
        // 这里需要设置不保存状态。否则在view被销毁重新加载并恢复时，由于spinnerID相同，导致value被复用
        mSpinner?.isSaveEnabled = false
    }

    fun initAttrs() {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.DescSpinnerCell, defStyleAttr, 0)
        if (ta.hasValue(R.styleable.DescSpinnerCell_summary)) {
            mSummary?.text = ta.getString(R.styleable.DescSpinnerCell_summary)
        } else {
            mSummary?.visibility = GONE
        }

        if (ta.hasValue(R.styleable.DescSpinnerCell_desc)) {
            mDesc?.text = ta.getString(R.styleable.DescSpinnerCell_desc)
        } else {
            mDesc?.visibility = GONE
        }
    }


    interface OnItemSelectedListener {
        fun onItemSelected(cell: DescSpinnerCell?, oldPosition: Int, newPosition: Int, item: String?)
    }

    open fun setSummaryText(summaryText: String) {
        mSummary?.show()
        mSummary?.text = summaryText
    }

    open fun setSummaryText(summaryTextId: Int) {
        mSummary?.show()
        mSummary?.setText(summaryTextId)
    }

    open fun setSDescText(descText: String) {
        mDesc?.show()
        mDesc?.text = descText
    }

    open fun setEntries(entries: List<String?>) {
        mAdapter?.clear()
        mAdapter?.addAll(entries)
    }

    open fun select(position: Int) {
        if (position >= 0 && position < mAdapter?.count ?: -1) {
            mSpinner?.setSelection(position, true)
            mSelectedPosition = position
        }
    }


    override fun setEnabled(enable: Boolean) {
        super.setEnabled(enable)
        mSpinner?.isEnabled = enable
    }


}