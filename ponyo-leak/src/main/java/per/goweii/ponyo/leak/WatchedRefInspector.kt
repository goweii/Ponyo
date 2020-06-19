package per.goweii.ponyo.leak

import shark.ObjectInspector
import shark.ObjectReporter

object WatchedRefInspector : ObjectInspector {

    override fun inspect(reporter: ObjectReporter) {
        val graph = reporter.heapObject.graph
        val references = WatchedRefFinder.findWatchedRefs(graph)
        val objectId = reporter.heapObject.objectId
        references.forEach { ref ->
            if (ref.referent.value == objectId) {
                reporter.leakingReasons += if (ref.description.isNotEmpty()) {
                    "LeakWatcher was watching this because ${ref.description}"
                } else {
                    "LeakWatcher was watching this"
                }
                reporter.labels += "key = ${ref.key}"
            }
        }
    }
}