import java.io.*;
//import java.util.*;

/*
5 10
3 5 7 11 17
1 1 4 4
2 1 4 7
2 1 5 3
1 1 5 2
2 1 5 5
4 1 1
4 2 2
4 3 3
4 4 4
4 5 5
*/
/*
[36, 1, 39, 44, 15, 17, 47, 18, 7, 2]
[1, 3, 4, 1, 2, 1, 1, 3, 3, 2]
[1, 1, 3, 0, 6, 0, 3, 6, 6, 3]
[7, 6, 7, 4, 8, 1, 9, 8, 9, 5]
[6, 3, 0, 5, 10, 9, 5, 4, 2, 7]

For query 2
171 36

*/

/*
 * For query 9
[8, 27, 33, 36, 41, 10, 12, 6, 37, 12]
[1, 1, 4, 4, 2, 3, 4, 1, 2, 4]
[3, 5, 6, 3, 4, 1, 0, 0, 5, 0]
[5, 5, 9, 7, 6, 8, 8, 2, 5, 1]
[1, 3, 0, 0, 2, 7, 0, 5, 5, 0]
 */
class ADDMUL {

	final static int mod = 1000000007;
	int N;
	int[] A;
	Node[] tree;
	class Node {
		long value;
		boolean updateRequired;
		long multiplier, summand, change;
		Node(long v) {
			value = v;
			multiplier = 1;
		}
		public String toString() {
			return String.valueOf(value);
		}
	}
	
	void multiplyRange(int node, int a, int b, int x, int y, long multiplier) {
		lazyupdate(node, a, b);
		if (y < a || b < x) return ;
		
		int mid = a + b >> 1;
		int left = node << 1;
		int right = left + 1;
		
		if (x <= a && b <= y) { // segment node is completely inside update range
			tree[node].value = tree[node].value * multiplier % mod;

			if (a != b) { // non-leaf
				tree[left].updateRequired = true;
				tree[right].updateRequired = true;
				
				tree[left].multiplier = tree[left].multiplier * multiplier % mod;
				tree[left].summand = tree[left].summand * multiplier % mod;
				
				tree[right].multiplier = tree[right].multiplier * multiplier % mod;
				tree[right].summand = tree[right].summand * multiplier % mod;
			}
		}
		
		else {
			multiplyRange(left, a, mid, x, y, multiplier);
			multiplyRange(right, mid+1, b, x, y, multiplier);
			tree[node].value = (tree[left].value + tree[right].value) % mod;
		}
		assert(tree[node].value >= 0);
	}
	
	void addRange(int node, int a, int b, int x, int y, long summand) {
		lazyupdate(node, a, b);
		if (y < a || b < x) return ;
		
		int mid = a + b >> 1;
		int left = node << 1;
		int right = left + 1;
		
		if (x <= a && b <= y) {
			tree[node].value = (tree[node].value + (b-a+1) * summand) % mod;
			
			if (a != b) {
				tree[left].updateRequired = true;
				tree[right].updateRequired = true;
				
				tree[left].summand = (tree[left].summand + summand) % mod;
				tree[right].summand = (tree[right].summand + summand) % mod;
			}
		}
		
		else {
			addRange(left, a, mid, x, y, summand);
			addRange(right, mid+1, b, x, y, summand);
			tree[node].value = (tree[left].value + tree[right].value) % mod;
		}
		assert(tree[node].value >= 0);
	}

	long query(int node, int a, int b, int x, int y) {
		lazyupdate(node, a, b);
		if (y < a || b < x) return 0;
		
//		if (x <= a && b <= y) return tree[node].value;
		if (x <= a && b <= y) {
			assert(tree[node].value >= 0);
			return tree[node].value;
		}
		
		int mid = a + b >> 1;
		int left = node << 1;
		int right = left + 1;
		
//		return (query(left, a, mid, x, y) +
//				query(right, mid+1, b, x, y)) % mod;
		
		long value = (query(left, a, mid, x, y) +
				query(right, mid+1, b, x, y)) % mod;
		assert(value >= 0);
		return value;
	}
	
