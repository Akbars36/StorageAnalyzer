using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestCache.Caches
{
    public class Node<TKey,TValue>
    {
        public Node<TKey, TValue> Next { get; set; }
        public Node<TKey, TValue> Previous { get; set; }
        public TKey Key { get; set; }
        public TValue Value { get; set; }
    }

}
