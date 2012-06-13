/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: QSort.java,v 1.5 2011-04-18 12:00:39 ptdeboer Exp $  
 * $Date: 2011-04-18 12:00:39 $
 */
// source: 

package nl.uva.vlet.util;

import java.util.List;

import nl.uva.vlet.Global;
import nl.uva.vlet.data.StringList;
import nl.uva.vlet.data.StringUtil;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.vrs.VNode;

/**
 * Implements QuickSort, on an array of objects. An implementor of `Comparer' is
 * passed in to compare two objects.
 */
public class QSort
{
    private Comparer comp;

    /**
     * Create a QSort object. One way to use this would with dynamic class
     * creation:
     * 
     * <PRE>
     * 
     * QSort sorter = new QSort(new Comparer() 
     * {
     *     public int compare(Object a,Object b)
     *     { 
     *         if (a.key == b.key) 
     *             return 0; 
     *         else  if (a.key &lt; b.key) 
     *             return -1 
     *         else
     *             return 1; 
     *     } 
     * }); 
     * 
     * sorter.sort(array);
     * 
     * </PRE>
     */

    public QSort(Comparer c)
    {
        comp = c;
    }

    /**
     * Sorts the array, according to the Comparer. The returned vector (I[])
     * provides mapping information about the new order. The sorted list Y[]
     * equals to the original list X[] as follows: Y[I[i]] = X[i] Or:
     * I[index-in-Y]=index-in-X
     * 
     **/
    public int[] sort(Object[] list)
    {
        return quicksort(newIndex(list.length), list, 0, list.length - 1);
    }

    /** Sorts the array, according to the Comparer. */
    public int[] sort(List<? extends Object> list)
    {
        return quicksort(newIndex(list.size()), list, 0, list.size() - 1);
    }

    /** Sorts a subsequence of the array, according to the Comparer. */
    public int[] sort(int mapping[], Object[] list, int start, int end)
    {
        return quicksort(mapping, list, start, end - 1);
    }

    /** Sorts a subsequence of the array, according to the Comparer. */
    public int[] sort(int mapping[], List<? extends Object> list, int start, int end)
    {
        return quicksort(mapping, list, start, end - 1);
    }

    private int[] newIndex(int len)
    {
        int index[] = new int[len];

        for (int i = 0; i < len; i++)
            index[i] = i;
        return index;
    }

    private int[] quicksort(int mapping[], Object[] list, int p, int r)
    {
        if (p < r)
        {
            int q = partition(mapping, list, p, r);
            if (q == r)
            {
                q--;
            }
            quicksort(mapping, list, p, q);
            quicksort(mapping, list, q + 1, r);
        }

        return mapping;

    }

    private int[] quicksort(int mapping[], List<? extends Object> list, int p, int r)
    {
        if (p < r)
        {
            int q = partition(mapping, list, p, r);
            if (q == r)
            {
                q--;
            }
            quicksort(mapping, list, p, q);
            quicksort(mapping, list, q + 1, r);
        }
        return mapping;
    }

    private int partition(int mapping[], Object[] list, int p, int r)
    {
        Object pivot = list[p];
        int lo = p;
        int hi = r;

        while (true)
        {

            while (comp.compare(list[hi], pivot) >= 0 && lo < hi)
            {
                hi--;
            }
            while (comp.compare(list[lo], pivot) < 0 && lo < hi)
            {
                lo++;
            }
            if (lo < hi)
            {
                Object T = list[lo];
                list[lo] = list[hi];
                list[hi] = T;

                int i = mapping[lo];
                mapping[lo] = mapping[hi];
                mapping[hi] = i;
            }
            else
                return hi;
        }
    }

    @SuppressWarnings("unchecked")
    private int partition(int mapping[], List<? extends Object> list, int p, int r)
    {
        Object pivot = list.get(p);
        int lo = p;
        int hi = r;

        while (true)
        {

            while (comp.compare(list.get(hi), pivot) >= 0 && lo < hi)
            {
                hi--;
            }
            while (comp.compare(list.get(lo), pivot) < 0 && lo < hi)
            {
                lo++;
            }
            if (lo < hi)
            {

                Object loVal = list.get(lo);
                Object hiVal = list.get(hi);
                // strange: type <? extends Object> doesn't accept Object
                ((List<Object>) list).set(lo, hiVal);
                ((List<Object>) list).set(hi, loVal);

                int i = mapping[lo];
                mapping[lo] = mapping[hi];
                mapping[hi] = i;
            }
            else
                return hi;
        }
    }

    // public static void main(String argv[]) {
    // String array[] = { "7", "3", "0", "4", "5", "6", "2", "1" };
    // QSort sorter = new QSort(new Comparer() {
    // public int compare(Object a, Object b) {
    // int ai = Integer.parseInt((String) a);
    // int bi = Integer.parseInt((String) b);
    // if (ai == bi) return 0;
    // else if (ai < bi) return -1;
    // else return 1;
    // }
    // });
    // sorter.sort(array);
    // for (int i = 0; i < array.length; i++)
    // System.out.print(array[i] + " ");
    // System.out.println();
    // }

    // ===========================================================
    // Static methods
    // ===========================================================

    public static class VAttributeComparer implements Comparer
    {
        public boolean ignoreCase = false;

