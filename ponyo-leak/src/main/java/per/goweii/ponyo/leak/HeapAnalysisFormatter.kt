package per.goweii.ponyo.leak

import shark.HeapAnalysis
import shark.HeapAnalysisFailure
import shark.HeapAnalysisSuccess

/**
 * @author CuiZhen
 * @date 2020/6/20
 */
object HeapAnalysisFormatter {

    fun HeapAnalysis.toFormatString(): String = run {
        when (this) {
            is HeapAnalysisSuccess -> toFormatString()
            is HeapAnalysisFailure -> toFormatString()
        }
    }

    private fun HeapAnalysisSuccess.toFormatString(): String = run {
        """
============================
=== HEAP ANALYSIS RESULT ===
============================
${applicationLeaks.size} APPLICATION LEAKS
${if (applicationLeaks.isNotEmpty()) "\n" + applicationLeaks.joinToString("\n\n") + "\n\n" else ""}
${libraryLeaks.size} LIBRARY LEAKS
${if (libraryLeaks.isNotEmpty()) "\n" + libraryLeaks.joinToString("\n\n") + "" else ""}"""
    }

    private fun HeapAnalysisFailure.toFormatString(): String = run {
        """
============================
=== HEAP ANALYSIS FAILED ===
============================"""
    }
}