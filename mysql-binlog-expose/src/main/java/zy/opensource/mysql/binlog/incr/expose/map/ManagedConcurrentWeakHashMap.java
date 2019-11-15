//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package zy.opensource.mysql.binlog.incr.expose.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ManagedConcurrentWeakHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    private final ConcurrentMap<ManagedConcurrentWeakHashMap.Key, V> map ;
    private final ReferenceQueue<Object> queue = new ReferenceQueue();

    public ManagedConcurrentWeakHashMap() {
        map = new ConcurrentHashMap();
    }

    public ManagedConcurrentWeakHashMap(int size) {
        map = new ConcurrentHashMap(size);
    }

    public void maintain() {
        ManagedConcurrentWeakHashMap.Key key;
        while ((key = (ManagedConcurrentWeakHashMap.Key) this.queue.poll()) != null) {
            if (!key.isDead()) {
                key.ackDeath();
                this.map.remove(key);
            }
        }

    }

    private ManagedConcurrentWeakHashMap.Key createStoreKey(Object key) {
        return new ManagedConcurrentWeakHashMap.Key(key, this.queue);
    }

    private ManagedConcurrentWeakHashMap.Key createLookupKey(Object key) {
        return new ManagedConcurrentWeakHashMap.Key(key, (ReferenceQueue) null);
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsValue(Object value) {
        return value == null ? false : this.map.containsValue(value);
    }

    public boolean containsKey(Object key) {
        return key == null ? false : this.map.containsKey(this.createLookupKey(key));
    }

    public V get(Object key) {
        return key == null ? null : this.map.get(this.createLookupKey(key));
    }

    public V put(K key, V value) {
        Objects.requireNonNull(value);
        return this.map.put(this.createStoreKey(key), value);
    }

    public V remove(Object key) {
        return this.map.remove(this.createLookupKey(key));
    }

    public void clear() {
        this.map.clear();
        this.maintain();
    }

    public V putIfAbsent(K key, V value) {
        Objects.requireNonNull(value);
        ManagedConcurrentWeakHashMap.Key storeKey = this.createStoreKey(key);
        V oldValue = this.map.putIfAbsent(storeKey, value);
        if (oldValue != null) {
            storeKey.ackDeath();
        }

        return oldValue;
    }

    public boolean remove(Object key, Object value) {
        return value == null ? false : this.map.remove(this.createLookupKey(key), value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        Objects.requireNonNull(newValue);
        return this.map.replace(this.createLookupKey(key), oldValue, newValue);
    }

    public V replace(K key, V value) {
        Objects.requireNonNull(value);
        return this.map.replace(this.createLookupKey(key), value);
    }

    public Collection<V> values() {
        return this.map.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            public boolean isEmpty() {
                return ManagedConcurrentWeakHashMap.this.map.isEmpty();
            }

            public int size() {
                return ManagedConcurrentWeakHashMap.this.map.size();
            }

            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    private final Iterator<Entry<ManagedConcurrentWeakHashMap.Key, V>> it;

                    {
                        this.it = ManagedConcurrentWeakHashMap.this.map.entrySet().iterator();
                    }

                    public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    public Entry<K, V> next() {
                        return new Entry<K, V>() {
                            private final Entry<ManagedConcurrentWeakHashMap.Key, V> en;

                            {
                                this.en = (Entry) it.next();
                            }

                            public K getKey() {
                                return (K) ((Key) this.en.getKey()).get();
                            }

                            public V getValue() {
                                return this.en.getValue();
                            }

                            public V setValue(V value) {
                                Objects.requireNonNull(value);
                                return this.en.setValue(value);
                            }
                        };
                    }

                    public void remove() {
                        this.it.remove();
                    }
                };
            }
        };
    }

    private static class Key extends WeakReference<Object> {
        private final int hash;
        private boolean dead;

        public Key(Object key, ReferenceQueue<Object> queue) {
            super(key, queue);
            this.hash = key.hashCode();
        }

        public int hashCode() {
            return this.hash;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (this.dead) {
                return false;
            } else if (!(obj instanceof Reference)) {
                return false;
            } else {
                Object oA = this.get();
                Object oB = ((Reference) obj).get();
                if (oA == oB) {
                    return true;
                } else {
                    return oA != null && oB != null ? oA.equals(oB) : false;
                }
            }
        }

        public void ackDeath() {
            this.dead = true;
        }

        public boolean isDead() {
            return this.dead;
        }
    }
}
