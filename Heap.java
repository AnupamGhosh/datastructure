class Heap {
    int[] a;
    int n;
    Heap(int[] a, int n) {
        this.a = new int[n+3];
        for (int i=1; i<=n; i++)
            this.a[i] = a[i-1];
        this.n = n;
        for (int i=n>>1; i>0; i--)
            heapify(i);
    }
    void heapify(int i) { //max heap
        int left = i << 1; 
        int right = left + 1;
        int max = i;
        if (left <= n && a[left] > a[max]) max = left;
        if (right <= n && a[right] > a[max]) max = right;
        
        if (max != i) {
            int temp = a[max];
            a[max] = a[i];
            a[i] = temp;
            heapify(max);
        }
    }
    void insert(int key) {
        n++;
        int i = n;
        while (i>1 && a[i>>1] < key) {
            a[i] = a[i>>1];
            i >>= 1;
        }
        a[i] = key;
    }
    int extractMax() {
        int max = a[1];
        a[1] = a[n];
        n--;
        heapify(1);
        return max;
    }
    void increaseKey(int i, long key) {
        while (i>1 && a[i>>1] < key) {
            a[i] = a[i>>1];
            i >>= 1;
        }
        a[i] = key;
    }
}
