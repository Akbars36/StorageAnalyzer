using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using TestCache;
using TestCache.Caches;
using Troschuetz.Random;

namespace TestCache
{
    class Program
    {
        private static Storage<Int64, Int64> storage;

        public const int MAIN_STORAGE_SPEED = 50000;

        private const int CACHE_STORAGE_SPEED = 1;


        private static Random rnd = new Random();
        static void Main(string[] args)
        {
            var ind = 3;
            var str = "";
            for (int i = 0;i< 20; i++)
            {
                str += "ABS(P" + (ind+i * 54)+ "-AE3);";
            }
            Console.WriteLine(str);
            //var cacheSize = 10000;
            var primarySize = 1000000;
            var primaryMax = 1000000;
            var commandsSize = 10000000;
            var commandsMax = 1000000;
            var cacheSizes = new int[] { 10, 9100, 18190, 27280, 36370, 45460, 54550, 63640, 72730, 81820, 90910, 100000,500000,1000000 };
            var sigmas = new int[] { 10, 50, 100, 500, 1000, 5000, 10000 };
            Type[] cachePolicyTypes = { typeof(LRUCache<Int64, Int64>), typeof(MRUCache<Int64, Int64>) };
            Type[] cacheStoragesTypes = { typeof(Dictionary<Int64, Node<Int64, Int64>>), typeof(SortedDictionary<Int64, Node<Int64, Int64>>),typeof(SortedList<Int64, Node<Int64, Int64>>) };
            var row = 1;
            for(int i = 0; i < 20; i++) {
                for (int cT = 0; cT < cacheStoragesTypes.Count(); cT++)
                {
                    for (int cP = 0; cP < cachePolicyTypes.Count(); cP++)
                    {
                        UpdateExcel("Sheet1", row, 1, cacheStoragesTypes[cT].Name + cachePolicyTypes[cP].Name);
                        row++;
                        for (int cacheSize = 0; cacheSize < cacheSizes.Count(); cacheSize++)
                        {
                            UpdateExcel("Sheet1", row, 2 + cacheSize, cacheSizes[cacheSize] + "");
                            UpdateExcel("Sheet1", row, 2 + cacheSizes.Count() + cacheSize, cacheSizes[cacheSize] + "");
                        }
                        row++;
                        for (int sigma = 0; sigma < sigmas.Count(); sigma++)
                        {
                            var commands = GenerateRandomListGauss(commandsSize, sigmas[sigma]);
                            UpdateExcel("Sheet1", row, 1, sigmas[sigma] + "");
                            for (int cacheSize = 0; cacheSize < cacheSizes.Count(); cacheSize++)
                            {


                                IDictionary<Int64, Node<Int64, Int64>> cacheSorage = (IDictionary<Int64, Node<Int64, Int64>>)Activator.CreateInstance(cacheStoragesTypes[cT], new object[] { new SortHelper() });
                                ICache<Int64, Int64> cache = (ICache<Int64, Int64>)Activator.CreateInstance(cachePolicyTypes[cP], new object[] { cacheSorage, cacheSizes[cacheSize] });


                                storage = new Storage<Int64, Int64>(cache, GenerateRandomDictionary(primarySize, primaryMax));
                                //storage.Print();

                                for (int j = 0; j < commandsSize; j++)
                                {
                                    Int64 val;
                                    storage.Get(commands[j], out val);
                                }
                                UpdateExcel("Sheet1", row, cacheSize + 2, Storage<Int64, long>.CompareCount.ToString());
                                UpdateExcel("Sheet1", row, cacheSize +cacheSizes.Count() + 2, Storage<Int64, long>.CacheCount.ToString());
                                //commands.ForEach(m => { Int64 val; storage.Get(m, out val); /*Console.Out.WriteLine("\nExtract key = {0}, value = {1}", m, val); storage.Print(); */});
                                Console.WriteLine("Compares count = {0} for cache {1} and policy {2}", Storage<Int64, Int64>.CompareCount, cacheStoragesTypes[cT].Name, cachePolicyTypes[cP].Name);
                            }
                            row++;
                        }
                    }
                }
                //row++;
                }

            Console.ReadLine();
        }

        private static void UpdateExcel(string sheetName, int row, int col, string data)
        {
            System.Threading.Thread.CurrentThread.CurrentCulture = new System.Globalization.CultureInfo("en-US");
            Microsoft.Office.Interop.Excel.Application oXL = null;
            Microsoft.Office.Interop.Excel._Workbook oWB = null;
            Microsoft.Office.Interop.Excel._Worksheet oSheet = null;

            try
            {
                oXL = new Microsoft.Office.Interop.Excel.Application();
                oWB = oXL.Workbooks.Open(Environment.CurrentDirectory + @"\Caches3DPlotsDiff.xlsm");
                oSheet = String.IsNullOrEmpty(sheetName) ? (Microsoft.Office.Interop.Excel._Worksheet)oWB.ActiveSheet : (Microsoft.Office.Interop.Excel._Worksheet)oWB.Worksheets[sheetName];

                oSheet.Cells[row, col] = data;

                oWB.Save();

                //MessageBox.Show("Done!");
            }
            catch (Exception ex)
            {
                throw ex;
                //MessageBox.Show(ex.ToString());
            }
            finally
            {
                if (oWB != null)
                    oWB.Close();
            }
        }


        static List<Int64> GenerateRandomList(int count, int maxval)
        {
            var res = new List<Int64>(count);
            for (var i = 0; i < count; i++)
            {
                res.Add(rnd.Next(-maxval, maxval));
            }
            return res;
        }

        static List<Int64> GenerateRandomListGauss(int count, int sigma)
        {
            var res = new List<Int64>(count);
            var distr = new NormalDistribution();
            distr.Sigma = sigma;
            for (var i = 0; i < count; i++)
            {
                res.Add(Convert.ToInt64(distr.NextDouble()));
            }
            //res.ForEach(Console.WriteLine);
            return res;
        }

        static Dictionary<Int64, Int64> GenerateRandomDictionary(int count, int maxval)
        {

            var res = new Dictionary<Int64, Int64>(count);
            for (var i = 0; i < count; i++)
            {
                var key = rnd.Next(0, maxval);
                var val = key * (maxval + 1);
                res[key] = val;
            }
            return new Dictionary<Int64, Int64>(res,new SortMainHelper());
        }

        class SortHelper : IComparer<Int64>, IEqualityComparer<Int64>
        {
            public int Compare(Int64 x, Int64 y)
            {
                Storage<Int64, Int64>.CompareCount += CACHE_STORAGE_SPEED;
                return x.CompareTo(y);
            }

            public bool Equals(long x, long y)
            {
                Storage<Int64, Int64>.CompareCount += CACHE_STORAGE_SPEED;
                return x.Equals(y);
            }

            public int GetHashCode(long obj)
            {
                return obj.GetHashCode();
            }
        }

        class SortMainHelper : IComparer<Int64>, IEqualityComparer<Int64>
        {
            public int Compare(Int64 x, Int64 y)
            {
                Storage<Int64, Int64>.CompareCount += MAIN_STORAGE_SPEED;
                return x.CompareTo(y);
            }

            public bool Equals(long x, long y)
            {
                Storage<Int64, Int64>.CompareCount += MAIN_STORAGE_SPEED;
                return x.Equals(y);
            }

            public int GetHashCode(long obj)
            {
                return obj.GetHashCode();
            }
        }
    }
}
