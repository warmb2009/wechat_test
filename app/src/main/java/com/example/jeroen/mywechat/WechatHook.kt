package com.example.jeroen.mywechat

import android.app.Activity
import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.gh0u1l5.wechatmagician.spellbook.SpellBook
import com.gh0u1l5.wechatmagician.spellbook.SpellBook.isImportantWechatProcess
import com.gh0u1l5.wechatmagician.spellbook.base.Operation
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IActivityHook
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IDatabaseHook
import com.gh0u1l5.wechatmagician.spellbook.interfaces.IMessageStorageHook
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil.tryVerbosely
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedBridge.log
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
//import kotlin.math.log


class WechatHook : IXposedHookLoadPackage{
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        tryVerbosely{
            if(isImportantWechatProcess(lpparam!!)){
                SpellBook.startup(lpparam, listOf(Alert, Message))
            }
        }
    }

}
object Alert : IActivityHook{
    override fun onActivityStarting(activity: Activity) {
        Toast.makeText(activity, "Hello world!", Toast.LENGTH_LONG).show()
    }

}

object Message : IDatabaseHook{
    override fun onDatabaseInserted(thisObject: Any, table: String, nullColumnHack: String?, initialValues: ContentValues?, conflictAlgorithm: Int, result: Long?): Operation<Long>
    {
        if(table == "message"){
            log("New Message: $initialValues")
            Log.e("message", initialValues.toString())
        }
        return super.onDatabaseInserted(thisObject, table, nullColumnHack, initialValues, conflictAlgorithm, result)
    }
}

object WechatMessage : IMessageStorageHook{
    override fun onMessageStorageInserted(msgId: Long, msgObject: Any) {
        XposedBridge.log("OnMessageStorageInserted msgId=$msgId, msgObject=$msgObject")
        val field_content = XposedHelpers.getObjectField(msgObject, "field_content") as String?
        val field_talker = XposedHelpers.getObjectField(msgObject, "field_talker") as String?
        val field_type = (XposedHelpers.getObjectField(msgObject, "field_type") as Int).toInt()
        val field_isSend = (XposedHelpers.getObjectField(msgObject, "field_isSend") as Int).toInt()
        XposedBridge.log("field_content=$field_content,field_talker=$field_talker," + "filed_type=$field_type,field_isSend=$field_isSend")
        if(field_isSend == 1){
            return
        }

        super.onMessageStorageInserted(msgId, msgObject)
    }

}