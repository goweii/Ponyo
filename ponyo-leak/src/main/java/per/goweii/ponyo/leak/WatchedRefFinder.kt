package per.goweii.ponyo.leak

import shark.HeapGraph
import shark.LeakingObjectFinder

object WatchedRefFinder : LeakingObjectFinder {

    override fun findLeakingObjectIds(graph: HeapGraph): Set<Long> =
        findWatchedRefs(graph).map {
            it.referent.value
        }.toSet()

    internal fun findWatchedRefs(graph: HeapGraph): List<WatchedRefMirror> {
        return graph.context.getOrPut(WatchedRefInspector::class.java.name) {
            val watchedRefClass = graph.findClassByName(WatchedRef::class.java.name)
            val watchedRefClassId = watchedRefClass?.objectId ?: 0
            val addedToContext: List<WatchedRefMirror> =
                graph.instances.filter { instance ->
                    instance.instanceClassId == watchedRefClassId
                }.map {
                    WatchedRefMirror.from(it)
                }.filter {
                    it.hasReferent
                }.toList()
            graph.context[WatchedRefInspector::class.java.name] = addedToContext
            addedToContext
        }
    }

}