	void set(int node, int a, int b, int x, int y, long value) {
		lazyupdate(node, a, b);
		if (y < a || b < x) return ;
		
		int mid = a + b >> 1;
		int left = node << 1;
		int right = left + 1;
		
		if (x <= a && b <= y) {
			tree[node].value = (b-a+1) * value % mod;
			
			if (a != b) {
				tree[left].updateRequired = true;
				tree[right].updateRequired = true;
				
				tree[left].multiplier = 1;
				tree[left].summand = 0;
				tree[left].change = value;
				
				tree[right].multiplier = 1;
				tree[right].summand = 0;
				tree[right].change = value;
			}
		}
		
		else {
			set(left, a, mid, x, y, value);
			set(right, mid+1, b, x, y, value);
			tree[node].value = (tree[left].value + tree[right].value) % mod;
		}
	}
	
	void build(int node, int a, int b) {
		int mid = a + b >> 1;
		int left = node << 1;
		int right = left + 1;
		
		if (a == b) tree[node].value = A[a];
		
		else {
			build(left, a, mid);
			build(right, mid+1, b);
			tree[node].value = (tree[left].value + tree[right].value) % mod;
		}
		assert(tree[node].value >= 0);
	}
	
	void lazyupdate(int node, int a, int b) {
		if (!tree[node].updateRequired) return ;
		
		if (tree[node].change > 0) tree[node].value = (b-a+1) * tree[node].change % mod;
		tree[node].value = (tree[node].value * tree[node].multiplier + (b-a+1) * tree[node].summand) % mod;
		
		if (a != b) {
			int left = node << 1;
			int right = left + 1;
			
			if (tree[node].change > 0) {
				tree[left].change = tree[node].change;
				tree[right].change = tree[node].change;
				
				tree[left].multiplier = 1;
				tree[left].summand = 0;
				
				tree[right].multiplier = 1;
				tree[right].summand = 0;
			}
			tree[left].multiplier = tree[left].multiplier * tree[node].multiplier % mod;
			tree[left].summand = (tree[left].summand * tree[node].multiplier + tree[node].summand) % mod;
			
			tree[right].multiplier = tree[right].multiplier * tree[node].multiplier % mod;
			tree[right].summand = (tree[right].summand * tree[node].multiplier + tree[node].summand) % mod;
			
			tree[left].updateRequired = true;
			tree[right].updateRequired = true;
		}
		
		tree[node].change = 0;
		tree[node].multiplier = 1;
		tree[node].summand = 0;
		assert(tree[node].value >= 0);
	}
	
