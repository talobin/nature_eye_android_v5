package dji.sampleV5.modulecommon.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import dji.sampleV5.modulecommon.DJIMainActivity
import dji.v5.utils.common.LogUtils
import java.lang.ref.WeakReference

private const val TAG = "ToastUtils"

object ToastUtils {
    private val handler = Handler(Looper.getMainLooper())
    private var isActivityDestroy = false
    private var contextWeakRef: WeakReference<Context> = WeakReference(null)

    fun init(activity: DJIMainActivity) {
        contextWeakRef = WeakReference(activity.applicationContext)
    }

    fun destroy() {
        handler.removeCallbacksAndMessages(null)
        isActivityDestroy = true
        contextWeakRef.clear()
    }

    fun showToast(msg: String) {
        val context = contextWeakRef.get()
        if (context == null) {
            LogUtils.e(TAG, "Context is null,Please call the init method first!!")
            return
        }
        if (!isActivityDestroy) {
            handler.post {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
