package io.github.stuff_stuffs.tbcexutil.common.path;

import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import it.unimi.dsi.fastutil.objects.ObjectHeaps;

import java.util.Comparator;

class PathHeap<K> extends ObjectHeapPriorityQueue<K> {
    public PathHeap(final int capacity, final Comparator<? super K> c) {
        super(capacity, c);
    }

    public PathHeap(final int capacity) {
        super(capacity);
    }

    public PathHeap(final Comparator<? super K> c) {
        super(c);
    }

    public void decreasePriority(final Object obj) {
        for (int i = 0; i < heap.length; i++) {
            if (heap[i] == obj) {
                ObjectHeaps.upHeap(heap, size, i, comparator());
                return;
            }
        }
        throw new RuntimeException();
    }
}
