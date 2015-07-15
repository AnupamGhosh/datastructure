import java.util.Arrays;

class DisjointSet {
	int N;
	int[] p;
	public DisjointSet(int n) {
		N = n;
		p = new int[n];
		Arrays.fill(p, -1);
	}
	void weightedUnion(int u, int v) {
		int uroot = compressedFind(u);
		int vroot = compressedFind(v);
		if (p[uroot] < p[vroot]) {
			p[uroot] += p[vroot];
			p[vroot] = uroot;
		}
		else {
			p[vroot] += p[uroot];
			p[uroot] = vroot;
		}
	}
	int compressedFind(int v) {
		if (p[v] < 0) return v;
		return p[v] = compressedFind(p[v]);
	}
	int size(int v) {
		return -p[compressedFind(v)];
	}
}
