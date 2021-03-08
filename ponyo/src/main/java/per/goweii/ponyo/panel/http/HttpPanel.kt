package per.goweii.ponyo.panel.http

import android.view.View
import com.yuyh.jsonviewer.library.JsonRecyclerView
import com.yuyh.jsonviewer.library.adapter.BaseJsonViewerAdapter
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.Panel

class HttpPanel : Panel() {
    private var jsonRecyclerView: JsonRecyclerView? = null

    override fun getPanelName(): String {
        return "网络请求"
    }

    override fun getLayoutRes(): Int {
        return R.layout.ponyo_panel_http
    }

    override fun onCreated(view: View) {
        super.onCreated(view)
        jsonRecyclerView = view.findViewById(R.id.ponyo_http_json_view)
        jsonRecyclerView?.apply {
            val textSizePx = context.resources.getDimension(R.dimen.ponyo_text_size_http)
            setTextSize(textSizePx / context.resources.displayMetrics.scaledDensity)
            setKeyColor(context.resources.getColor(R.color.ponyo_colorLogAssert))
            setBracesColor(context.resources.getColor(R.color.ponyo_colorLogVisible))
            setValueNumberColor(context.resources.getColor(R.color.ponyo_colorLogDebug))
            setValueBooleanColor(context.resources.getColor(R.color.ponyo_colorLogError))
            BaseJsonViewerAdapter.NULL_COLOR = context.resources.getColor(R.color.ponyo_colorLogWarn)
            setValueTextColor(context.resources.getColor(R.color.ponyo_colorLogInfo))
            setValueUrlColor(context.resources.getColor(R.color.ponyo_colorLogInfo))
        }
        jsonRecyclerView?.bindJson(json)
    }