        public int compare(Object o1, Object o2)
        {
            if (o1 == null)
                if (o2 == null)
                    return 0;
                else
                    return -1;
            else if (o2 == null)
                return 1;
            else
                ; // continue

            VAttribute a1 = (VAttribute) o1;
            VAttribute a2 = (VAttribute) o2;

            if (ignoreCase)
            {
                return a1.getName().compareToIgnoreCase(a2.getName());
            }
            else
            {
                return a1.getName().compareTo(a2.getName());
            }

        }

    }

    public static abstract class VNodeComparer implements Comparer
    {

    }

    public static class VNodeTypeNameComparer extends VNodeComparer
    {
        boolean ignoreCase = false;

        boolean typeFirst = false;

        int typeDirection = 1; // set to -1 for inverse Type

        int nameDirection = 1; // set to -1 for inverse name

        public VNodeTypeNameComparer(boolean typeFirst, boolean ignoreCase)
        {
            this.ignoreCase = ignoreCase;
            this.typeFirst = typeFirst;
        }

        public int compare(Object o1, Object o2)
        {
            if (o1 == null)
                if (o2 == null)
                    return 0;
                else
                    return -1;
            else if (o2 == null)
                return 1;
            else
                ; // continue

            VNode n1 = (VNode) o1;
            VNode n2 = (VNode) o2;

            if (typeFirst == true)
            {
                int res = StringUtil.compare(n1.getType(), n2.getType());

                if (res != 0)
                    return res * typeDirection;

                // else compare same type nodes !
            }

            if (ignoreCase)
            {
                return nameDirection * n1.getName().compareToIgnoreCase(n2.getName());
            }
            else
            {
                return nameDirection * n1.getName().compareTo(n2.getName());
            }
        }
    }

    public static class VNodeAttributeComparer extends VNodeComparer
    {
        public boolean ignoreCase = false;

        public String[] sortFields;

        public int directions[];// sort direction of above sort fields;

        public VNodeAttributeComparer(String[] sortFields, boolean ignoreCase)
        {
            this.ignoreCase = ignoreCase;
            this.sortFields = sortFields;
        }

        public int compare(Object o1, Object o2)
        {
            if (o1 == null)
                if (o2 == null)
                    return 0;
                else
                    return -1;
            else if (o2 == null)
                return 1;
            else
                ; // continue

            if ((o1 instanceof VNode) == false)
                throw new Error("Object 1 is NOT of VNode type!");
            if ((o2 instanceof VNode) == false)
                throw new Error("Object 2 is NOT of VNode type!");

            VNode n1 = (VNode) o1;
            VNode n2 = (VNode) o2;

            int result = 0;
            int i = 0;
            int n = sortFields.length;
            // compare attributes while values are equal
            while ((result == 0) && (i < n))
            {
                VAttribute a1;
                VAttribute a2;

                try
                {
                    a1 = n1.getAttribute(sortFields[i]);
                    a2 = n2.getAttribute(sortFields[i]);
                }
                catch (Exception e)
                {
                    Global.warnPrintf(this, "Error during sort. Got exception while fetching attribute:%s\n", e);
                    // throw new
                    // Error("Fatal: Exception while fetching attribute:"+e,e);
                    return 0;
                }

                if (a1 == null)
                    if (a2 == null)
                        result = 0;
                    else
                        result = -1;
                else if (a2 == null)
                    result = 1;
                else if (ignoreCase)
                    result = a1.compareToIgnoreCase(a2);
                else
                    result = a1.compareTo(a2);

                i++;
            }

            return result;
        }

    }

    public static class StringComparer implements Comparer
    {
        public boolean ignoreCase = false;

        public int compare(Object o1, Object o2)
        {
            if (o1 == null)
                if (o2 == null)
                    return 0;
                else
                    return -1;
            else if (o2 == null)
                return 1;
            else
                ; // continue
            String str1 = o1.toString();
            String str2 = o2.toString();

            return str1.compareToIgnoreCase(str2);
        }
    }

    private static VAttributeComparer vattributeComparer = new VAttributeComparer();

    public static void sortVAttributes(VAttribute[] attrs)
    {
        if (attrs == null)
            return;

        QSort qsort = new QSort(vattributeComparer);
        qsort.sort(attrs);
    }

    /** Sort by name. Optionally sort by type first, then by name */
    public static void sortVNodesByTypeName(VNode[] nodes, boolean typeFirst, boolean ignoreCase)
    {
        if (nodes == null)
            return;

        VNodeComparer comparer = new VNodeTypeNameComparer(typeFirst, ignoreCase);
        // comparer.ignoreCase=ignoreCase;
        // comparer.sortFields=sortFields;
        QSort qsort = new QSort(comparer);
        qsort.sort(nodes);
    }

    /** Sort by name. Optionally sort by type first, then by name */
    public static void sortVNodes(VNode[] nodes, String[] sortFields, boolean ignoreCase)
    {
        if (nodes == null)
            return;

        VNodeComparer comparer = new VNodeAttributeComparer(sortFields, ignoreCase);
        // comparer.ignoreCase=ignoreCase;
        // comparer.sortFields=sortFields;
        QSort qsort = new QSort(comparer);
        qsort.sort(nodes);
    }

    /** Sort by name. Optionally sort by type first, then by name */
    public static int[] sortStringList(StringList list, boolean ignoreCase)
    {
        if (list == null)
            return null;

        StringComparer comparer = new StringComparer();
        comparer.ignoreCase = ignoreCase;
        QSort qsort = new QSort(comparer);
        return qsort.sort(list);
    }

}