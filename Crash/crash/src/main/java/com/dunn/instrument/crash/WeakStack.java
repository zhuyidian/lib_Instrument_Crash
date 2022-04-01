/*
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 * DO NOT ALTER OR REMOVE NOTICES OR THIS FILE HEADER.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dunn.instrument.crash;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;

public class WeakStack<T> extends AbstractCollection<T> {

    private final List<WeakReference<T>> contents = new ArrayList<>();

    private void cleanup() {
        List<WeakReference<T>> clone = new ArrayList<>();
        for (WeakReference<T> weakReference : contents) {
            if (weakReference.get() != null) {
                clone.add(weakReference);
            }
        }
        contents.clear();
        contents.addAll(clone);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new WeakIterator<>(contents.iterator());
    }

    @Override
    public int size() {
        cleanup();
        return contents.size();
    }

    @Override
    public boolean contains(Object o) {
        if (o != null) {
            for (WeakReference<T> weakReference : contents) {
                if (o.equals(weakReference.get())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(T t) {
        return contents.add(new WeakReference<T>(t));
    }

    @Override
    public boolean remove(Object o) {
        if (o != null) {
            for (int i = 0; i < contents.size(); i++) {
                if (o.equals(contents.get(i).get())) {
                    contents.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 从集合的尾部取出弱引用对象.
     *
     * @return 弱引用对象
     */
    public T peek() {
        for (int i = contents.size() - 1; i >= 0; i--) {
            T result = contents.get(i).get();
            if (result != null) {
                return result;
            }
        }
        throw new EmptyStackException();
    }

    /**
     * 从集合的尾部取出弱引用对象并将其在集合中删除.
     *
     * @return 弱引用对象
     */
    public T pop() {
        T result = peek();
        remove(result);
        return result;
    }

    @Override
    public void clear() {
        contents.clear();
    }

    private static class WeakIterator<T> implements Iterator<T> {
        private final Iterator<WeakReference<T>> iterator;
        private T next;

        private WeakIterator(Iterator<WeakReference<T>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            if (next != null) {
                return true;
            }
            while (iterator.hasNext()) {
                T t = iterator.next().get();
                if (t != null) {
                    //to ensure next() can't throw after hasNext() returned true, we need to dereference this
                    next = t;
                    return true;
                }
            }
            return false;
        }

        @Override
        public T next() {
            T result = next;
            next = null;
            while (result == null) {
                result = iterator.next().get();
            }
            return result;
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
