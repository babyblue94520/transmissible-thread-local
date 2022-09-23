package pers.clare.transmissible;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class TransmissibleThreadLocal<T> extends ThreadLocal<T>{

    static final Map<Thread, Map<TransmissibleThreadLocal<Object>, Object>> threadLocalMap = Collections.synchronizedMap(new WeakHashMap<>());

    public static Map<TransmissibleThreadLocal<Object>, Object> export() {
        Map<TransmissibleThreadLocal<Object>, Object> map = threadLocalMap.get(Thread.currentThread());
        if (map == null) return null;
        return new HashMap<>(map);
    }

    public static void inject(Map<TransmissibleThreadLocal<Object>, Object> source) {
        if (source == null || source.size() == 0) return;
        threadLocalMap.computeIfAbsent(Thread.currentThread(), (key) -> Collections.synchronizedMap(new WeakHashMap<>()))
                .putAll(source);
    }

    @Override
    public T get() {
        Map<TransmissibleThreadLocal<T>, T> temp = getMapOrNull();
        if (temp == null) {
            return null;
        }
        return temp.get(this);
    }

    @Override
    public void set(T value) {
        getMap().put(this, value);
    }

    @Override
    public void remove() {
        super.remove();
        Map<TransmissibleThreadLocal<T>, T> temp = getMapOrNull();
        if (temp == null) return;
        temp.remove(this);
    }

    private Map<TransmissibleThreadLocal<T>, T> getMap() {
        return convert(threadLocalMap.computeIfAbsent(Thread.currentThread(), (key) -> Collections.synchronizedMap(new WeakHashMap<>())));
    }

    private Map<TransmissibleThreadLocal<T>, T> getMapOrNull() {
        return convert(threadLocalMap.get(Thread.currentThread()));
    }

    private static <T> Map<TransmissibleThreadLocal<T>, T> convert(Object map) {
        return (Map<TransmissibleThreadLocal<T>, T>) map;
    }
}