    private val json = """
    {
     	"id":"token-520405-0303160946-2_2704784##1615195342#9c0",
     	"code":200,
     	"message":"操作成功",
     	"data":[
     		{
     			"aSelect":16,
     			"accuracy":"62%",
     			"analysis":"（1）M大学社会学学院→甲县某些乡镇进行家庭收支情况\n（2）N大学历史学院→甲县的所有乡镇进行历史考察\n赵若兮：肯定逻辑（1）的后件，否定逻辑（2）的后件。肯定逻辑的后件什么也推不出，否定后件可以做逆否。\n可推出赵若兮不是N大学。同理，可推出陈北鱼不是M大学的老师。其他信息均推不出。\n所以A、C、D、E均错误。\n验证B选项，把逻辑的前件“赵若兮是N大”代入条件（2），可知他对甲县的所有乡镇进行过历史考察。\n同时，他未曾到项郢镇进行历史考察，说明郢镇不是甲县的。可以推出后件为真，正确。\nB选项正确",
     			"analysisQrcode":"0",
     			"bSelect":176,
     			"cSelect":30,
     			"childList":[
     		{
     			"aSelect":16,
     			"accuracy":"62%",
     			"analysis":"（1）M大学社会学学院→甲县某些乡镇进行家庭收支情况\n（2）N大学历史学院→甲县的所有乡镇进行历史考察\n赵若兮：肯定逻辑（1）的后件，否定逻辑（2）的后件。肯定逻辑的后件什么也推不出，否定后件可以做逆否。\n可推出赵若兮不是N大学。同理，可推出陈北鱼不是M大学的老师。其他信息均推不出。\n所以A、C、D、E均错误。\n验证B选项，把逻辑的前件“赵若兮是N大”代入条件（2），可知他对甲县的所有乡镇进行过历史考察。\n同时，他未曾到项郢镇进行历史考察，说明郢镇不是甲县的。可以推出后件为真，正确。\nB选项正确",
     			"analysisQrcode":"0",
     			"bSelect":176,
     			"cSelect":30,
     			"childList":[
     		{
     			"aSelect":16,
     			"accuracy":"62%",
     			"analysis":"（1）M大学社会学学院→甲县某些乡镇进行家庭收支情况\n（2）N大学历史学院→甲县的所有乡镇进行历史考察\n赵若兮：肯定逻辑（1）的后件，否定逻辑（2）的后件。肯定逻辑的后件什么也推不出，否定后件可以做逆否。\n可推出赵若兮不是N大学。同理，可推出陈北鱼不是M大学的老师。其他信息均推不出。\n所以A、C、D、E均错误。\n验证B选项，把逻辑的前件“赵若兮是N大”代入条件（2），可知他对甲县的所有乡镇进行过历史考察。\n同时，他未曾到项郢镇进行历史考察，说明郢镇不是甲县的。可以推出后件为真，正确。\nB选项正确",
     			"analysisQrcode":"0",
     			"bSelect":176,
     			"cSelect":30,
     			"childList":[
     		{
     			"aSelect":16,
     			"accuracy":"62%",
     			"analysis":"（1）M大学社会学学院→甲县某些乡镇进行家庭收支情况\n（2）N大学历史学院→甲县的所有乡镇进行历史考察\n赵若兮：肯定逻辑（1）的后件，否定逻辑（2）的后件。肯定逻辑的后件什么也推不出，否定后件可以做逆否。\n可推出赵若兮不是N大学。同理，可推出陈北鱼不是M大学的老师。其他信息均推不出。\n所以A、C、D、E均错误。\n验证B选项，把逻辑的前件“赵若兮是N大”代入条件（2），可知他对甲县的所有乡镇进行过历史考察。\n同时，他未曾到项郢镇进行历史考察，说明郢镇不是甲县的。可以推出后件为真，正确。\nB选项正确",
     			"analysisQrcode":"0",
     			"bSelect":176,
     			"cSelect":30,
     			"childList":[],
     			"childQuesCnt":0,
     			"childQuesNum":0,
     			"content":"M大学社会学学院的老师都曾经对甲县某些乡镇进行家庭收支情况调研，N大学历史学院的老师都曾经到甲县的所有乡镇进行历史考察，赵若兮曾经对甲县所有乡镇家庭收支情况进行调研，但未曾到项郢镇进行历史考察；陈北鱼曾经到梅河乡进行历史考察，但从未对甲县家庭收支情况进行调研。\n根据以上信息，可以得出以下哪项？",
     			"correctNum":176,
     			"dSelect":28,
     			"didNum":282,
     			"eSelect":32,
     			"errorLikelyOption":"E",
     			"fSelect":0,
     			"gSelect":0,
     			"id":7684,
     			"isRelaCourse":false,
     			"knowId":127,
     			"knowName":"代入逻辑推命题真假",
     			"optionList":[
     				{
     					"content":"陈北鱼是M大学社会学学院的老师，且梅河乡是甲县的。",
     					"quesId":7684,
     					"sign":"A"
     				},
     				{
     					"content":"若赵若兮是N大学历史学院的老师，则项郢镇不是甲县的。",
     					"quesId":7684,
     					"sign":"B"
     				},
     				{
     					"content":"对甲县的家庭收支情况调研，也会涉及相关的历史考察。",
     					"quesId":7684,
     					"sign":"C"
     				},
     				{
     					"content":"陈北鱼是N大学的老师。",
     					"quesId":7684,
     					"sign":"D"
     				},
     				{
     					"content":"赵若兮是M大学的老师。 ",
     					"quesId":7684,
     					"sign":"E"
     				}
     			],
     			"paperName":"2021年管理类联考逻辑试题",
     			"parentId":0,
     			"parentType":0,
     			"quesType":1,
     			"quesTypeName":"单项选择题",
     			"score":200,
     			"scoreName":"2",
     			"sectionId":0,
     			"sectionIds":"",
     			"shareUrl":"https://m.hd.mbadashi.com/share_question?id=7684",
     			"sign":"B",
     			"subject":1,
     			"subjectName":"逻辑",
     			"title":"",
     			"userCollect":false,
     			"userSign":""
     		}
     	],
     			"childQuesCnt":0,
     			"childQuesNum":0,
     			"content":"M大学社会学学院的老师都曾经对甲县某些乡镇进行家庭收支情况调研，N大学历史学院的老师都曾经到甲县的所有乡镇进行历史考察，赵若兮曾经对甲县所有乡镇家庭收支情况进行调研，但未曾到项郢镇进行历史考察；陈北鱼曾经到梅河乡进行历史考察，但从未对甲县家庭收支情况进行调研。\n根据以上信息，可以得出以下哪项？",
     			"correctNum":176,
     			"dSelect":28,
     			"didNum":282,
     			"eSelect":32,
     			"errorLikelyOption":"E",
     			"fSelect":0,
     			"gSelect":0,
     			"id":7684,
     			"isRelaCourse":false,
     			"knowId":127,
     			"knowName":"代入逻辑推命题真假",
     			"optionList":[
     				{
     					"content":"陈北鱼是M大学社会学学院的老师，且梅河乡是甲县的。",
     					"quesId":7684,
     					"sign":"A"
     				},
     				{
     					"content":"若赵若兮是N大学历史学院的老师，则项郢镇不是甲县的。",
     					"quesId":7684,
     					"sign":"B"
     				},
     				{
     					"content":"对甲县的家庭收支情况调研，也会涉及相关的历史考察。",
     					"quesId":7684,
     					"sign":"C"
     				},
     				{
     					"content":"陈北鱼是N大学的老师。",
     					"quesId":7684,
     					"sign":"D"
     				},
     				{
     					"content":"赵若兮是M大学的老师。 ",
     					"quesId":7684,
     					"sign":"E"
     				}
     			],
     			"paperName":"2021年管理类联考逻辑试题",
     			"parentId":0,
     			"parentType":0,
     			"quesType":1,
     			"quesTypeName":"单项选择题",
     			"score":200,
     			"scoreName":"2",
     			"sectionId":0,
     			"sectionIds":"",
     			"shareUrl":"https://m.hd.mbadashi.com/share_question?id=7684",
     			"sign":"B",
     			"subject":1,
     			"subjectName":"逻辑",
     			"title":"",
     			"userCollect":false,
     			"userSign":""
     		}
     	],
     			"childQuesCnt":0,
     			"childQuesNum":0,
     			"content":"M大学社会学学院的老师都曾经对甲县某些乡镇进行家庭收支情况调研，N大学历史学院的老师都曾经到甲县的所有乡镇进行历史考察，赵若兮曾经对甲县所有乡镇家庭收支情况进行调研，但未曾到项郢镇进行历史考察；陈北鱼曾经到梅河乡进行历史考察，但从未对甲县家庭收支情况进行调研。\n根据以上信息，可以得出以下哪项？",
     			"correctNum":176,
     			"dSelect":28,
     			"didNum":282,
     			"eSelect":32,
     			"errorLikelyOption":"E",
     			"fSelect":0,
     			"gSelect":0,
     			"id":7684,
     			"isRelaCourse":false,
     			"knowId":127,
     			"knowName":"代入逻辑推命题真假",
     			"optionList":[
     				{
     					"content":"陈北鱼是M大学社会学学院的老师，且梅河乡是甲县的。",
     					"quesId":7684,
     					"sign":"A"
     				},
     				{
     					"content":"若赵若兮是N大学历史学院的老师，则项郢镇不是甲县的。",
     					"quesId":7684,
     					"sign":"B"
     				},
     				{
     					"content":"对甲县的家庭收支情况调研，也会涉及相关的历史考察。",
     					"quesId":7684,
     					"sign":"C"
     				},
     				{
     					"content":"陈北鱼是N大学的老师。",
     					"quesId":7684,
     					"sign":"D"
     				},
     				{
     					"content":"赵若兮是M大学的老师。 ",
     					"quesId":7684,
     					"sign":"E"
     				}
     			],
     			"paperName":"2021年管理类联考逻辑试题",
     			"parentId":0,
     			"parentType":0,
     			"quesType":1,
     			"quesTypeName":"单项选择题",
     			"score":200,
     			"scoreName":"2",
     			"sectionId":0,
     			"sectionIds":"",
     			"shareUrl":"https://m.hd.mbadashi.com/share_question?id=7684",
     			"sign":"B",
     			"subject":1,
     			"subjectName":"逻辑",
     			"title":"",
     			"userCollect":false,
     			"userSign":""
     		}
     	],
     			"childQuesCnt":0,
     			"childQuesNum":0,
     			"content":"M大学社会学学院的老师都曾经对甲县某些乡镇进行家庭收支情况调研，N大学历史学院的老师都曾经到甲县的所有乡镇进行历史考察，赵若兮曾经对甲县所有乡镇家庭收支情况进行调研，但未曾到项郢镇进行历史考察；陈北鱼曾经到梅河乡进行历史考察，但从未对甲县家庭收支情况进行调研。\n根据以上信息，可以得出以下哪项？",
     			"correctNum":176,
     			"dSelect":28,
     			"didNum":282,
     			"eSelect":32,
     			"errorLikelyOption":"E",
     			"fSelect":0,
     			"gSelect":0,
     			"id":7684,
     			"isRelaCourse":false,
     			"knowId":127,
     			"knowName":"代入逻辑推命题真假",
     			"optionList":[
     				{
     					"content":"陈北鱼是M大学社会学学院的老师，且梅河乡是甲县的。",
     					"quesId":7684,
     					"sign":"A"
     				},
     				{
     					"content":"若赵若兮是N大学历史学院的老师，则项郢镇不是甲县的。",
     					"quesId":7684,
     					"sign":"B"
     				},
     				{
     					"content":"对甲县的家庭收支情况调研，也会涉及相关的历史考察。",
     					"quesId":7684,
     					"sign":"C"
     				},
     				{
     					"content":"陈北鱼是N大学的老师。",
     					"quesId":7684,
     					"sign":"D"
     				},
     				{
     					"content":"赵若兮是M大学的老师。 ",
     					"quesId":7684,
     					"sign":"E"
     				}
     			],
     			"paperName":"2021年管理类联考逻辑试题",
     			"parentId":0,
     			"parentType":0,
     			"quesType":1,
     			"quesTypeName":"单项选择题",
     			"score":200,
     			"scoreName":"2",
     			"sectionId":0,
     			"sectionIds":"",
     			"shareUrl":"https://m.hd.mbadashi.com/share_question?id=7684",
     			"sign":"B",
     			"subject":1,
     			"subjectName":"逻辑",
     			"title":"",
     			"userCollect":false,
     			"userSign":""
     		}
     	],
     	"error":null
    }
    """.trimIndent()

    override fun onVisible(view: View) {
        super.onVisible(view)
    }
}