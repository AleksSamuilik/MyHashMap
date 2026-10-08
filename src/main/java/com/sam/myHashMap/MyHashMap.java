package com.sam.myHashMap;

import java.util.Objects;

public class MyHashMap<K, V> {

    private static final int DEF_CAP = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;
    private int threshold;

    public MyHashMap() {
        table = (Node<K, V>[]) new Node[DEF_CAP];
        threshold = (int) (DEF_CAP * LOAD_FACTOR);
    }

    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private int hash(Object key) {
        int h = (key == null) ? 0 : key.hashCode();
        return h ^ (h >>> 16);
    }

    public V get(K key) {
        int h = hash(key);
        int index = (table.length - 1) & h;

        Node<K, V> node = table[index];
        while (node != null) {
            if (node.hash == h && Objects.equals(node.key, key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    public V put(K key, V value) {
        int h = hash(key);
        int index = (table.length - 1) & h;

        Node<K, V> node = table[index];
        while (node != null) {
            if (node.hash == h && Objects.equals(node.key, key)) {
                V old = node.value;
                node.value = value;
                return old;
            }
            node = node.next;
        }

        addNode(h, key, value, index);
        return null;
    }

    private void addNode(int hash, K key, V value, int index) {
        Node<K, V> newNode = new Node<>(hash, key, value, table[index]);
        table[index] = newNode;

        if (++size > threshold) {
            resize();
        }
    }

    private void resize() {
        Node<K, V>[] oldTable = table;
        int oldCap = oldTable.length;
        int newCap = oldCap << 1; // oldCap * 2

        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCap];

        for (int j = 0; j < oldCap; j++) {
            Node<K, V> head = oldTable[j];
            if (head == null) {
                continue;
            }

            Node<K, V> loHead = null, loTail = null;
            Node<K, V> hiHead = null, hiTail = null;

            for (Node<K, V> node = head; node != null; ) {
                Node<K, V> next = node.next;
                node.next = null;

                if ((node.hash & oldCap) == 0) {
                    if (loTail == null) {
                        loHead = node;
                    } else {
                        loTail.next = node;
                    }
                    loTail = node;
                } else {
                    if (hiTail == null) {
                        hiHead = node;
                    } else {
                        hiTail.next = node;
                    }
                    hiTail = node;
                }

                node = next;
            }

            if (loTail != null) {
                loTail.next = null;
                newTable[j] = loHead;
            }
            if (hiTail != null) {
                hiTail.next = null;
                newTable[j + oldCap] = hiHead;
            }
        }

        table = newTable;
        threshold = (int) (newCap * LOAD_FACTOR);
    }

    public V remove(K key) {
        int h = hash(key);
        int index = (table.length - 1) & h;

        Node<K, V> node = table[index];
        Node<K, V> prev = null;

        while (node != null) {
            if (node.hash == h && Objects.equals(node.key, key)) {
                if (prev == null) {
                    table[index] = node.next;
                } else {
                    prev.next = node.next;
                }

                node.next = null;

                size--;
                return node.value;
            }
            prev = node;
            node = node.next;
        }
        return null;
    }

    public int size() {
        return size;
    }
}