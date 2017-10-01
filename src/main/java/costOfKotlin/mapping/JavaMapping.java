package costOfKotlin.mapping;

import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaMapping {

    @Benchmark
    public List<String> baseline_indexed_arrayList(ListState listState) {
        List<String> list = listState.getArrayListOfStrings();
        ArrayList<String> result = new ArrayList<>(list.size());
        for (int i = 0, size = list.size(); i < size; i++) {
            result.add(list.get(i));
        }
        return result;
    }

    @Benchmark
    public List<String> baseline_iterator_arrayList(ListState listState) {
        List<String> list = listState.getArrayListOfStrings();
        ArrayList<String> result = new ArrayList<>(list.size());
        for (String aList : list) {
            result.add(aList);
        }
        return result;
    }

    @Benchmark
    public List<String> stream_arrayList(ListState listState) {
        return listState.getArrayListOfStrings().stream().map((String s) -> s).collect(Collectors.toList());
    }

    @Benchmark
    public List<String> stream_linkedList(ListState listState) {
        return listState.getLinkedListOfStrings().stream().map((String s) -> s).collect(Collectors.toList());
    }

}
