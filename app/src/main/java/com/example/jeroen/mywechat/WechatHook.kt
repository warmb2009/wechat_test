package com.example.jeroen.mywechat

import com.example.jeroen.mywechat.hook.SendMsgHooker
import com.example.jeroen.mywechat.hook.WechatMessageHook
import com.gh0u1l5.wechatmagician.spellbook.SpellBook
import com.gh0u1l5.wechatmagician.spellbook.SpellBook.isImportantWechatProcess
import com.gh0u1l5.wechatmagician.spellbook.util.BasicUtil
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage



class WechatHook : IXposedHookLoadPackage{
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        BasicUtil.tryVerbosely{
            if(isImportantWechatProcess(lpparam!!)){
                SpellBook.startup(lpparam, listOf(SendMsgHooker, WechatMessageHook))
            }
        }
    }

}
