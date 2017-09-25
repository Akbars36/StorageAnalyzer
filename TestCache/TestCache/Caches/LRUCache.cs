using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestCache.Caches
{
    public class LRUCache<TKey, TValue>:ICache<TKey, TValue>
    {
        public IDictionary<TKey, Node<TKey, TValue>> Entries { get; set; }
        public int Capacity;
        private Node<TKey, TValue> head;
        private Node<TKey, TValue> tail;

        
        public LRUCache(IDictionary<TKey,Node<TKey, TValue>> dict, int capacity = 16)
        {
            if (capacity <= 0)
                throw new ArgumentOutOfRangeException(
                    "capacity",
                    "Capacity should be greater than zero");
            Capacity = capacity;
            Entries = dict;
            head = null;
        }

        public LRUCache()
        {
        }

        public void Set(TKey key, TValue value)
        {
            Node<TKey, TValue> entry;
            if (!Entries.TryGetValue(key, out entry))
            {
                entry = new Node<TKey, TValue> { Key = key, Value = value };
                if (Entries.Count == Capacity)
                {
                    Entries.Remove(tail.Key);
                    tail = tail.Previous;
                    if (tail != null) tail.Next = null;
                }
                Entries.Add(key, entry);
            }

            entry.Value = value;
            MoveToHead(entry);
            if (tail == null) tail = head;
        }

        public bool TryGetValue(TKey key, out TValue value)
        {
            value = default(TValue);
            Node<TKey, TValue> entry;
            if (!Entries.TryGetValue(key, out entry)) return false;
            MoveToHead(entry);
            value = entry.Value;
            return true;
        }

        private void MoveToHead(Node<TKey, TValue> entry)
        {
            if (entry == head || entry == null) return;

            var next = entry.Next;
            var previous = entry.Previous;

            if (next != null) next.Previous = entry.Previous;
            if (previous != null) previous.Next = entry.Next;

            entry.Previous = null;
            entry.Next = head;

            if (head != null) head.Previous = entry;
            head = entry;

            if (tail == entry) tail = previous;
        }

        public void Print()
        {
            Console.WriteLine("Cache:");
            var node = head;
            while (node != null)
            {
                Console.WriteLine("Key = {0}, Value = {1}", node.Key, node.Value);
                node = node.Next;
            }
                
        }
    }

   
}
