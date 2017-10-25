package costOfKotlin.mapLike

import org.openjdk.jmh.annotations.Benchmark


open class Sets {

    @Benchmark fun hashSet(state: ObjectsState): List<String> {
        return HashSet(state.objects).map {
            it + " "
        }
    }

    @Benchmark fun treeSet(state: ObjectsState): List<String> {
        return state.objects.toSet().map {
            it + " "
        }
    }

    @Benchmark fun hashSet_with_dups(state: ObjectsState): List<String> {
        return HashSet(state.objects_with_dups).map {
            it + " "
        }
    }

    @Benchmark fun treeSet_with_dups(state: ObjectsState): List<String> {
        return state.objects_with_dups.toSet().map {
            it + " "
        }
    }
}