	void solve() {
		int N = nextInt();
		int Q = nextInt();
		tree = new Node[N * 3];
		for (int i = 0; i < N * 3; i++) tree[i] = new Node(0);
		A = new int[N];
		for (int i = 0; i < N; i++)
			A[i] = nextInt();
		build(1, 0, N-1);
		
		while (Q-- > 0) {
			int C = nextInt();
			int L = nextInt() - 1;
			int R = nextInt() - 1;
			if (C != 4) {
				int V = nextInt();

				if (C == 1) addRange(1, 0, N-1, L, R, V);
				else if (C == 2) multiplyRange(1, 0, N-1, L, R, V);
				else set(1, 0, N-1, L, R, V);
			}
			else out.println(query(1, 0, N-1, L, R));
		}
		
		
		
//		Random random = new Random();
//		int N = 10000;
//		int Q = 10000;
//		tree = new Node[N * 4];
//		for (int i = 0; i < N * 4; i++) tree[i] = new Node(0);
//		A = new int[N];
//		for (int i = 0; i < N; i++)
//			A[i] = random.nextInt(50) + 1;
//		build(1, 0, N-1);
//		
//		long[] clone = new long[N];
//		for(int i = 0; i < N; i++) clone[i] = A[i];
//		int[] C = new int[Q];
//		int[] L = new int[Q];
//		int[] R = new int[Q];
//		int[] V = new int[Q];
//		for (int q = 0; q < Q; q++) {
//			C[q] = random.nextInt(4) + 1;
//			L[q] = random.nextInt(3*N/4);
//			R[q] = random.nextInt(N-L[q]) + L[q];
//			if (C[q] != 4) V[q] = random.nextInt(10) + 1;
//			
//		}
//		boolean printonce = false;
//		for (int q = 0; q < Q; q++) {
//			if (C[q] != 4) {
//
//				if (C[q] == 1) {
//					addRange(1, 0, N-1, L[q], R[q], V[q]);
//					for (int i = L[q]; i <= R[q]; i++)
//						clone[i] = (clone[i] + V[q]) % mod;
//				}
//				else if (C[q] == 2) {
//					multiplyRange(1, 0, N-1, L[q], R[q], V[q]);
//					for (int i = L[q]; i <= R[q]; i++)
//						clone[i] = clone[i] * V[q] % mod;
//				}
//				else {
//					set(1, 0, N-1, L[q], R[q], V[q]);
//					for (int i = L[q]; i <= R[q]; i++)
//						clone[i] = V[q];
//				}
//			}
//			else {
//				long result1 = query(1, 0, N-1, L[q], R[q]);
//				long result2 = 0;
//				for (int i = L[q]; i <= R[q]; i++)
//					result2 = (result2 + clone[i]) % mod;
//				
//				if (result1 != result2){
//					if (!printonce) {
//						out.println(Arrays.toString(A));
//						out.println(Arrays.toString(C));
//						out.println(Arrays.toString(L));
//						out.println(Arrays.toString(R));
//						out.println(Arrays.toString(V));
//						printonce = true;
//					}
//					out.println("\nFor query " + q);
//					out.println(result1 + " " + result2);
//					
//				}
//			}
//		}
		
		
		
//		int N = 10;
//		int Q = 10;
//		tree = new Node[N * 3];
//		for (int i = 0; i < N * 3; i++) tree[i] = new Node(0);
//		int[] clone = {36, 1, 39, 44, 15, 17, 47, 18, 7, 2};
//		A = new int[N];
//		for (int i = 0; i < N; i++)
//			A[i] = clone[i];
//		build(1, 0, N-1);
//			
//		int[] C = {1, 3, 4, 1, 2, 1, 1, 3, 3, 2};
//		int[] L = {1, 1, 3, 0, 6, 0, 3, 6, 6, 3};
//		int[] R = {7, 6, 7, 4, 8, 1, 9, 8, 9, 5};
//		int[] V = {6, 3, 0, 5, 10, 9, 5, 4, 2, 7};
//		for (int q = 0; q < Q; q++) {
//			if (C[q] != 4) {
//
//				if (C[q] == 1) {
//					addRange(1, 0, N-1, L[q], R[q], V[q]);
//					for (int i = L[q]; i <= R[q]; i++)
//						clone[i] = (clone[i] + V[q]) % mod;
//				}
//				else if (C[q] == 2) {
//					multiplyRange(1, 0, N-1, L[q], R[q], V[q]);
//					for (int i = L[q]; i <= R[q]; i++)
//						clone[i] = (clone[i] * V[q]) % mod;
//				}
//				else {
//					set(1, 0, N-1, L[q], R[q], V[q]);
//					for (int i = L[q]; i <= R[q]; i++)
//						clone[i] = V[q];
//				}
//			}
//			else {
//				long result1 = query(1, 0, N-1, L[q], R[q]);
//				long result2 = 0;
//				for (int i = L[q]; i <= R[q]; i++)
//					result2 = (result2 + clone[i]) % mod;
//				
//				if (result1 != result2){
//					out.println(result1 + " " + result2);
//					out.println("For query " + q);
//					out.println(Arrays.toString(A));
//					out.println(Arrays.toString(C));
//					out.println(Arrays.toString(L));
//					out.println(Arrays.toString(R));
//					out.println(Arrays.toString(V));
//				}
//			}
//		}
	}
	
	BufferedInputStream in = new BufferedInputStream(System.in);
    PrintWriter out= new PrintWriter(System.out);
	int nextInt() {
        int no = 0;
        boolean minus = false;
        try {
            int a = in.read();
            while (a == 32 || a == 10)
                a = in.read();
            if (a == '-') {
                minus = true;
                a = in.read();
            }
            while ('0' <= a && a <= '9') {
                no = no * 10 + (a - '0');
                a = in.read();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return minus ? -no: no;
    }
	public static void main (String[] args)	{
	    ADDMUL Solve = new ADDMUL();
	    Solve.solve();
	    Solve.out.flush();
	}
}
